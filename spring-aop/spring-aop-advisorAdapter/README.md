## AdvisorAdapter

- [AdvisorAdapter](#advisoradapter)
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

The `AdvisorAdapter` interface is an important interface in Spring AOP. It adapts different types of advice (Advice) into the interceptor chain, so that they can be applied when the target method runs. It lets us write our own adapter to connect custom advice to the Spring AOP framework. This gives before, after, and around enhancement of the target method, and makes AOP flexible and extensible.

### 3. Main Features

1. **Advice adaptation** 

   + Adapts different types of advice (Advice) into the Spring AOP interceptor chain, so that they can be applied when the target method runs.

2. **Support for different advice types** 

   + Supports adapting many types of advice, including before advice (MethodBeforeAdvice), after returning advice (AfterReturningAdvice), around advice (MethodInterceptor), and throws advice (ThrowsAdvice).

3. **Adapter registration and management** 

   + Lets us register and manage adapters for different types of advice, so that different types of advice can be used in the application.


### 4. Interface Source

This interface defines a way to bring new Advisor and Advice types into the Spring AOP framework. An object that implements it can turn a custom Advice type into an AOP Alliance interceptor, so that the custom Advice type can be used in the Spring AOP framework. Most Spring users do not need to implement this interface directly. You only need to do so when you want to add a new Advisor or Advice type.

```java
/**
 * Interface that allows extension of the Spring AOP framework to handle new Advisor and Advice types.
 *
 * <p>Implementing objects can create AOP Alliance interceptors from custom Advice types,
 * so that these Advice types can be used in the Spring AOP framework, which uses interception under the hood.
 *
 * <p>Most Spring users do not need to implement this interface. You only need to do so if you want to introduce more Advisor or Advice types to Spring.
 *
 * @author Rod Johnson
 */
public interface AdvisorAdapter {

    /**
     * Does this adapter understand the advice object? Is it possible to call getInterceptors with an Advisor that contains this advice as an argument?
     * @param advice an Advice, such as a BeforeAdvice
     * @return whether this adapter understands the given Advice object
     * @see #getInterceptor(org.springframework.aop.Advisor)
     * @see org.springframework.aop.BeforeAdvice
     */
    boolean supportsAdvice(Advice advice);

    /**
     * Return an AOP Alliance MethodInterceptor that exposes the behavior of the given Advice to an interception-based AOP framework.
     * <p>Do not worry about the Pointcut contained in the Advisor. The AOP framework takes care of checking the pointcut.
     * @param advisor the Advisor. The supportsAdvice() method must return true on this object
     * @return an AOP Alliance interceptor for this Advisor. There is no need to cache instances for efficiency, because the AOP framework caches the Advice chain.
     */
    MethodInterceptor getInterceptor(Advisor advisor);
}
```

### 5. Main Implementations

1. **MethodBeforeAdviceAdapter** 

   + Adapts advice of type `MethodBeforeAdvice` into the Spring AOP interceptor chain. `MethodBeforeAdvice` is an advice interface that runs before the target method.

2. **ThrowsAdviceAdapter** 

   + Adapts advice of type `ThrowsAdvice` into the Spring AOP interceptor chain. `ThrowsAdvice` advice catches exceptions thrown by the target method.

3. **AfterReturningAdviceAdapter** 

   + Adapts advice of type `AfterReturningAdvice` into the Spring AOP interceptor chain. `AfterReturningAdvice` advice runs after the target method returns normally.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class AdvisorAdapter {
<<Interface>>

}
class AfterReturningAdviceAdapter
class MethodBeforeAdviceAdapter
class ThrowsAdviceAdapter

AfterReturningAdviceAdapter  ..>  AdvisorAdapter 
MethodBeforeAdviceAdapter  ..>  AdvisorAdapter 
ThrowsAdviceAdapter  ..>  AdvisorAdapter 
~~~

### 7. Best Practices

This example uses a custom AdvisorAdapter and Advice to enhance the target method. First, it registers a custom AdvisorAdapter (NullReturningAdviceAdapter). Then it creates a proxy factory (ProxyFactory) and adds a custom advice (MyNullReturningAdvice). Finally, it gets the proxy object from the factory and calls two methods. One triggers the advice. The other does not.

```java
public class AdvisorAdapterDemo {

    public static void main(String[] args) {
        // Register the custom adapter
        GlobalAdvisorAdapterRegistry.getInstance().registerAdvisorAdapter(new NullReturningAdviceAdapter());
        // Create the proxy factory
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // Add the Advisor
        proxyFactory.addAdvice(new MyNullReturningAdvice());
        // Get the proxy object
        MyService proxy = (MyService) proxyFactory.getProxy();
        // Does not trigger the advice
        System.out.println("foo return value : " + proxy.foo());
        // New line
        System.out.println("==================================");
        // Triggers the advice
        System.out.println("bar return value : " + proxy.bar());
    }
}
```

An adapter for null returning advice. It adapts null returning advice (NullReturningAdvice) into the interceptor chain. It implements the AdvisorAdapter interface, with methods to check whether a given advice is supported and to get a method interceptor. This exposes the behavior of this kind of advice to an interception-based AOP framework.

```java
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
```

A null returning advice interceptor. After a method runs, it checks whether the return value is null, and runs the null returning advice logic when needed. It implements the MethodInterceptor and AfterAdvice interfaces. It looks at the value returned by the method call to decide whether to run the null returning advice, and calls its logic when needed.

```java
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
```

A definition of null returning advice. It extends the AfterAdvice interface. It has one method, nullReturning, which runs logic when the return value of the target method is null and returns a new return value.

```java
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

```

This class implements the `NullReturningAdvice` interface. It runs specific logic when the return value of the target method is null. The nullReturning method returns a default string value.

```java
public class MyNullReturningAdvice implements NullReturningAdvice {

    @Override
    public Object nullReturning(Method method, Object[] args, Object target) throws Throwable {
        return "this is a defaultValue";
    }
}
```

A simple service class with two methods, foo and bar. After foo runs, it returns the string "this is a foo". After bar runs, it returns null.

```java
public class MyService {

    public String foo() {
        System.out.println("foo...");
        return "this is a foo";
    }

    public String bar() {
        System.out.println("bar...");
        return null;
    }
}
```

This is the result. The foo method was called and returned "this is a foo". Then the bar method was called. Its return value is null, so the null returning advice was triggered. It printed the matching message and returned the default value "this is a defaultValue".

```java
foo...
foo return value : this is a foo
==================================
bar...
bar return value : this is a defaultValue
```

### 8. Source Code Analysis

**Register the adapter**

In the method `org.springframework.aop.framework.adapter.DefaultAdvisorAdapterRegistry#registerAdvisorAdapter`, a new AdvisorAdapter instance is registered in the adapter list.

```java
/**
 * Register an Advisor adapter.
 * @param adapter the Advisor adapter to register
 */
@Override
public void registerAdvisorAdapter(AdvisorAdapter adapter) {
    this.adapters.add(adapter);
}
```

The field `org.springframework.aop.framework.adapter.DefaultAdvisorAdapterRegistry#adapters` stores the AdvisorAdapter instances.

```java
private final List<AdvisorAdapter> adapters = new ArrayList<>(3);
```

**Adapt advice to interceptors**

In the method `org.springframework.aop.framework.JdkDynamicAopProxy#invoke`, which is the entry of the JDK dynamic proxy, the interceptor chain for the given method is obtained.

```java
@Override
@Nullable
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // ... [code omitted for brevity]

    try {
        // ... [code omitted for brevity]

        // Get the interception chain for this method.
        List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);

        // ... [code omitted for brevity]
    }
    finally {
        // ... [code omitted for brevity]
    }
}
```

In the method `org.springframework.aop.framework.CglibAopProxy.DynamicAdvisedInterceptor#intercept`, which is the entry of the CGLIB dynamic proxy, the interceptor chain for the given method is obtained.

```java
@Override
@Nullable
public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
    // ... [code omitted for brevity]
    try {
        // ... [code omitted for brevity]
        List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
        // ... [code omitted for brevity]
    }
    finally {
        // ... [code omitted for brevity]
    }
}
```

In the method `org.springframework.aop.framework.AdvisedSupport#getInterceptorsAndDynamicInterceptionAdvice`, the configuration decides the interceptor chain for the given method. It first tries to get the chain from the cache. If it is not in the cache, it gets the chain through AdvisorChainFactory, puts the result in the cache, and returns it.

```java
/**
 * Determine the interceptor chain for the given method, based on the configuration.
 * @param method the proxied method
 * @param targetClass the target class
 * @return a list of method interceptors (may also include InterceptorAndDynamicMethodMatchers)
 */
public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, @Nullable Class<?> targetClass) {
    // Create the method cache key
    MethodCacheKey cacheKey = new MethodCacheKey(method);
    // Get the interceptor chain from the cache
    List<Object> cached = this.methodCache.get(cacheKey);
    // If the cache is empty
    if (cached == null) {
        // Get the interceptor chain through AdvisorChainFactory
        cached = this.advisorChainFactory.getInterceptorsAndDynamicInterceptionAdvice(
                this, method, targetClass);
        // Put the interceptor chain into the cache
        this.methodCache.put(cacheKey, cached);
    }
    // Return the interceptor chain
    return cached;
}
```

In the method `org.springframework.aop.framework.DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice`, the interceptor chain and dynamic interception advice that apply to the method are collected from the Advisor list, based on the given AOP configuration and method. Then, depending on how the configuration and the target class match, suitable interceptors are added to the list, and the list is returned.

```java
/**
 * Get the interceptor chain and dynamic interception advice, based on the given AOP configuration and method.
 * @param config the AOP configuration object
 * @param method the proxied method
 * @param targetClass the target class
 * @return a list of the interceptor chain and dynamic interception advice
 */
@Override
public List<Object> getInterceptorsAndDynamicInterceptionAdvice(
       Advised config, Method method, @Nullable Class<?> targetClass) {

    // Get the global AdvisorAdapterRegistry instance
    AdvisorAdapterRegistry registry = GlobalAdvisorAdapterRegistry.getInstance();
    // Get all Advisors in the AOP configuration
    Advisor[] advisors = config.getAdvisors();
    // Create an interceptor list, with the size of the advisors array
    List<Object> interceptorList = new ArrayList<>(advisors.length);
    
    // ... [code omitted for brevity]

    // Loop over all Advisors
    for (Advisor advisor : advisors) {
        
       // ... [code omitted for brevity]
        
       // Get the interceptors for the Advisor
       Interceptor[] interceptors = registry.getInterceptors(advisor);
       // Add the interceptors to the interceptor list
       interceptorList.addAll(Arrays.asList(interceptors));
        
       // ... [code omitted for brevity]
    }

    // Return the interceptor list
    return interceptorList;
}
```

In the method `org.springframework.aop.framework.adapter.DefaultAdvisorAdapterRegistry#getInterceptors`, the interceptor array is obtained for the given Advisor. It first checks the Advice type in the Advisor. If it is a MethodInterceptor, it is added to the interceptor list directly. Then it loops over the registered AdvisorAdapters and looks for one that supports the Advice type. The interceptors returned by that adapter are added to the list. Finally, the list is turned into an array and returned. If no matching interceptor is found, an UnknownAdviceTypeException is thrown.

```java
/**
 * Get the interceptor array for the Advisor.
 * @param advisor the Advisor object
 * @return the interceptor array
 * @throws UnknownAdviceTypeException if the Advice type in the Advisor is not recognized
 */
@Override
public MethodInterceptor[] getInterceptors(Advisor advisor) throws UnknownAdviceTypeException {
    // Create an interceptor list
    List<MethodInterceptor> interceptors = new ArrayList<>(3);
    // Get the Advice object in the Advisor
    Advice advice = advisor.getAdvice();
    // If the Advice object is a MethodInterceptor
    if (advice instanceof MethodInterceptor) {
       // Add the MethodInterceptor to the interceptor list
       interceptors.add((MethodInterceptor) advice);
    }
    // Loop over all adapters
    for (AdvisorAdapter adapter : this.adapters) {
       // If the adapter supports the Advice object
       if (adapter.supportsAdvice(advice)) {
          // Get the adapter's interceptor and add it to the interceptor list
          interceptors.add(adapter.getInterceptor(advisor));
       }
    }
    // If the interceptor list is empty
    if (interceptors.isEmpty()) {
       // Throw an unknown advice type exception
       throw new UnknownAdviceTypeException(advisor.getAdvice());
    }
    // Turn the interceptor list into an array and return it
    return interceptors.toArray(new MethodInterceptor[0]);
}
```

In the method `com.xcs.spring.NullReturningAdviceAdapter#supportsAdvice`, it checks whether this adapter supports the given Advice type.

```java
/**
 * Check whether this adapter supports the given Advice type.
 * @param advice the Advice object
 * @return true if the adapter supports the given Advice type; otherwise false
 */
@Override
public boolean supportsAdvice(Advice advice) {
    // Check whether the Advice object is of type NullReturningAdvice
    return (advice instanceof NullReturningAdvice);
}
```

In the method `com.xcs.spring.NullReturningAdviceAdapter#getInterceptor`, it first gets the Advice object from the Advisor and casts it to NullReturningAdvice. Then it uses that Advice object to create a NullReturningAdviceInterceptor and returns it.

```java
/**
 * Get the interceptor for the Advisor.
 * @param advisor the Advisor object
 * @return an interceptor
 */
@Override
public MethodInterceptor getInterceptor(Advisor advisor) {
    // Cast the Advice object in the Advisor to NullReturningAdvice
    NullReturningAdvice advice = (NullReturningAdvice) advisor.getAdvice();
    // Create a NullReturningAdviceInterceptor, with the Advice in the Advisor as the argument
    return new NullReturningAdviceInterceptor(advice);
}
```
