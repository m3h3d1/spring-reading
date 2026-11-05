## MethodInterceptor

- [MethodInterceptor](#methodinterceptor)
  - [1. Overview](#1-overview)
  - [2. Description](#2-description)
  - [3. Main Features](#3-main-features)
  - [4. Interface Source](#4-interface-source)
  - [5. Main Implementations](#5-main-implementations)
  - [6. Class Diagram](#6-class-diagram)
  - [7. Best Practices](#7-best-practices)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `MethodInterceptor` interface is a core interface in the Spring framework. It is used to implement aspect-oriented programming (AOP). By implementing it, you can intercept and enhance a method at key points, such as before and after the target method runs, or when an exception is thrown. This puts cross-cutting concerns in one place and makes the code easier to maintain and more flexible.

### 3. Main Features

1. **Method interception and enhancement** 

   + Intercepts and enhances a method at key points, such as before and after the target method runs, or when an exception is thrown. This keeps the code for cross-cutting concerns in one place.

### 4. Interface Source

The `MethodInterceptor` interface is the core interface for intercepting interface method calls and doing extra work before and after the target method. We need to implement its `invoke` method to define what the interceptor does. For example, you can write a tracing interceptor to track the calls of intercepted methods. In the `invoke` method, you usually call `proceed()` to continue to the target method, and handle the return value or exception when needed.

```java
/**
 * Interceptor that intercepts interface method calls and does extra work before and after the target method.
 * These interceptors are nested on top of the target method.
 *
 * <p>Users should implement the {@link #invoke(MethodInvocation)} method to change the original behavior. For example, the following class implements a tracing interceptor (it traces all calls of intercepted methods)
 *
 * @author Rod Johnson
 */
@FunctionalInterface
public interface MethodInterceptor extends Interceptor {

    /**
     * Implement this method to do extra work before and after the invocation. Polite implementations would certainly like to call {@link Joinpoint#proceed()}.
     * @param invocation the method invocation join point
     * @return the result of the call to {@link Joinpoint#proceed()}; might be intercepted by the interceptor
     * @throws Throwable if the interceptor or the target object throws an exception
     */
    @Nullable
    Object invoke(@Nonnull MethodInvocation invocation) throws Throwable;

}
```

### 5. Main Implementations

1. **MethodBeforeAdviceInterceptor** 

    + An interceptor that implements before advice. Before advice runs before the target method. It lets us insert extra logic before the method runs.

2. **AfterReturningAdviceInterceptor** 

    + An interceptor that implements after returning advice. After returning advice runs after the target method runs successfully and returns a result. It lets us insert extra logic after the method returns.

3. **ThrowsAdviceInterceptor** 

    + An interceptor that implements after throwing advice. After throwing advice runs after the target method throws an exception. It lets us insert extra logic after the method throws.

4. **AspectJAfterAdvice**

    + Implements after advice. It runs extra logic after the target method runs, and does not affect the result of the target method.

5. **AspectJAfterThrowingAdvice**

    + Implements after throwing advice. It runs extra logic after the target method throws an exception. It lets us handle the exception or do some clean-up.

6. **AspectJAroundAdvice**

    + Implements around advice. This is the most powerful type of advice. It lets us add extra logic before and after the target method, and fully control how the target method runs, including whether to run it and how to handle the return value.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class AbstractAspectJAdvice
class Advice {
<<Interface>>

}

class AspectJAfterAdvice
class AspectJAfterThrowingAdvice
class AspectJAroundAdvice
class Interceptor {
<<Interface>>

}
class MethodBeforeAdviceInterceptor
class AfterReturningAdviceInterceptor
class MethodInterceptor {
<<Interface>>

}
class ThrowsAdviceInterceptor

AbstractAspectJAdvice  ..>  Advice 
AfterReturningAdviceInterceptor  ..>  Advice 
AfterReturningAdviceInterceptor  ..>  MethodInterceptor 
AspectJAfterAdvice  -->  AbstractAspectJAdvice 
AspectJAfterAdvice  ..>  Advice 
AspectJAfterAdvice  ..>  MethodInterceptor 
AspectJAfterThrowingAdvice  -->  AbstractAspectJAdvice 
AspectJAfterThrowingAdvice  ..>  Advice 
AspectJAfterThrowingAdvice  ..>  MethodInterceptor 
AspectJAroundAdvice  -->  AbstractAspectJAdvice 
AspectJAroundAdvice  ..>  MethodInterceptor 
Interceptor  -->  Advice 
MethodBeforeAdviceInterceptor  ..>  Advice 
MethodBeforeAdviceInterceptor  ..>  MethodInterceptor 
MethodInterceptor  -->  Interceptor 
ThrowsAdviceInterceptor  ..>  Advice 
ThrowsAdviceInterceptor  ..>  MethodInterceptor 
~~~


### 7. Best Practices

This example creates a proxy factory `ProxyFactory` with the target object `MyService`. Then it uses `proxyFactory.addAdvice()` to add a custom method interceptor `MyMethodInterceptor` as advice. Next, it uses `proxyFactory.getProxy()` to get the proxy instance of `MyService`. Finally, it calls the method `doSomething()` on the proxy object.

```java
public class MethodInterceptorDemo {

    public static void main(String[] args) {
        // Create the proxy factory and the target object
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // Create the advice
        proxyFactory.addAdvice(new MyMethodInterceptor());
        // Get the proxy object
        MyService proxy = (MyService) proxyFactory.getProxy();
        // Call a method on the proxy object
        proxy.foo();
    }
}
```

The `MyMethodInterceptor` class implements method interception and enhancement. In `invoke()`, it first gets information about the called method, such as the method name, from the `MethodInvocation` object, and prints it before the method is called. Then it calls `invocation.proceed()` to run the original method and get the result. Finally, it returns the result.

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

`MyService` is a simple service class with a method named `foo()`. In this example, `MyService` is the target object, which is the object to be intercepted and enhanced.

```java
public class MyService {

    public void foo() {
        System.out.println("foo...");
    }
}
```

This is the result. When the `foo()` method of the `MyService` instance was called, the `MyMethodInterceptor` interceptor intercepted the call and added extra logic before and after the method ran.

```java
Before Method foo
foo...
After Method foo
```
