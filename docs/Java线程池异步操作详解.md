# Java 线程池异步操作详解

## 概述

线程池是 Java 并发编程的核心概念，它通过复用线程来减少线程创建和销毁的开销，提高系统性能和资源利用率。结合异步操作，可以构建高效的并发应用。

## 为什么需要线程池？

### 传统线程的问题
1. **创建开销**: 每个线程约需 1-8MB 内存
2. **管理复杂**: 需要手动管理线程生命周期
3. **资源浪费**: 线程用完即销毁，无法复用
4. **性能瓶颈**: 频繁创建销毁影响性能

### 线程池的优势
1. **线程复用**: 减少创建销毁开销
2. **资源控制**: 限制并发线程数量
3. **管理简化**: 自动管理线程生命周期
4. **性能提升**: 预创建线程，响应更快
5. **任务队列**: 缓冲待执行任务

## Java 线程池体系

### 核心接口
```java
Executor -> ExecutorService -> AbstractExecutorService -> ThreadPoolExecutor
```

### 主要类型

#### 1. ThreadPoolExecutor
**核心线程池实现**，提供最灵活的配置选项。

#### 2. ScheduledThreadPoolExecutor
**定时任务线程池**，支持延迟和周期性任务。

#### 3. ForkJoinPool
**工作窃取线程池**，适合递归分治任务。

## ThreadPoolExecutor 详解

### 核心参数
```java
ThreadPoolExecutor(
    int corePoolSize,           // 核心线程数
    int maximumPoolSize,        // 最大线程数
    long keepAliveTime,         // 空闲存活时间
    TimeUnit unit,              // 时间单位
    BlockingQueue<Runnable> workQueue,  // 工作队列
    ThreadFactory threadFactory,         // 线程工厂
    RejectedExecutionHandler handler     // 拒绝策略
)
```

### 执行流程
1. **核心线程**: 任务到达时，如果核心线程未满，创建新线程
2. **队列缓冲**: 核心线程满时，任务进入队列等待
3. **扩展线程**: 队列满时，创建非核心线程（直到最大线程数）
4. **拒绝处理**: 达到最大线程数且队列满时，执行拒绝策略

### 工作队列类型

#### 1. ArrayBlockingQueue
- **特点**: 有界队列，基于数组
- **适用**: 资源有限，需要控制内存使用

#### 2. LinkedBlockingQueue
- **特点**: 可选有界，基于链表
- **适用**: 任务量不确定的场景

#### 3. SynchronousQueue
- **特点**: 无缓冲队列，直接传递
- **适用**: 快速响应场景

#### 4. PriorityBlockingQueue
- **特点**: 优先级队列，支持任务排序
- **适用**: 有优先级需求的场景

### 拒绝策略

#### 1. AbortPolicy (默认)
抛出 `RejectedExecutionException` 异常

#### 2. CallerRunsPolicy
调用者线程执行任务

#### 3. DiscardPolicy
静默丢弃任务

#### 4. DiscardOldestPolicy
丢弃最老的任务

## Executors 工厂类

### 预定义线程池

#### 1. newFixedThreadPool
```java
// 固定大小线程池
ExecutorService executor = Executors.newFixedThreadPool(5);
```
- **特点**: 固定线程数，无界队列
- **适用**: 负载较稳定的场景

#### 2. newCachedThreadPool
```java
// 缓存线程池
ExecutorService executor = Executors.newCachedThreadPool();
```
- **特点**: 无核心线程，60秒回收，SynchronousQueue
- **适用**: 大量短期异步任务

#### 3. newSingleThreadExecutor
```java
// 单线程池
ExecutorService executor = Executors.newSingleThreadExecutor();
```
- **特点**: 单线程，保证任务顺序执行
- **适用**: 需要顺序处理的场景

#### 4. newScheduledThreadPool
```java
// 定时任务线程池
ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
```
- **特点**: 支持延迟和周期性任务
- **适用**: 定时任务调度

## 异步操作模式

### 1. Fire-and-Forget (提交即忘)
```java
executor.execute(() -> {
    // 异步任务，不关心结果
});
```

### 2. Future 模式
```java
Future<String> future = executor.submit(() -> {
    // 有返回值的异步任务
    return "result";
});
String result = future.get(); // 阻塞等待结果
```

### 3. CompletableFuture 模式
```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> "result", executor)
    .thenApply(s -> s.toUpperCase())
    .thenCompose(s -> anotherAsyncOperation(s));
```

## 最佳实践

### 1. 线程池大小设置

#### CPU 密集型任务
```java
int cores = Runtime.getRuntime().availableProcessors();
int threadCount = cores; // 或 cores + 1
```

#### I/O 密集型任务
```java
int cores = Runtime.getRuntime().availableProcessors();
int threadCount = cores * 2; // 或根据 I/O 比例调整
```

#### 混合型任务
```java
// 根据 CPU 时间和等待时间比例
int threadCount = cores * (1 + waitTime / cpuTime);
```

### 2. 队列大小设置
- **有界队列**: 防止内存溢出，便于背压控制
- **队列大小**: 通常设置为线程数的 2-4 倍

### 3. 线程命名
```java
ThreadFactory factory = new ThreadFactoryBuilder()
    .setNameFormat("worker-thread-%d")
    .setDaemon(true)
    .build();
```

### 4. 异常处理
```java
executor.submit(() -> {
    try {
        // 任务逻辑
    } catch (Exception e) {
        logger.error("Task execution failed", e);
    }
});
```

### 5. 优雅关闭
```java
executor.shutdown(); // 不再接受新任务
try {
    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
        executor.shutdownNow(); // 强制关闭
    }
} catch (InterruptedException e) {
    executor.shutdownNow();
}
```

## 监控和调优

### 1. 线程池监控指标
- **活跃线程数**: `getActiveCount()`
- **核心线程数**: `getCorePoolSize()`
- **最大线程数**: `getMaximumPoolSize()`
- **队列大小**: `getQueue().size()`
- **完成任务数**: `getCompletedTaskCount()`

### 2. 性能调优
- **线程数调优**: 根据 CPU 利用率调整
- **队列调优**: 避免过大导致内存问题
- **监控告警**: 设置队列积压、线程数异常告警

## 高级特性

### 1. 自定义线程池
```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    5,                          // 核心线程数
    10,                         // 最大线程数
    60L,                        // 空闲存活时间
    TimeUnit.SECONDS,           // 时间单位
    new ArrayBlockingQueue<>(100), // 工作队列
    Executors.defaultThreadFactory(), // 线程工厂
    new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
);
```

### 2. 动态调整
```java
executor.setCorePoolSize(8);        // 调整核心线程数
executor.setMaximumPoolSize(16);     // 调整最大线程数
executor.setKeepAliveTime(120, TimeUnit.SECONDS); // 调整存活时间
```

### 3. 预启动线程
```java
executor.prestartAllCoreThreads(); // 预启动所有核心线程
```

## 常见陷阱和注意事项

### 1. 避免无界队列
- **问题**: 可能导致内存溢出
- **解决**: 使用有界队列，设置合理大小

### 2. 合理设置线程数
- **问题**: 线程过多导致上下文切换开销
- **解决**: 根据任务特性和硬件配置调整

### 3. 异常处理
- **问题**: 未处理异常可能导致线程终止
- **解决**: 在任务中添加异常处理逻辑

### 4. 资源泄漏
- **问题**: 忘记关闭线程池
- **解决**: 使用 try-with-resources 或确保调用 shutdown

### 5. 死锁风险
- **问题**: 任务间相互等待
- **解决**: 避免任务间依赖，使用超时机制

## 选择指南

### 任务特性决策
- **CPU 密集型**: 固定线程池，线程数 = CPU 核心数
- **I/O 密集型**: 缓存线程池或较大的固定线程池
- **定时任务**: 定时线程池
- **单线程顺序**: 单线程池

### 性能要求决策
- **高吞吐量**: 缓存线程池 + SynchronousQueue
- **低延迟**: 预启动核心线程，较小队列
- **资源受限**: 固定线程池 + 有界队列

### 可靠性要求决策
- **高可靠性**: CallerRunsPolicy 拒绝策略
- **允许丢失**: DiscardPolicy 拒绝策略
- **优先级处理**: PriorityBlockingQueue

## 总结

线程池是 Java 异步编程的基石，正确使用可以显著提升应用性能。关键要点：

1. **合理配置**: 根据任务特性配置线程数和队列
2. **监控调优**: 持续监控性能指标，及时调整
3. **异常处理**: 完善的异常处理机制
4. **优雅关闭**: 确保资源正确释放
5. **避免陷阱**: 注意常见问题和最佳实践

选择合适的线程池类型和配置，能够在保证性能的同时，确保系统的稳定性和可维护性。 