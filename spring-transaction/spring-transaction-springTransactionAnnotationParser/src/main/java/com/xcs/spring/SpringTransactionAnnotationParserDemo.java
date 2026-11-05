package com.xcs.spring;

import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.lang.reflect.Method;

public class SpringTransactionAnnotationParserDemo {

    public static void main(String[] args) throws NoSuchMethodException {
        // Get the insertScore method of ScoresServiceImpl
        Method insertScoreMethod = ScoresServiceImpl.class.getMethod("insertScore");
        // Create a SpringTransactionAnnotationParser instance
        SpringTransactionAnnotationParser parser = new SpringTransactionAnnotationParser();
        // Parse the transaction annotation on insertScore into a transaction attribute
        TransactionAttribute transactionAttribute = parser.parseTransactionAnnotation(insertScoreMethod);
        // Print the transaction attribute
        System.out.println(transactionAttribute);
    }
}
