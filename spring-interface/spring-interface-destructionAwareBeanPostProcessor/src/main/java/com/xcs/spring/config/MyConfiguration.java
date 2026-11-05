package com.xcs.spring.config;

import com.xcs.spring.service.ConnectionService;
import com.xcs.spring.service.ConnectionServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xcs
 * @date 2023-08-07 16:25
 **/
@Configuration
public class MyConfiguration {

    @Bean
    public static MyDestructionAwareBeanPostProcessor myDestructionAwareBeanPostProcessor() {
        return new MyDestructionAwareBeanPostProcessor();
    }

    @Bean
    public ConnectionService connectionService() {
        return new ConnectionServiceImpl();
    }
}