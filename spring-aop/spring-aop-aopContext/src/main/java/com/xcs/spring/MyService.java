package com.xcs.spring;

import org.springframework.aop.framework.AopContext;

@MyAnnotation
public class MyService {

    public void foo() {
        System.out.println("foo...");
        // Calling bar directly makes the aspect not work
        // this.bar();
        // Get the proxy object and call bar
        ((MyService) AopContext.currentProxy()).bar();
    }

    public void bar() {
        System.out.println("bar...");
    }
}
