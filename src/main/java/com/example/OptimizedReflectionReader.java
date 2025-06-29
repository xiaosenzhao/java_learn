package com.example;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OptimizedReflectionReader {

    // 缓存字段信息以提高性能
    private static final ConcurrentHashMap<Class<?>, Field[]> FIELD_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Field, Boolean> ACCESSIBILITY_CACHE = new ConcurrentHashMap<>();

    /**
     * 高性能字段读取（使用缓存）
     */
    public static Map<String, Object> readFieldsCached(Object obj) {
        Class<?> clazz = obj.getClass();
        Map<String, Object> result = new HashMap<>();

        // 从缓存获取字段信息
        Field[] fields = FIELD_CACHE.computeIfAbsent(clazz, Class::getDeclaredFields);

        for (Field field : fields) {
            // 跳过静态字段
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            try {
                // 缓存可访问性设置
                ACCESSIBILITY_CACHE.computeIfAbsent(field, f -> {
                    f.setAccessible(true);
                    return true;
                });

                Object value = field.get(obj);
                result.put(field.getName(), value);

            } catch (IllegalAccessException e) {
                result.put(field.getName(), "无法访问");
            }
        }

        return result;
    }

    /**
     * 批量读取多个对象的相同字段
     */
    public static <T> Map<T, Map<String, Object>> batchReadFields(List<T> objects) {
        if (objects.isEmpty()) {
            return new HashMap<T, Map<String, Object>>();
        }

        Map<T, Map<String, Object>> results = new HashMap<>();

        // 假设所有对象都是同一类型
        Class<?> clazz = objects.get(0).getClass();
        Field[] fields = FIELD_CACHE.computeIfAbsent(clazz, Class::getDeclaredFields);

        // 预设置所有字段的可访问性
        for (Field field : fields) {
            ACCESSIBILITY_CACHE.computeIfAbsent(field, f -> {
                f.setAccessible(true);
                return true;
            });
        }

        // 批量读取
        for (T obj : objects) {
            Map<String, Object> fieldValues = new HashMap<>();

            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                try {
                    Object value = field.get(obj);
                    fieldValues.put(field.getName(), value);
                } catch (IllegalAccessException e) {
                    fieldValues.put(field.getName(), "无法访问");
                }
            }

            results.put(obj, fieldValues);
        }

        return results;
    }

    /**
     * 清除缓存（在需要时调用）
     */
    public static void clearCache() {
        FIELD_CACHE.clear();
        ACCESSIBILITY_CACHE.clear();
    }

    /**
     * 获取缓存统计信息
     */
    public static String getCacheStats() {
        return String.format("字段缓存: %d 个类, 可访问性缓存: %d 个字段",
                FIELD_CACHE.size(), ACCESSIBILITY_CACHE.size());
    }
}