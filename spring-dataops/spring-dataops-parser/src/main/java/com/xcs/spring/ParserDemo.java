package com.xcs.spring;

import com.xcs.spring.parser.CurrencyParser;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.support.FormattingConversionService;

import java.util.Locale;

public class ParserDemo {

    public static void main(String[] args) {
        // Create a formatting conversion service
        FormattingConversionService conversionService = new FormattingConversionService();
        // Add a currency parser to the conversion service
        conversionService.addParser(new CurrencyParser());

        // Set the current thread's Locale to the US
        LocaleContextHolder.setLocale(Locale.US);
        // Convert a US dollar string to a number
        Number formattedAmountForUS = conversionService.convert("$1,234.56", Number.class);
        System.out.println("Parsed Currency (US): " + formattedAmountForUS);

        // Change the Locale to China
        LocaleContextHolder.setLocale(Locale.CHINA);
        // Convert a yuan string to a number
        Number formattedAmountForCHINA = conversionService.convert("￥1,234.56", Number.class);
        System.out.println("Parsed Currency (CHINA): " + formattedAmountForCHINA);
    }
}
