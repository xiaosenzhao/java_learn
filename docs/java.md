# alibaba java 开发手册
## 代码和注释中避免使用种族歧视或者侮辱性词语
黑名单 blockList
白名单 allowList
副手/下属 secondary
## 类名用大写驼峰风格；方法名、参数名、成员变量、局部变量统一使用小写驼峰风格；常量用全部大写，分词用下划线分割。
## 类型和中括号紧挨相连来定义数组
## POJO 类中任何布尔类型的变量，都不要加 is 前缀，否则部分框架解析会引起序列化错误
## 如果模块、类、接口、方法使用了设计模式，在命名时要体现出具体模式
## 接口类中的方法和属性不要加任何修饰符号（public 也不要加），保持代码的简洁性。并加上有效的 javadoc 注释。尽量不要在接口里定义常量，如果一定要定义，最好确定该常量和接口的方法相关，并且是整个应用的基础常量。
## 枚举类名带上 Enum 后缀，枚举成员名称需要全大写，单词间用下划线隔开
枚举其实就是特殊的常量类，且构造方法被默认强制是私有的。
## 各层命名规约
### Service/DAO层方法命名规约
+ 获取单个对象的方法用 get 做前缀
+ 获取多个对象的方法用 list 做前缀，复数结尾，如 listObjects
+ 获取统计值的方法用 count 做前缀
+ 插入的方法用 save/insert 做前缀
+ 删除的方法用 remove/delete 做前缀
+ 修改的方法用 update 做前缀
### 领域模型命名规约
+ 数据对象：xxxDO，xxx即为表名
+ 数据传输对象：xxxDTO，xxx 为业务领域相关的名称
+ 展示对象：xxxVO，xxx 一般为网页名称
+ POJO 是 DO/DTO/BO/VO的统称，禁止命名成 xxxPOJO.
## 不允许任何魔法值（即未经预先定义的常量）直接出现在代码中
## long 或者 Long 赋值时，数值后使用大写 L；浮点数赋值时，后缀为大写 D 或 F
## 不要使用一个常量类维护所有常量
## 强制类型转换时，右括号和强制转化值之间不需要任何空格隔开
## 单行字符数不要超过120个，超出需要换行
### 第二行相对第一行缩进 4 个空格，第三行之后不再缩进
### 运算符和下文一起换行
### 方法调用的点符号与下文一起换行
### 方法调用的多个参数需要换行时，在逗号后进行
### 括号前不要换行
## 单个方法的总行数不超过 80 行
## 不同逻辑、不同语义、不同业务的代码之间插入一个空行
## 避免通过一个类的对象引用来访问该类的静态变量或静态方法，会增加编译器解析成本，直接用类名来访问
## 所有的覆写方法，都要标注 @Override
## 相同参数类型，相同业务含义，才可以使用可变参数，参数类型避免定义为 Object
## 接口过时必须加 @Deprecated 注解，并说明采用的新接口是啥
## Object 的 equals 方法容易抛异常，应使用常量或者确定有值的对象来调用 equals
## 所有的整型包装类对象之间的值比较，全部使用 equals 方法比较
对于 Integer val = ? 在 -128 至 127 之间的赋值，Integer 对象是在 IntegerCache.cache 产生，会复用已有对象，这个区间的 Integer 可以直接使用 == 比较。这个区间之外的所有数据，都会在堆上产生，并不会复用已有对象。所以 Integer 比较，使用 equals 比较。
## 任何货币金额，均以最小单位、整数类型进行存储
## 浮点数之间的等值判断，要考虑精度问题，基本数据类型不能使用 == 进行比较，包装数据类型不能使用 equals 进行判断
```java
float a = 1.0F - 0.9F;
float b = 0.9F - 0.8F;
float diff = 1e-6F;
if (Math.abs(a - b) < diff) {
	System.out.println("true");
}
```

```java
BigDecimal a = new BigDecimal("1.0");
BigDecimal b = new BigDecimal("0.9");
BigDecimal c = new BigDecimal("0.8");
BigDecimal x = a.subtract(b);
BigDecimal y = b.subtract(c);

if (x.compareTo(y) == 0) {
	System.out.println("true");
}
```

## BigDecimal 的等值比较应使用 compareTo 方法，而不是 equals 方法。
equals 方法会比较值和精度（1.0 和 1.00 的精度不同），而 compareTo 会忽略精度
## 禁止使用构造方法 BigDecimal(double) 的方法把 double 值转为 BigDecimal 对象
BigDecimal(double) 存在精度损失风险，在精确计算或值比较的场景中可能会导致业务逻辑异常。
优先推荐入参为 String 的构造方法，或使用 BigDecimal 的 valueOf 方法，此方法内部执行了 Double 的 toString， 而 Double 的 toString 按 double 的实际能表达的精度对尾数进行了截断。

## 基本数据类型与包装数据类型的使用标准如下：
+ 所有的 POJO 类属性必须使用包装数据类型
+ RPC 方法的返回值和参数必须使用包装数据类型
+ 所有的局部变量使用基本数据类型

## 定义 DO/PO/DTO/VO 等 POJO 类时，不要设定任何属性默认值

## 序列化类新增属性时，请不要修改 serialVersionUID 字段，避免反序列化失败；如果完全不兼容升级，避免反序列化混乱，修改 serialVersionUID
serialVersionUID 不一致，会抛序列化运行时异常

## 构造方法里不要写业务逻辑

## POJO 类必须写 toString 方法

## 日期时间
+ yyyy 表示当天所在的年
+ YYYY 表示当天所属的周属于的年份，只要本周跨年，返回的就是下一年
+ M 表示月份
+ m 表示分钟
+ H 表示 24 小时制
+ h 表示 12 小时制
+ 获取当前毫秒数 System.currentTimeMillis()，不能用 new Date().getTime()
+ 不允许使用 java.sql.Date, java.sql.Time，java.sql.Timestamp
+ 禁止在程序中写死一年为 365 天。
+ 使用枚举值来指月份，如果使用数字，注意 Date、Calendar 等日期相关类的月份 month 取值范围从 0 到 11 之间。例如 Calendar.JANUARY

```java
// 获取今年的天数
int daysOfThisYear = LocalDate.now().lengthOfYear();
// 获取指定某年的天数
LocalDate.of(2011, 1, 1).lengthOfYear();
```
## 泛型通配符
### 上界通配符 <? extends T>
表示一个未知类型，这个未知类型是 T 的子类型或者 T。通常用于读取数据。
此泛型写法的集合不能使用 add 方法。
### 下界通配符 <? super T>
表示一个未知类型，这个未知类型是 T 的父类型或者 T。通常用于写数据。
此泛型写法的集合不能使用 get 方法。
## HashMap 和 Hashtable
### Hashtable 是线程安全的，HashMap 不是线程安全的，并发情况下使用 ConcurrentHashMap
### Hashtable 性能比 HashMap 差
### Hashtable 使用 Enumeration 进行迭代，HashMap 使用 Iterator 进行迭代
### Hashtable 默认的初始容量是 11，负载因子是 0.75
### HashMap 默认的初始容量是 16，负载因子是 0.75
### Hashtable 继承自 Dictionary 类，而 HashMap 实现 Map 接口

## Executors 
### Executors 是一个工厂类，提供了几种常用的线程池创建方法
+ FixedThreadPool：固定大小的线程池。如果某个任务提交时，所有线程都在忙，那么该任务会被放入队列中等待空闲线程
+ SingleThreadExecutor：只包含一个线程的线程池。这个线程会顺序执行提交的所有任务，如果因异常终止，会有另一个线程替代它
+ CachedThreadPool：可根据需要创建新线程的线程池。如果没有线程可用，并且当前线程数小于最大线程数，会创建新线程；否则，等待。线程在空闲 60s 后会被回收。
+ ScheduledThreadPool：支持定时及周期性任务执行的线程池。可以安排任务在给定延迟后运行，或者定期执行
+ SingleThreadScheduledExecutor：单线程的定时线程池

```java
import java.util.concurrent.*;

public class ThreadPoolExample {

    public static void main(String[] args) {
        // 固定大小的线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            int taskId = i;
            fixedThreadPool.execute(() -> {
                System.out.println("Task " + taskId + " is running by " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // 单线程线程池
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10; i++) {
            int taskId = i;
            singleThreadExecutor.execute(() -> {
                System.out.println("Task " + taskId + " is running by " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // 可缓存的线程池
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            int taskId = i;
            cachedThreadPool.execute(() -> {
                System.out.println("Task " + taskId + " is running by " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // 定时线程池
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        for (int i = 0; i < 10; i++) {
            int taskId = i;
            scheduledThreadPool.schedule(() -> {
                System.out.println("Task " + taskId + " is running by " + Thread.currentThread().getName());
            }, 1, TimeUnit.SECONDS);
        }

        // 单线程定时线程池
        ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        for (int i = 0; i < 10; i++) {
            int taskId = i;
            singleThreadScheduledExecutor.schedule(() -> {
                System.out.println("Task " + taskId + " is running by " + Thread.currentThread().getName());
            }, 1, TimeUnit.SECONDS);
        }

        // 关闭线程池
        fixedThreadPool.shutdown();
        singleThreadExecutor.shutdown();
        cachedThreadPool.shutdown();
        scheduledThreadPool.shutdown();
        singleThreadScheduledExecutor.shutdown();
    }
}
```

## ThreadPoolExecutor
ThreadPoolExecutor 是一个更底层、更灵活的线程池实现。允许开发者自定义线程池的各种参数，例如核心线程数、最大线程数、线程空闲时间、任务队列等。
构造参数：
+ corePoolSize：核心线程数
+ maximumPoolSize：最大线程数
+ keepAliveTime：线程空闲时间
+ unit：时间单位
+ workQueue：任务队列
+ threadFactory：线程工厂
+ handler：拒绝策略

```java
import java.util.concurrent.*;

public class ThreadPoolExecutorExample {
    public static void main(String[] args) {
        // 创建一个自定义的线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            5, // 核心线程数
            10, // 最大线程数
            60L, // 线程空闲时间
            TimeUnit.SECONDS, // 时间单位
            new LinkedBlockingQueue<>(100), // 任务队列
            new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
        );
        
        // 提交任务
        for (int i = 0; i < 20; i++) {
            final int taskNumber = i;
            executor.submit(() -> {
                System.out.println("Task " + taskNumber + " executed by " + Thread.currentThread().getName());
            });
        }
        
        // 关闭线程池
        executor.shutdown();
    }
}
```
## 线程池不允许使用 Executors 创建，因为可以创建 Integer.MAX_VALUE长度的队列。允许通过 ThreadPoolExecutor 的方式，规避资源耗尽的风险。

## hashCode 和 equals
+ 只要覆写 equals，就必须覆写 hashCode
+ 因为 Set 存储的是不重复的对象，依据 hashCode 和 equals 进行判断，所以 Set 存储的对象必须覆写这两种方法
+ 如果自定义对象为 Map 的键，那么必须覆写 hashCode 和 equals

## 使用 java.util.stream.Collectors 类的 toMap() 方法转为 Map 集合
+ 一定要使用参数为 BinaryOperator，参数名为 mergeFunction 的方法，否则 key 相同时会抛出 IllegalStateException 异常
+ 当 value 为 null 时，会抛 NPE 异常

```java
List<Pair<String, Double>> pairArrayList = new ArrayList<>(3);
pairArrayList.add(new Pair<>("version", 12.0));
pairArrayList.add(new Pair<>("version", 12.19));
pairArrayList.add(new Pair<>("version", 6.8));

// 生成的 map 集合中只有一个键值对 （"version", 6.8)
Map<String, Double> map = pairArrayList.stream()
	.collect(Collectors.toMap(Pair::getKey, Pair::getValue, (v1, v2) -> v2));
```
## ArrayList 的 subList 结果不可以强转为 ArrayList
subList() 的返回是 ArrayList 的内部类 SubList，并不是 ArrayList 本身，而是 ArrayList 的一个视图，对于 SubList 的所有操作最终会反映到原列表上

## 使用 Map 的方法 keySet() / values() / entrySet() 返回集合对象时，不可以对其进行添加元素操作，否则会抛 UnsupportedOperationException 异常

## Collections 类返回的对象，如 emptyList() / singletonList() 等都是 immutable list，不可以对其进行添加或者删除元素操作，否则会抛 UnsupportedOperationException 异常

## 在 subList 场景中，高度注意对父集合元素的增加或删除，均会导致子列表的遍历、增加、删除产生 ConcurrentModificationException 异常

## 使用集合转数组的方法，必须使用集合的 toArray(T[] array)，传入类型完全一致、长度为 0 的空数组
如果直接使用 toArray 无参方法存在问题，返回的是 Object[] 类，若强制转为其它类型，会抛 ClassCastException 异常
传参数组空间大小 length：
+ 等于0，动态创建与 size 相同的数组，性能最好
+ 大于 0 但小于 size，重新创建大小等于 size 的数组，增加 GC 负担
+ 等于 size，在高并发情况下，数组创建完成之后，size 正在变大的情况下，负面影响与2相同
+ 大于 size，空间浪费，且在 size 处插入了 null 值

```java
List<String> list = new ArrayList<>(2);
list.add("guan");
list.add("bao");
String[] array = list.toArray(new String[0]);
```

## 使用 Collection 接口任何实现类的 addAll() 方法时，要对输入的集合参数做 NPE 判断

## 使用工具类 Arrays.asList() 把数组转换成集合时，不能使用其修改集合相关的方法，它的 add / remove / clear 方法会抛出 UnsupportedOperationException 异常。

## 在无泛型限制定义的集合赋值给泛型限制的集合时，在使用集合元素时，需要进行 instanceof 判断，避免抛出 ClassCastException 异常。

## 不要在 foreach 循环里进行元素的 remove / add 操作。remove 元素请使用 iterator 方式，如果并发操作，需要对 iterator 对象加锁。
```java
List<String> list = new ArrayList<>();
list.add("1");
list.add("2");
Iterator<String> iterator = list.iterator();
while (iterator.hasNext()) {
	String item = iterator.next();
	if (删除元素的条件) {
		iterator.remove();
	}
}
```

## 集合初始化时，指定集合初始值大小。

## 使用 entrySet 遍历 Map 类集合 KV，而不是 keySet 方式进行遍历。

## 合理利用好集合的有序性（sort）和稳定性（order），避免集合的无序性（unsort）和不稳定性（unorder）带来的负面影响。
有序性是指遍历的结果是按某种比较规则依次排列的，稳定性指集合每次遍历的元素次序是一定的。如：
ArrayList 是 order / unsort；HashMap 是 unorder / unsort；TreeSet 是 order / sort。

## SimpleDateFormat 是线程不安全的类，一般不要定义为 static 变量

## 使用 Instant 代替 Date，LocalDateTime 代替 Calendar，DateTimeFormatter 代替 SimpleDateFormat
Instant 表示时间线上的一个瞬间点

```java
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class InstantExample {

    public static void main(String[] args) {
        // 1. 获取当前时间的 Instant
        Instant now = Instant.now();
        System.out.println("Current Instant: " + now);

        // 2. 将 Instant 转换为 LocalDateTime
        LocalDateTime localDateTime = now.atZone(ZoneId.systemDefault()).toLocalDateTime();
        System.out.println("Converted to LocalDateTime: " + localDateTime);

        // 3. 对 Instant 进行加减操作
        Instant plusOneHour = now.plus(1, ChronoUnit.HOURS);
        System.out.println("Plus one hour: " + plusOneHour);

        Instant minusOneDay = now.minus(1, ChronoUnit.DAYS);
        System.out.println("Minus one day: " + minusOneDay);

        // 4. 比较两个 Instant 对象
        Instant anotherInstant = Instant.now().plus(2, ChronoUnit.HOURS);
        if (now.isBefore(anotherInstant)) {
            System.out.println(now + " is before " + anotherInstant);
        } else {
            System.out.println(now + " is not before " + anotherInstant);
        }

        // 5. 将 Instant 格式化为字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedInstant = now.atZone(ZoneId.systemDefault()).format(formatter);
        System.out.println("Formatted Instant: " + formattedInstant);

        // 6. 从字符串解析 Instant
        String instantString = "2023-10-01T12:34:56.789Z";
        Instant parsedInstant = Instant.parse(instantString);
        System.out.println("Parsed Instant: " + parsedInstant);

        // 7. 将 Instant 转换为 ZonedDateTime
        ZonedDateTime zonedDateTime = now.atZone(ZoneId.systemDefault());
        System.out.println("Converted to ZonedDateTime: " + zonedDateTime);
    }
}
```

## 必须回收自定义的 ThreadLocal 变量记录的当前线程的值

```java
objectThreadLocal.set(userInfo);
try {

} finally {
	objectThreadLocal.remove();
}
```

## 多线程并行处理定时任务时，Timer 运行多个 TimeTask 时，只要其中之一没有捕获抛出的异常，其它任务便会自动终止运行，使用 ScheduledExecutorService 则没有这个问题

## 乐观锁和悲观锁是处理并发控制的两种不同策略
### 悲观锁
假设在数据处理过程中最坏的情况会发生，在进行任何操作之前，会先锁定数据，直到事务完成并释放锁
+ 独占性
+ 阻塞
+ 使用：写操作多，竞争激烈的场景
数据库层面，通过 select .. for update 语句来实现
```sql
// 首先关闭mysql 自动更新 set autocommit = 0
// 开始事务
begin; /begin work; /start transaction （三者选一就行)
// 读数据
select ... from ... where id = 1 for update; （指定主键，锁行；不指定主键，锁表)
// 修改数据
// 提交事务
commit；/commit work
```
编程语言层面，用互斥锁来实现
### 乐观锁
假设在数据处理过程中最好的情况会发生。不会在读取数据时立刻加锁，而是在更新数据时检查数据是否被其他事务修改过。如果没有被修改，则更新成功；否则，更新失败，需要重新尝试。
+ 非阻塞
+ 版本号或时间戳：用来监测数据是否被修改
+ 使用：读操作较多、写操作较少并且冲突概率较低
数据库层面，可以在表里加一个版本号，更新时检查版本号是否一致
编程语言层面，可以通过原子类型来实现

## 资金相关的金融敏感信息，使用悲观锁策略

## 使用 CountDownLatch 进行异步转同步操作，每个线程退出前必须调用 countDown 方法

## 避免 Random 实例被多线程使用，虽然共享该实例是线程安全的，但会因竞争同一个 seed 导致性能下降
Random 实例包括 java.util.Random 的实例或者 Math.random() 的方式。Java7之后推荐使用 ThreadLocalRandom

## 通过双重检查锁实现延迟初始化需要将目标属性声明为 volatile 型。
```java
public class LazyInitDemo {
	private volatile Helper helper = null;
	public Helper getHelper() {
		if (helper == null) {
			synchronized(this) {
				if (helper == null) {
					helper = new Helper();
				}
			}
		}
		return helper;
	}
}

## switch 括号内的变量类型是 String 并且该变量为外部参数时，必须先进行 null 判断

## 三目运算符 condition ? 表达式1 : 表达式2 中，高度注意表达式1和表达式2在类型对齐时，可能会因为自动拆箱抛 NPE 异常

## 在高并发场景中，避免使用 “等于” 判断作为中断或退出的条件

## 前后端数据列表相关的接口，如果为空，返回空数组[] 或空集合 {}

## 对于需要使用超大整数的场景，服务端统一使用 String 返回

## HTTP 请求通过 URL 传递参数时，不能超过 2048 字节；通过 body 传递内容时，必须控制长度。nginx 默认限制是 1MB，tomcat 默认限制为 2MB。



