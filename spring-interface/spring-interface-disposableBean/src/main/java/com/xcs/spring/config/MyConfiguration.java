package com.xcs.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xcs
 * @date 2023-09-19 16:35
 **/
@Configuration
public class MyConfiguration {

    @Bean
    public static MyDisposableBean myDisposableBean(){
        return new MyDisposableBean();
    }
}
