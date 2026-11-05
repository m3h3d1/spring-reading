package com.xcs.spring.config;

import com.xcs.spring.bean.MyBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author xcs
 * @date 2023-08-07 16:25
 **/
@Configuration
@Import({MyBean.class, MyImportSelector.class, MyDeferredImportSelector.class, MyImportBeanDefinitionRegistrar.class})
public class MyConfiguration {

}