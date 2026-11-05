package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023-11-22 09:58
 **/
public class RegisterBeanDefinitionApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // Register the bean
        context.register(MyBean.class);
        // Scan the package
        context.scan("com.xcs.spring");
        // Print the bean definitions
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanDefinitionName = " + beanDefinitionName);
        }
    }
}
