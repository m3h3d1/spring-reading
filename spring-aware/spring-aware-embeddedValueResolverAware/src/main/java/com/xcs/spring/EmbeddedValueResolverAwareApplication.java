package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.config.MyEmbeddedValueResolverAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023-09-16 16:09
 **/
public class EmbeddedValueResolverAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyEmbeddedValueResolverAware resolverAware = context.getBean(MyEmbeddedValueResolverAware.class);
        resolverAware.resolve();
    }
}
