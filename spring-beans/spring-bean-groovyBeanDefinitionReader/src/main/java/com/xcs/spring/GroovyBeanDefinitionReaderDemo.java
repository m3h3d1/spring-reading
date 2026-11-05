package com.xcs.spring;

import com.xcs.spring.service.MyService;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author xcs
 * @date 2023-11-07 10:01
 **/
public class GroovyBeanDefinitionReaderDemo {

    public static void main(String[] args) {
        // Create a Spring IoC container
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // Create a GroovyBeanDefinitionReader
        GroovyBeanDefinitionReader reader = new GroovyBeanDefinitionReader(factory);

        // Load the Groovy file and register bean definitions
        reader.loadBeanDefinitions(new ClassPathResource("my-beans.groovy"));

        // Get MyService
        MyService myService = factory.getBean(MyService.class);
        // Print the message
        myService.showMessage();
    }
}
