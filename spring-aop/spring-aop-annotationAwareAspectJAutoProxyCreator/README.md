## AnnotationAwareAspectJAutoProxyCreator

- [AnnotationAwareAspectJAutoProxyCreator](#annotationawareaspectjautoproxycreator)
  - [1. Overview](#1-overview)
  - [2. Description](#2-description)
  - [3. Main Features](#3-main-features)
  - [4. Class Diagram](#4-class-diagram)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
  - [7. Source Code Analysis](#7-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

`AnnotationAwareAspectJAutoProxyCreator` is a key class in Spring AOP. It finds aspect classes marked with the `@Aspect` annotation and creates proxies for the beans of a Spring application. This weaves the aspect logic in automatically and supports annotation-driven aspect-oriented programming.

### 3. Main Features

1. **Automatic proxy creation** 

   + Finds the aspect classes marked with `@Aspect` in the application context and creates proxy objects automatically. The aspect logic is then woven into the method calls of the target beans.

2. **Aspect logic weaving** 

   + Weaves the aspect logic into the method calls of the target beans. This gives aspect actions such as running before or after a method, or when an exception is thrown, to cover all kinds of cross-cutting concerns.

3. **Support for annotation-driven aspects** 

   + Supports annotation-based aspect programming. With annotations such as `@Aspect`, developers can define aspect classes and aspect logic more easily.

4. **Flexible aspect configuration** 

   + Lets us configure aspects with annotations, without declaring them in XML configuration files. This makes aspect configuration more flexible and convenient.
   
5. **AOP feature integration** 

   + Brings AspectJ aspect features into the Spring framework. We can use AspectJ-style aspect programming in a Spring application, which makes cross-cutting concerns more modular and reusable.

### 4. Class Diagram

~~~mermaid
classDiagram
direction BT
class AbstractAdvisorAutoProxyCreator
class AbstractAutoProxyCreator
class AnnotationAwareAspectJAutoProxyCreator
class AopInfrastructureBean {
<<Interface>>

}
class AspectJAwareAdvisorAutoProxyCreator
class Aware {
<<Interface>>

}
class BeanClassLoaderAware {
<<Interface>>

}
class BeanFactoryAware {
<<Interface>>

}
class BeanPostProcessor {
<<Interface>>

}
class InstantiationAwareBeanPostProcessor {
<<Interface>>

}
class ProxyConfig
class ProxyProcessorSupport
class SmartInstantiationAwareBeanPostProcessor {
<<Interface>>

}

AbstractAdvisorAutoProxyCreator  -->  AbstractAutoProxyCreator 
AbstractAutoProxyCreator  ..>  BeanFactoryAware 
AbstractAutoProxyCreator  -->  ProxyProcessorSupport 
AbstractAutoProxyCreator  ..>  SmartInstantiationAwareBeanPostProcessor 
AnnotationAwareAspectJAutoProxyCreator  -->  AspectJAwareAdvisorAutoProxyCreator 
AspectJAwareAdvisorAutoProxyCreator  -->  AbstractAdvisorAutoProxyCreator 
BeanClassLoaderAware  -->  Aware 
BeanFactoryAware  -->  Aware 
InstantiationAwareBeanPostProcessor  -->  BeanPostProcessor 
ProxyProcessorSupport  ..>  AopInfrastructureBean 
ProxyProcessorSupport  ..>  BeanClassLoaderAware 
ProxyProcessorSupport  -->  ProxyConfig 
SmartInstantiationAwareBeanPostProcessor  -->  InstantiationAwareBeanPostProcessor 
~~~

### 5. Best Practices

This example uses `EnableAspectJAutoProxy`
and an annotation-based application context to turn on AspectJ auto proxy. In the program, it first creates an annotation-based application context. Then it gets the `MyService`
bean from the context and calls its method.

```java
public class EnableAspectJAutoProxyDemo {

    public static void main(String[] args) {
        // Create an annotation-based application context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // Get the MyService bean from the application context
        MyService myService = context.getBean(MyService.class);
        // Call the method of MyService
        myService.foo();
    }
}
```

The `AppConfig` class is a configuration class marked with `@Configuration`. It turns on AspectJ
auto proxy with `@EnableAspectJAutoProxy`, and turns on component scanning with `@ComponentScan` to find and register Spring components automatically.

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

`MyAspect` is a Java class marked with `@Aspect`, which means it is an aspect. In this class, a method named `advice` is defined and marked with `@Before`
to specify the advice that runs before the target method.

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

This is the result. Before the `foo()` method of the `MyService` class was called, an aspect advice ran successfully and printed the message "Before method execution"
Then the `foo()` method ran and printed the message "foo...".

```java
Before method
execution
foo...
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
autonumber
BeanFactory->>AbstractAutoProxyCreator:postProcessAfterInitialization()
Note over BeanFactory,AbstractAutoProxyCreator: BeanFactory calls the post-initialization method
AbstractAutoProxyCreator->>AbstractAutoProxyCreator:wrapIfNecessary()
Note over AbstractAutoProxyCreator,AbstractAutoProxyCreator: Check conditions and create the proxy
AbstractAutoProxyCreator->>AbstractAdvisorAutoProxyCreator:getAdvicesAndAdvisorsForBean()
Note over AbstractAutoProxyCreator,AbstractAdvisorAutoProxyCreator: Get and return the array of applicable Advisors
AbstractAdvisorAutoProxyCreator->>AbstractAdvisorAutoProxyCreator:findEligibleAdvisors()
Note over AbstractAdvisorAutoProxyCreator,AbstractAdvisorAutoProxyCreator: Find and extend the list of applicable Advisors
AbstractAdvisorAutoProxyCreator->>AnnotationAwareAspectJAutoProxyCreator:findCandidateAdvisors()
Note over AbstractAdvisorAutoProxyCreator,AnnotationAwareAspectJAutoProxyCreator: Add Spring and AspectJ Advisors and merge them into the candidate Advisor list
AnnotationAwareAspectJAutoProxyCreator->>AbstractAdvisorAutoProxyCreator:super.findCandidateAdvisors()
Note over AnnotationAwareAspectJAutoProxyCreator,AbstractAdvisorAutoProxyCreator: Get the candidate Advisors for auto proxy
AbstractAdvisorAutoProxyCreator->>BeanFactoryAdvisorRetrievalHelper:findAdvisorBeans()
Note over AbstractAdvisorAutoProxyCreator,BeanFactoryAdvisorRetrievalHelper: Get all eligible Advisor beans in the current bean factory
AnnotationAwareAspectJAutoProxyCreator->>BeanFactoryAspectJAdvisorsBuilder:buildAspectJAdvisors()
Note over AnnotationAwareAspectJAutoProxyCreator,BeanFactoryAspectJAdvisorsBuilder: Get the AspectJ annotated aspects and create Advisors
AnnotationAwareAspectJAutoProxyCreator->>AbstractAdvisorAutoProxyCreator:Return Advisors
AbstractAdvisorAutoProxyCreator->>AbstractAdvisorAutoProxyCreator:findAdvisorsThatCanApply()
Note over AbstractAdvisorAutoProxyCreator,AbstractAdvisorAutoProxyCreator: Find the applicable candidate Advisors and set up the proxy
AbstractAdvisorAutoProxyCreator->>AopUtils:findAdvisorsThatCanApply()
Note over AbstractAdvisorAutoProxyCreator,AopUtils: Filter the applicable Advisors and add them to the list.
AopUtils->>AopUtils:canApply(advisor,targetClass,hasIntroductions)
Note over AopUtils,AopUtils: Check whether the Advisor applies to the target class.
AopUtils->>AopUtils:canApply(pc,targetClass,hasIntroductions)
Note over AopUtils,AopUtils: Check whether the pointcut applies to the target class.
AbstractAdvisorAutoProxyCreator->>AspectJAwareAdvisorAutoProxyCreator:extendAdvisors()
Note over AbstractAdvisorAutoProxyCreator,AspectJAwareAdvisorAutoProxyCreator: Add AspectJ support to the Advisor chain.
AspectJAwareAdvisorAutoProxyCreator->>AspectJProxyUtils:makeAdvisorChainAspectJCapableIfNecessary()
Note over AspectJAwareAdvisorAutoProxyCreator,AspectJProxyUtils: Add AspectJ support to the Advisor chain.
AbstractAdvisorAutoProxyCreator->>AbstractAutoProxyCreator:Return interceptors
AbstractAutoProxyCreator->>AbstractAutoProxyCreator:createProxy()
Note over AbstractAutoProxyCreator,AbstractAutoProxyCreator: The process of creating the AOP proxy object.
AbstractAutoProxyCreator->>ProxyFactory:new ProxyFactory()
ProxyFactory->>AbstractAutoProxyCreator:Return proxyFactory
AbstractAutoProxyCreator->>ProxyFactory:getProxy()
ProxyFactory->>AbstractAutoProxyCreator:Return the proxy object
AbstractAutoProxyCreator->>BeanFactory:Return the proxy object
~~~

### 7. Source Code Analysis

In the method `org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization`, the bean is post-processed after initialization. It checks whether a proxy is needed for the bean. If so, it creates the proxy object with the configured interceptors, which gives the AOP feature.

```java
/**
 * Create a proxy with the configured interceptors if the bean is identified as one to proxy by the subclass.
 * @see #getAdvicesAndAdvisorsForBean
 */
@Override
public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) {
    if (bean != null) {
        // Get the cache key
        Object cacheKey = getCacheKey(bean.getClass(), beanName);
        // If the early proxy reference set has this bean and it is not the same reference, wrap it
        if (this.earlyProxyReferences.remove(cacheKey) != bean) {
            return wrapIfNecessary(bean, beanName, cacheKey);
        }
    }
    // Return the bean
    return bean;
}
```

In the method `org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary`, it first checks whether the bean name exists and belongs to a target source bean. If so, it returns the original bean instance. Then it checks the cache for beans already marked as not needing a proxy. If found, it also returns the original bean instance. Next, it checks whether the bean class is an infrastructure class or should be skipped. If so, it marks the bean as not needing a proxy and returns the original bean instance. Finally, if there is advice (interceptors), it creates the proxy object and returns it. Otherwise, it marks the bean as not needing a proxy and returns the original bean instance.

```java
/**
 * Wrap the given bean if necessary, that is, if it is eligible to be proxied.
 * @param bean the original bean instance
 * @param beanName the name of the bean
 * @param cacheKey the cache key for metadata access
 * @return a proxy wrapping the bean, or the original bean instance
 */
protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
    // If beanName is not empty and the bean is already contained in the target source beans, return the original bean instance directly
    if (StringUtils.hasLength(beanName) && this.targetSourcedBeans.contains(beanName)) {
        return bean;
    }
    // If the cache already marks this bean as not needing a proxy, return the original bean instance directly
    if (Boolean.FALSE.equals(this.advisedBeans.get(cacheKey))) {
        return bean;
    }
    // If the bean class is an infrastructure class, or the bean class should be skipped, return the original bean instance directly
    if (isInfrastructureClass(bean.getClass()) || shouldSkip(bean.getClass(), beanName)) {
        this.advisedBeans.put(cacheKey, Boolean.FALSE);
        return bean;
    }

    // If there is advice, create the proxy
    Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
    if (specificInterceptors != DO_NOT_PROXY) {
        this.advisedBeans.put(cacheKey, Boolean.TRUE);
        Object proxy = createProxy(
                bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
        this.proxyTypes.put(cacheKey, proxy.getClass());
        return proxy;
    }

    // No advice, so mark the bean as not needing a proxy
    this.advisedBeans.put(cacheKey, Boolean.FALSE);
    return bean;
}
```

In the method `org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#getAdvicesAndAdvisorsForBean`, it first calls the `findEligibleAdvisors` method to find the Advisors that apply to the bean. Then it converts them into an array and returns it. If no applicable Advisors are found, it returns the special value `DO_NOT_PROXY`.

```java
/**
 * Get the Advisors that apply to the given bean.
 * @param beanClass the class of the bean
 * @param beanName the name of the bean
 * @param targetSource the target source
 * @return an array that holds the Advisors, or DO_NOT_PROXY if no applicable Advisors are found
 */
@Override
@Nullable
protected Object[] getAdvicesAndAdvisorsForBean(
        Class<?> beanClass, String beanName, @Nullable TargetSource targetSource) {

    // Find the Advisors that apply to the bean
    List<Advisor> advisors = findEligibleAdvisors(beanClass, beanName);
    // If no applicable Advisors are found, return DO_NOT_PROXY
    if (advisors.isEmpty()) {
        return DO_NOT_PROXY;
    }
    // Return the array of Advisors
    return advisors.toArray();
}
```

In the method `org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#findEligibleAdvisors`, it finds all eligible Advisors for auto proxying the given class. It first calls the findCandidateAdvisors method to find the candidate Advisors. Then it calls the findAdvisorsThatCanApply method to keep the Advisors that can apply to the current class. Next, it calls the extendAdvisors method to extend the Advisor list, so that the proper interceptors and advice are applied. Finally, if there are applicable Advisors, it sorts the list and returns it. If there are none, it returns an empty list.

```java
/**
 * Find all eligible Advisors for auto proxying this class.
 * @param beanClass the class to find Advisors for
 * @param beanName the name of the bean currently being proxied
 * @return an empty list, not null, if there are no pointcuts or interceptors
 * @see #findCandidateAdvisors
 * @see #sortAdvisors
 * @see #extendAdvisors
 */
protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
    // Find the candidate Advisors
    List<Advisor> candidateAdvisors = findCandidateAdvisors();
    // Find the Advisors that can apply
    List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
    // Extend the Advisors
    extendAdvisors(eligibleAdvisors);
    // If there are applicable Advisors, sort them
    if (!eligibleAdvisors.isEmpty()) {
        eligibleAdvisors = sortAdvisors(eligibleAdvisors);
    }
    return eligibleAdvisors;
}
```

In the method `org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator#findCandidateAdvisors`, the parent method `findCandidateAdvisors()` is overridden. It first calls the parent method to get all Spring Advisors. Then it uses `aspectJAdvisorsBuilder` to build the Advisors for all AspectJ aspects, adds them to the `advisors` list, and returns the list.

[BeanFactoryAspectJAdvisorsBuilder Source Code Analysis](../spring-aop-beanFactoryAspectJAdvisorsBuilder/README.md)

```java
@Override
protected List<Advisor> findCandidateAdvisors() {
    // Add all the Spring Advisors found according to the superclass rules.
    List<Advisor> advisors = super.findCandidateAdvisors();
    // Build the Advisors for all AspectJ aspects in the bean factory.
    if (this.aspectJAdvisorsBuilder != null) {
        advisors.addAll(this.aspectJAdvisorsBuilder.buildAspectJAdvisors());
    }
    return advisors;
}
```

In the method `org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#findCandidateAdvisors`, the method `BeanFactoryAdvisorRetrievalHelper.findAdvisorBeans()` is called to get the list of candidate Advisors.

[BeanFactoryAdvisorRetrievalHelper Source Code Analysis](../spring-aop-beanFactoryAdvisorRetrievalHelper/README.md)

```java
/**
 * Find all candidate Advisors to use in auto proxying.
 * @return the list of candidate Advisors
 */
protected List<Advisor> findCandidateAdvisors() {
    Assert.state(this.advisorRetrievalHelper != null, "No BeanFactoryAdvisorRetrievalHelper available");
    return this.advisorRetrievalHelper.findAdvisorBeans();
}
```

In the method `org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#findAdvisorsThatCanApply`, the given candidate Advisors are searched to find all Advisors that can apply to the specified bean. It sets the name of the bean currently being proxied, and then looks for all Advisors that apply to the specified bean. When the search is done, it clears the name of the bean currently being proxied.

```java
/**
 * Search the given candidate Advisors to find all Advisors that can apply to the specified bean.
 * @param candidateAdvisors the list of candidate Advisors
 * @param beanClass the class of the target bean
 * @param beanName the name of the target bean
 * @return the list of applicable Advisors
 * @see ProxyCreationContext#getCurrentProxiedBeanName()
 */
protected List<Advisor> findAdvisorsThatCanApply(
        List<Advisor> candidateAdvisors, Class<?> beanClass, String beanName) {
    
    // Set the name of the bean currently being proxied
    ProxyCreationContext.setCurrentProxiedBeanName(beanName);
    try {
        // Find the Advisors that apply to the specified bean
        return AopUtils.findAdvisorsThatCanApply(candidateAdvisors, beanClass);
    }
    finally {
        // Clear the name of the bean currently being proxied
        ProxyCreationContext.setCurrentProxiedBeanName(null);
    }
}
```

In the method `org.springframework.aop.support.AopUtils#findAdvisorsThatCanApply`, the sub-list of candidate Advisors that apply to the given class is determined. It loops over the given Advisor list. First, it adds the introduction Advisors that can apply to the class to the result list. Then it checks whether any introduction Advisors exist. If so, it skips them. If not, it keeps looping over the list and adds the other Advisors that can apply to the class to the result list. In the end, it returns the sub-list that holds all Advisors that can apply to the given class.

```java
/**
 * Determine the sub-list of the {@code candidateAdvisors} list that is applicable to the given class.
 * @param candidateAdvisors the Advisors to evaluate
 * @param clazz the target class
 * @return the sub-list of Advisors that can apply to the given class
 * (may be the original list)
 */
public static List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> clazz) {
    // If the candidate Advisor list is empty, return it directly
    if (candidateAdvisors.isEmpty()) {
       return candidateAdvisors;
    }
    // Create a list to store the Advisors that apply to the given class
    List<Advisor> eligibleAdvisors = new ArrayList<>();
    // Loop over the candidate Advisor list
    for (Advisor candidate : candidateAdvisors) {
       // If the candidate Advisor is an introduction Advisor and can apply to the given class, add it to the result list
       if (candidate instanceof IntroductionAdvisor && canApply(candidate, clazz)) {
          eligibleAdvisors.add(candidate);
       }
    }
    // Check whether there are introduction Advisors
    boolean hasIntroductions = !eligibleAdvisors.isEmpty();
    // Keep looping over the candidate Advisor list
    for (Advisor candidate : candidateAdvisors) {
       // If the candidate Advisor is an introduction Advisor, skip it
       if (candidate instanceof IntroductionAdvisor) {
          // Already handled
          continue;
       }
       // If the candidate Advisor can apply to the given class, add it to the result list
       if (canApply(candidate, clazz, hasIntroductions)) {
          eligibleAdvisors.add(candidate);
       }
    }
    return eligibleAdvisors;
}
```

In the method `org.springframework.aop.support.AopUtils#canApply(org.springframework.aop.Advisor, java.lang.Class<?>, boolean)`, it checks whether the given Advisor can apply to the specified class. It first checks whether the Advisor is an introduction Advisor. If so, it uses the class filter to decide whether it can apply to the target class. If the Advisor is not an introduction Advisor but a pointcut Advisor, it uses the pointcut to decide. If the Advisor is neither, it assumes it applies to the target class.

```java
/**
 * Check whether the given Advisor can apply to the specified class.
 * <p>This is an important test, as it can be used to optimize out an Advisor for a class.
 * This version also takes introductions into account (for IntroductionAwareMethodMatchers).
 * @param advisor the Advisor to check
 * @param targetClass the class we are testing
 * @param hasIntroductions whether the Advisor chain contains any introductions
 * @return whether the pointcut can apply to any method
 */
public static boolean canApply(Advisor advisor, Class<?> targetClass, boolean hasIntroductions) {
    // If the Advisor is an introduction Advisor, use the class filter to decide whether it can apply to the target class
    if (advisor instanceof IntroductionAdvisor) {
       return ((IntroductionAdvisor) advisor).getClassFilter().matches(targetClass);
    }
    // If the Advisor is a pointcut Advisor, use the pointcut to decide whether it can apply to the target class
    else if (advisor instanceof PointcutAdvisor) {
       PointcutAdvisor pca = (PointcutAdvisor) advisor;
       return canApply(pca.getPointcut(), targetClass, hasIntroductions);
    }
    // Otherwise, assume it applies to the target class
    else {
       // It has no pointcut, so we assume it applies.
       return true;
    }
}
```

In the method `org.springframework.aop.support.AopUtils#canApply(org.springframework.aop.Pointcut, java.lang.Class<?>, boolean)`, it decides whether the given pointcut can apply to the specified class. First, it checks whether the class filter of the pointcut matches the target class. If not, it returns false. If the class filter matches, it checks whether the method matcher is MethodMatcher.TRUE. If so, the pointcut applies to any method of the target class, and it returns true. If not, it loops over the target class and all its interfaces, and checks whether the methods in each class match the pointcut. If a matching method is found, it returns true. If none is found, it returns false.

```java
/**
 * Check whether the given pointcut can apply to the specified class.
 * <p>This is an important test, as it can be used to optimize out a pointcut for a class.
 * @param pc the static or dynamic pointcut to check
 * @param targetClass the class to test
 * @param hasIntroductions whether the Advisor chain contains any introductions
 * @return whether the pointcut can apply to any method
 */
public static boolean canApply(Pointcut pc, Class<?> targetClass, boolean hasIntroductions) {
	Assert.notNull(pc, "Pointcut must not be null");
    // First check whether the class filter matches the target class
    if (!pc.getClassFilter().matches(targetClass)) {
        return false;
    }

    MethodMatcher methodMatcher = pc.getMethodMatcher();
    // If the method matcher is MethodMatcher.TRUE, there is no need to loop over the methods. Return true directly
    if (methodMatcher == MethodMatcher.TRUE) {
        // If we match any method, there is no need to loop over the methods...
        return true;
    }

    IntroductionAwareMethodMatcher introductionAwareMethodMatcher = null;
    if (methodMatcher instanceof IntroductionAwareMethodMatcher) {
        introductionAwareMethodMatcher = (IntroductionAwareMethodMatcher) methodMatcher;
    }

    // Get the set of the target class and all its interfaces
    Set<Class<?>> classes = new LinkedHashSet<>();
    // If the target class is not a proxy class, add it to the class set
    if (!Proxy.isProxyClass(targetClass)) {
        classes.add(ClassUtils.getUserClass(targetClass));
    }
    // Add all interfaces of the target class to the class set
    classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));

    // Loop over the class set
    for (Class<?> clazz : classes) {
        // Get all methods declared in the class
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
        // Loop over the methods
        for (Method method : methods) {
            // If there is an introduction-aware method matcher and the method matches, return true;
            // otherwise, if the method matcher matches the method, return true
            if (introductionAwareMethodMatcher != null ?
                    introductionAwareMethodMatcher.matches(method, targetClass, hasIntroductions) :
                    methodMatcher.matches(method, targetClass)) {
                return true;
            }
        }
    }

    return false;
}
```

In the method `org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator#extendAdvisors`, `ExposeInvocationInterceptor` is added to the start of the advice chain. This extra step is needed, especially when using AspectJ pointcut expressions and AspectJ-style advice.

```java
/**
 * Add the {@link ExposeInvocationInterceptor} to the start of the advice chain.
 * <p>This extra advice is needed when using AspectJ pointcut expressions and AspectJ-style advice.
 * @Override
 * @param candidateAdvisors the list of candidate Advisors
 */
@Override
protected void extendAdvisors(List<Advisor> candidateAdvisors) {
    // Make the Advisor chain support AspectJ if needed
    AspectJProxyUtils.makeAdvisorChainAspectJCapableIfNecessary(candidateAdvisors);
}
```

In the method `org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#createProxy`, it first checks whether the `BeanFactory` is a `ConfigurableListableBeanFactory`. If so, it calls `AutoProxyUtils.exposeTargetClass` to expose the target class. Then it creates a `ProxyFactory` instance and sets it up from the current proxy configuration. Depending on whether the proxy-target-class flag is on, it sets proxy target class to true or false. Next, it builds all the Advisors for the bean, adds them to the `ProxyFactory`, and sets the target source to the preconfigured `TargetSource`. Finally, it uses the `ProxyFactory` to get the proxy instance, based on the current proxy configuration and the proxy class loader, and returns it.

```java
/**
 * Create an AOP proxy for the given bean.
 * @param beanClass the class of the bean
 * @param beanName the name of the bean
 * @param specificInterceptors the set of interceptors that apply to this bean (may be empty, but not null)
 * @param targetSource the TargetSource for the proxy, already preconfigured to access the bean
 * @return the AOP proxy for the bean
 * @see #buildAdvisors
 */
protected Object createProxy(Class<?> beanClass, @Nullable String beanName,
                              @Nullable Object[] specificInterceptors, TargetSource targetSource) {

    // If beanFactory is a ConfigurableListableBeanFactory, expose the target class
    if (this.beanFactory instanceof ConfigurableListableBeanFactory) {
        AutoProxyUtils.exposeTargetClass((ConfigurableListableBeanFactory) this.beanFactory, beanName, beanClass);
    }

    // Create the ProxyFactory instance
    ProxyFactory proxyFactory = new ProxyFactory();
    proxyFactory.copyFrom(this);

    // If the proxy target class is needed, set it to true
    if (proxyFactory.isProxyTargetClass()) {
        // Handle the JDK proxy target explicitly (for the introduction advice case)
        if (Proxy.isProxyClass(beanClass)) {
            // Introductions must be allowed. Do not just set the interfaces as the proxied interfaces.
            for (Class<?> ifc : beanClass.getInterfaces()) {
                proxyFactory.addInterface(ifc);
            }
        }
    } else {
        // The proxy-target-class flag is not forced, so apply the default checks...
        if (shouldProxyTargetClass(beanClass, beanName)) {
            proxyFactory.setProxyTargetClass(true);
        } else {
            evaluateProxyInterfaces(beanClass, proxyFactory);
        }
    }

    // Build the Advisor array
    Advisor[] advisors = buildAdvisors(beanName, specificInterceptors);
    // Add the Advisors to the ProxyFactory
    proxyFactory.addAdvisors(advisors);
    proxyFactory.setTargetSource(targetSource);
    // Customize the ProxyFactory
    customizeProxyFactory(proxyFactory);

    proxyFactory.setFrozen(this.freezeProxy);
    if (advisorsPreFiltered()) {
        proxyFactory.setPreFiltered(true);
    }

    // If the bean class is not loaded locally in the overriding class loader, use the original ClassLoader
    ClassLoader classLoader = getProxyClassLoader();
    if (classLoader instanceof SmartClassLoader && classLoader != beanClass.getClassLoader()) {
        classLoader = ((SmartClassLoader) classLoader).getOriginalClassLoader();
    }
    // Get the proxy instance
    return proxyFactory.getProxy(classLoader);
}
```
