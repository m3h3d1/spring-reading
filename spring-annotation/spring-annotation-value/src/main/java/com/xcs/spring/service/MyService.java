package com.xcs.spring.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * @author xcs
 * @date 2023-10-13 14:55
 **/

public class MyService implements InitializingBean {

    /**
     * Inject a value directly
     */
    @Value("Some String Value")
    private String someString;

    /**
     * Inject a value from the properties file
     */
    @Value("${app.name}")
    private String appName;

    /**
     * Use a default value
     */
    @Value("${app.description:I am the default value}")
    private String appDescription;

    /**
     * Inject a list
     */
    @Value("#{'${app.servers}'.split(',')}")
    private List<String> servers;

    /**
     * Use Spring SpEL
     */
    @Value("#{${app.val1} + ${app.val2}}")
    private int sumOfValues;

    @Value("${myapp.names[0]}")
    private String firstName;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Direct value: " + someString);
        System.out.println("Value from properties file: " + appName);
        System.out.println("Default value: " + appDescription);
        System.out.println("List and property injection: " + servers);
        System.out.println("Spring SpEL: " + sumOfValues);
        System.out.println("firstName: " + firstName);
    }
}
