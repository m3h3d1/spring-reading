package com.xcs.spring.converter;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;

/**
 * @author xcs
 * @date 2023-12-08 11:05
 **/

public class StringToIntegerConditionalConverter implements Converter<String, Integer>, ConditionalConverter {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        // Condition: return true when the source type is String and the target type is Integer
        return String.class.equals(sourceType.getType()) && Integer.class.equals(targetType.getType());
    }

    @Override
    public Integer convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(source);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unable to convert String to Integer: " + source, e);
        }
    }
}
