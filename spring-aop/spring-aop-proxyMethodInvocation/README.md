## ProxyMethodInvocation

- [ProxyMethodInvocation](#proxymethodinvocation)
    - [1. Overview](#1-overview)
    - [2. Description](#2-description)
    - [3. Main Features](#3-main-features)
    - [4. Interface Source](#4-interface-source)
    - [5. Main Implementations](#5-main-implementations)
    - [6. Class Diagram](#6-class-diagram)
    - [7. Best Practices](#7-best-practices)
    - [8. Source Code Analysis](#8-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

`ProxyMethodInvocation` is one of the core interfaces in Spring AOP. It represents a proxy method call. It has methods to get the method, the arguments, the target object, and more, and it lets the interceptors in the interceptor chain handle the method call. It is a key part of method interception and enhancement.

### 3. Main Features

1. **Get method information**

   + The `getMethod()` method returns the method object that is being called, including the method name, the parameter types, and more.

2. **Get method arguments**

   + The `getArguments()` method returns the array of arguments passed in the method call. Interceptors can handle or check the arguments.

3. **Run the next interceptor or the target method**

   + The `proceed()` method continues with the next interceptor in the chain, or calls the target method. If `proceed()` is not called, the interceptor chain may stop and the target method does not run.

4. **Get the target object**

   + The `getThis()` method returns the target object being proxied, that is, the object instance that is intercepted.

5. **Get the proxy object**

   + The `getProxy()` method returns the proxy object that runs the current method call. This is useful when an interceptor needs to replace the return value with the proxy object.

### 4. Interface Source

The `ProxyMethodInvocation` interface extends the AOP Alliance `MethodInvocation` interface and gives access to the proxy object used for the method call. The interface can get the proxy object, create a clone of the method call, set the arguments of the method call, and add and get user attributes. It is used to handle method calls and enhance them in an AOP environment.

```java
/**
 * Extends the AOP Alliance {@link org.aopalliance.intercept.MethodInvocation} interface,
 * and allows access to the proxy object used for the method call.
 *
 * <p>If needed, this interface makes it easy to replace a return value with the proxy object,
 * for example if the invocation target returned itself.
 *
 * @author Juergen Hoeller
 * @author Adrian Colyer
 * @since 1.1.3
 * @see org.springframework.aop.framework.ReflectiveMethodInvocation
 * @see org.springframework.aop.support.DelegatingIntroductionInterceptor
 */
public interface ProxyMethodInvocation extends MethodInvocation {

    /**
     * Return the proxy object that runs this method call.
     * @return the original proxy object
     */
    Object getProxy();

    /**
     * Create a clone of this object. If cloning is done before {@code proceed()} is called on this object,
     * each clone can call {@code proceed()} once, to invoke the join point (and the rest of the advice chain) multiple times.
     * @return a callable clone of this invocation.
     * {@code proceed()} can be called once per clone.
     */
    MethodInvocation invocableClone();

    /**
     * Create a clone of this object with the given arguments for the clone. If cloning is done before {@code proceed()} is called on this object,
     * each clone can call {@code proceed()} once, to invoke the join point (and the rest of the advice chain) multiple times.
     * @param arguments the arguments for the cloned invocation, which override the original arguments
     * @return a callable clone of this invocation.
     * {@code proceed()} can be called once per clone.
     */
    MethodInvocation invocableClone(Object... arguments);

    /**
     * Set the arguments to be used on subsequent calls in this chain.
     * @param arguments the array of arguments
     */
    void setArguments(Object... arguments);

    /**
     * Add the given user attribute with the given value to this method call.
     * <p>These attributes are not used inside the AOP framework. They are just kept as part of the invocation object,
     * for use by special interceptors.
     * @param key the name of the attribute
     * @param value the value of the attribute, or {@code null} to reset it
     */
    void setUserAttribute(String key, @Nullable Object value);

    /**
     * Return the value of the given user attribute.
     * @param key the name of the attribute
     * @return the value of the attribute, or {@code null} if it is not set
     * @see #setUserAttribute
     */
    @Nullable
    Object getUserAttribute(String key);

}
```

### 5. Main Implementations

1. **ReflectiveMethodInvocation**

   + Runs the method call through Java reflection. When the target object uses an interface-based JDK dynamic proxy, Spring uses `ReflectiveMethodInvocation` to handle the method call. It is general, but slower. It fits target objects that are proxied by interface.

2. **CglibMethodInvocation** 

   + Runs the method call through a subclass generated by the CGLIB dynamic proxy. When the target object uses a class-based CGLIB proxy, Spring uses `CglibMethodInvocation` to handle the method call. It is usually faster than `ReflectiveMethodInvocation`, and is mainly used to proxy target objects that are not interface types.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class CglibMethodInvocation
class Invocation {
<<Interface>>

}
class Joinpoint {
<<Interface>>

}
class MethodInvocation {
<<Interface>>

}
class ProxyMethodInvocation {
<<Interface>>

}
class ReflectiveMethodInvocation

CglibMethodInvocation  -->  ReflectiveMethodInvocation 
Invocation  -->  Joinpoint 
MethodInvocation  -->  Invocation 
ProxyMethodInvocation  -->  MethodInvocation 
ReflectiveMethodInvocation  ..>  ProxyMethodInvocation 
~~~

### 7. Best Practices

This example uses the Java dynamic proxy to create a proxy object and call a method. First, it creates a target object `MyService target = new MyServiceImpl()`. Then it creates the proxy object with the `Proxy.newProxyInstance()` method, giving the class loader of the target object, the interfaces it implements, and the invocation handler. Finally, when a method is called on the proxy object, the `invoke()` method of the invocation handler runs the extra logic.

```java
public class ProxyMethodInvocationDemo {

    public static void main(String[] args) {
        // Create the target object
        MyService target = new MyServiceImpl();
        // Get the class object of the target object
        Class<? extends MyService> clz = target.getClass();
        // Create the proxy object with the class loader of the target object, the interfaces it implements, and the invocation handler
        MyService proxyObject = (MyService) Proxy.newProxyInstance(clz.getClassLoader(), clz.getInterfaces(), new MyInvocationHandler(target));
        // Call the method through the proxy object, which actually calls the invoke method in MyInvocationHandler
        proxyObject.foo();
    }
}
```

 The `MyInvocationHandler` class implements the `InvocationHandler` interface and works as the invocation handler of the Java dynamic proxy. In the `invoke()` method, it receives the proxy object, the method object, and the method arguments, uses them to create a `MyReflectiveMethodInvocation` object, and calls the `proceed()` method to run the method call chain. The purpose of this class is to pass the method call to the interceptor chain, to add extra logic or enhancement.

```java
/**
 * A custom InvocationHandler implementation that handles method calls of the Java dynamic proxy.
 */
class MyInvocationHandler implements InvocationHandler {

    // The target object
    private final Object target;

    /**
     * Constructor that initializes the target object.
     * @param target the target object
     */
    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    /**
     * The core method that handles method calls.
     * @param proxy the proxy object
     * @param method the method object that is called
     * @param args the method arguments
     * @return the result of the method call
     * @throws Throwable the exception that may be thrown
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Create a MyReflectiveMethodInvocation object to run the method call chain
        MyReflectiveMethodInvocation invocation = new MyReflectiveMethodInvocation(proxy, target, method, args, target.getClass(), List.of(new MyMethodInterceptor()));
        // Run the method call chain and return the result
        return invocation.proceed();
    }
}
```

We define our own `MyReflectiveMethodInvocation` class to extend `ReflectiveMethodInvocation` in Spring AOP and give it a public constructor. This lets you add extra logic or features in a custom method invocation object, and use this custom object in other places.

```java
/**
 * A custom method invocation object that extends the ReflectiveMethodInvocation class of Spring AOP.
 * It is used to add custom logic or enhancements to a method call.
 */
public class MyReflectiveMethodInvocation extends ReflectiveMethodInvocation {

    /**
     * Constructor that initializes the method invocation object.
     * @param proxy the proxy object
     * @param target the target object
     * @param method the method object that is called
     * @param arguments the method arguments
     * @param targetClass the class of the target object
     * @param interceptorsAndDynamicMethodMatchers the interceptor chain and the list of dynamic method matchers
     */
    public MyReflectiveMethodInvocation(Object proxy, Object target, Method method, Object[] arguments, Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
        super(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers);
    }
}
```

The `MyMethodInterceptor` class implements method interception and enhancement. In the `invoke()` method, it first gets information about the called method, such as the method name, from the `MethodInvocation` object, and prints that the method was called, before the method call. Then it calls `invocation.proceed()` to run the original method and get the result. Finally it returns the result.

```java
public class MyMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // Logic that runs before the method call
        System.out.println("Before Method " + invocation.getMethod().getName());
        // Call the original method
        Object result = invocation.proceed();
        // Logic that runs after the method call
        System.out.println("After Method " + invocation.getMethod().getName());
        return result;
    }
}
```

In the run result, when the `foo()` method of the `MyService` instance is called, the `MyMethodInterceptor` interceptor intercepts the method execution, and adds extra logic before and after it.

```java
Before Method foo
foo...
After Method foo
```

### 8. Source Code Analysis

The `org.springframework.aop.framework.ReflectiveMethodInvocation#ReflectiveMethodInvocation` method is the constructor of the `ReflectiveMethodInvocation` class. It creates a reflective method invocation object. It takes the proxy object, the target object, the method to call, the method arguments, the target class, and the list of interceptors and dynamic method matchers as parameters, and initializes them during construction.

```java
/**
 * Construct a new ReflectiveMethodInvocation with the given arguments.
 * @param proxy the proxy object the invocation is made on
 * @param target the target object to invoke
 * @param method the method to invoke
 * @param arguments the arguments to pass in the method call
 * @param targetClass the target class, used for method matcher calls
 * @param interceptorsAndDynamicMethodMatchers the interceptors that should be applied, and any InterceptorAndDynamicMethodMatchers that need to be evaluated at runtime.
 * The MethodMatchers in this structure must already have been found and matched, as static as possible. Passing an array might be about 10% faster, but it complicates the code. And it can only be used for static pointcuts.
 */
protected ReflectiveMethodInvocation(
        Object proxy, @Nullable Object target, Method method, @Nullable Object[] arguments,
        @Nullable Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
	// The proxy object
    this.proxy = proxy;
    // The target object
    this.target = target; 
    // The target class
    this.targetClass = targetClass; 
    // Find the bridged method
    this.method = BridgeMethodResolver.findBridgedMethod(method); 
    // Adapt the arguments
    this.arguments = AopProxyUtils.adaptArgumentsIfNecessary(method, arguments); 
    // The list of interceptors and dynamic method matchers
    this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers; 
}
```

The `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed` method first checks whether the current interceptor index has reached the end of the interceptor chain. If so, it calls the join point method. Otherwise, it gets the next interceptor, or the interceptor and dynamic method matcher object, and does the dynamic method match. If the method matches, it calls the `invoke()` method of the interceptor. If the method does not match, it skips the current interceptor and calls the next interceptor in the chain. If what it got is an interceptor object, it calls the `invoke()` method of the interceptor directly. This method runs the interceptors or the target method one by one in the method call chain, so the method call chain runs in order.

```java
/**
 * Run the next interceptor in the interceptor chain, or the target method.
 * @return the result of the method call
 * @throws Throwable the exception that may be thrown
 */
@Override
@Nullable
public Object proceed() throws Throwable {
    // We start with an index of -1 and increment early.
    if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
       // If the current interceptor index has reached the end of the interceptor chain, call the join point method.
       return invokeJoinpoint();
    }

    // Get the next interceptor, or the interceptor and dynamic method matcher object
    Object interceptorOrInterceptionAdvice =
          this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
    if (interceptorOrInterceptionAdvice instanceof InterceptorAndDynamicMethodMatcher) {
       // If it is an interceptor and dynamic method matcher, do the dynamic method match
       InterceptorAndDynamicMethodMatcher dm =
             (InterceptorAndDynamicMethodMatcher) interceptorOrInterceptionAdvice;
       // Get the target class object. Use it if it is not null, otherwise use the declaring class of the method
       Class<?> targetClass = (this.targetClass != null ? this.targetClass : this.method.getDeclaringClass());
       if (dm.methodMatcher.matches(this.method, targetClass, this.arguments)) {
          // If the method matches, call the invoke method of the interceptor
          return dm.interceptor.invoke(this);
       }
       else {
          // The dynamic match failed, skip the current interceptor and call the next interceptor in the chain
          return proceed();
       }
    }
    else {
       // If it is an interceptor, call the invoke method of the interceptor directly
       return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
    }
}
```

The `org.springframework.aop.framework.ReflectiveMethodInvocation#invokeJoinpoint` method calls the join point by reflection.

```java
/**
 * Call the join point by reflection.
 * Subclasses can override this method to use a custom invocation.
 * @return the return value of the join point
 * @throws Throwable if calling the join point throws an exception
 */
@Nullable
protected Object invokeJoinpoint() throws Throwable {
    // Call the join point by reflection
    return AopUtils.invokeJoinpointUsingReflection(this.target, this.method, this.arguments);
}
```

The `org.springframework.aop.support.AopUtils#invokeJoinpointUsingReflection` method calls the target method by reflection, as part of an AOP method call. It takes the target object, the method to call, and the method arguments as input, and tries to call the method by reflection.

```java
/**
 * Call the given target method by reflection, as part of an AOP method call.
 * @param target the target object
 * @param method the method to call
 * @param args the arguments of the method
 * @return the result of the call, if any
 * @throws Throwable if the target method throws an exception
 * @throws org.springframework.aop.AopInvocationException if a reflection error occurs
 */
@Nullable
public static Object invokeJoinpointUsingReflection(@Nullable Object target, Method method, Object[] args)
       throws Throwable {

    // Call the method by reflection.
    try {
       // Set the method accessibility
       ReflectionUtils.makeAccessible(method);
       // Call the method
       return method.invoke(target, args);
    }
    catch (InvocationTargetException ex) {
       // The called method threw a checked exception.
       // We must rethrow it. The client will not see the interceptor.
       throw ex.getTargetException();
    }
    catch (IllegalArgumentException ex) {
       // If an argument error occurs, throw an AOP invocation exception
       throw new AopInvocationException("AOP configuration seems to be invalid: tried calling method [" +
					method + "] on target [" + target + "]", ex);
    }
    catch (IllegalAccessException ex) {
       // If the method cannot be accessed, throw an AOP invocation exception
       throw new AopInvocationException("Could not access method [" + method + "]", ex);
    }
}

```
