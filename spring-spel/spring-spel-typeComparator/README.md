## TypeComparator

- [TypeComparator](#typecomparator)
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

1. **Type system**

   + It is important to know the type system of a programming language. This includes the difference between static and dynamic typing, and between strong and weak typing. Knowing the rules of type inheritance, implementation and conversion helps you understand type comparison.

2. **Generics**

   + Generics are an important feature of Java. They let classes, interfaces and methods use type parameters when they are defined. It may help to know how generics are handled at compile time and at runtime, and what type erasure is, to understand how `TypeComparator` is implemented.

3. **Reflection**

   + Reflection is a powerful Java feature. It lets a program inspect and work with classes, objects, methods and fields at runtime. Knowing how to use reflection to get class information, call methods and use fields may help when you implement `TypeComparator`.

4. **Design patterns**

   + Knowing some common design patterns, such as the strategy pattern and the adapter pattern, may help you understand the design and use of `TypeComparator`.

### 3. Description

The `TypeComparator` interface is an important component in the Spring Framework. It defines a comparator for object types. By implementing it, you can customize how objects are compared to fit your needs. For example, in the Spring Expression Language (SpEL), it decides whether two objects have the same type or can be compared.

### 4. Main Features

1. **Type comparison**

   + The `TypeComparator` interface defines the `compare(T o1, T o2)` method to compare two objects. You can implement it as you need, for example to decide whether two objects have the same type or are related in the inheritance hierarchy.

2. **Check whether objects can be compared**

   + The `TypeComparator` interface defines the `canCompare(Object o1, Object o2)` method to decide whether two objects can be compared. This is needed in some cases. For example, before comparing objects, you may need to check that they are of a given type or have a given property.

3. **Custom comparison logic**

   + By implementing the `TypeComparator` interface, you can define your own comparison logic for your needs. For example, you can decide the type of an object by some of its properties or features, and not only by the default behavior of the Java type system.

4. **Extensibility**

   + The `TypeComparator` interface gives the Spring Framework a flexible and extensible way to handle object type comparison. With a custom `TypeComparator`, you can extend a Spring application for different business needs.

### 5. Interface Source

The `TypeComparator` interface sets the standard for object type comparators. An implementation must be able to compare two objects, and must tell whether two objects can be compared. The main goal of this interface is to support object comparison in the Spring Expression Language (SpEL). It gives a flexible way to customize the comparison logic.

```java
/**
 * Instances of a type comparator should be able to compare pairs of objects for equality.
 * The specification of the return value is the same as for {@link java.lang.Comparable}.
 *
 * @author Andy Clement
 * @since 3.0
 * @see java.lang.Comparable
 */
public interface TypeComparator {

    /**
     * Return {@code true} if the comparator can compare these two objects.
     * @param firstObject the first object
     * @param secondObject the second object
     * @return {@code true} if the comparator can compare these objects
     */
    boolean canCompare(@Nullable Object firstObject, @Nullable Object secondObject);

    /**
     * Compare two given objects.
     * @param firstObject the first object
     * @param secondObject the second object
     * @return 0 if they are equal, <0 if the first is smaller than the second,
     * or >0 if the first is larger than the second
     * @throws EvaluationException if a problem occurs during comparison
     * (or if they are not comparable in the first place)
     */
    int compare(@Nullable Object firstObject, @Nullable Object secondObject) throws EvaluationException;

}
```

`StandardTypeComparator` is the standard implementation of the `TypeComparator` interface. It supports comparing `Number` types and types that implement the `Comparable` interface. It has comparison logic for different types of objects, including primitive numbers, BigDecimal and BigInteger. It gives a flexible and reliable way to compare object types.

```java
/**
 * Basic implementation of {@link TypeComparator}: supports comparison of
 * {@link Number} types as well as types implementing {@link Comparable}.
 * 
 * Authors: Andy Clement
 * Authors: Juergen Hoeller
 * Authors: Giovanni Dall'Oglio Risso
 * Since version 3.0
 */
public class StandardTypeComparator implements TypeComparator {

	@Override
	public boolean canCompare(@Nullable Object left, @Nullable Object right) {
		// If one of the objects is null, they can be compared
		if (left == null || right == null) {
			return true;
		}
		// If both objects are Number types, they can be compared
		if (left instanceof Number && right instanceof Number) {
			return true;
		}
		// If the left object implements Comparable, they can be compared
		if (left instanceof Comparable) {
			return true;
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public int compare(@Nullable Object left, @Nullable Object right) throws SpelEvaluationException {
		// If one of the objects is null, return the matching value
		if (left == null) {
			return (right == null ? 0 : -1);
		}
		else if (right == null) {
			return 1;  // The left object cannot be null here
		}

		// Basic number comparison
		if (left instanceof Number && right instanceof Number) {
			Number leftNumber = (Number) left;
			Number rightNumber = (Number) right;

			if (leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal) {
				// Compare BigDecimal values
				BigDecimal leftBigDecimal = NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
				BigDecimal rightBigDecimal = NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
				return leftBigDecimal.compareTo(rightBigDecimal);
			}
            // Compare other number types, including Double, Float, BigInteger, Long, Integer, Short, Byte
            // If it is none of these, compare as double
			else if (leftNumber instanceof Double || rightNumber instanceof Double) {
				return Double.compare(leftNumber.doubleValue(), rightNumber.doubleValue());
			}
            // Compare other number types
			else if (leftNumber instanceof Float || rightNumber instanceof Float) {
				return Float.compare(leftNumber.floatValue(), rightNumber.floatValue());
			}
			else if (leftNumber instanceof BigInteger || rightNumber instanceof BigInteger) {
				BigInteger leftBigInteger = NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
				BigInteger rightBigInteger = NumberUtils.convertNumberToTargetClass(rightNumber, BigInteger.class);
				return leftBigInteger.compareTo(rightBigInteger);
			}
			else if (leftNumber instanceof Long || rightNumber instanceof Long) {
				return Long.compare(leftNumber.longValue(), rightNumber.longValue());
			}
			else if (leftNumber instanceof Integer || rightNumber instanceof Integer) {
				return Integer.compare(leftNumber.intValue(), rightNumber.intValue());
			}
			else if (leftNumber instanceof Short || rightNumber instanceof Short) {
				return Short.compare(leftNumber.shortValue(), rightNumber.shortValue());
			}
			else if (leftNumber instanceof Byte || rightNumber instanceof Byte) {
				return Byte.compare(leftNumber.byteValue(), rightNumber.byteValue());
			}
			else {
				// Unknown Number subtype -> best guess is to compare as double
				return Double.compare(leftNumber.doubleValue(), rightNumber.doubleValue());
			}
		}

		try {
			// If the left object implements Comparable, compare with compareTo
			if (left instanceof Comparable) {
				return ((Comparable<Object>) left).compareTo(right);
			}
		}
		catch (ClassCastException ex) {
			// Throw an exception: the two objects are not comparable
			throw new SpelEvaluationException(ex, SpelMessage.NOT_COMPARABLE, left.getClass(), right.getClass());
		}

		// Throw an exception: the two objects are not comparable
		throw new SpelEvaluationException(SpelMessage.NOT_COMPARABLE, left.getClass(), right.getClass());
	}

}
```

### 6. Main Implementations

1. **StandardTypeComparator**
   + `StandardTypeComparator` is the standard implementation of the `TypeComparator` interface. It can compare `Number` types and types that implement the `Comparable` interface.

### 7. Best Practices

This example uses the `TypeComparator` interface of the Spring Expression Language (SpEL) to compare objects. First, it creates a `StandardEvaluationContext` as the evaluation context, and a `SpelExpressionParser` to parse SpEL expressions. Then it parses an expression with a comparison operator into an `Expression` object. Next, it calls `getValue` with the evaluation context. The SpEL engine runs the expression and compares with a `TypeComparator`. Finally, it prints the result.

```java
public class TypeComparatorDemo {
    public static void main(String[] args) {
        // Create an EvaluationContext
        StandardEvaluationContext context = new StandardEvaluationContext();
        
        // Create a SpEL expression parser
        SpelExpressionParser parser = new SpelExpressionParser();
        
        // Parse the expression
        Expression expression = parser.parseExpression("'2' < '-5.0'");
        
        // Compare with the TypeComparator
        boolean result = expression.getValue(context,Boolean.class);
        
        // Print the comparison result
        System.out.println("result : " + result);
    }
}
```

Output: the expression `'2' < '-5.0'` returns `false`. In SpEL, the string `'2'` is not smaller than the string `'-5.0'`, so the result is `false`.

```java
result : false
```

### 8. Relationship with Other Components

1. **java.lang.Comparable**

   + The `TypeComparator` interface is related to the `Comparable` interface. `Comparable` defines the natural order of an object itself. `TypeComparator` gives a way to compare any two objects. The `StandardTypeComparator` class uses the `Comparable` implementation of objects to compare them.

2. **StandardTypeComparator**

   + `StandardTypeComparator` is the default implementation of the `TypeComparator` interface. It provides standard logic to compare object types. It is widely used in the Spring Framework, especially in SpEL, where types are compared.

3. **StandardEvaluationContext**

   + In SpEL, it holds the context information needed to evaluate an expression. The `TypeComparatorDemo` class uses `StandardEvaluationContext` to create a context object, so it can evaluate a SpEL expression.

4. **`SpelExpressionParser` class**

   + The class that parses SpEL expressions. The `TypeComparatorDemo` class uses `SpelExpressionParser` to create an expression parser, so it can parse a SpEL expression.

### 9. FAQ

1. **Custom comparison logic**

   + Sometimes you need your own comparison logic for special needs. This may mean special handling for objects of a given type, or taking the inheritance relationship into account.

2. **Type conversion problems**

   + When you compare objects, you may run into type conversion problems, especially with objects of different types. Make sure to convert the objects to the right type before you compare them.
