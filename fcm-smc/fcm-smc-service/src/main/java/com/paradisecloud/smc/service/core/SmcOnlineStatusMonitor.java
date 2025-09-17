package com.paradisecloud.smc.service.core;

import com.paradisecloud.com.fcm.smc.modle.ScEndpointRep;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.message.terminal.TerminalOnlineStatusMessage;
import com.paradisecloud.fcm.common.message.terminal.TerminalOnlineStatusMessageQueue;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.fcm.smc.cache.modle.util.ScEndpointUtil;
import com.paradisecloud.smc.SMCWebsocketClient;
import com.paradisecloud.smc.SmcWebsocketContext;
import com.paradisecloud.smc.dao.model.BusiSmc;
import com.sinhy.utils.ThreadUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.java_websocket.enums.ReadyState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nj
 * @date 2022/8/30 15:31
 */
public class SmcOnlineStatusMonitor extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmcOnlineStatusMonitor.class);
    private final static long WAIT_TIME = 30 * 1000;
    private static final SmcOnlineStatusMonitor INSTANCE = new SmcOnlineStatusMonitor();
    private BusiTerminalMapper busiTerminalMapper;


    private SmcOnlineStatusMonitor() {
        super("SmcOnlineStatusMonitor");
    }

    public static SmcOnlineStatusMonitor getInstance() {
        return INSTANCE;
    }

    public void init(BusiTerminalMapper busiTerminalMapper) {
        this.busiTerminalMapper = busiTerminalMapper;
        this.start();
    }

    @Override
    public void run() {

        LOGGER.info("SMC终端在线状态监视器启动并初始化成功");
        long startTime = 0;
        long endTime = 0;
        while (true) {
            try {
                startTime = System.currentTimeMillis();
                checkTask();

            } catch (Throwable e) {
                LOGGER.error("主线程wait异常：", e);
            } finally {
                endTime = System.currentTimeMillis();
                long spendTime = endTime - startTime;
                if (spendTime < WAIT_TIME) {
                    ThreadUtils.sleep(WAIT_TIME - spendTime);
                }
                LOGGER.info("SMC终端本轮检测所有终端在线状态，共耗时：" + spendTime);
            }
        }

    }


    private void checkTask() {
        // Map<String, BusiTerminal> originalNumberTerminalMap = SmcTerminalCache.getInstance().getOriginalNumberTerminalMap();
        AtomicInteger onlineNumber = new AtomicInteger();
        BusiTerminal terminalquery = new BusiTerminal();
        terminalquery.setType(TerminalType.SMC_SIP.getId());
        List<BusiTerminal> busiTerminals = busiTerminalMapper.selectBusiTerminalList(terminalquery);
        //查询所有会议室
        List<SmcBridge> smcBridgeList = SmcBridgeCache.getInstance().getInitSmcBridgeList();

        if (CollectionUtils.isNotEmpty(smcBridgeList)) {
            for (SmcBridge smcBridge : smcBridgeList) {
                if (smcBridge.getWebsocketAvailable()) {
                    List<ScEndpointRep> endpoints = ScEndpointUtil.getEndpoints(smcBridge, 0, 200);
                    if (CollectionUtils.isNotEmpty(endpoints) && CollectionUtils.isNotEmpty(busiTerminals)) {
                        endpoints.stream().forEach(p -> {
                            Boolean sipState = p.getSipState();
                            //BusiTerminal terminal = originalNumberTerminalMap.get(p.getUri());
                            Optional<BusiTerminal> first = busiTerminals.stream().filter(b -> Objects.equals(b.getNumber(), p.getUri())).findFirst();
                            if (first.isPresent()) {
                                BusiTerminal terminal = first.get();
                                if (terminal != null) {
                                    TerminalOnlineStatus oldStatus = TerminalOnlineStatus.convert(terminal.getOnlineStatus());
                                    TerminalOnlineStatus newStatus = sipState ? TerminalOnlineStatus.ONLINE : TerminalOnlineStatus.OFFLINE;
                                    processTerminalInfo(terminal, oldStatus, newStatus);
                                    if (TerminalOnlineStatus.ONLINE.equals(newStatus)) {
                                        onlineNumber.getAndIncrement();
                                    }
                                }
                            }
                        });
                    }
                    ;
                    BusiSmc busiSMC = smcBridge.getBusiSMC();
                    smcBridge.getParams().put("onlineStatus", 1);
                    busiSMC.setStatus(1);
                    SMCWebsocketClient websocketClient = SmcWebsocketContext.getSmcWebsocketClientMap().get(smcBridge.getIp());
                    if (websocketClient == null) {
                        smcBridge.getParams().put("onlineStatus", 2);
                        busiSMC.setStatus(2);
                    } else {
                        if (!Objects.equals(websocketClient.getReadyState(), ReadyState.OPEN)) {
                            smcBridge.getParams().put("onlineStatus", 2);
                            busiSMC.setStatus(2);
                        }
                    }
                    //  smcBridge.getParams().put("onlineStatus", endpoints == null ? TerminalOnlineStatus.OFFLINE.getValue() : TerminalOnlineStatus.ONLINE.getValue());

                }
            }

        }

        LOGGER.info("SMC终端本轮检测所有终端在线状态，共在线数量：" + onlineNumber.get());
    }


    public void processTerminalInfo(BusiTerminal busiTerminal, TerminalOnlineStatus oldStatus, TerminalOnlineStatus realStatus) {
        if (busiTerminal == null) {
            return;
        }
        if (oldStatus != realStatus) {
            busiTerminal.setOnlineStatus(realStatus.getValue());
            busiTerminalMapper.updateBusiTerminal(busiTerminal);

            TerminalOnlineStatusMessageQueue.getInstance().put(new TerminalOnlineStatusMessage(busiTerminal.getId(), realStatus));
            LOGGER.info("[" + busiTerminal.getName() + "]-[" + busiTerminal.getIp() + "]检测到终端在线状态有变化: " + oldStatus.getName() + "--->"
                    + TerminalOnlineStatus.convert(busiTerminal.getOnlineStatus()).getName());
        }
    }

}
