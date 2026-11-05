## Converter

- [Converter](#converter)
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

1. **Generics**

   + The `Converter` interface uses Java generics, so you need to know them. Knowing how to write type-safe code with generics is important when you write your own converters.

2. **Spring MVC**

   + If you plan to use `Converter` in a web application, it helps to know how Spring MVC works. This includes controllers, request mappings, and view resolvers.
  
3. **Type conversion and formatting**

   + Know the basics of type conversion in Java and how to format data. This is key to writing good converter logic.

### 3. Description

The `Converter` interface is a key part of type conversion in Spring. It defines one simple method that converts a source type (S) to a target type (T). By implementing it, you can write your own conversion logic and handle complex data conversion in a Spring application.

### 4. Main Features

1. **Type conversion**

   + It gives you a way to convert an object from one type to another. This helps with incompatible data types. For example, when data from an external system must be turned into the format your app uses.

2. **Data formatting**

   + In web applications, a `Converter` can turn data from the user interface (usually strings) into a format the back end understands, or the other way round. This matters most for dates, times, and numbers.

3. **Data binding**

   + In Spring MVC, `Converter` converts request parameters (for example, strings from an HTTP request) to the right types for controller method parameters.

4. **Better maintenance and reuse**

   + Clear conversion rules keep code modular. The same conversion logic can be reused in different parts of the application.

5. **Custom conversion logic**

   + Compared with the built-in converters, a custom `Converter` lets you write conversion logic for your own application. This handles more complex or special needs.

6. **Integration and extension**

   + `Converter` fits easily into other parts of Spring, such as the data access layer and Spring Boot auto-configuration. You can also extend it for more complex needs.

### 5. Interface Source

The `Converter<S, T>` interface is Spring's standard way to convert from one type to another. It has a single `convert` method that does the conversion. The interface is thread-safe, so you can share and reuse it. It also has an `andThen` method. This lets you chain several converters for more complex conversions.

```java
/**
 * A converter converts a source object of type {@code S} to a target of type {@code T}.
 *
 * <p>Implementations of this interface are thread-safe and can be shared.
 *
 * <p>Implementations may additionally implement {@link ConditionalConverter}.
 *
 * @param <S> the source type
 * @param <T> the target type
 */
@FunctionalInterface
public interface Converter<S, T> {

	/**
	 * Convert the source object of type {@code S} to target type {@code T}.
	 * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
	 * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
	 * @throws IllegalArgumentException if the source cannot be converted to the desired target type
	 */
	@Nullable
	T convert(S source);

	/**
	 * Construct a composed {@link Converter} that first applies this {@link Converter}
	 * to its input, and then applies the {@code after} {@link Converter} to the result.
	 * @param after the {@link Converter} to apply after this {@link Converter} is applied
	 * @param <U> the type of output of both the {@code after} {@link Converter} and the composed {@link Converter}
	 * @return a composed {@link Converter} that first applies this {@link Converter} and then
	 * applies the {@code after} {@link Converter}
	 * @since 5.3
	 */
	default <U> Converter<S, U> andThen(Converter<? super T, ? extends U> after) {
		Assert.notNull(after, "After Converter must not be null");
		return (S s) -> {
			T initialResult = convert(s);
			return (initialResult != null ? after.convert(initialResult) : null);
		};
	}

}
```

### 6. Main Implementations

1. **StringToArrayConverter**

   - Converts a string to an array. For example, a comma-separated value becomes a string array.

2. **StringToBooleanConverter**

   - Converts a string to a boolean.

3. **StringToCharacterConverter**

   - Converts a string to a character.

4. **StringToCharsetConverter**

   - Converts a string to a `Charset` object.

5. **StringToCollectionConverter**

   - Converts a string to a collection. For example, a comma-separated string becomes a list.

6. **StringToCurrencyConverter**

   - Converts a string to a `Currency` object.

7. **StringToLocaleConverter**

   - Converts a string to a `Locale` object.

### 7. Best Practices

We use `DefaultConversionService` to manage and run type conversions. First, we create a conversion service and register our custom `StringToLocalDateConverter` and `StringToBooleanConverter`. Then the code calls `canConvert` to check whether a conversion is possible, and calls `convert` to do it.

```java
public class ConverterDemo {

    public static void main(String[] args) {
        // Create a default conversion service
        DefaultConversionService service = new DefaultConversionService();

        // Add custom converters to the service
        service.addConverter(new StringToLocalDateConverter());
        service.addConverter(new StringToBooleanConverter());

        // Check whether a String can be converted to LocalDate
        if (service.canConvert(String.class, LocalDate.class)) {
            LocalDate localDate = service.convert("2023-12-07", LocalDate.class);
            System.out.println("LocalDate = " + localDate);
        }

        // Check whether the String "yes" can be converted to Boolean
        if (service.canConvert(String.class, Boolean.class)) {
            Boolean boolValue = service.convert("yes", Boolean.class);
            System.out.println("yes = " + boolValue);
        }

        // Check whether the String "no" can be converted to Boolean
        if (service.canConvert(String.class, Boolean.class)) {
            Boolean boolValue = service.convert("no", Boolean.class);
            System.out.println("no = " + boolValue);
        }
    }
}
```

The `StringToLocalDateConverter` class converts a string in the format "`yyyy-MM-dd`" to a `LocalDate`. It implements Spring's `Converter` interface and parses the string with `DateTimeFormatter`.

```java
public class StringToLocalDateConverter implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(String source) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(source, df);
    }
}
```

The `StringToBooleanConverter` class converts a string to a boolean. It uses predefined sets of strings to find true values (such as "true", "yes", "1") and false values (such as "false", "no", "0"). It returns `Boolean.TRUE` or `Boolean.FALSE` to match.

```java
public class StringToBooleanConverter implements Converter<String, Boolean> {

    private static final Set<String> trueValues = new HashSet<>(8);

    private static final Set<String> falseValues = new HashSet<>(8);

    static {
        trueValues.add("true");
        trueValues.add("on");
        trueValues.add("yes");
        trueValues.add("1");

        falseValues.add("false");
        falseValues.add("off");
        falseValues.add("no");
        falseValues.add("0");
    }

    @Override
    @Nullable
    public Boolean convert(String source) {
        if (trueValues.contains(source)) {
            return Boolean.TRUE;
        }
        if (falseValues.contains(source)) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
    }
}
```

The output shows that the string "2023-12-07" became a `LocalDate`. The strings "yes" and "no" became the booleans `true` and `false`. This shows the custom converters work for dates and booleans.

```java
LocalDate = 2023-12-07
yes = true
no = false
```

### 8. Relationship with Other Components

1. **ConversionService**

   - `ConversionService` is the central interface for type conversion. `Converter` implementations are usually registered in a `ConversionService` so the whole application can use them.

2. **Data Binding**

   - In Spring MVC, data binding uses `Converter` to turn HTTP request parameters into the right types for controller method parameters. This matters most for form submission and query parameters.

3. **Spring Boot and Spring Configuration**

   - In Spring Boot and Spring configuration, `Converter` turns configuration properties (such as values in a properties file) into complex objects or custom types.

4. **Spring Data**

   - In Spring Data, `Converter` can convert between database entities and the domain models used in the application.

5. **Field Formatting**

   - `Converter` is often used together with the `Formatter` interface. `Formatter` focuses on formatting and parsing fields, especially in web environments. A `Formatter` can be seen as a special kind of `Converter`.

6. **Bean Validation**

   - In bean validation, `Converter` can convert data before or after the validation logic.

7. **RestTemplate and WebClient**

   - With Spring's RestTemplate or WebClient, `Converter` can help serialize and deserialize HTTP request and response bodies.

### 9. FAQ

1. **Wrong conversion logic**

   - A wrong implementation can give incorrect data or throw exceptions during conversion. Make sure the logic is correct and handles many kinds of input.

2. **Converter not registered**

   - You may forget to register the custom converter in Spring's `ConversionService`. Then the expected conversion does not happen.

3. **Handling null values**

   - Decide how to handle `null` input. Sometimes `null` should become a default value. In other cases it should stay `null` or throw an exception.

4. **Type checks and error handling**

   - Handle input types and conversion errors properly. For example, the conversion method should check that the input has the expected type, and throw a suitable exception if not.

5. **Conflicts with other converters**

   - In large projects, several converters may handle the same source and target types. This makes the conversion logic unclear. Manage them carefully to avoid conflicts.
