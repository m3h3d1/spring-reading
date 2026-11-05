package com.xcs.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class EnableTransactionManagementDemo {

    public static void main(String[] args) {
        // Create an annotation-based application context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // Get the ScoresService bean from the context
        ScoresService scoresService = context.getBean(ScoresService.class);
        // Call the ScoresService method
        scoresService.insertScore();
    }
}
