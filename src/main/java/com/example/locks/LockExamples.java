package com.example.locks;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 锁机制演示类 演示重入锁、悲观锁、乐观锁、互斥锁、自旋锁的使用
 */
public class LockExamples {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Java 锁机制演示 ===\n");

        // 1. 重入锁演示
        demonstrateReentrantLock();

        // 2. 不可重入锁演示
        demonstrateNonReentrantLock();

        // 3. 悲观锁vs乐观锁演示
        demonstratePessimisticVsOptimistic();

        // 4. 互斥锁演示
        demonstrateMutexLock();

        // 5. 自旋锁演示
        demonstrateSpinLock();
    }

    /**
     * 重入锁演示
     */
    private static void demonstrateReentrantLock() {
        System.out.println("1. 重入锁演示:");
        ReentrantLockExample example = new ReentrantLockExample();

        // 演示同一线程多次获取锁
        example.outerMethod();

        System.out.println();
    }

    /**
     * 不可重入锁演示
     */
    private static void demonstrateNonReentrantLock() {
        System.out.println("2. 不可重入锁演示:");
        NonReentrantLockExample example = new NonReentrantLockExample();

        try {
            // 这会导致死锁（注释掉避免程序hang住）
            // example.outerMethod();
            System.out.println("不可重入锁如果重复获取会导致死锁，这里跳过演示");
        } catch (Exception e) {
            System.out.println("捕获到死锁异常: " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * 悲观锁vs乐观锁演示
     */
    private static void demonstratePessimisticVsOptimistic() throws InterruptedException {
        System.out.println("3. 悲观锁 vs 乐观锁演示:");

        // 悲观锁示例
        PessimisticCounter pessimisticCounter = new PessimisticCounter();
        // 乐观锁示例
        OptimisticCounter optimisticCounter = new OptimisticCounter();

        int threadCount = 10;
        int incrementsPerThread = 1000;

        // 测试悲观锁
        long start = System.currentTimeMillis();
        CountDownLatch latch1 = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    pessimisticCounter.increment();
                }
                latch1.countDown();
            }).start();
        }

        latch1.await();
        long pessimisticTime = System.currentTimeMillis() - start;

        // 测试乐观锁
        start = System.currentTimeMillis();
        CountDownLatch latch2 = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    optimisticCounter.increment();
                }
                latch2.countDown();
            }).start();
        }

        latch2.await();
        long optimisticTime = System.currentTimeMillis() - start;

        System.out.println("悲观锁结果: " + pessimisticCounter.getCount() + ", 耗时: " + pessimisticTime + "ms");
        System.out.println("乐观锁结果: " + optimisticCounter.getCount() + ", 耗时: " + optimisticTime + "ms");
        System.out.println();
    }

    /**
     * 互斥锁演示
     */
    private static void demonstrateMutexLock() throws InterruptedException {
        System.out.println("4. 互斥锁演示:");

        MutexExample mutex = new MutexExample();
        CountDownLatch latch = new CountDownLatch(3);

        // 启动3个线程，只有一个能访问临界区
        for (int i = 0; i < 3; i++) {
            final int threadId = i + 1;
            new Thread(() -> {
                mutex.criticalSection(threadId);
                latch.countDown();
            }).start();
        }

        latch.await();
        System.out.println();
    }

    /**
     * 自旋锁演示
     */
    private static void demonstrateSpinLock() throws InterruptedException {
        System.out.println("5. 自旋锁演示:");

        SpinLockExample spinLock = new SpinLockExample();
        CountDownLatch latch = new CountDownLatch(3);

        for (int i = 0; i < 3; i++) {
            final int threadId = i + 1;
            new Thread(() -> {
                spinLock.doWork(threadId);
                latch.countDown();
            }).start();
        }

        latch.await();
        System.out.println();
    }
}

/**
 * 重入锁示例
 */
class ReentrantLockExample {
    private final ReentrantLock lock = new ReentrantLock();
    private int count = 0;

    public void outerMethod() {
        lock.lock();
        try {
            System.out.println("外层方法获取锁, 持有锁数量: " + lock.getHoldCount());
            count++;
            innerMethod(); // 重入锁允许同一线程再次获取
        } finally {
            lock.unlock();
            System.out.println("外层方法释放锁");
        }
    }

    public void innerMethod() {
        lock.lock();
        try {
            System.out.println("内层方法获取锁, 持有锁数量: " + lock.getHoldCount());
            count++;
        } finally {
            lock.unlock();
            System.out.println("内层方法释放锁, 当前count: " + count);
        }
    }
}

/**
 * 不可重入锁示例（模拟实现）
 */
class NonReentrantLockExample {
    private volatile boolean locked = false;
    private Thread owner;

    public synchronized void lock() {
        while (locked) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        locked = true;
        owner = Thread.currentThread();
    }

    public synchronized void unlock() {
        if (Thread.currentThread() != owner) {
            throw new IllegalStateException("当前线程未持有锁");
        }
        locked = false;
        owner = null;
        notify();
    }

    public void outerMethod() {
        lock();
        try {
            System.out.println("外层方法执行");
            innerMethod(); // 这里会死锁，因为不可重入
        } finally {
            unlock();
        }
    }

    public void innerMethod() {
        lock(); // 死锁！同一线程无法再次获取锁
        try {
            System.out.println("内层方法执行");
        } finally {
            unlock();
        }
    }
}

/**
 * 悲观锁示例
 */
class PessimisticCounter {
    private int count = 0;
    private final Object lock = new Object();

    public void increment() {
        synchronized (lock) {
            count++; // 假设冲突一定发生，先加锁
        }
    }

    public int getCount() {
        synchronized (lock) {
            return count;
        }
    }
}

/**
 * 乐观锁示例
 */
class OptimisticCounter {
    private final AtomicInteger count = new AtomicInteger(0);

    public void increment() {
        // 使用CAS操作，假设冲突很少发生
        count.incrementAndGet();
    }

    public int getCount() {
        return count.get();
    }
}

/**
 * 互斥锁示例
 */
class MutexExample {
    private final Object mutex = new Object();

    public void criticalSection(int threadId) {
        System.out.println("线程 " + threadId + " 尝试进入临界区");

        synchronized (mutex) {
            System.out.println("线程 " + threadId + " 进入临界区");
            try {
                Thread.sleep(1000); // 模拟工作
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("线程 " + threadId + " 离开临界区");
        }
    }
}

/**
 * 自旋锁示例
 */
class SpinLockExample {
    private final AtomicReference<Thread> owner = new AtomicReference<>();

    public void lock() {
        Thread current = Thread.currentThread();
        // 自旋等待，忙等待
        while (!owner.compareAndSet(null, current)) {
            // 不阻塞，继续尝试
        }
    }

    public void unlock() {
        Thread current = Thread.currentThread();
        owner.compareAndSet(current, null);
    }

    public void doWork(int threadId) {
        System.out.println("线程 " + threadId + " 尝试获取自旋锁");

        lock();
        try {
            System.out.println("线程 " + threadId + " 获取自旋锁成功，开始工作");
            // 短时间工作，自旋锁适合短期锁定
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            unlock();
            System.out.println("线程 " + threadId + " 释放自旋锁");
        }
    }
}