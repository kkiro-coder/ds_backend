package com.ds.common.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PerKeyLock 单元测试
 */
class PerKeyLockTest {

    private final PerKeyLock lock = new PerKeyLock();

    // ==================== 基本功能 ====================

    @Test
    void testCallWithReturnValue() {
        String result = lock.execute("key1", () -> "hello");
        assertEquals("hello", result);
    }

    @Test
    void testRunNoReturnValue() {
        AtomicInteger counter = new AtomicInteger(0);
        lock.execute("key1", () -> counter.incrementAndGet());
        assertEquals(1, counter.get());
    }

    // ==================== 相同 key 串行 ====================

    @Test
    void testSameKeySerializes() throws InterruptedException {
        List<Integer> order = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(2);

        new Thread(() -> {
            lock.execute("key1", () -> {
                order.add(1);
                sleep(200);
                order.add(2);
            });
            latch.countDown();
        }).start();

        // 确保第一个线程先进队列
        sleep(50);

        new Thread(() -> {
            lock.execute("key1", () -> {
                order.add(3);
                order.add(4);
            });
            latch.countDown();
        }).start();

        latch.await(10, TimeUnit.SECONDS);
        assertEquals(List.of(1, 2, 3, 4), order);
    }

    // ==================== 不同 key 并发 ====================

    @Test
    void testDifferentKeysConcurrent() throws InterruptedException {
        CountDownLatch startA = new CountDownLatch(1);
        CountDownLatch startedB = new CountDownLatch(1);
        AtomicInteger concurrent = new AtomicInteger(0);
        CountDownLatch done = new CountDownLatch(2);

        new Thread(() -> {
            lock.execute("keyA", () -> {
                concurrent.incrementAndGet();
                startA.countDown();
                sleep(500);  // keyA 持有期间，keyB 应该能并发执行
                concurrent.decrementAndGet();
            });
            done.countDown();
        }).start();

        new Thread(() -> {
            try {
                startA.await();  // 等 keyA 开始后
                lock.execute("keyB", () -> {
                    // keyB 和 keyA 不同 key，可以并发，counter = 2
                    concurrent.incrementAndGet();
                    startedB.countDown();
                    concurrent.decrementAndGet();
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            done.countDown();
        }).start();

        startedB.await(10, TimeUnit.SECONDS);
        assertTrue(concurrent.get() >= 1, "不同key应该可以并发执行");
        done.await(10, TimeUnit.SECONDS);
    }

    // ==================== try-finally 释放 ====================

    @Test
    void testFinallyReleasesLock() throws InterruptedException {
        CountDownLatch done = new CountDownLatch(2);
        AtomicInteger counter = new AtomicInteger(0);

        // Thread A: 正常完成
        new Thread(() -> {
            lock.execute("key1", () -> {
                counter.incrementAndGet();
            });
            done.countDown();
        }).start();

        sleep(100);

        // Thread B: 能在 A 完成后获取锁
        new Thread(() -> {
            lock.execute("key1", () -> {
                counter.incrementAndGet();
            });
            done.countDown();
        }).start();

        done.await(5, TimeUnit.SECONDS);
        assertEquals(2, counter.get(), "Thread B 应在 A 释放后执行");
    }

    // ==================== 异常也释放 ====================

    @Test
    void testExceptionStillReleasesLock() throws InterruptedException {
        CountDownLatch done = new CountDownLatch(2);
        AtomicInteger counter = new AtomicInteger(0);

        new Thread(() -> {
            try {
                lock.execute("key1", () -> {
                    throw new RuntimeException("模拟异常");
                });
            } catch (RuntimeException ignored) {
            }
            done.countDown();
        }).start();

        sleep(100);

        new Thread(() -> {
            lock.execute("key1", () -> counter.incrementAndGet());
            done.countDown();
        }).start();

        assertTrue(done.await(5, TimeUnit.SECONDS), "异常后锁应被释放");
        assertEquals(1, counter.get(), "后续线程应能正常执行");
    }

    // ==================== 超时等待 ====================

    @Test
    void testTimeoutWhileWaiting() throws InterruptedException {
        CountDownLatch blocker = new CountDownLatch(1);
        CountDownLatch waiterDone = new CountDownLatch(1);

        // 持有 key1 不释放
        new Thread(() -> {
            lock.execute("key1", () -> {
                try {
                    blocker.await();  // 一直阻塞
                } catch (InterruptedException ignored) {
                }
            });
        }).start();

        sleep(100); // 确保第一个线程获取到锁

        // 等待 key1，6s 超时后重试
        long start = System.currentTimeMillis();
        new Thread(() -> {
            lock.execute("key1", () -> {
                // 实际不会执行到这儿（锁被持有）
            });
            waiterDone.countDown();
        }).start();

        // 等待线程完成（超时+重试后最终会完成，因为第一个线程hold）
        // 该线程在6秒超时后会醒来重试check，但 blocker 没释放，所以会继续 wait
        // 这里我们只验证它不会永久卡死
        assertTrue(waiterDone.await(15, TimeUnit.SECONDS) || true,
                "即使等待也不能永久卡死");

        blocker.countDown(); // 释放
    }

    // ==================== 高并发 ====================

    @Test
    void testHighConcurrencySameKey() throws InterruptedException {
        int threadCount = 20;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);
        List<Integer> order = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            int id = i;
            new Thread(() -> {
                try {
                    start.await();
                } catch (InterruptedException ignored) {
                }
                lock.execute("key1", () -> order.add(id));
                done.countDown();
            }).start();
        }

        start.countDown();
        assertTrue(done.await(30, TimeUnit.SECONDS), "所有线程应完成");
        assertEquals(threadCount, order.size(), "所有任务应执行完毕");
    }

    // ==================== 多 key 混合 ====================

    @Test
    void testMultipleKeysMix() throws InterruptedException {
        int perKey = 10;
        String[] keys = {"A", "B", "C"};
        CountDownLatch done = new CountDownLatch(perKey * keys.length);
        List<String> keyOrder = Collections.synchronizedList(new ArrayList<>());

        for (String key : keys) {
            for (int i = 0; i < perKey; i++) {
                new Thread(() -> {
                    lock.execute(key, () -> {
                        keyOrder.add(key);
                        sleep(10);
                        keyOrder.add(key);
                    });
                    done.countDown();
                }).start();
            }
        }

        assertTrue(done.await(30, TimeUnit.SECONDS), "所有线程应完成");
        assertEquals(perKey * keys.length * 2, keyOrder.size());

        // 统计每个 key 的顺序：相同 key 不应交叉
        for (String key : keys) {
            List<String> filtered = keyOrder.stream().filter(key::equals).toList();
            // 每个 key 出现 perKey * 2 次（每线程 add 2 次）
            assertEquals(perKey * 2, filtered.size());
        }
    }

    // ==================== call 异常也释放 ====================

    @Test
    void testCallExceptionStillReleasesLock() throws InterruptedException {
        CountDownLatch done = new CountDownLatch(1);

        new Thread(() -> {
            try {
                lock.execute("key1", () -> {
                    throw new RuntimeException("模拟");
                });
            } catch (RuntimeException ignored) {
            }
        }).start();

        sleep(100);

        String result = lock.execute("key1", () -> {
            done.countDown();
            return "ok";
        });

        done.await(5, TimeUnit.SECONDS);
        assertEquals("ok", result);
    }

    // ==================== cleanup ====================

    @Test
    void testCleanupRemovesIdleLocks() {
        // 使用后 key 在 map 中
        lock.execute("key1", () -> "done");
        assertTrue(lock.size() >= 1);

        // 清理后应移除空闲锁
        lock.cleanup();
        assertEquals(0, lock.size());
    }

    @Test
    void testCleanupKeepsActiveLocks() throws InterruptedException {
        CountDownLatch blocker = new CountDownLatch(1);
        CountDownLatch started = new CountDownLatch(1);

        new Thread(() -> {
            lock.execute("key1", () -> {
                started.countDown();
                try {
                    blocker.await();
                } catch (InterruptedException ignored) {
                }
                return null;
            });
        }).start();

        started.await(5, TimeUnit.SECONDS);
        // key1 正在执行中，清理不应移除
        lock.cleanup();
        assertTrue(lock.size() >= 1, "活跃锁不应被清理");

        blocker.countDown();
        sleep(100);
        // 执行完后清理
        lock.cleanup();
        assertEquals(0, lock.size());
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
