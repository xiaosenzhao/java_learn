package com.example.strategy_pattern;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * 微信支付策略实现
 */
@Component
public class WechatPaymentStrategy implements PaymentStrategy {

    @Override
    public String getPaymentType() {
        return "wechat";
    }

    @Override
    public PaymentResult processPayment(BigDecimal amount, String orderNo) {
        try {
            // 模拟微信支付处理逻辑
            Thread.sleep(80); // 模拟网络请求

            // 微信支付逻辑
            System.out.println("正在使用微信支付处理支付，订单号：" + orderNo + "，金额：" + amount);

            // 生成交易流水号
            String transactionId = "wechat_" + UUID.randomUUID().toString().substring(0, 8);

            return PaymentResult.success(transactionId, getPaymentType());

        } catch (Exception e) {
            return PaymentResult.failure("微信支付失败：" + e.getMessage(), getPaymentType());
        }
    }

    @Override
    public boolean isSupported(BigDecimal amount) {
        // 微信支付支持0.01到20000的支付
        return amount.compareTo(BigDecimal.valueOf(0.01)) >= 0 && amount.compareTo(BigDecimal.valueOf(20000)) <= 0;
    }
}