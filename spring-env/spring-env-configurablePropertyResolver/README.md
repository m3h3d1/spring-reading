## ConfigurablePropertyResolver

- [ConfigurablePropertyResolver](#configurablepropertyresolver)
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

1. **PropertyResolver**
   + The [PropertyResolver](/spring-env/spring-env-propertyResolver/README.md) interface is a core component of Spring. It gives a flexible and powerful way to handle application configuration properties. It defines methods to access and work with property values from many sources, such as property files, environment variables, and JVM arguments.

### 3. Description

The `ConfigurablePropertyResolver` interface gives flexible resolution of configuration properties in Spring. It reads and converts property values from many sources. It supports placeholder resolution to make configuration more dynamic. It converts types so that property values have the expected format. It can also check whether a property exists and handle default values, which makes code more robust.

### 4. Main Features

1. **Property value resolution**

   + Reads and resolves property values from many property sources (such as config files and environment variables).

2. **Placeholder resolution**

   + Supports placeholders in property values (such as `${property.name}`) and resolves them to the real configuration values. This makes configuration more dynamic and flexible.

3. **Type conversion**

   + Converts property values from one type to another. For example, from a string to an integer or a boolean.

4. **Default value handling**

   + Lets you set a default value for a property. This is useful when a configuration item may be missing.

5. **Property existence check**

   + Has methods to check whether a specific property exists. This helps you act when a configuration item may be missing.

6. **Property source management**

   + Manages different property sources, so you can adjust the configuration to the runtime environment of the application.

### 5. Interface Source

The `ConfigurablePropertyResolver` interface has methods to manage and handle configuration properties. It lets you set a custom conversion service for property value types, manage the placeholder prefix and suffix, handle the default value separator, and choose whether to ignore unresolvable nested placeholders.

```java
/**
 * Configuration interface to be implemented by most if not all {@link PropertyResolver}
 * types. Provides facilities for accessing and customizing the
 * {@link org.springframework.core.convert.ConversionService ConversionService}
 * used when converting property values from one type to another.
 *
 * @author Chris Beams
 * @since 3.1
 */
public interface ConfigurablePropertyResolver extends PropertyResolver {

    /**
     * Return the {@link ConfigurableConversionService} used when performing type
     * conversions on properties.
     * <p>The configuration of the returned conversion service may be modified in order
     * to support the conversion of custom types; the {@code Converter} instances can be
     * added and removed easily.
     * 
     * @return the conversion service currently in use
     */
    ConfigurableConversionService getConversionService();

    /**
     * Set the {@link ConfigurableConversionService} to be used when performing type
     * conversions on properties.
     * <p><strong>Note:</strong> as an alternative to fully replacing the
     * {@code ConversionService}, consider adding or removing individual
     * {@code Converter} instances by drilling into {@link #getConversionService()}
     * and calling methods such as {@code #addConverter}.
     */
    void setConversionService(ConfigurableConversionService conversionService);

    /**
     * Set the prefix that placeholders replaced by this resolver must begin with.
     */
    void setPlaceholderPrefix(String placeholderPrefix);

    /**
     * Set the suffix that placeholders replaced by this resolver must end with.
     */
    void setPlaceholderSuffix(String placeholderSuffix);

    /**
     * Specify the separating character between the placeholders replaced by this
     * resolver and their associated default value, or {@code null} if no such
     * special character should be processed as a value separator.
     */
    void setValueSeparator(@Nullable String valueSeparator);

    /**
     * Set whether to throw an exception when encountering an unresolvable placeholder
     * nested within the value of a given property. A {@code false} value indicates strict
     * resolution, i.e. that an exception will be thrown. A {@code true} value indicates
     * that unresolvable nested placeholders should be passed through in their unresolved
     * ${...} form.
     */
    void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders);

    /**
     * Specify which properties must be present, to be verified by
     * {@link #validateRequiredProperties()}.
     */
    void setRequiredProperties(String... requiredProperties);

    /**
     * Validate that each of the properties specified by
     * {@link #setRequiredProperties} is present and resolves to a
     * non-{@code null} value.
     * @throws MissingRequiredPropertiesException if any of the required
     * properties are not resolvable.
     */
    void validateRequiredProperties() throws MissingRequiredPropertiesException;

}
```

### 6. Main Implementations

1. **`AbstractPropertyResolver`**

   + An abstract base class. It is the base for most common implementations of `ConfigurablePropertyResolver`. It implements most methods, but as an abstract class it cannot be created directly.

2. **`PropertySourcesPropertyResolver`**

   + A concrete implementation of `ConfigurablePropertyResolver`. It resolves properties based on `PropertySources`. `PropertySources` is part of Spring's environment abstraction. It represents the sources of configuration data, such as environment variables, JVM properties, and config files.

### 7. Best Practices

This example uses Spring's `ConfigurablePropertyResolver` interface to manage and resolve configuration properties. First, we create and set up the property source. Then we create a `PropertySourcesPropertyResolver` as the property resolver. The code sets the conversion service for property values, defines the placeholder prefix and suffix, sets the default value separator, and sets how unresolvable placeholders are handled. It also sets and validates the required properties. At the end, it reads and prints the property values.

```java
public class ConfigurablePropertyResolverDemo {

    public static void main(String[] args) {
        // Create the property source
        Map<String, Object> properties = new HashMap<>();
        properties.put("app.name", "Spring-Reading");
        properties.put("app.version", "1.0.0");

        MapPropertySource propertySource = new MapPropertySource("myPropertySource", properties);
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addLast(propertySource);

        // Create a ConfigurablePropertyResolver
        PropertySourcesPropertyResolver propertyResolver = new PropertySourcesPropertyResolver(propertySources);

        // Set and get the conversion service
        ConfigurableConversionService conversionService = new DefaultConversionService();
        propertyResolver.setConversionService(conversionService);

        // Set the placeholder prefix and suffix
        propertyResolver.setPlaceholderPrefix("${");
        propertyResolver.setPlaceholderSuffix("}");

        // Set the default value separator
        propertyResolver.setValueSeparator(":");

        // Set how unresolvable placeholders are handled
        propertyResolver.setIgnoreUnresolvableNestedPlaceholders(true);

        // Set and validate required properties
        propertyResolver.setRequiredProperties("app.name", "app.version");
        propertyResolver.validateRequiredProperties();

        // Read properties
        String appName = propertyResolver.getProperty("app.name");
        String appVersion = propertyResolver.getProperty("app.version", String.class, "Unknown Version");
        System.out.println("Get property app.name: " + appName);
        System.out.println("Get property app.version: " + appVersion);
    }
}
```

The output shows that `PropertySourcesPropertyResolver` resolved the property values from the given property source. It also shows that the property source setup and the resolver usage in the code are correct.

```java
Get property app.name: Spring-Reading
Get property app.version: 1.0.0
```

### 8. Relationship with Other Components

1. **`Environment` **

   - The `Environment` interface extends `PropertyResolver`. `ConfigurableEnvironment` is a sub-interface of `Environment` that lets you configure the property resolver. In Spring, `Environment` wraps all configuration properties, including system properties, environment variables, and application config files.

   - `ConfigurablePropertyResolver` gives more flexible property resolution and conversion. It lets you customize how properties are resolved, such as the placeholder prefix and suffix and type conversion.

2. **`PropertySources` **

   - `PropertySources` is a container that holds many `PropertySource` objects. They stand for different property sources, such as config files, environment variables, and command-line arguments.

   - `ConfigurablePropertyResolver` is usually used with `PropertySources` to resolve property values from these sources.

3. **`PropertySource` **

   - `PropertySource` is the abstraction of one property source, such as a config file or a set of environment variables.

   - `ConfigurablePropertyResolver` uses `PropertySource` (through `PropertySources`) to get the real property values.

4. **`ConversionService` **

   - `ConversionService` converts a value of one type to another type in Spring.

   - You can set a custom `ConversionService` on `ConfigurablePropertyResolver` to control how property values are converted. This is very useful for non-string configuration properties.

5. **`Profile` and the `@Conditional` annotation**

   - In Spring, config files and components can activate different configuration for different environments (development, test, production, and so on).

   - `ConfigurablePropertyResolver` can be used to check which config files or `Profile`s are active, and to decide which conditional annotations (such as `@Conditional`) should be met.

### 9. FAQ

1. **Placeholder cannot be resolved**

   + If a placeholder in a property value (such as `${property.name}`) cannot be resolved, make sure the placeholder prefix and suffix are set correctly. Also make sure the related property is defined in a property source.

2. **Type conversion error**

   + If you get a type conversion exception, check that the right `ConversionService` is set on the `ConfigurablePropertyResolver` and that the needed type converters are registered.

3. **Property values overridden**

   + If several property sources have a property with the same name, values may be overridden. Learn and manage the priority of property sources. Make sure they are added to the environment in the right order.

4. **Confusion with default values**

   + When you use methods with a default value (such as `getProperty(String, Class, T defaultValue)`), clearly tell apart an empty value in a property source and an undefined property. This avoids confusion.

5. **Missing required properties**

   + If `validateRequiredProperties` throws an exception because a required property is missing, check at application startup or configuration time that all required properties are defined in the property sources.

6. **Inconsistent environments and configuration**

   + If configuration differs between environments (development, test, production), use Spring Profiles to manage the configuration of each environment. Make sure each environment has the right config files and property settings.

7. **Concurrency problems**

   + When you use `ConfigurablePropertyResolver` in a multi-threaded environment, make sure access to the property sources is thread-safe. Or resolve all required properties in advance when the application starts.
