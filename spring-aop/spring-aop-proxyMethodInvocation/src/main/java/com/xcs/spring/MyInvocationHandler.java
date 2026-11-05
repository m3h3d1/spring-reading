package com.xcs.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * A custom InvocationHandler implementation that handles method calls of the Java dynamic proxy.
 */
class MyInvocationHandler implements InvocationHandler {

    // The target object
    private final Object target;

    /**
     * Constructor that initializes the target object.
     * @param target the target object
     */
    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    /**
     * The core method that handles method calls.
     * @param proxy the proxy object
     * @param method the method object that is called
     * @param args the method arguments
     * @return the result of the method call
     * @throws Throwable the exception that may be thrown
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Create a MyReflectiveMethodInvocation object to run the method call chain
        MyReflectiveMethodInvocation invocation = new MyReflectiveMethodInvocation(proxy, target, method, args, target.getClass(), List.of(new MyMethodInterceptor()));
        // Run the method call chain and return the result
        return invocation.proceed();
    }
}
