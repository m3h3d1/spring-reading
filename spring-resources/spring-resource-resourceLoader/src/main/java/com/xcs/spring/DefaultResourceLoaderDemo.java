package com.xcs.spring;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

/**
 * @author xcs
 * @date 2023-10-30 17:22
 **/
public class DefaultResourceLoaderDemo {
    public static void main(String[] args) {
        DefaultResourceLoader loader = new DefaultResourceLoader();

        // Load a resource from the classpath
        Resource classpathResource = loader.getResource("classpath:application.properties");
        System.out.println("Classpath Exists= " + classpathResource.exists());

        // Load a resource from the file system
        Resource fileResource = loader.getResource("file:/idea-work-space-xcs/spring-reading/spring-resources/spring-resource-resourceLoader/myfile1.txt");
        System.out.println("File Exists = " + fileResource.exists());
    }
}
