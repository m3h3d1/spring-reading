package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.controller.MyController;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Iterator;

/**
 * @author xcs
 * @date 2023-11-24 14:44
 **/
public class ConfigurableListableBeanFactoryDemo {

    public static void main(String[] args) throws NoSuchFieldException {
        // Create the ApplicationContext
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyConfiguration.class);

        // Get the ConfigurableListableBeanFactory
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();

        // Ignore dependencies of the given type
        beanFactory.ignoreDependencyType(String.class);

        // Ignore dependencies of the given interface
        beanFactory.ignoreDependencyInterface(BeanFactory.class);

        // Register a resolvable dependency
        beanFactory.registerResolvableDependency(ApplicationContext.class, applicationContext);

        // Check if the bean is a candidate for autowiring
        String beanName = "myService";

        DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(MyController.class.getDeclaredField("myService"), false);
        boolean isAutowireCandidate = beanFactory.isAutowireCandidate(beanName, dependencyDescriptor);
        System.out.println(beanName + " is an autowire candidate: " + isAutowireCandidate);

        // Get the BeanDefinition of the bean
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
        System.out.println(beanName + " BeanDefinition: " + beanDefinition);

        // Get an iterator over all bean names
        Iterator<String> beanNamesIterator = beanFactory.getBeanNamesIterator();
        System.out.print("All bean names: ");
        beanNamesIterator.forEachRemaining(System.out::print);

        // Clear the metadata cache
        beanFactory.clearMetadataCache();

        // Freeze the configuration
        beanFactory.freezeConfiguration();

        // Check if the configuration is frozen
        boolean isConfigurationFrozen = beanFactory.isConfigurationFrozen();
        System.out.println("Configuration frozen: " + isConfigurationFrozen);

        // Pre-instantiate all non-lazy singleton beans
        beanFactory.preInstantiateSingletons();
    }
}
