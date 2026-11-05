package com.xcs.spring;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;

/**
 * @author xcs
 * @date 2023-12-08 16:30
 **/
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
