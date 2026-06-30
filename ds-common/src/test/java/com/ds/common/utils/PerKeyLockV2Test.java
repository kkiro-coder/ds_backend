package com.ds.common.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class PerKeyLockV2Test {

    private final PerKeyLockV2 lock = new PerKeyLockV2();

    @Test
    void testCallWithReturnValue() {
        assertEquals("hello", lock.execute("key1", () -> "hello"));
    }

    @Test
    void testSameKeySerializes() throws InterruptedException {
        List<Integer> order = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch done = new CountDownLatch(2);

        new Thread(() -> {
            lock.execute("key1", (Runnable) () -> {
                order.add(1); sleep(200); order.add(2);
            });
            done.countDown();
        }).start();
        sleep(50);

        new Thread(() -> {
            lock.execute("key1", (Runnable) () -> {
                order.add(3); order.add(4);
            });
            done.countDown();
        }).start();

        done.await(10, TimeUnit.SECONDS);
        assertEquals(List.of(1, 2, 3, 4), order);
    }

    @Test
    void testDifferentKeysConcurrent() throws InterruptedException {
        CountDownLatch insideA = new CountDownLatch(1);
        CountDownLatch insideB = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(2);

        new Thread(() -> {
            lock.execute("keyA", (Runnable) () -> {
                insideA.countDown();
                sleep(500);
            });
            done.countDown();
        }).start();

        new Thread(() -> {
            try { insideA.await(); } catch (InterruptedException ignored) {}
            lock.execute("keyB", (Runnable) () -> {
                insideB.countDown();
            });
            done.countDown();
        }).start();

        assertTrue(insideB.await(5, TimeUnit.SECONDS), "不同 key 应并发执行");
        done.await(5, TimeUnit.SECONDS);
    }

    @Test
    void testExceptionStillReleases() throws InterruptedException {
        CountDownLatch done = new CountDownLatch(1);

        new Thread(() -> {
            try {
                lock.execute("key1", (Runnable) () -> { throw new RuntimeException("异常"); });
            } catch (RuntimeException ignored) {}
        }).start();
        sleep(100);

        String result = lock.execute("key1", () -> { done.countDown(); return "ok"; });
        assertTrue(done.await(5, TimeUnit.SECONDS));
        assertEquals("ok", result);
    }

    @Test
    void testHighConcurrencySameKey() throws InterruptedException {
        int n = 20;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(n);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < n; i++) {
            new Thread(() -> {
                try { start.await(); } catch (InterruptedException ignored) {}
                lock.execute("key1", (Runnable) () -> counter.incrementAndGet());
                done.countDown();
            }).start();
        }
        start.countDown();
        assertTrue(done.await(30, TimeUnit.SECONDS));
        assertEquals(n, counter.get());
    }

    @Test
    void testMultipleKeysMix() throws InterruptedException {
        String[] keys = {"A", "B", "C"};
        CountDownLatch done = new CountDownLatch(keys.length * 5);

        for (String key : keys) {
            for (int i = 0; i < 5; i++) {
                new Thread(() -> {
                    lock.execute(key, (Runnable) () -> sleep(10));
                    done.countDown();
                }).start();
            }
        }
        assertTrue(done.await(30, TimeUnit.SECONDS));
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
