## BeanResolver

- [BeanResolver](#beanresolver)
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

   + You should know the syntax and usage of SpEL, such as how to use expressions to access properties of objects and call methods. `BeanResolver` is mostly used to resolve beans in SpEL, so it is important to understand SpEL.

2. **Spring container**

   + You should understand what the Spring container is and how it works. This includes the difference between BeanFactory and ApplicationContext, the bean lifecycle, and bean scopes. `BeanResolver` is mostly used to resolve beans from the Spring container, so you need to know the container.

3. **Reflection**

   + You should know the basics of Java reflection, such as how to get and use class information at runtime. `BeanResolver` often uses reflection to create and work with objects, so some knowledge of reflection helps.

4. **Design patterns**

   + You should know the basics of design patterns and common ones, such as the factory pattern and the strategy pattern. The `BeanResolver` interface is often used for features like dependency injection. Knowing design patterns helps you write more flexible and extensible solutions.

### 3. Description

The `BeanResolver` interface is a key interface in the Spring Framework. It resolves beans in the Spring Expression Language (SpEL). It defines a `resolve` method. The method takes an `EvaluationContext` and a bean name, and returns the matching bean instance. By implementing `BeanResolver`, you can refer to beans of the Spring container in SpEL expressions and work with them. This makes expressions more flexible and powerful.

### 4. Main Features

1. **Resolve beans**

   + It provides a method to get a bean instance from the Spring container by bean name. You can get and use beans of the Spring container at runtime.

2. **SpEL support**

   + As part of the Spring Expression Language (SpEL), `BeanResolver` lets you refer to beans of the Spring container in SpEL expressions. You use a special syntax in the expression to refer to a bean. Then you can do things like access properties and call methods.

3. **Context support**

   + The `BeanResolver` interface is usually used with an `EvaluationContext` object. The context gives the expression the information it needs, such as variables and functions. This makes sure the needed context is available when a bean is resolved.

4. **Custom resolving logic**

   + By default, the Spring container provides the `BeanResolver` implementation. But you can write your own if you need to. This lets you customize how beans are resolved to fit your business needs. For example, you can write a `BeanResolver` that resolves beans by special rules, or gets bean instances from a non-standard place.

5. **Decoupling business logic**

   + With `BeanResolver`, you can separate business logic from the way beans are obtained. In different environments or cases, you can switch the `BeanResolver` implementation easily. The rest of the business logic is not affected.

### 5. Interface Source

The `BeanResolver` interface resolves bean references in the Spring Expression Language (SpEL). It is registered in the evaluation context. It finds the instance for a given bean name. It supports the `@myBeanName` and `&myBeanName` expressions to refer to beans. The `&` prefix gives access to a factory bean.

```java
/**
 * A bean resolver can be registered with the evaluation context and will kick in
 * for {@code @myBeanName} and {@code &myBeanName} expressions. The {@code &} variant
 * syntax allows access to a factory bean.
 *
 * @author Andy Clement
 * @since 3.0.3
 */
public interface BeanResolver {

    /**
     * Look up the bean by the given name and return the corresponding instance for it.
     * For attempting access to a factory bean, the name needs a {@code &} prefix.
     * @param context the current evaluation context
     * @param beanName the name of the bean to look up
     * @return an object representing the bean
     * @throws AccessException if there is an unexpected problem resolving the bean
     */
    Object resolve(EvaluationContext context, String beanName) throws AccessException;

}
```

`BeanFactoryResolver` implements the `BeanResolver` interface. It resolves beans in a Spring bean factory. It has a constructor for setup. It implements the `resolve` method to get the bean instance from the bean factory by the given bean name.

```java
/**
 * EL bean resolver that operates against a Spring
 * {@link org.springframework.beans.factory.BeanFactory}.
 * 
 * @author Juergen Hoeller
 * @since 3.0.4
 */
public class BeanFactoryResolver implements BeanResolver {

	private final BeanFactory beanFactory;

	/**
	 * Create a new {@code BeanFactoryResolver} for the given factory.
	 * 
	 * @param beanFactory the {@code BeanFactory} to resolve bean names against
	 */
	public BeanFactoryResolver(BeanFactory beanFactory) {
		Assert.notNull(beanFactory, "BeanFactory must not be null");
		this.beanFactory = beanFactory;
	}

	@Override
	public Object resolve(EvaluationContext context, String beanName) throws AccessException {
		try {
			return this.beanFactory.getBean(beanName);
		} catch (BeansException ex) {
			throw new AccessException("Could not resolve bean reference against BeanFactory", ex);
		}
	}

}
```

### 6. Main Implementations

1. **BeanFactoryResolver**

   + `BeanFactoryResolver` is a class that implements the `BeanResolver` interface. In the Spring Framework, it resolves beans from a bean factory. It takes a BeanFactory instance in its constructor. When you call the resolve method, it gets the bean instance from the BeanFactory by the given bean name.

### 7. Best Practices

This example uses the `BeanResolver` interface and the `BeanFactoryResolver` class to resolve beans in the Spring container. First, it creates a BeanFactory from annotation configuration. Then it uses a SpEL expression parser to parse the expression `@myBean`, and sets `BeanFactoryResolver` as the BeanResolver of the evaluation context. Finally, it gets the bean instance by parsing the SpEL expression and prints it.

```java
public class BeanResolverDemo {
    public static void main(String[] args) {
        // Create a BeanFactory
        // Here the BeanFactory is created from annotation configuration
        BeanFactory beanFactory = new AnnotationConfigApplicationContext(MyBean.class).getBeanFactory();

        // Create a SpEL expression parser
        ExpressionParser parser = new SpelExpressionParser();

        // Create a standard evaluation context
        StandardEvaluationContext context = new StandardEvaluationContext();
        // Set BeanFactoryResolver as the BeanResolver of the context
        context.setBeanResolver(new BeanFactoryResolver(beanFactory));

        // Parse the SpEL expression to get the bean instance
        Object myBean = parser.parseExpression("@myBean").getValue(context);

        // Print the bean instance
        System.out.println("myBean = " + myBean);
    }
}
```

Output: the expression `@myBean` is resolved, and the bean instance named `myBean` is returned.

```properties
myBean = com.xcs.spring.MyBean@34123d65
```

### 8. Relationship with Other Components

1. **ExpressionParser**

   +  `ExpressionParser` is the Spring interface that parses expressions. It is usually used together with `BeanResolver`. `ExpressionParser` parses the SpEL expression, and `BeanResolver` resolves the bean references in it.

2. **EvaluationContext**

   + `EvaluationContext` is the context object used when Spring evaluates an expression. It gives the expression the variables, functions and other information it needs. `BeanResolver` is usually part of the `EvaluationContext`, and resolves bean references in the expression.

3. **BeanFactory**: 

   + `BeanFactory` is one of the core interfaces of the Spring Framework. It manages and gets bean instances. The `BeanFactoryResolver` class implements the `BeanResolver` interface to resolve bean references in a BeanFactory.

### 9. FAQ

1. **How do I write a custom BeanResolver?**

   - You may want your own `BeanResolver` for special needs, for example to get bean instances from a different data source. Usually you create a new class that implements the `BeanResolver` interface and override the `resolve` method as needed.

2. **How do I handle a bean that cannot be resolved?**

   - When `BeanResolver` cannot resolve the requested bean, it may throw an exception. You need to decide how to handle it. For example, you can write a log, return a default value, or show a friendly error message to the user.

3. **How do I refer to a bean in a SpEL expression?**

   - You may need to refer to a bean of the Spring container in a SpEL expression to run some logic. Usually you use the `@beanName` or `&beanName` expression. `@` gets the bean instance. `&` gets the factory bean itself.

4. **How do I solve circular dependency problems?**

   - If beans have a circular dependency, `BeanResolver` may fail to resolve them. Design the dependencies between beans with care. You can also use techniques such as lazy initialization to solve circular dependencies.
