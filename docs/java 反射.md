# 基本反射读取字段

```java
import java.lang.reflect.Field;

// 测试用的学生类
class Student {
    private String name;
    public int age;
    protected double score;
    String major; // package-private
    
    public Student(String name, int age, double score, String major) {
        this.name = name;
        this.age = age;
        this.score = score;
        this.major = major;
    }
    
    // getter方法
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getScore() { return score; }
    public String getMajor() { return major; }
}

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
        Student student = new Student("张三", 20, 85.5, "计算机科学");
        readAllFields(student);
    }
}
```

# 高级反射读取工具

```java
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

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
```

# 深度反射读取

```java
import java.lang.reflect.Field;
import java.util.*;

// 地址类
class Address {
    private String street;
    private String city;
    private String zipCode;
    
    public Address(String street, String city, String zipCode) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
    }
}

// 复杂的员工类
class Employee {
    private String name;
    private int employeeId;
    private Address homeAddress;
    private Address workAddress;
    private List<String> skills;
    private Map<String, Object> metadata;
    
    public Employee(String name, int employeeId) {
        this.name = name;
        this.employeeId = employeeId;
        this.skills = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    public void setHomeAddress(Address address) { this.homeAddress = address; }
    public void setWorkAddress(Address address) { this.workAddress = address; }
    public void addSkill(String skill) { this.skills.add(skill); }
    public void addMetadata(String key, Object value) { this.metadata.put(key, value); }
}
public class DeepReflectionReader {
    
    private static final Set<Class<?>> PRIMITIVE_TYPES = Set.of(
        String.class, Integer.class, Long.class, Double.class, Float.class,
        Boolean.class, Character.class, Byte.class, Short.class,
        int.class, long.class, double.class, float.class, 
        boolean.class, char.class, byte.class, short.class
    );
    
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
        String indentStr = "  ".repeat(indent);
        
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

```



