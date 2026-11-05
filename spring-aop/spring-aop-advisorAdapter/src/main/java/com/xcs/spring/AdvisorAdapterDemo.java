package com.xcs.spring;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;

public class AdvisorAdapterDemo {

    public static void main(String[] args) {
        // Register the custom adapter
        GlobalAdvisorAdapterRegistry.getInstance().registerAdvisorAdapter(new NullReturningAdviceAdapter());
        // Create the proxy factory
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // Add the Advisor
        proxyFactory.addAdvice(new MyNullReturningAdvice());
        // Get the proxy object
        MyService proxy = (MyService) proxyFactory.getProxy();
        // Does not trigger the advice
        System.out.println("foo return value : " + proxy.foo());
        // New line
        System.out.println("==================================");
        // Triggers the advice
        System.out.println("bar return value : " + proxy.bar());
    }
}
