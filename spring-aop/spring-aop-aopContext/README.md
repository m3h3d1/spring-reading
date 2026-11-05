## AopContext

- [AopContext](#aopcontext)
  - [1. Overview](#1-overview)
  - [2. Description](#2-description)
  - [3. Main Features](#3-main-features)
  - [4. Class Source](#4-class-source)
  - [5. Best Practices](#5-best-practices)
  - [6. Source Code Analysis](#6-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `AopContext` class is a utility class in the Spring AOP framework. It lets you get the current AOP proxy object from inside a method. With the `currentProxy()` method, you can get the proxy of the current object, and then call other methods of the proxy or get related information.

### 3. Main Features

1. **Get the current AOP proxy object**

   + By calling `currentProxy()`, you can get the current AOP proxy object inside a method. This is the object after AOP enhancement.

2. **Call methods of the proxy object inside a method**

   + After getting the proxy object, you can call its other methods directly inside a method. This includes the enhanced aspect methods and the methods of the target object.

3. **Solve the problem of passing the proxy object**

   + In some cases, you may need to pass the AOP proxy object between methods, instead of calling `this` directly. The `AopContext` class gives a way to pass the AOP proxy object between method calls.

### 4. Class Source

The `AopContext` class provides a set of static methods to get information about the current AOP invocation. With the `currentProxy()` method, you can get the current AOP proxy object, as long as the AOP framework is set to expose the proxy. This is useful when a target object or advice needs to make an enhanced call on itself, and for finding the advice configuration. However, because it costs performance, the Spring AOP framework does not expose the proxy by default.

```java
/**
 * A set of static methods to get information about the current AOP invocation.
 * 
 * <p>If the AOP framework is set to expose the current proxy (not the default), the {@code currentProxy()} method gives the AOP proxy that is in use.
 * A target object or advice can use this method to make an enhanced call, like {@code getEJBObject()} in EJB. It can also be used to find the advice configuration.
 * 
 * <p>The Spring AOP framework does not expose the proxy by default, because doing so costs performance.
 * 
 * <p>The features in this class can be used by target objects to get resources from the invocation. However, this method should not be used when there is a reasonable alternative, because it makes application code depend on the AOP usage and the Spring AOP framework.
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 13.03.2003
 */
public final class AopContext {

	/**
	 * ThreadLocal variable that holds the AOP proxy object tied to this thread.
	 * It holds {@code null} unless the "exposeProxy" property that controls the proxy configuration is set to "true".
	 * @see ProxyConfig#setExposeProxy
	 */
	private static final ThreadLocal<Object> currentProxy = new NamedThreadLocal<>("Current AOP proxy");


	private AopContext() {
	}


	/**
	 * Try to return the current AOP proxy object. This method is available only when the calling method was invoked through AOP and the AOP framework is set to expose the proxy.
	 * Otherwise, this method throws an IllegalStateException.
	 * @return the current AOP proxy object (never returns {@code null})
	 * @throws IllegalStateException if the proxy object cannot be found, because the method was called outside an AOP invocation context, or because the AOP framework is not set to expose the proxy
	 */
	public static Object currentProxy() throws IllegalStateException {
		Object proxy = currentProxy.get();
		if (proxy == null) {
			throw new IllegalStateException(
					"Cannot find current proxy: Set 'exposeProxy' property on Advised to 'true' to make it available, and " +
							"ensure that AopContext.currentProxy() is invoked in the same thread as the AOP invocation context.");
		}
		return proxy;
	}

	/**
	 * Make the given proxy object available through the {@code currentProxy()} method.
	 * <p>Note that the caller should be careful to keep the old value.
	 * @param proxy the proxy object to expose (or {@code null} to reset it)
	 * @return the old proxy object, which may be {@code null} if none was bound
	 * @see #currentProxy()
	 */
	@Nullable
	static Object setCurrentProxy(@Nullable Object proxy) {
		Object old = currentProxy.get();
		if (proxy != null) {
			currentProxy.set(proxy);
		}
		else {
			currentProxy.remove();
		}
		return old;
	}

}
```

### 5. Best Practices

This example uses Spring AOP to create a proxy object and applies a custom before advice before the proxy method runs. First, it creates a proxy factory with `ProxyFactory` and sets the target object `MyService` to be proxied. Then it calls `proxyFactory.setExposeProxy(true)` to expose the proxy object, so that `AopContext` can reach the proxy inside a method. Next, it uses `proxyFactory.addAdvisor()` to add an advisor that applies the custom before advice `MyMethodBeforeAdvice` to methods marked with `MyAnnotation`. Finally, it gets the proxy object with `proxyFactory.getProxy()` and calls its method `foo()`.

```java
public class AopContextDemo {

    public static void main(String[] args) {
        // Create the proxy factory and the target object
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // Expose the proxy object
        proxyFactory.setExposeProxy(true);
        // Create the advisor
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(new AnnotationMatchingPointcut(MyAnnotation.class), new MyMethodBeforeAdvice()));
        // Get the proxy object
        MyService proxy = (MyService) proxyFactory.getProxy();
        // Call a method on the proxy object
        proxy.foo();
    }
}
```

`MyMethodBeforeAdvice` is the custom before advice class.

```java
public class MyMethodBeforeAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("Before method " + method.getName() + " is called.");
    }
}
```

The custom annotation `MyAnnotation`.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MyAnnotation {
}
```

This defines a Java class named `MyService`, marked with `@MyAnnotation`. The class has two methods, `foo()` and `bar()`. In `foo()`, it calls `AopContext.currentProxy()` to get the current AOP proxy object, and uses that proxy to call `bar()`. This makes sure the AOP aspect is applied correctly. It avoids the problem where calling `bar()` directly makes the AOP aspect not work.

```java
@MyAnnotation
public class MyService {

    public void foo() {
        System.out.println("foo...");
        // Calling bar directly makes the aspect not work
        // this.bar();
        // Get the proxy object and call bar
        ((MyService) AopContext.currentProxy()).bar();
    }

    public void bar() {
        System.out.println("bar...");
    }
}
```

Result 1. When `foo()` is called, the before advice runs and prints a log. Then `this.bar()` is called. Because calling `this.bar()` directly bypasses the AOP proxy object, the aspect logic is not triggered.

```java
Before method foo is called.
foo...
bar...
```

Result 2. When `foo()` is called, the before advice runs and prints a log. Then `((MyService) AopContext.currentProxy()).bar();` is called. Because `AopContext.currentProxy()` gets the current AOP proxy object and `bar()` is called on that proxy, the aspect logic is triggered.

```java
Before method foo is called.
foo...
Before method bar is called.
bar...
```

### 6. Source Code Analysis

In the Spring AOP framework, both the JDK dynamic proxy interceptor and the CGLIB dynamic proxy interceptor call `AopContext.setCurrentProxy(proxy)`. This sets the current AOP proxy object in the context of the current thread, so that `AopContext.currentProxy()` can get the proxy object inside a method.

**JDK dynamic proxy interceptor**

The method `org.springframework.aop.framework.JdkDynamicAopProxy#invoke` is part of the JDK dynamic proxy interceptor. It handles the context of the AOP proxy. Before the method runs, if the AOP proxy object is exposed (that is, `this.advised.exposeProxy` is `true`), the current AOP proxy object is set in the context of the current thread with `AopContext.setCurrentProxy(proxy)`. Then `AopContext.currentProxy()` can get the proxy object inside the method. After the method finishes, the previously set proxy object is restored, so that the AOP proxy context does not affect other threads.

```java
@Override
@Nullable
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Object oldProxy = null;
    boolean setProxyContext = false;
    // ... [code omitted for brevity]
    try {
        // ... [code omitted for brevity]
        if (this.advised.exposeProxy) {
            // Make invocation available if necessary.
            oldProxy = AopContext.setCurrentProxy(proxy);
            setProxyContext = true;
        }
        // ... [code omitted for brevity]
    }
    finally {
        // ... [code omitted for brevity]
        if (setProxyContext) {
            // Restore old proxy.
            AopContext.setCurrentProxy(oldProxy);
        }
    }
}
```

**CGLIB dynamic proxy interceptor**

The method `org.springframework.aop.framework.CglibAopProxy.DynamicAdvisedInterceptor#intercept` is part of the CGLIB dynamic proxy interceptor. While the method is intercepted, it handles the context of the AOP proxy. If the AOP proxy object is exposed (that is, `this.advised.exposeProxy` is `true`), the current AOP proxy object is set in the context of the current thread with `AopContext.setCurrentProxy(proxy)`. Then `AopContext.currentProxy()` can get the proxy object inside the method. After the method finishes, the previously set proxy object is restored, so that the AOP proxy context does not affect other threads.

```java
@Override
@Nullable
public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
    Object oldProxy = null;
    boolean setProxyContext = false;
    // ... [code omitted for brevity]
    try {
        if (this.advised.exposeProxy) {
            // Make invocation available if necessary.
            oldProxy = AopContext.setCurrentProxy(proxy);
            setProxyContext = true;
        }
        // ... [code omitted for brevity]
    }
    finally {
        // ... [code omitted for brevity]
        if (setProxyContext) {
            // Restore old proxy.
            AopContext.setCurrentProxy(oldProxy);
        }
    }
}
```
