package com.xcs.spring;

import org.springframework.aop.framework.ProxyFactory;

public class AfterReturningAdviceDemo {

    public static void main(String[] args) {
        // Create the proxy factory and the target object
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // Create the advice
        proxyFactory.addAdvice(new MyAfterReturningAdvice());
        // Get the proxy object
        MyService proxy = (MyService) proxyFactory.getProxy();
        // Call a method on the proxy object
        proxy.foo();
    }
}
