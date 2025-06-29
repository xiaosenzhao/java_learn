# java 版本
+ Java SE：Standard Edition，标准版，包含标准的JVM和标准库
+ Java EE: Enterprise Edition，企业版，它只是在Java SE的基础上加上了大量的API和库，以便方便开发Web应用、数据库、消息服务等，Java EE的应用使用的虚拟机和Java SE完全相同
+ Java ME: Micro Edition，瘦身版，为嵌入式设备设计，没有流行起来

```
┌───────────────────────────┐  
│Java EE 					│    
│    ┌────────────────────┐ │    
│    │Java SE             │ │  
│    │    ┌─────────────┐ │ │  
│    │    │   Java ME   │ │ │   
│    │    └─────────────┘ │ │  
│    └────────────────────┘ │  
└───────────────────────────┘
```
# 学习路径
1. 首先要学习Java SE，掌握Java语言本身、Java核心开发技术以及Java标准库的使用
2. 如果继续学习Java EE，那么Spring框架、数据库开发、分布式架构就是需要学习的
3. 如果要学习大数据开发，那么Hadoop、Spark、Flink这些大数据平台就是需要学习的，他们都基于Java或Scala开发
4. 如果想要学习移动开发，那么就深入Android平台，掌握Android App开发

# 数组类型
1. 数组是同一数据类型的集合，数组一旦创建后，大小就不可变
2. 可以通过索引访问数组元素，但索引超出范围将报错
3. 数组元素可以是值类型（如int）或引用类型（如String），但数组本身是引用类型
4. 数组所有元素初始化为默认值，整型都是0，浮点型是0.0，布尔型是false
```java
int[] ns = new int[5];
int[] ns = new int[] { 68, 79, 91, 85, 62 };  
int[] ns = { 68, 79, 91, 85, 62 };   
```

# switch 语句
从Java 12开始，switch语句升级为更简洁的表达式语法，使用类似模式匹配（Pattern Matching）的方法，保证只有一种路径会被执行，并且不需要break语句。
```java
// switch
public class Main {
    public static void main(String[] args) {
        String fruit = "apple";
        switch (fruit) {
        case "apple" -> System.out.println("Selected apple");
        case "pear" -> System.out.println("Selected pear");
        case "mango" -> {
            System.out.println("Selected mango");
            System.out.println("Good choice!");
        }
        default -> System.out.println("No fruit selected");
        }
    }
}
```

```java
// switch
public class Main {
    public static void main(String[] args) {
        String fruit = "apple";
        int opt = switch (fruit) {
            case "apple" -> 1;
            case "pear", "mango" -> 2;
            default -> 0;
        }; // 注意赋值语句要以;结束
        System.out.println("opt = " + opt);
    }
}
```

## yield
```java
// yield
public class Main {
    public static void main(String[] args) {
        String fruit = "orange";
        int opt = switch (fruit) {
            case "apple" -> 1;
            case "pear", "mango" -> 2;
            default -> {
                int code = fruit.hashCode();
                yield code; // switch语句返回值
            }
        };
        System.out.println("opt = " + opt);
    }
}
```

# 命令行参数
```java
public class Main {
    public static void main(String[] args) {
        for (String arg : args) {
            if ("-version".equals(arg)) {
                System.out.println("v 1.0");
                break;
            }
        }
    }
}
```

# 可变参数
可变参数可以保证无法传入 null，因为传入 0 个参数时，接收到的实际值是一个空数组，而不是 null。

# 继承
+ 如果父类没有默认的构造方法，子类就必须显示调用 super() 并给出参数，以便让编译器定位到父类的一个合适的构造方法。
+ 子类不会继承任何父类的构造方法，子类默认的构造方法都是编译器自动生成的。

## 阻止继承
+ final 修饰符，阻止任何类继承该类
+ 从 java 15 开始，允许使用 sealed 修饰 class，并通过 permits 明确写出能够从该 class 继承的子类的名称。
```java
public sealed class Shape permits Rect， Circle, Triangle {
}
```
## 向下转型
向下转型可能会失败。失败的时候，java 虚拟机会报 ClassCastException。
为了避免向下转型出错，java 提供了一个 instanceof 操作符，判断一个对象是不是某种类型。
```java
Person p = new Person();
System.out.println(p instanceof Person); // true
System.out.println(p instanceof Student); // false

Student s = new Student();
System.out.println(s instanceof Person); // true
System.out.println(s instanceof Student); // true

Student n = null;
System.out.println(n instanceof Student); // false
```
如果一个引用变量为 null，对任何 instanceof 判断都为 false
```java
Object obj = "hello";
if (obj instanceof String) {
    String s = (String) obj;
    System.out.println(s.toUpperCase());
}
```
从java 14开始，可以简化为
```java
Object obj = "hello";
if (obj instanceof String s) {
// 可以直接使用变量s:
    System.out.println(s.toUpperCase());
}
```
# final
+ final 修饰类，表示该类不能被继承
+ final 修饰方法，表示该方法不能被重写
+ final 修饰变量，该对象必须在创建对象时初始化，随后不可修改。

# 接口和抽象类
接口可以定义静态字段，而且静态字段必须是 final。
|| 抽象类 | 接口 |
| --- | --- | --- |
| 继承 | 只能继承一个类 | 可以实现多个 interface |
| 字段 | 可以定义实例字段 | 不能定义实例字段 |
| 抽象方法 | 可以定义抽象方法 | 可以定义抽象方法 |
| 非抽象方法 | 可以定义非抽象方法 | 可以定义 default 方法 |

```java
// interface
public class Main {
    public static void main(String[] args) {
        Person p = new Student("Xiao Ming");
        p.run();
    }
}

interface Person {
    String getName();
    default void run() {
        System.out.println(getName() + " run");
    }
}

class Student implements Person {
    private String name;

    public Student(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
```

# StringJoiner
字符串拼接
```java
import java.util.StringJoiner;
public class Main {
    public static void main(String[] args) {
        String[] names = {"Bob", "Alice", "Grace"};
        var sj = new StringJoiner(", ");
        for (String name : names) {
            sj.add(name);
        }
        System.out.println(sj.toString());
    }
}
```
拼接上开头和结尾
```java
import java.util.StringJoiner;
public class Main {
    public static void main(String[] args) {
        String[] names = {"Bob", "Alice", "Grace"};
        var sj = new StringJoiner(", ", "Hello ", "!");
        for (String name : names) {
            sj.add(name);
        }
        System.out.println(sj.toString());
    }
}
```
String.join() 内部调用了 StringJoiner。如果不需要开头和结尾，用这个更方便

# 包装类
所有的包装类型都是不变类。一旦创建了 Integer 对象，该对象就是不变的。
对包装类进行比较，不能使用 == 比较，必须使用 equals() 方法。

# HexFormat 转十六进制
```java
import java.util.HexFormat;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        byte[] data = "Hello".getBytes();
        HexFormat hf = HexFormat.of();
        String hexData = hf.formatHex(data); // 48656c6c6f
    }
}
```
定制转换格式
```java
// 分隔符为空格，添加前缀0x，大写字母:
HexFormat hf = HexFormat.ofDelimiter(" ").withPrefix("0x").withUpperCase();
hf.formatHex("Hello".getBytes())); // 0x48 0x65 0x6C 0x6C 0x6F
```
从十六进制字符串到 byte[] 数组转换，使用 parseHex()
```java
byte[] bs = HexFormat.of().parseHex("48656c6c6f");
```

# 异常
                     ┌───────────┐
                     │  Object   │
                     └───────────┘
                           ▲
                           │
                     ┌───────────┐
                     │ Throwable │
                     └───────────┘
                           ▲
                 ┌─────────┴─────────┐
                 │                   │
           ┌───────────┐       ┌───────────┐
           │   Error   │       │ Exception │
           └───────────┘       └───────────┘
                 ▲                   ▲
         ┌───────┘              ┌────┴──────────┐
         │                      │               │   
┌─────────────────┐    ┌─────────────────┐┌───────────┐     
│OutOfMemoryError │... │RuntimeException ││IOException│...   
└─────────────────┘    └─────────────────┘└───────────┘    
                                ▲    
                    ┌───────────┴─────────────┐  
                    │                         │  
         ┌─────────────────────┐ ┌─────────────────────────┐  
         │NullPointerException │ │IllegalArgumentException │...  
         └─────────────────────┘ └─────────────────────────┘   
java 规定：
+ 必须捕获的异常，包括Exception及其子类，但不包括RuntimeException及其子类，这种类型的异常称为Checked Exception。
+ 不需要捕获的异常，包括Error及其子类，RuntimeException及其子类。
+ RuntimeException 及其子类不强制要求捕获，不是说不可以捕获。

## printStackTrace 打印异常

## 常用异常
Exception
├─ RuntimeException
│  ├─ NullPointerException
│  ├─ IndexOutOfBoundsException
│  ├─ SecurityException
│  └─ IllegalArgumentException
│     └─ NumberFormatException
├─ IOException
│  ├─ UnsupportedCharsetException
│  ├─ FileNotFoundException
│  └─ SocketException
├─ ParseException
├─ GeneralSecurityException
├─ SQLException
└─ TimeoutException

## Java 日期和 Mysql 日期
### mysql 日期
#### datatime
用于存储日期和时间，格式为 `YYYY-MM-DD HH:MM:SS`，范围从 '1000-01-01 00:00:00‘ 到 '9999-12-31 23:59:59’
#### date
用于存储日期，格式为 `YYYY-MM-DD`，范围从 '1000-01-01' 到 ‘9999-12-31’
#### timestamp
用于存储日期和时间，格式为 `YYYY-MM-DD HH:MM:SS`，范围从 '1970-01-01 00:00:01‘ UTC 到 ’2038-01-19 03:14:07' UTC
datatime 和 date 存储方式相同，都是使用固定的 8 个字节。timestamp 使用 4 个字节来存储

### java数据类型
#### java.util.Date
支持日期和时间，通用
#### java.util.Date
只支持日期，对应 SQL 的 date 类型，格式 年月日
#### java.sql.Time
只支持时间，对应 SQL 的 time 类型，格式 时分秒
#### java.sql.Timestamp
支持日期和时间，对应 sql 的 timestamp、datetime 类型，格式 年月日时分秒毫秒
#### java.time.Year
支持年份，格式 整数

# 不可变集合
创建一个不可修改的集合视图，任何尝试修改这个集合的操作都会抛出 UnsupportedOperationException。

## 不可变 List

```java
// 创建一个可变的 List
List<String> mutableList = new ArrayList<>();
mutableList.add("Element1");
mutableList.add("Element2");

// 将可变的List转换为不可变的List
List<String> immutableList = Collections.unmodifiableList(mutableList);

// 遍历不可变List
for (String element : immutableList) {
	System.out.println(element);
}
```

## 不可变 Set

```java
// 创建一个可变的Set
Set<String> mutableSet = new HashSet<>();
mutableSet.add("Element1");
mutableSet.add("Element2");

// 将可变的 Set 转换为不可变的 Set
Set<String> immutableSet = Collections.unmodifiableSet(mutableSet);

// 遍历不可变 Set
for (String element : immutableSet) {
	System.out.println(element);
}
```

## 不可变 Map

```java
// 创建一个可变的 Map
Map<String, Integer> mutableMap = new HashMap<>();
mutableMap.put("key1", 1);
mutableMap.put("key2", 2);

// 将可变的 Map 转换为不可变的 Map
Map<String, Integer> immutableMap = Collections.unmodifiableMap(mutableMap);

// 遍历不可变 Map
for (Map.Entry<String, Integer>  entry : immutableMap.entrySet()) {
	//
}
```

## 使用 Guava 库创建不可变集合

```java
// 定义一个静态不可变列表

```