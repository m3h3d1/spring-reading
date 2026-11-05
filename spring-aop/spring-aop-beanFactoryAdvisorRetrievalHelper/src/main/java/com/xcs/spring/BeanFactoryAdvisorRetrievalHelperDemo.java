package com.xcs.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BeanFactoryAdvisorRetrievalHelperDemo {

    public static void main(String[] args) {
        // Create the annotation-based application context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // Get the MyService bean from the application context
        MyService myService = context.getBean(MyService.class);
        // Call the method of MyService
        myService.foo();
    }
}
