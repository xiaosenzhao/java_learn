package com.example.transaction;

/**
 * Spring事务管理演示类 运行各种事务场景的示例
 */
public class TransactionDemo {

    public static void main(String[] args) {
        System.out.println("=== Java事务管理概念演示 ===\n");

        // 创建事务示例对象
        TransactionExample transactionExample = new TransactionExample();

        // 运行所有演示
        transactionExample.runAllDemonstrations();

        System.out.println("\n=== 演示完成 ===");
    }
}