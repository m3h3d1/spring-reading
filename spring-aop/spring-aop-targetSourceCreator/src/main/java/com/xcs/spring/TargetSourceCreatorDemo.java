package com.xcs.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TargetSourceCreatorDemo {
    public static void main(String[] args) {
        // Create an annotation-based application context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // Get the MyConnection bean from the context
        MyConnection myConnection = context.getBean(MyConnection.class);
        // Print the class name of the MyConnection instance
        System.out.println("MyConnection Class = " + myConnection.getClass());
        // Call the getName() method of the MyConnection instance in a loop
        for (int i = 0; i < 10; i++) {
            // Print the name of the MyConnection instance
            System.out.println("MyConnection Name = " + myConnection.getName());
        }
    }
}
