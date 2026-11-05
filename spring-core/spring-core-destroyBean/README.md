## Bean Destruction Process

- [Bean Destruction Process](#bean-destruction-process)
  - [1. Overview](#1-overview)
  - [2. Background Knowledge](#2-background-knowledge)
  - [3. Basic Description](#3-basic-description)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
  - [7. Source Code Analysis](#7-source-code-analysis)
    - [Register the Adapter](#register-the-adapter)
    - [Bean Destruction](#bean-destruction)
  - [8. Notes](#8-notes)
  - [9. Summary](#9-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Background Knowledge

1. [**Bean Definition Registration**](https://github.com/xuchengsheng/spring-reading/tree/master/spring-core/spring-core-registerBeanDefinition)

   + Bean registration loads and parses configuration files and extracts the bean definitions from them. The parsed bean information, such as class name, scope and properties, is registered in the Spring container. From the configuration, the container gets the bean metadata and creates bean definitions with the class name, scope (for example singleton or prototype), property values and so on. Each definition is linked to the container by a unique identifier, so the bean can be retrieved and managed later through the ApplicationContext.

2. [**Bean Initialization Process**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-core/spring-core-getBean/README.md)

   + The bean is instantiated through its constructor. Then properties are injected to satisfy its dependencies. If the bean implements an Aware interface, the container injects context information through the matching callback. The registered post-processors work on the bean before and after initialization. If the bean implements InitializingBean, the container calls afterPropertiesSet to run the initialization logic. You can also set a custom initialization method with the init-method attribute. At the end, the bean is marked as initialized and the application can use it.

3. [**Bean Dependency Resolution Process**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-core/spring-core-resolveDependency/README.md)

   + A bean declares what other beans it needs. The container looks up these dependencies, that is, it searches the ApplicationContext for the bean definitions it needs. Then the container injects the found bean instances into the matching properties or constructor parameters. For circular dependencies, Spring uses a proxy object that is exposed early. Finally, the container supports lazy dependency resolution. The dependency is resolved and injected only when the bean is needed. This improves performance and reduces startup time.

### 3. Basic Description

The container calls the `destroy` method of beans that implement the `DisposableBean` interface to run custom destruction logic. If the bean configuration sets a custom destroy method with the `destroy-method` attribute, the container calls it too. For post-processors that implement `DestructionAwareBeanPostProcessor`, the container calls their `postProcessBeforeDestruction` and `postProcessAfterDestruction` methods, which allows extra cleanup. With the `@PreDestroy` annotation, you can mark a bean method as a cleanup step before destruction.

### 4. Main Features

1. **Run custom destruction logic**

   - Calls the `destroy` method of beans that implement `DisposableBean`. It runs your own destruction logic, such as releasing resources or doing needed cleanup.

2. **Call a custom destroy method**

   - If the bean configuration sets a custom destroy method with the `destroy-method` attribute, the container calls it. This lets you run specific cleanup when the bean is destroyed.

3. **Post-processor cleanup**

   - For post-processors that implement `DestructionAwareBeanPostProcessor`, the container calls `postProcessBeforeDestruction` and `postProcessAfterDestruction` before and after destruction. This allows extra cleanup.

4. **Run `@PreDestroy` methods**

   - With the `@PreDestroy` annotation, you can mark a bean method as a cleanup step before destruction. This makes sure specific business logic runs on destruction.

5. **Trigger destruction notification**

   - The Spring container triggers a destruction notification to the related listeners or observers. This allows the application to run specific handling when a bean is destroyed.

### 5. Best Practices

This example uses Spring annotation-based configuration. It creates an application context, registers a bean, and then closes the application context.

```java
public class DestroyBeanApplication {

    public static void main(String[] args) {
        // Create an annotation-based application context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // Register the MyBean configuration class so Spring manages the beans it defines
        context.register(MyBean.class);
        // Refresh the application context to initialize and start the Spring container
        context.refresh();
        // Close the application context to destroy the container and release resources
        context.close();
    }
}
```

This class implements the Spring `DisposableBean` interface. With this interface, we can run custom cleanup logic when the bean is destroyed.

```java
public class MyBean implements DisposableBean {

    @Override
    public void destroy() throws Exception {
        System.out.println("MyBean was destroyed");
    }
}
```

The result shows that when the Spring container closes, the destruction phase of the bean is triggered. A bean that implements `DisposableBean` has its `destroy` method called. In our `MyBean` class, the `destroy` method has only one line. It prints a message, so the output we see at run time is `"MyBean was destroyed"`.

```java
MyBean was destroyed
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
Title: Bean Destruction Process Sequence Diagram

par Register adapter phase
DestroyBeanApplication->>AbstractApplicationContext: refresh()
Note over AbstractApplicationContext: Initialize the application context

AbstractApplicationContext->>AbstractApplicationContext: finishBeanFactoryInitialization(beanFactory)
Note over AbstractApplicationContext: Finish initializing the bean factory

AbstractApplicationContext->>DefaultListableBeanFactory: preInstantiateSingletons()
Note over DefaultListableBeanFactory: Pre-instantiate all singleton beans

DefaultListableBeanFactory->>AbstractBeanFactory: getBean(name)
Note over AbstractBeanFactory: Get the bean instance from the bean factory

AbstractBeanFactory->>AbstractBeanFactory: doGetBean(name, requiredType, args, typeCheckOnly)
Note over AbstractBeanFactory: The method that actually gets the bean

AbstractBeanFactory->>DefaultSingletonBeanRegistry: getSingleton(beanName, singletonFactory)
Note over DefaultSingletonBeanRegistry: Get the singleton bean from the singleton registry

DefaultSingletonBeanRegistry->>AbstractBeanFactory: getObject()
Note over AbstractBeanFactory: Get the bean instance object

AbstractBeanFactory->>AbstractAutowireCapableBeanFactory: createBean(beanName, mbd, args)
Note over AbstractAutowireCapableBeanFactory: Create the bean instance

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory: doCreateBean(beanName, mbdToUse, args)
Note over AbstractAutowireCapableBeanFactory: The method that actually creates the bean instance

AbstractAutowireCapableBeanFactory->>AbstractBeanFactory: registerDisposableBeanIfNecessary(beanName, bean, mbd)
Note over AbstractBeanFactory: Register the destruction adapter of the bean

AbstractBeanFactory->>DisposableBeanAdapter: new DisposableBeanAdapter(bean, beanName, beanDefinition, postProcessors, acc)
Note over DisposableBeanAdapter: Create the destruction adapter of the bean

DisposableBeanAdapter->>AbstractBeanFactory: Return the destruction adapter
Note over AbstractBeanFactory: Return the destruction adapter

AbstractBeanFactory->>DefaultSingletonBeanRegistry: registerDisposableBean(beanName, bean)
Note over DefaultSingletonBeanRegistry: Register the destruction adapter of the singleton bean
end

par Bean destruction phase
DestroyBeanApplication->>AbstractApplicationContext: close()
Note over AbstractApplicationContext: Close the application context

AbstractApplicationContext->>AbstractApplicationContext: doClose()
Note over AbstractApplicationContext: Run the close operation

AbstractApplicationContext->>AbstractApplicationContext: destroyBeans()
Note over AbstractApplicationContext: Destroy all beans

AbstractApplicationContext->>DefaultListableBeanFactory: destroySingletons()
Note over DefaultListableBeanFactory: Destroy all singleton beans

DefaultListableBeanFactory->>DefaultSingletonBeanRegistry: destroySingletons()
Note over DefaultSingletonBeanRegistry: Destroy all singleton beans

DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry: destroySingleton(beanName)
Note over DefaultSingletonBeanRegistry: Destroy a singleton bean

DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry: destroyBean(beanName, disposableBean)
Note over DefaultSingletonBeanRegistry: Destroy the bean through its destruction adapter

DefaultSingletonBeanRegistry->>DisposableBeanAdapter: destroy()
Note over DisposableBeanAdapter: Call the destroy method of the bean

DisposableBeanAdapter->>MyBean: destroy()
Note over MyBean: Run the destruction logic of MyBean
end
~~~

### 7. Source Code Analysis

#### Register the Adapter

In the `org.springframework.context.support.AbstractApplicationContext#refresh` method, we focus on `finishBeanFactoryInitialization(beanFactory)`. It instantiates all remaining non-lazy singleton beans. The other methods are not the focus of this reading, so we ignore them for now.

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    // ... [code omitted for brevity]
    // Instantiate all remaining (non-lazy-init) singletons.
    finishBeanFactoryInitialization(beanFactory);
    // ... [code omitted for brevity]
}
```

The `org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization` method calls the `preInstantiateSingletons` method of `DefaultListableBeanFactory` to create all remaining non-lazy singleton beans.

```java
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [code omitted for brevity]
    // Instantiate all remaining non-lazy singleton beans.
    beanFactory.preInstantiateSingletons();
}
```

The main goal of the `org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons` method is to instantiate all non-lazy singleton beans in advance. It is triggered after the Spring context is set up. This makes sure all singleton beans are created and initialized correctly. The core operation is `getBean(beanName)`. For every singleton bean defined in the container, `getBean` is called. This triggers the instantiation, initialization and dependency injection of the bean. If the bean has not been created before, this call creates and initializes it.

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

The `org.springframework.beans.factory.support.AbstractBeanFactory#getBean()` method calls `doGetBean` to do the actual work of creating the bean. It passes the bean name and some other default values. `doGetBean` does most of the work, such as finding the bean definition, creating the bean (if it does not exist yet) and handling dependencies.

```java
@Override
public Object getBean(String name) throws BeansException {
    return doGetBean(name, null, null, false);
}
```

The `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean` method first checks whether the requested bean is a singleton that already exists. If not, it creates a new instance. On the way, it handles possible problems such as circular references and makes sure the returned bean has the right type. This is a core part of bean lifecycle management in the Spring container.

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
        // For some beans (for example FactoryBeans), more processing may be needed to get the real bean instance
        beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
    }
    // ... [code omitted for brevity]

    // Make sure the returned bean instance matches the requested type
    return adaptBeanInstance(name, beanInstance, requiredType);
}
```

The `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton()` method gets an existing bean instance from the singleton cache, or creates a new one with the given `ObjectFactory`. This is a key part of making sure a bean exists as a singleton in the Spring container.

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    // The bean name must not be null
    Assert.notNull(beanName, "Bean name must not be null");

    // Synchronize access to the singleton cache to keep it thread safe
    synchronized (this.singletonObjects) {
        // Get the singleton object from the cache
        Object singletonObject = this.singletonObjects.get(beanName);

        // If it is not found in the cache
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

        // Return the singleton object
        return singletonObject;
    }
}
```

The main logic of the `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean()` method is to call `doCreateBean`. That is where bean instantiation, property population and initialization really happen. The method returns the newly created bean instance.

```java
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
    throws BeanCreationException {
    
    // ... [code omitted for brevity]
    
    try {
        // Normal bean instantiation, property injection and initialization.
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

The `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` method registers the destruction adapter while the bean instance is created. This lets the destruction logic run when the container closes.

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {

    // Register the bean so it can be handled on destruction.
    try {
        registerDisposableBeanIfNecessary(beanName, bean, mbd);
    }
    catch (BeanDefinitionValidationException ex) {
        // ... [code omitted for brevity]
    }
}
```

The `org.springframework.beans.factory.support.AbstractBeanFactory#registerDisposableBeanIfNecessary` method first checks whether the bean is a singleton and needs destruction logic. If it is a singleton, it registers a `DisposableBean` adapter that does all the destruction work, including `DestructionAwareBeanPostProcessors`, the `DisposableBean` interface and custom destroy methods. For a custom scope, it finds the matching Scope and registers a destruction callback.

```java
protected void registerDisposableBeanIfNecessary(String beanName, Object bean, RootBeanDefinition mbd) {
    // ... [code omitted for brevity]

    // Check that the scope is not prototype and that destruction is needed
    if (!mbd.isPrototype() && requiresDestruction(bean, mbd)) {
        if (mbd.isSingleton()) {
            // For a singleton, register a DisposableBean adapter that does all the destruction work
            // including DestructionAwareBeanPostProcessors, the DisposableBean interface, custom destroy methods and so on
            registerDisposableBean(beanName, new DisposableBeanAdapter(
                bean, beanName, mbd, getBeanPostProcessorCache().destructionAware, acc));
        } else {
            // For a custom scope, find the matching Scope and register a destruction callback
            Scope scope = this.scopes.get(mbd.getScope());
            if (scope == null) {
                throw new IllegalStateException("No Scope registered for scope name '" + mbd.getScope() + "'");
            }
            // Register a DisposableBean adapter that does all the destruction work
            scope.registerDestructionCallback(beanName, new DisposableBeanAdapter(
                bean, beanName, mbd, getBeanPostProcessorCache().destructionAware, acc));
        }
    }
}
```

In the `org.springframework.beans.factory.support.AbstractBeanFactory#requiresDestruction` method, if the bean type is not `NullBean` and it has a destroy method or a related `DestructionAwareBeanPostProcessor`, it returns `true`. This means the bean needs destruction logic. Otherwise it returns `false`.

```java
protected boolean requiresDestruction(Object bean, RootBeanDefinition mbd) {
   return (bean.getClass() != NullBean.class && (DisposableBeanAdapter.hasDestroyMethod(bean, mbd) ||
         (hasDestructionAwareBeanPostProcessors() && DisposableBeanAdapter.hasApplicableProcessors(
               bean, getBeanPostProcessorCache().destructionAware))));
}
```

The `org.springframework.beans.factory.support.DisposableBeanAdapter#DisposableBeanAdapter` constructor builds the adapter that handles bean destruction. From the bean definition and type information, it decides whether a destroy method can be called and how to run it. During initialization, it checks whether the bean implements the `DisposableBean` interface, whether it has a custom destroy method, and which BeanPostProcessors apply. If a destroy method can be called, it sets things up so the destruction logic runs when the container closes.

```java
public DisposableBeanAdapter(Object bean, String beanName, RootBeanDefinition beanDefinition,
        List<DestructionAwareBeanPostProcessor> postProcessors, @Nullable AccessControlContext acc) {

    // Make sure the bean instance to destroy is not null
    Assert.notNull(bean, "Disposable bean must not be null");

    // Initialize the instance variables
    this.bean = bean;
    this.beanName = beanName;

    // Decide whether the destroy method of the DisposableBean interface must be called
    this.invokeDisposableBean = (this.bean instanceof DisposableBean && !beanDefinition.isExternallyManagedDestroyMethod("destroy"));

    // Whether non-public methods may be accessed
    this.nonPublicAccessAllowed = beanDefinition.isNonPublicAccessAllowed();
    this.acc = acc;

    // Infer the name of the destroy method
    String destroyMethodName = inferDestroyMethodIfNecessary(bean, beanDefinition);

    // If there is a destroy method and it is not an externally managed destroy method, handle it
    if (destroyMethodName != null && !(this.invokeDisposableBean && "destroy".equals(destroyMethodName)) &&
        !beanDefinition.isExternallyManagedDestroyMethod(destroyMethodName)) {
        
        // Record the name of the destroy method
        this.destroyMethodName = destroyMethodName;

        // Find the destroy method
        Method destroyMethod = determineDestroyMethod(destroyMethodName);

        // If the destroy method is not found and the bean definition requires it, throw an exception
        if (destroyMethod == null) {
            // ... [code omitted for brevity]
        }
        // Otherwise, check that the destroy method is valid
        else {
            if (destroyMethod.getParameterCount() > 0) {
                Class<?>[] paramTypes = destroyMethod.getParameterTypes();
                // ... [code omitted for brevity]
            }
            // Get the interface method of the destroy method (if any)
            destroyMethod = ClassUtils.getInterfaceMethodIfPossible(destroyMethod);
        }
        // Record the destroy method
        this.destroyMethod = destroyMethod;
    }

    // Filter the BeanPostProcessors related to destruction
    this.beanPostProcessors = filterPostProcessors(postProcessors, bean);
}
```

The `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#registerDisposableBean` method,

```java
public void registerDisposableBean(String beanName, DisposableBean bean) {
    // Use a synchronized block to keep it thread safe
    synchronized (this.disposableBeans) {
        // Put the bean to destroy into the disposableBeans collection
        this.disposableBeans.put(beanName, bean);
    }
}
```

#### Bean Destruction

The `org.springframework.context.support.AbstractApplicationContext#close` method first enters a synchronized block on the `startupShutdownMonitor` object. This makes sure only one thread at a time can run the code in the block. It prevents resource races and inconsistent data caused by multiple threads. Then it calls the `doClose` method.

```java
@Override
public void close() {
    synchronized (this.startupShutdownMonitor) {
        doClose();
        // ... [code omitted for brevity]
    }
}
```

The `org.springframework.context.support.AbstractApplicationContext#doClose` method calls the `destroyBeans` method.

```java
protected void doClose() {
    // ... [code omitted for brevity]
    // Destroy all cached singletons in the context's BeanFactory.
    destroyBeans();
    // ... [code omitted for brevity]
}
```

The `org.springframework.context.support.AbstractApplicationContext#destroyBeans` method first calls `getBeanFactory()` to get the Spring `BeanFactory`. Then it calls `destroySingletons` on that `BeanFactory`. This method destroys all singleton beans cached in the `BeanFactory`.

```java
protected void destroyBeans() {
    getBeanFactory().destroySingletons();
}
```

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#destroySingletons` method first calls the `destroySingletons` method of the parent class. This makes sure the destruction logic inherited from the parent class runs.

```java
@Override
public void destroySingletons() {
    super.destroySingletons();
    // ... [code omitted for brevity]
}
```

The `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingletons` method first takes all bean names from the key set of the `disposableBeans` field and turns them into a string array. `disposableBeans` may hold beans that implement the `DisposableBean` interface. These beans need special handling when the container is destroyed. Then it loops in reverse order, from the last one, and destroys all beans in the `disposableBeans` list. This keeps dependencies correct. A bean that was created first should be destroyed last.

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

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#destroySingleton` method first calls the `destroySingleton` method of the parent class. This makes sure the destruction logic inherited from the parent class runs.

```java
@Override
public void destroySingleton(String beanName) {
    super.destroySingleton(beanName);
    // ... [code omitted for brevity]
}
```

The `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingleton` method first synchronizes on the `disposableBeans` object, so that access and changes are safe in a multi-threaded environment. It removes the bean with the given name from the `disposableBeans` collection and casts it to `DisposableBean`. Finally, it calls the `destroyBean` method to do the actual destruction.

```java
public void destroySingleton(String beanName) {
    // Remove the singleton bean with the given name from the registered singletons
    removeSingleton(beanName);

    // Get the matching DisposableBean instance
    DisposableBean disposableBean;
    synchronized (this.disposableBeans) {
        disposableBean = (DisposableBean) this.disposableBeans.remove(beanName);
    }

    // Run the destruction logic
    destroyBean(beanName, disposableBean);
}
```

The `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroyBean` method destroys the bean with the given name. It triggers the destruction of the beans that depend on it, runs the `destroy` method of the bean, and destroys the other beans contained in it. At the end, it cleans up the dependency information of the destroyed bean.

```java
protected void destroyBean(String beanName, @Nullable DisposableBean bean) {
    // Trigger the destruction of other beans that depend on this bean...
    Set<String> dependencies;
    
    // Fully synchronized, so the Set we get is thread safe
    synchronized (this.dependentBeanMap) {
        dependencies = this.dependentBeanMap.remove(beanName);
    }
    
    if (dependencies != null) {
        // ... [code omitted for brevity]
        for (String dependentBeanName : dependencies) {
            destroySingleton(dependentBeanName);
        }
    }

    // Actually run the destruction logic of the bean...
    if (bean != null) {
        try {
            bean.destroy();
        }
        catch (Throwable ex) {
            // ... [code omitted for brevity]
        }
    }

    // Trigger the destruction of other beans contained in this bean...
    Set<String> containedBeans;
    synchronized (this.containedBeanMap) {
        // Fully synchronized, so the Set we get is disconnected
        containedBeans = this.containedBeanMap.remove(beanName);
    }
    if (containedBeans != null) {
        for (String containedBeanName : containedBeans) {
            destroySingleton(containedBeanName);
        }
    }

    // Remove the destroyed bean from the dependency information of other beans...
    synchronized (this.dependentBeanMap) {
        for (Iterator<Map.Entry<String, Set<String>>> it = this.dependentBeanMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Set<String>> entry = it.next();
            Set<String> dependenciesToClean = entry.getValue();
            dependenciesToClean.remove(beanName);
            if (dependenciesToClean.isEmpty()) {
                it.remove();
            }
        }
    }

    // Remove the prepared dependency information of the destroyed bean
    this.dependenciesForBeanMap.remove(beanName);
}
```

The `org.springframework.beans.factory.support.DisposableBeanAdapter#destroy` method implements the destroy method of the `DisposableBean` interface. It runs all the destruction steps in the bean destruction phase. This includes pre-destruction processing, calling the `destroy` method of the `DisposableBean` interface, and running the custom destroy method.

```java
@Override
public void destroy() {
    // Run the pre-destruction processing of DestructionAwareBeanPostProcessor
    if (!CollectionUtils.isEmpty(this.beanPostProcessors)) {
        for (DestructionAwareBeanPostProcessor processor : this.beanPostProcessors) {
            processor.postProcessBeforeDestruction(this.bean, this.beanName);
        }
    }

    // If DisposableBean is implemented, call its destroy method
    if (this.invokeDisposableBean) {
        // ... [code omitted for brevity]
        try {
            // ... [code omitted for brevity]
            ((DisposableBean) this.bean).destroy();
        }
        catch (Throwable ex) {
            // ... [code omitted for brevity]
        }
    }

    // If there is a custom destroy method, run it
    if (this.destroyMethod != null) {
        invokeCustomDestroyMethod(this.destroyMethod);
    }
    // If a custom destroy method name is given, find it and run it
    else if (this.destroyMethodName != null) {
        Method methodToInvoke = determineDestroyMethod(this.destroyMethodName);
        if (methodToInvoke != null) {
            // Get the interface method (if any) and run the custom destroy method
            invokeCustomDestroyMethod(ClassUtils.getInterfaceMethodIfPossible(methodToInvoke));
        }
    }
}
```

### 8. Notes

1. **Avoid throwing exceptions in destroy methods**

   + Try not to throw exceptions in a destroy method. An exception can affect the destruction of other beans. If an exception happens, log it properly.

2. **Watch the bean dependencies**

   + Make sure beans are destroyed in an order that respects their dependencies. Beans with fewer dependencies are destroyed first, and beans with more dependencies later. The Spring container tries to destroy beans in dependency order.

3. **Watch the bean lifecycle and scope**

   + For singleton beans, destruction is triggered when the container closes. For prototype beans, the Spring container does not destroy them. You must manage them by hand.


### 9. Summary

#### Best Practices Summary

1. **Create the application context**

   + Use `AnnotationConfigApplicationContext` to create an annotation-based application context.

2. **Register the bean**

   + Use `context.register(MyBean.class)` to register the `MyBean` class. This tells the Spring container to manage the beans defined by this configuration class.

3. **Refresh the application context**

   + Use `context.refresh()` to refresh the application context. This initializes and starts the Spring container.

4. **Close the container**

   + Use `context.close()` to close the application context. While closing, the Spring container triggers the destruction phase of the beans.

5. **Bean destruction**

   + Because `MyBean` implements the `DisposableBean` interface, the container calls the `destroy` method of `MyBean` when it closes. In the `destroy` method, we simply print a message to show that `MyBean` was destroyed.
   + At the end, the console prints the message `MyBean was destroyed`. This proves that the bean destruction process has run.

#### Source Code Analysis Summary

1. **Register the bean destruction adapter**

   + In the `finishBeanFactoryInitialization` method of `AbstractApplicationContext`, the `preInstantiateSingletons` method of `DefaultListableBeanFactory` is called. It instantiates all non-lazy singleton beans in advance. During instantiation, `registerDisposableBeanIfNecessary` is called for each bean to register its destruction adapter. This makes sure the destruction logic runs when the container closes.

2. **Bean destruction**

   + When the container closes, the `close` method of `AbstractApplicationContext` triggers the destruction process. In `destroyBeans`, the `destroySingletons` method of `DefaultListableBeanFactory` is called. It destroys all singleton beans cached in the `BeanFactory`.

   + The `destroySingletons` method goes through the `disposableBeans` collection, gets the names of all beans to destroy, and destroys them in reverse order. During destruction, it first calls `destroyBean` to do the actual work.

   + The `destroyBean` method triggers the destruction of other beans that depend on this bean, runs the `destroy` method of the bean, destroys the other beans contained in it, removes the destroyed bean from the dependency information of other beans, and cleans up its prepared dependency information.

   + Finally, the `DisposableBeanAdapter` class handles the destruction logic of the bean. It implements the `destroy` method of the `DisposableBean` interface. It runs pre-destruction processing, calls the `destroy` method of the `DisposableBean` interface, and runs the custom destroy method.

