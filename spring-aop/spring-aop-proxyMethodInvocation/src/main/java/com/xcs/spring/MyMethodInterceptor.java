package com.xcs.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class MyMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // Logic that runs before the method call
        System.out.println("Before Method " + invocation.getMethod().getName());
        // Call the original method
        Object result = invocation.proceed();
        // Logic that runs after the method call
        System.out.println("After Method " + invocation.getMethod().getName());
        return result;
    }
}
