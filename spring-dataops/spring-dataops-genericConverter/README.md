## GenericConverter

- [GenericConverter](#genericconverter)
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

`GenericConverter` is a key Spring interface for type conversion. It is closely tied to Spring's type conversion system and converts an object of one type to another. Compared with `Converter`, `GenericConverter` is more flexible. It allows conversion between multiple pairs of source and target types.

### 4. Main Features

1. **Multi-type conversion**

   + `GenericConverter` handles more complex cases. It can convert from one type to several target types, and from several source types to one target type. This is more flexible than the simple one-to-one conversion of `Converter`.

2. **Generic and complex types**

   + With the `TypeDescriptor` class, `GenericConverter` can handle complex conversions, including generics. This matters most for collection types and generic classes.

3. **Custom conversion logic**

   + You can implement `GenericConverter` to provide custom conversion logic for your business needs.

4. **Works with Spring's type conversion system**

   + `GenericConverter` fits into Spring's type conversion system and works with other Spring parts, such as data binding.

5. **Flexible type declaration**

   + The `getConvertibleTypes()` method declares the pairs of source and target types it can handle. This helps Spring pick the right converter.

### 5. Interface Source

The `GenericConverter` interface converts between many pairs of source and target types. During conversion, it uses `TypeDescriptor` to read context information about the fields, such as annotations and generics. This allows more complex and specific conversion logic. The interface also has an inner class, `ConvertiblePair`, which represents a convertible source-to-target type pair.

```java
/**
 * Generic converter interface for converting between two or more types.
 *
 * <p>This is the most flexible of the Converter SPI interfaces, but also the most complex.
 * It is flexible in that a GenericConverter may support converting between multiple source/target
 * type pairs (see {@link #getConvertibleTypes()}). In addition, GenericConverter implementations
 * have access to source/target {@link TypeDescriptor field context} during the type conversion
 * process. This allows for resolving source and target field metadata such as annotations and
 * generics information, which can be used to influence the conversion logic.
 *
 * <p>This interface should generally not be used when the simpler {@link Converter} or
 * {@link ConverterFactory} interface is sufficient.
 *
 * <p>Implementations may additionally implement {@link ConditionalConverter}.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 * @see TypeDescriptor
 * @see Converter
 * @see ConverterFactory
 * @see ConditionalConverter
 */
public interface GenericConverter {

	/**
	 * Return the source and target types that this converter can convert between.
	 * <p>Each entry is a convertible source-to-target type pair.
	 * <p>For {@link ConditionalConverter conditional converters} this method may return
	 * {@code null} to indicate all source-to-target pairs should be considered.
	 */
	@Nullable
	Set<ConvertiblePair> getConvertibleTypes();

	/**
	 * Convert the source object to the targetType described by the {@code TypeDescriptor}.
	 * @param source the source object to convert (may be {@code null})
	 * @param sourceType the type descriptor of the field we are converting from
	 * @param targetType the type descriptor of the field we are converting to
	 * @return the converted object
	 */
	@Nullable
	Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType);


	/**
	 * Holder for a source-to-target class pair.
	 */
	final class ConvertiblePair {

		private final Class<?> sourceType;

		private final Class<?> targetType;

		/**
		 * Create a new source-to-target pair.
		 * @param sourceType the source type
		 * @param targetType the target type
		 */
		public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
			Assert.notNull(sourceType, "Source type must not be null");
			Assert.notNull(targetType, "Target type must not be null");
			this.sourceType = sourceType;
			this.targetType = targetType;
		}

		public Class<?> getSourceType() {
			return this.sourceType;
		}

		public Class<?> getTargetType() {
			return this.targetType;
		}

		// equals, hashCode and toString are omitted
	}
}
```

### 6. Main Implementations

1. **ArrayToArrayConverter**

   - Converts an array to an array of another type. For example, `Integer[]` to `String[]`.

2. **ArrayToCollectionConverter**

   - Converts an array to a collection. For example, `String[]` to `List<String>`.

3. **ArrayToObjectConverter**

   - Converts an array to a single object. It is often used to take the element out of a one-element array.

4. **ArrayToStringConverter**

   - Converts an array to a string. It is often used for printing or logging.

5. **ByteBufferConverter**

   - Converts a `ByteBuffer` to other types, such as a string or a byte array.

6. **CollectionToArrayConverter**

   - Converts a collection to an array. For example, `List<Integer>` to `Integer[]`.

7. **CollectionToCollectionConverter**

   - Converts a collection to a collection of another type. For example, `List<Integer>` to `Set<Integer>`.

8. **CollectionToObjectConverter**

   - Converts a collection to a single object. It is often used to take the element out of a one-element collection.

9. **CollectionToStringConverter**

   - Converts a collection to a string. It is often used for printing or logging.

10. **EnumToIntegerConverter**

    - Converts an enum value to an integer, usually its ordinal.

11. **EnumToStringConverter**

    - Converts an enum value to a string.

12. **FallbackObjectToStringConverter**

    - A fallback converter that turns an object into a string when no more specific converter is available.

13. **IdToEntityConverter**

    - Converts an ID to an entity object. It is often used for database entities.

14. **MapToMapConverter**

    - Converts a `Map` of one type to a `Map` of another type.

15. **NumberToCharacterConverter**

    - Converts a number to a character.

16. **ObjectToArrayConverter**

    - Converts an object to an array. For example, it wraps a single object in a one-element array.

17. **ObjectToCollectionConverter**

    - Converts an object to a collection. For example, it wraps a single object in a one-element collection.

18. **ObjectToObjectConverter**

    - Converts an object to an object of another type. It is often used between complex objects.

19. **ObjectToOptionalConverter**

    - Converts an object to an `Optional`.

20. **ObjectToStringConverter**

    - Converts an object to a string.

21. **PropertiesToStringConverter**

    - Converts a `Properties` object to a string.

22. **StreamConverter**

    - Handles conversions for Java 8 `Stream` types.

### 7. Best Practices

We use Spring's `DefaultConversionService` for custom type conversion. We add our own `AnnotatedStringToDateConverter` to the conversion service. It converts strings to `Date` objects. The example shows two conversions: a plain date string and a date string with time. Each is converted to a `Date`. The dates are set on a `MyBean` instance, and the instance is printed to show the result.

```java
public class GenericConverterDemo {

    public static void main(String[] args) {
        // Create a default conversion service
        DefaultConversionService service = new DefaultConversionService();
        // Add custom converters to the conversion service
        service.addConverter(new AnnotatedStringToDateConverter());

        // Define source and target types to convert a String to Date
        TypeDescriptor sourceType1 = TypeDescriptor.valueOf(String.class);
        TypeDescriptor targetType1 = new TypeDescriptor(ReflectionUtils.findField(MyBean.class, "date"));
        // Run the conversion
        Date date = (Date) service.convert("2023-01-01", sourceType1, targetType1);

        // Define another pair of source and target types to convert another String format to Date
        TypeDescriptor sourceType2 = TypeDescriptor.valueOf(String.class);
        TypeDescriptor targetType2 = new TypeDescriptor(ReflectionUtils.findField(MyBean.class, "dateTime"));
        // Run the conversion
        Date dateTime = (Date) service.convert("2023-01-01 23:59:59", sourceType2, targetType2);

        // Set the MyBean property using the converted Date
        MyBean myBean = new MyBean();
        myBean.setDate(date);
        myBean.setDateTime(dateTime);

        // Print the conversion result
        System.out.println("myBean = " + myBean);
    }
}
```

`AnnotatedStringToDateConverter` is a custom type converter. It converts strings to `Date` objects. The conversion is not fixed. It depends on the `DateFormat` annotation on the target `Date` field.

```java
public class AnnotatedStringToDateConverter implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        // Define the convertible type pair: String to Date
        return Collections.singleton(new ConvertiblePair(String.class, Date.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        // If the source is null, return null
        if (source == null) {
            return null;
        }

        // Convert the source object to a string
        String dateString = (String) source;
        // Get the DateFormat annotation on the target type (the Date field)
        DateFormat dateFormatAnnotation = targetType.getAnnotation(DateFormat.class);
        // If the target field has no DateFormat annotation, throw an exception
        if (dateFormatAnnotation == null) {
            throw new IllegalArgumentException("Missing DateFormat annotation on the target field");
        }

        try {
            // Create a SimpleDateFormat from the date pattern in the annotation
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatAnnotation.value());
            // Use SimpleDateFormat to parse the string into a Date
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            // If parsing fails, throw an exception
            throw new IllegalArgumentException("Cannot parse date", e);
        }
    }
}
```

The Java annotation `DateFormat` sets the date format.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DateFormat {
    String value();
}
```

In the `MyBean` class, the `date` field uses the `"yyyy-MM-dd"` format. The `dateTime` field uses the `"yyyy-MM-dd hh:mm:ss"` format. The `AnnotatedStringToDateConverter` reads these annotations to know how to turn a string into a `Date`.

```java
public class MyBean {

    @DateFormat("yyyy-MM-dd")
    private Date date;

    @DateFormat("yyyy-MM-dd hh:mm:ss")
    private Date dateTime;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "date=" + date +
                ", dateTime=" + dateTime +
                '}';
    }
}
```

The output shows that the `MyBean` instance holds `Date` objects converted from the strings. Their values match the date and time in the source strings. This is a typical example of custom type conversion. An annotation and a converter work together to give flexible conversion logic.

```java
myBean = MyBean{date=Sun Jan 01 00:00:00 CST 2023, dateTime=Sun Jan 01 23:59:59 CST 2023}
```

### 8. Relationship with Other Components

1. **Converter<S, T>**

   + For simple one-to-one type conversion. The `Converter` interface has one method that converts type `S` to type `T`. It suits direct conversions.

2. **ConverterFactory<S, R>**

   + Provides converters from one source type `S` to many target types that are subtypes of `R`. A `ConverterFactory` creates a `Converter` for each target type. It suits cases where one source type converts to many related target types.

3. **ConversionService**

   + Defines a type conversion service that works at runtime. `ConversionService` can register and manage many `Converter` and `GenericConverter` instances. It gives the application one central place for type conversion.

4. **ConditionalConverter**

   + A special converter that depends on a condition. This interface lets the conversion logic decide whether to run, based on things like annotations or field properties.

### 9. FAQ

1. **Type matching problems**

   + The source or target type may not match during conversion. To fix this, make sure `getConvertibleTypes()` returns all supported source and target type pairs.

2. **Wrong conversion logic**

   + Wrong logic can make the conversion fail or give incorrect results. Check the `convert` method carefully. Make sure it handles all edge cases and exceptions.

3. **Performance problems**

   + Complex conversion logic can slow the application. Avoid expensive work in `convert`. Consider caching results of common conversions.

4. **Not enough context information**

   + Sometimes the conversion needs more context than `TypeDescriptor` gives. You can use its advanced features, such as reading field annotations or generic information. Or you can switch to a more suitable converter interface.

5. **Poor exception handling**

   + Many exceptions can happen during conversion. If they are not handled well, the program may crash or become unstable. Handle all possible exceptions in `convert`, and throw suitable custom exceptions when needed.
