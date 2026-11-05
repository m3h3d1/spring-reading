## Resource

- [Resource](#resource)
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

1. **I/O basics**
   + Know the basics of files, paths, and input/output streams.
2. **Classpath**
   + Know what the classpath is and how to load resources from it.
3. **URL and URI**
   + You need these to understand how to load resources from the network or other protocols.

### 3. Description

`Resource` is a core interface in Spring. It gives one simple, unified way to access low-level resources such as files, classpath resources, and URLs. It hides the details of how each resource is accessed. Reading resources is a common need in Java: configuration files, images, audio, and so on. But the Java standard library uses a different mechanism for each kind of resource. For a file system resource you may use `java.io.File`. For a classpath resource you may use `getResource` or `getResourceAsStream` of `ClassLoader`. For a network resource you may use `java.net.URL`. Having many mechanisms means more complex code, more duplicate code, and more room for mistakes. To fix this, Spring introduced the `Resource` interface. It is one common abstraction for all resources.

### 4. Main Features

1. **Unified resource abstraction**
   + Whether the resource comes from the file system, the classpath, a URL, or somewhere else, the `Resource` interface gives one common abstraction.
2. **Resource description**
   + With `getDescription()`, every `Resource` implementation can describe the resource behind it. This is very useful for error handling and logging.
3. **Reading**
   + `Resource` provides `getInputStream()`. You can read the content directly without caring where the resource really comes from.
4. **Existence and readability**
   + `Resource` provides `exists()` and `isReadable()` to check whether a resource exists and can be read.
5. **Open check**
   + `isOpen()` checks whether the resource is an already opened stream. This helps you avoid reading a stream twice.
6. **URI and URL access**
   + `Resource` lets you get the URI and URL of the underlying resource with `getURI()` and `getURL()`. This allows further processing.
7. **File access**
   + When the resource is a file in the file system, you can access that file directly with `getFile()`.
8. **Many implementations**
   + Spring provides many `Resource` implementations for different sources, such as `ClassPathResource`, `FileSystemResource`, and `UrlResource`.

### 5. Interface Source

`InputStreamSource` is a simple interface that provides an input stream. It is designed to return a new, unread input stream each time. This matters for APIs that need to read the stream more than once.

```java
/**
 * Simple interface for objects that are sources for an InputStream.
 */
public interface InputStreamSource {

	/**
	 * Return an InputStream for the underlying resource.
	 * A new stream is expected on each call.
	 * This matters for APIs such as JavaMail. When it creates mail attachments, JavaMail must be able to read the stream several times. For such uses, every getInputStream() call must return a fresh stream.
	 * @return the input stream for the underlying resource (must not be null)
	 * @throws java.io.FileNotFoundException if the underlying resource does not exist
	 * @throws IOException if the content stream could not be opened
	 */
	InputStream getInputStream() throws IOException;

}
```

`Resource` is a core interface in Spring. It stands for an external or internal resource, such as a file, a classpath resource, or a URL resource. It gives one abstraction over the underlying resource, so your code does not depend on the real type of the resource.

```java
/**
 * Interface for a resource descriptor that abstracts from the actual type of the underlying resource, such as a file or class path resource.
 *
 * <p>An InputStream can be opened for every resource if it exists in physical form. But a URL or file handle can be returned only for some resources. The exact behavior depends on the implementation.
 */
public interface Resource extends InputStreamSource {

    /**
     * Determine whether this resource actually exists in physical form.
     */
    boolean exists();

    /**
     * Indicate whether non-empty contents of this resource can be read via {@link #getInputStream()}.
     * Reading the actual content may still fail.
     */
    default boolean isReadable() {
        return exists();
    }

    /**
     * Indicate whether this resource represents a handle with an open stream.
     * If true, the input stream cannot be read multiple times, and must be closed after reading to avoid resource leaks.
     */
    default boolean isOpen() {
        return false;
    }

    /**
     * Determine whether this resource represents a file in a file system.
     */
    default boolean isFile() {
        return false;
    }

    /**
     * Return a URL handle for this resource.
     */
    URL getURL() throws IOException;

    /**
     * Return a URI handle for this resource.
     */
    URI getURI() throws IOException;

    /**
     * Return a File handle for this resource.
     */
    File getFile() throws IOException;

    /**
     * Return a {@link ReadableByteChannel}.
     */
    default ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(getInputStream());
    }

    /**
     * Determine the content length for this resource.
     */
    long contentLength() throws IOException;

    /**
     * Determine the last-modified timestamp for this resource.
     */
    long lastModified() throws IOException;

    /**
     * Create a resource relative to this resource.
     */
    Resource createRelative(String relativePath) throws IOException;

    /**
     * Return the file name of this resource.
     */
    @Nullable
    String getFilename();

    /**
     * Return a description for this resource, to be used for error output when working with the resource.
     */
    String getDescription();
}
```

### 6. Main Implementations

1. **`ClassPathResource`**
   + Loads resources from the classpath.
2. **`FileSystemResource`**
   + Accesses resources in the file system.
3. **`UrlResource`**
   + For URL-based resources.
4. **`ServletContextResource`**
   + For resources in web applications.
5. **`ByteArrayResource`** & **`InputStreamResource`**
   + Memory-based and stream-based resources.

~~~mermaid
classDiagram
    direction BT
    
    class InputStreamSource{
    	<<interface>>
    	+ getInputStream(): InputStream
    }
    
    class Resource {
    	<<interface>>
        + exists(): boolean
        + getDescription(): String
        + other methods()
    }
    class AbstractResource {
    	<<Abstract>>
    }
  
    class ByteArrayResource {
    }
    
    class InputStreamResource {
    }

    class ClassPathResource {
    }

    class UrlResource {
    }

    class ServletContextResource {
    }
    
    class AbstractFileResolvingResource {
    	<<Abstract>>
    }
    
    class FileSystemResource {
    }
    
    Resource ..|> InputStreamSource
    AbstractResource --|> Resource
    ByteArrayResource ..|> AbstractResource
    InputStreamResource ..|> AbstractResource
    AbstractFileResolvingResource ..|> AbstractResource
    FileSystemResource ..|> AbstractResource
    UrlResource ..|> AbstractFileResolvingResource
    ClassPathResource ..|> AbstractFileResolvingResource
    ServletContextResource ..|> AbstractFileResolvingResource

~~~

### 7. Best Practices

**`ClassPathResource`**

`ClassPathResource` is a Spring class for accessing resources on the classpath. In this example, the `path` variable holds the path of the classpath resource. The file is at `src/main/resources/application.properties`.

```java
public class ClassPathResourceDemo {
    public static void main(String[] args) throws Exception {
        String path = "application.properties";
        Resource resource = new ClassPathResource(path);
        try (InputStream is = resource.getInputStream()) {
            // Read and process the resource content
            System.out.println(new String(is.readAllBytes()));
        }
    }
}
```

**`FileSystemResource`**

`FileSystemResource` is a Spring class for accessing files in the file system. In this example, the `path` variable holds the full path of the file. Replace it with a valid file path of your own.

```java
public class FileSystemResourceDemo {
    public static void main(String[] args) throws Exception {
        // Replace with your own directory
        String path = "D:\\idea-work-space-xcs\\spring-reading\\spring-resources\\spring-resource\\myfile.txt";
        Resource resource = new FileSystemResource(path);
        try (InputStream is = resource.getInputStream()) {
            // Read and process the resource content
            System.out.println(new String(is.readAllBytes()));
        }
    }
}
```

**`UrlResource`**

`UrlResource` is a Spring class for accessing resources on the network. In this example, it reads an online text file.

```java
public class UrlResourceDemo {
    public static void main(String[] args) throws Exception {
        Resource resource = new UrlResource("https://dist.apache.org/repos/dist/test/test.txt");
        try (InputStream is = resource.getInputStream()) {
            // Read and process the resource content
            System.out.println(new String(is.readAllBytes()));
        }
    }
}
```

**`ByteArrayResource`**

`ByteArrayResource` is a Spring class that uses a byte array as a resource. In this example, it reads the simple string `"hello world"`.

```java
public class ByteArrayResourceDemo {
    public static void main(String[] args) throws Exception {
        byte[] data = "hello world".getBytes();
        Resource resource = new ByteArrayResource(data);
        try (InputStream is = resource.getInputStream()) {
            // Read and process the resource content
            System.out.println(new String(is.readAllBytes()));
        }
    }
}
```

**`InputStreamResource`**

`InputStreamResource` is a Spring class that wraps any `InputStream` as a Spring `Resource`. In this example, the bytes of the string "hello world" are used to create a `ByteArrayInputStream`. This gives us an input stream that can read "hello world".

```java
public class InputStreamResourceDemo {
    public static void main(String[] args) throws Exception {
        InputStream isSource = new ByteArrayInputStream("hello world".getBytes());
        Resource resource = new InputStreamResource(isSource);
        try (InputStream is = resource.getInputStream()) {
            // Read and process the resource content
            System.out.println(new String(is.readAllBytes()));
        }
    }
}
```

### 8. Relationship with Other Components

1. **`BeanFactory & ApplicationContext`**
   + `BeanFactory` and `ApplicationContext` often need to access resources when they create and configure beans. The `Resource` interface gives one common way to load resource files. This is useful for configuring and initializing beans. A bean definition can hold resource references, so beans can use these resources.

2. **`ResourceLoader`**
   + Spring provides the `ResourceLoader` interface. It is widely used in the application context to load resources. The default implementation is `DefaultResourceLoader`, which loads resources based on the `Resource` interface. With `ResourceLoader`, an application can easily get and manage resources from the file system, the classpath, a URL, or other sources.

3. **`PropertyPlaceholderConfigurer`**
   + `PropertyPlaceholderConfigurer` is a Spring class that replaces property placeholders. It reads property values from resource files and replaces the placeholders in the configuration. The resource files are given by the `locations` property.

4. **MVC framework**
   + Spring MVC often needs to handle file uploads and static resources. The `Resource` interface and its implementations can manage and serve these resources. Together with `ResourceLoader`, `Resource` is used to load static resources such as images, style sheets, and JavaScript files.

5. **Custom resource loading and processing**
   + You can also use the `Resource` interface to build your own loading and processing logic. For example, you can create a custom `Resource` implementation that loads a resource file, runs some special processing, and gives the result to the application.

### 9. FAQ

1. **How do I choose the right `Resource` implementation?**
   - `ClassPathResource`: access resources on the classpath.
   - `FileSystemResource`: access resources in the file system.
   - `UrlResource`: URL-based resources, such as HTTP or FTP resources.
   - `ServletContextResource`: made for web applications, to access resources in the `ServletContext`.
2. **Resource not found**
   - If you create a resource with a path or URL that does not exist, you may get a `FileNotFoundException`.
   - Make sure the path is correct and check that the resource really exists.
3. **How do I handle encoding or charset problems?**
   - When you read text from a resource, you may need to handle encoding.
   - Use a `Reader` with the right charset, or use Spring's `EncodedResource` class.
4. **Using relative paths**
   - With `FileSystemResource`, relative paths can be confusing. Make sure you know what the relative path is based on.
5. **What is the real URL or file path of the resource?**
   - The `Resource` interface gives one abstraction for all resource types, but sometimes you need to know the real type or location.
   - Use `resource.getURL()` or `resource.getFile()` to try to get the real URL or file.
6. **How do I use `ServletContextResource` in a non-web application?**
   - You can't. `ServletContextResource` is made for web applications. If you use it in a non-web application, you will get an error.
7. **How do I read resources from a Jar or War file?**
   - `ClassPathResource` or `UrlResource` can easily read resources from a Jar or War file.
   - For nested Jar files (for example, a Spring Boot executable Jar), special handling is needed. This is usually done with the `org.springframework.boot.loader.jar.JarFile` class.
8. **How do I refresh or reload a changed resource?**
   - By default, a `Resource` instance has no refresh or reload mechanism. But for some resource types, such as `UrlResource`, every call to `getInputStream()` reads the content again.
   - For resources that need refreshing, consider a cache or another approach.
9. **Performance of resource loading**
   - Loading many resources very often can cause performance problems.
   - Consider caching the resource content or using a more efficient loading strategy.
