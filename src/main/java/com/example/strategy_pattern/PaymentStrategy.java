package com.example.strategy_pattern;

import java.math.BigDecimal;

/**
 * 支付策略接口 定义所有支付方式的通用行为
 */
public interface PaymentStrategy {

    /**
     * 获取支付类型标识
     * 
     * @return 支付类型（如：alipay, wechat, unionpay等）
     */
    String getPaymentType();

    /**
     * 处理支付
     * 
     * @param amount  支付金额
     * @param orderNo 订单号
     * @return 支付结果
     */
    PaymentResult processPayment(BigDecimal amount, String orderNo);

    /**
     * 检查是否支持该支付方式
     * 
     * @param amount 支付金额
     * @return 是否支持
     */
    boolean isSupported(BigDecimal amount);
}