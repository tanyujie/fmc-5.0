package com.paradisecloud.fcm.terminal.fs.server;

import com.paradisecloud.fcm.dao.model.BusiFreeSwitch;
import org.freeswitch.esl.client.IEslEventListener;
import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class FreeSwitchServerPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(FreeSwitchServerPool.class);

    /**
     * 活跃连接池
     */
    private ConcurrentHashMap<String, Client> connectedMap;

    /**
     * 将移除的连接
     */
    private ConcurrentLinkedDeque<Map<String, Object>> removeConnectQueue;

    public FreeSwitchServerPool() {
        init();
    }

    /**
     * 连接池初始化
     */
    public void init() {
        if (connectedMap == null) {
            connectedMap = new ConcurrentHashMap<>();
        }
        if (removeConnectQueue == null) {
            removeConnectQueue = new ConcurrentLinkedDeque<>();
        }
    }

    /**
     * 添加连接地址
     *
     * @param ip
     */
    public void addConnectionIp(String ip) {
        Client client = connectedMap.get(ip);
        if (client != null) {
            if (client.canSend()) {
                client.close();
            }
        }
        client = new Client();
        FreeSwitchServerManager freeSwitchServerManager = FreeSwitchServerManager.getInstance();
        freeSwitchServerManager.updateOnlineUserFromDbTimesForServerOffline(ip);
        try {
            client.connect(ip, freeSwitchServerManager.getPort(), freeSwitchServerManager.getPassword(), 20);
            freeSwitchServerManager.updateLastEventTime(ip);
        } catch (InboundConnectionFailure e) {
            LOGGER.error("Connect failed", e);
//            e.printStackTrace();
            if (!freeSwitchServerManager.isLastEventTimeForServerOffline(ip)) {
                FreeSwitchTerminalOnlineEventListener freeSwitchTerminalOnlineEventListener = FreeSwitchServerManager.getInstance().getFreeSwitchTerminalOnlineEventListener();
                if (freeSwitchTerminalOnlineEventListener != null) {
                    Long serverId = FreeSwitchServerManager.getInstance().getServerIdByIp(ip);
                    if (serverId != null) {
                        freeSwitchTerminalOnlineEventListener.serverOffline(serverId);
                        freeSwitchServerManager.updateLastEventTimeForServerOffline(ip);
                    }
                }
            }
            return;
        }
        // 注册事件处理程序
        client.addEventListener(new IEslEventListener() {
            public void eventReceived(EslEvent event) {
                if ("CUSTOM".equals(event.getEventName())) {
                    Map<String, String> eventHeaders = event.getEventHeaders();
                    String eventSubclass = eventHeaders.get("Event-Subclass");
                    String username = eventHeaders.get("username");
                    if ("unknown".equals(username)) {
                        username = eventHeaders.get("from-user");
                    }
                    if ("sofia::register".equals(eventSubclass)) {
                        LOGGER.debug("###终端在线：" + username + "@" + ip);
                        FreeSwitchServerManager.getInstance().updateLastEventTime(ip);
                        FreeSwitchTerminalOnlineEventListener freeSwitchTerminalOnlineEventListener = FreeSwitchServerManager.getInstance().getFreeSwitchTerminalOnlineEventListener();
                        if (freeSwitchTerminalOnlineEventListener != null) {
                            Long serverId = FreeSwitchServerManager.getInstance().getServerIdByIp(ip);
                            if (serverId != null) {
                                freeSwitchTerminalOnlineEventListener.online(serverId, username);
                            }
                        }
                    } else if ("sofia::unregister".equals(eventSubclass) || "sofia::expire".equals(eventSubclass)) {
                        LOGGER.debug("###终端离线：" + username + "@" + ip);
                        FreeSwitchServerManager.getInstance().updateLastEventTime(ip);
                        FreeSwitchTerminalOnlineEventListener freeSwitchTerminalOnlineEventListener = FreeSwitchServerManager.getInstance().getFreeSwitchTerminalOnlineEventListener();
                        if (freeSwitchTerminalOnlineEventListener != null) {
                            Long serverId = FreeSwitchServerManager.getInstance().getServerIdByIp(ip);
                            if (serverId != null) {
                                freeSwitchTerminalOnlineEventListener.offline(serverId, username);
                            }
                        }
                    }
                } else if ("HEARTBEAT".equals(event.getEventName())) {
                    FreeSwitchServerManager.getInstance().updateLastEventTime(ip);
                    FreeSwitchTerminalOnlineEventListener freeSwitchTerminalOnlineEventListener = FreeSwitchServerManager.getInstance().getFreeSwitchTerminalOnlineEventListener();
                    if (freeSwitchTerminalOnlineEventListener != null) {
                        Long serverId = FreeSwitchServerManager.getInstance().getServerIdByIp(ip);
                        if (serverId != null) {
                            freeSwitchTerminalOnlineEventListener.serverOnline(serverId);
                        }
                    }
                }
            }

            public void backgroundJobResultReceived(EslEvent event) {
                //do nothing
            }
        });
        client.setEventSubscriptions("plain", "HEARTBEAT CUSTOM sofia::register sofia::unregister sofia::expire");
        connectedMap.put(ip, client);
    }

    /**
     * 移除连接地址
     *
     * @param busiFreeSwitch
     */
    public void removeConnectIp(BusiFreeSwitch busiFreeSwitch) {
        Client client = connectedMap.remove(busiFreeSwitch.getIp());
        if (client != null) {
            Map<String, Object> clientMap = new HashMap<>();
            clientMap.put("1", client);
            clientMap.put("2", busiFreeSwitch);
            removeConnectQueue.offer(clientMap);
        }
    }

    /**
     * 定时对连接进行健康检查
     * 注意：只能对idle连接池中的连接进行健康检查，
     * 不可以对busyConnectPool连接池中的连接进行健康检查，因为它正在被客户端使用;
     */
    @Scheduled(fixedRate = 10000)
    public void check() {
        FreeSwitchServerManager freeSwitchServerManager = FreeSwitchServerManager.getInstance();
        for (String ip : freeSwitchServerManager.getServerIps()) {
            Client client = connectedMap.get(ip);
            if (client == null) {
                freeSwitchServerManager.isServerOnline(ip);
                addConnectionIp(ip);
            } else if (!freeSwitchServerManager.isServerOnline(ip)) {
                freeSwitchServerManager.updateOnlineUserFromDbTimesForServerOffline(ip);
                if (!freeSwitchServerManager.isLastEventTimeForServerOffline(ip)) {
                    FreeSwitchTerminalOnlineEventListener freeSwitchTerminalOnlineEventListener = freeSwitchServerManager.getFreeSwitchTerminalOnlineEventListener();
                    if (freeSwitchTerminalOnlineEventListener != null) {
                        Long serverId = FreeSwitchServerManager.getInstance().getServerIdByIp(ip);
                        if (serverId != null) {
                            freeSwitchTerminalOnlineEventListener.serverOffline(serverId);
                            freeSwitchServerManager.updateLastEventTimeForServerOffline(ip);
                        }
                    }
                }
            }
        }
    }

    /**
     * 定时对移除连接进行关闭
     */
    @Scheduled(fixedRate = 60000)
    public void checkRemoved() {
        for (int i = 0; i < removeConnectQueue.size(); i++) {
            Map<String, Object> clientMap = removeConnectQueue.poll();
            if (clientMap != null) {
                Object clientObj = clientMap.get("1");
                Object busiFreeSwitchOj = clientMap.get("2");
                if (clientObj != null) {
                    Client client = (Client) clientObj;
                    BusiFreeSwitch busiFreeSwitch = (BusiFreeSwitch) busiFreeSwitchOj;
                    if (client.canSend()) {
                        if (busiFreeSwitch != null) {
                            int status = FreeSwitchServerManager.getInstance().getFreeSwitchServerStatus(busiFreeSwitch);
                            if (status == 1) {
                                client.close();
                            } else if (status == 2) {
                                removeConnectQueue.offer(clientMap);
                            }
                        }
                    }
                }
            }
        }
    }
}
