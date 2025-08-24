package com.example.thread_pool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池基础使用示例 演示各种线程池类型和基本操作
 */
public class ThreadPoolBasicExample {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Java 线程池基础使用示例 ===\n");

        // 1. 固定大小线程池
        testFixedThreadPool();

        // 2. 缓存线程池
        testCachedThreadPool();

        // 3. 单线程池
        testSingleThreadExecutor();

        // 4. 定时任务线程池
        testScheduledThreadPool();

        // 5. 自定义线程池
        testCustomThreadPool();

        // 6. 线程池参数对比
        compareThreadPools();

        System.out.println("=== 所有示例完成 ===");
    }

    /**
     * 测试固定大小线程池
     */
    private static void testFixedThreadPool() throws InterruptedException {
        System.out.println("1. 固定大小线程池测试:");

        ExecutorService executor = Executors.newFixedThreadPool(3);
        AtomicInteger taskCounter = new AtomicInteger(0);

        // 提交多个任务
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    System.out.println("  任务 " + taskId + " 开始执行 [线程: " + Thread.currentThread().getName() + "]");
                    Thread.sleep(1000); // 模拟任务执行
                    System.out.println("  任务 " + taskId + " 执行完成");
                    taskCounter.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 优雅关闭
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("  完成任务数: " + taskCounter.get());
        System.out.println("  特点: 固定3个线程，任务排队执行\n");
    }

    /**
     * 测试缓存线程池
     */
    private static void testCachedThreadPool() throws InterruptedException {
        System.out.println("2. 缓存线程池测试:");

        ExecutorService executor = Executors.newCachedThreadPool();
        AtomicInteger taskCounter = new AtomicInteger(0);

        // 快速提交多个任务
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    System.out.println("  任务 " + taskId + " 开始执行 [线程: " + Thread.currentThread().getName() + "]");
                    Thread.sleep(500); // 较短的执行时间
                    System.out.println("  任务 " + taskId + " 执行完成");
                    taskCounter.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("  完成任务数: " + taskCounter.get());
        System.out.println("  特点: 根据需要创建线程，60秒后回收空闲线程\n");
    }

    /**
     * 测试单线程池
     */
    private static void testSingleThreadExecutor() throws InterruptedException {
        System.out.println("3. 单线程池测试:");

        ExecutorService executor = Executors.newSingleThreadExecutor();

        // 提交多个任务，验证顺序执行
        for (int i = 1; i <= 4; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("  任务 " + taskId + " 执行 [线程: " + Thread.currentThread().getName() + "]");
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("  特点: 单线程顺序执行，保证任务执行顺序\n");
    }

    /**
     * 测试定时任务线程池
     */
    private static void testScheduledThreadPool() throws InterruptedException {
        System.out.println("4. 定时任务线程池测试:");

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        AtomicInteger counter = new AtomicInteger(0);

        // 延迟执行任务
        executor.schedule(() -> {
            System.out.println("  延迟任务执行 (2秒后)");
        }, 2, TimeUnit.SECONDS);

        // 周期性任务
        ScheduledFuture<?> periodicTask = executor.scheduleAtFixedRate(() -> {
            int count = counter.incrementAndGet();
            System.out.println("  周期任务执行 #" + count);
            if (count >= 3) {
                System.out.println("  周期任务完成");
            }
        }, 1, 1, TimeUnit.SECONDS); // 1秒后开始，每1秒执行一次

        // 等待一段时间观察执行
        Thread.sleep(5000);

        // 取消周期任务
        periodicTask.cancel(false);

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println("  特点: 支持延迟和周期性任务调度\n");
    }

    /**
     * 测试自定义线程池
     */
    private static void testCustomThreadPool() throws InterruptedException {
        System.out.println("5. 自定义线程池测试:");

        // 创建自定义线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, // 核心线程数
                4, // 最大线程数
                60L, // 空闲存活时间
                TimeUnit.SECONDS, // 时间单位
                new ArrayBlockingQueue<>(2), // 有界队列，容量为2
                new CustomThreadFactory("Custom"), // 自定义线程工厂
                new ThreadPoolExecutor.CallerRunsPolicy() // 调用者运行策略
        );

        // 提交任务，测试线程池行为
        for (int i = 1; i <= 8; i++) {
            final int taskId = i;
            try {
                executor.submit(() -> {
                    try {
                        System.out.println("  任务 " + taskId + " 开始 [线程: " + Thread.currentThread().getName()
                                + "] 活跃线程: " + executor.getActiveCount());
                        Thread.sleep(2000);
                        System.out.println("  任务 " + taskId + " 完成");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

                // 监控线程池状态
                System.out.println("    提交任务 " + taskId + " - 队列大小: " + executor.getQueue().size() + ", 活跃线程: "
                        + executor.getActiveCount() + ", 线程池大小: " + executor.getPoolSize());

                Thread.sleep(500); // 间隔提交

            } catch (RejectedExecutionException e) {
                System.out.println("    任务 " + taskId + " 被拒绝: " + e.getMessage());
            }
        }

        executor.shutdown();
        executor.awaitTermination(15, TimeUnit.SECONDS);

        System.out.println("  特点: 自定义参数，可观察线程池扩展和拒绝策略\n");
    }

    /**
     * 对比不同线程池的特性
     */
    private static void compareThreadPools() {
        System.out.println("6. 线程池特性对比:");

        System.out.println("┌─────────────────┬──────────┬──────────┬────────────┬──────────────┐");
        System.out.println("│ 线程池类型       │ 核心线程  │ 最大线程  │ 队列类型    │ 适用场景      │");
        System.out.println("├─────────────────┼──────────┼──────────┼────────────┼──────────────┤");
        System.out.println("│ FixedThreadPool │ n        │ n        │ 无界链表    │ 稳定负载      │");
        System.out.println("│ CachedThreadPool│ 0        │ Integer  │ 同步队列    │ 短期异步任务  │");
        System.out.println("│ SingleThread    │ 1        │ 1        │ 无界链表    │ 顺序执行      │");
        System.out.println("│ ScheduledPool   │ n        │ Integer  │ 延迟队列    │ 定时任务      │");
        System.out.println("│ CustomPool      │ 自定义    │ 自定义    │ 自定义      │ 特殊需求      │");
        System.out.println("└─────────────────┴──────────┴──────────┴────────────┴──────────────┘");
        System.out.println();
    }

    /**
     * 自定义线程工厂
     */
    static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        CustomThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, namePrefix + "-Thread-" + threadNumber.getAndIncrement());
            thread.setDaemon(false); // 设置为非守护线程
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }
}