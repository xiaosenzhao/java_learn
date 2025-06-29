package com.example.advanced_reflection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Modifier;
import java.util.*;

public class AdvancedReflectionReaderTest {
    private ComplexTestObject testObject;
    private NestedTestObject nestedObject;

    @BeforeEach
    public void setUp() {
        testObject = new ComplexTestObject();
        nestedObject = new NestedTestObject();
    }

    @Test
    @DisplayName("测试读取所有字段包括继承字段")
    public void testReadAllFieldsWithInheritance(AdvancedReflectionReaderTest advancedReflectionReaderTest) {
        System.out.println("=== 测试继承字段读取 ===");

        Map<String, Object> fields = AdvancedReflectionReader.readAllFieldsWithInheritance(advancedReflectionReaderTest.testObject);

        // 验证基类字段
        assertTrue(fields.containsKey("BaseEntity.id"));
        assertTrue(fields.containsKey("BaseEntity.createdTime"));
        assertTrue(fields.containsKey("BaseEntity.createdBy"));

        // 验证子类字段
        assertTrue(fields.containsKey("ComplexTestObject.publicField"));
        assertTrue(fields.containsKey("ComplexTestObject.privateField"));
        assertTrue(fields.containsKey("ComplexTestObject.protectedField"));

        // 验证字段值
        assertEquals("test_id", fields.get("BaseEntity.id"));
        assertEquals("public_value", fields.get("ComplexTestObject.publicField"));
        assertEquals("private_value", fields.get("ComplexTestObject.privateField"));

        // 验证不包含静态字段
        assertFalse(fields.containsKey("ComplexTestObject.staticField"));
        assertFalse(fields.containsKey("ComplexTestObject.CONSTANT"));

        // 打印结果
        System.out.println("继承字段读取结果:");
        fields.forEach((key, value) ->
                System.out.println("  " + key + " = " + value));

        System.out.println("总共读取字段数: " + fields.size());
    }

    @Test
    @DisplayName("测试按类型过滤字段")
    public void testReadFieldsByType() {
        System.out.println("\n=== 测试按类型过滤字段 ===");

        // 测试String类型字段
        List<String> stringFields = AdvancedReflectionReader.readFieldsByType(testObject, String.class);
        System.out.println("String类型字段:");
        stringFields.forEach(field -> System.out.println("  " + field));

        // 验证包含预期的字符串字段
        assertTrue(stringFields.contains("public_value"));
        assertTrue(stringFields.contains("private_value"));
        assertTrue(stringFields.contains("annotated_value"));

        // 测试List类型字段
        List<List> listFields = AdvancedReflectionReader.readFieldsByType(testObject, List.class);
        System.out.println("\nList类型字段:");
        listFields.forEach(field -> System.out.println("  " + field));

        // 验证List字段
        assertEquals(1, listFields.size()); // 应该只有一个非null的List
        assertEquals(Arrays.asList("item1", "item2"), listFields.get(0));

        // 测试基本类型
        List<Integer> intFields = AdvancedReflectionReader.readFieldsByType(testObject, int.class);
        System.out.println("\nint类型字段:");
        intFields.forEach(field -> System.out.println("  " + field));
        assertEquals(1, intFields.size());
        assertEquals(Integer.valueOf(42), intFields.get(0));

        // 测试不存在的类型
        List<StringBuilder> sbFields = AdvancedReflectionReader.readFieldsByType(testObject, StringBuilder.class);
        assertTrue(sbFields.isEmpty());
    }

    @Test
    @DisplayName("测试读取带特定注解的字段")
    public void testReadFieldsWithAnnotation() {
        System.out.println("\n=== 测试注解字段读取 ===");

        // 测试@TestAnnotation注解
        Map<String, Object> testAnnotatedFields =
                AdvancedReflectionReader.readFieldsWithAnnotation(testObject, TestAnnotation.class);

        System.out.println("@TestAnnotation注解的字段:");
        testAnnotatedFields.forEach((key, value) ->
                System.out.println("  " + key + " = " + value));

        // 验证注解字段
        assertTrue(testAnnotatedFields.containsKey("annotatedField"));
        assertTrue(testAnnotatedFields.containsKey("multiAnnotatedField"));
        assertEquals("annotated_value", testAnnotatedFields.get("annotatedField"));
        assertEquals("multi_annotated", testAnnotatedFields.get("multiAnnotatedField"));

        // 测试@Sensitive注解
        Map<String, Object> sensitiveFields =
                AdvancedReflectionReader.readFieldsWithAnnotation(testObject, Sensitive.class);

        System.out.println("\n@Sensitive注解的字段:");
        sensitiveFields.forEach((key, value) ->
                System.out.println("  " + key + " = " + value));

        assertTrue(sensitiveFields.containsKey("sensitiveField"));
        assertTrue(sensitiveFields.containsKey("multiAnnotatedField"));

        // 测试@JsonProperty注解
        Map<String, Object> jsonFields =
                AdvancedReflectionReader.readFieldsWithAnnotation(testObject, JsonProperty.class);

        System.out.println("\n@JsonProperty注解的字段:");
        jsonFields.forEach((key, value) ->
                System.out.println("  " + key + " = " + value));

        assertTrue(jsonFields.containsKey("jsonField"));
        assertEquals("json_value", jsonFields.get("jsonField"));

        // 测试不存在的注解
        Map<String, Object> nonExistentAnnotation =
                AdvancedReflectionReader.readFieldsWithAnnotation(testObject, Deprecated.class);
        assertTrue(nonExistentAnnotation.isEmpty());
    }

    @Test
    @DisplayName("测试按修饰符过滤字段")
    public void testReadFieldsByModifier() {
        System.out.println("\n=== 测试修饰符过滤 ===");

        // 测试public字段
        Map<String, Object> publicFields =
                AdvancedReflectionReader.readFieldsByModifier(testObject, Modifier.PUBLIC);

        System.out.println("Public字段:");
        publicFields.forEach((key, value) ->
                System.out.println("  " + key + " = " + value));

        assertTrue(publicFields.containsKey("publicField"));
        assertEquals("public_value", publicFields.get("publicField"));

        // 测试private字段
        Map<String, Object> privateFields =
                AdvancedReflectionReader.readFieldsByModifier(testObject, Modifier.PRIVATE);

        System.out.println("\nPrivate字段:");
        privateFields.forEach((key, value) ->
                System.out.println("  " + key + " = " + value));

        assertTrue(privateFields.containsKey("privateField"));
        assertTrue(privateFields.containsKey("intValue"));
        assertTrue(privateFields.containsKey("annotatedField"));

        // 测试protected字段
        Map<String, Object> protectedFields =
                AdvancedReflectionReader.readFieldsByModifier(testObject, Modifier.PROTECTED);

        System.out.println("\nProtected字段:");
        protectedFields.forEach((key, value) ->
                System.out.println("  " + key + " = " + value));

        assertTrue(protectedFields.containsKey("protectedField"));

        // 测试static字段
        Map<String, Object> staticFields =
                AdvancedReflectionReader.readFieldsByModifier(testObject, Modifier.STATIC);

        System.out.println("\nStatic字段:");
        staticFields.forEach((key, value) ->
                System.out.println("  " + key + " = " + value));
        // 注意：静态字段的值是类级别的，不是实例级别的
        assertTrue(staticFields.containsKey("staticField"));
        assertTrue(staticFields.containsKey("CONSTANT"));
    }

    @Test
    @DisplayName("测试空值和特殊情况处理")
    public void testNullAndSpecialCases() {
        System.out.println("\n=== 测试特殊情况 ===");

        // 测试null对象
        assertThrows(Exception.class, () -> {
            AdvancedReflectionReader.readAllFieldsWithInheritance(null);
        });

        // 测试包含null字段的对象
        Map<String, Object> fields = AdvancedReflectionReader.readAllFieldsWithInheritance(testObject);

        System.out.println("包含null值的字段:");
        fields.entrySet().stream()
                .filter(entry -> entry.getValue() == null)
                .forEach(entry -> System.out.println("  " + entry.getKey() + " = null"));

        assertTrue(fields.containsKey("ComplexTestObject.nullField"));
        assertNull(fields.get("ComplexTestObject.nullField"));

        // 测试空集合
        ComplexTestObject emptyObject = new ComplexTestObject();
        // 空但不为null
        emptyObject.setListValue(new ArrayList<>());

        List<List> lists = AdvancedReflectionReader.readFieldsByType(emptyObject, List.class);
        assertEquals(1, lists.size());
        assertTrue(lists.get(0).isEmpty());
    }

    @Test
    @DisplayName("测试复杂数据类型")
    void testComplexDataTypes() {
        System.out.println("\n=== 测试复杂数据类型 ===");

        // 测试Map类型
        List<Map> mapFields = AdvancedReflectionReader.readFieldsByType(testObject, Map.class);
        System.out.println("Map类型字段:");
        mapFields.forEach(map -> {
            System.out.println("  Map内容: " + map);
            map.forEach((key, value) ->
                    System.out.println("    " + key + " -> " + value));
        });

        assertEquals(1, mapFields.size());
        Map<String, Object> map = mapFields.get(0);
        assertEquals("value1", map.get("key1"));
        assertEquals(123, map.get("key2"));

        // 测试Date类型
        List<Date> dateFields = AdvancedReflectionReader.readFieldsByType(testObject, Date.class);
        System.out.println("\nDate类型字段:");
        dateFields.forEach(date -> System.out.println("  " + date));

        assertEquals(0, dateFields.size());
    }


    @Test
    @DisplayName("测试嵌套对象")
    public void testNestedObjects() {
        System.out.println("\n=== 测试嵌套对象 ===");

        Map<String, Object> nestedFields =
                AdvancedReflectionReader.readAllFieldsWithInheritance(nestedObject);

        System.out.println("嵌套对象字段:");
        nestedFields.forEach((key, value) -> {
            if (value != null) {
                System.out.println("  " + key + " = " + value + " (" + value.getClass().getSimpleName() + ")");
            } else {
                System.out.println("  " + key + " = null");
            }
        });

        // 验证嵌套对象字段
        assertTrue(nestedFields.containsKey("NestedTestObject.name"));
        assertTrue(nestedFields.containsKey("NestedTestObject.nested"));
        assertTrue(nestedFields.containsKey("NestedTestObject.nestedList"));

        assertEquals("nested_object", nestedFields.get("NestedTestObject.name"));
        assertNotNull(nestedFields.get("NestedTestObject.nested"));
        assertNotNull(nestedFields.get("NestedTestObject.nestedList"));
    }

    @Test
    @DisplayName("性能测试")
    public void testPerformance() {
        System.out.println("\n=== 性能测试 ===");

        int iterations = 1000;
        List<ComplexTestObject> testObjects = new ArrayList<>();

        // 创建测试数据
        for (int i = 0; i < 100; i++) {
            testObjects.add(new ComplexTestObject());
        }

        // 测试readAllFieldsWithInheritance性能
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            for (ComplexTestObject obj : testObjects) {
                AdvancedReflectionReader.readAllFieldsWithInheritance(obj);
            }
        }
        long time1 = System.currentTimeMillis() - start1;

        // 测试readFieldsByType性能
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            for (ComplexTestObject obj : testObjects) {
                AdvancedReflectionReader.readFieldsByType(obj, String.class);
            }
        }
        long time2 = System.currentTimeMillis() - start2;

        // 测试readFieldsWithAnnotation性能
        long start3 = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            for (ComplexTestObject obj : testObjects) {
                AdvancedReflectionReader.readFieldsWithAnnotation(obj, TestAnnotation.class);
            }
        }
        long time3 = System.currentTimeMillis() - start3;

        System.out.println("性能测试结果 (" + iterations + " 次迭代, " + testObjects.size() + " 个对象):");
        System.out.println("  readAllFieldsWithInheritance: " + time1 + "ms");
        System.out.println("  readFieldsByType: " + time2 + "ms");
        System.out.println("  readFieldsWithAnnotation: " + time3 + "ms");

        // 性能验证（根据实际情况调整）
        assertTrue(time1 < 5000, "readAllFieldsWithInheritance应该在5秒内完成");
        assertTrue(time2 < 3000, "readFieldsByType应该在3秒内完成");
        assertTrue(time3 < 3000, "readFieldsWithAnnotation应该在3秒内完成");
    }

    @Test
    @DisplayName("边界条件测试")
    public void testBoundaryConditions() {
        System.out.println("\n=== 边界条件测试 ===");

        // 测试空类
        class EmptyClass {}
        EmptyClass emptyObj = new EmptyClass();

        Map<String, Object> emptyFields =
                AdvancedReflectionReader.readAllFieldsWithInheritance(emptyObj);
        System.out.println("空类字段数: " + emptyFields.size());
        for (String key : emptyFields.keySet()) {
            System.out.println("  " + key + " = " + emptyFields.get(key));
        }
        assertFalse(emptyFields.isEmpty());

        // 嵌套类中定义静态数据成员，需要jdk升级到16+
        // // 测试只有静态字段的类
        // class StaticOnlyClass {
        //     public static String STATIC_FIELD = "static_value";
        // }
        // StaticOnlyClass staticObj = new StaticOnlyClass();

        // Map<String, Object> staticOnlyFields =
        //         AdvancedReflectionReader.readAllFieldsWithInheritance(staticObj);
        // System.out.println("只有静态字段的类，实例字段数: " + staticOnlyFields.size());
        // assertTrue(staticOnlyFields.isEmpty());

        // 测试原始类型包装类
        Integer integerObj = 42;
        Map<String, Object> integerFields =
                AdvancedReflectionReader.readAllFieldsWithInheritance(integerObj);
        System.out.println("Integer对象字段数: " + integerFields.size());

        // 测试数组
        String[] arrayObj = {"test1", "test2"};
        Map<String, Object> arrayFields =
                AdvancedReflectionReader.readAllFieldsWithInheritance(arrayObj);
        System.out.println("数组对象字段数: " + arrayFields.size());
    }

}
