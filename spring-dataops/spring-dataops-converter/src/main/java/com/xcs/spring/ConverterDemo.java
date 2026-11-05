package com.xcs.spring;

import com.xcs.spring.converter.StringToBooleanConverter;
import com.xcs.spring.converter.StringToLocalDateConverter;
import org.springframework.core.convert.support.DefaultConversionService;

import java.time.LocalDate;

/**
 * @author xcs
 * @date 2023-12-07 16:53
 **/
public class ConverterDemo {

    public static void main(String[] args) {
        // Create a default conversion service
        DefaultConversionService service = new DefaultConversionService();

        // Add custom converters to the service
        service.addConverter(new StringToLocalDateConverter());
        service.addConverter(new StringToBooleanConverter());

        // Check whether a String can be converted to LocalDate
        if (service.canConvert(String.class, LocalDate.class)) {
            LocalDate localDate = service.convert("2023-12-07", LocalDate.class);
            System.out.println("LocalDate = " + localDate);
        }

        // Check whether the String "yes" can be converted to Boolean
        if (service.canConvert(String.class, Boolean.class)) {
            Boolean boolValue = service.convert("yes", Boolean.class);
            System.out.println("yes = " + boolValue);
        }

        // Check whether the String "no" can be converted to Boolean
        if (service.canConvert(String.class, Boolean.class)) {
            Boolean boolValue = service.convert("no", Boolean.class);
            System.out.println("no = " + boolValue);
        }
    }
}
