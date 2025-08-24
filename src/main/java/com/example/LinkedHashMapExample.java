package com.example;

import java.util.*;

/**
 * LinkedHashMap 使用示例
 * 
 * LinkedHashMap 是 HashMap 的一个子类，它维护了一个额外的双向链表来保持元素的顺序。
 * 
 * 主要特性： 1. 保持插入顺序（默认） 2. 支持访问顺序（LRU缓存实现） 3. 性能稍低于HashMap，但提供了有序性 4. 允许null键和null值
 */
public class LinkedHashMapExample {

    public static void main(String[] args) {
        System.out.println("========== LinkedHashMap 使用示例 ==========\n");

        // 1. 基本使用 - 插入顺序
        demonstrateInsertionOrder();

        // 2. 访问顺序 - LRU缓存
        demonstrateAccessOrder();

        // 3. LRU缓存实现
        demonstrateLRUCache();

        // 4. 与HashMap对比
        compareWithHashMap();

        // 5. 常用操作
        demonstrateCommonOperations();

        // 6. 性能测试
        performanceTest();
    }

    /**
     * 演示插入顺序保持
     */
    private static void demonstrateInsertionOrder() {
        System.out.println("1. 插入顺序保持特性");
        System.out.println("---------------------");

        // 创建LinkedHashMap，默认保持插入顺序
        LinkedHashMap<String, Integer> linkedMap = new LinkedHashMap<>();
        HashMap<String, Integer> hashMap = new HashMap<>();

        // 插入相同的数据
        String[] keys = { "apple", "banana", "cherry", "date", "elderberry" };
        for (int i = 0; i < keys.length; i++) {
            linkedMap.put(keys[i], i + 1);
            hashMap.put(keys[i], i + 1);
        }

        System.out.println("LinkedHashMap (保持插入顺序):");
        linkedMap.forEach((k, v) -> System.out.println("  " + k + " = " + v));

        System.out.println("\nHashMap (顺序不确定):");
        hashMap.forEach((k, v) -> System.out.println("  " + k + " = " + v));

        System.out.println();
    }

    /**
     * 演示访问顺序特性
     */
    private static void demonstrateAccessOrder() {
        System.out.println("2. 访问顺序特性 (accessOrder = true)");
        System.out.println("-----------------------------------");

        // 创建按访问顺序排序的LinkedHashMap
        LinkedHashMap<String, String> accessOrderMap = new LinkedHashMap<>(16, 0.75f, true);

        // 添加元素
        accessOrderMap.put("first", "第一个");
        accessOrderMap.put("second", "第二个");
        accessOrderMap.put("third", "第三个");
        accessOrderMap.put("fourth", "第四个");

        System.out.println("初始顺序:");
        printMap(accessOrderMap);

        // 访问某些元素 - 这会改变它们的位置
        System.out.println("\n访问 'first' 和 'third':");
        accessOrderMap.get("first");
        accessOrderMap.get("third");

        System.out.println("访问后的顺序:");
        printMap(accessOrderMap);

        System.out.println();
    }

    /**
     * 演示LRU缓存实现
     */
    private static void demonstrateLRUCache() {
        System.out.println("3. LRU缓存实现");
        System.out.println("---------------");

        // 创建一个固定大小的LRU缓存
        LRUCache<String, String> lruCache = new LRUCache<>(3);

        System.out.println("添加元素到LRU缓存 (最大容量: 3):");
        lruCache.put("A", "Value A");
        lruCache.put("B", "Value B");
        lruCache.put("C", "Value C");
        System.out.println("当前缓存: " + lruCache);

        System.out.println("\n访问元素A:");
        lruCache.get("A");
        System.out.println("当前缓存: " + lruCache);

        System.out.println("\n添加新元素D (会淘汰最久未使用的B):");
        lruCache.put("D", "Value D");
        System.out.println("当前缓存: " + lruCache);

        System.out.println("\n添加新元素E (会淘汰最久未使用的C):");
        lruCache.put("E", "Value E");
        System.out.println("当前缓存: " + lruCache);

        System.out.println();
    }

    /**
     * 与HashMap对比
     */
    private static void compareWithHashMap() {
        System.out.println("4. LinkedHashMap vs HashMap 对比");
        System.out.println("--------------------------------");

        Map<Integer, String> linkedMap = new LinkedHashMap<>();
        Map<Integer, String> hashMap = new HashMap<>();

        // 添加相同的数据
        Random random = new Random(42); // 使用固定种子保证可重复性
        for (int i = 0; i < 5; i++) {
            int key = random.nextInt(100);
            String value = "Value" + key;
            linkedMap.put(key, value);
            hashMap.put(key, value);
        }

        System.out.println("LinkedHashMap (有序):");
        linkedMap.entrySet().forEach(entry -> System.out.println("  " + entry.getKey() + " = " + entry.getValue()));

        System.out.println("\nHashMap (无序):");
        hashMap.entrySet().forEach(entry -> System.out.println("  " + entry.getKey() + " = " + entry.getValue()));

        System.out.println();
    }

    /**
     * 常用操作演示
     */
    private static void demonstrateCommonOperations() {
        System.out.println("5. 常用操作演示");
        System.out.println("---------------");

        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

        // 添加元素
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);

        System.out.println("原始map: " + map);

        // 更新元素
        map.put("two", 22);
        System.out.println("更新 'two' 后: " + map);

        // 检查是否包含
        System.out.println("包含 'two': " + map.containsKey("two"));
        System.out.println("包含值 3: " + map.containsValue(3));

        // 获取第一个和最后一个元素
        String firstKey = map.keySet().iterator().next();
        String lastKey = null;
        for (String key : map.keySet()) {
            lastKey = key;
        }
        System.out.println("第一个键: " + firstKey + ", 最后一个键: " + lastKey);

        // 移除元素
        map.remove("one");
        System.out.println("移除 'one' 后: " + map);

        System.out.println();
    }

    /**
     * 性能测试
     */
    private static void performanceTest() {
        System.out.println("6. 性能测试 (插入100000个元素)");
        System.out.println("-----------------------------");

        int size = 100000;

        // LinkedHashMap性能测试
        long start = System.currentTimeMillis();
        LinkedHashMap<Integer, String> linkedMap = new LinkedHashMap<>();
        for (int i = 0; i < size; i++) {
            linkedMap.put(i, "Value" + i);
        }
        long linkedMapTime = System.currentTimeMillis() - start;

        // HashMap性能测试
        start = System.currentTimeMillis();
        HashMap<Integer, String> hashMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            hashMap.put(i, "Value" + i);
        }
        long hashMapTime = System.currentTimeMillis() - start;

        System.out.println("LinkedHashMap 插入时间: " + linkedMapTime + "ms");
        System.out.println("HashMap 插入时间: " + hashMapTime + "ms");
        System.out.println("性能差异: " + (linkedMapTime - hashMapTime) + "ms");

        // 内存使用估算
        System.out.println("\n内存使用分析:");
        System.out.println("LinkedHashMap: HashMap + 双向链表节点开销");
        System.out.println("额外内存开销: 每个节点约 16-24 字节");

        System.out.println();
    }

    /**
     * 辅助方法：打印Map
     */
    private static void printMap(Map<String, String> map) {
        map.forEach((k, v) -> System.out.println("  " + k + " = " + v));
    }

    /**
     * LRU缓存实现
     */
    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int maxSize;

        public LRUCache(int maxSize) {
            // 使用访问顺序
            super(16, 0.75f, true);
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            // 当大小超过最大容量时，自动移除最老的元素
            return size() > maxSize;
        }
    }
}