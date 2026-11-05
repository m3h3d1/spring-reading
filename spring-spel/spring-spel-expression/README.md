## Expression

- [Expression](#expression)
  - [1. Overview](#1-overview)
  - [2. Background Knowledge](#2-background-knowledge)
  - [3. Description](#3-description)
  - [4. Main Features](#4-main-features)
  - [5. Interface Source](#5-interface-source)
  - [6. Main Implementations](#6-main-implementations)
  - [7. Best Practices](#7-best-practices)
  - [8. Sequence Diagram](#8-sequence-diagram)
  - [9. Source Code Analysis](#9-source-code-analysis)
  - [10. Relationship with Other Components](#10-relationship-with-other-components)
  - [11. FAQ](#11-faq)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Background Knowledge

1. **Expression language (SpEL)**

   + You should understand the basic syntax and features of an expression language. This includes the structure of expressions, operators, functions and variable references. You should know where and how SpEL is used, and how it is applied in Spring.

2. **Evaluation context**

   + You should know the context in which an expression is evaluated, including the root object, variables and registered functions. You should understand how to give a custom context when you evaluate an expression.

3. **Type conversion and type inference**

   + You should understand how expressions convert types, and how to do type inference and type-safe expression evaluation.

4. **Using the SpEL parser**

   + You should know how to use the `SpelExpressionParser` class to parse an expression string into an `Expression` object, and how to evaluate the expression.

### 3. Description

The `Expression` interface is one of the key components of the expression language (SpEL) module in the Spring Framework. It defines methods to evaluate an expression and to get the string form of the expression. With this interface, we can use the powerful syntax of SpEL to query and operate on objects dynamically. This helps Spring applications handle configuration, conditions and data conversion in a flexible way. Implementations of this interface are usually created when `SpelExpressionParser` parses an expression string. They give the Spring Framework an easy way to be dynamic and extensible.

### 4. Main Features

1. **Evaluation**

   + The interface lets us evaluate an expression and get its result. When you call the `getValue()` method, the expression is evaluated in the given evaluation context and the result is returned. So we can get the result of an expression dynamically at runtime. This supports many flexible logic and business needs.

2. **Type conversion**

   + The `Expression` interface lets us set the type we expect for the result, and the value is converted during evaluation. When you call the `getValue()` method with an `expectedType` parameter, the result is converted to the given target type. This is important to keep the result type safe, and it avoids type conversion errors.

3. **Get the expression string**

   + The `Expression` interface has a `getExpressionString()` method. It returns the string form of the expression. This is useful for debugging and logging. You can easily see the real content of the expression to find and analyze problems.

### 5. Interface Source

The `Expression` interface is one of the core components of the expression language (SpEL) module in the Spring Framework. It wraps an expression string that was parsed before. It has methods to get the original expression string and to evaluate the expression in the default standard context.

```java
/**
 * An expression capable of evaluating itself against context objects.
 * Encapsulates the details of a previously parsed expression string.
 * Provides a common abstraction for expression evaluation.
 *
 * @author Keith Donald
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface Expression {

	/**
	 * Return the original string used to create this expression (unmodified).
	 * @return the original expression string
	 */
	String getExpressionString();

	/**
	 * Evaluate this expression in the default standard context.
	 * @return the evaluation result
	 * @throws EvaluationException if there is a problem during evaluation
	 */
	@Nullable
	Object getValue() throws EvaluationException;

	// ... [code omitted for brevity]
}
```

### 6. Main Implementations

1. **CompositeStringExpression**
   + It represents a composite string expression. It is made of several sub-expressions, and each sub-expression is a string.
   
2. **LiteralExpression**
   + It represents a literal expression. It returns the given literal value directly, without any calculation or parsing.
   
3. **SpelExpression**
+ One of the default implementations of SpEL. It evaluates SpEL expressions.

### 7. Best Practices

This example uses the Spring Expression Language (SpEL) parser to parse and evaluate a basic math expression. First, it creates a SpEL parser instance. Then it uses the parser to parse the string expression `"100 + 10"` into an `Expression` object. Next, it calls `getValue(Integer.class)` to compute the value of the expression, and prints the result.

```java
public class ExpressionDemo {
    public static void main(String[] args) {
        // Create the parser instance
        ExpressionParser parser = new SpelExpressionParser();
        // Parse a basic expression
        Expression expression = parser.parseExpression("100 + 10");
        // Compute the expression result
        Integer result = expression.getValue(Integer.class);
        System.out.println("Result of expression '100 + 10': " + result);
    }
}
```

Output: this shows that `Expression` can handle expressions such as math operations, and it computes the result correctly.

```properties
Result of expression '100 + 10': 110
```

### 8. Sequence Diagram

~~~mermaid
sequenceDiagram
    autonumber
    title: Expression Sequence Diagram
    
ExpressionDemo->>SpelExpression: getValue(expectedResultType)
Note over ExpressionDemo,SpelExpression: Send the expression evaluation request
SpelExpression->>SpelNodeImpl: getTypedValue(expressionState)
Note over SpelExpression,SpelNodeImpl: Call the inner node to get the value
SpelNodeImpl->>OpPlus: getValueInternal(state)
Note over SpelNodeImpl,OpPlus: Handle the addition
OpPlus->>SpelNodeImpl: Return TypedValue
Note over OpPlus,SpelNodeImpl: Return the addition result
SpelNodeImpl->>SpelExpression: Return TypedValue
Note over SpelNodeImpl,SpelExpression: Return the result to the SpEL expression
SpelExpression->>ExpressionUtils: convertTypedValue(context,typedValue,targetType)
Note over SpelExpression,ExpressionUtils: Convert the result type
ExpressionUtils->>SpelExpression: Return the SpEL expression result
Note over ExpressionUtils,SpelExpression: Return the converted result
SpelExpression->>ExpressionDemo: Return the SpEL expression result
Note over SpelExpression,ExpressionDemo: Return the expression evaluation result
~~~



### 9. Source Code Analysis

In the `org.springframework.expression.spel.standard.SpelExpression#getValue(expectedResultType)` method, it first checks whether the expression is already compiled (by checking that `compiledAst` is not null). If it is compiled, it tries to evaluate the expression on the compiled abstract syntax tree. If an exception happens during compiled evaluation and the compiler mode is `SpelCompilerMode.MIXED`, the expression goes back to interpreted mode. If the compiler mode is `SpelCompilerMode.IMMEDIATE`, the exception is thrown to the caller. If the expression is not compiled yet, it is evaluated in interpreted mode.

```java
@Override
@Nullable
public <T> T getValue(@Nullable Class<T> expectedResultType) throws EvaluationException {
    CompiledExpression compiledAst = this.compiledAst;
    if (compiledAst != null) {
        try {
            // Get the evaluation context
            EvaluationContext context = getEvaluationContext();
            // Evaluate the expression on the compiled abstract syntax tree
            Object result = compiledAst.getValue(context.getRootObject().getValue(), context);
            // If the expected result type is null, return the result directly
            if (expectedResultType == null) {
                return (T) result;
            } else {
                // Otherwise convert the result to the expected result type
                return ExpressionUtils.convertTypedValue(getEvaluationContext(), new TypedValue(result), expectedResultType);
            }
        } catch (Throwable ex) {
            // In mixed mode, fall back to interpreted mode
            if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                // Go back to interpreted mode
                this.compiledAst = null;
                // Reset the interpretation counter
                this.interpretedCount.set(0);
            } else {
                // In immediate compilation mode, pass the exception to the caller
                throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION);
            }
        }
    }

    // If the expression is not compiled yet, evaluate it in interpreted mode
    ExpressionState expressionState = new ExpressionState(getEvaluationContext(), this.configuration);
    TypedValue typedResultValue = this.ast.getTypedValue(expressionState);
    // Check the compile state
    checkCompile(expressionState);
    // Convert the result to the expected result type and return it
    return ExpressionUtils.convertTypedValue(expressionState.getEvaluationContext(), typedResultValue, expectedResultType);
}
```

The `org.springframework.expression.spel.ast.SpelNodeImpl#getTypedValue` method calls `getValueInternal()` to get the value of the expression. It wraps the value in a `TypedValue` object and returns it. This method gets the value of the expression in the given expression state.

```java
@Override
public final TypedValue getTypedValue(ExpressionState expressionState) throws EvaluationException {
    return getValueInternal(expressionState);
}
```

In the `org.springframework.expression.spel.ast.OpPlus#getValueInternal` method, it first checks the left operand. If there is only one operand, it does a unary plus. If there are two operands, it does an addition. For the addition, it converts and calculates according to the types of the operands, and returns the result.

```java
/**
 * Internal evaluation logic of the expression, mainly for the addition operation.
 * 
 * @param state the state of the expression
 * @return the result of the expression
 * @throws EvaluationException if there is a problem during evaluation
 */
@Override
public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    // Get the left operand
    SpelNodeImpl leftOp = getLeftOperand();

    // If there is only one operand, do a unary plus
    if (this.children.length < 2) {
        // Get the value of the left operand
        Object operandOne = leftOp.getValueInternal(state).getValue();
        // If the left operand is a number, return the number directly
        if (operandOne instanceof Number) {
            // Set the type descriptor of the return value by the number type
            if (operandOne instanceof Double) {
                this.exitTypeDescriptor = "D";
            } else if (operandOne instanceof Float) {
                this.exitTypeDescriptor = "F";
            } else if (operandOne instanceof Long) {
                this.exitTypeDescriptor = "J";
            } else if (operandOne instanceof Integer) {
                this.exitTypeDescriptor = "I";
            }
            return new TypedValue(operandOne);
        }
        // If the left operand is not a number, do the addition operation
        return state.operate(Operation.ADD, operandOne, null);
    }

    // Get the value of the left operand
    TypedValue operandOneValue = leftOp.getValueInternal(state);
    Object leftOperand = operandOneValue.getValue();
    // Get the value of the right operand
    TypedValue operandTwoValue = getRightOperand().getValueInternal(state);
    Object rightOperand = operandTwoValue.getValue();

    // If both operands are numbers, do the addition
    if (leftOperand instanceof Number && rightOperand instanceof Number) {
        Number leftNumber = (Number) leftOperand;
        Number rightNumber = (Number) rightOperand;
        // Handle adding numbers of different types
        if (...) {
            // Add BigDecimal values
        } else if (...) {
            // Add Double values
        } else if (...) {
            // Add Float values
        } else if (...) {
            // Add BigInteger values
        } else if (...) {
            // Add Long values
        } else if (...) {
            // Add Integer values
        } else {
            // Add other unknown types, best guess is to add as double
        }
    }

    // If both operands are strings, join the strings
    if (leftOperand instanceof String && rightOperand instanceof String) {
        this.exitTypeDescriptor = "Ljava/lang/String";
        return new TypedValue((String) leftOperand + rightOperand);
    }

    // If the left operand is a string, convert the right operand to a string and join them
    if (leftOperand instanceof String) {
        return new TypedValue(
            leftOperand + (rightOperand == null ? "null" : convertTypedValueToString(operandTwoValue, state)));
    }

    // If the right operand is a string, convert the left operand to a string and join them
    if (rightOperand instanceof String) {
        return new TypedValue(
            (leftOperand == null ? "null" : convertTypedValueToString(operandOneValue, state)) + rightOperand);
    }

    // If the operands are neither numbers nor strings, do the addition operation
    return state.operate(Operation.ADD, leftOperand, rightOperand);
}
```

In the `org.springframework.expression.common.ExpressionUtils#convertTypedValue` method, it first gets the value from the `TypedValue` and checks whether the target type is null. If it is null, the value is returned directly. Then, if an evaluation context `context` is given, the type converter of the context converts the value to the target type. If no context is given, it uses `ClassUtils.isAssignableValue()` to check whether the value can be assigned to the target type directly.

```java
@Nullable
public static <T> T convertTypedValue(
        @Nullable EvaluationContext context, TypedValue typedValue, @Nullable Class<T> targetType) {

    // Get the value of the TypedValue
    Object value = typedValue.getValue();
    // If the target type is null, return the value directly
    if (targetType == null) {
        return (T) value;
    }
    // If an evaluation context is given, convert with the type converter of the context
    if (context != null) {
        return (T) context.getTypeConverter().convertValue(
                value, typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(targetType));
    }
    // If no evaluation context is given, try to use the value as the target type directly
    if (ClassUtils.isAssignableValue(targetType, value)) {
        return (T) value;
    }
    // If it cannot be converted, throw an exception
    throw new EvaluationException("Cannot convert value '" + value + "' to type '" + targetType.getName() + "'");
}
```

### 10. Relationship with Other Components

1. **SpelExpressionParser**

   + The `SpelExpressionParser` class is a factory class that parses expression strings and creates `Expression` objects. Its relation to the `Expression` interface is that it creates `Expression` instances by calling `parseExpression()`, so expressions are parsed and handled.

2. **EvaluationContext**

   + The `EvaluationContext` interface defines the evaluation context of an expression, including the root object, variables and registered functions. The `Expression` interface usually needs an `EvaluationContext` object to evaluate the expression.

3. **TypedValue**

   + The `TypedValue` class represents the result of an expression evaluation. It holds the value and the type descriptor of the value. Implementations of the `Expression` interface often create `TypedValue` objects to represent the result of an expression.

4. **ExpressionParser**

   + The `ExpressionParser` interface is the top-level interface that parses expression strings and creates `Expression` objects. It defines the basic features of a parser. The `SpelExpressionParser` class implements `ExpressionParser`, so it is one concrete implementation that creates `Expression` instances.

5. **EvaluationException**

   + The `EvaluationException` class represents errors during expression evaluation, such as syntax errors and type conversion errors. Implementations of the `Expression` interface usually throw `EvaluationException` to report problems during evaluation.

### 11. FAQ

1. **Expression syntax errors**

   + You may make syntax errors when you write an expression, such as unmatched brackets or a wrong operator.

2. **Type conversion errors**

   + When the type of an operand does not match the expected result type, a type conversion error can happen. For example, converting a string to a number, or an object to an incompatible type.

3. **Null pointer exceptions**

   + If the context object is null when you evaluate an expression, or a property of an object in the expression is null, a null pointer exception may happen.

4. **Slow expression evaluation**

   + Some complex expressions can be slow to evaluate, especially with large data or high concurrency.

5. **Wrong results**

   + A wrong evaluation logic or a wrong expression parse can return a wrong result.

6. **Unsupported operations**

   + Some expressions may contain operations or function calls that are not supported. This can throw an exception during evaluation.
