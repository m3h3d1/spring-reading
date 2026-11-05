package com.xcs.spring;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;

import java.lang.reflect.Method;

class MyCustomPointcut implements Pointcut {

    @Override
    public ClassFilter getClassFilter() {
        // Match all classes
        return clazz -> true;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new MethodMatcher() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                // Match all methods whose names start with "get"
                return method.getName().startsWith("get");
            }

            @Override
            public boolean isRuntime() {
                // Whether dynamic matching is needed at runtime
                return false;
            }

            @Override
            public boolean matches(Method method, Class<?> targetClass, Object... args) {
                // Runtime matching is not needed here, so simply return false
                return false;
            }
        };
    }
}
