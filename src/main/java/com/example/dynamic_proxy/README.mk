# 代理模式
代理模式的主要作用是扩展目标对象的功能。使用代理对象来代替对真实对象的访问，可以在不修改真实对象的情况下，提供额外的功能操作，扩展目标对象的功能。

# 静态代理
静态代理是指在编译时就已经确定了代理类的实现，代理类和被代理类在编译时就已经确定，代理类和被代理类实现相同的接口。

# 动态代理
动态代理是指在运行时动态生成代理类，代理类和被代理类在运行时确定，代理类和被代理类实现相同的接口。

# 静态代理和动态代理的区别
静态代理在编译时就已经确定了代理类的实现，代理类和被代理类在编译时就已经确定，代理类和被代理类实现相同的接口。
动态代理在运行时动态生成代理类，代理类和被代理类在运行时确定，代理类和被代理类实现相同的接口。

# 动态代理实现
## JDK 动态代理
在 Java 动态代理机制中，InvocationHandler 接口和 Proxy 类是核心。

InvocationHandler 接口是动态代理类必须实现的接口，它定义了代理对象的逻辑，包括方法调用的处理。
```java
public interface InvocationHandler {
	// proxy 代理对象
	// method 被代理对象的方法
	// args 被代理对象的方法参数
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable;
}
```

Proxy 类是动态代理类，它提供了创建动态代理对象的方法。

Proxy 类中使用频率最高的方法：newProxyInstance()
```java
	// loader 类加载器，用于加载代理类
	// interfaces 被代理类实现的接口
	// h 代理类，实现了 InvocationHandler 接口
    public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h)
        throws IllegalArgumentException
    {
        ......
    }
```

## CGLIB 动态代理
CGLIB 是 Code Generation Library 的缩写，它是一个强大的字节码生成库，可以在运行时动态生成代理类。

CGLIB 动态代理的实现原理是通过字节码生成技术，生成一个代理类，代理类继承被代理类，并实现被代理类的接口。

在 CGLIB 动态代理机制中 MethodInterceptor 接口和 Enhancer 类是核心。

```java
public interface MethodInterceptor
extends Callback{
    // 拦截被代理类中的方法
    // obj 被代理对象
    // method 被代理对象的方法
    // args 被代理对象的方法参数
    // proxy 代理对象
    public Object intercept(Object obj, java.lang.reflect.Method method, Object[] args, MethodProxy proxy) throws Throwable;
}
```
通过 Enhancer类来动态获取被代理类，当代理类调用方法的时候，实际调用的是 MethodInterceptor 中的 intercept 方法。

# JDK 动态代理和 CGLIB 动态代理对比
1. JDK 动态代理只能代理实现了接口的类，而 CGLIB 动态代理可以代理没有实现接口的类。
2. JDK 动态代理只能代理接口中的方法，而 CGLIB 动态代理可以代理类中的所有方法。
3. JDK 动态代理生成代理类在代码中，而 CGLIB 动态代理生成代理类在内存中。
4. JDK 动态代理性能更优秀