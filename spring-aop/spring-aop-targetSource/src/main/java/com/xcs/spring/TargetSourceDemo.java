package com.xcs.spring;

import org.springframework.aop.framework.ProxyFactory;

public class TargetSourceDemo {

    public static void main(String[] args) {
        // Create the proxy factory
        ProxyFactory proxyFactory = new ProxyFactory();
        // Set the target source to the connection pool target source, with a pool size of 3
        proxyFactory.setTargetSource(new ConnectionPoolTargetSource(3));
        // Get the proxy object
        MyConnection proxy = (MyConnection) proxyFactory.getProxy();

        // Call the method 10 times
        for (int i = 0; i < 10; i++) {
            System.out.println("MyConnection Name = " + proxy.getName());
        }
    }
}
