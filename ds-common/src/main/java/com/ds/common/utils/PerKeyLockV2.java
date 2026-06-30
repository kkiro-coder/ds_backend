package com.ds.common.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * 按 key 串行执行的锁（V2：单锁 + 多 Condition）。
 * 全局一个 ReentrantLock，每个 key 对应一个 Condition，避免反复创建/销毁 Lock 实例。
 * 相同 key 的操作排队等待；不同 key 并发执行。
 *
 * <p>与 V1 的区别：V1 为每个 key 创建一个 ReentrantLock 实例；
 * V2 使用全局单锁 + Condition.newCondition() 按 key 隔离等待队列。
 *
 * <pre>{@code
 * PerKeyLockV2 lock = new PerKeyLockV2();
 * String result = lock.execute("ruleId", () -> doSomething());
 * }</pre>
 *
 * @author ds
 */
public class PerKeyLockV2 {

    private static final long TIMEOUT_MS = 30_000;

    /** 全局锁，仅用于进出检查和 Condition 操作，不包裹业务逻辑 */
    private final ReentrantLock lock = new ReentrantLock(true);

    /** key → 该 key 的等待/信号条件 */
    private final ConcurrentHashMap<String, Condition> conditionMap = new ConcurrentHashMap<>();

    /** key → 当前是否被某个线程占用（1=占用，0=空闲） */
    private final ConcurrentHashMap<String, Integer> busyMap = new ConcurrentHashMap<>();

    /**
     * 在指定 key 上同步执行（有返回值）
     */
    public <T> T execute(String key, Supplier<T> task) {
        Condition cond = conditionMap.computeIfAbsent(key, k -> lock.newCondition());
        checkIn(key, cond);
        try {
            return task.get();
        } finally {
            checkOut(key, cond);
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

    // ==================== 内部 ====================

    /**
     * 入场：全局锁下检查 busyMap，若该 key 已被占用则 await 等待
     */
    private void checkIn(String key, Condition cond) {
        lock.lock();
        try {
            long deadline = System.currentTimeMillis() + TIMEOUT_MS;
            while (busyMap.getOrDefault(key, 0) > 0) {
                long remaining = deadline - System.currentTimeMillis();
                if (remaining <= 0) {
                    throw new IllegalStateException("获取锁超时: " + key);
                }
                try {
                    cond.await(remaining, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    throw new IllegalStateException("等待被中断: " + key, e);
                }
                // await 返回后重新检查：可能是 signal 唤醒，也可能是超时
            }
            busyMap.put(key, 1);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 退场：全局锁下标记该 key 为空闲，并唤醒一个等待者
     */
    private void checkOut(String key, Condition cond) {
        lock.lock();
        try {
            busyMap.put(key, 0);
            cond.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 当前持有的 key 数量
     */
    public int size() {
        return conditionMap.size();
    }
}
