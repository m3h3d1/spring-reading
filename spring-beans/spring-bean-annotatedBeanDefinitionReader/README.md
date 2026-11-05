## AnnotatedBeanDefinitionReader

- [AnnotatedBeanDefinitionReader](#annotatedbeandefinitionreader)
  - [1. Background Knowledge](#1-background-knowledge)
  - [2. Basic Description](#2-basic-description)
  - [3. Main Features](#3-main-features)
  - [4. Best Practices](#4-best-practices)
  - [5. Sequence Diagram](#5-sequence-diagram)
  - [6. Source Code Analysis](#6-source-code-analysis)
    - [Register Annotation Post-Processors Phase](#register-annotation-post-processors-phase)
    - [Register Bean Definition Phase](#register-bean-definition-phase)
  - [7. Relationship with Other Components](#7-relationship-with-other-components)
  - [8. FAQ](#8-faq)


### 1. Background Knowledge

1. **`AnnotationMetadata`**
   + `AnnotationMetadata` is a Spring interface that handles the annotation information on a class. It gives methods to access and work with that information. `AnnotatedBeanDefinitionReader` uses `AnnotationMetadata` to parse the annotations on a class and turn them into a Spring BeanDefinition.
   + [View the AnnotationMetadata interface](https://github.com/xuchengsheng/spring-reading/tree/master/spring-metadata/spring-metadata-annotationMetadata)
2. **`BeanDefinition`**
   + `BeanDefinition` is the core Spring concept that describes and manages bean configuration. It has information about a bean, such as class name, scope, dependencies and initialization method. One of the main tasks of `AnnotatedBeanDefinitionReader` is to turn annotated classes into `BeanDefinition`s and register them in the Spring container.
   + [View the BeanDefinition interface](https://github.com/xuchengsheng/spring-reading/tree/master/spring-beans/spring-bean-beanDefinition)

### 2. Basic Description

`AnnotatedBeanDefinitionReader` reads and parses annotated bean definitions. It is used for annotation-based configuration. You mark a Java class as a Spring component, and the Spring container scans and registers it. You do not need to configure the bean definition by hand.

### 3. Main Features

1. **Register bean definitions**
   + Fits classes that do not use a specific annotation but still must be managed by the Spring container.
2. **Integrate with the Spring container**
   + `AnnotatedBeanDefinitionReader` is usually used with a registry of the Spring container (such as `GenericApplicationContext`), to register the parsed bean definitions in the container.

### 4. Best Practices

This example creates a container and a linked `AnnotatedBeanDefinitionReader`, registers the `MyBean` class as a Spring bean by hand, and then gets and prints the instance of the bean.

```java
public class AnnotatedBeanDefinitionReaderDemo {

    public static void main(String[] args) {
        // Create an AnnotationConfigApplicationContext
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // Create an AnnotatedBeanDefinitionReader and link it to the container
        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(factory);

        // Register the bean with the AnnotatedBeanDefinitionReader
        reader.registerBean(MyBean.class);

        // Get and print MyBean
        System.out.println("MyBean = " + factory.getBean(MyBean.class));
    }
}
```

`MyBean` is a simple Java class that represents a plain Java bean.

```java
public class MyBean {
}
```

The result shows that registering and getting the bean by hand worked, and `MyBean` is now managed by the Spring container.

```java
MyBean = com.xcs.spring.bean.MyBean@6166e06f
```

### 5. Sequence Diagram

~~~mermaid
sequenceDiagram
Title: AnnotatedBeanDefinitionReader Sequence Diagram

par Register annotation post-processors phase
AnnotatedBeanDefinitionReaderDemo->>AnnotatedBeanDefinitionReader:new AnnotatedBeanDefinitionReader(registry)
Note over AnnotatedBeanDefinitionReaderDemo, AnnotatedBeanDefinitionReader: Create the AnnotatedBeanDefinitionReader object
AnnotatedBeanDefinitionReader->>AnnotatedBeanDefinitionReader:AnnotatedBeanDefinitionReader(registry, environment)
Note over AnnotatedBeanDefinitionReader: Use the registry and environment information
AnnotatedBeanDefinitionReader->>AnnotationConfigUtils:registerAnnotationConfigProcessors(registry)
Note over AnnotatedBeanDefinitionReader, AnnotationConfigUtils: Call the utility method to register the annotation post-processors
AnnotationConfigUtils->>AnnotationConfigUtils:registerAnnotationConfigProcessors(registry, source)
Note over AnnotationConfigUtils: Register the annotation post-processors in the container
AnnotatedBeanDefinitionReader->>AnnotatedBeanDefinitionReaderDemo:Return the reader
Note over AnnotatedBeanDefinitionReaderDemo,AnnotatedBeanDefinitionReader: Return the AnnotatedBeanDefinitionReader instance
end

par Register bean definition phase
AnnotatedBeanDefinitionReaderDemo->>AnnotatedBeanDefinitionReader:registerBean(beanClass)
Note over AnnotatedBeanDefinitionReaderDemo, AnnotatedBeanDefinitionReader: Register BeanClass
AnnotatedBeanDefinitionReader->>AnnotatedBeanDefinitionReader:doRegisterBean(beanClass, name, qualifiers, supplier, customizers)
Note over AnnotatedBeanDefinitionReader: Run the bean registration
AnnotatedBeanDefinitionReader->>AnnotatedBeanDefinitionReader:new AnnotatedGenericBeanDefinition(beanClass)
Note over AnnotatedBeanDefinitionReader: Create the AnnotatedGenericBeanDefinition object
AnnotatedBeanDefinitionReader->>AnnotationConfigUtils:processCommonDefinitionAnnotations(abd)
Note over AnnotatedBeanDefinitionReader, AnnotationConfigUtils: Handle the common bean definition annotations
AnnotationConfigUtils->>AnnotationConfigUtils:processCommonDefinitionAnnotations(abd,metadata)
Note over AnnotationConfigUtils: Handle the metadata of the bean
AnnotatedBeanDefinitionReader->>AnnotatedBeanDefinitionReader:new BeanDefinitionHolder(abd, beanName)
Note over AnnotatedBeanDefinitionReader: Create the BeanDefinitionHolder
AnnotatedBeanDefinitionReader->>BeanDefinitionReaderUtils:registerBeanDefinition(definitionHolder, registry)
Note over AnnotatedBeanDefinitionReader, BeanDefinitionReaderUtils: Register the bean definition
end
~~~

### 6. Source Code Analysis

#### Register Annotation Post-Processors Phase

The `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#AnnotatedBeanDefinitionReader(registry)` constructor delegates to another constructor and passes the environment of the context.

```java
public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
    this(registry, getOrCreateEnvironment(registry));
}
```

The main goal of the `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#AnnotatedBeanDefinitionReader(registry,environment)` constructor is to initialize `AnnotatedBeanDefinitionReader` and make sure the condition evaluator and the annotation processors are registered in the Spring container. This supports annotation-based component scanning and bean registration.

```java
public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
    Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
    Assert.notNull(environment, "Environment must not be null");

    // Initialize the BeanDefinitionRegistry and the Environment
    this.registry = registry;
    this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);

    // Register the annotation-related post-processors to support annotation-based component scanning and bean registration
    AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
}
```

The `org.springframework.context.annotation.AnnotationConfigUtils#registerAnnotationConfigProcessors(registry)` method registers the annotation configuration processors in the Spring container. This enables annotation-driven configuration.

```java
public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
    registerAnnotationConfigProcessors(registry, null);
}
```

The main goal of the `org.springframework.context.annotation.AnnotationConfigUtils#registerAnnotationConfigProcessors(registry, source)` method is to register some key annotation configuration processors. They support annotation-driven configuration and handle different kinds of annotations.

```java
public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(
        BeanDefinitionRegistry registry, @Nullable Object source) {

    // 1. If the BeanDefinitionRegistry is an instance of DefaultListableBeanFactory, do the following
    DefaultListableBeanFactory beanFactory = unwrapDefaultListableBeanFactory(registry);
    if (beanFactory != null) {
        // 1.1 Check whether the current dependency comparator is an instance of AnnotationAwareOrderComparator. If not, set it to AnnotationAwareOrderComparator.INSTANCE to handle annotation-driven ordering.
        if (!(beanFactory.getDependencyComparator() instanceof AnnotationAwareOrderComparator)) {
            beanFactory.setDependencyComparator(AnnotationAwareOrderComparator.INSTANCE);
        }
        // 1.2 Check whether the current autowire candidate resolver is an instance of ContextAnnotationAutowireCandidateResolver. If not, set it to ContextAnnotationAutowireCandidateResolver to handle annotation-driven autowiring.
        if (!(beanFactory.getAutowireCandidateResolver() instanceof ContextAnnotationAutowireCandidateResolver)) {
            beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        }
    }

    // 2. Create an empty LinkedHashSet to hold the bean definitions to register.
    Set<BeanDefinitionHolder> beanDefs = new LinkedHashSet<>(8);

    // 3. Check whether a bean definition named CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME is already registered. If not, create a bean definition of type ConfigurationClassPostProcessor and add it to the BeanDefinitionRegistry.
    if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    // 4. Check whether a bean definition named AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME is already registered. If not, create a bean definition of type AutowiredAnnotationBeanPostProcessor and add it to the BeanDefinitionRegistry.
    if (!registry.containsBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessor.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    // 5. Check whether a bean definition named COMMON_ANNOTATION_PROCESSOR_BEAN_NAME is already registered. This is the processor that supports JSR-250 annotations. If not, and JSR-250 support is detected, create a bean definition of type CommonAnnotationBeanPostProcessor and add it to the BeanDefinitionRegistry.
    if (jsr250Present && !registry.containsBeanDefinition(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(CommonAnnotationBeanPostProcessor.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    // 6. Check whether a bean definition named PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME is already registered. This is the processor that supports JPA annotations. If not, and JPA support is detected, create a bean definition of type PersistenceAnnotationBeanPostProcessor and add it to the BeanDefinitionRegistry.
    if (jpaPresent && !registry.containsBeanDefinition(PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition();
        try {
            def.setBeanClass(ClassUtils.forName(PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME,
                                                AnnotationConfigUtils.class.getClassLoader()));
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalStateException(
						"Cannot load optional framework class: " + PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME, ex);
        }
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    // 7. Check whether a bean definition named EVENT_LISTENER_PROCESSOR_BEAN_NAME is already registered. If not, create a bean definition of type EventListenerMethodProcessor and add it to the BeanDefinitionRegistry.
    if (!registry.containsBeanDefinition(EVENT_LISTENER_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(EventListenerMethodProcessor.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, EVENT_LISTENER_PROCESSOR_BEAN_NAME));
    }

    // 8. Check whether a bean definition named EVENT_LISTENER_FACTORY_BEAN_NAME is already registered. If not, create a bean definition of type DefaultEventListenerFactory and add it to the BeanDefinitionRegistry.
    if (!registry.containsBeanDefinition(EVENT_LISTENER_FACTORY_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(DefaultEventListenerFactory.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, EVENT_LISTENER_FACTORY_BEAN_NAME));
    }

    // 9. Return the LinkedHashSet that holds the registered bean definitions.
    return beanDefs;
}
```

#### Register Bean Definition Phase

The `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#registerBean(beanClass)` method calls the `doRegisterBean` method.

```java
public void registerBean(Class<?> beanClass) {
    doRegisterBean(beanClass, null, null, null, null);
}
```

The `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#doRegisterBean` method creates a bean definition, sets its properties (such as scope, name, qualifiers and custom settings), and registers the definition in the Spring container. The bean is then managed by the container.

```java
private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name,
        @Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier,
        @Nullable BeanDefinitionCustomizer[] customizers) {
    // Step 1: Create an AnnotatedGenericBeanDefinition that stands for the definition of the bean to register
    AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
    // If there is a condition, decide by its result whether to register the bean
    if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
        return;
    }
    // Set the supplier of the bean instance, used to create the bean instance
    abd.setInstanceSupplier(supplier);
    // Resolve and set the scope of the bean
    ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
    abd.setScope(scopeMetadata.getScopeName());
    // Generate the bean name, or use the given one. If no name is given, generate a default bean name
    String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));
    
    // Step 2: Handle the common bean definition annotations, such as @Lazy and @Primary
    AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
    // Handle the qualifiers of the bean, such as the @Qualifier annotation
    if (qualifiers != null) {
        for (Class<? extends Annotation> qualifier : qualifiers) {
            if (Primary.class == qualifier) {
                abd.setPrimary(true);
            } else if (Lazy.class == qualifier) {
                abd.setLazyInit(true);
            } else {
                abd.addQualifier(new AutowireCandidateQualifier(qualifier));
            }
        }
    }
    // Customize the bean definition, if there are custom operations
    if (customizers != null) {
        for (BeanDefinitionCustomizer customizer : customizers) {
            customizer.customize(abd);
        }
    }
    // Step 3: Register the bean in the Spring container, so it is managed by the container
    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
    definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
    BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
}
```

> [Step 2 of `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#doRegisterBean`]

The `org.springframework.context.annotation.AnnotationConfigUtils#processCommonDefinitionAnnotations(abd)` method handles the common annotations on a bean definition. This makes sure the behavior and properties of the bean in the container follow these annotations.

```java
public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd) {
    processCommonDefinitionAnnotations(abd, abd.getMetadata());
}
```

The `org.springframework.context.annotation.AnnotationConfigUtils#processCommonDefinitionAnnotations(abd,metadata)` method handles the common bean definition annotations, such as `@Lazy`, `@Primary`, `@DependsOn`, `@Role` and `@Description`.

```java
static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd, AnnotatedTypeMetadata metadata) {
    // Handle the @Lazy annotation
    AnnotationAttributes lazy = attributesFor(metadata, Lazy.class);
    if (lazy != null) {
        abd.setLazyInit(lazy.getBoolean("value"));
    } else if (abd.getMetadata() != metadata) {
        lazy = attributesFor(abd.getMetadata(), Lazy.class);
        if (lazy != null) {
            abd.setLazyInit(lazy.getBoolean("value"));
        }
    }

    // Handle the @Primary annotation
    if (metadata.isAnnotated(Primary.class.getName())) {
        abd.setPrimary(true);
    }

    // Handle the @DependsOn annotation
    AnnotationAttributes dependsOn = attributesFor(metadata, DependsOn.class);
    if (dependsOn != null) {
        abd.setDependsOn(dependsOn.getStringArray("value"));
    }

    // Handle the @Role annotation
    AnnotationAttributes role = attributesFor(metadata, Role.class);
    if (role != null) {
        abd.setRole(role.getNumber("value").intValue());
    }

    // Handle the @Description annotation
    AnnotationAttributes description = attributesFor(metadata, Description.class);
    if (description != null) {
        abd.setDescription(description.getString("value"));
    }
}
```

> [Step 3 of `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#doRegisterBean`]

The `org.springframework.beans.factory.support.BeanDefinitionReaderUtils#registerBeanDefinition` method registers the bean definition in the bean definition registry of the Spring container, and also handles the registration of aliases.

```java
public static void registerBeanDefinition(
			BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
			throws BeanDefinitionStoreException {

    // Register bean definition under primary name.
    String beanName = definitionHolder.getBeanName();
    registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

    // Register aliases for bean name, if any.
    String[] aliases = definitionHolder.getAliases();
    if (aliases != null) {
        for (String alias : aliases) {
            registry.registerAlias(beanName, alias);
        }
    }
}
```

### 7. Relationship with Other Components

1. **`BeanDefinitionLoader`**
   + Usually related to the loading and initialization of a Spring Boot application. In Spring Boot, `BeanDefinitionLoader` loads the application configuration. It may involve `AnnotationConfigApplicationContext` or `AnnotationConfigWebApplicationContext`. These context classes use `AnnotatedBeanDefinitionReader` to register annotation-based beans.
2. **`AnnotationConfigServletWebApplicationContext`**
   + This is the context class of Spring Boot web applications, used for annotation-based configuration. Internally it uses `AnnotatedBeanDefinitionReader` to handle component scanning and bean registration, to support the initialization of a Spring Boot web application.
3. **`AnnotationConfigApplicationContext`**
   + This is the standard Spring application context for annotation-based configuration. Internally it uses `AnnotatedBeanDefinitionReader` to register annotation-based bean definitions, and it supports component scanning and auto-configuration.
4. **`AnnotationConfigWebApplicationContext`**
   + This is the context class of Spring web applications, used for annotation-based configuration. It is similar to `AnnotationConfigApplicationContext`, but made for web applications. It also uses `AnnotatedBeanDefinitionReader` to handle component scanning and bean registration, to support the initialization of a Spring web application.

### 8. FAQ

1. **Annotation-based Spring configuration**
   + `AnnotatedBeanDefinitionReader` can be used to create annotation-based Spring configuration, so you avoid traditional XML configuration. This is very common in modern Spring development. It makes configuration easier to read and maintain.
2. **Spring Boot auto-configuration**
   + In Spring Boot, `AnnotatedBeanDefinitionReader` is used to scan and register Spring Boot auto-configuration classes. This lets a Spring Boot application configure many features automatically, which lowers our workload.
3. **Custom component scanning**
   + We can write our own component scanner and use the features of `AnnotatedBeanDefinitionReader` to implement specific component registration logic. This is useful in some complex cases, for example when components must be registered dynamically under specific conditions.