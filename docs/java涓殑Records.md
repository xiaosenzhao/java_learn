# 简化数据载体类的创建
```java
public record Person(String name, int age) {}
```
自动提供：
+ 构造器：接收所有组件的构造器
+ 访问器方法：name(), age()
+ equals 方法：基于所有组件的值比较
+ hashCode方法：基于所有组件计算
+ toString() 方法：格式化输出所有组件

# 应用场景
## DTO（Data Transfer Object)
```java
public record UserDto(String username, String email, LocalDateTime createdAt) {}

// 使用
UserDto user = new UserDto("john", "john@example.com", LocalDateTime.now());
System.out.println(user.username()); // 访问数据
```
## API 相应对象
```java
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    int statusCode
) {}

// 使用
ApiResponse<List<String>> response = new ApiResponse<>(
    true, 
    "Success", 
    Arrays.asList("item1", "item2"), 
    200
);
```
## 配置对象
```java
public record DatabaseConfig(
    String host,
    int port,
    String database,
    String username,
    String password
) {}
```
## 坐标/几何对象
```java
public record Point(double x, double y) {
    public double distanceFrom(Point other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }
}

public record Rectangle(Point topLeft, Point bottomRight) {
    public double area() {
        return Math.abs((bottomRight.x() - topLeft.x()) * 
                       (bottomRight.y() - topLeft.y()));
    }
}
```

# 与其他特性结合使用
## 与 switch 表达式结合
```java
public sealed interface Shape permits Circle, Rectangle, Triangle {}
public record Circle(double radius) implements Shape {}
public record Rectangle(double width, double height) implements Shape {}
public record Triangle(double base, double height) implements Shape {}

public static double calculateArea(Shape shape) {
    return switch (shape) {
        case Circle(double radius) -> Math.PI * radius * radius;
        case Rectangle(double width, double height) -> width * height;
        case Triangle(double base, double height) -> 0.5 * base * height;
    };
}
```

## 与stream api结合使用

```java
public record Student(String name, int grade, String subject) {}

List<Student> students = List.of(
    new Student("Alice", 95, "Math"),
    new Student("Bob", 87, "Math"),
    new Student("Charlie", 92, "Science")
);

Map<String, Double> averageBySubject = students.stream()
    .collect(Collectors.groupingBy(
        Student::subject,
        Collectors.averagingInt(Student::grade)
    ));
```

# 限制
+ 不能继承类：Records 隐式继承 java.lang.Record
+ 不能被继承：Records 隐式 final
+ 不能声明实例字段：只有组件字段
+ 所有字段都是 final 的

# 优势
+ 代码简洁
+ 天生不可变
+ 自动实现核心方法
+ 类型安全：编译时检查
+ 可读性强：意图明确，专注于数据结构
+ 性能良好：JVM 对 Records 有特殊优化
