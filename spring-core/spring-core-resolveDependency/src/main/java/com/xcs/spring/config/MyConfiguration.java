package com.xcs.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author xcs
 * @date 2023-09-19 16:35
 **/
@Configuration
@ComponentScan("com.xcs.spring")
@PropertySource("classpath:application.properties")
public class MyConfiguration {
}
