package com.xcs.spring;

import org.springframework.aop.AfterAdvice;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

/**
 * Null returning advice interface. Extends AfterAdvice.
 */
public interface NullReturningAdvice extends AfterAdvice {

    /**
     * Called when the return value of the target method is null.
     * @param method the target method
     * @param args the method arguments
     * @param target the target object
     * @return the value returned after the null returning advice runs
     * @throws Throwable if an exception happens while the null returning advice runs
     */
    Object nullReturning(Method method, Object[] args, @Nullable Object target) throws Throwable;
}
