package com.xcs.spring;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * @author xcs
 * @date 2023-10-30 11:29
 **/
public class ByteArrayResourceDemo {
    public static void main(String[] args) throws Exception {
        byte[] data = "hello world".getBytes();
        Resource resource = new ByteArrayResource(data);
        try (InputStream is = resource.getInputStream()) {
            // Read and process the resource content
            System.out.println(new String(is.readAllBytes()));
        }
    }
}
