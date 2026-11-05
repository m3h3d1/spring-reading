package com.xcs.spring;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author xcs
 * @date 2024-03-12 11:28
 **/
public class MethodResolverDemo {

    public static void main(String[] args) {
        // Create a SpEL expression parser
        ExpressionParser parser = new SpelExpressionParser();

        StandardEvaluationContext context = new StandardEvaluationContext();

        // The methods defined in MyBean are called by the SpEL expression
        MyBean myBean = new MyBean();
        context.setVariable("myBean", myBean);

        // Create a SpEL expression that calls a method of MyBean
        SpelExpression expression = (SpelExpression) parser.parseExpression("#myBean.add(10, 5)");

        // Set the context for the expression and compute the result
        int result = (int) expression.getValue(context);

        // Print the result
        System.out.println("result = " + result);
    }
}
