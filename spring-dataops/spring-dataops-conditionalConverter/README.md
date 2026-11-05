## ConditionalConverter

- [ConditionalConverter](#conditionalconverter)
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

1. **Converter**

   + The [Converter](/spring-dataops/spring-dataops-converter/README.md) interface is a key part of type conversion in Spring. It defines one simple method that converts a source type (S) to a target type (T). By implementing it, you can write your own conversion logic and handle complex data conversion in a Spring application.

2. **ConverterFactory**

   + [ConverterFactory](/spring-dataops/spring-dataops-converterFactory/README.md) is a Spring interface for creating type converters. In Spring's type conversion system, it creates `Converter` objects for a specific target type. It is mainly used for a group of conversions that share the same traits.

3. **GenericConverter**

   + [GenericConverter](/spring-dataops/spring-dataops-genericConverter/README.md) is a key Spring interface for type conversion. It is closely tied to Spring's type conversion system and converts an object of one type to another. Compared with `Converter`, `GenericConverter` is more flexible. It allows conversion between multiple pairs of source and target types.

### 3. Description

`ConditionalConverter` is an interface in Spring's type conversion system. It lets a converter check a condition before it converts. The converter can then decide, based on specific conditions, whether it should do the conversion.

### 4. Main Features

1. **Conditional check**

   - `ConditionalConverter` provides the `matches(TypeDescriptor sourceType, TypeDescriptor targetType)` method. This is its main feature. Before the real conversion, the converter uses the source and target types to decide whether it should convert.

2. **Flexibility**

   - By implementing this interface, you can write more flexible conversion logic. The converter can decide at runtime, based on the current context or other conditions, whether to convert. This suits cases where the conversion depends on dynamic conditions or environment variables.

3. **Works with other converter interfaces**

   - You can implement `ConditionalConverter` alone. But it is usually combined with other converter interfaces such as `Converter` or `GenericConverter`. In that case, it adds an extra decision step, so the converter can choose based on conditions.

4. **Lower resource use**

   - For expensive conversions, `ConditionalConverter` avoids wasted work. The converter checks the condition first, before it knows the conversion is needed.

### 5. Interface Source

The `ConditionalConverter` interface is used for conditional type conversion in Spring. It lets a `Converter`, `GenericConverter`, or `ConverterFactory` decide whether to convert. The decision is based on traits of the source and target types, such as annotations or the presence of certain methods.

```java
/**
 * Allows a {@link Converter}, {@link GenericConverter} or {@link ConverterFactory} to
 * conditionally execute based on attributes of the {@code source} and {@code target}
 * {@link TypeDescriptor}.
 *
 * <p>Often used to selectively match custom conversion logic based on the presence of a
 * field or class-level characteristic, such as an annotation or method. For example, when
 * converting from a String field to a Date field, an implementation might return
 * {@code true} if the target field has also been annotated with {@code @DateTimeFormat}.
 *
 * <p>As another example, when converting from a String field to an {@code Account} field,
 * an implementation might return {@code true} if the target Account class defines a
 * {@code public static findAccount(String)} method.
 *
 * @author Phillip Webb
 * @author Keith Donald
 * @since 3.2
 * @see Converter
 * @see GenericConverter
 * @see ConverterFactory
 * @see ConditionalGenericConverter
 */
public interface ConditionalConverter {

	/**
	 * Should the conversion from {@code sourceType} to {@code targetType} currently under
	 * consideration be selected?
	 * @param sourceType the type descriptor of the field we are converting from
	 * @param targetType the type descriptor of the field we are converting to
	 * @return true if conversion should be performed, false otherwise
	 */
	boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType);

}
```

### 6. Main Implementations

1. **AnnotationParserConverter**

   - Parses an annotation and converts it to the matching object.

2. **AnnotationPrinterConverter**

   - Converts an object to a string that represents its annotation.

3. **ArrayToArrayConverter**

   - Converts an array to an array of another type.

4. **ArrayToCollectionConverter**

   - Converts an array to a collection.

5. **ArrayToDelimitedStringConverter**

   - Converts an array to a string separated by a given delimiter.

6. **ArrayToObjectConverter**

   - Converts an array to a single object.

7. **ArrayToStringConverter**

   - Converts an array to a string.

8. **ByteBufferConverter**

   - Converts a byte buffer to other types.

9. **CharSequenceToObjectConverter**

   - Converts a character sequence to an object.

10. **CollectionToArrayConverter**

    - Converts a collection to an array.

11. **CollectionToCollectionConverter**

    - Converts a collection of one type to a collection of another type.

12. **CollectionToDelimitedStringConverter**

    - Converts a collection to a string separated by a given delimiter.

13. **CollectionToObjectConverter**

    - Converts a collection to a single object.

14. **CollectionToStringConverter**

    - Converts a collection to a string.

15. **DelimitedStringToArrayConverter**

    - Converts a delimited string to an array.

16. **DelimitedStringToCollectionConverter**

    - Converts a delimited string to a collection.

17. **FallbackObjectToStringConverter**

    - Converts an object to a string as a fallback.

18. **IdToEntityConverter**

    - Converts an identifier to an entity object.

19. **MapToMapConverter**

    - Converts a `Map` of one type to a `Map` of another type.

20. **ObjectToArrayConverter**

    - Converts an object to an array.

21. **ObjectToCollectionConverter**

    - Converts an object to a collection.

22. **ObjectToObjectConverter**

    - Converts an object of one type to an object of another type.

23. **ObjectToOptionalConverter**

    - Converts an object to an `Optional`.

24. **StreamConverter**

    - Handles conversions related to `Stream`.

25. **StringToArrayConverter**

    - Converts a string to an array.

26. **StringToCollectionConverter**

    - Converts a string to a collection.

27. **TypeConverterConverter**

    - Handles conversion between type converters.

### 7. Best Practices

This example uses the `StringToIntegerConditionalConverter` converter. First, it creates the converter and defines type descriptors for the source type (`String`) and target type (`Integer`). Then it calls `matches` to check whether the String-to-Integer condition is met. If it matches, it calls `convert` and prints the result. If not, it prints a message saying the conversion does not apply.

```java
public class ConditionalConverterDemo {

    public static void main(String[] args) {
        // Create the custom converter instance
        StringToIntegerConditionalConverter converter = new StringToIntegerConditionalConverter();

        // Define descriptors for the source type (String) and target type (Integer)
        TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
        TypeDescriptor targetType = TypeDescriptor.valueOf(Integer.class);

        // Test whether the converter applies to String to Integer conversion
        if (converter.matches(sourceType, targetType)) {
            // If the condition matches, run the conversion
            Integer result = converter.convert("8");
            System.out.println("Converted result: " + result);
        } else {
            // If the condition does not match, print a not-applicable message
            System.out.println("Conversion not applicable.");
        }
    }
}
```

`StringToIntegerConditionalConverter` is a conditional converter. It uses the source and target type information to decide whether to convert. It only converts when the source type is `String` and the target type is `Integer`. It also holds the actual String-to-Integer logic.

```java
public class StringToIntegerConditionalConverter implements Converter<String, Integer>, ConditionalConverter {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        // Condition: return true when the source type is String and the target type is Integer
        return String.class.equals(sourceType.getType()) && Integer.class.equals(targetType.getType());
    }

    @Override
    public Integer convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(source);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unable to convert String to Integer: " + source, e);
        }
    }
}
```

The output shows that the string `"8"` was converted to the integer `8`. This proves the custom converter works as expected. It converts a string to an integer when the condition is met.

```java
Converted result: 8
```

### 8. Relationship with Other Components

1. **Converter**

   - `Converter<S, T>` is a simple interface that converts type `S` to type `T`. You can combine `ConditionalConverter` with `Converter` to convert only under certain conditions.

2. **GenericConverter**

   - `GenericConverter` is a more complex interface. It allows more flexible type conversion, including collections and generic types. You can combine `ConditionalConverter` with `GenericConverter` to add conditions to complex conversions.

3. **ConverterFactory**

   - `ConverterFactory` creates converters between specific types. Combined with `ConditionalConverter`, it can check conditions when it creates converters.

4. **ConditionalGenericConverter**

   - `ConditionalGenericConverter` combines `GenericConverter` and `ConditionalConverter`. It is for complex conversions that also need a condition check.

5. **ConversionService**

   - `ConversionService` is Spring's central interface for type conversion. Converters that implement `ConditionalConverter` can be registered in it. The conversion then runs automatically when its condition is met.

### 9. FAQ

1. **Unclear or overly complex conditions**

   - When you write the `matches` method, define the condition clearly and simply. Complex conditions are hard to understand and maintain. Keep the condition logic both clear and accurate.

2. **Performance problems**

   - Slow condition checks can hurt application performance. This matters most when you convert a lot of data. Optimize the check and avoid needless computation.

3. **Wrong type matching**

   - Make sure the type matching in `matches` is correct. Wrong matching can call the converter in the wrong cases and cause bad data or exceptions.
