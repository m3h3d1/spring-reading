package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023-11-22 14:27
 **/
public class DestroyBeanApplication {

    public static void main(String[] args) {
        // Create an annotation-based application context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // Register the MyBean configuration class so Spring manages the beans it defines
        context.register(MyBean.class);
        // Refresh the application context to initialize and start the Spring container
        context.refresh();
        // Close the application context to destroy the container and release resources
        context.close();
    }
}
