package com.example.advanced_reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 测试用的注解
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface TestAnnotation {
    String value() default "";
}