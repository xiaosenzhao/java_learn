package com.example.strategy_pattern;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * 支付宝支付策略实现
 */
@Component
public class AlipayPaymentStrategy implements PaymentStrategy {

    @Override
    public String getPaymentType() {
        return "alipay";
    }

    @Override
    public PaymentResult processPayment(BigDecimal amount, String orderNo) {
        try {
            // 模拟支付宝支付处理逻辑
            Thread.sleep(100); // 模拟网络请求

            // 支付宝支付逻辑
            System.out.println("正在使用支付宝处理支付，订单号：" + orderNo + "，金额：" + amount);

            // 生成交易流水号
            String transactionId = "alipay_" + UUID.randomUUID().toString().substring(0, 8);

            return PaymentResult.success(transactionId, getPaymentType());

        } catch (Exception e) {
            return PaymentResult.failure("支付宝支付失败：" + e.getMessage(), getPaymentType());
        }
    }

    @Override
    public boolean isSupported(BigDecimal amount) {
        // 支付宝支持0.01到50000的支付
        return amount.compareTo(BigDecimal.valueOf(0.01)) >= 0 && amount.compareTo(BigDecimal.valueOf(50000)) <= 0;
    }
}