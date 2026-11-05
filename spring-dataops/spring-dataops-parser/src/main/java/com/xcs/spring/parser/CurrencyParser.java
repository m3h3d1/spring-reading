package com.xcs.spring.parser;

import org.springframework.format.Parser;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class CurrencyParser implements Parser<Number> {
    @Override
    public Number parse(String text, Locale locale) throws ParseException {
        // Create a currency formatter for the given locale
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        // Parse the string into a number
        return format.parse(text);
    }
}
