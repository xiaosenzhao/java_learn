package com.example.resource_management;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * try-with-resources 使用示例
 */
public class TryWithResourcesExample {

    /**
     * 基本的 try-with-resources 使用
     */
    public static void basicFileReading() {
        System.out.println("=== 基本文件读取示例 ===");

        // 创建测试文件
        createTestFile();

        // 使用 try-with-resources 读取文件
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("test.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("读取内容: " + line);
            }
        } catch (IOException e) {
            System.err.println("文件读取失败: " + e.getMessage());
        }
        // reader 会自动关闭
    }

    /**
     * 多资源的 try-with-resources
     */
    public static void multipleResources() {
        System.out.println("\n=== 多资源管理示例 ===");

        createTestFile();

        // 同时管理多个资源
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("test.txt"));
                PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get("output.txt")))) {

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                String processedLine = lineNumber + ": " + line.toUpperCase();
                writer.println(processedLine);
                System.out.println("处理: " + processedLine);
                lineNumber++;
            }

        } catch (IOException e) {
            System.err.println("文件处理失败: " + e.getMessage());
        }
        // 所有资源都会自动关闭（逆序）
    }

    /**
     * 使用已存在的资源 (Java 9+) 注意：此方法演示Java 9+的新语法，当前使用Java 8兼容写法
     */
    public static void existingResource() {
        System.out.println("\n=== 使用已存在资源示例 ===");

        // Java 8 兼容写法
        try (Scanner scanner = new Scanner("模拟输入\n")) {
            System.out.println("Scanner 将在 try 块结束后自动关闭");
            System.out.println("读取输入: " + scanner.nextLine());
            System.out.println("模拟用户输入处理完成");
        }

        // Java 9+ 语法示例（已注释以保持兼容性）:
        /*
         * Scanner scanner = new Scanner(System.in); try (scanner) { // Java 9+ 才支持
         * System.out.println("Scanner 将在 try 块结束后自动关闭"); System.out.println("模拟用户输入处理完成"); }
         */
    }

    /**
     * 自定义资源类
     */
    public static void customResource() {
        System.out.println("\n=== 自定义资源示例 ===");

        try (CustomResource resource = new CustomResource("数据库连接")) {
            resource.doWork("查询用户数据");
            resource.doWork("更新用户信息");
        } catch (Exception e) {
            System.err.println("资源操作失败: " + e.getMessage());
        }
        // CustomResource 会自动关闭
    }

    /**
     * 异常抑制机制演示
     */
    public static void exceptionSuppression() {
        System.out.println("\n=== 异常抑制机制示例 ===");

        try (ProblematicAutoCloseableResource resource = new ProblematicAutoCloseableResource()) {
            System.out.println("开始使用资源...");
            throw new RuntimeException("业务逻辑异常");
        } catch (Exception e) {
            System.out.println("主异常: " + e.getMessage());

            // 检查被抑制的异常
            Throwable[] suppressed = e.getSuppressed();
            for (Throwable t : suppressed) {
                System.out.println("被抑制的异常: " + t.getMessage());
            }
        }
    }

    /**
     * Stream API 与 try-with-resources 结合
     */
    public static void streamWithTryWithResources() {
        System.out.println("\n=== Stream API 结合示例 ===");

        createTestFile();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get("test.txt"))) {
            List<String> upperCaseLines = reader.lines().map(String::toUpperCase).filter(line -> line.length() > 5)
                    .collect(Collectors.toList());

            System.out.println("处理后的行数: " + upperCaseLines.size());
            upperCaseLines.forEach(line -> System.out.println("  " + line));

        } catch (IOException e) {
            System.err.println("文件处理失败: " + e.getMessage());
        }
    }

    /**
     * 嵌套资源管理
     */
    public static void nestedResources() {
        System.out.println("\n=== 嵌套资源管理示例 ===");

        try (OuterResource outer = new OuterResource("外部资源")) {
            outer.doWork();

            try (InnerResource inner = new InnerResource("内部资源")) {
                inner.doWork();
                // 两个资源都会正确关闭
            }
        } catch (Exception e) {
            System.err.println("嵌套资源操作失败: " + e.getMessage());
        }
    }

    /**
     * 创建测试文件
     */
    private static void createTestFile() {
        try (PrintWriter writer = new PrintWriter("test.txt")) {
            writer.println("第一行测试数据");
            writer.println("第二行测试数据");
            writer.println("第三行测试数据");
            writer.println("这是一个较长的测试行，用于演示过滤功能");
        } catch (FileNotFoundException e) {
            System.err.println("创建测试文件失败: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        basicFileReading();
        multipleResources();
        // existingResource(); // 需要用户输入，在自动化演示中跳过
        customResource();
        exceptionSuppression();
        streamWithTryWithResources();
        nestedResources();

        // 清理测试文件
        cleanup();
    }

    private static void cleanup() {
        try {
            Files.deleteIfExists(Paths.get("test.txt"));
            Files.deleteIfExists(Paths.get("output.txt"));
        } catch (IOException e) {
            System.err.println("清理文件失败: " + e.getMessage());
        }
    }
}

/**
 * 自定义资源类 - 实现 AutoCloseable
 */
class CustomResource implements AutoCloseable {
    private final String name;
    private boolean closed = false;

    public CustomResource(String name) {
        this.name = name;
        System.out.println("创建资源: " + name);
    }

    public void doWork(String task) {
        checkClosed();
        System.out.println(name + " 执行任务: " + task);
    }

    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("资源已关闭: " + name);
        }
    }

    @Override
    public void close() {
        if (!closed) {
            System.out.println("关闭资源: " + name);
            closed = true;
        }
    }
}

/**
 * 会在关闭时抛出异常的资源类
 */
class ProblematicAutoCloseableResource implements AutoCloseable {
    public ProblematicAutoCloseableResource() {
        System.out.println("创建问题资源");
    }

    @Override
    public void close() throws Exception {
        System.out.println("关闭问题资源时抛出异常");
        throw new RuntimeException("关闭时发生异常");
    }
}

/**
 * 外部资源类
 */
class OuterResource implements AutoCloseable {
    private final String name;

    public OuterResource(String name) {
        this.name = name;
        System.out.println("创建 " + name);
    }

    public void doWork() {
        System.out.println(name + " 正在工作");
    }

    @Override
    public void close() {
        System.out.println("关闭 " + name);
    }
}

/**
 * 内部资源类
 */
class InnerResource implements AutoCloseable {
    private final String name;

    public InnerResource(String name) {
        this.name = name;
        System.out.println("创建 " + name);
    }

    public void doWork() {
        System.out.println(name + " 正在工作");
    }

    @Override
    public void close() {
        System.out.println("关闭 " + name);
    }
}