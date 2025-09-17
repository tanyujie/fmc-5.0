package com.paradisecloud.fcm.smc.cache.modle;

import com.sinhy.utils.HostUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author nj
 * @date 2022/8/30 9:34
 */
public class TokenManager {

    private static final Logger logger = LoggerFactory.getLogger(TokenManager.class);
    private static Map<String, String> bridgeSystemTokenMap = new ConcurrentHashMap<>();
    private static Map<String, String> bridgeMeetingTokenMap = new ConcurrentHashMap<>();
    private static Map<Long, String> bridgeSystemTokenMapLong = new ConcurrentHashMap<>();
    private static Map<Long, String> bridgeMeetingTokenMapLong = new ConcurrentHashMap<>();
    private static Map<Long, String> deptMeetingTokenMap = new ConcurrentHashMap<>();
    private static Map<Long, String> deptSystemTokenMap = new ConcurrentHashMap<>();
    private static ScheduledExecutorService scheduledExecutorService;
    private static Map<String, ScheduledFuture<?>> futureMap = new HashMap<String, ScheduledFuture<?>>();
    private static int poolSize = 2;
    private static boolean daemon = Boolean.TRUE;
    private static String defaultMeetingToken;
    private static String defaultSysToken;
    private static Map<String, SmcAuthResponse> bridgeMeetingTokenMapExpire = new ConcurrentHashMap<>();


    public static Map<String, SmcAuthResponse> getBridgeMeetingTokenMapExpire() {
        return bridgeMeetingTokenMapExpire;
    }

    /**
     * 初始化 scheduledExecutorService
     */
    private static void initScheduledExecutorService() {
        logger.info("daemon:{},poolSize:{}", daemon, poolSize);
        scheduledExecutorService = Executors.newScheduledThreadPool(poolSize, new ThreadFactory() {

            @Override
            public Thread newThread(Runnable arg0) {
                Thread thread = Executors.defaultThreadFactory().newThread(arg0);
                //设置守护线程
                thread.setDaemon(daemon);
                return thread;
            }
        });
    }

    /**
     * 设置线程池
     *
     * @param poolSize poolSize
     */
    public static void setPoolSize(int poolSize) {
        TokenManager.poolSize = poolSize;
    }

    /**
     * 设置线程方式
     *
     * @param daemon
     */
    public static void setDaemon(boolean daemon) {
        TokenManager.daemon = daemon;
    }

    /**
     * 初始化token刷新
     */
    public static void init(final String apiUrl) {
        init(apiUrl, 0, 60 * 60 * 12 * 30);
    }

    /**
     * 初始化token 刷新，每30分钟刷新一次。
     *
     * @param initialDelay 首次执行延迟（秒）
     * @param delay        执行间隔（秒）
     */
    public static void init(String apiUrl, int initialDelay, int delay) {
//        if (scheduledExecutorService == null) {
//            initScheduledExecutorService();
//        }
//
//        if (futureMap.containsKey(apiUrl)) {
//            futureMap.get(apiUrl).cancel(true);
//        }
//        //立即执行一次
//        if (initialDelay == 0) {
//            doRun();
//        }
//        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() ->
//                        doRefreshRun()
//                , initialDelay == 0 ? delay : initialDelay, delay, TimeUnit.SECONDS);
//        futureMap.put(apiUrl, scheduledFuture);
//        logger.info("apiKey:{}", apiUrl);
    }

    private static void doRefreshRun() {



        try {
            Map<Long, SmcBridge> smcBridgeMap = SmcBridgeCache.getInstance().getSmcBridgeMap();
            Map<Long, SmcBridge> smcDeptBridgeMap = SmcBridgeCache.getInstance().getDeptIdSmcBridgeMap();

            smcBridgeMap.entrySet().forEach(entry -> {
                        SmcBridge bridge = entry.getValue();
                        String ip = bridge.getBusiSMC().getIp();
                        Long key = entry.getKey();
                        if (HostUtils.isHostReachable(ip, 1000)) {
                            try {
                                String authToken = bridge.getSmcportalTokenInvoker().getAuthToken();
                                TokenManager.defaultSysToken=authToken;
                                if (StringUtils.isBlank(authToken)) {
                                    bridge.getBusiSMC().setStatus(2);
                                    smcBridgeMap.put(key, bridge);
                                    logger.error("authToken获取失败");
                                } else {
                                    initBridgeTokenMap(bridge, ip, key, authToken);
                                }

                            } catch (IOException e) {
                                bridge.getBusiSMC().setStatus(2);
                                smcBridgeMap.put(key, bridge);
                                logger.error("authToken获取异常终止");
                                e.printStackTrace();
                            }
                        } else {
                            bridge.getBusiSMC().setStatus(2);
                            smcBridgeMap.put(key, bridge);
                            logger.error("smc网络连接失败！");
                        }


                    }
            );
            List<SmcBridge> smcBridgeList = SmcBridgeCache.getInstance().getInitSmcBridgeList();
            if (CollectionUtils.isNotEmpty(smcBridgeList) && smcBridgeList.size() == 1) {
                SmcBridge bridge = smcBridgeList.get(0);
                String ip = bridge.getBusiSMC().getIp();
                defaultMeetingToken = bridgeMeetingTokenMap.get(ip);
                defaultSysToken = bridgeSystemTokenMap.get(ip);
            }
            if (!org.springframework.util.CollectionUtils.isEmpty(smcDeptBridgeMap)) {
                smcDeptBridgeMap.forEach((deptId, bridge) -> {
                    String ip = bridge.getBusiSMC().getIp();
                    String mToken = bridgeSystemTokenMap.get(ip);
                    String stoken = bridgeMeetingTokenMap.get(ip);
                    deptMeetingTokenMap.put(deptId, mToken);
                    deptSystemTokenMap.put(deptId, stoken);
                });
            }

            logger.info("authToken doRefreshRun with smcBridgeMap:{}", smcBridgeMap);
        } catch (Exception e) {
            logger.error("authToken  doRefreshRun error with smcBridgeMap");
            e.printStackTrace();
        }
    }

    public static void doRun() {
        try {
            Map<Long, SmcBridge> smcBridgeMap = SmcBridgeCache.getInstance().getSmcBridgeMap();
            Map<Long, SmcBridge> smcDeptBridgeMap = SmcBridgeCache.getInstance().getDeptIdSmcBridgeMap();

            smcBridgeMap.entrySet().forEach(entry -> {
                        SmcBridge bridge = entry.getValue();
                        String ip = bridge.getBusiSMC().getIp();
                        Long key = entry.getKey();
                        if (HostUtils.isHostReachable(ip, 1000)) {

                            try {
                                String authToken = bridge.getSmcportalTokenInvoker().getAuthToken();
                                defaultSysToken=authToken;
                                if (StringUtils.isBlank(authToken)) {
                                    bridge.getBusiSMC().setStatus(2);
                                    smcBridgeMap.put(key, bridge);
                                    logger.error("authToken获取失败！");
                                } else {
                                    initBridgeTokenMap(bridge, ip, key, authToken);
                                    bridge.getBusiSMC().setStatus(1);
                                    smcBridgeMap.put(key, bridge);
                                }


                            } catch (IOException e) {
                                bridge.getBusiSMC().setStatus(2);
                                smcBridgeMap.put(key, bridge);
                                logger.error("authToken获取异常终止");
                                e.printStackTrace();
                            }

                        } else {
                            bridge.getBusiSMC().setStatus(2);
                            smcBridgeMap.put(key, bridge);
                            logger.error("smc网络连接失败！");
                        }

                    }
            );
            List<SmcBridge> smcBridgeList = SmcBridgeCache.getInstance().getInitSmcBridgeList();
            if (CollectionUtils.isNotEmpty(smcBridgeList) && smcBridgeList.size() == 1) {
                SmcBridge bridge = smcBridgeList.get(0);
                String ip = bridge.getBusiSMC().getIp();
                if (bridge.getBusiSMC().getStatus() == 1) {
                    defaultMeetingToken = bridgeMeetingTokenMap.get(ip);
                    defaultSysToken = bridgeSystemTokenMap.get(ip);
                }

            }
            if (!org.springframework.util.CollectionUtils.isEmpty(smcDeptBridgeMap)) {
                smcDeptBridgeMap.forEach((deptId, bridge) -> {
                    if (bridge.getBusiSMC().getStatus() == 1) {
                        String ip = bridge.getBusiSMC().getIp();
                        String mToken = bridgeSystemTokenMap.get(ip);
                        String stoken = bridgeMeetingTokenMap.get(ip);
                        deptMeetingTokenMap.put(deptId, mToken);
                        deptSystemTokenMap.put(deptId, stoken);
                    }

                });
            }

            logger.info("authToken refurbish2 with smcBridgeMap:{}", smcBridgeMap);
        } catch (Exception e) {
            logger.error("authToken  refurbish error3 with smcBridgeMap");
            e.printStackTrace();
        }
    }

    private static void initBridgeTokenMap(SmcBridge bridge, String ip, Long key, String authToken) {
        bridgeSystemTokenMap.put(ip, authToken);
        bridgeSystemTokenMapLong.put(key, authToken);
        Map<String, String> meetingHeaders = bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders();
        String meetingAuthToken =  meetingHeaders.get(ip);
        defaultMeetingToken=meetingAuthToken;
        if (!StringUtils.isBlank(meetingAuthToken)) {
            bridgeMeetingTokenMap.put(ip, meetingAuthToken);
            bridgeMeetingTokenMapLong.put(key, meetingAuthToken);
        }
    }

    /**
     * 取消 token 刷新
     */
    public static void destroyed() {
        scheduledExecutorService.shutdownNow();
        logger.info("destroyed");
    }

    /**
     * 取消刷新
     *
     * @param authToken
     */
    public static void destroyed(String authToken) {
        if (futureMap.containsKey(authToken)) {
            futureMap.get(authToken).cancel(true);
            logger.info("destroyed appid:{}", authToken);
        }
    }

    /**
     * 获取token
     *
     * @param ip
     * @return token
     */
    public static String getMeetingToken(String ip) {
        return bridgeMeetingTokenMap.get(ip);
    }


    /**
     * 获取token
     *
     * @param ip
     * @return token
     */
    public static void setMeetingToken(String ip, String token) {
        bridgeMeetingTokenMap.put(ip, token);
    }

    /**
     * 设置系统token
     *
     * @param baseIp
     * @param systemToken
     */
    public static void setSystemToken(String baseIp, String systemToken) {
        bridgeSystemTokenMap.put(baseIp, systemToken);
    }

    /**
     * 获取token
     *
     * @param id
     * @return token
     */
    public static String getMeetingToken(Long id) {
        return bridgeMeetingTokenMapLong.get(id);
    }

    /**
     * 获取token
     *
     * @param ip
     * @return token
     */
    public static String getSystemToken(String ip) {
        return bridgeSystemTokenMap.get(ip);
    }

    /**
     * 获取token
     *
     * @param id
     * @return token
     */
    public static String getSystemToken(Long id) {
        return bridgeSystemTokenMapLong.get(id);
    }



}
