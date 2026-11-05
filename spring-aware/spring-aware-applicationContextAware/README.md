## ApplicationContextAware

- [ApplicationContextAware](#applicationcontextaware)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133914136) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [ApplicationContextAware source](https://github.com/xuchengsheng/spring-reading/tree/master/spring-aware/spring-aware-applicationContextAware)

### 2. Interface Description

The `ApplicationContextAware` interface lets us access the current application context (`ApplicationContext`). It is useful when a Spring bean needs the application context itself, or needs other beans inside it.

### 3. Interface Source

An object that implements `ApplicationContextAware` gets an `ApplicationContext` instance injected automatically by the Spring container.

```java
/**
 * Interface to be implemented by any object that wishes to be notified
 * of the ApplicationContext that it runs in.
 *
 * For example, implementing this interface makes sense when an object
 * needs access to a set of collaborating beans.
 * Note that configuration via bean references is preferable to implementing
 * this interface just for bean lookup purposes.
 *
 * This interface can also be implemented if an object needs access to file
 * resources (that is, it wants to call getResource), wants to publish an
 * application event, or needs access to the MessageSource.
 * In these cases, it is better to implement the more specific
 * ResourceLoaderAware, ApplicationEventPublisherAware or
 * MessageSourceAware interface.
 *
 * Note that file resource dependencies can also be exposed as bean properties
 * of type org.springframework.core.io.Resource. They are filled from strings,
 * and the bean factory converts the type automatically. This removes the need
 * to implement any callback interface just to access a specific file resource.
 *
 * org.springframework.context.support.ApplicationObjectSupport is a convenient
 * base class for application objects. It implements this interface.
 *
 * For a list of all bean lifecycle methods, see the
 * org.springframework.beans.factory.BeanFactory BeanFactory javadocs.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @see ResourceLoaderAware
 * @see ApplicationEventPublisherAware
 * @see MessageSourceAware
 * @see org.springframework.context.support.ApplicationObjectSupport
 * @see org.springframework.beans.factory.BeanFactoryAware
 */
public interface ApplicationContextAware extends Aware {

    /**
     * Set the ApplicationContext that this object runs in.
     * This call is normally used to initialize the object.
     * This method is called after the normal bean properties are populated, but before
     * an initialization callback such as org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     * or a custom init method.
     * It is called after ResourceLoaderAware#setResourceLoader,
     * ApplicationEventPublisherAware#setApplicationEventPublisher and
     * MessageSourceAware (if applicable).
     *
     * @param applicationContext the ApplicationContext object to be used by this object
     * @throws ApplicationContextException if the context fails to initialize
     * @throws BeansException if an application context method throws an exception
     * @see org.springframework.beans.factory.BeanInitializationException
     */
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;

}
```

### 4. Main Features

1. **Look up other beans dynamically**
   + We usually use dependency injection to get references to other beans. In some dynamic or complex cases, a bean may need to look up other beans at runtime.

2. **Publish events**
   + With `ApplicationContext`, a bean can publish application-level events. Other beans can catch and handle them. This is one way to get loosely coupled interaction.

3. **Load resources**
   + `ApplicationContext` extends `ResourceLoader`, so a bean can use it to load external resources such as files or URLs.

4. **Access the message source**
   + For applications that support internationalization, a bean can use `ApplicationContext` to reach the message source and resolve specific messages.

5. **Access other application context services**
   + `ApplicationContext` also offers other services, such as working with JNDI and reading the environment properties of the application.

### 5. Best Practices

First, the entry class. The context is `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). We pass one component class, `MyConfiguration`, to its constructor.

```java
public class ApplicationContextAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
    }
}
```

The `@Bean` annotation defines a bean here. This makes sure the Spring container runs `MyApplicationContextAware`.

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyApplicationContextAware myApplicationContextAware(){
        return new MyApplicationContextAware();
    }
}
```

This is the `MyApplicationContextAware` implementation. It implements the `ApplicationContextAware` interface.

```java
public class MyApplicationContextAware implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        System.out.println("Implements ApplicationContextAware, so setApplicationContext is called automatically");
        System.out.println("ApplicationContext = " + context);
    }
}
```

The output shows that the Spring container did call the `setApplicationContext` method and passed in the `ApplicationContext` object.

```java
Implements ApplicationContextAware, so setApplicationContext is called automatically
ApplicationContext = org.springframework.context.annotation.AnnotationConfigApplicationContext@64bf3bbf
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: BeanClassLoaderAware sequence diagram
    participant ApplicationContextAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant ApplicationContextAwareProcessor
    participant MyApplicationContextAware
    
    ApplicationContextAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Create the context
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
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsBeforeInitialization(existingBean, beanName)<br>Call the before-initialization processors
    AbstractAutowireCapableBeanFactory->>ApplicationContextAwareProcessor:postProcessBeforeInitialization(bean,beanName)<br>Trigger Aware handling
    ApplicationContextAwareProcessor->>ApplicationContextAwareProcessor:invokeAwareInterfaces(bean)<br>Run the Aware callbacks
    ApplicationContextAwareProcessor->>MyApplicationContextAware:setApplicationContext(context)<br>Set the application context
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:Return the bean object
    AbstractBeanFactory-->>DefaultListableBeanFactory:Return the bean object
    AnnotationConfigApplicationContext-->>ApplicationContextAwareApplication:Initialization complete

~~~

### 7. Source Code Analysis

First, the entry class. The context is `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). We pass one component class, `MyConfiguration`, to its constructor. Then we get a bean of type `MyApplicationContextAware` from the Spring context. Finally we call the `publish` method to publish an event.

```java
public class ApplicationContextAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyApplicationContextAware contextAware = context.getBean(MyApplicationContextAware.class);
        contextAware.publish("hello world");
    }
}
```

The constructor `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` runs three steps. We focus on the `refresh()` method.

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

The method `org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization` calls `preInstantiateSingletons` in `DefaultListableBeanFactory` to create all remaining non-lazy singleton beans.

```java
/**
 * Finish the bean initialization of this factory, instantiating all remaining non-lazy singleton beans.
 * 
 * @param beanFactory the bean factory to initialize
 */
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [code omitted for brevity]
    // Finish all remaining non-lazy singleton beans.
    beanFactory.preInstantiateSingletons();
}
```

The main goal of `org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons` is to instantiate all non-lazy singleton beans ahead of time. It is triggered after the Spring context is set up, so that all singleton beans are created and initialized correctly. The core operation is `getBean(beanName)`. It is called for every singleton bean defined in the container. This triggers instantiation, initialization and dependency injection. If the bean was not created before, this call creates and initializes it.

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

In `org.springframework.beans.factory.support.AbstractBeanFactory#getBean()`, the method calls `doGetBean` to do the real work of creating the bean. It passes the bean name and some default values. `doGetBean` does most of the work, such as finding the bean definition, creating the bean (if it does not exist yet) and handling dependencies.

```java
@Override
public Object getBean(String name) throws BeansException {
    return doGetBean(name, null, null, false);
}
```

In `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`, it first checks whether the requested bean is a singleton that already exists. If not, it creates a new instance. Along the way, it handles possible problems such as circular references, and makes sure the returned bean has the right type. This is a core part of bean lifecycle management in the Spring container.

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
        // Some beans (such as FactoryBeans) may need more handling to get the real bean instance
        beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
    }
    // ... [code omitted for brevity]

    // Make sure the returned bean instance matches the requested type
    return adaptBeanInstance(name, beanInstance, requiredType);
}
```

The method `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton()` gets an existing bean instance from the singleton cache, or creates a new one with the given `ObjectFactory`. This is a key part of making sure a bean is a singleton in the Spring container.

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    // Assert that the bean name is not null
    Assert.notNull(beanName, "Bean name must not be null");

    // Synchronize on the singleton cache to stay thread safe
    synchronized (this.singletonObjects) {
        // Get the singleton object from the cache
        Object singletonObject = this.singletonObjects.get(beanName);

        // If it is not found in the cache
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

        // Return the singleton object
        return singletonObject;
    }
}
```

The main logic of `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean()` is to call `doCreateBean`. That is where the bean is really instantiated, populated and initialized. The method returns the new bean instance.

```java
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
    throws BeanCreationException {
    
    // ... [code omitted for brevity]
    
    try {
        // Normal bean instantiation, property population and initialization.
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

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`, the `initializeBean` method initializes the bean. It makes sure the bean is fully configured and ready.

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

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean`, if the condition is met (the bean is not synthetic), it calls `applyBeanPostProcessorsBeforeInitialization`. This is a key point in the Spring lifecycle. It goes through all registered `BeanPostProcessor` implementations and calls their `postProcessBeforeInitialization` methods. This lets us, and Spring's internal processors, change the bean or do other work before the bean is initialized.

```java
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {

    // ... [code omitted for brevity]
    
    Object wrappedBean = bean;
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
    }
    
    // ... [code omitted for brevity]

    return wrappedBean;
}
```

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`, the method loops over every `BeanPostProcessor`. Each `postProcessBeforeInitialization` method gets a chance to change or enhance the bean.

```java
@Override
public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
    throws BeansException {

    Object result = existingBean;
    for (BeanPostProcessor processor : getBeanPostProcessors()) {
        Object current = processor.postProcessBeforeInitialization(result, beanName);
        if (current == null) {
            return result;
        }
        result = current;
    }
    return result;
}
```

In `org.springframework.context.support.ApplicationContextAwareProcessor#postProcessBeforeInitialization`, the implementation looks at beans that implement an "aware" interface. It gives them the runtime information they need.

```java
@Override
@Nullable
public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    if (!(bean instanceof EnvironmentAware || bean instanceof EmbeddedValueResolverAware ||
          bean instanceof ResourceLoaderAware || bean instanceof ApplicationEventPublisherAware ||
          bean instanceof MessageSourceAware || bean instanceof ApplicationContextAware ||
          bean instanceof ApplicationStartupAware)) {
        return bean;
    }

    // ... [code omitted for brevity]
    
    invokeAwareInterfaces(bean);

    return bean;
}
```

The method `org.springframework.context.support.ApplicationContextAwareProcessor#invokeAwareInterfaces` handles beans that implement "Aware" interfaces. These interfaces let a bean get a reference to its runtime environment or to specific dependencies automatically. The bean does not need to look them up or inject them explicitly.

```java
private void invokeAwareInterfaces(Object bean) {
    // ... [code omitted for brevity]
    
    // Callback for the ApplicationContextAware interface
    if (bean instanceof ApplicationContextAware) {
        ((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
    }
}
```

Finally, our own logic runs. It uses `ApplicationContextAware` to get a reference to the `ApplicationContext`.

```java
public class MyApplicationContextAware implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        System.out.println("Implements ApplicationContextAware, so setApplicationContext is called automatically");
        System.out.println("ApplicationContext = " + context);
    }
}
```

### 8. Notes

1. **Remember the lifecycle**
   + When we implement `ApplicationContextAware`, the context is injected at one specific point in the bean lifecycle. This is usually after property injection and before the init methods.

2. **Remember the context hierarchy**
   + In more complex applications, there can be several `ApplicationContext` hierarchies (for example, a root context and one or more child contexts). Make sure we know which context we get beans from, and what the lifecycle and visibility of those beans are.

3. **Be careful with lazy beans**
   + If we use `ApplicationContextAware` to look up a bean that is defined as lazy, that bean is initialized right away.

4. **Avoid circular dependencies**
   + When we use `ApplicationContext` to look up beans dynamically, make sure we do not create circular dependencies by accident.

5. **Avoid using ApplicationContext in the constructor**
   + When a bean implements `ApplicationContextAware`, `setApplicationContext` is called after property injection and before init methods (such as `afterPropertiesSet` or a custom init method). So do not try to use `ApplicationContext` in the constructor. It may not be set yet at that point.

### 9. Summary

#### Best Practices Summary

1. **Startup and configuration**:
   - In the `main` method of `ApplicationContextAwareApplication`, we start the Spring container with `AnnotationConfigApplicationContext`. This is a Java annotation based configuration style.
   - The `MyConfiguration` class is the configuration class. Spring looks for the beans and settings defined in it.
2. **Bean definition**:
   - In the `MyConfiguration` class, we use the `@Bean` annotation to define a bean of type `MyApplicationContextAware`. This makes sure the Spring container manages `MyApplicationContextAware`.
3. **Implement the Aware interface**:
   - The `MyApplicationContextAware` class implements the `ApplicationContextAware` interface. When a bean implements this special interface, the Spring container automatically calls `setApplicationContext` during bean initialization and passes in the current `ApplicationContext`.
4. **Result**:
   - When the application starts, the Spring container detects that `MyApplicationContextAware` implements `ApplicationContextAware` and calls `setApplicationContext` automatically.
   - The console output shows this clearly, including the `ApplicationContext` instance passed to the method.
5. **Conclusion**:
   - With the `ApplicationContextAware` interface, a bean managed by the Spring container can easily get the `ApplicationContext`. This gives beans a strong way to know their environment and act on it.

#### Source Code Analysis Summary

1. **Startup and context initialization**
   + Start the Spring application with `AnnotationConfigApplicationContext` and pass `MyConfiguration` to initialize the context.

2. **Spring context refresh**
   + In `refresh()`, the main point is the call to `finishBeanFactoryInitialization(beanFactory)`. It instantiates all non-lazy singleton beans.

3. **Bean instantiation**
   + `preInstantiateSingletons()` is called after the Spring context is set up. It makes sure all non-lazy singleton beans are instantiated. For each bean it calls `getBean(beanName)`, which triggers instantiation, initialization and dependency injection.

4. **Bean creation and initialization**
   + In `doCreateBean`, the core step is calling `initializeBean` to initialize the bean and make sure it is fully configured and ready. `initializeBean` calls `applyBeanPostProcessorsBeforeInitialization`, which goes through all registered `BeanPostProcessor` instances before the bean is initialized.

5. **Handling Aware interfaces**
   + `ApplicationContextAwareProcessor` gives special handling to beans that implement "Aware" interfaces. In `invokeAwareInterfaces`, each "Aware" interface is handled, so beans can know their runtime environment or specific dependencies automatically.
