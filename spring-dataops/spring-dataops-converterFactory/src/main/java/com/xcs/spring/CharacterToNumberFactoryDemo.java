package com.xcs.spring;

import com.xcs.spring.converter.StringToNumberConverterFactory;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;

public class CharacterToNumberFactoryDemo {

    public static void main(String[] args) {
        // Create a default conversion service
        // GenericConversionService is a general-purpose type conversion service
        GenericConversionService conversionService = new DefaultConversionService();

        // Add a String-to-Number converter factory to the conversion service
        // StringToNumberConverterFactory is a factory that creates String-to-Number converters
        conversionService.addConverterFactory(new StringToNumberConverterFactory());

        // Use the conversion service to convert the string "8" to Integer
        // This shows how to convert a string to the matching integer
        Integer num = conversionService.convert("8", Integer.class);

        // Print the conversion result
        System.out.println("String to Integer: " + num);
    }
}
