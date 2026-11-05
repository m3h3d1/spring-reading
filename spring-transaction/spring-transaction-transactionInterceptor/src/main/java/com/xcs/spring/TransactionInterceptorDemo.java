package com.xcs.spring;

import com.mysql.jdbc.Driver;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;

public class TransactionInterceptorDemo {

    public static void main(String[] args) throws Exception {
        // Database URL, format: jdbc:driver-name://host:port/database-name
        String url = "jdbc:mysql://localhost:3306/spring-reading";
        // Database username
        String username = "root";
        // Database password
        String password = "123456";

        // Create a SimpleDriverDataSource to manage the data source
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource(new Driver(), url, username, password);
        // Create a DataSourceTransactionManager to manage transactions
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        // Create a JdbcTemplate to run SQL statements
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        // Create an AnnotationTransactionAttributeSource to get transaction attributes
        AnnotationTransactionAttributeSource transactionAttributeSource = new AnnotationTransactionAttributeSource();

        // Create a TransactionInterceptor to intercept method calls and manage transactions
        TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
        transactionInterceptor.setTransactionAttributeSource(transactionAttributeSource);
        transactionInterceptor.setTransactionManager(transactionManager);

        // Create a BeanFactoryTransactionAttributeSourceAdvisor to set up the transaction interceptor
        BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        advisor.setTransactionAttributeSource(transactionAttributeSource);
        advisor.setAdvice(transactionInterceptor);

        // Create a ProxyFactory to create the proxy
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.addAdvisor(advisor);
        proxyFactory.setTarget(new ScoresServiceImpl(jdbcTemplate));

        // Get the proxy and call its method
        ScoresService scoresService = (ScoresService) proxyFactory.getProxy();
        scoresService.insertScore();
    }
}
