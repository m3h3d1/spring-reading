package com.xcs.spring;

import com.xcs.spring.printer.CurrencyPrinter;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.support.FormattingConversionService;

import java.util.Locale;

public class PrinterDemo {

    public static void main(String[] args) {
        // Create a FormattingConversionService instance
        FormattingConversionService conversionService = new FormattingConversionService();
        // Register the custom CurrencyPrinter with conversionService
        conversionService.addPrinter(new CurrencyPrinter());

        // Set the currency amount
        double amount = 1234.56;

        // Set the current thread's Locale to the US
        LocaleContextHolder.setLocale(Locale.US);
        // Use conversionService to convert the amount to a string with US currency format
        String formattedAmountForUS = conversionService.convert(amount, String.class);
        System.out.println("Formatted Currency (US): " + formattedAmountForUS);

        // Set the current thread's Locale to China
        LocaleContextHolder.setLocale(Locale.CHINA);
        // Use conversionService to convert the amount to a string with Chinese currency format
        String formattedAmountForCHINA = conversionService.convert(amount, String.class);
        System.out.println("Formatted Currency (CHINA): " + formattedAmountForCHINA);
    }
}
