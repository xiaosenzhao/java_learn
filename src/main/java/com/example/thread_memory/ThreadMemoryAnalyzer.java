package com.example.thread_memory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Java 线程内存开销分析器 实际测量线程创建和运行的内存使用情况
 */
public class ThreadMemoryAnalyzer {

    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private static final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Java 线程内存开销分析 ===\n");

        // 1. 基础内存信息
        printBasicMemoryInfo();

        // 2. 单个线程内存测试
        testSingleThreadMemory();

        // 3. 多线程内存累积测试
        testMultipleThreadsMemory();

        // 4. 不同栈大小的影响 (通过系统属性模拟)
        testStackSizeImpact();

        // 5. 线程池 vs 直接创建线程
        testThreadPoolVsDirectCreation();

        // 6. 线程生命周期内存分析
        testThreadLifecycleMemory();

        System.out.println("\n=== 分析完成 ===");
    }

    /**
     * 打印基础内存信息
     */
    private static void printBasicMemoryInfo() {
        System.out.println("1. 基础内存信息：");

        // JVM 信息
        String jvmName = System.getProperty("java.vm.name");
        String jvmVersion = System.getProperty("java.vm.version");
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");

        System.out.println("JVM: " + jvmName + " " + jvmVersion);
        System.out.println("操作系统: " + osName + " " + osArch);

        // 线程栈大小 (通过创建深度递归来估算)
        long stackSize = estimateStackSize();
        System.out.println("估算线程栈大小: " + formatBytes(stackSize));

        // 当前内存使用
        MemoryUsage heapMemory = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemory = memoryBean.getNonHeapMemoryUsage();

        System.out.println("堆内存使用: " + formatBytes(heapMemory.getUsed()) + " / " + formatBytes(heapMemory.getMax()));
        System.out.println(
                "非堆内存使用: " + formatBytes(nonHeapMemory.getUsed()) + " / " + formatBytes(nonHeapMemory.getMax()));
        System.out.println("当前线程数: " + threadBean.getThreadCount());
        System.out.println();
    }

    /**
     * 估算线程栈大小
     */
    private static long estimateStackSize() {
        try {
            // 通过递归深度来估算栈大小
            // 每次递归大约使用 100-200 字节的栈空间
            return deepRecursion(0) * 150; // 粗略估算
        } catch (StackOverflowError e) {
            // 无法准确估算，返回默认值
            return 1024 * 1024; // 1MB
        }
    }

    private static int deepRecursion(int depth) {
        if (depth > 10000)
            return depth; // 防止真正的栈溢出
        byte[] localArray = new byte[100]; // 占用一些栈空间
        return deepRecursion(depth + 1);
    }

    /**
     * 测试单个线程的内存开销
     */
    private static void testSingleThreadMemory() throws InterruptedException {
        System.out.println("2. 单个线程内存测试：");

        // 强制 GC，获得更准确的内存测量
        forceGC();

        long beforeHeap = memoryBean.getHeapMemoryUsage().getUsed();
        long beforeNonHeap = memoryBean.getNonHeapMemoryUsage().getUsed();
        int beforeThreadCount = threadBean.getThreadCount();

        CountDownLatch latch = new CountDownLatch(1);

        // 创建一个线程
        Thread testThread = new Thread(() -> {
            try {
                // 线程内分配一些对象，模拟实际使用
                List<String> localData = new ArrayList<>();
                for (int i = 0; i < 100; i++) {
                    localData.add("Data-" + i);
                }

                // 等待一段时间，让内存分配稳定
                Thread.sleep(100);
                latch.countDown();

                // 继续运行一段时间
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        testThread.start();
        latch.await(); // 等待线程初始化完成

        // 再次测量内存
        forceGC();
        long afterHeap = memoryBean.getHeapMemoryUsage().getUsed();
        long afterNonHeap = memoryBean.getNonHeapMemoryUsage().getUsed();
        int afterThreadCount = threadBean.getThreadCount();

        testThread.join(); // 等待线程结束

        // 计算差异
        long heapDiff = afterHeap - beforeHeap;
        long nonHeapDiff = afterNonHeap - beforeNonHeap;
        int threadCountDiff = afterThreadCount - beforeThreadCount;

        System.out.println("线程数变化: " + threadCountDiff);
        System.out.println("堆内存变化: " + formatBytes(heapDiff));
        System.out.println("非堆内存变化: " + formatBytes(nonHeapDiff));
        System.out.println("总内存变化: " + formatBytes(heapDiff + nonHeapDiff));
        System.out.println();
    }

    /**
     * 测试多线程内存累积
     */
    private static void testMultipleThreadsMemory() throws InterruptedException {
        System.out.println("3. 多线程内存累积测试：");

        int[] threadCounts = { 5, 10, 20, 50 };

        for (int count : threadCounts) {
            forceGC();
            long beforeMemory = getTotalMemoryUsage();
            int beforeThreadCount = threadBean.getThreadCount();

            CountDownLatch startLatch = new CountDownLatch(count);
            CountDownLatch endLatch = new CountDownLatch(count);
            List<Thread> threads = new ArrayList<>();

            // 创建多个线程
            for (int i = 0; i < count; i++) {
                Thread thread = new Thread(() -> {
                    startLatch.countDown();
                    try {
                        // 模拟一些工作
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        endLatch.countDown();
                    }
                });
                threads.add(thread);
                thread.start();
            }

            startLatch.await(); // 等待所有线程启动

            forceGC();
            long afterMemory = getTotalMemoryUsage();
            int afterThreadCount = threadBean.getThreadCount();

            long memoryDiff = afterMemory - beforeMemory;
            long memoryPerThread = count > 0 ? memoryDiff / count : 0;

            System.out.println(count + " 个线程:");
            System.out.println("  线程数变化: " + (afterThreadCount - beforeThreadCount));
            System.out.println("  总内存变化: " + formatBytes(memoryDiff));
            System.out.println("  平均每线程: " + formatBytes(memoryPerThread));

            endLatch.await(); // 等待所有线程结束

            // 等待线程清理
            for (Thread thread : threads) {
                thread.join();
            }
        }
        System.out.println();
    }

    /**
     * 测试栈大小影响 (模拟)
     */
    private static void testStackSizeImpact() {
        System.out.println("4. 不同栈大小的影响分析：");

        // 由于无法在运行时动态改变栈大小，我们提供理论分析
        long[] stackSizes = { 256 * 1024, 512 * 1024, 1024 * 1024, 2 * 1024 * 1024 }; // 256K, 512K, 1M, 2M
        String[] sizeNames = { "256K", "512K", "1M", "2M" };

        System.out.println("栈大小对线程内存的影响 (理论分析):");
        for (int i = 0; i < stackSizes.length; i++) {
            long totalMemoryFor100Threads = stackSizes[i] * 100;
            System.out.println("  栈大小 " + sizeNames[i] + ": 单线程 " + formatBytes(stackSizes[i]) + ", 100线程 "
                    + formatBytes(totalMemoryFor100Threads));
        }

        System.out.println("注意: 可通过 -Xss 参数调整栈大小，如 -Xss512k");
        System.out.println();
    }

    /**
     * 测试线程池 vs 直接创建线程
     */
    private static void testThreadPoolVsDirectCreation() throws InterruptedException {
        System.out.println("5. 线程池 vs 直接创建线程：");

        int taskCount = 20;
        int poolSize = 5;

        // 直接创建线程
        forceGC();
        long beforeDirect = getTotalMemoryUsage();
        int beforeDirectThreads = threadBean.getThreadCount();

        CountDownLatch directLatch = new CountDownLatch(taskCount);
        List<Thread> directThreads = new ArrayList<>();

        long directStartTime = System.currentTimeMillis();
        for (int i = 0; i < taskCount; i++) {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    directLatch.countDown();
                }
            });
            directThreads.add(thread);
            thread.start();
        }

        directLatch.await();
        long directEndTime = System.currentTimeMillis();

        long afterDirect = getTotalMemoryUsage();
        int afterDirectThreads = threadBean.getThreadCount();

        for (Thread thread : directThreads) {
            thread.join();
        }

        // 线程池方式
        forceGC();
        long beforePool = getTotalMemoryUsage();
        int beforePoolThreads = threadBean.getThreadCount();

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        CountDownLatch poolLatch = new CountDownLatch(taskCount);

        long poolStartTime = System.currentTimeMillis();
        for (int i = 0; i < taskCount; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    poolLatch.countDown();
                }
            });
        }

        poolLatch.await();
        long poolEndTime = System.currentTimeMillis();

        long afterPool = getTotalMemoryUsage();
        int afterPoolThreads = threadBean.getThreadCount();

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // 结果对比
        System.out.println("执行 " + taskCount + " 个任务:");
        System.out.println("直接创建线程:");
        System.out.println("  线程数变化: " + (afterDirectThreads - beforeDirectThreads));
        System.out.println("  内存使用: " + formatBytes(afterDirect - beforeDirect));
        System.out.println("  执行时间: " + (directEndTime - directStartTime) + "ms");

        System.out.println("线程池 (大小=" + poolSize + "):");
        System.out.println("  线程数变化: " + (afterPoolThreads - beforePoolThreads));
        System.out.println("  内存使用: " + formatBytes(afterPool - beforePool));
        System.out.println("  执行时间: " + (poolEndTime - poolStartTime) + "ms");
        System.out.println();
    }

    /**
     * 测试线程生命周期内存分析
     */
    private static void testThreadLifecycleMemory() throws InterruptedException {
        System.out.println("6. 线程生命周期内存分析：");

        CountDownLatch readyLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(1);

        // 线程创建前
        forceGC();
        long beforeCreation = getTotalMemoryUsage();
        System.out.println("线程创建前内存: " + formatBytes(beforeCreation));

        Thread lifecycleThread = new Thread(() -> {
            // 线程启动后
            readyLatch.countDown();

            try {
                // 模拟一些内存分配
                List<byte[]> allocations = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    allocations.add(new byte[1024]); // 分配 1KB
                    Thread.sleep(50);
                }

                finishLatch.await(); // 等待外部信号

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 线程创建后但未启动
        long afterCreation = getTotalMemoryUsage();
        System.out.println("线程创建后内存: " + formatBytes(afterCreation));
        System.out.println("创建开销: " + formatBytes(afterCreation - beforeCreation));

        lifecycleThread.start();
        readyLatch.await(); // 等待线程启动完成

        // 线程运行中
        forceGC();
        long duringExecution = getTotalMemoryUsage();
        System.out.println("线程运行中内存: " + formatBytes(duringExecution));
        System.out.println("运行时额外开销: " + formatBytes(duringExecution - afterCreation));

        finishLatch.countDown(); // 通知线程结束
        lifecycleThread.join();

        // 线程结束后
        forceGC();
        long afterCompletion = getTotalMemoryUsage();
        System.out.println("线程结束后内存: " + formatBytes(afterCompletion));
        System.out.println("结束后释放: " + formatBytes(duringExecution - afterCompletion));
        System.out.println();
    }

    /**
     * 获取总内存使用量
     */
    private static long getTotalMemoryUsage() {
        MemoryUsage heap = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memoryBean.getNonHeapMemoryUsage();
        return heap.getUsed() + nonHeap.getUsed();
    }

    /**
     * 强制垃圾回收
     */
    private static void forceGC() {
        System.gc();
        System.runFinalization();
        try {
            Thread.sleep(100); // 给 GC 一些时间
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 格式化字节数为可读格式
     */
    private static String formatBytes(long bytes) {
        if (bytes < 0)
            return "-" + formatBytes(-bytes);

        String[] units = { "B", "KB", "MB", "GB" };
        int unitIndex = 0;
        double size = bytes;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }
}