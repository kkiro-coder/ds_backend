package com.ds.common.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * 按 key 串行执行的锁，实例化使用，每个实例独立维护自己的锁队列。
 * 基于 ReentrantLock(true) 实现公平 FIFO 顺序。
 * 相同 key 的操作排队等待；不同 key 并发执行。
 *
 * <pre>{@code
 * PerKeyLock lock = new PerKeyLock();
 * String result = lock.execute("ruleId", () -> doSomething());
 * }</pre>
 *
 * @author ds
 */
public class PerKeyLock {

    private static final long TIMEOUT_MS = 30_000;
    private static final int CLEANUP_THRESHOLD = 1000;

    private final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();
    private final AtomicBoolean cleaning = new AtomicBoolean(false);

    /**
     * 获取指定 key 的公平锁，超过阈值时自动清理空闲锁
     */
    private ReentrantLock getLock(String key) {
        if (lockMap.size() > CLEANUP_THRESHOLD && cleaning.compareAndSet(false, true)) {
            try {
                cleanup();
            } finally {
                cleaning.set(false);
            }
        }
        return lockMap.computeIfAbsent(key, k -> new ReentrantLock(true));
    }

    /**
     * 在指定 key 上同步执行（有返回值）
     */
    public <T> T execute(String key, Supplier<T> task) {
        ReentrantLock lock = getLock(key);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (!acquired) {
                throw new IllegalStateException("获取锁超时: " + key);
            }
            return task.get();
        } catch (InterruptedException e) {
            throw new IllegalStateException("等待被中断: " + key, e);
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }

    /**
     * 在指定 key 上同步执行（无返回值）
     */
    public void execute(String key, Runnable task) {
        this.<Void>execute(key, () -> {
            task.run();
            return null;
        });
    }

    /**
     * 清理空闲锁。遍历所有 key，移除当前无人持有且无等待者的锁。
     * 利用 ConcurrentHashMap.computeIfPresent 的 bin-lock 保证原子性：
     * 在 tryLock → remove 之间，其他线程的 computeIfAbsent 被阻塞，不会拿到旧锁。
     */
    public void cleanup() {
        for (String key : lockMap.keySet()) {
            lockMap.computeIfPresent(key, (k, lock) -> {
                if (lock.tryLock()) {
                    try {
                        return null; // 移除
                    } finally {
                        lock.unlock();
                    }
                }
                return lock; // 保留
            });
        }
    }

    /**
     * 当前持有的 key 数量
     */
    public int size() {
        return lockMap.size();
    }
}
