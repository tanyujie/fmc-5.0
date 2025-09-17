package com.paradisecloud.smc3.monitor;

import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.message.terminal.TerminalOnlineStatusMessage;
import com.paradisecloud.fcm.common.message.terminal.TerminalOnlineStatusMessageQueue;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;

import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;

import com.paradisecloud.smc3.invoker.util.ScEndpointUtil;
import com.paradisecloud.smc3.model.ScEndpointRep;
import com.paradisecloud.smc3.task.Smc3CheckAttendeeOnlineStatusTask;
import com.paradisecloud.smc3.task.Smc3DelayTaskService;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nj
 * @date 2022/8/30 15:31
 */
public class Smc3OnlineStatusMonitor extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(Smc3OnlineStatusMonitor.class);
    private final static long WAIT_TIME = 10 * 1000;
    private static final Smc3OnlineStatusMonitor INSTANCE = new Smc3OnlineStatusMonitor();
    private BusiTerminalMapper busiTerminalMapper;


    private Smc3OnlineStatusMonitor() {
        super("Smc3OnlineStatusMonitor");
    }

    public static Smc3OnlineStatusMonitor getInstance() {
        return INSTANCE;
    }

    public void init(BusiTerminalMapper busiTerminalMapper) {
        this.busiTerminalMapper = busiTerminalMapper;
        this.start();
    }

    @Override
    public void run() {

        LOGGER.info("SMC3.0---终端在线状态监视器启动并初始化成功");
        long startTime = 0;
        long endTime = 0;
        while (true) {
            try {
                startTime = System.currentTimeMillis();
                checkTask();

                Smc3CheckAttendeeOnlineStatusTask smc3CheckAttendeeOnlineStatusTask = new Smc3CheckAttendeeOnlineStatusTask(McuType.SMC3.getCode(), 0);
                Smc3DelayTaskService smc3DelayTaskService = BeanFactory.getBean(Smc3DelayTaskService.class);
                smc3DelayTaskService.addTask(smc3CheckAttendeeOnlineStatusTask);


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
        AtomicInteger onlineNumber = new AtomicInteger();
        BusiTerminal terminalquery = new BusiTerminal();
        terminalquery.setType(TerminalType.SMC_SIP.getId());
        List<BusiTerminal> busiTerminals = busiTerminalMapper.selectBusiTerminalList(terminalquery);
        //查询所有会议室
        Map<Long, Smc3Bridge> idToTeleBridgeMap = Smc3BridgeCache.getInstance().getIdToTeleBridgeMap();

        Collection<Smc3Bridge> smcBridgeList = idToTeleBridgeMap.values();

        if (CollectionUtils.isNotEmpty(smcBridgeList)) {
            for (Smc3Bridge smcBridge : smcBridgeList) {
                if (smcBridge.getWebsocketAvailable()) {
                    List<ScEndpointRep> endpoints = ScEndpointUtil.getEndpoints(smcBridge, 0, 200);
                    if (CollectionUtils.isNotEmpty(endpoints) && CollectionUtils.isNotEmpty(busiTerminals)) {
                        endpoints.stream().forEach(p -> {
                            Boolean sipState = p.getSipState();
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
