## DisposableBean

- [DisposableBean](#disposablebean)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133845687) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [DisposableBean source](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-disposableBean)

### 2. Interface Description

The `DisposableBean` interface lets a bean clean up resources. For example, you can use it to release file handles, network connections, or database connections.

### 3. Interface Source

`DisposableBean` is a core Spring interface, introduced on 12.08.2003. A bean that needs to release resources when it is destroyed can implement it. A bean can also implement Java's `AutoCloseable` interface for the same purpose.

```java
/**
 * Interface to be implemented by beans that want to release resources on destruction.
 * A BeanFactory invokes the destroy method on individual destruction of a scoped bean.
 * An org.springframework.context.ApplicationContext is supposed to dispose of all of
 * its singletons on shutdown, driven by the application lifecycle.
 *
 * A Spring-managed bean may also implement Java's AutoCloseable interface for the same purpose.
 * An alternative to implementing an interface is to specify a custom destroy method,
 * for example in an XML bean definition. For a list of all bean lifecycle methods, see the BeanFactory BeanFactory.
 *
 * @author Juergen Hoeller
 * @since 12.08.2003
 * @see InitializingBean
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getDestroyMethodName()
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#destroySingletons()
 * @see org.springframework.context.ConfigurableApplicationContext#close()
 */
public interface DisposableBean {

    /**
     * Invoked by the containing BeanFactory on destruction of a bean.
     * @throws Exception in case of shutdown errors. Exceptions will get logged
     *                   but not rethrown to allow other beans to release their resources as well.
     */
    void destroy() throws Exception;

}
```

### 4. Main Features

1. **Destruction callback**
   + When the Spring container destroys a bean that implements `DisposableBean`, it calls the bean's `destroy()` method automatically. This gives the bean a chance to do any needed cleanup before it is destroyed.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a context that configures the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor. At the end, we call `context.close()` to close the container.

```java
public class DisposableBeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        context.close();
    }
}
```

The `@Bean` annotation defines a bean here. This makes sure the Spring container handles `MyDisposableBean`.

```java
@Configuration
public class MyConfiguration {

    @Bean
    public static MyDisposableBean myDisposableBean(){
        return new MyDisposableBean();
    }
}
```

 The `MyDisposableBean` class opens a simulated database connection when it is instantiated. It closes the connection when it is destroyed.

```java
public class MyDisposableBean implements DisposableBean {

    // Simulated database connection
    private String databaseConnection;

    public MyDisposableBean() {
        // Simulate opening a database connection in the constructor
        this.databaseConnection = "Database connection established";
        System.out.println(databaseConnection);
    }

    @Override
    public void destroy() throws Exception {
        // Simulate closing the database connection in the destroy method
        databaseConnection = null;
        System.out.println("Database connection closed");
    }
}
```

When we run it, `destroy()` is called when the `MyDisposableBean` bean is destroyed. It closes the simulated database connection.

```java
Database connection established
Database connection closed
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: DisposableBean Sequence Diagram
    participant DisposableBeanApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant DefaultSingletonBeanRegistry
    participant DisposableBeanAdapter
    participant MyDisposableBean
    
    DisposableBeanApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses	)<br>The application starts initializing the context
    AnnotationConfigApplicationContext-->>DisposableBeanApplication:Initialization done
    DisposableBeanApplication->>AbstractApplicationContext:close()<br>Request to close the context
    AbstractApplicationContext->>AbstractApplicationContext:doClose()<br>Run the close logic
    AbstractApplicationContext->>AbstractApplicationContext:destroyBeans()<br>Start destroying beans
    AbstractApplicationContext->>DefaultListableBeanFactory:destroySingletons()<br>Destroy singleton beans
    DefaultListableBeanFactory->>DefaultSingletonBeanRegistry:super.destroySingletons()<br>Call the parent destroy method
    DefaultSingletonBeanRegistry-->>DefaultListableBeanFactory:destroySingleton(beanName)<br>Destroy a single bean
    DefaultListableBeanFactory->>DefaultSingletonBeanRegistry:super.destroySingleton(beanName)<br>Call the parent method to destroy the bean
    DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:destroyBean(beanName,bean)<br>Run the bean destruction
    DefaultSingletonBeanRegistry->>DisposableBeanAdapter:destroy()<br>The adapter runs the destruction
    DisposableBeanAdapter->>MyDisposableBean:destroy()<br>Run the custom destruction logic
    AbstractApplicationContext-->>DisposableBeanApplication:Close request finished
~~~

### 7. Source Code Analysis

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a context that configures the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor. Then we call `context.close()` to close the application context.

```java
public class DisposableBeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        context.close();
    }
}
```

In `org.springframework.context.support.AbstractApplicationContext#close`, the code first enters a synchronized block on the `startupShutdownMonitor` object. This makes sure only one thread runs the block at a time, so threads do not compete for resources or leave data inconsistent. Then it calls `doClose`. Finally, it removes the JVM shutdown hook if one was registered.

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

In `org.springframework.context.support.AbstractApplicationContext#doClose`, the code calls `destroyBeans`.

```java
protected void doClose() {
    // ... [code omitted for brevity]
    // Destroy all cached singletons in the context's BeanFactory.
    destroyBeans();
    // ... [code omitted for brevity]
}
```

In `org.springframework.context.support.AbstractApplicationContext#destroyBeans`, the code first calls `getBeanFactory()` to get the Spring `BeanFactory`. Then it calls `destroySingletons` on it. This destroys all singleton beans cached in the `BeanFactory`.

```java
protected void destroyBeans() {
    getBeanFactory().destroySingletons();
}
```

In `org.springframework.beans.factory.support.DefaultListableBeanFactory#destroySingletons`, the code first calls the parent `destroySingletons` method. This makes sure the destruction logic of the parent class runs.

```java
@Override
public void destroySingletons() {
    super.destroySingletons();
    updateManualSingletonNames(Set::clear, set -> !set.isEmpty());
    clearByTypeCache();
}
```

In `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingletons`, the code first takes all bean names from the key set of the `disposableBeans` field and converts them to a string array. `disposableBeans` holds the beans that implement `DisposableBean`. These beans need special handling when the container is destroyed. Then the code loops backwards, from the last name to the first, and destroys every bean in the `disposableBeans` list. This keeps dependencies correct: a bean created first should be destroyed last.

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

In `org.springframework.beans.factory.support.DefaultListableBeanFactory#destroySingleton`, the code first calls the parent `destroySingleton` method. This makes sure the destruction logic of the parent class runs.

```java
@Override
public void destroySingleton(String beanName) {
    super.destroySingleton(beanName);
    removeManualSingletonName(beanName);
    clearByTypeCache();
}
```

In `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingleton`, the code first uses `synchronized` on the `disposableBeans` object, so it can be read and changed safely with many threads. It removes the bean with the given name from `disposableBeans` and casts it to `DisposableBean`. Finally, it calls `destroyBean` to do the actual destruction.

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

In `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroyBean`, the code calls the `destroy` method of `bean` directly. `bean` is a `DisposableBean`, so it always has a `destroy` method. This method holds the custom destruction logic of the bean.

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

In `org.springframework.beans.factory.support.DisposableBeanAdapter#destroy`, the code first checks the `invokeDisposableBean` field. If it is true, the code calls the `destroy` method of the bean that implements `DisposableBean`.

```java
@Override
public void destroy() {
    // ... [code omitted for brevity]
    
    if (this.invokeDisposableBean) {
        if (logger.isTraceEnabled()) {
            logger.trace("Invoking destroy() on bean with name '" + this.beanName + "'");
        }
        try {
            if (System.getSecurityManager() != null) {
                // ... [code omitted for brevity]
            }
            else {
                ((DisposableBean) this.bean).destroy();
            }
        }
        catch (Throwable ex) {
            // ... [code omitted for brevity]
        }
    }
}
```

Finally, our own logic runs. The `MyDisposableBean` class opens a simulated database connection when it is instantiated. It closes the connection when it is destroyed.

```java
public class MyDisposableBean implements DisposableBean {

    // Simulated database connection
    private String databaseConnection;

    public MyDisposableBean() {
        // Simulate opening a database connection in the constructor
        this.databaseConnection = "Database connection established";
        System.out.println(databaseConnection);
    }

    @Override
    public void destroy() throws Exception {
        // Simulate closing the database connection in the destroy method
        databaseConnection = null;
        System.out.println("Database connection closed");
    }
}
```

### 8. Notes

1. **Do not rely on it too much**
   + `DisposableBean` is a standard way to define destruction logic. But the `@PreDestroy` annotation or the `destroy-method` attribute in the bean definition is usually better. They are simpler, more declarative, and avoid needless coupling in the code.

2. **Prototype beans**
   + Spring does not manage the full lifecycle of prototype-scoped beans. So the `destroy()` method of `DisposableBean` is not called automatically for prototype beans. Release their resources in another way.

3. **Use it together with an initialization method**
   + If you use `DisposableBean` for destruction logic, you may also use `InitializingBean` for initialization logic. Again, `@PostConstruct` or the `init-method` attribute is recommended instead.

4. **Avoid duplicate code**
   + If several beans have similar destruction logic, move it into a shared method or helper class. This reduces duplicate code and is easier to maintain.

5. **Avoid long-running operations**
   + `destroy()` should run fast and release resources. Avoid slow work in it, because it can delay the shutdown of the application.

### 9. Summary

#### Best Practices Summary

1. **Application startup**
   + We created the entry class `DisposableBeanApplication`. It creates an annotation-based context, `AnnotationConfigApplicationContext`. The context configures the Spring container from the `MyConfiguration` class.

2. **Java configuration**
   + In `MyConfiguration`, we used the `@Bean` annotation to define a bean named `myDisposableBean`. This makes sure the Spring container manages an instance of `MyDisposableBean`.

3. **Resource management**
   + `MyDisposableBean` simulates opening and closing a database connection. When the class is instantiated, it simulates opening a connection. When the bean is destroyed (because the context is closed), its `destroy()` method is called and simulates closing the connection.

4. **Application shutdown**
   + At the end of `main`, we call `context.close()` to close the application context. The container then destroys all singleton beans. During this, the `destroy()` method of the `MyDisposableBean` bean is called, and the database connection is closed in the simulation.

5. **Output**
   + When we run the program, we first see `"Database connection established"`. It comes from the constructor of `MyDisposableBean` and shows that the simulated connection is open. When the context closes, we see `"Database connection closed"`. It comes from the `destroy()` method of `MyDisposableBean` and shows that the simulated connection is closed.

#### Source Code Analysis Summary

1. **Startup and shutdown**:

   - We created the `DisposableBeanApplication` entry class. It creates an annotation-based context, `AnnotationConfigApplicationContext`.

   - We pass the `MyConfiguration` class to the constructor to configure the Spring container.

   - We use `context.close()` to close the application context. This triggers cleanup and resource release.


2. **Synchronized shutdown**:

   - In `AbstractApplicationContext#close`, a synchronized block makes sure only one thread can close the application context at a time. This prevents resource competition and inconsistent data.

   - After the context is closed, any JVM shutdown hook that was registered is removed, because the context is already closed explicitly.


3. **The actual shutdown**:
   - In `AbstractApplicationContext#doClose`, the `destroyBeans` method is called to destroy the beans in the container.


4. **Destroying beans**:

   - In `AbstractApplicationContext#destroyBeans`, the `BeanFactory` calls `destroySingletons` to destroy all cached singleton beans.

   - In `DefaultListableBeanFactory`, the parent destruction logic is called first.

   - `DefaultSingletonBeanRegistry#destroySingletons` gets the names of all beans to destroy. It destroys them in the reverse order of creation, so dependencies are handled correctly.


5. **Destroying a single bean**:

   - `DefaultSingletonBeanRegistry#destroySingleton` is called for each bean to destroy.

   - If a bean implements `DisposableBean`, its `destroy` method is called.

   - To keep things thread-safe, many operations run in synchronized blocks, especially when working with `disposableBeans`.


6. **Custom destruction logic**:
   - Finally, the flow reaches our `MyDisposableBean` class. When an instance of it is destroyed, it simulates closing a database connection.