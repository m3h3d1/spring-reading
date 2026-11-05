package com.xcs.spring;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * @author xcs
 * @date 2023-10-30 17:30
 **/
public class PathMatchingResourcePatternResolverDemo {
    public static void main(String[] args) throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // Load all matching classpath resources
        Resource[] resources = resolver.getResources("classpath*:*.properties");
        for (Resource resource : resources) {
            System.out.println("Classpath = " + resource.getFilename());
        }

        // Load all matching file system resources
        Resource[] fileResources = resolver.getResources("file:/idea-work-space-xcs/spring-reading/spring-resources/spring-resource-resourceLoader/*.txt");
        for (Resource resource : fileResources) {
            System.out.println("File = " + resource.getFilename());
        }
    }
}
