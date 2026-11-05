## EmbeddedValueResolverAware

- [EmbeddedValueResolverAware](#embeddedvalueresolveraware)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133914999) 📚 **Contents** - [All Articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [EmbeddedValueResolverAware source code](https://github.com/xuchengsheng/spring-reading/tree/master/spring-aware/spring-aware-embeddedValueResolverAware)

### 2. Interface Description

The `EmbeddedValueResolverAware` interface provides a string value resolver. It can resolve placeholders and expressions in bean properties. If you know the Spring `${...}` placeholders and `#{...}` expressions, this interface helps you resolve such values in your own components.

### 3. Interface Source

`EmbeddedValueResolverAware` is a core Spring interface, available since 3.0.3. Spring automatically injects a `StringValueResolver` instance into any object that implements `EmbeddedValueResolverAware`.

```java
/**
 * Interface to be implemented by any object that wishes to be notified of a StringValueResolver for resolving embedded definition values.
 *
 * This is an alternative to implementing the full
 * ConfigurableBeanFactory through the ApplicationContextAware/BeanFactoryAware interfaces.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 3.0.3
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#resolveEmbeddedValue(String)
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#getBeanExpressionResolver()
 * @see org.springframework.beans.factory.config.EmbeddedValueResolver
 */
public interface EmbeddedValueResolverAware extends Aware {

	/**
	 * Set the StringValueResolver to use for resolving embedded definition values.
	 */
	void setEmbeddedValueResolver(StringValueResolver resolver);

}
```

### 4. Main Features

1. **Resolve embedded string values**
   + A bean property or constructor argument may have a value such as `${property.name}` or `#{some.expression}`. It must be resolved to a real value. `StringValueResolver` helps do this.

2. **Avoid a direct dependency on `ConfigurableBeanFactory`**
   + With `EmbeddedValueResolverAware`, we get this resolving feature indirectly, without depending on the whole `ConfigurableBeanFactory`. This is a lighter way, focused on one feature, to resolve embedded values.

3. **Automatic injection of `StringValueResolver`**
   + When our bean implements `EmbeddedValueResolverAware`, the Spring container calls `setEmbeddedValueResolver` automatically during bean initialization and injects a `StringValueResolver` instance. The bean can then use it at any time in its lifecycle to resolve string values.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`. Then it gets a bean of type `MyEmbeddedValueResolverAware` from the Spring context and calls the `resolve` method.

```java
public class EmbeddedValueResolverAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyEmbeddedValueResolverAware resolverAware = context.getBean(MyEmbeddedValueResolverAware.class);
        resolverAware.resolve();
    }
}
```

The `@Bean` annotation defines a bean here, to make sure the Spring container creates `MyEmbeddedValueResolverAware`.

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyEmbeddedValueResolverAware myEmbeddedValueResolverAware(){
        return new MyEmbeddedValueResolverAware();
    }
}
```

The `MyEmbeddedValueResolverAware` class implements `EmbeddedValueResolverAware`. When the Spring container initializes a bean of this class, this method is called and the container passes in a `StringValueResolver` instance. The `resolve()` method then uses the injected `stringValueResolver` to resolve a string that contains a placeholder and an SpEL expression, and prints the result to the console.

```java
public class MyEmbeddedValueResolverAware implements EmbeddedValueResolverAware {

    private StringValueResolver stringValueResolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.stringValueResolver = resolver;
    }

    public void resolve() {
        String resolvedValue = stringValueResolver.resolveStringValue("Hello, ${user.name:xcs}! Today is #{T(java.time.LocalDate).now().toString()}");
        System.out.println(resolvedValue);
    }
}
```

The output shows that the embedded string value was resolved with the Spring `Environment` and SpEL, and the result is as expected.

```java
Hello, Lex! Today is 2023-10-03
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: EnvironmentAware sequence diagram
    participant EmbeddedValueResolverAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant ApplicationContextAwareProcessor
    participant MyEmbeddedValueResolverAware
    
    EmbeddedValueResolverAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Create the context
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
    ApplicationContextAwareProcessor->>MyEmbeddedValueResolverAware:setEmbeddedValueResolver(resolver)<br>Set the StringValueResolver
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:Return the bean
    AbstractBeanFactory-->>DefaultListableBeanFactory:Return the bean
    AnnotationConfigApplicationContext-->>EmbeddedValueResolverAwareApplication:Initialization complete
~~~

### 7. Source Code Analysis

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`. Then it gets a bean of type `MyEmbeddedValueResolverAware` from the Spring context and calls the `resolve` method.

```java
public class EmbeddedValueResolverAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyEmbeddedValueResolverAware resolverAware = context.getBean(MyEmbeddedValueResolverAware.class);
        resolverAware.resolve();
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
	if (bean instanceof EmbeddedValueResolverAware) {
        ((EmbeddedValueResolverAware) bean).setEmbeddedValueResolver(this.embeddedValueResolver);
    }
    // ... [code omitted for brevity]
}
```

Finally, execution reaches our own logic. The `MyEmbeddedValueResolverAware` class implements `EmbeddedValueResolverAware`. When the Spring container initializes a bean of this class, this method is called and the container passes in a `StringValueResolver` instance. The `resolve()` method then uses the injected `stringValueResolver` to resolve a string that contains a placeholder and an SpEL expression, and prints the result to the console.

```java
public class MyEmbeddedValueResolverAware implements EmbeddedValueResolverAware {

    private StringValueResolver stringValueResolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.stringValueResolver = resolver;
    }

    public void resolve() {
        String resolvedValue = stringValueResolver.resolveStringValue("Hello, ${user.name:xcs}! Today is #{T(java.time.LocalDate).now().toString()}");
        System.out.println(resolvedValue);
    }
}
```

### 8. Notes

1. **Use the right environment**
   + Use it inside a Spring environment. `StringValueResolver` needs the Spring context to resolve embedded values correctly.

2. **No early use**
   + `setEmbeddedValueResolver` is called during bean initialization. If you try to use the `StringValueResolver` too early (for example in the constructor), it may not be injected yet.

3. **Default values**
   + With the `${user.name:xcs}` syntax, if `user.name` is not defined in the environment, `xcs` is used. This avoids errors from missing configuration.

4. **Know the scope of resolving**
   + `EmbeddedValueResolverAware` is mainly used to resolve placeholders and SpEL expressions. Do not confuse it with more complex bean resolving logic.

5. **Error handling**
   + When resolving a string value fails, Spring usually throws an exception. Handle these exceptions properly in your code.

6. **Interaction with other Aware interfaces**
   + If your bean implements several `Aware` interfaces, make sure you understand the initialization timing and order of each one, and how they interact with other Aware methods (such as `setBeanFactory` or `setApplicationContext`).

### 9. Summary

#### Best Practices Summary

1. **Entry class**
   + In `EmbeddedValueResolverAwareApplication`, we initialize the Spring `AnnotationConfigApplicationContext` and load `MyConfiguration` as the configuration class. Then we get the `MyEmbeddedValueResolverAware` bean from the context and call its `resolve` method.

2. **Configuration and bean declaration**
   + In the `MyConfiguration` class, we declare `MyEmbeddedValueResolverAware` as a bean. This makes sure the Spring container manages it and injects a `StringValueResolver` instance into it.

3. **Embedded value resolving**
   + The `MyEmbeddedValueResolverAware` class implements `EmbeddedValueResolverAware`. So when the bean is initialized, Spring provides a `StringValueResolver` instance automatically. This resolver is then used to resolve the string "Hello, ${user.name:xcs}! Today is #{T(java.time.LocalDate).now().toString()}".

#### Source Code Analysis Summary

1. **Application startup**
   + In the `EmbeddedValueResolverAwareApplication` class, `AnnotationConfigApplicationContext` starts the Spring application and loads the `MyConfiguration` class.

2. **Container initialization**
   + In the `AnnotationConfigApplicationContext` constructor, `refresh()` is called to initialize the Spring container.

3. **Instantiating beans**
   + In `refresh()` of `AbstractApplicationContext`, `finishBeanFactoryInitialization` is called to make sure all singleton beans are instantiated up front.

4. **Bean pre-instantiation**
   + `preInstantiateSingletons` of `DefaultListableBeanFactory` makes sure all non-lazy singleton beans are instantiated. The core operation is calling `getBean(beanName)`.

5. **Getting the bean instance**
   + `getBean` of `AbstractBeanFactory` calls `doGetBean` to really create the bean, handle exceptions and dependencies, and return the bean instance.

6. **Getting the singleton**
   + `getSingleton` of `DefaultSingletonBeanRegistry` makes sure the bean exists as a singleton. It gets the bean from the cache, or creates a new instance with the given `ObjectFactory`.

7. **Creating the bean instance**
   + `createBean` of `AbstractAutowireCapableBeanFactory` calls `doCreateBean` to really instantiate and initialize the bean, so that it is fully configured and ready.

8. **Bean initialization**
   + `initializeBean` of `AbstractAutowireCapableBeanFactory` makes sure the bean is initialized correctly. Its call to `applyBeanPostProcessorsBeforeInitialization` is a key point in the Spring lifecycle. It lets BeanPostProcessors act before the bean is initialized.

9. **Handling Aware interfaces**
   + During bean initialization, `ApplicationContextAwareProcessor` makes sure beans that implement `Aware` interfaces are handled correctly. These beans are automatically "aware" of their runtime environment or specific dependencies.

10. **Value resolving**
    + Finally, our `MyEmbeddedValueResolverAware` bean receives a `StringValueResolver` instance. When the `resolve` method is called, it uses this resolver to resolve the embedded string value and prints it to the console.