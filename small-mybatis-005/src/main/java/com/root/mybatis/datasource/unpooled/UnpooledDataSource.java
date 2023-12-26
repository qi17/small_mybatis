package com.root.mybatis.datasource.unpooled;

import cn.hutool.core.util.StrUtil;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author root
 * @description
 * @date 2023/10/15
 */
public class UnpooledDataSource implements DataSource {

    private ClassLoader classLoader;
    //驱动配置 可扩展属性信息
    private Properties properties;
    private static Map<String, Driver> registeredDrivers = new ConcurrentHashMap<>();
    //数据库驱动路径名
    private String driver;

    //数据库连接信息
    private String url;
    private String userName;
    private String password;

    //自动提交
    private Boolean autoCommit;
    //事务隔离级别
    private Integer defaultTransactionIsolation;

    static {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            registeredDrivers.put(driver.getClass().getName(), driver);
        }
    }

        /**
         * 驱动代理
         */
    static class DriverProxy implements Driver {
        private Driver driver;

        public DriverProxy(Driver driver) {
            this.driver = driver;
        }

        @Override
        public Connection connect(String url, Properties info) throws SQLException {
            return this.driver.connect(url, info);
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
            return this.driver.acceptsURL(url);
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
            return this.driver.getPropertyInfo(url, info);
        }

        @Override
        public int getMajorVersion() {
            return 0;
        }

        @Override
        public int getMinorVersion() {
            return this.driver.getMinorVersion();
        }

        @Override
        public boolean jdbcCompliant() {
            return this.driver.jdbcCompliant();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        }
    }



    /**
     * 初始化驱动
     *
     * @return
     * @throws SQLException
     */
    private synchronized void initializerDriver() throws SQLException {
        if (!registeredDrivers.containsKey(driver)) {
            //加载注册驱动
            try {
                Class<?> driverType = Class.forName(driver, true, classLoader);
                Driver driverInstance = (Driver) driverType.newInstance();
                //使用自定义的Driver代理驱动
                DriverManager.registerDriver(new DriverProxy(driverInstance));
                registeredDrivers.put(driver, driverInstance);
            } catch (Exception e) {
                throw new SQLException("Error setting driver on UnpooledDataSource. Cause: " + e);
            }
        }
    }

    private Connection doGetConnection(String userName, String password) throws SQLException {
        Properties props = new Properties();
        if (properties != null) {
            props.putAll(properties);
        }
        if (StrUtil.isNotEmpty(userName)) {
            props.put("user", userName);
        }
        if (StrUtil.isNotEmpty(password)) {
            props.put("password", password);
        }
        return doGetConnection(props);
    }


    /**
     * 获取数据库连接
     * @param props
     * @return
     * @throws SQLException
     */
    private Connection doGetConnection(Properties props) throws SQLException {
        //初始化驱动
        initializerDriver();
        Connection connection = DriverManager.getConnection(url, props);
        if (autoCommit != null && autoCommit != connection.getAutoCommit()) {
            connection.setAutoCommit(autoCommit);
        }
        if (defaultTransactionIsolation != null) {
            connection.setTransactionIsolation(defaultTransactionIsolation);
        }
        return connection;
    }




    @Override
    public Connection getConnection() throws SQLException {
        return doGetConnection(userName,password);
    }

    @Override
    public Connection getConnection(String u, String p) throws SQLException {
        return doGetConnection(u,p);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() +"is not a wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public static Map<String, Driver> getRegisteredDrivers() {
        return registeredDrivers;
    }

    public static void setRegisteredDrivers(Map<String, Driver> registeredDrivers) {
        UnpooledDataSource.registeredDrivers = registeredDrivers;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(Boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public Integer getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

    public void setDefaultTransactionIsolation(Integer defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }
}
