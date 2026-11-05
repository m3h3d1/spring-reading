## Parser

- [Parser](#parser)
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

✒ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Background Knowledge

1. **Localization and internationalization (i18n)**

   + Know how to format data for different locales. This is important for global applications.

2. **Java API for formatting**

   + Know the Java formatting APIs, such as `java.text.DateFormat`, `java.text.SimpleDateFormat`, and `java.time.format.DateTimeFormatter`. They are often used when you implement the `Printer` interface.

3. **ConversionService** 

   + `ConversionService` is the Spring service that manages converters (`Converters`) and formatters (`Formatters`, including `Printer` and `Parser`). Knowing how it works helps you integrate and use the `Printer` interface.

### 3. Description

The `Parser` interface is a key data conversion tool in Spring. It converts string data to more complex object types. It has one simple method that lets you decide how to turn text input into the type you need, such as a string to a `Number`. It supports internationalization, so it can parse data by the standards of different regions.

### 4. Main Features

1. **String to object conversion**

   + Converts string data to more complex object types. This matters most for text from user input or external data sources.

2. **Custom type conversion**

   + You can write your own parsers for specific data types, such as date and time, currency, or any custom type.

3. **Internationalization support**

   + The methods of `Parser` usually take a `Locale` parameter. This lets the parser read strings correctly for different regions. It supports different languages and format rules.

### 5. Interface Source

The `Parser<T>` interface is a key Spring interface for converting a string to a specific object type. It defines one method, `parse`. The method takes a string and a `Locale`, and returns an instance of the given type (T).

```java
/**
 * Parses text strings to produce instances of T.
 *
 * @author Keith Donald
 * @since 3.0
 * @param <T> the type of object this Parser produces
 */
@FunctionalInterface
public interface Parser<T> {

	/**
	 * Parse a text String to produce a T.
	 * @param text the text string
	 * @param locale the current user locale
	 * @return an instance of T
	 * @throws ParseException when a parse exception occurs in a java.text parsing library
	 * @throws IllegalArgumentException when a parse exception occurs
	 */
	T parse(String text, Locale locale) throws ParseException;

}
```

### 6. Main Implementations

1. **`CharArrayFormatter`**

   + Parses a string to a char array. It suits cases where text data must become a char array.

2. **`CurrencyParser`**

   + Parses currency strings. For example, it parses "$1,234.56" into the matching number.

3. **`CurrencyStyleFormatter`**

   + Formats and parses currency values. It parses strings and also formats numbers as currency strings.

4. **`CurrencyUnitFormatter`**

   + Formats and parses currency units. For example, it converts codes such as "USD" or "EUR" into currency unit objects.

5. **`DateFormatter`**

   + Parses date strings. For example, "2023-12-31" becomes a `java.util.Date`.

6. **`DateTimeParser`**

   + Parses date and time strings into a `java.time.LocalDateTime` or a similar object.

7. **`DurationFormatter`**

   + Parses strings that show a length of time, such as "PT10M" (10 minutes), into a `java.time.Duration`.

8. **`InetAddressFormatter`**

   + Parses IP address strings into a `java.net.InetAddress`.

9. **`InstantFormatter`**

   + Parses strings that show a point in time, such as "2020-01-01T00:00:00Z", into a `java.time.Instant`.

10. **`IsoOffsetFormatter`**

    + Parses date-time strings with a time zone offset into types such as `java.time.OffsetDateTime`.

11. **`LocalDateParser`**

    + Parses strings that contain only a date, such as "2020-01-01", into a `java.time.LocalDate`.

12. **`LocalDateTimeParser`**

    + Parses strings with a date and a time into a `java.time.LocalDateTime`.

13. **`LocalTimeParser`**

    + Parses time strings, such as "23:59", into a `java.time.LocalTime`.

14. **`MonetaryAmountFormatter`**

    + Formats and parses strings that show a money amount.

15. **`MonthDayFormatter`**

    + Parses strings with a month and a day, such as "12-31", into a `java.time.MonthDay`.

16. **`MonthFormatter`**

    + Parses strings that show a month, such as "January" or "01", into a `java.time.Month` enum.

17. **`NumberDecoratingFormatter`**

    + A decorator that adds extra formatting and parsing features for numbers.

18. **`PatternDecoratingFormatter`**

    + Applies a specific pattern or template when parsing and formatting.

19. **`PeriodFormatter`**

    + Parses strings that show a span of time, such as "P1Y2M" (1 year 2 months), into a `java.time.Period`.

20. **`TemporalAccessorParser`**

    + Parses implementations of `java.time.temporal.TemporalAccessor`, such as `LocalDate` and `LocalDateTime`.

21. **`YearFormatter`**

    + Parses year strings, such as "2020", into a `java.time.Year`.

22. **`YearMonthFormatter`**

    + Parses strings with a year and a month, such as "2020-01", into a `java.time.YearMonth`.

### 7. Best Practices

This example uses Spring's `FormattingConversionService` to parse currency strings. It creates a `FormattingConversionService` and adds a `CurrencyParser` to it. By setting the locale in `LocaleContextHolder`, the code parses currency strings in the format of each region (US dollars and Chinese yuan).

```java
public class ParserDemo {

    public static void main(String[] args) {
        // Create a formatting conversion service
        FormattingConversionService conversionService = new FormattingConversionService();
        // Add a currency parser to the conversion service
        conversionService.addParser(new CurrencyParser());

        // Set the current thread's Locale to the US
        LocaleContextHolder.setLocale(Locale.US);
        // Convert a US dollar string to a number
        Number formattedAmountForUS = conversionService.convert("$1,234.56", Number.class);
        System.out.println("Parsed Currency (US): " + formattedAmountForUS);

        // Change the Locale to China
        LocaleContextHolder.setLocale(Locale.CHINA);
        // Convert a yuan string to a number
        Number formattedAmountForCHINA = conversionService.convert("￥1,234.56", Number.class);
        System.out.println("Parsed Currency (CHINA): " + formattedAmountForCHINA);
    }
}
```

The `CurrencyParser` class implements Spring's `Parser<Number>` interface. It parses currency strings into numbers. Its `parse` method takes a currency string and a `Locale`. With the `Locale`, it creates a `NumberFormat` for the currency format of that region (such as the US or China).

```java
public class CurrencyParser implements Parser<Number> {
    @Override
    public Number parse(String text, Locale locale) throws ParseException {
        if (text == null) {
            throw new ParseException("Null string cannot be parsed to number", 0);
        }
        // Create a currency formatter for the given locale
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        // Parse the string into a number
        return format.parse(text);
    }
}
```

The output shows that `CurrencyParser` parsed currency strings in different regional formats. Both the US "$1,234.56" and the Chinese "￥1,234.56" became the same number, `1234.56`.

```java
Parsed Currency (US): 1234.56
Parsed Currency (CHINA): 1234.56
```

### 8. Relationship with Other Components

1. **Printer**

   + The `Printer` interface formats an object as a string. This helps when you show data, or turn objects into strings for storage and transfer.

2. **Formatter**

   - The `Formatter` interface adds to `Parser`. It parses a string to an object (like `Parser`) and also formats an object as a string. In practice, a `Formatter` often does both.

3. **FormattingConversionService**

   - `FormattingConversionService` is a core Spring class for registering and using formatters and parsers. You can register classes that implement `Parser`. It then gives you string-to-object conversion.

4. **DataBinder & WebDataBinder**

   - These classes handle data binding and type conversion. When a form or other user input is submitted, `DataBinder` and `WebDataBinder` can use the `Parser` implementations registered in `FormattingConversionService` to turn strings into objects.

5. **LocaleContextHolder**

   - `LocaleContextHolder` gives access to the locale of the current thread. The `parse` method of `Parser` usually needs a `Locale`. `LocaleContextHolder` can supply it, so parsing fits the format of each region.

6. **Locale**

   - The `parse` method of `Parser` takes a `Locale` parameter. This lets implementations handle different regions. It supports internationalization and localization.

### 9. FAQ

1. **Complex parsing logic**

   - For complex data types, or when you must support many locales, use existing Java APIs such as `DateFormat` and `NumberFormat`. You can also use Spring's internationalization support, such as `MessageSource`, to keep the implementation simple.

2. **Handling different locales**

   - Handling locales correctly is a challenge in international applications. Make sure `parse` uses the `Locale` parameter properly. Where possible, use Spring's localization and internationalization support so data is parsed correctly.

3. **Integration with Spring MVC**

   - In a Spring MVC application, make sure your custom `Parser` is registered in `FormattingConversionService`. Also check that controller methods use data binding and parsing logic correctly.

4. **Keeping parsing and formatting consistent**

   - When you use a `Parser` with its matching `Printer`, they must follow the same rules for the same type of data. Data of one type should convert smoothly between parsing and formatting, with no differences.
