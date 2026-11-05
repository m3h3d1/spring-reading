## @ComponentScan

- [@ComponentScan](#componentscan)
  - [1. Overview](#1-overview)
  - [2. Annotation Description](#2-annotation-description)
  - [3. Annotation Source](#3-annotation-source)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
  - [7. Source Code Analysis](#7-source-code-analysis)
  - [8. Notes](#8-notes)
  - [9. Summary](#9-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/132346179) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [@ComponentScan source](https://github.com/xuchengsheng/spring-reading/blob/master/spring-annotation/spring-annotation-componentScan/README.md)

### 2. Annotation Description

The `@ComponentScan` annotation scans given packages (and their sub-packages) for components. It registers them as beans in the Spring container. In Spring Boot, the main application package and its sub-packages are scanned by default. If you need finer control, or you use plain Spring instead of Spring Boot, you can use `@ComponentScan` explicitly.

### 3. Annotation Source

`@ComponentScan` is a core annotation added in Spring 3.1. It tells Spring how to scan for components. It is used together with `@Configuration`, and works like `<context:component-scan>` in Spring XML. You can set the packages to scan. It also has other attributes, such as the name generator, the scope resolver, and the proxy mode, to control scanning and registration in detail. If you set no package, scanning starts from the package of the annotated class. The nested `@Filter` annotation defines a type filter. It is used in `@ComponentScan` to include or exclude components by type, class, or pattern.

```java
/**
 * Configures component scanning directives for use with @Configuration classes.
 * Provides support similar to Spring XML's <context:component-scan> element.
 *
 * Either #basePackageClasses or #basePackages (or its alias
 * #value } ) may be specified to define specific packages to scan. If specific
 * packages are not defined, scanning will occur from the package of the
 * class that declares this annotation.
 *
 * Note that the <context:component-scan> element has an
 * annotation-config attribute; however, this annotation does not. This is because
 * in almost all cases when using @ComponentScan, default annotation config
 * processing (e.g. processing @Autowired and friends) is assumed. Furthermore,
 * when using AnnotationConfigApplicationContext, annotation config processors are
 * always registered, meaning that any attempt to disable them at the
 * @ComponentScan level would be ignored.
 *
 * See the Javadoc for Configuration @Configuration for usage examples.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.1
 * @see Configuration
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(ComponentScans.class)
public @interface ComponentScan {
    
    /**
	 * Alias for #basePackages.
	 * Allows for more concise annotation declarations if no other attributes
	 * are needed, for example, @ComponentScan("org.my.pkg")
	 * instead of @ComponentScan(basePackages = "org.my.pkg").
	 */
	@AliasFor("basePackages")
	String[] value() default {};

	/**
	 * Base packages to scan for annotated components.
	 * #value is an alias for (and mutually exclusive with) this attribute.
	 * Use #basePackageClasses for a type-safe alternative to
	 * String-based package names. The package of each specified class is scanned.
	 */
	@AliasFor("value")
	String[] basePackages() default {};

	/**
	 * Type-safe alternative to #basePackages for specifying the packages
	 * to scan. The package of each class specified will be scanned.
	 * Consider creating a special no-op marker class or interface in each package
	 * that serves no purpose other than being referenced by this attribute.
	 */
	Class<?>[] basePackageClasses() default {};
    
    /**
	 * The BeanNameGenerator class to be used for naming detected components
	 * within the Spring container.
	 * The default value of the BeanNameGenerator interface itself indicates
	 * that the scanner used to process this @ComponentScan annotation should
	 * use its inherited bean name generator, e.g. the default
	 * AnnotationBeanNameGenerator or any custom instance supplied to the
	 * application context at bootstrap time.
	 */
	Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

	/**
	 * The ScopeMetadataResolver to be used for resolving the scope of detected components.
	 */
	Class<? extends ScopeMetadataResolver> scopeResolver() default AnnotationScopeMetadataResolver.class;

	/**
	 * Indicates whether proxies should be generated for detected components, which may be
	 * necessary when using scopes in a proxy-style fashion.
	 * The default is to defer to the default behavior of the component scanner used to
	 * execute the actual scan.
	 * Note that setting this attribute overrides any value set for #scopeResolver.
	 */
	ScopedProxyMode scopedProxy() default ScopedProxyMode.DEFAULT;

	/**
	 * Controls the class files eligible for component detection.
	 * Consider use of #includeFilters and #excludeFilters
	 * for a more flexible approach.
	 */
	String resourcePattern() default ClassPathScanningCandidateComponentProvider.DEFAULT_RESOURCE_PATTERN;

	/**
	 * Indicates whether automatic detection of classes annotated with @Component @Repository, @Service, or @Controller
	 * should be enabled.
	 */
	boolean useDefaultFilters() default true;

	/**
	 * Specifies which types are eligible for component scanning.
	 * Further narrows the set of candidate components from everything in #basePackages
	 * to everything in the base packages that matches the given filter or filters.
	 * Note that these filters will be applied in addition to the default filters, if specified. Any type under the specified
	 * base packages which matches a given filter will be included, even if it does not match the default filters (i.e. is not annotated with @Component).
	 */
	Filter[] includeFilters() default {};

	/**
	 * Specifies which types are not eligible for component scanning.
	 */
	Filter[] excludeFilters() default {};

	/**
	 * Specify whether scanned beans should be registered for lazy initialization.
	 * Default is false; switch this to true when desired.
	 */
	boolean lazyInit() default false;

	/**
	 * Declares the type filter to be used as an ComponentScan#includeFilters include filter or
	 * ComponentScan#excludeFilters exclude filter.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({})
	@interface Filter {

		/**
		 * The type of filter to use.
		 * Default is FilterType#ANNOTATION.
		 * @see #classes
		 * @see #pattern
		 */
		FilterType type() default FilterType.ANNOTATION;

		/**
		 * Alias for #classes.
		 */
		@AliasFor("classes")
		Class<?>[] value() default {};

		/**
		 * The class or classes to use as the filter.
		 * The following table explains how the classes will be interpreted
		 * based on the configured value of the #type attribute.
		 * ...
		 * This part contains a table and other details. It is omitted here.
		 * ...
		 */
		@AliasFor("value")
		Class<?>[] classes() default {};

		/**
		 * The pattern (or patterns) to use for the filter, as an alternative
		 * to specifying a Class #value.
		 * If #type is set to FilterType#ASPECTJ ASPECTJ, this is an AspectJ type pattern expression.
		 * If #type is set to FilterType#REGEX REGEX, this is a regex pattern for the fully-qualified class names to match.
		 */
		String[] pattern() default {};

	}
}

```

`ScopedProxyMode` is an enum. It defines the proxy options for scoped beans. Scoped proxies are an advanced Spring feature. They let you use beans from a narrower scope, such as request or session, in other beans. This enum offers different proxy mechanisms for these beans.

```java
/**
 * Enumerates the various scoped-proxy options.
 *
 * For a more complete discussion of exactly what a scoped proxy is, see the
 * section of the Spring reference documentation entitled 'Scoped beans as dependencies'.
 *
 * @author Mark Fisher
 * @since 2.5
 * @see ScopeMetadata
 */
public enum ScopedProxyMode {

	/**
	 * Default typically equals #NO, unless a different default
	 * has been configured at the component-scan instruction level.
	 */
	DEFAULT,

	/**
	 * Do not create a scoped proxy.
	 * <p>This proxy-mode is not typically useful when used with a
	 * non-singleton scoped instance, which should favor the use of the
	 * #INTERFACES or #TARGET_CLASS proxy-modes instead if it
	 * is to be used as a dependency.
	 */
	NO,

	/**
	 * Create a JDK dynamic proxy implementing all interfaces exposed by
	 * the class of the target object.
	 */
	INTERFACES,

	/**
	 * Create a class-based proxy (uses CGLIB).
	 */
	TARGET_CLASS

}
```

`FilterType` is an enum. It defines the filter options used with `@ComponentScan`. These filters decide which components are included or excluded during scanning.

```java
/**
 * Enumeration of the type filters that may be used in conjunction with
 * ComponentScan @ComponentScan.
 * This enum defines the different types that can be used to filter components
 * during component scanning.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 2.5
 * @see ComponentScan
 * @see ComponentScan#includeFilters()
 * @see ComponentScan#excludeFilters()
 * @see org.springframework.core.type.filter.TypeFilter
 */
public enum FilterType {

	/**
	 * Filter candidates marked with a given annotation.
	 * @see org.springframework.core.type.filter.AnnotationTypeFilter
	 */
	ANNOTATION,

	/**
	 * Filter candidates assignable to a given type.
	 * @see org.springframework.core.type.filter.AssignableTypeFilter
	 */
	ASSIGNABLE_TYPE,

	/**
	 * Filter candidates matching a given AspectJ type pattern expression.
	 * @see org.springframework.core.type.filter.AspectJTypeFilter
	 */
	ASPECTJ,

	/**
	 * Filter candidates matching a given regex pattern.
	 * @see org.springframework.core.type.filter.RegexPatternTypeFilter
	 */
	REGEX,

	/**
	 * Filter candidates using a given custom
	 * org.springframework.core.type.filter.TypeFilter implementation.
	 */
	CUSTOM

}
```

### 4. Main Features

1. **Specify packages to scan**
   + With the `basePackages` and `basePackageClasses` attributes, you tell Spring which packages to search for classes annotated with `@Component`, `@Service`, `@Repository`, `@Controller`, and so on.

2. **Automatic scanning**
   + If you do not set a package, Spring scans from the package of the class that declares `@ComponentScan`.

3. **Filter scanned components**
   + With the `includeFilters` and `excludeFilters` attributes, you control which components are scanned or excluded.

4. **Other settings**
   + The annotation has more attributes for advanced setups. For example, `nameGenerator` names the detected components, `scopeResolver` resolves their scope, and `scopedProxy` decides whether to generate proxies.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). We pass the `MyConfiguration` class as the constructor argument. After the context starts, the program prints the names of all beans defined in the Spring container.

```java
public class ComponentScanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

In the `MyConfiguration` class, Spring scans the `com.xcs.spring` package and its sub-packages. It includes all components of type `SpecialComponent` and excludes all components of type `AdminService`.

```java
@Configuration
@ComponentScan(
        basePackages = "com.xcs.spring",
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SpecialComponent.class),
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AdminService.class)
)
public class MyConfiguration {

}
```

The `UserRepository` class is in the `com.xcs.spring.repository` package. It is marked with `@Repository`.

```java
package com.xcs.spring.repository;

@Repository
public class UserRepository {

}
```

`AdminService` and `UserService` are both in the `com.xcs.spring.service` package. Each is marked with `@Service`.

```java
package com.xcs.spring.service;

@Service
public class AdminService {

}

@Service
public class UserService {

}
```

The `SpecialComponent` class is in the `com.xcs.spring.special` package. It has no Spring annotation.

```java
package com.xcs.spring.special;

public class SpecialComponent {
    
}
```

Here is what the run shows. `UserRepository` is detected and registered as a Spring bean, because it is under the `com.xcs.spring` package we set. `UserService` is registered for the same reason. `AdminService` is also under `com.xcs.spring`, but the `@ComponentScan` configuration lists it in `excludeFilters`, so it is not registered. `SpecialComponent` is a plain Java class with no Spring annotation. But with `includeFilters` and `FilterType.ASSIGNABLE_TYPE`, we make Spring scan it and register it as a bean. It works even though the class has no `@Component` or other Spring annotation.

```java
beanName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanName = org.springframework.context.event.internalEventListenerProcessor
beanName = org.springframework.context.event.internalEventListenerFactory
beanName = myConfiguration
beanName = userRepository
beanName = userService
beanName = specialComponent
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
Title: @ComponentScan annotation sequence diagram
ComponentScanApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)
AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()
AbstractApplicationContext->>AbstractApplicationContext:invokeBeanFactoryPostProcessors(beanFactory)
AbstractApplicationContext->>PostProcessorRegistrationDelegate:invokeBeanFactoryPostProcessors(beanFactory,beanFactoryPostProcessors)
PostProcessorRegistrationDelegate->>PostProcessorRegistrationDelegate:invokeBeanDefinitionRegistryPostProcessors(postProcessors,registry,applicationStartup) 
PostProcessorRegistrationDelegate->>ConfigurationClassPostProcessor:postProcessBeanDefinitionRegistry(registry)
ConfigurationClassPostProcessor->>ConfigurationClassPostProcessor:processConfigBeanDefinitions(registry)
ConfigurationClassPostProcessor->>ConfigurationClassParser:ConfigurationClassParser(...)
ConfigurationClassParser-->>ConfigurationClassPostProcessor:Return the parser
ConfigurationClassPostProcessor->>ConfigurationClassParser:parser.parse(candidates)
ConfigurationClassParser->>ConfigurationClassParser:parse(metadata, String beanName)
ConfigurationClassParser->>ConfigurationClassParser:processConfigurationClass(configClass,filter)
ConfigurationClassParser->>ConfigurationClassParser:doProcessConfigurationClass(configClass,sourceClass,filter)
ConfigurationClassParser->>ComponentScanAnnotationParser:parse(componentScan,declaringClass)
ComponentScanAnnotationParser->>ClassPathBeanDefinitionScanner:ClassPathBeanDefinitionScanner(registry,useDefaultFilters,environment,resourceLoader) 
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner:registerDefaultFilters()
ClassPathBeanDefinitionScanner-->>ComponentScanAnnotationParser:Return the scanner
ComponentScanAnnotationParser->>ClassPathBeanDefinitionScanner:doScan(basePackages)
ClassPathBeanDefinitionScanner->>ClassPathScanningCandidateComponentProvider:findCandidateComponents(basePackage)
ClassPathScanningCandidateComponentProvider->>ClassPathScanningCandidateComponentProvider:scanCandidateComponents(basePackage)
ClassPathScanningCandidateComponentProvider-->>ClassPathBeanDefinitionScanner:Return BeanDefinition
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner:registerBeanDefinition(definitionHolder,registry)
ClassPathBeanDefinitionScanner->>BeanDefinitionReaderUtils:registerBeanDefinition(definitionHolder, registry)
BeanDefinitionReaderUtils->>DefaultListableBeanFactory:registerBeanDefinition(beanName,beanDefinition)
ClassPathBeanDefinitionScanner-->>ComponentScanAnnotationParser:Return BeanDefinition
~~~

### 7. Source Code Analysis

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). We pass the `MyConfiguration` class as the constructor argument. After the context starts, the program prints the names of all beans defined in the Spring container.

```java
public class ComponentScanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
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

In the `org.springframework.context.support.AbstractApplicationContext#refresh` method, we focus on `invokeBeanFactoryPostProcessors(beanFactory)`. Other methods are not the focus of this article, so we skip them.

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
     // ... [code omitted for brevity]
     // Invoke factory processors registered as beans in the context.
     invokeBeanFactoryPostProcessors(beanFactory);
     // ... [code omitted for brevity]
}
```

The `org.springframework.context.support.AbstractApplicationContext#invokeBeanFactoryPostProcessors` method hands the work to `PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors()`.

```java
protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
    // ... [code omitted for brevity]
}
```

In the `org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` method, it first calls the `BeanDefinitionRegistryPostProcessor` instances. This is a sub-interface of `BeanFactoryPostProcessor`. It lets you change bean definitions before any other bean definition is loaded.

```java
public static void invokeBeanFactoryPostProcessors(
        ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
    // ... [code omitted for brevity]
    invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
	// ... [code omitted for brevity]
}
```

The `org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors` method loops over the processors. It calls `postProcessBeanDefinitionRegistry(registry)` on each `BeanDefinitionRegistryPostProcessor`.

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

The `org.springframework.context.annotation.ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry` method calls `processConfigBeanDefinitions`. That method processes and registers the beans defined in configuration classes.

```java
@Override
public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
    // ... [code omitted for brevity]
    processConfigBeanDefinitions(registry);
}
```

The `org.springframework.context.annotation.ConfigurationClassPostProcessor#processConfigBeanDefinitions` method parses and validates the configuration classes. It makes sure all beans defined in them are registered in Spring's bean definition registry.

```java
public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
    // ... [code omitted for brevity]

    // Step 1: create a parser for the configuration classes
    ConfigurationClassParser parser = new ConfigurationClassParser(
            this.metadataReaderFactory, this.problemReporter, this.environment,
            this.resourceLoader, this.componentScanBeanNameGenerator, registry);

    // Step 2: initialize the set of candidate configuration classes and the set of parsed ones
    Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
    Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());

    // Step 3: process all candidate configuration classes until there are none left
    do {
        // Step 3.1: parse the configuration classes
        parser.parse(candidates);
        // Step 3.2: validate the configuration classes
        parser.validate();

        // Get the parsed configuration classes and remove those already processed
        Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());
        configClasses.removeAll(alreadyParsed);

        // Step 4: if the reader is null, create a new bean definition reader
        if (this.reader == null) {
            this.reader = new ConfigurationClassBeanDefinitionReader(
                    registry, this.sourceExtractor, this.resourceLoader, this.environment,
                    this.importBeanNameGenerator, parser.getImportRegistry());
        }

        // Step 5: use the reader to load bean definitions for the parsed configuration classes
        this.reader.loadBeanDefinitions(configClasses);

        // ... [code omitted for brevity]
        
    } while (!candidates.isEmpty());

    // ... [code omitted for brevity]
}
```

The `org.springframework.context.annotation.ConfigurationClassParser#parse` method loops over all configuration class candidates. It parses each annotated bean definition. This usually means finding the @Bean methods and component scan directives in the class, and registering that information in the Spring container.

```java
public void parse(Set<BeanDefinitionHolder> configCandidates) {
    // ... [code omitted for brevity]
    parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
    // ... [code omitted for brevity]
}
```

The `org.springframework.context.annotation.ConfigurationClassParser#parse(metadata, beanName)` method turns the annotation metadata and the bean name into a configuration class, then processes it. Processing configuration classes is the core of configuration-driven Spring. It handles key things such as the `@ComponentScan` annotation.

```java
protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
    processConfigurationClass(new ConfigurationClass(metadata, beanName), DEFAULT_EXCLUSION_FILTER);
}
```

The `org.springframework.context.annotation.ConfigurationClassParser#processConfigurationClass` method processes one configuration class. It first processes the class and its superclasses recursively, so all related configuration is read and parsed. After that, it adds the configuration class to the map of parsed configuration classes.

```java
protected void processConfigurationClass(ConfigurationClass configClass, Predicate<String> filter) throws IOException {
    // ... [code omitted for brevity]

    // Step 1: process the configuration class and its superclass hierarchy recursively
    SourceClass sourceClass = asSourceClass(configClass, filter);
    do {
        sourceClass = doProcessConfigurationClass(configClass, sourceClass, filter);
    } while (sourceClass != null);

    // Step 2: put the processed configuration class into the map
    this.configurationClasses.put(configClass, configClass);
}
```

The `org.springframework.context.annotation.ConfigurationClassParser#doProcessConfigurationClass` method processes and parses classes marked with `@Configuration`. It runs the component scan and makes sure all related configuration classes are parsed recursively.

```java
@Nullable
protected final SourceClass doProcessConfigurationClass(
    ConfigurationClass configClass, SourceClass sourceClass, Predicate<String> filter)
    throws IOException {
    
    // ... [code omitted for brevity]

    // Process any @ComponentScan annotations
    // Get the attributes of all @ComponentScan and @ComponentScans annotations on the current class (sourceClass)
    Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(
        sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class);

    // If @ComponentScan or @ComponentScans is present and the class is not excluded by condition evaluation
    if (!componentScans.isEmpty() &&
        !this.conditionEvaluator.shouldSkip(sourceClass.getMetadata(), ConfigurationPhase.REGISTER_BEAN)) {
        // Loop over each @ComponentScan annotation
        for (AnnotationAttributes componentScan : componentScans) {
            // Scan immediately for the configuration class marked with @ComponentScan
            Set<BeanDefinitionHolder> scannedBeanDefinitions =
                this.componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName());

            // Check the scanned definitions for further configuration classes, and parse them recursively if needed
            for (BeanDefinitionHolder holder : scannedBeanDefinitions) {
                BeanDefinition bdCand = holder.getBeanDefinition().getOriginatingBeanDefinition();
                if (bdCand == null) {
                    bdCand = holder.getBeanDefinition();
                }
                // Check whether the BeanDefinition is a candidate for a configuration class
                if (ConfigurationClassUtils.checkConfigurationClassCandidate(bdCand, this.metadataReaderFactory)) {
                    // If so, parse it recursively
                    parse(bdCand.getBeanClassName(), holder.getBeanName());
                }
            }
        }
    }
    
    // ... [code omitted for brevity]

    // No superclass -> processing is complete
    return null;
}
```

The `org.springframework.context.annotation.ComponentScanAnnotationParser#parse` method handles classes configured with `@ComponentScan`. It shows how to configure and run a component scan from the given attributes.

```java
public Set<BeanDefinitionHolder> parse(AnnotationAttributes componentScan, final String declaringClass) {
    // Step 1. Create a new scanner
    ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(this.registry,
             componentScan.getBoolean("useDefaultFilters"), this.environment, this.resourceLoader);

    // Step 2. Set the bean name generator from the nameGenerator attribute
    Class<? extends BeanNameGenerator> generatorClass = componentScan.getClass("nameGenerator");
    boolean useInheritedGenerator = (BeanNameGenerator.class == generatorClass);
    scanner.setBeanNameGenerator(useInheritedGenerator ? this.beanNameGenerator :
                                 BeanUtils.instantiateClass(generatorClass));

    // Step 3. Set the scoped proxy mode or the scope metadata resolver
    ScopedProxyMode scopedProxyMode = componentScan.getEnum("scopedProxy");
    if (scopedProxyMode != ScopedProxyMode.DEFAULT) {
        scanner.setScopedProxyMode(scopedProxyMode);
    }
    else {
        Class<? extends ScopeMetadataResolver> resolverClass = componentScan.getClass("scopeResolver");
        scanner.setScopeMetadataResolver(BeanUtils.instantiateClass(resolverClass));
    }

    // Step 4. Set the resource pattern
    scanner.setResourcePattern(componentScan.getString("resourcePattern"));

    // Step 5. Add type filters from the includeFilters and excludeFilters attributes
    for (AnnotationAttributes filter : componentScan.getAnnotationArray("includeFilters")) {
        for (TypeFilter typeFilter : typeFiltersFor(filter)) {
            scanner.addIncludeFilter(typeFilter);
        }
    }
    for (AnnotationAttributes filter : componentScan.getAnnotationArray("excludeFilters")) {
        for (TypeFilter typeFilter : typeFiltersFor(filter)) {
            scanner.addExcludeFilter(typeFilter);
        }
    }

    // Step 6. Set whether beans are lazily initialized
    boolean lazyInit = componentScan.getBoolean("lazyInit");
    if (lazyInit) {
        scanner.getBeanDefinitionDefaults().setLazyInit(true);
    }

    // Step 7. Determine the base packages for the scanner
    Set<String> basePackages = new LinkedHashSet<>();
    String[] basePackagesArray = componentScan.getStringArray("basePackages");
    for (String pkg : basePackagesArray) {
        String[] tokenized = StringUtils.tokenizeToStringArray(this.environment.resolvePlaceholders(pkg),
                                                               ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        Collections.addAll(basePackages, tokenized);
    }
    for (Class<?> clazz : componentScan.getClassArray("basePackageClasses")) {
        basePackages.add(ClassUtils.getPackageName(clazz));
    }
    if (basePackages.isEmpty()) {
        basePackages.add(ClassUtils.getPackageName(declaringClass));
    }

    // Step 8. Make sure the class that declares @ComponentScan is not registered as a bean
    scanner.addExcludeFilter(new AbstractTypeHierarchyTraversingFilter(false, false) {
        @Override
        protected boolean matchClassName(String className) {
            return declaringClass.equals(className);
        }
    });
    
    // Step 9. Run the actual component scan with the configured scanner
    return scanner.doScan(StringUtils.toStringArray(basePackages));
}
```

Now step 1 of the `org.springframework.context.annotation.ComponentScanAnnotationParser#parse` method. The `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#ClassPathBeanDefinitionScanner` constructor creates a new `ClassPathBeanDefinitionScanner`. It uses its arguments to decide whether to use the default filters. It also sets the environment and the resource loader.

```java
public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
			Environment environment, @Nullable ResourceLoader resourceLoader) {

    // Assert that the registry is not null
    Assert.notNull(registry, "BeanDefinitionRegistry must not be null");

    // Assign the given BeanDefinitionRegistry to the registry field
    this.registry = registry;

    // Register the default filters if useDefaultFilters is true
    if (useDefaultFilters) {
        registerDefaultFilters();
    }

    // Set the environment of the scanner
    setEnvironment(environment);

    // Set the resource loader
    setResourceLoader(resourceLoader);
}
```

The `org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#registerDefaultFilters` method registers the default type filters. First, it registers a filter for classes with the `@Component` annotation. Then it tries to register two filters for JSR standard annotations: JSR-250 `@ManagedBean` and JSR-330 `@Named`. If the related classes are not on the classpath, these two filters are not registered.

```java
protected void registerDefaultFilters() {
    // Add a filter that includes classes annotated with @Component
    this.includeFilters.add(new AnnotationTypeFilter(Component.class));

    // Get the class loader of the current class
    ClassLoader cl = ClassPathScanningCandidateComponentProvider.class.getClassLoader();
    
    try {
        // Try to add a filter that includes classes annotated with JSR-250 'javax.annotation.ManagedBean'
        this.includeFilters.add(new AnnotationTypeFilter(
            ((Class<? extends Annotation>) ClassUtils.forName("javax.annotation.ManagedBean", cl)), false));
        logger.trace("JSR-250 'javax.annotation.ManagedBean' found and supported for component scanning");
    }
    catch (ClassNotFoundException ex) {
        // JSR-250 1.1 API (as included in Java EE 6) not available - simply skip.
    }
    
    try {
        // Try to add a filter that includes classes annotated with JSR-330 'javax.inject.Named'
        this.includeFilters.add(new AnnotationTypeFilter(
            ((Class<? extends Annotation>) ClassUtils.forName("javax.inject.Named", cl)), false));
        logger.trace("JSR-330 'javax.inject.Named' annotation found and supported for component scanning");
    }
    catch (ClassNotFoundException ex) {
        // JSR-330 API not available - simply skip.
    }
}
```

Now step 9 of the `org.springframework.context.annotation.ComponentScanAnnotationParser#parse` method. The `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan` method finds all components in the given `basePackages` and creates a `BeanDefinition` for each. The Spring container later uses these `BeanDefinition` objects to create the real bean instances.

```java
protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
    // Assert that at least one base package is given
    Assert.notEmpty(basePackages, "At least one base package must be specified");

    // The set that holds the bean definitions found
    Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();

    // Loop over each base package
    for (String basePackage : basePackages) {
        // Step 1. Find all candidate bean definitions in the given base package
        Set<BeanDefinition> candidates = findCandidateComponents(basePackage);

        // Loop over the bean definitions found
        for (BeanDefinition candidate : candidates) {
            // Step 2. Resolve the scope metadata of the bean
            ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
            // Set the scope of the bean
            candidate.setScope(scopeMetadata.getScopeName());
            // Step 3. Generate the bean name
            String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);

            // Step 4. If it is an AbstractBeanDefinition, apply post-processing
            if (candidate instanceof AbstractBeanDefinition) {
                postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
            }

            // Step 5. If it is an AnnotatedBeanDefinition, process the common definition annotations
            if (candidate instanceof AnnotatedBeanDefinition) {
                AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
            }

            // Step 6. Check whether the bean name already exists. If not, register it
            if (checkCandidate(beanName, candidate)) {
                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                // Step 7. Apply the scoped proxy mode and create a proxy for the bean if needed
                definitionHolder =
                    AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
                // Add the bean definition to the set
                beanDefinitions.add(definitionHolder);
                // Step 8. Register the bean definition in the bean registry
                registerBeanDefinition(definitionHolder, this.registry);
            }
        }
    }
    // Return all registered bean definitions
    return beanDefinitions;
}
```

Now step 1 of the `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan` method. The `org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents` method finds components in two ways. One uses a pre-built index (if it is available and supported). The other is the traditional scan. We focus on the traditional scan.

```java
public Set<BeanDefinition> findCandidateComponents(String basePackage) {
    // If a components index exists and it supports the include filters
    if (this.componentsIndex != null && indexSupportsIncludeFilters()) {
        // Add candidate components from the index
        return addCandidateComponentsFromIndex(this.componentsIndex, basePackage);
    } 
    else {
        // Scan the given base package for candidate components
        return scanCandidateComponents(basePackage);
    }
}
```

The `org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#scanCandidateComponents` method works in four steps. First, it builds the search path for the given package. Second, it scans the classpath for matching resources (usually `.class` files). Third, it checks each resource to see whether it is a candidate component, for example whether it has `@Component`. Finally, for each candidate class, it creates a `BeanDefinition` and adds it to the result set.

```java
private Set<BeanDefinition> scanCandidateComponents(String basePackage) {
    // The set that holds candidate bean definitions
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
            // Check whether the resource is readable
            if (resource.isReadable()) {
                try {
                    // Use the metadata reader to get the class metadata
                    MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                    // Check whether the class is a candidate component (for example, has @Component)
                    if (isCandidateComponent(metadataReader)) {
                        ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                        sbd.setSource(resource);
                        // Check again whether the bean definition is a candidate component
                        if (isCandidateComponent(sbd)) {
                            // ... [code omitted for brevity]
                            candidates.add(sbd);
                        }
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

The `org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#isCandidateComponent` method first makes sure the class is not in the exclude list. Then it checks that the class is in the include list and meets any other specified conditions.

```java
protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
    // Loop over all exclude filters
    for (TypeFilter tf : this.excludeFilters) {
        // If the class matches any exclude filter, return false: it is not a candidate component
        if (tf.match(metadataReader, getMetadataReaderFactory())) {
            return false;
        }
    }

    // Loop over all include filters
    for (TypeFilter tf : this.includeFilters) {
        // If the class matches any include filter
        if (tf.match(metadataReader, getMetadataReaderFactory())) {
            // Check whether the component meets the specific conditions
            return isConditionMatch(metadataReader);
        }
    }

    // Return false by default: it is not a candidate component
    return false;
}
```

Now step 6 of the `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan` method. The `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#checkCandidate` method makes sure the Spring container has no bean definition with the same name that is not compatible.

```java
protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
    // Check whether the bean definition registry already has a bean definition with this name
    if (!this.registry.containsBeanDefinition(beanName)) {
        return true;  // No bean definition with the same name exists, so return true
    }

    // Get the existing bean definition
    BeanDefinition existingDef = this.registry.getBeanDefinition(beanName);

    // Get the original bean definition (if any)
    BeanDefinition originatingDef = existingDef.getOriginatingBeanDefinition();
    if (originatingDef != null) {
        existingDef = originatingDef;
    }

    // Check whether the given bean definition is compatible with the existing one
    if (isCompatible(beanDefinition, existingDef)) {
        return false;  // They are compatible, so return false
    }

    // The given bean definition is not compatible with the existing one, so throw an exception
    throw new ConflictingBeanDefinitionException("Annotation-specified bean name '" + beanName +
                                                 "' for bean class [" + beanDefinition.getBeanClassName() + "] conflicts with existing, " +
                                                 "non-compatible bean definition of same name and class [" + existingDef.getBeanClassName() + "]");
}
```

Now step 8 of the `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan` method. The `org.springframework.context.annotation.ClassPathBeanDefinitionScanner#registerBeanDefinition` method calls `registerBeanDefinition` of the `BeanDefinitionReaderUtils` class. That does the actual `BeanDefinition` registration.

```java
protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
	BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
}
```

The `org.springframework.beans.factory.support.BeanDefinitionReaderUtils#registerBeanDefinition` method registers the `BeanDefinition` in the given `BeanDefinitionHolder`, and all its aliases, in the `BeanDefinitionRegistry`. For `@ComponentScan`, the scan and registration phase is finished once `registerBeanDefinition` is called. But the container lifecycle has more important steps that come later, such as bean lifecycle callbacks, bean instantiation, and bean initialization.

```java
public static void registerBeanDefinition(
    BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
    throws BeanDefinitionStoreException {

    // Get the primary name of the bean and register it in the registry
    String beanName = definitionHolder.getBeanName();
    registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

    // If aliases are given for the bean, register them
    String[] aliases = definitionHolder.getAliases();
    if (aliases != null) {
        for (String alias : aliases) {
            registry.registerAlias(beanName, alias);
        }
    }
}
```

### 8. Notes

**Default scanning**

+ If you set no package, `@ComponentScan` scans the package of the class that declares it, and its sub-packages.

**Performance**

+ Avoid scanning packages you do not need. It can cause performance problems. In large projects, giving exact paths to scan makes startup faster.

**Default filters**

+ By default, the filters used by `@ComponentScan` look for classes with `@Component`, `@Service`, `@Repository`, and `@Controller`. You can customize this with the `includeFilters` and `excludeFilters` attributes.

**Conflicting bean names**

+ Make sure bean names are not duplicated. Otherwise you may get a `BeanDefinitionStoreException`.

**Using `basePackages` and `basePackageClasses`**

+ `basePackages` lets you give the names of the packages to scan. `basePackageClasses` lets you give one or more classes. Spring scans the packages of those classes.

**Avoid multiple configurations**

+ Using several `@ComponentScan` annotations on the same configuration class is not recommended. If you really need it, consider `@ComponentScans`.

**Proxy mode**

+ Think about how to use the `scopedProxy` attribute, especially when you use beans with a non-singleton scope.

**Overriding annotation attributes**

+ When several `@ComponentScan` annotations are defined in several configuration classes, a later definition overrides an earlier one. You need to check this yourself.

**Use modules in large projects**

+ In a large project, it is easier to manage and maintain the code if you split the application into modules. Each module has its own configuration class and `@ComponentScan`.

### 9. Summary

#### Best Practices Summary

1. **Application startup**
   + In the main method of `ComponentScanApplication`, `AnnotationConfigApplicationContext` starts the Spring context. The configuration class `MyConfiguration` is passed to it. This tells Spring to read the configuration from `MyConfiguration`.
2. **Configuration class**
   + The `MyConfiguration` class is marked with `@Configuration`, so it is a configuration class. It also uses `@ComponentScan` to tell Spring where to look for components. Spring scans the `com.xcs.spring` package and all its sub-packages.
3. **Scan rules**
   + In `@ComponentScan`, `includeFilters` explicitly includes the `SpecialComponent` class in the Spring container, even though it has no Spring annotation. `excludeFilters` says the `AdminService` class must not be managed by the Spring container, even though it is marked with `@Service`.
4. **Component classes**:
   - The `UserRepository` class is in the `com.xcs.spring.repository` package and is marked with `@Repository`. So the Spring container manages it automatically.
   - The `UserService` class is in the `com.xcs.spring.service` package and is marked with `@Service`. So the Spring container manages it automatically too.
   - `AdminService` is also marked with `@Service`, but the `excludeFilters` setting of `@ComponentScan` keeps it out of the Spring container.
   - The `SpecialComponent` class has no Spring annotation, but the `includeFilters` setting of `@ComponentScan` makes the Spring container manage it.
5. **Result**
   + When the application starts, the names of all beans managed by the Spring container are printed. These include `UserRepository`, `UserService`, and `SpecialComponent`. `AdminService` is not there, because it was excluded.

#### Source Code Analysis Summary

1. **Application startup**
   + The Spring application starts through the `AnnotationConfigApplicationContext` constructor, which receives the configuration class `MyConfiguration`.
2. **Refresh the context**
   + Inside the constructor, the `refresh()` method is called to start refreshing the container.
3. **Run the BeanFactory post processors**
   + The `invokeBeanFactoryPostProcessors(beanFactory)` method runs the `BeanDefinitionRegistryPostProcessor` and `BeanFactoryPostProcessor` instances. A `BeanDefinitionRegistryPostProcessor` can change bean definitions before any other bean definition is loaded.
4. **Process configuration classes**
   + `ConfigurationClassPostProcessor` is a core post processor. It parses configuration classes (such as classes with `@Configuration`), finds the `@ComponentScan` annotation, reads its attributes, and runs the component scan.
5. **Run the component scan**
   + The `ComponentScanAnnotationParser` class does the detailed scan work. It creates a `ClassPathBeanDefinitionScanner` and sets its properties (whether to use the default filters, the resource loader, the scope resolver, the resource pattern, the include and exclude filters, and so on). Then it scans the given base packages.
6. **Scan candidate components**
   + For each base package, it finds all components and creates a `BeanDefinition` for each one.
7. **Register bean definitions**
   + Every component found is registered in the Spring container by calling `registerBeanDefinition`. If a bean definition with the same name already exists in the container, a conflict check is done.
8. **Finish the component scan**
   + When all base packages have been scanned, the `@ComponentScan` operation is finished.
