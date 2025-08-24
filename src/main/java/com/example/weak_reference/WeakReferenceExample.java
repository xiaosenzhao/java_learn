package com.example.weak_reference;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 弱引用使用示例
 */
public class WeakReferenceExample {

    /**
     * 基本弱引用使用示例
     */
    public static void basicWeakReferenceDemo() {
        System.out.println("=== 基本弱引用示例 ===");

        // 创建一个对象
        Person person = new Person("张三", 25);

        // 创建弱引用
        WeakReference<Person> weakRef = new WeakReference<>(person);

        System.out.println("创建弱引用后:");
        System.out.println("强引用: " + person);
        System.out.println("弱引用获取: " + weakRef.get());

        // 清除强引用
        person = null;

        // 手动触发GC
        System.gc();

        // 等待GC完成
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("清除强引用并GC后:");
        System.out.println("弱引用获取: " + weakRef.get());
    }

    /**
     * 使用引用队列监听对象回收
     */
    public static void referenceQueueDemo() {
        System.out.println("\n=== 引用队列示例 ===");

        ReferenceQueue<Person> queue = new ReferenceQueue<>();
        List<WeakReference<Person>> references = new ArrayList<>();

        // 创建多个弱引用
        for (int i = 0; i < 3; i++) {
            Person person = new Person("Person" + i, 20 + i);
            WeakReference<Person> ref = new WeakReference<>(person, queue);
            references.add(ref);
            System.out.println("创建: " + person);
        }

        System.out.println("触发GC...");
        System.gc();

        // 等待GC完成
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 检查引用队列中被回收的对象
        WeakReference<?> ref;
        while ((ref = (WeakReference<?>) queue.poll()) != null) {
            System.out.println("检测到对象被回收: " + ref);
        }
    }

    /**
     * 弱引用缓存示例
     */
    public static void weakCacheDemo() {
        System.out.println("\n=== 弱引用缓存示例 ===");

        WeakCache cache = new WeakCache();

        // 添加数据到缓存
        Person person1 = new Person("缓存对象1", 30);
        Person person2 = new Person("缓存对象2", 35);

        cache.put("key1", person1);
        cache.put("key2", person2);

        System.out.println("缓存大小: " + cache.size());
        System.out.println("获取key1: " + cache.get("key1"));

        // 清除强引用
        person1 = null;
        person2 = null;

        System.gc();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        cache.cleanup();
        System.out.println("GC后缓存大小: " + cache.size());
    }

    /**
     * 观察者模式中的弱引用使用
     */
    public static void observerPatternDemo() {
        System.out.println("\n=== 观察者模式弱引用示例 ===");

        Subject subject = new Subject();

        // 创建观察者
        Observer observer1 = new ConcreteObserver("观察者1");
        Observer observer2 = new ConcreteObserver("观察者2");

        // 注册观察者
        subject.addObserver(observer1);
        subject.addObserver(observer2);

        System.out.println("观察者数量: " + subject.getObserverCount());

        // 通知观察者
        subject.notifyObservers("第一次通知");

        // 清除一个观察者的强引用
        observer1 = null;
        System.gc();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        subject.cleanupObservers();
        System.out.println("GC后观察者数量: " + subject.getObserverCount());

        // 再次通知
        subject.notifyObservers("第二次通知");
    }

    public static void main(String[] args) {
        basicWeakReferenceDemo();
        referenceQueueDemo();
        weakCacheDemo();
        observerPatternDemo();
    }
}

/**
 * 简单的Person类用于演示
 */
class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }

    // java 8 中，finalize 方法已经被标记为过时，不建议使用
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Person对象被回收: " + name);
        super.finalize();
    }
}

/**
 * 使用弱引用的缓存实现
 */
class WeakCache {
    private final Map<String, WeakReference<Person>> cache = new ConcurrentHashMap<>();
    private final ReferenceQueue<Person> queue = new ReferenceQueue<>();

    public void put(String key, Person value) {
        cleanup();
        cache.put(key, new WeakReference<>(value, queue));
    }

    public Person get(String key) {
        cleanup();
        WeakReference<Person> ref = cache.get(key);
        if (ref != null) {
            Person person = ref.get();
            if (person == null) {
                cache.remove(key);
            }
            return person;
        }
        return null;
    }

    public int size() {
        cleanup();
        return cache.size();
    }

    public void cleanup() {
        WeakReference<?> ref;
        while ((ref = (WeakReference<?>) queue.poll()) != null) {
            // 移除已被回收的引用
            cache.values().remove(ref);
        }
    }
}

/**
 * 观察者接口
 */
interface Observer {
    void update(String message);
}

/**
 * 具体观察者实现
 */
class ConcreteObserver implements Observer {
    private final String name;

    public ConcreteObserver(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println(name + " 收到消息: " + message);
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("观察者被回收: " + name);
        super.finalize();
    }
}

/**
 * 使用弱引用的主题类
 */
class Subject {
    private final List<WeakReference<Observer>> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        cleanupObservers();
        observers.add(new WeakReference<>(observer));
    }

    public void notifyObservers(String message) {
        cleanupObservers();
        for (WeakReference<Observer> ref : observers) {
            Observer observer = ref.get();
            if (observer != null) {
                observer.update(message);
            }
        }
    }

    public void cleanupObservers() {
        observers.removeIf(ref -> ref.get() == null);
    }

    public int getObserverCount() {
        cleanupObservers();
        return observers.size();
    }
}