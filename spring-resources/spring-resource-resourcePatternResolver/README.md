### ResourcePatternResolver

- [ResourcePatternResolver](#resourcepatternresolver)
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

+ **`ResourceLoader`**

  + [ResourceLoader](https://github.com/xuchengsheng/spring-reading/blob/master/spring-resources/spring-resource-resourceLoader/README.md) is a key interface in Spring. It defines the strategy for getting resources, such as classpath resources, file system resources, or web resources. It is the core of Spring's resource loading abstraction. It lets an application load resources from different locations in the same way.

### 3. Description

`ResourcePatternResolver` is a Spring interface that extends `ResourceLoader`. It resolves resource patterns and can find many resources by pattern matching. A common implementation is `PathMatchingResourcePatternResolver`. It works with the classpath, the file system, URLs, and other locations, and returns the resources that match a given pattern. When you call `getResources(String locationPattern)`, you can use a pattern with wildcards, such as `classpath*:com/example/**/*.xml`, to get an array of matching resources. This is a flexible way to load and handle resource files that follow a pattern, such as configuration files and template files.

### 4. Main Features

1. **Resource pattern resolution** 

   + With `getResources(String locationPattern)`, you can use resource patterns with wildcards, such as `classpath*:com/example/**/*.xml`, to find many resources that match a pattern.

2. **Getting resources** 

   + With `getResources(Resource location)`, you get an array of matching resources for the given resource object. This lets you get other resources related to a given resource, for example all resources related to a file under a given classpath.

3. **Many resource locations** 

   + It handles different locations, including the classpath, the file system, and URLs. An application can organize and store resources in different ways without changing how they are found and loaded.

4. **Flexible resource loading** 

   + Together with the abilities of `ResourceLoader`, `ResourcePatternResolver` lets an application load all kinds of resources in the same way. You do not need to care where or in what form they are stored.

5. **Common resource operations** 

   + Through the `Resource` interface, it offers common operations on resources, such as getting the URL, the input stream, or the file handle.

### 5. Interface Source

`ResourcePatternResolver` is the Spring strategy interface that resolves a location pattern into resource objects. It extends the `ResourceLoader` interface. It can find resources that match a pattern on the classpath, in the file system, at a URL, and so on.

```java
/**
 * Strategy interface for resolving a location pattern (for example, an Ant-style path pattern) into Resource objects.
 *
 * This is an extension to the org.springframework.core.io.ResourceLoader interface. A passed-in
 * ResourceLoader (for example, an org.springframework.context.ApplicationContext passed in via
 * org.springframework.context.ResourceLoaderAware when running in a context) can be checked
 * whether it implements this extended interface too.
 *
 * PathMatchingResourcePatternResolver is a standalone implementation that is usable
 * outside an ApplicationContext, also used by ResourceArrayPropertyEditor for
 * populating Resource array bean properties.
 *
 * Can be used with any sort of location pattern (e.g. "/WEB-INF/*-context.xml"): The input pattern must match the strategy implementation.
 * This interface just specifies the conversion method rather than a specific pattern format.
 *
 * This interface also suggests a new resource prefix "classpath*:" for all matching resources from the class path. Note that
 * the resource location is expected to be a path without placeholders in this case (e.g. "/beans.xml"); JAR files or different directories in the class path can contain multiple files of the same name.
 *
 * @author Juergen Hoeller
 * @since 1.0.2
 * @see org.springframework.core.io.Resource
 * @see org.springframework.core.io.ResourceLoader
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ResourceLoaderAware
 */
public interface ResourcePatternResolver extends ResourceLoader {

    /**
     * Pseudo URL prefix for all matching resources from the class path: "classpath*:"
     * This differs from ResourceLoader's classpath URL prefix in that it retrieves all
     * matching resources for a given name (e.g. "/beans.xml"), for example in the root of all deployed JAR files.
     * See org.springframework.core.io.ResourceLoader#CLASSPATH_URL_PREFIX
     */
    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    /**
     * Resolve the given location pattern into Resource objects.
     * Overlapping resource entries that point to the same physical resource should be avoided, as far as possible. The result should have set semantics.
     * @param locationPattern the location pattern to resolve
     * @return the corresponding Resource objects
     * @throws IOException in case of I/O errors
     */
    Resource[] getResources(String locationPattern) throws IOException;

}
```

### 6. Main Implementations

+ PathMatchingResourcePatternResolver
  + Supports Ant-style path patterns, such as `classpath*:com/example/**/*.xml`. This lets you find resources on the classpath that match the given pattern.

~~~mermaid
classDiagram
    direction BT
    class ResourceLoader {
    	<<interface>>
        + getResource(location) : Resource
        + getClassLoader() : ClassLoader
    }

    class ResourcePatternResolver {
    	<<interface>>
        +getResources(locationPattern) : Resource[]
    }

    class PathMatchingResourcePatternResolver {
        -ResourceLoader resourceLoader
    }

    ResourcePatternResolver --|> ResourceLoader
    PathMatchingResourcePatternResolver ..|> ResourcePatternResolver

~~~

### 7. Best Practices

Use `PathMatchingResourcePatternResolver` to load resources that match a given pattern.

```java
public class PathMatchingResourcePatternResolverDemo {
    public static void main(String[] args) throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // Load all matching classpath resources
        Resource[] resources = resolver.getResources("classpath*:*.properties");
        for (Resource resource : resources) {
            System.out.println("Classpath = " + resource.getFilename());
        }

        // Load all matching file system resources
        Resource[] fileResources = resolver.getResources("file:/idea-work-space-xcs/spring-reading/spring-resources/spring-resource-resourceLoader/*.txt");
        for (Resource resource : fileResources) {
            System.out.println("File = " + resource.getFilename());
        }
    }
}
```

Running it shows:

- From the classpath, we loaded two files: `application.properties` and `bootstrap.properties`. This means these two files are on the classpath of our project.
- From the file system, we loaded three files: `myfile1.txt`, `myfile2.txt`, and `myfile3.txt`. They are under the path `/idea-work-space-xcs/spring-reading/spring-resources/spring-resource-resourceLoader/` that we hard-coded in the code.

```java
Classpath = application.properties
Classpath = bootstrap.properties
File = myfile1.txt
File = myfile2.txt
File = myfile3.txt
```

### 8. Relationship with Other Components

1. **BeanDefinitionLoader:**

   - `BeanDefinitionLoader` is used in Spring to load `BeanDefinition`s. It is an abstraction of the loading process. During loading, `PathMatchingResourcePatternResolver` is used to resolve resource patterns.

2. **MessageSourceAutoConfiguration:**

   - `MessageSourceAutoConfiguration` is an auto-configuration class in Spring Boot. It configures the `MessageSource`. The configuration may load internationalization resource files, and `PathMatchingResourcePatternResolver` resolves the paths of these files.

3. **AbstractBeanDefinitionReader:**

   - `AbstractBeanDefinitionReader` is the abstract class in Spring that reads `BeanDefinition`s. Its implementations (such as `XmlBeanDefinitionReader`) may use `PathMatchingResourcePatternResolver` when they load XML files.

4. **ClassPathScanningCandidateComponentProvider:**

   - `ClassPathScanningCandidateComponentProvider` is the Spring class that scans the classpath for components. It uses `PathMatchingResourcePatternResolver` to resolve resources on the classpath.

5. **AbstractApplicationContext:**

   - `AbstractApplicationContext` is the abstract implementation of the `ApplicationContext` interface. It uses `PathMatchingResourcePatternResolver` in the resource loading phase.

6. **ResourcePatternUtils:**

   - `ResourcePatternUtils` offers helper methods for resource patterns. This class uses `PathMatchingResourcePatternResolver` to resolve resource patterns.

7. **ResourceArrayPropertyEditor:**

   - `ResourceArrayPropertyEditor` is the property editor in Spring for properties of type `Resource` array. To handle these properties, it needs to resolve resource patterns, and `PathMatchingResourcePatternResolver` provides this.

### 9. FAQ

1. **The resource pattern does not match**

   + If your pattern does not match the resources you expect, the pattern itself may be wrong, or the resource location may be wrong. Make sure the pattern matches the real resource paths, and check for typos.

2. **Classpath resources cannot be loaded** 

   + If you use a classpath prefix (such as "classpath:" or "classpath*:") but the resources are not loaded, the resource location may be wrong. Check that the path is correct, especially the path relative to the classpath.

3. **File system resource problems** 

   + When you use file system paths, make sure the path is correct and the application has permission to read the files. Also watch out for path separators on different platforms. Use `File.separator` to stay compatible across platforms.

4. **The resource does not exist or is empty** 

   + If `getResources` returns an empty array, the resource location may not exist, or no resource matches. Make sure the location is correct and check that matching resources exist.

5. **Special characters in the resource path** 

   + Special characters in the resource path can cause resolving problems. Consider escaping or encoding the path.

6. **Performance problems** 

   + Handling many resources or patterns can cause performance problems. Try to optimize the pattern. Avoid too many wildcards and too much recursion.
