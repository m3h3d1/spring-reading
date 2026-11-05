package com.xcs.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.adapter.DefaultAdvisorAdapterRegistry;

public class AdvisorAdapterRegistryDemo {

    public static void main(String[] args) {
        // Create the default Advisor adapter registry instance
        DefaultAdvisorAdapterRegistry registry = new DefaultAdvisorAdapterRegistry();
        // Wrap the given MyMethodBeforeAdvice as an Advisor
        Advisor advisor = registry.wrap(new MyMethodBeforeAdvice());

        // Get the interceptor array from the Advisor
        MethodInterceptor[] interceptors = registry.getInterceptors(advisor);
        // Print the interceptor information
        for (MethodInterceptor interceptor : interceptors) {
            System.out.println("interceptor = " + interceptor);
        }
    }
}
