## @PropertySource

- [@PropertySource](#propertysource)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133800438) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [@PropertySource source](https://github.com/xuchengsheng/spring-reading/tree/master/spring-annotation/spring-annotation-propertySource)

### 2. Annotation Description

The `@PropertySource` annotation points to an external properties file. The key-value pairs in that file are loaded into the Spring `Environment`. Then you can read and use these values easily in your application.

### 3. Annotation Source

`@PropertySource` was added in Spring 3.1. It declares a property source in Spring. It lets us point to external property files (such as `.properties` or `.xml` files) and load their key-value pairs into the Spring `Environment`. Then the application can read and use these values easily.

```java
/**
 * @PropertySource is an annotation that declares a property source in Spring.
 * With it, we can point to an external property file (such as a .properties or .xml file)
 * and load its key-value pairs into the Spring Environment.
 * Then the application can read and use these values easily.
 * 
 * It is usually used on a @Configuration class
 * to give configuration properties to the whole Spring environment.
 * 
 * Example:
 * Configuration
 * PropertySource("classpath:/com/myco/app.properties")
 * public class AppConfig {
 *     // ...
 * }
 * 
 * With the configuration above, the application can use all properties in the `app.properties` file from the Spring Environment.
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 3.1
 * @see PropertySources
 * @see Configuration
 * @see org.springframework.core.env.PropertySource
 * @see org.springframework.core.env.ConfigurableEnvironment#getPropertySources()
 * @see org.springframework.core.env.MutablePropertySources
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(PropertySources.class)
public @interface PropertySource {

    /**
     * Sets the name of this property source. If omitted, the factory generates a name from the underlying resource.
     */
    String name() default "";

    /**
     * Sets the resource location of the property file to load.
     * Both traditional properties files and XML formats are supported, for example: "classpath:/com/myco/app.properties" or "file:/path/to/file.xml".
     * Wildcards in resource locations are not supported.
     * ${...} placeholders are resolved against all property sources already registered in the Environment.
     * Each location is added to the enclosing Environment as its own property source, in the order declared.
     */
    String[] value();

    /**
     * Indicates whether a missing property resource should be ignored.
     * true is suitable if the property file is completely optional.
     * The default is false.
     */
    boolean ignoreResourceNotFound() default false;

    /**
     * Sets a specific character encoding for the given resource, such as "UTF-8".
     */
    String encoding() default "";

    /**
     * Sets a custom PropertySourceFactory.
     * By default, the default factory for standard resource files is used.
     */
    Class<? extends PropertySourceFactory> factory() default PropertySourceFactory.class;
}
```

### 4. Main Features

1. **Load property files**
   + Lets us point to one or more external property files (such as `.properties` or `.xml` files) and load them into the Spring context.
2. **Declare resource locations**
   + `@PropertySource` has a `value` attribute for the location of the external property file, such as `classpath:/com/myco/app.properties` or `file:/path/to/file.xml`.
3. **Property override**
   + When you use several `@PropertySource` annotations, or the `PropertySources` annotation (which lets us list several `@PropertySource` annotations), a property source declared later overrides properties with the same name from sources declared earlier.
4. **Handle missing resources**
   + With the `ignoreResourceNotFound` attribute, we choose whether an exception is thrown when the resource is not found. This helps when some property files are optional.
5. **Set the file encoding**
   + With the `encoding` attribute, we can set a specific character encoding for the resource file, such as "UTF-8".
6. **Custom property source factory**
   + With the `factory` attribute, we can set a custom `PropertySourceFactory` to parse and load the properties.
7. **Placeholder resolution**
   + Property values in files given by `@PropertySource` can use `${...}` placeholders. They are resolved to the values of other properties already loaded in the `Environment`.
8. **Work with `Environment`**
   + Once the property file is loaded into the `Environment`, you can inject the `Environment` with `@Autowired` to look up and use these properties.

### 5. Best Practices

Let's start with the main class. The context is `AnnotationConfigApplicationContext` (it configures the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor. Then we read two properties from it: `apiVersion` and `kind`.

```java
public class PropertySourceApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println("apiVersion = " + context.getEnvironment().getProperty("apiVersion"));
        System.out.println("kind = " + context.getEnvironment().getProperty("kind"));
    }
}
```

The `MyConfiguration` class is marked with `@Configuration`, so it is a Java-based Spring configuration class. It also uses `@PropertySource` to load the external property file named `my-application.yml`.

```java
@Configuration
@PropertySource("classpath:my-application.yml")
public class MyConfiguration {

}
```

The `my-application.yml` file has this content.

```yaml
apiVersion: v1
kind: ConfigMap
```

In the output, the Spring application loaded the properties from the YAML file and can use their values.

```java
apiVersion = v1
kind = ConfigMap
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
Title: @PropertySource annotation sequence diagram

ComponentScanApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>Create the context
AnnotationConfigApplicationContext->>AbstractApplicationContext: refresh()<br>Refresh the context
AbstractApplicationContext->>AbstractApplicationContext: invokeBeanFactoryPostProcessors(beanFactory)<br>Call bean factory post processors
AbstractApplicationContext->>PostProcessorRegistrationDelegate: invokeBeanFactoryPostProcessors(beanFactory,beanFactoryPostProcessors)<br>Delegate the work
PostProcessorRegistrationDelegate->>PostProcessorRegistrationDelegate: invokeBeanDefinitionRegistryPostProcessors(postProcessors,registry,applicationStartup) <br>Call registry post processors
PostProcessorRegistrationDelegate->>ConfigurationClassPostProcessor: postProcessBeanDefinitionRegistry(registry)<br>Post process configuration classes
ConfigurationClassPostProcessor->>ConfigurationClassPostProcessor: processConfigBeanDefinitions(registry)<br>Process bean definitions
ConfigurationClassPostProcessor->>ConfigurationClassParser: ConfigurationClassParser(...)<br>Create the parser
ConfigurationClassParser-->>ConfigurationClassPostProcessor: Return the parser<br>
ConfigurationClassPostProcessor->>ConfigurationClassParser: parser.parse(candidates)<br>Parse the candidates
ConfigurationClassParser->>ConfigurationClassParser: parse(metadata, String beanName)<br>Parse the metadata
ConfigurationClassParser->>ConfigurationClassParser: processConfigurationClass(configClass,filter)<br>Process the configuration class
ConfigurationClassParser->>ConfigurationClassParser: doProcessConfigurationClass(configClass,sourceClass,filter)<br>Do the processing
ConfigurationClassParser->>ConfigurationClassParser: processPropertySource(propertySource)<br>Process the property source
ConfigurationClassParser->>DefaultPropertySourceFactory:createPropertySource(name,resource)<br>Create the property source
DefaultPropertySourceFactory->>ConfigurationClassParser:Return PropertySource
ConfigurationClassParser->>StandardEnvironment:getPropertySources()<br>Get the property sources
StandardEnvironment->>ConfigurationClassParser:Return MutablePropertySources
ConfigurationClassParser->>ConfigurationClassParser: addPropertySource(propertySource)<br>Add the property source
ConfigurationClassParser->>MutablePropertySources:addLast(propertySource)<br>Add the property source with the lowest priority
~~~

### 7. Source Code Analysis

Let's start with the main class. The context is `AnnotationConfigApplicationContext` (it configures the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor. Then we read two properties from it: `apiVersion` and `kind`.

```java
public class PropertySourceApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println("apiVersion = " + context.getEnvironment().getProperty("apiVersion"));
        System.out.println("kind = " + context.getEnvironment().getProperty("kind"));
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

In `org.springframework.context.support.AbstractApplicationContext#refresh`, we focus on `finishBeanFactoryInitialization(beanFactory)`. It instantiates all remaining non-lazy singleton beans. The other methods are not the focus of this reading, so we skip them.

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
     // ... [code omitted for brevity]
     // Call factory processors registered as beans in the context
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

In `org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`, it first calls `BeanDefinitionRegistryPostProcessor` (a sub-interface of `BeanFactoryPostProcessor`). It is made to change the default bean definitions before all other bean definitions are loaded.

```java
public static void invokeBeanFactoryPostProcessors(
        ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
    // ... [code omitted for brevity]
    invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
	// ... [code omitted for brevity]
}
```

The `org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors` method loops over the processors and calls `postProcessBeanDefinitionRegistry(registry)` from the `BeanDefinitionRegistryPostProcessor` interface.

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

    // Step 3: Process all candidate configuration classes in a loop until none are left
    do {
        // Step 3.1 Parse the configuration classes
        parser.parse(candidates);
        
        // Step 3.2 Validate the configuration classes
        parser.validate();
        
        // ... [code omitted for brevity]
        
    } while (!candidates.isEmpty());

    // ... [code omitted for brevity]
}
```

The `org.springframework.context.annotation.ConfigurationClassParser#parse` method loops over all configuration class candidates and parses each annotated bean definition. This usually means finding the @Bean methods and component scan directives in the configuration class, and registering that information in the Spring container.

```java
public void parse(Set<BeanDefinitionHolder> configCandidates) {
    // ... [code omitted for brevity]
    parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
    // ... [code omitted for brevity]
}
```

The `org.springframework.context.annotation.ConfigurationClassParser#parse(metadata, beanName)` method turns the annotation metadata and the bean name into a configuration class, then processes it. Processing configuration classes is the core of Spring's configuration-driven setup. It covers many key tasks, such as handling the `@ComponentScan` annotation.

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

The `org.springframework.context.annotation.ConfigurationClassParser#doProcessConfigurationClass` method handles the `@PropertySource` annotations defined on a `ConfigurationClass` and makes sure they are added to the `ConfigurableEnvironment`. If the environment is not configurable (not an instance of `ConfigurableEnvironment`), the annotation is ignored and a message is logged.

```java
protected final SourceClass doProcessConfigurationClass(
			ConfigurationClass configClass, SourceClass sourceClass, Predicate<String> filter)
			throws IOException {

    // Process any @PropertySource annotations
    for (AnnotationAttributes propertySource : AnnotationConfigUtils.attributesForRepeatable(
        sourceClass.getMetadata(), PropertySources.class,
        org.springframework.context.annotation.PropertySource.class)) {
        
        // Check whether the current environment is an instance of ConfigurableEnvironment
        if (this.environment instanceof ConfigurableEnvironment) {
            // If so, process the property source
            processPropertySource(propertySource);
        } 
        else {
            // Otherwise, log a message saying the @PropertySource annotation is ignored
            logger.info("Ignoring @PropertySource annotation on [" + sourceClass.getMetadata().getClassName() +
                        "]. Reason: Environment must implement ConfigurableEnvironment");
        }
    }

    // No superclass -> processing is complete
    return null;
}
```

In the `org.springframework.context.annotation.ConfigurationClassParser#processPropertySource` method, it first reads the attributes of the `@PropertySource` annotation, such as `name`, `encoding`, `value`, and `factory`. Then it tries to load each given property file and creates a property source from the file content and the other attributes. Finally it adds this property source to the Spring environment.

```java
/**
 * Process the given @PropertySource annotation attributes.
 *
 * @param propertySource the attributes of the @PropertySource annotation
 * @throws IOException if an IO exception occurs during processing
 */
private void processPropertySource(AnnotationAttributes propertySource) throws IOException {
    
    // Get the "name" attribute. If it is not set, use null
    String name = propertySource.getString("name");
    if (!StringUtils.hasLength(name)) {
        name = null;
    }

    // Get the "encoding" attribute. If it is not set, use null
    String encoding = propertySource.getString("encoding");
    if (!StringUtils.hasLength(encoding)) {
        encoding = null;
    }

    // Get the "value" attribute, which holds the property file locations
    String[] locations = propertySource.getStringArray("value");
    // Make sure at least one location is given
    Assert.isTrue(locations.length > 0, "At least one @PropertySource(value) location is required");

    // Get the "ignoreResourceNotFound" attribute: whether to ignore a missing resource
    boolean ignoreResourceNotFound = propertySource.getBoolean("ignoreResourceNotFound");

    // Get and create the PropertySourceFactory used to create the property source
    Class<? extends PropertySourceFactory> factoryClass = propertySource.getClass("factory");
    PropertySourceFactory factory = (factoryClass == PropertySourceFactory.class ?
                                     DEFAULT_PROPERTY_SOURCE_FACTORY : BeanUtils.instantiateClass(factoryClass));

    // For each given location:
    for (String location : locations) {
        try {
            // Resolve placeholders in the location
            String resolvedLocation = this.environment.resolveRequiredPlaceholders(location);
            // Get the resource
            Resource resource = this.resourceLoader.getResource(resolvedLocation);
            // Add the property source to the environment
            addPropertySource(factory.createPropertySource(name, new EncodedResource(resource, encoding)));
        }
        catch (IllegalArgumentException | FileNotFoundException | UnknownHostException | SocketException ex) {
            // A placeholder cannot be resolved, or the resource is not found when opening it
            if (ignoreResourceNotFound) {
                // If missing resources are ignored, log a message
                if (logger.isInfoEnabled()) {
                    logger.info("Properties location [" + location + "] not resolvable: " + ex.getMessage());
                }
            }
            else {
                // Otherwise, throw the exception
                throw ex;
            }
        }
    }
}
```

In the `org.springframework.core.io.support.DefaultPropertySourceFactory#createPropertySource` method, if a name is given, it is used for the property source. If not, the property source is created from the resource alone. Its name may then come from the resource's own description or another default way.

```java
@Override
public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
    return (name != null ? new ResourcePropertySource(name, resource) : new ResourcePropertySource(resource));
}
```

In the `org.springframework.context.annotation.ConfigurationClassParser#addPropertySource` method, it first checks whether a property source with the same name was already added. If so, it merges the new property source with the existing one. If not, it adds the new property source in the right order.

```java
private void addPropertySource(PropertySource<?> propertySource) {
    // Get the name of the property source
    String name = propertySource.getName();
    // Get all property sources of the current environment
    MutablePropertySources propertySources = ((ConfigurableEnvironment) this.environment).getPropertySources();

    // If a property source with this name was already added
    if (this.propertySourceNames.contains(name)) {
        // A version was already added, so we need to extend it
        PropertySource<?> existing = propertySources.get(name);
        if (existing != null) {
            PropertySource<?> newSource = (propertySource instanceof ResourcePropertySource ?
                                           ((ResourcePropertySource) propertySource).withResourceName() : propertySource);
            // If the existing property source is a CompositePropertySource
            if (existing instanceof CompositePropertySource) {
                ((CompositePropertySource) existing).addFirstPropertySource(newSource);
            }
            else {
                // Create a new CompositePropertySource and add the new and existing property sources to it
                if (existing instanceof ResourcePropertySource) {
                    existing = ((ResourcePropertySource) existing).withResourceName();
                }
                CompositePropertySource composite = new CompositePropertySource(name);
                composite.addPropertySource(newSource);
                composite.addPropertySource(existing);
                propertySources.replace(name, composite);
            }
            return;
        }
    }

    // If no property source has been added yet
    if (this.propertySourceNames.isEmpty()) {
        propertySources.addLast(propertySource);
    }
    else {
        // Add the property source before the last processed one
        String firstProcessed = this.propertySourceNames.get(this.propertySourceNames.size() - 1);
        propertySources.addBefore(firstProcessed, propertySource);
    }
    // Add the property source name to the list of tracked names
    this.propertySourceNames.add(name);
}
```

### 8. Notes

1. **File location**
   + Make sure the file path you give is correct. For example, the `classpath:` prefix means the file is on the classpath. The `file:` prefix means the file is at a specific place in the file system.
2. **Placeholders**
   + In the `value` attribute of `@PropertySource`, you can use `${...}` placeholders. They are resolved against any property source already registered.
3. **Duplicate property source names**
   + If you have several `@PropertySource` annotations (or use the `@PropertySources` annotation) with the same name, they are merged. A `@PropertySource` declared later overrides an earlier one with the same name.
4. **Order of property sources**
   + The order of property sources matters. If the same property is defined in several property sources, the value found first is used. You can use the `name` attribute of `PropertySource` to set the name of the property source and control its order in the environment.
5. **Ignore missing resources**
   + Use the `ignoreResourceNotFound` attribute to choose whether an exception is thrown when the property file is not found. The default is `false`, so an exception is thrown if the file is missing. Set it to `true` to let Spring continue quietly when the file is missing.
6. **Character encoding**
   + Since Spring 4.3, the `@PropertySource` annotation has an `encoding` attribute. It lets you set a specific character encoding for the given resource.
7. **Custom property source factory**
   + If you need special logic to create property sources, use the `factory` attribute to set a custom `PropertySourceFactory`.
8. **Turning on placeholder resolution**
   + Using `@PropertySource` alone does not turn on placeholder resolution. To replace `${...}` placeholders in your bean definitions, you also need to add a `@Bean` definition for `PropertySourcesPlaceholderConfigurer`.
9. **Use with Profiles**
   + You can use `@PropertySource` with Spring's Profile feature to load different property files for different environments.

### 9. Summary

#### Best Practices Summary

1. **Initialize the context**
   + Use `AnnotationConfigApplicationContext` to start the Spring container. It lets you configure the Spring container with Java annotations.
2. **Define the configuration class**
   + Create a class named `MyConfiguration` and mark it with `@Configuration`. This shows it is a Java-based Spring configuration class.
3. **Specify the property source**
   + In the `MyConfiguration` class, the `@PropertySource` annotation sets the location of an external property file named `my-application.yml`.
4. **Define the properties**
   + The `my-application.yml` file defines two properties: `apiVersion` and `kind`.
5. **Load and read the properties**
   + After the application runs, the Spring `Environment` API reads these properties and prints them to the console.
6. **Result**
   + The properties loaded from the YAML file are shown on the console as `apiVersion = v1` and `kind = ConfigMap`.

#### Source Code Analysis Summary

1. **Context initialization**
   + The Spring context is initialized with `AnnotationConfigApplicationContext`. This class lets the Spring container be configured from Java annotations. The configuration class passed in is `MyConfiguration`. It defines the property source to load from.
2. **Handling the @PropertySource annotation**
   + While processing configuration classes, Spring checks each one for `@PropertySource` annotations. If it finds one, it extracts the data of the property source, such as its location and other attributes.
3. **Resource location resolution**
   + For each property file location given in `@PropertySource`, Spring tries to load and parse the file. It first resolves any placeholders, then tries to load the resource.
4. **Creating the property source**
   + Spring uses `DefaultPropertySourceFactory` to create a property source. This factory can create a property source from a given resource (such as a .properties or .yml file).
5. **Adding the property source to the environment**
   + Once the property source is created, it is added to Spring's runtime environment. If a property source with the same name already exists, the new one may be merged with the old one, or handled in a suitable way.
6. **Processing complete**
   + After all configuration classes and property sources are processed, the Spring context continues its normal startup. The properties are then available while the application runs.
