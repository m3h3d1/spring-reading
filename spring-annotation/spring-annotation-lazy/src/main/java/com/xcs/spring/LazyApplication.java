package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.service.MyService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023-08-07 16:21
 **/
public class LazyApplication {

    public static void main(String[] args) {
        System.out.println("Starting Spring ApplicationContext...");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println("Spring ApplicationContext started...");

        System.out.println("Getting MyService...");
        MyService myService = context.getBean(MyService.class);

        System.out.println("Calling show method...");
        myService.show();
    }
}
