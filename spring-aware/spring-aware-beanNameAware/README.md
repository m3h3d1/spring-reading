## BeanNameAware

- [BeanNameAware](#beannameaware)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [All Articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [BeanNameAware source code](https://github.com/xuchengsheng/spring-reading/tree/master/spring-aware/spring-aware-beanNameAware)

### 2. Interface Description

The `BeanNameAware` interface. When a bean implements it, the bean can learn its own name in the Spring container.

### 3. Interface Source

`BeanNameAware` is a core Spring interface, available since 01.11.2003. Spring passes the bean's name to any object that implements `BeanNameAware`.

```java
/**
 * Interface to be implemented by beans that want to know their name in the bean factory.
 * Note that it is usually not recommended for an object to depend on its bean name. It creates a fragile link to external configuration
 * and may add an unnecessary dependency on the Spring API.
 *
 * For a list of all bean lifecycle methods, see the
 * BeanFactory BeanFactory javadocs。
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 01.11.2003
 * @see BeanClassLoaderAware
 * @see BeanFactoryAware
 * @see InitializingBean
 */
public interface BeanNameAware extends Aware {

	/**
	 * Set the name of the bean in the bean factory that created this bean.
	 * This method is called after the normal bean properties are populated, but before an
	 * initialization callback such as InitializingBean#afterPropertiesSet() or a custom init method.
	 * @param name the name of the bean in the factory. Note that this is the actual bean name used in the factory,
     * which may differ from the originally specified name. For inner beans in particular, the actual name may have been made unique by adding a "#..." suffix.
     * If needed, use the BeanFactoryUtils#originalBeanName(String) method to extract the original bean name without the suffix.
	 */
	void setBeanName(String name);

}
```

### 4. Main Features

1. **Provides the `setBeanName` method**
   + A bean that implements `BeanNameAware` must implement the `setBeanName` method. It has one parameter: the name of the bean in the Spring container.

2. **Automatic callback**
   + When the Spring container creates and configures a bean that implements `BeanNameAware`, it calls `setBeanName` automatically and passes in the bean's name. You do not need to call this method yourself. The container handles it.

3. **Getting the bean name**
   + Sometimes a bean needs its own name for specific logic, for logging, or for other purposes. By implementing `BeanNameAware`, the bean gets this information easily.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`.

```java
public class BeanNameAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
    }
}
```

The `@ComponentScan` annotation tells the Spring container to scan the "`com.xcs.spring.service`" package and its sub-packages.

```java
@Configuration
@ComponentScan("com.xcs.spring.service")
public class MyConfiguration {

}
```

`MyBasePayService` is an abstract class that combines three special Spring interfaces: `BeanNameAware` (lets the bean know its name), `InitializingBean` (initialization after the properties are set), and `DisposableBean` (work before the bean is destroyed).

```java
public abstract class MyBasePayService implements BeanNameAware, InitializingBean, DisposableBean {

    private String beanName;

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Module " + beanName + " has been registered.");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("Module " + beanName + " has been unregistered.");
    }
}
```

`MyAliPayService` and `MyWeChatPayService` are two payment service classes. Both extend `MyBasePayService`, so they get the basic container lifecycle features automatically. This design gives the payment services one shared lifecycle pattern, and each service can still add its own payment logic.

```java
@Service
public class MyAliPayService extends MyBasePayService{
    
}

@Service
public class MyWeChatPayService extends MyBasePayService{
    
}
```

The output shows that when the Spring container starts and initializes the beans, it finds and creates `MyAliPayService` and `MyWeChatPayService`. Both extend `MyBasePayService`. After the bean properties are set (in `afterPropertiesSet()`), they print "Module myAliPayService has been registered." and "Module myWeChatPayService has been registered." This gives one shared lifecycle pattern.

```java
Module myAliPayService has been registered.
Module myWeChatPayService has been registered.
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: BeanNameAware sequence diagram
    participant BeanNameAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant MyBasePayService
    
    BeanNameAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Create the context
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
    AbstractAutowireCapableBeanFactory->>MyBasePayService:setBeanName(beanName)<br>Set the bean name
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:Return the bean
    AbstractBeanFactory-->>DefaultListableBeanFactory:Return the bean
    AnnotationConfigApplicationContext-->>BeanNameAwareApplication:Initialization complete
~~~

### 7. Source Code Analysis

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`.

```java
public class BeanNameAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
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
            ((BeanNameAware) bean).setBeanName(beanName);
        }
        if (bean instanceof BeanClassLoaderAware) {
            // ... [code omitted for brevity]
        }
        if (bean instanceof BeanFactoryAware) {
            // ... [code omitted for brevity]
        }
    }
}
```

Finally, execution reaches our own logic. We store the name in the `beanName` field for later use.

```java
public abstract class MyBasePayService implements BeanNameAware, InitializingBean, DisposableBean {

    private String beanName;

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Module " + beanName + " has been registered.");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("Module " + beanName + " has been unregistered.");
    }
}
```

### 8. Notes

1. **Order relative to other lifecycle methods**
   + `setBeanName` is called before many other lifecycle methods, such as `InitializingBean#afterPropertiesSet` and any defined init method. So do not expect other configuration or initialization to be finished inside `setBeanName`.

2. **Works only for container-managed beans**
   + `BeanNameAware` works only when the bean is managed by the Spring container. Simply creating an instance of the class (for example with `new`) does not trigger `BeanNameAware`.

3. **Using it with other Aware interfaces**
   + When a bean implements several `Aware` interfaces, pay attention to the call order. For example, the callbacks of `BeanNameAware`, `BeanFactoryAware`, and `ApplicationContextAware` are called in a fixed order.

4. **Bean names are unique**
   + A bean name is unique in the Spring container. But if aliases are used, the same bean can have several names. With `BeanNameAware`, we get the primary name of the bean.

### 9. Summary

#### Best Practices Summary

1. **Startup and configuration**
   + We use `AnnotationConfigApplicationContext` as the entry point of the Spring container. It is designed for Java-based configuration. The container is initialized and loads the `MyConfiguration` class, which defines the main configuration of the application.

2. **Component scanning**
   + With the `@ComponentScan` annotation in `MyConfiguration`, we tell the Spring container to scan the "`com.xcs.spring.service`" package and its sub-packages to find and manage beans.

3. **Lifecycle management**
   + The `MyBasePayService` class shows how to use special Spring interfaces such as `BeanNameAware`, `InitializingBean`, and `DisposableBean` to hook into specific stages of the bean lifecycle. When a bean instance is created and managed by Spring, it gets a name (through `BeanNameAware`), is initialized after all properties are set (through `InitializingBean`), and runs specific work when the application ends or the bean is destroyed (through `DisposableBean`).

4. **Concrete service implementations**
   + There are two concrete payment services, `MyAliPayService` and `MyWeChatPayService`. Both extend `MyBasePayService`, so both inherit the lifecycle features above. When the Spring container starts, the lifecycle methods of these two services are called, as the printed messages show.

5. **Actual effect**
   + When the application runs, each service class prints messages that it was registered and unregistered. This happens because they inherit the lifecycle methods defined in `MyBasePayService`.

#### Source Code Analysis Summary

1. **Startup and context initialization**
   + `AnnotationConfigApplicationContext` initializes the Spring container, and the configuration class `MyConfiguration` is passed in.

2. **Register and refresh the context**
   + In the `AnnotationConfigApplicationContext` constructor, `register()` registers the configuration class, and `refresh()` starts loading and initializing the beans.

3. **Start bean instantiation**
   + `refresh()` calls `finishBeanFactoryInitialization(beanFactory)`, which instantiates all non-lazy singleton beans up front.

4. **Instantiate singleton beans**
   + `preInstantiateSingletons()` loops over all bean names and calls `getBean(beanName)` to instantiate and initialize each bean.

5. **Create the bean instance**
   + `doGetBean()` is the core method that creates the bean. It handles instantiation, dependency injection, initialization, and more.

6. **Handle the Aware interfaces**
   + During bean initialization, `invokeAwareMethods(beanName, bean)` is called. It handles beans that implement `Aware` interfaces. This is where our `BeanNameAware` interface takes effect: when the bean implements it, its `setBeanName` method is called.

7. **User-defined logic**
   + In `MyBasePayService`, we implement `BeanNameAware` and override `setBeanName` to save the bean name. We also use `InitializingBean` and `DisposableBean` to run custom logic at specific moments of the bean lifecycle.