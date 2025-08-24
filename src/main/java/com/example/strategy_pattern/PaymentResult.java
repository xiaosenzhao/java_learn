package com.example.strategy_pattern;

/**
 * 支付结果类
 */
public class PaymentResult {
    private boolean success;
    private String message;
    private String transactionId;
    private String paymentType;

    public PaymentResult(boolean success, String message, String transactionId, String paymentType) {
        this.success = success;
        this.message = message;
        this.transactionId = transactionId;
        this.paymentType = paymentType;
    }

    // 快速创建成功结果
    public static PaymentResult success(String transactionId, String paymentType) {
        return new PaymentResult(true, "支付成功", transactionId, paymentType);
    }

    // 快速创建失败结果
    public static PaymentResult failure(String message, String paymentType) {
        return new PaymentResult(false, message, null, paymentType);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public String toString() {
        return "PaymentResult{" + "success=" + success + ", message='" + message + '\'' + ", transactionId='"
                + transactionId + '\'' + ", paymentType='" + paymentType + '\'' + '}';
    }
}