package com.xcs.spring;

import org.springframework.aop.aspectj.SimpleAspectInstanceFactory;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.aop.config.SimpleBeanFactoryAwareAspectInstanceFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

public class AspectInstanceFactoryDemo {

    public static void main(String[] args) {
        // Create an aspect instance with SimpleAspectInstanceFactory
        SimpleAspectInstanceFactory sAif = new SimpleAspectInstanceFactory(MyAspect.class);
        System.out.println("SimpleAspectInstanceFactory (1): " + sAif.getAspectInstance());
        System.out.println("SimpleAspectInstanceFactory (2): " + sAif.getAspectInstance());

        // Create a singleton aspect instance with SingletonAspectInstanceFactory
        SingletonAspectInstanceFactory singletonAif = new SingletonAspectInstanceFactory(new MyAspect());
        System.out.println("SingletonAspectInstanceFactory (1): " + singletonAif.getAspectInstance());
        System.out.println("SingletonAspectInstanceFactory (2): " + singletonAif.getAspectInstance());

        // Create a DefaultListableBeanFactory instance to register and manage beans
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // Register a singleton bean named "myAspect" of type MyAspect
        beanFactory.registerSingleton("myAspect", new MyAspect());
        // Create a BeanDefinition for the aspect factory
        RootBeanDefinition aspectFactoryDef = new RootBeanDefinition(SimpleBeanFactoryAwareAspectInstanceFactory.class);
        // Set the property aspectBeanName of the aspect factory to "myAspect"
        aspectFactoryDef.getPropertyValues().add("aspectBeanName", "myAspect");
        // Mark the aspect factory as synthetic, so it is not exposed
        aspectFactoryDef.setSynthetic(true);
        // Register a bean named "simpleBeanFactoryAwareAspectInstanceFactory" with the BeanDefinition of the aspect factory
        beanFactory.registerBeanDefinition("simpleBeanFactoryAwareAspectInstanceFactory", aspectFactoryDef);
        // Get the SimpleBeanFactoryAwareAspectInstanceFactory instance from the BeanFactory
        SimpleBeanFactoryAwareAspectInstanceFactory simpleBeanFactoryAwareAif = beanFactory.getBean(SimpleBeanFactoryAwareAspectInstanceFactory.class);
        System.out.println("SimpleBeanFactoryAwareAspectInstanceFactory (1): " + simpleBeanFactoryAwareAif.getAspectInstance());
        System.out.println("SimpleBeanFactoryAwareAspectInstanceFactory (2): " + simpleBeanFactoryAwareAif.getAspectInstance());
    }
}
