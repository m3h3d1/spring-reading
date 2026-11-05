package com.xcs.spring.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MyApplicationContextAware implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        System.out.println("Implements ApplicationContextAware, so setApplicationContext is called automatically");
        System.out.println("ApplicationContext = " + context);
    }
}
