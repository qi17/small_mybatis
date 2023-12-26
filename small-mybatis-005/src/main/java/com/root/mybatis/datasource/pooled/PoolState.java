package com.root.mybatis.datasource.pooled;


import java.util.ArrayList;
import java.util.List;

/**
 * @author root
 * @description
 * @date 2023/10/15
 */
public class PoolState {

    protected PooledDataSource dataSource;
    //空闲链接
    protected final List<PooledConnection> idleConnections = new ArrayList<>();
    //活跃链接
    protected final List<PooledConnection> activeConnections = new ArrayList<>();
    // 请求次数
    protected long requestCount = 0;
    // 总请求时间
    protected long accumulatedRequestTime = 0;
    protected long accumulatedCheckoutTime = 0;
    protected long claimedOverdueConnectionCount = 0;
    protected long accumulatedCheckoutTimeOfOverdueConnections = 0;

    // 总等待时间
    protected long accumulatedWaitTime = 0;
    // 要等待的次数
    protected long hadToWaitCount = 0;
    // 失败连接次数
    protected long badConnectionCount = 0;

    public PoolState(PooledDataSource dataSource) {
        this.dataSource = dataSource;
    }



    public PooledDataSource getDatasource() {
        return dataSource;
    }

    public void setDatasource(PooledDataSource datasource) {
        this.dataSource = datasource;
    }

    public synchronized List<PooledConnection> getIdleConnections() {
        return idleConnections;
    }

    public synchronized List<PooledConnection> getActiveConnections() {
        return activeConnections;
    }

    public synchronized long getRequestCount() {
        return requestCount;
    }

    public synchronized void setRequestCount(long requestCount) {
        this.requestCount = requestCount;
    }

    public synchronized long getAccumulatedRequestTime() {
        return accumulatedRequestTime;
    }

    public synchronized void setAccumulatedRequestTime(long accumulatedRequestTime) {
        this.accumulatedRequestTime = accumulatedRequestTime;
    }

    public synchronized long getAccumulatedCheckoutTime() {
        return accumulatedCheckoutTime;
    }

    public synchronized void setAccumulatedCheckoutTime(long accumulatedCheckoutTime) {
        this.accumulatedCheckoutTime = accumulatedCheckoutTime;
    }

    public synchronized long getClaimedOverdueConnectionCount() {
        return claimedOverdueConnectionCount;
    }

    public synchronized void setClaimedOverdueConnectionCount(long claimedOverdueConnectionCount) {
        this.claimedOverdueConnectionCount = claimedOverdueConnectionCount;
    }

    public synchronized long getAccumulatedCheckoutTimeOfOverdueConnections() {
        return accumulatedCheckoutTimeOfOverdueConnections;
    }

    public synchronized void setAccumulatedCheckoutTimeOfOverdueConnections(long accumulatedCheckoutTimeOfOverdueConnections) {
        this.accumulatedCheckoutTimeOfOverdueConnections = accumulatedCheckoutTimeOfOverdueConnections;
    }

    public synchronized long getAccumulatedWaitTime() {
        return accumulatedWaitTime;
    }

    public synchronized void setAccumulatedWaitTime(long accumulatedWaitTime) {
        this.accumulatedWaitTime = accumulatedWaitTime;
    }

    public synchronized long getHadToWaitCount() {
        return hadToWaitCount;
    }

    public synchronized void setHadToWaitCount(long hadToWaitCount) {
        this.hadToWaitCount = hadToWaitCount;
    }

    public synchronized long getBadConnectionCount() {
        return badConnectionCount;
    }

    public synchronized void setBadConnectionCount(long badConnectionCount) {
        this.badConnectionCount = badConnectionCount;
    }



}
