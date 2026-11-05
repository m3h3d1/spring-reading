package com.xcs.spring;

import com.xcs.spring.condition.MyOnClassCondition;
import org.springframework.context.annotation.Condition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.io.IOException;

public class ConditionDemo {

    public static void main(String[] args) throws IOException {
        // Create a resource resolver to get resources that match a pattern
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // Create a MetadataReader factory to read the metadata of classes
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();

        // Get all resources that match the pattern
        Resource[] resources = resolver.getResources("classpath*:com/xcs/spring/bean/**/*.class");

        // Create the custom condition used for matching
        Condition condition = new MyOnClassCondition("com.xcs.spring.ConditionDemo1");

        // Go through each resource and check the custom condition
        for (Resource resource : resources) {
            // Get the MetadataReader of the resource
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);

            // Check if the resource meets the custom condition
            if (condition.matches(null, metadataReader.getAnnotationMetadata())) {
                System.out.println(resource.getFile().getName() + " meets the condition");
            } else {
                System.out.println(resource.getFile().getName() + " does not meet the condition");
            }
        }
    }
}

