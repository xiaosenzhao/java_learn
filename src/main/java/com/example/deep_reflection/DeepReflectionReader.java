package com.example.deep_reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import com.example.deep_reflection.Employee;
import com.example.deep_reflection.Address;

public class DeepReflectionReader {

    private static final Set<Class<?>> PRIMITIVE_TYPES;

    static {
        PRIMITIVE_TYPES = new HashSet<>();
        PRIMITIVE_TYPES.add(String.class);
        PRIMITIVE_TYPES.add(Integer.class);
        PRIMITIVE_TYPES.add(Long.class);
        PRIMITIVE_TYPES.add(Double.class);
        PRIMITIVE_TYPES.add(Float.class);
        PRIMITIVE_TYPES.add(Boolean.class);
        PRIMITIVE_TYPES.add(Character.class);
        PRIMITIVE_TYPES.add(Byte.class);
        PRIMITIVE_TYPES.add(Short.class);
        PRIMITIVE_TYPES.add(int.class);
        PRIMITIVE_TYPES.add(long.class);
        PRIMITIVE_TYPES.add(double.class);
        PRIMITIVE_TYPES.add(float.class);
        PRIMITIVE_TYPES.add(boolean.class);
        PRIMITIVE_TYPES.add(char.class);
        PRIMITIVE_TYPES.add(byte.class);
        PRIMITIVE_TYPES.add(short.class);
    }
    /**
     * 深度读取对象（包括嵌套对象）
     */
    public static Map<String, Object> deepRead(Object obj) {
        return deepRead(obj, new HashSet<>(), 0, 3); // 最大深度3层
    }

    private static Map<String, Object> deepRead(Object obj, Set<Object> visited, int currentDepth, int maxDepth) {
        if (obj == null || currentDepth >= maxDepth || visited.contains(obj)) {
            return new HashMap<>();
        }

        visited.add(obj);
        Map<String, Object> result = new HashMap<>();
        Class<?> clazz = obj.getClass();

        // 跳过基本类型和字符串
        if (PRIMITIVE_TYPES.contains(clazz)) {
            result.put("value", obj);
            return result;
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            // 跳过静态字段
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (value == null) {
                    result.put(field.getName(), null);
                } else if (PRIMITIVE_TYPES.contains(value.getClass())) {
                    result.put(field.getName(), value);
                } else if (value instanceof Collection) {
                    result.put(field.getName(), readCollection((Collection<?>) value, visited, currentDepth + 1, maxDepth));
                } else if (value instanceof Map) {
                    result.put(field.getName(), readMap((Map<?, ?>) value, visited, currentDepth + 1, maxDepth));
                } else if (value.getClass().isArray()) {
                    result.put(field.getName(), readArray(value, visited, currentDepth + 1, maxDepth));
                } else {
                    // 递归读取嵌套对象
                    result.put(field.getName(), deepRead(value, visited, currentDepth + 1, maxDepth));
                }

            } catch (IllegalAccessException e) {
                result.put(field.getName(), "无法访问");
            }
        }

        return result;
    }

    private static List<Object> readCollection(Collection<?> collection, Set<Object> visited, int currentDepth, int maxDepth) {
        List<Object> result = new ArrayList<>();

        for (Object item : collection) {
            if (item == null) {
                result.add(null);
            } else if (PRIMITIVE_TYPES.contains(item.getClass())) {
                result.add(item);
            } else {
                result.add(deepRead(item, visited, currentDepth, maxDepth));
            }
        }

        return result;
    }

    private static Map<String, Object> readMap(Map<?, ?> map, Set<Object> visited, int currentDepth, int maxDepth) {
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = String.valueOf(entry.getKey());
            Object value = entry.getValue();

            if (value == null) {
                result.put(key, null);
            } else if (PRIMITIVE_TYPES.contains(value.getClass())) {
                result.put(key, value);
            } else {
                result.put(key, deepRead(value, visited, currentDepth, maxDepth));
            }
        }

        return result;
    }

    private static List<Object> readArray(Object array, Set<Object> visited, int currentDepth, int maxDepth) {
        List<Object> result = new ArrayList<>();
        int length = java.lang.reflect.Array.getLength(array);

        for (int i = 0; i < length; i++) {
            Object item = java.lang.reflect.Array.get(array, i);

            if (item == null) {
                result.add(null);
            } else if (PRIMITIVE_TYPES.contains(item.getClass())) {
                result.add(item);
            } else {
                result.add(deepRead(item, visited, currentDepth, maxDepth));
            }
        }

        return result;
    }

    public static void main(String[] args) {
        Employee employee = new Employee("张三", 1001);
        employee.setHomeAddress(new Address("长安街1号", "北京", "100001"));
        employee.setWorkAddress(new Address("中关村大街2号", "北京", "100080"));
        employee.addSkill("Java");
        employee.addSkill("Python");
        employee.addMetadata("department", "IT");
        employee.addMetadata("level", "Senior");

        Map<String, Object> deepData = deepRead(employee);

        System.out.println("深度读取结果:");
        printMap(deepData, 0);
    }

    private static void printMap(Map<String, Object> map, int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= indent; i++) {
            sb.append("  ");
        }
        String indentStr = sb.toString();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.print(indentStr + entry.getKey() + ": ");

            Object value = entry.getValue();
            if (value instanceof Map) {
                System.out.println();
                printMap((Map<String, Object>) value, indent + 1);
            } else if (value instanceof List) {
                System.out.println(value);
            } else {
                System.out.println(value);
            }
        }
    }
}