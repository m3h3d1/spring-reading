package com.xcs.spring;

import org.springframework.aop.framework.ReflectiveMethodInvocation;

import java.lang.reflect.Method;
import java.util.List;

/**
 * A custom method invocation object that extends the ReflectiveMethodInvocation class of Spring AOP.
 * It is used to add custom logic or enhancements to a method call.
 */
public class MyReflectiveMethodInvocation extends ReflectiveMethodInvocation {

    /**
     * Constructor that initializes the method invocation object.
     * @param proxy the proxy object
     * @param target the target object
     * @param method the method object that is called
     * @param arguments the method arguments
     * @param targetClass the class of the target object
     * @param interceptorsAndDynamicMethodMatchers the interceptor chain and the list of dynamic method matchers
     */
    public MyReflectiveMethodInvocation(Object proxy, Object target, Method method, Object[] arguments, Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
        super(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers);
    }
}
