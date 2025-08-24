package com.example.transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 事务管理概念示例 演示事务的各种概念和场景（不依赖Spring事务注解）
 */
public class TransactionExample {

    private UserRepository userRepository;
    private AccountRepository accountRepository;
    private OrderRepository orderRepository;
    private LogRepository logRepository;

    public TransactionExample() {
        // 初始化模拟的仓库
        this.userRepository = new MockUserRepository();
        this.accountRepository = new MockAccountRepository();
        this.orderRepository = new MockOrderRepository();
        this.logRepository = new MockLogRepository();
    }

    /**
     * 基础事务示例：用户注册 模拟事务的原子性：要么全部成功，要么全部回滚
     */
    public void registerUser(User user) {
        System.out.println("=== 开始用户注册事务 ===");

        // 模拟事务开始
        System.out.println("事务开始...");

        try {
            // 1. 保存用户信息
            user.setId(UUID.randomUUID().toString());
            userRepository.save(user);
            System.out.println("✓ 用户信息保存成功: " + user.getId());

            // 2. 创建用户账户
            Account account = new Account();
            account.setId(UUID.randomUUID().toString());
            account.setUserId(user.getId());
            account.setBalance(BigDecimal.ZERO);
            accountRepository.save(account);
            System.out.println("✓ 用户账户创建成功: " + account.getId());

            // 3. 记录操作日志
            logOperation("用户注册", user.getId());

            // 模拟事务提交
            System.out.println("✓ 事务提交成功");
            System.out.println("=== 用户注册事务完成 ===");

        } catch (Exception e) {
            // 模拟事务回滚
            System.err.println("✗ 事务回滚: " + e.getMessage());
            System.err.println("=== 用户注册事务失败 ===");
            throw e;
        }
    }

    /**
     * 复杂事务示例：转账操作 演示事务的隔离性和一致性
     */
    public void transferMoney(String fromAccountId, String toAccountId, BigDecimal amount) {
        System.out.println("=== 开始转账事务 ===");

        // 模拟事务开始
        System.out.println("事务开始（读已提交隔离级别）...");

        try {
            // 1. 读取源账户信息
            Account fromAccount = accountRepository.findById(fromAccountId);
            if (fromAccount == null) {
                throw new RuntimeException("源账户不存在: " + fromAccountId);
            }

            // 2. 读取目标账户信息
            Account toAccount = accountRepository.findById(toAccountId);
            if (toAccount == null) {
                throw new RuntimeException("目标账户不存在: " + toAccountId);
            }

            System.out.println("源账户余额: " + fromAccount.getBalance());
            System.out.println("目标账户余额: " + toAccount.getBalance());

            // 3. 检查余额
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("余额不足，当前余额: " + fromAccount.getBalance() + ", 转账金额: " + amount);
            }

            // 4. 执行转账
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            toAccount.setBalance(toAccount.getBalance().add(amount));

            // 5. 保存账户信息
            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);

            System.out.println("✓ 转账成功: " + amount + " 从 " + fromAccountId + " 到 " + toAccountId);

            // 6. 记录转账日志
            logOperation("转账操作", "从" + fromAccountId + "到" + toAccountId + "，金额:" + amount);

            // 模拟事务提交
            System.out.println("✓ 事务提交成功");
            System.out.println("=== 转账事务完成 ===");

        } catch (Exception e) {
            // 模拟事务回滚
            System.err.println("✗ 事务回滚: " + e.getMessage());
            System.err.println("=== 转账事务失败 ===");
            throw e;
        }
    }

    /**
     * 独立事务示例：日志记录 模拟REQUIRES_NEW传播行为
     */
    public void logOperation(String operation, String details) {
        System.out.println("=== 开始日志记录（独立事务） ===");

        // 模拟独立事务开始
        System.out.println("独立事务开始...");

        try {
            Log log = new Log();
            log.setId(UUID.randomUUID().toString());
            log.setOperation(operation);
            log.setDetails(details);
            log.setTimestamp(System.currentTimeMillis());

            logRepository.save(log);
            System.out.println("✓ 日志记录成功: " + log.getId());

            // 模拟独立事务提交
            System.out.println("✓ 独立事务提交成功");
            System.out.println("=== 日志记录事务完成 ===");

        } catch (Exception e) {
            // 模拟独立事务回滚
            System.err.println("✗ 独立事务回滚: " + e.getMessage());
            System.err.println("=== 日志记录事务失败 ===");
            throw e;
        }
    }

    /**
     * 只读事务示例 模拟只读事务的性能优化
     */
    public List<User> getAllUsers() {
        System.out.println("=== 执行只读事务：查询所有用户 ===");
        System.out.println("只读事务：不会创建写锁，性能更好");

        List<User> users = userRepository.findAll();
        System.out.println("✓ 查询成功，用户数量: " + users.size());

        return users;
    }

    /**
     * 可重复读隔离级别示例 演示事务期间多次读取同一数据的一致性
     */
    public BigDecimal getAccountBalance(String accountId) {
        System.out.println("=== 执行可重复读事务：查询账户余额 ===");

        Account account = accountRepository.findById(accountId);
        if (account == null) {
            throw new RuntimeException("账户不存在: " + accountId);
        }

        // 在事务期间，多次读取会得到相同结果
        BigDecimal balance1 = account.getBalance();
        BigDecimal balance2 = account.getBalance();

        System.out.println("第一次读取余额: " + balance1);
        System.out.println("第二次读取余额: " + balance2);
        System.out.println("余额是否一致: " + balance1.equals(balance2));

        return balance1;
    }

    /**
     * 事务超时示例
     */
    public void longRunningOperation() {
        System.out.println("=== 开始长时间运行的操作（5秒超时） ===");

        try {
            System.out.println("模拟长时间运行的操作...");
            Thread.sleep(3000); // 3秒，在超时范围内
            System.out.println("✓ 长时间操作完成");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("操作被中断", e);
        }
    }

    /**
     * 异常回滚示例
     */
    public void operationWithException() {
        System.out.println("=== 开始带异常的操作 ===");

        // 模拟事务开始
        System.out.println("事务开始...");

        try {
            // 创建一些数据
            User user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setName("测试用户");
            user.setEmail("test@example.com");
            userRepository.save(user);

            System.out.println("✓ 用户创建成功: " + user.getId());

            // 故意抛出异常，触发事务回滚
            throw new RuntimeException("模拟业务异常，触发事务回滚");

        } catch (Exception e) {
            // 模拟事务回滚
            System.err.println("✗ 事务回滚: " + e.getMessage());
            System.err.println("=== 操作失败 ===");
            throw e;
        }
    }

    /**
     * 演示嵌套事务
     */
    public void outerTransaction() {
        System.out.println("=== 外层事务开始 ===");

        // 模拟外层事务开始
        System.out.println("外层事务开始...");

        try {
            // 创建用户
            User user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setName("嵌套事务用户");
            user.setEmail("nested@example.com");
            userRepository.save(user);

            System.out.println("✓ 外层事务：用户创建成功");

            try {
                // 调用内层事务
                innerTransaction();
            } catch (Exception e) {
                System.err.println("内层事务异常: " + e.getMessage());
                // 外层事务也会回滚
                throw e;
            }

            // 模拟外层事务提交
            System.out.println("✓ 外层事务提交成功");
            System.out.println("=== 外层事务完成 ===");

        } catch (Exception e) {
            // 模拟外层事务回滚
            System.err.println("✗ 外层事务回滚: " + e.getMessage());
            System.err.println("=== 外层事务失败 ===");
            throw e;
        }
    }

    /**
     * 内层事务：独立事务
     */
    public void innerTransaction() {
        System.out.println("=== 内层事务开始（独立事务） ===");

        // 模拟独立事务开始
        System.out.println("内层独立事务开始...");

        try {
            // 创建账户
            Account account = new Account();
            account.setId(UUID.randomUUID().toString());
            account.setUserId("nested-user-id");
            account.setBalance(BigDecimal.valueOf(1000));
            accountRepository.save(account);

            System.out.println("✓ 内层事务：账户创建成功");

            // 故意抛出异常
            throw new RuntimeException("内层事务异常");

        } catch (Exception e) {
            // 模拟内层事务回滚
            System.err.println("✗ 内层事务回滚: " + e.getMessage());
            System.err.println("=== 内层事务失败 ===");
            throw e;
        }
    }

    /**
     * 演示事务传播行为
     */
    public void demonstrateTransactionPropagation() {
        System.out.println("\n=== 事务传播行为演示 ===");

        System.out.println("1. REQUIRED（默认）：如果存在事务则加入，否则创建新事务");
        System.out.println("2. SUPPORTS：支持当前事务，如果不存在则以非事务方式执行");
        System.out.println("3. MANDATORY：必须在事务中执行，否则抛出异常");
        System.out.println("4. REQUIRES_NEW：创建新事务，挂起当前事务");
        System.out.println("5. NOT_SUPPORTED：以非事务方式执行，挂起当前事务");
        System.out.println("6. NEVER：以非事务方式执行，如果存在事务则抛出异常");
        System.out.println("7. NESTED：如果存在事务则创建嵌套事务，否则创建新事务");
    }

    /**
     * 演示事务隔离级别
     */
    public void demonstrateTransactionIsolation() {
        System.out.println("\n=== 事务隔离级别演示 ===");

        System.out.println("1. READ_UNCOMMITTED（读未提交）：最低隔离级别，可能读到脏数据");
        System.out.println("2. READ_COMMITTED（读已提交）：只能读到已提交的数据，防止脏读");
        System.out.println("3. REPEATABLE_READ（可重复读）：同一事务内多次读取结果一致，防止不可重复读");
        System.out.println("4. SERIALIZABLE（串行化）：最高隔离级别，完全串行执行，防止幻读");
    }

    /**
     * 演示事务失效场景
     */
    public void demonstrateTransactionFailure() {
        System.out.println("\n=== 事务失效场景演示 ===");

        System.out.println("1. 非public方法：private、protected方法上的@Transactional不会生效");
        System.out.println("2. 自调用问题：同一个类内部的方法调用不会经过代理");
        System.out.println("3. 未被Spring管理：直接new的对象不是Spring Bean");
        System.out.println("4. 异常被捕获：捕获异常后不抛出，事务不会回滚");
        System.out.println("5. 数据库不支持：某些数据库操作不支持事务");
    }

    /**
     * 运行所有演示
     */
    public void runAllDemonstrations() {
        System.out.println("=== Spring事务管理演示开始 ===\n");

        try {
            // 基础事务演示
            User user = new User("张三", "zhangsan@example.com");
            registerUser(user);

            System.out.println("\n==================================================\n");

            // 转账事务演示
            Account account1 = new Account();
            account1.setId("acc1");
            account1.setBalance(BigDecimal.valueOf(1000));
            accountRepository.save(account1);

            Account account2 = new Account();
            account2.setId("acc2");
            account2.setBalance(BigDecimal.valueOf(500));
            accountRepository.save(account2);

            transferMoney("acc1", "acc2", BigDecimal.valueOf(200));

            System.out.println("\n==================================================\n");

            // 只读事务演示
            getAllUsers();

            System.out.println("\n==================================================\n");

            // 可重复读演示
            getAccountBalance("acc1");

            System.out.println("\n==================================================\n");

            // 异常回滚演示
            try {
                operationWithException();
            } catch (Exception e) {
                System.out.println("预期异常: " + e.getMessage());
            }

            System.out.println("\n==================================================\n");

            // 嵌套事务演示
            try {
                outerTransaction();
            } catch (Exception e) {
                System.out.println("预期异常: " + e.getMessage());
            }

            System.out.println("\n==================================================\n");

            // 概念演示
            demonstrateTransactionPropagation();
            demonstrateTransactionIsolation();
            demonstrateTransactionFailure();

        } catch (Exception e) {
            System.err.println("演示过程中发生异常: " + e.getMessage());
        }

        System.out.println("\n=== Spring事务管理演示结束 ===");
    }
}

/**
 * 用户实体类
 */
class User {
    private String id;
    private String name;
    private String email;

    // 构造函数
    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

/**
 * 账户实体类
 */
class Account {
    private String id;
    private String userId;
    private BigDecimal balance;

    // 构造函数
    public Account() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}

/**
 * 订单实体类
 */
class Order {
    private String id;
    private String userId;
    private BigDecimal amount;
    private String status;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

/**
 * 日志实体类
 */
class Log {
    private String id;
    private String operation;
    private String details;
    private long timestamp;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

/**
 * 用户仓库接口
 */
interface UserRepository {
    User save(User user);

    List<User> findAll();

    User findById(String id);
}

/**
 * 账户仓库接口
 */
interface AccountRepository {
    Account save(Account account);

    Account findById(String id);
}

/**
 * 订单仓库接口
 */
interface OrderRepository {
    Order save(Order order);

    Order findById(String id);
}

/**
 * 日志仓库接口
 */
interface LogRepository {
    Log save(Log log);

    List<Log> findAll();
}

/**
 * 模拟用户仓库实现
 */
class MockUserRepository implements UserRepository {
    private java.util.Map<String, User> users = new java.util.HashMap<>();

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return new java.util.ArrayList<>(users.values());
    }

    @Override
    public User findById(String id) {
        return users.get(id);
    }
}

/**
 * 模拟账户仓库实现
 */
class MockAccountRepository implements AccountRepository {
    private java.util.Map<String, Account> accounts = new java.util.HashMap<>();

    @Override
    public Account save(Account account) {
        accounts.put(account.getId(), account);
        return account;
    }

    @Override
    public Account findById(String id) {
        return accounts.get(id);
    }
}

/**
 * 模拟订单仓库实现
 */
class MockOrderRepository implements OrderRepository {
    private java.util.Map<String, Order> orders = new java.util.HashMap<>();

    @Override
    public Order save(Order order) {
        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public Order findById(String id) {
        return orders.get(id);
    }
}

/**
 * 模拟日志仓库实现
 */
class MockLogRepository implements LogRepository {
    private java.util.Map<String, Log> logs = new java.util.HashMap<>();

    @Override
    public Log save(Log log) {
        logs.put(log.getId(), log);
        return log;
    }

    @Override
    public List<Log> findAll() {
        return new java.util.ArrayList<>(logs.values());
    }
}