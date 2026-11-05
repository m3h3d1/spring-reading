package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;

/**
 * @author xcs
 * @date 2023-11-07 16:13
 **/
public class AnnotatedBeanDefinitionReaderDemo {

    public static void main(String[] args) {
        // Create an AnnotationConfigApplicationContext
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // Create an AnnotatedBeanDefinitionReader and link it to the container
        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(factory);

        // Register the bean with the AnnotatedBeanDefinitionReader
        reader.registerBean(MyBean.class);

        // Get and print MyBean
        System.out.println("MyBean = " + factory.getBean(MyBean.class));
    }
}
