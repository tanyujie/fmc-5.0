package com.paradisecloud.fcm.zte.service.impls;

import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZteConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZteTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.paradisecloud.fcm.zte.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.zte.attendee.model.operation.DiscussAttendeeOperation;
import com.paradisecloud.fcm.zte.attendee.utils.McuZteConferenceContextUtils;
import com.paradisecloud.fcm.zte.cache.McuZteConferenceContextCache;
import com.paradisecloud.fcm.zte.cache.McuZteWebSocketMessagePusher;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.zte.model.busi.attendee.*;
import com.paradisecloud.fcm.zte.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.zte.model.core.McuZteSyncInformation;
import com.paradisecloud.fcm.zte.model.enumer.McuZteLayoutTemplates;
import com.paradisecloud.fcm.zte.model.request.cc.CcLockConferenceRequest;
import com.paradisecloud.fcm.zte.model.request.cc.CcUnlockConferenceRequest;
import com.paradisecloud.fcm.zte.model.request.cm.CmGetMrCdrRequest;
import com.paradisecloud.fcm.zte.model.request.cm.CmStopMrRequest;
import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import com.paradisecloud.fcm.zte.model.response.cm.CmGetMrCdrResponse;
import com.paradisecloud.fcm.zte.model.response.cm.CmStopMrResponse;
import com.paradisecloud.fcm.zte.model.templateconference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.zte.model.templateconference.StartTemplateConference;
import com.paradisecloud.fcm.zte.service.interfaces.*;
import com.paradisecloud.fcm.zte.task.InviteAttendeesTask;
import com.paradisecloud.fcm.zte.task.McuZteDelayTaskService;
import com.sinhy.enumer.DateTimeFormatPattern;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import com.zte.m900.request.*;
import com.zte.m900.response.ConferenceResponse;
import com.zte.m900.response.EndConferenceResponse;
import com.zte.m900.response.ProlongConferenceResponse;
import org.apache.http.NameValuePair;
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
public class BusiMcuZteConferenceServiceImpl implements IBusiMcuZteConferenceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BusiMcuZteConferenceServiceImpl.class);

    @Resource
    private BusiMcuZteTemplateConferenceMapper busiMcuZteTemplateConferenceMapper;
    @Resource
    private BusiMcuZteConferenceAppointmentMapper busiMcuZteConferenceAppointmentMapper;
    @Resource
    private IBusiMcuZteConferenceAppointmentService busiMcuZteConferenceAppointmentService;
    @Resource
    private IBusiMcuZteTemplateConferenceService busiMcuZteTemplateConferenceService;
    @Resource
    private IBusiHistoryConferenceForMcuZteService busiHistoryConferenceForMcuZteService;
    @Resource
    private IAttendeeForMcuZteService attendeeForMcuZteService;
    @Resource
    private McuZteDelayTaskService mcuZteDelayTaskService;
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
    public McuZteConferenceContext buildTemplateConferenceContext(long templateConferenceId) {
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
        String contextKey = EncryptIdUtil.parasToContextKey(encryptConferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        conferenceContext.setEndReasonsType(EndReasonsType.ADMINISTRATOR_HANGS_UP);
        endConference(contextKey, endType, true, true);
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

        {
            McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
            if (conferenceContext != null) {
                if (forceEnd) {

                    EndConferenceRequest cmStopMrRequest = new EndConferenceRequest();
                    cmStopMrRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    EndConferenceResponse cmStopMrResponse = conferenceContext.getMcuZteBridge().getConferenceManageApi().stopMr(cmStopMrRequest);
                    if (cmStopMrResponse == null || !CommonResponse.STATUS_OK.equals(cmStopMrResponse.getStatus())) {
                        if(cmStopMrResponse!=null){
                            LOGGER.info("zte结束会议失败："+"("+conferenceContext.getConferenceNumber()+")"+cmStopMrResponse.getResult());

                            EndConferenceRequest cmStopMrRequest2 = new EndConferenceRequest();
                            cmStopMrRequest2.setConferenceIdentifier(conferenceContext.getConferenceNumber());
                            cmStopMrRequest2.setConferenceIdOption("1");
                            EndConferenceResponse cmStopMrResponse2 = conferenceContext.getMcuZteBridge().getConferenceManageApi().stopMr(cmStopMrRequest2);
                            if(cmStopMrResponse2 == null || !CommonResponse.STATUS_OK.equals(cmStopMrResponse2.getStatus())){
                                LOGGER.info("zte结束会议失败："+"("+conferenceContext.getConferenceNumber()+")"+cmStopMrResponse.getResult());
                            }

                        }
                       // throw new CustomException("结束会议失败");
                    }

                }

                if (pushMessage) {
                    BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment = conferenceContext.getConferenceAppointment();
                    if (busiMcuZteConferenceAppointment != null) {
                        busiMcuZteConferenceAppointment.setIsHangUp(YesOrNo.YES.getValue());
                        busiMcuZteConferenceAppointment.setIsStart(null);
                        busiMcuZteConferenceAppointment.setExtendMinutes(null);
                        busiMcuZteConferenceAppointmentMapper.updateBusiMcuZteConferenceAppointment(busiMcuZteConferenceAppointment);

                        AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiMcuZteConferenceAppointment.getRepeatRate());
                        if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM) {
                            busiMcuZteConferenceAppointmentService.deleteBusiMcuZteConferenceAppointmentById(busiMcuZteConferenceAppointment.getId());
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
                            CmGetMrCdrResponse cmGetMrCdrResponse = conferenceContext.getMcuZteBridge().getConferenceManageApi().getMrCdr(cmGetMrCdrRequest);
                            if (cmGetMrCdrResponse != null && CommonResponse.STATUS_OK.equals(cmGetMrCdrResponse.getStatus())) {
                                if (cmGetMrCdrResponse.getEndReasonType() != null) {
                                    conferenceContext.setEndReasonsType(cmGetMrCdrResponse.getEndReasonType());
                                }
                            }
                        } catch (Exception e) {
                        }
                        busiHistoryConference.setEndReasonsType(conferenceContext.getEndReasonsType());
                        busiHistoryConferenceForMcuZteService.saveHistory(busiHistoryConference, conferenceContext);

                    }
                    BusiMcuZteTemplateConference busiMcuZteTemplateConference = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(conferenceContext.getTemplateConferenceId());
                    if (busiMcuZteTemplateConference != null) {
                        busiMcuZteTemplateConference.setCreateTime(new Date());
                        busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(busiMcuZteTemplateConference);
                    }
                    conferenceContext.setEnd(true);
                    conferenceContext.setEndTime(new Date());
                    McuZteConferenceContextCache.getInstance().remove(contextKey);
                    redisCache.deleteObject(conferenceContext.getId() + "_" + "_master_attendee");
                    pushEndMessageToMqtt(conferenceContext.getConferenceNumber(), conferenceContext);
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_ENDED, "会议已结束");
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已结束");
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
    private void pushEndMessageToMqtt(String conferenceNumber, McuZteConferenceContext conferenceContext) {
        List<BaseAttendee> mqttJoinTerminals = new ArrayList<>();
        McuZteConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeForMcuZte) {
                TerminalAttendeeForMcuZte ta = (TerminalAttendeeForMcuZte) a;
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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {

            ConferenceResponse commonResponse;

            if (locked) {
                LockConferenceRequest ccLockConferenceRequest = new LockConferenceRequest();
                ccLockConferenceRequest.setConferenceIdentifier(conferenceContext.getConfId());

                commonResponse = conferenceContext.getConferenceControlApi().lockConferenceRequest(ccLockConferenceRequest);
            } else {
                UnlockConferenceRequest unLockConferenceRequest = new UnlockConferenceRequest();
                unLockConferenceRequest.setConferenceIdentifier(conferenceContext.getConfId());
                commonResponse = conferenceContext.getConferenceControlApi().unLockConferenceRequest(unLockConferenceRequest);
            }

            if (commonResponse != null && Objects.equals(commonResponse.getStatus(), CommonResponse.STATUS_OK)) {
                conferenceContext.setLocked(locked);
                // 消息和参会者信息同步到主级会议
                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_LOCK, locked);
                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已" + (locked ? "" : "解除") + "锁定");
                // 解除锁定后，推送消息更新前端状态
                if (!locked) {
                    McuZteConferenceContextUtils.eachNonMcuAttendeeInConference(conferenceContext, (a) -> {
                        if (a.isLocked()) {
                            synchronized (a) {
                                a.resetUpdateMap();
                                a.setLocked(locked);
                                if (a.getUpdateMap().size() > 1) {
                                    McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(a.getUpdateMap()));
                                }
                            }
                        }
                    });
                }
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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
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
     * @return
     * @author lilinhai
     * @since 2021-05-27 16:59
     */
    @Override
    public BusiMcuZteConferenceAppointment extendMinutes(String conferenceId, int minutes) {
        String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            throw new CustomException("会议不存在或者未开始");
        }
        ProlongConferenceRequest prolongConferenceRequest = new ProlongConferenceRequest();
        prolongConferenceRequest.setConferenceIdentifier(conferenceContext.getConfId());
        prolongConferenceRequest.setProlongTime(minutes);
        ProlongConferenceResponse prolongConferenceResponse = conferenceContext.getConferenceControlApi().prolongConference(prolongConferenceRequest);
        if (prolongConferenceResponse == null || (prolongConferenceResponse != null && !CommonResponse.STATUS_OK.equals(prolongConferenceResponse.getStatus()))) {
            throw new CustomException("延长会议失败");
        }

        BusiMcuZteConferenceAppointment busiMcuZteConferenceAppointment = conferenceContext.getConferenceAppointment();
        if (busiMcuZteConferenceAppointment != null) {
            busiMcuZteConferenceAppointment = busiMcuZteConferenceAppointmentMapper.selectBusiMcuZteConferenceAppointmentById(busiMcuZteConferenceAppointment.getId());
            if (busiMcuZteConferenceAppointment != null) {
                Integer extendMinutesNew = busiMcuZteConferenceAppointment.getExtendMinutes() != null ? (busiMcuZteConferenceAppointment.getExtendMinutes() + minutes) : minutes;
                if (extendMinutesNew > 1440) {
                    throw new CustomException("会议总延时最多24小时");
                }
                busiMcuZteConferenceAppointment.setExtendMinutes(extendMinutesNew);
                AppointmentConferenceRepeatRate rr = AppointmentConferenceRepeatRate.convert(busiMcuZteConferenceAppointment.getRepeatRate());
                Date end = null;
                if (rr == AppointmentConferenceRepeatRate.CUSTOM) {
                    end = DateUtils.convertToDate(busiMcuZteConferenceAppointment.getEndTime());
                } else {
                    String today = DateUtils.formatTo(DateTimeFormatPattern.PATTERN_13.getPattern());
                    end = DateUtils.convertToDate(today + " " + busiMcuZteConferenceAppointment.getEndTime());
                }

                if (busiMcuZteConferenceAppointment.getExtendMinutes() != null) {
                    end = DateUtils.getDiffDate(end, busiMcuZteConferenceAppointment.getExtendMinutes(), TimeUnit.MINUTES);
                }

                BusiMcuZteConferenceAppointment con = new BusiMcuZteConferenceAppointment();
                con.setTemplateId(busiMcuZteConferenceAppointment.getTemplateId());
                List<BusiMcuZteConferenceAppointment> cas = busiMcuZteConferenceAppointmentMapper.selectBusiMcuZteConferenceAppointmentList(con);
                if (!ObjectUtils.isEmpty(cas)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String endTime = sdf.format(end);
                    for (BusiConferenceAppointment busiConferenceAppointmentTemp : cas) {
                        if (busiConferenceAppointmentTemp.getId().longValue() != busiMcuZteConferenceAppointment.getId().longValue()) {
                            if (endTime.compareTo(busiConferenceAppointmentTemp.getStartTime()) >= 0 && endTime.compareTo(busiConferenceAppointmentTemp.getEndTime()) <= 0) {
                                throw new SystemException(1008435, "延长会议结束时间失败：延长后的结束时间已存在相同模板的预约会议！");
                            }
                        }
                    }
                }


                busiMcuZteConferenceAppointmentMapper.updateBusiMcuZteConferenceAppointment(busiMcuZteConferenceAppointment);
                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议结束时间延长至：" + DateUtils.formatTo(DateTimeFormatPattern.PATTERN_11, end));
            }
        } else {
            Long templateConferenceId = conferenceContext.getTemplateConferenceId();
            BusiMcuZteTemplateConference tc = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceById(templateConferenceId);
            Integer durationTimeNew = tc.getDurationTime() + minutes;
            if (durationTimeNew > 2880) {
                throw new CustomException("会议总延时最多24小时");
            }
            tc.setDurationTime(durationTimeNew);
            busiMcuZteTemplateConferenceMapper.updateBusiMcuZteTemplateConference(tc);
            conferenceContext.setDurationTime(durationTimeNew);

        }
        mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
        return busiMcuZteConferenceAppointment;
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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
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
    public void stream(McuZteConferenceContext mainConferenceContext, Boolean streaming, String streamUrl) {

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
        return McuZteLayoutTemplates.getLayoutTemplateScreenList();
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
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        long diff = System.currentTimeMillis() - conferenceContext.getStartTime().getTime();
        if (diff < 5000) {
            throw new SystemException(1, "MCU准备中，请等待" + (diff / 1000 + 1) + "秒！");
        }
        if (conferenceContext.isInvitingTerminal()) {
            throw new SystemException(1, "呼入处理正在进行中，请稍后再试！");
        }
        Map<Long, TerminalAttendeeForMcuZte> terminalAttendeeMap = conferenceContext.getTerminalAttendeeMap();
        List<AttendeeForMcuZte> attendees = new ArrayList<>();
        for (TerminalAttendeeForMcuZte value : terminalAttendeeMap.values()) {
            if (value.getAttendType() == 1 && !TerminalType.isFCMSIP(value.getTerminalType())) {
                AttendeeForMcuZte attendeeById = conferenceContext.getAttendeeById(value.getId());
                if (attendeeById.getMeetingStatus() == AttendeeMeetingStatus.OUT.getValue() && attendeeById.getOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                    attendees.add(value);
                }
            }
        }
        List<McuAttendeeForMcuZte> mcuAttendees = conferenceContext.getMcuAttendees();
        for (McuAttendeeForMcuZte value : mcuAttendees) {
            AttendeeForMcuZte attendeeById = conferenceContext.getAttendeeById(value.getId());
            if (attendeeById.getMeetingStatus() == AttendeeMeetingStatus.OUT.getValue() && attendeeById.getOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                attendees.add(value);
            }
        }
        if (attendees.size() > 0) {
            InviteAttendeesTask inviteAttendeesTask = new InviteAttendeesTask(conferenceContext.getId(), 100, conferenceContext, attendees);
            mcuZteDelayTaskService.addTask(inviteAttendeesTask);
        }
    }

    /**
     * <pre>同步会议数据</pre>
     *
     * @param conferenceId void
     * @author sinhy
     * @since 2021-08-30 10:46
     */
    @Override
    public void sync(String conferenceId) {
        final String contextKey = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.getSyncInformation() != null) {
            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始1！");
            return;
        }

        synchronized (conferenceContext.getSyncLock()) {
            if (conferenceContext.getSyncInformation() != null) {
                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始2！");
                return;
            }

            McuZteSyncInformation syncInformation = new McuZteSyncInformation();
            conferenceContext.setSyncInformation(syncInformation);

            int totalCount = 0;
            syncInformation.setInProgress(true);
            syncInformation.setTotalCallCount(0);
            syncInformation.setReason("同步");

            LOGGER.info("One click synchronization start：" + conferenceContext.getConferenceNumber());

            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "开始同步本会议的参会信息！");
            Set<String> attendeeIds = new HashSet<>();
            McuZteConferenceContextUtils.eachAttendeeInConference(conferenceContext, (attendee) -> {
                attendeeIds.add(attendee.getId());
                if (attendee.isMeetingJoined()) {
                    synchronized (attendee) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeForMcuZte) {
                            TerminalAttendeeForMcuZte terminalAttendee = (TerminalAttendeeForMcuZte) attendee;
                            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                            terminalAttendee.setTerminalType(bt.getType());
                            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                            if (attendee.isMeetingJoined()) {
                                terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            } else {
                                terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                            }
                        } else if (attendee instanceof InvitedAttendeeForMcuZte) {
                            InvitedAttendeeForMcuZte invitedAttendee = (InvitedAttendeeForMcuZte) attendee;
                            if (invitedAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(invitedAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    invitedAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    invitedAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        } else if (attendee instanceof SelfCallAttendeeForMcuZte) {
                            SelfCallAttendeeForMcuZte selfCallAttendee = (SelfCallAttendeeForMcuZte) attendee;
                            if (selfCallAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(selfCallAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    selfCallAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    selfCallAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        }
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                    }
                } else {
                    synchronized (attendee) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeForMcuZte) {
                            TerminalAttendeeForMcuZte terminalAttendee = (TerminalAttendeeForMcuZte) attendee;
                            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                            terminalAttendee.setTerminalType(bt.getType());
                            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                            if (attendee.isMeetingJoined()) {
                                terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            } else {
                                terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                            }
                        } else if (attendee instanceof InvitedAttendeeForMcuZte) {
                            InvitedAttendeeForMcuZte invitedAttendee = (InvitedAttendeeForMcuZte) attendee;
                            if (invitedAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(invitedAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    invitedAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    invitedAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        } else if (attendee instanceof SelfCallAttendeeForMcuZte) {
                            SelfCallAttendeeForMcuZte selfCallAttendee = (SelfCallAttendeeForMcuZte) attendee;
                            if (selfCallAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(selfCallAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    selfCallAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    selfCallAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        }
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                    }
                }
            });

            totalCount = attendeeIds.size();

            syncInformation.setTotalCallCount(totalCount);
            syncInformation.setCurrentCallTotalParticipantCount(totalCount);
            syncInformation.setCurrentCallMcuIp(conferenceContext.getBusiMcuZte().getIp());
            syncInformation.setSyncCurrentCallParticipantCount(totalCount);

            syncInformation.setInProgress(false);
            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.PARTICIPANT_SYNC, syncInformation);
            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "已同步完所有参会信息，共【" + totalCount + "】个！");
            conferenceContext.setSyncInformation(null);
        }
    }

    @Override
    public Integer getLiveTerminalCount(String conferenceId) {
        Integer liveConferenceTerminalCount = LiveBridgeCache.getInstance().getLiveConferenceTerminalCount(conferenceId);
        return liveConferenceTerminalCount;
    }


    @Override
    public void voiceActive(String conferenceId, Boolean enable) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        SwitchConfCtrlModeRequest switchConfCtrlModeRequest = new SwitchConfCtrlModeRequest();
        switchConfCtrlModeRequest.setConferenceIdentifier(conferenceContext.getConfId());
        switchConfCtrlModeRequest.setConfCtrlMode(enable?"vas":"director");
        conferenceContext.getConferenceControlApi().switchConfCtrlMode(switchConfCtrlModeRequest);
    }

    @Override
    public void changeQuiet(String conferenceId, boolean enable) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        McuZteConferenceContext conferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
        if(enable){
            QuietParticipantRequest quietParticipantRequest = new QuietParticipantRequest();
            quietParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
            conferenceContext.getConferenceControlApi().quietParticipant(quietParticipantRequest);
        }else {
            CancelQuietParticipantRequest cancelQuietParticipantRequest = new CancelQuietParticipantRequest();
            cancelQuietParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
            conferenceContext.getConferenceControlApi().cancelQuietParticipant(cancelQuietParticipantRequest);
        }

    }
}
