## BeanFactoryAware

- [BeanFactoryAware](#beanfactoryaware)
  - [1. Overview](#1-overview)
  - [2. Interface Description](#2-interface-description)
  - [3. Interface Source](#3-interface-source)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
  - [7. Source Code Analysis](#7-source-code-analysis)
  - [8. Notes](#8-notes)
  - [9. Summary](#9-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133914782) 📚 **Contents** - [All Articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [BeanFactoryAware source code](https://github.com/xuchengsheng/spring-reading/tree/master/spring-aware/spring-aware-beanFactoryAware)

### 2. Interface Description

The `BeanFactoryAware` interface lets a Spring bean get a reference to the `BeanFactory` that holds it. When a bean implements this interface, the Spring container calls its `setBeanFactory()` method during initialization and passes in a `BeanFactory` instance.

### 3. Interface Source

`BeanFactoryAware` is a core Spring interface, available since 11.03.2003. It lets Spring beans know about and work with the `BeanFactory` they live in. This gives beans direct access to the `BeanFactory`, so they can look up and work with other beans.

```java
/**
 * Interface to be implemented by beans that wish to be aware of their owning BeanFactory.
 *
 * For example, beans can look up collaborating beans via the factory (dependency lookup).
 * Note that most beans will choose to receive references to collaborating beans via corresponding bean properties or constructor arguments
 * (dependency injection).
 *
 * For a list of all bean lifecycle methods, see the
 * BeanFactory BeanFactory javadocs。
 *
 * @author Rod Johnson
 * @author Chris Beams
 * @since 11.03.2003
 * @see BeanNameAware
 * @see BeanClassLoaderAware
 * @see InitializingBean
 * @see org.springframework.context.ApplicationContextAware
 */
public interface BeanFactoryAware extends Aware {

	/**
	 * Callback that supplies the owning factory to a bean instance.
	 * Invoked after the population of normal bean properties but before an initialization callback such as
	 * InitializingBean#afterPropertiesSet() or a custom init method.
	 * @param beanFactory the owning BeanFactory (never null).
	 * The bean can immediately call methods on the factory.
	 * @throws BeansException in case of initialization errors
	 * @see BeanInitializationException
	 */
	void setBeanFactory(BeanFactory beanFactory) throws BeansException;

}
```

### 4. Main Features

1. **Get a `BeanFactory` reference**
   + If a bean implements `BeanFactoryAware` and overrides `setBeanFactory`, it receives a reference to its own `BeanFactory` during initialization. The Spring container calls `setBeanFactory` automatically for beans that implement this interface.

2. **Dependency lookup**
   + Once a bean has a `BeanFactory` reference, it can use the factory to look up other beans at runtime. This is called "Dependency Lookup", as opposed to the more common "Dependency Injection".

3. **Working with the `BeanFactory`**
   + A bean can use the `BeanFactory` for more than looking up other beans. For example, it can check a bean's scope, check whether a bean is a singleton, or get a bean's aliases.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`. Then it gets a bean of type `UserService` from the Spring context and calls the `validateUser` method.

```java
public class BeanNameAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        UserService userService = context.getBean(UserService.class);
        userService.validateUser("root", "123456");
    }
}
```

The `@ComponentScan` annotation tells the Spring container to scan the "`com.xcs.spring.validate`" and "`com.xcs.spring.service`" packages and their sub-packages.

```java
@Configuration
@ComponentScan({"com.xcs.spring.validate", "com.xcs.spring.service"})
public class MyConfiguration {

}
```

**UserValidator** is a simple validator interface with one method, `validate`, which checks whether a user name and password are valid. SimpleUserValidator is one implementation. It does a simple check: the user name and password must not be empty. `ComplexUserValidator` is another implementation of `UserValidator`. It is a bit stricter. Besides checking for empty values, it checks that the user name is longer than 5 characters and the password is longer than 8 characters.

```java
public interface UserValidator {
    boolean validate(String username, String password);
}

@Component("simpleUserValidator")
public class SimpleUserValidator implements UserValidator {
    @Override
    public boolean validate(String username, String password) {
        System.out.println("Using SimpleUserValidator");
        return username != null && password != null;
    }
}

@Component("complexUserValidator")
public class ComplexUserValidator implements UserValidator {
    @Override
    public boolean validate(String username, String password) {
        System.out.println("Using ComplexUserValidator");
        return username != null && username.length() > 5 && password != null && password.length() > 8;
    }
}
```

The `UserService` class uses the Spring `BeanFactoryAware` and `InitializingBean` interfaces to choose a validator dynamically. This design is very flexible. `UserService` can use different validation methods depending on configuration or conditions. It also means that if you need more validation methods later, you only add a new validator implementation and adjust `someConfigurationMethod()`.

```java
@Service
public class UserService implements BeanFactoryAware, InitializingBean {

    private BeanFactory beanFactory;
    private UserValidator userValidator;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (someConfigurationMethod()) {
            userValidator = beanFactory.getBean("simpleUserValidator", UserValidator.class);
        } else {
            userValidator = beanFactory.getBean("complexUserValidator", UserValidator.class);
        }
    }

    public void validateUser(String username, String password) {
        boolean success = userValidator.validate(username, password);
        if (success){
            System.out.println("Account and password verified");
        }else{
            System.out.println("Account and password verification failed");
        }
    }

    private boolean someConfigurationMethod() {
        return true;
    }
}
```

The output shows that `SimpleUserValidator` was used to check the account and password, and the check passed.

```java
Using SimpleUserValidator
Account and password verified
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: BeanFactoryAware sequence diagram
    participant BeanFactoryAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant UserService
    
    BeanFactoryAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Create the context
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>Refresh the context
    AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)<br>Initialize the bean factory
    AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()<br>Instantiate singletons
    DefaultListableBeanFactory->>AbstractBeanFactory:getBean(name)<br>Get the bean
    AbstractBeanFactory->>AbstractBeanFactory:doGetBean(name,requiredType,args,typeCheckOnly)<br>Do the actual get
    AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton(beanName,singletonFactory)<br>Get the singleton bean
    DefaultSingletonBeanRegistry-->>AbstractBeanFactory:getObject()<br>Get the bean instance
    AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean(beanName,mbd,args)<br>Create the bean
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean(beanName,mbd,args)<br>Do the actual creation
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:initializeBean(beanName,bean,mbd)<br>Initialize the bean
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:invokeAwareMethods(beanName, bean)<br>Call the Aware methods
    AbstractAutowireCapableBeanFactory->>UserService:setBeanFactory(beanFactory)<br>Set the beanFactory
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:Return the bean
    AbstractBeanFactory-->>DefaultListableBeanFactory:Return the bean
    AnnotationConfigApplicationContext-->>BeanFactoryAwareApplication:Initialization complete

~~~

### 7. Source Code Analysis

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`. Then it gets a bean of type `UserService` from the Spring context and calls the `validateUser` method.

```java
public class BeanNameAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        UserService userService = context.getBean(UserService.class);
        userService.validateUser("root", "123456");
    }
}
```

The constructor of `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` does three steps. We focus on the `refresh()` method.

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
}
```

In `org.springframework.context.support.AbstractApplicationContext#refresh`, we focus on `finishBeanFactoryInitialization(beanFactory)`. It instantiates all remaining non-lazy singleton beans. The other methods are not the focus of this reading, so we skip them.

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    // ... [code omitted for brevity]
    // Instantiate all remaining (non-lazy-init) singletons.
    finishBeanFactoryInitialization(beanFactory);
    // ... [code omitted for brevity]
}
```

The method `org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization` then calls `preInstantiateSingletons` in `DefaultListableBeanFactory` to create all remaining non-lazy singleton beans.

```java
/**
 * Finish the initialization of this factory by instantiating all remaining non-lazy-init singleton beans.
 * 
 * @param beanFactory the bean factory to initialize
 */
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [code omitted for brevity]
    // Instantiate all remaining non-lazy singleton beans.
    beanFactory.preInstantiateSingletons();
}
```

The main purpose of `org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons` is to instantiate all non-lazy singleton beans up front. It runs when the Spring context finishes initializing, so that all singleton beans are created and initialized. Its core operation is `getBean(beanName)`. For each singleton bean defined in the container, it calls `getBean`, which triggers instantiation, initialization, and dependency injection. If the bean has not been created yet, this call creates and initializes it.

```java
public void preInstantiateSingletons() throws BeansException {
    // ... [code omitted for brevity]
    // Loop over all bean names
    for (String beanName : beanNames) {
        getBean(beanName);
    }
    // ... [code omitted for brevity]
}
```

The method `org.springframework.beans.factory.support.AbstractBeanFactory#getBean()` calls `doGetBean` to do the real work of creating the bean. It passes the bean name and some default argument values. `doGetBean` does most of the work, such as finding the bean definition, creating the bean (if it does not exist yet), and handling dependencies.

```java
@Override
public Object getBean(String name) throws BeansException {
    return doGetBean(name, null, null, false);
}
```

In `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`, Spring first checks whether the requested bean is a singleton that has already been created. If not, it creates a new instance. Along the way it handles possible problems such as circular references, and makes sure the returned bean has the right type. This is a core part of bean lifecycle management in the Spring container.

```java
protected <T> T doGetBean(
        String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
        throws BeansException {
    // ... [code omitted for brevity]

    // Start creating the bean instance
    if (mbd.isSingleton()) {
        // If the bean is a singleton, try to get it from the singleton cache
        // If it does not exist, create a new instance with a lambda
        sharedInstance = getSingleton(beanName, () -> {
            try {
                // Try to create the bean instance
                return createBean(beanName, mbd, args);
            }
            catch (BeansException ex) {
                // ... [code omitted for brevity]
            }
        });
        // For some beans (for example FactoryBeans), more work may be needed to get the real bean instance
        beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
    }
    // ... [code omitted for brevity]

    // Make sure the returned bean instance matches the requested type
    return adaptBeanInstance(name, beanInstance, requiredType);
}
```

The method `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton()` gets an existing bean instance from the singleton cache, or creates a new one with the given `ObjectFactory`. This is the key part that keeps a bean a singleton in the Spring container.

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    // Assert that the bean name is not null
    Assert.notNull(beanName, "Bean name must not be null");

    // Synchronize on the singleton cache for thread safety
    synchronized (this.singletonObjects) {
        // Get the singleton from the cache
        Object singletonObject = this.singletonObjects.get(beanName);

        // If it is not in the cache
        if (singletonObject == null) {
            // ... [code omitted for brevity]

            try {
                // Create a new singleton instance with the factory
                singletonObject = singletonFactory.getObject();
                newSingleton = true;
            }
            catch (IllegalStateException ex) {
                // ... [code omitted for brevity]
            }
            catch (BeanCreationException ex) {
                // ... [code omitted for brevity]
            }
            finally {
                // ... [code omitted for brevity]
            }

            // ... [code omitted for brevity]
        }

        // Return the singleton
        return singletonObject;
    }
}
```

The main logic of `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean()` is to call `doCreateBean`. That is where the bean is really instantiated, populated, and initialized. The method returns the newly created bean instance.

```java
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
    throws BeanCreationException {
    
    // ... [code omitted for brevity]
    
    try {
        // Normal bean instantiation, property injection, and initialization.
        // This is where the bean is really created.
        Object beanInstance = doCreateBean(beanName, mbdToUse, args);
        // Log that the bean was created successfully
        if (logger.isTraceEnabled()) {
            logger.trace("Finished creating instance of bean '" + beanName + "'");
        }
        return beanInstance;
    }
    catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
        // ... [code omitted for brevity]
    }
    catch (Throwable ex) {
        // ... [code omitted for brevity]
    }
}
```

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`, the `initializeBean` method initializes the bean and makes sure it is fully configured and ready.

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {

    // Declare an object that may later hold the initialized bean or its proxy
    Object exposedObject = bean;

    // ... [code omitted for brevity]
    
    try {
        // ... [code omitted for brevity]
        
        // Initialize the bean
        exposedObject = initializeBean(beanName, exposedObject, mbd);
    } 
    catch (Throwable ex) {
        // ... [code omitted for brevity]
    }

    // Return the created and initialized bean
    return exposedObject;
}
```

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean`, `invokeAwareMethods(beanName, bean)` is an important step. It handles beans that implement the Spring `Aware` interfaces (such as `BeanNameAware` and `BeanFactoryAware`). If the bean implements any of them, the matching `Aware` method is called.

```java
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {

    // ... [code omitted for brevity]
    
    invokeAwareMethods(beanName, bean);
    
    // ... [code omitted for brevity]

    return wrappedBean;
}
```

The method `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeAwareMethods` handles beans that implement the Spring `Aware` interfaces. When a bean implements an interface such as `BeanNameAware`, `BeanClassLoaderAware`, or `BeanFactoryAware`, this method makes sure the right callback is called. This gives the bean information about its runtime environment.

```java
private void invokeAwareMethods(String beanName, Object bean) {
    if (bean instanceof Aware) {
        if (bean instanceof BeanNameAware) {
            // ... [code omitted for brevity]
        }
        if (bean instanceof BeanClassLoaderAware) {
            // ... [code omitted for brevity]
        }
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
        }
    }
}
```

Finally, execution reaches our own logic. The container calls `setBeanFactory()` and passes the current bean factory instance as the argument.

```java
@Service
public class UserService implements BeanFactoryAware, InitializingBean {

    private BeanFactory beanFactory;
    private UserValidator userValidator;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // ... [code omitted for brevity]
    }

    public void validateUser(String username, String password) {
        // ... [code omitted for brevity]
    }

    private boolean someConfigurationMethod() {
        return true;
    }
}
```

### 8. Notes

1. **Timing in the lifecycle**
   + `setBeanFactory` is called after the bean properties are set, but before other initialization methods (such as `@PostConstruct`, `InitializingBean#afterPropertiesSet`, or a specified init method).

2. **Avoid circular dependencies**
   + When beans look up other beans through the `BeanFactory`, a circular dependency can appear. For example, bean A looks up bean B in its `setBeanFactory` method, and bean B looks up bean A in its `setBeanFactory` method. This makes the container fail to initialize.

3. **Know the bean scope**
   + When you get beans from the `BeanFactory`, remember their scope. If a bean has prototype scope, every `getBean` call returns a new instance.

4. **Do not over-customize**
   + Unless there is a good reason, avoid heavy custom logic in `setBeanFactory`. It makes bean initialization complex and can cause unexpected side effects.

### 9. Summary

#### Best Practices Summary

1. **Build and configuration**
   + In the `BeanNameAwareApplication` entry class, `AnnotationConfigApplicationContext` initializes the Spring context from a Java configuration class (`MyConfiguration`). This Java-based configuration is more direct and flexible than the traditional XML-based configuration.

2. **Component scanning**
   + The `MyConfiguration` class uses `@ComponentScan` to set the packages to scan. The Spring container scans these packages and their sub-packages for components and registers them as Spring beans.

3. **Validator design**
   + We designed a `UserValidator` interface and two classes that implement it: `SimpleUserValidator` and `ComplexUserValidator`. They have different validation logic for different needs.

4. **Choose the validator dynamically**
   + The `UserService` class is the core of this application. It chooses a validator from the `BeanFactory` dynamically, based on some configuration. It does this by implementing `BeanFactoryAware` and `InitializingBean`: `BeanFactoryAware` lets `UserService` access the `BeanFactory` of the Spring container. `InitializingBean` makes sure the right validator is chosen after all properties (for example injected dependencies) are set.

5. **Run and output**
   + When `validateUser` is called to check the user name and password, the matching message is printed for the chosen validator (here, `SimpleUserValidator`). The validator itself also prints which validation method it is using.

#### Source Code Analysis Summary

1. **Application startup and context initialization**
   + When the `BeanNameAwareApplication` entry class runs, a new `AnnotationConfigApplicationContext` is created and initialized with the configuration class `MyConfiguration`.

2. **Configuration class and component scanning**
   + `MyConfiguration` is a Java configuration class. It tells the Spring container which packages to scan for components.

3. **Pre-instantiating singleton beans**
   + In the context's `refresh()` method, Spring instantiates all non-lazy singleton beans up front. So these beans are initialized when the container starts.

4. **Bean instantiation and initialization**
   + While the context refreshes, the Spring container creates and initializes all singleton beans one by one. The `doCreateBean` method instantiates the bean, injects dependencies, and calls any initialization methods.

5. **Handling Aware interfaces**
   + For a bean that implements an `Aware` interface, such as `BeanFactoryAware`, the Spring container calls the matching `Aware` method (for example `setBeanFactory`) during initialization. This gives the bean information about its runtime environment or other Spring features.

6. **Running custom logic**
   + Once the bean is initialized and all `Aware` methods have been called, custom logic can run. In this example, it runs through the `validateUser` method of `UserService`.

7. **What BeanFactoryAware provides**
   + By implementing `BeanFactoryAware`, `UserService` gets access to the `BeanFactory`. This lets it get beans from the `BeanFactory` at runtime, such as the `UserValidator` in this example.