## ConversionService

- [ConversionService](#conversionservice)
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

4. **ConditionalConverter**

   + [ConditionalConverter](/spring-dataops/spring-dataops-conditionalConverter/README.md) is an interface in Spring's type conversion system. It lets a converter check a condition before it converts. The converter can then decide, based on specific conditions, whether it should do the conversion.

### 3. Description

The `ConversionService` interface is a key part of Spring. It converts data between different types. It gives one central way to convert an object of one type to another, which greatly simplifies data handling. It also supports custom conversion rules and works closely with Spring's data binding and validation. It is especially useful for web request parameters, configuration data, and database records.

### 4. Main Features

1. **Type conversion**

   + The basic feature is converting one data type to another. This covers simple types (such as String to Integer) and more complex objects.

2. **Custom converters**

   + You can register your own converters (`Converter`, `ConverterFactory`, or `GenericConverter`) to handle the conversion logic for specific types.

3. **General purpose**

   + The interface is not tied to any specific data type. So it fits many cases, such as UI data binding and data persistence.

4. **Works with Spring MVC**

   + In Spring MVC, `ConversionService` handles data binding and type conversion, especially for form submissions and path parameters.
   
5. **Conversion capability check**
   + You can ask a `ConversionService` whether a conversion between two specific types is possible.

### 5. Interface Source

The `ConversionService` interface defines flexible methods for conversion between types. It has two core features. One checks whether conversion between a source type and a target type is possible. The other does the conversion. The interface pays special attention to collection, array, and map conversions. It also states the exceptions you may meet in those cases.

```java
/**
 * A service interface for type conversion. This is the entry point into the convert system.
 * Call {@link #convert(Object, Class)} to perform a thread-safe type conversion using this system.
 *
 * @author Keith Donald
 * @author Phillip Webb
 * @since 3.0
 */
public interface ConversionService {

	/**
	 * Return {@code true} if objects of {@code sourceType} can be converted to the {@code targetType}.
	 * <p>If this method returns {@code true}, it means {@link #convert(Object, Class)} is capable
	 * of converting an instance of {@code sourceType} to {@code targetType}.
	 * <p>Special note on collections, arrays, and maps types:
	 * For conversion between collection, array, and map types, this method will return {@code true}
	 * even though a convert invocation may still generate a {@link ConversionException} if the
	 * underlying elements are not convertible. Callers are expected to handle this exceptional case
	 * when working with collections and maps.
	 * @param sourceType the source type to convert from (may be {@code null} if source is {@code null})
	 * @param targetType the target type to convert to (required)
	 * @return {@code true} if a conversion can be performed, {@code false} if not
	 * @throws IllegalArgumentException if {@code targetType} is {@code null}
	 */
	boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType);

	/**
	 * Return {@code true} if objects of {@code sourceType} can be converted to the {@code targetType}.
	 * The TypeDescriptors provide additional context about the source and target locations
	 * where conversion would occur, often object fields or property locations.
	 * <p>If this method returns {@code true}, it means {@link #convert(Object, TypeDescriptor, TypeDescriptor)}
	 * is capable of converting an instance of {@code sourceType} to {@code targetType}.
	 * <p>Special note on collections, arrays, and maps types: same as above.
	 * @param sourceType context about the source type to convert from
	 * (may be {@code null} if source is {@code null})
	 * @param targetType context about the target type to convert to (required)
	 * @return {@code true} if a conversion can be performed between the source and target types,
	 * {@code false} if not
	 * @throws IllegalArgumentException if {@code targetType} is {@code null}
	 */
	boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType);

	/**
	 * Convert the given {@code source} to the specified {@code targetType}.
	 * @param source the source object to convert (may be {@code null})
	 * @param targetType the target type to convert to (required)
	 * @return the converted object, an instance of targetType
	 * @throws ConversionException if a conversion exception occurred
	 * @throws IllegalArgumentException if targetType is {@code null}
	 */
	@Nullable
	<T> T convert(@Nullable Object source, Class<T> targetType);

	/**
	 * Convert the given {@code source} to the specified {@code targetType}.
	 * The TypeDescriptors provide additional context about the source and target locations
	 * where conversion will occur.
	 * @param source the source object to convert (may be {@code null})
	 * @param sourceType context about the source type to convert from
	 * (may be {@code null} if source is {@code null})
	 * @param targetType context about the target type to convert to (required)
	 * @return the converted object, an instance of {@link TypeDescriptor#getObjectType() targetType}
	 * @throws ConversionException if a conversion exception occurred
	 * @throws IllegalArgumentException if targetType is {@code null},
	 * or {@code sourceType} is {@code null} but source is not {@code null}
	 */
	@Nullable
	Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType);

}
```

### 6. Main Implementations

1. **GenericConversionService**

   + The base implementation of `ConversionService`. `GenericConversionService` is a general conversion framework. It supports many converter types, including `Converter`, `ConverterFactory`, and `GenericConverter`. It is the base for more complex conversion services and fits many conversion cases.

2. **DefaultConversionService**

   + Extends `GenericConversionService`. `DefaultConversionService` adds default converters for many common data types. It works out of the box for most standard applications. It is a good choice for regular type conversion.

3. **FormattingConversionService**

   + `FormattingConversionService` supports type conversion. It also formats objects to strings and parses strings to objects. It suits UI-related data conversion. It is often used in web applications, especially in Spring MVC for form data and view data.

4. **DefaultFormattingConversionService**

   + A concrete implementation of `FormattingConversionService`. It includes everything in `DefaultConversionService` and adds common formatters and parsers. It is a complete solution for formatting and parsing data in web applications.

5. **ApplicationConversionService**

   + A full `ConversionService` for Spring applications. `ApplicationConversionService` extends `DefaultFormattingConversionService` and adds more Spring-specific converters. It is the default choice in Spring Boot auto-configuration and suits most Spring applications.

6. **TypeConverterConversionService**

   + An adapter class that bridges the `TypeConverter` and `ConversionService` interfaces. It lets old `PropertyEditor`-based converters be used inside the new `ConversionService` structure. It helps when you move old code to the new conversion service framework.

7. **WebConversionService**

   + A `ConversionService` made for web environments. `WebConversionService` extends `FormattingConversionService` and adds web-specific converters and formatters. It suits data conversion in HTTP requests and responses and is a good fit for web applications.

### 7. Best Practices

We use `DefaultConversionService` to convert types. First, it calls `canConvert` to check whether `Integer` can be converted to `String`. If yes, it converts and prints the result. Then the example uses `TypeDescriptor` for a finer check of conversion ability. `TypeDescriptor` handles harder cases, for example when the source and target types need more specific context.

```java
public class ConversionServiceDemo {

    public static void main(String[] args) {
        // Create a DefaultConversionService instance
        ConversionService conversionService = new DefaultConversionService();

        // Use canConvert to check whether the conversion is possible
        if (conversionService.canConvert(Integer.class, String.class)) {
            System.out.println("Can convert from Integer to String");

            // Run the conversion from Integer to String
            String converted = conversionService.convert(666, String.class);
            System.out.println("Converted: " + converted);
        }

        // Use TypeDescriptor to check whether the conversion is possible
        if (conversionService.canConvert(
                TypeDescriptor.valueOf(Integer.class),
                TypeDescriptor.valueOf(String.class))) {
            System.out.println("Can convert from Integer to String using TypeDescriptors");

            // Run the conversion with TypeDescriptor
            Object convertedWithTypeDescriptor = conversionService.convert(
                    888,
                    TypeDescriptor.valueOf(Integer.class),
                    TypeDescriptor.valueOf(String.class));
            System.out.println("Converted with TypeDescriptors: " + convertedWithTypeDescriptor);
        }
    }
}
```

The output shows that `ConversionService` is efficient and useful. It can confirm whether a type conversion is possible (such as `Integer` to `String`). It can also run the conversion and turn the integer into its string form.

```java
Can convert from Integer to String
Converted: 666
Can convert from Integer to String using TypeDescriptors
Converted with TypeDescriptors: 888
```

### 8. Relationship with Other Components

1. **Converter & ConverterFactory**

   + `ConversionService` relies on `Converter` and `ConverterFactory` implementations to do the actual conversion. You implement these interfaces and register your converters in `ConversionService` to add your own conversion logic. This makes type conversion flexible and extensible.

2. **GenericConverter** 

   + `GenericConverter` strengthens the conversion features of `ConversionService` with flexible multi-type conversion. When you register a `GenericConverter` in `ConversionService`, it can handle wider and more complex cases, including conversions that need extra context.

3. **PropertyEditor**

   + `PropertyEditor` is an early type conversion tool for simple property editing. It is not thread-safe and not very flexible, so `ConversionService` replaced it. `ConversionService` is a modern, thread-safe, and flexible way to convert types. It is the first choice in modern Spring applications. `PropertyEditor` is still supported, but `ConversionService` fits better in complex and concurrent applications.

4. **DataBinder**

   + During data binding, for example when binding form data to objects or MVC controller method parameters, `DataBinder` uses `ConversionService` to convert property values. This makes conversion from one type to another simple and fast.

5. **Configuration and property management**

   + `ConversionService` plays an important part when Spring handles configuration properties. For properties injected with `@Value`, it converts string values from property files into more complex object types. This simplifies configuration management.
   
6. **BeanFactory & ApplicationContext**

   + `ConversionService` is a bean in the Spring container. You can register it in a `BeanFactory` or `ApplicationContext`. After that, other components in the container can use it for all kinds of conversions. This improves type conversion across the whole Spring application.

### 9. FAQ

1. **Registering custom converters**

   + Registering custom converters correctly is a common challenge. Make sure your `Converter`, `ConverterFactory`, or `GenericConverter` implementations are added to the `ConversionService`. Otherwise the expected conversion may not happen, often because of a configuration mistake or omission.

2. **Converter conflicts**

   + When several converters fit the same conversion, they can conflict. Understand and manage their priority, so the right converter is used in each case.

3. **Complex type conversion**

   + For complex or generic types, write logic that handles these cases. This matters most when the conversion needs extra context, such as converting between collection types and converting the element types at the same time.

4. **Integration with other Spring components**

   + Integrating `ConversionService` with Spring MVC, data binding, validation, and other parts can be hard in complex cases. You need a good understanding of Spring configuration and how components interact.

5. **Precision and data loss**

   + When you convert number types, watch for precision loss and data loss. Make sure the data stays accurate and complete.
