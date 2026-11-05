package com.xcs.spring.config;

import com.xcs.spring.service.AdminService;
import com.xcs.spring.special.SpecialComponent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @author xcs
 * @date 2023-08-07 16:25
 **/
@Configuration
@ComponentScan(
        basePackages = "com.xcs.spring",
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SpecialComponent.class),
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AdminService.class)
)
public class MyConfiguration {

}
