package com.paradisecloud.fcm.terminal.fs.db;

import java.sql.Connection;
import java.util.Map;

/**
 * 数据库连接池接口
 */
public interface DatabasePool {

    /**
     * 连接池初始化
     */
    void init();

    /**
     * 添加连接地址
     */
    void addConnectionIp(String ip);

    /**
     * 获取一个连接
     *
     * @param ip
     * @return
     */
    Connection getConnection(String ip);

    /**
     * 开始事务
     *
     * @return
     */
    void startTransaction();

    /**
     * 结束事务
     *
     * @return
     */
    void endTransaction();

    /**
     * 结束事务
     *
     * @param dropConnection 丢弃连接
     * @return
     */
    void endTransaction(boolean dropConnection);


    /**
     * 是否是一个事务
     *
     * @return
     */
    boolean isTransaction();

    /**
     * 获取一个事务连接
     *
     * @return
     */
     Map<String, Connection> getTransactionConnections();

    /**
     * 释放一个连接
     * @param ip
     * @param connection
     */
    void releaseConnection(String ip, Connection connection);

    /**
     * 释放一个连接
     * @param ip
     * @param connection
     * @param dropConnection 丢弃连接
     */
    void releaseConnection(String ip, Connection connection, boolean dropConnection);

    /**
     * 销毁连接池
     */
    void destroy();
}