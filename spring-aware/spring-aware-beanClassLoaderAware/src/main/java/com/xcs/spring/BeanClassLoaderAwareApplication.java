package com.xcs.spring;

import com.xcs.spring.config.MyBeanClassLoaderAware;
import com.xcs.spring.config.MyConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023-09-16 16:09
 **/
public class BeanClassLoaderAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyBeanClassLoaderAware myBeanClassLoaderAware = context.getBean(MyBeanClassLoaderAware.class);
        myBeanClassLoaderAware.loadAndExecute();
    }
}
