package com.xcs.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ExposeInvocationInterceptorDemo {

    public static void main(String[] args) {
        // Create an annotation-based application context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // Get MyService from the context
        MyService myService = context.getBean(MyService.class);
        // Call the method
        myService.foo();
    }
}
