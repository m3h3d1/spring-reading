package com.xcs.spring;

import java.lang.reflect.Proxy;

public class JdkProxyDemo {

    public static void main(String[] args) {
        // Create the target object
        MyService target = new MyServiceImpl();
        // Get the class object of the target object
        Class clz = target.getClass();
        // Create the proxy object with the class loader of the target object, the interfaces it implements, and the invocation handler
        MyService proxyObject = (MyService) Proxy.newProxyInstance(clz.getClassLoader(), clz.getInterfaces(), new MyInvocationHandler(target));
        // Print the class information of the proxy object
        System.out.println("ProxyObject = " + proxyObject.getClass());
        // Call the method through the proxy object, which actually calls the invoke method in MyInvocationHandler
        proxyObject.doSomething();
    }
}
