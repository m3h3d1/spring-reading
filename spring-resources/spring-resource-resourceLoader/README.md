## ResourceLoader

- [ResourceLoader](#resourceloader)
  - [1. Overview](#1-overview)
  - [2. Prerequisites](#2-prerequisites)
  - [3. Description](#3-description)
  - [4. Main Features](#4-main-features)
  - [5. Interface Source](#5-interface-source)
  - [6. Main Implementations](#6-main-implementations)
  - [7. Best Practices](#7-best-practices)
  - [8. Relationship with Other Components](#8-relationship-with-other-components)
  - [9. FAQ](#9-faq)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Prerequisites

1. **Spring resource abstraction**

   - The [Resource](https://github.com/xuchengsheng/spring-reading/blob/master/spring-resources/spring-resource) interface and its implementations.

2. **Path and pattern resolution**

   - How Spring resolves paths, especially Ant-style path patterns such as `classpath*:com/example/**/*.xml`.

3. **Different resource types**

   - File resources, classpath resources, URL resources, resources inside a JAR, and so on.

### 3. Description

`org.springframework.core.io.ResourceLoader` is a key interface in Spring. It defines the strategy for getting resources, such as classpath resources, file system resources, or web resources. It is the core of Spring's resource loading abstraction. It lets an application load resources from different locations in the same way.

### 4. Main Features

1. **Unified resource loading**

   - `ResourceLoader` gives one standard way to load resources, whether they are on the classpath, in the file system, at a network URL, or somewhere else.

2. **Resource location resolution**

   - From the location string (such as "classpath:", "file:", "http:"), `ResourceLoader` can find the type of the resource and create the matching `Resource` instance.

3. **Returns a `Resource` instance**

   - With the `Resource getResource(String location)` method, `ResourceLoader` returns a `Resource` object for the given location. This makes reading and working with resources simple and consistent.

4. **Works with `ClassLoader`**

   - `ResourceLoader` returns its `ClassLoader` with the `ClassLoader getClassLoader()` method. This ties the loading strategy to a specific class loader.

5. **Extensible**

   - `ResourceLoader` is an interface. You can write your own loading strategy, or extend the default one for special needs.

6. **Built-in implementations**

   - Spring provides a default `ResourceLoader` implementation, `DefaultResourceLoader`. More importantly, `org.springframework.context.ApplicationContext` also implements `ResourceLoader`. So a Spring context is itself a resource loader.

### 5. Interface Source

The `ResourceLoader` interface defines the resource loading strategy for Spring. It has a method to get resources, and it exposes the `ClassLoader` it uses. With this strategy, resources can be loaded from many sources (classpath, file system, and so on) in the same way. This gives flexibility and consistency, and it supports many resource descriptors, such as URLs and classpath locations. It also lets you reuse and read a resource handle many times.

```java
/**
 * Strategy interface for loading resources (e.g., class path or file system resources).
 * An ApplicationContext is required to provide this functionality, plus extended ResourcePatternResolver support.
 * 
 * DefaultResourceLoader is a standalone implementation that is usable outside an ApplicationContext, and is also used by ResourceEditor.
 * 
 * Bean properties of type Resource and Resource[] can be populated from Strings when running in an ApplicationContext, using the particular context's resource loading strategy.
 *
 * @author Juergen Hoeller
 * @since 10.03.2004
 */
public interface ResourceLoader {

	/** Pseudo URL prefix for loading from the class path: "classpath:". */
	String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;

	/**
	 * Return a Resource handle for the specified resource location.
	 * The handle should always be a reusable resource descriptor, allowing for multiple Resource#getInputStream() calls.
	 * Must support fully qualified URLs, e.g. "file:C:/test.dat".
	 * Must support classpath pseudo-URLs, e.g. "classpath:test.dat".
	 * Should support relative file paths, e.g. "WEB-INF/test.dat".
	 * (This will be implementation-specific, typically provided by an ApplicationContext implementation.)
	 * Note that a Resource handle does not imply an existing resource; you need to call Resource#exists to check for existence.
	 *
	 * @param location the resource location
	 */
	Resource getResource(String location);

	/**
	 * Expose the ClassLoader used by this ResourceLoader.
	 * Clients which need to access the ClassLoader directly can do so in a uniform manner with the ResourceLoader, rather than relying on the thread context ClassLoader.
	 *
	 * @return the ClassLoader (only null if even the system ClassLoader isn't accessible)
	 */
	@Nullable
	ClassLoader getClassLoader();
}
```

### 6. Main Implementations

1. **`DefaultResourceLoader`**
   + This is the basic resource loader. It handles resources with the "classpath:" prefix. If there is no such prefix, it tries to load the resource with the class loader or the file system.

~~~mermaid
classDiagram
    direction BT
    class ResourceLoader {
    	<<interface>>
        + getResource(location) : Resource
        + getClassLoader() : ClassLoader
    }

    class DefaultResourceLoader {
        -ClassLoader classLoader
    }

    DefaultResourceLoader ..|> ResourceLoader

~~~

### 7. Best Practices

**`DefaultResourceLoader`**

Use `DefaultResourceLoader` to load content from different places (the classpath and the file system).

```java
public class DefaultResourceLoaderDemo {
    public static void main(String[] args) {
        DefaultResourceLoader loader = new DefaultResourceLoader();

        // Load a resource from the classpath
        Resource classpathResource = loader.getResource("classpath:application.properties");
        try (InputStream is = classpathResource.getInputStream()) {
            // Read and process the resource content
            System.out.println("Classpath = "+ new String(is.readAllBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load a resource from the file system
        Resource fileResource = loader.getResource("file:/idea-work-space-xcs/spring-reading/spring-resources/spring-resource-resourceLoader/myfile1.txt");
        try (InputStream is = fileResource.getInputStream()) {
            // Read and process the resource content
            System.out.println("File = "+ new String(is.readAllBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

Running it shows:

- From the classpath, we loaded a file: `application.properties`. This means the file is on the classpath of our project.
- From the file system, we loaded a file: `myfile1.txt`. The file is under the path `/idea-work-space-xcs/spring-reading/spring-resources/spring-resource-resourceLoader/` that we hard-coded in the code.

```java
Classpath Exists= true
File Exists = true
```

### 8. Relationship with Other Components

1. **`ApplicationContext`**
   + Every Spring `ApplicationContext` implements `ResourceLoader`. So you can use the Spring context itself to load resources.

2. **`Resource`**
   + `ResourceLoader` returns `Resource` objects. A `Resource` stands for a real resource, such as a file in the file system, a classpath resource, or a URL. `Resource` has methods to access and read the content.

3. **`ResourcePatternResolver`**
   + This extends `ResourceLoader`. It can resolve a location pattern to load many resources. `PathMatchingResourcePatternResolver` is its main implementation.

4. **`ResourceEditor`**
   + This is a property editor that turns strings into `Resource` objects. It uses `ResourceLoader` inside to do the conversion.

5. **`ResourceLoaderAware`**
   + This is a special interface. A bean that implements it gets a reference to the `ResourceLoader` when it is created, so it can load resources by itself.

6. **`EmbeddedValueResolverAware`**
   + Some components, such as the property placeholder processor, need to resolve dynamic parts of values. They can use `ResourceLoader` as part of this, especially when the value is a resource location.

7. **`PathMatchingResourcePatternResolver`**
   + It implements `ResourcePatternResolver`, which extends `ResourceLoader`. It handles resource patterns that start with "classpath*:". This loads all matching resources, not only the first one found.

### 9. FAQ

1. **Load a classpath resource**

   + Use the prefix "classpath:", for example: `loader.getResource("classpath:myconfig.xml")`.

2. **Load a file system resource**

   + Use the prefix "file:", for example: `loader.getResource("file:/path/to/myconfig.xml")`.

3. **Load a URL resource**

   + Use the URL directly, for example: `loader.getResource("http://www.example.com/config.xml")`.

4. **The resource does not exist**

   + Use `Resource.exists()` to check whether the resource exists. Make sure the path or location is correct and the resource is really at the expected place.

5. **How to read the resource content**

   + Get the `InputStream` from the `Resource` object, for example: `resource.getInputStream()`.

6. **Get a file path from a `Resource`**

   + Use `Resource.getFile()`. Note that this does not always work. For example, it fails when the resource is really a classpath resource inside a JAR, or a URL resource.

7. **Load many resources that match a pattern**

   + Use `ResourcePatternResolver` or its implementation `PathMatchingResourcePatternResolver`.

8. **Inject `ResourceLoader` automatically**

   + Implement the `ResourceLoaderAware` interface. Spring will give your bean a reference to the `ResourceLoader`.

9. **Extend or customize resource loading**

   + You can write your own `ResourceLoader` or extend an existing one, such as `DefaultResourceLoader`.

11. **Use environment or property placeholders when loading resources**

    + Use `PropertyPlaceholderConfigurer` or `PropertySourcesPlaceholderConfigurer` with the `@Value` annotation to resolve resource paths in property values.
