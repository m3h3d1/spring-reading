package com.xcs.spring.config;

import com.xcs.spring.bean.MyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xcs
 * @date 2023-08-07 16:25
 **/
@Configuration
public class MyConfiguration {

    @Bean(initMethod = "init",destroyMethod = "destroy")
    public MyBean myBean(){
        return new MyBean();
    }
}