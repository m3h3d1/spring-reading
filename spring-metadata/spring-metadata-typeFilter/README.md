## TypeFilter

- [TypeFilter](#typefilter)
  - [1. Overview](#1-overview)
  - [2. Background Knowledge](#2-background-knowledge)
  - [3. Description](#3-description)
  - [4. Main Features](#4-main-features)
  - [5. Interface Source](#5-interface-source)
  - [6. Main Implementation](#6-main-implementation)
  - [7. Best Practices](#7-best-practices)
  - [8. Relation to Other Components](#8-relation-to-other-components)
  - [9. Common Problems](#9-common-problems)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Background Knowledge

1. **Resource interface**
   + [Resource](https://github.com/xuchengsheng/spring-reading/blob/master/spring-resources/spring-resource) is an abstract interface to access resources. A resource can be a file, a file on the classpath, a URL, and so on. You need to know how to use the `Resource` interface to get the input stream, the file path, and other information of a resource.

2. **AnnotationMetadata interface**
   + [AnnotationMetadata](https://github.com/xuchengsheng/spring-reading/tree/master/spring-metadata/spring-metadata-annotationMetadata) is an interface in the Spring Framework to work with the annotation information of classes. It has methods to access and use this information.

3. **MetadataReader interface**
   + [MetadataReader](https://github.com/xuchengsheng/spring-reading/tree/master/spring-metadata/spring-metadata-metadataReader) is an interface in Spring to read the metadata of a class. It can be used to scan class files and get basic information such as the class name and the class annotations. In annotation-driven development, `MetadataReader` is usually used to scan the classes in packages and read their annotations, to configure Spring beans.

4. **Path and pattern resolution**
   + Path resolution in Spring, especially Ant-style path patterns such as `classpath*:com/xcs/spring/**/*.xml`.

### 3. Description

`TypeFilter` is an interface in the Spring Framework to filter classes during component scanning. By implementing the `match` method, you define your own logic to decide which classes are included in the scan result and which are excluded. This flexible mechanism is used by the `@ComponentScan` annotation in Spring. A custom `TypeFilter` can filter classes in a precise way. This fits complex application structures and special component management needs. Spring has several built-in `TypeFilter` implementations, such as `AnnotationTypeFilter` and `AssignableTypeFilter`. They filter by annotation or by type.

### 4. Main Features

1. **Custom filter logic**
   + You can implement the `TypeFilter` interface and override the `match` method to define your own filter logic. This lets you decide if a class is included in the component scan result based on specific conditions, such as the annotations of the class, the interfaces it implements, or its inheritance.

2. **Custom component scanning**
   + When you configure a class with `@ComponentScan`, you can set the `includeFilters` and `excludeFilters` attributes with your own `TypeFilter` instances. This customizes the rules of component scanning. You control more precisely which classes are managed by the Spring container and which are excluded.

3. **Fit complex application structures**
   + In a complex application, there may be classes from different modules or layers, and you may only want some modules or layers in the Spring container. With a custom `TypeFilter`, you can include or exclude classes based on the real structure of the project.

4. **Flexibility and extensibility**
   + `TypeFilter` is a flexible mechanism. You can extend and customize the behavior of component scanning for specific needs. This is very useful for applications that need to adapt to different cases.

### 5. Interface Source

The `TypeFilter` interface is the base for custom class filters in component scanning. It has one `match` method. It gets a `MetadataReader` and a `MetadataReaderFactory`. With them, you can write custom filter logic to decide which classes are included in the component scan result. This makes the filtering and management of classes at application startup more flexible in the Spring Framework.

```java
/**
 * Base interface for type filters using a
 * {@link org.springframework.core.type.classreading.MetadataReader}.
 *
 * @author Costin Leau
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @since 2.5
 */
@FunctionalInterface
public interface TypeFilter {

    /**
     * Determine whether this filter matches for the class described by
     * the given metadata.
     *
     * @param metadataReader       the metadata reader for the target class
     * @param metadataReaderFactory a factory for obtaining metadata readers
     * for other classes (such as superclasses and interfaces)
     * @return whether this filter matches
     * @throws IOException in case of I/O failure when reading metadata
     */
    boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
            throws IOException;

}
```

### 6. Main Implementation

1. **AnnotationTypeFilter (annotation-based filter)**
   + Matches classes that have the given annotation. During component scanning, it compares the annotation information of a class to decide if the class is included in the scan result.

2. **AssignableTypeFilter (type-based filter):**
   + Matches subclasses or implementation classes of the given type. It compares the inheritance of the target class to decide if the class is included in the component scan result.

3. **AspectJTypeFilter (AspectJ expression filter):**
   + Matches with an AspectJ expression. You write an AspectJ-style expression to choose the classes to scan in a flexible way.

4. **RegexPatternTypeFilter (regular expression filter):**
   + Matches the class name with a regular expression. You give a regular expression to decide if matching classes are included in the component scan result.

~~~mermaid
classDiagram
    direction BT
    
    class TypeFilter{
    	<<interface>>
    }
    
    class AbstractTypeHierarchyTraversingFilter {
    	<<Abstract>>
    }
    
    class AbstractClassTestingTypeFilter {
    	<<Abstract>>
    }
  
    class AnnotationTypeFilter {
    }
    
    class AssignableTypeFilter {
    }

    class AspectJTypeFilter {
    }

    class RegexPatternTypeFilter {
    }
    
    AbstractTypeHierarchyTraversingFilter ..|> TypeFilter
    AnnotationTypeFilter --|> AbstractTypeHierarchyTraversingFilter
    AssignableTypeFilter --|> AbstractTypeHierarchyTraversingFilter
    AspectJTypeFilter ..|> TypeFilter
    AbstractClassTestingTypeFilter ..|> TypeFilter
    RegexPatternTypeFilter --|> AbstractClassTestingTypeFilter

~~~

### 7. Best Practices

This example uses the Spring `TypeFilter` to filter classes. It creates a resolver for path pattern resources, a metadata reader factory, and an annotation type filter (`MyAnnotation` in this example). Then it gets all class files from the path `classpath*:com/xcs/spring/**/*.class` and filters the classes that have the given annotation. It goes through the scanned class files and prints the file name and the result of the annotation type filter.

```java
public class TypeFilterDemo {

    public static void main(String[] args) throws IOException {
        // Create a resolver for path pattern resources
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // Create a simple metadata reader factory
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();

        // Create an annotation type filter to match classes with the MyAnnotation annotation
        TypeFilter annotationTypeFilter = new AnnotationTypeFilter(MyAnnotation.class);

        // Use the resolver to get all class files that match the path
        Resource[] resources = resolver.getResources("classpath*:com/xcs/spring/**/*.class");

        // Go through the scanned class files
        for (Resource resource : resources) {
            // Get the metadata reader
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);

            // Match the current class with the annotation type filter
            boolean match = annotationTypeFilter.match(metadataReader, metadataReaderFactory);

            // Print the scanned file name and the match result
            System.out.printf("Scanned file: %-20s || Filter matches: %s%n", resource.getFile().getName(), match);
        }
    }
}
```

The `MyAnnotation` annotation is the match condition of the `TypeFilter`. It is used to filter the classes that have this annotation.

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAnnotation {
}
```

A class named `MyComponent` is defined and it uses the `MyAnnotation` annotation.

```java
package com.xcs.spring.component;

@MyAnnotation
public class MyComponent {
}
```

A class named `MyController` is defined. It does not use any custom annotation.

```java
package com.xcs.spring.controller;

public class MyController {
    
}
```

A class named `MyRepository` is defined. It does not use any custom annotation.

```java
package com.xcs.spring.repository;

public class MyRepository {
    
}
```

A class named `MyService` is defined. It does not use any custom annotation.

```java
package com.xcs.spring.service;

public class MyService {
    
}
```

The output shows that `MyController`, `MyRepository`, and `MyService` do not match the annotation type filter. `MyComponent`, which has the `MyAnnotation` annotation, matches. This is what we expect. It proves that the annotation type filter works when it filters classes.

```java
Scanned file: TypeFilterDemo.class || Filter matches: false
Scanned file: MyAnnotation.class   || Filter matches: false
Scanned file: MyComponent.class    || Filter matches: true
Scanned file: MyController.class   || Filter matches: false
Scanned file: MyRepository.class   || Filter matches: false
Scanned file: MyService.class      || Filter matches: false
```

### 8. Relation to Other Components

1. **ClassPathScanningCandidateComponentProvider**
   + `registerDefaultFilters` is a method of the `ClassPathScanningCandidateComponentProvider` class. It registers the default type filters. It is called during component scanning. It adds some default filters to the `includeFilters` collection. These filters scan the classes with the `@Component` annotation, and some annotation type filters from the JSR specifications, such as the `ManagedBean` and `Named` annotations.

2. **ComponentScanAnnotationParser**
   + `typeFiltersFor` is a private method of the `ComponentScanAnnotationParser` class. It creates a list of type filters from the attributes of the `@ComponentScan` annotation. For each filter type (such as annotation, assignable type, or custom), it creates the matching `TypeFilter` instance and adds it to the list.

3. **ComponentScanBeanDefinitionParser**
   + `createTypeFilter` is a method of the `ComponentScanBeanDefinitionParser` class. It creates a type filter from the XML configuration when the `<component-scan>` element is parsed. It picks the type filter to create based on the `filter-type` and `filter-expression` attributes. The supported filter types include annotation, assignable type, AspectJ expression, and regular expression.

### 9. Common Problems

1. **Classes are not matched correctly**
   + Make sure the match condition of the filter (such as the annotation, the type, or the regular expression) fits the real target class. Check that the filter is created and used correctly.
   
2. **A custom TypeFilter has no effect**
   + Make sure the custom `TypeFilter` is implemented correctly and configured correctly. Check that the match logic is what you expect.

3. **The package scan result is empty**
   + Check that the package path is correct, and make sure the filter condition matches the target classes. Also check that the class loader is correct, so that it can load the target classes.

4. **Many TypeFilters do not work**
   + Make sure the use cases and conditions of the `TypeFilter` objects do not overlap. Otherwise, only one filter may work.

5. **AspectJ expression does not match:**
   + Make sure the AspectJ expression is correct and the class loader can access the related classes. Also check that the class loader in the constructor of `AspectJTypeFilter` is correct.

6. **Performance problems:**
   + In large projects, `TypeFilter` can cause performance problems. Consider optimizing the filter, or cache the scan result when it fits. You can use caching or other tools to reduce the problem.
