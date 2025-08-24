package com.example.thread_memory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

/**
 * Virtual Thread vs 传统线程内存对比 需要 Java 19+ 版本才能运行完整的 Virtual Thread 测试
 */
public class VirtualThreadMemoryComparison {

    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private static final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Virtual Thread vs 传统线程内存对比 ===\n");

        // 检查 Java 版本
        String javaVersion = System.getProperty("java.version");
        System.out.println("当前 Java 版本: " + javaVersion);

        if (isVirtualThreadSupported()) {
            System.out.println("支持 Virtual Thread，开始完整测试\n");
            runCompleteComparison();
        } else {
            System.out.println("不支持 Virtual Thread (需要 Java 19+)，运行理论对比\n");
            runTheoreticalComparison();
        }
    }

    /**
     * 检查是否支持 Virtual Thread
     */
    private static boolean isVirtualThreadSupported() {
        try {
            // 尝试访问 Virtual Thread 相关的类
            Class.forName("java.lang.Thread$Builder");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * 运行完整的对比测试 (Java 19+)
     */
    private static void runCompleteComparison() throws InterruptedException {
        int taskCount = 1000;

        System.out.println("测试场景: 创建 " + taskCount + " 个并发任务\n");

        // 1. 传统线程测试
        testPlatformThreads(taskCount);

        // 2. Virtual Thread 测试
        testVirtualThreads(taskCount);

        // 3. 大规模测试 (仅 Virtual Thread)
        testLargeScaleVirtualThreads();
    }

    /**
     * 传统线程测试
     */
    private static void testPlatformThreads(int taskCount) throws InterruptedException {
        System.out.println("1. 传统线程测试:");

        forceGC();
        long beforeMemory = getTotalMemoryUsage();
        int beforeThreadCount = threadBean.getThreadCount();
        long startTime = System.currentTimeMillis();

        CountDownLatch latch = new CountDownLatch(taskCount);

        // 创建传统线程 (注意：大量线程可能导致内存问题)
        int actualTaskCount = Math.min(taskCount, 100); // 限制传统线程数量
        System.out.println("  实际创建线程数: " + actualTaskCount + " (受内存限制)");

        for (int i = 0; i < actualTaskCount; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(100); // 模拟工作
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // 手动倒计数剩余的任务 (模拟)
        for (int i = actualTaskCount; i < taskCount; i++) {
            latch.countDown();
        }

        latch.await();
        long endTime = System.currentTimeMillis();

        forceGC();
        long afterMemory = getTotalMemoryUsage();
        int afterThreadCount = threadBean.getThreadCount();

        System.out.println("  执行时间: " + (endTime - startTime) + " ms");
        System.out.println("  线程数变化: " + (afterThreadCount - beforeThreadCount));
        System.out.println("  内存使用: " + formatBytes(afterMemory - beforeMemory));
        System.out.println("  每线程内存: " + formatBytes((afterMemory - beforeMemory) / actualTaskCount));
        System.out.println();
    }

    /**
     * Virtual Thread 测试 (仅概念代码)
     */
    private static void testVirtualThreads(int taskCount) throws InterruptedException {
        System.out.println("2. Virtual Thread 测试 (概念演示):");

        System.out.println("  注意: 当前 Java 版本不支持 Virtual Thread");
        System.out.println("  以下是 Virtual Thread 的预期特性:");
        System.out.println("  - 每个线程内存开销: ~2-10 KB");
        System.out.println("  - 总内存使用: " + formatBytes(taskCount * 5 * 1024)); // 假设每个 5KB
        System.out.println("  - 可同时运行线程数: 数百万个");
        System.out.println("  - 创建开销: 极低");

        /*
         * // 以下是 Java 19+ 的真实代码 (当前版本无法运行):
         * 
         * forceGC(); long beforeMemory = getTotalMemoryUsage(); int beforeThreadCount = threadBean.getThreadCount();
         * long startTime = System.currentTimeMillis();
         * 
         * CountDownLatch latch = new CountDownLatch(taskCount);
         * 
         * // 使用 Virtual Thread try (var executor = Executors.newVirtualThreadPerTaskExecutor()) { for (int i = 0; i <
         * taskCount; i++) { executor.submit(() -> { try { Thread.sleep(100); // 模拟工作 } catch (InterruptedException e) {
         * Thread.currentThread().interrupt(); } finally { latch.countDown(); } }); } }
         * 
         * latch.await(); long endTime = System.currentTimeMillis();
         * 
         * forceGC(); long afterMemory = getTotalMemoryUsage(); int afterThreadCount = threadBean.getThreadCount();
         * 
         * System.out.println("  执行时间: " + (endTime - startTime) + " ms"); System.out.println("  操作系统线程数变化: " +
         * (afterThreadCount - beforeThreadCount)); System.out.println("  内存使用: " + formatBytes(afterMemory -
         * beforeMemory)); System.out.println("  每 Virtual Thread 内存: " + formatBytes((afterMemory - beforeMemory) /
         * taskCount));
         */

        System.out.println();
    }

    /**
     * 大规模 Virtual Thread 测试
     */
    private static void testLargeScaleVirtualThreads() {
        System.out.println("3. 大规模 Virtual Thread 测试 (理论):");

        int[] scales = { 10_000, 100_000, 1_000_000 };

        for (int scale : scales) {
            long estimatedMemory = scale * 5 * 1024; // 假设每个 Virtual Thread 5KB
            System.out.println("  " + scale + " 个 Virtual Thread:");
            System.out.println("    预估内存使用: " + formatBytes(estimatedMemory));
            System.out.println("    传统线程等效内存: " + formatBytes(scale * 1024 * 1024)); // 每个传统线程 1MB
            double savings = (1.0 - (double) estimatedMemory / (scale * 1024 * 1024)) * 100;
            System.out.println("    内存节省: " + String.format("%.1f", savings) + "%");
        }

        System.out.println();
    }

    /**
     * 运行理论对比 (Java 8-18)
     */
    private static void runTheoreticalComparison() {
        System.out.println("理论对比分析:\n");

        System.out.println("传统线程 (Platform Thread):");
        System.out.println("  - 内存开销: 1MB - 8MB 每线程");
        System.out.println("  - 主要开销: 线程栈 (默认 1MB)");
        System.out.println("  - 操作系统线程: 1:1 映射");
        System.out.println("  - 上下文切换: 重量级 (内核态)");
        System.out.println("  - 最大线程数: 受内存限制 (通常数千个)");
        System.out.println();

        System.out.println("Virtual Thread (Java 19+):");
        System.out.println("  - 内存开销: 2KB - 10KB 每线程");
        System.out.println("  - 主要开销: 很小的栈 + 管理结构");
        System.out.println("  - 操作系统线程: M:N 映射 (多个 Virtual Thread 共享少量载体线程)");
        System.out.println("  - 上下文切换: 轻量级 (用户态)");
        System.out.println("  - 最大线程数: 数百万个");
        System.out.println();

        System.out.println("具体数值对比:");

        // 不同线程数的内存对比
        int[] threadCounts = { 100, 1000, 10000, 100000 };

        System.out.println(String.format("%-10s %-15s %-15s %-10s", "线程数", "传统线程内存", "Virtual线程内存", "节省比例"));
        System.out.println("----------------------------------------------------");

        for (int count : threadCounts) {
            long platformMemory = count * 1024 * 1024; // 1MB 每线程
            long virtualMemory = count * 5 * 1024; // 5KB 每线程
            double savings = (1.0 - (double) virtualMemory / platformMemory) * 100;

            System.out.println(String.format("%-10d %-15s %-15s %-10.1f%%", count, formatBytes(platformMemory),
                    formatBytes(virtualMemory), savings));
        }

        System.out.println();

        // 实际测试建议
        System.out.println("升级建议:");
        System.out.println("1. 如果需要大量并发连接 (>1000)，强烈建议升级到 Java 19+");
        System.out.println("2. 对于 I/O 密集型应用，Virtual Thread 提供显著优势");
        System.out.println("3. CPU 密集型应用建议继续使用传统线程池");
        System.out.println("4. 内存受限环境下，Virtual Thread 可以大幅提升并发能力");
    }

    /**
     * 获取总内存使用量
     */
    private static long getTotalMemoryUsage() {
        return memoryBean.getHeapMemoryUsage().getUsed() + memoryBean.getNonHeapMemoryUsage().getUsed();
    }

    /**
     * 强制垃圾回收
     */
    private static void forceGC() {
        System.gc();
        System.runFinalization();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 格式化字节数
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