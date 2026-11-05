## ConstructorResolver

- [ConstructorResolver](#constructorresolver)
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

1. **Constructors**

   + A constructor is a special method that creates an instance of a class. It is important to know what a constructor is, how to use it, and how it differs from a normal method.

2. **Reflection**

   + Reflection is a Java feature. It lets a program inspect and work with classes, objects, methods and fields at runtime. You need to know how to use the classes in the `java.lang.reflect` package to get and use constructors.

3. **Spring Expression Language (SpEL)**

   + `ConstructorResolver` is usually used with the Spring Expression Language (SpEL). So it helps to know the basic syntax and usage of SpEL.

4. **Design patterns**

   + Knowing some design patterns, especially creational patterns such as the factory pattern and the builder pattern, helps you understand different ways to create objects.

### 3. Description

The `ConstructorResolver` interface is one of the core interfaces in the Spring Framework to find and run constructors. It lets you choose and call the constructor of an object at runtime. The interface defines a `resolve(ConstructorExecutor, TypedValue[])` method. It finds a constructor from the given constructor executor and arguments. By implementing `ConstructorResolver`, Spring can create objects in a flexible and dynamic way. This makes dependency injection and object creation more flexible and customizable. The implementations in Spring usually use reflection to create objects. They pick a suitable constructor from the object type and the argument list.

### 4. Main Features

1. **Resolve constructors**

   + The interface defines the method `resolve(ConstructorExecutor, TypedValue[])` to find a constructor. This means the constructor to call is chosen from the given arguments and the constructor signatures.

2. **Execute constructors**

   + Once a constructor is resolved, the interface calls it to create the object instance. This includes creating the instance, passing the arguments, and running the constructor.

3. **Dynamic object creation**

   + The `ConstructorResolver` interface lets you choose a suitable constructor at runtime to create an object. This gives flexibility and customization.

### 5. Interface Source

The `ConstructorResolver` interface defines one method, `resolve`. In the given context, it finds a suitable constructor on a type that can handle the given argument types. The interface returns a `ConstructorExecutor`, which can be used to call the constructor it found.

```java
/**
 * A constructor resolver attempts to locate a constructor and returns a ConstructorExecutor
 * that can be used to invoke that constructor. The ConstructorExecutor will be cached, but
 * if it 'goes stale' the resolvers will be called again.
 *
 * @author Andy Clement
 * @since 3.0
 */
@FunctionalInterface
public interface ConstructorResolver {

    /**
     * Within the supplied context, determine a suitable constructor on the
     * supplied type that can handle the specified arguments. Return a
     * ConstructorExecutor that can be used to invoke that constructor
     * (or {@code null} if no constructor could be found).
     *
     * @param context       the current evaluation context
     * @param typeName      the type upon which to look for the constructor
     * @param argumentTypes the arguments that the constructor must be able to handle
     * @return a ConstructorExecutor that can invoke the constructor, or null if non-found
     * @throws AccessException an access exception
     */
    @Nullable
    ConstructorExecutor resolve(EvaluationContext context, String typeName, List<TypeDescriptor> argumentTypes)
            throws AccessException;

}
```

`ReflectiveConstructorResolver` is an implementation in the Spring Framework. It uses reflection to find the constructor to call. It matches the constructors of the target class against the given argument types, and picks a suitable one to create the object. Three kinds of match can happen: an exact match, a close match, and a match that needs type conversion. Based on the result, `ReflectiveConstructorResolver` returns a `ConstructorExecutor` to run the chosen constructor.

```java
/**
 * A constructor resolver that uses reflection to locate the constructor that should be invoked.
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public class ReflectiveConstructorResolver implements ConstructorResolver {

    /**
     * Locate a constructor on the type. There are three kinds of match that might occur:
     * <ol>
     * <li>An exact match where the types of the arguments match the types of the constructor
     * <li>An inexact match where the types we are looking for are subtypes of those defined on the constructor
     * <li>A match where we are able to convert the arguments into those expected by the constructor,
     * according to the registered type converter.
     * </ol>
     */
    @Override
    @Nullable
    public ConstructorExecutor resolve(EvaluationContext context, String typeName, List<TypeDescriptor> argumentTypes)
            throws AccessException {

        try {
            // Get the type converter and the class type
            TypeConverter typeConverter = context.getTypeConverter();
            Class<?> type = context.getTypeLocator().findType(typeName);
            Constructor<?>[] ctors = type.getConstructors();

            // Sort the constructors by parameter count
            Arrays.sort(ctors, Comparator.comparingInt(Constructor::getParameterCount));

            // Initialize the match variables
            Constructor<?> closeMatch = null;
            Constructor<?> matchRequiringConversion = null;

            // Loop over the constructors
            for (Constructor<?> ctor : ctors) {
                int paramCount = ctor.getParameterCount();
                List<TypeDescriptor> paramDescriptors = new ArrayList<>(paramCount);
                for (int i = 0; i < paramCount; i++) {
                    paramDescriptors.add(new TypeDescriptor(new MethodParameter(ctor, i)));
                }
                ReflectionHelper.ArgumentsMatchInfo matchInfo = null;
                if (ctor.isVarArgs() && argumentTypes.size() >= paramCount - 1) {
                    // Match varargs parameters
                    matchInfo = ReflectionHelper.compareArgumentsVarargs(paramDescriptors, argumentTypes, typeConverter);
                } else if (paramCount == argumentTypes.size()) {
                    // Match normal parameters
                    matchInfo = ReflectionHelper.compareArguments(paramDescriptors, argumentTypes, typeConverter);
                }
                if (matchInfo != null) {
                    if (matchInfo.isExactMatch()) {
                        return new ReflectiveConstructorExecutor(ctor);
                    } else if (matchInfo.isCloseMatch()) {
                        closeMatch = ctor;
                    } else if (matchInfo.isMatchRequiringConversion()) {
                        matchRequiringConversion = ctor;
                    }
                }
            }

            // Return the executor of the matching constructor
            if (closeMatch != null) {
                return new ReflectiveConstructorExecutor(closeMatch);
            } else if (matchRequiringConversion != null) {
                return new ReflectiveConstructorExecutor(matchRequiringConversion);
            } else {
                return null;
            }
        } catch (EvaluationException ex) {
            throw new AccessException("Failed to resolve constructor", ex);
        }
    }
}
```

### 6. Main Implementations

+ **ReflectiveConstructorResolver**

  + The main implementation to find and run constructors. It uses Java reflection to choose and call the constructor of a class at runtime, and so creates the object.

### 7. Best Practices

First, we create a `SpelExpressionParser` object. It is the parser of the Spring Expression Language. Then we use the SpEL expression `"new com.xcs.spring.MyBean('spring-reading')"` to create a `MyBean` object with the argument `"spring-reading"`. Next, we call `getValue(MyBean.class)` to get the created `MyBean` object. Finally, we print this `MyBean` object.

```java
public class ConstructorResolverDemo {
    public static void main(String[] args) {
        ExpressionParser parser = new SpelExpressionParser();
        MyBean myBean = parser.parseExpression("new com.xcs.spring.MyBean('spring-reading')").getValue(MyBean.class);
        System.out.println(myBean);
    }
}
```

`MyBean` is a simple Java bean. It has a private property `name`, a constructor with one argument, and a getter and a setter. It also overrides `toString()`, so the property value is printed when the object is printed.

```java
public class MyBean {

    private String name;

    public MyBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "name='" + name + '\'' +
                '}';
    }
}
```

Output: a `MyBean` object is created, and its property `name` is set to `"spring-reading"`.

```properties
MyBean{name='spring-reading'}
```

### 8. Relationship with Other Components

1. **EvaluationContext**

   + When the `ConstructorResolver` interface resolves a constructor, it needs an evaluation context. The context gives the information and environment needed, such as variables, functions and types. In Spring, the `EvaluationContext` interface or one of its implementations represents the evaluation context.

2. **TypedValue**

   + The methods of the `ConstructorResolver` interface may use `TypedValue` objects. A `TypedValue` represents a value in an expression and gives access to the type of the value. In SpEL, `TypedValue` usually represents literal values and variable values in expressions.

3. **ConstructorExecutor**

   + An implementation of `ConstructorResolver` usually returns a `ConstructorExecutor`. It runs the resolved constructor. The `ConstructorExecutor` interface defines an `execute` method that runs the constructor and returns the result object.

4. **SpelExpressionParser**

   + `SpelExpressionParser` is one of the core classes in Spring that parse SpEL expressions. When it parses an expression, an implementation of `ConstructorResolver` may be used to handle constructor references in the expression.

5. **ReflectiveConstructorResolver**

   + `ReflectiveConstructorResolver` is one of the main implementations of the `ConstructorResolver` interface in Spring. It uses Java reflection to find and run constructors at runtime.

### 9. FAQ

1. **How do I handle type matching of constructor arguments?**

   - An implementation of `ConstructorResolver` usually picks a constructor based on the given argument types. If the argument types do not match the constructor parameter types, resolving may fail or the wrong constructor may be chosen. So make sure you pass arguments of the right types.

2. **How do I handle overloaded constructors?**

   - When a class has several constructors, the `ConstructorResolver` implementation needs to choose the best one. Usually it compares the given argument types with the constructor parameter lists. If several constructors match, resolving may fail or the chosen constructor may be unclear.

3. **How is the ConstructorResolver interface related to Java reflection?**

   - An implementation of `ConstructorResolver` usually uses Java reflection to find and run constructors. With reflection, you can inspect the structure of a class at runtime and call a specific constructor to create an object. So the `ConstructorResolver` interface is closely related to Java reflection.

4. **How do I handle exceptions thrown by a constructor?**

   - Many exceptions can happen when a constructor is called, such as argument type mismatch or access problems. An implementation of `ConstructorResolver` may handle them, depending on the implementation. Usually you should handle exceptions properly before you call the constructor.
