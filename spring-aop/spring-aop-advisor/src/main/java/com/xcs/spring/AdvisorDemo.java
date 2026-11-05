package com.xcs.spring;

import org.springframework.aop.framework.ProxyFactory;

public class AdvisorDemo {

    public static void main(String[] args) {
        // Create the proxy factory
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // Add the Advisor
        proxyFactory.addAdvisor(new MyCustomAdvisor());
        // Get the proxy object
        MyService proxy = (MyService) proxyFactory.getProxy();
        // Call the method
        proxy.foo();
    }
}
