# 公平锁
线程按照申请锁的顺序依次获取锁，先来的线程先得到锁，后来的线程需要等待前面的线程释放锁后才能获取。

# 非公平锁

当一个线程请求锁时，会首先尝试获取锁。如果成功，就直接获取，即使有其他线程在等待队列中。只有当获取失败时，才会进入等待队列，按照 FIFO 的顺序等待。

### 优点

非公平锁可以提高系统的吞吐量，因为它可以避免一些线程在等待队列中长时间等待，特别是当锁释放瞬间，恰好有线程在尝试获取锁时，可以直接获取，避免了唤醒线程的开销。

### 缺点

非公平锁可能导致某些线程长时间无法获取到锁，甚至出现饥饿现象。

# 重入锁 (ReentrantLock)

### 定义
重入锁是指同一个线程可以多次获取同一把锁而不会造成死锁。当线程已经持有锁时，再次请求该锁会成功，但需要相应次数的释放操作。

### 特点
- **可重入性**：同一线程可以多次获取
- **公平性选择**：可以选择公平锁或非公平锁
- **可中断**：支持响应中断
- **超时获取**：支持尝试获取锁

### Java实现
```java
// Java内置的synchronized关键字是重入锁
// ReentrantLock也是重入锁
ReentrantLock lock = new ReentrantLock();
```

### 应用场景
- 方法内部需要多次加锁
- 递归方法需要同步
- 需要灵活的锁控制

# 不可重入锁 (Non-reentrant Lock)

### 定义
不可重入锁是指同一个线程不能多次获取同一把锁。如果线程已经持有锁，再次请求会导致死锁。

### 特点
- **不可重入**：同一线程重复获取会死锁
- **简单实现**：逻辑相对简单
- **性能较好**：开销相对较小

### 风险
- 容易造成死锁
- 限制了编程灵活性
- 递归调用会出问题

# 悲观锁 (Pessimistic Lock)

### 定义
悲观锁假设并发冲突一定会发生，因此在访问数据前先获取锁，确保数据访问的独占性。

### 特点
- **独占访问**：假设冲突总会发生
- **阻塞等待**：其他线程必须等待
- **适合写多场景**：写操作频繁时效果好

### Java实现
```java
// synchronized关键字
synchronized (obj) {
    // 临界区代码
}

// ReentrantLock
ReentrantLock lock = new ReentrantLock();
lock.lock();
try {
    // 临界区代码
} finally {
    lock.unlock();
}
```

### 应用场景
- 写操作频繁
- 并发冲突概率高
- 对数据一致性要求严格

# 乐观锁 (Optimistic Lock)

### 定义
乐观锁假设并发冲突很少发生，因此不加锁，而是在更新数据时检查是否有冲突，如果有冲突则重试或报错。

### 特点
- **无锁访问**：假设冲突很少发生
- **CAS操作**：比较并交换
- **适合读多场景**：读操作频繁时效果好

### Java实现
```java
// AtomicInteger使用CAS实现乐观锁
AtomicInteger count = new AtomicInteger(0);
count.incrementAndGet(); // CAS操作

// 版本号机制
class OptimisticEntity {
    private int version;
    private String data;
    
    public boolean updateData(String newData, int expectedVersion) {
        if (this.version == expectedVersion) {
            this.data = newData;
            this.version++;
            return true;
        }
        return false; // 版本不匹配，更新失败
    }
}
```

### 应用场景
- 读操作频繁
- 并发冲突概率低
- 性能要求高

# 互斥锁 (Mutex Lock)

### 定义
互斥锁是最基本的锁类型，确保同一时刻只有一个线程可以访问共享资源。

### 特点
- **互斥访问**：同时只允许一个线程
- **阻塞等待**：其他线程被阻塞
- **基础锁类型**：其他锁的基础

### Java实现
```java
// synchronized关键字实现互斥
private final Object mutex = new Object();

public void criticalSection() {
    synchronized (mutex) {
        // 只有一个线程能执行这里
    }
}

// ReentrantLock也是互斥锁
private final ReentrantLock mutex = new ReentrantLock();
```

### 应用场景
- 保护临界区
- 确保数据一致性
- 基础同步需求

# 自旋锁 (Spin Lock)

### 定义
自旋锁是指线程在获取锁失败时不会立即阻塞，而是循环检查锁是否可用，直到获取锁为止。

### 特点
- **忙等待**：循环检查锁状态
- **无上下文切换**：避免线程阻塞和唤醒
- **适合短期锁定**：锁持有时间很短时效果好
- **消耗CPU**：会占用CPU资源

### Java实现
```java
// 简单自旋锁实现
public class SimpleSpinLock {
    private final AtomicReference<Thread> owner = new AtomicReference<>();
    
    public void lock() {
        Thread current = Thread.currentThread();
        // 自旋等待
        while (!owner.compareAndSet(null, current)) {
            // 忙等待，不断尝试获取锁
        }
    }
    
    public void unlock() {
        Thread current = Thread.currentThread();
        owner.compareAndSet(current, null);
    }
}
```

### 应用场景
- 锁持有时间极短
- 线程数不多
- CPU资源充足

# 锁的对比总结

| 锁类型 | 重入性 | 阻塞性 | 适用场景 | 性能特点 |
|--------|--------|--------|----------|----------|
| 重入锁 | 可重入 | 阻塞 | 复杂同步逻辑 | 灵活但有开销 |
| 不可重入锁 | 不可重入 | 阻塞 | 简单同步 | 性能较好但易死锁 |
| 悲观锁 | 取决于实现 | 阻塞 | 写多读少 | 安全但开销大 |
| 乐观锁 | 无锁 | 非阻塞 | 读多写少 | 高性能但可能重试 |
| 互斥锁 | 取决于实现 | 阻塞 | 基础同步 | 标准性能 |
| 自旋锁 | 取决于实现 | 忙等待 | 短期锁定 | 低延迟但耗CPU |