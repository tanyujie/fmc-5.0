package com.paradisecloud.fcm.smc2.monitor;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.enumer.AttendType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.FcmThreadPool;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.smc2.cache.*;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.TerminalAttendeeSmc2;
import com.paradisecloud.fcm.smc2.utils.Smc2ConferenceContextUtils;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.SiteAccessInfoEx;
import com.suntek.smc.esdk.pojo.local.SiteInfoEx;
import com.suntek.smc.esdk.pojo.local.TPSDKResponseEx;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.paradisecloud.fcm.smc2.task.InviteAttendeeSmc2Task.Max_INT;
import static io.swagger.v3.oas.integration.StringOpenApiConfigurationLoader.LOGGER;

/**
 * @author nj
 * @date 2022/9/8 16:47
 */
@Component
public class ConferenceAutoCallMonitorThread extends Thread implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void run() {

        while (true) {

            try {
                Collection<Smc2ConferenceContext> values = Smc2ConferenceContextCache.getInstance().values();

                if (CollectionUtils.isNotEmpty(values)) {

                    for (Smc2ConferenceContext conferenceContext : values) {

                        if (conferenceContext != null && conferenceContext.isStart()) {
                            if (conferenceContext.isAutoCallTerminal()) {

                                List<TerminalAttendeeSmc2> mqttJoinTerminals = new ArrayList<>();
                                Smc2ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
                                    if (a instanceof TerminalAttendeeSmc2) {
                                        TerminalAttendeeSmc2 ta = (TerminalAttendeeSmc2) a;
                                        BusiTerminal bt = TerminalCache.getInstance().get(ta.getTerminalId());

                                        // 被叫由会控负责发起呼叫
                                        if (AttendType.convert(ta.getAttendType()) == AttendType.OUT_BOUND) {
                                            if (!ta.isMeetingJoined()) {
                                                FcmThreadPool.exec(() ->
                                                        doCall(conferenceContext, a, bt)
                                                );

                                            }
                                        }
                                        if (AttendType.convert(ta.getAttendType()) == AttendType.AUTO_JOIN) {
                                            if (!ObjectUtils.isEmpty(bt.getSn())) {
                                                if (!ta.isMeetingJoined()) {
                                                    // 自动主叫终端由mqtt下发入会邀请后主叫入会
                                                    mqttJoinTerminals.add(ta);
                                                }
                                            }
                                        }
                                    } else {
                                        if (!a.isHangUp()&&!a.isMeetingJoined()) {
                                            doCall(conferenceContext, a, null);
                                        }
                                    }
                                });
                            }

                        }

                    }

                }
            } catch (Exception e) {
                logger.info(e.getMessage());
            } finally {
                Threads.sleep(3000);
            }

        }
    }

    private boolean doCall(Smc2ConferenceContext conferenceContext, AttendeeSmc2 a, BusiTerminal b) {
        Smc2Bridge telepBridge = Smc2BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
        if (telepBridge == null) {
            return true;
        }

        try {
            SiteInfoEx siteInfo = new SiteInfoEx();
            siteInfo.setUri(a.getRemoteParty());
            siteInfo.setName(a.getName());
            siteInfo.setType(7);
            ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
            TPSDKResponseEx<List<SiteAccessInfoEx>> result = conferenceServiceEx.addSiteToConfEx(conferenceContext.getSmc2conferenceId(), siteInfo, null);
            if (result.getResultCode() != 0) {
                if (result.getResultCode() == Max_INT) {
                    throw new CustomException("超出最大与会方数，添加会场失败");
                }
                throw new CustomException("添加与会者失败");
            }
            LOGGER.info("会场：" + a.getRemoteParty() + "添加结果：" + result.getResultCode());
        } catch (Exception e) {
            logger.error("呼叫与会者发生异常-doCall：" + a, e);
            StringBuilder messageTip = new StringBuilder();
            messageTip.append("【").append(a.getName()).append("】呼叫失败：").append(e.getMessage());
            Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
        }
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}