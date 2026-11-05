## DestructionAwareBeanPostProcessor

- [DestructionAwareBeanPostProcessor](#destructionawarebeanpostprocessor)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133845486) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [DestructionAwareBeanPostProcessor source](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-destructionAwareBeanPostProcessor)

### 2. Interface Description

The `DestructionAwareBeanPostProcessor` interface gives you a chance to do extra work before a bean is destroyed. Its main job is to let you run custom logic when a bean is about to be destroyed.

### 3. Interface Source

`DestructionAwareBeanPostProcessor` is a core Spring interface, available since version 1.0.1. It calls custom initialization and destruction callbacks for specific bean types. It lets us hook into the Spring bean lifecycle and run our own logic in the destruction phase.

```java
/**
 * Subinterface of BeanPostProcessor that adds a before-destruction callback.
 *
 * The typical usage will be to invoke custom destruction callbacks on
 * specific bean types, matching corresponding initialization callbacks.
 *
 * @author Juergen Hoeller
 * @since 1.0.1
 */
public interface DestructionAwareBeanPostProcessor extends BeanPostProcessor {

    /**
     * Apply this BeanPostProcessor to the given bean instance before its
     * destruction, e.g. invoking custom destruction callbacks.
     * Like DisposableBean's {@code destroy} and a custom destroy method, this
     * callback will only apply to beans which the container fully manages the
     * lifecycle for. This is usually the case for singletons and scoped beans.
     * @param bean the bean instance to be destroyed
     * @param beanName the name of the bean
     * @throws org.springframework.beans.BeansException in case of errors
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     * @see org.springframework.beans.factory.support.AbstractBeanDefinition#setDestroyMethodName(String)
     */
    void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException;

    /**
     * Determine whether the given bean instance requires destruction by this
     * post-processor.
     * The default implementation returns true. If a pre-5 implementation of
     * DestructionAwareBeanPostProcessor does not provide a concrete implementation
     * of this method, Spring silently assumes true as well.
     * @param bean the bean instance to check
     * @return true if postProcessBeforeDestruction is supposed to be called
     * for this bean instance eventually, or false if not needed
     * @since 4.3
     */
    default boolean requiresDestruction(Object bean) {
        return true;
    }
}
```

### 4. Main Features

1. **Logic before destruction**
   + With `postProcessBeforeDestruction(Object bean, String beanName)`, we can run custom destruction logic for a bean. The container calls this method when it marks a bean for destruction. (For example, to release resources, record state, or clean up dependencies when the container closes.)

### 5. Best Practices

First, the entry class. The context is `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor. Then we get a bean of type `ConnectionService` from the Spring context and print the `isConnected` state.

```java
public class DestructionAwareBeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        ConnectionService connection = context.getBean("connectionService", ConnectionService.class);
        System.out.println("Is connected: " + connection.isConnected());
        context.close();
    }
}
```

Here we use the `@Bean` annotation to define two beans, so that the Spring container handles both `ConnectionService` and `MyDestructionAwareBeanPostProcessor`.

```java
@Configuration
public class MyConfiguration {

    @Bean
    public static MyDestructionAwareBeanPostProcessor myDestructionAwareBeanPostProcessor() {
        return new MyDestructionAwareBeanPostProcessor();
    }

    @Bean
    public ConnectionService connectionService() {
        return new ConnectionServiceImpl();
    }
}
```

The `MyDestructionAwareBeanPostProcessor` class manages the lifecycle of the `ConnectionServiceImpl` bean. After the bean is initialized, its connection is opened. When the bean is about to be destroyed, its connection is closed. This makes sure the resource is released when it is no longer needed.

```java
public class MyDestructionAwareBeanPostProcessor implements DestructionAwareBeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ConnectionServiceImpl) {
            ((ConnectionServiceImpl) bean).openConnection();
        }
        return bean;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (bean instanceof ConnectionServiceImpl) {
            ((ConnectionServiceImpl) bean).closeConnection();
        }
    }

    @Override
    public boolean requiresDestruction(Object bean) {
        return (bean instanceof ConnectionServiceImpl);
    }
}
```

A connection service interface.

```java
public interface ConnectionService {

    void openConnection();

    void closeConnection();

    boolean isConnected();
}
```

The `ConnectionServiceImpl` class is a mock connection service. It tracks its connection state. Callers can open and close the connection and check its state.

```java
public class ConnectionServiceImpl implements ConnectionService {

    private boolean isConnected = false;

    @Override
    public void openConnection() {
        isConnected = true;
        System.out.println("connection opened.");
    }

    @Override
    public void closeConnection() {
        if (isConnected) {
            isConnected = false;
            System.out.println("connection closed.");
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }
}
```

The output shows the following. In `postProcessAfterInitialization` of `MyDestructionAwareBeanPostProcessor`, we detect that the bean is a `ConnectionServiceImpl` and call its `openConnection` method, so the connection is opened. Then the main method of `DestructionAwareBeanPostProcessorApplication` calls `isConnected` and prints the result. This shows the connection is open while the application context is running. In `postProcessBeforeDestruction` of `MyDestructionAwareBeanPostProcessor`, we detect that the bean is a `ConnectionServiceImpl` and call its `closeConnection` method, so the connection is closed.

```java
connection opened.
Is connected: true
connection closed.
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: DestructionAwareBeanPostProcessor Sequence Diagram
    participant DestructionAwareBeanPostProcessorApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant DefaultSingletonBeanRegistry
    participant DisposableBeanAdapter
    participant MyDestructionAwareBeanPostProcessor
    
    DestructionAwareBeanPostProcessorApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses	)<br>The application starts initializing the context
    AnnotationConfigApplicationContext-->>DestructionAwareBeanPostProcessorApplication:Initialization complete
    DestructionAwareBeanPostProcessorApplication->>AbstractApplicationContext:close()<br>Request to close the context
    AbstractApplicationContext->>AbstractApplicationContext:doClose()<br>Run the close logic
    AbstractApplicationContext->>AbstractApplicationContext:destroyBeans()<br>Start destroying beans
    AbstractApplicationContext->>DefaultListableBeanFactory:destroySingletons()<br>Destroy singleton beans
    DefaultListableBeanFactory->>DefaultSingletonBeanRegistry:super.destroySingletons()<br>Call the parent destroy method
    DefaultSingletonBeanRegistry-->>DefaultListableBeanFactory:destroySingleton(beanName)<br>Destroy a single bean
    DefaultListableBeanFactory->>DefaultSingletonBeanRegistry:super.destroySingleton(beanName)<br>Call the parent bean destroy method
    DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:destroyBean(beanName,bean)<br>Run the bean destruction
    DefaultSingletonBeanRegistry->>DisposableBeanAdapter:destroy()<br>The adapter runs destruction
    DisposableBeanAdapter->>MyDestructionAwareBeanPostProcessor:postProcessBeforeDestruction(bean,beanName)<br>Run custom destruction logic
    AbstractApplicationContext-->>DestructionAwareBeanPostProcessorApplication:Context close request finished
~~~

### 7. Source Code Analysis

First, the entry class. The context is `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor. Then we get a bean of type `ConnectionService` from the Spring context and print the `isConnected` state.

```java
public class DestructionAwareBeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        ConnectionService connection = context.getBean("connectionService", ConnectionService.class);
        System.out.println("Is connected: " + connection.isConnected());
        context.close();
    }
}
```

In `org.springframework.context.support.AbstractApplicationContext#close`, Spring first opens a synchronized block on the `startupShutdownMonitor` object. This makes sure only one thread runs the code inside at a time, which prevents resource conflicts and inconsistent data. Then it calls `doClose`. Finally, it removes the JVM shutdown hook, if one was registered.

```java
@Override
public void close() {
    synchronized (this.startupShutdownMonitor) {
        doClose();
        // If we registered a JVM shutdown hook, we don't need it anymore now:
        // We've already explicitly closed the context.
        if (this.shutdownHook != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
            }
            catch (IllegalStateException ex) {
                // ignore - VM is already shutting down
            }
        }
    }
}
```

The method `org.springframework.context.support.AbstractApplicationContext#doClose` calls `destroyBeans`.

```java
protected void doClose() {
    // ... [code omitted for brevity]
    // Destroy all cached singletons in the context's BeanFactory.
    destroyBeans();
    // ... [code omitted for brevity]
}
```

In `org.springframework.context.support.AbstractApplicationContext#destroyBeans`, `getBeanFactory()` returns the Spring `BeanFactory`. Then `destroySingletons` is called on it. This method destroys all singleton beans cached in the `BeanFactory`.

```java
protected void destroyBeans() {
    getBeanFactory().destroySingletons();
}
```

In `org.springframework.beans.factory.support.DefaultListableBeanFactory#destroySingletons`, Spring first calls `destroySingletons` of the parent class. This makes sure the destruction logic inherited from the parent class runs.

```java
@Override
public void destroySingletons() {
    super.destroySingletons();
    updateManualSingletonNames(Set::clear, set -> !set.isEmpty());
    clearByTypeCache();
}
```

In `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingletons`, Spring first takes all bean names from the key set of the `disposableBeans` field and turns them into a string array. `disposableBeans` may hold beans that implement the `DisposableBean` interface. These beans need special handling when the container is destroyed. Then Spring loops in reverse order, from the last one, and destroys all beans in the `disposableBeans` list. This keeps dependencies correct: a bean that was created first should be destroyed last.

```java
public void destroySingletons() {
    // ... [code omitted for brevity]
    String[] disposableBeanNames;
    synchronized (this.disposableBeans) {
        disposableBeanNames = StringUtils.toStringArray(this.disposableBeans.keySet());
    }
    for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
        destroySingleton(disposableBeanNames[i]);
    }
    // ... [code omitted for brevity]
}
```

In `org.springframework.beans.factory.support.DefaultListableBeanFactory#destroySingleton`, Spring first calls `destroySingleton` of the parent class. This makes sure the destruction logic inherited from the parent class runs.

```java
@Override
public void destroySingleton(String beanName) {
    super.destroySingleton(beanName);
    removeManualSingletonName(beanName);
    clearByTypeCache();
}
```

In `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingleton`, Spring first uses the `synchronized` keyword on the `disposableBeans` object. This makes access and changes safe in a multi-threaded environment. It removes the bean with the given name from the `disposableBeans` collection and casts it to `DisposableBean`. Finally, it calls `destroyBean` to do the real destruction.

```java
public void destroySingleton(String beanName) {
    // Remove a registered singleton of the given name, if any.
    removeSingleton(beanName);

    // Destroy the corresponding DisposableBean instance.
    DisposableBean disposableBean;
    synchronized (this.disposableBeans) {
        disposableBean = (DisposableBean) this.disposableBeans.remove(beanName);
    }
    destroyBean(beanName, disposableBean);
}
```

In `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroyBean`, Spring calls the `destroy` method of `bean` directly. `bean` is a `DisposableBean`, so it always has a `destroy` method. That method holds the custom destruction logic of the bean.

```java
protected void destroyBean(String beanName, @Nullable DisposableBean bean) {
    // ... [code omitted for brevity]
    // Actually destroy the bean now...
    if (bean != null) {
        try {
            bean.destroy();
        }
        catch (Throwable ex) {
            // ... [code omitted for brevity]
        }
    }
    // ... [code omitted for brevity]
}
```

In `org.springframework.beans.factory.support.DisposableBeanAdapter#destroy`, a loop goes over the `beanPostProcessors` collection. Each element is a `DestructionAwareBeanPostProcessor`. This is a sub-interface of `BeanPostProcessor` that adds a callback for the destruction phase.

```java
@Override
public void destroy() {
    if (!CollectionUtils.isEmpty(this.beanPostProcessors)) {
        for (DestructionAwareBeanPostProcessor processor : this.beanPostProcessors) {
            processor.postProcessBeforeDestruction(this.bean, this.beanName);
        }
    }
    // ... [code omitted for brevity]
}
```

Finally, we reach our own method `com.xcs.spring.config.MyDestructionAwareBeanPostProcessor#postProcessBeforeDestruction`. If the bean is an instance of `ConnectionServiceImpl`, its `closeConnection` method is called. This makes sure the connection of a `ConnectionServiceImpl` bean is always closed before the bean is destroyed.

```java
public class MyDestructionAwareBeanPostProcessor implements DestructionAwareBeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ConnectionServiceImpl) {
            ((ConnectionServiceImpl) bean).openConnection();
        }
        return bean;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (bean instanceof ConnectionServiceImpl) {
            ((ConnectionServiceImpl) bean).closeConnection();
        }
    }

    @Override
    public boolean requiresDestruction(Object bean) {
        return (bean instanceof ConnectionServiceImpl);
    }
}
```

### 8. Notes

1. **Performance impact**
   + Every `DestructionAwareBeanPostProcessor` is called at the end of a bean's lifecycle. Make sure its code runs fast, so it does not become a bottleneck.

2. **Check `requiresDestruction`**
   + Implement `requiresDestruction` to say which beans need processing at destruction. This avoids needless `postProcessBeforeDestruction` calls and improves performance.

3. **Exception handling**
   + Any kind of exception can happen in `postProcessBeforeDestruction`. Handle it properly, so it does not stop other beans from being destroyed.

4. **Work well with other `BeanPostProcessors`**
   + If your application has other `BeanPostProcessors`, make sure they do not cause problems for each other.

5. **Work together with the `@PreDestroy` annotation**
   + If a bean already defines its destroy method with `@PreDestroy`, that method runs before `postProcessBeforeDestruction` is called. Make sure the logic of the two does not conflict.

### 9. Summary

#### Best Practices Summary

1. **Application startup**:

   - In the `main` method of `DestructionAwareBeanPostProcessorApplication`, an `AnnotationConfigApplicationContext` is created and configured with the `MyConfiguration` class.

   - Through this context, we get a bean of type `ConnectionService` named `connectionService`.


2. **Spring container initialization**:

   - During initialization, the Spring container creates two beans from `MyConfiguration`: `MyDestructionAwareBeanPostProcessor` and `ConnectionServiceImpl`.

   - After the `ConnectionServiceImpl` bean is initialized, the `postProcessAfterInitialization` method of `MyDestructionAwareBeanPostProcessor` is called. At this point the connection is opened.

   - `MyDestructionAwareBeanPostProcessor` makes sure the connection is opened right after the `ConnectionServiceImpl` bean is initialized.


4. **Application running**:

   - In the `main` method, the application checks the connection state of the `ConnectionService` bean and prints the result. It shows the connection is "open".

   - Then the context is closed, which means all singleton beans will be destroyed.


5. **Destruction phase**:

   - When the context closes, the `postProcessBeforeDestruction` method of `MyDestructionAwareBeanPostProcessor` is called.

   - In this method we check whether the bean is an instance of `ConnectionServiceImpl`. It is, so `closeConnection` is called and the connection is closed.

   - This makes sure the resource is released when the bean's lifecycle ends.


6. **Output**:
   - The console output shows that the resource is opened at the start of the bean's lifecycle and closed at the end.


#### Source Code Analysis Summary

1. **Application startup**:

   - The application starts with `AnnotationConfigApplicationContext`, configured with the `MyConfiguration` class.

   - Then it gets a bean of type `ConnectionService` from the Spring container and checks its connection state.


2. **Spring container destruction**:

   - By calling `context.close()`, the application starts the shutdown process of the Spring container.

   - The shutdown process first uses synchronization, so only one thread can run the shutdown at any time.

   - It calls `doClose` to run the real shutdown logic.

   - The JVM shutdown hook is removed, which shows that the context was closed explicitly.


3. **Destroying beans**:

   - In `doClose`, `destroyBeans` is called. Its main job is to destroy all singleton beans.

   - `destroyBeans` then calls `destroySingletons` of the `BeanFactory`.


4. **Destroying singleton beans**:

   - `destroySingletons` destroys all singleton beans cached in the `BeanFactory`.

   - It first gets the names of all beans to destroy, then loops over them in reverse order. This makes sure beans that others depend on are destroyed later.

   - For each bean to destroy, `destroySingleton` is called.


5. **Running the destruction logic**:

   - In `destroySingleton`, the bean that implements `DisposableBean` is taken from the `disposableBeans` list, and its `destroy` method is called.

   - In the `destroy` method of `DisposableBeanAdapter`, all registered `DestructionAwareBeanPostProcessor`s are looped over. For each processor, `postProcessBeforeDestruction` is called.


6. **Custom destruction logic**:

   - Finally, the `postProcessBeforeDestruction` method of our own `DestructionAwareBeanPostProcessor` is called.

   - In this method, we check whether the bean is an instance of `ConnectionServiceImpl`. If it is, we close its connection.
