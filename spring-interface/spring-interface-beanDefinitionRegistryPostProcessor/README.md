## BeanDefinitionRegistryPostProcessor

- [BeanDefinitionRegistryPostProcessor](#beandefinitionregistrypostprocessor)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133844850) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading) 🔗 **Source link** - [BeanDefinitionRegistryPostProcessor source](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-beanDefinitionRegistryPostProcessor)

### 2. Interface Description

`BeanDefinitionRegistryPostProcessor` is an interface in the Spring Framework. It lets you change the bean definitions of the application context during the standard initialization of the Spring container. It extends the `BeanFactoryPostProcessor` interface. Unlike `BeanFactoryPostProcessor`, it also gives access to the `BeanDefinitionRegistry`. This means you can register new beans or change existing bean definitions at runtime.

### 3. Interface Source

`BeanDefinitionRegistryPostProcessor` has been part of the Spring Framework since version 3.0.1. Its most important method is `postProcessBeanDefinitionRegistry`. It lets you register new beans or change existing bean definitions at runtime.

```java
/**
 * Extension to the standard {@link BeanFactoryPostProcessor} SPI,
 * allowing for the registration of further bean definitions before
 * regular BeanFactoryPostProcessor detection kicks in.
 * In particular, BeanDefinitionRegistryPostProcessor may register
 * further bean definitions which in turn define BeanFactoryPostProcessor instances.
 *
 * Author: Juergen Hoeller
 * Since: 3.0.1
 * See: org.springframework.context.annotation.ConfigurationClassPostProcessor
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

    /**
     * Modify the application context's internal bean definition registry after its
     * standard initialization. All regular bean definitions will have been loaded,
     * but no beans will have been instantiated yet. This allows for adding further
     * bean definitions before the next post-processing phase kicks in.
     * 
     * @param registry the bean definition registry used by the application context
     * @throws org.springframework.beans.BeansException in case of errors
     */
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

}
```

### 4. Main Features

1. **Register new bean definitions**
   + This interface lets you register new bean definitions dynamically. This happens after the Spring container finishes its standard initialization (all bean definitions are loaded) and before any bean is instantiated.

2. **Modify existing bean definitions**
   + Besides adding new bean definitions, `BeanDefinitionRegistryPostProcessor` can also change bean definitions that are already registered. For example, it can change property values, constructor arguments, or other settings of a bean.

3. **Control the execution order of `BeanFactoryPostProcessor`**
   + `BeanDefinitionRegistryPostProcessor` is a sub-interface of `BeanFactoryPostProcessor`, so its implementations can also influence the order in which `BeanFactoryPostProcessor` beans run. When the Spring container starts, all `BeanDefinitionRegistryPostProcessor` beans are instantiated and called first. Other `BeanFactoryPostProcessor` beans come after them.

4. **Conditional bean registration**
   + You can use `BeanDefinitionRegistryPostProcessor` to decide at runtime whether to register a bean. For example, you can check whether a certain class is on the classpath.

5. **Extension point for advanced configuration**
   + For complex applications or frameworks, this interface is an extension point for more advanced configuration during initialization. For example, you can load external configuration or run special validation logic.

### 5. Best Practices

First, look at the entry class. The context is `AnnotationConfigApplicationContext`, which configures the Spring container with Java annotations. We pass the `MyConfiguration` component class to its constructor. Then we get `MySimpleBean` from the `AnnotationConfigApplicationContext` and call its `show` method.

```java
public class BeanDefinitionRegistryPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MySimpleBean mySimpleBean1 = context.getBean(MySimpleBean.class);
        mySimpleBean1.show();
    }
}
```

We use the `@Bean` annotation here. For the Spring container to run `MyBeanDefinitionRegistryPostProcessor`, we must register it as a bean. This post-processor adds a new `BeanDefinition`.

```java
@Configuration
public class MyConfiguration {

    @Bean
    public static MyBeanDefinitionRegistryPostProcessor myBeanDefinitionRegistryPostProcessor(){
        return new MyBeanDefinitionRegistryPostProcessor();
    }
}
```

In our own `postProcessBeanDefinitionRegistry` method, we create a new `RootBeanDefinition` object for the `MySimpleBean` class. Then we call the `registerBeanDefinition` method of `registry` to register this new bean definition under the name `"mySimpleBean"`.

```java
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("Start adding bean definition");
        // Create a new BeanDefinition
        BeanDefinition beanDefinition = new RootBeanDefinition(MySimpleBean.class);
        // Register the new bean definition with the registry
        registry.registerBeanDefinition("mySimpleBean", beanDefinition);
        System.out.println("Finished adding bean definition");
    }
}
```

The bean that is registered dynamically:

```java
public class MySimpleBean {

    public void show() {
        System.out.println("MySimpleBean instance: " + this);
    }
}
```

The output shows that the `MySimpleBean` instance was created and its instance information was printed. This proves that `BeanDefinitionRegistryPostProcessor` registered the bean dynamically at runtime.

```java
Start adding bean definition
Finished adding bean definition
MySimpleBean instance: com.xcs.spring.config.MySimpleBean@7e5afaa6
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: BeanFactoryPostProcessor sequence diagram
    participant BeanDefinitionRegistryPostProcessorApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant PostProcessorRegistrationDelegate
    participant MyBeanDefinitionRegistryPostProcessor
    participant BeanDefinitionRegistry
    
    BeanDefinitionRegistryPostProcessorApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(...)<br>Start the context
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()
    AbstractApplicationContext->>AbstractApplicationContext:invokeBeanFactoryPostProcessors(...)<br>Start the whole BeanFactoryPostProcessor invocation flow
    AbstractApplicationContext->>PostProcessorRegistrationDelegate:invokeBeanFactoryPostProcessors(...)<br>Invoke BeanDefinitionRegistryPostProcessor in the correct order
    PostProcessorRegistrationDelegate->>PostProcessorRegistrationDelegate:invokeBeanDefinitionRegistryPostProcessors(...)<br>Finally call back the BeanDefinitionRegistryPostProcessor interface
    PostProcessorRegistrationDelegate->>MyBeanDefinitionRegistryPostProcessor:postProcessBeanDefinitionRegistry(...)<br>Run the custom logic
    MyBeanDefinitionRegistryPostProcessor-->>BeanDefinitionRegistry:Add a new bean definition
    BeanDefinitionRegistry-->>MyBeanDefinitionRegistryPostProcessor:Adding is done
    PostProcessorRegistrationDelegate-->>AbstractApplicationContext: Bean factory post-processor invocation is done
    AnnotationConfigApplicationContext->>BeanDefinitionRegistryPostProcessorApplication:Initialization is done
~~~

### 7. Source Code Analysis

First, look at the entry class. The context is `AnnotationConfigApplicationContext`, which configures the Spring container with Java annotations. We pass the `MyConfiguration` component class to its constructor. Then we get `MySimpleBean` from the `AnnotationConfigApplicationContext` and call its `show` method.

```java
public class BeanDefinitionRegistryPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MySimpleBean mySimpleBean1 = context.getBean(MySimpleBean.class);
        mySimpleBean1.show();
    }
}
```

The constructor `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` runs three steps. We focus on the `refresh()` method.

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
}
```

In the method `org.springframework.context.support.AbstractApplicationContext#refresh`, we focus on `invokeBeanFactoryPostProcessors(beanFactory)`. The other methods are not the focus of this article, so we skip them for now.

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
     // ... [code omitted for brevity]
     // Invoke factory processors registered as beans in the context
     invokeBeanFactoryPostProcessors(beanFactory);
     // ... [code omitted for brevity]
}
```

The method `org.springframework.context.support.AbstractApplicationContext#invokeBeanFactoryPostProcessors` hands the work to `PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors()`.

```java
protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
    // ... [code omitted for brevity]
}
```

The method `org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` calls back the implementations of `BeanDefinitionRegistryPostProcessor` and `BeanFactoryPostProcessor`. Why is this method so long? Most of the code handles the execution order of the processors. First it calls the ones that implement `PriorityOrdered.class`. Then it calls the ones that implement `Ordered.class`. Last, it calls the ones that have no priority.

```java
public static void invokeBeanFactoryPostProcessors(
        ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

    // Invoke BeanDefinitionRegistryPostProcessors first, if any
    Set<String> processedBeans = new HashSet<>();

    // Check whether beanFactory is a BeanDefinitionRegistry
    if (beanFactory instanceof BeanDefinitionRegistry) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
        List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

        // Loop over all post-processors and sort them by type
        for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
            if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                BeanDefinitionRegistryPostProcessor registryProcessor =
                    (BeanDefinitionRegistryPostProcessor) postProcessor;
                registryProcessor.postProcessBeanDefinitionRegistry(registry);
                registryProcessors.add(registryProcessor);
            }
            else {
                regularPostProcessors.add(postProcessor);
            }
        }

        // Do not initialize FactoryBeans here, so that the bean factory post-processors can still be applied to them
        List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

        // First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered
        String[] postProcessorNames =
            beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
        for (String ppName : postProcessorNames) {
            if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                processedBeans.add(ppName);
            }
        }
        sortPostProcessors(currentRegistryProcessors, beanFactory);
        registryProcessors.addAll(currentRegistryProcessors);
        invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
        currentRegistryProcessors.clear();

        // Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered
        postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
        for (String ppName : postProcessorNames) {
            if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
                currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                processedBeans.add(ppName);
            }
        }
        sortPostProcessors(currentRegistryProcessors, beanFactory);
        registryProcessors.addAll(currentRegistryProcessors);
        invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
        currentRegistryProcessors.clear();

        // Finally, invoke all other BeanDefinitionRegistryPostProcessors until no more appear
        boolean reiterate = true;
        while (reiterate) {
            reiterate = false;
            postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
            for (String ppName : postProcessorNames) {
                if (!processedBeans.contains(ppName)) {
                    currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName);
                    reiterate = true;
                }
            }
            sortPostProcessors(currentRegistryProcessors, beanFactory);
            registryProcessors.addAll(currentRegistryProcessors);
            invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
            currentRegistryProcessors.clear();
        }

        // Now invoke the postProcessBeanFactory callback of all processors handled so far
        invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
        invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
    }
    else {
        // Invoke factory processors registered with the context instance
        invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
    }
    
    // ... [code omitted for brevity]
}
```

Here is a sequence diagram I drew of the ordered callback process of `BeanDefinitionRegistryPostProcessor`.

~~~mermaid
sequenceDiagram
    Title: BeanDefinitionRegistryPostProcessor callback order sequence diagram
    participant Init as invokeBeanFactoryPostProcessors
    participant BDRPP_PO as BDRPP(PriorityOrdered)
    participant BDRPP_O as BDRPP(Ordered)
    participant BDRPP as Other BDRPP

    Init->>BDRPP_PO: Callback
    BDRPP_PO-->>Init: Done
    Init->>BDRPP_O: Callback
    BDRPP_O-->>Init: Done
    Init->>BDRPP: Callback
    BDRPP-->>Init: Done
    
    Note right of BDRPP: Tip: 
    Note right of BDRPP: BDRPP = BeanDefinitionRegistryPostProcessor
~~~

The method `org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors` loops over the processors and calls `postProcessBeanDefinitionRegistry(registry)` from the `BeanDefinitionRegistryPostProcessor` interface.

```java
private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry, ApplicationStartup applicationStartup) {

    for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
        StartupStep postProcessBeanDefRegistry = applicationStartup.start("spring.context.beandef-registry.post-process")
            .tag("postProcessor", postProcessor::toString);
        postProcessor.postProcessBeanDefinitionRegistry(registry);
        postProcessBeanDefRegistry.end();
    }
}
```

Finally, the call reaches our own logic. In `postProcessBeanDefinitionRegistry` we register a new bean definition. In real projects, you can do more complex work inside `postProcessBeanDefinitionRegistry`. For example, you can change bean properties, wrap beans in a proxy to add features, change their scope, or add new bean definitions.

```java
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("Start adding bean definition");
        // Create a new BeanDefinition
        BeanDefinition beanDefinition = new RootBeanDefinition(MySimpleBean.class);
        // Register the new bean definition with the registry
        registry.registerBeanDefinition("mySimpleBean", beanDefinition);
        System.out.println("Finished adding bean definition");
    }
}
```

### 8. Notes

1. **Call order**
   + `BeanDefinitionRegistryPostProcessor` has a higher priority than `BeanFactoryPostProcessor`. This means the `postProcessBeanDefinitionRegistry` method of a `BeanDefinitionRegistryPostProcessor` is called before the `postProcessBeanFactory` method of any `BeanFactoryPostProcessor`. If you have several `BeanDefinitionRegistryPostProcessor` beans in Spring, their order can be affected by the `Ordered` or `PriorityOrdered` interfaces. Implement these interfaces correctly to get the order you expect.

2. **Early instantiation**
   + A common trap: a non-static `@Bean` method in a `@Configuration` class that returns a `BeanDefinitionRegistryPostProcessor` can cause the configuration class to be instantiated too early. To avoid this, declare such a method as `static`. In our best practice example, we also use `static` for `MyBeanDefinitionRegistryPostProcessor`.

3. **Do not overuse it**
   + `BeanDefinitionRegistryPostProcessor` is powerful. But do not use it when you do not need to change or add bean definitions dynamically, unless you are writing a framework. In most business systems, the annotations `@Component`, `@Service`, `@Repository`, `@Controller`, and `@Configuration` should be enough.

### 9. Summary

#### Best Practices Summary

1. **Application startup**
   + The entry class `BeanDefinitionRegistryPostProcessorApplication` creates the Spring container with `AnnotationConfigApplicationContext` and loads the `MyConfiguration` class as configuration.

2. **Configuration class**
   + In the `MyConfiguration` class, we use the `@Bean` annotation on a static method. The method returns a `MyBeanDefinitionRegistryPostProcessor` object. This makes sure it runs when the Spring container initializes.

3. **Dynamic registration**
   + In our own `MyBeanDefinitionRegistryPostProcessor`, we override the `postProcessBeanDefinitionRegistry` method. In this method, we create a new `RootBeanDefinition` object for the `MySimpleBean` class. Then we register this new bean definition with the `registerBeanDefinition` method of `registry` under the name `"mySimpleBean"`.

4. **Target bean**
   + `MySimpleBean` is a simple bean class. Its `show` method prints its own instance information.

5. **Verify the dynamic registration**
   + When you run `BeanDefinitionRegistryPostProcessorApplication`, the console first prints "Start adding bean definition" and "Finished adding bean definition". This shows that `postProcessBeanDefinitionRegistry` ran correctly. Then the `MySimpleBean` instance is created and its instance information is printed. This proves that `BeanDefinitionRegistryPostProcessor` registered the bean dynamically at runtime.

#### Source Code Analysis Summary

1. **Application startup**
   + `AnnotationConfigApplicationContext` creates the Spring container and loads the `MyConfiguration` class. Then the code gets `MySimpleBean` from this context and calls its `show` method.

2. **Spring container refresh**
   + The constructor of `AnnotationConfigApplicationContext` calls `refresh()`. This starts the initialization flow of the Spring container.

3. **Run the bean factory post-processors**
   + In `refresh()`, the method `invokeBeanFactoryPostProcessors(beanFactory)` is called. It runs all registered implementations of `BeanFactoryPostProcessor` and `BeanDefinitionRegistryPostProcessor`.

4. **Callbacks by priority**
   + Spring sorts the `BeanDefinitionRegistryPostProcessor` beans by priority (such as `PriorityOrdered` and `Ordered`) and calls them back in that order. This makes the call order predictable.

5. **Dynamic bean definition registration**:
   + In our own `MyBeanDefinitionRegistryPostProcessor`, we override the `postProcessBeanDefinitionRegistry` method. Inside it, we create the definition of `MySimpleBean` dynamically and register it in the container.

6. **Execution result**
   + `MySimpleBean` is registered in the Spring container dynamically. It can then be retrieved and used when the application starts.
