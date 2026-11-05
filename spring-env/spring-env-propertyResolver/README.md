## PropertyResolver

- [PropertyResolver](#propertyresolver)
  - [1. Overview](#1-overview)
  - [2. Background Knowledge](#2-background-knowledge)
  - [3. Description](#3-description)
  - [4. Main Features](#4-main-features)
  - [5. Interface Source](#5-interface-source)
  - [6. Main Implementations](#6-main-implementations)
  - [7. Best Practices](#7-best-practices)
  - [8. Relationship with Other Components](#8-relationship-with-other-components)
  - [9. FAQ](#9-faq)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Background Knowledge

1. **PropertySource**

   - The [PropertySource](https://github.com/xuchengsheng/spring-reading/blob/master/spring-env/spring-env-propertySources/spring-env/spring-env-propertySource/README.md) class is a key abstract class in Spring. It wraps configuration data from different sources, such as files, environment variables, and system properties. It gives one unified interface to these sources, so you can access different kinds of configuration data in the same way. Its core is the `getProperty(String name)` method, which returns the property value for a given name. In Spring's environment abstraction, `PropertySource` instances can be added to an `Environment` object. This lets you access and manage these properties easily in your application.

### 3. Description

The `PropertyResolver` interface is a core component of Spring. It gives a flexible and powerful way to handle application configuration properties. It defines methods to access and work with property values from many sources, such as property files, environment variables, and JVM arguments.

### 4. Main Features

1. **Get property values**

   + The `getProperty(key)` method returns the property value for a given key. This is the most common feature when you handle configuration data.

2. **Get properties with a default value**

   + If the property key does not exist, `getProperty(key, defaultValue)` returns a default value.

3. **Property value type conversion**
   + The `getProperty(key, targetType)` and `getProperty(key,targetType,defaultValue)` methods convert the property value to a given type, for example from a string to an integer or boolean.
  
4. **Check property existence**

   + The `containsProperty(key)` method tells whether a property key exists.

5. **Get required properties**

   + The `getRequiredProperty(key)` and `getRequiredProperty(key, targetType)` methods get property values that must exist. If the property is missing, they throw an exception.

6. **Resolve placeholders**

   + The `resolvePlaceholders(text)` method resolves placeholders in a string and replaces them with the matching property values. This is useful for config files with dynamic content.

### 5. Interface Source

The `PropertyResolver` interface has methods to resolve property values. They check whether a property exists, get property values, support default values, and convert types. It also has methods to resolve placeholders in strings, so configuration values can be replaced dynamically.

```java
/**
 * Interface for resolving properties against any underlying source.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see Environment
 * @see PropertySourcesPropertyResolver
 */
public interface PropertyResolver {

    /**
     * Return whether the given property key is available for resolution,
     * i.e. if the value for the given key is not {@code null}.
     */
    boolean containsProperty(String key);

    /**
     * Return the property value associated with the given key,
     * or {@code null} if the key cannot be resolved.
     * @param key the property name to resolve
     */
    @Nullable
    String getProperty(String key);

    /**
     * Return the property value associated with the given key, or
     * {@code defaultValue} if the key cannot be resolved.
     * @param key the property name to resolve
     * @param defaultValue the default value to return if no value is found
     */
    String getProperty(String key, String defaultValue);

    /**
     * Return the property value associated with the given key,
     * or {@code null} if the key cannot be resolved.
     * @param key the property name to resolve
     * @param targetType the expected type of the property value
     */
    @Nullable
    <T> T getProperty(String key, Class<T> targetType);

    /**
     * Return the property value associated with the given key,
     * or {@code defaultValue} if the key cannot be resolved.
     * @param key the property name to resolve
     * @param targetType the expected type of the property value
     * @param defaultValue the default value to return if no value is found
     */
    <T> T getProperty(String key, Class<T> targetType, T defaultValue);

    /**
     * Return the property value associated with the given key (never {@code null}).
     * @throws IllegalStateException if the key cannot be resolved
     */
    String getRequiredProperty(String key) throws IllegalStateException;

    /**
     * Return the property value associated with the given key, converted to the given
     * targetType (never {@code null}).
     * @throws IllegalStateException if the given key cannot be resolved
     */
    <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException;

    /**
     * Resolve ${...} placeholders in the given text, replacing them with corresponding
     * property values as resolved by {@link #getProperty}. Unresolvable placeholders with
     * no default value are ignored and passed through unchanged.
     * @param text the String to resolve
     * @return the resolved String (never {@code null})
     * @throws IllegalArgumentException if given text is {@code null}
     */
    String resolvePlaceholders(String text);

    /**
     * Resolve ${...} placeholders in the given text, replacing them with corresponding
     * property values as resolved by {@link #getProperty}. Unresolvable placeholders with
     * no default value will cause an IllegalArgumentException to be thrown.
     * @return the resolved String (never {@code null})
     * @throws IllegalArgumentException if given text is {@code null}
     * or if any placeholders are unresolvable
     */
    String resolveRequiredPlaceholders(String text) throws IllegalArgumentException;

}
```

### 6. Main Implementations

1. **`PropertySourcesPropertyResolver`**

   + One of the most used `PropertyResolver` implementations. It uses `PropertySource` objects (possibly many, such as environment variables, JVM properties, and config files) to resolve properties.

2. **`AbstractEnvironment`**

   + `AbstractEnvironment` does not implement `PropertyResolver` directly, but it gives access to `PropertyResolver` features. It provides property resolution through an internal `PropertySourcesPropertyResolver` instance.

3. **`StandardEnvironment` and `ConfigurableEnvironment`**

   + These classes extend `AbstractEnvironment` and give more specific environment configuration. They provide `PropertyResolver` features indirectly, by inheriting from `AbstractEnvironment`.

### 7. Best Practices

This example uses `PropertyResolver` to show how configuration properties are managed and resolved. We create a property source with application info. With `PropertySourcesPropertyResolver`, we get and check properties, handle default values, and resolve strings that contain placeholders.

```java
public class SimplePropertyResolverDemo {

    public static void main(String[] args) {
        // Create the property source
        Map<String, Object> properties = new HashMap<>();
        properties.put("app.name", "Spring-Reading");
        properties.put("app.version", "1.0.0");
        properties.put("app.description", "This is a ${app.name} with version ${app.version}");

        MapPropertySource propertySource = new MapPropertySource("myPropertySource", properties);
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addLast(propertySource);

        // Use PropertySourcesPropertyResolver
        PropertyResolver propertyResolver = new PropertySourcesPropertyResolver(propertySources);

        // Get property
        String appName = propertyResolver.getProperty("app.name");
        String appVersion = propertyResolver.getProperty("app.version");
        System.out.println("Get property app.name: " + appName);
        System.out.println("Get property app.version: " + appVersion);

        // Check whether the properties exist
        boolean containsDescription = propertyResolver.containsProperty("app.description");
        boolean containsReleaseDate = propertyResolver.containsProperty("app.releaseDate");
        System.out.println("Contains 'app.description' property: " + containsDescription);
        System.out.println("Contains 'app.releaseDate' property: " + containsReleaseDate);

        // Get property with default value
        String appReleaseDate = propertyResolver.getProperty("app.releaseDate", "2023-11-30");
        System.out.println("Get property with default value app.releaseDate : " + appReleaseDate);

        // Get required property
        String requiredAppName = propertyResolver.getRequiredProperty("app.name");
        System.out.println("Get required property app.name: " + requiredAppName);

        // Resolve placeholders
        String appDescription = propertyResolver.resolvePlaceholders(properties.get("app.description").toString());
        System.out.println("Resolve placeholders app.description: " + appDescription);

        // Resolve required placeholders
        String requiredAppDescription = propertyResolver.resolveRequiredPlaceholders("App Description: ${app.description}");
        System.out.println("Resolve required placeholders : " + requiredAppDescription);
    }
}
```

The output shows that `PropertyResolver` got property values from the configuration, checked whether properties exist, provided a default value, and resolved strings with placeholders.

```java
Get property app.name: Spring-Reading
Get property app.version: 1.0.0
Contains 'app.description' property: true
Contains 'app.releaseDate' property: false
Get property with default value app.releaseDate : 2023-11-30
Get required property app.name: Spring-Reading
Resolve placeholders app.description: This is a Spring-Reading with version 1.0.0
Resolve required placeholders : App Description: This is a Spring-Reading with version 1.0.0
```

### 8. Relationship with Other Components

1. **`Environment` abstraction**

   - `PropertyResolver` is part of the `Environment` interface. `Environment` is a fuller interface for configuration and property management. It abstracts property sources, such as system environment variables, JVM properties, and config files.

   - In practice, when you use an `Environment` object, you also use `PropertyResolver` features, because `Environment` extends `PropertyResolver`.

2. **`PropertySource(s)` and `PropertySourcesPropertyResolver`**

   - A `PropertySource` is the origin of properties, such as a `.properties` file or environment variables. `MutablePropertySources` is a container that holds many `PropertySource` objects.

   - `PropertySourcesPropertyResolver` is a concrete implementation of `PropertyResolver`. It resolves properties provided by one or more `PropertySource` objects.

3. **Placeholder resolution**

   - `PropertyResolver` can resolve placeholders (such as `${property.name}`). This is very useful when you handle config files.

   - It works with Spring's `PropertyPlaceholderConfigurer` or the `@Value` annotation to inject property values from config files into Spring-managed beans.

4. **Configuration classes and annotations**

   - With annotation-based configuration classes (such as classes marked with `@Configuration`), `PropertyResolver` can resolve and inject property values dynamically. This is most useful together with the `@PropertySource` annotation.
   
   - For example, you can use `@Value("${property.name}")` to inject a property value into a field or method of a configuration class.

5. **Profile management**

   - `PropertyResolver` is closely tied to the concept of Spring Profiles. With it, you can easily access and check the active profiles. This helps you load specific configuration for different environments (development, test, production, and so on).

### 9. FAQ

1. **Property value not found**

   + If you resolve a property that does not exist, call `containsProperty` first to check whether it exists. Also check that the property name is correct and that the property source contains the property.

2. **Property type conversion error**

   + When you convert a property value to another type, make sure the value format fits the target type. If the format does not match, use a type conversion service (such as Spring's `ConversionService`) to convert it explicitly.

3. **Placeholder not resolved**

   + If a `${...}` placeholder in a property value is not resolved, make sure you use a suitable `PropertyResolver` implementation (such as `PropertySourcesPropertyResolver`). Also make sure the property sources contain the properties that the placeholder refers to.

4. **Environment-specific properties**

   + To handle properties for different environments (development, test, production), use Spring Profiles to define environment-specific properties. Make sure the right Profile is active when the application starts.

5. **Config file loading problems**

   + If a property file is not loaded, check that its path and format are correct. Also make sure the `@PropertySource` annotation in the Spring configuration class points to the property file.

6. **Errors when injecting properties with `@Value`**

   + When you inject property values with `@Value`, make sure `PropertyPlaceholderConfigurer` or `PropertySourcesPlaceholderConfigurer` is configured correctly and loaded into the Spring container.
