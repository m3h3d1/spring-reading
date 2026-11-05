package com.xcs.spring;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

/**
 * @author xcs
 * @date 2023-12-04 14:06
 **/
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
