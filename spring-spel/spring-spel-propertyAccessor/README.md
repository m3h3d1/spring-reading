## PropertyAccessor

- [PropertyAccessor](#propertyaccessor)
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

   + You should know the basic syntax and features of SpEL, including parsing expressions, operators, variables and functions.

2. **Property access and reflection** 

   + You should know how to use Java reflection to access and change the properties of an object. This includes getting the fields of a class, calling methods of an object, and setting field values.

3. **Design patterns** 

   + You should know common design patterns, such as the adapter pattern and the strategy pattern. They may be useful when you write your own `PropertyAccessor`.

### 3. Description

The `PropertyAccessor` interface defines one common way to access properties. With it, you can read and write the properties of an object. Different modules of the Spring Framework can then access object properties in the same way. This makes application development more flexible and extensible. By implementing this interface, you can customize how properties are accessed for different cases. It is useful in the Spring Expression Language (SpEL), data binding, AOP and more.

### 4. Main Features

1. **Reading properties** 

   + The `canRead()` method checks whether a given property can be read. The `read()` method reads the value of the property. This gives one common way to get property values from objects.

2. **Writing properties**

   + The `canWrite()` method checks whether a given property can be written. The `write()` method sets the value of the property. This gives one common way to set property values on objects.

3. **Custom property access logic** 

   + You can write your own property access logic as you need. For example, use your own `PropertyAccessor` implementation to access the properties of an object and run specific business logic.

4. **Different object structures** 

   + You can write different `PropertyAccessor` implementations for different object structures. These include JavaBeans, Maps and arrays.

### 5. Interface Source

This is the `PropertyAccessor` interface. It reads and writes the properties of an object. It has four methods. They check whether a property can be read, read the property value, check whether a property can be written, and write the property value. It also has a method that returns the specific target classes. This interface gives one common way to access object properties. You can write custom property access logic and adapt to different object structures.

```java
/**
 * A property accessor is able to read (and possibly write) the properties of an object.
 *
 * <p>This interface places no restrictions, and so implementors are free to access properties
 * directly as fields or through getters or in any other way they see as appropriate.
 *
 * <p>A resolver can optionally specify an array of target classes for which it should be
 * called. However, if it returns {@code null} from {@link #getSpecificTargetClasses()},
 * it will be called for all property references and given a chance to determine if it
 * can read or write them.
 *
 * <p>Property resolvers are considered to be ordered, and each will be called in turn.
 * The only rule that affects the call order is that any resolver naming the target
 * class directly in {@link #getSpecificTargetClasses()} will be called first, before
 * the general resolvers.
 *
 * @author Andy Clement
 * @since 3.0
 */
public interface PropertyAccessor {

    /**
     * Return an array of classes for which this resolver should be called.
     * <p>Returning {@code null} indicates this is a general resolver that
     * can be called in an attempt to resolve a property on any type.
     * @return an array of classes that this resolver is suitable for
     * (or {@code null} if a general resolver)
     */
    @Nullable
    Class<?>[] getSpecificTargetClasses();

    /**
     * Called to determine if a resolver instance is able to access a specified property
     * on a specified target object.
     * @param context the evaluation context in which the access is being attempted
     * @param target the target object upon which the property is being accessed
     * @param name the name of the property being accessed
     * @return true if this resolver is able to read the property
     * @throws AccessException if there is any problem determining whether the property can be read
     */
    boolean canRead(EvaluationContext context, @Nullable Object target, String name) throws AccessException;

    /**
     * Called to read a property from a specified target object.
     * Should only succeed if {@link #canRead} also returns {@code true}.
     * @param context the evaluation context in which the access is being attempted
     * @param target the target object upon which the property is being accessed
     * @param name the name of the property being accessed
     * @return a TypedValue object wrapping the property value read and a type descriptor for it
     * @throws AccessException if there is any problem accessing the property value
     */
    TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException;

    /**
     * Called to determine if a resolver instance is able to write to a specified
     * property on a specified target object.
     * @param context the evaluation context in which the access is being attempted
     * @param target the target object upon which the property is being accessed
     * @param name the name of the property being accessed
     * @return true if this resolver is able to write to the property
     * @throws AccessException if there is any problem determining whether the property can be written to
     */
    boolean canWrite(EvaluationContext context, @Nullable Object target, String name) throws AccessException;

    /**
     * Called to write to a property on a specified target object.
     * Should only succeed if {@link #canWrite} also returns {@code true}.
     * @param context the evaluation context in which the access is being attempted
     * @param target the target object upon which the property is being accessed
     * @param name the name of the property being accessed
     * @param newValue the new value for the property
     * @throws AccessException if there is any problem writing to the property value
     */
    void write(EvaluationContext context, @Nullable Object target, String name, @Nullable Object newValue)
            throws AccessException;

}
```

### 6. Main Implementations

1. **BeanExpressionContextAccessor**

   +  It allows access to specific objects and properties in expressions. With `BeanExpressionContextAccessor`, you can access properties of objects in the context directly in SpEL expressions. This is very useful in some cases. For example, you can use a SpEL expression in the Spring `@Value` annotation to read a configuration property.

2. **BeanFactoryAccessor** 

   + It allows access to the properties of beans registered in a Spring `BeanFactory` from SpEL expressions. `BeanFactory` is the core interface of the Spring IoC container. It manages and creates beans. With `BeanFactoryAccessor`, you can access beans registered in the IoC container from SpEL expressions, and read and set their property values. This makes it easy to access Spring beans in SpEL.

3. **CompilablePropertyAccessor**

   + It allows property access to be optimized for better performance. With the compile feature, the property access logic can be optimized at runtime. This avoids needless work and makes execution faster. It is very useful where performance matters.

4. **DataBindingPropertyAccessor** 

   + It allows reading and writing properties in Spring applications. Data binding is the process of binding user input to a back-end data model. It is widely used in web applications. `DataBindingPropertyAccessor` gives an easy way to read and set the property values of an object. It handles the property access needed during data binding.

5. **EnvironmentAccessor** 

   + `EnvironmentAccessor` accesses properties from the Spring environment, for example to read property values from a configuration file. The Spring environment abstraction gives one common way to access the environment properties of an application, such as system properties, environment variables and configuration files. With `EnvironmentAccessor`, you can access property values of the Spring environment directly in SpEL expressions. This gives dynamic configuration and property injection.

6. **JspPropertyAccessor**

   + It allows SpEL expressions to be used in JSP pages, and lets JSP tags access the properties of objects. JSP is a common web view technology. Used with SpEL, it gives more flexible and dynamic page rendering. You can use SpEL expressions in JSP and access object properties inside custom tags (such as the `eval` tag). This gives more flexible and configurable page logic.

7. **MapAccessor** 

   + `MapAccessor` accesses the key-value pairs of a Map object directly in SpEL expressions. A Map is a common data structure that stores a set of key-value pairs. With `MapAccessor`, you can read and set the values of a Map object directly in SpEL expressions. This makes it easy to work with Maps in SpEL.

8. **OptimalPropertyAccessor** 

   + `OptimalPropertyAccessor` is an optimized implementation of property access. It is usually used inside `ReflectivePropertyAccessor` to make property access faster. By optimizing the access logic, it avoids needless work and makes execution faster, so property values are accessed more efficiently.

### 7. Best Practices

This example first creates a SpEL expression parser, `ExpressionParser`. Then it creates a `StandardEvaluationContext` as the context of the SpEL expression. In the context, it sets a variable named `myBean`. Its value is a `MyBean` object. Next, `parser.parseExpression("#myBean.name")` parses a SpEL expression that accesses the `name` property of the `myBean` object. Finally, `getValue(context, String.class)` gets the property value, and the example prints it.

```java
public class PropertyAccessorDemo {
    public static void main(String[] args)  {
        // Create a SpEL expression parser
        ExpressionParser parser = new SpelExpressionParser();

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("myBean",new MyBean("spring-reading"));

        // Parse the SpEL expression and create the object with the constructor
        String name = parser.parseExpression("#myBean.name").getValue(context, String.class);

        System.out.println("name = " + name);
    }
}
```

 The `MyBean` class is a simple Java bean. It has a private field `name` with a getter and a setter.

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

Output: the SpEL expression parser accessed the property of the object and got the value "spring-reading".

```properties
name = spring-reading
```

### 8. Relationship with Other Components

1. **Spring Expression Language (SpEL)** 

   + In SpEL, the `PropertyAccessor` interface is used to access the properties of objects. SpEL evaluates expressions dynamically at runtime. The `PropertyAccessor` interface lets it read and write object properties.

2. **Spring data binding** 

   + In Spring, data binding is a common task. It binds data submitted by users to a back-end data model. The `PropertyAccessor` interface gives one common way to access object properties, and it can be used during data binding.

3. **Spring AOP** 

   + In Spring aspect-oriented programming (AOP), the `PropertyAccessor` interface can be used to access the properties of the advised object. With AOP, you can intercept or change the properties of the advised object.

4. **Spring IoC container** 

   + In the Spring inversion of control (IoC) container, the `PropertyAccessor` interface can be used to access the properties of beans registered in the container. With the IoC container, it is easy to manage and access object properties.

5. **Other custom components** 

   + Besides the cases above, custom components can also use the `PropertyAccessor` interface. For example, a custom framework or library can use it to access object properties for specific features or logic.

### 9. FAQ

1. **Property access permission problems** 

   + When you use the `PropertyAccessor` interface, you may not have enough permission to access a property of an object. Then you cannot read or write it. This is related to access control and permission settings of the object.

2. **Property naming problems** 

   + When you access a property, its name may not match. For example, the case may differ or the name may be misspelled. Then the property cannot be accessed correctly.

3. **Property type conversion problems** 

   + When you read or write a property, the type conversion may fail. For example, you may try to assign a string value to an integer property, which causes a conversion exception.

4. **Context configuration problems** 

   + When you use the `PropertyAccessor` interface, you need to give a suitable context (such as an `EvaluationContext`). If the configuration is wrong or the context is not set properly, property access fails.

5. **Exception handling problems** 

   + Many exceptions can happen during property access, such as null pointer exceptions and access permission exceptions. Handle them properly to keep the program stable and reliable.
