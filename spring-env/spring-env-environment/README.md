## Environment

- [Environment](#environment)
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

2. **ConfigurablePropertyResolver**
   + The [ConfigurablePropertyResolver](/spring-env/spring-env-configurablePropertyResolver/README.md) interface gives flexible resolution of configuration properties in Spring. It reads and converts property values from many sources. It supports placeholder resolution to make configuration more dynamic. It converts types so that property values have the expected format. It can also check whether a property exists and handle default values, which makes code more robust.

### 3. Description

The `Environment` interface is a core part of Spring. It gives one unified way to access external configuration data, such as environment variables, JVM system properties, command-line arguments, and application config files (such as properties or YAML files). It lets you read and work with this data at runtime. It also supports the concept of profiles. Profiles make it easy to configure things conditionally for different environments (such as development, test, and production). With `Environment`, you can query and work with property values, check whether a property exists, and handle many property sources.

### 4. Main Features

1. **Property access**

   + It reads property values from different property sources (such as environment variables, system properties, and config files). It has methods to get string values or type-safe values.

2. **Property source management**

   + The `Environment` abstraction supports many property sources and searches them in priority order. You can load properties from several places (such as different config files and environment variables) and override them as needed.

3. **Profiles**

   + It supports profiles, a way to separate configuration for different environments (such as development, test, and production). The application can change its behavior based on the active profiles.

4. **Property existence check**

   + It has methods to check whether a specific property exists. This helps you write more robust code when a property may be missing.

5. **Type-safe property access**

   + Besides the normal property methods, `Environment` has type-safe methods. They convert the property value directly to the expected type.

6. **Required properties**

   + You can get properties that are marked as required. If they are missing, an exception is thrown. This helps catch configuration errors at startup.

### 5. Interface Source

The `Environment` interface is the main model of the application environment in Spring. It focuses on two things: profiles and properties. A profile is a named, logical group of bean definitions. These beans are registered only when the profile is active. Properties are configuration data from many sources, such as files and environment variables. `Environment` has methods to activate and manage profiles, and to access and resolve properties.

```java
/**
 * Interface representing the environment in which the current application is running.
 * Models two key aspects of the application environment: <em>profiles</em> and
 * <em>properties</em>. Methods related to property access are exposed via the
 * {@link PropertyResolver} superinterface.
 *
 * <p>A <em>profile</em> is a named, logical group of bean definitions to be registered
 * with the container only if the given profile is <em>active</em>. Beans may be assigned
 * to a profile whether defined in XML or via annotations; see the spring-beans 3.1 schema
 * or the {@link org.springframework.context.annotation.Profile @Profile} annotation for
 * syntax details. The role of the {@code Environment} object with relation to profiles is
 * in determining which profiles (if any) are currently {@linkplain #getActiveProfiles
 * active}, and which profiles (if any) should be {@linkplain #getDefaultProfiles active
 * by default}.
 *
 * <p><em>Properties</em> play an important role in almost all applications, and may
 * originate from a variety of sources: properties files, JVM system properties, system
 * environment variables, JNDI, servlet context parameters, ad-hoc Properties objects,
 * Maps, and so on. The role of the environment object with relation to properties is to
 * provide the user with a convenient service interface for configuring property sources
 * and resolving properties from them.
 *
 * <p>Beans managed within an {@code ApplicationContext} may register to be {@link
 * org.springframework.context.EnvironmentAware EnvironmentAware} or {@code @Inject} the
 * {@code Environment} in order to query profile state or resolve properties directly.
 *
 * <p>In most cases, however, application-level beans should not need to interact with the
 * {@code Environment} directly but instead may have to have {@code ${...}} property
 * values replaced by a property placeholder configurer such as
 * {@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 * PropertySourcesPlaceholderConfigurer}, which itself is {@code EnvironmentAware} and
 * as of Spring 3.1 is registered by default when using
 * {@code <context:property-placeholder/>}.
 *
 * <p>Configuration of the environment object must be done through the
 * {@code ConfigurableEnvironment} interface, returned from all
 * {@code AbstractApplicationContext} subclass {@code getEnvironment()} methods. See
 * {@link ConfigurableEnvironment} Javadoc for usage examples demonstrating manipulation
 * of property sources prior to application context {@code refresh()}.
 *
 * @author Chris Beams
 * @since 3.1
 * @see PropertyResolver
 * @see EnvironmentCapable
 * @see ConfigurableEnvironment
 * @see AbstractEnvironment
 * @see StandardEnvironment
 * @see org.springframework.context.EnvironmentAware
 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment
 * @see org.springframework.context.ConfigurableApplicationContext#setEnvironment
 * @see org.springframework.context.support.AbstractApplicationContext#createEnvironment
 */
public interface Environment extends PropertyResolver {

	/**
	 * Return the set of profiles explicitly made active for this environment. Profiles
	 * are used for creating logical groupings of bean definitions to be registered
	 * conditionally, for example based on deployment environment. Profiles can be
	 * activated by setting {@linkplain AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
	 * "spring.profiles.active"} as a system property or by calling
	 * {@link ConfigurableEnvironment#setActiveProfiles(String...)}.
	 * <p>If no profiles have explicitly been specified as active, then any
	 * {@linkplain #getDefaultProfiles() default profiles} will automatically be activated.
	 * @see #getDefaultProfiles
	 * @see ConfigurableEnvironment#setActiveProfiles
	 * @see AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
	 */
	String[] getActiveProfiles();

	/**
	 * Return the set of profiles to be active by default when no active profiles have
	 * been set explicitly.
	 * @see #getActiveProfiles
	 * @see ConfigurableEnvironment#setDefaultProfiles
	 * @see AbstractEnvironment#DEFAULT_PROFILES_PROPERTY_NAME
	 */
	String[] getDefaultProfiles();

	/**
	 * Return whether one or more of the given profiles is active or, in the case of no
	 * explicit active profiles, whether one or more of the given profiles is included in
	 * the set of default profiles. If a profile begins with '!' the logic is inverted,
	 * i.e. the method will return {@code true} if the given profile is <em>not</em> active.
	 * For example, {@code env.acceptsProfiles("p1", "!p2")} will return {@code true} if
	 * profile 'p1' is active or 'p2' is not active.
	 * @throws IllegalArgumentException if called with zero arguments
	 * or if any profile is {@code null}, empty, or whitespace only
	 * @see #getActiveProfiles
	 * @see #getDefaultProfiles
	 * @see #acceptsProfiles(Profiles)
	 * @deprecated as of 5.1 in favor of {@link #acceptsProfiles(Profiles)}
	 */
	@Deprecated
	boolean acceptsProfiles(String... profiles);

	/**
	 * Return whether the {@linkplain #getActiveProfiles() active profiles}
	 * match the given {@link Profiles} predicate.
	 */
	boolean acceptsProfiles(Profiles profiles);

}
```

### 6. Main Implementations

1. **AbstractEnvironment**

   + The abstract base class of the `Environment` interface. It gives the common implementation that other classes extend.

2. **StandardEnvironment**

   + A general implementation. It handles system properties and environment variables. It suits most standard applications.

3. **StandardServletEnvironment**

   + For Servlet-based web applications. It adds support for the Servlet context and config parameters.

### 7. Best Practices

This example uses Spring's `StandardEnvironment` to simulate profile activation and property access in a Java program. It sets and shows the active profile ("test") and the default profile ("dev"). It checks whether a specific profile ("test") is active. It also gets and prints the Java version of the system.

```java
public class EnvironmentDemo {

    public static void main(String[] args) {
        // Set a system property to simulate Spring profile behavior
        System.setProperty("spring.profiles.default", "dev");
        System.setProperty("spring.profiles.active", "test");

        // Create a StandardEnvironment instance to access properties and profile information
        Environment environment = new StandardEnvironment();

        // Use getProperty to read a system property. Here it reads the Java version
        String javaVersion = environment.getProperty("java.version");
        System.out.println("java.version: " + javaVersion);

        // Get the active profiles
        String[] activeProfiles = environment.getActiveProfiles();
        System.out.println("activeProfiles = " + String.join(",", activeProfiles));

        // Get the default profiles (used when no profile is active)
        String[] defaultProfiles = environment.getDefaultProfiles();
        System.out.println("defaultProfiles = " + String.join(",", defaultProfiles));

        // Check whether a given profile is active. Here it checks the 'test' profile
        boolean isDevProfileActive = environment.acceptsProfiles("test");
        System.out.println("acceptsProfiles('test'): " + isDevProfileActive);
    }
}
```

The output shows that `StandardEnvironment` can simulate and manage Spring profiles and access system properties. It works even without a full Spring application context.

```java
java.version: 11
activeProfiles = test
defaultProfiles = dev
acceptsProfiles('test'): true
```

### 8. Relationship with Other Components

1. **ApplicationContext**

   - When the `ApplicationContext` (the Spring container) starts, it creates and uses an `Environment` to load and handle profiles and properties. It lets you access and customize the `Environment`. For example, you can get or replace the default `Environment` through the `ApplicationContext`.

2. **PropertySourcesPlaceholderConfigurer**

   - This is a `BeanFactoryPostProcessor` that resolves property placeholders (such as `${property.name}`). It works with `Environment` and resolves property values from the property sources that `Environment` manages.

3. **@PropertySource** and **PropertySources**

   - The `@PropertySource` annotation and the `PropertySources` class declare property sources (such as .properties or .yml files). These sources are added to the `Environment`, so configuration properties can be read from them.

4. **@ConfigurationProperties**

   - This annotation usually binds configuration properties to a class with fields. A `@ConfigurationProperties` class can use `Environment` to get the property values and map them to its fields.

### 9. FAQ

1. **Property value not found**

   + If you read a property that does not exist, you may get `null` or an exception. To fix this, make sure the property name is correct. Also check that the property is defined in the right property source.

2. **Profile not activated correctly**

   + If a profile is not active as expected, check how it is activated (for example through environment variables or JVM arguments). Also check that the `@Profile` annotation is applied to the right beans.

3. **Property type conversion error**

   + If converting a property value to the target type fails, make sure the value can be converted to the needed type. You can also define a custom converter.

4. **Placeholder not resolved**

   + If a property placeholder (such as `${property.name}`) is not replaced by the real value, check that `PropertySourcesPlaceholderConfigurer` or a similar mechanism is configured in the Spring context.

5. **Property source priority**

   + If the same property exists in several property sources and you get an unexpected value, learn and manage the priority of the property sources. Make sure the right source is read first.
