package com.paradisecloud.fcm.mcu.plc.service.impls;

import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.dao.mapper.BusiMcuPlcConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuPlcTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.plc.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.mcu.plc.attendee.model.operation.DiscussAttendeeOperation;
import com.paradisecloud.fcm.mcu.plc.attendee.utils.McuPlcConferenceContextUtils;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcConferenceContextCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.mcu.plc.conference.model.templateconference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.mcu.plc.conference.model.templateconference.StartTemplateConference;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.*;
import com.paradisecloud.fcm.mcu.plc.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.plc.model.core.McuPlcSyncInformation;
import com.paradisecloud.fcm.mcu.plc.model.enumer.McuPlcLayoutTemplates;
import com.paradisecloud.fcm.mcu.plc.model.request.cm.CmGetMrCdrRequest;
import com.paradisecloud.fcm.mcu.plc.model.request.cm.CmStopMrRequest;
import com.paradisecloud.fcm.mcu.plc.model.response.CommonResponse;
import com.paradisecloud.fcm.mcu.plc.model.response.cm.CmGetMrCdrResponse;
import com.paradisecloud.fcm.mcu.plc.model.response.cm.CmStopMrResponse;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.*;
import com.paradisecloud.fcm.mcu.plc.task.McuPlcDelayTaskService;
import com.paradisecloud.fcm.mcu.plc.task.InviteAttendeesTask;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.sinhy.enumer.DateTimeFormatPattern;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 会议相关服务类
 */
@Service
public class BusiMcuPlcConferenceServiceImpl implements IBusiMcuPlcConferenceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BusiMcuPlcConferenceServiceImpl.class);

    @Resource
    private BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper;
    @Resource
    private BusiMcuPlcConferenceAppointmentMapper busiMcuPlcConferenceAppointmentMapper;
    @Resource
    private IBusiMcuPlcConferenceAppointmentService busiMcuPlcConferenceAppointmentService;
    @Resource
    private IBusiMcuPlcTemplateConferenceService busiMcuPlcTemplateConferenceService;
    @Resource
    private IBusiHistoryConferenceForMcuPlcService busiHistoryConferenceForMcuPlcService;
    @Resource
    private IAttendeeForMcuPlcService attendeeForMcuPlcService;
    @Resource
    private McuPlcDelayTaskService mcuPlcDelayTaskService;
    @Resource
    private IMqttService mqttService;
    @Resource
    private RedisCache redisCache;

    /**
     * 开始模板会议
     *
     * @param templateConferenceId void
     * @return
     */
    @Override
    public String startTemplateConference(long templateConferenceId) {
        return new StartTemplateConference().startTemplateConference(templateConferenceId);
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
    public McuPlcConferenceContext buildTemplateConferenceContext(long templateConferenceId) {
        return new BuildTemplateConferenceContext().buildTemplateConferenceContext(templateConferenceId);
    }

    /**
     * <pre>根据coSpaceId挂断会议</pre>
     *
     * @param encryptConferenceId void
     * @param endType
     * @author lilinhai
     * @since 2021-02-03 18:05
     */
    @Override
    public void endConference(String encryptConferenceId, int endType) {
        final String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(encryptConferenceId);
        McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
        conferenceContext.setEndReasonsType(EndReasonsType.ADMINISTRATOR_HANGS_UP);
        endConference(contextKey, endType, true, false);
    }

    /**
     * <pre>根据coSpaceId挂断会议</pre>
     *
     * @param contextKey void
     * @param endType
     * @author lilinhai
     * @since 2021-02-03 18:05
     */
    @Override
    public void endConference(String contextKey, int endType, boolean forceEnd, boolean pushMessage) {
        // 会议结束类型
        ConferenceEndType conferenceEndType = ConferenceEndType.convert(endType);
        if (conferenceEndType == ConferenceEndType.CASCADE) {
            AtomicInteger successCount = new AtomicInteger();
            McuPlcConferenceContextCache.getInstance().destroyAllCascadeConferenceContexts(contextKey, (cc) -> {
//                endConference(cc, successCount);
            });
        }
//        else
        {
            McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
            if (conferenceContext != null) {
                if (forceEnd) {
                    String encryptToHex = AesEnsUtils.getAesEncryptor().encryptToHex(String.valueOf(conferenceContext.getConferenceNumber()));
                    attendeeForMcuPlcService.setMessageBannerText(encryptToHex, "");
                    CmStopMrRequest cmStopMrRequest = new CmStopMrRequest();
                    cmStopMrRequest.setId(conferenceContext.getConfId());
                    CmStopMrResponse cmStopMrResponse = conferenceContext.getMcuPlcBridge().getConferenceManageApi().stopMr(cmStopMrRequest);
                    if (cmStopMrResponse != null && (CommonResponse.STATUS_OK.equals(cmStopMrResponse.getStatus()) || CommonResponse.IN_PROGRESS.equals(cmStopMrResponse.getStatus()))) {
                    }
                }

                if (pushMessage) {
                    BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment = conferenceContext.getConferenceAppointment();
                    if (busiMcuPlcConferenceAppointment != null) {
                        busiMcuPlcConferenceAppointment.setIsHangUp(YesOrNo.YES.getValue());
                        busiMcuPlcConferenceAppointment.setIsStart(null);
                        busiMcuPlcConferenceAppointment.setExtendMinutes(null);
                        busiMcuPlcConferenceAppointmentMapper.updateBusiMcuPlcConferenceAppointment(busiMcuPlcConferenceAppointment);

                        AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiMcuPlcConferenceAppointment.getRepeatRate());
                        if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM) {
                            busiMcuPlcConferenceAppointmentService.deleteBusiMcuPlcConferenceAppointmentById(busiMcuPlcConferenceAppointment.getId());
                        }
                    }
                    BusiHistoryConference busiHistoryConference = conferenceContext.getHistoryConference();
                    if (busiHistoryConference != null) {
                        busiHistoryConference.setConferenceEndTime(new Date());
                        if (conferenceContext.getEndReasonsType() == null) {
                            conferenceContext.setEndReasonsType(EndReasonsType.ABNORMAL_END);
                        }
                        try {
                            CmGetMrCdrRequest cmGetMrCdrRequest = new CmGetMrCdrRequest();
                            cmGetMrCdrRequest.setId(conferenceContext.getConfId());
                            CmGetMrCdrResponse cmGetMrCdrResponse = conferenceContext.getMcuPlcBridge().getConferenceManageApi().getMrCdr(cmGetMrCdrRequest);
                            if (cmGetMrCdrResponse != null && CommonResponse.STATUS_OK.equals(cmGetMrCdrResponse.getStatus())) {
                                if (cmGetMrCdrResponse.getEndReasonType() != null) {
                                    conferenceContext.setEndReasonsType(cmGetMrCdrResponse.getEndReasonType());
                                }
                            }
                        } catch (Exception e) {
                        }
                        busiHistoryConference.setEndReasonsType(conferenceContext.getEndReasonsType());
                        busiHistoryConferenceForMcuPlcService.saveHistory(busiHistoryConference, conferenceContext);

                    }
                    BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                    if (busiMcuPlcTemplateConference != null) {
                        busiMcuPlcTemplateConference.setCreateTime(new Date());
                        busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(busiMcuPlcTemplateConference);
                    }

                    McuPlcConferenceContextCache.getInstance().remove(contextKey);
                    redisCache.deleteObject(conferenceContext.getId() + "_" + "_master_attendee");
                    pushEndMessageToMqtt(conferenceContext.getConferenceNumber(), conferenceContext);
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_ENDED, "会议已结束");
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已结束");
                }
            }
        }
    }


    /**
     * <pre>会议结束推送mqtt</pre>
     *
     * @param conferenceNumber
     * @param conferenceContext void
     * @author sinhy
     * @since 2021-12-13 15:03
     */
    private void pushEndMessageToMqtt(String conferenceNumber, McuPlcConferenceContext conferenceContext) {
        List<BaseAttendee> mqttJoinTerminals = new ArrayList<>();
        McuPlcConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeForMcuPlc) {
                TerminalAttendeeForMcuPlc ta = (TerminalAttendeeForMcuPlc) a;
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
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (!(attendeeOperation instanceof DiscussAttendeeOperation)) {
                if (attendeeOperation instanceof DefaultAttendeeOperation) {
                    conferenceContext.setLastAttendeeOperation(attendeeOperation);
                }
                DiscussAttendeeOperation discussAttendeeOperation = new DiscussAttendeeOperation(conferenceContext);
                conferenceContext.setAttendeeOperation(discussAttendeeOperation);
                attendeeOperation.cancel();
            }
        }
    }

    /**
     * <pre>锁定会议</pre>
     *
     * @param conferenceId void
     * @param locked
     * @author lilinhai
     * @since 2021-04-27 16:23
     */
    @Override
    public void lock(String conferenceId, Boolean locked) {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            // 锁定开锁会议
//            if (success) {
//                conferenceContext.setLocked(locked);
//
//                // 解除锁定后，推送消息更新前端状态
//                if (!locked) {
//                    McuPlcConferenceContextUtils.eachNonMcuAttendeeInConference(conferenceContext, (a) -> {
//                        if (a.isLocked()) {
//                            synchronized (a) {
//                                a.resetUpdateMap();
//                                a.setLocked(locked);
//                                if (a.getUpdateMap().size() > 1) {
//                                    McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(a.getUpdateMap()));
//                                }
//                            }
//                        }
//                    });
//                }
//
//                // 消息和参会者信息同步到主级会议
//                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_LOCK, locked);
//                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已" + (locked ? "" : "解除") + "锁定");
//            }
        }
    }

    /**
     * 修改call
     *
     * @param conferenceNumber
     * @param nameValuePairs   void
     * @author lilinhai
     * @since 2021-04-28 13:38
     */
    @Override
    public void updateCall(String conferenceNumber, List<NameValuePair> nameValuePairs) {

    }

    @Override
    public boolean updateCallRecordStatus(String contextKey, Boolean record) {
        McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            boolean recorded = conferenceContext.isRecorded();
            if (recorded && record) {
                throw new SystemException("正在录制中！");
            }
            if (recorded != record) {
                boolean success = false;
                return success;
            }
        }
        return false;
    }

    /**
     * <pre>延长会议时间</pre>
     *
     * @param conferenceId
     * @param minutes      void
     * @author lilinhai
     * @since 2021-05-27 16:59
     * @return
     */
    @Override
    public BusiMcuPlcConferenceAppointment extendMinutes(String conferenceId, int minutes) {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            throw new CustomException("会议不存在或者未开始");
        }
        BusiMcuPlcConferenceAppointment busiMcuPlcConferenceAppointment = conferenceContext.getConferenceAppointment();
        if (busiMcuPlcConferenceAppointment != null) {
            busiMcuPlcConferenceAppointment = busiMcuPlcConferenceAppointmentMapper.selectBusiMcuPlcConferenceAppointmentById(busiMcuPlcConferenceAppointment.getId());
            if (busiMcuPlcConferenceAppointment != null) {
                Integer extendMinutesNew = busiMcuPlcConferenceAppointment.getExtendMinutes() != null ? (busiMcuPlcConferenceAppointment.getExtendMinutes() + minutes) : minutes;
                if (extendMinutesNew > 1440) {
                    throw new CustomException("会议总延时最多24小时");
                }
                busiMcuPlcConferenceAppointment.setExtendMinutes(extendMinutesNew);
                AppointmentConferenceRepeatRate rr = AppointmentConferenceRepeatRate.convert(busiMcuPlcConferenceAppointment.getRepeatRate());
                Date end = null;
                if (rr == AppointmentConferenceRepeatRate.CUSTOM) {
                    end = DateUtils.convertToDate(busiMcuPlcConferenceAppointment.getEndTime());
                } else {
                    String today = DateUtils.formatTo(DateTimeFormatPattern.PATTERN_13.getPattern());
                    end = DateUtils.convertToDate(today + " " + busiMcuPlcConferenceAppointment.getEndTime());
                }

                if (busiMcuPlcConferenceAppointment.getExtendMinutes() != null) {
                    end = DateUtils.getDiffDate(end, busiMcuPlcConferenceAppointment.getExtendMinutes(), TimeUnit.MINUTES);
                }

                BusiMcuPlcConferenceAppointment con = new BusiMcuPlcConferenceAppointment();
                con.setTemplateId(busiMcuPlcConferenceAppointment.getTemplateId());
                List<BusiMcuPlcConferenceAppointment> cas = busiMcuPlcConferenceAppointmentMapper.selectBusiMcuPlcConferenceAppointmentList(con);
                if (!ObjectUtils.isEmpty(cas)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String endTime = sdf.format(end);
                    for (BusiConferenceAppointment busiConferenceAppointmentTemp : cas) {
                        if (busiConferenceAppointmentTemp.getId().longValue() != busiMcuPlcConferenceAppointment.getId().longValue()) {
                            if (endTime.compareTo(busiConferenceAppointmentTemp.getStartTime()) >= 0 && endTime.compareTo(busiConferenceAppointmentTemp.getEndTime()) <= 0) {
                                throw new SystemException(1008435, "延长会议结束时间失败：延长后的结束时间已存在相同模板的预约会议！");
                            }
                        }
                    }
                }

                busiMcuPlcConferenceAppointmentMapper.updateBusiMcuPlcConferenceAppointment(busiMcuPlcConferenceAppointment);
                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议结束时间延长至：" + DateUtils.formatTo(DateTimeFormatPattern.PATTERN_11, end));
            }
        } else {
            Long templateConferenceId = conferenceContext.getTemplateConferenceId();
            BusiMcuPlcTemplateConference tc = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceById(templateConferenceId);
            Integer durationTimeNew = tc.getDurationTime() + minutes;
            if (durationTimeNew > 2880) {
                throw new CustomException("会议总延时最多24小时");
            }
            tc.setDurationTime(durationTimeNew);
            busiMcuPlcTemplateConferenceMapper.updateBusiMcuPlcTemplateConference(tc);
            conferenceContext.setDurationTime(durationTimeNew);
        }
        mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
        return busiMcuPlcConferenceAppointment;
    }

    /**
     * <pre>取消会议讨论</pre>
     *
     * @param conferenceId void
     * @author sinhy
     * @since 2021-08-17 13:36
     */
    @Override
    public void cancelDiscuss(String conferenceId) {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (attendeeOperation instanceof DiscussAttendeeOperation) {
                conferenceContext.setAttendeeOperation(conferenceContext.getLastAttendeeOperation());
                attendeeOperation.cancel();
            }
        }
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
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
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
        }
    }

    /**
     * 直播
     *
     * @param mainConferenceContext
     * @param streaming
     * @param streamUrl
     * @author sinhy
     * @since 2021-08-17 16:39
     */
    @Override
    public void stream(McuPlcConferenceContext mainConferenceContext, Boolean streaming, String streamUrl) {

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

    }

    /**
     * 获取显示模板
     *
     * @param deptId
     * @return
     */
    @Override
    public List<ModelBean> getLayoutTemplates(Long deptId) {
        return McuPlcLayoutTemplates.getLayoutTemplateScreenList();
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
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
        long diff = System.currentTimeMillis() - conferenceContext.getStartTime().getTime();
        if (diff < 5000) {
            throw new SystemException(1, "MCU准备中，请等待" + (diff / 1000 + 1) + "秒！");
        }
        if (conferenceContext.isInvitingTerminal()) {
            throw new SystemException(1, "呼入处理正在进行中，请稍后再试！");
        }
        Map<Long, TerminalAttendeeForMcuPlc> terminalAttendeeMap = conferenceContext.getTerminalAttendeeMap();
        List<AttendeeForMcuPlc> attendees = new ArrayList<>();
        for (TerminalAttendeeForMcuPlc value : terminalAttendeeMap.values()) {
            if (value.getAttendType() == 1 && !TerminalType.isFCMSIP(value.getTerminalType())) {
                AttendeeForMcuPlc attendeeById = conferenceContext.getAttendeeById(value.getId());
                if (attendeeById.getMeetingStatus() == AttendeeMeetingStatus.OUT.getValue() && attendeeById.getOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                    attendees.add(value);
                }
            }
        }
        List<McuAttendeeForMcuPlc> mcuAttendees = conferenceContext.getMcuAttendees();
        for (McuAttendeeForMcuPlc value : mcuAttendees) {
            AttendeeForMcuPlc attendeeById = conferenceContext.getAttendeeById(value.getId());
            if (attendeeById.getMeetingStatus() == AttendeeMeetingStatus.OUT.getValue() && attendeeById.getOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                attendees.add(value);
            }
        }
        if (attendees.size() > 0) {
            InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 100, conferenceContext, attendees);
            mcuPlcDelayTaskService.addTask(inviteAttendeesTask);
        }
    }

    /**
     * <pre>同步会议数据</pre>
     *
     * @param conferenceId void
     * @author sinhy
     * @since 2021-08-30 10:46
     */
    public void sync(String conferenceId) {
        final String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuPlcConferenceContext conferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.getSyncInformation() != null) {
            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始1！");
            return;
        }

        synchronized (conferenceContext.getSyncLock()) {
            if (conferenceContext.getSyncInformation() != null) {
                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始2！");
                return;
            }

            McuPlcSyncInformation syncInformation = new McuPlcSyncInformation();
            conferenceContext.setSyncInformation(syncInformation);

            int totalCount = 0;
            syncInformation.setInProgress(true);
            syncInformation.setTotalCallCount(0);
            syncInformation.setReason("同步");

            LOGGER.info("One click synchronization start：" + conferenceContext.getConferenceNumber());

            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "开始同步本会议的参会信息！");
            Set<String> attendeeIds = new HashSet<>();
            McuPlcConferenceContextUtils.eachAttendeeInConference(conferenceContext, (attendee) -> {
                attendeeIds.add(attendee.getId());
                if (attendee.isMeetingJoined()) {
                    synchronized (attendee) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeForMcuPlc) {
                            TerminalAttendeeForMcuPlc terminalAttendee = (TerminalAttendeeForMcuPlc) attendee;
                            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                            terminalAttendee.setTerminalType(bt.getType());
                            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                            if (attendee.isMeetingJoined()) {
                                terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            } else {
                                terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                            }
                        } else if (attendee instanceof InvitedAttendeeForMcuPlc) {
                            InvitedAttendeeForMcuPlc invitedAttendee = (InvitedAttendeeForMcuPlc) attendee;
                            if (invitedAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(invitedAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    invitedAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    invitedAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        } else if (attendee instanceof SelfCallAttendeeForMcuPlc) {
                            SelfCallAttendeeForMcuPlc selfCallAttendee = (SelfCallAttendeeForMcuPlc) attendee;
                            if (selfCallAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(selfCallAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    selfCallAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    selfCallAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        }
                        McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                    }
                } else {
                    synchronized (attendee) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeForMcuPlc) {
                            TerminalAttendeeForMcuPlc terminalAttendee = (TerminalAttendeeForMcuPlc) attendee;
                            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                            terminalAttendee.setTerminalType(bt.getType());
                            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                            if (attendee.isMeetingJoined()) {
                                terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            } else {
                                terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                            }
                        } else if (attendee instanceof InvitedAttendeeForMcuPlc) {
                            InvitedAttendeeForMcuPlc invitedAttendee = (InvitedAttendeeForMcuPlc) attendee;
                            if (invitedAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(invitedAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    invitedAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    invitedAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        } else if (attendee instanceof SelfCallAttendeeForMcuPlc) {
                            SelfCallAttendeeForMcuPlc selfCallAttendee = (SelfCallAttendeeForMcuPlc) attendee;
                            if (selfCallAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(selfCallAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    selfCallAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    selfCallAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        }
                        McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                    }
                }
            });

            totalCount = attendeeIds.size();

            syncInformation.setTotalCallCount(totalCount);
            syncInformation.setCurrentCallTotalParticipantCount(totalCount);
            syncInformation.setCurrentCallMcuIp(conferenceContext.getBusiMcuPlc().getIp());
            syncInformation.setSyncCurrentCallParticipantCount(totalCount);

            syncInformation.setInProgress(false);
            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.PARTICIPANT_SYNC, syncInformation);
            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "已同步完所有参会信息，共【" + totalCount + "】个！");
            conferenceContext.setSyncInformation(null);
        }
    }

    @Override
    public Integer getLiveTerminalCount(String conferenceId) {
        Integer liveConferenceTerminalCount = LiveBridgeCache.getInstance().getLiveConferenceTerminalCount(conferenceId);
        return liveConferenceTerminalCount;
    }
}
