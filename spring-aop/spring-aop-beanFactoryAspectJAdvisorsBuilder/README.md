## BeanFactoryAspectJAdvisorsBuilder

- [BeanFactoryAspectJAdvisorsBuilder](#beanfactoryaspectjadvisorsbuilder)
  - [1. Overview](#1-overview)
  - [2. Description](#2-description)
  - [3. Main Features](#3-main-features)
  - [4. Best Practices](#4-best-practices)
  - [5. Sequence Diagram](#5-sequence-diagram)
  - [6. Source Code Analysis](#6-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

`BeanFactoryAspectJAdvisorsBuilder` is an important class in Spring AOP. It parses the aspects marked with AspectJ annotations in the application and turns them into Spring AOP Advisors. This makes annotation-based aspect programming work.

### 3. Main Features

1. **Scan AspectJ annotations**

   + This class scans the classes in the application and looks for classes with AspectJ annotations, such as `@Aspect`. It recognizes these classes and turns them into Spring AOP Advisors.

2. **Parse aspects and advice**

   + When it finds a class with AspectJ annotations, `BeanFactoryAspectJAdvisorsBuilder` parses it to find the aspect and the advice defined in the aspect.

3. **Create Advisors**

   + From the parsed aspect and advice data, this class creates the matching Advisors. An Advisor holds the aspect logic and the join point (pointcut) data. It is applied to the method calls of the target object.

4. **Register Advisors**

   + Finally, `BeanFactoryAspectJAdvisorsBuilder` registers the created Advisors in the Spring AOP framework, so they take effect while the application runs.

### 4. Best Practices

This example uses an annotation-based application context to get the `MyService` bean and call its `foo()`
method. First, it creates an `AnnotationConfigApplicationContext` instance and passes `AppConfig.class`
to set up the context. Then it gets the `MyService` bean with `context.getBean(MyService.class)`
and calls its `foo()` method.

```java
public class BeanFactoryAspectJAdvisorsBuilderDemo {

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

It is marked as a Spring component with the @Aspect and @Component annotations, and it defines a before advice (Before advice) that runs before the foo
method of the com.xcs.spring.MyService class.

```java
@Aspect
@Component
class MyAspect {

    @Before("execution(* com.xcs.spring.MyService.foo(..))")
    public void before() {
        System.out.println("Before method execution");
    }
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

In the run result, before the `foo()` method of the `MyService` class is called, the aspect advice runs and prints "Before method execution".
Then the `foo()` method runs and prints "foo...".

```java
Before method
execution
foo...
```

### 5. Sequence Diagram

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
    AbstractAdvisorAutoProxyCreator->>AnnotationAwareAspectJAutoProxyCreator: findCandidateAdvisors()
    Note over AbstractAdvisorAutoProxyCreator,AnnotationAwareAspectJAutoProxyCreator: Find candidate Advisors
    AnnotationAwareAspectJAutoProxyCreator->>BeanFactoryAspectJAdvisorsBuilder: buildAspectJAdvisors()
    Note over AnnotationAwareAspectJAutoProxyCreator,BeanFactoryAspectJAdvisorsBuilder: Build AspectJ Advisors
    BeanFactoryAspectJAdvisorsBuilder->>AbstractAutoProxyCreator: Return advisors
~~~

### 6. Source Code Analysis

The `org.springframework.aop.aspectj.annotation.BeanFactoryAspectJAdvisorsBuilder#buildAspectJAdvisors` method finds the aspect beans marked with AspectJ annotations in the current bean factory and turns them into a list of Spring AOP Advisors. It loops over all bean names, recognizes the aspect beans, and creates the AspectJ Advisors based on the instantiation model (singleton or prototype). It also caches the Advisors of singleton aspects to improve performance.

```java
/**
 * Find the aspect beans marked with AspectJ annotations in the current bean factory, and return the list of Spring AOP Advisors that represent them.
 * <p>Creates one Spring Advisor for each AspectJ advice method.
 * @return a list of {@link org.springframework.aop.Advisor} beans
 * @see #isEligibleBean
 */
public List<Advisor> buildAspectJAdvisors() {
    // If the list of aspect bean names is empty, look them up
    List<String> aspectNames = this.aspectBeanNames;

    if (aspectNames == null) {
        synchronized (this) {
            aspectNames = this.aspectBeanNames;
            if (aspectNames == null) {
                // Initialize the list of aspect Advisors and the list of aspect bean names
                List<Advisor> advisors = new ArrayList<>();
                aspectNames = new ArrayList<>();
                // Get all bean names in the current bean factory
                String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                        this.beanFactory, Object.class, true, false);
                // Loop over all bean names
                for (String beanName : beanNames) {
                    // Check whether the bean is eligible
                    if (!isEligibleBean(beanName)) {
                        continue;
                    }
                    // Get the type of the bean
                    Class<?> beanType = this.beanFactory.getType(beanName, false);
                    // If the type cannot be found, skip it
                    if (beanType == null) {
                        continue;
                    }
                    // Check whether the bean is an aspect
                    if (this.advisorFactory.isAspect(beanType)) {
                        // Add the aspect bean name to the list
                        aspectNames.add(beanName);
                        // Get the aspect metadata
                        AspectMetadata amd = new AspectMetadata(beanType, beanName);
                        // Check the instantiation model of the aspect
                        if (amd.getAjType().getPerClause().getKind() == PerClauseKind.SINGLETON) {
                            // Singleton model
                            // Create the aspect instance factory for the singleton model
                            MetadataAwareAspectInstanceFactory factory =
                                    new BeanFactoryAspectInstanceFactory(this.beanFactory, beanName);
                            // Get the Advisors of the aspect
                            List<Advisor> classAdvisors = this.advisorFactory.getAdvisors(factory);
                            // Cache the Advisors of the singleton aspect
                            if (this.beanFactory.isSingleton(beanName)) {
                                this.advisorsCache.put(beanName, classAdvisors);
                            } else {
                                this.aspectFactoryCache.put(beanName, factory);
                            }
                            advisors.addAll(classAdvisors);
                        } else {
                            // Prototype model
                            if (this.beanFactory.isSingleton(beanName)) {
                                // If the aspect instantiation model is prototype but the bean is a singleton, throw an exception
                                throw new IllegalArgumentException("Bean with name '" + beanName +
                                        "' is a singleton, but aspect instantiation model is not singleton");
                            }
                            // Create the aspect instance factory for the prototype model
                            MetadataAwareAspectInstanceFactory factory =
                                    new PrototypeAspectInstanceFactory(this.beanFactory, beanName);
                            // Cache the aspect instance factory
                            this.aspectFactoryCache.put(beanName, factory);
                            // Get the Advisors of the aspect
                            advisors.addAll(this.advisorFactory.getAdvisors(factory));
                        }
                    }
                }
                // Cache the list of aspect bean names
                this.aspectBeanNames = aspectNames;
                return advisors;
            }
        }
    }

    // If the list of aspect bean names is empty, return an empty list
    if (aspectNames.isEmpty()) {
        return Collections.emptyList();
    }
    // Create a list to hold all Advisors
    List<Advisor> advisors = new ArrayList<>();
    // Loop over the list of aspect bean names
    for (String aspectName : aspectNames) {
        // Get the Advisors from the cache
        List<Advisor> cachedAdvisors = this.advisorsCache.get(aspectName);
        if (cachedAdvisors != null) {
            // If the cache has Advisors, add them to the result list
            advisors.addAll(cachedAdvisors);
        } else {
            // If the cache has no Advisors, get them from the aspect instance factory
            MetadataAwareAspectInstanceFactory factory = this.aspectFactoryCache.get(aspectName);
            advisors.addAll(this.advisorFactory.getAdvisors(factory));
        }
    }
    return advisors;
}
```

The `org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactory#isAspect` method checks whether the given class is an aspect. It first checks whether the class has an AspectJ annotation, and then checks that the class was not compiled by the AspectJ compiler. If both conditions are met, it returns true, which means the class is an aspect. Otherwise it returns false.

```java
/**
 * Check whether the given class is an aspect.
 * @param clazz the class to check
 * @return true if the class is an aspect, otherwise false
 */
@Override
public boolean isAspect(Class<?> clazz) {
    // Check that the class has an AspectJ annotation and was not compiled by the AspectJ compiler
    return (hasAspectAnnotation(clazz) && !compiledByAjc(clazz));
}
```

The `org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactory#hasAspectAnnotation` method checks whether the given class has an AspectJ annotation.

```java
/**
 * Check whether the given class has an AspectJ annotation.
 * @param clazz the class to check
 * @return true if the class has an AspectJ annotation, otherwise false
 */
private boolean hasAspectAnnotation(Class<?> clazz) {
    // Use the AnnotationUtils.findAnnotation method to find the Aspect annotation on the class
    return (AnnotationUtils.findAnnotation(clazz, Aspect.class) != null);
}
```



