# ThreadLocal

```java
public class Thread implements Runnable {
	ThreadLocal.ThreadLocalMap threadLocals = null;
	
	ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;
}
```

Thread 中有两个字段，threadLocals 和 inheritableThreadLocals。在创建一个线程对象的时候，会执行一个初始化方法，会将父线程的 inheritableThreadLocals 的对象中包含的值传递到子线程的 inheritableThreadLocals 对象中。
	