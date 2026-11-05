package com.xcs.spring.service;

/**
 * @author xcs
 * @date 2023-09-19 16:43
 **/
public class MyServiceImpl implements MyService{

    private String message = "Hello from MyService";

    @Override
    public String show() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
