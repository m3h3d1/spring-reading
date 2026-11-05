package com.xcs.spring.config;

import com.xcs.spring.bean.MyBeanB;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author xcs
 * @date 2023-08-29 11:08
 **/
public class MyDeferredImportSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{MyBeanB.class.getName()};
    }
}
