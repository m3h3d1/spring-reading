## @Import

- [@Import](#import)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/132806548) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [@Import source code](https://github.com/xuchengsheng/spring-reading/tree/master/spring-annotation/spring-annotation-import)

### 2. Annotation Description

`@Import` is a core Spring annotation. It imports configuration classes or components into the current Spring context. It can import regular `@Configuration` classes, regular component classes, or classes that implement the `ImportSelector` and `ImportBeanDefinitionRegistrar` interfaces. `ImportSelector` lets you choose the components to import dynamically, based on conditions. `ImportBeanDefinitionRegistrar` lets you register beans in code. With `@Import`, you can organize Spring configuration in a more flexible and modular way, and make sure the context has all the components and configuration it needs.

### 3. Annotation Source

`@Import` has been a core Spring annotation since version 3.0. It lets you import one or more component classes, usually `@Configuration` classes. It works like the `<import/>` element in Spring XML. You can import `@Configuration` classes, `ImportSelector` and `ImportBeanDefinitionRegistrar` implementations, and other regular component classes. Bean definitions declared in an imported `@Configuration` class should be injected with `@Autowired`.

```java
/**
 * Indicates one or more component classes to import — usually
 * Configuration @Configuration classes.
 *
 * Provides the same functionality as the <import/> element in Spring XML.
 * Allows importing @Configuration classes, ImportSelector and
 * ImportBeanDefinitionRegistrar implementations, and regular component
 * classes (as of 4.2; similar to AnnotationConfigApplicationContext#register).
 *
 * @Bean definitions declared in imported @Configuration classes should be
 * injected using org.springframework.beans.factory.annotation.Autowired @Autowired.
 * Either the bean itself can be autowired, or the configuration class instance
 * declaring the bean can be autowired.
 * The latter allows explicit, IDE-friendly navigation between @Configuration class methods.
 *
 * May be declared at the class level or as a meta-annotation.
 *
 * If XML or other non-@Configuration bean definition resources need to be
 * imported, use the ImportResource @ImportResource annotation instead.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see Configuration
 * @see ImportSelector
 * @see ImportBeanDefinitionRegistrar
 * @see ImportResource
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {

	/**
	 * Configuration @Configuration, ImportSelector,
	 * ImportBeanDefinitionRegistrar or regular component classes to import.
	 */
	Class<?>[] value();

}
```

### 4. Main Features

1. **Import configuration classes**
   + Lets one `@Configuration` class bring in another `@Configuration` class.

2. **Import selectors**
   + By implementing the `ImportSelector` interface, you can choose and import configuration classes dynamically.

3. **Register beans manually**
   + By implementing the `ImportBeanDefinitionRegistrar` interface, you can register beans by hand at runtime.

4. **Import regular component classes**
   + Since Spring 4.2, you can also import regular component classes.

### 5. Best Practices

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (it configures the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor, then loop over all bean definition names and print them.

```java
public class ImportApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

The `@Import` annotation imports other components or configuration into the current configuration class. In `MyConfiguration`, it imports four different components or selectors. The first is `MyBean.class`, a regular bean component. The second is `MyImportSelector.class`, which implements `ImportSelector` and selects configuration to import dynamically. The third is `MyDeferredImportSelector.class`, which implements `DeferredImportSelector` and selects configuration to import later. The fourth is `MyImportBeanDefinitionRegistrar.class`, which implements `ImportBeanDefinitionRegistrar` and registers beans by hand.

```java
@Configuration
@Import({MyBean.class, MyImportSelector.class, MyDeferredImportSelector.class, MyImportBeanDefinitionRegistrar.class})
public class MyConfiguration {

}
```

The `MyImportSelector` class imports the `MyBeanA` component dynamically. It makes sure `MyBeanA` is added to the Spring context.

```java
public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{MyBeanA.class.getName()};
    }
}
```

The `MyDeferredImportSelector` class imports the `MyBeanB` component later, and makes sure `MyBeanB` is added to the Spring context. Unlike a normal `ImportSelector`, a `DeferredImportSelector` runs after Spring has processed all other configuration classes. This gives you control over the processing order.

```java
public class MyDeferredImportSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{MyBeanB.class.getName()};
    }
}
```

The `MyImportBeanDefinitionRegistrar` class registers the `MyBeanC` component in the Spring container by hand. It does not rely on component scanning or other auto-configuration. It makes sure `MyBeanC` is added to the Spring context.

```java
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(MyBeanC.class);
        registry.registerBeanDefinition(MyBeanC.class.getName(), beanDefinition);
    }
}
```

These bean classes are imported into the Spring context with the `@Import` annotation and its related selectors and registrar.

```java
public class MyBean {

}

public class MyBeanA {

}

public class MyBeanB {

}

public class MyBeanC {

}
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    participant ImportApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant PostProcessorRegistrationDelegate
    participant ConfigurationClassPostProcessor
    participant ConfigurationClassParser
    participant DeferredImportSelectorHandler
    participant DeferredImportSelectorGroupingHandler
    participant DeferredImportSelectorGrouping
    participant DefaultDeferredImportSelectorGroup
    participant ConfigurationClassBeanDefinitionReader
    participant MyImportSelector
    participant MyDeferredImportSelector
    participant MyImportBeanDefinitionRegistrar
    participant DefaultListableBeanFactory

    ImportApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Initialize the context
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>Refresh the context
    AbstractApplicationContext->>AbstractApplicationContext:invokeBeanFactoryPostProcessors(beanFactory)<br>Call the BeanFactory post processors
    AbstractApplicationContext->>PostProcessorRegistrationDelegate:invokeBeanFactoryPostProcessors(beanFactory,beanFactoryPostProcessors)<br>Delegate the call to the BeanFactory post processors
    PostProcessorRegistrationDelegate->>PostProcessorRegistrationDelegate:invokeBeanDefinitionRegistryPostProcessors(postProcessors,registry,applicationStartup)<br>Run the BeanDefinition registry post processors
    PostProcessorRegistrationDelegate->>ConfigurationClassPostProcessor:postProcessBeanDefinitionRegistry(registry)<br>Process configuration classes
    ConfigurationClassPostProcessor->>ConfigurationClassPostProcessor:processConfigBeanDefinitions(registry)<br>Process configuration class bean definitions
    ConfigurationClassPostProcessor->>ConfigurationClassParser:new ConfigurationClassParser()<br>Create the configuration class parser
    ConfigurationClassParser-->>ConfigurationClassPostProcessor:Return parser
    ConfigurationClassPostProcessor->>ConfigurationClassParser:parser.parse(candidates)<br>Parse the candidate classes
    ConfigurationClassParser->>ConfigurationClassParser:parse(metadata,beanName)<br>Parse the class metadata further
    ConfigurationClassParser->>ConfigurationClassParser:processConfigurationClass(configClass,filter)<br>Process the @Configuration class
    ConfigurationClassParser->>+ConfigurationClassParser:doProcessConfigurationClass(configClass, sourceClass, filter)<br>Do the actual processing of the configuration class
    ConfigurationClassParser-->>-ConfigurationClassParser:Return SourceClass
    ConfigurationClassParser->>+ConfigurationClassParser:processImports(configClass, sourceClass, importCandidates, filter, true)<br>Process imports
    ConfigurationClassParser->>MyImportSelector:selectImports(importingClassMetadata)<br>Call the custom import selector
    MyImportSelector-->>ConfigurationClassParser:Return the bean array
    ConfigurationClassParser->>DeferredImportSelectorHandler:process()<br>Process the deferred import selectors
    DeferredImportSelectorHandler->>DeferredImportSelectorGroupingHandler:processGroupImports()<br>Process group imports
    DeferredImportSelectorGroupingHandler->>DeferredImportSelectorGrouping:getImports()<br>Get the imports
    DeferredImportSelectorGrouping->>DefaultDeferredImportSelectorGroup:process(metadata,selector)<br>Process the group of the default deferred import selector
    DefaultDeferredImportSelectorGroup->>MyDeferredImportSelector:selectImports(importingClassMetadata)<br> Call the custom import selector
    MyDeferredImportSelector-->>DefaultDeferredImportSelectorGroup:Return the bean array, stored in the imports field
    DeferredImportSelectorGrouping->>DefaultDeferredImportSelectorGroup:selectImports()<br>Select the imports
    DeferredImportSelectorGrouping-->>DeferredImportSelectorGroupingHandler:Return Iterable<Group.Entry>
    DeferredImportSelectorGroupingHandler->>ConfigurationClassParser:processImports(configClass, sourceClass, importCandidates, filter, true)<br> Process imports again
    ConfigurationClassParser-->>-ConfigurationClassParser:Process @Configuration recursively
    ConfigurationClassPostProcessor->>ConfigurationClassBeanDefinitionReader:loadBeanDefinitions(configClasses)<br>Load bean definitions
    ConfigurationClassBeanDefinitionReader->>ConfigurationClassBeanDefinitionReader:loadBeanDefinitionsForConfigurationClass(configClass, trackedConditionEvaluator)<br>Load bean definitions for the configuration class
    ConfigurationClassBeanDefinitionReader->>ConfigurationClassBeanDefinitionReader:registerBeanDefinitionForImportedConfigurationClass(configClass)<br>Register the bean definition of the imported configuration class
    ConfigurationClassBeanDefinitionReader->>DefaultListableBeanFactory:registerBeanDefinition(beanName,beanDefinition)<br>Register the bean definition in the bean factory
    ConfigurationClassBeanDefinitionReader->>ConfigurationClassBeanDefinitionReader:loadBeanDefinitionsFromRegistrars(registrars)<br>Load bean definitions from the registrars
    ConfigurationClassBeanDefinitionReader->>MyImportBeanDefinitionRegistrar:registerBeanDefinitions(importingClassMetadata,registry)<br>Call the custom bean definition registrar
    MyImportBeanDefinitionRegistrar->>DefaultListableBeanFactory:registerBeanDefinition(beanName,beanDefinition)<br>Register the bean definition in the bean factory
~~~

### 7. Source Code Analysis

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (it configures the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor, then loop over all bean definition names and print them.

```java
public class ImportApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

The `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` constructor does three things. We focus on the `refresh()` method.

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
}
```

In `org.springframework.context.support.AbstractApplicationContext#refresh`, we focus on `finishBeanFactoryInitialization(beanFactory)`. This method instantiates all remaining non-lazy singleton beans. The other methods are not the focus of this reading, so we skip them for now.

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
     // ... [code omitted for brevity]
     // Invoke factory processors registered as beans in the context
     invokeBeanFactoryPostProcessors(beanFactory);
     // ... [code omitted for brevity]
}
```

In `org.springframework.context.support.AbstractApplicationContext#invokeBeanFactoryPostProcessors`, the work is handed to `PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors()`.

```java
protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
    // ... [code omitted for brevity]
}
```

In `org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`, it first calls the `BeanDefinitionRegistryPostProcessor` instances (a sub-interface of `BeanFactoryPostProcessor`). They are meant to change the default bean definitions before all other bean definitions are loaded.

```java
public static void invokeBeanFactoryPostProcessors(
        ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
    // ... [code omitted for brevity]
    invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
	// ... [code omitted for brevity]
}
```

In `org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors`, it loops over the processors and calls `postProcessBeanDefinitionRegistry(registry)` from the `BeanDefinitionRegistryPostProcessor` interface.

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

In `org.springframework.context.annotation.ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry`, it calls `processConfigBeanDefinitions`. This method processes and registers the beans defined in configuration classes.

```java
@Override
public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
    // ... [code omitted for brevity]
    processConfigBeanDefinitions(registry);
}
```

The `org.springframework.context.annotation.ConfigurationClassPostProcessor#processConfigBeanDefinitions` method parses and validates the configuration classes. It makes sure all beans defined in them are registered correctly in Spring's bean definition registry.

```java
public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
    // ... [code omitted for brevity]

    // Step 1: Create a parser for configuration classes
    ConfigurationClassParser parser = new ConfigurationClassParser(
            this.metadataReaderFactory, this.problemReporter, this.environment,
            this.resourceLoader, this.componentScanBeanNameGenerator, registry);

    // Step 2: Initialize the set of candidate configuration classes and the set of already parsed ones
    Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
    Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());

    // Step 3: Process all candidate configuration classes until none are left
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

Now step 3.1 of `org.springframework.context.annotation.ConfigurationClassPostProcessor#processConfigBeanDefinitions`. The `org.springframework.context.annotation.ConfigurationClassParser#parse` method loops over all configuration class candidates and parses each annotated bean definition. This usually means finding the @Bean methods and component scan instructions in the class, and registering them in the Spring container.

```java
public void parse(Set<BeanDefinitionHolder> configCandidates) {
    // Loop over each configuration class holder
    for (BeanDefinitionHolder holder : configCandidates) {
        BeanDefinition bd = holder.getBeanDefinition();
        try {
            // Step 1: If the bean definition is an AnnotatedBeanDefinition, get its annotation metadata and parse it
            if (bd instanceof AnnotatedBeanDefinition) {
                parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
            }
            // Step 2: If it is an AbstractBeanDefinition that already has a bean class, parse that class directly
            else if (bd instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) bd).hasBeanClass()) {
                parse(((AbstractBeanDefinition) bd).getBeanClass(), holder.getBeanName());
            }
            // Step 3: If neither applies, parse using the bean class name
            else {
                parse(bd.getBeanClassName(), holder.getBeanName());
            }
        }
        // If a BeanDefinitionStoreException is caught during parsing, rethrow it
        catch (BeanDefinitionStoreException ex) {
            throw ex;
        }
        // For any other exception, wrap it in a new BeanDefinitionStoreException and throw it
        catch (Throwable ex) {
            // ... [code omitted for brevity]
        }
    }
    // Step 4: After all configuration classes are parsed, process all deferred import selectors
    this.deferredImportSelectorHandler.process();
}
```

Now the first step of that method: `org.springframework.context.annotation.ConfigurationClassParser#parse(metadata, beanName)`. It turns the annotation metadata and bean name into a configuration class, then processes it. Processing configuration classes is the core of Spring's configuration-driven setup. It covers many key tasks, such as parsing `@Bean` methods, handling `@ComponentScan`, and handling `@Import`.

```java
protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
    processConfigurationClass(new ConfigurationClass(metadata, beanName), DEFAULT_EXCLUSION_FILTER);
}
```

The `org.springframework.context.annotation.ConfigurationClassParser#processConfigurationClass` method processes a given configuration class. It first processes the configuration class and its superclasses recursively, so all related configuration is read and parsed. After that, it adds the configuration class to the map of parsed configuration classes.

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

The main job of `org.springframework.context.annotation.ConfigurationClassParser#doProcessConfigurationClass` here is to process the `@Import` annotations of the given `ConfigurationClass`. After all imports are processed, it finishes and returns null.

```java
@Nullable
protected final SourceClass doProcessConfigurationClass(
    ConfigurationClass configClass, SourceClass sourceClass, Predicate<String> filter)
    throws IOException {

    // ... [code omitted for brevity]
    
    // Process all @Import annotations on sourceClass
    processImports(configClass, sourceClass, getImports(sourceClass), filter, true);

    // ... [code omitted for brevity]
    
    // In this implementation, null is returned, which means no superclass needs further processing
    return null;
}
```

The `org.springframework.context.annotation.ConfigurationClassParser#processImports` method has three main steps. Step one is for `ImportSelector`: `processImports` selects and imports classes dynamically, which makes Spring configuration more flexible and modular. Step two is for `ImportBeanDefinitionRegistrar`: it gives a hook into the bean definition registration process, which allows dynamic and conditional configuration. `processImports` makes sure this logic runs correctly when `@Import` is handled. Step three is for a class that is neither an `ImportSelector` nor an `ImportBeanDefinitionRegistrar`: it is treated as a regular Spring component or configuration class and goes through the normal process. This makes sure every class imported with `@Import`, whatever its type, is registered and processed correctly.

```java
private void processImports(ConfigurationClass configClass, SourceClass currentSourceClass,
            Collection<SourceClass> importCandidates, Predicate<String> exclusionFilter,
            boolean checkForCircularImports) {
    // If there are no import candidates, return directly
    if (importCandidates.isEmpty()) {
        return;
    }
    // If circular import checking is on and the configuration class forms a chain on the import stack, report a circular import problem
    if (checkForCircularImports && isChainedImportOnStack(configClass)) {
        this.problemReporter.error(new CircularImportProblem(configClass, this.importStack));
    }
    else {
        // Push the current configuration class onto the import stack
        this.importStack.push(configClass);
        try {
            for (SourceClass candidate : importCandidates) {
                // Step 1: Check whether the candidate implements the ImportSelector interface
                if (candidate.isAssignable(ImportSelector.class)) {
                    // Load the current candidate class
                    Class<?> candidateClass = candidate.loadClass();
                    // Instantiate the ImportSelector implementation with a utility method
                    ImportSelector selector = ParserStrategyUtils.instantiateClass(candidateClass, ImportSelector.class,
                                        this.environment, this.resourceLoader, this.registry);
                    // Get the exclusion filter given by the ImportSelector
                    Predicate<String> selectorFilter = selector.getExclusionFilter();
                    // If the selector provides an extra exclusion filter, combine it with the current one
                    if (selectorFilter != null) {
                        exclusionFilter = exclusionFilter.or(selectorFilter);
                    }
                    // Step 1.1: Check whether the selector is a DeferredImportSelector
                    if (selector instanceof DeferredImportSelector) {
                        // If so, hand it to the dedicated handler
                        this.deferredImportSelectorHandler.handle(configClass, (DeferredImportSelector) selector);
                    }
                    else {
                        // Step 1.2: If not, use the selector to choose the classes to import
                        String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
                        // Turn these class names into a SourceClass collection
                        Collection<SourceClass> importSourceClasses = asSourceClasses(importClassNames, exclusionFilter);
                        // Process these classes recursively
                        processImports(configClass, currentSourceClass, importSourceClasses, exclusionFilter, false);
                    }
                }
                // Step 2: Check whether the candidate implements the ImportBeanDefinitionRegistrar interface
                else if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
                    // Load the current candidate class
                    Class<?> candidateClass = candidate.loadClass();
                    // Instantiate the ImportBeanDefinitionRegistrar implementation with a utility method
                    ImportBeanDefinitionRegistrar registrar =
                        ParserStrategyUtils.instantiateClass(candidateClass, ImportBeanDefinitionRegistrar.class,
                            this.environment, this.resourceLoader, this.registry);
                    // Add the registrar to the current configuration class
                    configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());
                }
                // Step 3: The candidate is neither an ImportSelector nor an ImportBeanDefinitionRegistrar
                else {
                    // Register the imported class for the current source class on the import stack
                    this.importStack.registerImport(
                        currentSourceClass.getMetadata(), candidate.getMetadata().getClassName());
                    // Process the candidate as a configuration class, recursively
                    processConfigurationClass(candidate.asConfigClass(configClass), exclusionFilter);
                }
            }
        }
        catch (BeanDefinitionStoreException ex) {
            throw ex;
        }
        catch (Throwable ex) {
            // ... [code omitted for brevity]
        }
        finally {
            // After processing, pop the current configuration class off the import stack
            this.importStack.pop();
        }
    }
}
```

In step 1.2 of `org.springframework.context.annotation.ConfigurationClassParser#processImports`, the call finally reaches our own code. The `selectImports` method always returns the fully qualified name of `MyBeanA`. This means that when this `ImportSelector` is processed, the `MyBeanA` class is imported into the Spring application context.

```java
public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{MyBeanA.class.getName()};
    }
}
```

Back to step 4 of `org.springframework.context.annotation.ConfigurationClassParser#parse`. In `org.springframework.context.annotation.ConfigurationClassParser.DeferredImportSelectorHandler#process`, it first gets the pending deferred import selectors. Then it uses `DeferredImportSelectorGroupingHandler` to process them group by group. Finally it resets the deferred import selector list, ready for the next round.

```java
/**
 * Process all registered deferred import selectors.
 */
public void process() {
    // Get the current list of deferred import selectors
    List<DeferredImportSelectorHolder> deferredImports = this.deferredImportSelectors;
    // Set the current list to null, which means they are about to be processed
    this.deferredImportSelectors = null;
    
    try {
        // If there are deferred import selectors to process
        if (deferredImports != null) {
            // Create a new grouping handler for the grouping logic of deferred import selectors
            DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
            
            // Sort the deferred import selectors so they are processed in the expected order
            deferredImports.sort(DEFERRED_IMPORT_COMPARATOR);
            
            // Register each deferred import selector with the grouping handler
            deferredImports.forEach(handler::register);
            
            // Let the grouping handler process the grouped imports
            handler.processGroupImports();
        }
    }
    // When the method ends, with or without an exception, reset the deferred import selector list to a new empty list
    finally {
        this.deferredImportSelectors = new ArrayList<>();
    }
}
```

The `org.springframework.context.annotation.ConfigurationClassParser.DeferredImportSelectorGroupingHandler#processGroupImports` method loops over each registered group of deferred import selectors. For each import entry in a group, it does the processing. This includes handling all related `@Import` and `@Bean` definitions recursively.

```java
/**
 * Process the deferred import selectors in each group.
 */
public void processGroupImports() {
    // Loop over each registered group of deferred import selectors
    for (DeferredImportSelectorGrouping grouping : this.groupings.values()) {
        // Get the candidate exclusion filter of the group
        Predicate<String> exclusionFilter = grouping.getCandidateFilter();
        
        // Loop over each import entry in the group
        grouping.getImports().forEach(entry -> {
            // Get the configuration class linked to the current import entry
            ConfigurationClass configurationClass = this.configurationClasses.get(entry.getMetadata());
            
            try {
                // Process each import entry, including all related @Import and @Bean definitions, recursively
                processImports(configurationClass, asSourceClass(configurationClass, exclusionFilter),
                               Collections.singleton(asSourceClass(entry.getImportClassName(), exclusionFilter)),
                               exclusionFilter, false);
            }
            // If a BeanDefinitionStoreException is caught during processing, rethrow it
            catch (BeanDefinitionStoreException ex) {
                throw ex;
            }
            // For any other exception, handle it (simplified here, the detailed handling is not shown)
            catch (Throwable ex) {
                // ... [code omitted for brevity]
            }
        });
    }
}
```

The `org.springframework.context.annotation.ConfigurationClassParser.DeferredImportSelectorGrouping#getImports` method loops over each registered deferred import selector. It passes their information to the group for processing. Finally it uses the group instance to select and return the classes to import.

```java
public Iterable<Group.Entry> getImports() {
    // Loop over each registered deferred import selector
    for (DeferredImportSelectorHolder deferredImport : this.deferredImports) {
        // For each one, pass the metadata of its configuration class and the selector to the group
        this.group.process(deferredImport.getConfigurationClass().getMetadata(),
                           deferredImport.getImportSelector());
    }
    // Use the group instance to select and return the classes to import
    return this.group.selectImports();
}
```

The `org.springframework.context.annotation.ConfigurationClassParser.DefaultDeferredImportSelectorGroup#process` method takes the given `DeferredImportSelector`, finds all the classes it selects, and stores them in a list together with the given annotation metadata for later use.

```java
@Override
public void process(AnnotationMetadata metadata, DeferredImportSelector selector) {
    // Use the selector and the given metadata to select the classes to import
    for (String importClassName : selector.selectImports(metadata)) {
        // For each selected class, create a new Entry and add it to the import list of the current group
        this.imports.add(new Entry(metadata, importClassName));
    }
}
```

Finally, the call reaches our own code. The `selectImports` method always returns the fully qualified name of `MyBeanB`. This means that when this `DeferredImportSelector` is processed, the `MyBeanB` class is imported into the Spring application context.

```java
public class MyDeferredImportSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{MyBeanB.class.getName()};
    }
}
```

Now step 5 of `org.springframework.context.annotation.ConfigurationClassPostProcessor#processConfigBeanDefinitions`. The `org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitions` method loops over the given set of `ConfigurationClass` objects (they stand for the classes annotated with `@Configuration`) and loads the related bean definitions for each class.

```java
public void loadBeanDefinitions(Set<ConfigurationClass> configurationModel) {
    TrackedConditionEvaluator trackedConditionEvaluator = new TrackedConditionEvaluator();
    for (ConfigurationClass configClass : configurationModel) {
        loadBeanDefinitionsForConfigurationClass(configClass, trackedConditionEvaluator);
    }
}
```

In `org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForConfigurationClass`, if the configuration class was imported with `@Import` or another mechanism, the method registers a bean definition for it. It also handles all `ImportBeanDefinitionRegistrar` instances linked to the `ConfigurationClass`. These registrars let you add extra bean definitions in code while the configuration class is being parsed.

```java
private void loadBeanDefinitionsForConfigurationClass(
        ConfigurationClass configClass, TrackedConditionEvaluator trackedConditionEvaluator) {

    // ... [possible setup or other pre-processing]

    // Step 1: If this configuration class was imported through @Import or another mechanism, register a bean definition for it.
    // This makes sure the imported @Configuration class is also treated as a bean and can be retrieved from the ApplicationContext.
    if (configClass.isImported()) {
        registerBeanDefinitionForImportedConfigurationClass(configClass);
    }
    
    // ... [possible other processing or bean definition loading]

    // Step 2: Load bean definitions from the ImportBeanDefinitionRegistrars linked to the current configuration class.
    // An ImportBeanDefinitionRegistrar lets us register extra bean definitions in code while parsing the @Configuration class.
    loadBeanDefinitionsFromRegistrars(configClass.getImportBeanDefinitionRegistrars());
}
```

Now step 1 of `loadBeanDefinitionsForConfigurationClass`. In `org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#registerBeanDefinitionForImportedConfigurationClass`, it first creates a bean definition from the annotation metadata of the configuration class. Then it finds and sets the bean scope, such as singleton or prototype. After it generates a unique name for the bean definition, it handles common annotations on it, such as `@Lazy` and `@Primary`. If the scope, such as request or session, needs a proxy, it applies the proxy. Finally, it registers the bean definition in the Spring container and links the generated name to the original configuration class. This means the imported configuration class can also be accessed or injected in Spring as a regular bean.

```java
/**
 * Register a bean definition for the imported ConfigurationClass.
 *
 * @param configClass the ConfigurationClass to process
 */
private void registerBeanDefinitionForImportedConfigurationClass(ConfigurationClass configClass) {
    // Get the metadata of configClass
    AnnotationMetadata metadata = configClass.getMetadata();

    // Create a bean definition based on the metadata
    AnnotatedGenericBeanDefinition configBeanDef = new AnnotatedGenericBeanDefinition(metadata);

    // Resolve the bean scope (for example: singleton, prototype)
    ScopeMetadata scopeMetadata = scopeMetadataResolver.resolveScopeMetadata(configBeanDef);
    configBeanDef.setScope(scopeMetadata.getScopeName());

    // Generate a bean name for configBeanDef
    String configBeanName = this.importBeanNameGenerator.generateBeanName(configBeanDef, this.registry);

    // Process common annotation definitions (for example: @Lazy, @Primary)
    AnnotationConfigUtils.processCommonDefinitionAnnotations(configBeanDef, metadata);

    // Wrap the bean definition in a BeanDefinitionHolder to carry extra configuration metadata
    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(configBeanDef, configBeanName);

    // Apply the scoped proxy mode if needed (for example, for beans in the "request" and "session" scopes)
    definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);

    // Register the bean definition in the bean definition registry
    this.registry.registerBeanDefinition(definitionHolder.getBeanName(), definitionHolder.getBeanDefinition());

    // Set the generated bean name on configClass
    configClass.setBeanName(configBeanName);

    // If the log level is TRACE, log a message about the registration
    if (logger.isTraceEnabled()) {
        logger.trace("Registered bean definition for imported class '" + configBeanName + "'");
    }
}
```

Now step 2 of `loadBeanDefinitionsForConfigurationClass`. Each `ImportBeanDefinitionRegistrar` gets the chance to register extra bean definitions in the Spring container with its own logic.

```java
private void loadBeanDefinitionsFromRegistrars(Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> registrars) {
    registrars.forEach((registrar, metadata) ->
                       registrar.registerBeanDefinitions(metadata, this.registry, this.importBeanNameGenerator));
}
```

Finally, the call reaches our own code. The `MyImportBeanDefinitionRegistrar` class registers the `MyBeanC` component in the Spring container by hand. It does not rely on component scanning or other auto-configuration. It makes sure `MyBeanC` is added to the Spring context.

```java
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(MyBeanC.class);
        registry.registerBeanDefinition(MyBeanC.class.getName(), beanDefinition);
    }
}
```

### 8. Notes

1. **Avoid circular references**
   + Make sure you do not create a circular reference, where one configuration class imports another, and the other imports the first one back.

2. **Relation to `@ComponentScan`**
   + Both `@Import` and `@ComponentScan` can register beans, but they are used a little differently. `@ComponentScan` scans for and registers beans automatically. `@Import` imports other configuration classes explicitly.

3. **Property overriding**
   + If the same bean definition is imported from several configuration classes with different properties or values, the bean definition imported later overrides the one imported earlier.

4. **Relation to `@Profile`**
   + You can use `@Import` together with `@Profile`, so a configuration class is imported only when a specific profile is active.

### 9. Summary

#### Best Practices Summary

1. **Entry class**
   + In the `ImportApplication` class, we create the Spring context with `AnnotationConfigApplicationContext`, which is Spring's way of configuring with Java annotations. We set `MyConfiguration` as the main configuration class, then loop over all bean names registered in the context.

2. **Main configuration class**

   + The `MyConfiguration` class uses `@Import` to import four kinds of components or selectors:

     - `MyBean`: a plain Java class.

     - `MyImportSelector`: a class that implements the `ImportSelector` interface.

     - `MyDeferredImportSelector`: a class that implements the `DeferredImportSelector` interface.

     - `MyImportBeanDefinitionRegistrar`: a class that implements the `ImportBeanDefinitionRegistrar` interface.


3. **Selectors and registrar**:

   - `MyImportSelector` implements `ImportSelector`. It selects and imports the `MyBeanA` class dynamically.

   - `MyDeferredImportSelector` implements `DeferredImportSelector`. It selects and imports the `MyBeanB` class later. Unlike `ImportSelector`, it imports only after all other configuration classes are processed.

   - `MyImportBeanDefinitionRegistrar` registers beans by hand. In this example, it registers `MyBeanC` in the Spring context.


4. **Component definitions**
   + Our best-practice example has four Java classes: `MyBean`, `MyBeanA`, `MyBeanB` and `MyBeanC`. All of them are selected and imported into the Spring context by the selectors or registrar above.

#### Source Code Analysis Summary

1. **Startup phase**
   + When Spring starts, it uses `AnnotationConfigApplicationContext` to load and parse configuration classes. These classes may contain the `@Import` annotation.

2. **Parsing configuration classes**
   + In the `AnnotationConfigApplicationContext` constructor, the `refresh()` method starts refreshing the container and parsing bean definitions. Then `AbstractApplicationContext#refresh` calls `invokeBeanFactoryPostProcessors` to handle the factory processors registered as beans in the context.

3. **Processing the `@Import` annotation**
   + During processing, Spring pays special attention to `BeanDefinitionRegistryPostProcessor`, a sub-interface of `BeanFactoryPostProcessor`. It changes the default bean definitions before all other bean definitions are loaded. Then Spring parses the configuration classes and validates them. This mainly means finding the @Bean methods and component scan instructions in the class, and registering them in the Spring container.

4. **Processing `ImportSelector` and `DeferredImportSelector`**
   + During this parsing, if a configuration class contains a class that implements `ImportSelector` or `DeferredImportSelector`, it is used to select and import other classes. In the example, `MyImportSelector` and `MyDeferredImportSelector` are such selectors. They import `MyBeanA` and `MyBeanB`.

5. **Processing `ImportBeanDefinitionRegistrar`**
   + While loading bean definitions, Spring also handles all `ImportBeanDefinitionRegistrar` instances linked to the `ConfigurationClass`. These registrars let you add extra bean definitions in code during parsing. For example, `MyImportBeanDefinitionRegistrar` registers `MyBeanC` by hand.

6. **Registering imported configuration classes**
   + If a `@Configuration` class is imported through `@Import` or another mechanism, Spring handles its `@Bean` methods and other annotations. It also registers a bean definition for the class itself, so it can be injected into other beans or retrieved by the application.

7. **Registering extra beans**
   + With `ImportBeanDefinitionRegistrar` and its own logic, you can register any number of extra bean definitions in the container as needed.