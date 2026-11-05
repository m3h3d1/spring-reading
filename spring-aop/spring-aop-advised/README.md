## Advised

- [Advised](#advised)
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

The `Advised` interface is one of the core interfaces in Spring AOP. It stands for an object that Spring AOP can advise. It gives common methods to manage that object, such as checking whether it is frozen, whether it proxies the target class, getting the proxied interfaces, and adding, removing, and getting advice. With these methods you can manage the advice of an advised object at runtime.

### 3. Main Features

1. **Check frozen state (isFrozen)**

   + Checks whether the advised object is frozen, that is, whether its AOP configuration can still be changed.

2. **Check proxy type (isProxyTargetClass)**

   + Checks whether the target class is proxied instead of the target interfaces.

3. **Get proxied interfaces (getProxiedInterfaces)**

   + Gets the array of interfaces implemented by the proxied object.

4. **Check whether an interface is proxied (isInterfaceProxied)**

   + Checks whether the given interface is proxied.

5. **Add advice (addAdvice)**

   + Adds advice to the advised object to enhance the target method.

6. **Add advice at a position (addAdvice)**

   + Adds advice at a given position to control the order in which advice runs.

7. **Get all advice (getAdvices)**

   + Gets all the advice added to the advised object.

8. **Remove advice (removeAdvice)**

   + Removes the given advice to change the advice setup at runtime.

### 4. Interface Source

The `Advised` interface defines the structure and behavior of the proxy factory configuration. It has many methods to manage the configuration of an AOP proxy, including interceptors, advisors, and proxied interfaces. With this interface, you can read and change the AOP proxy configuration, such as whether it is frozen, whether it proxies the target class, which interfaces are proxied, and adding and removing advisors.

```java
/**
 * Interface to be implemented by classes that hold the configuration of an AOP proxy factory. This configuration includes the interceptors and other advice, the advisors, and the proxied interfaces.
 *
 * Any AOP proxy obtained from Spring can be cast to this interface, so that its AOP advice can be manipulated.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 13.03.2003
 * @see org.springframework.aop.framework.AdvisedSupport
 */
public interface Advised extends TargetClassAware {

	/**
	 * Return whether the Advised configuration is frozen. If it is frozen, advice changes cannot be made.
	 */
	boolean isFrozen();

	/**
	 * Are the full target classes proxied, instead of only the specified interfaces?
	 */
	boolean isProxyTargetClass();

	/**
	 * Return the interfaces proxied by the AOP proxy.
	 * Does not include the target class, which may also be proxied.
	 */
	Class<?>[] getProxiedInterfaces();

	/**
	 * Determine whether the given interface is proxied.
	 * @param intf the interface to check
	 */
	boolean isInterfaceProxied(Class<?> intf);

	/**
	 * Change the TargetSource used by this Advised object.
	 * Only works if the configuration is not frozen.
	 * @param targetSource the new TargetSource to use
	 */
	void setTargetSource(TargetSource targetSource);

	/**
	 * Return the TargetSource used by this Advised object.
	 */
	TargetSource getTargetSource();

	/**
	 * Set whether the proxy should be exposed by the AOP framework as a {@link ThreadLocal} for retrieval through the {@link AopContext} class.
	 * Exposing the proxy may be needed if an advised object needs to call a method on itself and have the advice applied. Otherwise, if an advised object calls a method on {@code this}, no advice is applied.
	 * Defaults to {@code false}, for optimal performance.
	 */
	void setExposeProxy(boolean exposeProxy);

	/**
	 * Return whether the factory should expose the proxy as a {@link ThreadLocal}.
	 * Exposing the proxy may be needed if an advised object needs to call a method on itself and have the advice applied. Otherwise, if an advised object calls a method on {@code this}, no advice is applied.
	 * Getting the proxy is similar to an EJB calling {@code getEJBObject()}.
	 * @see AopContext
	 */
	boolean isExposeProxy();

	/**
	 * Set whether this proxy configuration is pre-filtered, so that it contains only the applicable advisors (matching this proxy's target class).
	 * Defaults to "false". Set it to "true" if the advisors have already been pre-filtered, so that the ClassFilter check can be skipped when building the actual proxy invocation chain.
	 * @see org.springframework.aop.ClassFilter
	 */
	void setPreFiltered(boolean preFiltered);

	/**
	 * Return whether this proxy configuration is pre-filtered, so that it contains only the applicable advisors (matching this proxy's target class).
	 */
	boolean isPreFiltered();

	/**
	 * Return the advisors applied to this proxy.
	 * @return a list of advisors applied to this proxy (never {@code null})
	 */
	Advisor[] getAdvisors();

	/**
	 * Return the number of advisors applied to this proxy.
	 * The default implementation delegates to {@code getAdvisors().length}.
	 * @since 5.3.1
	 */
	default int getAdvisorCount() {
		return getAdvisors().length;
	}

	/**
	 * Add an advisor at the end of the advisor chain.
	 * The advisor may be an {@link org.springframework.aop.IntroductionAdvisor},
	 * with the new interfaces made available when the proxy is next obtained from the relevant factory.
	 * @param advisor the advisor to add to the end of the chain
	 * @throws AopConfigException if the advisor is invalid
	 */
	void addAdvisor(Advisor advisor) throws AopConfigException;

	/**
	 * Add an advisor at the given position in the chain.
	 * @param advisor the advisor to add at the given position in the chain
	 * @param pos position in chain (0 is head). Must be valid.
	 * @throws AopConfigException if the advisor is invalid
	 */
	void addAdvisor(int pos, Advisor advisor) throws AopConfigException;

	/**
	 * Remove the given advisor.
	 * @param advisor the advisor to remove
	 * @return {@code true} if the advisor was removed; {@code false} if the advisor was not found and hence could not be removed
	 */
	boolean removeAdvisor(Advisor advisor);

	/**
	 * Remove the advisor at the given index.
	 * @param index the index of the advisor to remove
	 * @throws AopConfigException if the index is invalid
	 */
	void removeAdvisor(int index) throws AopConfigException;

	/**
	 * Return the index (from 0) of the given advisor,
	 * or -1 if no such advisor applies to this proxy.
	 * The return value of this method can be used to index into the advisors array.
	 * @param advisor the advisor to search for
	 * @return the 0-based index of this advisor, or -1 if there is no such advisor
	 */
	int indexOf(Advisor advisor);

	/**
	 * Replace the given advisor.
	 * Note: if the advisor is an {@link org.springframework.aop.IntroductionAdvisor}
	 * and the replacement is not or implements different interfaces, the proxy needs to be obtained again. Otherwise the old interfaces will not be supported and the new interfaces will not be implemented.
	 * @param a the advisor to replace
	 * @param b the new advisor to replace it with
	 * @return whether it was replaced. If the advisor is not found in the advisor list, this method returns {@code false} and does nothing.
	 * @throws AopConfigException if the advisor is invalid
	 */
	boolean replaceAdvisor(Advisor a, Advisor b) throws AopConfigException;

	/**
	 * Add the given AOP Alliance advice to the end of the advice (interceptor) chain.
	 * It is wrapped in a DefaultPointcutAdvisor that always applies, and is returned from the {@code getAdvisors()} method in this wrapped form.
	 * Note that the given advice applies to all invocations on the proxy, even to the {@code toString()} method! Use an appropriate advice implementation or specify an appropriate pointcut to apply to a narrower set of methods.
	 * @param advice the advice to add to the end of the chain
	 * @throws AopConfigException if the advice is invalid
	 * @see #addAdvice(int, Advice)
	 * @see org.springframework.aop.support.DefaultPointcutAdvisor
	 */
	void addAdvice(Advice advice) throws AopConfigException;

	/**
	 * Add the given AOP Alliance advice at the given position in the advice chain.
	 * It is wrapped in a {@link org.springframework.aop.support.DefaultPointcutAdvisor},
	 * and is returned from the {@link #getAdvisors()} method in this wrapped form.
	 * Note that the given advice applies to all invocations on the proxy, even to the {@code toString()} method! Use an appropriate advice implementation or specify an appropriate pointcut to apply to a narrower set of methods.
	 * @param pos index from 0 (the head)
	 * @param advice the advice to add at the given position in the advice chain
	 * @throws AopConfigException if the advice is invalid
	 */
	void addAdvice(int pos, Advice advice) throws AopConfigException;

	/**
	 * Remove the advisor that contains the given advice.
	 * @param advice the advice to remove
	 * @return {@code true} if the advice was found and removed; {@code false} if the advice was not found
	 */
	boolean removeAdvice(Advice advice);

	/**
	 * Return the index (from 0) of the given AOP Alliance advice,
	 * or -1 if no such advice is an advice for this proxy.
	 * The return value of this method can be used to index into the advisors array.
	 * @param advice the AOP Alliance advice to search for
	 * @return the 0-based index of this advice, or -1 if there is no such advice
	 */
	int indexOf(Advice advice);

	/**
	 * As {@code toString()} is usually delegated to the target, this method returns an equivalent description of the AOP proxy.
	 * @return a string description of the proxy configuration
	 */
	String toProxyConfigString();

}
```

### 5. Main Implementations

+ **AdvisedSupport**

  + The core class that manages AOP proxy configuration. It holds key properties such as advisors, the target object, and the target source. It lets you configure how an AOP proxy is created, and it has methods to handle the frozen state of the configuration, proxy exposure, and more.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class Advised {
<<Interface>>

}
class AdvisedSupport
class TargetClassAware {
<<Interface>>

}

Advised  -->  TargetClassAware 
AdvisedSupport  ..>  Advised 
~~~

### 7. Best Practices

This example uses the `AdvisedSupport` class to configure a proxy. It sets the target object, interfaces, advice, advisors, whether to expose the proxy, whether to use a CGLIB proxy, and freezes the object. Then it prints the proxy configuration with `toProxyConfigString()`.

```java
public class AdvisedDemo {

    public static void main(String[] args) {
        // Create an AdvisedSupport object
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // Set the target object
        advisedSupport.setTarget(new MyServiceImpl());
        // Set the interfaces implemented by the target object
        advisedSupport.setInterfaces(MyService.class);
        // Add advice
        advisedSupport.addAdvice(new Advice() {});
        // Add an advisor
        advisedSupport.addAdvisor(new DefaultPointcutAdvisor());
        // Expose the proxy object
        advisedSupport.setExposeProxy(true);
        // Set CGLIB proxy
        advisedSupport.setProxyTargetClass(true);
        // Freeze the object
        advisedSupport.setFrozen(true);
        // Print
        System.out.println("AdvisedSupport = " + advisedSupport.toProxyConfigString());
    }
}
```

Defines a `MyService` interface.

```java
public interface MyService {
    void foo();
}
```

The `MyServiceImpl` class, which implements the `MyService` interface.

```java
public class MyServiceImpl implements MyService {

    @Override
    public void foo() {
        System.out.println("foo...");
    }
}
```

This is the result. It shows the configuration of the `AdvisedSupport` object, including the proxied interfaces, advisors, target object, proxy type, and other settings.

```java
AdvisedSupport = org.springframework.aop.framework.AdvisedSupport: 1 interfaces [com.xcs.spring.MyService]; 2 advisors [org.springframework.aop.support.DefaultPointcutAdvisor: pointcut [Pointcut.TRUE]; advice [com.xcs.spring.AdvisedDemo$1@32d992b2], org.springframework.aop.support.DefaultPointcutAdvisor: pointcut [Pointcut.TRUE]; advice [org.springframework.aop.Advisor$1@215be6bb]]; targetSource [SingletonTargetSource for target object [com.xcs.spring.MyServiceImpl@5d5eef3d]]; proxyTargetClass=true; optimize=false; opaque=false; exposeProxy=true; frozen=true
```
