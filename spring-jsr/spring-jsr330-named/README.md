## @Named

- [@Named](#named)
  - [1. Overview](#1-overview)
  - [2. Annotation Description](#2-annotation-description)
  - [3. Annotation Source](#3-annotation-source)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
    - [@Named Default Filter Registration](#named-default-filter-registration)
    - [@Named Component Scan](#named-component-scan)
  - [7. Source Code Analysis](#7-source-code-analysis)
    - [@Named Default Filter Registration](#named-default-filter-registration-1)
    - [@Named Component Scan](#named-component-scan-1)
      - [Prerequisites](#prerequisites)
      - [Scan Entry](#scan-entry)
    - [@Named Dependency Injection](#named-dependency-injection)
  - [8. Notes](#8-notes)
  - [9. Summary](#9-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133945784) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Annotation Description

The `@Named` annotation is a standard Java annotation from JSR 330: Dependency Injection for Java. Its main goal is to give a standard way to do dependency injection on the Java platform.

### 3. Annotation Source

`@Named` is an annotation defined in JSR-330. It sets a qualifier for dependency injection. It is mainly used when there are several beans of the same type. It gives a clear name or identifier, so the choice is not ambiguous. The annotation has a `value` attribute, which lets you set the name of the bean. The default value is an empty string. `@Named` carries an important meta-annotation, `@Qualifier`, which shows that it can be used as a qualifier. In Java dependency injection, especially when you need to remove ambiguity, this annotation plays a key role.

```java
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Named {
    String value() default "";
}

```

### 4. Main Features

1. **Bean identifier**
   + The `@Named` annotation gives a name to a class, so the class can be recognized and managed as a bean. Then the bean can be referred to and injected elsewhere by this name.
2. **Replace the default name**
   + By default, a bean without a specific name uses its class name with a lowercase first letter. With `@Named`, you can use the `value` attribute to override this default name.
3. **Remove ambiguity**
   + In dependency injection, several beans may fit one injection point. In this case, `@Named` can be used together with `@Inject` to say clearly which bean should be injected.

### 5. Best Practices

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (this class configures the Spring container with Java annotations). We pass the `MyConfiguration` component class as the constructor argument. Then we get a bean of type `MyController` from the Spring context and call its `showService` method.

```java
public class NamedApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyController controller = context.getBean(MyController.class);
        controller.showService();
    }
}
```

The `MyConfiguration` class uses `@ComponentScan("com.xcs.spring")`. This tells Spring to search the given package ("`com.xcs.spring`") and its sub-packages for classes with annotations such as `@Component`, `@Service`, `@Repository`, and `@Controller`. Spring registers them as beans automatically. So you do not need to write a bean definition for each component.

```java
@Configuration
@ComponentScan("com.xcs.spring")
public class MyConfiguration {

}
```

`MyController` is marked with `@Controller`. This class depends on a service of type `MyService`. With the `@Inject` annotation, we say we want this service injected. The `@Named("myServiceB")` annotation sets the name of the service instance to inject, which is "`myServiceB`". The `showService()` method simply prints the service instance that was injected.

```java
@Controller
public class MyController {

    @Inject
    @Named("myServiceB")
    private MyService myService;

    public void showService(){
        System.out.println("myService = " + myService);
    }
}
```

`MyService` is a base class. `MyServiceA` is a subclass of `MyService`. It is marked with `@Named("myServiceA")`, so in the dependency injection container, an instance of this class is known as "myServiceA". In the same way, `MyServiceB` is also a subclass of `MyService`. It is marked with `@Named("myServiceB")`, so in the dependency injection container, an instance of this class is known as "myServiceB".

```java
public class MyService {

}

@Named("myServiceA")
public class MyServiceA extends MyService{

}

@Named("myServiceB")
public class MyServiceB extends MyService{

}
```

The result shows that the `@Named("myServiceB")` annotation injected an instance of type `MyServiceB`.

```
myService = com.xcs.spring.service.MyServiceB@2e8c1c9b
```

### 6. Sequence Diagram

#### @Named Default Filter Registration

~~~mermaid
sequenceDiagram
Title: @Named annotation default filter registration sequence diagram

Note right of NamedApplication: Initialize the application
NamedApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)
Note right of AnnotationConfigApplicationContext: Initialize the Spring context
AnnotationConfigApplicationContext->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext()
Note right of AnnotationConfigApplicationContext: Create the bean definition scanner
AnnotationConfigApplicationContext->>ClassPathBeanDefinitionScanner: ClassPathBeanDefinitionScanner(registry)
Note right of ClassPathBeanDefinitionScanner: Set whether to use the default filters
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner: ClassPathBeanDefinitionScanner(registry,useDefaultFilters)
Note right of ClassPathBeanDefinitionScanner: Get or create the environment
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner: getOrCreateEnvironment(registry)
Note right of ClassPathBeanDefinitionScanner: Initialize the scanner with the given environment
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner: ClassPathBeanDefinitionScanner(registry,useDefaultFilters,environment)
Note right of ClassPathBeanDefinitionScanner: Finish initializing the scanner
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner: ClassPathBeanDefinitionScanner(registry,useDefaultFilters,environment,resourceLoader)
Note right of ClassPathBeanDefinitionScanner: Register the default annotation filters
ClassPathBeanDefinitionScanner->>ClassPathScanningCandidateComponentProvider: registerDefaultFilters()
Note right of ClassPathScanningCandidateComponentProvider: Add the @Named filter
ClassPathScanningCandidateComponentProvider->>ClassPathScanningCandidateComponentProvider: this.includeFilters.add("javax.inject.Named");
~~~

#### @Named Component Scan

~~~mermaid
sequenceDiagram
Title: @Named annotation component scan sequence diagram
ComponentScanAnnotationParser->>ClassPathBeanDefinitionScanner: doScan(basePackages)
Note right of ClassPathBeanDefinitionScanner: Find candidate components
ClassPathBeanDefinitionScanner->>ClassPathScanningCandidateComponentProvider: findCandidateComponents(basePackage)
Note right of ClassPathScanningCandidateComponentProvider: Scan candidate components
ClassPathScanningCandidateComponentProvider->>ClassPathScanningCandidateComponentProvider: scanCandidateComponents(basePackage)
Note right of ClassPathScanningCandidateComponentProvider: Check if it is a candidate component
ClassPathScanningCandidateComponentProvider->>ClassPathScanningCandidateComponentProvider: isCandidateComponent(metadataReader)
Note right of ClassPathScanningCandidateComponentProvider: Filter match check
ClassPathScanningCandidateComponentProvider->>AbstractTypeHierarchyTraversingFilter: match(metadataReader,metadataReaderFactory)
Note right of AbstractTypeHierarchyTraversingFilter: Check the annotation match
AbstractTypeHierarchyTraversingFilter->>AnnotationTypeFilter: matchSelf(metadataReader)
Note right of ClassPathScanningCandidateComponentProvider: Return the bean definition result
ClassPathScanningCandidateComponentProvider-->>ClassPathBeanDefinitionScanner: Return BeanDefinition
Note right of ClassPathBeanDefinitionScanner: Register the bean definition
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner: registerBeanDefinition(definitionHolder,registry)
Note right of ClassPathBeanDefinitionScanner: The helper utility registers the bean
ClassPathBeanDefinitionScanner->>BeanDefinitionReaderUtils: registerBeanDefinition(definitionHolder, registry)
Note right of BeanDefinitionReaderUtils: Register the bean in the bean factory
BeanDefinitionReaderUtils->>DefaultListableBeanFactory: registerBeanDefinition(beanName,beanDefinition)
~~~

### 7. Source Code Analysis

#### @Named Default Filter Registration

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (this class configures the Spring container with Java annotations). We pass the `MyConfiguration` component class as the constructor argument. Then we get a bean of type `MyController` from the Spring context and call its `showService` method.

```java
public class NamedApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyController controller = context.getBean(MyController.class);
        controller.showService();
    }
}
```

The `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` constructor does three steps. This time we focus on `this()`.

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
```

The `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext()` method initializes two core components. One reads bean definitions from annotations (`AnnotatedBeanDefinitionReader`). The other scans the classpath and detects bean components automatically (`ClassPathBeanDefinitionScanner`). This is also the focus of this analysis.

```java
public AnnotationConfigApplicationContext() {
    StartupStep createAnnotatedBeanDefReader = this.getApplicationStartup().start("spring.context.annotated-bean-reader.create");
    this.reader = new AnnotatedBeanDefinitionReader(this);
    createAnnotatedBeanDefReader.end();
    this.scanner = new ClassPathBeanDefinitionScanner(this);
}
```

The `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#ClassPathBeanDefinitionScanner(registry)` method calls another constructor.

```java
public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
    this(registry, true);
}
```

The `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#ClassPathBeanDefinitionScanner(registry, useDefaultFilters)` method calls another constructor.

```java
public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
    this(registry, useDefaultFilters, getOrCreateEnvironment(registry));
}
```

The `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#ClassPathBeanDefinitionScanner(registry, useDefaultFilters,environment)` method calls one more constructor. It does the actual initialization with all these arguments.

```java
public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
			Environment environment) {

    this(registry, useDefaultFilters, environment,
         (registry instanceof ResourceLoader ? (ResourceLoader) registry : null));
}
```

In the `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#ClassPathBeanDefinitionScanner(registry, useDefaultFilters,environment,resourceLoader)` method, when `useDefaultFilters` is `true`, Spring registers the JSR-330 `@Named` annotation as a default filter. It does this besides the usual annotations such as `@Component`, `@Service`, and `@Repository`. This is how Spring supports the JSR-330 injection specification.

```java
public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
			Environment environment, @Nullable ResourceLoader resourceLoader) {

    // ... [code omitted for brevity]
    if (useDefaultFilters) {
        registerDefaultFilters();
    }
    // ... [code omitted for brevity]
}
```

The `org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#registerDefaultFilters` method finds and handles components with the `@Named` annotation, if the annotation is on the classpath. If the JSR-330 library is not on the classpath, Spring skips this step quietly instead of failing. With this, the registration of the default filter for `@Named` is done. The filter is used later, when components are scanned.

```java
protected void registerDefaultFilters() {
    // ... [code omitted for brevity]
    try {
        this.includeFilters.add(new AnnotationTypeFilter(
            ((Class<? extends Annotation>) ClassUtils.forName("javax.inject.Named", cl)), false));
        logger.trace("JSR-330 'javax.inject.Named' annotation found and supported for component scanning");
    }
    catch (ClassNotFoundException ex) {
        // JSR-330 API not available - simply skip.
    }
}
```

#### @Named Component Scan

##### Prerequisites

To fully understand how the `@Named` annotation works in Spring, you need to look at component scanning and bean definition registration. The annotation and interface below give the basic support for finding, parsing, and registering `@Named`. To understand how `@Named` works in Spring, it is important to know these parts.

1. **`@ComponentScan` annotation**
   - The `@ComponentScan` annotation is used on a configuration class. It tells the Spring framework which packages to search for classes with annotations such as `@Component`, `@Service`, `@Repository`, `@Controller`, and `@Named`. The components that are found are registered automatically as beans in the Spring application context. So components with `@Named` are scanned and registered too.
   - 🔗 [@ComponentScan annotation link](https://github.com/xuchengsheng/spring-reading/blob/master/spring-annotation/spring-annotation-componentScan)
2. **`BeanDefinitionRegistryPostProcessor` interface**
   - Its `postProcessBeanDefinitionRegistry` method lets you change or add bean definitions after all other bean definitions are loaded, but before any bean is instantiated. For component scanning, when the `@ComponentScan` annotation is parsed, the matching `BeanDefinitionRegistryPostProcessor` implementation (such as `ConfigurationClassPostProcessor`) is triggered. It handles the component scan and registers the matching bean definitions.
   - 🔗 [BeanDefinitionRegistryPostProcessor interface link](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-beanDefinitionRegistryPostProcessor)

##### Scan Entry

The main goal of the `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan` method is to find all components in the given `basePackages` and create a `BeanDefinition` for each. The Spring container later uses these `BeanDefinition` objects to create the real bean instances.

```java
protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
    // Assert that at least one base package is given
    Assert.notEmpty(basePackages, "At least one base package must be specified");

    // The set that holds the bean definitions that are found
    Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();

    // Go through each base package
    for (String basePackage : basePackages) {
        // Find all candidate bean definitions in the given base package
        Set<BeanDefinition> candidates = findCandidateComponents(basePackage);

        // Go through the bean definitions that were found
        for (BeanDefinition candidate : candidates) {
            // ... [code omitted for brevity]

            // Check if the given bean name already exists. If not, register it
            if (checkCandidate(beanName, candidate)) {
                // ... [code omitted for brevity]
                
                // Register the bean definition in the bean registry
                registerBeanDefinition(definitionHolder, this.registry);
            }
        }
    }
    // Return all registered bean definitions
    return beanDefinitions;
}
```

The `org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents` method has two ways to find components: with a pre-built index (if available and supported), or with the traditional scan. We focus on the traditional scan.

```java
public Set<BeanDefinition> findCandidateComponents(String basePackage) {
    // If a component index exists and it supports include filters
    if (this.componentsIndex != null && indexSupportsIncludeFilters()) {
        // Add the candidate components from the index
        return addCandidateComponentsFromIndex(this.componentsIndex, basePackage);
    } 
    else {
        // Scan the candidate components in the given base package
        return scanCandidateComponents(basePackage);
    }
}
```

The `org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#scanCandidateComponents` method works in these steps. First, it builds the search path used to search the given package on the classpath. Then it scans the classpath to get the matching resources (usually `.class` files). Then, for each resource, it checks whether it is a candidate component, for example whether it has the `@Named` annotation. At the end, for each class that is a candidate component, it creates a `BeanDefinition` object and adds it to the result.

```java
private Set<BeanDefinition> scanCandidateComponents(String basePackage) {
    // Holds the candidate bean definitions
    Set<BeanDefinition> candidates = new LinkedHashSet<>();
    try {
        // Build the package search path, for example: "classpath*:com/example/*"
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
            resolveBasePackage(basePackage) + '/' + this.resourcePattern;

        // Use the pattern resolver to get all matching resources (the .class files)
        Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
        
        // ... [code omitted for brevity]
        
        for (Resource resource : resources) {
            // ... [code omitted for brevity]
            // Check if the resource is readable
            if (resource.isReadable()) {
                try {
                    // Use the metadata reader to get the metadata of the class
                    MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                    // Check if the class is a candidate component (for example, whether it has the @Named annotation)
                    if (isCandidateComponent(metadataReader)) {
                        ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                        sbd.setSource(resource);
                        // Check again if the bean definition is a candidate component
                        if (isCandidateComponent(sbd)) {
                            // ... [code omitted for brevity]
                            candidates.add(sbd);
                        } 
                        else {
                            // ... [code omitted for brevity]
                        }
                    } 
                    else {
                        // ... [code omitted for brevity]
                    }
                }
                catch (Throwable ex) {
                    // ... [code omitted for brevity]
                }
            } 
            else {
                // ... [code omitted for brevity]
            }
        }
    }
    catch (IOException ex) {
        // ... [code omitted for brevity]
    }
    return candidates;
}
```

The `org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#isCandidateComponent` method is a key step in the scan. It decides whether a class should be seen as a candidate for a Spring component. If a class has the `@Named` annotation, the matching `AnnotationTypeFilter` recognizes it, and `isCandidateComponent` returns `true`. This means the class is a valid component candidate.

```java
protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
    // ... [code omitted for brevity]

    // Go through all include filters
    for (TypeFilter tf : this.includeFilters) {
        // If the current class matches any include filter
        if (tf.match(metadataReader, getMetadataReaderFactory())) {
            // Check if the component meets the specific conditions
            return isConditionMatch(metadataReader);
        }
    }

    // Return false by default, which means it is not a candidate component
    return false;
}
```

The `org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter#match(metadataReader, metadataReaderFactory)` method decides whether the given `metadataReader` matches some specific conditions. In this context, it is usually used to check whether a class or its metadata meets some conditions, for example whether a class has the `@Named` annotation.

```java
@Override
public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
    throws IOException {

    // This method optimizes avoiding unnecessary creation of ClassReaders
    // as well as visiting over those readers.
    if (matchSelf(metadataReader)) {
        return true;
    }
    // ... [code omitted for brevity]
    return false;
}
```

In the `org.springframework.core.type.filter.AnnotationTypeFilter#matchSelf` method: when we use component scanning in Spring, one goal is to find and register classes with a specific annotation automatically. The `@Named` annotation is one we care about here. It marks a class and tells Spring to treat it as a bean and add it to the Spring application context.

**Walkthrough with our example:**

+ First, it gets the annotation metadata of the class from `metadataReader`.

+ Then, it checks whether the `MyServiceA` class has the `@Named` annotation directly. This is done with `metadata.hasAnnotation(this.annotationType.getName())`. Here `this.annotationType.getName()` returns `javax.inject.Named` (the fully qualified class name of `@Named`).

+ If `considerMetaAnnotations` is `true`, it also checks whether the `MyServiceA` class has any meta-annotations that are themselves marked with `@Named`.
+ In this case, `MyServiceA` has the `@Named` annotation directly, so `matchSelf` returns `true`. This means the `MyServiceA` class meets the filter condition, and it should be registered as a Spring bean.

```java
@Override
protected boolean matchSelf(MetadataReader metadataReader) {
    AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
    return metadata.hasAnnotation(this.annotationType.getName()) ||
        (this.considerMetaAnnotations && metadata.hasMetaAnnotation(this.annotationType.getName()));
}
```

#### @Named Dependency Injection

In Spring, the `@Named` annotation marks a class as a Spring bean. It also works as a qualifier in dependency injection, to remove ambiguity. Here, `@Named` works much like `@Qualifier`. In fact, in Spring `@Named` is handled as a special kind of `@Qualifier`.

1. **`@Qualifier` annotation**:
   - In Spring, the `@Qualifier` annotation sets the name of the bean you want when there are several beans of the same type. When used together with `@Named` or `@Inject`, it helps the Spring container resolve the right dependency.
   - 🔗 [Qualifier annotation link](https://github.com/xuchengsheng/spring-reading/tree/master/spring-annotation/spring-annotation-qualifier)

### 8. Notes

1. **Difference from @Component**
   + `@Named` and `@Component` are similar in function (both can identify and register beans), but they come from different specifications. `@Component` is specific to Spring, while `@Named` comes from JSR-330.
2. **Use with @Qualifier**
   + `@Named` can be used with the `@Inject` annotation as a replacement for `@Qualifier`, to say which specific bean should be injected.
3. **Do not mix annotation sources**
   + Spring supports both its own dependency injection annotations and JSR-330. So `@Autowired` and `@Inject` may be mixed in one project, which can cause confusion. For consistency, it is better to stick to one of them in a project.
4. **Classpath dependency**
   + To use `@Named` and other JSR-330 annotations, make sure the `javax.inject` library is on the classpath. If you do not add this dependency, using these annotations causes a class-not-found error.
5. **Integration with other JSR-330 annotations**
   + When you use `@Named`, consider using other JSR-330 annotations such as `@Inject` too, to stay consistent.
6. **Avoid name conflicts**
   + When you use `@Named` to give a bean a name, make sure no other bean in the Spring context uses the same name. Otherwise, you may see unpredictable behavior or errors.
7. **Scope**
   + The default scope of `@Named` is singleton. If you need a different scope, such as prototype, use the `@Scope` annotation as well.

### 9. Summary

#### Best Practices Summary

1. **Startup**
   + In `NamedApplication`, we initialize the `AnnotationConfigApplicationContext` context and use `MyConfiguration` as the configuration class. Then we get the `MyController` bean from this context and call its `showService` method.

2. **Configuration class**
   + The `MyConfiguration` class uses `@ComponentScan` to tell Spring to search the "`com.xcs.spring`" package and its sub-packages for components marked with stereotype annotations. So all classes with annotations such as `@Component`, `@Service`, `@Repository`, and `@Controller` are recognized and registered as beans automatically.

3. **Controller**
   + `MyController` is a controller class marked with `@Controller`. It has a dependency of type `MyService`. We use `@Inject` to ask for this dependency to be injected, and `@Named("myServiceB")` to say clearly which bean instance we want.

4. **Service classes**
   + There are three service-related classes: the base class `MyService` and its two subclasses `MyServiceA` and `MyServiceB`. Both subclasses are marked with `@Named`. This gives them clear identifying names in the dependency injection container.

5. **Result**
   + When the program runs, the `@Named("myServiceB")` instruction injects an instance of type `MyServiceB` into `MyController`. So when the `showService` method is called, the output shows that the `MyServiceB` instance was injected correctly.

#### Source Code Analysis Summary

1. **Initialize the context**
   + The entry class `NamedApplication` uses `AnnotationConfigApplicationContext` as the context, registers the configuration class `MyConfiguration`, and gets the bean of type `MyController` from this context.

2. **Annotation configuration class scan**
   + When `AnnotationConfigApplicationContext` of Spring is constructed, it initializes two key components. One reads bean definitions from annotations (`AnnotatedBeanDefinitionReader`). The other scans the classpath and finds bean components automatically (`ClassPathBeanDefinitionScanner`).

3. **Registration of the `@Named` annotation**
   + When `ClassPathBeanDefinitionScanner` is constructed, if `useDefaultFilters` is `true`, Spring adds a default filter for the `@Named` annotation. This way it is recognized during the classpath scan. This is how Spring integrates the JSR-330 specification, so we can use `@Named` to identify components.

4. **`@Named` component scan**

   + When the application runs, Spring scans the given package and its sub-packages for components, as `@ComponentScan` says. During this scan, it checks each class to see whether it has a specific annotation, such as `@Named`.

   + During the component scan, the `isCandidateComponent` method checks whether a class should be seen as a candidate for a Spring component. It decides by applying the registered filters (one of them is the filter for the `@Named` annotation).

5. **`@Named` dependency injection**
   + In Spring, another key use of the `@Named` annotation is as a qualifier in dependency injection. When there are several beans of the same type and we need to say which one to inject, `@Named` helps remove the ambiguity. In this respect, it works like the `@Qualifier` annotation.
