## AopProxyFactory
- [AopProxyFactory](#aopproxyfactory)
    - [1. Overview](#1-overview)
    - [2. Description](#2-description)
    - [3. Main Features](#3-main-features)
    - [4. Interface Source](#4-interface-source)
    - [5. Main Implementations](#5-main-implementations)
    - [6. Class Diagram](#6-class-diagram)
    - [7. Best Practices](#7-best-practices)
    - [8. Source Code Analysis](#8-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `AopProxyFactory` interface is one of the key parts of the Spring AOP framework. It creates AOP proxy objects from configuration. It chooses a suitable proxy type (JDK dynamic proxy or CGLIB proxy) based on the type of the target object and the configuration.

### 3. Main Features

1. **Create AOP proxy objects**

   + Creates an AOP proxy object from the given `AdvisedSupport` object, using the chosen proxy type (JDK dynamic proxy or CGLIB proxy).
   
2. **Decide the proxy type**

   + Decides whether to use a JDK dynamic proxy or a CGLIB proxy, based on the type of the target class and the configuration. The decision usually depends on some settings, such as whether interfaces should be proxied or whether CGLIB proxies are allowed.
   
3. **Flexible configuration**

   + By implementing this interface, you can customize how AOP proxies are created, to fit different needs.

### 4. Interface Source

The `AopProxyFactory` interface creates AOP proxies from an `AdvisedSupport` configuration object. The proxy objects must follow a set of rules. They implement all interfaces the configuration says to proxy. They implement the `Advised` interface. They implement the `equals` method to compare the proxied interfaces, advice, and target. They should be serializable if all advisors and the target are serializable. They should be thread-safe if the advisors and the target are thread-safe. An implementation should create the matching AOP proxy object from the given AOP configuration, and throw an `AopConfigException` if the configuration is invalid.

```java
/**
 * Interface to be implemented by factories that can create AOP proxies based on an {@link AdvisedSupport} configuration object.
 *
 * <p>Proxies should follow these rules:
 * <ul>
 * <li>They should implement all interfaces that the configuration says should be proxied.
 * <li>They should implement the {@link Advised} interface.
 * <li>They should implement the equals method to compare the proxied interfaces, advice, and target.
 * <li>They should be serializable if all advisors and the target are serializable.
 * <li>They should be thread-safe if the advisors and the target are thread-safe.
 * </ul>
 *
 * <p>A proxy may or may not allow changes to the advice. If it does not allow changes (for example, because the configuration is frozen), the proxy should throw an {@link AopConfigException} when someone tries to change the advice.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface AopProxyFactory {

	/**
	 * Create an {@link AopProxy} for the given AOP configuration.
	 * @param config the AOP configuration in the form of an AdvisedSupport object
	 * @return the corresponding AOP proxy
	 * @throws AopConfigException if the configuration is invalid
	 */
	AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException;

}

```

### 5. Main Implementations

1. **DefaultAopProxyFactory**

   + `DefaultAopProxyFactory` is the default implementation of the `AopProxyFactory` interface. It creates an AOP proxy from the given `AdvisedSupport` configuration object. It chooses a suitable proxy type (JDK dynamic proxy or CGLIB proxy) from the configuration, and handles errors that may happen. This lets Spring AOP create and use AOP proxy objects flexibly, which gives the aspect programming feature.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class AopProxyFactory {
<<Interface>>

}
class DefaultAopProxyFactory

DefaultAopProxyFactory  ..>  AopProxyFactory 
~~~

### 7. Best Practices

This example shows how to use Spring AOP to create a JDK dynamic proxy and a CGLIB proxy. In the `jdkProxy()` method, it first creates an `AdvisedSupport` object to configure the AOP proxy. Then it uses a `DefaultAopProxyFactory` instance to create a JDK dynamic proxy object, and prints the generated proxy class. In the `cglibProxy()` method, it also creates an `AdvisedSupport` object to configure the AOP proxy. Then it uses a `DefaultAopProxyFactory` instance to create a CGLIB proxy object, and prints the generated proxy class.

```java
public class AopProxyFactoryDemo {

    public static void main(String[] args) {
        // Show the JDK dynamic proxy and the CGLIB proxy one by one
        jdkProxy();
        cglibProxy();
    }

    /**
     * JDK dynamic proxy example
     */
    private static void jdkProxy() {
        // Create an AdvisedSupport object to configure the AOP proxy
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // Set the target object
        advisedSupport.setTarget(new MyServiceImpl());
        // Set the class of the target object
        advisedSupport.setTargetClass(MyService.class);

        // Create a DefaultAopProxyFactory instance
        AopProxyFactory aopProxyFactory = new DefaultAopProxyFactory();
        // Create the JDK dynamic proxy object
        MyService myService = (MyService) aopProxyFactory.createAopProxy(advisedSupport).getProxy();
        // Print the generated proxy class
        System.out.println("jdkProxy = " + myService.getClass());
    }

    /**
     * CGLIB proxy example
     */
    private static void cglibProxy() {
        // Create an AdvisedSupport object to configure the AOP proxy
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // Set the target object
        advisedSupport.setTarget(new MyServiceImpl());
        // Create a DefaultAopProxyFactory instance
        AopProxyFactory aopProxyFactory = new DefaultAopProxyFactory();
        // Create the CGLIB proxy object
        MyService myService = (MyService) aopProxyFactory.createAopProxy(advisedSupport).getProxy();
        // Print the generated proxy class
        System.out.println("cglibProxy = " + myService.getClass());
    }
}
```

This is the result. It shows two different proxy classes. `jdkProxy` is the proxy class created by the JDK dynamic proxy, shown as `com.sun.proxy.$Proxy0`. `cglibProxy` is the proxy class created by CGLIB, shown as `com.xcs.spring.MyServiceImpl$$EnhancerBySpringCGLIB$$3c109cf5`. The two proxy classes match two different proxy types, and Spring AOP creates each of them from the configuration.

```java
jdkProxy = class com.sun.proxy.$Proxy0
cglibProxy = class com.xcs.spring.MyServiceImpl$$EnhancerBySpringCGLIB$$3c109cf5
```

### 8. Source Code Analysis

`DefaultAopProxyFactory` is the default implementation of the `AopProxyFactory` interface. Based on the given `AdvisedSupport` configuration object, it can create a CGLIB proxy or a JDK dynamic proxy. A CGLIB proxy is created if the given `AdvisedSupport` instance meets one of these conditions: the optimize flag is set, the proxy target class flag is set, or no proxy interfaces are specified. Usually, you can force a CGLIB proxy by setting `proxyTargetClass`, or use a JDK dynamic proxy by specifying one or more interfaces.

[AopProxy Source Code Analysis](../spring-aop-aopProxy/README.md)

```java
/**
 * Default {@link AopProxyFactory} implementation. It creates a CGLIB proxy or a JDK dynamic proxy based on conditions.
 *
 * <p>A CGLIB proxy is created if the given {@link AdvisedSupport} instance meets one of these conditions:
 * <ul>
 * <li>The {@code optimize} flag is set
 * <li>The {@code proxyTargetClass} flag is set
 * <li>No proxy interfaces are specified
 * </ul>
 *
 * <p>Usually, you can force a CGLIB proxy by setting {@code proxyTargetClass}, or use a JDK dynamic proxy by specifying one or more interfaces.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 * @since 2004-03-12
 * @see AdvisedSupport#setOptimize
 * @see AdvisedSupport#setProxyTargetClass
 * @see AdvisedSupport#setInterfaces
 */
@SuppressWarnings("serial")
public class DefaultAopProxyFactory implements AopProxyFactory, Serializable {

	@Override
    public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
        // Check whether a CGLIB proxy is supported. If so, create a CGLIB proxy
        if (!NativeDetector.inNativeImage() &&
                (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config))) {
            // Get the target class
            Class<?> targetClass = config.getTargetClass();
            if (targetClass == null) {
                // If the target class is null, throw an AopConfigException
                throw new AopConfigException("TargetSource cannot determine target class: " +
                        "Either an interface or a target is required for proxy creation.");
            }
            // If the target class is an interface or a proxy class, create a JDK dynamic proxy
            if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
                return new JdkDynamicAopProxy(config);
            }
            // Otherwise, create a CGLIB proxy
            return new ObjenesisCglibAopProxy(config);
        } else {
            // Otherwise, create a JDK dynamic proxy
            return new JdkDynamicAopProxy(config);
        }
    }

	/**
	 * Determine whether the supplied {@link AdvisedSupport} only specifies the {@link org.springframework.aop.SpringProxy} interface
	 * (or specifies no proxy interfaces at all).
	 */
	private boolean hasNoUserSuppliedProxyInterfaces(AdvisedSupport config) {
		Class<?>[] ifcs = config.getProxiedInterfaces();
		return (ifcs.length == 0 || (ifcs.length == 1 && SpringProxy.class.isAssignableFrom(ifcs[0])));
	}

}
```
