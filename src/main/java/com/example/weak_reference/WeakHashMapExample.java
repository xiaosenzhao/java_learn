package com.example.weak_reference;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * WeakHashMap 使用示例 演示 WeakHashMap 与 HashMap 的区别
 */
public class WeakHashMapExample {

    /**
     * 比较 HashMap 和 WeakHashMap 的行为
     */
    public static void compareHashMapAndWeakHashMap() {
        System.out.println("=== HashMap vs WeakHashMap 比较 ===");

        // 普通 HashMap
        Map<Key, String> hashMap = new HashMap<>();
        // WeakHashMap (key使用弱引用)
        Map<Key, String> weakHashMap = new WeakHashMap<>();

        // 创建key对象
        Key key1 = new Key("key1");
        Key key2 = new Key("key2");
        Key key3 = new Key("key3");

        // 添加到两个map中
        hashMap.put(key1, "value1");
        hashMap.put(key2, "value2");
        hashMap.put(key3, "value3");

        weakHashMap.put(key1, "value1");
        weakHashMap.put(key2, "value2");
        weakHashMap.put(key3, "value3");

        System.out.println("添加元素后:");
        System.out.println("HashMap size: " + hashMap.size());
        System.out.println("WeakHashMap size: " + weakHashMap.size());

        // 清除部分key的强引用
        key1 = null;
        key2 = null;

        // 手动触发GC
        System.gc();

        // 等待GC完成
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n清除部分key引用并GC后:");
        System.out.println("HashMap size: " + hashMap.size());
        System.out.println("WeakHashMap size: " + weakHashMap.size());

        // 打印剩余的entries
        System.out.println("\nHashMap 剩余内容:");
        hashMap.forEach((k, v) -> System.out.println("  " + k + " = " + v));

        System.out.println("WeakHashMap 剩余内容:");
        weakHashMap.forEach((k, v) -> System.out.println("  " + k + " = " + v));
    }

    /**
     * 演示WeakHashMap作为缓存的使用
     */
    public static void cacheUsageDemo() {
        System.out.println("\n=== WeakHashMap 缓存使用示例 ===");

        ImageCache cache = new ImageCache();

        // 模拟创建一些图片对象
        Image img1 = new Image("photo1.jpg", 1024);
        Image img2 = new Image("photo2.jpg", 2048);
        Image img3 = new Image("photo3.jpg", 512);

        // 缓存图片
        cache.cacheImage("img1", img1);
        cache.cacheImage("img2", img2);
        cache.cacheImage("img3", img3);

        System.out.println("缓存图片后, 缓存大小: " + cache.getCacheSize());

        // 获取缓存的图片
        System.out.println("获取img1: " + cache.getImage("img1"));

        // 清除某些图片的强引用
        img1 = null;
        img2 = null;

        System.gc();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("GC后缓存大小: " + cache.getCacheSize());
        System.out.println("尝试获取img1: " + cache.getImage("img1"));
        System.out.println("获取img3: " + cache.getImage("img3"));
    }

    /**
     * 演示元数据关联的使用场景
     */
    public static void metadataAssociationDemo() {
        System.out.println("\n=== 元数据关联示例 ===");

        MetadataManager manager = new MetadataManager();

        // 创建一些对象
        Object obj1 = new Object();
        Object obj2 = new Object();
        Object obj3 = new Object();

        // 关联元数据
        manager.setMetadata(obj1, "这是对象1的元数据");
        manager.setMetadata(obj2, "这是对象2的元数据");
        manager.setMetadata(obj3, "这是对象3的元数据");

        System.out.println("关联元数据后, 管理器大小: " + manager.size());
        System.out.println("obj1的元数据: " + manager.getMetadata(obj1));

        // 清除对象引用
        obj1 = null;
        obj2 = null;

        System.gc();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("GC后管理器大小: " + manager.size());
        System.out.println("obj3的元数据: " + manager.getMetadata(obj3));
    }

    public static void main(String[] args) {
        compareHashMapAndWeakHashMap();
        cacheUsageDemo();
        metadataAssociationDemo();
    }
}

/**
 * 自定义Key类，重写equals和hashCode
 */
class Key {
    private final String name;

    public Key(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Key key = (Key) obj;
        return name.equals(key.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Key{" + name + "}";
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("Key对象被回收: " + name);
        super.finalize();
    }
}

/**
 * 模拟图片类
 */
class Image {
    private final String filename;
    private final int size;

    public Image(String filename, int size) {
        this.filename = filename;
        this.size = size;
    }

    @Override
    public String toString() {
        return "Image{filename='" + filename + "', size=" + size + "KB}";
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("Image对象被回收: " + filename);
        super.finalize();
    }
}

/**
 * 使用WeakHashMap实现的图片缓存
 */
class ImageCache {
    private final Map<String, Image> cache = new WeakHashMap<>();

    public void cacheImage(String key, Image image) {
        cache.put(key, image);
    }

    public Image getImage(String key) {
        return cache.get(key);
    }

    public int getCacheSize() {
        return cache.size();
    }

    public void clearCache() {
        cache.clear();
    }
}

/**
 * 使用WeakHashMap管理对象元数据
 */
class MetadataManager {
    private final Map<Object, String> metadata = new WeakHashMap<>();

    public void setMetadata(Object obj, String meta) {
        metadata.put(obj, meta);
    }

    public String getMetadata(Object obj) {
        return metadata.get(obj);
    }

    public int size() {
        return metadata.size();
    }

    public void clear() {
        metadata.clear();
    }
}