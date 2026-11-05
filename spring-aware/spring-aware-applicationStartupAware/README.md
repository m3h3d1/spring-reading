## ApplicationStartupAware

- [ApplicationStartupAware](#applicationstartupaware)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133914474) 📚 **Contents** - [All Articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [ApplicationStartupAware source code]()

### 2. Interface Description

The `ApplicationStartupAware` interface is for fine-grained tracking of the startup process. With `StartupStep`, we can define the steps of application startup and collect performance and context information about them.

### 3. Interface Source

`ApplicationStartupAware` is a core Spring interface, available since 5.3. Spring automatically injects an `ApplicationStartup` instance into any object that implements `ApplicationStartupAware`.

```java
/**
 * Interface to be implemented by any object that wishes to be notified of the ApplicationStartup it runs with.
 * In simpler terms, this interface makes a bean aware of the application startup tracking mechanism.
 * 
 * @author Brian Clozel
 * @since 5.3
 * @see ApplicationContextAware
 */
public interface ApplicationStartupAware extends Aware {

    /**
     * Set the ApplicationStartup that this object runs with.
     * This method is called after the normal bean properties are populated, but before any initialization callback (such as InitializingBean's afterPropertiesSet or a custom init method).
     * It is called before ApplicationContextAware's setApplicationContext.
     * 
     * @param applicationStartup application startup to be used by this object
     */
    void setApplicationStartup(ApplicationStartup applicationStartup);
}
```

### 4. Main Features

1. **Startup performance tracking**
   + With access to `ApplicationStartup`, beans that implement this interface can use the `StartupStep` API to track their own steps during startup. This is very useful for finding and tuning startup performance.

2. **Tracking for beans**
   + It is not only for use inside the Spring framework. We can give our own beans or components the same startup tracking ability that the Spring framework has.

3. **Fine-grained control**
   + Together with `StartupStep`, `ApplicationStartupAware` lets beans track specific parts of their startup, such as database initialization, connecting to external services, or any other operation that may take time.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). It first sets `BufferingApplicationStartup`, an `ApplicationStartup` implementation that keeps the last 100 startup steps. This lets us look at and analyze these steps after startup, to see which operations may slow it down. Then the `register` method tells the Spring context to load the `MyConfiguration` class. Finally, the `refresh` method triggers the initialization of the application context, including bean creation and dependency injection.

```java
public class ApplicationStartupAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setApplicationStartup(new BufferingApplicationStartup(100));
        context.register(MyConfiguration.class);
        context.refresh();
        context.close();
    }
}
```

The `@Bean` annotation defines a bean here, to make sure the Spring container creates `MyApplicationStartupAware`.

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyApplicationStartupAware myApplicationStartupAware(){
        return new MyApplicationStartupAware();
    }
}
```

The main purpose of the `MyApplicationStartupAware` class is to show how to use `ApplicationStartup` to track specific logic during Spring application startup. This is useful because we can see which startup steps take the most time, and tune them. This implementation only simulates two steps, but in a real application you can track any number and type of steps.

```java
public class MyApplicationStartupAware implements ApplicationStartupAware, InitializingBean {

    private ApplicationStartup applicationStartup;

    @Override
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        this.applicationStartup = applicationStartup;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        StartupStep step1 = applicationStartup.start("MyApplicationStartupAware Logic Step 1");
        // Custom logic
        Thread.sleep(200);
        step1.tag("status", "done").end();

        StartupStep step2 = applicationStartup.start("MyApplicationStartupAware Logic Step 2");
        // Custom logic
        Thread.sleep(300);
        step2.tag("status", "done").end();
    }
}
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: ApplicationStartupAware sequence diagram
    participant ApplicationStartupAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant ApplicationContextAwareProcessor
    participant MyApplicationStartupAware
    
    ApplicationStartupAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Create the context
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
    ApplicationContextAwareProcessor->>MyApplicationStartupAware:setApplicationStartup(applicationStartup)<br>Set the applicationStartup
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:Return the bean
    AbstractBeanFactory-->>DefaultListableBeanFactory:Return the bean
    AnnotationConfigApplicationContext-->>ApplicationStartupAwareApplication:Initialization complete
~~~

### 7. Source Code Analysis

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). It first sets `BufferingApplicationStartup`, an `ApplicationStartup` implementation that keeps the last 100 startup steps. This lets us look at and analyze these steps after startup, to see which operations may slow it down. Then the `register` method tells the Spring context to load the `MyConfiguration` class. Finally, the `refresh` method triggers the initialization of the application context, including bean creation and dependency injection.

```java
public class ApplicationStartupAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setApplicationStartup(new BufferingApplicationStartup(100));
        context.register(MyConfiguration.class);
        context.refresh();
        context.close();
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

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean`, if the condition is met (the bean is not synthetic), it calls `applyBeanPostProcessorsBeforeInitialization`. This is a key point in the Spring lifecycle. It loops over all registered `BeanPostProcessor` implementations and calls their `postProcessBeforeInitialization` methods. This lets us, and Spring's internal processors, change the bean or do other work before it is initialized.

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

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`, the `postProcessBeforeInitialization` method of each `BeanPostProcessor` gets a chance to change or enhance the bean.

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

In `org.springframework.context.support.ApplicationContextAwareProcessor#postProcessBeforeInitialization`, the implementation looks mainly at beans that implement "aware" interfaces and gives them the runtime information they need.

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

The method `org.springframework.context.support.ApplicationContextAwareProcessor#invokeAwareInterfaces` handles beans that implement "Aware" interfaces. These interfaces let a bean be "aware" of its runtime environment or specific dependencies automatically, without an explicit lookup or injection.

```java
private void invokeAwareInterfaces(Object bean) {
    // ... [code omitted for brevity]
	if (bean instanceof ApplicationStartupAware) {
        ((ApplicationStartupAware) bean).setApplicationStartup(this.applicationContext.getApplicationStartup());
    }
    // ... [code omitted for brevity]
}
```

Finally, execution reaches our own logic. The main purpose of the `MyApplicationStartupAware` class is to show how to use `ApplicationStartup` to track specific logic during Spring application startup. This is useful because we can see which startup steps take the most time, and tune them. This implementation only simulates two steps, but in a real application you can track any number and type of steps.

```java
public class MyApplicationStartupAware implements ApplicationStartupAware, InitializingBean {

    private ApplicationStartup applicationStartup;

    @Override
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        this.applicationStartup = applicationStartup;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        StartupStep step1 = applicationStartup.start("MyApplicationStartupAware Logic Step 1");
        // Custom logic
        Thread.sleep(200);
        step1.tag("status", "done").end();

        StartupStep step2 = applicationStartup.start("MyApplicationStartupAware Logic Step 2");
        // Custom logic
        Thread.sleep(300);
        step2.tag("status", "done").end();
    }
}
```

### 8. Notes

1. **Timing in the lifecycle**
   + `setApplicationStartup` is called after the other bean properties are set and before `afterPropertiesSet` of `InitializingBean`. Make sure your bean does not depend on other properties that are not initialized or injected yet at this stage.

2. **Performance**
   + Startup tracking is useful for analyzing application startup time. But adding too many startup step trackers may have a small effect on performance. In production, you may need to balance the level of detail against performance.

3. **Clear step names**
   + When you define a `StartupStep`, give it a clear, descriptive name. Then others can easily understand which step it stands for.

4. **Do not overuse it**
   + Use startup tracking only for steps that are really important and may affect startup performance. You do not need to track every small operation.

5. **Do not forget to end the step**
   + Whenever you start a `StartupStep`, remember to call `end` at the right time. Otherwise the step may show as still running in the report, which can cause confusion.

### 9. Summary

#### Best Practices Summary

1. **Entry class overview**
   + It uses `AnnotationConfigApplicationContext`, a Java annotation-based way to initialize a Spring context. It sets `BufferingApplicationStartup` to keep the last 100 steps of application startup. This lets you analyze which steps may affect startup performance. It registers the `MyConfiguration` class to load the configuration. It refreshes and initializes the application context, which triggers bean creation and dependency injection. Then it closes the context.

2. **Configuration class overview**
   + It is marked with the `@Configuration` annotation, which tells Spring that this is a configuration class. The `@Bean` annotation defines the `MyApplicationStartupAware` bean. This makes sure it is handled by the Spring container and its lifecycle methods run when the container starts.

3. **`MyApplicationStartupAware` class overview**
   + It implements `ApplicationStartupAware`, so it learns the `ApplicationStartup` instance at startup. It defines two startup steps that simulate possibly long-running tasks, and tracks them with `StartupStep`. At the end of each step, it tags the status as "done" and then ends the step.

#### Source Code Analysis Summary

1. **Instantiating beans**
   + In `refresh()` of `AbstractApplicationContext`, `finishBeanFactoryInitialization` is called to make sure all singleton beans are instantiated up front.

2. **Bean pre-instantiation**
   + `preInstantiateSingletons` of `DefaultListableBeanFactory` makes sure all non-lazy singleton beans are instantiated. The core operation is calling `getBean(beanName)`.

3. **Getting the bean instance**
   + `getBean` of `AbstractBeanFactory` calls `doGetBean` to really create the bean, handle exceptions and dependencies, and return the bean instance.

4. **Getting the singleton**
   + `getSingleton` of `DefaultSingletonBeanRegistry` makes sure the bean exists as a singleton. It gets the bean from the cache, or creates a new instance with the given `ObjectFactory`.

5. **Creating the bean instance**
   + `createBean` of `AbstractAutowireCapableBeanFactory` calls `doCreateBean` to really instantiate and initialize the bean, so that it is fully configured and ready.

6. **Bean initialization**
   + `initializeBean` of `AbstractAutowireCapableBeanFactory` makes sure the bean is initialized correctly. Its call to `applyBeanPostProcessorsBeforeInitialization` is a key point in the Spring lifecycle. It lets BeanPostProcessors act before the bean is initialized.

7. **Handle Aware interfaces**
   + During bean initialization, `ApplicationContextAwareProcessor` makes sure beans that implement `Aware` interfaces are handled correctly. These beans are automatically "aware" of their runtime environment or specific dependencies.

8. **Running custom logic**
   + The `MyApplicationStartupAware` class implements `ApplicationStartupAware`, so it receives an `ApplicationStartup` instance.