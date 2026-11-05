package com.xcs.spring;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class ExpressionDemo {
    public static void main(String[] args) {
        // Create the parser instance
        ExpressionParser parser = new SpelExpressionParser();
        // Parse a basic expression
        Expression expression = parser.parseExpression("100 + 10");
        // Compute the expression result
        Integer result = expression.getValue(Integer.class);
        System.out.println("Result of expression '100 + 10': " + result);
    }
}
