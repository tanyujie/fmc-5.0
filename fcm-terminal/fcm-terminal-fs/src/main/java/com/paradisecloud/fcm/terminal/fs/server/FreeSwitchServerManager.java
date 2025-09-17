package com.paradisecloud.fcm.terminal.fs.server;

import com.paradisecloud.fcm.dao.model.BusiFreeSwitch;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.common.SshRemoteServerOperate;
import com.paradisecloud.fcm.terminal.fs.db.FreeSwitchDatabaseManager;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Free Switch数据库连接管理器
 */
public class FreeSwitchServerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(FreeSwitchServerManager.class);

    private int port;
    private String password;

    private Map<String, BusiFreeSwitch> servers;
    private FreeSwitchTerminalOnlineEventListener freeSwitchTerminalOnlineEventListener;
    private FreeSwitchTerminalStatusChangeListener freeSwitchTerminalStatusChangeListener;

    /**
     * 接收事件消息最新时间
     */
    private Map<String, Long> lastEventTimeMap;
    /**
     * 启动时是否已从DB获取在线用户
     */
    private Map<String, Long> onlineUserFromDbTimeMap;

    /**
     * 重启服务器最新时间
     */
    private Map<String, Long> lastRestartTimeMap;

    private FreeSwitchServerManager() {
    }

    /**
     * 只在程序启动时调用
     *
     * @param port
     * @param password
     * @return
     */
    public static void init(int port, String password) {
        FreeSwitchServerManager instance = InnerClass.INSTANCE;
        if (instance.port <= 0) {
            instance.port = port;
            instance.password = password;
            instance.servers = new ConcurrentHashMap<>();
            Map<Long, FcmBridge> fcmBridgeMap = FcmBridgeCache.getInstance().getFcmBridgeMap();
            for (Long key : fcmBridgeMap.keySet()) {
                instance.addConnectionIp(fcmBridgeMap.get(key).getBusiFreeSwitch().getIp(), fcmBridgeMap.get(key).getBusiFreeSwitch());
            }
            if (instance.lastEventTimeMap == null) {
                instance.lastEventTimeMap = new ConcurrentHashMap<>();
            }
            if (instance.onlineUserFromDbTimeMap == null) {
                instance.onlineUserFromDbTimeMap = new ConcurrentHashMap<>();
            }
            if (instance.lastRestartTimeMap == null) {
                instance.lastRestartTimeMap = new ConcurrentHashMap<>();
            }
        }
    }

    public static FreeSwitchServerManager getInstance() {
        FreeSwitchServerManager instance = FreeSwitchServerManager.InnerClass.INSTANCE;
        if (instance.lastEventTimeMap == null) {
            instance.lastEventTimeMap = new ConcurrentHashMap<>();
        }
        if (instance.onlineUserFromDbTimeMap == null) {
            instance.onlineUserFromDbTimeMap = new ConcurrentHashMap<>();
        }
        return instance;
    }

    private static class InnerClass {
        private final static FreeSwitchServerManager INSTANCE = new FreeSwitchServerManager();
    }

    public int getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }

    public void setFreeSwitchTerminalOnlineEventListener(FreeSwitchTerminalOnlineEventListener listener) {
        freeSwitchTerminalOnlineEventListener = listener;
    }

    public FreeSwitchTerminalOnlineEventListener getFreeSwitchTerminalOnlineEventListener() {
        return freeSwitchTerminalOnlineEventListener;
    }

    public FreeSwitchTerminalStatusChangeListener getFreeSwitchTerminalStatusChangeListener() {
        return freeSwitchTerminalStatusChangeListener;
    }

    public void setFreeSwitchTerminalStatusChangeListener(FreeSwitchTerminalStatusChangeListener freeSwitchTerminalStatusChangeListener) {
        this.freeSwitchTerminalStatusChangeListener = freeSwitchTerminalStatusChangeListener;
    }

    public Long getServerIdByIp(String ip) {
        BusiFreeSwitch busiFreeSwitch = servers.get(ip);
        if (busiFreeSwitch != null) {
            return busiFreeSwitch.getId();
        }
        return null;
    }

    public void addConnectionIp(String ip, BusiFreeSwitch busiFreeSwitch) {
        servers.put(ip, busiFreeSwitch);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FreeSwitchServerPool freeSwitchServerPool = BeanFactory.getBean(FreeSwitchServerPool.class);
                freeSwitchServerPool.addConnectionIp(ip);
            }
        }).start();
    }

    public void removeConnectionIp(String ip) {
        BusiFreeSwitch busiFreeSwitch = servers.remove(ip);
        FreeSwitchServerPool freeSwitchServerPool = BeanFactory.getBean(FreeSwitchServerPool.class);
        freeSwitchServerPool.removeConnectIp(busiFreeSwitch);
    }

    public Set<String> getServerIps() {
        if (servers != null) {
            return servers.keySet();
        }
        return new HashSet<>();
    }

    public boolean isValidServer(String ip) {
        if (servers != null) {
            return servers.containsKey(ip);
        }
        return false;
    }

    public boolean isServerOnline(String ip) {
        if (lastEventTimeMap != null) {
            Long lastEventTime = lastEventTimeMap.get(ip);
            if (lastEventTime != null) {
                long currentTime = System.currentTimeMillis();
                long diff = currentTime - lastEventTime;
                LOGGER.info("FS事件间隔时间：" + diff + "(" + currentTime + " - " + lastEventTime + ")");
                if (diff < 25000) {
                    return true;
                } else {
                    BusiFreeSwitch busiFreeSwitch = servers.get(ip);
                    if (busiFreeSwitch != null) {
                        Integer status = getFreeSwitchServerStatus(busiFreeSwitch);
                        if (status == 1 || status == 3) {
                            Long lastRestartTime = null;
                            if (lastRestartTimeMap != null) {
                                lastRestartTime = lastRestartTimeMap.get(ip);
                            }
                            if (diff > 60000) {
                                if (lastRestartTime == null || currentTime - lastRestartTime > 90000) {
                                    restartFreeSwitchServer(busiFreeSwitch);
                                    updateLastRestartTime(ip);
                                }
                                FreeSwitchServerPool freeSwitchServerPool = BeanFactory.getBean(FreeSwitchServerPool.class);
                                freeSwitchServerPool.addConnectionIp(ip);
                            }
                            return true;
                        }
                    }
                }
            } else {
                BusiFreeSwitch busiFreeSwitch = servers.get(ip);
                if (busiFreeSwitch != null) {
                    Integer status = getFreeSwitchServerStatus(busiFreeSwitch);
                    if (status == 1 || status == 3) {
                        long currentTime = System.currentTimeMillis();
                        Long lastRestartTime = null;
                        if (lastRestartTimeMap != null) {
                            lastRestartTime = lastRestartTimeMap.get(ip);
                        }
                        if (lastRestartTime == null || currentTime - lastRestartTime > 90000) {
                            restartFreeSwitchServer(busiFreeSwitch);
                            updateLastRestartTime(ip);
                        }
                    }
                }
            }
        }
        return false;
    }

    public void updateLastEventTime(String ip) {
        if (lastEventTimeMap != null) {
            lastEventTimeMap.put(ip, System.currentTimeMillis());
        }
    }

    public void updateLastEventTimeForServerOffline(String ip) {
        if (lastEventTimeMap != null) {
            lastEventTimeMap.put(ip, 0L);
        }
    }

    public boolean isLastEventTimeForServerOffline(String ip) {
        if (lastEventTimeMap != null) {
            Long lastEventTime = lastEventTimeMap.get(ip);
            if (lastEventTime != null && lastEventTime == 0) {
                return true;
            }
        }
        return false;
    }

    public void updateOnlineUserFromDbTimes(String ip) {
        if (onlineUserFromDbTimeMap != null) {
            onlineUserFromDbTimeMap.put(ip, System.currentTimeMillis());
        }
    }

    public void updateOnlineUserFromDbTimesForServerOffline(String ip) {
        if (onlineUserFromDbTimeMap != null) {
            onlineUserFromDbTimeMap.remove(ip);
        }
    }

    public void updateLastRestartTime(String ip) {
        if (lastRestartTimeMap != null) {
            lastRestartTimeMap.put(ip, System.currentTimeMillis());
        }
    }

    /**
     * 启动系统时需要抽取一次，当1分钟未接收到事件推送消息时可以抽取
     *
     * @param ip
     * @return
     */
    public boolean canGetOnlineUserFromDb(String ip) {
        if (onlineUserFromDbTimeMap == null || onlineUserFromDbTimeMap.get(ip) == null) {
            if (FreeSwitchDatabaseManager.getInstance().isValidDatabase(ip)) {
                FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByIp(ip);
                if (fcmBridge != null && fcmBridge.isAvailable()) {
                    if (isServerOnline(ip)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 重启freeswitch服务
     */
    public Integer restartFreeSwitchServer(BusiFreeSwitch freeSwitch) {
        LOGGER.info("==========>>>>restartFreeSwitchServer");
        if (pingIp(freeSwitch.getIp())) {
            try {
                SshRemoteServerOperate.getInstance().sshRemoteCallLogin(freeSwitch.getIp(), freeSwitch.getUserName(), freeSwitch.getPassword(), freeSwitch.getPort());
                if (SshRemoteServerOperate.getInstance().isLogined()) {
                    //发送指令
                    SshRemoteServerOperate.getInstance().execCommand("systemctl restart freeswitch.service");
                }
            } catch (Exception e) {
                LOGGER.error("==========>>>>systemctl restart freeswitch.service 指令失败", e);
            } finally {
                SshRemoteServerOperate.getInstance().closeSession();
            }
        }
        return 2;
    }

    /**
     * 查看freeswitch服务是否在线：1在线，2离线, 3FS未启动
     */
    public Integer getFreeSwitchServerStatus(BusiFreeSwitch freeSwitch) {
        if (pingIp(freeSwitch.getIp())) {
            try {
                SshRemoteServerOperate.getInstance().sshRemoteCallLogin(freeSwitch.getIp(), freeSwitch.getUserName(), freeSwitch.getPassword(), freeSwitch.getPort());
                if (SshRemoteServerOperate.getInstance().isLogined()) {
                    //发送指令
                    String status = SshRemoteServerOperate.getInstance().execCommand("ps -e | grep freeswitch");
                    if (StringUtils.isNotEmpty(status)) {
                        return 1;
                    } else {
                        return 3;
                    }
                }
            } catch (Exception e) {
                LOGGER.error("==========>>>>ps -e | grep freeswitch 指令失败", e);
            } finally {
                SshRemoteServerOperate.getInstance().closeSession();
            }
        }
        return 2;
    }

    /**
     * ping服务器Ip
     *
     * @param ip
     * @return
     */
    public Boolean pingIp(String ip) {
        if (null == ip || 0 == ip.length()) {
            return false;
        }
        try {
            boolean reachable = InetAddress.getByName(ip).isReachable(500);
            if (reachable) {
                return true;
            }
        } catch (IOException e) {
        }
        return false;
    }
}