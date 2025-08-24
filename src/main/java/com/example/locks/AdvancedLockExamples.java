package com.example.locks;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 高级锁使用示例 演示公平锁vs非公平锁、读写锁、条件锁等高级特性
 */
public class AdvancedLockExamples {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 高级锁机制演示 ===\n");

        // 1. 公平锁vs非公平锁
        demonstrateFairVsUnfairLock();

        // 2. 读写锁演示
        demonstrateReadWriteLock();

        // 3. 条件锁演示
        demonstrateConditionLock();

        // 4. 锁降级演示
        demonstrateLockDowngrade();
    }

    /**
     * 公平锁vs非公平锁演示
     */
    private static void demonstrateFairVsUnfairLock() throws InterruptedException {
        System.out.println("1. 公平锁 vs 非公平锁演示:");

        // 非公平锁（默认）
        ReentrantLock unfairLock = new ReentrantLock(false);
        // 公平锁
        ReentrantLock fairLock = new ReentrantLock(true);

        System.out.println("非公平锁执行顺序:");
        testLockFairness(unfairLock, "非公平");

        Thread.sleep(1000);

        System.out.println("\n公平锁执行顺序:");
        testLockFairness(fairLock, "公平");

        System.out.println();
    }

    private static void testLockFairness(ReentrantLock lock, String type) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(5);

        for (int i = 1; i <= 5; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    lock.lock();
                    try {
                        System.out.println(type + "锁 - 线程" + threadId + "获得锁");
                        Thread.sleep(100 + new Random().nextInt(100));
                    } finally {
                        lock.unlock();
                        latch.countDown();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Thread-" + threadId).start();
        }

        latch.await();
    }

    /**
     * 读写锁演示
     */
    private static void demonstrateReadWriteLock() throws InterruptedException {
        System.out.println("2. 读写锁演示:");

        ReadWriteLockExample example = new ReadWriteLockExample();
        CountDownLatch latch = new CountDownLatch(8);

        // 启动少数写线程
        for (int i = 1; i <= 3; i++) {
            final int writerId = i;
            new Thread(() -> {
                example.write(writerId, "数据" + writerId);
                try {
                    Thread.sleep(100 + new Random().nextInt(100));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                latch.countDown();
            }, "Writer-" + i).start();
        }

        // 启动多个读线程
        for (int i = 1; i <= 5; i++) {
            final int readerId = i;
            new Thread(() -> {
                example.read(readerId);
                try {
                    Thread.sleep(100 + new Random().nextInt(100));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                latch.countDown();
            }, "Reader-" + i).start();
        }

        latch.await();
        System.out.println();
    }

    /**
     * 条件锁演示
     */
    private static void demonstrateConditionLock() throws InterruptedException {
        System.out.println("3. 条件锁演示 (生产者-消费者模式):");

        BoundedBuffer<String> buffer = new BoundedBuffer<>(3);
        CountDownLatch latch = new CountDownLatch(8);

        // 启动生产者
        for (int i = 1; i <= 4; i++) {
            final int producerId = i;
            new Thread(() -> {
                try {
                    buffer.put("商品" + producerId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            }, "Producer-" + i).start();
        }

        // 启动消费者
        for (int i = 1; i <= 4; i++) {
            final int consumerId = i;
            new Thread(() -> {
                try {
                    String item = buffer.take();
                    System.out.println("消费者" + consumerId + "消费了: " + item);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            }, "Consumer-" + i).start();
        }

        latch.await();
        System.out.println();
    }

    /**
     * 锁降级演示
     */
    private static void demonstrateLockDowngrade() {
        System.out.println("4. 锁降级演示:");

        LockDowngradeExample example = new LockDowngradeExample();

        // 演示锁降级过程
        example.processDataWithDowngrade("重要数据");

        System.out.println();
    }
}

/**
 * 读写锁示例
 */
class ReadWriteLockExample {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private String data = "初始数据";

    public void read(int readerId) {
        lock.readLock().lock();
        try {
            System.out.println("读者" + readerId + "开始读取");
            Thread.sleep(1000); // 模拟读取时间
            System.out.println("读者" + readerId + "读取到: " + data);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.readLock().unlock();
            System.out.println("读者" + readerId + "完成读取");
        }
    }

    public void write(int writerId, String newData) {
        lock.writeLock().lock();
        try {
            System.out.println("写者" + writerId + "开始写入");
            Thread.sleep(1500); // 模拟写入时间
            this.data = newData;
            System.out.println("写者" + writerId + "写入完成: " + newData);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.writeLock().unlock();
            System.out.println("写者" + writerId + "完成写入");
        }
    }
}

/**
 * 有界缓冲区 - 使用条件锁实现生产者消费者模式
 */
class BoundedBuffer<T> {
    private final Object[] buffer;
    private int count = 0;
    private int putIndex = 0;
    private int takeIndex = 0;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition(); // 缓冲区不满条件
    private final Condition notEmpty = lock.newCondition(); // 缓冲区不空条件

    public BoundedBuffer(int capacity) {
        this.buffer = new Object[capacity];
    }

    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            // 等待缓冲区不满
            while (count == buffer.length) {
                System.out.println("缓冲区已满，生产者等待...");
                notFull.await();
            }

            buffer[putIndex] = item;
            putIndex = (putIndex + 1) % buffer.length;
            count++;

            System.out.println("生产者放入: " + item + ", 缓冲区大小: " + count);

            // 通知消费者缓冲区不空
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public T take() throws InterruptedException {
        lock.lock();
        try {
            // 等待缓冲区不空
            while (count == 0) {
                System.out.println("缓冲区为空，消费者等待...");
                notEmpty.await();
            }

            T item = (T) buffer[takeIndex];
            buffer[takeIndex] = null;
            takeIndex = (takeIndex + 1) % buffer.length;
            count--;

            System.out.println("消费者取出: " + item + ", 缓冲区大小: " + count);

            // 通知生产者缓冲区不满
            notFull.signal();

            return item;
        } finally {
            lock.unlock();
        }
    }
}

/**
 * 锁降级示例
 */
class LockDowngradeExample {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private String data = "原始数据";
    private volatile boolean dataChanged = false;

    /**
     * 锁降级：写锁 -> 读锁 注意：不能从读锁升级到写锁，但可以从写锁降级到读锁
     */
    public void processDataWithDowngrade(String newData) {
        lock.writeLock().lock(); // 1. 获取写锁
        try {
            System.out.println("获取写锁，准备更新数据");

            // 更新数据
            this.data = newData;
            dataChanged = true;
            System.out.println("数据更新完成: " + newData);

            // 在释放写锁前获取读锁（锁降级）
            lock.readLock().lock(); // 2. 获取读锁
            System.out.println("锁降级：从写锁降级到读锁");

        } finally {
            lock.writeLock().unlock(); // 3. 释放写锁
            System.out.println("释放写锁");
        }

        try {
            // 现在只持有读锁，可以安全地读取数据
            System.out.println("持有读锁，读取数据: " + data);
            System.out.println("数据是否发生变化: " + dataChanged);

            // 其他线程现在可以并发读取
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.readLock().unlock(); // 4. 释放读锁
            System.out.println("释放读锁，锁降级完成");
        }
    }
}