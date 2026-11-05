package com.xcs.spring;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.DefaultAdvisorChainFactory;

import java.lang.reflect.Method;
import java.util.List;

public class AdvisorChainFactoryDemo {

    public static void main(String[] args) throws NoSuchMethodException {
        // Create the AOP configuration object
        AdvisedSupport config = new AdvisedSupport();
        // Add before advice
        config.addAdvice(new MyMethodBeforeAdvice());
        // Add after returning advice
        config.addAdvice(new MyAfterReturningAdvice());
        // Set the target class
        Class<MyService> targetClass = MyService.class;
        // Get the target method
        Method method = targetClass.getDeclaredMethod("foo");

        // Create the default Advisor chain factory instance
        DefaultAdvisorChainFactory chainFactory = new DefaultAdvisorChainFactory();
        // Get the Advisor chain
        List<Object> chain = chainFactory.getInterceptorsAndDynamicInterceptionAdvice(config, method, targetClass);
        // Print the interceptors in the Advisor chain
        chain.forEach(System.out::println);
    }
}
