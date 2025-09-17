package com.paradisecloud.fcm.tencent.service2.impls;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.AesEnsUtils;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiOpsResourceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.tencent.busi.SyncInformation;
import com.paradisecloud.fcm.tencent.busi.attende.InvitedAttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.SelfCallAttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.TerminalAttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.*;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentTemplateConferenceMapper;
import com.paradisecloud.fcm.tencent.model.client.TencentConferenceCtrlClient;
import com.paradisecloud.fcm.tencent.model.client.TencentMeetingClient;
import com.paradisecloud.fcm.tencent.model.reponse.MeetingStatusResponse;
import com.paradisecloud.fcm.tencent.model.reponse.WaitingRoomResponse;
import com.paradisecloud.fcm.tencent.model.request.ModifyConferenceRequest;
import com.paradisecloud.fcm.tencent.model.request.QueryWaitingRoomRealRequest;
import com.paradisecloud.fcm.tencent.model.request.TencentDismissMeetingRequest;
import com.paradisecloud.fcm.tencent.model.request.WaitingRoomParticipantSetiingReq;
import com.paradisecloud.fcm.tencent.model.vo.WaitingRoomParticipantSetting;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentConferenceAppointmentService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentHistoryConferenceService;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiTencentConferenceService;
import com.paradisecloud.fcm.tencent.templateConference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.tencent.templateConference.StartTemplateConference;
import com.paradisecloud.fcm.tencent.utils.TencentConferenceContextUtils;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import com.tencentcloudapi.wemeet.models.meeting.CancelMeetingRequest;
import org.apache.http.NameValuePair;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * 会议相关服务类
 */
@Service
public class BusiTencentConferenceServiceImpl implements IBusiTencentConferenceService {
    private static final Logger logger = LoggerFactory.getLogger(BusiTencentConferenceServiceImpl.class);
    @Resource
    private BusiMcuTencentTemplateConferenceMapper busiMcuTencentTemplateConferenceMapper;
    @Resource
    private BusiMcuTencentConferenceAppointmentMapper busiMcuTencentConferenceAppointmentMapper;
    @Resource
    private IBusiMcuTencentConferenceAppointmentService busiMcuTencentConferenceAppointmentService;

    @Resource
    private IBusiMcuTencentHistoryConferenceService busiMcuTencentHistoryConferenceService;
    @Resource
    private BusiOpsResourceMapper busiOpsResourceMapper;
    /**
     * 开始模板会议
     *
     * @param templateConferenceId void
     * @return
     */
    @Override
    public String startTemplateConference(long templateConferenceId) {
        TencentConferenceContext TencentConferenceContext = new StartTemplateConference().startTemplateConference(templateConferenceId);
        return TencentConferenceContext.getContextKey();
    }

    /**
     * 开始会议
     *
     * @param templateConferenceId void
     */
    @Override
    public String startConference(long templateConferenceId) {
        return startTemplateConference(templateConferenceId);
    }

    /**
     * @param templateConferenceId
     * @return
     */
    @Override
    public TencentConferenceContext buildTemplateConferenceContext(long templateConferenceId) {
        return new BuildTemplateConferenceContext().buildTemplateConferenceContext(templateConferenceId);
    }

    /**
     * <pre>根据coSpaceId挂断会议</pre>
     *
     * @param encryptConferenceId void
     * @param endReasonsType
     * @author lilinhai
     * @since 2021-02-02 18:05
     */
    @Override
    public void endConference(String encryptConferenceId, int endReasonsType) {
        String contextKey = EncryptIdUtil.parasToContextKey(encryptConferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            conferenceContext.setEndReasonsType(endReasonsType);
            endConference(contextKey, endReasonsType, true, false);
        }


    }


    public void endConferenceType(String conferenceId, int endReasonsType) {
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().remove(conferenceId);
        if (conferenceContext != null) {
            try {
                // 设置结束状态
                conferenceContext.setEnd(true);
                conferenceContext.setEndTime(new Date());
                // 保存历史记录
                conferenceContext.getHistoryConference().setEndReasonsType(endReasonsType);
                BeanFactory.getBean(IBusiMcuTencentHistoryConferenceService.class).saveHistory(conferenceContext.getHistoryConference(), conferenceContext);
                // 会议结束推送mqtt
                try {
                   // pushEndMessageToMqtt(conferenceId, conferenceContext);
                } catch (Exception e) {
                    logger.error("结束会议时取消会议当前操作失败", e);
                }

                BusiMcuTencentConferenceAppointment busiConferenceAppointment = conferenceContext.getConferenceAppointment();
                if (busiConferenceAppointment != null) {
                    busiConferenceAppointment.setIsHangUp(YesOrNo.YES.getValue());
                    busiConferenceAppointment.setIsStart(null);
                    busiConferenceAppointment.setExtendMinutes(null);
                    busiMcuTencentConferenceAppointmentMapper.updateBusiMcuTencentConferenceAppointment(busiConferenceAppointment);

                    AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiConferenceAppointment.getRepeatRate());
                    if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM) {
                        busiMcuTencentConferenceAppointmentService.deleteBusiMcuTencentConferenceAppointmentById(busiConferenceAppointment.getId());
                    }
                }
                BusiHistoryConference busiHistoryConference = conferenceContext.getHistoryConference();
                if (busiHistoryConference != null) {
                    busiHistoryConference.setConferenceEndTime(new Date());
                    busiHistoryConference.setEndReasonsType(conferenceContext.getEndReasonsType());
                    busiMcuTencentHistoryConferenceService.saveHistory(busiHistoryConference, conferenceContext);

                }
                BusiMcuTencentTemplateConference busiSmc2TemplateConference = busiMcuTencentTemplateConferenceMapper.selectBusiMcuTencentTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                if (busiSmc2TemplateConference != null) {
                    busiSmc2TemplateConference.setUpdateTime(new Date());
                    busiSmc2TemplateConference.setConferenceNumber(null);
                    busiMcuTencentTemplateConferenceMapper.updateBusiMcuTencentTemplateConference(busiSmc2TemplateConference);

                }

                String sn = conferenceContext.getSn();
                if(Strings.isNotBlank(sn)){
                    BusiOpsResource busiOpsResource = new BusiOpsResource();
                    busiOpsResource.setSn(conferenceContext.getSn());
                    List<BusiOpsResource> busiOpsResources = busiOpsResourceMapper.selectBusiOpsResourceList(busiOpsResource);
                    if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(busiOpsResources)){
                        BusiOpsResource busiOpsResource1 = busiOpsResources.get(0);
                        long startTime = conferenceContext.getStartTime().getTime();
                        long endTime = conferenceContext.getEndTime().getTime();
                        busiOpsResource1.setUsedTime( Integer.valueOf((endTime-startTime)/60000+""));
                        busiOpsResource1.setUpdateTime(new Date());
                        busiOpsResourceMapper.updateBusiOpsResource(busiOpsResource1);
                    }
                }

                pushEndMessageToMqtt(conferenceContext.getConferenceNumber(), conferenceContext);
                TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_ENDED, "会议已结束");
                TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已结束");

                TencentConferenceContextCache.getInstance().remove(conferenceContext.getId());
                TencentConferenceContextCache.getInstance().remove(conferenceContext.getMeetingId());
                conferenceContext.clear();
            } catch (Throwable e) {
                logger.error("结束会议失败: " + conferenceContext.getAccessCode(), e);
            }
        }
    }


    /**
     * <pre>根据coSpaceId挂断会议</pre>
     *
     * @param contextKey void
     * @param endReasonsType
     * @author lilinhai
     * @since 2021-02-02 18:05
     */
    @Override
    public void endConference(String contextKey, int endReasonsType, boolean forceEnd, boolean pushMessage) {
        TencentConferenceContext cc = TencentConferenceContextCache.getInstance().get(contextKey);
        if (cc != null) {
            try {
                //TencentBridge bridge = TencentBridgeCache.getInstance().getAvailableBridgesByDept(cc.getDeptId());
                TencentBridge bridge = cc.getTencentBridge();
                if (bridge == null) {
                    throw new CustomException("会议桥不存在");
                }
                TencentMeetingClient meeting_client = bridge.getMEETING_CLIENT();

                try {
                    TencentDismissMeetingRequest dismissMeetingRequest = new TencentDismissMeetingRequest();
                    dismissMeetingRequest.setMeetingId(cc.getMeetingId());
                    dismissMeetingRequest.setUserId(cc.getTencentUser());
                    dismissMeetingRequest.setInstanceId(cc.getInstanceid());
                    dismissMeetingRequest.setReasonCode(endReasonsType);
                    meeting_client.dismissMeeting(dismissMeetingRequest);
                } catch (WemeetSdkException e) {
                    logger.info(e.getMessage());
                }

                try {
                    CancelMeetingRequest cancelMeetingRequest = new CancelMeetingRequest();
                    cancelMeetingRequest.setMeetingId(cc.getMeetingId());
                    cancelMeetingRequest.setUserId(cc.getTencentUser());
                    cancelMeetingRequest.setInstanceId(cc.getInstanceid());
                    cancelMeetingRequest.setReasonCode(endReasonsType);
                    meeting_client.cancelMeeting(cancelMeetingRequest);
                } catch (WemeetSdkException e) {
                    logger.info(e.getMessage());
                }
            } catch (Throwable e) {
                logger.error("Tencent mcu endConference-error", e);
            } finally {
                endConferenceType(contextKey, endReasonsType);

                StringBuilder infoBuilder = new StringBuilder();
                infoBuilder.append("结束会议【").append(cc.getName()).append("】");
                infoBuilder.append(", 会议号码: ").append(cc.getAccessCode());
                TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, infoBuilder);
            }
        }

    }

    @Override
    public void endConference(String encryptConferenceId, int endType, int EndReasonsType) {
        String contextKey = EncryptIdUtil.parasToContextKey(encryptConferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            conferenceContext.setEndReasonsType(EndReasonsType);
            endConference(contextKey, EndReasonsType, true, false);
        }
    }

    /**
     * <pre>会议结束推送mqtt</pre>
     *
     * @param conferenceNumber
     * @param conferenceContext void
     * @author sinhy
     * @since 2021-12-12 15:02
     */
    private void pushEndMessageToMqtt(String conferenceNumber, TencentConferenceContext conferenceContext) {
        List<BaseAttendee> mqttJoinTerminals = new ArrayList<>();
        TencentConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeTencent) {
                TerminalAttendeeTencent ta = (TerminalAttendeeTencent) a;
                BusiTerminal bt = TerminalCache.getInstance().get(ta.getTerminalId());
                if (!ObjectUtils.isEmpty(bt.getSn())) {
                    mqttJoinTerminals.add(ta);
                }
            }
        });

        BeanFactory.getBean(IMqttService.class).endConference(conferenceNumber, mqttJoinTerminals, new ArrayList<>(conferenceContext.getLiveTerminals()), conferenceContext);
    }

    /**
     * 结束会议
     *
     * @param encryptConferenceId
     * @author lilinhai
     * @since 2021-02-28 15:19
     */
    @Override
    public void endConference(String encryptConferenceId) {
        endConference(encryptConferenceId, ConferenceEndType.COMMON.getValue());
    }

    /**
     * <pre>会议讨论</pre>
     *
     * @param conferenceId void
     * @author lilinhai
     * @since 2021-04-25 14:07
     */
    @Override
    public void discuss(String conferenceId) {

    }

    /**
     * <pre>锁定会议</pre>
     *
     * @param conferenceId void
     * @param locked
     * @author lilinhai
     * @since 2021-04-27 16:22
     */
    @Override
    public void lock(String conferenceId, Boolean locked) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return ;
        }
        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();

        ModifyConferenceRequest modifyConferenceRequest = new ModifyConferenceRequest();
        modifyConferenceRequest.setInstanceid(1);
        modifyConferenceRequest.setOperatorIdType(4);
        modifyConferenceRequest.setOperatorId(conferenceContext.getMsopenid());
        modifyConferenceRequest.setMeetingId(conferenceContext.getMeetingId());
        modifyConferenceRequest.setMeetingLocked(locked);

        try {
            MeetingStatusResponse meetingStatusResponse = conferenceCtrlClient.modifyConferenceStatus(modifyConferenceRequest);
            conferenceContext.setLock(locked);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("locked",locked);
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
        } catch (WemeetSdkException e) {
            logger.info("conferenceCtrlClient modifyConferenceStatus error "+e.getMessage());
            throw new CustomException(e.getMessage());
        }

    }

    /**
     * 修改call
     *
     * @param conferenceNumber
     * @param nameValuePairs   void
     * @author lilinhai
     * @since 2021-04-28 12:28
     */
    @Override
    public void updateCall(String conferenceNumber, List<NameValuePair> nameValuePairs) {

    }

    @Override
    public boolean updateCallRecordStatus(String conferenceNumber, Boolean record) {
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(conferenceNumber);
        if (conferenceContext != null) {
            boolean recorded = conferenceContext.isRecorded();
            if (recorded && record) {
                throw new SystemException("正在录制中！");
            }
            if (recorded != record) {

            }
        }
        return false;
    }

    /**
     * <pre>延长会议时间</pre>
     *
     * @param conferenceId
     * @param minutes      void
     * @return
     * @author lilinhai
     * @since 2021-05-27 16:59
     */
    @Override
    public BusiMcuTencentConferenceAppointment extendMinutes(String conferenceId, int minutes) {
        return null;
    }

    /**
     * <pre>取消会议讨论</pre>
     *
     * @param conferenceId void
     * @author sinhy
     * @since 2021-08-17 12:26
     */
    @Override
    public void cancelDiscuss(String conferenceId) {

    }

    /**
     * <pre>直播</pre>
     *
     * @param conferenceId
     * @param enabled
     * @param streamUrl
     * @author sinhy
     * @since 2021-08-17 16:58
     */
    @Override
    public void stream(String conferenceId, Boolean enabled, String streamUrl) {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(conferenceNumber);
        if (conferenceContext != null) {
            if (!enabled) {
                if (conferenceContext.getIsAutoCreateStreamUrl() == 2) {
                    LiveBridgeCache.getInstance().setLiveUrlTerminalCount(conferenceContext.getStreamingUrl(), 0);
                    LiveBridgeCache.getInstance().setLiveConferenceTerminalCount(conferenceContext.getId(), 0);
                } else {
                    Long deptId = conferenceContext.getDeptId();
                    BusiLiveDept busiLiveDept = LiveDeptCache.getInstance().get(deptId);
                    if (busiLiveDept != null) {
                        if (busiLiveDept.getLiveType() == 1) {
                            LiveBridgeCache.getInstance().setLiveUrlTerminalCount(conferenceContext.getStreamingUrl(), 0);
                            LiveBridgeCache.getInstance().setLiveConferenceTerminalCount(conferenceContext.getId(), 0);
                        } else {
                            List<String> streamUrlList = conferenceContext.getStreamUrlList();
                            if (streamUrlList != null && streamUrlList.size() > 0) {
                                for (String url : streamUrlList) {
                                    LiveBridgeCache.getInstance().setLiveUrlTerminalCount(url, 0);
                                }
                            }
                            LiveBridgeCache.getInstance().setLiveConferenceTerminalCount(conferenceContext.getId(), 0);
                        }
                    }
                }
            }

            if (enabled) {

            } else {

            }
//
        }
    }

    /**
     * 直播
     *
     * @param mainConferenceContext
     * @param streaming
     * @param streamUrl
     * @author sinhy
     * @since 2021-08-17 16:29
     */
    @Override
    public void stream(TencentConferenceContext mainConferenceContext, Boolean streaming, String streamUrl) {

    }

    /**
     * <pre>允许所有人静音自己</pre>
     *
     * @param conferenceId
     * @param enabled      void
     * @author sinhy
     * @since 2021-08-17 18:18
     */
    @Override
    public void allowAllMuteSelf(String conferenceId, Boolean enabled) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return ;
        }

        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();

        ModifyConferenceRequest modifyConferenceRequest = new ModifyConferenceRequest();
        modifyConferenceRequest.setInstanceid(1);
        modifyConferenceRequest.setOperatorIdType(4);
        modifyConferenceRequest.setOperatorId(conferenceContext.getMsopenid());
        modifyConferenceRequest.setMeetingId(conferenceContext.getMeetingId());
        modifyConferenceRequest.setAllowUnmuteBySelf(enabled);

        try {
            MeetingStatusResponse meetingStatusResponse = conferenceCtrlClient.modifyConferenceStatus(modifyConferenceRequest);
            conferenceContext.setAllowUnmuteBySelf(enabled);
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,meetingStatusResponse);
        } catch (WemeetSdkException e) {
            logger.info("conferenceCtrlClient modifyConferenceStatus error "+e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * <pre>允许辅流控制</pre>
     *
     * @param conferenceId
     * @param enabled      void
     * @author sinhy
     * @since 2021-08-18 10:14
     */
    @Override
    public void allowAllPresentationContribution(String conferenceId, Boolean enabled) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return ;
        }

        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();

        ModifyConferenceRequest modifyConferenceRequest = new ModifyConferenceRequest();
        modifyConferenceRequest.setInstanceid(1);
        modifyConferenceRequest.setOperatorIdType(4);
        modifyConferenceRequest.setOperatorId(conferenceContext.getMsopenid());
        modifyConferenceRequest.setMeetingId(conferenceContext.getMeetingId());
        modifyConferenceRequest.setShareScreen(enabled);

        try {
            MeetingStatusResponse meetingStatusResponse = conferenceCtrlClient.modifyConferenceStatus(modifyConferenceRequest);
            conferenceContext.setShareScreen(enabled);
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,meetingStatusResponse);
        } catch (WemeetSdkException e) {
            logger.info("conferenceCtrlClient modifyConferenceStatus error "+e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * 新加入用户静音
     *
     * @param conferenceId
     * @param enabled      void
     * @author sinhy
     * @since 2021-08-18 10:15
     */
    @Override
    public void joinAudioMuteOverride(String conferenceId, Boolean enabled) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return ;
        }

        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();

        ModifyConferenceRequest modifyConferenceRequest = new ModifyConferenceRequest();
        modifyConferenceRequest.setInstanceid(1);
        modifyConferenceRequest.setOperatorIdType(4);
        modifyConferenceRequest.setOperatorId(conferenceContext.getMsopenid());
        modifyConferenceRequest.setMeetingId(conferenceContext.getMeetingId());
        modifyConferenceRequest.setParticipantJoinMute(enabled?1:0);

        try {
            MeetingStatusResponse meetingStatusResponse = conferenceCtrlClient.modifyConferenceStatus(modifyConferenceRequest);
            conferenceContext.setParticipantJoinMute(enabled?1:0);
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,meetingStatusResponse);
        } catch (WemeetSdkException e) {
            logger.info("conferenceCtrlClient modifyConferenceStatus error "+e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * 获取显示模板
     *
     * @param deptId
     * @return
     */
    @Override
    public List<Map<String, String>> getLayoutTemplates(Long deptId) {

        return null;

    }

    @Override
    public JSONObject getLayoutTemplate(Long deptId, String name) {
        return null;
    }

    /**
     * <pre>一键呼入</pre>
     *
     * @param conferenceId void
     * @author sinhy
     * @since 2021-09-27 21:47
     */
    @Override
    public void reCall(String conferenceId) {

    }

    /**
     * <pre>同步会议数据</pre>
     *
     * @param conferenceId void
     * @author sinhy
     * @since 2021-08-20 10:46
     */
    @Override
    public void sync(String conferenceId) {
        final String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(conferenceNumber);
        if (conferenceContext.getSyncInformation() != null) {
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始1！");
            return;
        }

        synchronized (conferenceContext.getSyncLock()) {
            if (conferenceContext.getSyncInformation() != null) {
                TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始2！");
                return;
            }

            SyncInformation syncInformation = new SyncInformation();
            conferenceContext.setSyncInformation(syncInformation);

            int totalCount = 0;
            syncInformation.setInProgress(true);
            syncInformation.setTotalCallCount(0);
            syncInformation.setReason("同步");

            logger.info("One click synchronization start：" + conferenceContext.getConferenceNumber());

            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "开始同步本会议的参会信息！");
            Set<String> attendeeIds = new HashSet<>();
            TencentConferenceContextUtils.eachAttendeeInConference(conferenceContext, (attendee) -> {
                attendeeIds.add(attendee.getId());
                if (attendee.isMeetingJoined()) {
                    synchronized (attendee) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeTencent) {
                            TerminalAttendeeTencent terminalAttendee = (TerminalAttendeeTencent) attendee;
                            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                            terminalAttendee.setTerminalType(bt.getType());
                            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                            if (attendee.isMeetingJoined()) {
                                terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            } else {
                                terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                            }
                        } else if (attendee instanceof InvitedAttendeeTencent) {
                            InvitedAttendeeTencent invitedAttendee = (InvitedAttendeeTencent) attendee;
                            if (invitedAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(invitedAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    invitedAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    invitedAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        } else if (attendee instanceof SelfCallAttendeeTencent) {
                            SelfCallAttendeeTencent selfCallAttendee = (SelfCallAttendeeTencent) attendee;
                            if (selfCallAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(selfCallAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    selfCallAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    selfCallAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        }
                        TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                    }
                } else {
                    synchronized (attendee) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeTencent) {
                            TerminalAttendeeTencent terminalAttendee = (TerminalAttendeeTencent) attendee;
                            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                            terminalAttendee.setTerminalType(bt.getType());
                            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                            if (attendee.isMeetingJoined()) {
                                terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            } else {
                                terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                            }
                        } else if (attendee instanceof InvitedAttendeeTencent) {
                            InvitedAttendeeTencent invitedAttendee = (InvitedAttendeeTencent) attendee;
                            if (invitedAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(invitedAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    invitedAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    invitedAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        } else if (attendee instanceof SelfCallAttendeeTencent) {
                            SelfCallAttendeeTencent selfCallAttendee = (SelfCallAttendeeTencent) attendee;
                            if (selfCallAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(selfCallAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    selfCallAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    selfCallAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        }
                        TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                    }
                }
            });

            totalCount = attendeeIds.size();

            syncInformation.setTotalCallCount(totalCount);
            syncInformation.setCurrentCallTotalParticipantCount(totalCount);
            syncInformation.setCurrentCallFmeIp(TencentBridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId().intValue()).getBusiTencent().getAppId());
            syncInformation.setSyncCurrentCallParticipantCount(totalCount);



            syncInformation.setInProgress(false);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("attendeeCountingStatistics",conferenceContext.getAttendeeCountingStatistics());
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.PARTICIPANT_SYNC, syncInformation);
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "已同步完所有参会信息，共【" + totalCount + "】个！");
            conferenceContext.setSyncInformation(null);
        }
    }

    @Override
    public Integer getLiveTerminalCount(String conferenceId) {
        Integer liveConferenceTerminalCount = LiveBridgeCache.getInstance().getLiveConferenceTerminalCount(conferenceId);
        return liveConferenceTerminalCount;
    }

    @Override
    public void setMute(String conferenceId, Boolean mute) {
        final String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(conferenceNumber);


        String confId = conferenceContext.getMeetingId();
        List<String> list = new ArrayList<String>();
//        List<SmcParitipantsStateRep.ContentDTO> content = conferenceContext.getContent();
//        if (!CollectionUtils.isEmpty(content)) {
//            for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {
//                if (contentDTO.getState().getOnline()) {
//                    list.add(contentDTO.getGeneralParam().getUri());
//                }
//            }
//        }
        //是否闭音。
        //0：不闭音
        //1：闭音
        int isMute = 0;
        if (mute) {
            isMute = 1;
        }


    }

    @Override
    public void meetingstatus(ModifyConferenceRequest request) {
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(request.getMeetingId());
        if (conferenceContext == null) {
            return ;
        }

        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();

        ModifyConferenceRequest modifyConferenceRequest = new ModifyConferenceRequest();
        modifyConferenceRequest.setInstanceid(1);
        modifyConferenceRequest.setOperatorIdType(4);
        modifyConferenceRequest.setOperatorId(conferenceContext.getMsopenid());
        modifyConferenceRequest.setMeetingId(request.getMeetingId());

        JSONObject jsonObject = new JSONObject();


        if(request.getMuteAll()!=null){
            modifyConferenceRequest.setMuteAll(request.getMuteAll());
            conferenceContext.setMuteAll(request.getMuteAll());
        }
        if(request.getAllowUnmuteBySelf()!=null){
            modifyConferenceRequest.setAllowUnmuteBySelf(request.getAllowUnmuteBySelf());
            conferenceContext.setAllowUnmuteBySelf(request.getAllowUnmuteBySelf());
            Boolean muteAll = conferenceContext.getMuteAll();
            modifyConferenceRequest.setMuteAll(muteAll);
            jsonObject.put("allowUnmuteBySelf",request.getAllowUnmuteBySelf());
        }

        if(request.getParticipantJoinMute()!=null){
            modifyConferenceRequest.setParticipantJoinMute(request.getParticipantJoinMute());
            conferenceContext.setParticipantJoinMute(request.getParticipantJoinMute());
            jsonObject.put("participantJoinMute",request.getParticipantJoinMute());
        }

        if(request.getMeetingLocked()!=null){
            modifyConferenceRequest.setMeetingLocked(request.getMeetingLocked());
            conferenceContext.setLock(request.getMeetingLocked());
            jsonObject.put("locked",request.getMeetingLocked());
        }

        if(request.getHideMeetingCodePassword()!=null){
            modifyConferenceRequest.setHideMeetingCodePassword(request.getHideMeetingCodePassword());
            conferenceContext.setHideMeetingCodePassword(request.getHideMeetingCodePassword());
            jsonObject.put("hideMeetingCodePassword",request.getHideMeetingCodePassword());
        }

        if(request.getAllowChat()!=null){
            modifyConferenceRequest.setAllowChat(request.getAllowChat());
            conferenceContext.setAllowChat(request.getAllowChat());
            jsonObject.put("allowChat",request.getAllowChat());
        }
        if(request.getShareScreen()!=null){
            modifyConferenceRequest.setShareScreen(request.getShareScreen());
            conferenceContext.setShareScreen(request.getShareScreen());
            jsonObject.put("shareScreen",request.getShareScreen());
        }
        if(request.getEnableRedEnvelope()!=null){
            modifyConferenceRequest.setEnableRedEnvelope(request.getEnableRedEnvelope());
            conferenceContext.setEnableRedEnvelope(request.getEnableRedEnvelope());
        }
        if(request.getOnlyEnterpriseUserAllowed()!=null){
            modifyConferenceRequest.setOnlyEnterpriseUserAllowed(request.getOnlyEnterpriseUserAllowed());
            conferenceContext.setOnlyEnterpriseUserAllowed(request.getOnlyEnterpriseUserAllowed());
            jsonObject.put("onlyEnterpriseUserAllowed",request.getOnlyEnterpriseUserAllowed());
        }

        if(request.getPlayIvrOnJoin()!=null){
            modifyConferenceRequest.setPlayIvrOnJoin(request.getPlayIvrOnJoin());
            conferenceContext.setPlayIvrOnJoin(request.getPlayIvrOnJoin());
            jsonObject.put("playIvrOnJoin",request.getPlayIvrOnJoin());
        }
        if(request.getAutoWaitingRoom()!=null){
            if(request.getAutoWaitingRoom()){
               //todo
            }
            modifyConferenceRequest.setAutoWaitingRoom(request.getAutoWaitingRoom());
            conferenceContext.setAutoWaitingRoom(request.getAutoWaitingRoom());
            jsonObject.put("autoWaitingRoom",request.getAutoWaitingRoom());
        }


        try {
            MeetingStatusResponse meetingStatusResponse = conferenceCtrlClient.modifyConferenceStatus(modifyConferenceRequest);
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
        } catch (WemeetSdkException e) {
            logger.info("conferenceCtrlClient modifyConferenceStatus error "+e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }


    @Override
    public WaitingRoomResponse queryWaitingRoomParticipants(String conferenceId, int page, int pageSize) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        TencentMeetingClient meeting_client = conferenceContext.getTencentBridge().getMEETING_CLIENT();

        QueryWaitingRoomRealRequest request = new QueryWaitingRoomRealRequest();
        request.setMeetingId(conferenceContext.getMeetingId());
        request.setUserid(conferenceContext.getTencentUser());
        request.setPage(page);
        request.setPageSize(pageSize);
        try {
            WaitingRoomResponse waitingRoomResponse = meeting_client.queryWaitingRoomParticipants(request);
            return waitingRoomResponse;
        } catch (WemeetSdkException e) {
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    public void waitingRoomParticipantSetting(String conferenceId, WaitingRoomParticipantSetting waitingRoomParticipantSetting) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(contextKey);
        TencentMeetingClient meeting_client = conferenceContext.getTencentBridge().getMEETING_CLIENT();
        List<WaitingRoomParticipantSetting.UsersDTO> usersDTOS = waitingRoomParticipantSetting.getUsers();
        if(CollectionUtils.isEmpty(usersDTOS)){
            return ;
        }
        WaitingRoomParticipantSetiingReq waitingRoomParticipantSetiingReq = new WaitingRoomParticipantSetiingReq();
        waitingRoomParticipantSetiingReq.setMeetingId(conferenceContext.getMeetingId());
        waitingRoomParticipantSetiingReq.setInstanceid(1);
        waitingRoomParticipantSetiingReq.setOperatorIdType(4);
        waitingRoomParticipantSetiingReq.setOperatorId(conferenceContext.getMsopenid());
        waitingRoomParticipantSetiingReq.setOperateType(waitingRoomParticipantSetting.getOperateType());
        if(waitingRoomParticipantSetting.getAllowRejoin()!=null){
            waitingRoomParticipantSetiingReq.setAllowRejoin(waitingRoomParticipantSetting.getAllowRejoin());
        }
        List<WaitingRoomParticipantSetiingReq.UserDTO> users=new ArrayList<>();
        waitingRoomParticipantSetiingReq.setUsers(users);
        for (WaitingRoomParticipantSetting.UsersDTO userDTOREQ : usersDTOS) {
            WaitingRoomParticipantSetiingReq.UserDTO userDTO = new WaitingRoomParticipantSetiingReq.UserDTO();
            userDTO.setInstanceid(userDTOREQ.getInstanceid());
            userDTO.setToOperatorIdType(4);
            userDTO.setToOperatorId(userDTOREQ.getMsOpenId());
            users.add(userDTO);
        }
        try {
            meeting_client.waitingRoomParticipantSetiing(waitingRoomParticipantSetiingReq);
        } catch (WemeetSdkException e) {
            throw new CustomException(e.getMessage());
        }
    }
}
