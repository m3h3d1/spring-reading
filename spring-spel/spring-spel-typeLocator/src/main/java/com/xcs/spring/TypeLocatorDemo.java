package com.xcs.spring;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class TypeLocatorDemo {
    public static void main(String[] args) {
        // Create a SpEL expression parser
        ExpressionParser parser = new SpelExpressionParser();

        // Parse the expression to get the Class object of Date
        Class dateClass = parser.parseExpression("T(java.util.Date)").getValue(Class.class);
        System.out.println("dateClass = " + dateClass);

        // Parse the expression to get the Class object of String
        Class stringClass = parser.parseExpression("T(String)").getValue(Class.class);
        System.out.println("stringClass = " + stringClass);

        // Parse the expression to compare two RoundingMode enum values
        boolean trueValue = parser.parseExpression("T(java.math.RoundingMode).CEILING < T(java.math.RoundingMode).FLOOR").getValue(Boolean.class);
        System.out.println("trueValue = " + trueValue);
    }
}
