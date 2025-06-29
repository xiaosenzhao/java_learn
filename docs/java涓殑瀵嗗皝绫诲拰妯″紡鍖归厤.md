# Sealed Classes（密封类）
Sealed Class 允许限制哪些类可以继承或者实现某个类/接口
## 基本语法
```java
// 定义密封接口
public sealed interface Shape 
    permits Circle, Rectangle, Triangle {
}

// 只有这些类可以实现Shape
public final class Circle implements Shape {
    private final double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    public double radius() { return radius; }
}

public final class Rectangle implements Shape {
    private final double width, height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    public double width() { return width; }
    public double height() { return height; }
}

public final class Triangle implements Shape {
    private final double base, height;
    
    public Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }
    
    public double base() { return base; }
    public double height() { return height; }
}
```

## 规则

```java
public sealed class Animal permits Dog, Cat {
    // 基类可以是密封的
}

// 子类必须是以下之一：
public final class Dog extends Animal {     // final - 不能再被继承
    // ...
}

public sealed class Cat extends Animal     // sealed - 可以有限制地被继承
    permits PersianCat, SiameseCat {
    // ...
}

public non-sealed class Bird extends Animal {  // non-sealed - 重新开放继承
    // ...
}
```

# 应用场景

## 状态建模

```java
public sealed interface HttpResponse 
    permits SuccessResponse, ErrorResponse, RedirectResponse {
}

public record SuccessResponse(int statusCode, String body) implements HttpResponse {}
public record ErrorResponse(int statusCode, String error) implements HttpResponse {}
public record RedirectResponse(String location) implements HttpResponse {}

// 处理响应
public String handleResponse(HttpResponse response) {
    return switch (response) {
        case SuccessResponse(int code, String body) -> 
            "成功: " + code + " - " + body;
        case ErrorResponse(int code, String error) -> 
            "错误: " + code + " - " + error;
        case RedirectResponse(String location) -> 
            "重定向到: " + location;
    };
}
```

## 表达式树

```java
public sealed interface Expression 
    permits NumberExpr, AddExpr, MulExpr, VarExpr {
}

public record NumberExpr(double value) implements Expression {}
public record VarExpr(String name) implements Expression {}
public record AddExpr(Expression left, Expression right) implements Expression {}
public record MulExpr(Expression left, Expression right) implements Expression {}

// 表达式求值
public double evaluate(Expression expr, Map<String, Double> variables) {
    return switch (expr) {
        case NumberExpr(double value) -> value;
        case VarExpr(String name) -> variables.get(name);
        case AddExpr(Expression left, Expression right) -> 
            evaluate(left, variables) + evaluate(right, variables);
        case MulExpr(Expression left, Expression right) -> 
            evaluate(left, variables) * evaluate(right, variables);
    };
}
```

# 模式匹配 Pattern Matching

## instanceof 模式匹配

```java
// 传统方式
public String describe(Object obj) {
    if (obj instanceof String) {
        String str = (String) obj;
        return "字符串长度: " + str.length();
    } else if (obj instanceof Integer) {
        Integer num = (Integer) obj;
        return "整数值: " + num;
    }
    return "未知类型";
}

// 使用模式匹配
public String describe(Object obj) {
    if (obj instanceof String str) {
        return "字符串长度: " + str.length();
    } else if (obj instanceof Integer num) {
        return "整数值: " + num;
    }
    return "未知类型";
}
```

## switch 表达式模式匹配

```java
public String processValue(Object value) {
    return switch (value) {
        case String str -> "字符串: " + str.toUpperCase();
        case Integer num -> "整数: " + (num * 2);
        case Double d -> "双精度: " + String.format("%.2f", d);
        case null -> "空值";
        default -> "未知类型: " + value.getClass().getSimpleName();
    };
}
```

## 解构模式

```java
public record Point(int x, int y) {}
public record Circle(Point center, int radius) {}

public String analyzeShape(Object shape) {
    return switch (shape) {
        case Circle(Point(int x, int y), int radius) when radius > 10 -> 
            String.format("大圆形，中心在 (%d, %d)", x, y);
        case Circle(Point center, int radius) -> 
            String.format("小圆形，半径 %d", radius);
        case Point(0, 0) -> "原点";
        case Point(int x, int y) -> String.format("点 (%d, %d)", x, y);
        default -> "未知形状";
    };
}
```

## 守卫 guard

```java
public String classifyNumber(Integer num) {
    return switch (num) {
        case Integer n when n < 0 -> "负数";
        case Integer n when n == 0 -> "零";
        case Integer n when n > 0 && n <= 10 -> "小正数";
        case Integer n when n > 10 -> "大正数";
        case null -> "空值";
    };
}
```

## 函数式编程风格

```java
public sealed interface Option<T> permits Some, None {
    
    static <T> Option<T> some(T value) {
        return new Some<>(value);
    }
    
    static <T> Option<T> none() {
        return new None<>();
    }
    
    default <U> Option<U> map(Function<T, U> mapper) {
        return switch (this) {
            case Some<T>(T value) -> some(mapper.apply(value));
            case None<T> ignored -> none();
        };
    }
    
    default T orElse(T defaultValue) {
        return switch (this) {
            case Some<T>(T value) -> value;
            case None<T> ignored -> defaultValue;
        };
    }
}

public record Some<T>(T value) implements Option<T> {}
public final class None<T> implements Option<T> {}

// 使用示例
Option<String> name = Option.some("Alice");
String result = name
    .map(String::toUpperCase)
    .map(s -> "Hello, " + s)
    .orElse("Hello, World!");
```

## 结果类型

```java
public sealed interface Result<T, E> permits Success, Failure {
    
    static <T, E> Result<T, E> success(T value) {
        return new Success<>(value);
    }
    
    static <T, E> Result<T, E> failure(E error) {
        return new Failure<>(error);
    }
    
    default <U> Result<U, E> map(Function<T, U> mapper) {
        return switch (this) {
            case Success<T, E>(T value) -> success(mapper.apply(value));
            case Failure<T, E>(E error) -> failure(error);
        };
    }
}

public record Success<T, E>(T value) implements Result<T, E> {}
public record Failure<T, E>(E error) implements Result<T, E> {}

// 使用示例
public Result<Integer, String> divide(int a, int b) {
    if (b == 0) {
        return Result.failure("除零错误");
    }
    return Result.success(a / b);
}

Result<Integer, String> result = divide(10, 2)
    .map(x -> x * 2);

String message = switch (result) {
    case Success<Integer, String>(Integer value) -> "结果: " + value;
    case Failure<Integer, String>(String error) -> "错误: " + error;
};
```

## 状态机

```java
public sealed interface State permits Idle, Running, Paused, Stopped {
}

public record Idle() implements State {}
public record Running(String task) implements State {}
public record Paused(String task) implements State {}
public record Stopped(String reason) implements State {}

public class StateMachine {
    private State currentState = new Idle();
    
    public void transition(String event) {
        currentState = switch (currentState) {
            case Idle() -> switch (event) {
                case "start" -> new Running("默认任务");
                default -> currentState;
            };
            case Running(String task) -> switch (event) {
                case "pause" -> new Paused(task);
                case "stop" -> new Stopped("正常停止");
                default -> currentState;
            };
            case Paused(String task) -> switch (event) {
                case "resume" -> new Running(task);
                case "stop" -> new Stopped("从暂停状态停止");
                default -> currentState;
            };
            case Stopped(String reason) -> currentState; // 终态
        };
    }
}
```
