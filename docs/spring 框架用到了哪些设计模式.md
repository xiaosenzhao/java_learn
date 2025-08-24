# 模板方法模式

处理重复流程但允许细节变化

```java
// 伪代码展示模板方法核心
public abstract class JdbcTemplate {
    // 定义算法骨架（不可重写）
    public final Object execute(String sql) {
        Connection conn = getConnection(); // 抽象方法
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        Object result = mapResult(rs);     // 抽象方法
        releaseResources(conn, stmt, rs);
        return result;
    }
    
    // 留给子类实现的钩子方法
    protected abstract Connection getConnection();
    protected abstract Object mapResult(ResultSet rs);
}
```

# 工厂模式

# 代理模式

```java
// JDK动态代理示例
public class LogProxy implements InvocationHandler {
    private Object target;
    
    public Object createProxy(Object target) {
        this.target = target;
        return Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            this);
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        System.out.println("【日志】调用方法: " + method.getName());
        return method.invoke(target, args); // 执行原方法
    }
}

// Spring中通过@Aspect实现类似功能
@Aspect
@Component
public class LogAspect {
    @Before("execution(* com.example.service.*.*(..))")
    public void logMethodCall(JoinPoint jp) {
        System.out.println("调用方法: " + jp.getSignature().getName());
    }
}
```

# 单例模式

# 观察者模式

ApplicationEvent 机制

```java
// 1. 定义事件
public class OrderCreatedEvent extends ApplicationEvent {
    public OrderCreatedEvent(Order source) {
        super(source);
    }
}

// 2. 发布事件
@Service
public class OrderService {
    @Autowired ApplicationEventPublisher publisher;
    
    public void createOrder(Order order) {
        // 业务逻辑...
        publisher.publishEvent(new OrderCreatedEvent(order));
    }
}

// 3. 监听事件
@Component
public class EmailListener {
    @EventListener
    public void handleOrderEvent(OrderCreatedEvent event) {
        // 发送邮件通知
    }
}
```

# 策略模式

```java
// 资源加载策略族
Resource res1 = new ClassPathResource("config.xml"); // 类路径策略
Resource res2 = new UrlResource("http://config.com");// 网络策略
Resource res3 = new FileSystemResource("/opt/config");// 文件系统策略

// 统一调用接口
InputStream is = res1.getInputStream();
```

# 适配器模式

```java
// 伪代码：处理多种Controller
public class RequestMappingHandlerAdapter implements HandlerAdapter {
    
    public boolean supports(Object handler) {
        return handler instanceof Controller;
    }
    
    public ModelAndView handle(HttpRequest req, HttpResponse res, Object handler) {
        Controller controller = (Controller) handler;
        return controller.handleRequest(req, res); // 统一适配调用
    }
}

// 实际Spring源码中处理了：
// 1. @Controller注解类 2. HttpRequestHandler 3. Servlet实现等
```

# 装饰器模式

```java
// 典型应用：缓存请求体
ContentCachingRequestWrapper wrappedRequest = 
    new ContentCachingRequestWrapper(rawRequest);

// 可在filter中多次读取body
byte[] body = wrappedRequest.getContentAsByteArray();

public class ContentCachingRequestWrapper extends HttpServletRequestWrapper {
    private ByteArrayOutputStream cachedContent;
    
    @Override
    public ServletInputStream getInputStream() {
        // 装饰原方法：缓存流数据
    }
}
```

# 建造者模式

```java
// 构建复杂的Bean定义
BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(UserService.class);
builder.addPropertyValue("maxRetry", 3);
builder.setInitMethodName("init");
builder.setScope(BeanDefinition.SCOPE_SINGLETON);

// 注册到容器
registry.registerBeanDefinition("userService", builder.getBeanDefinition());
```

# 责任链模式

```java
// Spring MVC核心执行链
public class HandlerExecutionChain {
    private final List<HandlerInterceptor> interceptors = new ArrayList<>();
    
    // 执行前置处理（责任链核心）
    public boolean applyPreHandle(HttpServletRequest request, 
                                 HttpServletResponse response) {
        for (int i = 0; i < interceptors.size(); i++) {
            HandlerInterceptor interceptor = interceptors.get(i);
            // 任意拦截器返回false则中断链条
            if (!interceptor.preHandle(request, response, this.handler)) {
                triggerAfterCompletion(request, response, i); // 清理已完成
                return false;
            }
        }
        return true;
    }
}



@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 构建责任链
        registry.addInterceptor(new LogInterceptor()).order(1);
        registry.addInterceptor(new AuthInterceptor()).order(2);
        registry.addInterceptor(new RateLimitInterceptor()).order(3);
    }
}

// 独立拦截器实现
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        if (!checkToken(req.getHeader("Authorization"))) {
            res.sendError(401); // 认证失败
            return false; // 中断链
        }
        return true; // 放行
    }
}
```

