package com.xcs.spring;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

public class AopContextDemo {

    public static void main(String[] args) {
        // Create the proxy factory and the target object
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // Expose the proxy object
        proxyFactory.setExposeProxy(true);
        // Create the advisor
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(new AnnotationMatchingPointcut(MyAnnotation.class), new MyMethodBeforeAdvice()));
        // Get the proxy object
        MyService proxy = (MyService) proxyFactory.getProxy();
        // Call a method on the proxy object
        proxy.foo();
    }
}
