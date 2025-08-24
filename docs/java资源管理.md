# Java 资源管理详解

## 概述

Java中的资源管理是确保应用程序正确获取、使用和释放系统资源的关键技术。不当的资源管理会导致内存泄漏、文件句柄耗尽、数据库连接池溢出等严重问题。

## 资源类型

### 1. 内存资源
- 对象实例
- 数组
- 集合

### 2. 系统资源
- 文件句柄
- 网络连接
- 数据库连接
- 线程

### 3. 外部资源
- 输入/输出流
- 图形资源
- 原生库资源

## 资源管理发展历程

### 1. 传统方式：try-catch-finally (Java 1.0+)
```java
FileInputStream fis = null;
try {
    fis = new FileInputStream("file.txt");
    // 使用资源
} catch (IOException e) {
    // 处理异常
} finally {
    if (fis != null) {
        try {
            fis.close();
        } catch (IOException e) {
            // 处理关闭异常
        }
    }
}
```

**缺点**：
- 代码冗长
- 容易遗漏资源释放
- 异常处理复杂

### 2. finalize方法 (已废弃)
```java
@Override
protected void finalize() throws Throwable {
    // 清理资源 - 不推荐使用
    super.finalize();
}
```

**问题**：
- 不确定的执行时机
- 性能开销大
- 可能导致内存泄漏
- Java 9开始被标记为过时

### 3. try-with-resources (Java 7+) - **推荐**
```java
try (FileInputStream fis = new FileInputStream("file.txt")) {
    // 使用资源
} catch (IOException e) {
    // 处理异常
}
// 资源自动关闭
```

### 4. Cleaner API (Java 9+)
```java
public class ResourceHolder {
    private final Cleaner.Cleanable cleanable;
    
    public ResourceHolder() {
        this.cleanable = cleaner.register(this, new CleanupAction(resource));
    }
}
```

## AutoCloseable 接口

### 定义
```java
public interface AutoCloseable {
    void close() throws Exception;
}
```

### 实现原则
1. **幂等性**：多次调用close()应该是安全的
2. **异常处理**：close()中的异常不应该掩盖主要业务逻辑的异常
3. **状态检查**：关闭后的对象应该拒绝进一步操作

## try-with-resources 详解

### 语法格式
```java
// 单个资源
try (Resource resource = new Resource()) {
    // 使用资源
}

// 多个资源
try (Resource1 r1 = new Resource1();
     Resource2 r2 = new Resource2()) {
    // 使用资源
}

// Java 9+ - 使用已存在的final变量
Resource resource = new Resource();
try (resource) {
    // 使用资源
}
```

### 执行顺序
1. 初始化资源
2. 执行try块
3. 自动调用close()方法（逆序）
4. 处理异常

### 异常抑制机制
```java
try (Resource resource = new Resource()) {
    throw new RuntimeException("业务异常");
} 
// 如果close()也抛异常，业务异常为主异常，close()异常被抑制
```

## 资源管理最佳实践

### 1. 优先使用try-with-resources
```java
// 好的做法
try (BufferedReader reader = Files.newBufferedReader(path)) {
    return reader.lines().collect(Collectors.toList());
}

// 避免
BufferedReader reader = Files.newBufferedReader(path);
try {
    return reader.lines().collect(Collectors.toList());
} finally {
    reader.close();
}
```

### 2. 自定义资源类实现AutoCloseable
```java
public class CustomResource implements AutoCloseable {
    private boolean closed = false;
    
    public void doSomething() {
        if (closed) {
            throw new IllegalStateException("Resource is closed");
        }
        // 业务逻辑
    }
    
    @Override
    public void close() {
        if (!closed) {
            // 清理资源
            closed = true;
        }
    }
}
```

### 3. 资源池管理
```java
public class ConnectionPool {
    // BlockingQueue 阻塞队列
    // 线程安全，入队和出队时加锁了
    // 队列满时，会阻塞

    private final BlockingQueue<Connection> pool = new LinkedBlockingQueue<>();
    
    public Connection getConnection() throws InterruptedException {
        return pool.take();
    }
    
    public void returnConnection(Connection conn) {
        pool.offer(conn);
    }
    
    public void close() {
        pool.forEach(Connection::close);
    }
}
```

### 4. 延迟资源初始化
```java
public class LazyResource implements AutoCloseable {
    private volatile Resource resource;
    
    private Resource getResource() {
        if (resource == null) {
            synchronized (this) {
                if (resource == null) {
                    resource = new Resource();
                }
            }
        }
        return resource;
    }
    
    @Override
    public void close() {
        if (resource != null) {
            resource.close();
        }
    }
}
```

## 常见陷阱和解决方案

### 1. 资源泄漏
```java
// 错误：可能导致资源泄漏
public InputStream getInputStream() throws IOException {
    return new FileInputStream("file.txt"); // 调用者负责关闭
}

// 正确：明确资源所有权
public void processFile(Consumer<InputStream> processor) throws IOException {
    try (InputStream is = new FileInputStream("file.txt")) {
        processor.accept(is);
    }
}
```

### 2. 双重关闭
```java
public class SafeResource implements AutoCloseable {
    private volatile boolean closed = false;
    
    @Override
    public void close() {
        if (!closed) {
            synchronized (this) {
                if (!closed) {
                    // 实际关闭逻辑
                    closed = true;
                }
            }
        }
    }
}
```

### 3. 异常掩盖
```java
// 使用try-with-resources避免异常掩盖
try (Resource resource = new Resource()) {
    // 主要逻辑异常会被保留
    throw new RuntimeException("主要异常");
} // close()异常会被抑制
```

## 内存管理相关

### 1. 弱引用在资源管理中的应用
```java
public class ResourceRegistry {
    private final Map<Object, WeakReference<Resource>> resources = new WeakHashMap<>();
    
    public void register(Object key, Resource resource) {
        resources.put(key, new WeakReference<>(resource));
    }
    
    public void cleanup() {
        resources.entrySet().removeIf(entry -> entry.getValue().get() == null);
    }
}
```

### 2. 软引用缓存
```java
public class SoftReferenceCache<K, V> {
    private final Map<K, SoftReference<V>> cache = new ConcurrentHashMap<>();
    
    public V get(K key) {
        SoftReference<V> ref = cache.get(key);
        if (ref != null) {
            V value = ref.get();
            if (value == null) {
                cache.remove(key);
            }
            return value;
        }
        return null;
    }
}
```

## 现代Java资源管理工具

### 1. Cleaner API (Java 9+)
```java
public class ManagedResource {
    private static final Cleaner cleaner = Cleaner.create();
    private final Cleaner.Cleanable cleanable;
    
    public ManagedResource() {
        this.cleanable = cleaner.register(this, new ResourceCleaner(nativeResource));
    }
    
    private static class ResourceCleaner implements Runnable {
        private final long nativeResource;
        
        ResourceCleaner(long nativeResource) {
            this.nativeResource = nativeResource;
        }
        
        @Override
        public void run() {
            // 清理原生资源
            freeNativeResource(nativeResource);
        }
    }
}
```

### 2. Shutdown Hook
```java
public class ResourceManager {
    private final Set<AutoCloseable> resources = ConcurrentHashMap.newKeySet();
    
    public ResourceManager() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::closeAllResources));
    }
    
    public void register(AutoCloseable resource) {
        resources.add(resource);
    }
    
    private void closeAllResources() {
        resources.forEach(resource -> {
            try {
                resource.close();
            } catch (Exception e) {
                // 记录日志
            }
        });
    }
}
```

## 总结

1. **优先使用 try-with-resources**：这是现代Java资源管理的标准做法
2. **实现 AutoCloseable**：自定义资源类应该实现此接口
3. **避免 finalize**：已被标记为过时，不应该依赖
4. **考虑 Cleaner API**：对于原生资源，可以作为安全网
5. **明确资源所有权**：谁创建谁负责释放
6. **使用资源池**：对于昂贵资源，考虑池化管理
7. **监控和诊断**：使用工具监控资源使用情况 