package com.example.basic_reflect;

import java.lang.reflect.*;

public class BasicReflectionExample {
    public static void readAllFields(Object obj) {
        Class<?> clazz = obj.getClass();

        System.out.println("=== 读取对象: " + clazz.getSimpleName() + " ===");

        // 获取所有声明的字段（包括private）
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                // 设置可访问（绕过private限制）
                field.setAccessible(true);

                Object value = field.get(obj);
                String typeName = field.getType().getSimpleName();
                String modifier = java.lang.reflect.Modifier.toString(field.getModifiers());

                System.out.printf("字段: %s, 类型: %s, 修饰符: %s, 值: %s%n",
                        field.getName(), typeName, modifier, value);

            } catch (IllegalAccessException e) {
                System.err.println("无法访问字段: " + field.getName());
            }
        }
    }

    public static void main(String[] args) {
        Student student = new Student("张三", 20, 90.0, "计算机");
        readAllFields(student);
    }
}
