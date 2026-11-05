## ThrowsAdvice

- [ThrowsAdvice](#throwsadvice)
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

The `ThrowsAdvice` interface is a type of advice in Spring AOP. It runs extra logic when a method throws an exception. A class that implements this interface can catch the exceptions thrown by a method and run custom exception handling.

### 3. Main Features

1. **Catch exceptions** 

   + Lets you catch exceptions thrown by the target method.

2. **Run extra logic** 

   + Provides the `afterThrowing()` method. Implementing classes can run extra logic when a method throws an exception, such as logging or sending notifications.

3. **Pass arguments** 

   + The `afterThrowing()` method receives the method object, the argument array, the target object, and the exception. This makes it easy to get related information while handling the exception.

4. **Custom handling** 

   + You can customize exception handling to fit business needs. This makes the application more flexible and robust.

### 4. Interface Source

The `ThrowsAdvice` interface is a tag interface for throws advice. It has no methods, because the methods are called by reflection. An implementing class must implement the `afterThrowing()` method to handle exceptions thrown by a method. The method has the form `void afterThrowing([Method, args, target], ThrowableSubclass)`. The first three arguments are optional. They give more information about the join point.

```java
/**
 * Tag interface for throws advice.
 *
 * <p>This interface has no methods, because the methods are called by reflection. Implementing classes must implement methods of the following form:
 *
 * <pre class="code">void afterThrowing([Method, args, target], ThrowableSubclass);</pre>
 *
 * <p>Here are some valid methods:
 *
 * <pre class="code">public void afterThrowing(Exception ex)</pre>
 * <pre class="code">public void afterThrowing(RemoteException)</pre>
 * <pre class="code">public void afterThrowing(Method method, Object[] args, Object target, Exception ex)</pre>
 * <pre class="code">public void afterThrowing(Method method, Object[] args, Object target, ServletException ex)</pre>
 *
 * The first three arguments are optional. They are only useful if we need more information about the join point, as in AspectJ <b>after-throwing</b> advice.
 *
 * <p><b>Note:</b> If a throws-advice method throws an exception itself, it overrides the original exception (that is, it changes the exception to the user).
 * The overriding exception is usually a RuntimeException; this is compatible with any method signature. However, if a throws-advice method throws a checked exception,
 * it must match the declared exceptions of the target method, and it is coupled to the specific target method signature in some way.
 * <b>Do not throw an undeclared checked exception that is not compatible with the target method signature!</b>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see AfterReturningAdvice
 * @see MethodBeforeAdvice
 */
public interface ThrowsAdvice extends AfterAdvice {

}
```

### 5. Main Implementations

1. **ThrowsAdviceInterceptor**
   + Intercepts exceptions thrown by a method and triggers the matching throws advice (`ThrowsAdvice`). It catches the exception thrown while the method runs and calls the related throws advice to handle it.

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
class ThrowsAdvice {
<<Interface>>

}

AfterAdvice  -->  Advice 
ThrowsAdvice  -->  AfterAdvice 
~~~

### 7. Best Practices

This example uses the `ThrowsAdvice` interface to handle exceptions thrown by a method. It creates a proxy factory and passes the target object (`MyService`) and the throws advice (`MyThrowsAdvice`) to it. Then it gets the proxy object from the factory and calls the method `foo()` on it.

```java
public class ThrowsAdviceDemo {

    public static void main(String[] args) {
        // Create the proxy factory and the target object
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // Create the advice
        proxyFactory.addAdvice(new MyThrowsAdvice());
        // Get the proxy object
        MyService proxy = (MyService) proxyFactory.getProxy();
        // Call a method on the proxy object
        proxy.foo();
    }
}
```

The `MyThrowsAdvice` class implements the `ThrowsAdvice` interface and defines the `afterThrowing()` method to handle exceptions thrown by a method. When the target method throws an exception, this method is called and prints the exception information.

```java
public class MyThrowsAdvice implements ThrowsAdvice {
    public void afterThrowing(Exception ex) throws Throwable {
        System.out.println("Exception thrown: " + ex.getMessage());
    }
}
```

The `MyService` class has a method named `foo()`. It does some work and throws an exception on purpose (by dividing by zero).

```java
public class MyService {

    public void foo() {
        System.out.println("foo...");
        int i = 1 / 0;
    }
}
```

This is the result. The `foo()` method of the `MyService` class was called. A divide-by-zero error happened inside it, so a `java.lang.ArithmeticException: / by zero` exception was thrown.

```java
Doing something exception...
Exception thrown: / by zero
Exception in thread "main" java.lang.ArithmeticException: / by zero
	at com.xcs.spring.MyService.doSomethingException(MyService.java:7)
	at com.xcs.spring.MyService$$FastClassBySpringCGLIB$$c768e93b.invoke(<generated>)
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:779)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.framework.adapter.ThrowsAdviceInterceptor.invoke(ThrowsAdviceInterceptor.java:113)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:692)
	at com.xcs.spring.MyService$$EnhancerBySpringCGLIB$$abe9fbc2.doSomethingException(<generated>)
	at com.xcs.spring.ThrowsAdviceDemo.main(ThrowsAdviceDemo.java:15)
```
