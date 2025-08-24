package com.example;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorNumber;

public class AviatorExample {
    public static void main(String[] args) {
        // 基本表达式计算
        System.out.println("1 + 2 = " + AviatorEvaluator.execute("1 + 2"));
        System.out.println("2 * 3 = " + AviatorEvaluator.execute("2 * 3"));
        System.out.println("10 / 2 = " + AviatorEvaluator.execute("10 / 2"));

        // 使用变量
        Map<String, Object> env = new HashMap<>();
        env.put("a", 10);
        env.put("b", 20);
        System.out.println("a + b = " + AviatorEvaluator.execute("a + b", env));

        // 条件表达式
        System.out.println("a > b ? 'a大' : 'b大' = " + AviatorEvaluator.execute("a > b ? 'a大' : 'b大'", env));

        // 字符串操作
        env.put("name", "张三");
        System.out.println("'Hello, ' + name = " + AviatorEvaluator.execute("'Hello, ' + name", env));

        // 正则
        env.put("email", "test@example.com");
        System.out.println("email =~ /^[\\w-]+@[\\w-]+\\.[\\w-]+$/ = " +
            AviatorEvaluator.execute("email =~ /^[\\w-]+@[\\w-]+\\.[\\w-]+$/", env));

        // 编译表达式（提升性能）
        Expression compiledExp = AviatorEvaluator.compile("a * b + c");
        env.put("c", 30);
        System.out.println("a * b + c = " + compiledExp.execute(env));

        // 自定义函数
        AviatorEvaluator.addFunction(new AbstractFunction() {
            @Override
            public String getName() {
                return "maxDouble";
            }
            
            @Override
            public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
                // 从 AviatorJavaType 中获取值
                Number a = (Number) arg1.getValue(env);
                Number b = (Number) arg2.getValue(env);
                return AviatorNumber.valueOf(Math.max(a.doubleValue(), b.doubleValue()));
            }
        });
        
        System.out.println("maxDouble(a, b) = " + AviatorEvaluator.execute("maxDouble(a, b)", env));
    }
}
