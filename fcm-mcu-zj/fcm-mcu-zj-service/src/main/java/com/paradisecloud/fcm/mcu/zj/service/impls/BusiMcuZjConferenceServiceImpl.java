package com.paradisecloud.fcm.mcu.zj.service.impls;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiLiveSettingMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZjConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZjTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.zj.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.attendee.model.operation.DiscussAttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.attendee.utils.McuZjConferenceContextUtils;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.conference.model.templateconference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.mcu.zj.conference.model.templateconference.StartTemplateConference;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.*;
import com.paradisecloud.fcm.mcu.zj.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.model.core.McuZjSyncInformation;
import com.paradisecloud.fcm.mcu.zj.model.enumer.LayoutTemplates;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateBypassUrlRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateMrEpsStatusRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateMrStatusRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmDelScheduleRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmGetRoomHistoryListRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmStopMrRequest;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmDelScheduleResponse;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmGetRoomHistoryListResponse;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.*;
import com.paradisecloud.fcm.mcu.zj.task.DelayTaskService;
import com.paradisecloud.fcm.mcu.zj.task.DownloadRecordFileTask;
import com.paradisecloud.fcm.mcu.zj.task.InviteAttendeesTask;
import com.paradisecloud.fcm.mcu.zj.task.McuZjDeleteRoomTask;
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
public class BusiMcuZjConferenceServiceImpl implements IBusiMcuZjConferenceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BusiMcuZjConferenceServiceImpl.class);

    @Resource
    private BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper;
    @Resource
    private BusiMcuZjConferenceAppointmentMapper busiMcuZjConferenceAppointmentMapper;
    @Resource
    private IBusiMcuZjConferenceAppointmentService busiMcuZjConferenceAppointmentService;
    @Resource
    private IBusiMcuZjTemplateConferenceService busiMcuZjTemplateConferenceService;
    @Resource
    private IBusiHistoryConferenceForMcuZjService busiHistoryConferenceForMcuZjService;
    @Resource
    private IAttendeeForMcuZjService attendeeForMcuZjService;
    @Resource
    private DelayTaskService delayTaskService;
    @Resource
    private IMqttService mqttService;

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
    public McuZjConferenceContext buildTemplateConferenceContext(long templateConferenceId) {
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
        final String contextKey = EncryptIdUtil.parasToContextKey(encryptConferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
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
            McuZjConferenceContextCache.getInstance().destroyAllCascadeConferenceContexts(contextKey, (cc) -> {
//                endConference(cc, successCount);
            });
        }
//        else
        {
            McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
            if (conferenceContext != null) {
                if (forceEnd) {
                    attendeeForMcuZjService.setMessageBannerText(conferenceContext.getId(), "");
                    CmStopMrRequest cmStopMrRequest = new CmStopMrRequest();
                    cmStopMrRequest.setMr_id(conferenceContext.getTenantId() + conferenceContext.getConferenceNumber());
                    cmStopMrRequest.setReason("用户结束会议");
                    boolean success = conferenceContext.getMcuZjBridge().getConferenceManageApi().stopMr(cmStopMrRequest);
                    if (success) {
                        BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                        if (busiMcuZjTemplateConference != null) {
                            if (StringUtils.isNotEmpty(busiMcuZjTemplateConference.getCallLegProfileId())) {
                                try {
                                    Integer scheduleId = Integer.valueOf(busiMcuZjTemplateConference.getCallLegProfileId());
                                    CmDelScheduleRequest cmDelScheduleRequest = new CmDelScheduleRequest();
                                    Integer[] scheduleIds = new Integer[]{scheduleId};
                                    cmDelScheduleRequest.setSchedule_ids(scheduleIds);
                                    CmDelScheduleResponse cmDelScheduleResponse = conferenceContext.getMcuZjBridge().getConferenceManageApi().delSchedules(cmDelScheduleRequest);
                                    if (cmDelScheduleResponse != null) {
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }

                if (pushMessage) {
                    BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment = conferenceContext.getConferenceAppointment();
                    if (busiMcuZjConferenceAppointment != null) {
                        busiMcuZjConferenceAppointment.setIsHangUp(YesOrNo.YES.getValue());
                        busiMcuZjConferenceAppointment.setIsStart(null);
                        busiMcuZjConferenceAppointment.setExtendMinutes(null);
                        busiMcuZjConferenceAppointmentMapper.updateBusiMcuZjConferenceAppointment(busiMcuZjConferenceAppointment);

                        AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiMcuZjConferenceAppointment.getRepeatRate());
                        if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM) {
                            busiMcuZjConferenceAppointmentService.deleteBusiMcuZjConferenceAppointmentById(busiMcuZjConferenceAppointment.getId());
                        }
                    }
                    BusiHistoryConference busiHistoryConference = conferenceContext.getHistoryConference();
                    if (busiHistoryConference != null) {
                        busiHistoryConference.setConferenceEndTime(new Date());
                        if (conferenceContext.getEndReasonsType() == null) {
                            conferenceContext.setEndReasonsType(EndReasonsType.ABNORMAL_END);
                        }
                        try {
                            String mrId = conferenceContext.getTenantId() + conferenceContext.getConferenceNumber();
                            CmGetRoomHistoryListRequest cmGetRoomHistoryListRequest = new CmGetRoomHistoryListRequest();
                            Date fromDate = DateUtils.getDiffDate(busiHistoryConference.getConferenceStartTime(), -1, TimeUnit.MINUTES);
                            Date toDate = DateUtils.getDiffDate(busiHistoryConference.getConferenceStartTime(), 1, TimeUnit.MINUTES);
                            int fromTimeInt = (int) (fromDate.getTime() / 1000);
                            int toTimeInt = 0;
                            cmGetRoomHistoryListRequest.setFrom_dtm(fromTimeInt);
                            cmGetRoomHistoryListRequest.setTo_dtm(toTimeInt);
                            cmGetRoomHistoryListRequest.setTenant_id(conferenceContext.getTenantId());
                            CmGetRoomHistoryListResponse cmGetRoomHistoryListResponse = conferenceContext.getMcuZjBridge().getConferenceManageApi().getRoomHistoryList(cmGetRoomHistoryListRequest);
                            if (cmGetRoomHistoryListResponse != null && cmGetRoomHistoryListResponse.getLst_mrids() != null) {
                                for (int i = 0; i < cmGetRoomHistoryListResponse.getLst_mrids().length; i++) {
                                    if (mrId.equals(cmGetRoomHistoryListResponse.getLst_mrids()[i])) {
                                        if (cmGetRoomHistoryListResponse.getLst_stop_reasons()[i].contains("idle too long")) {
                                            conferenceContext.setEndReasonsType(EndReasonsType.IDLE_TOO_LONG);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }
                        busiHistoryConference.setEndReasonsType(conferenceContext.getEndReasonsType());
                        busiHistoryConferenceForMcuZjService.saveHistory(busiHistoryConference, conferenceContext);

                    }
                    BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                    if (busiMcuZjTemplateConference != null) {
                        busiMcuZjTemplateConference.setCreateTime(new Date());
                        busiMcuZjTemplateConference.setDurationTime(1440);
                        busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(busiMcuZjTemplateConference);
                        if (StringUtils.isNotEmpty(busiMcuZjTemplateConference.getCallLegProfileId())) {
                            try {
                                Integer scheduleId = Integer.valueOf(busiMcuZjTemplateConference.getCallLegProfileId());
                                CmDelScheduleRequest cmDelScheduleRequest = new CmDelScheduleRequest();
                                Integer[] scheduleIds = new Integer[]{scheduleId};
                                cmDelScheduleRequest.setSchedule_ids(scheduleIds);
                                CmDelScheduleResponse cmDelScheduleResponse = conferenceContext.getMcuZjBridge().getConferenceManageApi().delSchedules(cmDelScheduleRequest);
                                if (cmDelScheduleResponse != null) {
                                }
                            } catch (Exception e) {
                            }
                        }
                    }

                    String coSpace = conferenceContext.getCoSpaceId();
                    DownloadRecordFileTask downloadRecordFileTask = new DownloadRecordFileTask(coSpace, 30000, conferenceContext.getMcuZjBridge(), coSpace, conferenceContext.getDeptId(), conferenceContext.getName());
                    delayTaskService.addTask(downloadRecordFileTask);
                    // 删除会议室
                    McuZjDeleteRoomTask mcuZjDeleteRoomTask = new McuZjDeleteRoomTask(conferenceContext.getMcuZjBridge().getBusiMcuZj().getId() + "_" + conferenceContext.getConferenceNumber(), 30000, conferenceContext.getMcuZjBridge(), conferenceContext.getConferenceNumber());
                    delayTaskService.addTask(mcuZjDeleteRoomTask);

                    McuZjConferenceContextCache.getInstance().remove(contextKey);
                    pushEndMessageToMqtt(conferenceContext.getTenantId() + conferenceContext.getConferenceNumber(), conferenceContext);
                    McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_ENDED, "会议已结束");
                    McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已结束");
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
    private void pushEndMessageToMqtt(String conferenceNumber, McuZjConferenceContext conferenceContext) {
        List<BaseAttendee> mqttJoinTerminals = new ArrayList<>();
        McuZjConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeForMcuZj) {
                TerminalAttendeeForMcuZj ta = (TerminalAttendeeForMcuZj) a;
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            // 锁定开锁会议
            CcUpdateMrStatusRequest ccUpdateMrStatusRequest = new CcUpdateMrStatusRequest();
            ccUpdateMrStatusRequest.setMrStatusAction(CcUpdateMrStatusRequest.PARAM_lock_mr);
            ccUpdateMrStatusRequest.setMrStatusValue(locked ? 1 : 0);
            boolean success = conferenceContext.getConferenceControlApi().updateMrStatus(ccUpdateMrStatusRequest);
            if (success) {
//                conferenceContext.setLocked(locked);
//
//                // 解除锁定后，推送消息更新前端状态
//                if (!locked) {
//                    McuZjConferenceContextUtils.eachNonMcuAttendeeInConference(conferenceContext, (a) -> {
//                        if (a.isLocked()) {
//                            synchronized (a) {
//                                a.resetUpdateMap();
//                                a.setLocked(locked);
//                                if (a.getUpdateMap().size() > 1) {
//                                    McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(a.getUpdateMap()));
//                                }
//                            }
//                        }
//                    });
//                }
//
//                // 消息和参会者信息同步到主级会议
//                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_LOCK, locked);
//                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已" + (locked ? "" : "解除") + "锁定");
            }
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
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            boolean recorded = conferenceContext.isRecorded();
            if (recorded && record) {
                throw new SystemException("正在录制中！");
            }
            if (recorded != record) {
                CcUpdateMrStatusRequest ccUpdateMrStatusRequest = new CcUpdateMrStatusRequest();
                ccUpdateMrStatusRequest.setMrStatusAction(CcUpdateMrStatusRequest.PARAM_enable_rec);
                if (record) {
                    ccUpdateMrStatusRequest.setMrStatusValue(1);
                } else {
                    ccUpdateMrStatusRequest.setMrStatusValue(0);
                }
                boolean success = conferenceContext.getConferenceControlApi().updateMrStatus(ccUpdateMrStatusRequest);
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
    public BusiMcuZjConferenceAppointment extendMinutes(String conferenceId, int minutes) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            throw new CustomException("会议不存在或者未开始");
        }
        BusiMcuZjConferenceAppointment busiMcuZjConferenceAppointment = conferenceContext.getConferenceAppointment();
        if (busiMcuZjConferenceAppointment != null) {
            busiMcuZjConferenceAppointment = busiMcuZjConferenceAppointmentMapper.selectBusiMcuZjConferenceAppointmentById(busiMcuZjConferenceAppointment.getId());
            if (busiMcuZjConferenceAppointment != null) {
                if (StringUtils.isNotEmpty(busiMcuZjConferenceAppointment.getEndTime()) && busiMcuZjConferenceAppointment.getEndTime().startsWith("9999")) {
                    throw new CustomException("永久会议不能延时");
                }
                Integer extendMinutesNew = busiMcuZjConferenceAppointment.getExtendMinutes() != null ? (busiMcuZjConferenceAppointment.getExtendMinutes() + minutes) : minutes;
                if (extendMinutesNew > 1440) {
                    throw new CustomException("会议总延时最多24小时");
                }
                busiMcuZjConferenceAppointment.setExtendMinutes(extendMinutesNew);
                AppointmentConferenceRepeatRate rr = AppointmentConferenceRepeatRate.convert(busiMcuZjConferenceAppointment.getRepeatRate());
                Date end = null;
                if (rr == AppointmentConferenceRepeatRate.CUSTOM) {
                    end = DateUtils.convertToDate(busiMcuZjConferenceAppointment.getEndTime());
                } else {
                    String today = DateUtils.formatTo(DateTimeFormatPattern.PATTERN_13.getPattern());
                    end = DateUtils.convertToDate(today + " " + busiMcuZjConferenceAppointment.getEndTime());
                }

                if (busiMcuZjConferenceAppointment.getExtendMinutes() != null) {
                    end = DateUtils.getDiffDate(end, busiMcuZjConferenceAppointment.getExtendMinutes(), TimeUnit.MINUTES);
                }

                BusiMcuZjConferenceAppointment con = new BusiMcuZjConferenceAppointment();
                con.setTemplateId(busiMcuZjConferenceAppointment.getTemplateId());
                List<BusiMcuZjConferenceAppointment> cas = busiMcuZjConferenceAppointmentMapper.selectBusiMcuZjConferenceAppointmentList(con);
                if (!ObjectUtils.isEmpty(cas)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String endTime = sdf.format(end);
                    for (BusiConferenceAppointment busiConferenceAppointmentTemp : cas) {
                        if (busiConferenceAppointmentTemp.getId().longValue() != busiMcuZjConferenceAppointment.getId().longValue()) {
                            if (endTime.compareTo(busiConferenceAppointmentTemp.getStartTime()) >= 0 && endTime.compareTo(busiConferenceAppointmentTemp.getEndTime()) <= 0) {
                                throw new SystemException(1008435, "延长会议结束时间失败：延长后的结束时间已存在相同模板的预约会议！");
                            }
                        }
                    }
                }

                busiMcuZjConferenceAppointmentMapper.updateBusiMcuZjConferenceAppointment(busiMcuZjConferenceAppointment);
                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议结束时间延长至：" + DateUtils.formatTo(DateTimeFormatPattern.PATTERN_11, end));
            }
        } else {
            Long templateConferenceId = conferenceContext.getTemplateConferenceId();
            BusiMcuZjTemplateConference tc = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceById(templateConferenceId);
            Integer durationTimeNew = tc.getDurationTime() + minutes;
            if (durationTimeNew > 2880) {
                throw new CustomException("会议总延时最多24小时");
            }
            tc.setDurationTime(durationTimeNew);
            busiMcuZjTemplateConferenceMapper.updateBusiMcuZjTemplateConference(tc);
            conferenceContext.setDurationTime(durationTimeNew);
            busiMcuZjConferenceAppointment = new BusiMcuZjConferenceAppointment();
            if (conferenceContext.getStartTime() != null) {
                busiMcuZjConferenceAppointment.setStartTime(DateUtil.convertDateToString(conferenceContext.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
                busiMcuZjConferenceAppointment.setEndTime(DateUtil.convertDateToString(new Date(conferenceContext.getStartTime().getTime() + durationTimeNew * 60000), "yyyy-MM-dd HH:mm:ss"));
                busiMcuZjConferenceAppointment.setExtendMinutes(0);
                busiMcuZjConferenceAppointment.setType(0);
            }
        }
        mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
        return busiMcuZjConferenceAppointment;
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            if (!enabled) {
                try {
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
                } catch (Exception e) {
                }
            }

            String streamingRemoteParty = conferenceContext.getStreamingRemoteParty();
            if (StringUtils.isEmpty(streamingRemoteParty)) {
                if (StringUtils.isNotEmpty(streamUrl)) {
                    BusiLiveSettingMapper busiLiveSettingMapper = BeanFactory.getBean(BusiLiveSettingMapper.class);
                    BusiLiveSetting busiLiveSettingCon = new BusiLiveSetting();
                    busiLiveSettingCon.setUrl(streamUrl);
                    List<BusiLiveSetting> busiLiveSettingList = busiLiveSettingMapper.selectBusiLiveSettingList(busiLiveSettingCon);
                    if (busiLiveSettingList.size() > 0) {
                        BusiLiveSetting busiLiveSetting = busiLiveSettingList.get(0);
                        if (StringUtils.isNotEmpty(busiLiveSetting.getRemoteParty())) {
                            streamingRemoteParty = busiLiveSetting.getRemoteParty();
                            conferenceContext.setStreamingRemoteParty(streamingRemoteParty);
                        }
                        conferenceContext.setStreamingName(busiLiveSetting.getName());
                    }
                }
                if (StringUtils.isNotEmpty(conferenceContext.getStreamingRemoteParty())) {
                    InvitedAttendeeForMcuZj ia = new InvitedAttendeeForMcuZj();
                    ia.setConferenceNumber(conferenceContext.getConferenceNumber());
                    ia.setId(java.util.UUID.randomUUID().toString());
                    ia.setName(conferenceContext.getStreamingName());
                    ia.setRemoteParty(conferenceContext.getStreamingRemoteParty());
                    ia.setWeight(1);
                    ia.setDeptId(conferenceContext.getDeptId());
                    if (ia.getRemoteParty().contains("@"))
                    {
                        ia.setIp(ia.getRemoteParty().split("@")[1]);
                    }
                    else
                    {
                        ia.setIp(ia.getRemoteParty());
                    }
                    ia.setCallType(2);
                    conferenceContext.setStreamingAttendee(ia);
                }
            }
            if (StringUtils.isNotEmpty(streamingRemoteParty)) {
                AttendeeForMcuZj streamingAttendee = conferenceContext.getStreamingAttendee();
                if (enabled) {
                    if (streamingAttendee == null || !streamingAttendee.isMeetingJoined()) {
                        InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask("stream_" + conferenceId, 100, conferenceContext, streamingAttendee);
                        delayTaskService.addTask(inviteAttendeesTask);
                    }
                } else {
                    if (streamingAttendee != null) {
                        if (StringUtils.isNotEmpty(streamingAttendee.getEpUserId())) {
                            CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
                            ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_drop);
                            ccUpdateMrEpsStatusRequest.setUsr_ids(new String[]{streamingAttendee.getEpUserId()});
                            conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
                        }
                    }
                }
            }
            else {
                if (enabled) {
                    CcUpdateBypassUrlRequest ccUpdateBypassUrlRequest = new CcUpdateBypassUrlRequest();
                    ccUpdateBypassUrlRequest.setBypass_url(streamUrl);
                    ccUpdateBypassUrlRequest.setEnable_bypass(1);
                    conferenceContext.getConferenceControlApi().updateBypassUrl(ccUpdateBypassUrlRequest);
                    conferenceContext.setStreamingUrl(streamUrl);
                } else {
                    CcUpdateMrStatusRequest ccUpdateMrStatusRequest = new CcUpdateMrStatusRequest();
                    ccUpdateMrStatusRequest.setMrStatusAction(CcUpdateMrStatusRequest.PARAM_enable_bypass);
                    ccUpdateMrStatusRequest.setMrStatusValue(0);
                    conferenceContext.getConferenceControlApi().updateMrStatus(ccUpdateMrStatusRequest);
                }
//            conferenceContext.setStreamingUrl(streamUrl);
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
    public void stream(McuZjConferenceContext mainConferenceContext, Boolean streaming, String streamUrl) {

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
    public List<Map<String, String>> getLayoutTemplates(Long deptId) {
        List<Map<String, String>> list = LayoutTemplates.getLayoutTemplateList();
        return list;
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
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        long diff = System.currentTimeMillis() - conferenceContext.getStartTime().getTime();
        if (diff < 5000) {
            throw new SystemException(1, "MCU准备中，请等待" + (diff / 1000 + 1) + "秒！");
        }
        if (conferenceContext.isInvitingTerminal()) {
            throw new SystemException(1, "呼入处理正在进行中，请稍后再试！");
        }
        Map<Long, TerminalAttendeeForMcuZj> terminalAttendeeMap = conferenceContext.getTerminalAttendeeMap();
        List<AttendeeForMcuZj> attendees = new ArrayList<>();
        for (TerminalAttendeeForMcuZj value : terminalAttendeeMap.values()) {
            if (value.getAttendType() == 1 && !TerminalType.isFCMSIP(value.getTerminalType())) {
                AttendeeForMcuZj attendeeById = conferenceContext.getAttendeeById(value.getId());
                if (attendeeById.getMeetingStatus() == AttendeeMeetingStatus.OUT.getValue() && attendeeById.getOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                    attendees.add(value);
                }
            }
        }
        List<McuAttendeeForMcuZj> mcuAttendees = conferenceContext.getMcuAttendees();
        for (McuAttendeeForMcuZj value : mcuAttendees) {
            AttendeeForMcuZj attendeeById = conferenceContext.getAttendeeById(value.getId());
            if (attendeeById.getMeetingStatus() == AttendeeMeetingStatus.OUT.getValue() && attendeeById.getOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                attendees.add(value);
            }
        }
        if (attendees.size() > 0) {
            InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 100, conferenceContext, attendees);
            delayTaskService.addTask(inviteAttendeesTask);
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
        final String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZjConferenceContext conferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.getSyncInformation() != null) {
            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始1！");
            return;
        }

        synchronized (conferenceContext.getSyncLock()) {
            if (conferenceContext.getSyncInformation() != null) {
                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始2！");
                return;
            }

            McuZjSyncInformation syncInformation = new McuZjSyncInformation();
            conferenceContext.setSyncInformation(syncInformation);

            int totalCount = 0;
            syncInformation.setInProgress(true);
            syncInformation.setTotalCallCount(0);
            syncInformation.setReason("同步");

            LOGGER.info("One click synchronization start：" + conferenceContext.getConferenceNumber());

            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "开始同步本会议的参会信息！");
            Set<String> attendeeIds = new HashSet<>();
            McuZjConferenceContextUtils.eachAttendeeInConference(conferenceContext, (attendee) -> {
                attendeeIds.add(attendee.getId());
                if (attendee.isMeetingJoined()) {
                    synchronized (attendee) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeForMcuZj) {
                            TerminalAttendeeForMcuZj terminalAttendee = (TerminalAttendeeForMcuZj) attendee;
                            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                            terminalAttendee.setTerminalType(bt.getType());
                            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                            if (attendee.isMeetingJoined()) {
                                terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            } else {
                                terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                            }
                        } else if (attendee instanceof InvitedAttendeeForMcuZj) {
                            InvitedAttendeeForMcuZj invitedAttendee = (InvitedAttendeeForMcuZj) attendee;
                            if (invitedAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(invitedAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    invitedAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    invitedAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        } else if (attendee instanceof SelfCallAttendeeForMcuZj) {
                            SelfCallAttendeeForMcuZj selfCallAttendee = (SelfCallAttendeeForMcuZj) attendee;
                            if (selfCallAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(selfCallAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    selfCallAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    selfCallAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        }
                        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                    }
                } else {
                    synchronized (attendee) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeForMcuZj) {
                            TerminalAttendeeForMcuZj terminalAttendee = (TerminalAttendeeForMcuZj) attendee;
                            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                            terminalAttendee.setTerminalType(bt.getType());
                            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                            if (attendee.isMeetingJoined()) {
                                terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            } else {
                                terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                            }
                        } else if (attendee instanceof InvitedAttendeeForMcuZj) {
                            InvitedAttendeeForMcuZj invitedAttendee = (InvitedAttendeeForMcuZj) attendee;
                            if (invitedAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(invitedAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    invitedAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    invitedAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        } else if (attendee instanceof SelfCallAttendeeForMcuZj) {
                            SelfCallAttendeeForMcuZj selfCallAttendee = (SelfCallAttendeeForMcuZj) attendee;
                            if (selfCallAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(selfCallAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    selfCallAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    selfCallAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        }
                        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                    }
                }
            });

            totalCount = attendeeIds.size();

            syncInformation.setTotalCallCount(totalCount);
            syncInformation.setCurrentCallTotalParticipantCount(totalCount);
            syncInformation.setCurrentCallMcuIp(conferenceContext.getBusiMcuZj().getIp());
            syncInformation.setSyncCurrentCallParticipantCount(totalCount);

            syncInformation.setInProgress(false);
            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.PARTICIPANT_SYNC, syncInformation);
            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "已同步完所有参会信息，共【" + totalCount + "】个！");
            conferenceContext.setSyncInformation(null);
        }
    }

    @Override
    public Integer getLiveTerminalCount(String conferenceId) {
        Integer liveConferenceTerminalCount = LiveBridgeCache.getInstance().getLiveConferenceTerminalCount(conferenceId);
        return liveConferenceTerminalCount;
    }
}
