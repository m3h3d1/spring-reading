## MethodBeforeAdvice

- [MethodBeforeAdvice](#methodbeforeadvice)
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

The `MethodBeforeAdvice` interface is a core interface in Spring AOP. It lets us add custom logic before the target method runs, such as argument checks or logging. This is the before advice feature of aspect-oriented programming.

### 3. Main Features

1. **Before advice**

   + Lets us run extra logic before the target method runs.

2. **Separation of cross-cutting concerns**

   + Separates concerns that have nothing to do with business logic (such as logging, performance monitoring, and security checks) from the core business logic. This makes the code more modular and easier to maintain.

3. **Argument validation**

   + Checks the method arguments before the target method runs, to make sure they are valid.

4. **Permission control**

   + Checks permissions before the method runs, so that only users with enough permission can run it.

### 4. Interface Source

The `MethodBeforeAdvice` interface runs advice before a method is called. The advice method `before` receives the called method, the method arguments, and the target object of the call. It can throw a Throwable to stop the method call. Such advice cannot stop the method call from going on, unless it throws a Throwable.

```java
/**
 * Advice called before a method is invoked. Such advice cannot prevent the method call from proceeding, unless it throws a Throwable.
 *
 * @author Rod Johnson
 * @see AfterReturningAdvice
 * @see ThrowsAdvice
 */
public interface MethodBeforeAdvice extends BeforeAdvice {

	/**
	 * Callback before the given method is invoked.
	 * @param method the method that is being called
	 * @param args the arguments of the method
	 * @param target the target object of the method call. May be {@code null}.
	 * @throws Throwable if this object wants to abort the call. Any exception thrown is returned to the caller if the method signature allows it. Otherwise, the exception is wrapped in a runtime exception.
	 */
	void before(Method method, Object[] args, @Nullable Object target) throws Throwable;

}
```

### 5. Main Implementations

1. **AspectJMethodBeforeAdvice**

   - Implements before advice in AspectJ style. It runs extra logic before the target method runs.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class Advice {
<<Interface>>

}
class AspectJMethodBeforeAdvice
class BeforeAdvice {
<<Interface>>

}
class MethodBeforeAdvice {
<<Interface>>

}

AspectJMethodBeforeAdvice  ..>  Advice 
AspectJMethodBeforeAdvice  ..>  MethodBeforeAdvice 
BeforeAdvice  -->  Advice 
MethodBeforeAdvice  -->  BeforeAdvice 
~~~

### 7. Best Practices

This example uses the `MethodBeforeAdvice` interface. First, it creates a proxy factory and a target object. Then it creates the custom before advice `MyMethodBeforeAdvice` and adds it to the proxy factory. Next, it gets the proxy object from the factory and calls a method on it. Before the method runs, the before advice is triggered and runs the custom logic.

```java
public class MethodBeforeAdviceDemo {

    public static void main(String[] args) {
        // Create the proxy factory and the target object
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // Create the advice
        proxyFactory.addAdvice(new MyMethodBeforeAdvice());
        // Get the proxy object
        MyService proxy = (MyService) proxyFactory.getProxy();
        // Call a method on the proxy object
        proxy.doSomething();
    }
}
```

The `MyMethodBeforeAdvice` class implements the `MethodBeforeAdvice` interface. In its `before` method, it prints information before the target method is called, including the method name. This class can be used as before advice in Spring AOP to run extra logic before the target method runs.

```java
public class MyMethodBeforeAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("Before Method " + method.getName());
    }
}
```

`MyService` is a simple service class with a method named `doSomething()`. In this example, `MyService` is the target object, which is the object to be intercepted and enhanced.

```java
public class MyService {

    public void foo() {
        System.out.println("foo...");
    }
}
```

This is the result. Before the target method `foo` was called, the before advice in `MyMethodBeforeAdvice` was triggered, and it printed the matching message.

```java
Before Method foo
foo...
```
