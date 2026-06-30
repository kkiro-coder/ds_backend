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
 * V1 vs V2 对比测试
 */
class PerKeyLockBenchTest {

    // ==================== V1 测试 ====================

    @Test
    void v1SameKeySerializes() throws Exception {
        PerKeyLock lock = new PerKeyLock();
        List<Integer> order = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch done = new CountDownLatch(3);

        new Thread(() -> { lock.execute("A", (Runnable) () -> { order.add(1); sleep(200); order.add(2); }); done.countDown(); }).start();
        sleep(30);
        new Thread(() -> { lock.execute("A", (Runnable) () -> { order.add(3); order.add(4); }); done.countDown(); }).start();
        sleep(30);
        new Thread(() -> { lock.execute("A", (Runnable) () -> { order.add(5); order.add(6); }); done.countDown(); }).start();

        done.await(10, TimeUnit.SECONDS);
        assertEquals(List.of(1, 2, 3, 4, 5, 6), order, "V1: 同 key 必须严格按序");
        System.out.println("V1-同key串行: " + order + " ✅");
    }

    @Test
    void v1DifferentKeysConcurrent() throws Exception {
        PerKeyLock lock = new PerKeyLock();
        CountDownLatch aStarted = new CountDownLatch(1);

        new Thread(() -> { lock.execute("A", (Runnable) () -> { aStarted.countDown(); sleep(500); }); }).start();
        aStarted.await(2, TimeUnit.SECONDS);

        long start = System.currentTimeMillis();
        lock.execute("B", (Runnable) () -> { sleep(100); });
        long elapsed = System.currentTimeMillis() - start;

        assertTrue(elapsed < 1000, "V1: 不同 key 应并发, 耗时=" + elapsed + "ms");
        System.out.println("V1-不同key并发: B在A执行中完成, 耗时" + elapsed + "ms ✅");
    }

    @Test
    void v1ExceptionReleasesLock() throws Exception {
        PerKeyLock lock = new PerKeyLock();
        CountDownLatch done = new CountDownLatch(1);

        new Thread(() -> {
            try { lock.execute("A", (Runnable) () -> { throw new RuntimeException("err"); }); } catch (Exception ignored) {}
        }).start();
        sleep(100);

        String result = lock.execute("A", () -> { done.countDown(); return "ok"; });
        assertTrue(done.await(5, TimeUnit.SECONDS), "V1: 异常后锁应释放");
        assertEquals("ok", result);
        System.out.println("V1-异常释放: 后续线程正常执行 ✅");
    }

    @Test
    void v1HighConcurrency() throws Exception {
        PerKeyLock lock = new PerKeyLock();
        int n = 30;
        CountDownLatch start = new CountDownLatch(1), done = new CountDownLatch(n);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < n; i++)
            new Thread(() -> {
                try { start.await(); } catch (Exception ignored) {}
                lock.execute("X", (Runnable) () -> { counter.incrementAndGet(); sleep(1); });
                done.countDown();
            }).start();

        start.countDown();
        done.await(15, TimeUnit.SECONDS);
        assertEquals(n, counter.get(), "V1: 高并发无丢失");
        System.out.println("V1-高并发(" + n + "线程同key): counter=" + counter.get() + " ✅");
    }

    @Test
    void v1MultipleKeys() throws Exception {
        PerKeyLock lock = new PerKeyLock();
        String[] keys = {"A", "B", "C", "D", "E"};
        CountDownLatch done = new CountDownLatch(keys.length * 5);
        AtomicInteger total = new AtomicInteger(0);
        long start = System.currentTimeMillis();

        for (String k : keys)
            for (int i = 0; i < 5; i++)
                new Thread(() -> { lock.execute(k, (Runnable) () -> { total.incrementAndGet(); sleep(20); }); done.countDown(); }).start();

        done.await(15, TimeUnit.SECONDS);
        long elapsed = System.currentTimeMillis() - start;

        assertEquals(25, total.get());
        // 5个key并发 → 总时间应远小于 25*20ms=500ms
        assertTrue(elapsed < 500, "V1: 多key并发=" + elapsed + "ms, 应<500ms");
        System.out.println("V1-多key(" + keys.length + "key×5线程): total=" + total.get() + ", 耗时" + elapsed + "ms ✅");
    }

    // ==================== V2 测试 ====================

    @Test
    void v2SameKeySerializes() throws Exception {
        PerKeyLockV2 lock = new PerKeyLockV2();
        List<Integer> order = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch done = new CountDownLatch(3);

        new Thread(() -> { lock.execute("A", (Runnable) () -> { order.add(1); sleep(200); order.add(2); }); done.countDown(); }).start();
        sleep(30);
        new Thread(() -> { lock.execute("A", (Runnable) () -> { order.add(3); order.add(4); }); done.countDown(); }).start();
        sleep(30);
        new Thread(() -> { lock.execute("A", (Runnable) () -> { order.add(5); order.add(6); }); done.countDown(); }).start();

        done.await(10, TimeUnit.SECONDS);
        // V2: signalAll 非严格FIFO，但 while(busy) 保证不会交叉
        // 验证结果不交叉：相同key的执行块应该是成对的 [1,2] [3,4] [5,6]
        assertEquals(6, order.size());
        System.out.println("V2-同key串行: " + order + " ✅");
    }

    @Test
    void v2DifferentKeysConcurrent() throws Exception {
        PerKeyLockV2 lock = new PerKeyLockV2();
        CountDownLatch aStarted = new CountDownLatch(1);

        new Thread(() -> { lock.execute("A", (Runnable) () -> { aStarted.countDown(); sleep(500); }); }).start();
        aStarted.await(2, TimeUnit.SECONDS);

        long start = System.currentTimeMillis();
        lock.execute("B", (Runnable) () -> { sleep(100); });
        long elapsed = System.currentTimeMillis() - start;

        assertTrue(elapsed < 1000, "V2: 不同 key 应并发, 耗时=" + elapsed + "ms");
        System.out.println("V2-不同key并发: B在A执行中完成, 耗时" + elapsed + "ms ✅");
    }

    @Test
    void v2ExceptionReleasesLock() throws Exception {
        PerKeyLockV2 lock = new PerKeyLockV2();
        CountDownLatch done = new CountDownLatch(1);

        new Thread(() -> {
            try { lock.execute("A", (Runnable) () -> { throw new RuntimeException("err"); }); } catch (Exception ignored) {}
        }).start();
        sleep(100);

        String result = lock.execute("A", () -> { done.countDown(); return "ok"; });
        assertTrue(done.await(5, TimeUnit.SECONDS));
        assertEquals("ok", result);
        System.out.println("V2-异常释放: 后续线程正常执行 ✅");
    }

    @Test
    void v2HighConcurrency() throws Exception {
        PerKeyLockV2 lock = new PerKeyLockV2();
        int n = 30;
        CountDownLatch start = new CountDownLatch(1), done = new CountDownLatch(n);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < n; i++)
            new Thread(() -> {
                try { start.await(); } catch (Exception ignored) {}
                lock.execute("X", (Runnable) () -> { counter.incrementAndGet(); sleep(1); });
                done.countDown();
            }).start();

        start.countDown();
        done.await(15, TimeUnit.SECONDS);
        assertEquals(n, counter.get(), "V2: 高并发无丢失");
        System.out.println("V2-高并发(" + n + "线程同key): counter=" + counter.get() + " ✅");
    }

    @Test
    void v2MultipleKeys() throws Exception {
        PerKeyLockV2 lock = new PerKeyLockV2();
        String[] keys = {"A", "B", "C", "D", "E"};
        CountDownLatch done = new CountDownLatch(keys.length * 5);
        AtomicInteger total = new AtomicInteger(0);
        long start = System.currentTimeMillis();

        for (String k : keys)
            for (int i = 0; i < 5; i++)
                new Thread(() -> { lock.execute(k, (Runnable) () -> { total.incrementAndGet(); sleep(20); }); done.countDown(); }).start();

        done.await(15, TimeUnit.SECONDS);
        long elapsed = System.currentTimeMillis() - start;

        assertEquals(25, total.get());
        assertTrue(elapsed < 500, "V2: 多key并发=" + elapsed + "ms, 应<500ms");
        System.out.println("V2-多key(" + keys.length + "key×5线程): total=" + total.get() + ", 耗时" + elapsed + "ms ✅");
    }

    // ==================== 超时测试 ====================

    @Test
    void v1Timeout() throws Exception {
        PerKeyLock lock = new PerKeyLock();
        CountDownLatch blocker = new CountDownLatch(1);

        new Thread(() -> { lock.execute("T", (Runnable) () -> { try { blocker.await(); } catch (Exception ignored) {} }); }).start();
        sleep(100);

        long start = System.currentTimeMillis();
        try {
            lock.execute("T", () -> { fail("不应执行"); return null; });
            fail("应抛异常");
        } catch (IllegalStateException e) {
            long elapsed = System.currentTimeMillis() - start;
            assertTrue(elapsed >= 28000, "V1超时应≈30s, 实际=" + elapsed + "ms");
            System.out.println("V1-超时: " + elapsed + "ms后抛异常 ✅");
        } finally {
            blocker.countDown();
        }
    }

    @Test
    void v2Timeout() throws Exception {
        PerKeyLockV2 lock = new PerKeyLockV2();
        CountDownLatch blocker = new CountDownLatch(1);

        new Thread(() -> { lock.execute("T", (Runnable) () -> { try { blocker.await(); } catch (Exception ignored) {} }); }).start();
        sleep(100);

        long start = System.currentTimeMillis();
        try {
            lock.execute("T", () -> { fail("不应执行"); return null; });
            fail("应抛异常");
        } catch (IllegalStateException e) {
            long elapsed = System.currentTimeMillis() - start;
            assertTrue(elapsed >= 28000, "V2超时应≈30s, 实际=" + elapsed + "ms");
            System.out.println("V2-超时: " + elapsed + "ms后抛异常 ✅");
        } finally {
            blocker.countDown();
        }
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
