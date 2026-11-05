package com.xcs.spring;

import org.springframework.cglib.proxy.Enhancer;

public class CglibProxyDemo {

    public static void main(String[] args) {
        // Create an Enhancer object to generate the proxy class
        Enhancer enhancer = new Enhancer();
        // Set the superclass of the target object
        enhancer.setSuperclass(MyServiceImpl.class);
        // Set the callback interceptor
        enhancer.setCallback(new MyMethodInterceptor());
        // Create the proxy object
        MyService proxyObject = (MyService) enhancer.create();
        // Print the class name of the proxy object
        System.out.println("ProxyObject = " + proxyObject.getClass());
        // Call the method of the proxy object
        proxyObject.doSomething();
    }
}
