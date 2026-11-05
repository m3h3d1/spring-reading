package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.config.MyResourceLoaderAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023-09-16 16:09
 **/
public class ResourceLoaderAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyResourceLoaderAware resourceLoaderAware = context.getBean(MyResourceLoaderAware.class);
        resourceLoaderAware.getResource("classpath:xcs.txt");
    }
}
