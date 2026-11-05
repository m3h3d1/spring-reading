## Printer

- [Printer](#printer)
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

The `Printer` interface is a key part of Spring. It formats an object as a human-readable string. It defines one method, `String print(T object, Locale locale)`. `T` is the type of object to format. `Locale` is the locale used for formatting. This lets you adapt the output to different cultures and regions.

### 4. Main Features

1. **Format objects as strings**

   + Turns objects of many types (such as dates, times, and numbers) into human-readable strings.

2. **Localization support**

   + `Printer` takes a `Locale` parameter. So it can format by locale and supports internationalization and localization.

3. **User-friendly display**

   + Shows data in a way users find easy to read and understand on the user interface.

### 5. Interface Source

The `Printer<T>` interface defines a print method for display. It is a functional interface. Its main job is to turn an object of type T into a string that is good for display. It takes two parameters: the object to print, `object`, and the current user's locale, `locale`. This design supports formatting by regional standards, for internationalization and localization.

```java
/**
 * Prints objects of type T for display.
 *
 * @author Keith Donald
 * @since 3.0
 * @param <T> the type of object this Printer prints
 */
@FunctionalInterface
public interface Printer<T> {

	/**
	 * Print the object of type T for display.
	 * @param object the instance to print
	 * @param locale the current user locale
	 * @return the printed text string
	 */
	String print(T object, Locale locale);

}
```

### 6. Main Implementations

1. **CharArrayFormatter**

   + Formats a char array as a string. It usually turns the char array directly into the matching string.

2. **CurrencyUnitFormatter**

   + Formats currency units. It may turn a currency code such as "USD" or "EUR" into a fuller or localized currency name.
   
3. **DateFormatter**

   + Formats a `java.util.Date` as a readable date string. It supports many date formats and may adjust to the locale.

4. **DurationFormatter**

   + Formats a `java.time.Duration` (a time interval) as a readable string. For example, "2 hours 15 minutes".

5. **InetAddressFormatter**

   + Formats a `java.net.InetAddress` object (for example, an IP address) as a standard string. For example, it turns the binary IP address into dotted decimal form.

6. **InstantFormatter**

   + Formats a `java.time.Instant` (a specific point in time) as a date and time string.

7. **IsoOffsetFormatter**

   + Formats date-time objects that have time zone offset information. It follows ISO 8601 (for example "2023-03-28T15:20:45+02:00").

8. **MillisecondInstantPrinter**

   + Turns a timestamp in milliseconds into a standard date and time format. It is often used to turn system timestamps into human-readable date and time.

9. **MonetaryAmountFormatter**

   + Formats objects that show a money amount. It may combine the value and the currency unit, and take currency format localization into account.

10. **MonthDayFormatter**

    + Formats a `java.time.MonthDay`. It usually turns the month and day into a specific string format, such as "March 28".

11. **MonthFormatter**

    + Formats months. It turns a month number (1-12) into text (such as "January", "February").

12. **NumberDecoratingFormatter**

    + Adds decoration when formatting numbers. For example, it can add a prefix or suffix, or use a specific display format.

13. **PatternDecoratingFormatter**

    + Formats and decorates strings with a given pattern. This may involve complex string handling and pattern matching.

14. **PeriodFormatter**

    + Formats a `java.time.Period` (a date interval) as a readable string, such as "2 years 6 months".

15. **ReadableInstantPrinter**

    + For `ReadableInstant` objects in the Joda-Time library. It turns them into date and time strings.

16. **ReadablePartialPrinter**

    + For `ReadablePartial` objects in the Joda-Time library. These usually hold partial date-time information, such as only the month and day.

17. **TemporalAccessorPrinter**

    + Formats any date-time object that implements `java.time.temporal.TemporalAccessor`. This covers most Java 8 date-time types.

18. **YearFormatter**

    + Formats years. It may turn a numeric year into a specific string.

19. **YearMonthFormatter**

    + Formats a `java.time.YearMonth`. It usually joins the year and month in a readable format, such as "March 2023".

### 7. Best Practices

This example uses Spring's `FormattingConversionService` with a custom `CurrencyPrinter` to format currency. It creates a `FormattingConversionService` and registers the `CurrencyPrinter`. Then it sets the `Locale` in `LocaleContextHolder`. The code shows the same amount as a string in US format and in Chinese format.

```java
public class PrinterDemo {

    public static void main(String[] args) {
        // Create a FormattingConversionService instance
        FormattingConversionService conversionService = new FormattingConversionService();
        // Register the custom CurrencyPrinter with conversionService
        conversionService.addPrinter(new CurrencyPrinter());

        // Set the currency amount
        double amount = 1234.56;

        // Set the current thread's Locale to the US
        LocaleContextHolder.setLocale(Locale.US);
        // Use conversionService to convert the amount to a string with US currency format
        String formattedAmountForUS = conversionService.convert(amount, String.class);
        System.out.println("Formatted Currency (US): " + formattedAmountForUS);

        // Set the current thread's Locale to China
        LocaleContextHolder.setLocale(Locale.CHINA);
        // Use conversionService to convert the amount to a string with Chinese currency format
        String formattedAmountForCHINA = conversionService.convert(amount, String.class);
        System.out.println("Formatted Currency (CHINA): " + formattedAmountForCHINA);
    }
}
```

The `CurrencyPrinter` class implements Spring's `Printer<Number>` interface. It formats a number (`Number`) as a currency string. In the `print` method, it uses Java's `NumberFormat.getCurrencyInstance` to get the currency formatter for the locale. Then it uses that formatter to turn the number into the currency string of that region.

```java
public class CurrencyPrinter implements Printer<Number> {

    @Override
    public String print(Number number, Locale locale) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        return currencyFormat.format(number);
    }
}
```

The output shows how `CurrencyPrinter` and the `Locale` setting handle international currency display. The amount is shown in a way users in each region know and understand.

```java
Formatted Currency (US): $1,234.56
Formatted Currency (CHINA): ￥1,234.56
```

### 8. Relationship with Other Components

1. **Parser**

   - `Printer` and `Parser` are often used together. `Printer` formats an object as a string. `Parser` does the reverse and parses a string back to the object. This pair is useful for form data, display, and user input.

2. **Formatter**

   - The `Formatter` interface combines `Printer` and `Parser`. It is one interface for formatting and parsing objects. A class that implements `Formatter` has both `print` and `parse` methods, matching `Printer` and `Parser`.

3. **FormattingConversionService**

   - `FormattingConversionService` is a core Spring component for registering and managing formatters (Formatters) and converters (Converters). You can register `Printer` implementations in it. Spring then uses them automatically to format data when needed.

4. **DataBinder & WebDataBinder**

   - In data binding and form handling, `Printer` implementations turn object properties into strings for display. For example, in Spring MVC, `WebDataBinder` can use registered `Printer` implementations to format model attributes for the view.

5. **Locale**

   - The `print` method of `Printer` takes a `Locale` parameter. This lets implementations format objects by locale. It supports internationalization and localization.

### 9. FAQ

1. **Complex formatting logic**

   + For complex data types, or when you must support many locales, use existing Java APIs such as `DateFormat` and `NumberFormat`. You can also use Spring's internationalization support, such as `MessageSource`, to keep the implementation simple.

2. **Handling different locales (`Locale`)**

   + Handling locales correctly is a challenge in international applications. Make sure `print` uses the `Locale` parameter properly. Where possible, use Spring's localization and internationalization support.

3. **Integration with Spring MVC**

   + In a Spring MVC application, make sure your custom `Printer` is registered in `FormattingConversionService`. Also check that controller methods use data binding and formatting correctly.

4. **Keeping formatting and parsing consistent**

   + When you use a `Printer` with its matching `Parser`, they must follow the same formatting and parsing rules for the same type of data.
