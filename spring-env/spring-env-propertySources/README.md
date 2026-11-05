## PropertySources

- [PropertySources](#propertysources)
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

   + The [PropertySource](spring-env/spring-env-propertySource/README.md) class is a key abstract class in Spring. It wraps configuration data from different sources, such as files, environment variables, and system properties. It gives one unified interface to these sources, so you can access different kinds of configuration data in the same way. Its core is the `getProperty(String name)` method, which returns the property value for a given name. In Spring's environment abstraction, `PropertySource` instances can be added to an `Environment` object. This lets you access and manage these properties easily in your application.

### 3. Description

`PropertySources` is a Spring interface. It represents and manages a set of property sources (`PropertySource` objects). These sources hold the configuration data of the application environment. The interface has methods to get, add, replace, and remove these property sources. This lets you access configuration from different places, such as environment variables, system properties, and config files, in a unified way.

### 4. Main Features

1. **Property source management**
   + You can add, remove, and replace property sources. This lets you adjust the application configuration at runtime.

2. **Property lookup**
   + It has methods to check whether a specific property exists in any registered property source.

3. **Property access**
   + You can get property values from a series of property sources. Property sources are ordered, so the lookup follows a set order, usually the order in which the sources were added.

4. **Property source enumeration**
   + You can list all registered property sources. This is very useful for debugging and analyzing the configuration environment.

5. **Layered property sources**
   + Property sources can be layered, so there are priorities and overrides. For example, an environment variable can override a system property with the same name.

6. **Custom property sources**
   + Since it is an interface, you can implement your own `PropertySources` with a more specific strategy for managing property sources.

### 5. Interface Source

`PropertySources` is a Spring interface. It is a container that manages a set of `PropertySource` objects. These objects stand for the property sources of the application, such as environment variables, system properties, or config files. The interface gives stream access to these sources, so you can work on them as an ordered stream. It also has a method to check whether a property source exists, and a method to get a property source by name.

```java
/**
 * Holder containing one or more {@link PropertySource} objects.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see PropertySource
 */
public interface PropertySources extends Iterable<PropertySource<?>> {

    /**
     * Return a sequential {@link Stream} containing the property sources.
     * @since 5.1
     */
    default Stream<PropertySource<?>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Return whether a property source with the given name is contained.
     * @param name the {@linkplain PropertySource#getName() name of the property source} to find
     */
    boolean contains(String name);

    /**
     * Return the property source with the given name, {@code null} if not found.
     * @param name the {@linkplain PropertySource#getName() name of the property source} to find
     */
    @Nullable
    PropertySource<?> get(String name);

}
```

### 6. Main Implementations

1. **MutablePropertySources**

   + The most used implementation. It gives a modifiable collection of property sources. You can add, replace, and remove property sources. It is the first choice for dynamic environment properties.

### 7. Best Practices

This example uses Spring's `MutablePropertySources` to manage application configuration properties. It creates a collection of property sources. It adds, replaces, and removes different property sources, and shows how to check whether a property source exists.

```java
public class PropertySourcesDemo {

    public static void main(String[] args) {
        // Create a MutablePropertySources object
        MutablePropertySources propertySources = new MutablePropertySources();

        // Create two MapPropertySource objects
        Map<String, Object> config1 = new HashMap<>();
        config1.put("key1", "value1");
        PropertySource<?> mapPropertySource1 = new MapPropertySource("config1", config1);

        Map<String, Object> config2 = new HashMap<>();
        config2.put("key2", "value2");
        PropertySource<?> mapPropertySource2 = new MapPropertySource("config2", config2);

        // Add a property source at the start
        propertySources.addFirst(mapPropertySource1);
        // Add a property source at the end
        propertySources.addLast(mapPropertySource2);

        // Print
        System.out.println("Print property sources");
        for (PropertySource<?> ps : propertySources) {
            System.out.printf("Name: %-10s || Source: %s%n", ps.getName(), ps.getSource());
        }
        System.out.println();

        // Replace a property source
        Map<String, Object> newConfig = new HashMap<>();
        newConfig.put("app.name", "Spring-Reading");
        newConfig.put("app.version", "1.0.0");
        PropertySource<?> newMapPropertySource = new MapPropertySource("config1", newConfig);
        propertySources.replace("config1", newMapPropertySource);

        // Print after replacing
        System.out.println("Print property sources after replacing");
        for (PropertySource<?> ps : propertySources) {
            System.out.printf("Name: %-10s || Source: %s%n", ps.getName(), ps.getSource());
        }
        System.out.println();

        // Check whether a property source exists
        System.out.println("Contains property source config2: " + propertySources.contains("config2"));

        // Remove a property source
        System.out.println("Remove property source config2: " + propertySources.remove("config2"));

        // Check again whether the property source exists
        System.out.println("Contains property source config2 after removal: " + propertySources.contains("config2"));
    }
}
```

The output shows how `MutablePropertySources` manages property sources flexibly. This includes add, replace, and remove operations, and checking whether a property source exists.

```java
Print property sources
Name: config1    || Source: {key1=value1}
Name: config2    || Source: {key2=value2}

Print property sources after replacing
Name: config1    || Source: {app.name=Spring-Reading, app.version=1.0.0}
Name: config2    || Source: {key2=value2}

Contains property source config2: true
Remove property source config2: MapPropertySource {name='config2'}
Contains property source config2 after removal: false
```

### 8. Relationship with Other Components

1. **`Environment`**

   + `PropertySources` is a core part of the `Environment` interface. It provides the configuration data of the application. In `Environment`, `PropertySources` is used to search and access property values from different sources, such as system properties, environment variables, and config files. This gives one unified way to access configuration properties.

2. **`PropertySource`**

   + `PropertySources` manages a set of `PropertySource` objects. Each `PropertySource` stands for one property source, such as a config file or a set of environment variables. `PropertySources` gives central access and management for these different sources.

3. **`PropertyResolver`**

   + `PropertySources` is closely related to the `PropertyResolver` interface, which resolves property values, for example by handling placeholders. When property values are handled and converted, `PropertySources` is usually combined with `PropertyResolver`. This makes property management more flexible and efficient.

4. **`ApplicationContext`**

   + In Spring's `ApplicationContext`, `PropertySources` is used to configure and manage the environment properties of the application. It works with the application context so you can define and access configuration settings flexibly. It is an important part of Spring application configuration.

5. **`Profile`**

   + `PropertySources` is linked to `Profile`, which defines the configuration of a specific environment. With `PropertySources`, you can store and access the properties of a specific `Profile`. You can also activate or deactivate a `Profile` at startup to control which configuration is loaded.

### 9. FAQ

1. **Duplicate or overridden property values**

   + When several property sources define the same property, values may be overridden unexpectedly. To fix this, check the priority and order of each property source in `PropertySources`. Make sure the expected source has the right priority.

2. **Property value not found**

   + Getting a property that does not exist may throw an exception. Make sure the needed property is defined in the right property source. Or use default values and condition checks to handle properties that may be missing.

3. **Environment dependency problems**

   + Different environments (such as development, test, and production) may need different property settings. Mixing them up can cause configuration errors. Use Spring Profiles or conditional annotations to separate the configuration of each environment.

4. **Property type mismatch**

   + Converting a property value to a data type that does not match may throw a type conversion exception. Make sure the value is compatible with the target type, or use a custom type converter.

5. **Updating property sources**

   + Updating property values at runtime can cause synchronization problems, or the update may not take effect. Use `MutablePropertySources` and make sure you synchronize properly and refresh the configuration. You can also consider an external configuration management system.

6. **Property resolution problems**

   + Resolving placeholders or expressions may fail. Check the syntax of property placeholders and make sure they can be resolved. Make sure all related property sources are loaded and configured correctly.
