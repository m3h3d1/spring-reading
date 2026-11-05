## AdvisorChainFactory

- [AdvisorChainFactory](#advisorchainfactory)
    - [1. Overview](#1-overview)
    - [2. Description](#2-description)
    - [3. Main Features](#3-main-features)
    - [4. Interface Source](#4-interface-source)
    - [5. Main Implementations](#5-main-implementations)
    - [6. Class Diagram](#6-class-diagram)
    - [7. Best Practices](#7-best-practices)
    - [8. Source Code Analysis](#8-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `AdvisorChainFactory` interface is the factory interface in Spring AOP that creates advisor chains. Its method `getInterceptorsAndDynamicInterceptionAdvice()` turns a set of advisors into an array of interceptors. These are used to manage and run aspect logic, and give flexibility and extensibility to customize how aspects run.

### 3. Main Features

1. **Create an advisor chain (Advisor Chain)** 

   + The method `getInterceptorsAndDynamicInterceptionAdvice()` turns a set of advisors into an array of interceptors. This forms the advisor chain, which runs specific actions before and after the target method.

2. **Create dynamic advisor chains** 

   + The advisor chain can be created dynamically at runtime. For example, the type of the target object or the method signature can decide which advice should run.

### 4. Interface Source

The `AdvisorChainFactory` interface is a factory interface that creates Advisor chains. Its method `getInterceptorsAndDynamicInterceptionAdvice()` takes the AOP configuration (an `Advised` object), the proxied method, and the target class. It returns a list of MethodInterceptors that configure the Advisor chain. The interface decides which interceptors should apply when a proxied method runs, and whether the method needs dynamic matching, based on the given configuration.

```java
/**
 * Factory interface for Advisor chains.
 * Factory interface for advisor chains.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface AdvisorChainFactory {

	/**
	 * Determine a set of MethodInterceptor objects for the given Advisor chain configuration.
	 * Determine a list of {@link org.aopalliance.intercept.MethodInterceptor} objects
	 * for the given advisor chain configuration.
	 * @param config the Advised object that stands for the AOP configuration
	 * @param method the proxied method
	 * @param targetClass the target class (may be null, which means a proxy without a target object. In that case, the declaring class of the method is the next best choice)
	 * @return a list of MethodInterceptors (may also include InterceptorAndDynamicMethodMatchers)
	 */
	List<Object> getInterceptorsAndDynamicInterceptionAdvice(Advised config, Method method, @Nullable Class<?> targetClass);

}
```

### 5. Main Implementations

1. **DefaultAdvisorChainFactory**

   + Decides which interceptors apply, based on the given AOP configuration, the proxied method, and the target class. It supports dynamic method matching and a caching mechanism, so that advisor chains are created efficiently.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class AdvisorChainFactory {
<<Interface>>

}
class DefaultAdvisorChainFactory

DefaultAdvisorChainFactory  ..>  AdvisorChainFactory 
~~~

### 7. Best Practices

This example uses the `DefaultAdvisorChainFactory` class to create an Advisor chain. First, it creates an `AdvisedSupport` object and configures before advice and after returning advice. Then it sets the target class and the target method. Next, it creates a `DefaultAdvisorChainFactory` and calls its `getInterceptorsAndDynamicInterceptionAdvice()` method to get the Advisor chain. Finally, it prints the interceptors in the chain.

```java
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
```

This is the result. It shows two interceptors in the Advisor chain: `MethodBeforeAdviceInterceptor` and `AfterReturningAdviceInterceptor`. They were created from the configured before advice and after returning advice, and they run extra actions before and after the target method.

```java
org.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor@215be6bb
org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor@4439f31e
```

### 8. Source Code Analysis

The `DefaultAdvisorChainFactory` class. It gives a simple but definite way to build the advice chain of a method, based on the given `Advised` object. It loops over the configured Advisor array, and decides which interceptors apply based on the Advisor type and Pointcut. In the end, it returns a list of interceptors. Along the way, it supports dynamic method matching and handling of introduction interceptors, and it has a cache to improve performance.

[AdvisorAdapterRegistry Source Code Analysis](../spring-aop-advisorAdapterRegistry/README.md)

```java
/**
 * A simple but definite way to determine an advice chain for a method, given an {@link Advised} object. It always rebuilds each advice chain;
 * subclasses can provide caching.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @author Adrian Colyer
 * @since 2.0.3
 */
@SuppressWarnings("serial")
public class DefaultAdvisorChainFactory implements AdvisorChainFactory, Serializable {

	@Override
	public List<Object> getInterceptorsAndDynamicInterceptionAdvice(
			Advised config, Method method, @Nullable Class<?> targetClass) {

		// Get the Advisor adapter registry
		AdvisorAdapterRegistry registry = GlobalAdvisorAdapterRegistry.getInstance();
		// Get all Advisors in the AOP configuration
		Advisor[] advisors = config.getAdvisors();
		// Create an interceptor list
		List<Object> interceptorList = new ArrayList<>(advisors.length);
		// Get the actual class
		Class<?> actualClass = (targetClass != null ? targetClass : method.getDeclaringClass());
		Boolean hasIntroductions = null;

		// Loop over all Advisors
		for (Advisor advisor : advisors) {
			if (advisor instanceof PointcutAdvisor) {
				// Add conditionally.
				PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
				if (config.isPreFiltered() || pointcutAdvisor.getPointcut().getClassFilter().matches(actualClass)) {
					// Get the Pointcut and MethodMatcher of the Advisor
					MethodMatcher mm = pointcutAdvisor.getPointcut().getMethodMatcher();
					boolean match;
					if (mm instanceof IntroductionAwareMethodMatcher) {
						if (hasIntroductions == null) {
							// Check whether there is a matching IntroductionAdvisor
							hasIntroductions = hasMatchingIntroductions(advisors, actualClass);
						}
						match = ((IntroductionAwareMethodMatcher) mm).matches(method, actualClass, hasIntroductions);
					}
					else {
						match = mm.matches(method, actualClass);
					}
					// If it matches, add the Interceptor to the interceptor list
					if (match) {
						MethodInterceptor[] interceptors = registry.getInterceptors(advisor);
						if (mm.isRuntime()) {
							// If it is a dynamic match, create a new InterceptorAndDynamicMethodMatcher object
							for (MethodInterceptor interceptor : interceptors) {
								interceptorList.add(new InterceptorAndDynamicMethodMatcher(interceptor, mm));
							}
						}
						else {
							// Otherwise add the Interceptor directly
							interceptorList.addAll(Arrays.asList(interceptors));
						}
					}
				}
			}
			else if (advisor instanceof IntroductionAdvisor) {
				IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
				if (config.isPreFiltered() || ia.getClassFilter().matches(actualClass)) {
					// If it is an IntroductionAdvisor, get the Interceptor directly and add it to the interceptor list
					Interceptor[] interceptors = registry.getInterceptors(advisor);
					interceptorList.addAll(Arrays.asList(interceptors));
				}
			}
			else {
				// For other types of Advisors, get the Interceptor directly and add it to the interceptor list
				Interceptor[] interceptors = registry.getInterceptors(advisor);
				interceptorList.addAll(Arrays.asList(interceptors));
			}
		}

		// Return the interceptor list
		return interceptorList;
	}

	/**
	 * Check whether the Advisor list has a matching introduction interceptor.
	 */
	private static boolean hasMatchingIntroductions(Advisor[] advisors, Class<?> actualClass) {
		for (Advisor advisor : advisors) {
			if (advisor instanceof IntroductionAdvisor) {
				IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
				if (ia.getClassFilter().matches(actualClass)) {
					return true;
				}
			}
		}
		return false;
	}

}
```
