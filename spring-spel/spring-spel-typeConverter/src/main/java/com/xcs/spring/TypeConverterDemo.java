package com.xcs.spring;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class TypeConverterDemo {

    public static void main(String[] args) {
        // Create a SpEL expression parser
        SpelExpressionParser parser = new SpelExpressionParser();

        // Create an EvaluationContext
        StandardEvaluationContext context = new StandardEvaluationContext();

        // Define a value to convert
        String stringValue = "'123'";

        // Parse the expression
        Expression expression = parser.parseExpression(stringValue);

        // Convert with the TypeConverter
        Integer intValue = expression.getValue(context, Integer.class);

        // Print the converted value
        System.out.println("Converted Integer value: " + intValue);
    }
}
