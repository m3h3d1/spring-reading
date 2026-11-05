package com.xcs.spring;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author xcs
 * @date 2023-12-04 14:13
 **/
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
