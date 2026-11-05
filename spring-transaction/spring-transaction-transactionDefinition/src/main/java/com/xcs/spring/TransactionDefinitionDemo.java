package com.xcs.spring;

import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionDefinitionDemo {

    public static void main(String[] args) {
        // Create a DefaultTransactionDefinition instance
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();

        // Set the propagation behavior to PROPAGATION_REQUIRES_NEW
        transactionDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
        System.out.println("Propagation Behavior: " + transactionDefinition.getPropagationBehavior());

        // Set the isolation level to ISOLATION_REPEATABLE_READ
        transactionDefinition.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_REPEATABLE_READ);
        System.out.println("Isolation Level: " + transactionDefinition.getIsolationLevel());

        // Set the transaction timeout to 30 seconds
        transactionDefinition.setTimeout(30);
        System.out.println("Timeout: " + transactionDefinition.getTimeout());

        // Set the transaction to read-only
        transactionDefinition.setReadOnly(true);
        System.out.println("Is Read Only: " + transactionDefinition.isReadOnly());

        // Set the transaction name to "DemoTransaction"
        transactionDefinition.setName("DemoTransaction");
        System.out.println("Transaction Name: " + transactionDefinition.getName());
    }
}
