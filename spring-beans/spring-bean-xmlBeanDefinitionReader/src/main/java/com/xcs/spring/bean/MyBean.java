package com.xcs.spring.bean;

/**
 * @author xcs
 * @date 2023-11-04 15:51
 **/
public class MyBean {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "message='" + message + '\'' +
                '}';
    }
}
