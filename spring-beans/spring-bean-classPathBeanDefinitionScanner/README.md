## ClassPathBeanDefinitionScanner

- [ClassPathBeanDefinitionScanner](#classpathbeandefinitionscanner)
  - [1. Background Knowledge](#1-background-knowledge)
  - [2. Basic Description](#2-basic-description)
  - [3. Main Features](#3-main-features)
  - [4. Best Practices](#4-best-practices)
  - [5. Sequence Diagram](#5-sequence-diagram)
  - [6. Source Code Analysis](#6-source-code-analysis)
    - [Register Default Filters Phase](#register-default-filters-phase)
    - [Scan Bean Definitions Phase](#scan-bean-definitions-phase)
  - [7. Relationship with Other Components](#7-relationship-with-other-components)
  - [8. FAQ](#8-faq)


### 1. Background Knowledge

5. **`Resource`**
   - `Resource` stands for a resource. It can be a file, a file on the classpath, a URL and so on. It gives an abstraction and access methods for resources.
   - [View the Resource interface](https://github.com/xuchengsheng/spring-reading/tree/master/spring-resources/spring-resource)
2. **`ResourceLoader`**
   - `ResourceLoader` gets resources. They can be files, classpath resources, URLs or even remote resources. It gives one uniform way to load resources, wherever they are.
   - [View the ResourceLoader interface](https://github.com/xuchengsheng/spring-reading/tree/master/spring-resources/spring-resource-resourceLoader)
3. **`MetadataReader`**
   + `MetadataReader` is a Spring interface that reads class files and class metadata. It gives information about a class, including the class name, annotations and parent class. In `ClassPathBeanDefinitionScanner`, `MetadataReader` is used to scan the classpath, find candidate bean classes, and read the class metadata to create the matching `BeanDefinition`.
   + [View the MetadataReader interface](https://github.com/xuchengsheng/spring-reading/tree/master/spring-metadata/spring-metadata-metadataReader)
4. **`AnnotationMetadata`**
   - `AnnotationMetadata` is a Spring interface that handles the annotation information on a class. It gives methods to access and work with that information. `AnnotatedBeanDefinitionReader` uses `AnnotationMetadata` to parse the annotations on a class and turn them into a Spring BeanDefinition.
   - [View the AnnotationMetadata interface](https://github.com/xuchengsheng/spring-reading/tree/master/spring-metadata/spring-metadata-annotationMetadata)
5. **`BeanDefinition`**
   - `BeanDefinition` is the core Spring concept that describes and manages bean configuration. It has information about a bean, such as class name, scope, dependencies and initialization method. One of the main tasks of `AnnotatedBeanDefinitionReader` is to turn annotated classes into `BeanDefinition`s and register them in the Spring container.
   - [View the BeanDefinition interface](https://github.com/xuchengsheng/spring-reading/tree/master/spring-beans/spring-bean-beanDefinition)

### 2. Basic Description

The `ClassPathBeanDefinitionScanner` class scans a package and its sub-packages on the classpath. It finds matching classes and registers them as Spring bean definitions. This gives you component scanning and autowiring, so beans in the application are easy to manage and configure. You can define filters to decide which classes become beans. Together with autowiring, it gives dependency injection and makes the application easier to maintain and extend.

### 3. Main Features

1. **Package scanning**
   + Scans the class files in the given package and its sub-packages on the classpath, to find possible Spring bean candidates.
2. **`BeanDefinition` registration**
   + Registers the matching classes as Spring bean definitions (`BeanDefinition`). The Spring container can then manage the instances of these classes.
3. **Autowiring**
   + Works with the autowiring mechanism to find and inject dependent beans automatically. This reduces the need to configure bean dependencies by hand and makes the application easier to maintain.
4. **Annotation detection**
   + The scanner finds classes with specific annotations, such as `@Component`, `@Service` and `@Repository`, and registers them as Spring beans.
5. **Filters**
   + Filters decide which classes are registered as beans. A filter can be an annotation type, a class name rule, an interface that the class implements, and so on. This selects the matching classes.
6. **Bean name generation**
   + Generates the bean name automatically. It is usually the class name with a lowercase first letter, but you can customize it through configuration.
7. **Custom configuration**
   + You can change the behavior of `ClassPathBeanDefinitionScanner` through its properties, for example the packages to scan, the filters and the bean name generation rule.

### 4. Best Practices

This example creates a `DefaultListableBeanFactory` and a linked `ClassPathBeanDefinitionScanner` object. Then the `scan` method of the scanner scans the classes in the given package and registers them as Spring beans. In the end, the beans can be retrieved and used through the factory.

```java
public class ClassPathBeanDefinitionScannerDemo {

    public static void main(String[] args) {
        // Create an AnnotationConfigApplicationContext
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // Create a ClassPathBeanDefinitionScanner and link it to the container
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(factory);

        // Scan beans with the scan method of ClassPathBeanDefinitionScanner
        scanner.scan("com.xcs.spring");

        System.out.println("MyController = " + factory.getBean(MyController.class));
        System.out.println("MyService = " + factory.getBean(MyService.class));
        System.out.println("MyRepository = " + factory.getBean(MyRepository.class));
    }
}
```

`@Controller`, `@Service` and `@Repository`. These annotations usually mark components in different layers, so the Spring container can find them and register them as Spring beans.

```java
@Controller
public class MyController {
}

@Service
public class MyService {
}

@Repository
public class MyRepository {
}
```

The result shows that `ClassPathBeanDefinitionScanner` scanned the classes in the given package and registered them as Spring beans.

```java
MyController = com.xcs.spring.controller.MyController@1e802ef9
MyService = com.xcs.spring.service.MyService@2b6faea6
MyRepository = com.xcs.spring.repository.MyRepository@778d1062
```

### 5. Sequence Diagram

~~~mermaid
sequenceDiagram
Title: ClassPathBeanDefinitionScanner Sequence Diagram

par Register default filters phase
ClassPathBeanDefinitionScannerDemo->>ClassPathBeanDefinitionScanner:new ClassPathBeanDefinitionScanner(registry)
note right of ClassPathBeanDefinitionScannerDemo: Create a ClassPathBeanDefinitionScanner instance and link it to the registry
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner:ClassPathBeanDefinitionScanner(registry,useDefaultFilters)
note over ClassPathBeanDefinitionScanner: Create a ClassPathBeanDefinitionScanner instance and enable the default filters
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner:ClassPathBeanDefinitionScanner(registry,useDefaultFilters,environment)
note over ClassPathBeanDefinitionScanner: Create a ClassPathBeanDefinitionScanner instance and set the environment
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner:ClassPathBeanDefinitionScanner(registry,useDefaultFilters,environment,resourceLoader)
note over ClassPathBeanDefinitionScanner: Create a ClassPathBeanDefinitionScanner instance and set the resource loader
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner:registerDefaultFilters()
note over ClassPathBeanDefinitionScanner: Register the default component filters
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScannerDemo:Return the scanner
note over ClassPathBeanDefinitionScanner,ClassPathBeanDefinitionScannerDemo: Return the ClassPathBeanDefinitionScanner instance
end

par Scan bean definitions phase
ClassPathBeanDefinitionScannerDemo->>ClassPathBeanDefinitionScanner:scan(basePackages)
note over ClassPathBeanDefinitionScannerDemo,ClassPathBeanDefinitionScanner: Run the component scan
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner:doScan(basePackages)
note over ClassPathBeanDefinitionScanner: Run the component scan
ClassPathBeanDefinitionScanner->>ClassPathScanningCandidateComponentProvider:findCandidateComponents(basePackage)
note over ClassPathScanningCandidateComponentProvider,ClassPathBeanDefinitionScanner: Find the matching candidate components
ClassPathScanningCandidateComponentProvider->>ClassPathScanningCandidateComponentProvider:scanCandidateComponents(basePackage)
note over ClassPathScanningCandidateComponentProvider: Scan the candidate components
ClassPathScanningCandidateComponentProvider->>ClassPathScanningCandidateComponentProvider:isCandidateComponent(metadataReader)
note over ClassPathScanningCandidateComponentProvider: Check whether it is a candidate component
ClassPathScanningCandidateComponentProvider->>AbstractTypeHierarchyTraversingFilter:match(metadataReader,metadataReaderFactory)
note over AbstractTypeHierarchyTraversingFilter,ClassPathScanningCandidateComponentProvider: Match with the filters
AbstractTypeHierarchyTraversingFilter->>AnnotationTypeFilter:matchSelf(metadataReader)
note left of AnnotationTypeFilter: Call matchSelf(metadataReader)
AnnotationTypeFilter->>AbstractTypeHierarchyTraversingFilter:Return whether it matches
AbstractTypeHierarchyTraversingFilter->>ClassPathScanningCandidateComponentProvider:Return whether it matches
ClassPathScanningCandidateComponentProvider->>ClassPathScanningCandidateComponentProvider:candidates.add(sbd)
note over ClassPathScanningCandidateComponentProvider: If it matches, add the candidate component to the list
ClassPathScanningCandidateComponentProvider->>ClassPathBeanDefinitionScanner:Return the scanned bean definitions
ClassPathBeanDefinitionScanner->>AnnotationConfigUtils:processCommonDefinitionAnnotations(abd)
Note over ClassPathBeanDefinitionScanner, AnnotationConfigUtils: Handle the common bean definition annotations
AnnotationConfigUtils->>AnnotationConfigUtils:processCommonDefinitionAnnotations(abd,metadata)
Note over AnnotationConfigUtils: Handle the metadata of the bean
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner:checkCandidate(beanName, candidate)
note over ClassPathBeanDefinitionScanner: Check the candidate component
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner:registerBeanDefinition(definitionHolder,registry)
note over ClassPathBeanDefinitionScanner: Register the bean definition
ClassPathBeanDefinitionScanner->>AnnotationConfigUtils:registerBeanDefinition(definitionHolder,registry)
note over ClassPathBeanDefinitionScanner,AnnotationConfigUtils: Register the bean definition
ClassPathBeanDefinitionScanner->>AnnotationConfigUtils:registerAnnotationConfigProcessors(registry)
note over ClassPathBeanDefinitionScanner,AnnotationConfigUtils: Enable annotation-driven configuration
AnnotationConfigUtils->>AnnotationConfigUtils:registerAnnotationConfigProcessors(registry,source)
note over AnnotationConfigUtils: Enable annotation-driven configuration
end

~~~



### 6. Source Code Analysis

#### Register Default Filters Phase

The `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#ClassPathBeanDefinitionScanner(registry)` constructor delegates to another constructor. The second argument is set to `true`. This means the annotation config processors (`Annotation Config Processors`) are registered, which is usually done to support annotation configuration, including annotations such as `@Component`, `@Service` and `@Repository`.

```java
public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
    this(registry, true);
}
```

The `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#ClassPathBeanDefinitionScanner(registry,useDefaultFilters)` constructor delegates to another constructor and passes the environment of the context.

```java
public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
	this(registry, useDefaultFilters, getOrCreateEnvironment(registry));
}
```

The `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#ClassPathBeanDefinitionScanner(registry,useDefaultFilters,environment)` constructor delegates to another constructor. The last argument is a `ResourceLoader`. If the `BeanDefinitionRegistry` is also an instance of `ResourceLoader`, it is passed on to the next constructor.

```java
public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
			Environment environment) {
    this(registry, useDefaultFilters, environment,
         (registry instanceof ResourceLoader ? (ResourceLoader) registry : null));
}
```

In the `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#ClassPathBeanDefinitionScanner(registry,useDefaultFilters,environment,resourceLoader)` constructor, we focus on the `registerDefaultFilters()` method. It registers the default filters, which are used to scan the default annotated classes, such as `@Component`, `@Service` and `@Repository`.

```java
public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
			Environment environment, @Nullable ResourceLoader resourceLoader) {
    // Make sure the BeanDefinitionRegistry is not null
    Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
    this.registry = registry;

    // If the default filters are needed, register them
    if (useDefaultFilters) {
        registerDefaultFilters();
    }

    // Set the environment
    setEnvironment(environment);
    
    // Set the resource loader (ResourceLoader)
    setResourceLoader(resourceLoader);
}
```

The main goal of the `org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#registerDefaultFilters` method is to register the `@Component` annotation, which is used to scan and find classes marked as components. It also supports some annotations from the Java EE specifications.

```java
protected void registerDefaultFilters() {
    // Add the default filter to the includeFilters collection
    this.includeFilters.add(new AnnotationTypeFilter(Component.class));
    ClassLoader cl = ClassPathScanningCandidateComponentProvider.class.getClassLoader();

    // Try to register the JSR-250 'javax.annotation.ManagedBean' filter (if available)
    try {
        this.includeFilters.add(new AnnotationTypeFilter(
            ((Class<? extends Annotation>) ClassUtils.forName("javax.annotation.ManagedBean", cl)), false));
        logger.trace("JSR-250 'javax.annotation.ManagedBean' found and supported for component scanning");
    }
    catch (ClassNotFoundException ex) {
        // If the JSR-250 1.1 API (included in Java EE 6) is not available, simply skip it.
    }

    // Try to register the JSR-330 'javax.inject.Named' filter (if available)
    try {
        this.includeFilters.add(new AnnotationTypeFilter(
            ((Class<? extends Annotation>) ClassUtils.forName("javax.inject.Named", cl)), false));
        logger.trace("JSR-330 'javax.inject.Named' annotation found and supported for component scanning");
    }
    catch (ClassNotFoundException ex) {
        // If the JSR-330 API is not available, simply skip it.
    }
}
```

#### Scan Bean Definitions Phase

The `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#scan` method calls `doScan` to scan the given packages, find matching classes and register them as `BeanDefinition`s. It also computes the difference between the new `BeanDefinition` count and the count before the scan.

```java
public int scan(String... basePackages) {
    // Record the number of BeanDefinitions when the scan starts
    int beanCountAtScanStart = this.registry.getBeanDefinitionCount();

    // Step 1: Run the scan
    doScan(basePackages);

    // Step 2: Register the annotation config processors (if needed)
    if (this.includeAnnotationConfig) {
        AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
    }

    // Return the difference between the BeanDefinition count after the scan and the count before it, which is the number of new BeanDefinitions registered during the scan
    return (this.registry.getBeanDefinitionCount() - beanCountAtScanStart);
}
```

> [Step 1 of `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#scan`]

The `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan` method finds the matching classes, processes them, and registers them as Spring beans.

```java
protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
    Assert.notEmpty(basePackages, "At least one base package must be specified");
    Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
    for (String basePackage : basePackages) {
        // Step 1: Find the candidate BeanDefinitions that match
        Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
        for (BeanDefinition candidate : candidates) {
            
            // Step 2: Resolve the scope information of the bean
            ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
            candidate.setScope(scopeMetadata.getScopeName());
            
            // Step 3: Generate the name of the bean
            String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
            
            // Step 4: Process the BeanDefinition further
            if (candidate instanceof AbstractBeanDefinition) {
                postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
            }
            
            // Step 5: Handle the common annotations
            if (candidate instanceof AnnotatedBeanDefinition) {
                AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
            }
            
            // Step 6: Register the BeanDefinition
            if (checkCandidate(beanName, candidate)) {
                // Create a BeanDefinitionHolder and register it as a BeanDefinition
                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                // Handle the scope proxy mode of the bean
                definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
                beanDefinitions.add(definitionHolder);
                // Step 6.1 Register the BeanDefinition in the BeanDefinitionRegistry
                registerBeanDefinition(definitionHolder, this.registry);
            }
        }
    }
    return beanDefinitions;
}
```

> [Step 1 of `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan`]

The `org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents` method decides whether to get the candidate `BeanDefinition`s from the component index or to run the standard package scan.

```java
public Set<BeanDefinition> findCandidateComponents(String basePackage) {
    if (this.componentsIndex != null && indexSupportsIncludeFilters()) {
        // If a component index exists and it supports the include filters
        return addCandidateComponentsFromIndex(this.componentsIndex, basePackage);
    }
    else {
        // Otherwise, run the standard scan
        return scanCandidateComponents(basePackage);
    }
}
```

The `org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#scanCandidateComponents` method scans the classes under the given package path and finds the candidate `BeanDefinition`s. It contains the logic for file scanning, reading metadata and filtering candidate classes.

```java
private Set<BeanDefinition> scanCandidateComponents(String basePackage) {
    // Create a collection to hold the candidate BeanDefinitions
    Set<BeanDefinition> candidates = new LinkedHashSet<>();
    try {
        // Build the package search path from the classpath prefix and the resolved base package path
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
            resolveBasePackage(basePackage) + '/' + this.resourcePattern;
        // Get the array of resources from the resource pattern resolver
        Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
        // Check whether trace and debug logging are enabled
        boolean traceEnabled = logger.isTraceEnabled();
        boolean debugEnabled = logger.isDebugEnabled();
        // Loop over the resource array to find candidate BeanDefinitions
        for (Resource resource : resources) {
            // ... [code omitted for brevity]
            if (resource.isReadable()) {
                try {
                    // Get the metadata reader of the resource
                    MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                    if (isCandidateComponent(metadataReader)) {
                        // If it is a candidate component class
                        ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                        sbd.setSource(resource);
                        // If it is a candidate component class, add it to the candidate BeanDefinition collection
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
    // Return the collection of candidate BeanDefinitions that were found
    return candidates;
}
```

The `org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#isCandidateComponent(metadataReader)` method is used during component scanning. By the filter rules and condition checks, it decides whether the given class is a candidate component class, and so whether it is registered as a `Spring` `BeanDefinition`.

```java
protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
    // Loop over the exclude filters
    for (TypeFilter tf : this.excludeFilters) {
        // If any exclude filter matches, mark it as not a candidate component
        if (tf.match(metadataReader, getMetadataReaderFactory())) {
            return false;
        }
    }
    
    // Loop over the include filters
    for (TypeFilter tf : this.includeFilters) {
        // If any include filter matches
        if (tf.match(metadataReader, getMetadataReaderFactory())) {
            // Check whether the conditions are met (usually for further condition checks)
            return isConditionMatch(metadataReader);
        }
    }
    
    // By default, it is not a candidate component
    return false;
}
```

The `org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter#match(metadataReader, metadataReaderFactory)` method is used during filtering. It checks whether the given `MetadataReader` should match.

```java
@Override
public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
    throws IOException {

    // Optimization: first check whether the current metadataReader can be matched directly
    if (matchSelf(metadataReader)) {
        return true;
    }
    // Get the class metadata
    ClassMetadata metadata = metadataReader.getClassMetadata();
    
    // Check whether the class name matches
    if (matchClassName(metadata.getClassName())) {
        return true;
    }

    // ... [code omitted for brevity]

    // If no condition matches, return false, which means no match
    return false;
}
```

The `org.springframework.core.type.filter.AnnotationTypeFilter#matchSelf` method checks whether the given `metadataReader` meets a specific matching condition. If the `metadataReader` is a class that is marked with the `@Component` annotation, `matchSelf` checks whether it matches the `@Component` annotation or its meta-annotations.

```java
@Override
protected boolean matchSelf(MetadataReader metadataReader) {
    // Get the annotation metadata
    AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
    
    // Check whether the given annotation or its meta-annotations are present
    return metadata.hasAnnotation(this.annotationType.getName()) ||
        (this.considerMetaAnnotations && metadata.hasMetaAnnotation(this.annotationType.getName()));
}
```

> [Step 2 of `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan`]

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

> [Step 6 of `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan`]

The `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#checkCandidate` method makes sure that no two bean definitions with the same name are registered, or that, if the names are the same, they are compatible. If they are not compatible, it throws an exception to stop conflicting bean definitions from being registered.

```java
protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
    if (!this.registry.containsBeanDefinition(beanName)) {
        // If the registry has no bean definition with the same name, return true, which means the candidate bean can be registered.
        return true;
    }
    BeanDefinition existingDef = this.registry.getBeanDefinition(beanName);
    BeanDefinition originatingDef = existingDef.getOriginatingBeanDefinition();
    if (originatingDef != null) {
        existingDef = originatingDef;
    }
    if (isCompatible(beanDefinition, existingDef)) {
        // If the candidate bean definition is compatible with the existing one, return false, which means the existing bean definition does not need to be overridden.
        return false;
    }
    throw new ConflictingBeanDefinitionException("Annotation-specified bean name '" + beanName +
                                                 "' for bean class [" + beanDefinition.getBeanClassName() + "] conflicts with existing, " +
                                                 "non-compatible bean definition of same name and class [" + existingDef.getBeanClassName() + "]");
    // If the candidate bean definition is not compatible with the existing one, throw an exception to stop conflicting bean definitions from being registered.
}
```

> [Step 6.1 of `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan`]

The `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#registerBeanDefinition` method calls another method.

```java
protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
    BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
}
```

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

> [Step 2 of `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#scan`]

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

### 7. Relationship with Other Components

1. **`BeanDefinitionLoader`** 
   + The `BeanDefinitionLoader` in Spring Boot loads bean definitions. It is usually used with `ClassPathBeanDefinitionScanner` to support auto-configuration.
2. **`AnnotationConfigApplicationContext`** 
   + This is `AnnotationConfigApplicationContext` of the Spring Framework. It configures the Spring application context and supports annotation-driven configuration. Internally, it may use `ClassPathBeanDefinitionScanner` to scan and register components.
3. **`ComponentScanAnnotationParser` &`ComponentScanBeanDefinitionParser`**
   + These classes usually parse the `@ComponentScan` annotation to find out which packages and components to scan. They may use `ClassPathBeanDefinitionScanner` to run the scan.
4. **`AnnotationConfigWebApplicationContext`** 
   + This is `AnnotationConfigWebApplicationContext` in Spring web applications. It is usually used to configure the Spring context of a web application. It can use `ClassPathBeanDefinitionScanner` for component scanning.

### 8. FAQ

1. **What is `ClassPathBeanDefinitionScanner`?**
   - It runs component scanning and registration. It finds the classes marked as components on the classpath of the application (such as classes with `@Component`, `@Service` or `@Repository`) and registers them as Spring beans.
2. **What are the main features of `ClassPathBeanDefinitionScanner`?**
   - It scans the classes in the given packages, finds classes with specific annotations, registers them as Spring beans, and supports autowiring. It also supports custom filters, to select the classes to scan and register for your application.
3. **How do I use `ClassPathBeanDefinitionScanner` in Spring configuration?**
   - Usually, `ClassPathBeanDefinitionScanner` is not used directly in configuration files. It is used implicitly by Spring annotation-driven configuration or the Spring Boot auto-configuration mechanism. A configuration class uses the `@ComponentScan` annotation to enable component scanning, and `ClassPathBeanDefinitionScanner` is used internally.
4. **How do I customize the behavior of `ClassPathBeanDefinitionScanner`?**
   - Create a custom component scanner that extends `ClassPathBeanDefinitionScanner`, and use it to override the default scanning behavior. This lets us customize the scan filters, the scan paths and so on.
5. **Does `ClassPathBeanDefinitionScanner` support custom annotations?**
   - Yes. You can configure `ClassPathBeanDefinitionScanner` to scan classes with custom annotations. With include and exclude filters, we can set which custom annotation types to scan.
6. **Does `ClassPathBeanDefinitionScanner` support excluding specific classes?**
   - Yes. Use the filters of `ClassPathBeanDefinitionScanner` to exclude specific classes, so they are not scanned and registered as Spring beans.
7. **Does `ClassPathBeanDefinitionScanner` support excluding specific packages?**
   - Yes. Use the filters of `ClassPathBeanDefinitionScanner` to exclude the classes in specific packages, so they are not scanned and registered as Spring beans.
8. **How do I use `ClassPathBeanDefinitionScanner` in Spring Boot?**
   - In a Spring Boot application, you usually do not need to use `ClassPathBeanDefinitionScanner` directly. Spring Boot uses the auto-configuration mechanism to scan and register components. To customize component scanning, use the `@ComponentScan` annotation in a configuration class to set the packages to scan.
9. **Does `ClassPathBeanDefinitionScanner` support excluding specific annotations?**
   - Yes. Use the filters of `ClassPathBeanDefinitionScanner` to exclude classes with specific annotations, so they are not scanned and registered as Spring beans.