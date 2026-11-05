## TypeLocator

- [TypeLocator](#typelocator)
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

1. **Spring Expression Language (SpEL)**

   + You need to know the basic syntax and usage of SpEL. The `TypeLocator` interface is mostly used in SpEL to find type information at runtime.

2. **Reflection**

   + You should know Java reflection, including the `Class`, `Method` and `Field` classes. A `TypeLocator` usually uses reflection to find and work with types.

3. **Design patterns**
+ It helps to know common design patterns, such as the factory pattern and the strategy pattern. They can be useful when you implement the `TypeLocator` interface.

### 3. Description

The `TypeLocator` interface is a key interface in the Spring Framework. It finds type information at runtime and plays an important role in the Spring Expression Language (SpEL). It provides methods such as `findType(String typeName)` and `hasType(String typeName)`. With them, SpEL can get and check type information at runtime. This makes Spring applications more flexible.

### 4. Main Features

1. **Find type information**

   + The `findType(String typeName)` method finds the type for a given type name. SpEL can then get the type it needs at runtime.

2. **Check whether a type exists**

   + The `hasType(String typeName)` method checks whether a type with the given name exists. This is useful to know if a type can be resolved.

### 5. Interface Source

The `TypeLocator` interface defines a way to locate types. It has one abstract method, `findType(String typeName)`. The method finds the type for the given type name and returns the `Class` object for it.

```java
/**
 * Implementations of this interface are expected to be able to locate types.
 * They may use a custom {@link ClassLoader} and/or deal with common
 * package prefixes (for example {@code java.lang}) however they wish.
 *
 * <p>See {@link org.springframework.expression.spel.support.StandardTypeLocator}
 * for an example implementation.
 *
 * @author Andy Clement
 * @since 3.0
 */
@FunctionalInterface
public interface TypeLocator {

	/**
	 * Find a type by name. The name may or may not be fully qualified
	 * (for example {@code String} or {@code java.lang.String}).
	 * @param typeName the type to be located
	 * @return the {@code Class} object representing that type
	 * @throws EvaluationException if there is a problem finding the type
	 */
	Class<?> findType(String typeName) throws EvaluationException;

}

```

`StandardTypeLocator` is a simple implementation of the `TypeLocator` interface. It finds the type for a given type name. It uses the context ClassLoader and the registered import prefixes. If a type is not found, it tries the registered import prefixes.

```java
/**
 * A simple {@link TypeLocator} implementation that uses the context ClassLoader
 * (or any ClassLoader set upon it). It supports 'well-known' packages: if a type
 * cannot be found, it will try the registered imports to locate it.
 * 
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public class StandardTypeLocator implements TypeLocator {

    @Nullable
    private final ClassLoader classLoader;

    private final List<String> knownPackagePrefixes = new ArrayList<>(1);

    /**
     * Create a StandardTypeLocator for the default ClassLoader
     * (typically, the thread context ClassLoader).
     */
    public StandardTypeLocator() {
        this(ClassUtils.getDefaultClassLoader());
    }

    /**
     * Create a StandardTypeLocator for the given ClassLoader.
     * @param classLoader the ClassLoader to delegate to
     */
    public StandardTypeLocator(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
        // Similar to when writing regular Java code, it only knows about java.lang by default
        registerImport("java.lang");
    }

    /**
     * Register a new import prefix that will be used when searching for unqualified types.
     * Expected format is something like "java.lang".
     * @param prefix the prefix to register
     */
    public void registerImport(String prefix) {
        this.knownPackagePrefixes.add(prefix);
    }

    /**
     * Remove that specified prefix from this locator's list of imports.
     * @param prefix the prefix to remove
     */
    public void removeImport(String prefix) {
        this.knownPackagePrefixes.remove(prefix);
    }

    /**
     * Return a list of all the import prefixes registered with this StandardTypeLocator.
     * @return a list of registered import prefixes
     */
    public List<String> getImportPrefixes() {
        return Collections.unmodifiableList(this.knownPackagePrefixes);
    }

    /**
     * Find a (possibly unqualified) type reference - first using the type name as-is,
     * then trying any registered prefixes if the type name cannot be found.
     * @param typeName the type to locate
     * @return the class object for the type
     * @throws EvaluationException if the type cannot be found
     */
    @Override
    public Class<?> findType(String typeName) throws EvaluationException {
        String nameToLookup = typeName;
        try {
            return ClassUtils.forName(nameToLookup, this.classLoader);
        }
        catch (ClassNotFoundException ey) {
            // try any registered prefixes before giving up
        }
        for (String prefix : this.knownPackagePrefixes) {
            try {
                nameToLookup = prefix + '.' + typeName;
                return ClassUtils.forName(nameToLookup, this.classLoader);
            }
            catch (ClassNotFoundException ex) {
                // might be a different prefix
            }
        }
        throw new SpelEvaluationException(SpelMessage.TYPE_NOT_FOUND, typeName);
    }
}
```

### 6. Main Implementations

1. **StandardTypeLocator**

   + `StandardTypeLocator` is a simple implementation of the `TypeLocator` interface. It finds type information for a given type name.

### 7. Best Practices

This example uses the Spring Expression Language (SpEL) to get type information. It parses different expressions: getting the Class object of a type, and comparing enum values of a type. This shows how SpEL locates and compares types.

```java
public class TypeLocatorDemo {
    public static void main(String[] args) {
        // Create a SpEL expression parser
        ExpressionParser parser = new SpelExpressionParser();

        // Parse the expression to get the Class object of Date
        Class dateClass = parser.parseExpression("T(java.util.Date)").getValue(Class.class);
        System.out.println("dateClass = " + dateClass);

        // Parse the expression to get the Class object of String
        Class stringClass = parser.parseExpression("T(String)").getValue(Class.class);
        System.out.println("stringClass = " + stringClass);

        // Parse the expression to compare two RoundingMode enum values
        boolean trueValue = parser.parseExpression("T(java.math.RoundingMode).CEILING < T(java.math.RoundingMode).FLOOR").getValue(Boolean.class);
        System.out.println("trueValue = " + trueValue);
    }
}
```

Output: the program got the Class objects of `java.util.Date` and `java.lang.String`. It also compared two `java.math.RoundingMode` enum values, and the result is true.

```properties
dateClass = class java.util.Date
stringClass = class java.lang.String
trueValue = true
```

### 8. Relationship with Other Components

1. **Class**

   + `Class` is an important class in Java reflection. It represents the runtime information of a class. It has methods to get the name, methods, fields and more. A `TypeLocator` implementation may use `Class` to represent the type it finds.

2. **ClassLoader**

   + `ClassLoader` is a key Java class. It loads class files into the Java Virtual Machine and creates the matching `Class` objects. A `TypeLocator` implementation may use a `ClassLoader` to load and get class information.

3. **StandardTypeLocator**

   + An implementation of the `TypeLocator` interface. It is a simple type locator, and is usually used to find type information in SpEL.

### 9. FAQ

1. **How do I write a custom TypeLocator?**

   - You may want your own `TypeLocator` for special needs. Implement the `findType(String typeName)` method. It should find the type for the given type name and handle the lookup logic as you need.

2. **How do I handle different type lookup strategies?**

   - Sometimes you need different lookup strategies in different cases. For example, you may need different lookup logic for different package prefixes. Or you may need to switch strategies at runtime based on conditions. In that case, think about how to design a flexible lookup strategy.

3. **How do I handle a failed type lookup?**

   - When the type cannot be found, you need to decide what to do. You can throw an exception, return a default value, or try another lookup strategy. Choose the way that fits your case, and make sure the user gets clear feedback.
