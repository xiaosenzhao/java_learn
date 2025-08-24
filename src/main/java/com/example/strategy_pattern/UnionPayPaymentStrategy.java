package com.example.strategy_pattern;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * 银联支付策略实现
 */
@Component
@Order(1) // 设置优先级，数字越小优先级越高
public class UnionPayPaymentStrategy implements PaymentStrategy {

    @Override
    public String getPaymentType() {
        return "unionpay";
    }

    @Override
    public PaymentResult processPayment(BigDecimal amount, String orderNo) {
        try {
            // 模拟银联支付处理逻辑
            Thread.sleep(120); // 模拟网络请求

            // 银联支付逻辑
            System.out.println("正在使用银联支付处理支付，订单号：" + orderNo + "，金额：" + amount);

            // 生成交易流水号
            String transactionId = "unionpay_" + UUID.randomUUID().toString().substring(0, 8);

            return PaymentResult.success(transactionId, getPaymentType());

        } catch (Exception e) {
            return PaymentResult.failure("银联支付失败：" + e.getMessage(), getPaymentType());
        }
    }

    @Override
    public boolean isSupported(BigDecimal amount) {
        // 银联支付支持0.01到100000的支付
        return amount.compareTo(BigDecimal.valueOf(0.01)) >= 0 && amount.compareTo(BigDecimal.valueOf(100000)) <= 0;
    }
}