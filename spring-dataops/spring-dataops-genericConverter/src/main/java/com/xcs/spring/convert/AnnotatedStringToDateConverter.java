package com.xcs.spring.convert;

import com.xcs.spring.annotation.DateFormat;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

public class AnnotatedStringToDateConverter implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        // Define the convertible type pair: String to Date
        return Collections.singleton(new ConvertiblePair(String.class, Date.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        // If the source is null, return null
        if (source == null) {
            return null;
        }

        // Convert the source object to a string
        String dateString = (String) source;
        // Get the DateFormat annotation on the target type (the Date field)
        DateFormat dateFormatAnnotation = targetType.getAnnotation(DateFormat.class);
        // If the target field has no DateFormat annotation, throw an exception
        if (dateFormatAnnotation == null) {
            throw new IllegalArgumentException("Missing DateFormat annotation on the target field");
        }

        try {
            // Create a SimpleDateFormat from the date pattern in the annotation
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatAnnotation.value());
            // Use SimpleDateFormat to parse the string into a Date
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            // If parsing fails, throw an exception
            throw new IllegalArgumentException("Cannot parse date", e);
        }
    }
}
