# Spring事务管理详解

## 1. @Transactional注解原理

### 1.1 核心原理
`@Transactional`注解是Spring声明式事务管理的核心，其工作原理基于**AOP（面向切面编程）**：

1. **代理机制**：Spring通过动态代理为带有`@Transactional`注解的类创建代理对象
2. **事务拦截器**：代理对象会拦截方法调用，在方法执行前后添加事务逻辑
3. **事务管理器**：通过`PlatformTransactionManager`接口实现具体的事务操作

### 1.2 代理类型
- **JDK动态代理**：基于接口的代理（默认）
- **CGLIB代理**：基于类的代理（当类没有实现接口时）

### 1.3 事务传播机制
Spring定义了7种事务传播行为：

```java
public enum Propagation {
    REQUIRED,        // 默认：如果存在事务则加入，否则创建新事务
    SUPPORTS,        // 支持当前事务，如果不存在则以非事务方式执行
    MANDATORY,       // 必须在事务中执行，否则抛出异常
    REQUIRES_NEW,    // 创建新事务，挂起当前事务
    NOT_SUPPORTED,   // 以非事务方式执行，挂起当前事务
    NEVER,           // 以非事务方式执行，如果存在事务则抛出异常
    NESTED           // 如果存在事务则创建嵌套事务，否则创建新事务
}
```

## 2. @Transactional注解属性详解

### 2.1 基本属性
```java
@Transactional(
    // 事务传播行为
    propagation = Propagation.REQUIRED,
    
    // 事务隔离级别
    isolation = Isolation.READ_COMMITTED,
    
    // 超时时间（秒）
    timeout = 30,
    
    // 是否只读
    readOnly = false,
    
    // 回滚异常类
    rollbackFor = Exception.class,
    
    // 不回滚异常类
    noRollbackFor = RuntimeException.class
)
```

### 2.2 事务隔离级别
```java
public enum Isolation {
    DEFAULT,         // 使用数据库默认隔离级别
    READ_UNCOMMITTED, // 读未提交（最低隔离级别）
    READ_COMMITTED,   // 读已提交（Oracle默认）
    REPEATABLE_READ,  // 可重复读（MySQL默认）
    SERIALIZABLE     // 串行化（最高隔离级别）
}
```

## 3. 使用示例

### 3.1 基础事务示例
```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    /**
     * 基础事务示例：用户注册
     */
    @Transactional
    public void registerUser(User user) {
        // 1. 保存用户信息
        userRepository.save(user);
        
        // 2. 创建用户账户
        Account account = new Account();
        account.setUserId(user.getId());
        account.setBalance(BigDecimal.ZERO);
        accountRepository.save(account);
        
        // 如果任何一步失败，整个事务都会回滚
    }
}
```

### 3.2 复杂事务示例
```java
@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private PaymentService paymentService;
    
    /**
     * 复杂事务示例：创建订单
     */
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        timeout = 60,
        rollbackFor = {Exception.class},
        readOnly = false
    )
    public OrderResult createOrder(OrderRequest request) {
        try {
            // 1. 检查库存
            Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("产品不存在"));
            
            if (product.getStock() < request.getQuantity()) {
                throw new InsufficientStockException("库存不足");
            }
            
            // 2. 扣减库存
            product.setStock(product.getStock() - request.getQuantity());
            productRepository.save(product);
            
            // 3. 创建订单
            Order order = new Order();
            order.setProductId(request.getProductId());
            order.setQuantity(request.getQuantity());
            order.setAmount(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            order.setStatus(OrderStatus.CREATED);
            orderRepository.save(order);
            
            // 4. 处理支付
            PaymentResult paymentResult = paymentService.processPayment(order.getAmount(), request.getPaymentMethod());
            
            if (paymentResult.isSuccess()) {
                order.setStatus(OrderStatus.PAID);
                orderRepository.save(order);
                return OrderResult.success(order.getId());
            } else {
                throw new PaymentFailedException("支付失败：" + paymentResult.getMessage());
            }
            
        } catch (Exception e) {
            // 任何异常都会导致事务回滚
            throw new OrderCreationException("订单创建失败", e);
        }
    }
}
```

### 3.3 事务传播行为示例
```java
@Service
public class TransactionPropagationExample {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LogRepository logRepository;
    
    /**
     * 外层事务：REQUIRED
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void outerMethod() {
        // 创建用户
        User user = new User("张三", "zhangsan@example.com");
        userRepository.save(user);
        
        // 调用内层方法
        innerMethod();
        
        // 如果innerMethod抛出异常，整个事务都会回滚
    }
    
    /**
     * 内层事务：REQUIRES_NEW
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void innerMethod() {
        // 记录日志（独立事务）
        Log log = new Log("用户创建操作", "INFO");
        logRepository.save(log);
        
        // 即使外层事务回滚，这个日志记录也会被保存
    }
    
    /**
     * 只读事务示例
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        // 只读操作，不会创建写锁，性能更好
        return userRepository.findAll();
    }
}
```

### 3.4 事务隔离级别示例
```java
@Service
public class TransactionIsolationExample {
    
    @Autowired
    private AccountRepository accountRepository;
    
    /**
     * 读已提交隔离级别
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // 读取账户信息（只能看到已提交的数据）
        Account fromAccount = accountRepository.findById(fromAccountId)
            .orElseThrow(() -> new AccountNotFoundException("源账户不存在"));
        
        Account toAccount = accountRepository.findById(toAccountId)
            .orElseThrow(() -> new AccountNotFoundException("目标账户不存在"));
        
        // 检查余额
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("余额不足");
        }
        
        // 执行转账
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }
    
    /**
     * 可重复读隔离级别
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BigDecimal getAccountBalance(Long accountId) {
        // 在事务期间，多次读取同一数据会得到相同结果
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("账户不存在"));
        
        return account.getBalance();
    }
}
```

## 4. 事务失效场景及解决方案

### 4.1 常见失效场景

#### 场景1：非public方法
```java
@Service
public class TransactionInvalidExample {
    
    @Transactional
    private void privateMethod() {
        // 事务不会生效！
    }
    
    @Transactional
    protected void protectedMethod() {
        // 事务不会生效！
    }
}
```

#### 场景2：自调用问题
```java
@Service
public class SelfInvocationExample {
    
    @Transactional
    public void methodA() {
        // 事务生效
        methodB(); // 自调用，事务不会生效！
    }
    
    @Transactional
    public void methodB() {
        // 事务不会生效！
    }
}
```

#### 场景3：未被Spring管理
```java
// 错误示例：直接new对象
public class WrongExample {
    public void wrongWay() {
        UserService userService = new UserService(); // 不是Spring管理的Bean
        userService.registerUser(user); // @Transactional不会生效
    }
}
```

### 4.2 解决方案

#### 解决自调用问题
```java
@Service
public class SelfInvocationSolution {
    
    @Autowired
    private SelfInvocationSolution self; // 注入自己
    
    public void methodA() {
        // 通过代理对象调用
        self.methodB(); // 事务生效
    }
    
    @Transactional
    public void methodB() {
        // 事务生效
    }
}
```

#### 使用AopContext获取代理对象
```java
@Service
@EnableAspectJAutoProxy(exposeProxy = true)
public class AopContextSolution {
    
    public void methodA() {
        // 获取当前代理对象
        AopContextSolution proxy = (AopContextSolution) AopContext.currentProxy();
        proxy.methodB(); // 事务生效
    }
    
    @Transactional
    public void methodB() {
        // 事务生效
    }
}
```

## 5. 最佳实践

### 5.1 事务粒度控制
```java
@Service
public class TransactionBestPractice {
    
    /**
     * 好的实践：事务方法只包含必要的业务逻辑
     */
    @Transactional
    public void createOrder(OrderRequest request) {
        // 只包含需要事务保护的数据库操作
        validateOrder(request);
        saveOrder(request);
        updateInventory(request);
    }
    
    /**
     * 避免在事务方法中执行耗时操作
     */
    public void processOrder(OrderRequest request) {
        // 非事务操作
        sendNotification(request);
        
        // 事务操作
        createOrder(request);
        
        // 非事务操作
        generateReport(request);
    }
}
```

### 5.2 异常处理
```java
@Service
public class ExceptionHandlingExample {
    
    /**
     * 正确的异常处理
     */
    @Transactional(rollbackFor = Exception.class)
    public void processWithException() {
        try {
            // 业务逻辑
            businessLogic();
        } catch (BusinessException e) {
            // 业务异常，需要回滚
            throw e;
        } catch (Exception e) {
            // 系统异常，需要回滚
            throw new SystemException("系统错误", e);
        }
    }
    
    /**
     * 避免捕获异常后不抛出
     */
    @Transactional
    public void wrongExceptionHandling() {
        try {
            businessLogic();
        } catch (Exception e) {
            // 错误：捕获异常后不抛出，事务不会回滚
            log.error("处理失败", e);
        }
    }
}
```

### 5.3 事务超时设置
```java
@Service
public class TimeoutExample {
    
    /**
     * 设置合理的事务超时时间
     */
    @Transactional(timeout = 30) // 30秒超时
    public void longRunningOperation() {
        // 长时间运行的操作
        processLargeData();
    }
    
    /**
     * 只读事务不需要超时
     */
    @Transactional(readOnly = true)
    public List<Data> queryData() {
        return dataRepository.findAll();
    }
}
```

## 6. 配置示例

### 6.1 启用事务管理
```java
@Configuration
@EnableTransactionManagement
public class TransactionConfig {
    
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```

### 6.2 多数据源事务配置
```java
@Configuration
@EnableTransactionManagement
public class MultiDataSourceTransactionConfig {
    
    @Bean
    @Primary
    public PlatformTransactionManager primaryTransactionManager(DataSource primaryDataSource) {
        return new DataSourceTransactionManager(primaryDataSource);
    }
    
    @Bean
    public PlatformTransactionManager secondaryTransactionManager(DataSource secondaryDataSource) {
        return new DataSourceTransactionManager(secondaryDataSource);
    }
}
```

## 7. 调试和监控

### 7.1 事务日志配置
```properties
# application.properties
logging.level.org.springframework.transaction=DEBUG
logging.level.org.springframework.orm.jpa=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### 7.2 事务监控
```java
@Component
public class TransactionMonitor {
    
    @EventListener
    public void handleTransactionEvent(TransactionApplicationEvent event) {
        System.out.println("事务事件: " + event.getClass().getSimpleName());
    }
}
```

## 总结

`@Transactional`注解是Spring声明式事务管理的核心，通过AOP代理机制实现事务的自动管理。使用时需要注意：

1. **方法必须是public**
2. **避免自调用问题**
3. **合理设置事务传播行为和隔离级别**
4. **正确处理异常**
5. **控制事务粒度**
6. **设置合理的超时时间**

通过正确使用`@Transactional`注解，可以大大简化事务管理代码，提高开发效率和代码可维护性。 