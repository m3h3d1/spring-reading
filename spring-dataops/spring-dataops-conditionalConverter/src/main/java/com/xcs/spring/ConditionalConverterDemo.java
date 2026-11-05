package com.xcs.spring;

import com.xcs.spring.converter.StringToIntegerConditionalConverter;
import org.springframework.core.convert.TypeDescriptor;

/**
 * @author xcs
 * @date 2023-12-08 11:02
 **/
public class ConditionalConverterDemo {

    public static void main(String[] args) {
        // Create the custom converter instance
        StringToIntegerConditionalConverter converter = new StringToIntegerConditionalConverter();

        // Define descriptors for the source type (String) and target type (Integer)
        TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
        TypeDescriptor targetType = TypeDescriptor.valueOf(Integer.class);

        // Test whether the converter applies to String to Integer conversion
        if (converter.matches(sourceType, targetType)) {
            // If the condition matches, run the conversion
            Integer result = converter.convert("8");
            System.out.println("Converted result: " + result);
        } else {
            // If the condition does not match, print a not-applicable message
            System.out.println("Conversion not applicable.");
        }
    }
}
