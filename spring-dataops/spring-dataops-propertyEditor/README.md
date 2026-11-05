## PropertyEditor

- [PropertyEditor](#propertyeditor)
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

1. **JavaBeans specification**

   + It is key to know the JavaBeans specification, because `PropertyEditor` is part of it. Knowing the JavaBeans component model, properties, events, and methods helps you understand what `PropertyEditor` does and where to use it.
2. **Reflection API** 

   + Knowing the Java reflection API helps. `PropertyEditor` is often used with reflection, especially when you handle properties dynamically and call getter/setter methods.
3. **Type conversion and data binding** 
   + You also need a basic understanding of type conversion and data binding. `PropertyEditor` is often used there, especially to turn string config values into complex object types.

### 3. Description

The `PropertyEditor` interface is part of the Java Beans specification. It handles conversion and editing of Java Bean properties. It lets you convert a property value from one format to another (for example, from a string to an object). It also supports custom editing.

### 4. Main Features

1. **Value conversion** 

   + Converts a property value from one format to another. The most common case is turning a string into the matching object type. For example, a date string becomes a `Date`.

2. **Text editing** 

   + The `setAsText(String text)` method takes a string and converts it to the type of the property. The `getAsText()` method converts the property value to its string form.

3. **State management** 

   + The `setValue(Object value)` and `getValue()` methods set and get the current value of the property.

4. **Custom editor support** 

   + If needed, it can provide a custom editor UI. `supportsCustomEditor()` tells whether a custom editor is supported. `getCustomEditor()` returns the custom editor instance.

5. **Property change notification** 

   + It supports property change listeners. Use `addPropertyChangeListener(PropertyChangeListener listener)` and `removePropertyChangeListener(PropertyChangeListener listener)` to add and remove them.

6. **Java initialization string** 

   + `getJavaInitializationString()` returns a string that shows how to initialize the property value in code.

### 5. Interface Source

The `PropertyEditor` interface manages and edits properties in the Java Beans specification. It has methods to convert property values (`setAsText` and `getAsText`), get and set the property (`setValue` and `getValue`), support graphical display of the property (`isPaintable` and `paintValue`), and generate initialization code for the property (`getJavaInitializationString`). It also supports creating and using custom editors, and listening to property changes.

```java
public interface PropertyEditor {

    /**
     * Set (or change) the object that is to be edited. Primitive types such as "int" must
     * be wrapped as the corresponding object type such as "java.lang.Integer".
     * @param value The new target object to be edited. Note that this object should not be
     * modified by the PropertyEditor, rather the PropertyEditor should create a new object
     * to hold any modified value.
     */
    void setValue(Object value);

    /**
     * Gets the property value.
     * @return The value of the property. Primitive types such as "int" will be wrapped as
     * the corresponding object type such as "java.lang.Integer".
     */
    Object getValue();

    /**
     * Determines whether this property editor is paintable.
     * @return True if the class will honor the paintValue method.
     */
    boolean isPaintable();

    /**
     * Paint a representation of the value into a given area of screen real estate. Note
     * that the PropertyEditor is responsible for doing its own clipping so that it fits
     * into the given rectangle. If the PropertyEditor doesn't honor paint requests
     * (see isPaintable) this method should be a silent noop.
     * @param gfx Graphics object to paint into.
     * @param box Rectangle within graphics object into which we should paint.
     */
    void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box);

    /**
     * Returns a fragment of Java code that can be used to set a property to match the
     * editors current state. This method is intended for use when generating Java code to
     * reflect changes made through the property editor.
     * @return A fragment of Java code representing an initializer for the current value.
     * It should not contain a semi-colon (';') to end the expression.
     */
    String getJavaInitializationString();

    /**
     * Gets the property value as text.
     * @return The property value as a human editable string. Returns null if the value
     * can't be expressed as an editable string. If a non-null value is returned, then the
     * PropertyEditor should be prepared to parse that string back in setAsText().
     */
    String getAsText();

    /**
     * Set the property value by parsing a given String. May raise java.lang.IllegalArgumentException
     * if either the String is badly formatted or if this kind of property can't be expressed as text.
     * @param text The string to be parsed.
     */
    void setAsText(String text) throws java.lang.IllegalArgumentException;

    /**
     * If the property value must be one of a set of known tagged values, then this method
     * should return an array of the tags. This can be used to represent (for example) enum
     * values. If a PropertyEditor supports tags, then it should support the use of
     * setAsText with a tag value as a way of setting the value.
     * @return The tag values for this property. May be null if this property cannot be
     * represented as a tagged value.
     */
    String[] getTags();

    /**
     * A PropertyEditor may choose to make available a full custom Component that edits
     * its property value. It is the responsibility of the PropertyEditor to hook itself up
     * to its editor Component itself and to report property value changes by firing a
     * PropertyChange event.
     * @return A java.awt.Component that will allow a human to directly edit the current
     * property value. May be null if this is not supported.
     */
    java.awt.Component getCustomEditor();

    /**
     * Determines whether this property editor supports a custom editor.
     * @return True if the propertyEditor can provide a custom editor.
     */
    boolean supportsCustomEditor();

    /**
     * Adds a listener for the value change. When the property editor changes its value
     * it should fire a PropertyChangeEvent on all registered PropertyChangeListeners,
     * specifying the null value for the property name and itself as the source.
     * @param listener The PropertyChangeListener to add.
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes a listener for the value change.
     * @param listener The PropertyChangeListener to remove.
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

}
```

### 6. Main Implementations

1. **`ByteArrayPropertyEditor`**

   - Converts a string to a byte array. It is often used for encoded data such as BASE64.

2. **`CharacterEditor`**

   - Converts a string to a single character.

3. **`CharArrayPropertyEditor`**

   - Converts a string to a char array.

4. **`CharsetEditor`**

   - Converts a string (a charset name) to a `java.nio.charset.Charset`.

5. **`ClassArrayEditor`**

   - Converts a string array to a `Class` array. Each string is a class name.

6. **`ClassEditor`**

   - Converts a single string (a class name) to a `Class` object.

7. **`CurrencyEditor`**

   - Converts a string to a `java.util.Currency`.

8. **`CustomBooleanEditor`**

   - Converts a string to a boolean. It handles more boolean forms, such as "on"/"off".

9. **`CustomCollectionEditor`**

   - Converts a string to a specific collection type, such as `List` or `Set`.

10. **`CustomDateEditor`**

    - Converts a string to a `java.util.Date`. It usually needs a date format.

11. **`CustomMapEditor`**

    - Converts a string to a `Map`. It handles key-value data.

12. **`CustomNumberEditor`**

    - Converts a string to a number, including types such as `Integer`, `Double`, and `BigDecimal`.

13. **`FileEditor`**

    - Converts a file path string to a `java.io.File`.

14. **`InputSourceEditor`**

    - Converts a string to an `org.xml.sax.InputSource`. It is often used for XML data sources.

15. **`InputStreamEditor`**

    - Converts a resource location string to a `java.io.InputStream`.

16. **`LocaleEditor`**

    - Converts a string such as "en_US" to a `java.util.Locale`.

17. **`PathEditor`**

    - Like `FileEditor`, but converts to a `java.nio.file.Path`.

18. **`PatternEditor`**

    - Converts a string to a `java.util.regex.Pattern`, for regular expressions.

19. **`PropertiesEditor`**

    - Converts a string to a `java.util.Properties`. It handles strings in properties file format.

20. **`ReaderEditor`**

    - Converts a resource location string to a `java.io.Reader`.

21. **`ResourceBundleEditor`**

    - Converts a string to a `java.util.ResourceBundle`, for internationalization resources.

22. **`StringArrayPropertyEditor`**

    - Converts a comma-separated string to a string array.

23. **`StringTrimmerEditor`**

    - Trims whitespace around a string. It can optionally turn an empty string into `null`.

24. **`TimeZoneEditor`**

    - Converts a string to a `java.util.TimeZone`.

25. **`URIEditor`**

    - Converts a string to a `java.net.URI`.

26. **`URLEditor`**

    - Converts a string to a `java.net.URL`.

27. **`UUIDEditor`**

    - Converts a string to a `java.util.UUID`.

28. **`ZoneIdEditor`**

    - Converts a string to a `java.time.ZoneId`, for time zone information.

### 7. Best Practices

This example wraps the `MyBean` class with `BeanWrapperImpl`. It registers the custom editor `MyCustomDateEditor` for `Date` properties to convert date strings. Then it sets the "date" and "path" properties of `MyBean` and prints the resulting `MyBean`. This shows how property editors work in Spring and how property values are converted on the fly.

```java
public class PropertyEditorDemo {

    public static void main(String[] args) {
        // Create a BeanWrapperImpl instance to work with the MyBean class.
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(MyBean.class);

        // Register the custom property editor MyCustomDateEditor for Date properties.
        beanWrapper.overrideDefaultEditor(Date.class, new MyCustomDateEditor());

        // Set the "date" property of MyBean using the string "2023-12-5".
        // The registered MyCustomDateEditor converts the string to a Date.
        beanWrapper.setPropertyValue("date", "2023-12-5");

        // Set the "path" property of MyBean using the string "/opt/spring-reading".
        // No editor is registered for Path, so the default conversion logic is used.
        beanWrapper.setPropertyValue("path", "/opt/spring-reading");

        // Print the wrapped MyBean instance.
        System.out.println("MyBean = " + beanWrapper.getWrappedInstance());
    }
}
```

`MyCustomDateEditor` is a custom property editor that extends `PropertyEditorSupport`. It converts strings in the "`yyyy-MM-DD`" format to `Date` objects, and formats `Date` objects back to that string format. It handles conversion between strings and dates, and fits data binding and type conversion in Spring.

```java
public class MyCustomDateEditor extends PropertyEditorSupport {

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD");

    @Override
    public void setAsText(@Nullable String text) throws IllegalArgumentException {
        try {
            setValue(this.dateFormat.parse(text));
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
        }
    }

    @Override
    public String getAsText() {
        Date value = (Date) getValue();
        return (value != null ? this.dateFormat.format(value) : "");
    }
}
```

`MyBean` is a Java Bean. It wraps its properties and gives access through getters and setters, so you can read and change the two properties easily. The `toString` method shows the current state of a `MyBean`.

```java
public class MyBean {

    private Path path;

    private Date date;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "path=" + path +
                ", date=" + date +
                '}';
    }
}
```

The output shows that `PropertyEditor` implementations are at the core of type conversion. When a Java Bean property is set, a string is converted to the needed complex type, such as a date. This is very common when Spring handles configuration and data binding.

```java
MyBean = MyBean{path=\opt\spring-reading, date=Thu Jan 05 00:00:00 CST 2023}
```

### 8. Relationship with Other Components

1. **Java Beans**

   - `PropertyEditor` is part of the Java Beans specification. It manages editing and conversion of Java Bean properties. It lets a bean property convert from one type to another, especially from a string to a complex type.

2. **Spring Framework**

   - In Spring, `PropertyEditor` converts data from property files or annotations at runtime. For example, it turns strings in a config file (such as XML) into property values of Java objects. `PropertyEditor` is closely tied to Spring's `BeanWrapper` and `DataBinder`, which bind and validate bean properties.

3. **Data binding**

   - `PropertyEditor` plays a key role in data binding, especially in web applications. When request parameters are mapped to object properties, it converts string parameters to the property types.

4. **Type conversion**

   - Since Spring 3.0, `ConversionService` is the recommended main way to convert types. But `PropertyEditor` is still used in old code and in some special cases. `ConversionService` is more consistent and general. `PropertyEditor` still helps with editing and converting some property types.

### 9. FAQ

1. **Thread safety**

   - `PropertyEditor` instances are usually not thread-safe. Sharing one across threads can cause inconsistent data or race conditions.

2. **State management**

   - `PropertyEditor` keeps state. So one instance cannot be used for many properties or many operations at the same time. You need a new instance each time, or you must reset the state. This can hurt efficiency.

3. **Limits for complex types**

   - For some complex data types, converting with `PropertyEditor` can be hard. This is true when the conversion has complex logic or many steps.

4. **Developing and maintaining custom editors**

   - A custom `PropertyEditor` needs a deep understanding of the internal structure and behavior of the target type. This makes development and maintenance harder.

5. **Integration with Spring's `ConversionService`**

   - Since Spring 3.0 added `ConversionService`, you must choose between `PropertyEditor` and `ConversionService`. In some cases they must live together. This can make configuration and use confusing.
