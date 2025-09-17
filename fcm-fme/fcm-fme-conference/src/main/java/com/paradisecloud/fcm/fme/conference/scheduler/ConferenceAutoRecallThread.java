package com.paradisecloud.fcm.fme.conference.scheduler;

import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.cache.LicenseCache;
import com.paradisecloud.fcm.common.enumer.AttendType;
import com.paradisecloud.fcm.common.enumer.StreamingEnabledType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.dao.model.BusiOperationLog;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.utils.ConferenceContextUtils;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.InvitedAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.McuAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.service.interfaces.IBusiOperationLogService;
import com.paradisecloud.fcm.service.model.CloudConference;
import com.paradisecloud.fcm.service.ops.OpsDataCache;
import com.paradisecloud.fcm.service.util.TencentCloudUtil;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author nj
 * @date 2024/7/10
 */
@Component
public class ConferenceAutoRecallThread extends Thread implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IAttendeeService attendeeService;

    @Override
    public void run() {

        while (true) {


            try {
                Collection<ConferenceContext> values = ConferenceContextCache.getInstance().values();

                if (CollectionUtils.isNotEmpty(values)) {

                    for (ConferenceContext conferenceContext : values) {

                        if (conferenceContext != null && conferenceContext.isStart()) {
                            //自动挂断腾讯会议
                            try {
                                Integer tencentTime = LicenseCache.getInstance().getTencentTime();
                                if (OpsDataCache.getInstance().getTencentTime() != 0) {
                                    tencentTime = OpsDataCache.getInstance().getTencentTime();
                                }
                                if (tencentTime != null && tencentTime > 0) {

                                    List<CloudConference> cloudConferenceList = conferenceContext.getCloudConferenceList();

                                    Iterator<CloudConference> iterator = cloudConferenceList.iterator();
                                    while (iterator.hasNext()) {
                                        CloudConference conference = iterator.next();

                                        String cascadeMcuType = conference.getCascadeMcuType();
                                        if (Objects.equals("mcu-tencent", cascadeMcuType)) {

                                            String conferenceNumber = conference.getConferenceNumber();

                                            Attendee attendeeByRemoteParty = conferenceContext.getAttendeeByRemoteParty(conferenceNumber + "@" + getMraIp());
                                            if (attendeeByRemoteParty != null) {
                                                long meetingJoinedTime1 = attendeeByRemoteParty.getMeetingJoinedTime();
                                                if (System.currentTimeMillis() - meetingJoinedTime1 > tencentTime * 60000) {
                                                    String text = "腾讯会议使用时长" + tencentTime + "分钟用完自动结束";
                                                    WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, text);
                                                    TencentCloudUtil.endConference(conference);
                                                    iterator.remove();
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
                                    }


                                }
                            } catch (Exception e) {
                                logger.info("自动挂断腾讯会议错误" + e.getMessage());
                            }
                            // 自动挂断云直播
                            try {
                                Integer cloudLiveTime = LicenseCache.getInstance().getCloudLiveTime();
                                if (OpsDataCache.getInstance().getCloudLiveTime() != 0) {
                                    cloudLiveTime = OpsDataCache.getInstance().getCloudLiveTime();
                                }
                                if (cloudLiveTime != null && cloudLiveTime > 0) {
                                    if (conferenceContext.getStreamingEnabled() == StreamingEnabledType.CLOUDS.getValue()) {
                                        if (conferenceContext.isStreaming() && System.currentTimeMillis() - conferenceContext.getStreamingStartTime() > cloudLiveTime * 60000) {
                                            IBusiConferenceService busiConferenceService = BeanFactory.getBean(IBusiConferenceService.class);
                                            busiConferenceService.stream(conferenceContext.getId(), false, conferenceContext.getCloudsStreamingUrl());
                                            String text = "云直播使用时长" + cloudLiveTime + "分钟用完自动结束";
                                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_SHOW_TIP, text);
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
                            } catch (Exception e) {
                                logger.info("自动挂断云直播错误" + e.getMessage());
                            }

                            // 离线重邀
                            if (conferenceContext.isAutoCallTerminal()) {
                                List<Attendee> outBoundTerminals = new ArrayList<>();
                                ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
                                    if (a instanceof TerminalAttendee) {
                                        TerminalAttendee ta = (TerminalAttendee) a;

                                        // 被叫由会控负责发起呼叫
                                        if (AttendType.convert(ta.getAttendType()) == AttendType.OUT_BOUND) {
                                            if (!ta.isMeetingJoined() && !ta.isHangUp()) {
                                                outBoundTerminals.add(ta);
                                            }
                                        }
                                    } else if (a instanceof InvitedAttendee) {
                                        InvitedAttendee ia = (InvitedAttendee) a;

                                        if (!ia.isMeetingJoined() && !ia.isHangUp()) {
                                            outBoundTerminals.add(ia);
                                        }
                                    }
                                });
                                List<McuAttendee> mcuAttendees = conferenceContext.getMcuAttendees();
                                if(CollectionUtils.isNotEmpty(mcuAttendees)){
                                    for (McuAttendee mcuAttendee : mcuAttendees) {
                                        boolean meetingJoined = mcuAttendee.isMeetingJoined();
                                        if(!meetingJoined){
                                            outBoundTerminals.add(mcuAttendee);
                                        }
                                    }
                                }

                                for (Attendee attendee : outBoundTerminals) {
                                    logger.info("发起自动重呼");
                                    attendeeService.recall(conferenceContext.getId(), attendee.getId());
                                }
                            }
                        }

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Threads.sleep(1000 * 30);
            }

        }
    }

    public String getMraIp() {
        Set<String> mraIpList = ExternalConfigCache.getInstance().getMRAIpList();
        Iterator<String> iterator = mraIpList.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return "1.13.136.2";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}