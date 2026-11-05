package com.xcs.spring.service;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author xcs
 * @date 2023-10-25 10:37
 **/
public class MyServiceB {

    /**
     * Method injection
     */
    private MyServiceA methodMyServiceA;

    /**
     * Field injection
     */
    private MyServiceA fieldMyServiceA;

    /**
     * Field injection (environment variable)
     */
    @Value("${my.property.value}")
    private String myPropertyValue;

    public void setMethodMyServiceA(MyServiceA methodMyServiceA){
        this.methodMyServiceA = methodMyServiceA;
    }

    @Override
    public String toString() {
        return "MyServiceB{" +
                "myPropertyValue='" + myPropertyValue + '\'' +
                ", methodMyServiceA=" + methodMyServiceA +
                ", fieldMyServiceA=" + fieldMyServiceA +
                '}';
    }
}
