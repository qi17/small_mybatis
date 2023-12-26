package com.root.mybatis.datasource.pooled;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author root
 * @description
 * @date 2023/10/15
 */
public class PooledConnection implements InvocationHandler {

    private static final String CLOSE = "close";
    private static final Class<?>[] IFACES = new Class<?>[]{Connection.class};

    private int hashCode = 0;
    private PooledDataSource dataSource;

    // 真实的连接
    private Connection realConnection;
    // 代理的连接
    private Connection proxyConnection;

    private long checkoutTimestamp;
    private long createdTimestamp;
    private long lastUsedTimestamp;
    private int connectionTypeCode;
    private boolean valid;

    public PooledConnection(Connection connection,PooledDataSource dataSource) {
        this.hashCode = connection.hashCode();
        this.dataSource = dataSource;
        this.realConnection = connection;
        this.proxyConnection =(Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),IFACES,this);
        this.createdTimestamp = System.currentTimeMillis();
        this.lastUsedTimestamp = System.currentTimeMillis();
        this.valid = true;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        //关闭连接归还连接到池中 使用hashcode提高效率
        if (CLOSE.hashCode() == name.hashCode() && CLOSE.equals(name)){
                dataSource.pushConnection(this);
                return null;
        }else {
            if(!Object.class.equals(method.getDeclaringClass())){
                checkoutConnection();
            }
            return method.invoke(realConnection,args);
        }
    }

    private void checkoutConnection() throws SQLException {
        if(!valid){
            throw new SQLException("error accessing PooledConnection. Connection is invalid.");
        }
    }

    @Override
    public int hashCode(){
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PooledConnection){
            return realConnection.hashCode() == ((PooledConnection)o).realConnection.hashCode();
        }else if (o instanceof Connection) {
            return hashCode == o.hashCode();
        }else {
            return false;
        }
    }

    public PooledDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(PooledDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getRealConnection() {
        return realConnection;
    }

    public void setRealConnection(Connection realConnection) {
        this.realConnection = realConnection;
    }

    public Connection getProxyConnection() {
        return proxyConnection;
    }

    public int getRealHashCode() {
        return realConnection == null ? 0 : realConnection.hashCode();
    }

    public void setProxyConnection(Connection proxyConnection) {
        this.proxyConnection = proxyConnection;
    }

    public long getCheckoutTimestamp() {
        return System.currentTimeMillis() - checkoutTimestamp ;
    }

    public void setCheckoutTimestamp(long timestamp) {
        this.checkoutTimestamp = timestamp;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public long getLastUsedTimestamp() {
        return lastUsedTimestamp;
    }

    public long getCheckoutTime() {
        return System.currentTimeMillis() - checkoutTimestamp;
    }

    public long getTimeElapsedSinceLastUse() {
        return System.currentTimeMillis() - lastUsedTimestamp;
    }

    public long getAge() {
        return System.currentTimeMillis() - createdTimestamp;
    }


    public void setLastUsedTimestamp(long lastUsedTimestamp) {
        this.lastUsedTimestamp = lastUsedTimestamp;
    }

    public int getConnectionTypeCode() {
        return connectionTypeCode;
    }

    public void setConnectionTypeCode(int connectionTypeCode) {
        this.connectionTypeCode = connectionTypeCode;
    }

    public boolean isValid() {
        return valid && realConnection != null && dataSource.pingConnection(this);
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * 无效
     */
    public void invalidate() {
        valid = false;
    }


}
