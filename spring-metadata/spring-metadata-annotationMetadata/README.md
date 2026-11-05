## AnnotationMetadata

- [AnnotationMetadata](#annotationmetadata)
  - [1. Background Knowledge](#1-background-knowledge)
  - [2. Description](#2-description)
  - [3. Main Features](#3-main-features)
  - [4. Interface Source](#4-interface-source)
  - [5. Main Implementation](#5-main-implementation)
  - [6. Best Practices](#6-best-practices)
    - [ASM bytecode](#asm-bytecode)
    - [Java reflection](#java-reflection)
  - [7. Relation to Other Components](#7-relation-to-other-components)
  - [8. Common Problems](#8-common-problems)


### 1. Background Knowledge

1. **Annotations**
   + It is important to know what annotations are and how to define and use them. `AnnotationMetadata` works with the annotation information of a class. So you need to know how to write custom annotations and how to put standard and custom annotations on classes, methods, or fields.
2. **Reflection**
   + `AnnotationMetadata` can use reflection to analyze the annotations of a class. So you need to know the basics of Java reflection, including the `Class` object, `Method`, `Field`, and `Annotation` classes.
3. **Classpath scanning**
   + If you read the annotation information of a class with `metadataReader.getAnnotationMetadata()`, you need to know how classpath scanning and resource loading work. This is how the bytecode of a class is found and read.

### 2. Description

`AnnotationMetadata` is an interface in the Spring Framework. It is used to access and analyze the annotation information of classes, methods, fields, and other elements. It gives an easy way to get and use the annotation information of a class at runtime.

### 3. Main Features

1. **Check annotations on a class**:
   - The `hasAnnotation(String annotationName)` method checks if an annotation with the given name is present on the class. This is useful for conditional configuration. You can change the behavior based on the annotations of a class.
2. **Get annotation attribute values**:
   - The `getAnnotationAttributes(String annotationName)` method returns the attribute values of the given annotation. This lets you configure the behavior of a class dynamically. You can choose different logic based on the attribute values.
3. **Get all annotations on a class**:
   - The `getAnnotations()` method returns all annotations on the class. This gives you a full view of the annotations of a class.
4. **Get meta-annotation information**:
   - The `getMetaAnnotationTypes(String annotationName)` method returns the types of the meta-annotations on the given annotation. This helps to analyze the annotation hierarchy.
5. **Find annotated methods**:
   - The `getAnnotatedMethods(String annotationName)` method returns the metadata of all methods that have the given annotation. This can be used to build custom component scanning or annotation handling.
6. **Get interface and super class information**:
   - The `getInterfaceNames()` method returns the fully qualified names of the interfaces that the class implements. The `getSuperClassName()` method returns the fully qualified name of the super class.
7. **Get member information**:
   - With methods such as `getAnnotatedMethods()` and the `ClassMetadata` methods, you can get the metadata of the methods and the class itself, to analyze the annotations on these elements.

### 4. Interface Source

The `AnnotationMetadata` interface has a set of methods to analyze and use the annotation information of a class at runtime. It lets you get the annotations on a class, the meta-annotations, the interface and super class information, and the annotations on methods. This supports many features, such as conditional configuration, component scanning, and custom annotation handling. The `introspect` method of the interface creates an `AnnotationMetadata` instance, so you can analyze the annotations of a class at runtime.

```java
/**
 * Interface that defines abstract access to the annotations of a specific
 * class, in a form that does not require that class to be loaded yet.
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 2.5
 * @see StandardAnnotationMetadata
 * @see org.springframework.core.type.classreading.MetadataReader#getAnnotationMetadata()
 * @see AnnotatedTypeMetadata
 */
public interface AnnotationMetadata extends ClassMetadata, AnnotatedTypeMetadata {
    /**
     * Get the fully qualified class names of all annotation types that
     * are <em>present</em> on the underlying class.
     * @return the annotation type names
     */
    default Set<String> getAnnotationTypes() {
        return getAnnotations().stream()
                .filter(MergedAnnotation::isDirectlyPresent)
                .map(annotation -> annotation.getType().getName())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Get the fully qualified class names of all meta-annotation types that
     * are <em>present</em> on the given annotation type on the underlying class.
     * @param annotationName the fully qualified class name of the meta-annotation
     * type to look for
     * @return the meta-annotation type names, or an empty set if none found
     */
    default Set<String> getMetaAnnotationTypes(String annotationName) {
        MergedAnnotation<?> annotation = getAnnotations().get(annotationName, MergedAnnotation::isDirectlyPresent);
        if (!annotation.isPresent()) {
            return Collections.emptySet();
        }
        return MergedAnnotations.from(annotation.getType(), SearchStrategy.INHERITED_ANNOTATIONS).stream()
                .map(mergedAnnotation -> mergedAnnotation.getType().getName())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Determine whether an annotation of the given type is <em>present</em> on
     * the underlying class.
     * @param annotationName the fully qualified class name of the annotation
     * type to look for
     * @return {@code true} if a matching annotation is present
     */
    default boolean hasAnnotation(String annotationName) {
        return getAnnotations().isDirectlyPresent(annotationName);
    }

    /**
     * Determine whether the underlying class has an annotation that is itself
     * annotated with the meta-annotation of the given type.
     * @param metaAnnotationName the fully qualified class name of the
     * meta-annotation type to look for
     * @return {@code true} if a matching meta-annotation is present
     */
    default boolean hasMetaAnnotation(String metaAnnotationName) {
        return getAnnotations().get(metaAnnotationName,
                MergedAnnotation::isMetaPresent).isPresent();
    }

    /**
     * Determine whether the underlying class has any methods that are
     * annotated (or meta-annotated) with the given annotation type.
     * @param annotationName the fully qualified class name of the annotation
     * type to look for
     */
    default boolean hasAnnotatedMethods(String annotationName) {
        return !getAnnotatedMethods(annotationName).isEmpty();
    }

    /**
     * Retrieve the method metadata for all methods that are annotated
     * (or meta-annotated) with the given annotation type.
     * <p>For any returned method, {@link MethodMetadata#isAnnotated} will
     * return {@code true} for the given annotation type.
     * @param annotationName the fully qualified class name of the annotation
     * type to look for
     * @return a set of {@link MethodMetadata} for methods that have a matching
     * annotation. The return value will be an empty set if no methods match
     * the annotation type.
     */
    Set<MethodMetadata> getAnnotatedMethods(String annotationName);

    /**
     * Factory method to create a new {@link AnnotationMetadata} instance
     * for the given class using standard reflection.
     * @param type the class to introspect
     * @return a new {@link AnnotationMetadata} instance
     * @since 5.2
     */
    static AnnotationMetadata introspect(Class<?> type) {
        return StandardAnnotationMetadata.from(type);
    }
}
```

### 5. Main Implementation

1. **StandardAnnotationMetadata**:
   - This implementation uses standard Java reflection. It uses the `java.lang.Class` object to analyze and access the annotation information of a class.
   - With this implementation, you can easily check the annotations on a class, get the annotation attribute values, and do other annotation-related work.
2. **SimpleAnnotationMetadata**:
   - This implementation is based on ASM (a bytecode library). It analyzes and accesses the annotation information of a class. Compared to `StandardAnnotationMetadata`, it is usually lighter. It does not use standard Java reflection. It reads the bytecode of the class directly.

~~~mermaid
classDiagram
    direction BT
    
    class ClassMetadata {
    	<<interface>>
    }
    
    class AnnotatedTypeMetadata {
    	<<interface>>
    }
    
    class AnnotationMetadata {
    	<<interface>>
    }
    
    class StandardClassMetadata {
    }

    class SimpleAnnotationMetadata {
    }
    
    class StandardAnnotationMetadata {
    }

    AnnotationMetadata ..|> ClassMetadata
    AnnotationMetadata ..|> AnnotatedTypeMetadata
    SimpleAnnotationMetadata --|> AnnotationMetadata
    StandardClassMetadata --|> ClassMetadata
    StandardAnnotationMetadata ..|> StandardClassMetadata
    StandardAnnotationMetadata --|> AnnotationMetadata

~~~

### 6. Best Practices

#### ASM bytecode

This example uses ASM bytecode to create an `AnnotationMetadata` object. It creates a `MetadataReaderFactory` and a `MetadataReader`, and loads the metadata of the class "`com.xcs.spring.bean.MyBean`". Then it uses `AnnotationMetadata` to check if the class is annotated with `@Component`, gets the annotation attribute value, and prints the results. This way, you can analyze the annotation information of a class without loading the class.

```java
public class AnnotationMetadataDemoByASM {

    public static void main(String[] args) throws Exception {
        // Create the MetadataReaderFactory
        SimpleMetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory();
        // Get the MetadataReader
        MetadataReader metadataReader = readerFactory.getMetadataReader("com.xcs.spring.bean.MyBean");

        // Get the AnnotationMetadata
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();

        System.out.println("AnnotationMetadata impl class is " + annotationMetadata.getClass());

        // Check if the MyBean class is annotated with @Component
        boolean isComponent = annotationMetadata.hasAnnotation(Component.class.getName());
        System.out.println("MyBean is a @Component: " + isComponent);

        // Get the annotation attributes on the MyBean class
        if (isComponent) {
            Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(Component.class.getName());
            System.out.println("@Component value is " + annotationAttributes.get("value"));
        }
    }
}
```

The output shows that the implementation of `AnnotationMetadata` is `SimpleAnnotationMetadata`. It is based on ASM and does not use standard Java reflection.

```java
AnnotationMetadata impl class is class org.springframework.core.type.classreading.SimpleAnnotationMetadata
MyBean is a @Component: true
@Component value is myBean
```

#### Java reflection

This example uses Java reflection to create an `AnnotationMetadata` object. It calls `AnnotationMetadata.introspect(MyBean.class)` to load the metadata of the `MyBean` class. Then it uses `AnnotationMetadata` to check if the class is annotated with `@Component`, gets the annotation attribute value, and prints the results. It uses standard Java reflection. This is the common way and fits most Spring applications.

```java
public class AnnotationMetadataDemoByReflection {

    public static void main(String[] args) throws Exception {
        // Get the AnnotationMetadata
        AnnotationMetadata annotationMetadata = AnnotationMetadata.introspect(MyBean.class);

        System.out.println("AnnotationMetadata impl class is " + annotationMetadata.getClass());

        // Check if the MyBean class is annotated with @Component
        boolean isComponent = annotationMetadata.hasAnnotation(Component.class.getName());
        System.out.println("MyBean is a @Component: " + isComponent);

        // Get the annotation attributes on the MyBean class
        if (isComponent) {
            Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(Component.class.getName());
            System.out.println("@Component value is " + annotationAttributes.get("value"));
        }
    }
}
```

The output shows that the implementation of `AnnotationMetadata` is `StandardAnnotationMetadata`. It is based on Java reflection.

```java
AnnotationMetadata impl class is class org.springframework.core.type.StandardAnnotationMetadata
MyBean is a @Component: true
@Component value is myBean
```

### 7. Relation to Other Components

1. **Component scanning**
   - `AnnotationMetadata` is related to the `ClassPathBeanDefinitionScanner` class. During component scanning, `ClassPathBeanDefinitionScanner` uses `AnnotationMetadata` to check for specific annotations on classes (such as `@Component`, `@Service`, and `@Controller`). It registers these classes as Spring beans.
2. **Bean definitions**
   - `AnnotationMetadata` is related to the `AnnotatedGenericBeanDefinition` and `ScannedGenericBeanDefinition` classes. When Spring finds an annotated class, it uses `AnnotationMetadata` to build the bean definition. It reads the annotation information of the class, such as the bean name, scope, and dependencies. Then it uses this information to create the bean definition.
3. **Conditional bean registration**
   - For conditional bean registration, `AnnotationMetadata` works with the `Conditional` annotation and the `Condition` interface. During conditional registration, `AnnotationMetadata` is used to evaluate the `@Conditional` annotation. The result decides if a specific bean is registered.

### 8. Common Problems

1. **How do I get the information of a specific annotation on a class?**
   - Use the `getAnnotations()` method of `AnnotationMetadata`. Then filter and check each annotation to get the information of a specific one. For example, use `isDirectlyPresent()` to check if the annotation is directly present on the class. Then use `getAnnotationAttributes()` to get its attribute values.
2. **How do I check if a class has a specific annotation?**
   - Use the `hasAnnotation()` method of `AnnotationMetadata` and pass the fully qualified name of the annotation.
3. **How do I handle conditional registration?**
   - When you use conditional annotations (such as `@Conditional`), use `AnnotationMetadata` to evaluate the condition. The result decides if the bean is registered. Usually, you write a custom condition class that implements the `Condition` interface to handle the condition logic.
4. **How do I scan and analyze many classes?**
   - Use the component scanning of Spring. Configure the `@ComponentScan` annotation or an XML configuration file to scan many classes. Then use `AnnotationMetadata` to analyze each scanned class.
5. **How do I write a custom annotation processor?**
   - If you need custom logic for a specific annotation, write a custom annotation processor and use `AnnotationMetadata` to analyze and handle the annotation information. This usually means custom logic, such as creating bean definitions dynamically or doing other work.
6. **How do I choose between ASM and standard Java reflection?**
   - When you use `AnnotationMetadata`, choose ASM or standard Java reflection based on your needs and performance. ASM suits cases that need high performance. Standard Java reflection is usually easier to use and maintain. The choice depends on the project requirements.
