package com.xcs.spring;

import org.springframework.aop.TargetSource;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Connection pool target source that manages a connection pool of custom connection objects.
 *
 * @author xcs
 * @date 2024-04-09 15:26
 */
public class ConnectionPoolTargetSource implements TargetSource {

    /**
     * Connection pool
     */
    private final BlockingQueue<MyConnection> connectionPool;

    /**
     * Constructor that initializes the connection pool.
     *
     * @param poolSize the pool size
     */
    public ConnectionPoolTargetSource(int poolSize) {
        this.connectionPool = new ArrayBlockingQueue<>(poolSize);
        initializeConnectionPool(poolSize);
    }

    /**
     * Initializes the connection pool and fills it with connection objects.
     *
     * @param poolSize the pool size
     */
    private void initializeConnectionPool(int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            MyConnection connection = new MyConnection("Connection" + i);
            connectionPool.offer(connection);
        }
    }

    /**
     * Gets the type of the target class.
     *
     * @return the type of the target class
     */
    @Override
    public Class<?> getTargetClass() {
        return MyConnection.class;
    }

    /**
     * Checks whether the target object is static.
     *
     * @return true if the target object is static, otherwise false
     */
    @Override
    public boolean isStatic() {
        return false;
    }

    /**
     * Gets a connection object.
     *
     * @return the connection object
     * @throws Exception if an exception occurs while getting the connection object
     */
    @Override
    public Object getTarget() throws Exception {
        return connectionPool.take();
    }

    /**
     * Releases a connection object.
     *
     * @param target the connection object to release
     * @throws Exception if an exception occurs while releasing the connection object
     */
    @Override
    public void releaseTarget(Object target) throws Exception {
        if (target instanceof MyConnection) {
            connectionPool.offer((MyConnection) target);
        }
    }
}
