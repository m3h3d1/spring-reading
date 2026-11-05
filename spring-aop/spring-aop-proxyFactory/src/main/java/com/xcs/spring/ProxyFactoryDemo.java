package com.xcs.spring;

import org.springframework.aop.framework.ProxyFactory;

public class ProxyFactoryDemo {

    public static void main(String[] args) {
        // Create the proxy factory and the target object
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // Get the proxy object
        Object proxy = proxyFactory.getProxy();
        // Call the method of the proxy object
        System.out.println("proxy = " + proxy.getClass());
    }
}
