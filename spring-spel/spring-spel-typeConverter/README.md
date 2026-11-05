## TypeConverter

- [TypeConverter](#typeconverter)
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

1. **Type conversion**

   + It is important to know what type conversion is and why software often needs it. Knowing about basic data types, conversion between objects, and type compatibility helps you understand what `TypeConverter` does and how to use it.

2. **Spring Expression Language (SpEL)**

   + `TypeConverter` is usually used with the Spring Expression Language (SpEL), so you should know the basic syntax and usage of SpEL. SpEL is a powerful expression language that evaluates expressions at runtime. Learning to write and parse SpEL expressions helps you use `TypeConverter` for type conversion in SpEL.

3. **Java reflection**

   + `TypeConverter` often uses Java reflection to get the type information of objects and convert them. Knowing how reflection works, and how to use reflection APIs such as `Class`, `Method` and `Field`, helps you understand how `TypeConverter` is implemented.

4. **Extending Spring type converters**

   + It is also useful to know how to customize and extend type converters in the Spring Framework. Spring has many built-in type converters. It also lets you write your own for special needs. Learning to write a custom type converter helps you understand how `TypeConverter` is used and implemented.

### 3. Description

The `TypeConverter` interface is the core interface for type conversion in SpEL (Spring Expression Language). It converts objects from one type to another. For example, it converts a string to a number or an object to a string. It gives Spring applications one flexible way to convert types, so it can handle many complex cases.

### 4. Main Features

1. **Type conversion**

   + It can convert an object to another type. For example, it converts a string to a number or an object to a string.

2. **Type compatibility check**

   + It can check whether an object can be converted to a given type. This avoids exceptions caused by wrong type conversions.

3. **Custom conversion rules**

   + You can define your own conversion rules. You can extend and customize the type converter for special cases.

4. **SpEL integration**

   + As part of SpEL, the `TypeConverter` interface works with the Spring Expression Language (SpEL). You can use it directly in SpEL expressions for flexible type conversion.

5. **Default implementation**

   + Spring provides a default `TypeConverter` implementation. It also supports custom type converters, so you can fit different applications and needs.

### 5. Interface Source

The `TypeConverter` interface defines a type converter. It converts values of different types into each other while an expression is evaluated. It has a method to check whether a conversion is possible and a method to do the conversion. It also supports converting typed collections.

```java
/**
 * A type converter can convert values between different types encountered during
 * expression evaluation. This is an SPI for the expression parser; see
 * {@link org.springframework.core.convert.ConversionService} for the primary
 * user API to Spring's conversion facilities.
 * 
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface TypeConverter {

	/**
	 * Return {@code true} if the type converter can convert the specified type
	 * to the desired target type.
	 * @param sourceType a type descriptor that describes the source type
	 * @param targetType a type descriptor that describes the requested result type
	 * @return {@code true} if that conversion can be performed
	 */
	boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType);

	/**
	 * Convert (or coerce) a value from one type to another, for example from a
	 * {@code boolean} to a {@code String}.
	 * <p>The {@link TypeDescriptor} parameters enable support for typed collections:
	 * A caller may prefer a {@code List<Integer>}, for example, rather than simply
	 * any {@code List}.
	 * @param value the value to be converted
	 * @param sourceType a type descriptor that supplies extra information about the source object
	 * @param targetType a type descriptor that supplies extra information about the requested result type
	 * @return the converted value
	 * @throws EvaluationException if conversion failed or is not possible to begin with
	 */
	@Nullable
	Object convertValue(@Nullable Object value, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType);

}
```

`StandardTypeConverter` is the default implementation of the `TypeConverter` interface. It delegates to the core Spring `ConversionService` to do the conversion. It has two constructors. One uses the default `ConversionService`. The other takes a `ConversionService` that you give it. It implements `canConvert` to check whether a conversion is possible, and `convertValue` to do the real conversion.

```java
/**
 * Default implementation of the {@link TypeConverter} interface,
 * delegating to a core Spring {@link ConversionService}.
 * 
 * @author Juergen Hoeller
 * @author Andy Clement
 * @since 3.0
 * @see org.springframework.core.convert.ConversionService
 */
public class StandardTypeConverter implements TypeConverter {

	private final ConversionService conversionService;


	/**
	 * Create a StandardTypeConverter for the default ConversionService.
	 * @see DefaultConversionService#getSharedInstance()
	 */
	public StandardTypeConverter() {
		this.conversionService = DefaultConversionService.getSharedInstance();
	}

	/**
	 * Create a StandardTypeConverter for the given ConversionService.
	 * @param conversionService the ConversionService to delegate to
	 */
	public StandardTypeConverter(ConversionService conversionService) {
		Assert.notNull(conversionService, "ConversionService must not be null");
		this.conversionService = conversionService;
	}


	@Override
	public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
		return this.conversionService.canConvert(sourceType, targetType);
	}

	@Override
	@Nullable
	public Object convertValue(@Nullable Object value, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
		try {
			return this.conversionService.convert(value, sourceType, targetType);
		}
		catch (ConversionException ex) {
			throw new SpelEvaluationException(ex, SpelMessage.TYPE_CONVERSION_ERROR,
					(sourceType != null ? sourceType.toString() : (value != null ? value.getClass().getName() : "null")),
					targetType.toString());
		}
	}

}
```

### 6. Main Implementations

1. **StandardTypeConverter**
   + `StandardTypeConverter` is the default implementation of the `TypeConverter` interface. It uses the core Spring `ConversionService` to convert types.

### 7. Best Practices

This example uses a SpEL expression and a `TypeConverter` to convert a type. First, it creates a parser with the SpEL expression parser. It also creates an EvaluationContext as the environment for the expression. Then it defines a string value to convert and parses it to get an expression object. Next, `expression.getValue(context, Integer.class)` converts the string value to an integer. Finally, it prints the converted integer value.

```java
public class TypeConverterDemo {

    public static void main(String[] args) {
        // Create a SpEL expression parser
        SpelExpressionParser parser = new SpelExpressionParser();

        // Create an EvaluationContext
        StandardEvaluationContext context = new StandardEvaluationContext();

        // Define a value to convert
        String stringValue = "'123'";

        // Parse the expression
        Expression expression = parser.parseExpression(stringValue);

        // Convert with the TypeConverter
        Integer intValue = expression.getValue(context, Integer.class);

        // Print the converted value
        System.out.println("Converted Integer value: " + intValue);
    }
}
```

Output: the string `'123'` is converted to an integer, and the integer value `123` is printed.

```properties
Converted Integer value: 123
```

### 8. Relationship with Other Components

1. **ConversionService interface**

   +  The `TypeConverter` interface is related to the `ConversionService` interface. It is the SPI (Service Provider Interface) for `ConversionService`. `ConversionService` gives a general way to convert types. It defines methods to convert between different types. `TypeConverter` does the actual type conversion inside `ConversionService`. With the `ConversionService` interface, it is easier to manage and use `TypeConverter`.

2. **SpEL (Spring Expression Language)**

   +  The `TypeConverter` interface is usually used with SpEL to support type conversion in expressions. In a SpEL expression, the methods of `TypeConverter` can be called directly to convert types. This allows more complex expression evaluation.

3. **StandardEvaluationContext class**

   + The `TypeConverter` interface is usually used with the `StandardEvaluationContext` class. `StandardEvaluationContext` provides the environment for an expression, including variables, functions and type converters. You can configure a `TypeConverter` in `StandardEvaluationContext` and use it in SpEL expressions.

### 9. FAQ

1. **Type conversion fails**

   + A type conversion can fail. For example, the source type cannot be converted to the target type, or an exception happens during conversion. This can cause logic errors or exceptions in the application.

2. **Incompatible types**

   + Sometimes the source type and the target type are not compatible, so the conversion is not possible. For example, converting an object to an incompatible target type, or converting a string to a type that cannot hold it.

3. **Missing type converter**

   + If no suitable type converter is available, some conversions cannot be done. In that case, write a custom type converter for the conversion you need.
