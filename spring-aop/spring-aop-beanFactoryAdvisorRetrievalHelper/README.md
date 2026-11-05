## BeanFactoryAdvisorRetrievalHelper

- [BeanFactoryAdvisorRetrievalHelper](#beanfactoryadvisorretrievalhelper)
  - [1. Overview](#1-overview)
  - [2. Description](#2-description)
  - [3. Main Features](#3-main-features)
  - [4. Best Practices](#4-best-practices)
  - [5. Sequence Diagram](#5-sequence-diagram)
  - [6. Source Code Analysis](#6-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `BeanFactoryAdvisorRetrievalHelper` class is a helper in the Spring AOP framework. It finds Advisors in the bean factory. These Advisors define the aspect logic, so the advice can be woven into the method calls of target beans.

### 3. Main Features

1. **Help retrieve Advisors**
   + Helps the Spring AOP framework find the Advisors related to a target bean in the bean factory of the application.
   
2. **Resolve Advisor bean names**
   + Resolves the bean names of Advisors in the Spring container, and gets the matching Advisor instances from the bean factory by name.
   
3. **Support different types of Advisors**
   + Supports different types of Advisors, including before advice (BeforeAdvice), after advice (AfterAdvice), and around advice (AroundAdvice), and applies them correctly to the target bean.
   
4. **Help create proxies**

   + Helps the Spring container create proxy objects and weave the advice logic defined in the Advisors into the method calls of the target bean.

### 4. Best Practices

This example uses an annotation-based application context to get the `MyService` bean and call its `foo()`
method. First, it creates an `AnnotationConfigApplicationContext` instance and passes `AppConfig.class`
to set up the context. Then it gets the `MyService` bean with `context.getBean(MyService.class)`
and calls its `foo()` method.

```java
public class BeanFactoryAdvisorRetrievalHelperDemo {

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

A custom Advisor marked with `@Component`. It extends `AbstractPointcutAdvisor`. It defines a Pointcut that always returns true, and applies a custom
Advice `MyAdvice` to the target methods.

```java

@Component
public class MyAdvisor extends AbstractPointcutAdvisor {

    @Override
    public Pointcut getPointcut() {
        return Pointcut.TRUE;
    }

    @Override
    public Advice getAdvice() {
        return new MyAdvice();
    }
}
```

The `MyAdvice` class is a custom advice that implements the `MethodBeforeAdvice` interface. It runs some logic before the target method runs. In the `before()`
method, it prints the message "Before method execution".

```java
public class MyAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
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
    AnnotationAwareAspectJAutoProxyCreator->>AbstractAdvisorAutoProxyCreator: super.findCandidateAdvisors()
    Note over AnnotationAwareAspectJAutoProxyCreator,AbstractAdvisorAutoProxyCreator: Call the parent class method to find candidate Advisors
    AbstractAdvisorAutoProxyCreator->>BeanFactoryAdvisorRetrievalHelper: findAdvisorBeans()
    Note over AbstractAdvisorAutoProxyCreator,BeanFactoryAdvisorRetrievalHelper: Find all eligible Advisors in the current bean factory
    BeanFactoryAdvisorRetrievalHelper->>AbstractAutoProxyCreator: Return advisors
~~~

### 6. Source Code Analysis

The main job of the `org.springframework.aop.framework.autoproxy.BeanFactoryAdvisorRetrievalHelper#findAdvisorBeans` method is to find all eligible Advisor beans in the current bean factory. It ignores FactoryBeans and skips beans that are currently being created. First, it gets the list of Advisor bean names. If the list is not cached yet, it gets it with `BeanFactoryUtils.beanNamesForTypeIncludingAncestors()`. Then it loops over these names, checks whether each one is eligible, and adds the eligible Advisor beans to the result list. Before adding, it checks whether the bean is currently being created, and skips it if so. Finally, it returns the list of all eligible Advisor beans.

```java
/**
 * Find all eligible Advisor beans in the current bean factory,
 * ignoring FactoryBeans and excluding beans that are currently being created.
 * @return the list of {@link org.springframework.aop.Advisor} beans
 * @see #isEligibleBean
 */
public List<Advisor> findAdvisorBeans() {
    // If the list of Advisor bean names is not cached yet, determine it.
    String[] advisorNames = this.cachedAdvisorBeanNames;
    if (advisorNames == null) {
        // Do not initialize FactoryBeans here: we need to keep all regular beans uninitialized so the auto-proxy creator can apply to them!
        advisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                this.beanFactory, Advisor.class, true, false);
        this.cachedAdvisorBeanNames = advisorNames;
    }
    if (advisorNames.length == 0) {
        return new ArrayList<>();
    }

    List<Advisor> advisors = new ArrayList<>();
    // Loop over the list of Advisor bean names
    for (String name : advisorNames) {
        // Check whether the bean is eligible
        if (isEligibleBean(name)) {
            // If the bean is currently being created, skip it
            if (this.beanFactory.isCurrentlyInCreation(name)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Skipping currently created advisor '" + name + "'");
                }
            }
            else {
                try {
                    // Try to get the Advisor bean and add it to the list
                    advisors.add(this.beanFactory.getBean(name, Advisor.class));
                }
                catch (BeanCreationException ex) {
                    Throwable rootCause = ex.getMostSpecificCause();
                    if (rootCause instanceof BeanCurrentlyInCreationException) {
                        BeanCreationException bce = (BeanCreationException) rootCause;
                        String bceBeanName = bce.getBeanName();
                        // If the current bean depends on a bean that is being created, skip it
                        if (bceBeanName != null && this.beanFactory.isCurrentlyInCreation(bceBeanName)) {
                            if (logger.isTraceEnabled()) {
                                logger.trace("Skipping advisor '" + name +
                                        "' with dependency on currently created bean: " + ex.getMessage());
                            }
                            // Ignore the reference to the bean that is currently being advised.
                            // We want to find Advisors other than the bean that is being created itself.
                            continue;
                        }
                    }
                    // If getting the Advisor bean fails, throw an exception
                    throw ex;
                }
            }
        }
    }
    return advisors;
}
```
