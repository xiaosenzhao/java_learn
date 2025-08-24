package com.example.builder_pattern.lombok_example;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * 使用Lombok @Builder注解的产品类 Lombok会自动生成Builder类和相关方法
 */
@Data
@Builder
public class Product {
    @NonNull
    private String name;

    @NonNull
    private String category;

    private BigDecimal price;

    private String description;

    private String brand;

    private String color;

    private String size;

    private Integer weight;

    private Boolean isAvailable;

    // @Singular注解用于集合类型，会生成单个元素添加方法
    @Singular
    private List<String> features;

    @Singular("addTag")
    private Set<String> tags;

    private String manufacturer;

    private String model;

    private Integer warrantyMonths;

    // 自定义构造方法可以与@Builder共存
    public static Product createBasic(String name, String category) {
        return Product.builder().name(name).category(category).isAvailable(true).build();
    }
}