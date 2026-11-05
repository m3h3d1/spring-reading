package com.xcs.spring;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.InputStream;

/**
 * @author xcs
 * @date 2023-10-30 11:17
 **/
public class UrlResourceDemo {
    public static void main(String[] args) throws Exception {
        Resource resource = new UrlResource("https://dist.apache.org/repos/dist/test/test.txt");
        try (InputStream is = resource.getInputStream()) {
            // Read and process the resource content
            System.out.println(new String(is.readAllBytes()));
        }
    }
}
