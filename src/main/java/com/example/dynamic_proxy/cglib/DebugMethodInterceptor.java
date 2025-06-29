package com.example.dynamic_proxy.cglib;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class DebugMethodInterceptor implements MethodInterceptor {
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("cglib before method " + method.getName());
        Object result = proxy.invokeSuper(obj, args);
        System.out.println("cglib after method " + method.getName());
        return result;
    }
}
