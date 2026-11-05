## ConverterFactory

- [ConverterFactory](#converterfactory)
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

### 3. Description

`ConverterFactory` is a Spring interface for creating type converters. In Spring's type conversion system, it creates `Converter` objects for a specific target type. It is mainly used for a group of conversions that share the same traits.

### 4. Main Features

1. **Create converters dynamically**

   + `ConverterFactory` creates the right `Converter` for each target type. You write one `ConverterFactory` for a group of related conversions, instead of one `Converter` for each.

2. **Wide range of type conversions**

   + It works well when the conversion rules are similar but the target type can vary. For example, converting a string to different enum types or number types.

3. **Better reuse and maintenance**

   + It keeps similar conversion logic in one place. This reduces duplicate code and makes maintenance easier.

4. **Flexible and extensible**

   + It is a flexible way to extend Spring's type conversion. You can write your own `ConverterFactory` to fit your application.

5. **Works with Spring ConversionService**

   + `ConverterFactory` plugs easily into `ConversionService`. This gives the whole application one consistent type conversion service.

6. **Simpler configuration**

   + You register one factory instead of many separate converters. This keeps the configuration for related types short.

### 5. Interface Source

`ConverterFactory` is a Spring interface. It creates converters that convert an object from a source type (S) to a subtype of the target type (R). It suits cases with many related but different target subtypes. Its `getConverter` method returns the right converter for a given target type. This keeps conversion code and configuration simple.

```java
/**
 * A factory for "ranged" converters that can convert objects from S to subtypes of R.
 *
 * <p>Implementations may additionally implement {@link ConditionalConverter}.
 *
 * @author Keith Donald
 * @since 3.0
 * @param <S> the source type converters created by this factory can convert from
 * @param <R> the target range (or base) type converters created by this factory can convert to;
 *            for example {@link Number} for a set of number subtypes.
 * @see ConditionalConverter
 */
public interface ConverterFactory<S, R> {

	/**
	 * Get the converter to convert from S to target type T, where T is also an instance of R.
	 * @param <T> the target type
	 * @param targetType the target type to convert to
	 * @return a converter from S to T
	 */
	<T extends R> Converter<S, T> getConverter(Class<T> targetType);

}
```

### 6. Main Implementations

1. **CharacterToNumberFactory**

   + Creates converters from `Character` to `Number`. For example, it converts the character '1' to the number 1.

2. **IntegerToEnumConverterFactory**

   + Creates converters from `Integer` to `Enum`. This helps with enums based on an integer index. For example, 0, 1, and 2 become the first, second, and third enum values.

3. **LenientObjectToEnumConverterFactory**

   + Provides a lenient conversion from any object to an enum. It does more than direct type mapping. It usually has extra logic so that many source types can be converted to enum values safely.

4. **NumberToNumberConverterFactory**

   + Creates converters from one number type to another. For example, `Double` to `Integer`, or `Long` to `Float`.

5. **StringToEnumConverterFactory**

   + Creates converters from `String` to `Enum`. It is useful for enum values that come from text, such as config files or user input. It maps a string to the matching enum constant.

6. **StringToNumberConverterFactory**

   + Creates converters from `String` to number types. This is often needed when parsing numeric config values or user input. It supports many number types, such as `Integer`, `Double`, and `BigDecimal`.

### 7. Best Practices

We use a `DefaultConversionService` instance. It is Spring's default type conversion service. Then we add a converter factory, `StringToNumberConverterFactory`, which creates String-to-Number converters. Finally, we call `conversionService.convert("8", Integer.class)` to convert the string "8" to an `Integer`.

```java
public class CharacterToNumberFactoryDemo {

    public static void main(String[] args) {
        // Create a default conversion service
        // GenericConversionService is a general-purpose type conversion service
        DefaultConversionService conversionService = new DefaultConversionService();

        // Add a String-to-Number converter factory to the conversion service
        // StringToNumberConverterFactory is a factory that creates String-to-Number converters
        conversionService.addConverterFactory(new StringToNumberConverterFactory());

        // Use the conversion service to convert the string "8" to Integer
        // This shows how to convert a string to the matching integer
        Integer num = conversionService.convert("8", Integer.class);

        // Print the conversion result
        System.out.println("String to Integer: " + num);
    }
}
```

 `StringToNumberConverterFactory` implements the `ConverterFactory` interface. It converts strings to number types. It has a static inner class `StringToNumber` that implements `Converter` and does the actual conversion.

```java
public class StringToNumberConverterFactory implements ConverterFactory<String, Number> {

    @Override
    public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToNumberConverterFactory.StringToNumber<>(targetType);
    }

    private static final class StringToNumber<T extends Number> implements Converter<String, T> {

        private final Class<T> targetType;

        public StringToNumber(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override
        @Nullable
        public T convert(String source) {
            return NumberUtils.parseNumber(source, this.targetType);
        }
    }
}
```

The output shows that `StringToNumberConverterFactory` converted the string "8" to the `Integer` 8.

```java
String to Integer: 8
```

### 8. Relationship with Other Components

1. **Converter interface**

   - `ConverterFactory` creates `Converter` instances. The `Converter<S, T>` interface is simpler. It converts type `S` directly to type `T`. `ConverterFactory` is for harder cases where the converter depends on the target type.

2. **ConditionalConverter**

   - A `ConverterFactory` implementation can also implement `ConditionalConverter`. This lets the converter check a condition before converting. For example, it can check whether the source and target types meet some requirement.

3. **ConversionService**

   - `ConversionService` is the core interface of Spring's type conversion service. It is one entry point for type conversion. You can register `Converter` and `ConverterFactory` implementations in it. When it converts a type, it looks up the registered converters and converter factories.

4. **GenericConversionService**

   - `GenericConversionService` is a concrete implementation of `ConversionService`. It manages a set of `Converter` and `ConverterFactory` instances and converts types as needed.

5. **DefaultConversionService**

   - `DefaultConversionService` extends `GenericConversionService`. It registers many common `Converter` and `ConverterFactory` instances in advance. It is ready to use for common conversions.

### 9. FAQ

1. **Converter is not registered correctly**

   + Make sure the `ConverterFactory` is registered in the Spring configuration. Also make sure `ConversionService` is available in the application context.

2. **Wrong or incomplete conversion logic**

   + Test the `Converter` implementation well. Make sure it handles many kinds of input, including bad values and edge cases.

3. **Type conversion exceptions**

   + Add proper error handling and type checks. Make sure the source and target types are compatible before converting.

4. **Slow conversion**

   + Optimize the converter. Avoid extra computation and resource use. You can cache results that repeat.

5. **Generic type erasure**

   + Register and look up converters with concrete target types, not generic types.

6. **Maintaining custom conversion rules**

   + Keep converter logic clear and modular. This makes it easy to maintain and extend.
