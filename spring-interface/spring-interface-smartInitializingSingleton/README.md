## SmartInitializingSingleton

- [SmartInitializingSingleton](#smartinitializingsingleton)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133845553) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [SmartInitializingSingleton source](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-smartInitializingSingleton)

### 2. Interface Description

The `SmartInitializingSingleton` interface is a callback for bean initialization. It is called after all singleton beans are fully initialized.

### 3. Interface Source

`SmartInitializingSingleton` is a core Spring interface, introduced in Spring 4.1. Its `afterSingletonsInstantiated()` method is called at the end of the singleton pre-instantiation phase. At that point, all regular singleton beans are already created and initialized.

```java
/**
 * Callback interface triggered at the end of the singleton pre-instantiation phase during BeanFactory bootstrap.
 * A singleton bean can implement this interface to run some initialization after the regular singleton instantiation algorithm,
 * which avoids side effects of unexpected early initialization (for example, from a ListableBeanFactory#getBeansOfType call).
 * In this sense, it is an alternative to InitializingBean, which is triggered at the end of a bean's local construction phase.
 *
 * This callback variant is somewhat similar to org.springframework.context.event.ContextRefreshedEvent,
 * but it does not need to implement org.springframework.context.ApplicationListener,
 * and it does not need to filter context references across a context hierarchy and so on. It also means
 * that it depends only on the beans package and is respected by standalone ListableBeanFactory implementations, not just in an org.springframework.context.ApplicationContext environment.
 *
 * Note: If we intend to start or manage asynchronous tasks, it is better to implement org.springframework.context.Lifecycle,
 * which offers a richer model for runtime management and allows phased startup and shutdown.
 *
 * @author Juergen Hoeller
 * @since 4.1
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory#preInstantiateSingletons()
 */
public interface SmartInitializingSingleton {

	/**
	 * Invoked at the end of the singleton pre-instantiation phase,
	 * with a guarantee that all regular singleton beans have been created already.
	 * ListableBeanFactory#getBeansOfType calls within this method will not cause unexpected side effects during bootstrap.
	 * Note: This callback will not be triggered for singleton beans that are lazily initialized on demand after BeanFactory bootstrap,
	 * nor for any other bean scope. Use it with care, only for beans with the intended bootstrap semantics.
	 */
	void afterSingletonsInstantiated();

}
```

### 4. Main Features

1. **Callback after beans are fully initialized**
   + It gives a singleton bean a way to run special initialization after all other regular singleton beans in the Spring container are fully initialized. When all singleton beans are instantiated and initialized, the `afterSingletonsInstantiated()` method of `SmartInitializingSingleton` is called.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a context that configures the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor.

```java
public class SmartInitializingSingletonApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
    }
}
```

The `@Bean` annotation defines a bean here. This makes sure the Spring container handles `MySmartInitializingSingleton`.

```java
@Configuration
@ComponentScan("com.xcs.spring.service")
public class MyConfiguration {

    @Bean
    public static MySmartInitializingSingleton mySmartInitializingSingleton(){
        return new MySmartInitializingSingleton();
    }
}
```

In `MySmartInitializingSingleton`, after all other singleton beans are fully initialized, the `afterSingletonsInstantiated()` method starts the scheduled task defined in `MyService`.

```java
public class MySmartInitializingSingleton implements SmartInitializingSingleton {

    @Autowired
    private MyService myService;

    @Override
    public void afterSingletonsInstantiated() {
        myService.startScheduledTask();
    }
}
```

`MyService` defines a scheduled task. Every 2 seconds, it prints the current date and time and the message "hello world". `MySmartInitializingSingleton` calls `startScheduledTask()` after all singleton beans are fully initialized. This starts the task.

```java
@Service
public class MyService {

    /**
     * Java's Timer is used here to simulate a scheduled task. In a real application, a more advanced scheduling tool may be used.
     */
    public void startScheduledTask() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        System.out.println(getDate() + " hello world ");
                    }
                },
                0,
                2000
        );
    }

    /**
     * Get the current time
     *
     * @return
     */
    public String getDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}
```

When we run it, `MySmartInitializingSingleton` starts the scheduled task in `MyService` after all other singleton beans are initialized. It works as expected and prints the following output every 2 seconds.

```java
2023-09-27 10:41:36 hello world 
2023-09-27 10:41:38 hello world 
2023-09-27 10:41:40 hello world 
2023-09-27 10:41:42 hello world 
2023-09-27 10:41:44 hello world 
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: SmartInitializingSingleton Sequence Diagram
    participant SmartInitializingSingletonApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant MySmartInitializingSingleton
    
    SmartInitializingSingletonApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Create the context
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>Refresh the context
    AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)<br>Initialize the bean factory
    AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()<br>Instantiate singletons
    DefaultListableBeanFactory->>MySmartInitializingSingleton:afterSingletonsInstantiated()<br>All singletons are initialized
~~~

### 7. Source Code Analysis

```java
public class SmartInitializingSingletonApplication {

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
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [code omitted for brevity]
    // Instantiate all remaining (non-lazy-init) singletons.
    beanFactory.preInstantiateSingletons();
}
```

In `org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons`, beans that implement `SmartInitializingSingleton` get their `afterSingletonsInstantiated` method called only after all other singleton beans are fully instantiated. This makes sure the initialization order is correct.

```java
@Override
public void preInstantiateSingletons() throws BeansException {
    // ... [code omitted for brevity]

    // Trigger post-initialization callback for all applicable beans...
    for (String beanName : beanNames) {
        Object singletonInstance = getSingleton(beanName);
        if (singletonInstance instanceof SmartInitializingSingleton) {
            // ... [code omitted for brevity]
            smartSingleton.afterSingletonsInstantiated();
            // ... [code omitted for brevity]
        }
    }
}
```

### 8. Notes

1. **Avoid complex logic**:
   + `SmartInitializingSingleton` is designed for logic that runs after initialization. Do not put overly complex logic in `afterSingletonsInstantiated()`.

2. **Watch the dependencies**
   + When `afterSingletonsInstantiated()` is called, all regular singleton beans are initialized. Still, make sure any bean you use in this method is fully initialized and all its dependencies are satisfied.

3. **Avoid early initialization**
   + Make sure you do not trigger early initialization of other beans by accident, especially in `afterSingletonsInstantiated()`. Early initialization can cause unexpected side effects.

4. **Limited scope**
   +  `SmartInitializingSingleton` works only for regular singleton beans. It is not triggered for beans that are lazily initialized on demand after `BeanFactory` startup, or for beans in other scopes (such as prototype).

5. **Asynchronous tasks**
   +  To start or manage asynchronous tasks, it is better to use the `Lifecycle` interface or other Spring startup listeners, such as `ApplicationListener<ContextRefreshedEvent>`. `Lifecycle` offers a more complete model for runtime management.

6. **Make it idempotent**
   + The application context may be refreshed more than once (this is rare, in my view). If so, make sure `afterSingletonsInstantiated()` is idempotent. This means running it many times has the same effect as running it once.

7. **Difference from `InitializingBean` and `@PostConstruct`**
   +  `SmartInitializingSingleton` is different from `InitializingBean` and `@PostConstruct`. The latter two are bean-level initialization callbacks. `SmartInitializingSingleton` works at the container level and runs only after all beans are initialized.

8. **Do not overuse it**
   + Use `SmartInitializingSingleton` only when you really need to run something after all other beans are initialized. If you do not need this guarantee, use a more standard initialization callback.

### 9. Summary

#### Best Practices Summary

1. **Entry point**
   + In the example entry class `SmartInitializingSingletonApplication`, we use `AnnotationConfigApplicationContext` to load and initialize the Spring container. We give the context a Java configuration class, `MyConfiguration`, which defines the beans Spring should manage.

2. **Configuration**
   + In `MyConfiguration`, we use the `@Bean` annotation to define the `MySmartInitializingSingleton` bean. This makes sure the Spring container manages `MySmartInitializingSingleton` and runs it at the right time.

3. **Implementing the SmartInitializingSingleton interface**
   + `MySmartInitializingSingleton` implements `SmartInitializingSingleton`. After all other singleton beans are fully initialized, `afterSingletonsInstantiated()` is called. In this method, we start the scheduled task defined in `MyService`.

4. **Scheduled task**
   + `MyService` defines a scheduled task that simulates a timer with Java's `Timer`. Every 2 seconds, it prints the current time and the message "hello world". In a real application, a more advanced scheduling tool may be used, such as Spring's `TaskScheduler` or Quartz.

5. **Result**
   + After we start the example, the console shows the formatted current time followed by "hello world" every 2 seconds. This proves that the scheduled task started and is running.

#### Source Code Analysis Summary

1. **Application startup**
   + Everything starts in the `main` method of `SmartInitializingSingletonApplication`. It creates an `AnnotationConfigApplicationContext`, the Spring context for Java annotation configuration.

2. **AnnotationConfigApplicationContext constructor**
   + The constructor of `AnnotationConfigApplicationContext` runs three main steps. The most important one is `refresh()`.

3. **Running refresh**
   + `refresh()` is the core of the Spring context refresh. The key part is `finishBeanFactoryInitialization(beanFactory)`. It instantiates all remaining non-lazy singleton beans.

4. **Finishing BeanFactory initialization**
   + To do this, `finishBeanFactoryInitialization` calls `beanFactory.preInstantiateSingletons()`.

5. **Pre-instantiating singletons**
   + This is the most important step. In `preInstantiateSingletons` of `DefaultListableBeanFactory`, all singleton beans are instantiated. Then the `afterSingletonsInstantiated` callback is triggered for beans that implement `SmartInitializingSingleton`. This makes sure these callbacks run only after all other singleton beans are fully instantiated.