## @EnableAspectJAutoProxy

- [@EnableAspectJAutoProxy](#enableaspectjautoproxy)
  - [1. Overview](#1-overview)
  - [2. Description](#2-description)
  - [3. Main Features](#3-main-features)
  - [4. Annotation Source](#4-annotation-source)
  - [5. Best Practices](#5-best-practices)
  - [6. Source Code Analysis](#6-source-code-analysis)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `EnableAspectJAutoProxy` annotation in the Spring Framework turns on AspectJ auto proxy support. It joins AspectJ aspects with the Spring IoC container automatically, so you do not need to configure many AOP settings by hand. This makes AOP simpler to use and configure.

### 3. Main Features

1. **Enable AspectJ auto proxy support**

   + When you add this annotation to a configuration class, Spring turns on AspectJ auto proxy support. You do not need to configure many AOP settings by hand.

2. **Create proxy objects automatically**

   + Spring creates proxy objects automatically to apply aspects, and combines the aspect logic with the target object.

3. **Integrate AspectJ aspects with the Spring container**

   + It is easy to integrate AspectJ aspects with the Spring IoC container, so cross-cutting concerns are managed in separate modules.

4. **Simplify AOP configuration**

   + It removes the tedious steps of configuring AOP by hand and makes development faster.

5. **Scan aspects automatically**

+ Spring scans the AspectJ aspects in the application and applies them to the matching target objects. You do not need to configure aspects by hand.

### 4. Annotation Source

The `EnableAspectJAutoProxy` annotation turns on support for components marked with the AspectJ `@Aspect` annotation. In Spring, it works like the `<aop:aspectj-autoproxy>` element in XML configuration. It lets you integrate AspectJ aspects easily through a configuration class, and control the proxy type and the visibility of the proxy.

```java
/**
 * Enables support for handling components marked with AspectJ's {@code @Aspect} annotation,
 * similar to the functionality of Spring's {@code <aop:aspectj-autoproxy>} XML element.
 * Applied to a @{@link Configuration} class as follows:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableAspectJAutoProxy
 * public class AppConfig {
 *
 *     &#064;Bean
 *     public FooService fooService() {
 *         return new FooService();
 *     }
 *
 *     &#064;Bean
 *     public MyAspect myAspect() {
 *         return new MyAspect();
 *     }
 * }</pre>
 *
 * Here {@code FooService} is a typical POJO component and {@code MyAspect} is an
 * {@code @Aspect}-style aspect:
 *
 * <pre class="code">
 * public class FooService {
 *
 *     // various methods
 * }</pre>
 *
 * <pre class="code">
 * &#064;Aspect
 * public class MyAspect {
 *
 *     &#064;Before("execution(* FooService+.*(..))")
 *     public void advice() {
 *         // advise the FooService methods as appropriate
 *     }
 * }</pre>
 *
 * In the scenario above, {@code @EnableAspectJAutoProxy} ensures that {@code MyAspect}
 * will be properly processed and that {@code FooService} will be proxied, mixing in the advice.
 *
 * <p>Users can control the type of proxy created for {@code FooService} through the {@link #proxyTargetClass()} attribute.
 * The following example enables CGLIB-style "subclass" proxying instead of the default interface-based JDK proxy approach.
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableAspectJAutoProxy(proxyTargetClass=true)
 * public class AppConfig {
 *     // ...
 * }</pre>
 *
 * <p>Note that {@code @Aspect} beans can be component-scanned like any other component.
 * Simply mark the aspect with both {@code @Aspect} and {@code @Component}:
 *
 * <pre class="code">
 * package com.foo;
 *
 * &#064;Component
 * public class FooService { ... }
 *
 * &#064;Aspect
 * &#064;Component
 * public class MyAspect { ... }</pre>
 *
 * Then use the @{@link ComponentScan} annotation to pick them up together:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;ComponentScan("com.foo")
 * &#064;EnableAspectJAutoProxy
 * public class AppConfig {
 *
 *     // no explicit {@code @Bean} definitions needed
 * }</pre>
 *
 * <b>Note: {@code @EnableAspectJAutoProxy} applies only to its local application context,
 * which allows beans to be proxied selectively at different levels.</b>
 * If you need its behavior at several levels, for example in a common root web application context and in any separate {@code DispatcherServlet} application context,
 * redeclare {@code @EnableAspectJAutoProxy} in each separate context.
 *
 * <p>This feature requires {@code aspectjweaver} on the classpath.
 * While {@code spring-aop} generally treats this dependency as optional, it is required for {@code @EnableAspectJAutoProxy} and its infrastructure.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see org.aspectj.lang.annotation.Aspect
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AspectJAutoProxyRegistrar.class)
public @interface EnableAspectJAutoProxy {

    /**
     * Indicates whether subclass-based (CGLIB) proxies are to be created, as opposed to standard Java interface-based proxies. The default is {@code false}.
     */
    boolean proxyTargetClass() default false;

    /**
     * Indicates whether the proxy should be exposed by the AOP framework as a {@code ThreadLocal} for retrieval through the {@link org.springframework.aop.framework.AopContext} class.
     * Off by default, which means there is no guarantee that {@code AopContext} access will work.
     * @since 4.3.1
     */
    boolean exposeProxy() default false;

}
```

### 5. Best Practices

This example uses the `EnableAspectJAutoProxy`
annotation and the Spring annotation-based application context to turn on AspectJ auto proxy support. The program first creates an annotation-based application context, then gets the `MyService`
bean from it and calls its method.

```java
public class EnableAspectJAutoProxyDemo {

    public static void main(String[] args) {
        // Create the annotation-based application context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // Get the MyService bean from the application context
        MyService myService = context.getBean(MyService.class);
        // Call the method of MyService
        myService.foo();
    }
}
```

The `AppConfig` class is a configuration class marked with `@Configuration`. It turns on AspectJ auto proxy support with `@EnableAspectJAutoProxy`
and turns on component scanning with `@ComponentScan`, to find and register Spring components automatically.

```java
@Configuration
@EnableAspectJAutoProxy
@ComponentScan
public class AppConfig {

}
```

The `MyService` class is a service class marked with `@Service`. It has a method named `foo()` that prints the message "foo..." when called.

```java

@Service
public class MyService {

    public void foo() {
        System.out.println("foo...");
    }
}
```

`MyAspect` is a Java class marked with the `@Aspect` annotation, which means it is an aspect. It defines a method named `advice` and uses the `@Before` annotation to set the advice that runs before the target method.

```java
@Aspect
@Component
public class MyAspect {

    @Before("execution(* com.xcs.spring.MyService+.*(..))")
    public void before() {
        System.out.println("Before method execution");
    }
}
```

In the run result, before the `foo()` method of the `MyService` class is called, the aspect advice runs and prints "Before method execution".
Then the `foo()` method runs and prints "foo...".

```java
Before method
execution
foo...
```

### 6. Source Code Analysis

The `org.springframework.context.annotation.AspectJAutoProxyRegistrar#registerBeanDefinitions` method first registers the AspectJ annotation auto proxy creator. Then it reads the attributes of the `@EnableAspectJAutoProxy` annotation. If `proxyTargetClass` is true in the annotation, it forces the use of CGLIB proxies. If `exposeProxy` is true, it forces the proxy object to be exposed as a ThreadLocal.

```java
@Override
public void registerBeanDefinitions(
        AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

    // 1. Register the AspectJ annotation auto proxy creator
    AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry);

    // Get the attributes of the @EnableAspectJAutoProxy annotation
    AnnotationAttributes enableAspectJAutoProxy =
            AnnotationConfigUtils.attributesFor(importingClassMetadata, EnableAspectJAutoProxy.class);
    if (enableAspectJAutoProxy != null) {
        // 2. If the @EnableAspectJAutoProxy annotation sets proxyTargetClass to true, force the use of CGLIB proxies
        if (enableAspectJAutoProxy.getBoolean("proxyTargetClass")) {
            AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
        }
        // 3. If the @EnableAspectJAutoProxy annotation sets exposeProxy to true, force the proxy object to be exposed as a ThreadLocal
        if (enableAspectJAutoProxy.getBoolean("exposeProxy")) {
            AopConfigUtils.forceAutoProxyCreatorToExposeProxy(registry);
        }
    }
}

```

The `org.springframework.aop.config.AopConfigUtils#registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry)` method registers the AspectJ annotation auto proxy creator. An overloaded method accepts an extra parameter, but here the version without the extra parameter is called. The method registers the AspectJ annotation auto proxy creator in the given `BeanDefinitionRegistry` and returns the matching BeanDefinition.

```java
@Nullable
public static BeanDefinition registerAspectJAnnotationAutoProxyCreatorIfNecessary(BeanDefinitionRegistry registry) {
    return registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry, null);
}
```

The `org.springframework.aop.config.AopConfigUtils#registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry,source)` method registers the AspectJ annotation auto proxy creator, and lets you give a source object. It calls the helper method `registerOrEscalateApcAsRequired`, which registers or upgrades the AspectJ annotation auto proxy creator as needed and returns the matching BeanDefinition.

[AnnotationAwareAspectJAutoProxyCreator Source Analysis](../spring-aop-annotationAwareAspectJAutoProxyCreator/README.md)

```java
@Nullable
public static BeanDefinition registerAspectJAnnotationAutoProxyCreatorIfNecessary(
       BeanDefinitionRegistry registry, @Nullable Object source) {

    return registerOrEscalateApcAsRequired(AnnotationAwareAspectJAutoProxyCreator.class, registry, source);
}
```

The `org.springframework.aop.config.AopConfigUtils#registerOrEscalateApcAsRequired` method first checks whether the given `BeanDefinitionRegistry` already has an auto proxy creator definition. If it does, it compares the priority of the existing auto proxy creator with the given class, and upgrades it if the given class has a higher priority. If it does not, it creates a new auto proxy creator definition and registers it in the given `BeanDefinitionRegistry`.

```java
@Nullable
private static BeanDefinition registerOrEscalateApcAsRequired(
       Class<?> cls, BeanDefinitionRegistry registry, @Nullable Object source) {

    // Check whether the BeanDefinitionRegistry object is null
    Assert.notNull(registry, "BeanDefinitionRegistry must not be null");

    // If an auto proxy creator with the same name already exists, upgrade it
    if (registry.containsBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME)) {
       // Get the BeanDefinition of the existing auto proxy creator
       BeanDefinition apcDefinition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
       // Compare the priority of the existing auto proxy creator with the given class
       if (!cls.getName().equals(apcDefinition.getBeanClassName())) {
          int currentPriority = findPriorityForClass(apcDefinition.getBeanClassName());
          int requiredPriority = findPriorityForClass(cls);
          // If the given class has a higher priority than the existing auto proxy creator, upgrade it
          if (currentPriority < requiredPriority) {
             apcDefinition.setBeanClassName(cls.getName());
          }
       }
       return null;
    }

    // If no auto proxy creator with the same name exists, register one
    // Create the BeanDefinition of the new auto proxy creator
    RootBeanDefinition beanDefinition = new RootBeanDefinition(cls);
    beanDefinition.setSource(source);
    beanDefinition.getPropertyValues().add("order", Ordered.HIGHEST_PRECEDENCE);
    beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
    // Register the BeanDefinition of the new auto proxy creator
    registry.registerBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME, beanDefinition);
    return beanDefinition;
}
```

The `org.springframework.aop.config.AopConfigUtils#forceAutoProxyCreatorToUseClassProxying` method forces the auto proxy creator to use class-based proxies. It first checks whether the given `BeanDefinitionRegistry` already has an auto proxy creator definition. If it does, it gets the definition and sets its `proxyTargetClass` property to `true`, which means class-based proxies are used.

```java
public static void forceAutoProxyCreatorToUseClassProxying(BeanDefinitionRegistry registry) {
    // Check whether the BeanDefinition of the auto proxy creator exists
    if (registry.containsBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME)) {
        // Get the BeanDefinition of the auto proxy creator
        BeanDefinition definition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
        // Set the proxyTargetClass property to true, to use class-based proxies
        definition.getPropertyValues().add("proxyTargetClass", Boolean.TRUE);
    }
}
```

The `org.springframework.aop.config.AopConfigUtils#forceAutoProxyCreatorToExposeProxy` method forces the auto proxy creator to expose the proxy object. It first checks whether the given `BeanDefinitionRegistry` already has an auto proxy creator definition. If it does, it gets the definition and sets its `exposeProxy` property to `true`, which means the proxy object is exposed.

```java
public static void forceAutoProxyCreatorToExposeProxy(BeanDefinitionRegistry registry) {
    // Check whether the BeanDefinition of the auto proxy creator exists
    if (registry.containsBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME)) {
        // Get the BeanDefinition of the auto proxy creator
        BeanDefinition definition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
        // Set the exposeProxy property to true, to expose the proxy object
        definition.getPropertyValues().add("exposeProxy", Boolean.TRUE);
    }
}

```
