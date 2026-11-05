package com.xcs.spring.bean;

import com.xcs.spring.annotation.MyAnnotation;
import com.xcs.spring.annotation.MyClassAnnotation;

import java.io.Serializable;

/**
 * @author xcs
 * @date 2023-10-31 10:53
 **/
@MyClassAnnotation
public final class MyBean extends MyAbstract implements Serializable {

    public String key;

    public String value;

    @MyAnnotation
    public static void myMethod1() {
        // Implementation of method 1
    }

    @MyAnnotation
    public String myMethod2() {
        return "hello world";
    }

    public void myMethod3() {
        // Implementation of method 3
    }

    public static class MyInnerClass {
        // Definition of the inner class
    }
}
