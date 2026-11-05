## BeanClassLoaderAware

- [BeanClassLoaderAware](#beanclassloaderaware)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133916700) 📚 **Contents** - [All Articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [BeanClassLoaderAware source code](https://github.com/xuchengsheng/spring-reading/tree/master/spring-aware/spring-aware-beanClassLoaderAware)

### 2. Interface Description

The `BeanClassLoaderAware` interface lets a bean know the class loader that loaded it. When a bean implements this interface, the Spring container sets its class loader when the bean is initialized.

### 3. Interface Source

`BeanClassLoaderAware` is a core Spring interface, available since version 2.0. When a bean implements this interface and is initialized in the Spring container, the container calls the `setClassLoader` method automatically and passes in the class loader that loaded the bean.

```java
/**
 * Callback that allows a bean to be aware of the bean ClassLoader,
 * that is, the class loader used by the present bean factory to load bean classes.
 * 
 * This is mainly intended to be implemented by framework classes that need to pick up application classes by name, even though they may themselves have been loaded from a shared class loader.
 *
 * For a list of all bean lifecycle methods, see the BeanFactory BeanFactory javadocs.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 2.0
 * @see BeanNameAware
 * @see BeanFactoryAware
 * @see InitializingBean
 */
public interface BeanClassLoaderAware extends Aware {

    /**
     * Callback that supplies the bean class loader to a bean instance.
     * Invoked after the population of normal bean properties but before an initialization callback such as InitializingBean's
     * InitializingBean#afterPropertiesSet() method or a custom init method.
     * 
     * @param classLoader the owning class loader
     */
    void setBeanClassLoader(ClassLoader classLoader);

}
```

### 4. Main Features

1. **Provides class loader information**
   + A bean can learn the class loader that loaded it. It can use this class loader to load classes or look up resources dynamically.

2. **Separating framework and application class loaders**
   + In some complex environments, framework classes and application classes may be loaded by different class loaders. For example, in some application servers, the framework may be loaded by a shared class loader, while the application classes are loaded by a dedicated class loader. With `BeanClassLoaderAware`, framework classes can make sure they use the right class loader to load or access application classes.

3. **Lifecycle management**
   + `setBeanClassLoader` is called after the normal bean properties are populated, but before any initialization callback (such as `InitializingBean#afterPropertiesSet()`). This makes sure the class loader information is provided at the right stage of the bean lifecycle.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`. Then it gets a bean of type `MyBeanClassLoaderAware` from the Spring context and calls the `loadAndExecute` method.

```java
public class BeanClassLoaderAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyBeanClassLoaderAware myBeanClassLoaderAware = context.getBean(MyBeanClassLoaderAware.class);
        myBeanClassLoaderAware.loadAndExecute();
    }
}
```

The `@Bean` annotation defines a bean here, to make sure the Spring container creates `MyBeanClassLoaderAware`.

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyBeanClassLoaderAware myBeanClassLoaderAware(){
        return new MyBeanClassLoaderAware();
    }
}
```

In the `MyBeanClassLoaderAware` class, we implement `BeanClassLoaderAware`, which lets this bean get its `ClassLoader` when it is initialized. Then, in the `loadAndExecute` method, we use this `ClassLoader` to load a class named `com.xcs.spring.service.UserServiceImpl` dynamically and run its `getUserInfo` method.

```java
public class MyBeanClassLoaderAware implements BeanClassLoaderAware {

    private ClassLoader classLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void loadAndExecute() {
        try {
            Class<?> clazz = classLoader.loadClass("com.xcs.spring.service.UserServiceImpl");
            UserServiceImpl instance = (UserServiceImpl) clazz.getDeclaredConstructor().newInstance();
            System.out.println("UserInfo = " + instance.getUserInfo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

Define an interface and an implementation class of that interface.

```java
package com.xcs.spring.service;

public interface UserService {
    String getUserInfo();
}

public class UserServiceImpl implements UserService {
    @Override
    public String getUserInfo() {
        return "this is user info";
    }
}
```

The output shows that this way, the code of `MyBeanClassLoaderAware` is decoupled from the concrete implementation of `UserServiceImpl`. If the implementation of `UserServiceImpl` changes, or a new implementation appears, our `MyBeanClassLoaderAware` code does not need any change, as long as we follow the `UserService` interface.

```java
UserInfo = this is user info
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: BeanClassLoaderAware sequence diagram
    participant BeanFactoryAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant BeanClassLoaderAware
    
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
    AbstractAutowireCapableBeanFactory->>BeanClassLoaderAware:setBeanClassLoader(classLoader)<br>Set the classLoader
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:Return the bean
    AbstractBeanFactory-->>DefaultListableBeanFactory:Return the bean
    AnnotationConfigApplicationContext-->>BeanFactoryAwareApplication:Initialization complete

~~~

### 7. Source Code Analysis

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`. Then it gets a bean of type `MyBeanClassLoaderAware` from the Spring context and calls the `loadAndExecute` method.

```java
public class BeanClassLoaderAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyBeanClassLoaderAware myBeanClassLoaderAware = context.getBean(MyBeanClassLoaderAware.class);
        myBeanClassLoaderAware.loadAndExecute();
    }
}
```

The constructor of `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` does three steps. We focus on the `refresh()` method.

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();

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
            ClassLoader bcl = getBeanClassLoader();
            if (bcl != null) {
                ((BeanClassLoaderAware) bean).setBeanClassLoader(bcl);
            }
        }
        if (bean instanceof BeanFactoryAware) {
            // ... [code omitted for brevity]
        }
    }
}
```

Finally, execution reaches our own logic. We implement `BeanClassLoaderAware`, which lets this bean get its `ClassLoader` when it is initialized.

```java
public class MyBeanClassLoaderAware implements BeanClassLoaderAware {

    private ClassLoader classLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void loadAndExecute() {
        try {
            Class<?> clazz = classLoader.loadClass("com.xcs.spring.service.UserServiceImpl");
            UserServiceImpl instance = (UserServiceImpl) clazz.getDeclaredConstructor().newInstance();
            System.out.println("UserInfo = " + instance.getUserInfo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### 8. Notes

1. **Avoid overuse**
   + Use `BeanClassLoaderAware` only when you really need access to the class loader. Do not abuse it, because it may couple your code too tightly to the Spring framework.

2. **Consider the class loader hierarchy**
   + In Java, class loaders usually have a parent-child relationship. If you cannot find a class with the current class loader, you may need to check its parent class loader.

3. **Do not run complex logic in the setter**
   + `setBeanClassLoader` is a setter method. Avoid complex logic in it. It should only be used to set the class loader.

4. **Avoid storing state**
   + Try not to store state in a bean that implements `BeanClassLoaderAware`, or depend on the state of other beans. It makes the bean lifecycle and initialization more complex.

5. **Consider other techniques**
   + In some cases, other techniques can reach the same goal without `BeanClassLoaderAware`. For example, use the Spring `@Value` annotation or `ResourceLoader` to load resources, instead of using the class loader directly.

6. **Consider the class loader hierarchy**
   + In Java, class loaders usually have a parent-child relationship. If you cannot find a class with the current class loader, you may need to check its parent class loader.

7. **Resource management**
   + A class loader can load more than classes. It can also load other resources (for example property files). But be careful to make sure the resource path is correct, and remember that the search behavior of a class loader may differ from the file system or other loading mechanisms.

### 9. Summary

#### Best Practices Summary

1. **Startup and context configuration**
   + `AnnotationConfigApplicationContext` initializes the Spring container, and `MyConfiguration` is used as the configuration class for the Spring context.

2. **Configuration class definition**
   + The configuration class is marked with `@Configuration`, and the `@Bean` annotation makes sure `MyBeanClassLoaderAware` is managed by the Spring container.

3. **Implementing `BeanClassLoaderAware`**
   + By implementing `BeanClassLoaderAware`, the bean gets the `ClassLoader` that loaded it when it is initialized. In the `loadAndExecute` method, it loads and runs a specific service method dynamically.

4. **Interface and implementation**
   + We define a clear `UserService` interface and the matching `UserServiceImpl` implementation.

5. **Result and conclusion**
   + After the program runs, we see the expected output. This shows that `MyBeanClassLoaderAware` is decoupled from the specific implementation.

#### Source Code Analysis Summary

1. **Application entry**
   + `AnnotationConfigApplicationContext` initializes the Spring container with `MyConfiguration` as the configuration class. Then the `MyBeanClassLoaderAware` bean is taken and its `loadAndExecute` method is called.

2. **Initializing the Spring context**
   + In the `AnnotationConfigApplicationContext` constructor, `refresh()` is called to refresh or initialize the Spring container.

3. **Bean pre-instantiation**
   + In `refresh()` of the Spring context initialization, `finishBeanFactoryInitialization(beanFactory)` makes sure all non-lazy singleton beans are instantiated.

4. **Creating singleton beans**
   + The `preInstantiateSingletons` method in `DefaultListableBeanFactory` instantiates all non-lazy singleton beans up front. It calls `getBean` for each singleton bean in the container.

5. **Bean instantiation and initialization**
   + While getting a bean, if the bean is not created yet, `doCreateBean` is called. It instantiates the bean, populates its properties, and initializes it.

6. **Handle the Aware interfaces**
   + In the bean initialization stage, `invokeAwareMethods` makes sure any bean that implements the `Aware` interfaces (such as `BeanNameAware` and `BeanClassLoaderAware`) gets the right callback.

7. **Implementation of BeanClassLoaderAware**
   + For a bean that implements `BeanClassLoaderAware`, the Spring container sets the bean's `ClassLoader` through `setBeanClassLoader` in the initialization stage.

8. **Running custom logic**
   + In `MyBeanClassLoaderAware`, the bean's class loader has been saved. Then, in the `loadAndExecute` method, this class loader is used to load and run a specific class and method dynamically.