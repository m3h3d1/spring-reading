package com.xcs.spring;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.annotation.BeanFactoryAspectInstanceFactory;
import org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory;
import org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.List;

public class AspectJAdvisorFactoryDemo {

    public static void main(String[] args) {
        // Create a default bean factory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // Register a singleton bean named "myAspect" of type MyAspect in the bean factory
        beanFactory.registerSingleton("myAspect", new MyAspect());

        // Create an aspect instance factory to instantiate the aspect
        MetadataAwareAspectInstanceFactory factory = new BeanFactoryAspectInstanceFactory(beanFactory, "myAspect");
        // Create a ReflectiveAspectJAdvisorFactory instance to create Advisors
        ReflectiveAspectJAdvisorFactory aspectJAdvisorFactory = new ReflectiveAspectJAdvisorFactory(beanFactory);
        // Get the Advisors of all annotated AspectJ methods
        List<Advisor> advisors = aspectJAdvisorFactory.getAdvisors(factory);
        // Print the Advisors
        advisors.forEach(System.out::println);
    }
}
