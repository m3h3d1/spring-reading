## AfterReturningAdvice

- [AfterReturningAdvice](#afterreturningadvice)
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

The `AfterReturningAdvice` interface is a core interface in the Spring AOP framework. It runs custom logic after the target method finishes. By implementing its `afterReturning` method, you can do some work after the target method returns a result successfully.

### 3. Main Features

1. **Logging**

   + Record how the target method ran, such as the method name, argument values, and return value. This helps you follow the state of the application.

2. **Performance monitoring**
   + Measure how long the target method takes. Find performance bottlenecks and improve them.
   
3. **Cache handling**

   + After the method returns, cache the result. This speeds up later identical requests.

4. **Common handling**

   + Run common work that has nothing to do with business logic, such as releasing or cleaning up resources.

### 4. Interface Source

The `AfterReturningAdvice` interface is mainly used to run after advice when a method returns normally. After advice can see the return value but cannot change it. It is called only when the method returns normally, not when an exception is thrown. The interface has one method, `afterReturning`. It is a callback that runs after the method returns successfully. It receives the return value, the called method, the method arguments, and the target object of the call.

```java
/**
 * After returning advice is called only when a method returns normally. It is not called if an exception is thrown. Such advice can see the return value of the method but cannot change it.
 * 
 * Author: Rod Johnson
 * @see MethodBeforeAdvice
 * @see ThrowsAdvice
 */
public interface AfterReturningAdvice extends AfterAdvice {

    /**
     * Callback after the given method returns successfully.
     * @param returnValue the value returned by the method, if any
     * @param method the method that was called
     * @param args the arguments of the method
     * @param target the target object of the method call. May be {@code null}.
     * @throws Throwable if this object wants to abort the call. Any exception thrown is returned to the caller if the method signature allows it. Otherwise, the exception is wrapped in a runtime exception.
     */
    void afterReturning(@Nullable Object returnValue, Method method, Object[] args, @Nullable Object target) throws Throwable;

}
```

### 5. Main Implementations

1. **AspectJAfterReturningAdvice** 

   + Implements after returning advice in AspectJ style. It runs extra logic after the target method finishes successfully and returns a result.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class Advice {
<<Interface>>

}
class AfterAdvice {
<<Interface>>

}
class AfterReturningAdvice {
<<Interface>>

}
class AspectJAfterReturningAdvice

AfterAdvice  -->  Advice 
AfterReturningAdvice  -->  AfterAdvice 
AspectJAfterReturningAdvice  ..>  Advice 
AspectJAfterReturningAdvice  ..>  AfterAdvice 
AspectJAfterReturningAdvice  ..>  AfterReturningAdvice 
~~~

### 7. Best Practices

This example uses after returning advice (AfterReturningAdvice) in Spring AOP. First, it creates a proxy factory (ProxyFactory) with a target object (MyService). Then it creates an after returning advice (MyAfterReturningAdvice) and adds it to the proxy factory. Next, it gets the proxy object from the factory and calls a method on it.

```java
public class AfterReturningAdviceDemo {

    public static void main(String[] args) {
        // Create the proxy factory and the target object
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // Create the advice
        proxyFactory.addAdvice(new MyAfterReturningAdvice());
        // Get the proxy object
        MyService proxy = (MyService) proxyFactory.getProxy();
        // Call a method on the proxy object
        proxy.foo();
    }
}
```

`MyAfterReturningAdvice` is a class that implements the `AfterReturningAdvice` interface of the Spring AOP framework. In its `afterReturning` method, when the target method returns successfully, it prints the name of the target method.

```java
public class MyAfterReturningAdvice implements AfterReturningAdvice {
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println("After Method " + method.getName());
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

This is the result. The `foo()` method of the proxy ran successfully. After it finished, the after returning advice `MyAfterReturningAdvice` was triggered.

```java
foo...
After Method foo
```
