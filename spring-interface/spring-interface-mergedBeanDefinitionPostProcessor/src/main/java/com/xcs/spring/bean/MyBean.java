package com.xcs.spring.bean;

import com.xcs.spring.annotation.MyValue;

/**
 * @author xcs
 * @date 2023-09-20 10:52
 **/
public class MyBean {

    @MyValue("hello world")
    private String message;

    public String getMessage() {
        return message;
    }
}
