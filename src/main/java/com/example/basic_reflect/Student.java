package com.example.basic_reflect;

public class Student {
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
    public String getName() {
        return name;
    }
    public int getAge() {
        return age;
    }
    public double getScore() {
        return score;
    }
    public String getMajor() {
        return major;
    }
}
