package com.xcs.spring;

import com.xcs.spring.annotation.MyAnnotation;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

/**
 * @author xcs
 * @date 2023-11-20 10:34
 **/
public class TypeFilterDemo {

    public static void main(String[] args) throws IOException {
        // Create a resolver for path pattern resources
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // Create a simple metadata reader factory
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();

        // Create an annotation type filter to match classes with the MyAnnotation annotation
        TypeFilter annotationTypeFilter = new AnnotationTypeFilter(MyAnnotation.class);

        // Use the resolver to get all class files that match the path
        Resource[] resources = resolver.getResources("classpath*:com/xcs/spring/**/*.class");

        // Go through the scanned class files
        for (Resource resource : resources) {
            // Get the metadata reader
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);

            // Match the current class with the annotation type filter
            boolean match = annotationTypeFilter.match(metadataReader, metadataReaderFactory);

            // Print the scanned file name and the match result
            System.out.printf("Scanned file: %-20s || Filter matches: %s%n", resource.getFile().getName(), match);
        }
    }
}
