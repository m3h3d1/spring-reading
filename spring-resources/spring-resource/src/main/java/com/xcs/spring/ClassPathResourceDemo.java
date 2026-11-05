package com.xcs.spring;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * @author xcs
 * @date 2023-10-30 11:04
 **/
public class ClassPathResourceDemo {
    public static void main(String[] args) throws Exception {
        String path = "application.properties";
        Resource resource = new ClassPathResource(path);
        try (InputStream is = resource.getInputStream()) {
            // Read and process the resource content
            System.out.println(new String(is.readAllBytes()));
        }
    }
}
