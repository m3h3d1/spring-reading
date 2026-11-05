package com.xcs.spring;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;

public class IntroductionInterceptorDemo {

    public static void main(String[] args) {
        // Create the proxy factory and the target object
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // Force the use of CGLIB
        proxyFactory.setProxyTargetClass(true);
        // Create the advice
        proxyFactory.addAdvisor(new DefaultIntroductionAdvisor(new MyMonitoringIntroductionAdvice(), MyMonitoringCapable.class));
        // Create the proxy object
        MyService proxy = (MyService) proxyFactory.getProxy();
        // Call a method on the proxy object
        proxy.foo();
        // Start monitoring
        ((MyMonitoringCapable) proxy).toggleMonitoring();
        // Call a method on the proxy object again
        proxy.foo();
    }
}
