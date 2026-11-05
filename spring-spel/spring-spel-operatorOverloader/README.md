## OperatorOverloader

- [OperatorOverloader](#operatoroverloader)
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

   + You should know the basic syntax and usage of SpEL. This includes using SpEL expressions in Spring applications for property injection, conditions and more. You should also know the operators, functions and variables that SpEL supports.

2. **Custom operators**

   + You should understand what a custom operator is and why you may need one. Know the role and meaning of custom operators in programming languages and frameworks.

### 3. Description

The `OperatorOverloader` interface is an extension point in the Spring Framework to customize how operators behave. By implementing it, you can define new operator behavior and extend the Spring Expression Language (SpEL). This gives more flexible and customized expression evaluation.

### 4. Main Features

1. **Custom operator behavior**

   + You can define new operator behavior, such as your own arithmetic or comparison operators. This extends SpEL so it can run specific business logic.

2. **Operations on non-standard types**

   + You can run operations on operands of non-standard types in SpEL expressions, such as your own objects or data types. This meets specific business needs.

3. **Extending SpEL**

   + It makes SpEL more flexible and customizable. You can write complex expressions more easily, and tune them for specific cases.

4. **More flexible expression evaluation**

   + You can customize how operators behave to fit your business. This gives more flexible and precise expression evaluation for many complex needs.

### 5. Interface Source

The `OperatorOverloader` interface defines methods to customize operator behavior. One method checks whether an operation is supported. The other method runs the operation. With them, you can extend the Spring Expression Language (SpEL) to support other operand types.

```java
/**
 * By default the mathematical operators {@link Operation} support simple types
 * like numbers. By providing an implementation of OperatorOverloader, a user
 * of the expression language can support these operations on other types.
 *
 * @author Andy Clement
 * @since 3.0
 */
public interface OperatorOverloader {

    /**
     * Return true if the operator overloader supports the specified operation
     * between the two operands and so should be invoked to handle it.
     * @param operation the operation to be performed
     * @param leftOperand the left operand
     * @param rightOperand the right operand
     * @return true if the OperatorOverloader supports the specified operation
     * between the two operands
     * @throws EvaluationException if there is a problem performing the operation
     */
    boolean overridesOperation(Operation operation, @Nullable Object leftOperand, @Nullable Object rightOperand)
            throws EvaluationException;

    /**
     * Execute the specified operation on two operands, returning a result.
     * See {@link Operation} for supported operations.
     * @param operation the operation to be performed
     * @param leftOperand the left operand
     * @param rightOperand the right operand
     * @return the result of performing the operation on the two operands
     * @throws EvaluationException if there is a problem performing the operation
     */
    Object operate(Operation operation, @Nullable Object leftOperand, @Nullable Object rightOperand)
            throws EvaluationException;

}
```

`StandardOperatorOverloader` is the standard implementation of the `OperatorOverloader` interface. Its methods are the default behavior. The `overridesOperation` method returns false. This means no operation is overridden by default. The `operate` method throws an `EvaluationException`. This means no operation is supported by default, and you need to define your own operator behavior.

```java
/**
 * Standard implementation of {@link OperatorOverloader}.
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
public class StandardOperatorOverloader implements OperatorOverloader {

    @Override
    public boolean overridesOperation(Operation operation, @Nullable Object leftOperand, @Nullable Object rightOperand)
            throws EvaluationException {
        // No operation is overridden by default
        return false;
    }

    @Override
    public Object operate(Operation operation, @Nullable Object leftOperand, @Nullable Object rightOperand)
            throws EvaluationException {
        // No operation is supported by default, so throw an exception
        throw new EvaluationException("No operation overloaded by default");
    }

}
```

### 6. Main Implementations

1. **StandardOperatorOverloader**
   + `StandardOperatorOverloader` is the standard implementation of the `OperatorOverloader` interface. It provides the default behavior.

### 7. Best Practices

This example uses a custom operator in the Spring Expression Language (SpEL). It creates an `ExpressionParser` and a `StandardEvaluationContext`, and registers a custom `OperatorOverloader` in the context. Then it can parse a SpEL expression that uses the custom operator. Here the expression is `#myBean1 + #myBean2`, which uses a custom addition operator. The example parses and evaluates it, gets the sum of two `MyBean` objects, and prints it.

```java
public class OperatorOverloaderDemo {

    public static void main(String[] args) {
        // Create the expression parser
        ExpressionParser parser = new SpelExpressionParser();

        // Create the evaluation context
        StandardEvaluationContext context = new StandardEvaluationContext();
        // Create a custom OperatorOverloader and register it in the context
        context.setOperatorOverloader(new CustomOperatorOverloader());
        context.setVariable("myBean1", new MyBean(18));
        context.setVariable("myBean2", new MyBean(20));

        // Define a SpEL expression that uses the custom operator
        Expression expression = parser.parseExpression("#myBean1 + #myBean2");

        // Parse and evaluate the expression
        MyBean myBean = expression.getValue(context, MyBean.class);

        System.out.println("myBean1+myBean2 = " + myBean);
    }
}
```

The `CustomOperatorOverloader` class implements the `OperatorOverloader` interface to customize operator behavior. The `overridesOperation` method checks whether the operation is overridden. Here it checks that both operands are of type `MyBean`. The `operate` method runs the operation. Here it adds the ages of the two `MyBean` objects and returns a new `MyBean`.

```java
public class CustomOperatorOverloader implements OperatorOverloader {
    @Override
    public boolean overridesOperation(Operation operation, Object leftOperand, Object rightOperand) throws EvaluationException {
        return leftOperand instanceof MyBean && rightOperand instanceof MyBean;
    }

    @Override
    public Object operate(Operation operation, Object leftOperand, Object rightOperand) throws EvaluationException {
        return new MyBean(((MyBean) leftOperand).getAge() + ((MyBean) rightOperand).getAge());
    }
}
```

`MyBean` is a simple Java bean. It has an `age` property with a getter and a setter. It also overrides the `toString` method, so the value of `age` is printed when the object is printed.

```java
public class MyBean {

    private int age;

    public MyBean(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "age=" + age +
                '}';
    }
}
```

Output: in the expression `#myBean1 + #myBean2`, the ages of the two `MyBean` objects are 18 and 20. Their sum is 38. So the output is `MyBean{age=38}`.

```java
myBean1+myBean2 = MyBean{age=38}
```

### 8. Relationship with Other Components

1. **Operation**

   + The `Operation` enum defines the operation types SpEL supports, such as addition, subtraction and multiplication. The methods `overridesOperation` and `operate` of the `OperatorOverloader` interface use the `Operation` enum to say which operation to run.

2. **EvaluationException**

   + `EvaluationException` represents an error that happens while an expression is evaluated. The methods of the `OperatorOverloader` interface may throw it to report problems during operator overloading.

3. **StandardEvaluationContext**

   + `StandardEvaluationContext` is the context object SpEL uses to evaluate expressions. With the `OperatorOverloader` interface, you set your own `OperatorOverloader` on the `StandardEvaluationContext` to extend SpEL.

### 9. FAQ

1. **Correctness of operator overloading**

   + When you write a custom operator, make sure it behaves correctly and follows the expected logic. If not, expression evaluation may give wrong results, or the program may even throw exceptions.

2. **Operator conflicts**

   + A custom operator may conflict with an existing operator. Make sure there is no conflict, or handle the conflict properly, to avoid unexpected behavior.

3. **Operator consistency**

   + A custom operator should behave like the existing operators and match what users expect. If not, the expression result may differ from what they expect and cause confusion.


4. **Documentation**

   + Give clear documentation on how to use custom operators and how they behave. Then other people can use and understand them correctly.
