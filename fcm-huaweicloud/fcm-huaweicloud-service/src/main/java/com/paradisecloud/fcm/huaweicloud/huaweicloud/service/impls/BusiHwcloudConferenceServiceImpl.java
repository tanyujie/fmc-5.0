package com.paradisecloud.fcm.huaweicloud.huaweicloud.service.impls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huaweicloud.sdk.meeting.v1.model.*;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.AesEnsUtils;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.SyncInformation;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.*;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.*;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.event.NetConditionNotifyParticipantCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.HwcloudBridgeStatus;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.ImageTypeEnum;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.PresetMultiPicReqDto;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.WaitingRoomParticipantSetting;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.MeetingControl;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.AttendeeOperation;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiHwcloudConferenceService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudConferenceAppointmentService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudHistoryConferenceService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.task.HwcloudDelayTaskService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.task.InviteAttendeeHwcloudTask;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference.StartTemplateConference;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.utils.HwcloudConferenceContextUtils;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client.HwcloudMeetingWebsocketReconnecter;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudTemplateConferenceMapper;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.sinhy.enumer.DateTimeFormatPattern;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 会议相关服务类
 */
@Service
public class BusiHwcloudConferenceServiceImpl implements IBusiHwcloudConferenceService {
    private static final Logger logger = LoggerFactory.getLogger(BusiHwcloudConferenceServiceImpl.class);
    public static final String MEETTING = "MEETTING";
    @Resource
    private BusiMcuHwcloudTemplateConferenceMapper busiMcuHwcloudTemplateConferenceMapper;
    @Resource
    private BusiMcuHwcloudConferenceAppointmentMapper busiMcuHwcloudConferenceAppointmentMapper;
    @Resource
    private IBusiMcuHwcloudConferenceAppointmentService busiMcuHwcloudConferenceAppointmentService;

    @Resource
    private IBusiMcuHwcloudHistoryConferenceService busiMcuHwcloudHistoryConferenceService;

    @Resource
    private HwcloudDelayTaskService hwclouddelayTaskService;

    /**
     * 开始模板会议
     *
     * @param templateConferenceId void
     * @return
     */
    @Override
    public String startTemplateConference(long templateConferenceId) {
        HwcloudConferenceContext HwcloudConferenceContext = new StartTemplateConference().startTemplateConference(templateConferenceId);
        return HwcloudConferenceContext.getContextKey();
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
    public HwcloudConferenceContext buildTemplateConferenceContext(long templateConferenceId) {
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
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            conferenceContext.setEndReasonsType(endReasonsType);
            endConference(contextKey, endReasonsType, true, false);
        }


    }


    public void endConferenceType(String conferenceId, int endReasonsType) {
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().remove(conferenceId);
        if (conferenceContext != null) {
            try {
                // 设置结束状态
                conferenceContext.setEnd(true);
                conferenceContext.setEndTime(new Date());
                // 保存历史记录
                conferenceContext.getHistoryConference().setEndReasonsType(endReasonsType);
                BeanFactory.getBean(IBusiMcuHwcloudHistoryConferenceService.class).saveHistory(conferenceContext.getHistoryConference(), conferenceContext);
                // 会议结束推送mqtt
                try {
                    pushEndMessageToMqtt(conferenceId, conferenceContext);
                } catch (Exception e) {
                    logger.error("结束会议时取消会议当前操作失败", e);
                }

                BusiMcuHwcloudConferenceAppointment busiConferenceAppointment = conferenceContext.getConferenceAppointment();
                if (busiConferenceAppointment != null) {
                    busiConferenceAppointment.setIsHangUp(YesOrNo.YES.getValue());
                    busiConferenceAppointment.setIsStart(null);
                    busiConferenceAppointment.setExtendMinutes(null);
                    busiMcuHwcloudConferenceAppointmentMapper.updateBusiMcuHwcloudConferenceAppointment(busiConferenceAppointment);

                    AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiConferenceAppointment.getRepeatRate());
                    if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM) {
                        busiMcuHwcloudConferenceAppointmentService.deleteBusiMcuHwcloudConferenceAppointmentById(busiConferenceAppointment.getId());
                    }
                }
                BusiHistoryConference busiHistoryConference = conferenceContext.getHistoryConference();
                if (busiHistoryConference != null) {
                    busiHistoryConference.setConferenceEndTime(new Date());
                    busiHistoryConference.setEndReasonsType(conferenceContext.getEndReasonsType());
                    busiMcuHwcloudHistoryConferenceService.saveHistory(busiHistoryConference, conferenceContext);

                }
                BusiMcuHwcloudTemplateConference busiSmc2TemplateConference = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                if (busiSmc2TemplateConference != null) {
                    busiSmc2TemplateConference.setUpdateTime(new Date());
                    busiSmc2TemplateConference.setConferenceNumber(null);
                    busiSmc2TemplateConference.setConfId(null);
                    busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(busiSmc2TemplateConference);
                }

                NetConditionNotifyParticipantCache.getInstance().getParticipantMap().remove(conferenceContext.getMeetingId());

                HwcloudConferenceContextCache.getInstance().remove(conferenceContext.getMeetingId());
                conferenceContext.clear();
                pushEndMessageToMqtt(conferenceContext.getConferenceNumber(), conferenceContext);
                HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_ENDED, "会议已结束");
                HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已结束");

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
        HwcloudConferenceContext cc = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (cc != null) {
            try {
                HwcloudBridge bridge = HwcloudBridgeCache.getInstance().getAvailableBridgesByDept(cc.getDeptId());
                if (bridge == null) {
                    throw new CustomException("会议桥不存在");
                }

                HwcloudMeetingBridge hwcloudMeetingBridge = cc.getHwcloudMeetingBridge();
                hwcloudMeetingBridge.setDeleted(true);
                hwcloudMeetingBridge.getMeetingManager().deleteMeeting(cc.getMeetingId());
                hwcloudMeetingBridge.getMeetingControl().stopMeeting(hwcloudMeetingBridge.getTokenInfo().getToken(),cc.getMeetingId());

            } catch (Throwable e) {
                logger.error("Hwcloud mcu endConference-error", e.getMessage());
            } finally {
                endConferenceType(contextKey, endReasonsType);

                StringBuilder infoBuilder = new StringBuilder();
                infoBuilder.append("结束会议【").append(cc.getName()).append("】");
                infoBuilder.append(", 会议号码: ").append(cc.getConferenceNumber());
                HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, infoBuilder);
            }
        }

    }

    @Override
    public void endConference(String encryptConferenceId, int endType, int EndReasonsType) {
        String contextKey = EncryptIdUtil.parasToContextKey(encryptConferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
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
    private void pushEndMessageToMqtt(String conferenceNumber, HwcloudConferenceContext conferenceContext) {
        List<BaseAttendee> mqttJoinTerminals = new ArrayList<>();
        HwcloudConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeHwcloud) {
                TerminalAttendeeHwcloud ta = (TerminalAttendeeHwcloud) a;
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
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return ;
        }
        conferenceContext.getHwcloudMeetingBridge().getMeetingControl().lock(conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(),conferenceContext.getMeetingId(),locked?1:0);

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
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(conferenceNumber);
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
    public BusiMcuHwcloudConferenceAppointment extendMinutes(String conferenceId, int minutes) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            throw new CustomException("会议不存在或者未开始");
        }

        HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
        hwcloudMeetingBridge.getMeetingControl().prolongMeeting(hwcloudMeetingBridge.getTokenInfo().getToken(),hwcloudMeetingBridge.getConfID(),0,minutes);


        BusiMcuHwcloudConferenceAppointment busiConferenceAppointment = conferenceContext.getConferenceAppointment();
        if (busiConferenceAppointment != null) {
            busiConferenceAppointment = busiMcuHwcloudConferenceAppointmentMapper.selectBusiMcuHwcloudConferenceAppointmentById(busiConferenceAppointment.getId());
            if (busiConferenceAppointment != null) {
                Integer extendMinutesNew = busiConferenceAppointment.getExtendMinutes() != null ? (busiConferenceAppointment.getExtendMinutes() + minutes) : minutes;
                if (extendMinutesNew > 1440) {
                    throw new CustomException("会议总延时最多24小时");
                }
                busiConferenceAppointment.setExtendMinutes(extendMinutesNew);
                AppointmentConferenceRepeatRate rr = AppointmentConferenceRepeatRate.convert(busiConferenceAppointment.getRepeatRate());
                Date end = null;
                if (rr == AppointmentConferenceRepeatRate.CUSTOM) {
                    end = DateUtils.convertToDate(busiConferenceAppointment.getEndTime());
                } else {
                    String today = DateUtils.formatTo(DateTimeFormatPattern.PATTERN_13.getPattern());
                    end = DateUtils.convertToDate(today + " " + busiConferenceAppointment.getEndTime());
                }

                if (busiConferenceAppointment.getExtendMinutes() != null) {
                    end = DateUtils.getDiffDate(end, busiConferenceAppointment.getExtendMinutes(), TimeUnit.MINUTES);
                }

                BusiMcuHwcloudConferenceAppointment con = new BusiMcuHwcloudConferenceAppointment();
                con.setTemplateId(busiConferenceAppointment.getTemplateId());
                List<BusiMcuHwcloudConferenceAppointment> cas = busiMcuHwcloudConferenceAppointmentMapper.selectBusiMcuHwcloudConferenceAppointmentList(con);
                if (!ObjectUtils.isEmpty(cas)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String endTime = sdf.format(end);
                    for (BusiMcuHwcloudConferenceAppointment busiConferenceAppointmentTemp : cas) {
                        if (busiConferenceAppointmentTemp.getId().longValue() != busiConferenceAppointment.getId().longValue()) {
                            if (endTime.compareTo(busiConferenceAppointmentTemp.getStartTime()) >= 0 && endTime.compareTo(busiConferenceAppointmentTemp.getEndTime()) <= 0) {
                                throw new SystemException(1008435, "延长会议结束时间失败：延长后的结束时间已存在相同模板的预约会议！");
                            }
                        }
                    }
                }
                conferenceContext.setEndTime(end);
                busiConferenceAppointment.setEndTime(DateUtil.convertDateToString(end,null));
                busiMcuHwcloudConferenceAppointmentMapper.updateBusiMcuHwcloudConferenceAppointment(busiConferenceAppointment);
                HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议结束时间延长至：" + DateUtils.formatTo(DateTimeFormatPattern.PATTERN_11, end));
                HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, conferenceContext);
            }
        } else {
            Long templateConferenceId = conferenceContext.getTemplateConferenceId();
            BusiMcuHwcloudTemplateConference tc = busiMcuHwcloudTemplateConferenceMapper.selectBusiMcuHwcloudTemplateConferenceById(templateConferenceId);
            Integer durationTimeNew = tc.getDurationTime() + minutes;
            if (durationTimeNew > 2880) {
                throw new CustomException("会议总延时最多24小时");
            }
            tc.setDurationTime(durationTimeNew);
            busiMcuHwcloudTemplateConferenceMapper.updateBusiMcuHwcloudTemplateConference(tc);
            conferenceContext.setDurationTime(durationTimeNew);

            Date endTime = conferenceContext.getEndTime();
            Date dateNew = org.apache.commons.lang3.time.DateUtils.addMinutes(endTime,minutes);

            conferenceContext.setEndTime(dateNew);

            HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议结束时间延长至：" + DateUtils.formatTo(DateTimeFormatPattern.PATTERN_11, dateNew));
            HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, conferenceContext);
        }
        return busiConferenceAppointment;
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
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(conferenceNumber);
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
    public void stream(HwcloudConferenceContext mainConferenceContext, Boolean streaming, String streamUrl) {

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
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return ;
        }
        HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
        String token = hwcloudMeetingBridge.getTokenInfo().getToken();
        //与会者自己解除静音
        hwcloudMeetingBridge.getMeetingControl().guestUnMute(token,conferenceContext.getMeetingId(),enabled?1:0);

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
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
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
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            List<AttendeeHwcloud> attendeeHwcloudList = new ArrayList<>();
            HwcloudConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
                if (!a.isMeetingJoined()) {
                    attendeeHwcloudList.add(a);

                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("【").append(a.getName()).append("】呼叫已发起！");
                    HwcloudWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                }

            });

            List<McuAttendeeHwcloud> mcuAttendees = conferenceContext.getMcuAttendees();
            if(CollectionUtils.isNotEmpty(mcuAttendees)){
                for (McuAttendeeHwcloud mcuAttendee : mcuAttendees) {
                    if(!mcuAttendee.isMeetingJoined()){
                        attendeeHwcloudList.add(mcuAttendee);
                        StringBuilder messageTip = new StringBuilder();
                        messageTip.append("【").append(mcuAttendee.getName()).append("】呼叫已发起！");
                        HwcloudWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                    }
                }
            }

            InviteAttendeeHwcloudTask inviteAttendeesTask = new InviteAttendeeHwcloudTask(contextKey, 100, conferenceContext, attendeeHwcloudList);
            hwclouddelayTaskService.addTask(inviteAttendeesTask);



        }
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.getSyncInformation() != null) {
            HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始1！");
            return;
        }

        synchronized (conferenceContext.getSyncLock()) {
            if (conferenceContext.getSyncInformation() != null) {
                HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始2！");
                return;
            }

            SyncInformation syncInformation = new SyncInformation();
            conferenceContext.setSyncInformation(syncInformation);

            int totalCount = 0;
            syncInformation.setInProgress(true);
            syncInformation.setTotalCallCount(0);
            syncInformation.setReason("同步");

            logger.info("One click synchronization start：" + conferenceContext.getConferenceNumber());

            HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "开始同步本会议的参会信息！");
            Set<String> attendeeIds = new HashSet<>();
            HwcloudConferenceContextUtils.eachAttendeeInConference(conferenceContext, (attendee) -> {
                attendeeIds.add(attendee.getId());
                if (attendee.isMeetingJoined()) {
                    synchronized (attendee) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeHwcloud) {
                            TerminalAttendeeHwcloud terminalAttendee = (TerminalAttendeeHwcloud) attendee;
                            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                            terminalAttendee.setTerminalType(bt.getType());
                            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                            if (attendee.isMeetingJoined()) {
                                terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            } else {
                                terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                            }
                        } else if (attendee instanceof InvitedAttendeeHwcloud) {
                            InvitedAttendeeHwcloud invitedAttendee = (InvitedAttendeeHwcloud) attendee;
                            if (invitedAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(invitedAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    invitedAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    invitedAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        } else if (attendee instanceof SelfCallAttendeeHwcloud) {
                            SelfCallAttendeeHwcloud selfCallAttendee = (SelfCallAttendeeHwcloud) attendee;
                            if (selfCallAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(selfCallAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    selfCallAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    selfCallAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        }
                        else if (attendee instanceof CropDirAttendeeHwcloud) {
                            CropDirAttendeeHwcloud cropDirAttendeeHwcloud = (CropDirAttendeeHwcloud) attendee;


                            HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();

                            if(hwcloudMeetingBridge.isAvailable()){
                                String participantUuid = cropDirAttendeeHwcloud.getParticipantUuid();
                                if(Strings.isNotBlank(participantUuid)){
                                    AttendeeHwcloud attendeeByPUuid = conferenceContext.getAttendeeByPUuid(participantUuid);
                                    if(attendeeByPUuid!=null){
                                        cropDirAttendeeHwcloud.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                    }else {
                                        cropDirAttendeeHwcloud.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                                    }
                                }
                            }else {
                                cropDirAttendeeHwcloud.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                                cropDirAttendeeHwcloud.leaveMeeting();
                            }

                        }
                        HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                    }
                } else {
                    synchronized (attendee) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeHwcloud) {
                            TerminalAttendeeHwcloud terminalAttendee = (TerminalAttendeeHwcloud) attendee;
                            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                            terminalAttendee.setTerminalType(bt.getType());
                            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                            if (attendee.isMeetingJoined()) {
                                terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            } else {
                                terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                            }
                        } else if (attendee instanceof InvitedAttendeeHwcloud) {
                            InvitedAttendeeHwcloud invitedAttendee = (InvitedAttendeeHwcloud) attendee;
                            if (invitedAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(invitedAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    invitedAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    invitedAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        } else if (attendee instanceof SelfCallAttendeeHwcloud) {
                            SelfCallAttendeeHwcloud selfCallAttendee = (SelfCallAttendeeHwcloud) attendee;
                            if (selfCallAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(selfCallAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    selfCallAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    selfCallAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        }
                        HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                    }
                }
            });
            HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
            if(!hwcloudMeetingBridge.isAvailable()){
                logger.info("Can't find HwcloudMeetingBridge: "+conferenceContext.getMeetingId());
                CreateConfTokenResponse confToken = hwcloudMeetingBridge.getMeetingControl().createConfTokenResponse(conferenceContext.getMeetingId(), conferenceContext.getChairmanPassword());
                hwcloudMeetingBridge.setHostPassword( conferenceContext.getChairmanPassword());
                hwcloudMeetingBridge.setConfID(conferenceContext.getMeetingId());
                hwcloudMeetingBridge.setTokenInfo(confToken.getData());
                HwcloudMeetingWebsocketReconnecter.getInstance().add(hwcloudMeetingBridge);
            }


            List<AttendeeHwcloud> attendees = conferenceContext.getAttendees();
            if(CollectionUtils.isNotEmpty(attendees)){
                for (AttendeeHwcloud attendee : attendees) {
                    try {
                        if(attendee.isMeetingJoined()){
                            if(attendee.getUserRole()!=null&&attendee.getUserRole()==2){
                                if(!attendee.getMaster()){
                                    if(conferenceContext.getAttendeeOperation()==null||(conferenceContext.getAttendeeOperation()!=null&&conferenceContext.getAttendeeOperation() instanceof  DefaultAttendeeOperation)){

                                        if(!conferenceContext.getMultiPicBroadcastStatus()){
                                            ChangeMasterAttendeeOperation changeMasterAttendeeOperation = new ChangeMasterAttendeeOperation(conferenceContext, attendee);
                                            conferenceContext.setAttendeeOperation(changeMasterAttendeeOperation);
                                            changeMasterAttendeeOperation.operate();
                                            break;
                                        }else {
                                            conferenceContext.setMasterAttendee(attendee);
                                            StringBuilder messageTip = new StringBuilder();
                                            messageTip.append("主会场已切换至【").append(attendee.getName()).append("】");
                                            HwcloudWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

                                            Map<String, Object> data = new HashMap<>(1);
                                            data.put("newMasterAttendee", attendee);
                                            HwcloudWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);
                                        }


                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }

            totalCount = attendeeIds.size();

            syncInformation.setTotalCallCount(totalCount);
            syncInformation.setCurrentCallTotalParticipantCount(totalCount);
            syncInformation.setCurrentCallFmeIp(HwcloudBridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId()).getBusiHwcloud().getAppId());
            syncInformation.setSyncCurrentCallParticipantCount(totalCount);



            syncInformation.setInProgress(false);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("attendeeCountingStatistics",conferenceContext.getAttendeeCountingStatistics());
            HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.PARTICIPANT_SYNC, syncInformation);
            HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
            HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "已同步完所有参会信息，共【" + totalCount + "】个！");
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return ;
        }
        conferenceContext.getHwcloudMeetingBridge().getMeetingControl().muteMeeting( conferenceContext.getHwcloudMeetingBridge().getTokenInfo().getToken(),conferenceContext.getMeetingId(),1,mute?1:0);

    }

    @Override
    public void multiPicBroad(String conferenceId, Boolean enable) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);


        if(enable){
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            conferenceContext.setLastAttendeeOperation(attendeeOperation);
            attendeeOperation.cancel();

            if(conferenceContext.getMultiPicInfo()!=null){
                PresetMultiPicReqDto multiPicInfo = conferenceContext.getMultiPicInfo();
                multiPicInfo.setAutoBroadCast(true);
                MultiPicDisplayDO multiPicDisplayDO = new MultiPicDisplayDO();
                multiPicDisplayDO.setImageType(ImageTypeEnum.getByNumberAndMode(multiPicInfo.getPicNum(), multiPicInfo.getMode()).name());
                multiPicDisplayDO.setSwitchTime(multiPicInfo.getSwitchTime());
                multiPicDisplayDO.setManualSet(multiPicInfo.getManualSet()==null?1:multiPicInfo.getManualSet());

                List<PresetMultiPicReqDto.PresetMultiPicRollsDTO> subPicList = multiPicInfo.getSubPicPollInfoList();
                List<PicInfoNotify>  list=new ArrayList<>();

                for (int i = 0; i < subPicList.size(); i++) {

                    List<String> ids = new ArrayList<>();

                    PicInfoNotify picInfoNotify = new PicInfoNotify();
                    picInfoNotify.setIndex(i+1);
                    List<PresetMultiPicReqDto.PresetMultiPicRollsDTO.SubPicListDTO> subPicListDTOS = subPicList.get(i).getParticipantIds();
                    if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(subPicListDTOS)){
                        picInfoNotify.setShare(subPicListDTOS.get(0).getStreamNumber());

                        for (PresetMultiPicReqDto.PresetMultiPicRollsDTO.SubPicListDTO subPicListDTO : subPicListDTOS) {
                            ids.add(subPicListDTO.getParticipantId());
                        }
                    }

                    picInfoNotify.setId(ids);

                    list.add(picInfoNotify);
                }
                multiPicDisplayDO.setSubscriberInPics(list);


                DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext, JSONObject.parseObject(JSON.toJSONString(multiPicDisplayDO)),false);
                defaultAttendeeOperation.operate();
            }


        }else {
            HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
            hwcloudMeetingBridge.getMeetingControl().cancelBroadcast(hwcloudMeetingBridge.getTokenInfo().getToken(),hwcloudMeetingBridge.getConfID());
            PresetMultiPicReqDto multiPicInfo = conferenceContext.getMultiPicInfo();
            if(multiPicInfo!=null){
                multiPicInfo.setAutoBroadCast(false);
            }

        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("multiPicInfo",conferenceContext.getMultiPicInfo());
        HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);


    }

    @Override
    public void waitingRoomParticipantSetting(String conferenceId, WaitingRoomParticipantSetting waitingRoomParticipantSetting) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
        MeetingControl meetingControl = hwcloudMeetingBridge.getMeetingControl();
        /**
         * 操作类型：
         * 1：主持人将等候室成员移入会议
         * 2：主持人将会中成员移入等候室
         * 3：主持人将等候室成员移出等候室
         */
        Integer operateType = waitingRoomParticipantSetting.getOperateType();
        List<WaitingRoomParticipantSetting.UsersDTO> users = waitingRoomParticipantSetting.getUsers();
        List<String> userIds =new ArrayList<>();
        if(operateType==1){
            for (WaitingRoomParticipantSetting.UsersDTO user : users) {
                meetingControl.allowWaitingParticipant(hwcloudMeetingBridge.getTokenInfo().getToken(),conferenceContext.getMeetingId(),user.getMsOpenId(),false);
                Map<String, Object> waitingParticipantMap = conferenceContext.getWaitingParticipantMap();
                if(waitingParticipantMap!=null){
                    waitingParticipantMap.remove(user.getMsOpenId());
                }

            }
        }else if( operateType==2) {
            for (WaitingRoomParticipantSetting.UsersDTO user : users) {
                meetingControl.moveToWaitingRoom(hwcloudMeetingBridge.getTokenInfo().getToken(),conferenceContext.getMeetingId(),user.getMsOpenId());

            }
        }else if( operateType==3) {
            for (WaitingRoomParticipantSetting.UsersDTO user : users) {
                meetingControl.allowWaitingParticipant(hwcloudMeetingBridge.getTokenInfo().getToken(),conferenceContext.getMeetingId(),user.getMsOpenId(),false);

            }
        }
    }

    @Override
    public void switchMode(String conferenceId, String switchMode, Integer imageType) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
        MeetingControl meetingControl = hwcloudMeetingBridge.getMeetingControl();

        meetingControl.switchMode(hwcloudMeetingBridge.getTokenInfo().getToken(),hwcloudMeetingBridge.getConfID(),imageType,switchMode);
    }

    @Override
    public void meetingstatus(String conferenceId, Integer lockSharing, Integer callInRestriction) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if(conferenceContext==null){
            return;
        }

        HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
        MeetingControl meetingControl = hwcloudMeetingBridge.getMeetingControl();
        meetingControl.updateStartedConfConfig(hwcloudMeetingBridge.getTokenInfo().getToken(), hwcloudMeetingBridge.getConfID(),lockSharing,callInRestriction);

    }


    @Override
    public void lockPresenter(String conferenceId, Boolean lock) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(contextKey);
        if(conferenceContext==null){
            return;
        }
        HwcloudMeetingBridge hwcloudMeetingBridge = conferenceContext.getHwcloudMeetingBridge();
        MeetingControl meetingControl = hwcloudMeetingBridge.getMeetingControl();
        meetingControl.updateStartedConfConfig(hwcloudMeetingBridge.getTokenInfo().getToken(), hwcloudMeetingBridge.getConfID(),lock?1:0,null);

    }
}
