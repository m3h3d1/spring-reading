package com.xcs.spring.config;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xcs
 * @date 2023-09-19 16:35
 **/
@Configuration
public class MyConfiguration {

    @Bean
    public static MergedBeanDefinitionPostProcessor myBeanPostProcessor() {
        return new MyMergedBeanDefinitionPostProcessor();
    }

    @Bean
    public MyBean myBean() {
        return new MyBean();
    }
}
