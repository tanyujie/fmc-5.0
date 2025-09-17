package com.paradisecloud.fcm.smc2.core;


import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;

import com.paradisecloud.fcm.smc2.cache.Smc2BridgeCache;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2;
import com.paradisecloud.fcm.smc2.cache.Smc2Bridge;
import com.sinhy.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nj
 * @date 2022/8/30 15:31
 */
public class Smc2OnlineStatusMonitor extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(Smc2OnlineStatusMonitor.class);
    private final static long WAIT_TIME = 30 * 1000;


    private static final Smc2OnlineStatusMonitor INSTANCE = new Smc2OnlineStatusMonitor();


    public void init(BusiTerminalMapper busiTerminalMapper) {
        this.start();
    }

    private Smc2OnlineStatusMonitor() {
        super("SmcOnlineStatusMonitor");
    }

    public static Smc2OnlineStatusMonitor getInstance() {
        return INSTANCE;
    }

    @Override
    public void run() {

        LOGGER.info("SMC2终端在线状态监视器启动并初始化成功");
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
        AtomicInteger onlineNumber= new AtomicInteger();
        //查询所有会议室
        Map<Long, Smc2Bridge> smc2BridgeMap = Smc2BridgeCache.getInstance().getSmc2BridgeMap();
        smc2BridgeMap.forEach((aLong, smc2Bridge) -> {
            BusiMcuSmc2 busiSMC = smc2Bridge.getBusiSmc2();
            if(smc2Bridge.isAvailable()){
                smc2Bridge.getParams().put("onlineStatus", 1);
                busiSMC.setStatus(1);
                smc2Bridge.getParams().put("onlineStatus", 2);
            }else {
                smc2Bridge.getParams().put("onlineStatus", 2);
                busiSMC.setStatus(2);
            }
        });

        LOGGER.info("SMC2本轮检测所有在线状态，共在线数量：" + onlineNumber.get());
    }



}
