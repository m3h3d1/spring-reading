package com.xcs.spring;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.adapter.AdvisorAdapter;

/**
 * Null returning advice adapter. It adapts null returning advice (NullReturningAdvice) into the interceptor chain.
 */
public class NullReturningAdviceAdapter implements AdvisorAdapter {

    /**
     * Check whether this adapter supports the given advice.
     * @param advice an advice, such as null returning advice (NullReturningAdvice)
     * @return true if this adapter supports the given advice; otherwise false
     */
    @Override
    public boolean supportsAdvice(Advice advice) {
        return (advice instanceof NullReturningAdvice);
    }

    /**
     * Get a method interceptor that exposes the behavior of the given advice to an interception-based AOP framework.
     * @param advisor the Advisor. The supportsAdvice() method must return true on this object
     * @return the method interceptor for the given Advisor
     */
    @Override
    public MethodInterceptor getInterceptor(Advisor advisor) {
        NullReturningAdvice advice = (NullReturningAdvice) advisor.getAdvice();
        return new NullReturningAdviceInterceptor(advice);
    }
}

