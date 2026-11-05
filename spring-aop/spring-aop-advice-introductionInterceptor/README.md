## IntroductionInterceptor

- [IntroductionInterceptor](#introductioninterceptor)
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

The `IntroductionInterceptor` interface is a key interface in Spring AOP. It implements the introduction feature. It lets you add new methods or properties to a target object at runtime without changing the code of the target class. This supports cross-cutting concerns such as logging and transaction management.

### 3. Main Features

1. **Introduce new interfaces or classes**

    + Through the `implementsInterface()` method, new interfaces or classes are introduced to the target object before its methods are called. This gives the target object extra features or properties.

### 4. Interface Source

The `IntroductionInterceptor` interface is a sub-interface of the AOP Alliance `MethodInterceptor`. It lets an interceptor implement extra interfaces, and lets the proxy that uses this interceptor expose them. The interface shows the idea of introduction in AOP. With introduction, extra features can be added to a target object, like a mixin. This allows you to build composite objects and get a result similar to multiple inheritance in Java.

```java
/**
 * Sub-interface of the AOP Alliance MethodInterceptor. It lets an interceptor implement extra interfaces, and lets the proxy that uses this interceptor expose them. This is a basic AOP concept called <b>introduction</b>.
 *
 * <p>Introductions are usually <b>mixins</b>. They allow you to build composite objects that achieve the goal of multiple inheritance.
 *
 * @author Rod Johnson
 * @see DynamicIntroductionAdvice
 */
public interface IntroductionInterceptor extends MethodInterceptor, DynamicIntroductionAdvice {

}
```

### 5. Main Implementations

1. **DelegatingIntroductionInterceptor**

   + `DelegatingIntroductionInterceptor` is the general introduction interceptor provided by Spring AOP. It lets us define our own introduction logic and apply it to a target object when needed. With configuration, new methods or properties can be added to a target object at runtime without changing the target class. This makes the code easier to maintain and more flexible.

2. **DelegatePerTargetObjectIntroductionInterceptor**

   + `DelegatePerTargetObjectIntroductionInterceptor` is a sub-class of `DelegatingIntroductionInterceptor`. It creates a separate introduction delegate for each target object. This means each target object can have its own introduction logic, not affected by other target objects. This is useful when you need to add different features or properties to different target objects at runtime. It gives you more control.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class Advice {
<<Interface>>

}
class DelegatePerTargetObjectIntroductionInterceptor
class DelegatingIntroductionInterceptor
class DynamicIntroductionAdvice {
<<Interface>>

}
class Interceptor {
<<Interface>>

}
class IntroductionInterceptor {
<<Interface>>

}
class MethodInterceptor {
<<Interface>>

}

DelegatePerTargetObjectIntroductionInterceptor  ..>  IntroductionInterceptor 
DelegatingIntroductionInterceptor  ..>  IntroductionInterceptor 
DynamicIntroductionAdvice  -->  Advice 
Interceptor  -->  Advice 
IntroductionInterceptor  -->  DynamicIntroductionAdvice 
IntroductionInterceptor  -->  MethodInterceptor 
MethodInterceptor  -->  Interceptor 
~~~

### 7. Best Practices

This example uses the introduction feature in Spring AOP. It creates a proxy factory and forces the use of a CGLIB proxy. Then it adds an advisor that applies the custom introduction advice (`MyMonitoringIntroductionAdvice`) to the target object (the `MyService` class). As a result, the target object implements the `MyMonitoringCapable` interface. Finally, it calls methods on the proxy and turns on monitoring when needed. This shows how to add new features to a target object at runtime.

```java
public class IntroductionInterceptorDemo {

    public static void main(String[] args) {
        // Create the proxy factory and the target object
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // Force the use of CGLIB
        proxyFactory.setProxyTargetClass(true);
        // Create the advice
        proxyFactory.addAdvisor(new DefaultIntroductionAdvisor(new MyMonitoringIntroductionAdvice(), MyMonitoringCapable.class));
        // Create the proxy object
        MyService proxy = (MyService) proxyFactory.getProxy();
        // Call a method on the proxy object
        proxy.foo();
        // Start monitoring
        ((MyMonitoringCapable) proxy).toggleMonitoring();
        // Call a method on the proxy object again
        proxy.foo();
    }
}
```

`MyMonitoringIntroductionAdvice` is a custom introduction advice class. It extends `DelegatingIntroductionInterceptor` and implements the `MyMonitoringCapable` interface. It has an `active` property that shows whether monitoring is on, and a method `toggleMonitoring()` that turns monitoring on. When a monitored method is called and monitoring is on, the class prints log messages, including the execution time. By overriding `doProceed()`, it can add custom logic before and after the method runs. This is how the monitoring feature is introduced at runtime.

```java
public class MyMonitoringIntroductionAdvice extends DelegatingIntroductionInterceptor implements MyMonitoringCapable {

    private boolean active = false;

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void toggleMonitoring() {
        setActive(true);
    }

    // When a monitored method is called and monitoring is active, print logs
    @Override
    protected Object doProceed(MethodInvocation mi) throws Throwable {
        if (this.active) {
            System.out.println("[Start monitoring " + mi.getMethod().getName() + "]");
            long startTime = System.currentTimeMillis();
            Object result = super.doProceed(mi);
            long endTime = System.currentTimeMillis();
            System.out.println("[End monitoring " + mi.getMethod().getName() + "] Time taken: " + (endTime - startTime) + " ms");
            return result;
        }
        return super.doProceed(mi);
    }
}
```

The `MyMonitoringCapable` interface defines a `toggleMonitoring()` method. It turns the monitoring feature on or off.

```java
public interface MyMonitoringCapable {
    void toggleMonitoring();
}
```

`MyService` is a simple service class with a method named `foo()`. In this example, `MyService` is the target object, which is the object to be intercepted and enhanced.

```java
public class MyService {

    public void foo() {
        System.out.println("foo...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
```

This result shows that the introduction advice enhanced the target method. Extra logic was added before and after the target method at runtime.

```java
foo...
[Start monitoring foo]
foo...
[End monitoring foo] Time taken: 1008 ms
```
