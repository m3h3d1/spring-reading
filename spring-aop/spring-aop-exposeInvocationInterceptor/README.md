## ExposeInvocationInterceptor

- [ExposeInvocationInterceptor](#exposeinvocationinterceptor)
	- [1. Overview](#1-overview)
	- [2. Description](#2-description)
	- [3. Main Features](#3-main-features)
	- [4. Class Source](#4-class-source)
	- [5. Best Practices](#5-best-practices)
    - [6. Sequence Diagram](#6-sequence-diagram)
    - [7. Source Code Analysis](#7-source-code-analysis)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

`ExposeInvocationInterceptor` is an interceptor class in Spring AOP. Its main job is to expose the context of the current method call in the AOP call chain. It exposes the `MethodInvocation` object, so other interceptors or aspects can read and use the details of the method call.

### 3. Main Features

1. **Expose the context of the current method call**

   + By exposing the `MethodInvocation` object, it lets other interceptors or aspects read the details of the current method call, such as the target object, the method, and the arguments.

2. **Provide the `currentInvocation()` method**

   + Interceptors or aspects can call `currentInvocation()` to get the `MethodInvocation` object of the current method call, and so get the context of the call.

3. **Support the AOP call chain**

   + As an interceptor of Spring AOP, `ExposeInvocationInterceptor` can be added to the AOP proxy chain. This makes sure the `MethodInvocation` object is exposed at the start of the call chain, so later interceptors or aspects can use it.

### 4. Class Source

 The `ExposeInvocationInterceptor` interceptor exposes the current method call context as a thread-local object. It lets you get the details of a method call in Spring AOP, such as the target object, the method, and the arguments. This interceptor is usually the first one in the AOP chain, so other interceptors or aspects can access the full context of the method call.

```java
/**
 * Interceptor that exposes the current {@link org.aopalliance.intercept.MethodInvocation} as a thread-local object.
 * Use this interceptor only when necessary; for example, when a pointcut (such as an AspectJ expression pointcut) needs to know the full invocation context.
 *
 * <p>Do not use this interceptor unless it is absolutely necessary. A target object should normally not know about Spring AOP,
 * because that creates a dependency on the Spring API. Target objects should be plain POJOs wherever possible.
 *
 * <p>If used, this interceptor will normally be the first in the interceptor chain.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
@SuppressWarnings("serial")
public final class ExposeInvocationInterceptor implements MethodInterceptor, PriorityOrdered, Serializable {

	/** The singleton instance of this class. */
	public static final ExposeInvocationInterceptor INSTANCE = new ExposeInvocationInterceptor();

	/**
	 * The singleton advisor for this class. Use it when using Spring AOP, because it avoids creating a new Advisor to wrap the instance.
	 */
	public static final Advisor ADVISOR = new DefaultPointcutAdvisor(INSTANCE) {
		@Override
		public String toString() {
			return ExposeInvocationInterceptor.class.getName() +".ADVISOR";
		}
	};

	private static final ThreadLocal<MethodInvocation> invocation =
			new NamedThreadLocal<>("Current AOP method invocation");


	/**
	 * Return the AOP Alliance MethodInvocation object associated with the current invocation.
	 * @return the invocation object associated with the current invocation
	 * @throws IllegalStateException if there is no AOP invocation in progress,
	 * or if the ExposeInvocationInterceptor was not added to this interceptor chain
	 */
	public static MethodInvocation currentInvocation() throws IllegalStateException {
		MethodInvocation mi = invocation.get();
		if (mi == null) {
			throw new IllegalStateException(
					"No MethodInvocation found: Check that an AOP invocation is in progress and that the " +
					"ExposeInvocationInterceptor is upfront in the interceptor chain. Specifically, note that " +
					"advices with order HIGHEST_PRECEDENCE will execute before ExposeInvocationInterceptor! " +
					"In addition, ExposeInvocationInterceptor and ExposeInvocationInterceptor.currentInvocation() " +
					"must be invoked from the same thread.");
		}
		return mi;
	}


	/**
	 * Ensures that only the canonical instance can be created.
	 */
	private ExposeInvocationInterceptor() {
	}

	@Override
	@Nullable
	public Object invoke(MethodInvocation mi) throws Throwable {
		MethodInvocation oldInvocation = invocation.get();
		invocation.set(mi);
		try {
			return mi.proceed();
		}
		finally {
			invocation.set(oldInvocation);
		}
	}

	@Override
	public int getOrder() {
		return PriorityOrdered.HIGHEST_PRECEDENCE + 1;
	}

	/**
	 * Required to support serialization. Replaces with canonical instance
	 * on deserialization, protecting Singleton pattern.
	 * <p>Alternative to overriding the {@code equals} method.
	 */
	private Object readResolve() {
		return INSTANCE;
	}

}
```

### 5. Best Practices

This example creates an annotation-based application context, gets a bean named `MyService` from it, and calls its `foo()` method.

```java
public class ExposeInvocationInterceptorDemo {

    public static void main(String[] args) {
        // Create an annotation-based application context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // Get MyService from the context
        MyService myService = context.getBean(MyService.class);
        // Call the method
        myService.foo();
    }
}
```

It uses the `@EnableAspectJAutoProxy` annotation to turn on AspectJ auto proxy support, and the `@ComponentScan` annotation to scan the components in the package `com.xcs.spring`.

```java
@EnableAspectJAutoProxy
@Configuration
@ComponentScan("com.xcs.spring")
public class AppConfig {

}
```

A service class that implements the business logic.

```java
@Service
public class MyService {

    public void foo() {
        System.out.println("foo...");
    }
}
```

`MyMethodInterceptor` is marked with `@Aspect` and `@Component`. This means it is an aspect and is managed by the Spring container. It has a method named `before()`, marked with `@Before`, which runs before the target method. Inside the method, it calls `LogUtil.print()` to write a log or do other work. This aspect adds some logic before the execution of all public methods of the `com.xcs.spring.MyService` class.

```java
@Aspect
@Component
public class MyMethodInterceptor {

    @Before("execution(public * com.xcs.spring.MyService.*(..))")
    public void before() {
        LogUtil.print();
    }
}
```

It gets the `ProxyMethodInvocation` object of the current method call with `ExposeInvocationInterceptor.currentInvocation()`. Then it prints the method name, the number of arguments, the target object, and the class name of the proxy object.

```java
public class LogUtil {

    public static void print() {
        ProxyMethodInvocation methodInvocation = (ProxyMethodInvocation) ExposeInvocationInterceptor.currentInvocation();
        System.out.println("Method = " + methodInvocation.getMethod());
        System.out.println("Arguments Length = " + methodInvocation.getArguments().length);
        System.out.println("Target = " + methodInvocation.getThis());
        System.out.println("Proxy Class = " + methodInvocation.getProxy().getClass());
    }
}
```

In the run result, the log is printed with the method call context obtained from `ExposeInvocationInterceptor.currentInvocation()`.

```java
Method = public void com.xcs.spring.MyService.doSomething()
Arguments Length = 0
Target = com.xcs.spring.MyService@49964d75
Proxy Class = class com.xcs.spring.MyService$$EnhancerBySpringCGLIB$$f30643a6
Doing something...
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    AbstractAutowireCapableBeanFactory->>AbstractAutoProxyCreator: postProcessAfterInitialization()
    Note over AbstractAutowireCapableBeanFactory,AbstractAutoProxyCreator: Call the post-processing method
    AbstractAutoProxyCreator->>AbstractAutoProxyCreator: wrapIfNecessary()
    Note over AbstractAutoProxyCreator: Call the wrap method
    AbstractAutoProxyCreator->>AbstractAdvisorAutoProxyCreator: getAdvicesAndAdvisorsForBean()
    Note over AbstractAutoProxyCreator,AbstractAdvisorAutoProxyCreator: Get the advice and Advisors
    AbstractAdvisorAutoProxyCreator->>AbstractAdvisorAutoProxyCreator: findEligibleAdvisors()
    Note over AbstractAdvisorAutoProxyCreator: Find eligible Advisors
    AbstractAdvisorAutoProxyCreator->>AspectJAwareAdvisorAutoProxyCreator: extendAdvisors()
    Note over AbstractAdvisorAutoProxyCreator,AspectJAwareAdvisorAutoProxyCreator: Extension hook for Advisors
    AspectJAwareAdvisorAutoProxyCreator->>AspectJProxyUtils:makeAdvisorChainAspectJCapableIfNecessary()
    Note over AspectJAwareAdvisorAutoProxyCreator,AspectJProxyUtils: Add the special interceptor
~~~

### 7. Source Code Analysis

The `org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator#extendAdvisors` method adds an `ExposeInvocationInterceptor` at the start.

```java
/**
 * Add the {@link ExposeInvocationInterceptor} to the start of the advice chain.
 * <p>These extra Advisors are needed when using AspectJ pointcut expressions and AspectJ-style advice.
 */
@Override
protected void extendAdvisors(List<Advisor> candidateAdvisors) {
    AspectJProxyUtils.makeAdvisorChainAspectJCapableIfNecessary(candidateAdvisors);
}
```

The `org.springframework.aop.aspectj.AspectJProxyUtils#makeAdvisorChainAspectJCapableIfNecessary`
method adds a special interceptor to the proxy chain, to make sure it works with a chain that contains AspectJ advice. Specifically, it adds `ExposeInvocationInterceptor`
to the start of the advisors list. The goal is to expose the current Spring AOP invocation (needed for some AspectJ pointcut matching) and to make the current AspectJ
JoinPoint available. If there is no AspectJ advisor in the advisors chain, this call has no effect. The method returns `true`
if `ExposeInvocationInterceptor` was added to the advice list, and `false` otherwise.

```java
/**
 * Add special advice to a proxy chain that contains AspectJ advice, if needed:
 * specifically, add the {@link ExposeInvocationInterceptor} at the start of the list.
 * <p>This exposes the current Spring AOP invocation (needed for some AspectJ pointcut matching),
 * and makes the current AspectJ JoinPoint available. If there is no AspectJ advice in the advice chain, the call has no effect.
 * @param advisors the list of available advisors
 * @return {@code true} if the {@link ExposeInvocationInterceptor} was added to the list, otherwise {@code false}
 */
public static boolean makeAdvisorChainAspectJCapableIfNecessary(List<Advisor> advisors) {
    // Do not add advice to an empty list; this may mean no proxy is needed
    if (!advisors.isEmpty()) {
        boolean foundAspectJAdvice = false;
        for (Advisor advisor : advisors) {
            // Use unguarded Advice with care, because it may eagerly instantiate non-singleton AspectJ aspects...
            if (isAspectJAdvice(advisor)) {
                foundAspectJAdvice = true;
                break;
            }
        }
        // Add it if AspectJ advice is found in the advice chain and there is no ExposeInvocationInterceptor.ADVISOR
        if (foundAspectJAdvice && !advisors.contains(ExposeInvocationInterceptor.ADVISOR)) {
            advisors.add(0, ExposeInvocationInterceptor.ADVISOR);
            return true;
        }
    }
    return false;
}
```

The `org.springframework.aop.aspectj.AspectJProxyUtils#isAspectJAdvice` method checks whether the given Advisor contains AspectJ Advice. It checks whether the Advisor instance is of a specific type, or its Advice is a subclass of AbstractAspectJAdvice, or its Pointcut is an instance of AspectJExpressionPointcut.

```java
/**
 * Check whether the given Advisor contains AspectJ Advice.
 * @param advisor the Advisor to check
 */
private static boolean isAspectJAdvice(Advisor advisor) {
    return (advisor instanceof InstantiationModelAwarePointcutAdvisor ||
          advisor.getAdvice() instanceof AbstractAspectJAdvice ||
          (advisor instanceof PointcutAdvisor &&
                ((PointcutAdvisor) advisor).getPointcut() instanceof AspectJExpressionPointcut));
}
```
