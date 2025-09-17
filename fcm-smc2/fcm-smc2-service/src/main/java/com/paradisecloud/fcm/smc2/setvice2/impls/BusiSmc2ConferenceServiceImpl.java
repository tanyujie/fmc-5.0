package com.paradisecloud.fcm.smc2.setvice2.impls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.ChooseMultiPicInfo;
import com.paradisecloud.com.fcm.smc.modle.ConferenceState;
import com.paradisecloud.com.fcm.smc.modle.DetailConference;
import com.paradisecloud.com.fcm.smc.modle.PollOperateTypeDto;
import com.paradisecloud.com.fcm.smc.modle.request.BroadcastPollRequest;
import com.paradisecloud.com.fcm.smc.modle.request.ChairmanPollOperateReq;
import com.paradisecloud.com.fcm.smc.modle.request.MultiPicInfoReq;
import com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.AesEnsUtils;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2ConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2TemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.enumer.FmeBridgeProcessingStrategy;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.smc2.cache.Smc2BridgeCache;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.conference.templateconference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.smc2.conference.templateconference.StartTemplateConference;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.SyncInformation;
import com.paradisecloud.fcm.smc2.model.attendee.*;
import com.paradisecloud.fcm.smc2.model.attendee.operation.ChairmanPollingAttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.operation.DiscussAttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.operation.PollingAttendeeOperation;
import com.paradisecloud.fcm.smc2.model.layout.ContinuousPresenceModeEnum;
import com.paradisecloud.fcm.smc2.model.layout.LayoutTemplates;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiConferenceNumberSmc2Service;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2ConferenceAppointmentService;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2HistoryConferenceService;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiSmc2ConferenceService;
import com.paradisecloud.fcm.smc2.task.InviteAttendeeSmc2Task;
import com.paradisecloud.fcm.smc2.task.Smc2DelayTaskService;
import com.paradisecloud.fcm.smc2.utils.AttendeeSmc2Utils;
import com.paradisecloud.fcm.smc2.utils.Smc2ConferenceContextUtils;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.sinhy.enumer.DateTimeFormatPattern;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.GetContinuousPresenceParamExResponse;
import com.suntek.smc.esdk.pojo.local.WSCtrlSiteCommParamEx;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.apache.http.NameValuePair;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.xml.datatype.Duration;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 会议相关服务类
 */
@Service

public class BusiSmc2ConferenceServiceImpl implements IBusiSmc2ConferenceService {
    public static final int _Max2880 = 2880;
    public static final String SMC_2 = "会议监控smc2";
    private static final Logger LOGGER = LoggerFactory.getLogger(BusiSmc2ConferenceServiceImpl.class);
    @Resource
    private BusiMcuSmc2TemplateConferenceMapper busiSmc2TemplateConferenceMapper;
    @Resource
    private BusiMcuSmc2ConferenceAppointmentMapper busiSmc2ConferenceAppointmentMapper;
    @Resource
    private IBusiMcuSmc2ConferenceAppointmentService busiSmc2ConferenceAppointmentService;

    @Resource
    private IBusiMcuSmc2HistoryConferenceService busiSmc2HistoryConferenceService;

    @Resource
    private Smc2DelayTaskService Smc2delayTaskService;
    @Resource
    private IMqttService mqttService;

    @Resource
    private IBusiConferenceNumberSmc2Service iBusiConferenceNumberSmc2Service;

    /**
     * 开始模板会议
     *
     * @param templateConferenceId void
     * @return
     */
    @Override
    public String startTemplateConference(long templateConferenceId) {
        Smc2ConferenceContext smc2ConferenceContext = new StartTemplateConference().startTemplateConference(templateConferenceId);
        return smc2ConferenceContext.getContextKey();
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
    public Smc2ConferenceContext buildTemplateConferenceContext(long templateConferenceId) {
        return new BuildTemplateConferenceContext().buildTemplateConferenceContext(templateConferenceId);
    }

    /**
     * <pre>根据coSpaceId挂断会议</pre>
     *
     * @param encryptConferenceId void
     * @param endType
     * @author lilinhai
     * @since 2021-02-02 18:05
     */
    @Override
    public void endConference(String encryptConferenceId, int endType) {
        String contextKey = EncryptIdUtil.parasToContextKey(encryptConferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            conferenceContext.setEndReasonsType(endType);
            endConference(contextKey, endType, true, false);
        }
    }

    /**
     * <pre>根据coSpaceId挂断会议</pre>
     *
     * @param conferenceNumber void
     * @param endType
     * @author lilinhai
     * @since 2021-02-02 18:05
     */
    @Override
    public void endConference(String conferenceNumber, int endType, boolean forceEnd, boolean pushMessage) {
        // 会议结束类型
        ConferenceEndType conferenceEndType = ConferenceEndType.convert(endType);
        if (conferenceEndType == ConferenceEndType.CASCADE) {


        }
//        else
        {
            Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceNumber);
            if (conferenceContext != null) {
                if (forceEnd) {
                    ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
                    Integer resultCode = conferenceServiceEx.delScheduledConfEx(conferenceContext.getSmc2conferenceId(), null);
                    if (resultCode != 0) {
                        throw new CustomException("smc2.0 endConference-error" + resultCode);
                    }

                    conferenceContext.setEnd(true);
                    conferenceContext.setEndTime(new Date());
                    AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
                    if (attendeeOperation != null) {
                        attendeeOperation.cancel();
                    }
                }


                BusiMcuSmc2ConferenceAppointment busiSmc2ConferenceAppointment = conferenceContext.getConferenceAppointment();
                if (busiSmc2ConferenceAppointment != null) {
                    busiSmc2ConferenceAppointment.setIsHangUp(YesOrNo.YES.getValue());
                    busiSmc2ConferenceAppointment.setIsStart(null);
                    busiSmc2ConferenceAppointment.setExtendMinutes(null);
                    busiSmc2ConferenceAppointmentMapper.updateBusiMcuSmc2ConferenceAppointment(busiSmc2ConferenceAppointment);

                    AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiSmc2ConferenceAppointment.getRepeatRate());
                    if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM) {

                        busiSmc2ConferenceAppointmentService.deleteBusiMcuSmc2ConferenceAppointmentById(busiSmc2ConferenceAppointment.getId());
                    }
                }
                BusiHistoryConference busiHistoryConference = conferenceContext.getHistoryConference();
                if (busiHistoryConference != null) {
                    busiHistoryConference.setConferenceEndTime(new Date());
                    busiHistoryConference.setEndReasonsType(conferenceContext.getEndReasonsType());
                    busiSmc2HistoryConferenceService.saveHistory(busiHistoryConference, conferenceContext);

                }
                BusiMcuSmc2TemplateConference busiSmc2TemplateConference = busiSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(conferenceContext.getTemplateConferenceId());
                if (busiSmc2TemplateConference != null) {
                    busiSmc2TemplateConference.setUpdateTime(new Date());
                    if (busiSmc2TemplateConference.getIsAutoCreateConferenceNumber() == 1) {
                        busiSmc2TemplateConference.setConferenceNumber(null);
                        busiSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiSmc2TemplateConference);
                        iBusiConferenceNumberSmc2Service.deleteBusiConferenceNumberById(Long.parseLong(conferenceContext.getConferenceNumber()));
                    }
                    busiSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(busiSmc2TemplateConference);

                }


                try {
                    CoSpace coSpace = (CoSpace) conferenceContext.getMonitorParticipantMap().get(conferenceContext.getSmc2conferenceId());
                    if (coSpace != null) {
                        deleteCospace(BridgeUtils.getAvailableFmeBridge(conferenceContext.getDeptId()), coSpace.getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Smc2ConferenceContextCache.getInstance().remove(conferenceNumber);
                Smc2ConferenceContextCache.getInstance().remove(conferenceContext.getSmc2conferenceId());
                conferenceContext.clear();
                pushEndMessageToMqtt(conferenceContext.getTenantId() + conferenceContext.getConferenceNumber(), conferenceContext);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_ENDED, "会议已结束");
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已结束");
            }
        }

    }

    private void deleteCospace(FmeBridge fmeBridge, String cospaceId) {
        try {
            fmeBridge.getCoSpaceInvoker().deleteCoSpace(cospaceId);
            fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor() {
                @Override
                public void process(FmeBridge fmeBridge) {
                    fmeBridge.getDataCache().deleteCoSpace(cospaceId);
                }
            });
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("recoveryCospace fail: ", e);
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
    private void pushEndMessageToMqtt(String conferenceNumber, Smc2ConferenceContext conferenceContext) {
        List<BaseAttendee> mqttJoinTerminals = new ArrayList<>();
        Smc2ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeSmc2) {
                TerminalAttendeeSmc2 ta = (TerminalAttendeeSmc2) a;
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
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceNumber);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (!(attendeeOperation instanceof DiscussAttendeeOperation)) {
                if (attendeeOperation instanceof DefaultAttendeeOperation) {
                    conferenceContext.setLastAttendeeOperation(attendeeOperation);
                }
                DiscussAttendeeOperation discussAttendeeOperation = new DiscussAttendeeOperation(conferenceContext);
                conferenceContext.setAttendeeOperation(discussAttendeeOperation);
                attendeeOperation.cancel();
                discussAttendeeOperation.operate();
            }
        }
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
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceNumber);
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
    public BusiMcuSmc2ConferenceAppointment extendMinutes(String conferenceId, int minutes) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            throw new CustomException("会议不存在或者未开始");
        }
        if (minutes > 360 || minutes < 10) {
            throw new CustomException("会议每次延时最多360分钟,不能少于10分钟");
        }

        BusiMcuSmc2ConferenceAppointment busiSmc2ConferenceAppointment = conferenceContext.getConferenceAppointment();
        if (busiSmc2ConferenceAppointment != null) {
            busiSmc2ConferenceAppointment = busiSmc2ConferenceAppointmentMapper.selectBusiMcuSmc2ConferenceAppointmentById(busiSmc2ConferenceAppointment.getId());
            if (busiSmc2ConferenceAppointment != null) {
                Integer extendMinutesNew = busiSmc2ConferenceAppointment.getExtendMinutes() != null ? (busiSmc2ConferenceAppointment.getExtendMinutes() + minutes) : minutes;
                if (extendMinutesNew > 1440) {
                    throw new CustomException("会议总延时最多24小时");
                }
                busiSmc2ConferenceAppointment.setExtendMinutes(extendMinutesNew);
                AppointmentConferenceRepeatRate rr = AppointmentConferenceRepeatRate.convert(busiSmc2ConferenceAppointment.getRepeatRate());
                Date end = null;
                if (rr == AppointmentConferenceRepeatRate.CUSTOM) {
                    end = DateUtils.convertToDate(busiSmc2ConferenceAppointment.getEndTime());
                } else {
                    String today = DateUtils.formatTo(DateTimeFormatPattern.PATTERN_12.getPattern());
                    end = DateUtils.convertToDate(today + " " + busiSmc2ConferenceAppointment.getEndTime());
                }

                if (busiSmc2ConferenceAppointment.getExtendMinutes() != null) {
                    end = DateUtils.getDiffDate(end, busiSmc2ConferenceAppointment.getExtendMinutes(), TimeUnit.MINUTES);
                }

                BusiMcuSmc2ConferenceAppointment con = new BusiMcuSmc2ConferenceAppointment();
                con.setTemplateId(busiSmc2ConferenceAppointment.getTemplateId());
                List<BusiMcuSmc2ConferenceAppointment> cas = busiSmc2ConferenceAppointmentMapper.selectBusiMcuSmc2ConferenceAppointmentList(con);
                if (!ObjectUtils.isEmpty(cas)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String endTime = sdf.format(end);
                    for (BusiMcuSmc2ConferenceAppointment busiConferenceAppointmentTemp : cas) {
                        if (busiConferenceAppointmentTemp.getId().longValue() != busiSmc2ConferenceAppointment.getId().longValue()) {
                            if (endTime.compareTo(busiConferenceAppointmentTemp.getStartTime()) >= 0 && endTime.compareTo(busiConferenceAppointmentTemp.getEndTime()) <= 0) {
                                throw new SystemException(1008425, "延长会议结束时间失败：延长后的结束时间已存在相同模板的预约会议！");
                            }
                        }
                    }
                }

                busiSmc2ConferenceAppointmentMapper.updateBusiMcuSmc2ConferenceAppointment(busiSmc2ConferenceAppointment);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议结束时间延长至：" + DateUtils.formatTo(DateTimeFormatPattern.PATTERN_11, end));
            }
        } else {
            Long templateConferenceId = conferenceContext.getTemplateConferenceId();
            BusiMcuSmc2TemplateConference tc = busiSmc2TemplateConferenceMapper.selectBusiMcuSmc2TemplateConferenceById(templateConferenceId);
            Integer durationTimeNew = tc.getDurationTime() + minutes;
            if (durationTimeNew > _Max2880) {
                throw new CustomException("会议总延时最多24小时");
            }
            tc.setDurationTime(durationTimeNew);
            busiSmc2TemplateConferenceMapper.updateBusiMcuSmc2TemplateConference(tc);
            conferenceContext.setDurationTime(durationTimeNew);
        }

        try {
            //会议开始时间为8小时后，如果需要延长周期性会议中的单个会议时长，才需要该参数。
            Date date = conferenceContext.getStartTime();
            String confId = conferenceContext.getSmc2conferenceId();
            ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);

            Duration prolongTime = javax.xml.datatype.DatatypeFactory.newInstance().newDuration(1000 * 60 * minutes);
            //如果返回值为0，则表示延长成功，否则表示延长失败，具体失败原因请参考错误码列表。
            Integer resultCode = conferenceServiceEx.prolongScheduledConfEx(confId, date, prolongTime);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("extendtime", minutes);
            Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
        mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
        return busiSmc2ConferenceAppointment;
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
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
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
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
    public void stream(Smc2ConferenceContext mainConferenceContext, Boolean streaming, String streamUrl) {

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
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        List<AttendeeSmc2> attendees = new ArrayList<>();
        List<String> list = new ArrayList<String>();
        List<AttendeeSmc2> all = AttendeeSmc2Utils.getAll(conferenceContext);
        for (AttendeeSmc2 attendeeSmc2 : all) {
            if (!attendeeSmc2.isMeetingJoined()) {
                list.add(attendeeSmc2.getRemoteParty());
                attendees.add(attendeeSmc2);
            }
        }
        List<McuAttendeeSmc2> mcuAttendees = conferenceContext.getMcuAttendees();
        for (McuAttendeeSmc2 value : mcuAttendees) {
            AttendeeSmc2 attendeeById = conferenceContext.getAttendeeById(value.getId());
            if (attendeeById.getMeetingStatus() == AttendeeMeetingStatus.IN.getValue() && attendeeById.getOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                attendees.add(value);
                list.add(attendeeById.getRemoteParty());
            }
        }
        if (attendees.size() > 0) {
            InviteAttendeeSmc2Task inviteAttendeesTask = new InviteAttendeeSmc2Task(conferenceContext.getId(), 100, conferenceContext, attendees);
            Smc2delayTaskService.addTask(inviteAttendeesTask);
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
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.getSyncInformation() != null) {
            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始1！");
            return;
        }

        synchronized (conferenceContext.getSyncLock()) {
            if (conferenceContext.getSyncInformation() != null) {
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始2！");
                return;
            }

            SyncInformation syncInformation = new SyncInformation();
            conferenceContext.setSyncInformation(syncInformation);

            int totalCount = 0;
            syncInformation.setInProgress(true);
            syncInformation.setTotalCallCount(0);
            syncInformation.setReason("同步");

            LOGGER.info("One click synchronization start：" + conferenceContext.getConferenceNumber());

            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "开始同步本会议的参会信息！");
            Set<String> attendeeIds = new HashSet<>();
            Smc2ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (attendee) -> {
                attendeeIds.add(attendee.getId());
                if (attendee.isMeetingJoined()) {
                    synchronized (attendee) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeSmc2) {
                            TerminalAttendeeSmc2 terminalAttendee = (TerminalAttendeeSmc2) attendee;
                            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                            terminalAttendee.setTerminalType(bt.getType());
                            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                            if (attendee.isMeetingJoined()) {
                                terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            } else {
                                terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                            }
                        } else if (attendee instanceof InvitedAttendeeSmc2) {
                            InvitedAttendeeSmc2 invitedAttendee = (InvitedAttendeeSmc2) attendee;
                            if (invitedAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(invitedAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    invitedAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    invitedAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        } else if (attendee instanceof SelfCallAttendeeSmc2) {
                            SelfCallAttendeeSmc2 selfCallAttendee = (SelfCallAttendeeSmc2) attendee;
                            if (selfCallAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(selfCallAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    selfCallAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    selfCallAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        }
                        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                    }
                } else {
                    synchronized (attendee) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeSmc2) {
                            TerminalAttendeeSmc2 terminalAttendee = (TerminalAttendeeSmc2) attendee;
                            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                            terminalAttendee.setTerminalType(bt.getType());
                            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                            if (attendee.isMeetingJoined()) {
                                terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            } else {
                                terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                            }
                        } else if (attendee instanceof InvitedAttendeeSmc2) {
                            InvitedAttendeeSmc2 invitedAttendee = (InvitedAttendeeSmc2) attendee;
                            if (invitedAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(invitedAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    invitedAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    invitedAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        } else if (attendee instanceof SelfCallAttendeeSmc2) {
                            SelfCallAttendeeSmc2 selfCallAttendee = (SelfCallAttendeeSmc2) attendee;
                            if (selfCallAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(selfCallAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    selfCallAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    selfCallAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        }
                        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                    }
                }
            });

            totalCount = attendeeIds.size();

            syncInformation.setTotalCallCount(totalCount);
            syncInformation.setCurrentCallTotalParticipantCount(totalCount);
            syncInformation.setCurrentCallFmeIp(Smc2BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId()).getBusiSmc2().getIp());
            syncInformation.setSyncCurrentCallParticipantCount(totalCount);

            syncInformation.setInProgress(false);
            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.PARTICIPANT_SYNC, syncInformation);
            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "已同步完所有参会信息，共【" + totalCount + "】个！");
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
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);


        String confId = conferenceContext.getConference().getConfId();
        List<String> list = new ArrayList<String>();
        List<SmcParitipantsStateRep.ContentDTO> content = conferenceContext.getContent();
        if (!CollectionUtils.isEmpty(content)) {
            for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {
                if (contentDTO.getState().getOnline()) {
                    list.add(contentDTO.getGeneralParam().getUri());
                }
            }
        }
        //是否闭音。
        //0：不闭音
        //1：闭音
        int isMute = 0;
        if (mute) {
            isMute = 1;
        }
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer resultCode = conferenceServiceEx.setSitesMuteEx(confId, list, isMute);


    }

    private Integer subPicsSetting(MultiPicPollRequest multiPicPollRequest, Smc2ConferenceContext smc2ConferenceContext, Integer picNum, List<List<String>> subPics) {
        Integer interval;
        List<MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoList = multiPicPollRequest.getSubPicPollInfoList();

        MultiPicPollRequest.SubPicPollInfoListDTO msubPicPollInfoListDTOMax = subPicPollInfoList.stream().max(Comparator.comparingInt(dto -> dto.getParticipantIds().size())).get();
        interval = msubPicPollInfoListDTOMax.getInterval() == null ? 5 : msubPicPollInfoListDTOMax.getInterval();
        List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIdsMax = msubPicPollInfoListDTOMax.getParticipantIds();

        for (int i = 0; i < participantIdsMax.size(); i++) {
            List<String> sub = new ArrayList<>();

            for (Integer integer = 0; integer < picNum; integer++) {
                List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIds = subPicPollInfoList.get(integer).getParticipantIds();
                if (CollectionUtils.isEmpty(participantIds)) {
                    sub.add("");
                } else {
                    if (participantIds.size() == 1) {
                        MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantIdsDTO = participantIds.get(0);
                        subaddUri(smc2ConferenceContext, sub, participantIdsDTO);
                    } else if (participantIds.size() > i) {
                        MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantIdsDTO = participantIds.get(i);
                        subaddUri(smc2ConferenceContext, sub, participantIdsDTO);
                    } else if (participantIds.size() > 1 && participantIds.size() <= i) {
                        int size = i - participantIds.size();
                        if (size < participantIds.size()) {
                            MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantIdsDTO = participantIds.get(size);
                            subaddUri(smc2ConferenceContext, sub, participantIdsDTO);
                        } else {
                            int i1 = i % (participantIds.size());
                            MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantIdsDTO = participantIds.get(i1);
                            subaddUri(smc2ConferenceContext, sub, participantIdsDTO);
                        }

                    }

                }
            }
            subPics.add(sub);
        }
        return interval;
    }

    private void subaddUri(Smc2ConferenceContext smc2ConferenceContext, List<String> sub, MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantIdsDTO) {
        AttendeeSmc2 attendeeById = smc2ConferenceContext.getAttendeeById(participantIdsDTO.getParticipantId());
        if (attendeeById == null) {
            throw new CustomException("离线终端不能参与");
        }
        if (!attendeeById.isMeetingJoined()) {
            throw new CustomException("离线终端不能参与");
        }
        SmcParitipantsStateRep.ContentDTO m_participant = attendeeById.getSmcParticipant();
        Boolean online = m_participant.getState().getOnline();
        if (!online) {
            throw new CustomException("离线终端不能参与");
        }
        sub.add(m_participant.getGeneralParam().getUri());
    }

    @Override
    public void setMultiPicPoll(MultiPicPollRequest multiPicPollRequest) {
        Integer interval = multiPicPollRequest.getInterval();
        String contextKey = EncryptIdUtil.parasToContextKey(multiPicPollRequest.getConferenceId());
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            String confId = conferenceContext.getSmc2conferenceId();
            String target = "(%CP)";
            Integer picNum = multiPicPollRequest.getPicNum();
            Integer mode = multiPicPollRequest.getMode();
            int presenceMode = ContinuousPresenceModeEnum.getModelValue(picNum, mode);
            if (presenceMode == -1) {
                throw new CustomException("多画面设置失败:不支持该" + picNum + "画面");
            }
            List<List<String>> subPics = new ArrayList<>();

            if (conferenceContext.isUpCascadeConference() && multiPicPollRequest.getPicNum() == 1) {

                if (Objects.equals(multiPicPollRequest.getPollStatus(), PollOperateTypeDto.SET.name())) {
                    if (isCascadePolling(conferenceContext, multiPicPollRequest)) {
                        conferenceContext.setMultiPicPollRequest(multiPicPollRequest);
                    } else {
                        interval = subPicsSetting(multiPicPollRequest, conferenceContext, picNum, subPics);
                        ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
                        int result = conferenceServiceEx.setContinuousPresencePollingEx(confId, target, presenceMode, subPics, interval, -1);
                        if (result != 0) {
                            throw new CustomException("多画面轮询设置失败");
                        } else {
                            conferenceContext.setMultiPicPollRequest(multiPicPollRequest);
                        }
                    }
                }
                if (Objects.equals(multiPicPollRequest.getPollStatus(), PollOperateTypeDto.SET_AND_START.name())) {
                    multiPicPollRequest.setPollStatus(PollOperateTypeDto.SET_AND_START.name());
                    conferenceContext.setMultiPicPollRequest(multiPicPollRequest);
                    AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
                    conferenceContext.setLastAttendeeOperation(attendeeOperation);
                    attendeeOperation.cancel();
                    PollingAttendeeOperation pollingAttendeeOperation = new PollingAttendeeOperation(conferenceContext, JSONObject.parseObject(JSON.toJSONString(multiPicPollRequest)));
                    conferenceContext.setAttendeeOperation(pollingAttendeeOperation);
                    pollingAttendeeOperation.operate();
                }

            } else {
                if (Objects.equals(multiPicPollRequest.getPollStatus(), PollOperateTypeDto.SET.name())) {
                    interval = subPicsSetting(multiPicPollRequest, conferenceContext, picNum, subPics);
                    ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
                    int result = conferenceServiceEx.setContinuousPresencePollingEx(confId, target, presenceMode, subPics, interval, -1);
                    if (result != 0) {
                        throw new CustomException("多画面轮询设置失败");
                    } else {
                        conferenceContext.setMultiPicPollRequest(multiPicPollRequest);
                    }
                }
                if (Objects.equals(multiPicPollRequest.getPollStatus(), PollOperateTypeDto.SET_AND_START.name())) {

                    multiPicPollRequest.setPollStatus(PollOperateTypeDto.SET_AND_START.name());
                    conferenceContext.setMultiPicPollRequest(multiPicPollRequest);
                    AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
                    conferenceContext.setLastAttendeeOperation(attendeeOperation);
                    attendeeOperation.cancel();
                    PollingAttendeeOperation pollingAttendeeOperation = new PollingAttendeeOperation(conferenceContext, JSONObject.parseObject(JSON.toJSONString(multiPicPollRequest)));
                    conferenceContext.setAttendeeOperation(pollingAttendeeOperation);
                    pollingAttendeeOperation.operate();
                }
            }


        }
    }

    public boolean isCascadePolling(Smc2ConferenceContext conferenceContext, MultiPicPollRequest multiPicPollRequest) {
        List<MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoList = multiPicPollRequest.getSubPicPollInfoList();
        for (MultiPicPollRequest.SubPicPollInfoListDTO subPicPollInfoListDTO : subPicPollInfoList) {
            List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIds = subPicPollInfoListDTO.getParticipantIds();
            for (MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantId : participantIds) {
                AttendeeSmc2 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc2Id(participantId.getParticipantId());
                if (attendeeBySmc3Id == null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void stopMultiPicPoll(String conferenceId) {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceNumber);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (attendeeOperation instanceof PollingAttendeeOperation) {
                ((PollingAttendeeOperation) attendeeOperation).setPause(true);
            }
        }
    }

    @Override
    public void startMultiPicPoll(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {

            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            MultiPicPollRequest multiPicPollRequest = conferenceContext.getMultiPicPollRequest();
            if (multiPicPollRequest == null) {
                throw new CustomException("未设置多画面轮询");
            }
            multiPicPollRequest.setPollStatus(PollOperateTypeDto.START.name());

            if (attendeeOperation instanceof PollingAttendeeOperation) {
                PollingAttendeeOperation pollingAttendeeOperation = (PollingAttendeeOperation) attendeeOperation;
                if (pollingAttendeeOperation.isPause()) {
                    pollingAttendeeOperation.setPause(false);
                } else {
                    attendeeOperation.operate();
                }
            } else {
                conferenceContext.setLastAttendeeOperation(attendeeOperation);
                attendeeOperation.cancel();
                PollingAttendeeOperation pollingAttendeeOperation = new PollingAttendeeOperation(conferenceContext, JSONObject.parseObject(JSON.toJSONString(multiPicPollRequest)));
                conferenceContext.setAttendeeOperation(pollingAttendeeOperation);
                pollingAttendeeOperation.operate();
            }


        }
    }

    @Override
    public void cancelMultiPicPoll(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (attendeeOperation instanceof PollingAttendeeOperation) {
                attendeeOperation.cancel();
            }
        }
    }

    @Override
    public void setChairmanParticipantMultiPicPoll(MultiPicPollRequest multiPicPollRequest) {
        String contextKey = EncryptIdUtil.parasToContextKey(multiPicPollRequest.getConferenceId());
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        AttendeeSmc2 masterAttendee = conferenceContext.getMasterAttendee();
        if (masterAttendee == null) {
            throw new CustomException("请先设置主席");
        }
        if (multiPicPollRequest.getPicNum() == 1 && conferenceContext.isUpCascadeConference() && isCascadePolling(conferenceContext, multiPicPollRequest)) {
            conferenceContext.setChairmanMultiPicPollRequest(multiPicPollRequest);
        } else {
            SmcParitipantsStateRep.ContentDTO participant = masterAttendee.getSmcParticipant();
            String uri = participant.getGeneralParam().getUri();
            String target = uri + "(%CP)";
            Integer picNum = multiPicPollRequest.getPicNum();
            Integer mode = multiPicPollRequest.getMode();
            int presenceMode = ContinuousPresenceModeEnum.getModelValue(picNum, mode);
            if (presenceMode == -1) {
                throw new CustomException("多画面设置失败:不支持该" + picNum + "画面");
            }
            List<List<String>> subPics = new ArrayList<>();
            Integer interval = subPicsSetting(multiPicPollRequest, conferenceContext, picNum, subPics);
            ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
            int result = conferenceServiceEx.setContinuousPresencePollingEx(conferenceContext.getSmc2conferenceId(), target, presenceMode, subPics, interval, -1);
            if (result != 0) {
                throw new CustomException("主席轮询设置失败");
            }
            conferenceContext.setChairmanMultiPicPollRequest(multiPicPollRequest);
        }


    }

    @Override
    public MultiPicPollRequest chairmanParticipantMultiPicPollQuery(String conferenceId, String participantId) {


        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        MultiPicPollRequest chairmanMultiPicPollRequest = conferenceContext.getChairmanMultiPicPollRequest();

        return chairmanMultiPicPollRequest;
    }

    @Override
    public void chairmanParticipantMultiPicPollOperate(ChairmanPollOperateReq chairmanPollOperateReq) {
        String contextKey = EncryptIdUtil.parasToContextKey(chairmanPollOperateReq.getConferenceId());
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {

            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();

            if (attendeeOperation instanceof ChairmanPollingAttendeeOperation) {
                if (Objects.equals(chairmanPollOperateReq.getPollStatus().name(), PollOperateTypeDto.START.name())) {
                    if (Objects.equals(conferenceContext.getChairmanPollStatus().name(), PollOperateTypeDto.STOP.name())) {
                        ((ChairmanPollingAttendeeOperation) attendeeOperation).setPause(false);
                    } else {
                        conferenceContext.getChairmanMultiPicPollRequest().setPollStatus(PollOperateTypeDto.START.name());
                        attendeeOperation.operate();
                    }

                }
                if (Objects.equals(chairmanPollOperateReq.getPollStatus().name(), PollOperateTypeDto.STOP.name())) {
                    ((ChairmanPollingAttendeeOperation) attendeeOperation).setPause(true);
                }

                if (Objects.equals(chairmanPollOperateReq.getPollStatus().name(), PollOperateTypeDto.CANCEL.name())) {
                    attendeeOperation.cancel();

                    AttendeeOperation lastAttendeeOperation = conferenceContext.getLastAttendeeOperation();
                    if (lastAttendeeOperation != null) {
                        if (!(lastAttendeeOperation instanceof ChairmanPollingAttendeeOperation)) {
                            conferenceContext.setLastAttendeeOperation(attendeeOperation);
                            conferenceContext.setAttendeeOperation(lastAttendeeOperation);
                            lastAttendeeOperation.operate();
                        } else {
                            conferenceContext.defaultAttendeeOperation();
                        }
                    } else {
                        conferenceContext.defaultAttendeeOperation();
                    }

                }
            } else {
                if (Objects.equals(chairmanPollOperateReq.getPollStatus().name(), PollOperateTypeDto.START.name())) {
                    MultiPicPollRequest chairmanMultiPicPollRequest = conferenceContext.getChairmanMultiPicPollRequest();
                    if (chairmanMultiPicPollRequest == null) {
                        throw new CustomException("未设置主席轮询");
                    }
                    chairmanMultiPicPollRequest.setPollStatus(PollOperateTypeDto.START.name());
                    conferenceContext.setLastAttendeeOperation(attendeeOperation);
                    attendeeOperation.cancel();
                    ChairmanPollingAttendeeOperation chairmanPollingAttendeeOperation = new ChairmanPollingAttendeeOperation(conferenceContext);
                    conferenceContext.setAttendeeOperation(chairmanPollingAttendeeOperation);
                    chairmanPollingAttendeeOperation.operate();
                }
            }


        }
    }

    @Override
    public void createMultiPic(MultiPicInfoReq multiPicInfoReq) {
        String conferenceId = multiPicInfoReq.getConferenceId();
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceNumber);
        AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
        conferenceContext.setLastAttendeeOperation(attendeeOperation);
        attendeeOperation.cancel();
        DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
        defaultAttendeeOperation.operate();

    }

    @Override
    public void conferencesControlChoose(String conferenceId, String participantId, MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        String confId = smc2ConferenceContext.getSmc2conferenceId();
        AttendeeSmc2 attendeeSmc2 = smc2ConferenceContext.getAttendeeById(participantId);
        if (attendeeSmc2 == null) {
            throw new CustomException("未找到与会者");
        }
        ConferenceServiceEx conferenceServiceEx = smc2ConferenceContext.getSmc2Bridge().getConferenceServiceEx();
        String uri = attendeeSmc2.getRemoteParty();
        //锁定视频源
        List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams = new ArrayList<>();
        WSCtrlSiteCommParamEx item1 = new WSCtrlSiteCommParamEx();
        //锁定
        item1.setOperaTypeParam(1);
        item1.setSiteUri(uri);
        wsCtrlSiteCommParams.add(item1);
        conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams);


        String target = "(%CP)";
        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfoDTO.getSubPicList();
        Integer picNum = multiPicInfoDTO.getPicNum();
        Integer mode = multiPicInfoDTO.getMode();
        int presenceMode = ContinuousPresenceModeEnum.getModelValue(picNum, mode);
        if (presenceMode == -1) {
            throw new CustomException("多画面设置失败:不支持该" + picNum + "画面");
        }
        List<String> subPics = new ArrayList<>();
        if (!CollectionUtils.isEmpty(subPicList)) {
            for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                String m_participantId = subPicListDTO.getParticipantId();
                AttendeeSmc2 attendeeById = smc2ConferenceContext.getAttendeeById(m_participantId);

                if (attendeeById != null && attendeeById.isMeetingJoined()) {
                    subPics.add(attendeeById.getRemoteParty());
                }
            }

        }


        Integer resultCode = 0;
        if (!CollectionUtils.isEmpty(subPics) && subPics.size() == 1) {
            String videoSourceUri = subPics.get(0);
            resultCode = conferenceServiceEx.setVideoSourceEx(confId, uri, videoSourceUri, 0);
        } else if (CollectionUtils.isEmpty(subPics)) {

            //选看多画面实现方式二
            String chairmanPollStatus = smc2ConferenceContext.getChairmanPollStatus().name();
            String multiPicPollStatus = smc2ConferenceContext.getMultiPicPollStatus();
            Boolean multiPicBroadcastStatus = smc2ConferenceContext.getMultiPicBroadcastStatus();
            if (Objects.equals(chairmanPollStatus, "CANCEL") && Objects.equals(multiPicPollStatus, "CANCEL")) {
                //锁定全部会场
                //锁定视频源
                List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParamsAll = new ArrayList<>();

                List<SmcParitipantsStateRep.ContentDTO> content = smc2ConferenceContext.getContent();
                for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {

                    Boolean online = contentDTO.getState().getOnline();
                    if (online) {
                        WSCtrlSiteCommParamEx item = new WSCtrlSiteCommParamEx();
                        //锁定
                        item.setOperaTypeParam(1);
                        item.setSiteUri(contentDTO.getGeneralParam().getUri());
                        wsCtrlSiteCommParamsAll.add(item);
                    }
                }
                conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParamsAll);
                //解锁当前
                List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams2 = new ArrayList<>();
                WSCtrlSiteCommParamEx item2 = new WSCtrlSiteCommParamEx();
                item2.setOperaTypeParam(0);
                item2.setSiteUri(attendeeSmc2.getRemoteParty());
                wsCtrlSiteCommParams2.add(item2);
                conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams2);
                if (!multiPicBroadcastStatus) {
                    conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 0);
                    conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 1);
                }
                if (multiPicBroadcastStatus) {
                    conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 0);
                }
                //锁定当前
                List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams3 = new ArrayList<>();
                WSCtrlSiteCommParamEx item3 = new WSCtrlSiteCommParamEx();
                item3.setOperaTypeParam(1);
                item3.setSiteUri(attendeeSmc2.getRemoteParty());
                wsCtrlSiteCommParams3.add(item3);
                conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams3);
                //点名
                String spokesmanId = smc2ConferenceContext.getSpokesmanId();
                if (Strings.isNotBlank(spokesmanId)) {
                    SmcParitipantsStateRep.ContentDTO participantSp = smc2ConferenceContext.getParticipant(spokesmanId);
                    conferenceServiceEx.setBroadcastSiteEx(confId, participantSp.getGeneralParam().getUri(), 0);
                }
                //解锁所有
                List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParamsAll2 = new ArrayList<>();
                for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {

                    Boolean online = contentDTO.getState().getOnline();
                    if (online) {
                        WSCtrlSiteCommParamEx item = new WSCtrlSiteCommParamEx();
                        //锁定
                        item.setOperaTypeParam(0);
                        item.setSiteUri(contentDTO.getGeneralParam().getUri());
                        wsCtrlSiteCommParamsAll2.add(item);
                    }
                }
                conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParamsAll2);


            } else {
                throw new CustomException("会议正在轮询,不能选看多画面");
            }


        } else {
            resultCode = conferenceServiceEx.setContinuousPresenceEx(confId, target, presenceMode, subPics);
        }

        //解锁
        if (!("会议监控smc2").equals(attendeeSmc2.getName())) {
            item1.setOperaTypeParam(0);
            item1.setSiteUri(uri);
            wsCtrlSiteCommParams.add(item1);
            conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams);
        }
        if (resultCode != 0) {
            throw new CustomException("多画面选看失败:" + resultCode);
        }
    }

    @Override
    public Object queryMulitiPicPoll(String conferenceId) {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceNumber);
        if (conferenceContext == null) {
            throw new CustomException("会议不存在");
        }
        return conferenceContext.getMultiPicPollRequest();
    }

    @Override
    public void setBroadcastPoll(BroadcastPollRequest broadcastPollRequest) {
        String conferenceNumber = AesEnsUtils.getAesEncryptor().decryptHexToString(broadcastPollRequest.getConferenceId());
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceNumber);

    }

    @Override
    public void multiPicBroad(String conferenceId, boolean enable) {


        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);

        if (enable) {
            com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq.MultiPicInfoDTO multiPicInfo = conferenceContext.getMultiPicInfo();
            if (multiPicInfo == null) {
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "多画面未设置不能广播");
                throw new CustomException("未设置多画面");
            }
        }


        AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
        conferenceContext.setLastAttendeeOperation(attendeeOperation);
        attendeeOperation.cancel();
        MultiPicPollRequest multiPicPollRequest = new MultiPicPollRequest();
        multiPicPollRequest.setBroadcast(enable);
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(multiPicPollRequest));
        DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext, jsonObject);
        defaultAttendeeOperation.operate();

    }

    @Override
    public void voiceActive(String conferenceId, Boolean enable) {
        Integer isSwitch = enable == true ? 1 : 0;
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        String confId = smc2ConferenceContext.getSmc2conferenceId();
        ConferenceServiceEx conferenceServiceEx = smc2ConferenceContext.getSmc2Bridge().getConferenceServiceEx();
        Integer resultCode = conferenceServiceEx.setAudioSwitchEx(confId, 50, isSwitch);
        if (resultCode != 0) {
            throw new CustomException("声控切换错误" + resultCode);
        }
    }

    @Override
    public void changeQuiet(String conferenceId, boolean enable) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null || conferenceContext.isEnd()) {
            return;
        }
        String confId = conferenceContext.getSmc2conferenceId();
        List<String> list = new ArrayList<>();

        List<AttendeeSmc2> all = AttendeeSmc2Utils.getAll(conferenceContext);
        for (AttendeeSmc2 attendeeSmc2 : all) {
            if (attendeeSmc2.isMeetingJoined()) {
                list.add(attendeeSmc2.getRemoteParty());
            }
        }

        List<McuAttendeeSmc2> mcuAttendees = conferenceContext.getMcuAttendees();
        for (McuAttendeeSmc2 value : mcuAttendees) {
            AttendeeSmc2 attendeeById = conferenceContext.getAttendeeById(value.getId());
            if (attendeeById.getMeetingStatus() == AttendeeMeetingStatus.IN.getValue() && attendeeById.getOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                list.add(attendeeById.getRemoteParty());
            }
        }
        // 0：不静音 1：静音
        int isQuiet = 0;
        if (enable) {
            isQuiet = 1;
        }
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer resultCode = conferenceServiceEx.setSitesQuietEx(confId, list, isQuiet);
    }


    @Override
    public void chooseMultiPicManly(String conferenceId, String participantId, MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO2) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        ConferenceServiceEx conferenceServiceEx = smc2ConferenceContext.getSmc2Bridge().getConferenceServiceEx();
        String chairmanPollStatus = smc2ConferenceContext.getChairmanPollStatus().name();
        String multiPicPollStatus = smc2ConferenceContext.getMultiPicPollStatus();
        Boolean broadcastPollStatus = smc2ConferenceContext.getMultiPicBroadcastStatus();
        AttendeeSmc2 attendee = smc2ConferenceContext.getAttendeeById(participantId);
        if (attendee == null) {
            throw new CustomException("未找到与会者");
        }
        SmcParitipantsStateRep.ContentDTO participant = attendee.getSmcParticipant();
        com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq multiPicInfoReq1 = smc2ConferenceContext.getMultiPicInfoReq();
        String confId = smc2ConferenceContext.getSmc2conferenceId();

        if (Objects.equals(chairmanPollStatus, "START")) {
            //锁定主席
            AttendeeSmc2 attendeeSmc2 = smc2ConferenceContext.getMasterAttendee();
            String chairmanUri = attendeeSmc2.getRemoteParty();

            //锁定视频源
            List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams = new ArrayList<>();
            WSCtrlSiteCommParamEx item = new WSCtrlSiteCommParamEx();
            //锁定
            item.setOperaTypeParam(1);
            item.setSiteUri(chairmanUri);
            wsCtrlSiteCommParams.add(item);
            conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams);
        }


        if (Objects.equals(multiPicPollStatus, "START")) {
            GetContinuousPresenceParamExResponse continuousPresenceParamEx = conferenceServiceEx.getContinuousPresenceParamEx(confId, "(%CP)");
            List<String> subPics = continuousPresenceParamEx.getSubPics();
            for (String subPic : subPics) {
                //锁定视频源
                List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams = new ArrayList<>();
                WSCtrlSiteCommParamEx item = new WSCtrlSiteCommParamEx();
                //锁定
                item.setOperaTypeParam(1);
                item.setSiteUri(subPic);
                wsCtrlSiteCommParams.add(item);
                conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams);
            }
        }

        //锁定全部会场
        List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParamsAll = new ArrayList<>();

        List<AttendeeSmc2> all = AttendeeSmc2Utils.getAll(smc2ConferenceContext);
        for (AttendeeSmc2 attendeeSmc2 : all) {
            if (attendeeSmc2.isMeetingJoined()) {
                WSCtrlSiteCommParamEx item = new WSCtrlSiteCommParamEx();
                //锁定
                item.setOperaTypeParam(1);
                item.setSiteUri(attendeeSmc2.getRemoteParty());
                wsCtrlSiteCommParamsAll.add(item);
            }
        }
        conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParamsAll);
        // 解锁当前
        List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams2 = new ArrayList<>();
        WSCtrlSiteCommParamEx item2 = new WSCtrlSiteCommParamEx();
        item2.setOperaTypeParam(0);
        item2.setSiteUri(participant.getGeneralParam().getUri());
        wsCtrlSiteCommParams2.add(item2);
        conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams2);
        makeSubpic(multiPicInfoDTO2, smc2ConferenceContext, conferenceServiceEx, confId);


        if (broadcastPollStatus) {
            conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 0);
        } else {
            conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 0);
            conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 1);
        }


        //锁定当前
        List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams3 = new ArrayList<>();
        WSCtrlSiteCommParamEx item3 = new WSCtrlSiteCommParamEx();
        item3.setOperaTypeParam(1);
        item3.setSiteUri(participant.getGeneralParam().getUri());
        wsCtrlSiteCommParams3.add(item3);
        conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams3);
        //点名
        String spokesmanId = smc2ConferenceContext.getSpokesmanId();
        if (Strings.isNotBlank(spokesmanId)) {
            SmcParitipantsStateRep.ContentDTO participantSp = smc2ConferenceContext.getParticipant(spokesmanId);
            conferenceServiceEx.setBroadcastSiteEx(confId, participantSp.getGeneralParam().getUri(), 0);
        }

        if (multiPicInfoReq1 != null) {
            multiPicInfoReq1.setBroadcast(false);

            String jsonString = JSONObject.toJSONString(multiPicInfoReq1);

            createMulitiPic(JSONObject.parseObject(jsonString, MultiPicInfoReq.class));
        }
        //解锁所有
        List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParamsAll2 = new ArrayList<>();
        for (AttendeeSmc2 attendeeSmc2 : all) {

            //解锁所有
            //&& !Objects.equals(uri1, contentDTO.getGeneralParam().getUri())
            if (attendeeSmc2.isMeetingJoined() && !Objects.equals(SMC_2, attendeeSmc2.getName())) {
                WSCtrlSiteCommParamEx item = new WSCtrlSiteCommParamEx();
                //解锁所有
                item.setOperaTypeParam(0);
                item.setSiteUri(attendeeSmc2.getRemoteParty());
                wsCtrlSiteCommParamsAll2.add(item);
            }
        }
        conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParamsAll2);
        //查询自身锁定状态

        Integer videoSwitchAttribute = attendee.getVideoSwitchAttribute();
        //锁定
        if (Objects.equals(1, videoSwitchAttribute)) {
            List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams4 = new ArrayList<>();
            WSCtrlSiteCommParamEx item4 = new WSCtrlSiteCommParamEx();
            item4.setOperaTypeParam(1);
            item4.setSiteUri(participant.getGeneralParam().getUri());
            wsCtrlSiteCommParams4.add(item4);
            conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams4);
        }


    }


    @Override
    public void lockPresenter(String conferenceId, Boolean lock) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);


        String confId = conferenceContext.getSmc2conferenceId();


        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer resultCode = 0;
        if (lock) {
            resultCode = conferenceServiceEx.lockPresentationEx(confId,null);
        } else {
            resultCode = conferenceServiceEx.unlockPresentationEx(confId);
        }
        if (resultCode != 0) {
            LOGGER.error("会议："+conferenceContext.getName()+""+confId+" 锁定/取消会议材料"+resultCode);
        }

    }

    private void makeSubpic(MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO, Smc2ConferenceContext smc2ConferenceContext, ConferenceServiceEx conferenceServiceEx, String confId) {
        String target = "(%CP0)";
        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfoDTO.getSubPicList();
        Integer picNum = multiPicInfoDTO.getPicNum();
        Integer mode = multiPicInfoDTO.getMode();
        int presenceMode = ContinuousPresenceModeEnum.getModelValue(picNum, mode);
        if (presenceMode == -1) {
            throw new CustomException("多画面设置失败:不支持该" + picNum + "画面");
        }
        List<String> subPics = new ArrayList<>();
        if (!CollectionUtils.isEmpty(subPicList)) {
            for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                String m_participantId = subPicListDTO.getParticipantId();
                AttendeeSmc2 attendee = smc2ConferenceContext.getAttendeeById(m_participantId);
                if (attendee != null) {
                    subPics.add(attendee.getRemoteParty());
                } else {
                    subPics.add("");
                }
            }

        }

        conferenceServiceEx.setContinuousPresenceEx(confId, target, presenceMode, subPics);
    }

    public synchronized void createMulitiPic(MultiPicInfoReq multiPicInfoReq) {
        String conferenceId = multiPicInfoReq.getConferenceId();
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(contextKey);
        String confId = smc2ConferenceContext.getSmc2conferenceId();
        MultiPicInfoReq.MultiPicInfoDTO multiPicInfo = multiPicInfoReq.getMultiPicInfo();
        String target = "(%CP)";
        Integer picNum = multiPicInfo.getPicNum();
        Integer mode = multiPicInfo.getMode();
        int presenceMode = ContinuousPresenceModeEnum.getModelValue(picNum, mode);
        if (presenceMode == -1) {
            throw new CustomException("多画面设置失败:不支持该" + picNum + "画面");
        }
        List<String> subPics = new ArrayList<>();
        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfoReq.getMultiPicInfo().getSubPicList();
        if (!CollectionUtils.isEmpty(subPicList)) {
            for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                String participantId = subPicListDTO.getParticipantId();
                AttendeeSmc2 attendee = smc2ConferenceContext.getAttendeeById(participantId);
                if (attendee != null) {
                    subPics.add(attendee.getRemoteParty());
                } else {
                    subPics.add("");
                }
            }
        }
        ConferenceServiceEx conferenceServiceEx = smc2ConferenceContext.getSmc2Bridge().getConferenceServiceEx();
        Integer resultCode = conferenceServiceEx.setContinuousPresenceEx(confId, target, presenceMode, subPics);
        if (resultCode != 0) {
            throw new CustomException("多画面设置失败:" + resultCode);
        } else {
            String jsonString = JSONObject.toJSONString(multiPicInfoReq);
            com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq multiPicInfoReq1 = JSONObject.parseObject(jsonString, com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq.class);
            smc2ConferenceContext.setMultiPicInfoReq(multiPicInfoReq1);
            MultiPicInfoReq.MultiPicInfoDTO source = multiPicInfoReq.getMultiPicInfo();
            DetailConference detailConference = smc2ConferenceContext.getDetailConference();
            ConferenceState conferenceState = detailConference.getConferenceState();
            ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfoTarget = new ChooseMultiPicInfo.MultiPicInfoDTO();
            multiPicInfoTarget.setPicNum(source.getPicNum());
            multiPicInfoTarget.setMode(source.getMode());
            List<ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO> targetSubPicList = source.getSubPicList().stream().map(t -> {
                ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO picListDTO = new ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO();
                BeanUtils.copyProperties(t, picListDTO);
                return picListDTO;
            }).collect(Collectors.toList());
            multiPicInfoTarget.setSubPicList(targetSubPicList);
            conferenceState.setMultiPicInfo(multiPicInfoTarget);

            if (multiPicInfoReq.getBroadcast()) {
                multiPicBroad(conferenceId, true);
            }
        }
    }

}
