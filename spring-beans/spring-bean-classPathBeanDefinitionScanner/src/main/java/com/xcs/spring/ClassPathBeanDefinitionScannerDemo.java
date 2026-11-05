package com.xcs.spring;

import com.xcs.spring.controller.MyController;
import com.xcs.spring.repository.MyRepository;
import com.xcs.spring.service.MyService;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

/**
 * @author xcs
 * @date 2023-11-07 17:45
 **/
public class ClassPathBeanDefinitionScannerDemo {

    public static void main(String[] args) {
        // Create an AnnotationConfigApplicationContext
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // Create a ClassPathBeanDefinitionScanner and link it to the container
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(factory);

        // Scan beans with the scan method of ClassPathBeanDefinitionScanner
        scanner.scan("com.xcs.spring");

        System.out.println("MyController = " + factory.getBean(MyController.class));
        System.out.println("MyService = " + factory.getBean(MyService.class));
        System.out.println("MyRepository = " + factory.getBean(MyRepository.class));
    }
}
