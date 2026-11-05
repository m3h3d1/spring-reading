package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author xcs
 * @date 2023-10-31 16:17
 **/
public class AnnotationMetadataDemoByReflection {

    public static void main(String[] args) throws Exception {
        // Get the AnnotationMetadata
        AnnotationMetadata annotationMetadata = AnnotationMetadata.introspect(MyBean.class);

        System.out.println("AnnotationMetadata impl class is " + annotationMetadata.getClass());

        // Check if the MyBean class is annotated with @Component
        boolean isComponent = annotationMetadata.hasAnnotation(Component.class.getName());
        System.out.println("MyBean is a @Component: " + isComponent);

        // Get the annotation attributes on the MyBean class
        if (isComponent) {
            Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(Component.class.getName());
            System.out.println("@Component value is " + annotationAttributes.get("value"));
        }
    }
}
