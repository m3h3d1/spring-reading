package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import com.xcs.spring.config.MyConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023-09-16 16:09
 **/
public class MergedBeanDefinitionPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyBean bean = context.getBean(MyBean.class);
        System.out.println("message = " + bean.getMessage());
    }
}
