package com.xcs.spring;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author xcs
 * @date 2023-10-30 11:30
 **/
public class InputStreamResourceDemo {
    public static void main(String[] args) throws Exception {
        InputStream isSource = new ByteArrayInputStream("hello world".getBytes());
        Resource resource = new InputStreamResource(isSource);
        try (InputStream is = resource.getInputStream()) {
            // Read and process the resource content
            System.out.println(new String(is.readAllBytes()));
        }
    }
}
