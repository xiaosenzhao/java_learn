package com.example.deep_reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void setHomeAddress(Address address) {
        this.homeAddress = address;
    }
    public void setWorkAddress(Address address) {
        this.workAddress = address;
    }
    public void addSkill(String skill) {
        this.skills.add(skill);
    }
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
}