package com.xcs.spring;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class TypeComparatorDemo {
    public static void main(String[] args) {
        // Create an EvaluationContext
        StandardEvaluationContext context = new StandardEvaluationContext();

        // Create a SpEL expression parser
        SpelExpressionParser parser = new SpelExpressionParser();

        // Parse the expression
        Expression expression = parser.parseExpression("'2' < '-5.0'");

        // Compare with the TypeComparator
        boolean result = expression.getValue(context,Boolean.class);

        // Print the comparison result
        System.out.println("result : " + result);
    }
}
