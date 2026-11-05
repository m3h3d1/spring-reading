package com.xcs.spring.bean;

import org.springframework.beans.factory.DisposableBean;

/**
 * @author xcs
 * @date 2023-11-22 14:28
 **/
public class MyBean implements DisposableBean {

    @Override
    public void destroy() throws Exception {
        System.out.println("MyBean was destroyed");
    }
}
