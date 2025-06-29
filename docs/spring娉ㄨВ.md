## springMVC 相关注解
#### @Controller
修饰 controller 层的组件，由控制器负责将用户发来的 URL 请求转发到对应的服务接口，通常还需要配合注解 @RequestMapping 使用
#### @RequestMapping
提供路由信心，负责 URL 到 Controller 中具体函数的映射。当用于方法上时，可以指定请求协议，比如 GET、POST等
#### @RequestBody
请求体的 Content-Type 必须为 application/json 格式的数据，接收到数据后，自动将数据绑定到 java 对象上。
#### @ResponseBody
方法的返回结果直接写到 http response body中，返回的数据格式为 application/json

```java
/** * 登录服务 */
@Controller
@RequestMapping("api")
public class LoginController {
  /**
  	* 登录请求，post请求协议，请求参数数据格式为json
  	* @param request
  	*/    
  @RequestMapping(value = "login", method = RequestMethod.POST)    
  @ResponseBody    
  public ResponseEntity login(@RequestBody UserLoginDTO request){        
    //...业务处理        
    return new ResponseEntity(HttpStatus.OK);    
  }
}
```

#### @RestController
和 @Controller 一样，标注控制层组件。不同的是，它是 @ResponseBody 和 @Controller 的合集

```java
  @RestController
  @RequestMapping("api")
  public class LoginController {        
    /**     * 登录请求，post请求协议，请求参数数据格式为json     * @param request     */   
    @RequestMapping(value = "login", method = RequestMethod.POST)    
    public ResponseEntity login(@RequestBody UserLoginDTO request){        
      //...业务处理        
      return new ResponseEntity(HttpStatus.OK);    
    }
  }
```

#### @RequestParam
用于接收请求参数为表单类型的数据，通常用在方法的参数前面

``` java
/**
    * 登录请求，post请求协议，请求参数数据格式为表单
    */
@RequestMapping(value = "login", method = RequestMethod.POST)
@ResponseBody
public ResponseEntity login(@RequestParam(value = "userName",required = true) String userName,                            
                                         @RequestParam(value = "userPwd",required = true) String userPwd) {    
  //...业务处理    
  return new ResponseEntity(HttpStatus.OK);
}
```

#### @PathVariable
用于获取请求路径中的参数，通常用于 restful 风格的 api 上。

```java
/** 
    * restful风格的参数请求
    * @param id
    */
@RequestMapping(value = "queryProduct/{id}", method = RequestMethod.POST)
@ResponseBody
public ResponseEntity queryProduct(@PathVariable("id") String id) {    
  //...业务处理    
  return new ResponseEntity(HttpStatus.OK);
}
```

#### @GetMapping
表示只支持 GET 请求，等价于 `@RequestMapping(value="/get", method=RequestMethod.GET)`
#### @PostMapping
表示只支持 POST 请求
#### @PutMapping
表示只支持 PUT 请求，通常表示更新某些资源
#### @DeleteMapping
表示只支持 DELETE 请求，通常表示删除某些资源的意思

## bean 相关注解
#### @Service
修饰 service 层的组件，声明一个对象，会将类实例化并注入到 bean 容器里面
#### @Component
泛指组件，当组件不好分类时，使用这个注解标注
#### @Repository
通常用于修饰 dao 层组件。
@Repository 注解属于 Spring 里面最先引入的一批注解，用于将数据库访问层 DAO 的类标识为 Spring Bean。

```java
@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {    
  //具体的方法
}
```

当我们配置服务启动自动扫描dao层包时，Spring会自动帮我们创建一个实现类，然后注入到bean容器里面。当某些类无法被扫描到时，我们可以显式的在数据持久类上标注@Repository注解，Spring会自动帮我们声明对象。
#### @Bean
产生一个Bean对象，并交给 spring 管理

```java
@Configuration
public class AppConfig {       
  //相当于 xml 中配置 Bean    
  @Bean    
  public Uploader initFileUploader() {        
    return new FileUploader();    
  }
}
```

#### @Autowired
自动导入依赖的 bean 对象，默认按照 byType 方式导入对象，而且导入的对象必须存在，当需要导入的对象并不存在时，可以通过配置 required=false 来关闭强制验证。


#### @Resource
自动导入依赖的 bean 对象。默认是按照 byName 方式导入依赖的对象。

```java
/** * 通过名称导入（默认通过名称导入依赖对象） */
@Resource(name = "deptService")
private DeptService deptService;
/** * 通过类型导入 */
@Resource(type = RoleRepository.class)
private DeptService deptService;
```

#### @Qualifier
当有多个同一类型的 bean 时，使用 @Autowired 导入会报错，提示当前对象不是唯一。可以使用 @Qualifier 进行更细粒度的控制，选择其中一个候选。

```java
@Autowired
@Qualifier("deptService")
private DeptService deptService;
```

#### @Scope
限定一个 bean 的作用域。一共有几种:
+ singleton: spring 中的 bean 默认都是单例的
+ prototype：每次请求都会创建一个新的 bean 实例，对象多例
+ request: 每一次 HTTP 请求都会产生一个新的 bean，该 bean 仅在当前 HTTP request 内有效
+ session：每一次 HTTP 请求都会产生一个新的 bean，该 bean 仅在当前 HTTP session 内有效

```java
/** * 单例对象 */
@RestController
@Scope("singleton")
public class HelloController {}
```

## JPA 相关注解
#### @Entity 和 @Table
标明这是一个实体类，这两个注解一般一起使用，如果表名和实体类名相同，@Table 可以省略

#### @Id
表示该属性字段对应数据库表中的关键字段

#### @Column
表示该属性字段对应的数据库表中的列名，如果字段名和列名相同，可以省略

#### @GeneratedValue
表示主键的生成策略，有四个选项：
+ AUTO: 表示由程序控制，默认选项
+ IDENTITY：表示由数据库生成，采用数据库自增，Oracle不支持这种方式
+ SEQUENCE：表示通过数据库的序列生成主键ID，mysql 不支持
+ Table: 表示由特定的数据库产生主键，该方式有利于数据库的移植
+ @SequenceGeneretor 用来定义一个生成主键的序列，需要与 @GeneratedValue 联合使用才有效

````java
@Entity
@Table(name = "TB_ROLE")
@SequenceGenerator(name = "id_seq", sequenceName = "seq_repair", allocationSize = 1)
public class Role implements Serializable {    
  private static final long serialVersionUID = 1L;    
  /**     * 主键ID，采用【id_seq】序列函数自增长     */    
  @Id    
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_seq")    
  private Long id;    
  /* 角色名称     */    
  @Column(nullable = false)    
  private String roleName;    
  /**     * 角色类型     */    
  @Column(nullable = false)    
  private String roleType;
}
```

#### @Transient
表示该属性并非与数据库表的字段进行映射，ORM 框架会忽略该字段

#### @Basic(fetch=FetchType.LAZY)
用在某些属性上，实现懒加载效果。如果配置成 fetch=FetchType.EAGER，表示立刻加载，也就是默认的方式

```java
/** * 延迟加载该属性 */
@Column(nullable = false)
@Basic(fetch = FetchType.LAZY)
private String roleType;
```

#### @JoinColumn
用于标注表与表之间关系的字段，通常与 @OneToOne，@OneToMany 搭配使用

```java
@Entity
@Table(name = "tb_login_log")
public class LoginLog implements Serializable {        
  /**     * 查询登录的用户信息     */    
  @OneToOne    
  @JoinColumn(name = "user_id")    
  private User user;        
  //...get、set
}
```

#### @OneToOne @OneToMany @ManyToOne

## 配置相关注解
#### @Configuration
表示声明一个 java 形式的配置类

#### @EnableAutoConfiguration
帮助 SpingBoot 应用将所有符合条件的 @Configuration 配置类，全部加载到当前的 SpringBoot 里，并创建对应的配置类的 Bean，并把 Bean 交给 IoC 容器进行管理
如果想避开某些配置类的扫描，可以这样处理

```java
@Configuration
@EnableAutoConfiguration(exclude = { org.springframework.boot.autoconfigure.security
                                    .servlet.SecurityAutoConfiguration.class})
  public class AppConfig {    
    //具有业务方法
  }
```

#### ComponentScan
标注哪些路径下的类需要被Spring扫描，用于自动发现和装配一些Bean对象，默认配置是扫描当前文件夹下和子目录下的所有类，如果我们想指定扫描某些包路径，可以这样处理。

```java
@ComponentScan(basePackages = {"com.xxx.a", "com.xxx.b", "com.xxx.c"})
```

#### @SpringBootApplication
等价于使用@Configuration、@EnableAutoConfiguration、@ComponentScan这三个注解，通常用于全局启动类上

#### @EnableTransactionManagement
表示开启事务支持

```java
@SpringBootApplication
@EnableTransactionManagement
public class PropertyApplication {    
  public static void main(String[] args) {        
    SpringApplication.run(PropertyApplication.class, args);    
  }
}
```

#### @Conditional
从 Spring4 开始，可以通过@Conditional注解实现按条件装载bean对象，目前 Spring Boot 源码中大量扩展了@Condition注解，用于实现智能的自动化配置，满足各种使用场景。
+ @ConditionalOnBean: 当某个特定的Bean存在时，配置生效
+ @ConditionalOnMissingBean：当某个特定的Bean不存在时，配置生效
+ @ConditionalOnClass：当Classpath里存在指定的类，配置生效
+ @ConditionalOnMissingClass: 当Classpath里不存在指定的类，配置生效
+ @ConditionalOnExpression: 当给定的SpEL表达式计算结果为true，配置生效
+ @ConditionalOnProperty：当指定的配置属性有一个明确的值并匹配，配置生效

```java
@Configuration
public class ConditionalConfig {    
    /**     * 当AppConfig对象存在时，创建一个A对象     * @return     */    
    @ConditionalOnBean(AppConfig.class)    
    @Bean    
    public A createA(){        
      return new A();    
    }    
    /**     * 当AppConfig对象不存在时，创建一个B对象     * @return     */    
    @ConditionalOnMissingBean(AppConfig.class)    
    @Bean    
    public B createB(){        
      return new B();    
    }    
    /**     * 当KafkaTemplate类存在时，创建一个C对象     * @return     */    
    @ConditionalOnClass(KafkaTemplate.class)   
    @Bean   
    public C createC(){        
      return new C();    
    }    
    /**     * 当KafkaTemplate类不存在时，创建一个D对象     * @return     */    
    @ConditionalOnMissingClass(KafkaTemplate.class)    
    @Bean    
    public D createD(){        
      return new D();    
    }    
    /**     * 当enableConfig的配置为true，创建一个E对象     * @return     */    
    @ConditionalOnExpression("${enableConfig:false}")    
    @Bean    
    public E createE(){        
      return new E();    
    }    
    /**     * 当filter.loginFilter的配置为true，创建一个F对象     * @return     */    
    @ConditionalOnProperty(prefix = "filter",name = "loginFilter",havingValue = "true")    
    @Bean    
    public F createF(){        
      return new F();    
    }
  }
```

#### @value
可以在任意 Spring 管理的 Bean 中通过这个注解获取任何来源配置的属性值，比如你在application.properties文件里，定义了一个参数变量！

```java
config.name=zhangsan
```

在任意的bean容器里面，可以通过@Value注解注入参数，获取参数变量值。

```java
@RestController
public class HelloController {    
  @Value("${config.name}")    
  private String config;    
  @GetMapping("config")    
  public String config(){        
    return JSON.toJSONString(config);    
  }
}
```

#### @ConfigurationProperties
上面@Value在每个类中获取属性配置值的做法，其实是不推荐的。
一般在企业项目开发中，不会使用那么杂乱无章的写法而且维护也麻烦，通常会一次性读取一个 Java 配置类，然后在需要使用的地方直接引用这个类就可以多次访问了，方便维护，示例如下：
首先，在application.properties文件里定义好参数变量。

```java
config.name=demo_1
config.value=demo_value_1
```

然后，创建一个 Java 配置类，将参数变量注入即可！

```java
@Component
@ConfigurationProperties(prefix = "config")
  public class Config {    
    public String name;    
    public String value;    
    //...get、set
  }
```

最后，在需要使用的地方，通过ioc注入Config对象即可！

#### @PropertySource
这个注解是用来读取我们自定义的配置文件的，比如导入test.properties和bussiness.properties两个配置文件

```java
@SpringBootApplication
@PropertySource(value = {"test.properties","bussiness.properties"})
public class PropertyApplication {    
    public static void main(String[] args) {        
      SpringApplication.run(PropertyApplication.class, args);    
    }
  }
```

#### @ImportResource
用来加载 xml 配置文件，比如导入自定义的aaa.xml文件

```java
@ImportResource(locations = "classpath:aaa.xml")
@SpringBootApplication
public class PropertyApplication {    
public static void main(String[] args) {        
      SpringApplication.run(PropertyApplication.class, args);    
    }
  }
```

## 异常处理相关注解
#### @ControllerAdvice 和  @ExceptionHandler
通常组合使用，用于处理全局异常

```java
@ControllerAdvice
@Configuration
@Slf4j
public class GlobalExceptionConfig {        
  private static final Integer GLOBAL_ERROR_CODE = 500;        
  @ExceptionHandler(value = Exception.class)   
  @ResponseBody    
  public void exceptionHandler(HttpServletRequest request, 
                               HttpServletResponse response, 
                               Exception e) throws Exception {        
    log.error("【统一异常处理器】", e);        
    ResultMsg<Object> resultMsg = new ResultMsg<>();        
    resultMsg.setCode(GLOBAL_ERROR_CODE);        
    if (e instanceof CommonException) {            
      CommonException ex = (CommonException) e;           
      if(ex.getErrCode() != 0) {                
        resultMsg.setCode(ex.getErrCode());            
      }            
      resultMsg.setMsg(ex.getErrMsg());        
    }else {            
      resultMsg.setMsg(CommonErrorMsg.SYSTEM_ERROR.getMessage());        
    }        
    WebUtil.buildPrintWriter(response, resultMsg);    
  }        
}
```




