## Pointcut

- [Pointcut](#pointcut)
  - [1. Overview](#1-overview)
  - [2. Description](#2-description)
  - [3. Main Features](#3-main-features)
  - [4. Interface Source](#4-interface-source)
  - [5. Main Implementations](#5-main-implementations)
  - [6. Class Diagram](#6-class-diagram)
  - [7. Best Practices](#7-best-practices)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `Pointcut` interface defines pointcuts, that is, it decides which methods an aspect should affect. The Pointcut interface provides the matching rules that decide on which methods of which classes the aspect applies, and under which conditions.

### 3. Main Features

1. **Define pointcuts**

   + The Pointcut interface defines pointcuts, that is, it decides which methods an aspect should affect. It lets us say on which methods of which classes the aspect applies.

2. **Matching rules**

   + It provides the matching rules that decide on which methods the aspect applies. The rules can be based on the method name, the parameters, the return type, the class name, and other conditions, so the pointcut can be located exactly.

3. **Get the class filter**

   + The `getClassFilter()` method returns a `ClassFilter` object that decides which classes should be matched. We can write our own class filter logic for our needs.
   
4. **Get the method matcher**

   + The `getMethodMatcher()` method returns a `MethodMatcher` object that decides which methods should be matched. We can write our own method matching logic for our needs.

### 4. Interface Source

The `Pointcut` interface is the core abstraction of a pointcut in Spring AOP. It is made of a `ClassFilter` and a `MethodMatcher`, which decide which classes and methods should be matched. With this interface, you can create different pointcuts and combine them flexibly to define complex aspects. The interface also defines a constant `TRUE`, a pointcut that always matches.

```java
/**
 * The core Spring pointcut abstraction.
 *
 * <p>A pointcut is made of a {@link ClassFilter} and a {@link MethodMatcher}.
 * These two basic parts, and a pointcut itself, can be combined to build a composition (for example through {@link org.springframework.aop.support.ComposablePointcut}).
 *
 * @author Rod Johnson
 * @see ClassFilter
 * @see MethodMatcher
 * @see org.springframework.aop.support.Pointcuts
 * @see org.springframework.aop.support.ClassFilters
 * @see org.springframework.aop.support.MethodMatchers
 */
public interface Pointcut {

	/**
	 * Return the ClassFilter of this pointcut.
	 * @return the ClassFilter (never {@code null})
	 */
	ClassFilter getClassFilter();

	/**
	 * Return the MethodMatcher of this pointcut.
	 * @return the MethodMatcher (never {@code null})
	 */
	MethodMatcher getMethodMatcher();


	/**
	 * Canonical pointcut instance that always matches.
	 */
	Pointcut TRUE = TruePointcut.INSTANCE;

}
```

### 5. Main Implementations

1. **NameMatchMethodPointcut** 

   + A pointcut that matches by method name. You can configure given method names or wildcards to match the methods of the target class.

2. **JdkRegexpMethodPointcut** 

   + A pointcut that matches methods with regular expressions. You can use a regular expression to set the matching rule of methods.
   
3. **AspectJExpressionPointcut** 

   + A pointcut that matches methods with an AspectJ pointcut expression. You can use the AspectJ syntax to define more flexible pointcut matching rules.

4. **ComposablePointcut** 

   + A composable pointcut. It lets you combine several pointcuts, and supports logic operations such as AND, OR, and NOT.

5. **StaticMethodMatcherPointcut** 

   + A static method matcher pointcut. It sets the method matching rule directly and does not support dynamic matching.

6. **TruePointcut** 

   + A pointcut that always matches. It does no matching at all, so it matches all classes and methods.

7. **AnnotationMatchingPointcut**

   + A pointcut defined by annotation matching. It matches classes or methods by the given annotation type, and is used to apply advice to the methods of target objects that have a specific annotation.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class AbstractExpressionPointcut
class AbstractRegexpMethodPointcut
class AnnotationMatchingPointcut
class AspectJExpressionPointcut
class DynamicMethodMatcherPointcut
class ExpressionPointcut {
<<Interface>>

}
class JdkRegexpMethodPointcut
class NameMatchMethodPointcut
class Pointcut {
<<Interface>>

}
class StaticMethodMatcherPointcut
class TruePointcut

AbstractExpressionPointcut  ..>  ExpressionPointcut 
AbstractRegexpMethodPointcut  -->  StaticMethodMatcherPointcut 
AnnotationMatchingPointcut  ..>  Pointcut 
AspectJExpressionPointcut  -->  AbstractExpressionPointcut 
DynamicMethodMatcherPointcut  ..>  Pointcut 
ExpressionPointcut  -->  Pointcut 
JdkRegexpMethodPointcut  -->  AbstractRegexpMethodPointcut 
NameMatchMethodPointcut  -->  StaticMethodMatcherPointcut 
StaticMethodMatcherPointcut  ..>  Pointcut 
TruePointcut  ..>  Pointcut 
~~~

### 7. Best Practices

**MyCustomPointcut**

This example uses a custom `Pointcut` object `MyCustomPointcut`. In the `customPointcut` method, we create an instance of `MyCustomPointcut` and show how it matches classes and methods with the `showMatchesLog` method. Finally, we call the `showMatchesLog` method to check how the `MyCustomPointcut` object matches the methods of the target class `MyService`, and print the match results.

```java
public class PointcutDemo {
    public static void main(String[] args) {
        customPointcut();
    }

    /**
     * Custom Pointcut
     */
    private static void customPointcut() {
        MyCustomPointcut pointcut = new MyCustomPointcut();
        showMatchesLog(pointcut);
    }
    
    public static void showMatchesLog(Pointcut pointcut) {
        try {
            Class<MyService> target = MyService.class;
            Method getNameMethod = target.getDeclaredMethod("getName");
            Method getAgeMethod = target.getDeclaredMethod("getAge");
            Method setNameMethod = target.getDeclaredMethod("setName");

            ClassFilter classFilter = pointcut.getClassFilter();
            MethodMatcher methodMatcher = pointcut.getMethodMatcher();

            System.out.println("ClassFilter MyService = " + classFilter.matches(target));
            System.out.println("MethodMatcher MyService getName = " + methodMatcher.matches(getNameMethod, target));
            System.out.println("MethodMatcher MyService getAge = " + methodMatcher.matches(getAgeMethod, target));
            System.out.println("MethodMatcher MyService setName = " + methodMatcher.matches(setNameMethod, target));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

This defines a custom pointcut `MyCustomPointcut`. It matches all classes, and all methods whose names start with "get". This means an aspect defined with this pointcut intercepts calls of all methods that start with "get" in all classes.

```java
class MyCustomPointcut implements Pointcut {

    @Override
    public ClassFilter getClassFilter() {
        // Match all classes
        return clazz -> true;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new MethodMatcher() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                // Match all methods whose names start with "get"
                return method.getName().startsWith("get");
            }

            @Override
            public boolean isRuntime() {
                // Whether dynamic matching is needed at runtime
                return false;
            }

            @Override
            public boolean matches(Method method, Class<?> targetClass, Object... args) {
                // Runtime matching is not needed here, so simply return false
                return false;
            }
        };
    }
}
```

The `MyService` class is an example service class. It is marked with the class-level `@MyClassAnnotation` annotation and has three methods: `getName()`, `setName()`, and `getAge()`. The `setName()` method is marked with the method-level `@MyMethodAnnotation` annotation.

```java
@MyClassAnnotation
public class MyService {

    public void getName() {
        System.out.println("getName...");
    }

    @MyMethodAnnotation
    public void setName() {
        System.out.println("setName...");
    }

    public void getAge() {
        System.out.println("getAge...");
    }
}
```

In the run result, the class-level filter of `MyService` matches. At the method level, the `getName` and `getAge` methods match, but the `setName` method does not.

```java
ClassFilter MyService = true
MethodMatcher MyService getName = true
MethodMatcher MyService getAge = true
MethodMatcher MyService setName = false
```

**AspectJExpressionPointcut**

This example uses `AspectJExpressionPointcut` to create a pointcut based on an AspectJ expression. In the `aspectJExpressionPointcut` method, we create an instance of `AspectJExpressionPointcut` and set the AspectJ expression `"execution(* com.xcs.spring.MyService.get*())"`, which matches all methods that start with `get` in the `com.xcs.spring.MyService` class. Finally, we call the `showMatchesLog` method to check how the `AspectJExpressionPointcut` object matches the methods of the given class, and print the match results.

```java
public class PointcutDemo {
    public static void main(String[] args) {
        aspectJExpressionPointcut();
    }

     /**
     * AspectJExpressionPointcut
     */
    private static void aspectJExpressionPointcut() {
        // Create the AspectJ expression pointcut
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.xcs.spring.MyService.get*())");
        showMatchesLog(pointcut);
    }
}
```

In the run result, the class-level filter of `MyService` matches. At the method level, the `getName` and `getAge` methods match, but the `setName` method does not.

```java
ClassFilter MyService = true
MethodMatcher MyService getName = true
MethodMatcher MyService getAge = true
MethodMatcher MyService setName = false
```

**AnnotationMatchingPointcut**

This example uses `AnnotationMatchingPointcut` to create a pointcut based on annotation matching. In the `annotationMatchingPointcut` method, we create an instance of `AnnotationMatchingPointcut`, give the class-level annotation `MyClassAnnotation` and the method-level annotation `MyMethodAnnotation`, and set it not to check inherited methods. Finally, we call the `showMatchesLog` method to check how the `AnnotationMatchingPointcut` object matches the methods of the given class, and print the match results.

```java
public class PointcutDemo {
    public static void main(String[] args) {
        annotationMatchingPointcut();
    }

    /**
     * AnnotationMatchingPointcut
     */
    private static void annotationMatchingPointcut() {
        // Use the AnnotationMatchingPointcut pointcut
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(MyClassAnnotation.class, MyMethodAnnotation.class, false);
        showMatchesLog(pointcut);
    }
}
```

In the run result, the class-level filter of `MyService` matches. At the method level, the matcher matches the `setName` method, but not the `getName` and `getAge` methods.

```java
ClassFilter MyService = true
MethodMatcher MyService getName = false
MethodMatcher MyService getAge = false
MethodMatcher MyService setName = true
```

**NameMatchMethodPointcut**

This example uses `NameMatchMethodPointcut` to create a pointcut based on method names. In the `nameMatchMethodPointcut` method, we create an instance of `NameMatchMethodPointcut` and add the method name to match, `getAge`. Then we call the `showMatchesLog` method to check how the `NameMatchMethodPointcut` object matches the methods of the given class, and print the match results.

```java
public class PointcutDemo {
    public static void main(String[] args) {
        nameMatchMethodPointcut();
    }

    /**
     * AspectJExpressionPointcut
     */
    private static void nameMatchMethodPointcut() {
        // Use the AnnotationMatchingPointcut pointcut
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.addMethodName("getAge");
        showMatchesLog(pointcut);
    }
}
```

In the run result, the class-level filter of `MyService` matches. At the method level, the matcher matches the `getAge` method, but not the `getName` and `setName` methods.

```java
ClassFilter MyService = true
MethodMatcher MyService getName = false
MethodMatcher MyService getAge = true
MethodMatcher MyService setName = false
```

**JdkRegexpMethodPointcut**

This example uses `JdkRegexpMethodPointcut` to create a pointcut based on JDK regular expressions. In the `jdkRegexpMethodPointcut` method, we create an instance of `JdkRegexpMethodPointcut` and set the regular expression pattern `".*set.*"`, which matches all method names that contain the string "set". Then we call the `showMatchesLog` method to check how the `JdkRegexpMethodPointcut` object matches the methods of the given class, and print the match results.

```java
public class PointcutDemo {
    public static void main(String[] args) {
        jdkRegexpMethodPointcut();
    }

    /**
     * JdkRegexpMethodPointcut
     */
    private static void jdkRegexpMethodPointcut() {
        JdkRegexpMethodPointcut pointcut = new JdkRegexpMethodPointcut();
        pointcut.setPattern(".*set.*");
        showMatchesLog(pointcut);
    }
}
```

In the run result, the class-level filter of `MyService` matches. At the method level, the matcher matches the `setName` method, but not the `getName` and `getAge` methods.

```java
ClassFilter MyService = true
MethodMatcher MyService getName = false
MethodMatcher MyService getAge = false
MethodMatcher MyService setName = true
```
