Java Stream 的 filter 方法是用于过滤集合元素的核心方法。

filter() true 保留 false 删除。

# 基本用法

```java
import java.util.*;
import java.util.stream.Collectors;

public class BasicFilterExample {
    
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 过滤偶数
        List<Integer> evenNumbers = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
        System.out.println("偶数: " + evenNumbers); // [2, 4, 6, 8, 10]
        
        // 过滤大于5的数
        List<Integer> greaterThanFive = numbers.stream()
            .filter(n -> n > 5)
            .collect(Collectors.toList());
        System.out.println("大于5: " + greaterThanFive); // [6, 7, 8, 9, 10]
        
        // 过滤字符串
        List<String> names = Arrays.asList("张三", "李四", "王五", "赵六", "陈七");
        List<String> twoCharNames = names.stream()
            .filter(name -> name.length() == 2)
            .collect(Collectors.toList());
        System.out.println("两个字的名字: " + twoCharNames);
    }
}
```

## 复杂对象过滤

```java
// 学生类
class Student {
    private String name;
    private int age;
    private double score;
    private String major;
    private String city;
    
    public Student(String name, int age, double score, String major, String city) {
        this.name = name;
        this.age = age;
        this.score = score;
        this.major = major;
        this.city = city;
    }
    
    // getter方法
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getScore() { return score; }
    public String getMajor() { return major; }
    public String getCity() { return city; }
    
    @Override
    public String toString() {
        return String.format("Student{name='%s', age=%d, score=%.1f, major='%s', city='%s'}", 
                           name, age, score, major, city);
    }
}
public class ObjectFilterExample {
    
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("张三", 20, 85.5, "计算机", "北京"),
            new Student("李四", 19, 92.0, "数学", "上海"),
            new Student("王五", 21, 78.5, "计算机", "深圳"),
            new Student("赵六", 20, 88.0, "物理", "北京"),
            new Student("陈七", 22, 95.5, "数学", "广州")
        );
        
        // 按年龄过滤
        List<Student> adults = students.stream()
            .filter(s -> s.getAge() >= 21)
            .collect(Collectors.toList());
        System.out.println("年龄>=21的学生:");
        adults.forEach(System.out::println);
        
        // 按分数过滤
        List<Student> highScores = students.stream()
            .filter(s -> s.getScore() > 90)
            .collect(Collectors.toList());
        System.out.println("\n分数>90的学生:");
        highScores.forEach(System.out::println);
        
        // 按专业过滤
        List<Student> csStudents = students.stream()
            .filter(s -> "计算机".equals(s.getMajor()))
            .collect(Collectors.toList());
        System.out.println("\n计算机专业学生:");
        csStudents.forEach(System.out::println);
    }
}
```

# 多条件过滤

```java
public class MultiConditionFilter {
    
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("张三", 20, 85.5, "计算机", "北京"),
            new Student("李四", 19, 92.0, "数学", "上海"),
            new Student("王五", 21, 78.5, "计算机", "深圳"),
            new Student("赵六", 20, 88.0, "物理", "北京"),
            new Student("陈七", 22, 95.5, "数学", "广州")
        );
        
        // AND条件：年龄>=20 且 分数>=85
        List<Student> condition1 = students.stream()
            .filter(s -> s.getAge() >= 20 && s.getScore() >= 85)
            .collect(Collectors.toList());
        System.out.println("年龄>=20且分数>=85:");
        condition1.forEach(System.out::println);
        
        // OR条件：计算机专业 或 分数>90
        List<Student> condition2 = students.stream()
            .filter(s -> "计算机".equals(s.getMajor()) || s.getScore() > 90)
            .collect(Collectors.toList());
        System.out.println("\n计算机专业或分数>90:");
        condition2.forEach(System.out::println);
        
        // 复杂条件：(北京的学生 且 年龄>=20) 或 分数>90
        List<Student> condition3 = students.stream()
            .filter(s -> ("北京".equals(s.getCity()) && s.getAge() >= 20) || s.getScore() > 90)
            .collect(Collectors.toList());
        System.out.println("\n复杂条件过滤:");
        condition3.forEach(System.out::println);
    }
}
```

# 链式过滤

```java
public class ChainedFilter {
    
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("张三", 20, 85.5, "计算机", "北京"),
            new Student("李四", 19, 92.0, "数学", "上海"),
            new Student("王五", 21, 78.5, "计算机", "深圳"),
            new Student("赵六", 20, 88.0, "物理", "北京"),
            new Student("陈七", 22, 95.5, "数学", "广州")
        );
        
        // 链式过滤：先过滤年龄，再过滤分数，最后过滤专业
        List<Student> result = students.stream()
            .filter(s -> s.getAge() >= 20)        // 第一层过滤
            .filter(s -> s.getScore() >= 85)      // 第二层过滤
            .filter(s -> !"物理".equals(s.getMajor())) // 第三层过滤
            .collect(Collectors.toList());
        
        System.out.println("链式过滤结果:");
        result.forEach(System.out::println);
    }
}
```

# 使用 Predicate 动态过滤
## 预定义 Predicate

```java
import java.util.function.Predicate;

public class PredicateFilter {
    
    // 预定义谓词
    public static final Predicate<Student> IS_ADULT = s -> s.getAge() >= 18;
    public static final Predicate<Student> HIGH_SCORE = s -> s.getScore() >= 90;
    public static final Predicate<Student> CS_MAJOR = s -> "计算机".equals(s.getMajor());
    public static final Predicate<Student> BEIJING_STUDENT = s -> "北京".equals(s.getCity());
    
    // 动态创建Predicate
    public static Predicate<Student> ageGreaterThan(int age) {
        return s -> s.getAge() > age;
    }
    
    public static Predicate<Student> scoreGreaterThan(double score) {
        return s -> s.getScore() > score;
    }
    
    public static Predicate<Student> fromCity(String city) {
        return s -> city.equals(s.getCity());
    }
    
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("张三", 20, 85.5, "计算机", "北京"),
            new Student("李四", 19, 92.0, "数学", "上海"),
            new Student("王五", 21, 78.5, "计算机", "深圳"),
            new Student("赵六", 20, 88.0, "物理", "北京"),
            new Student("陈七", 22, 95.5, "数学", "广州")
                    );
        
        // 使用预定义Predicate
        List<Student> highScoreStudents = students.stream()
            .filter(HIGH_SCORE)
            .collect(Collectors.toList());
        System.out.println("高分学生:");
        highScoreStudents.forEach(System.out::println);
        
        // 组合Predicate (AND)
        List<Student> beijingCSStudents = students.stream()
            .filter(BEIJING_STUDENT.and(CS_MAJOR))
            .collect(Collectors.toList());
        System.out.println("\n北京计算机专业学生:");
        beijingCSStudents.forEach(System.out::println);
        
        // 组合Predicate (OR)
        List<Student> highScoreOrCS = students.stream()
            .filter(HIGH_SCORE.or(CS_MAJOR))
            .collect(Collectors.toList());
        System.out.println("\n高分或计算机专业学生:");
        highScoreOrCS.forEach(System.out::println);
        
        // 取反Predicate (NOT)
        List<Student> notFromBeijing = students.stream()
            .filter(BEIJING_STUDENT.negate())
            .collect(Collectors.toList());
        System.out.println("\n非北京学生:");
        notFromBeijing.forEach(System.out::println);
        
        // 使用动态Predicate
        List<Student> customFilter = students.stream()
            .filter(ageGreaterThan(20).and(scoreGreaterThan(80)))
            .collect(Collectors.toList());
        System.out.println("\n年龄>20且分数>80:");
        customFilter.forEach(System.out::println);
    }
}
```

# 字符串过滤

```java
public class StringFilterPatterns {
    
    public static void main(String[] args) {
        List<String> words = Arrays.asList(
            "apple", "banana", "cherry", "date", "elderberry", 
            "fig", "grape", "honeydew", "kiwi", "lemon"
        );
        
        // 按长度过滤
        List<String> longWords = words.stream()
            .filter(word -> word.length() > 5)
            .collect(Collectors.toList());
        System.out.println("长度>5的单词: " + longWords);
        
        // 按前缀过滤
        List<String> startsWithG = words.stream()
            .filter(word -> word.startsWith("g"))
            .collect(Collectors.toList());
        System.out.println("以g开头的单词: " + startsWithG);
        
        // 按后缀过滤
        List<String> endsWithE = words.stream()
            .filter(word -> word.endsWith("e"))
            .collect(Collectors.toList());
        System.out.println("以e结尾的单词: " + endsWithE);
        
        // 包含特定字符
        List<String> containsR = words.stream()
            .filter(word -> word.contains("r"))
            .collect(Collectors.toList());
        System.out.println("包含r的单词: " + containsR);
        
        // 正则表达式过滤
        List<String> hasDoubleLetters = words.stream()
            .filter(word -> word.matches(".*([a-z])\\1.*"))  // 包含连续相同字母
            .collect(Collectors.toList());
        System.out.println("包含连续相同字母的单词: " + hasDoubleLetters);
    }
}
```

# 空值处理

```java
public class NullSafeFilter {
    
    public static void main(String[] args) {
        List<String> names = Arrays.asList("张三", null, "李四", "", "王五", null, "赵六");
        
        // 过滤null值
        List<String> nonNullNames = names.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        System.out.println("非null值: " + nonNullNames);
        
        // 过滤null和空字符串
        List<String> validNames = names.stream()
            .filter(Objects::nonNull)
            .filter(name -> !name.isEmpty())
            .collect(Collectors.toList());
        System.out.println("有效名字: " + validNames);
        
        // 或者使用一个条件
        List<String> validNames2 = names.stream()
            .filter(name -> name != null && !name.isEmpty())
            .collect(Collectors.toList());
        System.out.println("有效名字2: " + validNames2);
        
        // 处理对象中的null字段
        List<Student> studentsWithNull = Arrays.asList(
            new Student("张三", 20, 85.5, "计算机", "北京"),
            new Student("李四", 19, 92.0, null, "上海"),  // major为null
            new Student("王五", 21, 78.5, "计算机", null)   // city为null
        );
        
        // 过滤专业不为null的学生
        List<Student> studentsWithMajor = studentsWithNull.stream()
            .filter(s -> s.getMajor() != null)
            .collect(Collectors.toList());
        System.out.println("\n有专业信息的学生:");
        studentsWithMajor.forEach(System.out::println);
    }
}
```

# 数值范围过滤

```java
public class NumericRangeFilter {
    
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50);
        
        // 范围过滤
        List<Integer> inRange = numbers.stream()
            .filter(n -> n >= 10 && n <= 30)
            .collect(Collectors.toList());
        System.out.println("10-30范围内: " + inRange);
        
        // 使用Predicate的范围过滤
        Predicate<Integer> inRangePredicate = n -> n >= 15 && n <= 35;
        List<Integer> inRange2 = numbers.stream()
            .filter(inRangePredicate)
            .collect(Collectors.toList());
        System.out.println("15-35范围内: " + inRange2);
        
        // 多个范围的OR条件
        List<Integer> multiRange = numbers.stream()
            .filter(n -> (n >= 1 && n <= 10) || (n >= 30 && n <= 40))
            .collect(Collectors.toList());
        System.out.println("1-10或30-40范围内: " + multiRange);
        
        // 学生分数等级过滤
        List<Student> students = Arrays.asList(
            new Student("张三", 20, 85.5, "计算机", "北京"),
            new Student("李四", 19, 92.0, "数学", "上海"),
            new Student("王五", 21, 78.5, "计算机", "深圳"),
            new Student("赵六", 20, 96.0, "物理", "北京")
        );
        
        // 按成绩等级过滤
        List<Student> excellentStudents = students.stream()
            .filter(s -> s.getScore() >= 90)  // 优秀
            .collect(Collectors.toList());
        
        List<Student> goodStudents = students.stream()
            .filter(s -> s.getScore() >= 80 && s.getScore() < 90)  // 良好
            .collect(Collectors.toList());
        
        System.out.println("\n优秀学生(>=90):");
                excellentStudents.forEach(System.out::println);
        
        System.out.println("\n良好学生(80-90):");
        goodStudents.forEach(System.out::println);
    }
}
```

# 集合操作过滤

```java
public class CollectionFilter {
    
    public static void main(String[] args) {
        List<List<Integer>> listOfLists = Arrays.asList(
            Arrays.asList(1, 2, 3),
            Arrays.asList(4, 5),
            Arrays.asList(6, 7, 8, 9),
            Arrays.asList(10)
        );
        
        // 过滤包含特定元素数量的列表
        List<List<Integer>> listsWithMoreThanTwo = listOfLists.stream()
            .filter(list -> list.size() > 2)
            .collect(Collectors.toList());
        System.out.println("元素超过2个的列表: " + listsWithMoreThanTwo);
        
        // 过滤包含特定值的列表
        List<List<Integer>> listsContaining5 = listOfLists.stream()
            .filter(list -> list.contains(5))
            .collect(Collectors.toList());
        System.out.println("包含5的列表: " + listsContaining5);
        
        // 过滤所有元素都满足条件的列表
        List<List<Integer>> allEven = listOfLists.stream()
            .filter(list -> list.stream().allMatch(n -> n % 2 == 0))
            .collect(Collectors.toList());
        System.out.println("所有元素都是偶数的列表: " + allEven);
        
        // 过滤至少有一个元素满足条件的列表
        List<List<Integer>> hasEven = listOfLists.stream()
            .filter(list -> list.stream().anyMatch(n -> n % 2 == 0))
            .collect(Collectors.toList());
        System.out.println("至少有一个偶数的列表: " + hasEven);
    }
}
```

# 自定义过滤工具

```java
public class FilterUtils {
    
    // 通用过滤方法
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        return list.stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    // 多条件AND过滤
    @SafeVarargs
    public static <T> List<T> filterAnd(List<T> list, Predicate<T>... predicates) {
        Predicate<T> combined = Arrays.stream(predicates)
            .reduce(Predicate::and)
            .orElse(x -> true); // 默认值
        
        return list.stream()
            .filter(combined)
            .collect(Collectors.toList());
    }
    
    // 多条件OR过滤
    @SafeVarargs
    public static <T> List<T> filterOr(List<T> list, Predicate<T>... predicates) {
        Predicate<T> combined = Arrays.stream(predicates)
            .reduce(Predicate::or)
            .orElse(x -> false); // orElse 默认值
        
        return list.stream()
            .filter(combined)
            .collect(Collectors.toList());
    }
    
    // 字符串模糊匹配
    public static List<String> fuzzyMatch(List<String> strings, String pattern) {
        String regex = ".*" + pattern.toLowerCase() + ".*";
        return strings.stream()
            .filter(s -> s.toLowerCase().matches(regex))
            .collect(Collectors.toList());
    }
    
    // 去重过滤
    public static <T> List<T> distinctBy(List<T> list, Function<T, ?> keyExtractor) {
        Set<Object> seen = new HashSet<>();
        return list.stream()
            .filter(item -> seen.add(keyExtractor.apply(item)))
                        .collect(Collectors.toList());
    }
    
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("张三", 20, 85.5, "计算机", "北京"),
            new Student("李四", 19, 92.0, "数学", "上海"),
            new Student("王五", 21, 78.5, "计算机", "深圳"),
            new Student("赵六", 20, 88.0, "物理", "北京")
        );
        
        // 使用工具类进行AND过滤
        List<Student> result1 = filterAnd(students,
            s -> s.getAge() >= 20,
            s -> s.getScore() >= 85,
            s -> "北京".equals(s.getCity())
        );
        System.out.println("AND过滤结果:");
        result1.forEach(System.out::println);
        
        // 使用工具类进行OR过滤
        List<Student> result2 = filterOr(students,
            s -> s.getScore() >= 90,
            s -> "计算机".equals(s.getMajor())
        );
        System.out.println("\nOR过滤结果:");
        result2.forEach(System.out::println);
        
        // 按专业去重
        List<Student> distinctByMajor = distinctBy(students, Student::getMajor);
        System.out.println("\n按专业去重:");
        distinctByMajor.forEach(System.out::println);
    }
}
```

# 性能优化建议

```java
public class FilterPerformance {
    
    public static void demonstrateOptimization() {
        // 创建大量测试数据
        List<Integer> largeList = IntStream.range(1, 1000000)
            .boxed()
            .collect(Collectors.toList());
        
        // 优化1：尽早过滤，减少后续操作的数据量
        long start1 = System.currentTimeMillis();
        List<String> result1 = largeList.stream()
            .filter(n -> n % 2 == 0)        // 先过滤
            .filter(n -> n > 100000)        // 再过滤
            .map(n -> "Number: " + n)       // 最后转换
            .collect(Collectors.toList());
        long end1 = System.currentTimeMillis();
        System.out.println("优化后耗时: " + (end1 - start1) + "ms");
        
        // 优化2：使用并行流（适合大数据量和CPU密集型操作）
        long start2 = System.currentTimeMillis();
        List<Integer> result2 = largeList.parallelStream()
            .filter(n -> n % 2 == 0)
            .filter(n -> isPrime(n))  // 假设这是一个CPU密集型操作
            .collect(Collectors.toList());
        long end2 = System.currentTimeMillis();
        System.out.println("并行流耗时: " + (end2 - start2) + "ms");
        
        // 优化3：避免重复计算
        List<String> words = Arrays.asList("apple", "banana", "cherry", "date");
        
        // 不好的做法 - 重复计算
        words.stream()
            .filter(word -> word.toUpperCase().length() > 5)  // toUpperCase()被调用多次
            .filter(word -> word.toUpperCase().startsWith("A"))
            .collect(Collectors.toList());
        
        // 好的做法 - 先计算一次
        words.stream()
            .map(String::toUpperCase)  // 先转换
            .filter(word -> word.length() > 5)  // 再过滤
            .filter(word -> word.startsWith("A"))
            .collect(Collectors.toList());
    }
        private static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
```
