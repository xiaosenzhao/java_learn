# 作用
+ 保证内存可见性
+ 保证内存操作有序性（禁止指令重排序）

# 内存可见性

## java 内存模型

java 内存模型（java memory model，简称 JMM），主要是用来屏蔽不同硬件和操作系统的内存访问差异的，因为在不同的硬件和不同的操作系统下，内存的访问是有一定的差异得，这种差异会导致相同的代码在不同的硬件和不同的操作系统下有着不一样的行为，而 Java 内存模型就是解决这个差异，统一相同代码在不同硬件和不同操作系统下的差异的。

Java 内存模型规定：所有的变量（实例变量和静态变量）都必须存储在主内存中，每个线程也会有自己的工作内存，线程的工作内存保存了该线程用到的变量和主内存的副本拷贝，线程对变量的操作都在工作内存中进行。线程不能直接读写主内存中的变量。

内存可见性问题是指，当某个线程修改了主内存中共享变量的值之后，其他线程不能感知到此值被修改了，它会一直使用自己工作内存中的“旧值”，这样程序的执行结果就不符合我们的预期了。

# 有序性

有序性也叫禁止指令重排序。

指令重排序是指编译器或 CPU 为了优化程序的执行性能，而对指令进行重新排序的一种手段。

比较典型的问题场景是单例：

```java
public class Singleton {
    private Singleton() {}
    private static Singleton instance = null;
    public static Singleton getInstance() {
        if (instance == null) { // ①
            synchronized (Singleton.class) {
             if (instance == null) {
                 instance = new Singleton(); // ②
                }
            }
        }
        return instance;
    }
}
```

② 行代码实际执行了3步：

1. 创建内存空间
2. 内存空间初始化对象
3. 内存地址赋值给 instance 对象

**如果此变量不加 volatile，那么线程 1 在执行到上述代码的第 ② 处时就可能会执行指令重排序，将原本是 1、2、3 的执行顺序，重排为 1、3、2。但是特殊情况下，线程 1 在执行完第 3 步之后，如果来了线程 2 执行到上述代码的第 ① 处，判断 instance 对象已经不为 null，但此时线程 1 还未将对象实例化完，那么线程 2 将会得到一个被实例化“一半”的对象，从而导致程序执行出错，这就是为什么要给私有变量添加 volatile 的原因了。**

修正代码：

```java
public class Singleton {
    private Singleton() {}
    // 使用 volatile 禁止指令重排序
    private static volatile Singleton instance = null; // 【主要是此行代码发生了变化】
    public static Singleton getInstance() {
        if (instance == null) { // ①
            synchronized (Singleton.class) {
             if (instance == null) {
                 instance = new Singleton(); // ②
                }
            }
        }
        return instance;
    }
}
```

## volatile 原理

volatile 内存可见性主要通过 lock 前缀指令实现的，它会锁定当前内存区域的缓存（缓存行），并且立即将当前缓存行数据写入主内存（耗时非常短），回写主内存的时候会通过 [MESI 协议](https://zhida.zhihu.com/search?content_id=251863311&content_type=Article&match_order=1&q=MESI+协议&zhida_source=entity)使其他线程缓存了该变量的地址失效，从而导致其他线程需要重新去主内存中重新读取数据到其工作线程中。
