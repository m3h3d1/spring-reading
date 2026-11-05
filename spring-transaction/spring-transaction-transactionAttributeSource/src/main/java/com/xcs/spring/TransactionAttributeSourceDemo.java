package com.xcs.spring;

import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

import java.lang.reflect.Method;

public class TransactionAttributeSourceDemo {

    public static void main(String[] args) throws NoSuchMethodException {
        // Get the insertScore method of ScoresServiceImpl
        Method insertScoreMethod = ScoresServiceImpl.class.getMethod("insertScore");
        // Create an annotation-based transaction attribute source
        TransactionAttributeSource transactionAttributeSource = new AnnotationTransactionAttributeSource();
        // Parse the transaction attribute of insertScore
        TransactionAttribute transactionAttribute = transactionAttributeSource.getTransactionAttribute(insertScoreMethod, ScoresServiceImpl.class);
        // Print the transaction attribute
        System.out.println(transactionAttribute);
    }
}
