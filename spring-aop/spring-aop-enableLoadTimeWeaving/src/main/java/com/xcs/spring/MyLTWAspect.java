package com.xcs.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class MyLTWAspect {

    @Around("ltwPointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // Logic that runs before the method call
        System.out.println("Before Method " + pjp.getSignature().getName());
        // Call the original method
        Object result = pjp.proceed();
        // Logic that runs after the method call
        System.out.println("After Method " + pjp.getSignature().getName());
        return result;
    }

    @Pointcut("execution(public * com.xcs.spring.MyService.*(..))")
    public void ltwPointcut() {
    }
}
