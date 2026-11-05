package com.xcs.spring;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author xcs
 * @date 2024-01-03 22:35
 */
public class ExpressionParserDemo {

    public static void main(String[] args) {
        // Create the parser instance
        ExpressionParser parser = new SpelExpressionParser();
        // Parse a basic expression
        Expression expression = parser.parseExpression("100 * 2 + 10");

        System.out.println("expression = " + expression);
    }
}
