## MessageSourceAware

- [MessageSourceAware](#messagesourceaware)
  - [1. Overview](#1-overview)
  - [2. Interface Description](#2-interface-description)
  - [3. Interface Source](#3-interface-source)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
  - [7. Source Code Analysis](#7-source-code-analysis)
  - [8. Notes](#8-notes)
  - [8. Summary](#8-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133916775) 📚 **Contents** - [All Articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [MessageSourceAware source code](https://github.com/xuchengsheng/spring-reading/blob/master/spring-aware/spring-aware-messageSourceAware)

### 2. Interface Description

The `MessageSourceAware` interface is for objects that want a `MessageSource` injected. `MessageSource` is the Spring interface for internationalization (i18n). It provides ways to get messages from different message resources (for example property files). With `MessageSource`, we can give an application internationalized message support.

### 3. Interface Source

`MessageSourceAware` is a core Spring interface, available since 1.1.1. Spring automatically injects a `MessageSource` instance into any object that implements `MessageSourceAware`.

```java
/**
 * Interface to be implemented by any object that wishes to be notified of the MessageSource (typically the ApplicationContext) that it runs in.
 *
 * Note that the MessageSource can usually also be passed on as a bean reference
 * (to arbitrary bean properties or constructor arguments), because it is defined as a bean with name "messageSource" in the application context.
 * 
 * Author: Juergen Hoeller, Chris Beams
 * Version: 1.1.1
 * See: ApplicationContextAware
 */
public interface MessageSourceAware extends Aware {

    /**
     * Set the MessageSource that this object runs in.
     * This method is called after the normal bean properties are populated, but before an initialization callback such as InitializingBean's afterPropertiesSet or a custom init-method.
     * This method is called before ApplicationContextAware's setApplicationContext.
     * 
     * @param messageSource the message source to be used by this object
     */
    void setMessageSource(MessageSource messageSource);

}
```

### 4. Main Features

1. **Automatic injection**
   + When a bean implements `MessageSourceAware` and is managed by the Spring container, Spring calls the bean's `setMessageSource` method automatically and passes in the `MessageSource` instance of the current application context.

2. **Internationalization support**
   + With `MessageSourceAware`, beans get access to the `MessageSource`. They can then get messages for different regions and languages. This is especially useful for beans that show error messages, UI labels, or other user-facing text in different languages.

3. **Simpler configuration**
   + We can inject `MessageSource` into beans with normal dependency injection. But `MessageSourceAware` is a more automatic and explicit way, especially when the bean needs the `MessageSource` at a specific stage of initialization.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`. Then it gets a bean of type `MyMessageSourceAware` from the Spring context and calls the `getMessage` method.

```java
public class MessageSourceAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyMessageSourceAware messageSourceAware = context.getBean(MyMessageSourceAware.class);
        messageSourceAware.getMessage();
    }
}
```

The `@Bean` annotation defines two beans here, to make sure the Spring container creates `MyMessageSourceAware` and `MessageSource`. `ResourceBundleMessageSource` is a concrete Spring implementation for internationalization (i18n). It lets the application read internationalized messages from property files.

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyMessageSourceAware myMessageSourceAware(){
        return new MyMessageSourceAware();
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        return messageSource;
    }
}
```

The `MyMessageSourceAware` class uses the `MessageSourceAware` interface to get a reference to the `MessageSource` automatically. This reference can be used to get internationalized messages for different languages or regions. Using the injected `MessageSource`, it reads two internationalized messages from the property files and prints them. One is in English and the other is in Simplified Chinese.

```java
public class MyMessageSourceAware implements MessageSourceAware {

    private MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void getMessage() {
        System.out.println("English："+messageSource.getMessage("greeting", null, Locale.ENGLISH));
        System.out.println("Chinese: "+messageSource.getMessage("greeting", null, Locale.SIMPLIFIED_CHINESE));
    }
}
```

The output shows that the `MyMessageSourceAware` class got the internationalized messages from the property files.

```java
English：Hello!
Chinese: (the Chinese text for "Hello")
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: EnvironmentAware sequence diagram
    participant MessageSourceAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant ApplicationContextAwareProcessor
    participant MyMessageSourceAware
    
    MessageSourceAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Create the context
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
    ApplicationContextAwareProcessor->>MyMessageSourceAware:setMessageSource(messageSource)<br>Set the messageSource
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:Return the bean
    AbstractBeanFactory-->>DefaultListableBeanFactory:Return the bean
    AnnotationConfigApplicationContext-->>MessageSourceAwareApplication:Initialization complete
~~~

### 7. Source Code Analysis

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`. Then it gets a bean of type `MyMessageSourceAware` from the Spring context and calls the `getMessage` method.

```java
public class MessageSourceAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyMessageSourceAware messageSourceAware = context.getBean(MyMessageSourceAware.class);
        messageSourceAware.getMessage();
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
    
    // Step 1. Initialize message source for this context.
	initMessageSource();
    
    // Step 2. Instantiate all remaining (non-lazy-init) singletons.
    finishBeanFactoryInitialization(beanFactory);
    // ... [code omitted for brevity]
}
```

We come to step 1 in `org.springframework.context.support.AbstractApplicationContext#refresh`. The method `org.springframework.context.support.AbstractApplicationContext#initMessageSource` makes sure the Spring application context always has a `MessageSource` bean, whether or not one is defined explicitly. If the user did not define one, it provides a default. So in a Spring context we can always call `getMessage()` safely, because a `MessageSource` is always available.

```java
protected void initMessageSource() {
    // Get the bean factory
    ConfigurableListableBeanFactory beanFactory = getBeanFactory();
    // Check whether a bean named MESSAGE_SOURCE_BEAN_NAME already exists
    if (beanFactory.containsLocalBean(MESSAGE_SOURCE_BEAN_NAME)) {
        // If it exists, get that bean
        this.messageSource = beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MessageSource.class);
        // If the current MessageSource is hierarchical and has no parent MessageSource set
        if (this.parent != null && this.messageSource instanceof HierarchicalMessageSource) {
            HierarchicalMessageSource hms = (HierarchicalMessageSource) this.messageSource;
            if (hms.getParentMessageSource() == null) {
                // Set the parent context as the parent MessageSource (if no parent MessageSource was registered before)
                hms.setParentMessageSource(getInternalParentMessageSource());
            }
        }
        // ... [code omitted for brevity]
    }
    else {
        // If there is no bean named MESSAGE_SOURCE_BEAN_NAME, create a DelegatingMessageSource and register it in the context
        // Use DelegatingMessageSource so that getMessage calls can be handled
        DelegatingMessageSource dms = new DelegatingMessageSource();
        dms.setParentMessageSource(getInternalParentMessageSource());
        this.messageSource = dms;
        beanFactory.registerSingleton(MESSAGE_SOURCE_BEAN_NAME, this.messageSource);
        // ... [code omitted for brevity]
    }
}
```

We come to step 2 in `org.springframework.context.support.AbstractApplicationContext#refresh`. The method `org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization` then calls `preInstantiateSingletons` in `DefaultListableBeanFactory` to create all remaining non-lazy singleton beans.

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
	if (bean instanceof MessageSourceAware) {
        ((MessageSourceAware) bean).setMessageSource(this.applicationContext);
    }
    // ... [code omitted for brevity]
}
```

Finally, execution reaches our own logic. The `MyMessageSourceAware` class uses the `MessageSourceAware` interface to get a reference to the `MessageSource` automatically. This reference can be used to get internationalized messages for different languages or regions. Using the injected `MessageSource`, it reads two internationalized messages from the property files and prints them. One is in English and the other is in Simplified Chinese.

```java
public class MyMessageSourceAware implements MessageSourceAware {

    private MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void getMessage() {
        System.out.println("English："+messageSource.getMessage("greeting", null, Locale.ENGLISH));
        System.out.println("Chinese: "+messageSource.getMessage("greeting", null, Locale.SIMPLIFIED_CHINESE));
    }
}
```

### 8. Notes

1. **Explicit configuration**
   + Make sure your Spring context has a `MessageSource` bean, usually named "messageSource". Spring provides a default one, but to meet custom needs you may have to configure it explicitly.

2. **Lifecycle timing**
   + `setMessageSource` of `MessageSourceAware` is called after the normal properties are set and before initialization methods (such as `afterPropertiesSet` of `InitializingBean`, or any custom init method). Make sure your bean does not expect to use the `MessageSource` early in its lifecycle (for example in the constructor).

3. **File location and naming**
   + If you use `ResourceBundleMessageSource` or a similar mechanism, make sure your property files are on the classpath and match the basename set in the `MessageSource` configuration.

4. **Encoding**
   + Property files use ISO-8859-1 encoding by default. If your messages contain characters outside this encoding (such as Chinese or Russian), use Unicode escapes or set the file encoding correctly.

5. **Parent and child contexts**
   + With Spring parent and child contexts (for example in a web application), a child context can access the `MessageSource` of the parent context, but not the other way around. Make sure you configure the `MessageSource` in the right context.

6. **Avoid hard-coding**
   + Avoid hard-coding message keys or default messages in code. It is better to manage them in property files. Then, when you need to change them or add a new language later, you do not have to change the code.

7. **Default messages**
   + When you get a message with `MessageSource`, consider providing a default message. It works as a fallback when the message is not found, and it avoids throwing an exception.

### 8. Summary

#### Best Practices Summary

1. **Entry class**
   + In the `MessageSourceAwareApplication` class, `AnnotationConfigApplicationContext` starts the Spring application. This context is designed for Java annotation-based configuration. On startup, it loads the `MyConfiguration` class, gets the `MyMessageSourceAware` bean from the context, and then calls the `getMessage` method to show the messages.

2. **Configuration class**
   + `MyConfiguration` is a Java-based Spring configuration class that defines two beans: `MyMessageSourceAware` and `messageSource`. The `messageSource` bean is a `ResourceBundleMessageSource` instance. It reads internationalized messages from the property files with the basename `i18n/messages`.

3. **Implementing the MessageSourceAware interface**
   + The `MyMessageSourceAware` class implements `MessageSourceAware`. So the Spring container injects a `MessageSource` instance into this bean automatically, through the `setMessageSource` method.

4. **Getting messages**
   + In the `getMessage` method of `MyMessageSourceAware`, the injected `MessageSource` is used to get and print internationalized messages in two languages: English and Simplified Chinese.

5. **Result**
   + When the application runs, it gets and shows the English and Simplified Chinese internationalized messages from the matching property files.

#### Source Code Analysis Summary

1. **Application startup**
   + We start the application from `MessageSourceAwareApplication`. `AnnotationConfigApplicationContext` initializes the Spring container and loads the `MyConfiguration` configuration.

2. **Container initialization**
   + The constructor of `AnnotationConfigApplicationContext` runs `register` and `refresh`. `refresh` is the most important. It triggers the container initialization and the bean creation process.

3. **Message source initialization**
   + In the container's `refresh` method, the first thing is to make sure a `MessageSource` bean exists. This is done by the `initMessageSource` method. If no `MessageSource` bean is defined explicitly, Spring provides a default one, so the application context always has one available.

4. **Bean instantiation**
   + Then, in `refresh`, the `finishBeanFactoryInitialization` method is called, and the container starts to instantiate all non-lazy singleton beans.

5. **Bean lifecycle**
   + While beans are created, the Spring container makes sure all lifecycle callbacks run correctly. The most important ones are the `BeanPostProcessors`. They are a plug-in mechanism that lets us run custom logic before and after bean initialization.

6. **Handling Aware interfaces**
   + `ApplicationContextAwareProcessor` is a special `BeanPostProcessor` that looks at beans implementing "Aware" interfaces. For beans that implement `MessageSourceAware`, this processor injects the `MessageSource` of the application context automatically.

7. **Getting messages**
   + In our `MyMessageSourceAware` class, we have a reference to the `MessageSource`. We call its `getMessage` method to get two internationalized messages from the property files and print them.