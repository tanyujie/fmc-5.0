package com.paradisecloud.fcm.smc2.monitor;

import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.message.terminal.TerminalOnlineStatusMessage;
import com.paradisecloud.fcm.common.message.terminal.TerminalOnlineStatusMessageQueue;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.sinhy.utils.ThreadUtils;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.*;
import com.suntek.smc.esdk.pojo.smc.DeviceStatusEnum;
import com.suntek.smc.esdk.service.client.SiteServiceEx;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nj
 * @date 2022/8/30 15:31
 */

public class Smc2SIPOnlineStatusMonitor extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(Smc2SIPOnlineStatusMonitor.class);
    private final static long WAIT_TIME = 15 * 1000;
    private static final Smc2SIPOnlineStatusMonitor INSTANCE = new Smc2SIPOnlineStatusMonitor();
    @Resource
    private BusiTerminalMapper busiTerminalMapper;


    private Smc2SIPOnlineStatusMonitor() {
        super("Smc2SIPOnlineStatusMonitor");
    }

    public static Smc2SIPOnlineStatusMonitor getInstance() {
        return INSTANCE;
    }



    @Override
    public void run() {

        LOGGER.info("SMC2SIP--在线状态监视器启动并初始化成功");
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
                LOGGER.info("SMC2SIP本轮检测所有终端在线状态，共耗时：" + spendTime);
            }
        }

    }


    private void checkTask() {
        AtomicInteger onlineNumber = new AtomicInteger();
        BusiTerminal terminalquery = new BusiTerminal();
        terminalquery.setType(TerminalType.SMC2_SIP.getId());
        List<BusiTerminal> busiTerminals = busiTerminalMapper.selectBusiTerminalList(terminalquery);
        getListTPSDKResponseWithPageEx(1,busiTerminals,onlineNumber);
        LOGGER.info("SMC终端本轮检测所有终端在线状态，共在线数量：" + onlineNumber.get());
    }

    private TPSDKResponseWithPageEx<List<TerminalInfoEx>> getListTPSDKResponseWithPageEx(Integer page,List<BusiTerminal> busiTerminals,AtomicInteger onlineNumber ) {
        QueryConfigEx queryConfig = new QueryConfigEx();
        PageParamEx pageParam = new PageParamEx();
        pageParam.setCurrentPage(page);
        pageParam.setNumberPerPage(10);
        queryConfig.setPageParam(pageParam);
        SiteServiceEx siteServiceEx = ServiceFactoryEx.getService(SiteServiceEx.class);
        TPSDKResponseWithPageEx<List<TerminalInfoEx>> result = siteServiceEx.querySitesInfoEx(queryConfig);
        Integer resultCode = result.getResultCode();
        PagesInfoEx pageInfo = result.getPageInfo();
        if (0 == resultCode)
        {
            //查询成功，则返回查询后的会场信息
            List<TerminalInfoEx> list = result.getResult();
            if (CollectionUtils.isNotEmpty(list) && CollectionUtils.isNotEmpty(busiTerminals)) {
                list.stream().forEach(p -> {
                    Boolean sipState = Objects.equals(p.getDeviceStatus().getSipStatus(), DeviceStatusEnum.DEVICE_STATUS_OK);
                    Optional<BusiTerminal> first = busiTerminals.stream().filter(b -> Objects.equals(b.getCredential(), p.getUri())).findFirst();
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

            if (pageInfo.getCurrentPage() < pageInfo.getTotalPages()) {
                Integer currentPage = pageInfo.getCurrentPage();
                currentPage++;
                getListTPSDKResponseWithPageEx(currentPage,busiTerminals,onlineNumber);

            }
        }
        return result;
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
