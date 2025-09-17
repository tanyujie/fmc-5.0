package com.paradisecloud.fcm.terminal.fs.db;

import com.jcraft.jsch.UserInfo;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Free Switch数据库连接管理器
 */
public class FreeSwitchDatabaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(FreeSwitchDatabaseManager.class);

    private String driverClass;
    private String url;
    private String username;
    private String password;
    private boolean transactionEnable;

    private static final String USERINFO_DB_IP = "userinfo_db_ip";
    private boolean userinfo_db_enable;
    private boolean userinfo_db_useFcmDb;
    private String userinfo_db_driverClass;
    private String userinfo_db_ip;
    private String userinfo_db_url;
    private String userinfo_db_username;
    private String userinfo_db_password;

    private Map<String, Object> databases;

    private FreeSwitchDatabaseManager() {
    }

    /**
     * 只在程序启动时调用
     *
     * @param driverClass
     * @param url
     * @param username
     * @param password
     * @return
     */
    public static void init(String driverClass, String url, String username, String password, boolean transactionEnable,
                            boolean userinfo_db_enable, boolean userinfo_db_useFcmDb, String userinfo_db_driverClass, String userinfo_db_ip, String userinfo_db_url, String userinfo_db_username, String userinfo_db_password) {
        FreeSwitchDatabaseManager instance = InnerClass.INSTANCE;
        if (!StringUtils.hasText(instance.driverClass)) {
            instance.driverClass = driverClass;
            instance.url = url;
            instance.username = username;
            instance.password = password;
            instance.transactionEnable = transactionEnable;
            instance.userinfo_db_enable = userinfo_db_enable;
            instance.userinfo_db_useFcmDb = userinfo_db_useFcmDb;
            instance.userinfo_db_driverClass = userinfo_db_driverClass;
            instance.userinfo_db_ip = userinfo_db_ip;
            String fsUserInfoDbIp = ExternalConfigCache.getInstance().getFsUserInfoDbIp();
            if (StringUtils.hasText(fsUserInfoDbIp)) {
                instance.userinfo_db_ip = fsUserInfoDbIp;
            }
            instance.userinfo_db_url = userinfo_db_url.replace("{ip}", instance.userinfo_db_ip);
            instance.userinfo_db_username = userinfo_db_username;
            instance.userinfo_db_password = userinfo_db_password;
            instance.databases = new ConcurrentHashMap<>();
            if (!url.contains("{ip}")) {
                String[] urlArr = url.split(":", -1);
                if (urlArr != null && urlArr.length >= 3) {
                    if (urlArr[2].startsWith("//")) {
                        String ip = urlArr[2].substring(2);
                        instance.databases.put(ip, ip);
                    }
                }
            }
            if (userinfo_db_enable) {
                if (!userinfo_db_useFcmDb) {
                    instance.databases.put(USERINFO_DB_IP, instance.userinfo_db_ip);
                    instance.addConnectionIp(USERINFO_DB_IP);
                }
            }
            Map<Long, FcmBridge> fcmBridgeMap = FcmBridgeCache.getInstance().getFcmBridgeMap();
            for (Long key : fcmBridgeMap.keySet()) {
                instance.addConnectionIp(fcmBridgeMap.get(key).getBusiFreeSwitch().getIp());
            }
        }
    }

    public static FreeSwitchDatabaseManager getInstance() {
        FreeSwitchDatabaseManager instance = InnerClass.INSTANCE;
        return instance;
    }

    private static class InnerClass {
        private final static FreeSwitchDatabaseManager INSTANCE = new FreeSwitchDatabaseManager();
    }

    public String getDriverClass(String ip) {
        if (USERINFO_DB_IP.equals(ip)) {
            return userinfo_db_driverClass;
        }
        return driverClass;
    }

    public String getUrl(String ip) {
        if (USERINFO_DB_IP.equals(ip)) {
            return userinfo_db_url;
        }
        if (url.contains("{ip}")) {
            return url.replace("{ip}", ip);
        }
        return url;
    }

    public String getIp(String ip) {
        if (USERINFO_DB_IP.equals(ip)) {
            return ip;
        }
        if (!url.contains("{ip}")) {
            for (String ipStr : databases.keySet()) {
                ip = ipStr;
            }
        }
        return ip;
    }

    public String getUsername(String ip) {
        if (USERINFO_DB_IP.equals(ip)) {
            return userinfo_db_username;
        }
        return username;
    }

    public String getPassword(String ip) {
        if (USERINFO_DB_IP.equals(ip)) {
            return userinfo_db_password;
        }
        return password;
    }

    public String getUserinfoDbIp(String ip) {
        if (userinfo_db_enable) {
            if (!userinfo_db_useFcmDb) {
                return USERINFO_DB_IP;
            }
        }
        return ip;
    }

    public String getFixedUserInfoDbIp() {
        return USERINFO_DB_IP;
    }

    public void addConnectionIp(String ip) {
        if (USERINFO_DB_IP.equals(ip)) {
            DatabasePool databasePool = BeanFactory.getBean(DatabasePool.class);
            for (String ipStr : databases.keySet()) {
                databasePool.addConnectionIp(ipStr);
            }
            return;
        }
        if (url.contains("{ip}")) {
            databases.put(ip, ip);
            DatabasePool databasePool = BeanFactory.getBean(DatabasePool.class);
            databasePool.addConnectionIp(ip);
        } else {
            DatabasePool databasePool = BeanFactory.getBean(DatabasePool.class);
            for (String ipStr : databases.keySet()) {
                databasePool.addConnectionIp(ipStr);
            }
        }
    }

    public void removeConnectionIp(String ip) {
        if (url.contains("{ip}")) {
            databases.remove(ip);
        }
    }

    public boolean isValidDatabase(String ip) {
        return databases.containsKey(ip);
    }

    public boolean canTransaction() {
        if (transactionEnable) {
            if (userinfo_db_enable) {
                if (databases.size() > 1) {
                    return true;
                }
            } else {
                if (databases.size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
