package com.example.advanced_reflection;

import java.util.*;

class ComplexTestObject extends BaseEntity {
    // 不同修饰符的字段
    public String publicField = "public_value";
    private String privateField = "private_value";
    protected String protectedField = "protected_value";
    String packageField = "package_value";

    // 静态字段
    public static String staticField = "static_value";
    private static final String CONSTANT = "constant_value";

    // 不同类型的字段
    private int intValue = 42;
    private double doubleValue = 3.14;
    private boolean booleanValue = true;
    private List<String> listValue = Arrays.asList("item1", "item2");
    private Map<String, Object> mapValue = new HashMap<>();

    // 带注解的字段
    @TestAnnotation("test_annotation")
    private String annotatedField = "annotated_value";

    @Sensitive
    private String sensitiveField = "sensitive_data";

    @JsonProperty(name = "custom_name")
    private String jsonField = "json_value";

    @TestAnnotation
    @Sensitive
    private String multiAnnotatedField = "multi_annotated";

    // 空值字段
    private String nullField = null;
    private List<String> nullList = null;

    public void setListValue(List<String> listValue) {
        this.listValue = listValue;
    }

    public ComplexTestObject() {
        super("test_id", new Date(), "test_user");

        // 初始化map
        mapValue.put("key1", "value1");
        mapValue.put("key2", 123);
    }
}