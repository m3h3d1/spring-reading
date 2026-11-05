package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.Arrays;

/**
 * @author xcs
 * @date 2023-11-03 17:20
 **/
public class BeanDefinitionHolderDemo {

    public static void main(String[] args) {
        // Create a DefaultListableBeanFactory, an implementation of BeanDefinitionRegistry
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // Create a new BeanDefinition
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(MyBean.class);

        // Bean name
        String beanName = "myBean";
        // Set aliases
        String[] aliases = {"myBeanX", "myBeanY"};
        // Create a BeanDefinitionHolder to link the BeanDefinition with its name
        BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName, aliases);

        // Register the BeanDefinitionHolder with BeanDefinitionReaderUtils
        BeanDefinitionReaderUtils.registerBeanDefinition(beanDefinitionHolder, beanFactory);

        System.out.println("myBean = " + beanFactory.getBean("myBean"));
        System.out.println("myBeanX = " + beanFactory.getBean("myBeanX"));
        System.out.println("myBeanY = " + beanFactory.getBean("myBeanY"));
    }
}
