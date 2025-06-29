package com.example.dynamic_proxy.cglib;

public class Demo {
    public static void main(String[] args) {
        SmsService smsService = (SmsService) CglibProxyFactory.getProxy(SmsService.class);
        smsService.send("java");
    }
}
