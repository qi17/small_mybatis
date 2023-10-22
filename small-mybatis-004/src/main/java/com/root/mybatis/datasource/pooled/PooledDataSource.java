package com.root.mybatis.datasource.pooled;

import com.root.mybatis.datasource.unpooled.UnpooledDataSource;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.logging.Logger;

/**
 * @author root
 * @description
 * @date 2023/10/15
 */

public class PooledDataSource implements DataSource {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(PooledDataSource.class);

    private final PoolState state = new PoolState(this);
    private final UnpooledDataSource dataSource;
    // 活跃连接数
    protected int poolMaximumActiveConnections = 10;
    // 空闲连接数
    protected int poolMaximumIdleConnections = 5;
    // 在被强制返回之前,池中连接被检查的时间
    protected int poolMaximumCheckoutTime = 20000;
    // 这是给连接池一个打印日志状态机会的低层次设置,还有重新尝试获得连接, 这些情况下往往需要很长时间 为了避免连接池没有配置时静默失败)。
    protected int poolTimeToWait = 20000;
    // 发送到数据的侦测查询,用来验证连接是否正常工作,并且准备 接受请求。默认是“NO PING QUERY SET” ,这会引起许多数据库驱动连接 由一个错误信息而导致失败
    protected String poolPingQuery = "NO PING QUERY SET";
    // 开启或禁用侦测查询
    protected boolean poolPingEnabled = false;
    // 用来配置 poolPingQuery 多次时间被用一次
    protected int poolPingConnectionsNotUsedFor = 0;

    private int expectedConnectionTypeCode;


    public PooledDataSource() {
        this.dataSource = new UnpooledDataSource();
    }

    /**
     * 回收链接 如果连接充足则新建 不充足则继续使用池中链接
     *
     * @param connection connection
     * @throws SQLException
     */
    protected void pushConnection(PooledConnection connection) throws SQLException {
        synchronized (state) {
            state.activeConnections.remove(connection);
            if (connection.isValid()) {
                //空闲链接不足 创建新的链接
                if (state.idleConnections.size() < poolMaximumIdleConnections && connection.getConnectionTypeCode() == expectedConnectionTypeCode) {
                    state.accumulatedCheckoutTime += connection.getCheckoutTimestamp();
                    // 它首先检查数据库连接是否处于自动提交模式，如果不是，则调用rollback()方法执行回滚操作。
                    if (!connection.getRealConnection().getAutoCommit()) {
                        // 在MyBatis中，如果没有开启自动提交模式，则需要手动提交或回滚事务。因此，这段代码可能是在确保操作完成后，如果没有开启自动提交模式，则执行回滚操作。
                        // 总的来说，这段代码用于保证数据库的一致性，确保操作完成后，如果未开启自动提交模式，则执行回滚操作。
                        connection.getRealConnection().rollback();
                    }
                    //实例化新的db连接 加入到空闲列表
                    PooledConnection newConnection = new PooledConnection(connection.getRealConnection(), this);
                    state.idleConnections.add(newConnection);
                    newConnection.setCreatedTimestamp(connection.getCreatedTimestamp());
                    newConnection.setLastUsedTimestamp(connection.getLastUsedTimestamp());
                    connection.invalidate();
                    logger.info("Returned connection " + newConnection.getRealHashCode() + " to pool.");
                    //唤醒线程 可以抢夺链接
                    state.notifyAll();
                }
                //空闲链接充足 不需要回收 直接关闭
                else {
                    state.accumulatedCheckoutTime += connection.getCheckoutTime();
                    if (!connection.getRealConnection().getAutoCommit()) {
                        connection.getRealConnection().rollback();
                    }
                    connection.getRealConnection().close();
                    logger.info("Closed connection " + connection.getRealHashCode() + ".");
                    connection.invalidate();
                }
            } else {
                logger.info("A bad connection (" + connection.getRealHashCode() + ") attempted to return to the pool, discarding connection.");
                state.badConnectionCount++;
            }
        }
    }

    /**
     * 从池中获取连接
     * @param username
     * @param password
     * @return
     * @throws SQLException
     */
    private PooledConnection popConnection(String username, String password) throws SQLException {
        boolean countedWait = false;
        PooledConnection connection = null;
        long t = System.currentTimeMillis();
        int localBadConnectionCount = 0;
        while (connection == null) {
            synchronized (state) {
                //先从空闲连接中获取
                if (!state.idleConnections.isEmpty()) {
                    connection = state.getIdleConnections().remove(0);
                    logger.info("Checked out connection " + connection.getRealHashCode() + " from pool.");
                } else {
                    //空闲连接用完 判断已激活链接是否已满
                    if (state.activeConnections.size() < poolMaximumActiveConnections) {
                        connection = new PooledConnection(dataSource.getConnection(), this);
                        logger.info("Created connection " + connection.getRealHashCode() + ".");
                    } else {
                        //失效最老活跃连接
                        PooledConnection oldestConnection = state.activeConnections.get(0);
                        long checkoutTime = oldestConnection.getCheckoutTime();
                        //如果检查时间超时 这过期这个链接 使用新连接替换
                        if (checkoutTime > poolMaximumCheckoutTime) {
                            state.claimedOverdueConnectionCount++;
                            state.accumulatedCheckoutTimeOfOverdueConnections += checkoutTime;
                            state.accumulatedCheckoutTime += checkoutTime;
                            state.activeConnections.remove(oldestConnection);
                            if (!oldestConnection.getRealConnection().getAutoCommit()) {
                                oldestConnection.getRealConnection().rollback();
                            }
                            connection = new PooledConnection(oldestConnection.getRealConnection(), this);
                            oldestConnection.invalidate();
                            logger.info("Claimed overdue connection " + connection.getRealHashCode() + ".");
                        }
                        //否则等待
                        else {
                            try {
                                if (!countedWait) {
                                    state.hadToWaitCount++;
                                    countedWait = true;
                                }
                                logger.info("Waiting as long as " + poolTimeToWait + " milliseconds for connection.");
                                long wt = System.currentTimeMillis();
                                state.wait(poolTimeToWait);
                                state.accumulatedWaitTime += System.currentTimeMillis() - wt;
                            } catch (InterruptedException e) {
                                break;
                            }

                        }
                    }
                }
                //获取到连接 设置连接信息
                if (connection != null) {
                    if (connection.isValid()) {
                        if (!connection.getRealConnection().getAutoCommit()) {
                            connection.getRealConnection().rollback();
                        }
                        connection.setConnectionTypeCode(assembleConnectionTypeCode(dataSource.getUrl(), username, password));
                        // 记录checkout时间
                        connection.setCheckoutTimestamp(System.currentTimeMillis());
                        connection.setLastUsedTimestamp(System.currentTimeMillis());
                        state.activeConnections.add(connection);
                        state.requestCount++;
                        state.accumulatedRequestTime += System.currentTimeMillis() - t;
                    } else {
                        logger.info("A bad connection (" + connection.getRealHashCode() + ") was returned from the pool, getting another connection.");
                        // 如果没拿到 while循环重新获取
                        state.badConnectionCount++;
                        localBadConnectionCount++;
                        connection = null;
                        // 失败次数较多，抛异常
                        if (localBadConnectionCount > (poolMaximumIdleConnections + 3)) {
                            logger.debug("PooledDataSource: Could not get a good connection to the database.");
                            throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
                        }
                    }
                }
            }
        }
        if (connection == null) {
            logger.debug("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
            throw new SQLException("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
        }

        return connection;

    }

    private void forceCloseAll() {
        synchronized (state) {
            expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUserName(), dataSource.getPassword());
            for (int i = state.getActiveConnections().size(); i > 0; i--) {
                try {
                    PooledConnection conn = state.getActiveConnections().remove(i - 1);
                    conn.invalidate();
                    Connection realConn = conn.getRealConnection();
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }
                    realConn.close();
                } catch (Exception e) {

                }
            }
            for (int i = state.getIdleConnections().size(); i >0 ; i--) {
                try {
                    PooledConnection idleConn = state.getActiveConnections().remove(i - 1);
                    idleConn.invalidate();
                    Connection realConnection = idleConn.getRealConnection();
                    if(!realConnection.getAutoCommit()){
                        realConnection.rollback();
                    }
                    realConnection.close();
                }catch (Exception e) {

                }

            }
            logger.info("PooledDataSource forcefully closed/removed all connections.");
        }
    }


    protected boolean pingConnection(PooledConnection conn) {
        boolean result = true;
        try {
            result = !conn.getRealConnection().isClosed();
        } catch (SQLException e) {
            logger.info("Connection " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
            result = false;
        }
        if (result) {
            if (poolPingEnabled) {
                if (poolPingConnectionsNotUsedFor >= 0 && conn.getTimeElapsedSinceLastUse() > poolPingConnectionsNotUsedFor) {
                    try {
                        logger.info("Testing connection " + conn.getRealHashCode() + " ...");
                        Connection realConn = conn.getRealConnection();
                        Statement statement = realConn.createStatement();
                        ResultSet resultSet = statement.executeQuery(poolPingQuery);
                        resultSet.close();
                        if (!realConn.getAutoCommit()) {
                            realConn.rollback();
                        }
                        result = true;
                        logger.info("Connection " + conn.getRealHashCode() + " is GOOD!");
                    } catch (Exception e) {
                        logger.info("Execution of ping query '" + poolPingQuery + "' failed: " + e.getMessage());
                        try {
                            conn.getRealConnection().close();
                        } catch (SQLException ignore) {
                        }
                        result = false;
                        logger.info("Connection " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
                    }
                }
            }
        }

        return result;
    }


    private int assembleConnectionTypeCode(String url, String username, String password) {
        return ("" + url + username + password).hashCode();
    }

    public static Connection unwrapConnection(Connection conn) {
        if (Proxy.isProxyClass(conn.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(conn);
            if (handler instanceof PooledConnection) {
                return ((PooledConnection) handler).getRealConnection();
            }
        }
        return conn;
    }

    @Override
    protected void finalize() throws Throwable {
        forceCloseAll();
        super.finalize();
    }


    public void setDriver(String driver) {
        dataSource.setDriver(driver);
        forceCloseAll();
    }

    public void setUrl(String url) {
        dataSource.setUrl(url);
        forceCloseAll();
    }

    public void setUsername(String username) {
        dataSource.setUserName(username);
        forceCloseAll();
    }

    public void setPassword(String password) {
        dataSource.setPassword(password);
        forceCloseAll();
    }



    public int getPoolMaximumActiveConnections() {
        return poolMaximumActiveConnections;
    }

    public void setPoolMaximumActiveConnections(int poolMaximumActiveConnections) {
        this.poolMaximumActiveConnections = poolMaximumActiveConnections;
    }

    public int getPoolMaximumIdleConnections() {
        return poolMaximumIdleConnections;
    }

    public void setPoolMaximumIdleConnections(int poolMaximumIdleConnections) {
        this.poolMaximumIdleConnections = poolMaximumIdleConnections;
    }

    public int getPoolMaximumCheckoutTime() {
        return poolMaximumCheckoutTime;
    }

    public void setPoolMaximumCheckoutTime(int poolMaximumCheckoutTime) {
        this.poolMaximumCheckoutTime = poolMaximumCheckoutTime;
    }

    public int getPoolTimeToWait() {
        return poolTimeToWait;
    }

    public void setPoolTimeToWait(int poolTimeToWait) {
        this.poolTimeToWait = poolTimeToWait;
    }

    public String getPoolPingQuery() {
        return poolPingQuery;
    }

    public void setPoolPingQuery(String poolPingQuery) {
        this.poolPingQuery = poolPingQuery;
    }

    public boolean isPoolPingEnabled() {
        return poolPingEnabled;
    }

    public void setPoolPingEnabled(boolean poolPingEnabled) {
        this.poolPingEnabled = poolPingEnabled;
    }

    public int getPoolPingConnectionsNotUsedFor() {
        return poolPingConnectionsNotUsedFor;
    }

    public void setPoolPingConnectionsNotUsedFor(int poolPingConnectionsNotUsedFor) {
        this.poolPingConnectionsNotUsedFor = poolPingConnectionsNotUsedFor;
    }

    public int getExpectedConnectionTypeCode() {
        return expectedConnectionTypeCode;
    }

    public void setExpectedConnectionTypeCode(int expectedConnectionTypeCode) {
        this.expectedConnectionTypeCode = expectedConnectionTypeCode;
    }


    @Override
    public Connection getConnection() throws SQLException {
        return popConnection(dataSource.getUserName(), dataSource.getPassword()).getProxyConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return popConnection(username, password).getProxyConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

}
