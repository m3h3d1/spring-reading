## Advisor

- [Advisor](#advisor)
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

The `Advisor` interface is a key interface in the Spring framework. It combines a pointcut (Pointcut) and advice (Advice), so that AOP (aspect-oriented programming) can define when, where, and how to apply cross-cutting concerns.

### 3. Main Features

1. **Combine a pointcut and advice**

   + The Advisor interface lets you combine a pointcut (Pointcut) and advice (Advice). The pointcut decides when the advice should be applied. The advice defines the code that runs at the join point.


### 4. Interface Source

The `Advisor` interface is a basic interface in the Spring framework. It holds AOP advice (the action taken at a join point) and a filter that decides whether the advice applies (such as a pointcut). The interface defines the method `getAdvice()` to get the advice part, and the method `isPerInstance()` to tell whether the advice is tied to a specific instance. It also provides a constant `EMPTY_ADVICE`, used as a placeholder when no proper advice is configured. In Spring AOP, the Advisor interface supports different types of advice, such as interceptors, before advice, and throws advice. Not all advice has to be implemented with interception.

```java
/**
 * Base interface that holds an AOP <b>advice</b> (the action taken at a join point) and a filter that decides whether the advice applies (such as a pointcut).
 * <i>This interface is not for Spring users. It exists to provide common ground when supporting different types of advice.</i>
 *
 * <p>Spring AOP is based on <b>around advice</b> delivered through method interception, in line with the AOP Alliance interception API.
 * The Advisor interface allows support for different types of advice, such as <b>before</b> and <b>after</b> advice, which do not have to be implemented with interception.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface Advisor {

	/**
	 * Common placeholder for an empty {@code Advice} returned from {@link #getAdvice()} if no proper advice has been configured yet.
	 * @since 5.0
	 */
	Advice EMPTY_ADVICE = new Advice() {};


	/**
	 * Return the advice part of this aspect. An advice may be an interceptor, a before advice, a throws advice, and so on.
	 * @return the advice that should apply if the pointcut matches
	 * @see org.aopalliance.intercept.MethodInterceptor
	 * @see BeforeAdvice
	 * @see ThrowsAdvice
	 * @see AfterReturningAdvice
	 */
	Advice getAdvice();

	/**
	 * Return whether this advice is tied to a particular instance (for example, to create a mixin), or shared with all instances of the advised class obtained from the same Spring bean factory.
	 * <p><b>Note that this method is not currently used by the framework.</b>
	 * Typical Advisor implementations always return {@code true}.
	 * Use singleton/prototype bean definitions or appropriate programmatic proxy creation to make sure the Advisor has the correct lifecycle model.
	 * @return whether this advice is tied to a particular target instance
	 */
	boolean isPerInstance();

}
```

The `PointcutAdvisor` interface is the super interface of all pointcut-driven Advisors. It covers almost all Advisors, except introduction Advisors, because method-level matching does not apply to introduction Advisors. The interface stands for an Advisor driven by a pointcut. The pointcut that drives the Advisor is obtained with `getPointcut()`. PointcutAdvisor is usually used for pointcut-based aspects. The pointcut decides at which join points the advice logic applies.

```java
/**
 * Super interface of all pointcut-driven Advisors.
 * This covers almost all Advisors, except introduction Advisors,
 * because method-level matching does not apply to introduction Advisors.
 *
 * This interface is a sub-interface of Advisor. It stands for an Advisor driven by a pointcut.
 * A pointcut-driven Advisor is usually used for pointcut-based aspects. The pointcut decides at which join points the advice logic applies.
 * 
 * Author: Rod Johnson
 */
public interface PointcutAdvisor extends Advisor {

	/**
	 * Get the pointcut that drives this Advisor.
	 */
	Pointcut getPointcut();

}
```

### 5. Main Implementations

1. **RegexpMethodPointcutAdvisor**

   - A pointcut that matches method names with regular expressions. With a regular expression, join points can be matched by method name pattern, and the advice is applied to the matched join points. This gives aspect logic based on method name patterns.
2. **AspectJExpressionPointcutAdvisor**

   - A pointcut defined with an AspectJ expression. With AspectJ syntax, aspects can be defined more flexibly to match join points and apply the advice to them. This gives more complex aspect logic.
3. **NameMatchMethodPointcutAdvisor**

   - A pointcut defined by method name pattern matching. With method name patterns, join points are easy to match, and the advice is applied to the matched join points. This gives aspect logic based on method name patterns.
4. **DefaultPointcutAdvisor**

   - A general pointcut Advisor that combines a pointcut and advice. It lets you use any type of advice with any type of pointcut, and applies the advice to the matched join points. This helps manage cross-cutting concerns.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class Advisor {
<<Interface>>

}
class AspectJPointcutAdvisor
class DefaultPointcutAdvisor
class NameMatchMethodPointcutAdvisor
class PointcutAdvisor {
<<Interface>>

}
class RegexpMethodPointcutAdvisor

AspectJPointcutAdvisor  ..>  PointcutAdvisor 
DefaultPointcutAdvisor  ..>  PointcutAdvisor 
NameMatchMethodPointcutAdvisor  ..>  PointcutAdvisor 
PointcutAdvisor  -->  Advisor 
RegexpMethodPointcutAdvisor  ..>  PointcutAdvisor 

~~~

### 7. Best Practices

This example uses an Advisor to create a proxy and apply aspect logic. First, it creates a proxy factory `ProxyFactory` and passes the target object `MyService` to it. Then, with `proxyFactory.addAdvisor(new MyCustomAdvisor())`, it adds a custom Advisor that holds the pointcut and advice definitions. Next, it gets the proxy object of `MyService` with `proxyFactory.getProxy()`. Finally, it calls the `foo()` method on the proxy, which triggers the aspect logic.

```java
public class AdvisorDemo {

    public static void main(String[] args) {
        // Create the proxy factory
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // Add the Advisor
        proxyFactory.addAdvisor(new MyCustomAdvisor());
        // Get the proxy object
        MyService proxy = (MyService) proxyFactory.getProxy();
        // Call the method
        proxy.foo();
    }
}
```

The `MyCustomAdvisor` class is a custom Advisor. It implements the `PointcutAdvisor` interface and applies advice to methods that have a specific annotation. In this class, we define an advice object `advice` and a pointcut object `pointcut`. The pointcut matches methods that have the custom annotation `MyCustomAnnotation`. By implementing `getPointcut()` and `getAdvice()`, we give the pointcut and the advice logic.

```java

/**
 * Custom Advisor that applies advice to methods with a specific annotation.
 */
public class MyCustomAdvisor implements PointcutAdvisor {

    /**
     * Advice object
     */
    private final Advice advice = new MyAdvice();

    /**
     * Pointcut object that matches methods with the custom annotation
     */
    private final Pointcut pointcut = new AnnotationMatchingPointcut(null, MyCustomAnnotation.class);

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }
}
```

The `MyAdvice` class is an advice class. It implements the `MethodInterceptor` interface and adds extra logic before and after a method runs. In the `invoke` method, we first print the name of the method being called. Then we call `invocation.proceed()` to run the original method and get its result. Finally, after the method runs, we print the method name again.

```java
public class MyAdvice implements MethodInterceptor {

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

`MyCustomAnnotation` is a custom annotation. It is usually used to mark methods that need special handling.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyCustomAnnotation {
}
```

The `MyCustomAnnotation` class contains a method named `foo`. The method is marked with the `@MyCustomAnnotation` annotation, which shows that it needs special handling.

```java
public class MyService {

    @MyCustomAnnotation
    public void foo() {
        System.out.println("foo...");
    }
}
```

This is the result. The aspect logic was applied to the method that has the specific annotation.

```java
Before Method foo
foo...
After Method foo
```
