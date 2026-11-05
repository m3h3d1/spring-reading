package com.xcs.spring;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

/**
 * Custom Advisor that applies advice to methods with a specific annotation.
 */
public class MyCustomAdvisor implements PointcutAdvisor {

    /**
     * Advice object
     */
    private final Advice advice = new MyAdvice();

    /**
     * Pointcut object that matches methods with the custom annotation
     */
    private final Pointcut pointcut = new AnnotationMatchingPointcut(null, MyCustomAnnotation.class);

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }
}
