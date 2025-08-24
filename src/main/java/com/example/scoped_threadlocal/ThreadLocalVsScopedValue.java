package com.example.scoped_threadlocal;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadLocal vs ScopedValue 详细对比示例 展示两种线程本地存储机制的使用方法和性能差异
 */
public class ThreadLocalVsScopedValue {

    // ThreadLocal 示例
    private static final ThreadLocal<String> threadLocalUser = new ThreadLocal<>();
    private static final ThreadLocal<Integer> threadLocalCounter = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    // ScopedValue 示例 (Java 19+)
    // 注意：这里使用注释形式展示，因为需要 Java 19+ 才能编译
    /*
     * private static final ScopedValue<String> scopedUser = ScopedValue.newInstance(); private static final
     * ScopedValue<String> scopedRequestId = ScopedValue.newInstance();
     */

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ThreadLocal vs ScopedValue 对比演示 ===\n");

        // 1. ThreadLocal 基本使用
        demonstrateThreadLocal();

        // 2. ThreadLocal 内存泄漏风险
        demonstrateThreadLocalMemoryLeak();

        // 3. ThreadLocal 性能测试
        performanceTestThreadLocal();

        // 4. ScopedValue 概念演示（模拟实现）
        demonstrateScopedValueConcept();

        // 5. 继承性对比
        demonstrateInheritance();

        System.out.println("\n=== 演示完成 ===");
    }

    /**
     * ThreadLocal 基本使用演示
     */
    private static void demonstrateThreadLocal() {
        System.out.println("1. ThreadLocal 基本使用：");

        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(3);

        for (int i = 1; i <= 3; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    // 设置 ThreadLocal 值
                    threadLocalUser.set("User-" + threadId);
                    threadLocalCounter.set(threadId * 10);

                    // 模拟业务操作
                    processBusinessLogic();

                    // 读取值
                    System.out.println("线程 " + Thread.currentThread().getName() + ": User=" + threadLocalUser.get()
                            + ", Counter=" + threadLocalCounter.get());
                } finally {
                    // 重要：清理 ThreadLocal
                    threadLocalUser.remove();
                    threadLocalCounter.remove();
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor.shutdown();
        System.out.println();
    }

    /**
     * 模拟业务逻辑处理
     */
    private static void processBusinessLogic() {
        // 在业务逻辑中可以随时访问 ThreadLocal 值
        String user = threadLocalUser.get();
        Integer counter = threadLocalCounter.get();

        if (user != null && counter != null) {
            // 可以修改 ThreadLocal 值
            threadLocalCounter.set(counter + 1);
        }

        // 模拟一些处理时间
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 演示 ThreadLocal 内存泄漏风险
     */
    private static void demonstrateThreadLocalMemoryLeak() {
        System.out.println("2. ThreadLocal 内存泄漏风险演示：");

        ThreadLocal<byte[]> leakyThreadLocal = new ThreadLocal<>();

        // 模拟内存泄漏场景
        Thread leakyThread = new Thread(() -> {
            // 存储大对象但不清理
            leakyThreadLocal.set(new byte[1024 * 1024]); // 1MB
            System.out.println("线程设置了大对象，但没有调用 remove()");
            // 注意：这里故意不调用 remove()，在真实场景中会导致内存泄漏
        });

        leakyThread.start();
        try {
            leakyThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("线程结束，但 ThreadLocal 对象可能仍在内存中");
        System.out.println("正确做法：在 finally 块中调用 remove()\n");
    }

    /**
     * ThreadLocal 性能测试
     */
    private static void performanceTestThreadLocal() {
        System.out.println("3. ThreadLocal 性能测试：");

        int iterations = 1_000_000;
        ThreadLocal<Integer> perfThreadLocal = new ThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return 0;
            }
        };

        // 性能测试
        long startTime = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            perfThreadLocal.set(i);
            Integer value = perfThreadLocal.get();
            // 模拟使用值
            if (value == null) {
                throw new RuntimeException("Unexpected null value");
            }
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // 转换为毫秒

        System.out.println("ThreadLocal " + iterations + " 次操作耗时: " + duration + " ms");

        // 清理
        perfThreadLocal.remove();
        System.out.println();
    }

    /**
     * ScopedValue 概念演示（模拟实现） 注意：这是概念演示，真实的 ScopedValue 需要 Java 19+
     */
    private static void demonstrateScopedValueConcept() {
        System.out.println("4. ScopedValue 概念演示（模拟实现）：");

        // 模拟 ScopedValue 的作用域特性
        String requestId = "REQ-12345";
        String userId = "USER-67890";

        // 模拟 ScopedValue.where() 方法
        simulateScopedValueExecution(requestId, userId, () -> {
            // 在这个作用域内，值是不可变的且自动可用
            System.out.println("处理请求: " + requestId + " for 用户: " + userId);

            // 调用其他方法，值自动传递
            processRequest();

            // 值在整个作用域内保持一致，无需手动清理
        });

        System.out.println("作用域结束，值自动清理\n");
    }

    /**
     * 模拟 ScopedValue 的执行环境
     */
    private static void simulateScopedValueExecution(String requestId, String userId, Runnable task) {
        // 模拟绑定值到作用域
        System.out.println("绑定值到作用域: RequestId=" + requestId + ", UserId=" + userId);

        try {
            // 执行任务
            task.run();
        } finally {
            // 作用域结束，自动清理（无需显式调用）
            System.out.println("作用域结束，自动清理");
        }
    }

    /**
     * 模拟在 ScopedValue 作用域内的方法调用
     */
    private static void processRequest() {
        // 在真实的 ScopedValue 中，这里可以直接访问作用域内的值
        System.out.println("  -> 处理业务逻辑，自动获取作用域内的值");
        System.out.println("  -> 值是不可变的，无法意外修改");
    }

    /**
     * 演示继承性对比
     */
    private static void demonstrateInheritance() {
        System.out.println("5. 继承性对比：");

        // ThreadLocal - 子线程无法继承
        ThreadLocal<String> normalThreadLocal = new ThreadLocal<>();
        normalThreadLocal.set("Parent-Value");

        Thread childThread1 = new Thread(() -> {
            String value = normalThreadLocal.get();
            System.out.println("普通 ThreadLocal 在子线程中的值: " + value + " (null - 无法继承)");
        });

        // InheritableThreadLocal - 子线程可以继承
        InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();
        inheritableThreadLocal.set("Inheritable-Value");

        Thread childThread2 = new Thread(() -> {
            String value = inheritableThreadLocal.get();
            System.out.println("InheritableThreadLocal 在子线程中的值: " + value + " (继承成功)");
        });

        try {
            childThread1.start();
            childThread1.join();

            childThread2.start();
            childThread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 清理
        normalThreadLocal.remove();
        inheritableThreadLocal.remove();

        System.out.println("ScopedValue 默认支持结构化继承，无需特殊处理");
    }
}

/**
 * ThreadLocal 最佳实践工具类
 */
class ThreadLocalUtils {

    /**
     * 安全的 ThreadLocal 操作模板
     */
    public static <T> void executeWithThreadLocal(ThreadLocal<T> threadLocal, T value, Runnable task) {
        threadLocal.set(value);
        try {
            task.run();
        } finally {
            threadLocal.remove(); // 确保清理
        }
    }

    /**
     * 带返回值的安全 ThreadLocal 操作
     */
    public static <T, R> R executeWithThreadLocal(ThreadLocal<T> threadLocal, T value, Callable<R> task) {
        threadLocal.set(value);
        try {
            return task.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            threadLocal.remove(); // 确保清理
        }
    }
}

/**
 * 模拟现代化的作用域值管理器 这是对 ScopedValue 概念的简化实现
 */
class ScopedValueManager {
    private static final ThreadLocal<ScopedContext> CONTEXT = new ThreadLocal<>();

    public static <T> void runWithScope(String key, T value, Runnable task) {
        ScopedContext oldContext = CONTEXT.get();
        ScopedContext newContext = new ScopedContext(oldContext);
        newContext.put(key, value);

        CONTEXT.set(newContext);
        try {
            task.run();
        } finally {
            CONTEXT.set(oldContext);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        ScopedContext context = CONTEXT.get();
        return context != null ? (T) context.get(key) : null;
    }

    private static class ScopedContext {
        private final ScopedContext parent;
        private final java.util.Map<String, Object> values = new java.util.HashMap<>();

        ScopedContext(ScopedContext parent) {
            this.parent = parent;
        }

        void put(String key, Object value) {
            values.put(key, value);
        }

        Object get(String key) {
            Object value = values.get(key);
            if (value != null) {
                return value;
            }
            return parent != null ? parent.get(key) : null;
        }
    }
}