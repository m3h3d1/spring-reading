package com.xcs.spring.config;

import com.xcs.spring.event.MyEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xcs
 * @date 2023-09-19 16:35
 **/
@Configuration
public class MyConfiguration {

    @Bean
    public MyApplicationEventPublisherAware myApplicationEventPublisherAware(){
        return new MyApplicationEventPublisherAware();
    }

    @Bean
    public MyEventListener MyEventListener(){
        return new MyEventListener();
    }
}
