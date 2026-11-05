package com.xcs.spring;

import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class AdvisedDemo {

    public static void main(String[] args) {
        // Create an AdvisedSupport object
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // Set the target object
        advisedSupport.setTarget(new MyServiceImpl());
        // Set the interfaces implemented by the target object
        advisedSupport.setInterfaces(MyService.class);
        // Add advice
        advisedSupport.addAdvice(new Advice() {});
        // Add an advisor
        advisedSupport.addAdvisor(new DefaultPointcutAdvisor());
        // Expose the proxy object
        advisedSupport.setExposeProxy(true);
        // Set CGLIB proxy
        advisedSupport.setProxyTargetClass(true);
        // Freeze the object
        advisedSupport.setFrozen(true);
        // Print
        System.out.println("AdvisedSupport = " + advisedSupport.toProxyConfigString());
    }
}
