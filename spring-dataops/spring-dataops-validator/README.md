## Validator

- [Validator](#validator)
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

1. **Spring MVC**

   + It is important to know Spring MVC. This includes controllers, `RequestMapping`, and view resolution. Understand how HTTP requests and responses are handled, and how Spring MVC does data binding and form handling.

2. **Bean Validation (JSR 303/JSR 380)**

   + The `Validator` interface is part of Spring. But it also helps to know JSR 303/JSR 380 (the Bean Validation API), because Spring supports these standards. Know the common Bean Validation annotations, such as `@NotNull`, `@Size`, `@Min`, and `@Max`.

3. **Error handling**

   + Understand Spring's error handling, especially for data validation. This includes the `Errors` and `BindingResult` interfaces.

### 3. Description

The `Validator` interface is a key part of Spring for validating object data. It gives a standard way to validate Java objects, especially for user input and form data.

### 4. Main Features

1. **Custom data validation**

   + You can write custom validation logic for your business needs. It checks whether object properties meet specific rules and conditions.

2. **Support for many object types**

   + The `supports(Class<?> clazz)` method tells which types of objects a validator can validate. So one validator can serve different data models.

3. **Error recording**

   + In `validate(Object target, Errors errors)`, the validator checks the properties of the target object. If something is invalid, it adds an error to the `Errors` object. You can use these errors for display and later handling.

4. **Works with Spring MVC**

   + In Spring MVC, a `Validator` can be part of the controller flow. It validates model objects automatically. It is often used with the `@Valid` annotation to validate form submissions and request parameters.

### 5. Interface Source

The `Validator` interface is a key component for custom object validation. It has two main methods. `supports(Class<?> clazz)` tells whether the validator supports a type of object. It is usually implemented by checking whether the object is a given class or a subclass. `validate(Object target, Errors errors)` runs the real validation. `target` is the object to validate, and `errors` records the errors found.

```java
/**
 * A validator for application-specific objects.
 *
 * <p>This interface is totally divorced from any infrastructure
 * or context; that is to say it is not coupled to validating
 * only objects in the web tier, the data-access tier, or the
 * whatever-tier. As such it is amenable to being used in any layer
 * of an application, and supports the encapsulation of validation
 * logic as a first-class citizen in its own right.
 *
 * <p>Find below a simple but complete {@code Validator}
 * implementation, which validates that the various {@link String}
 * properties of a {@code UserLogin} instance are not empty
 * (they are not {@code null} and do not consist
 * wholly of whitespace), and that any password that is present is
 * at least {@code 'MINIMUM_PASSWORD_LENGTH'} characters in length.
 *
 * <pre class="code"> public class UserLoginValidator implements Validator {
 *
 *    private static final int MINIMUM_PASSWORD_LENGTH = 6;
 *
 *    public boolean supports(Class clazz) {
 *       return UserLogin.class.isAssignableFrom(clazz);
 *    }
 *
 *    public void validate(Object target, Errors errors) {
 *       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "field.required");
 *       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required");
 *       UserLogin login = (UserLogin) target;
 *       if (login.getPassword() != null
 *             && login.getPassword().trim().length() < MINIMUM_PASSWORD_LENGTH) {
 *          errors.rejectValue("password", "field.min.length",
 *                new Object[]{Integer.valueOf(MINIMUM_PASSWORD_LENGTH)},
 *                "The password must be at least [" + MINIMUM_PASSWORD_LENGTH + "] characters in length.");
 *       }
 *    }
 * }</pre>
 *
 * <p>See also the Spring reference manual for a fuller discussion of
 * the {@code Validator} interface and its role in an enterprise
 * application.
 *
 * @author Rod Johnson
 * @see SmartValidator
 * @see Errors
 * @see ValidationUtils
 */
public interface Validator {

    /**
     * Can this {@link Validator} {@link #validate(Object, Errors) validate}
     * instances of the supplied {@code clazz}?
     * <p>This method is <i>typically</i> implemented like so:
     * <pre class="code">return Foo.class.isAssignableFrom(clazz);</pre>
     * (Where {@code Foo} is the class (or superclass) of the actual
     * object instance that is to be {@link #validate(Object, Errors) validated}.)
     * @param clazz the {@link Class} that this {@link Validator} is
     * being asked if it can {@link #validate(Object, Errors) validate}
     * @return {@code true} if this {@link Validator} can indeed
     * {@link #validate(Object, Errors) validate} instances of the
     * supplied {@code clazz}
     */
    boolean supports(Class<?> clazz);

    /**
     * Validate the supplied {@code target} object, which must be
     * of a {@link Class} for which the {@link #supports(Class)} method
     * typically has (or would) return {@code true}.
     * <p>The supplied {@link Errors errors} instance can be used to report
     * any resulting validation errors.
     * @param target the object that is to be validated
     * @param errors contextual state about the validation process
     * @see ValidationUtils
     */
    void validate(Object target, Errors errors);

}
```

### 6. Main Implementations

1. **LocalValidatorFactoryBean**

   - A bridge class that brings the JSR-303/JSR-380 Bean Validation API into a Spring application. It implements the `Validator` interface and lets you validate objects with standard Bean Validation annotations.

2. **SpringValidatorAdapter**

   - This class adapts a standard JSR-303/JSR-380 validator to Spring's `Validator` interface. It lets you use the standard Bean Validation API in Spring.

### 7. Best Practices

This example uses Spring's `Validator` interface to validate the properties of a `Person` object. The program creates a `Person` with possibly invalid values. Then it creates a custom `PersonValidator` to validate it. A `BeanPropertyBindingResult` stores and tracks the errors found. At the end, the program checks for errors and prints all of them.

```java
public class ValidatorDemo {

    public static void main(String[] args) {
        // Create a Person instance
        Person person = new Person();
        person.setName(null);
        person.setAge(130);

        // Create a BeanPropertyBindingResult to hold validation errors
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(person, "person");

        // Create a PersonValidator instance (a custom validator)
        PersonValidator validator = new PersonValidator();

        // Check whether PersonValidator supports the Person class
        if (validator.supports(person.getClass())) {
            // Run the validation
            validator.validate(person, result);
        }

        // Check for validation errors and print all field errors
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ":" + error.getDefaultMessage());
            }
        }
    }
}
```

The `PersonValidator` class checks that the `name` of a `Person` is not empty and that the `age` is in a valid range. If a field breaks a rule, it records an error in the `Errors` object.

```java
public class PersonValidator implements Validator {

    @Override
    public boolean supports(Class clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors e) {
        // Check that the name is not empty
        ValidationUtils.rejectIfEmpty(e, "name", "name.empty", "Name must not be empty");

        // Cast the object to Person
        Person p = (Person) obj;

        // Check whether the age is negative
        if (p.getAge() < 0) {
            e.rejectValue("age", "negative.value", "Age must not be negative");
        }
        // Check whether the age is over 120
        else if (p.getAge() > 120) {
            e.rejectValue("age", "too.darn.old", "The oldest known age is 120");
        }
    }
}
```

A simple Java class `Person`.

```java
public class Person {

    private String name;

    private int age;

    // get and set
}
```

The output shows that `PersonValidator` found the invalid data in the `Person` object by the validation rules. It created the matching error messages in the `Errors` object.

```
name:Name must not be empty
age:The oldest known age is 120
```

### 8. Relationship with Other Components

+ **Spring MVC framework**

  + The `Validator` interface mainly validates user input and model data in the web layer. It works closely with controllers. It validates model data before a client request (such as a form submission) is handled. During data binding, a `Validator` can check whether the request parameters bound to a model object meet specific rules. Spring MVC also supports Bean Validation (JSR 303/JSR 380) through `LocalValidatorFactoryBean`. So you can use standard Bean Validation annotations without changing existing code.

### 9. FAQ

1. **Implementing `supports` correctly**

   - Getting the `supports(Class<?> clazz)` method right is a common problem. It must correctly tell whether the validator supports a type of object. If not, the validation logic may never run.

2. **Separating logic and internationalization**

   - Keep validation logic separate from business logic. Make sure the validation is complete and matches the business rules. Also manage and show validation messages, especially when messages must be translated for many languages.

3. **Integration with JSR 303/JSR 380 Bean Validation**

   + When you use `Validator` together with the JSR 303/JSR 380 Bean Validation standard, it is common to be unsure how they differ and how to integrate them well.

4. **Validating nested properties and complex objects**

   + When an object has nested properties or a complex structure, it is harder to write validation logic that is both effective and efficient.
