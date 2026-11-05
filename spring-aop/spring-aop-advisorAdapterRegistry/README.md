## AdvisorAdapterRegistry

- [AdvisorAdapterRegistry](#advisoradapterregistry)
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

The `AdvisorAdapterRegistry` interface is one of the key interfaces in Spring AOP. It registers and manages AdvisorAdapters. It links an Advisor to the specific interceptor that the AOP framework supports. This lets the framework intercept and enhance methods of the target object, which is the aspect-oriented programming feature.

### 3. Main Features

1. **Register AdvisorAdapters**

   + Lets us register custom AdvisorAdapters to support new interceptor types or extend existing interceptor logic.

3. **Support built-in interceptors**

   + The default implementation registers some standard AdvisorAdapters in advance. They support the interceptor types built into the Spring AOP framework (such as BeforeAdvice and AfterReturningAdvice).

### 4. Interface Source

A registry of Advisor adapters. It has methods to wrap a given advice as an Advisor, and to get the array of interceptors from an Advisor. By registering AdvisorAdapters, it adapts between an Advisor and the different interceptor types that the AOP framework supports.

```java
/**
 * Interface for registries of Advisor adapters.
 *
 * <p><i>This is an SPI interface and should not be implemented by any Spring user.</i>
 *
 * @author Rod Johnson
 * @author Rob Harrop
 */
public interface AdvisorAdapterRegistry {

	/**
	 * Return an {@link Advisor} that wraps the given advice.
	 * <p>By default, at least the following should be supported:
	 * {@link org.aopalliance.intercept.MethodInterceptor},
	 * {@link org.springframework.aop.MethodBeforeAdvice},
	 * {@link org.springframework.aop.AfterReturningAdvice},
	 * {@link org.springframework.aop.ThrowsAdvice}。
	 * @param advice an object that should be an advice
	 * @return an Advisor that wraps the given advice (never {@code null};
	 * if the advice argument is already an Advisor, it is returned directly)
	 * @throws UnknownAdviceTypeException if no registered advisor adapter
	 * can wrap the given advice
	 */
	Advisor wrap(Object advice) throws UnknownAdviceTypeException;

	/**
	 * Return an array of AOP Alliance MethodInterceptors, so that the given Advisor can be used in an interception-based framework.
	 * <p>If the Advisor is a {@link org.springframework.aop.PointcutAdvisor},
	 * do not worry about the pointcut expression attached to it. Just return an interceptor.
	 * @param advisor the Advisor to find interceptors for
	 * @return an array of MethodInterceptors that expose the behavior of this Advisor
	 * @throws UnknownAdviceTypeException if the Advisor type
	 * is not understood by any registered AdvisorAdapter
	 */
	MethodInterceptor[] getInterceptors(Advisor advisor) throws UnknownAdviceTypeException;

	/**
	 * Register the given {@link AdvisorAdapter}.
	 * Note that there is no need to register adapters for AOP Alliance Interceptors or Spring Advices.
	 * These must be recognized automatically by an implementation of {@code AdvisorAdapterRegistry}.
	 * @param adapter an AdvisorAdapter that understands a particular Advisor or Advice type
	 */
	void registerAdvisorAdapter(AdvisorAdapter adapter);

}
```

### 5. Main Implementations

1. **DefaultAdvisorAdapterRegistry**

   + The default Advisor adapter registry. It registers the standard Advisor adapters in advance. It adapts the various types of Advice to the AOP Alliance MethodInterceptor, and lets us register custom Advisor adapters. This gives flexible adaptation and management between Advisors and interceptors.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class AdvisorAdapterRegistry {
<<Interface>>

}
class DefaultAdvisorAdapterRegistry

DefaultAdvisorAdapterRegistry  ..>  AdvisorAdapterRegistry 
~~~

### 7. Best Practices

This example uses `DefaultAdvisorAdapterRegistry` to wrap the custom `MyMethodBeforeAdvice` and get its interceptor array. The `wrap()` method turns `MyMethodBeforeAdvice` into an `Advisor`. Then the `getInterceptors()` method gets the interceptor array of that `Advisor`. Finally, it prints the interceptor information.

```java
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
```

### 8. Source Code Analysis

`DefaultAdvisorAdapterRegistry` is the default implementation of the `AdvisorAdapterRegistry` interface. It adapts different types of Advice objects into Advisors, and it can get the interceptor array from an Advisor. It registers some common Advisor adapters in advance, and lets users register custom adapters. Its core logic is to wrap an Advice object as an Advisor, get the interceptor array from an Advisor, and register Advisor adapters.

[AdvisorAdapter Source Code Analysis](../spring-aop-advisorAdapter/README.md)

```java
/**
 * Default implementation of the AdvisorAdapterRegistry interface.
 * Supports {@link org.aopalliance.intercept.MethodInterceptor},
 * {@link org.springframework.aop.MethodBeforeAdvice}、
 * {@link org.springframework.aop.AfterReturningAdvice}、
 * {@link org.springframework.aop.ThrowsAdvice}。
 *
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Juergen Hoeller
 */
@SuppressWarnings("serial")
public class DefaultAdvisorAdapterRegistry implements AdvisorAdapterRegistry, Serializable {

    // List that stores the registered AdvisorAdapters
    private final List<AdvisorAdapter> adapters = new ArrayList<>(3);

    /**
     * Create a new DefaultAdvisorAdapterRegistry instance and register the known adapters.
     * The "known adapters" here are MethodBeforeAdviceAdapter, AfterReturningAdviceAdapter, and ThrowsAdviceAdapter.
     */
    public DefaultAdvisorAdapterRegistry() {
        // Register the MethodBeforeAdviceAdapter adapter
        registerAdvisorAdapter(new MethodBeforeAdviceAdapter());
        // Register the AfterReturningAdviceAdapter adapter
        registerAdvisorAdapter(new AfterReturningAdviceAdapter());
        // Register the ThrowsAdviceAdapter adapter
        registerAdvisorAdapter(new ThrowsAdviceAdapter());
    }

    /**
     * Wrap the given adviceObject as an Advisor.
     * If adviceObject is already an Advisor, it is returned directly.
     * If it is not an Advice type, an UnknownAdviceTypeException is thrown.
     * If the advice is a MethodInterceptor, a DefaultPointcutAdvisor is created and returned.
     * Otherwise, it loops over the registered AdvisorAdapters, finds an adapter that supports the advice, creates a DefaultPointcutAdvisor, and returns it.
     *
     * @param adviceObject the Advice object to wrap as an Advisor
     * @return the wrapped Advisor object
     * @throws UnknownAdviceTypeException if adviceObject cannot be recognized as an Advisor or Advice
     */
    @Override
    public Advisor wrap(Object adviceObject) throws UnknownAdviceTypeException {
        if (adviceObject instanceof Advisor) {
            return (Advisor) adviceObject;
        }
        if (!(adviceObject instanceof Advice)) {
            throw new UnknownAdviceTypeException(adviceObject);
        }
        Advice advice = (Advice) adviceObject;
        if (advice instanceof MethodInterceptor) {
            // For a MethodInterceptor, no adapter is needed. Create the Advisor and return it directly
            return new DefaultPointcutAdvisor(advice);
        }
        // Loop over the registered AdvisorAdapters and look for one that supports the current Advice
        for (AdvisorAdapter adapter : this.adapters) {
            // Check whether the current Advice is supported
            if (adapter.supportsAdvice(advice)) {
                // Create the Advisor and return it
                return new DefaultPointcutAdvisor(advice);
            }
        }
        // If no suitable adapter is found, throw an exception
        throw new UnknownAdviceTypeException(advice);
    }

    /**
     * Get the interceptor array from the Advisor.
     * If the Advice in the Advisor is a MethodInterceptor, it is returned directly.
     * Otherwise, it loops over the registered AdvisorAdapters, finds an adapter that supports the Advice in the Advisor, gets its interceptor, and returns the interceptor array.
     *
     * @param advisor the Advisor to get the interceptors from
     * @return an array that holds the interceptors of the Advisor
     * @throws UnknownAdviceTypeException if the Advice in the Advisor cannot be recognized
     */
    @Override
    public MethodInterceptor[] getInterceptors(Advisor advisor) throws UnknownAdviceTypeException {
        List<MethodInterceptor> interceptors = new ArrayList<>(3);
        Advice advice = advisor.getAdvice();
        // If the Advice in the Advisor is a MethodInterceptor, add it to the interceptor list directly
        if (advice instanceof MethodInterceptor) {
            interceptors.add((MethodInterceptor) advice);
        }
        // Loop over the registered AdvisorAdapters and look for one that supports the Advice in the Advisor
        for (AdvisorAdapter adapter : this.adapters) {
            // If the adapter supports the current Advice, get its interceptor and add it to the list
            if (adapter.supportsAdvice(advice)) {
                interceptors.add(adapter.getInterceptor(advisor));
            }
        }
        // If the interceptor list is empty, no adapter was found. Throw an exception
        if (interceptors.isEmpty()) {
            throw new UnknownAdviceTypeException(advice);
        }
        // Turn the interceptor list into an array and return it
        return interceptors.toArray(new MethodInterceptor[0]);
    }

    /**
     * Register the given AdvisorAdapter.
     *
     * @param adapter the AdvisorAdapter to register
     */
    @Override
    public void registerAdvisorAdapter(AdvisorAdapter adapter) {
        this.adapters.add(adapter);
    }

}
```
