package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * @author xcs
 * @date 2023-11-04 11:28
 **/
public class BeanDefinitionRegistryDemo {

    public static void main(String[] args) {
        // Create a BeanFactory, which is a BeanDefinitionRegistry
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // Create a bean definition
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(MyBean.class);

        // Register the bean definition in the BeanFactory
        beanFactory.registerBeanDefinition("myBean", beanDefinition);

        // Get the BeanDefinition
        BeanDefinition retrievedBeanDefinition = beanFactory.getBeanDefinition("myBean");
        System.out.println("Bean definition class name = " + retrievedBeanDefinition.getBeanClassName());

        // Check whether the bean definition exists
        boolean containsBeanDefinition = beanFactory.containsBeanDefinition("myBean");
        System.out.println("Contains bean definition (myBean) = " + containsBeanDefinition);

        // Get the names of all bean definitions
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        System.out.println("Bean definition names = " + String.join(", ", beanDefinitionNames));

        // Get the number of bean definitions
        int beanDefinitionCount = beanFactory.getBeanDefinitionCount();
        System.out.println("Bean definition count = " + beanDefinitionCount);

        // Check whether the bean name is in use
        boolean isBeanNameInUse = beanFactory.isBeanNameInUse("myBean");
        System.out.println("Bean name (myBean) in use = " + isBeanNameInUse);

        // Remove the bean definition
        beanFactory.removeBeanDefinition("myBean");
        System.out.println("Bean definition removed (myBean)");
    }
}
