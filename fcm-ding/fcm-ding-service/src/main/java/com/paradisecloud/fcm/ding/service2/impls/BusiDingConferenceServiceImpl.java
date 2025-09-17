package com.paradisecloud.fcm.ding.service2.impls;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.ding.busi.SyncInformation;
import com.paradisecloud.fcm.ding.busi.attende.InvitedAttendeeDing;
import com.paradisecloud.fcm.ding.busi.attende.SelfCallAttendeeDing;
import com.paradisecloud.fcm.ding.busi.attende.TerminalAttendeeDing;
import com.paradisecloud.fcm.ding.cache.*;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiDingConferenceService;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiMcuDingConferenceAppointmentService;
import com.paradisecloud.fcm.ding.service2.interfaces.IBusiMcuDingHistoryConferenceService;
import com.paradisecloud.fcm.ding.templateConference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.ding.templateConference.StartTemplateConference;
import com.paradisecloud.fcm.ding.utils.DingConferenceContextUtils;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.AesEnsUtils;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiLiveDept;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.interfaces.IMqttService;

import com.paradisecloud.fcm.dao.mapper.BusiMcuDingConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuDingTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuDingConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiMcuDingTemplateConference;

import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * 会议相关服务类
 */
@Service
public class BusiDingConferenceServiceImpl implements IBusiDingConferenceService {
    private static final Logger logger = LoggerFactory.getLogger(BusiDingConferenceServiceImpl.class);
    @Resource
    private BusiMcuDingTemplateConferenceMapper busiMcuDingTemplateConferenceMapper;
    @Resource
    private BusiMcuDingConferenceAppointmentMapper busiMcuDingConferenceAppointmentMapper;
    @Resource
    private IBusiMcuDingConferenceAppointmentService busiMcuDingConferenceAppointmentService;

    @Resource
    private IBusiMcuDingHistoryConferenceService busiMcuDingHistoryConferenceService;

    /**
     * 开始模板会议
     *
     * @param templateConferenceId void
     * @return
     */
    @Override
    public String startTemplateConference(long templateConferenceId) {
        DingConferenceContext DingConferenceContext = new StartTemplateConference().startTemplateConference(templateConferenceId);
        return DingConferenceContext.getContextKey();
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
    public DingConferenceContext buildTemplateConferenceContext(long templateConferenceId) {
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
        DingConferenceContext conferenceContext = DingConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            conferenceContext.setEndReasonsType(endReasonsType);
            endConference(contextKey, endReasonsType, true, false);
        }


    }


    public void endConferenceType(String conferenceId, int endReasonsType) {
        DingConferenceContext conferenceContext = DingConferenceContextCache.getInstance().remove(conferenceId);
        if (conferenceContext != null) {
            try {
                // 设置结束状态
                conferenceContext.setEnd(true);
                conferenceContext.setEndTime(new Date());
                // 保存历史记录
                conferenceContext.getHistoryConference().setEndReasonsType(endReasonsType);
                BeanFactory.getBean(IBusiMcuDingHistoryConferenceService.class).saveHistory(conferenceContext.getHistoryConference(), conferenceContext);
                // 会议结束推送mqtt
                try {
                    pushEndMessageToMqtt(conferenceId, conferenceContext);
                } catch (Exception e) {
                    logger.error("结束会议时取消会议当前操作失败", e);
                }

                BusiMcuDingConferenceAppointment busiConferenceAppointment = conferenceContext.getConferenceAppointment();
                if (busiConferenceAppointment != null) {
                    busiConferenceAppointment.setIsHangUp(YesOrNo.YES.getValue());
                    busiConferenceAppointment.setIsStart(null);
                    busiConferenceAppointment.setExtendMinutes(null);
                    busiMcuDingConferenceAppointmentMapper.updateBusiMcuDingConferenceAppointment(busiConferenceAppointment);

                    AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiConferenceAppointment.getRepeatRate());
                    if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM) {
                        busiMcuDingConferenceAppointmentService.deleteBusiMcuDingConferenceAppointmentById(busiConferenceAppointment.getId());
                    }
                }
                BusiHistoryConference busiHistoryConference = conferenceContext.getHistoryConference();
                if (busiHistoryConference != null) {
                    busiHistoryConference.setConferenceEndTime(new Date());
                    busiHistoryConference.setEndReasonsType(conferenceContext.getEndReasonsType());
                    busiMcuDingHistoryConferenceService.saveHistory(busiHistoryConference, conferenceContext);

                }
                BusiMcuDingTemplateConference busiSmc2TemplateConference = busiMcuDingTemplateConferenceMapper.selectBusiMcuDingTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                if (busiSmc2TemplateConference != null) {
                    busiSmc2TemplateConference.setUpdateTime(new Date());
                    busiMcuDingTemplateConferenceMapper.updateBusiMcuDingTemplateConference(busiSmc2TemplateConference);

                }

                DingConferenceContextCache.getInstance().remove(conferenceContext.getMeetingId());
                conferenceContext.clear();
                pushEndMessageToMqtt(conferenceContext.getConferenceNumber(), conferenceContext);
                DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_ENDED, "会议已结束");
                DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已结束");

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
        DingConferenceContext cc = DingConferenceContextCache.getInstance().get(contextKey);
        if (cc != null) {
            try {
                DingBridge bridge = DingBridgeCache.getInstance().getAvailableBridgesByDept(cc.getDeptId());
                if (bridge == null) {
                    throw new CustomException("会议桥不存在");
                }

            } catch (Throwable e) {
                logger.error("Ding mcu endConference-error", e);
            } finally {
                endConferenceType(contextKey, endReasonsType);

                StringBuilder infoBuilder = new StringBuilder();
                infoBuilder.append("结束会议【").append(cc.getName()).append("】");
                infoBuilder.append(", 会议号码: ").append(cc.getAccessCode());
                DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, infoBuilder);
            }
        }

    }

    @Override
    public void endConference(String encryptConferenceId, int endType, int EndReasonsType) {
        String contextKey = EncryptIdUtil.parasToContextKey(encryptConferenceId);
        DingConferenceContext conferenceContext = DingConferenceContextCache.getInstance().get(contextKey);
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
    private void pushEndMessageToMqtt(String conferenceNumber, DingConferenceContext conferenceContext) {
        List<BaseAttendee> mqttJoinTerminals = new ArrayList<>();
        DingConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeDing) {
                TerminalAttendeeDing ta = (TerminalAttendeeDing) a;
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
        DingConferenceContext conferenceContext = DingConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return ;
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
        DingConferenceContext conferenceContext = DingConferenceContextCache.getInstance().get(conferenceNumber);
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
    public BusiMcuDingConferenceAppointment extendMinutes(String conferenceId, int minutes) {
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
        DingConferenceContext conferenceContext = DingConferenceContextCache.getInstance().get(conferenceNumber);
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
    public void stream(DingConferenceContext mainConferenceContext, Boolean streaming, String streamUrl) {

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
        DingConferenceContext conferenceContext = DingConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return ;
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
        DingConferenceContext conferenceContext = DingConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return ;
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
        DingConferenceContext conferenceContext = DingConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return ;
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
        DingConferenceContext conferenceContext = DingConferenceContextCache.getInstance().get(conferenceNumber);
        if (conferenceContext.getSyncInformation() != null) {
            DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始1！");
            return;
        }

        synchronized (conferenceContext.getSyncLock()) {
            if (conferenceContext.getSyncInformation() != null) {
                DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始2！");
                return;
            }

            SyncInformation syncInformation = new SyncInformation();
            conferenceContext.setSyncInformation(syncInformation);

            int totalCount = 0;
            syncInformation.setInProgress(true);
            syncInformation.setTotalCallCount(0);
            syncInformation.setReason("同步");

            logger.info("One click synchronization start：" + conferenceContext.getConferenceNumber());

            DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "开始同步本会议的参会信息！");
            Set<String> attendeeIds = new HashSet<>();
            DingConferenceContextUtils.eachAttendeeInConference(conferenceContext, (attendee) -> {
                attendeeIds.add(attendee.getId());
                if (attendee.isMeetingJoined()) {
                    synchronized (attendee) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeDing) {
                            TerminalAttendeeDing terminalAttendee = (TerminalAttendeeDing) attendee;
                            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                            terminalAttendee.setTerminalType(bt.getType());
                            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                            if (attendee.isMeetingJoined()) {
                                terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            } else {
                                terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                            }
                        } else if (attendee instanceof InvitedAttendeeDing) {
                            InvitedAttendeeDing invitedAttendee = (InvitedAttendeeDing) attendee;
                            if (invitedAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(invitedAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    invitedAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    invitedAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        } else if (attendee instanceof SelfCallAttendeeDing) {
                            SelfCallAttendeeDing selfCallAttendee = (SelfCallAttendeeDing) attendee;
                            if (selfCallAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(selfCallAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    selfCallAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    selfCallAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        }
                        DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                    }
                } else {
                    synchronized (attendee) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeDing) {
                            TerminalAttendeeDing terminalAttendee = (TerminalAttendeeDing) attendee;
                            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                            terminalAttendee.setTerminalType(bt.getType());
                            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                            if (attendee.isMeetingJoined()) {
                                terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            } else {
                                terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                            }
                        } else if (attendee instanceof InvitedAttendeeDing) {
                            InvitedAttendeeDing invitedAttendee = (InvitedAttendeeDing) attendee;
                            if (invitedAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(invitedAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    invitedAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    invitedAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        } else if (attendee instanceof SelfCallAttendeeDing) {
                            SelfCallAttendeeDing selfCallAttendee = (SelfCallAttendeeDing) attendee;
                            if (selfCallAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(selfCallAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    selfCallAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    selfCallAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        }
                        DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                    }
                }
            });

            totalCount = attendeeIds.size();

            syncInformation.setTotalCallCount(totalCount);
            syncInformation.setCurrentCallTotalParticipantCount(totalCount);
            syncInformation.setCurrentCallFmeIp(DingBridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId().intValue()).getBusiDing().getAppId());
            syncInformation.setSyncCurrentCallParticipantCount(totalCount);



            syncInformation.setInProgress(false);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("attendeeCountingStatistics",conferenceContext.getAttendeeCountingStatistics());
            DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.PARTICIPANT_SYNC, syncInformation);
            DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
            DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "已同步完所有参会信息，共【" + totalCount + "】个！");
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
        DingConferenceContext conferenceContext = DingConferenceContextCache.getInstance().get(conferenceNumber);


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


}
