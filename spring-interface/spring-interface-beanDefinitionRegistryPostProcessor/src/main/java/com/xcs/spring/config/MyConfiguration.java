package com.xcs.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xcs
 * @date 2023-08-07 16:25
 **/
@Configuration
public class MyConfiguration {

    @Bean
    public static MyBeanDefinitionRegistryPostProcessor myBeanDefinitionRegistryPostProcessor(){
        return new MyBeanDefinitionRegistryPostProcessor();
    }
}