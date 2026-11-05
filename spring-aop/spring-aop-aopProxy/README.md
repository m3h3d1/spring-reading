## AopProxy

- [AopProxy](#aopproxy)
  - [1. Overview](#1-overview)
  - [2. Description](#2-description)
  - [3. Main Features](#3-main-features)
  - [4. Interface Source](#4-interface-source)
  - [5. Main Implementations](#5-main-implementations)
  - [6. Class Diagram](#6-class-diagram)
  - [7. Best Practices](#7-best-practices)
  - [8. Sequence Diagram](#8-sequence-diagram)
  - [9. Source Code Analysis](#9-source-code-analysis)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `AopProxy` interface is a key part of Spring's support for aspect-oriented programming (AOP). It defines the standard interface for creating proxy objects. It lets you create proxy objects at runtime, so that method calls on the target object can be intercepted and enhanced.

### 3. Main Features

1. **Create and manage proxy objects**

   + The `AopProxy` interface defines the standard methods to create and manage proxy objects. You can use them to create proxy objects at runtime.

3. **Support for different proxy types**

   + The `AopProxy` interface supports several proxy types, including the JDK dynamic proxy and the CGLIB proxy. The right type is chosen depending on whether the target object implements an interface.

### 4. Interface Source

The `AopProxy` interface is a delegate interface that configures an AOP proxy and lets you create the actual proxy object. It has two methods to create a proxy object. The first one uses the default class loader, usually the thread context class loader. The second one lets you give a class loader. The proxy object can be created with the JDK dynamic proxy or with CGLIB.

```java
/**
 * Delegate interface for a configured AOP proxy, which allows the creation of the actual proxy object.
 *
 * <p>By default, the implementations available for creating proxy objects are the JDK dynamic proxy and the CGLIB proxy,
 * and these are applied by {@link DefaultAopProxyFactory}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see DefaultAopProxyFactory
 */
public interface AopProxy {

	/**
	 * Create a new proxy object.
	 * <p>Uses the default class loader of the AopProxy (if needed for proxy creation):
	 * usually the thread context class loader.
	 * @return the new proxy object (never {@code null})
	 * @see Thread#getContextClassLoader()
	 */
	Object getProxy();

	/**
	 * Create a new proxy object.
	 * <p>Uses the given class loader (if needed for proxy creation).
	 * If the given class loader is {@code null}, it is simply passed on, which leads to the defaults of the low-level proxy facility,
	 * and these usually differ from the default chosen by the {@link #getProxy()} method of the AopProxy implementation.
	 * @param classLoader the class loader to create the proxy with
	 * (or {@code null} to use the defaults of the low-level proxy facility)
	 * @return the new proxy object (never {@code null})
	 */
	Object getProxy(@Nullable ClassLoader classLoader);

}
```

### 5. Main Implementations

1. **JdkDynamicAopProxy**

   + The `AopProxy` implementation that uses the JDK dynamic proxy. When the target object implements at least one interface, Spring uses this class to create the proxy object. It creates the proxy object with the `java.lang.reflect.Proxy` class from the Java standard library.
   
2. **CglibAopProxy**

     + The `AopProxy` implementation that uses the CGLIB (Code Generation Library) dynamic proxy. When the target object implements no interface, Spring uses this class to create the proxy object. It creates the proxy object by generating a subclass of the target class, which intercepts and enhances the methods of the target object.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class AopProxy {
<<Interface>>

}
class CglibAopProxy
class JdkDynamicAopProxy
class ObjenesisCglibAopProxy

CglibAopProxy  ..>  AopProxy 
JdkDynamicAopProxy  ..>  AopProxy 
ObjenesisCglibAopProxy  -->  CglibAopProxy 
~~~

### 7. Best Practices

**JDK dynamic proxy**

This example uses the JDK dynamic proxy to create an AOP proxy object. In the `jdkProxy` method, it configures an `AdvisedSupport` object with the target object and interfaces. Then it creates a `JdkDynamicAopProxy` instance by reflection, and calls the `getProxy` method of the `AopProxy` interface to create the proxy object. Finally, it prints information about the proxy object and the result of calling a method on it.

```java
public class AopProxyDemo {

    public static void main(String[] args) throws Exception {
        jdkProxy();
    }

    /**
     * JDK proxy
     *
     * @throws Exception
     */
    private static void jdkProxy() throws Exception {
        // Create an AdvisedSupport object to configure the AOP proxy
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // Set the target object
        advisedSupport.setTarget(new MyServiceImpl());
        // Set the interfaces implemented by the target object
        advisedSupport.setInterfaces(MyService.class);

        // Get the Class object of JdkDynamicAopProxy
        Class jdkClass = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy");

        // Get the constructor of JdkDynamicAopProxy
        Constructor constructor = jdkClass.getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);

        // Create a JdkDynamicAopProxy instance with the constructor
        AopProxy aopProxy = (AopProxy) constructor.newInstance(advisedSupport);

        // Call the getProxy method to create the proxy object
        MyService myService = (MyService) aopProxy.getProxy();

        // Print information about the proxy object
        System.out.println("JDK Class = " + myService.getClass());
        // Call a method on the proxy object
        myService.foo();
    } 
}
```

This is the result. The class of the proxy object is `com.sun.proxy.$Proxy0`.

```java
JDK Class = class com.sun.proxy.$Proxy0
Before foo
foo...
After foo
```

**CGLIB proxy**

This example uses the CGLIB dynamic proxy to create an AOP proxy object. In the `cglibProxy` method, it configures an `AdvisedSupport` object with the target object. Then it creates a `CglibAopProxy` instance by reflection, and calls the `getProxy` method of the `AopProxy` interface to create the proxy object. Finally, it prints information about the proxy object and the result of calling a method on it.

```java
public class AopProxyDemo {

    public static void main(String[] args) throws Exception {
        cglibProxy();
    }

    /**
     * CGLIB proxy
     *
     * @throws Exception
     */
    private static void cglibProxy() throws Exception {
        // Create an AdvisedSupport object to configure the AOP proxy
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // Set the target object
        advisedSupport.setTarget(new MyServiceImpl());

        // Get the Class object of CglibAopProxy
        Class cglibClass = Class.forName("org.springframework.aop.framework.CglibAopProxy");

        // Get the constructor of CglibAopProxy
        Constructor constructor = cglibClass.getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);

        // Create a CglibAopProxy instance with the constructor
        AopProxy aopProxy = (AopProxy) constructor.newInstance(advisedSupport);

        // Call the getProxy method to create the proxy object
        MyService myService = (MyService) aopProxy.getProxy();

        // Print information about the proxy object
        System.out.println("Cglib Class = " + myService.getClass());
        // Call a method on the proxy object
        myService.foo();
    }   
}
```

This is the result. The class of the proxy object is `com.xcs.spring.MyServiceImpl$$EnhancerBySpringCGLIB$$db84547f`.

```java
Cglib Class = class com.xcs.spring.MyServiceImpl$$EnhancerBySpringCGLIB$$db84547f
Before foo
foo...
After foo
```

### 8. Sequence Diagram

**JdkDynamicAopProxy**

~~~mermaid
sequenceDiagram
autonumber
AopProxyDemo->>JdkDynamicAopProxy:new JdkDynamicAopProxy()
JdkDynamicAopProxy->>JdkDynamicAopProxy:this.advised
JdkDynamicAopProxy->>JdkDynamicAopProxy:this.proxiedInterfaces
JdkDynamicAopProxy->>AopProxyDemo:Return aopProxy
AopProxyDemo->>JdkDynamicAopProxy:aopProxy.getProxy()
JdkDynamicAopProxy->>JdkDynamicAopProxy:getProxy(classLoader)
JdkDynamicAopProxy->>Proxy:Proxy.newProxyInstance()
JdkDynamicAopProxy->>AopProxyDemo:Return the proxy object
AopProxyDemo->>$Proxy0:aopProxy.foo()
$Proxy0->>JdkDynamicAopProxy:invoke()
JdkDynamicAopProxy->>ReflectiveMethodInvocation:new ReflectiveMethodInvocation()
ReflectiveMethodInvocation->>JdkDynamicAopProxy:Return invocation
JdkDynamicAopProxy->>ReflectiveMethodInvocation:invocation.proceed()
~~~

**CglibAopProxy**

~~~mermaid
sequenceDiagram
autonumber
AopProxyDemo->>CglibAopProxy:new CglibAopProxy()
CglibAopProxy->>CglibAopProxy:this.advised
CglibAopProxy->>CglibAopProxy:this.advisedDispatcher
CglibAopProxy->>AopProxyDemo:Return aopProxy
AopProxyDemo->>CglibAopProxy:aopProxy.getProxy()
CglibAopProxy->>CglibAopProxy:getProxy(classLoader)
CglibAopProxy->>Enhancer:new Enhancer()
Enhancer->>CglibAopProxy:Return enhancer
CglibAopProxy->>CglibAopProxy:getCallbacks()
CglibAopProxy->>CglibAopProxy:createProxyClassAndInstance()
CglibAopProxy->>Enhancer:enhancer.create()
CglibAopProxy->>AopProxyDemo:Return the proxy object
AopProxyDemo->>MyServiceImpl$$EnhancerBySpringCGLIB$$:aopProxy.foo()
MyServiceImpl$$EnhancerBySpringCGLIB$$->>DynamicAdvisedInterceptor:intercept()
DynamicAdvisedInterceptor->>CglibMethodInvocation:new CglibMethodInvocation()
CglibMethodInvocation->>DynamicAdvisedInterceptor:Return invocation
DynamicAdvisedInterceptor->>CglibMethodInvocation:invocation.proceed()
~~~

### 9. Source Code Analysis

**JdkDynamicAopProxy**

The method `org.springframework.aop.framework.JdkDynamicAopProxy#getProxy()` returns a proxy object. It uses the default class loader to create the proxy.

```java
@Override
public Object getProxy() {
    return getProxy(ClassUtils.getDefaultClassLoader());
}
```

The method `org.springframework.aop.framework.JdkDynamicAopProxy#getProxy(java.lang.ClassLoader)` takes a class loader as an argument. It creates a JDK dynamic proxy object from the given class loader and the array of proxied interfaces.

```java
@Override
public Object getProxy(@Nullable ClassLoader classLoader) {
    if (logger.isTraceEnabled()) {
       logger.trace("Creating JDK dynamic proxy: " + this.advised.getTargetSource());
    }
    return Proxy.newProxyInstance(classLoader, this.proxiedInterfaces, this);
}
```

In the method `org.springframework.aop.framework.JdkDynamicAopProxy#invoke`, `JdkDynamicAopProxy` implements the `InvocationHandler` interface, so it can run the `invoke` method. In the method, it first handles the `equals` and `hashCode` methods specially. Then it gets the target object and the interceptor chain. Next, depending on whether the interceptor chain is empty, it either calls the target method directly or runs the method interceptor chain one by one. Finally, it handles the return value by its type. If the return value is the target object and the return type matches the proxy type, the return value is changed to the proxy object. After the method finishes, it makes sure the target object is released and the old proxy object is restored.

[AdvisorChainFactory Source Code Analysis](../spring-aop-advisorChainFactory/README.md)

[ProxyMethodInvocation Source Code Analysis](../spring-aop-proxyMethodInvocation/README.md)

```java
/**
 * Implementation of the {@code InvocationHandler.invoke} method.
 * <p>Callers will see exactly the exception thrown by the target, unless a hook method throws an exception.
 */
@Override
@Nullable
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // The old proxy object
    Object oldProxy = null; 
    // Whether the proxy context flag was set
    boolean setProxyContext = false; 

    // The target source
    TargetSource targetSource = this.advised.targetSource; 
    // The target object
    Object target = null; 

    try {
        if (!this.equalsDefined && AopUtils.isEqualsMethod(method)) {
            // The target object does not implement the equals(Object) method
            return equals(args[0]);
        } else if (!this.hashCodeDefined && AopUtils.isHashCodeMethod(method)) {
            // The target object does not implement the hashCode() method
            return hashCode();
        } else if (method.getDeclaringClass() == DecoratingProxy.class) {
            // Only getDecoratedClass() is declared -> forward to the proxy configuration
            return AopProxyUtils.ultimateTargetClass(this.advised);
        } else if (!this.advised.opaque && method.getDeclaringClass().isInterface() &&
                method.getDeclaringClass().isAssignableFrom(Advised.class)) {
            // Make a service call on the proxy configuration...
            return AopUtils.invokeJoinpointUsingReflection(this.advised, method, args);
        }

        Object retVal;

        if (this.advised.exposeProxy) {
            // Make the invocation available if necessary
            oldProxy = AopContext.setCurrentProxy(proxy);
            setProxyContext = true;
        }

        // Get the target object as late as possible, to minimize the time we "own" the target, in case it comes from a pool.
        target = targetSource.getTarget();
        Class<?> targetClass = (target != null ? target.getClass() : null);

        // Get the interceptor chain for this method.
        List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);

        // Check whether there is any advice. If not, we can fall back to a direct reflective call on the target, and avoid creating a MethodInvocation.
        if (chain.isEmpty()) {
            // We can skip creating a MethodInvocation: just invoke the target directly
            // Note that the final invoker must be an InvokerInterceptor, so we know it only does reflective operations on the target, with no hot swapping or fancy proxying.
            Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
            retVal = AopUtils.invokeJoinpointUsingReflection(target, method, argsToUse);
        } else {
            // We need to create a method invocation...
            MethodInvocation invocation =
                    new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
            // Proceed to the join point through the interceptor chain.
            retVal = invocation.proceed();
        }

        // Modify the return value if necessary.
        Class<?> returnType = method.getReturnType();
        if (retVal != null && retVal == target &&
                returnType != Object.class && returnType.isInstance(proxy) &&
                !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
            // Special case: it returned "this", and the return type of the method is compatible.
            // Note that we cannot help if the target sets a reference to itself in another returned object.
            retVal = proxy;
        } else if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
            throw new AopInvocationException(
						"Null return value from advice does not match primitive return type for: " + method);
        }
        return retVal;
    } finally {
        if (target != null && !targetSource.isStatic()) {
            // Must come from the TargetSource.
            targetSource.releaseTarget(target);
        }
        if (setProxyContext) {
            // Restore the old proxy.
            AopContext.setCurrentProxy(oldProxy);
        }
    }
}
```

**CglibAopProxy**

The method `org.springframework.aop.framework.CglibAopProxy#getProxy()` returns the proxy object. When no target class loader is given, it calls the other overloaded method `getProxy(null)` to create the proxy object and returns it.

```java
@Override
public Object getProxy() {
    return getProxy(null);
}
```

The method `org.springframework.aop.framework.CglibAopProxy#getProxy(java.lang.ClassLoader)` uses CGLIB to generate the proxy class and create the proxy object. First, it checks whether trace logging is on, then gets the root class of the target class and makes sure the target class can be used to create a proxy. Next, it sets the superclass of the proxy class to the root class of the target class and adds extra interfaces if needed. After configuring the CGLIB Enhancer, it sets the callbacks for the proxy class, and finally generates the proxy class and creates the proxy instance.

```java
@Override
public Object getProxy(@Nullable ClassLoader classLoader) {
    // If trace logging is on, log that a CGLIB proxy is being created
    if (logger.isTraceEnabled()) {
        logger.trace("Creating CGLIB proxy: " + this.advised.getTargetSource());
    }

    try {
        // Get the root class of the target class
        Class<?> rootClass = this.advised.getTargetClass();
        // Assert that the target class can be used to create a CGLIB proxy
        Assert.state(rootClass != null, "Target class must be available for creating a CGLIB proxy");

        // Set the superclass of the proxy class to the root class of the target class
        Class<?> proxySuperClass = rootClass;
        // If the name of the target class contains the CGLIB separator, change the superclass to the superclass of the target class and add the extra interfaces to the proxy class
        if (rootClass.getName().contains(ClassUtils.CGLIB_CLASS_SEPARATOR)) {
            proxySuperClass = rootClass.getSuperclass();
            // Get the extra interfaces and add them to the proxy class
            Class<?>[] additionalInterfaces = rootClass.getInterfaces();
            for (Class<?> additionalInterface : additionalInterfaces) {
                this.advised.addInterface(additionalInterface);
            }
        }

        // Validate the class if needed, and write log messages
        validateClassIfNecessary(proxySuperClass, classLoader);

        // Configure the CGLIB Enhancer...
        Enhancer enhancer = createEnhancer();
        // If a class loader is given, set it on the Enhancer, and disable caching when the class loader is reloadable
        if (classLoader != null) {
            enhancer.setClassLoader(classLoader);
            if (classLoader instanceof SmartClassLoader &&
                    ((SmartClassLoader) classLoader).isClassReloadable(proxySuperClass)) {
                enhancer.setUseCache(false);
            }
        }
        // Set the superclass of the proxy class
        enhancer.setSuperclass(proxySuperClass);
        // Set the interfaces implemented by the proxy class
        enhancer.setInterfaces(AopProxyUtils.completeProxiedInterfaces(this.advised));
        // Set the naming policy
        enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
        // Set the strategy to take the class loader into account
        enhancer.setStrategy(new ClassLoaderAwareGeneratorStrategy(classLoader));

        // Get the callbacks and their types
        Callback[] callbacks = getCallbacks(rootClass);
        Class<?>[] types = new Class<?>[callbacks.length];
        for (int x = 0; x < types.length; x++) {
            types[x] = callbacks[x].getClass();
        }
        // fixedInterceptorMap is only filled after the getCallbacks call above
        // Set the callback filter, used to filter the fixed interceptors
        enhancer.setCallbackFilter(new ProxyCallbackFilter(
                this.advised.getConfigurationOnlyCopy(), this.fixedInterceptorMap, this.fixedInterceptorOffset));
        // Set the callback types
        enhancer.setCallbackTypes(types);

        // Generate the proxy class and create the proxy instance
        return createProxyClassAndInstance(enhancer, callbacks);
    } catch (CodeGenerationException | IllegalArgumentException ex) {
        // If generating the proxy class fails, throw an AopConfigException
        throw new AopConfigException("Could not generate CGLIB subclass of " + this.advised.getTargetClass() +
                ": Common causes of this problem include using a final class or a non-visible class", ex);
    } catch (Throwable ex) {
        // If getting the target class instance fails, throw an AopConfigException
        throw new AopConfigException("Unexpected AOP exception", ex);
    }
}
```

The method `org.springframework.aop.framework.CglibAopProxy#getCallbacks` chooses different interceptors and dispatchers depending on the proxy configuration. It also optimizes the choice based on whether the target is static and whether the advice chain is frozen. If the target is static and the advice chain is frozen, it optimizes by sending AOP calls straight to the target with a fixed chain. In the end, it returns an array that holds all the chosen callbacks.

```java
private Callback[] getCallbacks(Class<?> rootClass) throws Exception {
    // Parameters used for the optimized choice...
    boolean exposeProxy = this.advised.isExposeProxy();
    boolean isFrozen = this.advised.isFrozen();
    boolean isStatic = this.advised.getTargetSource().isStatic();

    // Choose an "AOP" interceptor (for AOP invocations).
    Callback aopInterceptor = new DynamicAdvisedInterceptor(this.advised);

    // Choose a "straight to target" interceptor (for unadvised invocations, but which can return this).
    Callback targetInterceptor;
    if (exposeProxy) {
        targetInterceptor = (isStatic ?
                new StaticUnadvisedExposedInterceptor(this.advised.getTargetSource().getTarget()) :
                new DynamicUnadvisedExposedInterceptor(this.advised.getTargetSource()));
    } else {
        targetInterceptor = (isStatic ?
                new StaticUnadvisedInterceptor(this.advised.getTargetSource().getTarget()) :
                new DynamicUnadvisedInterceptor(this.advised.getTargetSource()));
    }

    // Choose a "straight to target" dispatcher (for unadvised invocations on a static target, which cannot return this).
    Callback targetDispatcher = (isStatic ?
            new StaticDispatcher(this.advised.getTargetSource().getTarget()) : new SerializableNoOp());

    Callback[] mainCallbacks = new Callback[] {
            aopInterceptor,  // For normal advice
            targetInterceptor,  // Invoke the target in the optimized case, ignoring advice
            new SerializableNoOp(),  // No override for methods mapped to this
            targetDispatcher, this.advisedDispatcher,
            new EqualsInterceptor(this.advised),
            new HashCodeInterceptor(this.advised)
    };

    Callback[] callbacks;

    // If the target is static and the advice chain is frozen,
    // we can optimize by sending AOP calls straight to the target with a fixed chain.
    if (isStatic && isFrozen) {
        Method[] methods = rootClass.getMethods();
        Callback[] fixedCallbacks = new Callback[methods.length];
        this.fixedInterceptorMap = CollectionUtils.newHashMap(methods.length);

        // TODO: Some memory optimization is done here (creation could be skipped for methods without advice)
        for (int x = 0; x < methods.length; x++) {
            Method method = methods[x];
            List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, rootClass);
            fixedCallbacks[x] = new FixedChainStaticTargetInterceptor(
                    chain, this.advised.getTargetSource().getTarget(), this.advised.getTargetClass());
            this.fixedInterceptorMap.put(method, x);
        }

        // Now copy the callbacks from mainCallbacks and fixedCallbacks into the callbacks array.
        callbacks = new Callback[mainCallbacks.length + fixedCallbacks.length];
        System.arraycopy(mainCallbacks, 0, callbacks, 0, mainCallbacks.length);
        System.arraycopy(fixedCallbacks, 0, callbacks, mainCallbacks.length, fixedCallbacks.length);
        this.fixedInterceptorOffset = mainCallbacks.length;
    } else {
        callbacks = mainCallbacks;
    }
    return callbacks;
}
```

The method `org.springframework.aop.framework.CglibAopProxy.DynamicAdvisedInterceptor#intercept` first gets the target object and the target class, and gets the interceptor chain for the given method. Then it handles the call depending on the interceptor chain and the features of the method. If the interceptor chain is empty and the method is public, it calls the target method directly. Otherwise, it creates a method invocation. At the end, it handles the return value of the method invocation and returns the result. During the call, it also decides from the configuration whether to expose the proxy object, and sets the AOP context when needed. Finally, in the finally block, it releases the target object and restores the old proxy object when needed.

[AdvisorChainFactory Source Code Analysis](../spring-aop-advisorChainFactory/README.md)

[ProxyMethodInvocation Source Code Analysis](../spring-aop-proxyMethodInvocation/README.md)

```java
@Override
@Nullable
public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
    // Save the old proxy object
    Object oldProxy = null; 
    // Whether the proxy context was set
    boolean setProxyContext = false; 
    // The target object
    Object target = null; 
    // Get the target source
    TargetSource targetSource = this.advised.getTargetSource(); 
    try {
       if (this.advised.exposeProxy) {
          // If the configuration allows exposing the proxy object, set the current proxy object as the current proxy of the AOP context
          oldProxy = AopContext.setCurrentProxy(proxy);
          setProxyContext = true;
       }
       // Get the target object as late as possible, to minimize the time we own the target, in case it comes from a pool...
       target = targetSource.getTarget();
       // The class of the target object
       Class<?> targetClass = (target != null ? target.getClass() : null); 
       // Get the interceptor chain
       List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass); 
       // The return value of the method invocation
       Object retVal; 
       // Check whether there is only one InvokerInterceptor: that is, there is no real advice, just a reflective call on the target.
       if (chain.isEmpty() && Modifier.isPublic(method.getModifiers())) {
          // We can skip creating a MethodInvocation: just invoke the target directly.
          // Note that the final invoker must be an InvokerInterceptor, so we know it only does reflective operations on the target, with no hot swapping or fancy proxying.
          Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args); 
          // Call the target method directly
          retVal = methodProxy.invoke(target, argsToUse); 
       }
       else {
          // We need to create a method invocation...
          // Create the method invocation and run it
          retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed(); 
       }
          // Handle the return type
       retVal = processReturnType(proxy, target, method, retVal); 
          // Return the result of the method invocation
       return retVal; 
    }
    finally {
       if (target != null && !targetSource.isStatic()) {
          // If the target object is not static, release it
          targetSource.releaseTarget(target); 
       }
       if (setProxyContext) {
          // Restore the old proxy object.
          AopContext.setCurrentProxy(oldProxy); // Restore the current proxy of the AOP context to the old proxy object
       }
    }
}
```
