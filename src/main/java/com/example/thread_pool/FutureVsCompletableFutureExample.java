package com.example.thread_pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Future vs CompletableFuture 详细对比示例 通过实际代码演示两者在各个方面的差异
 */
public class FutureVsCompletableFutureExample {

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws Exception {
        System.out.println("=== Future vs CompletableFuture 对比演示 ===\n");

        try {
            // 1. 基本使用对比
            compareBasicUsage();

            // 2. 阻塞 vs 非阻塞对比
            compareBlockingVsNonBlocking();

            // 3. 异常处理对比
            compareExceptionHandling();

            // 4. 任务组合对比
            compareTaskCombination();

            // 5. 链式操作对比
            compareChainingOperations();

            // 6. 批量处理对比
            compareBatchProcessing();

            // 7. 性能对比
            comparePerformance();

        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }

        System.out.println("=== 对比演示完成 ===");
    }

    /**
     * 1. 基本使用对比
     */
    private static void compareBasicUsage() throws Exception {
        System.out.println("1. 基本使用对比:");

        System.out.println("  Future 方式:");
        // Future 基本使用
        Future<String> future = executor.submit(() -> {
            sleep(1000);
            return "Future 结果";
        });

        System.out.println("    任务已提交，等待结果...");
        String futureResult = future.get(); // 阻塞等待
        System.out.println("    " + futureResult);

        System.out.println("\n  CompletableFuture 方式:");
        // CompletableFuture 基本使用
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            sleep(1000);
            return "CompletableFuture 结果";
        }, executor);

        System.out.println("    任务已提交，等待结果...");
        String cfResult = completableFuture.get(); // 也可以阻塞等待
        System.out.println("    " + cfResult);

        System.out.println("  对比: 基本用法相似，但 CompletableFuture 提供更多选项\n");
    }

    /**
     * 2. 阻塞 vs 非阻塞对比
     */
    private static void compareBlockingVsNonBlocking() throws Exception {
        System.out.println("2. 阻塞 vs 非阻塞对比:");

        System.out.println("  Future - 只能阻塞等待:");
        long startTime = System.currentTimeMillis();

        Future<String> future = executor.submit(() -> {
            sleep(1500);
            return "Future 慢任务完成";
        });

        System.out.println("    等待 Future 结果...");
        String result = future.get(); // 必须阻塞等待
        System.out.println("    " + result);
        System.out.println("    阻塞等待时间: " + (System.currentTimeMillis() - startTime) + "ms");

        System.out.println("\n  CompletableFuture - 支持非阻塞回调:");
        final long cfStartTime = System.currentTimeMillis();

        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            sleep(1500);
            return "CompletableFuture 慢任务完成";
        }, executor);

        // 注册回调，非阻塞
        cf.thenAccept(result2 -> {
            System.out.println("    异步回调: " + result2);
            System.out.println("    回调时间: " + (System.currentTimeMillis() - cfStartTime) + "ms");
        });

        System.out.println("    主线程立即继续执行");
        System.out.println("    主线程时间: " + (System.currentTimeMillis() - cfStartTime) + "ms");

        // 等待回调完成
        cf.get();
        System.out.println("  对比: CompletableFuture 支持真正的非阻塞编程\n");
    }

    /**
     * 3. 异常处理对比
     */
    private static void compareExceptionHandling() throws Exception {
        System.out.println("3. 异常处理对比:");

        System.out.println("  Future - 手动异常处理:");
        Future<String> futureWithError = executor.submit(() -> {
            sleep(500);
            if (Math.random() > 0.5) {
                throw new RuntimeException("Future 模拟异常");
            }
            return "Future 成功";
        });

        try {
            String result = futureWithError.get();
            System.out.println("    Future 结果: " + result);
        } catch (ExecutionException e) {
            System.out.println("    Future 异常: " + e.getCause().getMessage());
        }

        System.out.println("\n  CompletableFuture - 声明式异常处理:");
        CompletableFuture<String> cfWithError = CompletableFuture.supplyAsync(() -> {
            sleep(500);
            if (Math.random() > 0.5) {
                throw new RuntimeException("CompletableFuture 模拟异常");
            }
            return "CompletableFuture 成功";
        }, executor).exceptionally(ex -> {
            System.out.println("    CompletableFuture 异常处理: " + ex.getMessage());
            return "CompletableFuture 默认值";
        }).thenApply(result -> "处理后的结果: " + result);

        String cfResult = cfWithError.get();
        System.out.println("    最终结果: " + cfResult);

        System.out.println("  对比: CompletableFuture 异常处理更优雅、更函数式\n");
    }

    /**
     * 4. 任务组合对比
     */
    private static void compareTaskCombination() throws Exception {
        System.out.println("4. 任务组合对比:");

        System.out.println("  Future - 手动组合:");
        Future<String> future1 = executor.submit(() -> {
            sleep(800);
            return "任务1";
        });

        Future<String> future2 = executor.submit(() -> {
            sleep(1000);
            return "任务2";
        });

        // 手动等待和组合
        String result1 = future1.get();
        String result2 = future2.get();
        String combined = "组合: " + result1 + " + " + result2;
        System.out.println("    " + combined);

        System.out.println("\n  CompletableFuture - 声明式组合:");
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            sleep(800);
            return "任务A";
        }, executor);

        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
            sleep(1000);
            return "任务B";
        }, executor);

        // 声明式组合
        CompletableFuture<String> cfCombined = cf1.thenCombine(cf2, (s1, s2) -> "组合: " + s1 + " + " + s2);

        String cfResult = cfCombined.get();
        System.out.println("    " + cfResult);

        System.out.println("  对比: CompletableFuture 组合更简洁，支持并行执行\n");
    }

    /**
     * 5. 链式操作对比
     */
    private static void compareChainingOperations() throws Exception {
        System.out.println("5. 链式操作对比:");

        System.out.println("  Future - 不支持链式操作:");
        Future<String> step1 = executor.submit(() -> {
            return "hello";
        });

        String intermediate = step1.get();

        Future<String> step2 = executor.submit(() -> {
            return intermediate.toUpperCase();
        });

        String step2Result = step2.get();

        Future<String> step3 = executor.submit(() -> {
            return "PREFIX_" + step2Result;
        });

        String finalResult = step3.get();
        System.out.println("    最终结果: " + finalResult);

        System.out.println("\n  CompletableFuture - 支持链式操作:");
        CompletableFuture<String> chain = CompletableFuture.supplyAsync(() -> {
            System.out.println("    步骤1: 获取初始值");
            return "hello";
        }, executor).thenApply(s -> {
            System.out.println("    步骤2: 转换为大写");
            return s.toUpperCase();
        }).thenApply(s -> {
            System.out.println("    步骤3: 添加前缀");
            return "PREFIX_" + s;
        }).thenCompose(s -> {
            System.out.println("    步骤4: 异步后处理");
            return CompletableFuture.supplyAsync(() -> s + "_SUFFIX", executor);
        });

        String chainResult = chain.get();
        System.out.println("    最终结果: " + chainResult);

        System.out.println("  对比: CompletableFuture 链式调用使代码更流畅\n");
    }

    /**
     * 6. 批量处理对比
     */
    private static void compareBatchProcessing() throws Exception {
        System.out.println("6. 批量处理对比:");

        System.out.println("  Future - 手动批量处理:");
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            futures.add(executor.submit(() -> {
                sleep(500 + taskId * 100);
                return taskId * taskId;
            }));
        }

        List<Integer> results = new ArrayList<>();
        for (Future<Integer> future : futures) {
            results.add(future.get());
        }
        System.out.println("    Future 批量结果: " + results);

        System.out.println("\n  CompletableFuture - 更灵活的批量处理:");
        CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            sleep(600);
            return 1;
        }, executor);
        CompletableFuture<Integer> cf2 = CompletableFuture.supplyAsync(() -> {
            sleep(700);
            return 4;
        }, executor);
        CompletableFuture<Integer> cf3 = CompletableFuture.supplyAsync(() -> {
            sleep(800);
            return 9;
        }, executor);

        // 等待所有完成
        CompletableFuture<Void> allOf = CompletableFuture.allOf(cf1, cf2, cf3);
        allOf.get();

        List<Integer> cfResults = java.util.Arrays.asList(cf1.get(), cf2.get(), cf3.get());
        System.out.println("    CompletableFuture 批量结果: " + cfResults);

        // anyOf 示例
        CompletableFuture<Integer> fast1 = CompletableFuture.supplyAsync(() -> {
            sleep(300);
            return 100;
        }, executor);
        CompletableFuture<Integer> fast2 = CompletableFuture.supplyAsync(() -> {
            sleep(500);
            return 200;
        }, executor);

        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(fast1, fast2);
        System.out.println("    最先完成的任务结果: " + anyOf.get());

        System.out.println("  对比: CompletableFuture 提供 allOf/anyOf 等批量操作\n");
    }

    /**
     * 7. 性能对比
     */
    private static void comparePerformance() throws Exception {
        System.out.println("7. 性能对比:");

        int iterations = 1000;

        // Future 性能测试
        System.out.println("  测试 Future 性能 (" + iterations + " 次):");
        long startTime = System.currentTimeMillis();

        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            final int value = i;
            futures.add(executor.submit(() -> value * 2));
        }

        // 等待所有完成
        int futureSum = 0;
        for (Future<Integer> future : futures) {
            futureSum += future.get();
        }

        long futureTime = System.currentTimeMillis() - startTime;
        System.out.println("    Future 耗时: " + futureTime + "ms, 结果总和: " + futureSum);

        // CompletableFuture 性能测试
        System.out.println("\n  测试 CompletableFuture 性能 (" + iterations + " 次):");
        startTime = System.currentTimeMillis();

        List<CompletableFuture<Integer>> completableFutures = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            final int value = i;
            completableFutures.add(CompletableFuture.supplyAsync(() -> value * 2, executor));
        }

        // 等待所有完成
        CompletableFuture<Void> allDone = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));
        allDone.get();

        int cfSum = completableFutures.stream().mapToInt(cf -> cf.join()).sum();

        long cfTime = System.currentTimeMillis() - startTime;
        System.out.println("    CompletableFuture 耗时: " + cfTime + "ms, 结果总和: " + cfSum);

        System.out.println("\n  性能分析:");
        if (futureTime < cfTime) {
            System.out.println("    Future 在简单场景下性能更好 (轻量级)");
        } else {
            System.out.println("    CompletableFuture 性能相当或更好");
        }
        System.out.println("    CompletableFuture 在复杂场景下优势明显");

        System.out.println("\n  特性总结:");
        System.out.println("    Future: 简单、轻量、阻塞式");
        System.out.println("    CompletableFuture: 功能丰富、链式操作、非阻塞回调");
        System.out.println();
    }

    /**
     * 工具方法：模拟耗时操作
     */
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}