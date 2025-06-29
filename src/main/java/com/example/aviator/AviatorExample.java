package com.example.aviator;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.exception.ExpressionRuntimeException;

import java.util.HashMap;
import java.util.Map;

public class AviatorExample {
    public static void main(String[] args) {
        // 1. 基本表达式计算
        System.out.println("1. 基本表达式计算:");
        System.out.println("1 + 2 = " + AviatorEvaluator.execute("1 + 2"));
        System.out.println("2 * 3 = " + AviatorEvaluator.execute("2 * 3"));
        System.out.println("10 / 2 = " + AviatorEvaluator.execute("10 / 2"));
        
        // 2. 使用变量
        System.out.println("\n2. 使用变量:");
        Map<String, Object> env = new HashMap<>();
        env.put("a", 10);
        env.put("b", 20);
        System.out.println("a + b = " + AviatorEvaluator.execute("a + b", env));
        
        // 3. 条件表达式
        System.out.println("\n3. 条件表达式:");
        System.out.println("a > b ? 'a大' : 'b大' = " + AviatorEvaluator.execute("a > b ? 'a大' : 'b大'", env));
        
        // 4. 字符串操作
        System.out.println("\n4. 字符串操作:");
        env.put("name", "张三");
        System.out.println("'Hello, ' + name = " + AviatorEvaluator.execute("'Hello, ' + name", env));
        
        // 5. 正则表达式
        System.out.println("\n5. 正则表达式:");
        env.put("email", "test@example.com");
        System.out.println("email =~ /^[\\w-]+@[\\w-]+\\.[\\w-]+$/ = " + 
            AviatorEvaluator.execute("email =~ /^[\\w-]+@[\\w-]+\\.[\\w-]+$/", env));
        
        // 6. 三元运算符
        System.out.println("\n6. 三元运算符:");
        env.put("score", 85);
        System.out.println("score >= 60 ? '及格' : '不及格' = " + 
            AviatorEvaluator.execute("score >= 60 ? '及格' : '不及格'", env));
        
        // 7. 编译表达式（提高性能）
        System.out.println("\n7. 编译表达式:");
        Expression compiledExp = AviatorEvaluator.compile("a * b + c");
        env.put("c", 30);
        System.out.println("a * b + c = " + compiledExp.execute(env));
        
        // 8. 异常处理
        System.out.println("\n8. 异常处理:");
        try {
            AviatorEvaluator.execute("1/0");
        } catch (ExpressionRuntimeException e) {
            System.out.println("捕获到异常: " + e.getMessage());
        }
        
        // 9. 自定义函数
        System.out.println("\n9. 自定义函数:");
        AviatorEvaluator.addFunction("max", new com.googlecode.aviator.runtime.function.AbstractFunction() {
            @Override
            public String getName() {
                return "max";
            }
            
            @Override
            public Object call(Map<String, Object> env, Object... args) {
                if (args.length != 2) {
                    throw new IllegalArgumentException("max函数需要两个参数");
                }
                Number a = (Number) args[0];
                Number b = (Number) args[1];
                return Math.max(a.doubleValue(), b.doubleValue());
            }
        });
        
        System.out.println("max(a, b) = " + AviatorEvaluator.execute("max(a, b)", env));
    }
} 