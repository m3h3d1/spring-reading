package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.service.ConnectionService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023-09-16 16:09
 **/
public class DestructionAwareBeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        ConnectionService connection = context.getBean("connectionService", ConnectionService.class);
        System.out.println("Is connected: " + connection.isConnected());
        context.close();
    }
}
