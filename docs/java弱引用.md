# Java 弱引用（WeakReference）详解

## 概述

Java中的引用类型从强到弱分为四种：
1. **强引用（Strong Reference）** - 普通引用，默认引用类型
2. **软引用（Soft Reference）** - 内存不足时才会被回收
3. **弱引用（Weak Reference）** - 下次GC时一定会被回收
4. **虚引用（Phantom Reference）** - 无法通过引用获取对象，主要用于跟踪对象回收

## 弱引用特点

### 1. 定义
弱引用是一种比强引用生命周期更短的引用类型。当JVM进行垃圾回收时，无论当前内存空间是否足够，都会回收被弱引用关联的对象。

### 2. 主要特征
- **不阻止垃圾回收**：弱引用不会阻止其指向的对象被垃圾回收器回收
- **自动清理**：当被引用的对象被回收时，弱引用会自动变成null
- **通知机制**：可以配合ReferenceQueue使用，在对象被回收时得到通知

## 弱引用的使用场景

### 1. 缓存实现
```java
// 防止缓存导致内存泄漏
Map<String, WeakReference<ExpensiveObject>> cache = new HashMap<>();
```

### 2. 观察者模式
```java
// 避免观察者阻止被观察对象的回收
List<WeakReference<Observer>> observers = new ArrayList<>();
```

### 3. 父子关系引用
```java
// 子对象持有父对象的弱引用，避免循环引用
private WeakReference<Parent> parentRef;
```

## WeakReference API

### 构造方法
```java
// 创建弱引用
WeakReference<T> ref = new WeakReference<>(object);

// 创建带引用队列的弱引用
WeakReference<T> ref = new WeakReference<>(object, referenceQueue);
```

### 主要方法
```java
T get()              // 获取引用的对象，可能返回null
void clear()         // 清除引用
boolean isEnqueued() // 检查是否已加入引用队列
```

## 与其他引用类型的比较

| 引用类型 | 回收时机 | 使用场景 |
|---------|---------|---------|
| 强引用 | 永不回收（除非显式置null） | 普通对象引用 |
| 软引用 | 内存不足时回收 | 内存敏感的缓存 |
| 弱引用 | 下次GC时回收 | 防止内存泄漏 |
| 虚引用 | 对象回收前通知 | 跟踪对象回收 |

## 注意事项

### 1. 线程安全
WeakReference本身不是线程安全的，在多线程环境下需要额外的同步措施。

### 2. 空指针检查
使用弱引用时必须检查get()方法的返回值是否为null：
```java
WeakReference<MyObject> ref = new WeakReference<>(obj);
MyObject obj = ref.get();
if (obj != null) {
    // 使用对象
}
```

### 3. 引用队列清理
如果使用引用队列，需要定期清理已被回收的引用：
```java
while ((ref = (WeakReference) queue.poll()) != null) {
    // 清理相关资源
}
```

## 最佳实践

1. **及时检查null**：每次调用get()后都要检查返回值
2. **使用引用队列**：配合ReferenceQueue监听对象回收
3. **避免重复引用**：不要让强引用和弱引用同时指向同一对象
4. **合理使用**：只在确实需要弱引用语义时使用

## WeakHashMap

Java提供了WeakHashMap，它的key使用弱引用：
```java
Map<Key, Value> weakMap = new WeakHashMap<>();
```
当key被回收时，对应的entry会自动从map中移除。 