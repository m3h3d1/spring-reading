package com.xcs.spring.config;

import com.xcs.spring.bean.MyBeanA;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author xcs
 * @date 2023-08-28 11:12
 **/
public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{MyBeanA.class.getName()};
    }
}
