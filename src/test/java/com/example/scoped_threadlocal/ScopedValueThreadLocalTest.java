package com.example.scoped_threadlocal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ScopedValue 和 ThreadLocal 功能测试
 */
public class ScopedValueThreadLocalTest {

    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        executor = Executors.newFixedThreadPool(4);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    void testThreadLocalIsolation() throws InterruptedException {
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        CountDownLatch latch = new CountDownLatch(3);
        AtomicReference<String> thread1Value = new AtomicReference<>();
        AtomicReference<String> thread2Value = new AtomicReference<>();
        AtomicReference<String> thread3Value = new AtomicReference<>();

        // 线程1
        executor.submit(() -> {
            try {
                threadLocal.set("Value-1");
                Thread.sleep(50); // 让其他线程有机会设置值
                thread1Value.set(threadLocal.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                threadLocal.remove();
                latch.countDown();
            }
        });

        // 线程2
        executor.submit(() -> {
            try {
                threadLocal.set("Value-2");
                Thread.sleep(50);
                thread2Value.set(threadLocal.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                threadLocal.remove();
                latch.countDown();
            }
        });

        // 线程3
        executor.submit(() -> {
            try {
                threadLocal.set("Value-3");
                Thread.sleep(50);
                thread3Value.set(threadLocal.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                threadLocal.remove();
                latch.countDown();
            }
        });

        latch.await();

        // 验证每个线程的值都是独立的
        assertEquals("Value-1", thread1Value.get());
        assertEquals("Value-2", thread2Value.get());
        assertEquals("Value-3", thread3Value.get());

        System.out.println("✓ ThreadLocal 隔离性测试通过");
    }

    @Test
    void testInheritableThreadLocal() throws InterruptedException {
        InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();
        CountDownLatch latch = new CountDownLatch(2);
        AtomicReference<String> parentValue = new AtomicReference<>();
        AtomicReference<String> childValue = new AtomicReference<>();

        // 父线程
        Thread parentThread = new Thread(() -> {
            try {
                inheritableThreadLocal.set("Parent-Value");
                parentValue.set(inheritableThreadLocal.get());

                // 创建子线程
                Thread childThread = new Thread(() -> {
                    try {
                        // 子线程应该能够继承父线程的值
                        childValue.set(inheritableThreadLocal.get());
                    } finally {
                        latch.countDown();
                    }
                });

                childThread.start();
                childThread.join();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                inheritableThreadLocal.remove();
                latch.countDown();
            }
        });

        parentThread.start();
        latch.await();

        assertEquals("Parent-Value", parentValue.get());
        assertEquals("Parent-Value", childValue.get());

        System.out.println("✓ InheritableThreadLocal 继承性测试通过");
    }

    @Test
    void testScopedValueManager() {
        AtomicReference<String> capturedValue = new AtomicReference<>();

        // 使用 ScopedValueManager 模拟 ScopedValue 行为
        ScopedValueManager.runWithScope("testKey", "testValue", () -> {
            String value = ScopedValueManager.get("testKey");
            capturedValue.set(value);

            // 嵌套作用域测试
            ScopedValueManager.runWithScope("innerKey", "innerValue", () -> {
                String outerValue = ScopedValueManager.get("testKey");
                String innerValue = ScopedValueManager.get("innerKey");

                assertEquals("testValue", outerValue);
                assertEquals("innerValue", innerValue);
            });

            // 内层作用域结束后，内层值应该不可访问
            String innerValueAfterScope = ScopedValueManager.get("innerKey");
            assertNull(innerValueAfterScope);
        });

        assertEquals("testValue", capturedValue.get());

        // 作用域外应该无法访问
        String valueOutsideScope = ScopedValueManager.get("testKey");
        assertNull(valueOutsideScope);

        System.out.println("✓ ScopedValueManager 作用域测试通过");
    }

    @Test
    void testThreadLocalPerformance() {
        ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return 0;
            }
        };

        int iterations = 100_000;
        long startTime = System.nanoTime();

        try {
            for (int i = 0; i < iterations; i++) {
                threadLocal.set(i);
                Integer value = threadLocal.get();
                assertNotNull(value);
            }
        } finally {
            threadLocal.remove();
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // 转换为毫秒

        System.out.println("ThreadLocal " + iterations + " 次操作耗时: " + duration + " ms");
        assertTrue(duration < 1000, "性能测试：操作应该在1秒内完成");

        System.out.println("✓ ThreadLocal 性能测试通过");
    }

    @Test
    void testThreadLocalUtils() {
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        AtomicReference<String> capturedValue = new AtomicReference<>();

        // 测试安全的 ThreadLocal 操作工具
        ThreadLocalUtils.executeWithThreadLocal(threadLocal, "test-value", () -> {
            String value = threadLocal.get();
            capturedValue.set(value);
        });

        assertEquals("test-value", capturedValue.get());

        // 验证 ThreadLocal 已经被清理
        assertNull(threadLocal.get());

        System.out.println("✓ ThreadLocalUtils 工具类测试通过");
    }

    @Test
    void testThreadLocalWithReturnValue() {
        ThreadLocal<String> threadLocal = new ThreadLocal<>();

        // 测试带返回值的安全操作
        String result = ThreadLocalUtils.executeWithThreadLocal(threadLocal, "input-value", () -> {
            String value = threadLocal.get();
            return "processed-" + value;
        });

        assertEquals("processed-input-value", result);

        // 验证 ThreadLocal 已经被清理
        assertNull(threadLocal.get());

        System.out.println("✓ ThreadLocalUtils 返回值测试通过");
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        ThreadLocal<AtomicInteger> threadLocal = new ThreadLocal<AtomicInteger>() {
            @Override
            protected AtomicInteger initialValue() {
                return new AtomicInteger(0);
            }
        };

        int threadCount = 5;
        int incrementsPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger totalSum = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    AtomicInteger counter = threadLocal.get();
                    for (int j = 0; j < incrementsPerThread; j++) {
                        counter.incrementAndGet();
                    }
                    totalSum.addAndGet(counter.get());
                } finally {
                    threadLocal.remove();
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 每个线程应该独立计数到 incrementsPerThread
        int expectedTotal = threadCount * incrementsPerThread;
        assertEquals(expectedTotal, totalSum.get());

        System.out.println("✓ 并发访问测试通过 - 总计数: " + totalSum.get());
    }
}