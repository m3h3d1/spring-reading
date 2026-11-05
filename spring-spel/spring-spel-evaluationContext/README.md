## EvaluationContext

- [EvaluationContext](#evaluationcontext)
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

1. **Spring Framework basics**

   + To understand `SpEL`, you must know the core concepts and basic usage of the Spring Framework. This includes dependency injection, inversion of control and aspect-oriented programming.

2. **Spring Expression Language (`SpEL`)**
   + You need to know the syntax, features and usage of `SpEL`. This includes how to use `SpEL` expressions in Spring applications, such as in XML configuration, annotations and Spring Data query methods.
   
3. **Spring expression parser**

   + You need to know how Spring parses and runs `SpEL` expressions. This includes how the parser, the compiler and the evaluation context of `SpEL` work and how to use them.

### 3. Description

The `EvaluationContext` interface is a key interface in the Spring Framework. It is used to evaluate Spring Expression Language (`SpEL`) expressions at runtime. It gives an environment that manages the variables, functions, type information and other context of a `SpEL` expression. This information can be accessed and used when the expression is evaluated. With `EvaluationContext`, we can define and control the context in which a `SpEL` expression is evaluated. This gives dynamic access to the object graph of the application, and supports features such as dynamic filtering, sorting and calculation.

### 4. Main Features

1. **Manage variables**
   + `EvaluationContext` lets you define and use variables in `SpEL` expressions. It has methods to set and get variable values, so the variables can be used when the expression is evaluated.
   
2. **Manage functions**

   + Besides basic operators and operations, `SpEL` can call custom functions. `EvaluationContext` has methods to register and manage functions, so they can be called in expressions.

3. **Type conversion and type checking**

   + While a `SpEL` expression is evaluated, it may need type conversion and type checking. `EvaluationContext` has methods for these, so the operations in the expression run correctly.

4. **Object navigation and method calls**

   + `EvaluationContext` has methods for object navigation and method calls. In a `SpEL` expression, you can access the properties and methods of objects directly. This gives dynamic access to the object graph of the application.

5. **Security settings**

   + `EvaluationContext` lets you set security-related options, such as whether private fields and methods can be accessed and whether safe access is enabled. This keeps expressions safe.

### 5. Interface Source

The `EvaluationContext` interface gives the evaluation context of `SpEL` expressions. It is used to evaluate and process `SpEL` expressions at runtime. The interface has methods to get and set the root object, and to get components such as the property accessors, constructor resolvers, method resolvers and bean resolver. These components play an important role when an expression is evaluated. The interface also gives the type locator, type converter, type comparator and operator overloader. They handle type conversion, type comparison and operator overloading in expressions. Finally, the interface lets you set and look up named variables. So variables can be referred to and changed while the expression is evaluated. In short, the `EvaluationContext` interface gives `SpEL` expressions the runtime environment and support they need. We can then handle and evaluate expressions in a flexible way.

```java
/**
 * Expressions are executed in an evaluation context. It is in this context that
 * references are resolved when encountered during expression evaluation.
 *
 * <p>There is a default implementation of this EvaluationContext interface:
 * {@link org.springframework.expression.spel.support.StandardEvaluationContext}
 * which can be extended, rather than having to implement everything manually.
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface EvaluationContext {

	/**
	 * Return the default root context object against which unqualified
	 * properties/methods/etc should be resolved. This can be overridden
	 * when evaluating an expression.
	 */
	TypedValue getRootObject();

	/**
	 * Return a list of accessors that will be asked in turn to read/write a property.
	 */
	List<PropertyAccessor> getPropertyAccessors();

	/**
	 * Return a list of resolvers that will be asked in turn to locate a constructor.
	 */
	List<ConstructorResolver> getConstructorResolvers();

	/**
	 * Return a list of resolvers that will be asked in turn to locate a method.
	 */
	List<MethodResolver> getMethodResolvers();

	/**
	 * Return a bean resolver that can look up beans by name.
	 */
	@Nullable
	BeanResolver getBeanResolver();

	/**
	 * Return a type locator that can be used to find types, either by short or
	 * fully qualified name.
	 */
	TypeLocator getTypeLocator();

	/**
	 * Return a type converter that can convert (or coerce) a value from one type to another.
	 */
	TypeConverter getTypeConverter();

	/**
	 * Return a type comparator for comparing pairs of objects for equality.
	 */
	TypeComparator getTypeComparator();

	/**
	 * Return an operator overloader that may support mathematical operations
	 * between more than the standard set of types.
	 */
	OperatorOverloader getOperatorOverloader();

	/**
	 * Set a named variable in this evaluation context to a specified value.
	 * @param name the name of the variable to set
	 * @param value the value to be placed in the variable
	 */
	void setVariable(String name, @Nullable Object value);

	/**
	 * Look up a named variable within this evaluation context.
	 * @param name the name of the variable to look up
	 * @return the value of the variable, or {@code null} if not found
	 */
	@Nullable
	Object lookupVariable(String name);

}
```

### 6. Main Implementations

1. **StandardEvaluationContext**

   + `StandardEvaluationContext` is the default evaluation context of SpEL. It supports basic features such as variables, functions, type conversion and type checking. It is the most used evaluation context and fits most SpEL use cases. It is also the base of other context implementations.

2. **SimpleEvaluationContext**

   +  `SimpleEvaluationContext` is a simpler version of `StandardEvaluationContext`. It drops some rarely used features, so it is a lighter evaluation context. It has fewer features. But for some simple cases, or where performance matters, `SimpleEvaluationContext` may be a better choice.

3. **CacheEvaluationContext**

   + `CacheEvaluationContext` is a subclass of `StandardEvaluationContext`. It adds caching of expression results on top of the standard evaluation context. For the same expression with the same input, the result is not computed again. The cached value is returned instead. This improves performance, especially for expressions that are costly to compute.

4. **MethodBasedEvaluationContext**

   + `MethodBasedEvaluationContext` is another subclass of `StandardEvaluationContext`. It is made for calling methods in expressions. Unlike the standard evaluation context, `MethodBasedEvaluationContext` exposes the methods of the target object to the `SpEL` expression, so they can be called in the expression. In a `SpEL` expression, you can access the methods of objects directly and handle data more flexibly.

### 7. Best Practices

This example uses the `EvaluationContext` interface of the Spring Expression Language (`SpEL`) and its implementation `StandardEvaluationContext`. It sets the root object, sets a variable, and gets context information such as the property accessors, constructor resolvers, method resolvers and bean resolver. It creates a custom root object class `MyRootObject` and passes it to the `StandardEvaluationContext` constructor to set the root object. Then it shows how to set a variable and how to get the property accessors, constructor resolvers and more.

```java
public class EvaluationContextDemo {
    public static void main(String[] args) {
        // Create the evaluation context
        EvaluationContext context = new StandardEvaluationContext(new MyRootObject("Root Data"));

        // Get the root object
        TypedValue root = context.getRootObject();
        System.out.println("Root object: " + root.getValue());

        // Set a variable
        context.setVariable("name", "spring-reading");
        System.out.println("Value of variable 'name': " + context.lookupVariable("name"));

        // Get the property accessors
        List<PropertyAccessor> propertyAccessors = context.getPropertyAccessors();
        System.out.println("Property accessors: " + propertyAccessors);

        // Get the constructor resolvers
        List<ConstructorResolver> constructorResolvers = context.getConstructorResolvers();
        System.out.println("Constructor resolvers: " + constructorResolvers);

        // Get the method resolvers
        List<MethodResolver> methodResolvers = context.getMethodResolvers();
        System.out.println("Method resolvers: " + methodResolvers);

        // Get the bean resolver
        BeanResolver beanResolver = context.getBeanResolver();
        System.out.println("Bean resolver: " + beanResolver);

        // Get the type locator
        TypeLocator typeLocator = context.getTypeLocator();
        System.out.println("Type locator: " + typeLocator);

        // Get the type converter
        TypeConverter typeConverter = context.getTypeConverter();
        System.out.println("Type converter: " + typeConverter);

        // Get the type comparator
        TypeComparator typeComparator = context.getTypeComparator();
        System.out.println("Type comparator: " + typeComparator);

        // Get the operator overloader
        OperatorOverloader operatorOverloader = context.getOperatorOverloader();
        System.out.println("Operator overloader: " + operatorOverloader);
    }

    // Class of the root object
    static class MyRootObject {
        private String data;

        public MyRootObject(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
```

Output: the evaluation context set by `StandardEvaluationContext` has this information. The root object is an instance of the `MyRootObject` class defined in `EvaluationContextDemo`. The value of the variable `'name'` is `'spring-reading'`. The output also shows the concrete classes of the property accessors, constructor resolvers, method resolvers, type locator, type converter, type comparator and operator overloader.

```properties
Root object: com.xcs.spring.EvaluationContextDemo$MyRootObject@735b478
Value of variable 'name': spring-reading
Property accessors: [org.springframework.expression.spel.support.ReflectivePropertyAccessor@13c78c0b]
Constructor resolvers: [org.springframework.expression.spel.support.ReflectiveConstructorResolver@7ca48474]
Method resolvers: [org.springframework.expression.spel.support.ReflectiveMetho
        dResolver@b97c004]
Bean resolver: null
Type locator: org.springframework.expression.spel.support.StandardTypeLocator@51565ec2
Type converter: org.springframework.expression.spel.support.StandardTypeConverter@1c3a4799
Type comparator: org.springframework.expression.spel.support.StandardTypeComparator@131276c2
Operator overloader: org.springframework.expression.spel.support.StandardOperatorOverloader@26aa12dd
```

### 8. Relationship with Other Components

1. **StandardEvaluationContext**

   + `StandardEvaluationContext` is an implementation of the `EvaluationContext` interface. It is the standard evaluation context. It manages the context information while a SpEL expression is evaluated.

2. **TypedValue**

   + The `TypedValue` class wraps the result of an expression evaluation. It works with the `EvaluationContext` interface to pass and handle values while the expression is evaluated.

3. **PropertyAccessor**, **ConstructorResolver**, **MethodResolver**, **BeanResolver**

   + These interfaces and classes are related to the `EvaluationContext` interface. They support resolving and accessing properties, constructors, methods and beans in `SpEL` expressions.

4. **TypeLocator**, **TypeConverter**, **TypeComparator**, **OperatorOverloader**

   + These interfaces and classes are also related to the `EvaluationContext` interface. They support type-related operations and comparisons in `SpEL` expressions.

5. **ExpressionParser**, **Expression**

   + The `ExpressionParser` interface parses an expression string and returns an `Expression` object. The `Expression` interface represents a parsed expression. They work with the `EvaluationContext` interface to evaluate expressions.

### 9. FAQ

1. **How do I create and use an `EvaluationContext` instance?**

   - Create an `EvaluationContext` instance with `StandardEvaluationContext` or one of its subclasses. Use its methods to set context information such as the root object and variables. Then pass it to the expression evaluator to evaluate the expression.

2. **How do I set the root object and variables?**

   - Use the `setRootObject` method to set the root object. Use the `setVariable` method to set variables. The root object is the starting point of the evaluation. Variables give context information used while the expression is evaluated.

3. **What are the implementations of the `EvaluationContext` interface? How do they differ?**

   -  The implementations include `StandardEvaluationContext` and `SimpleEvaluationContext`. They may differ in feature complexity and performance. Choose the one that fits your needs.

4. **How are `EvaluationContext`, `ExpressionParser` and `Expression` related?**

   - `ExpressionParser` parses an expression string and returns an `Expression` object. `EvaluationContext` gives the context information while the expression is evaluated. The `Expression` object and the `EvaluationContext` instance are used together to evaluate the expression.

5. **How does `EvaluationContext` work with components such as the property accessors, method resolvers and constructor resolvers in SpEL?**

   - `EvaluationContext` has methods to get components such as the property accessors, method resolvers and constructor resolvers. During evaluation, they are used to resolve and access properties, methods and constructors.

6. **How do I handle exceptions that can happen during expression evaluation?**

   - Many exceptions can happen while an expression is evaluated, such as syntax errors and type conversion errors. Catch them and handle them properly to keep the program stable and robust.

