## ApplicationEventPublisherAware

- [ApplicationEventPublisherAware](#applicationeventpublisheraware)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133914254) 📚 **Contents** - [All Articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [ApplicationEventPublisherAware source code](https://github.com/xuchengsheng/spring-reading/tree/master/spring-aware/spring-aware-applicationEventPublisherAware)

### 2. Interface Description

The `ApplicationEventPublisherAware` interface gives beans a convenient way to publish application events. A bean that implements it receives a reference to an `ApplicationEventPublisher`, so it can publish events to the Spring application context.

### 3. Interface Source

`ApplicationEventPublisherAware` is a core Spring interface, available since 1.1.1. Spring automatically injects an `ApplicationEventPublisher` instance (usually the `ApplicationContext`) into any object that implements `ApplicationEventPublisherAware`.

```java
/**
 * Interface to be implemented by any object that wishes to be notified of the ApplicationEventPublisher (typically the ApplicationContext) that it runs in.
 * The object is notified when it runs inside an ApplicationEventPublisher.
 * 
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 1.1.1
 * @see ApplicationContextAware
 */
public interface ApplicationEventPublisherAware extends Aware {

    /**
     * Set the ApplicationEventPublisher that this object runs in.
     * This method is called after the normal bean properties are populated, but before an init callback such as InitializingBean's afterPropertiesSet or a custom init-method.
     * It is called before ApplicationContextAware's setApplicationContext.
     * 
     * @param applicationEventPublisher event publisher to be used by this object
     */
    void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher);

}
```

### 4. Main Features

1. **Event publishing ability**
   + It gives Spring beans the ability to publish events, so they can publish events to the Spring application context.

2. **Callback mechanism**
   + When a bean implements `ApplicationEventPublisherAware`, the Spring container injects an `ApplicationEventPublisher` instance into the bean automatically.

3. **Relation to ApplicationContext**
   + The injected `ApplicationEventPublisher` instance is usually the `ApplicationContext` itself. So beans can use it to publish events.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`. Then it gets a bean of type `MyApplicationEventPublisherAware` from the Spring context and calls the `publish` method to publish an event.

```java
public class ApplicationEventPublisherAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyApplicationEventPublisherAware publisherAware = context.getBean(MyApplicationEventPublisherAware.class);
        publisherAware.publish("hello world");
    }
}
```

The `@Bean` annotation defines two beans here, to make sure the Spring container creates `MyEventListener` and `MyApplicationEventPublisherAware`.

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyApplicationEventPublisherAware myApplicationEventPublisherAware(){
        return new MyApplicationEventPublisherAware();
    }

    @Bean
    public MyEventListener MyEventListener(){
        return new MyEventListener();
    }
}
```

The `MyApplicationContextAware` class uses `ApplicationEventPublisherAware` to get a reference to `ApplicationEventPublisher`, and uses it to publish a custom event.

```java
public class MyApplicationEventPublisherAware implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(String message) {
        publisher.publishEvent(new MyEvent(this, message));
    }
}
```

`MyEvent` is our custom Spring application event. It carries a string message.

```java
public class MyEvent extends ApplicationEvent {

    private final String message;

    public MyEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```

`MyEventListener` is a listener. When a `MyEvent` event is published, this listener is triggered automatically and runs the logic in its `onApplicationEvent` method.

```java
public class MyEventListener implements ApplicationListener<MyEvent> {

    @Override
    public void onApplicationEvent(MyEvent event) {
        System.out.println("Received my event - " + event.getMessage());
    }
}
```

The output shows that the listener caught the event and handled it.

```java
Received my event - hello world
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: ApplicationEventPublisherAware sequence diagram
    participant ApplicationEventPublisherAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant ApplicationContextAwareProcessor
    participant MyApplicationEventPublisherAware
    
    ApplicationEventPublisherAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Create the context
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
AbstractAutowireCapableBeanFactory->>ApplicationContextAwareProcessor:postProcessBeforeInitialization(bean,beanName)<br>Before-initialization processor method
ApplicationContextAwareProcessor->>ApplicationContextAwareProcessor:invokeAwareInterfaces(bean)<br>Call the Aware interfaces
ApplicationContextAwareProcessor->>MyApplicationEventPublisherAware:setApplicationEventPublisher(publisher)<br>Inject the event publisher
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:Return the bean
    AbstractBeanFactory-->>DefaultListableBeanFactory:Return the bean
    AnnotationConfigApplicationContext-->>ApplicationEventPublisherAwareApplication:Initialization complete
~~~

### 7. Source Code Analysis

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`. Then it gets a bean of type `MyApplicationEventPublisherAware` from the Spring context and calls the `publish` method to publish an event.

```java
public class ApplicationEventPublisherAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyApplicationEventPublisherAware publisherAware = context.getBean(MyApplicationEventPublisherAware.class);
        publisherAware.publish("hello world");
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
    // Call back the ApplicationEventPublisherAware interface
    if (bean instanceof ApplicationEventPublisherAware) {
        ((ApplicationEventPublisherAware) bean).setApplicationEventPublisher(this.applicationContext);
    }
    // ... [code omitted for brevity]
}
```

Finally, execution reaches our own logic. It uses `ApplicationEventPublisherAware` to get a reference to `ApplicationEventPublisher`, and uses it to publish a custom event.

```java
public class MyApplicationEventPublisherAware implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(String message) {
        publisher.publishEvent(new MyEvent(this, message));
    }
}
```

### 8. Notes

1. **Make sure the container supports it**
   + Not all Spring containers support `Aware` interfaces. For example, a basic `BeanFactory` does not, but `ApplicationContext` does. Make sure your bean is managed by a container that supports `ApplicationEventPublisherAware`.

2. **Avoid complex business logic**
   + Avoid putting complex business logic in your `setApplicationEventPublisher` method. It is mainly for injecting the `ApplicationEventPublisher`.

3. **Know who receives the event**
   + When you publish an event with `ApplicationEventPublisher`, all matching listeners receive it. Make sure you know which listeners exist and what they do, to avoid strange problems.

4. **Do not call it manually**
   + `setApplicationEventPublisher` is meant to be called by the Spring container, not by application code. Do not call this method yourself in business logic.

### 9. Summary

#### Best Practices Summary

1. **Application startup**
   + In the main method of `ApplicationEventPublisherAwareApplication`, `AnnotationConfigApplicationContext` initializes the Spring context with the configuration class `MyConfiguration`.

2. **Configuration class**
   + `MyConfiguration` is marked with `@Configuration`, so it is a Spring configuration class. It uses `@Bean` to define two beans, `MyApplicationEventPublisherAware` and `MyEventListener`, so that the Spring container manages them.

3. **Event publisher**
   + The `MyApplicationEventPublisherAware` class implements `ApplicationEventPublisherAware`, so it gets a reference to `ApplicationEventPublisher`. It also defines a `publish` method that publishes a custom `MyEvent` event.

4. **Custom event**
   + `MyEvent` is a custom event class that extends `ApplicationEvent`. It carries a string message.

5. **Event listener**
   + `MyEventListener` is an event listener. It implements `ApplicationListener` and listens only for `MyEvent` events. When such an event is published, its `onApplicationEvent` method is triggered automatically.

6. **Result**
   + When the main method of `ApplicationEventPublisherAwareApplication` runs, the application publishes a `MyEvent` event with the message "hello world". `MyEventListener` catches the event and prints the matching message.

#### Source Code Analysis Summary

1. **Application startup**
   + In the main method of `ApplicationEventPublisherAwareApplication`, `AnnotationConfigApplicationContext` initializes the Spring context with the configuration class `MyConfiguration`.

2. **Register and refresh**
   + In the `AnnotationConfigApplicationContext` constructor, the component classes are registered first. Then `refresh()` is called to start the initialization of the Spring container.

3. **Initialize the bean factory**
   + In `AbstractApplicationContext#refresh`, `finishBeanFactoryInitialization` is called to instantiate all non-lazy singleton beans.

4. **Pre-instantiate singletons**
   + In `DefaultListableBeanFactory`, the `preInstantiateSingletons` method instantiates all non-lazy singleton beans up front.

5. **Bean creation**
   + In `AbstractBeanFactory#getBean`, `doGetBean` is called to really create the bean. This involves real bean instantiation, property injection, and initialization.

6. **Initialize the bean**
   + In the `AbstractAutowireCapableBeanFactory` class, the `initializeBean` method makes sure the bean is fully configured and ready. All `BeanPostProcessor`s are applied here. They can do extra work before and after bean initialization.

7. **Handle Aware interfaces**
   + In `ApplicationContextAwareProcessor`, the `invokeAwareInterfaces` method handles beans that implement `Aware` interfaces. It injects the matching dependencies or runtime information into them.

8. **Publish the event**
   + In our custom logic, the `ApplicationEventPublisherAware` interface gives us the Spring event publisher. We then use it to publish a custom event.