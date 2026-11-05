package com.xcs.spring;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * @author xcs
 * @date 2023-10-30 11:11
 **/
public class FileSystemResourceDemo {
    public static void main(String[] args) throws Exception {
        // Replace with your own directory
        String path = "D:\\idea-work-space-xcs\\spring-reading\\spring-resources\\spring-resource\\myfile.txt";
        Resource resource = new FileSystemResource(path);
        try (InputStream is = resource.getInputStream()) {
            // Read and process the resource content
            System.out.println(new String(is.readAllBytes()));
        }
    }
}
