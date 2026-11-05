package com.xcs.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.AfterAdvice;
import org.springframework.util.Assert;

/**
 * Null returning advice interceptor. After a method runs, it checks whether the return value is null, and runs the null returning advice logic when needed.
 */
public class NullReturningAdviceInterceptor implements MethodInterceptor, AfterAdvice {

    /** Null returning advice */
    private final NullReturningAdvice advice;

    /**
     * Construct a null returning advice interceptor.
     * @param advice the null returning advice
     */
    public NullReturningAdviceInterceptor(NullReturningAdvice advice) {
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
    }

    /**
     * Intercept after the method runs. Check whether the return value is null, and run the null returning advice logic when needed.
     * @param mi the method invocation
     * @return the result of the method. If the return value is null, the value returned by the null returning advice
     * @throws Throwable if an exception happens during the method call
     */
    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        // Run the method call and get the return value
        Object retVal = mi.proceed();
        // If the return value is null, use the value returned by the null returning advice
        if (retVal == null) {
            retVal = this.advice.nullReturning(mi.getMethod(), mi.getArguments(), mi.getThis());
        }
        return retVal;
    }
}
