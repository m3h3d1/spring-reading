package com.xcs.spring;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxyFactory;
import org.springframework.aop.framework.DefaultAopProxyFactory;

public class AopProxyFactoryDemo {

    public static void main(String[] args) {
        // Show the JDK dynamic proxy and the CGLIB proxy one by one
        jdkProxy();
        cglibProxy();
    }

    /**
     * JDK dynamic proxy example
     */
    private static void jdkProxy() {
        // Create an AdvisedSupport object to configure the AOP proxy
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // Set the target object
        advisedSupport.setTarget(new MyServiceImpl());
        // Set the class of the target object
        advisedSupport.setTargetClass(MyService.class);

        // Create a DefaultAopProxyFactory instance
        AopProxyFactory aopProxyFactory = new DefaultAopProxyFactory();
        // Create the JDK dynamic proxy object
        MyService myService = (MyService) aopProxyFactory.createAopProxy(advisedSupport).getProxy();
        // Print the generated proxy class
        System.out.println("jdkProxy = " + myService.getClass());
    }

    /**
     * CGLIB proxy example
     */
    private static void cglibProxy() {
        // Create an AdvisedSupport object to configure the AOP proxy
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // Set the target object
        advisedSupport.setTarget(new MyServiceImpl());
        // Create a DefaultAopProxyFactory instance
        AopProxyFactory aopProxyFactory = new DefaultAopProxyFactory();
        // Create the CGLIB proxy object
        MyService myService = (MyService) aopProxyFactory.createAopProxy(advisedSupport).getProxy();
        // Print the generated proxy class
        System.out.println("cglibProxy = " + myService.getClass());
    }
}

