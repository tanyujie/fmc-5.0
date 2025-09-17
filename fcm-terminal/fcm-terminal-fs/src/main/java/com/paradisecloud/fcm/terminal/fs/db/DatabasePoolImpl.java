package com.paradisecloud.fcm.terminal.fs.db;

import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据库连接池实现
 */
public class DatabasePoolImpl implements DatabasePool {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePoolImpl.class);

    /**
     * 空闲连接池
     */
    private ConcurrentHashMap<String, LinkedBlockingQueue<Connection>> idleConnectPools;

    /**
     * 活跃连接池
     */
    private ConcurrentHashMap<String, LinkedBlockingQueue<Connection>> busyConnectPools;

    /**
     * 当前正在被使用的连接数
     */
    private ConcurrentHashMap<String, AtomicInteger> activeSizes;


    /**
     * 保持当前线程连接
     */
    private static final InheritableThreadLocal<Map<String, Connection>> inheritableThreadLocal = new InheritableThreadLocal<>();

    /**
     * 最大连接数
     */
    private final int maxSize;

    public DatabasePoolImpl(int maxSize) {
        this.maxSize = maxSize;
        init();// init pool
    }

    /**
     * 连接池初始化
     */
    @Override
    public void init() {
        if (idleConnectPools == null) {
            idleConnectPools = new ConcurrentHashMap<>();
        }
        if (busyConnectPools == null) {
            busyConnectPools = new ConcurrentHashMap<>();
        }
        if (activeSizes == null) {
            activeSizes = new ConcurrentHashMap<>();
        }
    }

    /**
     * 添加连接地址
     *
     * @param ip
     */
    @Override
    public void addConnectionIp(String ip) {
        if (!idleConnectPools.containsKey(ip)) {
            idleConnectPools.put(ip, new LinkedBlockingQueue<>());
        }
        if (!busyConnectPools.containsKey(ip)) {
            busyConnectPools.put(ip, new LinkedBlockingQueue<>());
        }
        if (!activeSizes.containsKey(ip)) {
            activeSizes.put(ip, new AtomicInteger());
        }
    }

    /**
     * 获取一个连接
     *
     * @return
     */
    @Override
    public Connection getConnection(String ip) {
        FreeSwitchDatabaseManager freeSwitchDatabaseManager = FreeSwitchDatabaseManager.getInstance();
        ip = freeSwitchDatabaseManager.getIp(ip);
        // 获取当前连接
        if (inheritableThreadLocal.get() != null) {
            Map<String, Connection> connectionMap = inheritableThreadLocal.get();
            if (connectionMap.containsKey(ip)) {
                return connectionMap.get(ip);
            }
        }
        Connection connection = null;
        // 从idle池中取出一个连接
        if (idleConnectPools.size() > 0) {
            LinkedBlockingQueue<Connection> idleConnectPool = idleConnectPools.get(ip);
            if (idleConnectPool != null) {
                connection = idleConnectPool.poll();
                if (connection != null) {
                    // 如果有连接，则放入busy池中
                    LinkedBlockingQueue<Connection> busyConnectPool = busyConnectPools.get(ip);
                    if (busyConnectPool == null) {
                        busyConnectPool = new LinkedBlockingQueue<>();
                        busyConnectPools.put(ip, busyConnectPool);
                    }
                    busyConnectPool.offer(connection);
                    LOGGER.info("获取到连接");
                    return connection;
                }
            }
        }

//        synchronized (DatabasePoolImpl.class) { // 锁--效率低下
        // idle池中没有连接
        // 如果idle池中连接未满maxSize，就新建一个连接
        AtomicInteger activeSize = activeSizes.get(ip);
        if (activeSize == null) {
            activeSize = new AtomicInteger();
            activeSizes.put(ip, activeSize);
        }
        if (activeSize.get() < maxSize) {
            if (!freeSwitchDatabaseManager.getFixedUserInfoDbIp().equals(ip)) {
                FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByIp(ip);
                if (fcmBridge == null || !fcmBridge.isAvailable()) {
                    return null;
                }
            }
            // 通过 activeSize.incrementAndGet() <= maxSize 这个判断
            // 解决 if(activeSize.get() < maxSize) 存在的线程安全问题
            if (activeSize.incrementAndGet() <= maxSize) {
                String driverClass = freeSwitchDatabaseManager.getDriverClass(ip);
                String url = freeSwitchDatabaseManager.getUrl(ip);
                String username = freeSwitchDatabaseManager.getUsername(ip);
                String password = freeSwitchDatabaseManager.getPassword(ip);
                connection = DatabaseUtil.createConnection(driverClass, url, username, password);// 创建新连接
                LinkedBlockingQueue<Connection> busyConnectPool = busyConnectPools.get(ip);
                if (busyConnectPool == null) {
                    busyConnectPool = new LinkedBlockingQueue<>();
                    busyConnectPools.put(ip, busyConnectPool);
                }
                if (connection != null) {
                    busyConnectPool.offer(connection);
                }
                return connection;
            }
        }
//        }

        // 如果空闲池中连接数达到maxSize， 则阻塞等待归还连接
        try {
            LOGGER.info("排队等待连接");
            LOGGER.info("idle:" + idleConnectPools.get(ip).size());
            LOGGER.info("activeSize:" + activeSizes.get(ip).get());
            LOGGER.info("maxSize:" + maxSize);
            LinkedBlockingQueue<Connection> idleConnectPool = idleConnectPools.get(ip);
            connection = idleConnectPool.poll(10000, TimeUnit.MILLISECONDS);// 阻塞获取连接，如果10秒内有其他连接释放，
            if (connection == null) {
                LOGGER.info("等待超时");
                throw new RuntimeException("等待连接超时");
            }
            LOGGER.info("等待到了一个连接");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 开始事务（事务时候使用）
     *
     * @return
     */
    @Override
    public void startTransaction() {
        FreeSwitchDatabaseManager freeSwitchDatabaseManager = FreeSwitchDatabaseManager.getInstance();
        if (freeSwitchDatabaseManager.canTransaction()) {
            if (idleConnectPools.size() > 0) {
                Map<String, Connection> connectionMap = new HashMap<>();
                if (freeSwitchDatabaseManager.getFixedUserInfoDbIp().equals(freeSwitchDatabaseManager.getUserinfoDbIp(""))) {
                    String ip = freeSwitchDatabaseManager.getFixedUserInfoDbIp();
                    Connection connection = getConnection(ip);
                    if (connection != null) {
                        connectionMap.put(ip, connection);
                    }
                } else {
                    for (String ip : idleConnectPools.keySet()) {
                        Connection connection = getConnection(ip);
                        if (connection != null) {
                            connectionMap.put(ip, connection);
                        }
                    }
                }
                inheritableThreadLocal.set(connectionMap);
            }
        }
    }

    /**
     * 结束事务（事务时候使用）
     */
    @Override
    public void endTransaction() {
        endTransaction(false);
    }

    /**
     * 结束事务（事务时候使用）
     */
    @Override
    public void endTransaction(boolean dropConnection) {
        if (inheritableThreadLocal.get() != null) {
            Map<String, Connection> connectionMap = inheritableThreadLocal.get();
            for (String ip : connectionMap.keySet()) {
                Connection connection = connectionMap.get(ip);
                releaseConnection(ip, connection, dropConnection);
            }
        }
    }

    /**
     * 是否是一个事务
     *
     * @return
     */
    @Override
    public boolean isTransaction() {
        if (inheritableThreadLocal.get() != null) {
            Map<String, Connection> connectionMap = inheritableThreadLocal.get();
            if (connectionMap.size() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是一个事务
     *
     * @return
     */
    @Override
    public Map<String, Connection> getTransactionConnections() {
        if (inheritableThreadLocal.get() != null) {
            return inheritableThreadLocal.get();
        }
        return null;
    }

    /**
     * 释放一个连接
     *
     * @param ip
     * @param connection
     */
    @Override
    public void releaseConnection(String ip, Connection connection) {
        releaseConnection(ip, connection, false);
    }

    /**
     * 释放一个连接
     *
     * @param ip
     * @param connection
     * @param dropConnection 丢弃连接
     */
    @Override
    public void releaseConnection(String ip, Connection connection, boolean dropConnection) {
        if (connection != null) {
            LinkedBlockingQueue<Connection> busyConnectPool = busyConnectPools.get(ip);
            if (busyConnectPool != null) {
                busyConnectPool.remove(connection);
            }
            if (dropConnection) {
                try {
                    connection.close();
                } catch (Exception e1) {
                }
                AtomicInteger activeSize = activeSizes.get(ip);
                if (activeSize != null) {
                    if (activeSize.get() > 0) {
                        activeSize.decrementAndGet();
                    }
                }
                return;
            }
            LinkedBlockingQueue<Connection> idleConnectPool = idleConnectPools.get(ip);
            if (idleConnectPool != null) {
                idleConnectPool.offer(connection);
            } else {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 销毁连接池
     */
    @Override
    public void destroy() {
    }

    /**
     * 定时对连接进行健康检查
     * 注意：只能对idle连接池中的连接进行健康检查，
     * 不可以对busyConnectPool连接池中的连接进行健康检查，因为它正在被客户端使用;
     */
    @Scheduled(fixedRate = 60000)
    public void check() {
        for (String ip : idleConnectPools.keySet()) {
            if (FreeSwitchDatabaseManager.getInstance().isValidDatabase(ip)) {
                LinkedBlockingQueue<Connection> idleConnectPool = idleConnectPools.get(ip);
                if (idleConnectPool == null) {
                    idleConnectPool = new LinkedBlockingQueue<>();
                }
                for (int i = 0; i < idleConnectPool.size(); i++) {
                    Connection connection = idleConnectPool.poll();
                    boolean isCreateNew = false;
                    try {
                        boolean valid = connection.isValid(2000);
                        if (!valid) {
                            // 如果连接不可用，则创建一个新的连接
                            isCreateNew = true;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        isCreateNew = true;
                    }
                    if (isCreateNew) {
                        // 如果连接不可用，则创建一个新的连接
                        FreeSwitchDatabaseManager freeSwitchDatabaseManager = FreeSwitchDatabaseManager.getInstance();
                        String driverClass = freeSwitchDatabaseManager.getDriverClass(ip);
                        String url = freeSwitchDatabaseManager.getUrl(ip);
                        String username = freeSwitchDatabaseManager.getUsername(ip);
                        String password = freeSwitchDatabaseManager.getPassword(ip);
                        connection = DatabaseUtil.createConnection(driverClass, url, username, password);// 创建新连接
                    }
                    idleConnectPool.offer(connection);// 放进一个可用的连接
                }
            } else {
                LinkedBlockingQueue<Connection> idleConnectPool = idleConnectPools.remove(ip);
                if (idleConnectPool != null) {
                    for (int i = 0; i < idleConnectPool.size(); i++) {
                        Connection connection = idleConnectPool.poll();
                        if (connection != null) {
                            try {
                                connection.close();
                            } catch (Exception e) {
                            }
                        }
                    }
                }
                activeSizes.remove(ip);
            }
        }
    }
}
