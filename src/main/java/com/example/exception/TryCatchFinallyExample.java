package com.example.exception;

/**
 * try-catch-finally执行顺序演示 演示finally块在各种情况下的执行行为
 */
public class TryCatchFinallyExample {

    public static void main(String[] args) {
        System.out.println("=== try-catch-finally执行顺序演示 ===\n");

        // 场景1：正常执行，无异常
        System.out.println("场景1：正常执行，无异常");
        normalExecution();
        System.out.println();

        // 场景2：捕获异常
        System.out.println("场景2：捕获异常");
        catchException();
        System.out.println();

        // 场景3：未捕获异常
        System.out.println("场景3：未捕获异常");
        try {
            uncaughtException();
        } catch (Exception e) {
            System.out.println("主方法捕获到异常: " + e.getMessage());
        }
        System.out.println();

        // 场景4：finally中抛出异常
        System.out.println("场景4：finally中抛出异常");
        try {
            finallyThrowsException();
        } catch (Exception e) {
            System.out.println("主方法捕获到finally异常: " + e.getMessage());
        }
        System.out.println();

        // 场景5：return语句
        System.out.println("场景5：return语句");
        returnInTry();
        System.out.println();

        // 场景6：System.exit()
        System.out.println("场景6：System.exit()");
        systemExitInTry();
        System.out.println();

        // 场景7：嵌套try-catch-finally
        System.out.println("场景7：嵌套try-catch-finally");
        nestedTryCatchFinally();
        System.out.println();

        // 场景8：finally中的return
        System.out.println("场景8：finally中的return");
        returnInFinally();
        System.out.println();
    }

    /**
     * 场景1：正常执行，无异常
     */
    public static void normalExecution() {
        try {
            System.out.println("  try块开始");
            int result = 10 / 2;
            System.out.println("  try块正常执行，结果: " + result);
        } catch (Exception e) {
            System.out.println("  catch块执行: " + e.getMessage());
        } finally {
            System.out.println("  finally块执行");
        }
        System.out.println("  方法正常结束");
    }

    /**
     * 场景2：捕获异常
     */
    public static void catchException() {
        try {
            System.out.println("  try块开始");
            int result = 10 / 0; // 抛出ArithmeticException
            System.out.println("  这行不会执行");
        } catch (ArithmeticException e) {
            System.out.println("  catch块执行，捕获异常: " + e.getMessage());
        } finally {
            System.out.println("  finally块执行");
        }
        System.out.println("  方法正常结束");
    }

    /**
     * 场景3：未捕获异常
     */
    public static void uncaughtException() {
        try {
            System.out.println("  try块开始");
            String str = null;
            str.length(); // 抛出NullPointerException
            System.out.println("  这行不会执行");
        } catch (ArithmeticException e) {
            System.out.println("  catch块执行，捕获算术异常: " + e.getMessage());
        } finally {
            System.out.println("  finally块执行");
        }
        System.out.println("  这行不会执行，因为异常未被捕获");
    }

    /**
     * 场景4：finally中抛出异常
     */
    public static void finallyThrowsException() {
        try {
            System.out.println("  try块开始");
            int result = 10 / 2;
            System.out.println("  try块正常执行，结果: " + result);
        } catch (Exception e) {
            System.out.println("  catch块执行: " + e.getMessage());
        } finally {
            System.out.println("  finally块开始执行");
            throw new RuntimeException("finally块抛出的异常");
        }
    }

    /**
     * 场景5：return语句
     */
    public static void returnInTry() {
        try {
            System.out.println("  try块开始");
            System.out.println("  准备return");
            return; // 即使有return，finally也会执行
        } catch (Exception e) {
            System.out.println("  catch块执行: " + e.getMessage());
        } finally {
            System.out.println("  finally块执行（在return之后）");
        }
        System.out.println("  这行不会执行");
    }

    /**
     * 场景6：System.exit()
     */
    public static void systemExitInTry() {
        try {
            System.out.println("  try块开始");
            System.out.println("  准备System.exit(0)");
            // System.exit(0); // 注释掉，避免程序退出
            System.out.println("  如果执行System.exit(0)，finally不会执行");
        } catch (Exception e) {
            System.out.println("  catch块执行: " + e.getMessage());
        } finally {
            System.out.println("  finally块执行");
        }
        System.out.println("  方法正常结束");
    }

    /**
     * 场景7：嵌套try-catch-finally
     */
    public static void nestedTryCatchFinally() {
        try {
            System.out.println("  外层try块开始");

            try {
                System.out.println("    内层try块开始");
                int result = 10 / 0; // 抛出异常
                System.out.println("    这行不会执行");
            } catch (ArithmeticException e) {
                System.out.println("    内层catch块执行: " + e.getMessage());
            } finally {
                System.out.println("    内层finally块执行");
            }

            System.out.println("  外层try块继续执行");
        } catch (Exception e) {
            System.out.println("  外层catch块执行: " + e.getMessage());
        } finally {
            System.out.println("  外层finally块执行");
        }
        System.out.println("  方法正常结束");
    }

    /**
     * 场景8：finally中的return
     */
    public static int returnInFinally() {
        try {
            System.out.println("  try块开始");
            System.out.println("  准备return 100");
            return 100; // 这个return会被finally中的return覆盖
        } catch (Exception e) {
            System.out.println("  catch块执行: " + e.getMessage());
            return 200;
        } finally {
            System.out.println("  finally块执行");
            System.out.println("  finally中return 300");
            return 300; // 这个return会覆盖try或catch中的return
        }
    }

    /**
     * 演示finally的执行时机
     */
    public static void demonstrateFinallyTiming() {
        System.out.println("\n=== finally执行时机演示 ===");

        // 演示1：异常传播
        System.out.println("演示1：异常传播");
        try {
            methodThatThrowsException();
        } catch (Exception e) {
            System.out.println("主方法捕获异常: " + e.getMessage());
        }

        // 演示2：资源清理
        System.out.println("\n演示2：资源清理");
        resourceCleanup();
    }

    /**
     * 抛出异常的方法
     */
    public static void methodThatThrowsException() {
        try {
            System.out.println("  方法开始执行");
            throw new RuntimeException("方法内部异常");
        } finally {
            System.out.println("  方法finally块执行");
        }
    }

    /**
     * 资源清理示例
     */
    public static void resourceCleanup() {
        Resource resource = null;
        try {
            System.out.println("  获取资源");
            resource = new Resource("测试资源");
            resource.use();

            // 模拟异常
            throw new RuntimeException("使用资源时发生异常");
        } catch (Exception e) {
            System.out.println("  捕获异常: " + e.getMessage());
        } finally {
            if (resource != null) {
                System.out.println("  清理资源");
                resource.close();
            }
        }
    }
}

/**
 * 模拟资源类
 */
class Resource {
    private String name;
    private boolean closed = false;

    public Resource(String name) {
        this.name = name;
        System.out.println("    创建资源: " + name);
    }

    public void use() {
        if (closed) {
            throw new IllegalStateException("资源已关闭");
        }
        System.out.println("    使用资源: " + name);
    }

    public void close() {
        if (!closed) {
            closed = true;
            System.out.println("    关闭资源: " + name);
        }
    }
}

/**
 * 实际应用示例
 */
class PracticalExample {

    /**
     * 数据库连接示例
     */
    public static void databaseExample() {
        System.out.println("\n=== 数据库连接示例 ===");

        Connection connection = null;
        try {
            System.out.println("  获取数据库连接");
            connection = new Connection("MySQL");

            System.out.println("  执行SQL查询");
            connection.executeQuery("SELECT * FROM users");

            // 模拟异常
            throw new RuntimeException("数据库操作异常");

        } catch (Exception e) {
            System.out.println("  捕获异常: " + e.getMessage());
            // 可以在这里进行错误处理，如记录日志
        } finally {
            if (connection != null) {
                System.out.println("  关闭数据库连接");
                connection.close();
            }
        }
    }

    /**
     * 文件操作示例
     */
    public static void fileExample() {
        System.out.println("\n=== 文件操作示例 ===");

        FileReader reader = null;
        try {
            System.out.println("  打开文件");
            reader = new FileReader("test.txt");

            System.out.println("  读取文件内容");
            reader.read();

        } catch (Exception e) {
            System.out.println("  捕获异常: " + e.getMessage());
        } finally {
            if (reader != null) {
                System.out.println("  关闭文件");
                reader.close();
            }
        }
    }
}

/**
 * 模拟数据库连接类
 */
class Connection {
    private String type;
    private boolean closed = false;

    public Connection(String type) {
        this.type = type;
    }

    public void executeQuery(String sql) {
        if (closed) {
            throw new IllegalStateException("连接已关闭");
        }
        System.out.println("    执行SQL: " + sql);
    }

    public void close() {
        if (!closed) {
            closed = true;
            System.out.println("    关闭" + type + "连接");
        }
    }
}

/**
 * 模拟文件读取器类
 */
class FileReader {
    private String filename;
    private boolean closed = false;

    public FileReader(String filename) {
        this.filename = filename;
        System.out.println("    打开文件: " + filename);
    }

    public void read() {
        if (closed) {
            throw new IllegalStateException("文件已关闭");
        }
        System.out.println("    读取文件: " + filename);
    }

    public void close() {
        if (!closed) {
            closed = true;
            System.out.println("    关闭文件: " + filename);
        }
    }
}