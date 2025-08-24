package com.example.resource_management;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 传统 vs 现代资源管理方式对比
 */
public class TraditionalVsModernExample {

    /**
     * 传统的 try-catch-finally 方式
     */
    public static void traditionalApproach() {
        System.out.println("=== 传统资源管理方式 ===");

        FileInputStream fis = null;
        BufferedReader reader = null;
        FileOutputStream fos = null;
        PrintWriter writer = null;

        try {
            // 创建资源
            fis = new FileInputStream("input.txt");
            reader = new BufferedReader(new InputStreamReader(fis));

            fos = new FileOutputStream("output_traditional.txt");
            writer = new PrintWriter(fos);

            // 处理数据
            String line;
            while ((line = reader.readLine()) != null) {
                writer.println("传统方式处理: " + line);
            }

        } catch (IOException e) {
            System.err.println("传统方式处理失败: " + e.getMessage());
        } finally {
            // 必须手动关闭所有资源，顺序很重要
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    System.err.println("关闭writer失败: " + e.getMessage());
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    System.err.println("关闭fos失败: " + e.getMessage());
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("关闭reader失败: " + e.getMessage());
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    System.err.println("关闭fis失败: " + e.getMessage());
                }
            }
        }
    }

    /**
     * 现代的 try-with-resources 方式
     */
    public static void modernApproach() {
        System.out.println("\n=== 现代资源管理方式 ===");

        try (FileInputStream fis = new FileInputStream("input.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                FileOutputStream fos = new FileOutputStream("output_modern.txt");
                PrintWriter writer = new PrintWriter(fos)) {

            // 处理数据
            String line;
            while ((line = reader.readLine()) != null) {
                writer.println("现代方式处理: " + line);
            }

        } catch (IOException e) {
            System.err.println("现代方式处理失败: " + e.getMessage());
        }
        // 所有资源自动关闭，无需手动处理
    }

    /**
     * 演示 finalize 方法的问题
     */
    public static void finalizeProblemsDemo() {
        System.out.println("\n=== finalize 方法问题演示 ===");

        // 创建大量使用 finalize 的对象
        List<ProblematicResource> resources = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            resources.add(new ProblematicResource("资源" + i));
        }

        // 清除引用
        resources.clear();

        // 手动触发GC
        System.gc();

        // 等待一段时间
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("使用finalize的资源处理时间: " + (endTime - startTime) + "ms");

        // 对比现代方式
        startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            try (ModernResource resource = new ModernResource("资源" + i)) {
                // 使用资源
            }
        }

        endTime = System.currentTimeMillis();
        System.out.println("使用try-with-resources的资源处理时间: " + (endTime - startTime) + "ms");
    }

    /**
     * Cleaner API 演示 (Java 9+)
     */
    public static void cleanerDemo() {
        System.out.println("\n=== Cleaner API 演示 ===");

        // 注意：这里使用模拟的Cleaner，因为实际的Cleaner需要Java 9+
        CleanerResource resource1 = new CleanerResource("Cleaner资源1");
        CleanerResource resource2 = new CleanerResource("Cleaner资源2");

        // 清除引用
        resource1 = null;
        resource2 = null;

        // 触发GC
        System.gc();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Cleaner API 允许更可控的资源清理");
    }

    /**
     * 资源泄漏演示
     */
    public static void resourceLeakDemo() {
        System.out.println("\n=== 资源泄漏演示 ===");

        // 错误的资源管理 - 可能导致泄漏
        try {
            leakyMethod();
        } catch (Exception e) {
            System.err.println("leakyMethod出现异常: " + e.getMessage());
        }

        // 正确的资源管理
        try {
            safeMethod();
        } catch (Exception e) {
            System.err.println("safeMethod出现异常: " + e.getMessage());
        }
    }

    private static void leakyMethod() throws IOException {
        System.out.println("执行可能泄漏的方法...");

        FileOutputStream fos = new FileOutputStream("leak_test.txt");
        PrintWriter writer = new PrintWriter(fos);

        writer.println("这可能导致资源泄漏");

        // 模拟异常
        if (System.currentTimeMillis() > 0) {
            throw new RuntimeException("模拟异常 - 资源未正确关闭");
        }

        // 这行代码可能永远不会执行
        writer.close();
        fos.close();
    }

    private static void safeMethod() throws IOException {
        System.out.println("执行安全的方法...");

        try (FileOutputStream fos = new FileOutputStream("safe_test.txt"); PrintWriter writer = new PrintWriter(fos)) {

            writer.println("这是安全的资源管理");

            // 即使这里抛出异常，资源也会被正确关闭
            if (System.currentTimeMillis() > 0) {
                throw new RuntimeException("模拟异常 - 但资源会正确关闭");
            }
        }
    }

    /**
     * 创建测试文件
     */
    private static void createInputFile() {
        try (PrintWriter writer = new PrintWriter("input.txt")) {
            writer.println("第一行数据");
            writer.println("第二行数据");
            writer.println("第三行数据");
        } catch (FileNotFoundException e) {
            System.err.println("创建输入文件失败: " + e.getMessage());
        }
    }

    /**
     * 清理测试文件
     */
    private static void cleanup() {
        String[] files = { "input.txt", "output_traditional.txt", "output_modern.txt", "leak_test.txt",
                "safe_test.txt" };

        for (String filename : files) {
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static void main(String[] args) {
        createInputFile();

        traditionalApproach();
        modernApproach();
        finalizeProblemsDemo();
        cleanerDemo();
        resourceLeakDemo();

        cleanup();
    }
}

/**
 * 使用 finalize 的问题资源类 (不推荐)
 */
class ProblematicResource {
    private final String name;

    public ProblematicResource(String name) {
        this.name = name;
        // 模拟资源分配
    }

    // Java 8 中，finalize 方法已经被标记为过时，不建议使用
    @Override
    protected void finalize() throws Throwable {
        // 模拟清理工作
        System.out.println("finalize 清理资源: " + name);
        super.finalize();
    }
}

/**
 * 现代资源类
 */
class ModernResource implements AutoCloseable {
    private final String name;
    private boolean closed = false;

    public ModernResource(String name) {
        this.name = name;
        // 模拟资源分配
    }

    @Override
    public void close() {
        if (!closed) {
            // 立即清理资源
            closed = true;
        }
    }
}

/**
 * 使用 Cleaner API 的资源类 (Java 9+) 注意：这里是模拟实现，实际使用需要Java 9+
 */
class CleanerResource {
    // 在实际的Java 9+中，这里会使用 java.lang.ref.Cleaner
    // private static final Cleaner cleaner = Cleaner.create();
    // private final Cleaner.Cleanable cleanable;

    private final String name;

    public CleanerResource(String name) {
        this.name = name;
        System.out.println("创建Cleaner资源: " + name);

        // 在实际的Java 9+中会这样使用:
        // this.cleanable = cleaner.register(this, new ResourceCleaner(name));

        // 模拟注册清理操作
        registerCleaner();
    }

    private void registerCleaner() {
        // 模拟Cleaner注册
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Cleaner清理资源: " + name);
        }));
    }

    // 实际的Cleaner清理操作类
    private static class ResourceCleaner implements Runnable {
        private final String resourceName;

        ResourceCleaner(String resourceName) {
            this.resourceName = resourceName;
        }

        @Override
        public void run() {
            System.out.println("Cleaner清理资源: " + resourceName);
        }
    }
}