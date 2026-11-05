package com.xcs.spring.config;

import com.xcs.spring.service.MyService;
import com.xcs.spring.service.impl.MyServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xcs
 * @date 2023-11-24 14:17
 **/
@Configuration
public class MyConfiguration {
    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }
}
