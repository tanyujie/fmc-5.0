package com.paradisecloud.smc3.monitor;

import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.cache.LicenseCache;
import com.paradisecloud.fcm.common.enumer.AttendType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.FcmThreadPool;
import com.paradisecloud.fcm.dao.model.BusiOperationLog;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.interfaces.IBusiOperationLogService;
import com.paradisecloud.fcm.service.ops.OpsDataCache;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.TerminalAttendeeSmc3;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextUtils;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.service.interfaces.IAttendeeSmc3Service;
import com.paradisecloud.smc3.task.InviteAttendeeSmc3Task;
import com.paradisecloud.smc3.task.Smc3DelayTaskService;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @author nj
 * @date 2023/9/8 16:47
 */
@Component
public class ConferenceMonitorThread extends Thread implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void run() {

        while (true) {

            try {
                Collection<Smc3ConferenceContext> values = Smc3ConferenceContextCache.getInstance().values();

                if (CollectionUtils.isNotEmpty(values)) {

                    for (Smc3ConferenceContext conferenceContext : values) {

                        if (conferenceContext != null && conferenceContext.isStart()) {

                            BaseAttendee minutesAttendee = conferenceContext.getMinutesAttendee();
                            if (minutesAttendee != null && minutesAttendee.isMeetingJoined()) {
                                Integer asrTime = LicenseCache.getInstance().getAsrTime();
                                if (OpsDataCache.getInstance().getAsrTime() != 0) {
                                    asrTime = OpsDataCache.getInstance().getAsrTime();
                                    if (asrTime == -1) {
                                        asrTime = null;
                                    }
                                }
                                if (asrTime != null) {
                                    long meetingJoinedTime = minutesAttendee.getMeetingJoinedTime();
                                    if (System.currentTimeMillis() - meetingJoinedTime > asrTime * 60000) {
                                        IAttendeeSmc3Service attendeeService = BeanFactory.getBean(IAttendeeSmc3Service.class);
                                        attendeeService.hangUp(conferenceContext.getId(), minutesAttendee.getId());
                                        if (conferenceContext.getAttendeeById(minutesAttendee.getId()) != null) {
                                            attendeeService.remove(conferenceContext.getId(), minutesAttendee.getId());
                                        }
                                        String text = "字幕使用时长" + asrTime + "分钟用完自动结束";
                                        conferenceContext.minutesLog("------" + text + "------");
                                        BusiOperationLog busiOperationLog = new BusiOperationLog();
                                        busiOperationLog.setActionDetails(text);
                                        busiOperationLog.setHistoryConferenceId(conferenceContext.getHistoryConference().getId());
                                        busiOperationLog.setUserId(null);
                                        busiOperationLog.setOperatorName("系统");
                                        busiOperationLog.setTime(new Date());
                                        busiOperationLog.setActionResult(2);
                                        busiOperationLog.setIp("127.0.0.1");
                                        busiOperationLog.setDeviceType("system");
                                        IBusiOperationLogService busiOperationLogService = BeanFactory.getBean(IBusiOperationLogService.class);
                                        busiOperationLogService.insertBusiOperationLog(busiOperationLog);
                                    }
                                }
                            }
                            if (conferenceContext.isAutoCallTerminal()) {

                                List<TerminalAttendeeSmc3> mqttJoinTerminals = new ArrayList<>();
                                Smc3ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
                                    if (a instanceof TerminalAttendeeSmc3) {
                                        TerminalAttendeeSmc3 ta = (TerminalAttendeeSmc3) a;
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
                                        if(!a.isMeetingJoined()){
                                            doCall(conferenceContext, a, null);
                                        }
                                    }
                                });
                            }

                        }

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Threads.sleep(30*1000);
            }

        }
    }

    private void doCall(Smc3ConferenceContext conferenceContext, AttendeeSmc3 a, BusiTerminal b) {
        if (a.isHangUp()) {
            return;
        }
        if(Objects.equals(ConstAPI.NORMAL,conferenceContext.getCategory())){
            try {
                Smc3DelayTaskService delayTaskService = BeanFactory.getBean(Smc3DelayTaskService.class);
                InviteAttendeeSmc3Task inviteAttendeesTask = new InviteAttendeeSmc3Task(conferenceContext.getConferenceNumber(), 200, conferenceContext, a);
                delayTaskService.addTask(inviteAttendeesTask);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("呼叫与会者发生异常-doCall：" + a, e);
                StringBuilder messageTip = new StringBuilder();
                messageTip.append("【").append(a.getName()).append("】呼叫失败：").append(e.getMessage());
                Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}