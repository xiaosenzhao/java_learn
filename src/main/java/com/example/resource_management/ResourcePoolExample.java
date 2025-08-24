package com.example.resource_management;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 资源池管理示例
 */
public class ResourcePoolExample {

    /**
     * 数据库连接池示例
     */
    public static void databaseConnectionPoolDemo() {
        System.out.println("=== 数据库连接池示例 ===");

        DatabaseConnectionPool pool = new DatabaseConnectionPool(3, 10);

        // 模拟多个线程并发获取连接
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 8; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    System.out.println("任务 " + taskId + " 尝试获取连接...");

                    try (DatabaseConnection conn = pool.getConnection()) {
                        System.out.println("任务 " + taskId + " 获得连接: " + conn.getId());

                        // 模拟数据库操作
                        conn.executeQuery("SELECT * FROM users WHERE id = " + taskId);
                        Thread.sleep(1000); // 模拟执行时间

                    } // 连接会自动归还到池中

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("任务 " + taskId + " 被中断");
                } catch (Exception e) {
                    System.err.println("任务 " + taskId + " 出错: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("池状态: " + pool.getStatus());
        pool.close();
    }

    /**
     * 线程池资源管理示例
     */
    public static void threadPoolDemo() {
        System.out.println("\n=== 线程池资源管理示例 ===");

        // 使用try-with-resources管理线程池
        try (ManagedThreadPool threadPool = new ManagedThreadPool(3)) {

            for (int i = 0; i < 6; i++) {
                final int taskId = i;
                threadPool.submit(() -> {
                    System.out.println("执行任务 " + taskId + " - 线程: " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("任务 " + taskId + " 完成");
                });
            }

            // 等待所有任务完成
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // 线程池会自动关闭
    }

    /**
     * 文件句柄池示例
     */
    public static void fileHandlePoolDemo() {
        System.out.println("\n=== 文件句柄池示例 ===");

        try (FileHandlePool pool = new FileHandlePool(2)) {

            // 并发访问文件
            CompletableFuture<?>[] futures = new CompletableFuture[4];

            for (int i = 0; i < 4; i++) {
                final int taskId = i;
                futures[i] = CompletableFuture.runAsync(() -> {
                    try (FileHandle handle = pool.getFileHandle()) {
                        System.out.println("任务 " + taskId + " 获得文件句柄: " + handle.getId());
                        handle.write("任务 " + taskId + " 的数据");
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        System.err.println("任务 " + taskId + " 出错: " + e.getMessage());
                    }
                });
            }

            // 等待所有任务完成
            CompletableFuture.allOf(futures).join();

        } catch (Exception e) {
            System.err.println("文件句柄池操作失败: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        databaseConnectionPoolDemo();
        threadPoolDemo();
        fileHandlePoolDemo();
    }
}

/**
 * 数据库连接类
 */
class DatabaseConnection implements AutoCloseable {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    private final long id;
    private final DatabaseConnectionPool pool;
    private volatile boolean closed = false;

    public DatabaseConnection(DatabaseConnectionPool pool) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.pool = pool;
        System.out.println("创建数据库连接: " + id);
    }

    public long getId() {
        return id;
    }

    public void executeQuery(String sql) {
        checkClosed();
        System.out.println("连接 " + id + " 执行SQL: " + sql);
    }

    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("连接已关闭: " + id);
        }
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            pool.returnConnection(this);
        }
    }

    // 物理关闭连接
    void physicalClose() {
        System.out.println("物理关闭数据库连接: " + id);
    }
}

/**
 * 数据库连接池
 */
class DatabaseConnectionPool {
    private final BlockingQueue<DatabaseConnection> pool;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private final int maxConnections;
    private volatile boolean closed = false;

    public DatabaseConnectionPool(int initialSize, int maxConnections) {
        this.maxConnections = maxConnections;
        this.pool = new LinkedBlockingQueue<>();

        // 初始化连接池
        for (int i = 0; i < initialSize; i++) {
            pool.offer(new DatabaseConnection(this));
        }
        activeConnections.set(initialSize);
    }

    public DatabaseConnection getConnection() throws InterruptedException {
        if (closed) {
            throw new IllegalStateException("连接池已关闭");
        }

        DatabaseConnection conn = pool.poll();
        if (conn == null && activeConnections.get() < maxConnections) {
            // 创建新连接
            synchronized (this) {
                if (activeConnections.get() < maxConnections) {
                    conn = new DatabaseConnection(this);
                    activeConnections.incrementAndGet();
                }
            }
        }

        if (conn == null) {
            // 等待可用连接
            conn = pool.take();
        }

        return conn;
    }

    void returnConnection(DatabaseConnection conn) {
        if (!closed && conn != null) {
            pool.offer(conn);
        }
    }

    public String getStatus() {
        return String.format("活跃连接: %d, 池中可用: %d, 最大连接: %d", activeConnections.get(), pool.size(), maxConnections);
    }

    public void close() {
        closed = true;

        // 关闭所有连接
        DatabaseConnection conn;
        while ((conn = pool.poll()) != null) {
            conn.physicalClose();
        }

        System.out.println("数据库连接池已关闭");
    }
}

/**
 * 管理的线程池
 */
class ManagedThreadPool implements AutoCloseable {
    private final ExecutorService executor;

    public ManagedThreadPool(int poolSize) {
        this.executor = Executors.newFixedThreadPool(poolSize);
        System.out.println("创建线程池，大小: " + poolSize);
    }

    public void submit(Runnable task) {
        if (executor.isShutdown()) {
            throw new IllegalStateException("线程池已关闭");
        }
        executor.submit(task);
    }

    @Override
    public void close() {
        System.out.println("关闭线程池...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("强制关闭线程池");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("线程池已关闭");
    }
}

/**
 * 文件句柄类
 */
class FileHandle implements AutoCloseable {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

    private final int id;
    private final FileHandlePool pool;
    private volatile boolean closed = false;

    public FileHandle(FileHandlePool pool) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.pool = pool;
        System.out.println("打开文件句柄: " + id);
    }

    public int getId() {
        return id;
    }

    public void write(String data) {
        checkClosed();
        System.out.println("文件句柄 " + id + " 写入数据: " + data);
    }

    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("文件句柄已关闭: " + id);
        }
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            pool.returnHandle(this);
        }
    }

    void physicalClose() {
        System.out.println("物理关闭文件句柄: " + id);
    }
}

/**
 * 文件句柄池
 */
class FileHandlePool implements AutoCloseable {
    private final BlockingQueue<FileHandle> pool;
    private final AtomicInteger handleCount = new AtomicInteger(0);
    private final int maxHandles;
    private volatile boolean closed = false;

    public FileHandlePool(int maxHandles) {
        this.maxHandles = maxHandles;
        this.pool = new LinkedBlockingQueue<>();

        // 预创建一些句柄
        for (int i = 0; i < Math.min(2, maxHandles); i++) {
            pool.offer(new FileHandle(this));
            handleCount.incrementAndGet();
        }
    }

    public FileHandle getFileHandle() throws InterruptedException {
        if (closed) {
            throw new IllegalStateException("文件句柄池已关闭");
        }

        FileHandle handle = pool.poll();
        if (handle == null && handleCount.get() < maxHandles) {
            synchronized (this) {
                if (handleCount.get() < maxHandles) {
                    handle = new FileHandle(this);
                    handleCount.incrementAndGet();
                }
            }
        }

        if (handle == null) {
            handle = pool.take();
        }

        return handle;
    }

    void returnHandle(FileHandle handle) {
        if (!closed && handle != null) {
            pool.offer(handle);
        }
    }

    @Override
    public void close() {
        closed = true;

        FileHandle handle;
        while ((handle = pool.poll()) != null) {
            handle.physicalClose();
        }

        System.out.println("文件句柄池已关闭");
    }
}