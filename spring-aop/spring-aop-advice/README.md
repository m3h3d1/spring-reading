## Advice

- [Advice](#advice)
  - [1. Overview](#1-overview)
  - [2. Description](#2-description)
  - [3. Main Features](#3-main-features)
  - [4. Interface Source](#4-interface-source)
  - [5. Sub-interfaces](#5-sub-interfaces)
  - [6. Class Diagram](#6-class-diagram)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `Advice` interface is a core interface of Spring AOP. It defines the action to run inside an aspect. It lets us add custom behavior at different join points, such as before a method runs, after it runs, or when it throws an exception. Classes that implement `Advice` can use a method interceptor (MethodInterceptor), before advice (BeforeAdvice), after returning advice (AfterReturningAdvice), or throws advice (ThrowsAdvice) to build different kinds of advice.

### 3. Main Features

1. **Define advice logic**

   + Lets us define actions that run at different join points, such as before the target method, after it, or when it throws an exception.

2. **Support different kinds of advice**

   + Classes that implement `Advice` can provide different kinds of advice logic, such as before advice, after advice, around advice, and throws advice.

3. **Work with a pointcut**

   + `Advice` is usually used with a pointcut (Pointcut) to decide at which join points the advice runs.

4. **Used in an Advisor**

   + `Advice` is usually part of an `Advisor`. Together with a pointcut, it forms the aspect logic.

### 4. Interface Source

The `Advice` interface is a marker interface in Spring AOP. It stands for all kinds of advice, such as interceptors. By implementing it, we can define actions that run before a method, after a method, or when an exception is thrown. This lets us change and control how the application behaves.

```java
/**
 * Tag interface for Advice. Implementations can be any type of advice, such as interceptors.
 * 
 * This interface defines advice. Advice can run before a method, after a method, or when an exception is thrown.
 * Classes that implement this interface can be any type of advice, such as interceptors.
 *
 * @author Rod Johnson
 * @version $Id: Advice.java,v 1.1 2004/03/19 17:02:16 johnsonr Exp $
 */
public interface Advice {

}
```

### 5. Sub-interfaces

1. **AfterAdvice (after advice)**

   + A marker interface that stands for after advice.

2. **AfterReturningAdvice (after returning advice)**

   + Runs custom logic after the target method finishes successfully and returns a result.

3. **BeforeAdvice (before advice)**

   + Runs custom logic before the target method runs.

4. **ConstructorInterceptor (constructor interceptor)**

   + Classes that implement this interface can run custom logic when the constructor of the target object is called.

5. **Interceptor (interceptor)**

   + A marker interface for general interceptors. It is usually used to wrap method calls.

6. **IntroductionInterceptor (introduction interceptor)**

   + Classes that implement this interface can add new methods and properties to the target object. This is how AOP introduction works.

7. **MethodBeforeAdvice (method before advice)**

   + Runs custom logic before the target method runs.

8. **MethodInterceptor (method interceptor)**

   + Classes that implement this interface can intercept the target method before it runs, after it runs, and when it throws an exception, and run custom logic.

9. **ThrowsAdvice (throws advice)**

   + Runs custom logic when the target method throws an exception.

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
class BeforeAdvice {
<<Interface>>

}
class ConstructorInterceptor {
<<Interface>>

}
class Interceptor {
<<Interface>>

}
class IntroductionInterceptor {
<<Interface>>

}
class MethodBeforeAdvice {
<<Interface>>

}
class MethodInterceptor {
<<Interface>>

}
class ThrowsAdvice {
<<Interface>>

}

AfterAdvice  -->  Advice 
AfterReturningAdvice  -->  AfterAdvice 
BeforeAdvice  -->  Advice 
ConstructorInterceptor  -->  Interceptor 
Interceptor  -->  Advice 
IntroductionInterceptor  -->  Advice 
IntroductionInterceptor  -->  MethodInterceptor 
MethodBeforeAdvice  -->  BeforeAdvice 
MethodInterceptor  -->  Interceptor 
ThrowsAdvice  -->  AfterAdvice 

~~~