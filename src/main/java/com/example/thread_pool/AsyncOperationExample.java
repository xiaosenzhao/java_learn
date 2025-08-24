package com.example.thread_pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * 异步操作示例 演示 Future、CompletableFuture 等异步编程模式
 */
public class AsyncOperationExample {

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws Exception {
        System.out.println("=== Java 异步操作示例 ===\n");

        try {
            // 1. Fire-and-Forget 模式
            demonstrateFireAndForget();

            // 2. Future 基础用法
            demonstrateFutureBasics();

            // 3. Future 批量处理
            demonstrateBatchFutures();

            // 4. CompletableFuture 基础
            demonstrateCompletableFutureBasics();

            // 5. CompletableFuture 链式操作
            demonstrateCompletableFutureChaining();

            // 6. CompletableFuture 组合操作
            demonstrateCompletableFutureCombining();

            // 7. 异常处理
            demonstrateExceptionHandling();

            // 8. 超时处理
            demonstrateTimeoutHandling();

        } finally {
            // 关闭线程池
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }

        System.out.println("=== 所有异步操作示例完成 ===");
    }

    /**
     * Fire-and-Forget 模式演示
     */
    private static void demonstrateFireAndForget() throws InterruptedException {
        System.out.println("1. Fire-and-Forget 模式:");

        // 提交任务，不关心结果
        executor.execute(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("  异步任务1完成 [线程: " + Thread.currentThread().getName() + "]");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executor.execute(() -> {
            try {
                Thread.sleep(800);
                System.out.println("  异步任务2完成 [线程: " + Thread.currentThread().getName() + "]");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        System.out.println("  任务已提交，主线程继续执行");
        Thread.sleep(1500); // 等待任务完成
        System.out.println("  特点: 提交即忘，不等待结果\n");
    }

    /**
     * Future 基础用法演示
     */
    private static void demonstrateFutureBasics() throws Exception {
        System.out.println("2. Future 基础用法:");

        // 提交有返回值的任务
        Future<String> future1 = executor.submit(() -> {
            try {
                Thread.sleep(1000);
                return "任务1结果";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "任务1被中断";
            }
        });

        Future<Integer> future2 = executor.submit(() -> {
            try {
                Thread.sleep(800);
                return 42;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return -1;
            }
        });

        System.out.println("  任务已提交，主线程可以做其他事情");

        // 获取结果（阻塞等待）
        String result1 = future1.get();
        Integer result2 = future2.get();

        System.out.println("  Future1结果: " + result1);
        System.out.println("  Future2结果: " + result2);
        System.out.println("  特点: 可以获取异步任务的返回值\n");
    }

    /**
     * Future 批量处理演示
     */
    private static void demonstrateBatchFutures() throws Exception {
        System.out.println("3. Future 批量处理:");

        List<Future<Integer>> futures = new ArrayList<>();

        // 批量提交任务
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            Future<Integer> future = executor.submit(() -> {
                try {
                    Thread.sleep(500 + taskId * 100); // 不同的执行时间
                    return taskId * taskId;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return -1;
                }
            });
            futures.add(future);
        }

        System.out.println("  已提交5个批量任务");

        // 收集所有结果
        List<Integer> results = new ArrayList<>();
        for (int i = 0; i < futures.size(); i++) {
            try {
                Integer result = futures.get(i).get(2, TimeUnit.SECONDS); // 2秒超时
                results.add(result);
                System.out.println("  任务" + (i + 1) + "结果: " + result);
            } catch (TimeoutException e) {
                System.out.println("  任务" + (i + 1) + "超时");
                results.add(-1);
            }
        }

        System.out.println("  所有结果: " + results);
        System.out.println("  特点: 批量提交，逐个收集结果\n");
    }

    /**
     * CompletableFuture 基础演示
     */
    private static void demonstrateCompletableFutureBasics() throws Exception {
        System.out.println("4. CompletableFuture 基础:");

        // 异步供应
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                return "CompletableFuture结果";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "被中断";
            }
        }, executor);

        // 异步运行（无返回值）
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(800);
                System.out.println("  CompletableFuture异步任务完成");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, executor);

        // 等待完成
        String result = future1.get();
        future2.get();

        System.out.println("  结果: " + result);
        System.out.println("  特点: 更现代的异步编程API\n");
    }

    /**
     * CompletableFuture 链式操作演示
     */
    private static void demonstrateCompletableFutureChaining() throws Exception {
        System.out.println("5. CompletableFuture 链式操作:");

        CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> {
            System.out.println("  步骤1: 获取原始数据");
            return "hello";
        }, executor).thenApply(s -> {
            System.out.println("  步骤2: 转换为大写");
            return s.toUpperCase();
        }).thenApply(s -> {
            System.out.println("  步骤3: 添加前缀");
            return "PREFIX_" + s;
        }).thenCompose(s -> {
            System.out.println("  步骤4: 异步处理");
            return CompletableFuture.supplyAsync(() -> s + "_SUFFIX", executor);
        }).whenComplete((res, ex) -> {
            if (ex == null) {
                System.out.println("  处理完成: " + res);
            } else {
                System.out.println("  处理失败: " + ex.getMessage());
            }
        });

        String finalResult = result.get();
        System.out.println("  最终结果: " + finalResult);
        System.out.println("  特点: 支持链式调用，代码更简洁\n");
    }

    /**
     * CompletableFuture 组合操作演示
     */
    private static void demonstrateCompletableFutureCombining() throws Exception {
        System.out.println("6. CompletableFuture 组合操作:");

        // 并行执行两个异步任务
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            sleep(1000);
            return "任务1";
        }, executor);

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(1200);
            return "任务2";
        }, executor);

        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> {
            sleep(800);
            return 100;
        }, executor);

        // 组合两个Future的结果
        CompletableFuture<String> combined = future1.thenCombine(future2, (s1, s2) -> {
            System.out.println("  组合结果: " + s1 + " + " + s2);
            return s1 + " 和 " + s2;
        });

        // 等待所有任务完成
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(future1, future2, future3);
        allTasks.get();

        System.out.println("  组合结果: " + combined.get());
        System.out.println("  Future3结果: " + future3.get());

        // anyOf 示例：等待任何一个完成
        CompletableFuture<String> fastTask1 = CompletableFuture.supplyAsync(() -> {
            sleep(500);
            return "快速任务1";
        }, executor);

        CompletableFuture<String> fastTask2 = CompletableFuture.supplyAsync(() -> {
            sleep(800);
            return "快速任务2";
        }, executor);

        CompletableFuture<Object> anyResult = CompletableFuture.anyOf(fastTask1, fastTask2);
        System.out.println("  最快完成的任务: " + anyResult.get());
        System.out.println("  特点: 支持多种组合模式\n");
    }

    /**
     * 异常处理演示
     */
    private static void demonstrateExceptionHandling() throws Exception {
        System.out.println("7. 异常处理:");

        // 正常情况
        CompletableFuture<String> normalFuture = CompletableFuture.supplyAsync(() -> "正常结果", executor)
                .exceptionally(ex -> {
                    System.out.println("  处理异常: " + ex.getMessage());
                    return "默认值";
                });

        System.out.println("  正常结果: " + normalFuture.get());

        // 异常情况
        CompletableFuture<String> errorFuture = CompletableFuture.supplyAsync((Supplier<String>) () -> {
            throw new RuntimeException("模拟异常");
        }, executor).exceptionally(ex -> {
            System.out.println("  捕获到异常: " + ex.getCause().getMessage());
            return "异常恢复值";
        });

        System.out.println("  异常处理结果: " + errorFuture.get());

        // 使用 handle 处理正常和异常情况
        CompletableFuture<String> handledFuture = CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.5) {
                throw new RuntimeException("随机异常");
            }
            return "随机成功";
        }, executor).handle((result, ex) -> {
            if (ex != null) {
                System.out.println("  handle处理异常: " + ex.getCause().getMessage());
                return "handle恢复值";
            } else {
                System.out.println("  handle处理正常结果: " + result);
                return result;
            }
        });

        System.out.println("  handle结果: " + handledFuture.get());
        System.out.println("  特点: 完善的异常处理机制\n");
    }

    /**
     * 超时处理演示
     */
    private static void demonstrateTimeoutHandling() throws Exception {
        System.out.println("8. 超时处理:");

        // 正常完成的任务
        CompletableFuture<String> quickTask = CompletableFuture.supplyAsync(() -> {
            sleep(500);
            return "快速完成";
        }, executor);

        try {
            String result = quickTask.get(1, TimeUnit.SECONDS);
            System.out.println("  快速任务结果: " + result);
        } catch (TimeoutException e) {
            System.out.println("  快速任务超时");
        }

        // 超时的任务
        CompletableFuture<String> slowTask = CompletableFuture.supplyAsync(() -> {
            sleep(2000);
            return "慢速完成";
        }, executor);

        try {
            String result = slowTask.get(1, TimeUnit.SECONDS);
            System.out.println("  慢速任务结果: " + result);
        } catch (TimeoutException e) {
            System.out.println("  慢速任务超时，取消任务");
            slowTask.cancel(true);
        }

        // Java 9+ 的 orTimeout (模拟实现)
        CompletableFuture<String> timeoutTask = CompletableFuture.supplyAsync(() -> {
            sleep(1500);
            return "可能超时的任务";
        }, executor);

        // 模拟 orTimeout 功能
        CompletableFuture<String> timeoutResult = timeoutTask.exceptionally(ex -> {
            if (ex instanceof CancellationException) {
                return "任务被取消";
            }
            return "其他异常: " + ex.getMessage();
        });

        // 使用定时器实现超时
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            if (!timeoutTask.isDone()) {
                timeoutTask.cancel(true);
            }
        }, 1, TimeUnit.SECONDS);

        System.out.println("  超时任务结果: " + timeoutResult.get());

        scheduler.shutdown();
        System.out.println("  特点: 支持超时控制，避免无限等待\n");
    }

    /**
     * 工具方法：sleep
     */
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}