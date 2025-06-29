package com.example.advanced_reflection;

import java.util.Arrays;
import java.util.List;

class NestedTestObject {
    private String name = "nested_object";
    private ComplexTestObject nested = new ComplexTestObject();
    private List<ComplexTestObject> nestedList = Arrays.asList(new ComplexTestObject());
}