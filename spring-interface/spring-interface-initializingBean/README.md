## InitializingBean

- [InitializingBean](#initializingbean)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133845609) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [InitializingBean source](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-initializingBean)

### 2. Interface Description

The `InitializingBean` interface runs initialization logic or setup after all properties of a bean are set, but before the bean is used.

### 3. Interface Source

For a bean that implements `InitializingBean`, the `BeanFactory` calls its `afterPropertiesSet()` method after all properties are set.

```java
/**
 * Interface for beans that need to perform an action once all their properties have been set by a BeanFactory.
 * For example, it can run custom initialization or check that all required properties are set.
 * 
 * A bean that implements this interface gets its `afterPropertiesSet()` method called by the BeanFactory after all properties are set.
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see DisposableBean  // callback interface for when a bean is no longer needed
 * @see org.springframework.beans.factory.config.BeanDefinition#getPropertyValues()
 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getInitMethodName()
 */
public interface InitializingBean {

	/**
	 * Invoked by the containing BeanFactory after it has set all bean properties
     * and satisfied BeanFactoryAware, ApplicationContextAware etc.
     * 
	 * This method allows the bean instance to perform validation of its overall
	 * configuration and final initialization when all bean properties have been set.
	 * 
	 * @throws Exception in the event of misconfiguration or any other initialization failure
	 */
	void afterPropertiesSet() throws Exception;
}
```

### 4. Main Features

1. **Initialization callback**
   + The `InitializingBean` interface gives the Spring container a way to run initialization logic after all properties of a bean are set, and before other components use the bean.

2. **Property validation**
   + In `afterPropertiesSet`, we can check that the properties of the bean are set correctly, especially the required ones.

3. **Custom initialization logic**
   + If a bean needs special setup, such as opening resources, connecting to a database, or starting threads, it can do this in `afterPropertiesSet`.

4. **Lifecycle management**
   + `InitializingBean` is a key point in the Spring lifecycle. It is called after property injection (`Property Injection`) and before the bean is used. This is a clean lifecycle hook that makes sure the bean is fully ready before use.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a context that configures the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor.

```java
public class InitializingBeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
    }
}
```

The `@Bean` annotation defines a bean here. This makes sure the Spring container handles `MyInitializingBean`.

```java
@Configuration
public class MyConfiguration {

    @Bean
    public static MyInitializingBean myInitializingBean(){
        return new MyInitializingBean();
    }
}
```

The `afterPropertiesSet()` method simulates loading data.

```java
public class MyInitializingBean implements InitializingBean {

    private List<String> data;

    public List<String> getData() {
        return data;
    }

    @Override
    public void afterPropertiesSet() {
        // In this method, we simulate loading data
        data = new ArrayList<>();
        data.add("Data 1");
        data.add("Data 2");
        data.add("Data 3");
        System.out.println("MyInitializingBean initialized, data loaded!");
    }
}
```

When we run it, we see "`MyInitializingBean initialized, data loaded!`" in the console. This means the data was loaded into the `data` list.

```java
MyInitializingBean initialized, data loaded!
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: InitializingBean Sequence Diagram
    participant InitializingBeanApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant MyInitializingBean
    
    InitializingBeanApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Create the context
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>Refresh the context
    AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)<br>Initialize the bean factory
    AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()<br>Instantiate singletons
    DefaultListableBeanFactory->>AbstractBeanFactory:getBean(name)<br>Get the bean
    AbstractBeanFactory->>AbstractBeanFactory:doGetBean(name,requiredType,args,typeCheckOnly)<br>Do the work of getting the bean
    AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton(beanName,singletonFactory)<br>Get the singleton bean
    DefaultSingletonBeanRegistry-->>AbstractBeanFactory:getObject()<br>Get the bean instance
    AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean(beanName,mbd,args)<br>Create the bean
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean(beanName,mbd,args)<br>Do the work of creating the bean
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:initializeBean(beanName,bean,mbd)<br>Initialize the bean
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:invokeInitMethods(beanName,bean,mbd)<br>Call the init methods of the bean
    AbstractAutowireCapableBeanFactory->>MyInitializingBean:afterPropertiesSet()<br>Run the InitializingBean method
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:Return the bean
    AbstractBeanFactory-->>DefaultListableBeanFactory:Return the bean
    AnnotationConfigApplicationContext-->>InitializingBeanApplication:Initialization done
~~~

### 7. Source Code Analysis

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a context that configures the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor.

```java
public class InitializingBeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
    }
}
```

The `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` constructor runs three steps. We focus on the `refresh()` method.

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

In `org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization`, the code calls `preInstantiateSingletons` of `DefaultListableBeanFactory` to create all remaining non-lazy singleton beans.

```java
/**
 * Finish the initialization of this context's bean factory, initializing all remaining singleton beans.
 * 
 * @param beanFactory the bean factory to initialize
 */
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [code omitted for brevity]
    // Instantiate all remaining non-lazy singleton beans.
    beanFactory.preInstantiateSingletons();
}
```

The main goal of `org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons` is to instantiate all non-lazy singleton beans ahead of time. It is triggered after the Spring context finishes its setup, so every singleton bean is created and initialized correctly. Its core action is `getBean(beanName)`. For every singleton bean defined in the container, it calls `getBean`. This triggers instantiation, initialization, and dependency injection of the bean. If the bean was not created before, this call instantiates and initializes it.

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

In `org.springframework.beans.factory.support.AbstractBeanFactory#getBean()`, the code calls `doGetBean` to do the actual work of creating the bean. It passes the bean name and some default values. `doGetBean` does most of the work, such as finding the bean definition, creating the bean (if it does not exist yet), and handling dependencies.

```java
@Override
public Object getBean(String name) throws BeansException {
    return doGetBean(name, null, null, false);
}
```

In `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`, the code first checks whether the requested bean is a singleton that already exists. If not, it creates a new instance. On the way, it handles possible problems such as circular references, and makes sure the returned bean has the right type. This is a core part of bean lifecycle management in the Spring container.

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
        // Some beans (for example FactoryBeans) may need extra processing to get the real bean instance
        beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
    }
    // ... [code omitted for brevity]

    // Make sure the returned bean matches the requested type
    return adaptBeanInstance(name, beanInstance, requiredType);
}
```

The main job of `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton()` is to get an existing bean from the singleton cache, or to create a new one with the given `ObjectFactory`. This is the key part that makes a bean a singleton in the Spring container.

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    // The bean name must not be null
    Assert.notNull(beanName, "Bean name must not be null");

    // Synchronize access to the singleton cache to stay thread-safe
    synchronized (this.singletonObjects) {
        // Get the singleton from the cache
        Object singletonObject = this.singletonObjects.get(beanName);

        // If it is not in the cache
        if (singletonObject == null) {
            // ... [code omitted for brevity]

            try {
                // Use the factory to create a new singleton instance
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

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean()`, the main logic calls `doCreateBean`. This is where the bean is really instantiated, populated, and initialized. The method returns the new bean instance.

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

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`, `initializeBean` initializes the bean. It makes sure the bean is fully configured and ready.

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

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean`, if the bean implements `InitializingBean`, its `afterPropertiesSet` method is called here. If the bean definition has a custom init method, Spring also calls it here.

```java
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {

    // ... [code omitted for brevity]
    
    try {
        invokeInitMethods(beanName, wrappedBean, mbd);
    }
    catch (Throwable ex) {
        // ... [code omitted for brevity]
    }
    
    // ... [code omitted for brevity]

    return wrappedBean;
}
```

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeInitMethods`, the code first checks whether the bean implements `InitializingBean`. If it does, the code then checks whether `afterPropertiesSet` is managed externally. If not, the code calls it. This is a key step of the Spring bean lifecycle. It makes sure the bean is properly initialized before other parts of the application use it.

```java
protected void invokeInitMethods(String beanName, Object bean, @Nullable RootBeanDefinition mbd)
			throws Throwable {

    boolean isInitializingBean = (bean instanceof InitializingBean);
    if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
        if (logger.isTraceEnabled()) {
            logger.trace("Invoking afterPropertiesSet() on bean with name '" + beanName + "'");
        }
        if (System.getSecurityManager() != null) {
            // ... [code omitted for brevity]
        }
        else {
            ((InitializingBean) bean).afterPropertiesSet();
        }
    }
    // ... [code omitted for brevity]
}
```

Finally, our own logic runs. It simulates loading data.

```java
public class MyInitializingBean implements InitializingBean {

    private List<String> data;

    public List<String> getData() {
        return data;
    }

    @Override
    public void afterPropertiesSet() {
        // In this method, we simulate loading data
        data = new ArrayList<>();
        data.add("Data 1");
        data.add("Data 2");
        data.add("Data 3");
        System.out.println("MyInitializingBean initialized, data loaded!");
    }
}
```

### 8. Notes

1. **Use @PostConstruct**
   + `InitializingBean` is one way to initialize a bean. But in modern Spring, we prefer the `@PostConstruct` annotation. It is part of JSR-250 and does not depend on a Spring-specific interface.

2. **Avoid business logic**
   + `afterPropertiesSet` should only hold logic related to initialization. Do not put core business logic here.

3. **Handle exceptions**
   + `afterPropertiesSet` may throw exceptions. Make sure we handle all possible exceptions, especially those that could stop the bean from initializing correctly.

4. **Clear initialization order**:
   + Remember that `afterPropertiesSet` is called after all properties are set, but before any custom init method and before `@PostConstruct` methods.

5. **Do not rely on it too much**
   + Try not to make too many beans implement `InitializingBean`. It can make the code hard to read and manage. If possible, use other ways to initialize, such as the `@PostConstruct` annotation.

### 9. Summary

#### Best Practices Summary

1. **Entry class**
   + In `InitializingBeanApplication`, we use `AnnotationConfigApplicationContext` as the context. This context configures the Spring container with Java annotations instead of XML. We pass the `MyConfiguration` class to the constructor to tell Spring where the bean definitions are.

2. **Configuration class**
   + The `MyConfiguration` class uses `@Configuration` to mark it as a Spring configuration class. In it, we define a bean named `myInitializingBean` that returns a new `MyInitializingBean` instance. This makes sure the Spring container manages `MyInitializingBean` and calls its lifecycle methods, such as `afterPropertiesSet()`.

3. **Initialization logic**
   + The `MyInitializingBean` class implements `InitializingBean` and overrides `afterPropertiesSet()`. In this method, we simulate loading data by adding three strings to the `data` list. When the Spring container initializes this bean, it calls `afterPropertiesSet()` automatically and runs this logic.

4. **Result**
   + When we run the application, the Spring container manages and initializes `MyInitializingBean` and calls `afterPropertiesSet()`. So we see "`MyInitializingBean initialized, data loaded!`" in the console.

#### Source Code Analysis Summary

1. **Start the context**
   + We start the Spring context with `AnnotationConfigApplicationContext` and pass the `MyConfiguration` class as the argument. The Spring container then starts and initializes.

2. **Key points in the constructor**
   + The constructor of `AnnotationConfigApplicationContext` runs `register` and `refresh`. `refresh` is the one we focus on.

3. **Refresh the context**
   + In `refresh`, the Spring context starts its core refresh process. The key part is `finishBeanFactoryInitialization`. It makes sure all remaining non-lazy singleton beans are instantiated.

4. **Pre-instantiate singleton beans**
   + `preInstantiateSingletons` instantiates all non-lazy singleton beans ahead of time. So after the Spring context is set up, all singleton beans are instantiated, initialized, and have their dependencies injected.

5. **Get a bean**
   + The core methods `getBean` and `doGetBean` get beans from the container. If a bean is not created yet, they also create it, inject its properties, and initialize it.

6. **Singleton registration**
   + `getSingleton` in `DefaultSingletonBeanRegistry` makes sure a bean exists as a singleton. If the bean is not in the cache, it creates a new instance with the given `ObjectFactory`.

7. **Create a bean**
   + `createBean` and `doCreateBean` do the actual bean creation, including instantiation, property injection, and initialization.

8. **Initialize a bean**
   + `initializeBean` initializes the bean and calls its init methods. This includes `afterPropertiesSet` of the `InitializingBean` interface.

9. **Call the init methods**
   + `invokeInitMethods` checks whether the bean implements `InitializingBean`. If it does, and `afterPropertiesSet` is not managed externally, the method is called.

10. **Custom initialization logic**
    + Our own `MyInitializingBean` class implements `InitializingBean` and overrides `afterPropertiesSet` to simulate loading data.