## @Bean

- [@Bean](#bean)
  - [1. Overview](#1-overview)
  - [2. Annotation Description](#2-annotation-description)
  - [3. Annotation Source](#3-annotation-source)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
    - [@Bean Registration Sequence Diagram](#bean-registration-sequence-diagram)
    - [@Bean Init Method Sequence Diagram](#bean-init-method-sequence-diagram)
    - [@Bean Destroy Method Sequence Diagram](#bean-destroy-method-sequence-diagram)
  - [7. Source Code Analysis](#7-source-code-analysis)
    - [@Bean Registration Source Code Analysis](#bean-registration-source-code-analysis)
    - [@Bean Initialization Source Code Analysis](#bean-initialization-source-code-analysis)
    - [@Bean Destruction Source Code Analysis](#bean-destruction-source-code-analysis)
  - [8. Notes](#8-notes)
  - [9. Summary](#9-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/132498762) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [@Bean source](https://github.com/xuchengsheng/spring-reading/tree/master/spring-annotation/spring-annotation-bean)

### 2. Annotation Description

`@Bean` is a core Spring annotation. You put it on a method. The value the method returns is registered as an object (a bean) in the Spring container. Compared with XML configuration, it is a shorter and clearer way to define beans. `@Bean` is usually used together with `@Configuration`, which marks a class as a Spring configuration class. By default, the method name is the bean ID. You can change it with the `name` attribute of `@Bean`. Because the definition is plain Java code, you can use the full power of Java to configure and initialize objects. You can also combine it with other Spring features such as `@Autowired` to do dependency injection easily. In short, `@Bean` makes application configuration cleaner and easier to maintain.

### 3. Annotation Source

The `@Bean` annotation was introduced in Spring 3.0. It marks a method that returns an object. That object should be registered as a bean in the Spring application context.

```java
/**
 * Indicates that a method produces a bean to be managed by the Spring container.
 * @author Rod Johnson
 * @author Costin Leau
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.0
 * @see Configuration
 * @see Scope
 * @see DependsOn
 * @see Lazy
 * @see Primary
 * @see org.springframework.stereotype.Component
 * @see org.springframework.beans.factory.annotation.Autowired
 * @see org.springframework.beans.factory.annotation.Value
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {

    /**
     * Alias for {@link #name}.
     * Use it when no other attributes are needed, for example: {@code @Bean("customBeanName")}.
     * @see #name
     */
    @AliasFor("name")
    String[] value() default {};

    /**
     * The name of this bean. If there are several names, the first is the primary bean name and the rest are aliases.
     * If not specified, the bean name is the name of the annotated method.
     * @see #value
     */
    @AliasFor("value")
    String[] name() default {};

    /**
     * Whether to autowire by name or type using the convention-based approach.
     * Note that this mode is externally driven autowiring through the setter methods of bean properties.
     * @deprecated ...
     */
    @Deprecated
    Autowire autowire() default Autowire.NO;

    /**
     * Whether this bean is a candidate for autowiring into other beans.
     */
    boolean autowireCandidate() default true;

    /**
     * Optional name of a method to call on the bean instance during initialization.
     * Usually, the method can also be called directly in the body of the @Bean method.
     */
    String initMethod() default "";

    /**
     * Optional name of a method to call on the bean instance when the application context is closed.
     * For example, the close() method of a JDBC DataSource.
     */
    String destroyMethod() default AbstractBeanDefinition.INFER_METHOD;

}
```

### 4. Main Features

1. **Creating and registering beans**:
   + `@Bean` marks a method. The object the method returns is managed by the Spring container. When the application context starts, the method is called and its return value is added to the container as a bean.

2. **Custom bean names**
   + By default, the bean name is the name of the method marked with `@Bean`. You can use the `name` attribute of `@Bean` to give the bean one or more names.

3. **Lifecycle management**
   + With the `initMethod` and `destroyMethod` attributes, you can set callback methods for initialization and destruction. They are called when the bean is created or destroyed.

4. **Replacing XML configuration**
   + In early Spring versions, beans were usually defined in XML files. With `@Bean`, you can replace XML completely with Java configuration. The configuration is then in one place and type safe.

5. **Flexible dependency injection**
   + Inside a `@Bean` method, you can call other `@Bean` methods directly to inject dependencies. This keeps the code type safe and keeps code and configuration close together.

6. **Working with other annotations**
   + `@Bean` is often used with other Spring annotations. For example, `@Scope` (sets the bean scope, such as singleton or prototype), `@Lazy` (delays bean initialization), and `@Primary` (marks one bean as the preferred one when several beans have the same type). They give more detailed configuration for the bean.

7. **Controlling autowiring**
   + The `autowireCandidate` attribute controls whether the bean can be chosen when other beans are autowired by type.

### 5. Best Practices

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (a container configured with Java annotations). We pass the `MyConfiguration` class to its constructor. Then we get a bean of type `MyBean` from the context. Finally, we call `context.close()` to close the container.

```java
public class BeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println(context.getBean(MyBean.class));
        context.close();
    }
}
```

In the `MyConfiguration` class, the `@Bean` annotation defines a bean named `myBean`. It is an instance of the `MyBean` class. When the container initializes this bean, it first calls the `init` method of `MyBean` (set by the `initMethod` attribute). When the container is closed and the bean is destroyed, it calls the `destroy` method of `MyBean` (set by the `destroyMethod` attribute).

```java
@Configuration
public class MyConfiguration {

    @Bean(initMethod = "init",destroyMethod = "destroy")
    public MyBean myBean(){
        return new MyBean();
    }
}
```

When the container creates the `MyBean` instance, the `init` method is called automatically, so "MyBean.init" is printed to the console. When the container is about to destroy the bean or close, the `destroy` method is called, so "MyBean.destroy" is printed.

```java
public class MyBean {

    public void init(){
        System.out.println("MyBean.init");
    }

    public void destroy(){
        System.out.println("MyBean.destroy");
    }
}
```

The output shows that `init` is called when the bean is created and `destroy` is called when the bean is destroyed. This shows the order in which the lifecycle methods set by `@Bean` run.

```java
MyBean.init
com.xcs.spring.bean.MyBean@2fb3536e
MyBean.destroy
```

### 6. Sequence Diagram

#### @Bean Registration Sequence Diagram

~~~mermaid
sequenceDiagram
Title: @Bean Registration Sequence Diagram
BeanApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>Create the application context
AnnotationConfigApplicationContext->>AbstractApplicationContext: refresh()<br>Refresh the application context
AbstractApplicationContext->>AbstractApplicationContext: invokeBeanFactoryPostProcessors(beanFactory)<br>Call the BeanFactory post processors
AbstractApplicationContext->>PostProcessorRegistrationDelegate: invokeBeanFactoryPostProcessors(beanFactory,beanFactoryPostProcessors)<br>Run the BeanFactory post processors
PostProcessorRegistrationDelegate->>PostProcessorRegistrationDelegate: invokeBeanDefinitionRegistryPostProcessors(postProcessors,registry,applicationStartup)<br>Run the BeanDefinition registry post processors
PostProcessorRegistrationDelegate->>ConfigurationClassPostProcessor: postProcessBeanDefinitionRegistry(registry)<br>Process the bean definitions of @Configuration classes
ConfigurationClassPostProcessor->>ConfigurationClassPostProcessor: processConfigBeanDefinitions(registry)<br>Process the bean definitions of configuration classes
ConfigurationClassPostProcessor->>ConfigurationClassParser: ConfigurationClassParser(...)<br>Create the configuration class parser
ConfigurationClassParser-->>ConfigurationClassPostProcessor: Return the parser<br>
ConfigurationClassPostProcessor->>ConfigurationClassParser: parser.parse(candidates)<br>Parse the candidate @Configuration classes
ConfigurationClassParser->>ConfigurationClassParser: parse(metadata, String beanName)<br>Parse one @Configuration class
ConfigurationClassParser->>ConfigurationClassParser: processConfigurationClass(configClass,filter)<br>Process the @Configuration class
ConfigurationClassParser->>+ConfigurationClassParser: doProcessConfigurationClass(configClass,sourceClass,filter)<br>Do the actual processing of the @Configuration class
ConfigurationClassParser->>+ConfigurationClassParser: retrieveBeanMethodMetadata(sourceClass)<br>Get the metadata of the @Bean methods
ConfigurationClassParser-->>-ConfigurationClassParser: Return Set<MethodMetadata><br>
ConfigurationClassParser-->>-ConfigurationClassParser: Return SourceClass<br>
ConfigurationClassPostProcessor->>ConfigurationClassBeanDefinitionReader: loadBeanDefinitions(configurationModel)<br>Load the bean definitions
ConfigurationClassBeanDefinitionReader->>ConfigurationClassBeanDefinitionReader: loadBeanDefinitionsForConfigurationClass(configClass,trackedConditionEvaluator)<br>Load the bean definitions for the @Configuration class
ConfigurationClassBeanDefinitionReader->>ConfigurationClassBeanDefinitionReader: loadBeanDefinitionsForBeanMethod(beanMethod)<br>Load the bean definition for the @Bean method
ConfigurationClassBeanDefinitionReader->>DefaultListableBeanFactory: registerBeanDefinition(beanName, beanDefToRegister)<br>Register the bean definition in the BeanFactory
~~~

#### @Bean Init Method Sequence Diagram

~~~mermaid
sequenceDiagram
Title: @Bean Init Method Sequence Diagram
BeanApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>Create the application context
AnnotationConfigApplicationContext->>AbstractApplicationContext: refresh()<br>Refresh the application context
AbstractApplicationContext->>AbstractApplicationContext: finishBeanFactoryInitialization(beanFactory)<br>Finish initializing the BeanFactory
AbstractApplicationContext->>DefaultListableBeanFactory: preInstantiateSingletons()<br>Instantiate singleton beans in advance
DefaultListableBeanFactory->>AbstractBeanFactory: getBean(name)<br>Get the bean
AbstractBeanFactory->>AbstractBeanFactory: doGetBean(name,requiredType,args,typeCheckOnly)<br>Do the actual work of getting the bean
AbstractBeanFactory->>DefaultSingletonBeanRegistry: getSingleton(beanName,singletonFactory)<br>Get the bean from the singleton registry
DefaultSingletonBeanRegistry->>AbstractBeanFactory: singletonFactory.getObject()<br>Get the bean object from the singleton factory
AbstractBeanFactory->>AbstractAutowireCapableBeanFactory: createBean(beanName,mbd,args)<br>Create the bean
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory: doCreateBean(beanName,mbd,args)<br>Run the bean creation process
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory: initializeBean(beanName,bean,mbd)<br>Initialize the bean
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory: invokeInitMethods(beanName,bean,mbd)<br>Call the init methods of the bean
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory: invokeCustomInitMethod(beanName,bean,mbd)<br>Run the custom init method
AbstractAutowireCapableBeanFactory->>Method: invoke(obj,args)<br>Call the bean method by reflection
Method->>MyBean: init()<br>Run the init method of the bean
AbstractBeanFactory-->>DefaultListableBeanFactory: Return the created bean

~~~

#### @Bean Destroy Method Sequence Diagram

~~~mermaid
sequenceDiagram
Title: @Bean Destroy Method Sequence Diagram
BeanApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>Create the application context
AnnotationConfigApplicationContext-->>BeanApplication: The application context is initialized and returned
BeanApplication->>AbstractApplicationContext: close()<br>Request to close the application context
AbstractApplicationContext->>AbstractApplicationContext: doClose()<br>Run the close operation
AbstractApplicationContext->>AbstractApplicationContext: destroyBeans()<br>Destroy all beans in the container
AbstractApplicationContext->>DefaultListableBeanFactory: destroySingletons()<br>Destroy all singleton beans
DefaultListableBeanFactory->>DefaultSingletonBeanRegistry: super.destroySingletons()<br>Call the parent class method to destroy all singleton beans
DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry: destroySingleton(beanName)<br>Destroy the singleton bean with the given name
DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry: destroyBean(beanName,bean)<br>Run the destruction logic of the bean
DefaultSingletonBeanRegistry->>DisposableBeanAdapter: destroy()<br>Call the destroy method of the bean
DisposableBeanAdapter->>DisposableBeanAdapter: invokeCustomDestroyMethod(destroyMethod)<br>Call the custom destroy method
DisposableBeanAdapter->>Method: invoke(this.bean, args)<br>Call the bean destroy method by reflection
Method->>MyBean: destroy()<br>Run the destroy method of the bean
~~~

### 7. Source Code Analysis

#### @Bean Registration Source Code Analysis

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (a container configured with Java annotations). We pass the `MyConfiguration` class to its constructor. Then we get a bean of type `MyBean` from the context. Finally, we call `context.close()` to close the container.

```java
public class BeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println(context.getBean(MyBean.class));
        context.close();
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

In the `org.springframework.context.support.AbstractApplicationContext#refresh` method, we focus on `invokeBeanFactoryPostProcessors(beanFactory)`. The other methods are not the focus of this article, so we skip them.

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

In the method `org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`, the `BeanDefinitionRegistryPostProcessor` beans are called first. This is a sub-interface of `BeanFactoryPostProcessor`. It is made to change the default bean definitions before all other bean definitions are loaded.

```java
public static void invokeBeanFactoryPostProcessors(
        ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
    // ... [code omitted for brevity]
    invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
	// ... [code omitted for brevity]
}
```

The method `org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors` loops over the processors. It calls `postProcessBeanDefinitionRegistry(registry)` of each `BeanDefinitionRegistryPostProcessor`.

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

The method `org.springframework.context.annotation.ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry` calls `processConfigBeanDefinitions`. Its main job is to process and register the beans defined in configuration classes.

```java
@Override
public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
    // ... [code omitted for brevity]
    processConfigBeanDefinitions(registry);
}
```

The method `org.springframework.context.annotation.ConfigurationClassPostProcessor#processConfigBeanDefinitions` parses and validates the configuration classes. It makes sure all beans defined in them are registered correctly in the Spring bean definition registry.

```java
public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
    // ... [code omitted for brevity]

    // Step 1: Create a parser for configuration classes
    ConfigurationClassParser parser = new ConfigurationClassParser(
            this.metadataReaderFactory, this.problemReporter, this.environment,
            this.resourceLoader, this.componentScanBeanNameGenerator, registry);

    // Step 2: Initialize the set of candidate configuration classes and the set of parsed ones
    Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
    Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());

    // Step 3: Process all candidate configuration classes until no candidates are left
    do {
        // Step 3.1 Parse the configuration classes
        parser.parse(candidates);
        // Step 3.2 Validate the configuration classes
        parser.validate();

        // Get the parsed configuration classes and remove the ones already processed
        Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());
        configClasses.removeAll(alreadyParsed);

        // Step 4: If the reader is null, create a new bean definition reader
        if (this.reader == null) {
            this.reader = new ConfigurationClassBeanDefinitionReader(
                    registry, this.sourceExtractor, this.resourceLoader, this.environment,
                    this.importBeanNameGenerator, parser.getImportRegistry());
        }

        // Step 5: Use the reader to load bean definitions for the parsed configuration classes
        this.reader.loadBeanDefinitions(configClasses);

        // ... [code omitted for brevity]
        
    } while (!candidates.isEmpty());

    // ... [code omitted for brevity]
}
```

Now step 3.1 of `org.springframework.context.annotation.ConfigurationClassPostProcessor#processConfigBeanDefinitions`. The method `org.springframework.context.annotation.ConfigurationClassParser#parse` goes through all configuration class candidates. It parses each annotated bean definition. This usually means finding the @Bean methods and component scan directives in the class, and recording them for the Spring container.

```java
public void parse(Set<BeanDefinitionHolder> configCandidates) {
    // ... [code omitted for brevity]
    parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
    // ... [code omitted for brevity]
}
```

The method `org.springframework.context.annotation.ConfigurationClassParser#parse(metadata, beanName)` turns the annotation metadata and the bean name into a configuration class, then processes it. Processing configuration classes is the core of Spring's configuration-driven setup. It includes parsing @Bean methods, handling @ComponentScan, handling @Import, and more.

```java
protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
    processConfigurationClass(new ConfigurationClass(metadata, beanName), DEFAULT_EXCLUSION_FILTER);
}
```

The method `org.springframework.context.annotation.ConfigurationClassParser#processConfigurationClass` processes one configuration class. It first processes the class and its parent classes recursively, so all related configuration is read and parsed. After that, it adds the configuration class to the map of parsed configuration classes.

```java
protected void processConfigurationClass(ConfigurationClass configClass, Predicate<String> filter) throws IOException {
    // ... [code omitted for brevity]

    // Step 1: Process the configuration class and its superclass hierarchy recursively
    SourceClass sourceClass = asSourceClass(configClass, filter);
    do {
        sourceClass = doProcessConfigurationClass(configClass, sourceClass, filter);
    } while (sourceClass != null);

    // Step 2: Put the processed configuration class into the map
    this.configurationClasses.put(configClass, configClass);
}
```

The method `org.springframework.context.annotation.ConfigurationClassParser#doProcessConfigurationClass` processes the configuration class further. In particular, it handles all methods with the @Bean annotation. These methods define how beans are initialized in the Spring application context. The simplified code below focuses on @Bean methods. The original method also contains other important logic.

```java
@Nullable
protected final SourceClass doProcessConfigurationClass(
    ConfigurationClass configClass, SourceClass sourceClass, Predicate<String> filter)
    throws IOException {

    // ... [code omitted for brevity]
    
    // Process each method annotated with @Bean in the configuration class
    // The goal is to find and record all @Bean methods defined in the configuration class. These methods define how beans in the application are initialized.
    Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(sourceClass);
    for (MethodMetadata methodMetadata : beanMethods) {
        configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
    }

    // ... [code omitted for brevity]
    return null;
}
```

In the method `org.springframework.context.annotation.ConfigurationClassParser#retrieveBeanMethodMetadata`, we finally see how `@Bean` is parsed. First, it tries to use ASM to get a fixed method order. The Java reflection API does not guarantee the order of methods, but in some cases the declaration order matters. So it tries to read the class file with ASM (a Java bytecode library) to get a fixed order. Second, it compares the reflection result with the ASM result. If ASM finds at least as many methods as reflection, it compares the two sets further. If the method names match, it prefers the ASM result because the order is fixed. Finally, the method returns a `Set<MethodMetadata>` with all the methods marked with @Bean.

**Why care about method order?** Sometimes you may expect @Bean methods to run in the order they are declared in the class. You should usually avoid depending on this. But Spring tries to follow your declaration order where it can, especially for lifecycle callbacks and dependency injection.

```java
private Set<MethodMetadata> retrieveBeanMethodMetadata(SourceClass sourceClass) {
    // Get the annotation metadata of the source class
    AnnotationMetadata original = sourceClass.getMetadata();
    
    // Get all methods with the @Bean annotation from the annotation metadata
    Set<MethodMetadata> beanMethods = original.getAnnotatedMethods(Bean.class.getName());

    // If several @Bean methods are found and the metadata is a StandardAnnotationMetadata,
    // try to read the class file with ASM to get a fixed declaration order.
    if (beanMethods.size() > 1 && original instanceof StandardAnnotationMetadata) {
        try {
            // Read the class file metadata with ASM
            AnnotationMetadata asm =
                this.metadataReaderFactory.getMetadataReader(original.getClassName()).getAnnotationMetadata();
            Set<MethodMetadata> asmMethods = asm.getAnnotatedMethods(Bean.class.getName());

            // Check whether the ASM method set contains all methods found by standard reflection
            if (asmMethods.size() >= beanMethods.size()) {
                Set<MethodMetadata> selectedMethods = new LinkedHashSet<>(asmMethods.size());
                for (MethodMetadata asmMethod : asmMethods) {
                    for (MethodMetadata beanMethod : beanMethods) {
                        if (beanMethod.getMethodName().equals(asmMethod.getMethodName())) {
                            selectedMethods.add(beanMethod);
                            break;
                        }
                    }
                }
                if (selectedMethods.size() == beanMethods.size()) {
                    // All methods found by reflection are in the ASM set, so use the ASM set
                    beanMethods = selectedMethods;
                }
            }
        }
        catch (IOException ex) {
            // If reading the class file with ASM fails, log it and keep using the reflection metadata
            logger.debug("Failed to read class file via ASM for determining @Bean method order", ex);
        }
    }

    // Return the @Bean methods found
    return beanMethods;
}
```

The code above is step 3.1. It scans the `@Bean` methods and stores the result in the `beanMethods` field of `ConfigurationClass`.

Now step 4 of `org.springframework.context.annotation.ConfigurationClassPostProcessor#processConfigBeanDefinitions`. In the method `org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitions`, a tool that tracks condition evaluation is set up first. Then each configuration class is visited and its bean definitions are loaded. This is the core of Spring's Java configuration. It makes sure `@Bean` methods are parsed correctly and registered as beans in the container.

```java
public void loadBeanDefinitions(Set<ConfigurationClass> configurationModel) {
    TrackedConditionEvaluator trackedConditionEvaluator = new TrackedConditionEvaluator();
    for (ConfigurationClass configClass : configurationModel) {
        loadBeanDefinitionsForConfigurationClass(configClass, trackedConditionEvaluator);
    }
}
```

In the method `org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForConfigurationClass`, `loadBeanDefinitionsForBeanMethod` is called for each `BeanMethod`. It parses one @Bean method and turns it into a bean definition that the Spring IoC container can manage.

```java
private void loadBeanDefinitionsForConfigurationClass(
			ConfigurationClass configClass, TrackedConditionEvaluator trackedConditionEvaluator) {
    // ... [code omitted for brevity]
    for (BeanMethod beanMethod : configClass.getBeanMethods()) {
        loadBeanDefinitionsForBeanMethod(beanMethod);
    }
	// ... [code omitted for brevity]
}
```

In the method `org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForBeanMethod`, the steps are as follows. If the @Bean method is static, the class that holds the method is used as the bean class. Otherwise, the bean name of the configuration class is used as the factory bean name, and the factory method name is set to the name of the current @Bean method. Next, it tries to get more detailed method metadata. If the metadata is of type `StandardMethodMetadata`, it can get the original method object directly. Then it handles attributes such as `autowire`, `autowireCandidate`, `initMethod`, and `destroyMethod`. These attributes tell how to handle the bean lifecycle and dependency injection. If the @Bean method has a `@Scope` annotation, it handles its attributes, such as the scope name and the proxy mode, and may create a proxy for the bean. Finally, it registers the bean definition in the IoC container with `registerBeanDefinition`.

```java
private void loadBeanDefinitionsForBeanMethod(BeanMethod beanMethod) {
    // ... [code omitted for brevity]

    // Create a new bean definition based on the metadata of the @Bean method
    ConfigurationClassBeanDefinition beanDef = new ConfigurationClassBeanDefinition(configClass, metadata, beanName);

    // Set the source information of the bean definition
    beanDef.setSource(this.sourceExtractor.extractSource(metadata, configClass.getResource()));

    // Check whether the @Bean method is static
    if (metadata.isStatic()) {
        // Handle a static @Bean method
        if (configClass.getMetadata() instanceof StandardAnnotationMetadata) {
            beanDef.setBeanClass(((StandardAnnotationMetadata) configClass.getMetadata()).getIntrospectedClass());
        }
        else {
            beanDef.setBeanClassName(configClass.getMetadata().getClassName());
        }
        beanDef.setUniqueFactoryMethodName(methodName);
    }
    else {
        // Handle an instance @Bean method
        beanDef.setFactoryBeanName(configClass.getBeanName());
        beanDef.setUniqueFactoryMethodName(methodName);
    }

    // If the metadata is of type StandardMethodMetadata, set the resolved factory method
    if (metadata instanceof StandardMethodMetadata) {
        beanDef.setResolvedFactoryMethod(((StandardMethodMetadata) metadata).getIntrospectedMethod());
    }

    // Set the autowire mode to constructor autowiring
    beanDef.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
    beanDef.setAttribute(org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor.
                         SKIP_REQUIRED_CHECK_ATTRIBUTE, Boolean.TRUE);

    // Handle the common annotation attributes of the bean definition
    AnnotationConfigUtils.processCommonDefinitionAnnotations(beanDef, metadata);

    // Get and set the autowire attribute
    Autowire autowire = bean.getEnum("autowire");
    if (autowire.isAutowire()) {
        beanDef.setAutowireMode(autowire.value());
    }

    // Get and set the autowireCandidate attribute
    boolean autowireCandidate = bean.getBoolean("autowireCandidate");
    if (!autowireCandidate) {
        beanDef.setAutowireCandidate(false);
    }

    // Get and set the init method name
    String initMethodName = bean.getString("initMethod");
    if (StringUtils.hasText(initMethodName)) {
        beanDef.setInitMethodName(initMethodName);
    }

    // Get and set the destroy method name
    String destroyMethodName = bean.getString("destroyMethod");
    beanDef.setDestroyMethodName(destroyMethodName);

    // Handle the bean scope and its proxy mode based on the @Scope annotation
    ScopedProxyMode proxyMode = ScopedProxyMode.NO;
    AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(metadata, Scope.class);
    if (attributes != null) {
        beanDef.setScope(attributes.getString("value"));
        proxyMode = attributes.getEnum("proxyMode");
        if (proxyMode == ScopedProxyMode.DEFAULT) {
            proxyMode = ScopedProxyMode.NO;
        }
    }

    // Create a proxy bean definition if needed
    BeanDefinition beanDefToRegister = beanDef;
    if (proxyMode != ScopedProxyMode.NO) {
        BeanDefinitionHolder proxyDef = ScopedProxyCreator.createScopedProxy(
            new BeanDefinitionHolder(beanDef, beanName), this.registry,
            proxyMode == ScopedProxyMode.TARGET_CLASS);
        beanDefToRegister = new ConfigurationClassBeanDefinition(
            (RootBeanDefinition) proxyDef.getBeanDefinition(), configClass, metadata, beanName);
    }

    // Finally, register the bean definition of the @Bean method in the container
    this.registry.registerBeanDefinition(beanName, beanDefToRegister);
}
```

#### @Bean Initialization Source Code Analysis

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (a container configured with Java annotations). We pass the `MyConfiguration` class to its constructor. Then we get a bean of type `MyBean` from the context. Finally, we call `context.close()` to close the container.

```java
public class BeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println(context.getBean(MyBean.class));
        context.close();
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

In the `org.springframework.context.support.AbstractApplicationContext#refresh` method, we focus on `finishBeanFactoryInitialization(beanFactory)`. It instantiates all remaining non-lazy singleton beans. The other methods are not the focus of this article, so we skip them.

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    // ... [code omitted for brevity]
    // Instantiate all remaining (non-lazy-init) singletons.
    finishBeanFactoryInitialization(beanFactory);
    // ... [code omitted for brevity]
}
```

The method `org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization` calls `preInstantiateSingletons` of `DefaultListableBeanFactory` to create all remaining non-lazy singleton beans.

```java
/**
 * Finish the bean initialization of this factory, instantiating all remaining non-lazy-init singleton beans.
 * 
 * @param beanFactory the bean factory to initialize
 */
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [code omitted for brevity]
    // Finish all remaining non-lazy singleton beans.
    beanFactory.preInstantiateSingletons();
}
```

The main goal of `org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons` is to instantiate all non-lazy singleton beans in advance. It is triggered after the Spring context is set up, so all singleton beans are created and initialized correctly. The key call is `getBean(beanName)`. It is called for every singleton bean defined in the container. This triggers instantiation, initialization, and dependency injection. If the bean has not been created yet, the call creates and initializes it.

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

The method `org.springframework.beans.factory.support.AbstractBeanFactory#getBean()` calls `doGetBean` to do the real work of creating the bean. It passes the bean name and some default values. `doGetBean` does most of the work, such as finding the bean definition, creating the bean (if not created yet), and handling dependencies.

```java
@Override
public Object getBean(String name) throws BeansException {
    return doGetBean(name, null, null, false);
}
```

The method `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean` first checks whether the requested bean is a singleton that already exists. If not, it creates a new instance. On the way, it handles special cases such as circular references and makes sure the returned bean has the right type. This is a core part of the lifecycle management of beans in the Spring container.

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
        // For some beans (such as FactoryBeans), more work may be needed to get the real bean instance
        beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
    }
    // ... [code omitted for brevity]

    // Make sure the returned bean instance matches the requested type
    return adaptBeanInstance(name, beanInstance, requiredType);
}
```

The method `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton()` gets an existing bean instance from the singleton cache. Or it creates a new one with the given `ObjectFactory`. This is the key part that makes a bean a singleton in the Spring container.

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

The main job of `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean()` is to call `doCreateBean`. That is where the bean is really instantiated, populated, and initialized. The method returns the new bean instance.

```java
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
    throws BeanCreationException {
    
    // ... [code omitted for brevity]
    
    try {
        // Normal bean instantiation, property population, and initialization.
        // This is where the bean is really created.
        Object beanInstance = doCreateBean(beanName, mbdToUse, args);
        // Log that the bean was created
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

In the method `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`, the `initializeBean` method initializes the bean. It makes sure the bean is fully configured and ready.

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

In the method `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean`, if the bean implements the `InitializingBean` interface, its `afterPropertiesSet` method is called here. If the bean definition has a custom init method, Spring also calls it here.

```java
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {

    // ... [code omitted for brevity]
    
    try {
        invokeInitMethods(beanName, wrappedBean, mbd);
    }
    catch (Throwable ex) {
        // ... [code omitted for brevity]
    }
    
    // ... [code omitted for brevity]

    return wrappedBean;
}
```

In the method `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeInitMethods`, `afterPropertiesSet` is a special init method defined by the `InitializingBean` interface. If the bean implements this interface, the method is called automatically after the properties are injected. To avoid calling it twice, Spring checks whether the bean is an `InitializingBean` and whether the init method name is `afterPropertiesSet`.

```java
protected void invokeInitMethods(String beanName, Object bean, @Nullable RootBeanDefinition mbd)
			throws Throwable {
    // ... [code omitted for brevity]
    
    // Check that the bean definition exists and the bean is not a NullBean (a special type Spring uses as a placeholder bean)
    if (mbd != null && bean.getClass() != NullBean.class) {
        // Get the init method name
        String initMethodName = mbd.getInitMethodName();
        // Check whether there is an init method to call
        if (StringUtils.hasLength(initMethodName) &&
            // The bean is not an InitializingBean, or the init method name is not "afterPropertiesSet"
            !(isInitializingBean && "afterPropertiesSet".equals(initMethodName)) &&
            // The init method is not managed externally (for example, by an AspectJ aspect)
            !mbd.isExternallyManagedInitMethod(initMethodName)) {
            // Call the custom init method
            invokeCustomInitMethod(beanName, bean, mbd);
        }
    }
}
```

The method `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeCustomInitMethod` uses the Java reflection API to call the init method of the bean.

```java
protected void invokeCustomInitMethod(String beanName, Object bean, RootBeanDefinition mbd)
			throws Throwable {
	// ... [code omitted for brevity]

	// Call the init method of the bean by reflection
    try {
        // Make sure the method to call is accessible
        ReflectionUtils.makeAccessible(methodToInvoke);
        // Call the custom init method of the bean
        methodToInvoke.invoke(bean);
    }
    catch (InvocationTargetException ex) {
        // If the init method throws an exception, it is caught here and its target exception is rethrown
        throw ex.getTargetException();
    }
    // ... [code omitted for brevity]
}
```

#### @Bean Destruction Source Code Analysis

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (a container configured with Java annotations). We pass the `MyConfiguration` class to its constructor. Then we get a bean of type `MyBean` from the context. Finally, we call `context.close()` to close the container.

```java
public class BeanApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println(context.getBean(MyBean.class));
        context.close();
    }
}
```

In the method `org.springframework.context.support.AbstractApplicationContext#close`, a synchronized block on the `startupShutdownMonitor` object starts first. This makes sure only one thread runs the code in the block at a time. It prevents resource conflicts and inconsistent data between threads. Then it calls `doClose`. Finally, it removes the JVM shutdown hook, if one was registered.

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

The method `org.springframework.context.support.AbstractApplicationContext#destroyBeans` first calls `getBeanFactory()` to get the Spring `BeanFactory`. Then it calls `destroySingletons` on it. This destroys all singleton beans cached in the `BeanFactory`.

```java
protected void destroyBeans() {
    getBeanFactory().destroySingletons();
}
```

The method `org.springframework.beans.factory.support.DefaultListableBeanFactory#destroySingletons` first calls `destroySingletons` of the parent class. This makes sure the destruction logic of the parent class also runs.

```java
@Override
public void destroySingletons() {
    super.destroySingletons();
    updateManualSingletonNames(Set::clear, set -> !set.isEmpty());
    clearByTypeCache();
}
```

The method `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingletons` first takes all bean names from the key set of the `disposableBeans` field and turns them into a string array. `disposableBeans` holds the beans that need special handling when the container is destroyed, such as beans that implement `DisposableBean`. Then it loops backwards, from the last one, and destroys all beans in the `disposableBeans` list. This keeps dependencies correct: a bean created earlier should be destroyed later.

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

The method `org.springframework.beans.factory.support.DefaultListableBeanFactory#destroySingleton` first calls `destroySingleton` of the parent class. This makes sure the destruction logic of the parent class also runs.

```java
@Override
public void destroySingleton(String beanName) {
    super.destroySingleton(beanName);
    removeManualSingletonName(beanName);
    clearByTypeCache();
}
```

The method `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingleton` first synchronizes on the `disposableBeans` object with `synchronized`, so access is safe with many threads. It removes the bean with the given name from the `disposableBeans` collection and casts it to `DisposableBean`. Finally, it calls `destroyBean` to do the real destruction.

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

The method `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroyBean` calls the `destroy` method of `bean` directly. Because `bean` is a `DisposableBean`, it always has a `destroy` method. That method holds the custom destruction logic of the bean.

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

The method `org.springframework.beans.factory.support.DisposableBeanAdapter#destroy` first checks whether a destroy method (`destroyMethod`) is set for the bean. If so, it calls `invokeCustomDestroyMethod` directly. If there is no method but there is a method name (`destroyMethodName`), it tries to find the method by that name. Once the method is found, it calls `invokeCustomDestroyMethod`.

```java
@Override
public void destroy() {
    // ... [code omitted for brevity]

    // If a destroy method is provided, call it directly
    if (this.destroyMethod != null) {
        invokeCustomDestroyMethod(this.destroyMethod);
    }
    // Otherwise, if a destroy method name is provided, try to find the method and call it
    else if (this.destroyMethodName != null) {
        // Find the destroy method to call from the method name
        Method methodToInvoke = determineDestroyMethod(this.destroyMethodName);
        if (methodToInvoke != null) {
            // If the method is found, call it, using the interface version of the method if possible
            invokeCustomDestroyMethod(ClassUtils.getInterfaceMethodIfPossible(methodToInvoke));
        }
    }
}
```

The method `org.springframework.beans.factory.support.DisposableBeanAdapter#invokeCustomDestroyMethod` runs the custom destroy method. We define this method for the bean in the Spring configuration. It releases resources and cleans up at the end of the bean lifecycle.

```java
private void invokeCustomDestroyMethod(final Method destroyMethod) {
    // ... [code omitted for brevity]
    try {
        if (System.getSecurityManager() != null) {
            // ... [code omitted for brevity]
        }
        else {
            ReflectionUtils.makeAccessible(destroyMethod);
            destroyMethod.invoke(this.bean, args);
        }
    }
    catch (InvocationTargetException ex) {
        // ... [code omitted for brevity]
    }
    catch (Throwable ex) {
        // ... [code omitted for brevity]
    }
}
```

### 8. Notes

1. **The method name is the bean name**
   + By default, the name of the method marked with `@Bean` is used as the bean name. To use a custom name, set it in the annotation, for example `@Bean("customBeanName")`.

2. **Return type**
   + Make sure the return type of the `@Bean` method matches the bean object that is really created.

3. **Calling other `@Bean` methods**
   + In a configuration class, one `@Bean` method can call another `@Bean` method to get its result. This works because `@Bean` method calls go through a Spring proxy. The proxy makes sure singleton beans have only one instance.

4. **Scope**
   + Use the `@Scope` annotation with `@Bean` to set the bean scope, such as singleton (the default), prototype, request, or session.

5. **Lifecycle callbacks**
   + `@Bean` lets you set `initMethod` and `destroyMethod`. They are called when the bean is initialized and destroyed.

6. **Avoid duplicate definitions**
   + Do not define the same bean in both XML and Java configuration. If you must, make sure the bean name and definition are the same. Otherwise the behavior is unpredictable.

7. **Use `@Lazy` with care**
   + `@Lazy` makes a bean initialize only when it is first requested. If a bean must be initialized at application startup, do not mark it as lazy.

8. **`@Bean` methods with parameters**
   + A `@Bean` method can take parameters. They are resolved from the Spring container automatically. This is very useful when one bean depends on another bean.

9. **Think about thread safety**
   + This matters most for prototype-scoped beans. Make sure the bean is thread safe, or that it is not shared between threads.

10. **Use conditional annotations**
    + With `@Conditional` or related annotations, you can create a bean only when certain conditions are met.

11. **Difference between `@Bean` and `@Component`**
    + Both define beans. But `@Bean` is usually put on methods, especially in Java configuration classes. `@Component` (and its specializations such as `@Service`, `@Repository`, and `@Controller`) is put on classes.

12. **Watch for circular dependencies**
    + If two `@Bean` methods call each other, a circular dependency may appear. Spring can resolve circular dependencies between singleton beans, but this is not recommended.

13. **Configuration classes should be managed by Spring**
    + Make sure the class that holds `@Bean` methods is managed by Spring and marked with `@Configuration`. Then `@Bean` methods are called through the Spring proxy. This supports the features above, such as the singleton guarantee and circular references.

### 9. Summary

#### Best Practices Summary

1. **Application startup**
   + `AnnotationConfigApplicationContext` starts and configures the Spring context from Java annotations. In the example above, we pass the Java configuration class `MyConfiguration` to initialize the context.

2. **Using a configuration class**
   + Mark the configuration class with `@Configuration` to show that it holds Spring bean definitions. In the class, use `@Bean` to define beans. By default, the method name is the bean name, and the returned instance is the bean instance managed by the Spring container.

3. **Bean lifecycle**
   + The `initMethod` and `destroyMethod` attributes of `@Bean` set the methods to call when the bean is initialized and destroyed. This gives the bean its own initialization and cleanup. When the Spring container instantiates the bean, the init method is called. When the bean is destroyed or the container is closed, the destroy method is called.

#### Source Code Analysis Summary

1. **@Bean Registration Source Code Analysis Summary**

   + **Startup and loading configuration**
     + `AnnotationConfigApplicationContext` initializes the Spring context with the `MyConfiguration` class. The context constructor runs `register()` and `refresh()`.

   + **Parsing bean definitions**
     + `refresh()` refreshes the context, which includes handling bean definitions. It calls `invokeBeanFactoryPostProcessors()` to run the bean factory post processors.

   + **Processing configuration classes**
     + `BeanDefinitionRegistryPostProcessor` is a special `BeanFactoryPostProcessor`. It runs before all other bean definitions are loaded. This work is done mainly by `ConfigurationClassPostProcessor`, which handles classes marked with `@Configuration`.

   + **Parsing `@Bean` methods**
     + Each `@Configuration` class is parsed, and its `@Bean` methods become bean definitions. This reads the method metadata and turns it into a `BeanDefinition` object that Spring can understand and manage.

   + **Handling bean scope and proxy**
     + If a `@Bean` method has a `@Scope` annotation, the bean scope is handled from its attributes. A proxy may be created for the bean if needed. `ScopedProxyCreator` does this.

   + **Registering bean definitions**
     + At the end, the parsed bean definitions are registered in the Spring bean definition registry. After the context starts, these beans can be instantiated and used.

2. **@Bean Initialization Source Code Analysis Summary**

   + **Startup**
     + When `AnnotationConfigApplicationContext` is used with the `MyConfiguration` class, the Spring container starts to initialize.

   + **Refreshing the context**
     +  The `refresh()` method refreshes the Spring context. This is the key stage where all beans in the context are created and initialized.

   + **Initializing the bean factory**
     +  The method `finishBeanFactoryInitialization` makes sure all non-lazy singleton beans are initialized. It calls `preInstantiateSingletons` in `DefaultListableBeanFactory`. That method loops over every singleton bean defined in the container and initializes it with `getBean`.

   + **Getting the bean**:
     + When a bean is requested, Spring first checks whether the singleton already exists. If not, it creates it. The core method is `doGetBean`. It manages the whole bean lifecycle, from finding the bean definition to initializing the bean.

   + **Singleton handling**
     + The `getSingleton` method manages the singleton bean cache. It first tries to get the bean from the cache. If it is not found, it creates a new instance with the given `ObjectFactory`.

   + **Creating the bean**
     +  If needed, `createBean` instantiates the bean. The real bean creation work is done in `doCreateBean`.

   + **Initializing the bean**
     +  After the bean is instantiated, it must be initialized. If the bean implements `InitializingBean`, its `afterPropertiesSet` method is called. If the bean definition has a custom init method, that method is also called at this point.

   + **Calling by reflection**
     + At the end, if a custom init method is defined, Spring calls it with the Java reflection API.

3. **@Bean Destruction Source Code Analysis Summary**

   + **Startup and shutdown**
     + After the Spring container is initialized through `AnnotationConfigApplicationContext`, `context.close()` is called to close the container.

   + **Closing the context**
     + `close()` first synchronizes on `startupShutdownMonitor`, so only one thread can close the context at a time. Then it calls `doClose` to do the real closing.

   + **Destroying beans**
     + In `doClose`, `destroyBeans` is called to destroy all cached singleton beans.

   + **Destroying singleton beans**
     + `destroyBeans` calls `destroySingletons` of the BeanFactory to destroy all cached singleton beans.

   + **Looping and destroying**
     + In `destroySingletons`, all cached singleton beans are visited. For each bean, `destroySingleton` is called to destroy it.

   + **The real destruction**
     + In `destroySingleton`, the bean is removed from the `disposableBeans` list and then really destroyed.

   + **Custom destruction logic**
     + If the bean implements `DisposableBean` or has a custom destroy method, the Spring container makes sure these methods are called when the bean is destroyed. The destroy method can be any method in the bean configuration. It does not need a specific signature.

   + **Calling the destroy method by reflection**
     + At the end, Spring uses the Java reflection API to call the custom destroy method of the bean.