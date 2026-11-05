package com.xcs.spring;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;

import java.lang.reflect.Constructor;

public class AopProxyDemo {

    public static void main(String[] args) throws Exception {
//        cglibProxy();
        jdkProxy();
    }

    /**
     * CGLIB proxy
     *
     * @throws Exception
     */
    private static void cglibProxy() throws Exception {
        // Create an AdvisedSupport object to configure the AOP proxy
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // Set the target object
        advisedSupport.setTarget(new MyServiceImpl());
        // Add an interceptor
        advisedSupport.addAdvice(new MyMethodInterceptor());

        // Get the Class object of CglibAopProxy
        Class<?> cglibClass = Class.forName("org.springframework.aop.framework.CglibAopProxy");

        // Get the constructor of CglibAopProxy
        Constructor<?> constructor = cglibClass.getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);

        // Create a CglibAopProxy instance with the constructor
        AopProxy aopProxy = (AopProxy) constructor.newInstance(advisedSupport);

        // Call the getProxy method to create the proxy object
        MyService myService = (MyService) aopProxy.getProxy();

        // Print information about the proxy object
        System.out.println("Cglib Class = " + myService.getClass());
        // Call a method on the proxy object
        myService.foo();
    }

    /**
     * JDK proxy
     *
     * @throws Exception
     */
    private static void jdkProxy() throws Exception {
        // Create an AdvisedSupport object to configure the AOP proxy
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // Set the target object
        advisedSupport.setTarget(new MyServiceImpl());
        // Set the interfaces implemented by the target object
        advisedSupport.setInterfaces(MyService.class);
        // Add an interceptor
        advisedSupport.addAdvice(new MyMethodInterceptor());

        // Get the Class object of JdkDynamicAopProxy
        Class<?> jdkClass = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy");

        // Get the constructor of JdkDynamicAopProxy
        Constructor<?> constructor = jdkClass.getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);

        // Create a JdkDynamicAopProxy instance with the constructor
        AopProxy aopProxy = (AopProxy) constructor.newInstance(advisedSupport);

        // Call the getProxy method to create the proxy object
        MyService myService = (MyService) aopProxy.getProxy();

        // Print information about the proxy object
        System.out.println("JDK Class = " + myService.getClass());
        // Call a method on the proxy object
        myService.foo();
    }
}
