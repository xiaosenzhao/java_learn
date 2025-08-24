package com.example.advanced_reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedReflectionReader {
    /**
     * 读取对象所有字段（包括继承的字段）
     */
    public static Map<String, Object> readAllFieldsWithInheritance(Object obj) {
        Map<String, Object> fieldValues = new HashMap<>();
        Class<?> currentClass = obj.getClass();

        // 遍历类继承层次
        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();

            for (Field field : fields) {
                // 跳过静态字段
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);

                    // 使用完全限定名避免字段名冲突
                    String fieldKey = currentClass.getSimpleName() + "." + field.getName();
                    fieldValues.put(fieldKey, value);

                } catch (IllegalAccessException e) {
                    fieldValues.put(field.getName(), "无法访问");
                }
            }

            // 移动到父类
            currentClass = currentClass.getSuperclass();
        }

        return fieldValues;
    }

    /**
     * 读取指定类型的字段
     */
    public static <T> List<T> readFieldsByType(Object obj, Class<T> fieldType) {
        List<T> values = new ArrayList<>();
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.getType() == fieldType || fieldType.isAssignableFrom(field.getType())) {
                try {
                    field.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    T value = (T) field.get(obj);
                    if (value != null) {
                        values.add(value);
                    }
                } catch (IllegalAccessException e) {
                    System.err.println("无法访问字段: " + field.getName());
                }
            }
        }

        return values;
    }

    /**
     * 读取带有特定注解的字段
     */
    public static Map<String, Object> readFieldsWithAnnotation(Object obj,
                                                               Class<? extends java.lang.annotation.Annotation> annotationClass) {
        Map<String, Object> annotatedFields = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(annotationClass)) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    annotatedFields.put(field.getName(), value);
                } catch (IllegalAccessException e) {
                    annotatedFields.put(field.getName(), "无法访问");
                }
            }
        }

        return annotatedFields;
    }

    /**
     * 按修饰符过滤字段
     */
    public static Map<String, Object> readFieldsByModifier(Object obj, int modifierMask) {
        Map<String, Object> filteredFields = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            if ((field.getModifiers() & modifierMask) != 0) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    filteredFields.put(field.getName(), value);
                } catch (IllegalAccessException e) {
                    filteredFields.put(field.getName(), "无法访问");
                }
            }
        }

        return filteredFields;
    }
}
