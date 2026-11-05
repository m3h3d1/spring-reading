package com.xcs.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author xcs
 * @date 2023-08-07 16:25
 **/
@Configuration
@PropertySource("classpath:my-application.yml")
public class MyConfiguration {

}