package com.paradisecloud.smc3.service.impls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceNumberMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3ConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc3TemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.enumer.FmeBridgeProcessingStrategy;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.terminal.fs.cache.LiveDeptCache;
import com.paradisecloud.smc3.busi.DefaultAttendeeOperation;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.SyncInformation;
import com.paradisecloud.smc3.busi.attende.*;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.layout.LayoutTemplates;
import com.paradisecloud.smc3.busi.operation.AttendeeOperation;
import com.paradisecloud.smc3.busi.operation.ChairmanPollingAttendeeOperation;
import com.paradisecloud.smc3.busi.operation.DiscussAttendeeOperation;
import com.paradisecloud.smc3.busi.operation.PollingAttendeeOperation;
import com.paradisecloud.smc3.busi.templateconference.BuildTemplateConferenceContext;
import com.paradisecloud.smc3.busi.templateconference.StartTemplateConference;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextCache;
import com.paradisecloud.smc3.busi.utils.Smc3ConferenceContextUtils;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.invoker.ConferenceState;
import com.paradisecloud.smc3.model.PollOperateTypeDto;
import com.paradisecloud.smc3.model.*;
import com.paradisecloud.smc3.model.request.*;
import com.paradisecloud.smc3.model.response.GetVmrResponse;
import com.paradisecloud.smc3.model.response.LogsConferenceRep;
import com.paradisecloud.smc3.service.interfaces.*;
import com.paradisecloud.smc3.task.InviteAttendeeSmc3Task;
import com.paradisecloud.smc3.task.Smc3DelayTaskService;
import com.paradisecloud.smc3.task.Smc3DeleteConferenceTemplateTask;
import com.sinhy.enumer.DateTimeFormatPattern;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.apache.http.NameValuePair;
import org.apache.http.util.Asserts;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 会议相关服务类
 */
@Service
public class BusiSmc3ConferenceServiceImpl implements IBusiSmc3ConferenceService {
    public static final String MAX_PARTICIPANT_NUM = "maxParticipantNum";
    private static final Logger LOGGER = LoggerFactory.getLogger(BusiSmc3ConferenceServiceImpl.class);
    @Resource
    private BusiMcuSmc3TemplateConferenceMapper busiSmc3TemplateConferenceMapper;
    @Resource
    private BusiMcuSmc3ConferenceAppointmentMapper busiSmc3ConferenceAppointmentMapper;
    @Resource
    private IBusiMcuSmc3ConferenceAppointmentService busiSmc3ConferenceAppointmentService;
    @Resource
    private IBusiMcuSmc3TemplateConferenceService busiSmc3TemplateConferenceService;
    @Resource
    private IBusiMcuSmc3HistoryConferenceService busiSmc3HistoryConferenceService;
    @Resource
    private IAttendeeSmc3Service attendeeSmc3Service;
    @Resource
    private Smc3DelayTaskService Smc3delayTaskService;
    @Resource
    private IMqttService mqttService;
    @Resource
    private IBusiConferenceNumberSmc3Service busiConferenceNumberSmc3Service;


    /**
     * 开始模板会议
     *
     * @param templateConferenceId void
     * @return
     */
    @Override
    public synchronized String startTemplateConference(long templateConferenceId) {
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
    public Smc3ConferenceContext buildTemplateConferenceContext(long templateConferenceId) {
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
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            conferenceContext.setEndReasonsType(EndReasonsType.ADMINISTRATOR_HANGS_UP);
            endConference(encryptConferenceId, endType, true, false);
        }
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
    public void endConference(String encryptConferenceId, int endType, boolean forceEnd, boolean pushMessage) {
        // 会议结束类型
        String contextKey = EncryptIdUtil.parasToContextKey(encryptConferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        conferenceContext.setEndReasonsType(endType);
        if (conferenceContext != null) {
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                if (Strings.isNotBlank(conferenceContext.getParentConferenceId())) {
                    throw new CustomException("多级会议请先结束主会议");
                }
            }
            BusiMcuSmc3TemplateConference busiSmc3TemplateConferenceExist = busiSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(conferenceContext.getTemplateConferenceId());
            if (forceEnd) {
                attendeeSmc3Service.setMessageBannerText(contextKey, "");
                Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
                bridge.getSmcConferencesInvoker().endConferences(conferenceContext.getSmc3conferenceId(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }

            conferenceContext.setEnd(true);
            conferenceContext.setEndTime(new Date());
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (attendeeOperation != null) {
                attendeeOperation.cancel();
            }

            BusiMcuSmc3ConferenceAppointment busiSmc3ConferenceAppointment = conferenceContext.getConferenceAppointment();
            if (busiSmc3ConferenceAppointment != null) {
                busiSmc3ConferenceAppointment.setIsHangUp(YesOrNo.YES.getValue());
                busiSmc3ConferenceAppointment.setIsStart(null);
                busiSmc3ConferenceAppointment.setExtendMinutes(null);
                busiSmc3ConferenceAppointmentMapper.updateBusiMcuSmc3ConferenceAppointment(busiSmc3ConferenceAppointment);

                AppointmentConferenceRepeatRate appointmentConferenceRepeatRate = AppointmentConferenceRepeatRate.convert(busiSmc3ConferenceAppointment.getRepeatRate());
                if (appointmentConferenceRepeatRate == AppointmentConferenceRepeatRate.CUSTOM) {
                    busiSmc3ConferenceAppointmentService.deleteBusiMcuSmc3ConferenceAppointmentById(busiSmc3ConferenceAppointment.getId());
                }
            }
            BusiHistoryConference busiHistoryConference = conferenceContext.getHistoryConference();
            if (busiHistoryConference != null) {
                busiHistoryConference.setConferenceEndTime(new Date());
                busiHistoryConference.setEndReasonsType(conferenceContext.getEndReasonsType());
                conferenceContext.setEndTime(new Date());
                busiHistoryConference.setEndReasonsType(conferenceContext.getEndReasonsType());
                busiSmc3HistoryConferenceService.saveHistory(busiHistoryConference, conferenceContext);

            }
            BusiMcuSmc3TemplateConference busiSmc3TemplateConference = busiSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(conferenceContext.getTemplateConferenceId());
            if (busiSmc3TemplateConference != null) {
                busiSmc3TemplateConference.setUpdateTime(new Date());
                if (busiSmc3TemplateConference.getIsAutoCreateConferenceNumber() == YesOrNo.YES.getValue()) {
                    busiSmc3TemplateConference.setConferenceNumber(null);
                    busiSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiSmc3TemplateConference);
                    long conferenceNumberId = Long.parseLong(conferenceContext.getConferenceNumber());
                    BusiConferenceNumber busiConferenceNumberExist = busiConferenceNumberSmc3Service.selectBusiConferenceNumberById(conferenceNumberId);
                    if (busiConferenceNumberExist != null) {
                        busiConferenceNumberSmc3Service.deleteBusiConferenceNumberById(conferenceNumberId);
                    }
                }
                busiSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiSmc3TemplateConference);

            }
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                try {
                    List<TemplateNode> cascadeTree = conferenceContext.getCascadeTree();
                    Long cascadeLocalTemplateId = conferenceContext.getCascadeLocalTemplateId();
                    String contextKey_MAIN = EncryptIdUtil.generateContextKey(cascadeLocalTemplateId, McuType.SMC3);
                    Smc3ConferenceContextCache.getInstance().remove(contextKey_MAIN);
                    String mainConferenceId = EncryptIdUtil.generateEncryptId(contextKey_MAIN);
                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceId, WebsocketMessageType.MESSAGE_TIP, "会议已结束");
                    Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceId, WebsocketMessageType.CONFERENCE_ENDED, "会议已结束");
                    for (TemplateNode templateNode : cascadeTree) {
                        if (Strings.isNotBlank(templateNode.getParentTemplateId())) {
                            String templateId = templateNode.getTemplateId();

                            Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(templateId);
                            if(smc3ConferenceContext != null){
                                String contextKey1 = smc3ConferenceContext.getContextKey();
                                Smc3ConferenceContext remove = Smc3ConferenceContextCache.getInstance().remove(contextKey1);
                                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(remove, WebsocketMessageType.CONFERENCE_ENDED, "会议已结束");
                                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(remove, WebsocketMessageType.MESSAGE_TIP, "会议已结束");
                            }
                        }

                    }
                } catch (Exception e) {
                  LOGGER.info(e.getMessage());
                }
            }
            Smc3ConferenceContextCache.getInstance().remove(conferenceContext.getContextKey());
            Smc3ConferenceContextCache.getInstance().remove(conferenceContext.getSmc3conferenceId());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            if (StringUtils.isNotEmpty(busiSmc3TemplateConferenceExist.getCascadeId())) {
                if (Objects.equals(ConstAPI.CASCADE, busiSmc3TemplateConferenceExist.getCategory())) {
                    deleteSmcTemplate(conferenceContext, busiSmc3TemplateConferenceExist);
                } else {
                    BusiMcuSmc3TemplateConference busiSmc3TemplateConferenceTop = busiSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(Long.valueOf(busiSmc3TemplateConferenceExist.getCascadeId()));
                    if (busiSmc3TemplateConferenceTop != null) {
                        if (Objects.equals(ConstAPI.CASCADE, busiSmc3TemplateConferenceTop.getCategory())) {
                            Smc3ConferenceContext conferenceContextTop = buildTemplateConferenceContext(busiSmc3TemplateConferenceTop.getId());
                            deleteSmcTemplate(conferenceContextTop, busiSmc3TemplateConferenceTop);
                        }
                    }
                }
            } else {
                deleteSmcTemplate(conferenceContext, busiSmc3TemplateConferenceExist);
            }

            pushEndMessageToMqtt(conferenceContext.getTenantId() + conferenceContext.getConferenceNumber(), conferenceContext);
            if( conferenceContext.getCospaceId()!=null){
                deleteCospace(BridgeUtils.getAvailableFmeBridge(conferenceContext.getDeptId()), conferenceContext.getCospaceId());
            }
            conferenceContext.clear();
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_ENDED, "会议已结束");
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已结束");

            Smc3DeleteConferenceTemplateTask smc3DeleteConferenceTemplateTask = new Smc3DeleteConferenceTemplateTask("1", 5000);
            Smc3delayTaskService.addTask(smc3DeleteConferenceTemplateTask);
        }


    }

    private void deleteCospace(FmeBridge fmeBridge, String cospaceId) {
        if (Strings.isNotBlank(cospaceId)) {
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
    }

    private void deleteSmcTemplate(Smc3ConferenceContext conferenceContext, BusiMcuSmc3TemplateConference busiTemplateConference) {
        BusiMcuSmc3TemplateConferenceMapper busiMcuSmc3TemplateConferenceMapper = BeanFactory.getBean(BusiMcuSmc3TemplateConferenceMapper.class);
        BusiConferenceNumberMapper busiConferenceNumberMapper = BeanFactory.getBean(BusiConferenceNumberMapper.class);
        IBusiConferenceNumberService busiConferenceNumberService = BeanFactory.getBean(IBusiConferenceNumberService.class);
        if (busiTemplateConference != null) {
            if (busiTemplateConference.getConferenceNumber() != null) {
                BusiMcuSmc3TemplateConference con = new BusiMcuSmc3TemplateConference();
                con.setConferenceNumber(busiTemplateConference.getConferenceNumber());
                List<BusiMcuSmc3TemplateConference> cs = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceList(con);
                if (ObjectUtils.isEmpty(cs)) {
                    // 修改号码状态为闲置
                    BusiConferenceNumber cn = new BusiConferenceNumber();
                    cn.setId(busiTemplateConference.getConferenceNumber());
                    cn.setStatus(ConferenceNumberStatus.IDLE.getValue());
                    busiConferenceNumberMapper.updateBusiConferenceNumber(cn);
                }

                // 若是自动创建的会议号，则删除模板的时候同步进行删除
                BusiConferenceNumber bcn = busiConferenceNumberService.selectBusiConferenceNumberById(busiTemplateConference.getConferenceNumber());
                if (bcn != null) {
                    if (ConferenceNumberCreateType.convert(bcn.getCreateType()) == ConferenceNumberCreateType.AUTO) {
                        busiConferenceNumberService.deleteBusiConferenceNumberById(busiTemplateConference.getConferenceNumber());
                    }
                }

            }

            Smc3Bridge smc3Bridge = Smc3BridgeCache.getInstance().getBridgesByDept(busiTemplateConference.getDeptId());
            if (Objects.equals(ConstAPI.CASCADE, busiTemplateConference.getCategory())) {
                if (StringUtils.isNotEmpty(busiTemplateConference.getCascadeId())) {
                    smc3Bridge.getSmcConferencesTemplateInvoker().deleteCascadeTemplate(busiTemplateConference.getCascadeId(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }
            }
            if (StringUtils.isNotEmpty(busiTemplateConference.getSmcTemplateId())) {
                smc3Bridge.getSmcConferencesTemplateInvoker().deleteConferencesTemplate(busiTemplateConference.getSmcTemplateId(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }

            if (Objects.equals(ConstAPI.CASCADE, busiTemplateConference.getCategory())) {
                List<TemplateNodeTemp> nodesTemplateTemp_local = JSON.parseArray(busiTemplateConference.getCascadeNodesTemp(), TemplateNodeTemp.class);
                for (TemplateNodeTemp templateNodeTemp : nodesTemplateTemp_local) {

                    if(templateNodeTemp.getTemplateId() != null){
                        BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConferenceTemp = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateNodeTemp.getTemplateId());
                        Smc3ConferenceContext conferenceContextTemp = buildTemplateConferenceContext(templateNodeTemp.getTemplateId());
                        if (conferenceContextTemp != null) {
                            deleteSmcTemplate(conferenceContextTemp, busiMcuSmc3TemplateConferenceTemp);
                        }
                    }
                }
            }
            BusiMcuSmc3TemplateConference busiMcuSmc3TemplateConferenceUpdate = busiMcuSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(busiTemplateConference.getId());
            if (busiMcuSmc3TemplateConferenceUpdate != null) {
                busiMcuSmc3TemplateConferenceUpdate.setCascadeNodes(null);
                busiMcuSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(busiMcuSmc3TemplateConferenceUpdate);
            }
            if (true) {
                try {
                    String responseStr = smc3Bridge.getSmcportalTokenInvoker().getVmr(conferenceContext.getConferenceNumber(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    GetVmrResponse getVmrResponse = JSON.parseObject(responseStr, GetVmrResponse.class);
                    if (StringUtils.isNotEmpty(getVmrResponse.getId())) {
                        responseStr = smc3Bridge.getSmcportalTokenInvoker().deleteVmr(getVmrResponse.getId(), smc3Bridge.getSmcportalTokenInvoker().getSystemHeaders());
                        if (responseStr != null) {
                            LOGGER.error("删除SMC虚拟会议室号码失败，请重试或者联系管理员！");
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("删除虚拟会议室失败!");
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
    private void pushEndMessageToMqtt(String conferenceNumber, Smc3ConferenceContext conferenceContext) {
        List<BaseAttendee> mqttJoinTerminals = new ArrayList<>();
        Smc3ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeSmc3) {
                TerminalAttendeeSmc3 ta = (TerminalAttendeeSmc3) a;
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
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (!(attendeeOperation instanceof DiscussAttendeeOperation)) {
                conferenceContext.setLastAttendeeOperation(attendeeOperation);
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
     * @since 2021-04-27 16:23
     */
    @Override
    public void lock(String conferenceId, Boolean locked) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            // 锁定开锁会议
            Smc3Bridge smc3Bridge = conferenceContext.getSmc3Bridge();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("isLock", locked);
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                smc3Bridge.getSmcConferencesInvoker().conferencesControlCascade(conferenceContext.getSmc3conferenceId(), jsonObject.toJSONString(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                smc3Bridge.getSmcConferencesInvoker().conferencesControl(conferenceContext.getSmc3conferenceId(), jsonObject.toJSONString(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

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
    public boolean updateCallRecordStatus(String conferenceNumber, Boolean record) {
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(conferenceNumber);
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
    public BusiMcuSmc3ConferenceAppointment extendMinutes(String conferenceId, int minutes) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            throw new CustomException("会议不存在或者未开始");
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("extendTime", minutes);
            Smc3Bridge smc3Bridge = conferenceContext.getSmc3Bridge();

            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                smc3Bridge.getSmcConferencesInvoker().conferencesExTendTimeCascade(conferenceContext.getSmc3conferenceId(), jsonObject.toJSONString(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                smc3Bridge.getSmcConferencesInvoker().conferencesExTendTime(conferenceContext.getSmc3conferenceId(), jsonObject.toJSONString(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        BusiMcuSmc3ConferenceAppointment busiSmc3ConferenceAppointment = conferenceContext.getConferenceAppointment();
        if (busiSmc3ConferenceAppointment != null) {
            busiSmc3ConferenceAppointment = busiSmc3ConferenceAppointmentMapper.selectBusiMcuSmc3ConferenceAppointmentById(busiSmc3ConferenceAppointment.getId());
            if (busiSmc3ConferenceAppointment != null) {
                Integer extendMinutesNew = busiSmc3ConferenceAppointment.getExtendMinutes() != null ? (busiSmc3ConferenceAppointment.getExtendMinutes() + minutes) : minutes;
                if (extendMinutesNew > 1440) {
                    throw new CustomException("会议总延时最多24小时");
                }
                busiSmc3ConferenceAppointment.setExtendMinutes(extendMinutesNew);
                AppointmentConferenceRepeatRate rr = AppointmentConferenceRepeatRate.convert(busiSmc3ConferenceAppointment.getRepeatRate());
                Date end = null;
                if (rr == AppointmentConferenceRepeatRate.CUSTOM) {
                    end = DateUtils.convertToDate(busiSmc3ConferenceAppointment.getEndTime());
                } else {
                    String today = DateUtils.formatTo(DateTimeFormatPattern.PATTERN_13.getPattern());
                    end = DateUtils.convertToDate(today + " " + busiSmc3ConferenceAppointment.getEndTime());
                }

                if (busiSmc3ConferenceAppointment.getExtendMinutes() != null) {
                    end = DateUtils.getDiffDate(end, busiSmc3ConferenceAppointment.getExtendMinutes(), TimeUnit.MINUTES);
                }

                BusiMcuSmc3ConferenceAppointment con = new BusiMcuSmc3ConferenceAppointment();
                con.setTemplateId(busiSmc3ConferenceAppointment.getTemplateId());
                List<BusiMcuSmc3ConferenceAppointment> cas = busiSmc3ConferenceAppointmentMapper.selectBusiMcuSmc3ConferenceAppointmentList(con);
                if (!ObjectUtils.isEmpty(cas)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String endTime = sdf.format(end);
                    for (BusiMcuSmc3ConferenceAppointment busiConferenceAppointmentTemp : cas) {
                        if (busiConferenceAppointmentTemp.getId().longValue() != busiSmc3ConferenceAppointment.getId().longValue()) {
                            if (endTime.compareTo(busiConferenceAppointmentTemp.getStartTime()) >= 0 && endTime.compareTo(busiConferenceAppointmentTemp.getEndTime()) <= 0) {
                                throw new SystemException(1008435, "延长会议结束时间失败：延长后的结束时间已存在相同模板的预约会议！");
                            }
                        }
                    }
                }

                busiSmc3ConferenceAppointmentMapper.updateBusiMcuSmc3ConferenceAppointment(busiSmc3ConferenceAppointment);
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议结束时间延长至：" + DateUtils.formatTo(DateTimeFormatPattern.PATTERN_11, end));
            }
        } else {
            Long templateConferenceId = conferenceContext.getTemplateConferenceId();
            BusiMcuSmc3TemplateConference tc = busiSmc3TemplateConferenceMapper.selectBusiMcuSmc3TemplateConferenceById(templateConferenceId);
            Integer durationTimeNew = tc.getDurationTime() + minutes;
            if (durationTimeNew > 2880) {
                throw new CustomException("会议总延时最多24小时");
            }
            tc.setDurationTime(durationTimeNew);
            busiSmc3TemplateConferenceMapper.updateBusiMcuSmc3TemplateConference(tc);
            conferenceContext.setDurationTime(durationTimeNew);
        }
        mqttService.sendConferenceInfoToPushTargetTerminal(conferenceContext);
        return busiSmc3ConferenceAppointment;
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
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (attendeeOperation instanceof DiscussAttendeeOperation) {
                AttendeeOperation lastAttendeeOperation = conferenceContext.getLastAttendeeOperation();
                if (lastAttendeeOperation == null) {
                    lastAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
                }
                conferenceContext.setAttendeeOperation(lastAttendeeOperation);
                attendeeOperation.cancel();
                if (lastAttendeeOperation instanceof PollingAttendeeOperation || lastAttendeeOperation instanceof ChairmanPollingAttendeeOperation) {
                    return;
                }
                lastAttendeeOperation.operate();
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
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
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
     * @since 2021-08-17 16:39
     */
    @Override
    public void stream(Smc3ConferenceContext mainConferenceContext, Boolean streaming, String streamUrl) {

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
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        Map<Long, TerminalAttendeeSmc3> terminalAttendeeMap = conferenceContext.getTerminalAttendeeMap();
        List<AttendeeSmc3> attendees = new ArrayList<>();
        for (TerminalAttendeeSmc3 value : terminalAttendeeMap.values()) {
            if (value.getAttendType() == 1 && !TerminalType.isFCMSIP(value.getTerminalType())) {
                AttendeeSmc3 attendeeById = conferenceContext.getAttendeeById(value.getId());
                if (attendeeById.getMeetingStatus() == AttendeeMeetingStatus.OUT.getValue() && attendeeById.getOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                    attendees.add(value);
                }
            }
        }
        List<McuAttendeeSmc3> mcuAttendees = conferenceContext.getMcuAttendees();
        for (McuAttendeeSmc3 value : mcuAttendees) {
            AttendeeSmc3 attendeeById = conferenceContext.getAttendeeById(value.getId());
            if (attendeeById.getMeetingStatus() == AttendeeMeetingStatus.OUT.getValue() && attendeeById.getOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                attendees.add(value);
            }
        }
        if (attendees.size() > 0) {
            InviteAttendeeSmc3Task inviteAttendeesTask = new InviteAttendeeSmc3Task(conferenceContext.getConferenceNumber(), 100, conferenceContext, attendees);
            Smc3delayTaskService.addTask(inviteAttendeesTask);
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
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext.getSyncInformation() != null) {
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始1！");
            return;
        }

        synchronized (conferenceContext.getSyncLock()) {
            if (conferenceContext.getSyncInformation() != null) {
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【一键同步】一开始，请勿重复开始2！");
                return;
            }

            SyncInformation syncInformation = new SyncInformation();
            conferenceContext.setSyncInformation(syncInformation);

            int totalCount = 0;
            syncInformation.setInProgress(true);
            syncInformation.setTotalCallCount(0);
            syncInformation.setReason("同步");

            LOGGER.info("One click synchronization start：" + conferenceContext.getConferenceNumber());

            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "开始同步本会议的参会信息！");
            Set<String> attendeeIds = new HashSet<>();
            Smc3ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (attendee) -> {
                attendeeIds.add(attendee.getId());
                if (attendee.isMeetingJoined()) {
                    synchronized (attendee) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeSmc3) {
                            TerminalAttendeeSmc3 terminalAttendee = (TerminalAttendeeSmc3) attendee;
                            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                            terminalAttendee.setTerminalType(bt.getType());
                            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                            if (attendee.isMeetingJoined()) {
                                terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            } else {
                                terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                            }
                        } else if (attendee instanceof InvitedAttendeeSmc3) {
                            InvitedAttendeeSmc3 invitedAttendee = (InvitedAttendeeSmc3) attendee;
                            if (invitedAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(invitedAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    invitedAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    invitedAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        } else if (attendee instanceof SelfCallAttendeeSmc3) {
                            SelfCallAttendeeSmc3 selfCallAttendee = (SelfCallAttendeeSmc3) attendee;
                            if (selfCallAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(selfCallAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    selfCallAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    selfCallAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        }
                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                    }
                } else {
                    synchronized (attendee) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeSmc3) {
                            TerminalAttendeeSmc3 terminalAttendee = (TerminalAttendeeSmc3) attendee;
                            BusiTerminal bt = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                            terminalAttendee.setTerminalType(bt.getType());
                            terminalAttendee.setTerminalTypeName(TerminalType.convert(bt.getType()).getDisplayName());
                            if (attendee.isMeetingJoined()) {
                                terminalAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            } else {
                                terminalAttendee.setOnlineStatus(bt.getOnlineStatus());
                            }
                        } else if (attendee instanceof InvitedAttendeeSmc3) {
                            InvitedAttendeeSmc3 invitedAttendee = (InvitedAttendeeSmc3) attendee;
                            if (invitedAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(invitedAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    invitedAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    invitedAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        } else if (attendee instanceof SelfCallAttendeeSmc3) {
                            SelfCallAttendeeSmc3 selfCallAttendee = (SelfCallAttendeeSmc3) attendee;
                            if (selfCallAttendee.getTerminalId() != null) {
                                BusiTerminal bt = TerminalCache.getInstance().get(selfCallAttendee.getTerminalId());
                                if (attendee.isMeetingJoined()) {
                                    selfCallAttendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                } else {
                                    selfCallAttendee.setOnlineStatus(bt.getOnlineStatus());
                                }
                            }
                        }
                        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                    }
                }
            });

            totalCount = attendeeIds.size();

            syncInformation.setTotalCallCount(totalCount);
            syncInformation.setCurrentCallTotalParticipantCount(totalCount);
            syncInformation.setCurrentCallFmeIp(Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId()).getBusiSMC().getIp());
            syncInformation.setSyncCurrentCallParticipantCount(totalCount);

            syncInformation.setInProgress(false);
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.PARTICIPANT_SYNC, syncInformation);
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "已同步完所有参会信息，共【" + totalCount + "】个！");
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
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        Smc3Bridge smc3Bridge = conferenceContext.getSmc3Bridge();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isMute", mute);
        if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
            smc3Bridge.getSmcConferencesInvoker().conferencesControlCascade(conferenceId, jsonObject.toJSONString(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

        } else {
            smc3Bridge.getSmcConferencesInvoker().conferencesControl(conferenceId, jsonObject.toJSONString(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

        }

    }

    @Override
    public void setMultiPicPoll(MultiPicPollRequest multiPicPollRequest) {
        String contextKey = EncryptIdUtil.parasToContextKey(multiPicPollRequest.getConferenceId());
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        if (conferenceContext.isUpCascadeConference() && multiPicPollRequest.getPicNum() == 1) {

            MultiPicPollRequest multiPicPollRequest_new = parseMutilPic(multiPicPollRequest, conferenceContext);

            if (Objects.equals(multiPicPollRequest.getPollStatus(), PollOperateTypeDto.SET.name())) {
                if (isCascadePolling(conferenceContext, multiPicPollRequest)) {
                    conferenceContext.setMultiPicPollRequest(multiPicPollRequest);
                } else {
                    if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                        bridge.getSmcMultiPicPollInvoker().createMultiPicPollCascade(conferenceContext.getSmc3conferenceId(), multiPicPollRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    } else {
                        bridge.getSmcMultiPicPollInvoker().createMultiPicPoll(conferenceContext.getSmc3conferenceId(), multiPicPollRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    }
                    conferenceContext.setMultiPicPollRequest(multiPicPollRequest);
                }
            }
            if (Objects.equals(multiPicPollRequest.getPollStatus(), PollOperateTypeDto.SET_AND_START.name())) {

                multiPicPollRequest.setPollStatus(PollOperateTypeDto.SET_AND_START.name());
                conferenceContext.setMultiPicPollRequest(multiPicPollRequest);
                AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
                conferenceContext.setLastAttendeeOperation(attendeeOperation);
                attendeeOperation.cancel();
                PollingAttendeeOperation pollingAttendeeOperation = new PollingAttendeeOperation(conferenceContext, JSONObject.parseObject(JSON.toJSONString(multiPicPollRequest_new)));
                conferenceContext.setAttendeeOperation(pollingAttendeeOperation);
                pollingAttendeeOperation.operate();
            }

        } else {
            if (Objects.equals(multiPicPollRequest.getPollStatus(), PollOperateTypeDto.SET.name())) {
                bridge.getSmcMultiPicPollInvoker().createMultiPicPoll(conferenceContext.getSmc3conferenceId(), multiPicPollRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                conferenceContext.setMultiPicPollRequest(multiPicPollRequest);
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

    private MultiPicPollRequest parseMutilPic(MultiPicPollRequest multiPicPollRequest, Smc3ConferenceContext conferenceContext) {
        MultiPicPollRequest multiPicPollRequest_Parse = new MultiPicPollRequest();
        multiPicPollRequest_Parse.setPollStatus(multiPicPollRequest.getPollStatus());
        multiPicPollRequest_Parse.setPicNum(multiPicPollRequest.getPicNum());
        multiPicPollRequest_Parse.setMode(multiPicPollRequest.getMode());
        multiPicPollRequest_Parse.setConferenceId(multiPicPollRequest.getConferenceId());
        multiPicPollRequest_Parse.setBroadcast(multiPicPollRequest.getBroadcast());
        multiPicPollRequest_Parse.setInterval(3600);
        List<MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoListDTOS = new ArrayList<>();
        multiPicPollRequest_Parse.setSubPicPollInfoList(subPicPollInfoListDTOS);

        if (conferenceContext.isUpCascadeConference()) {
            List<MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoList = multiPicPollRequest.getSubPicPollInfoList();
            for (MultiPicPollRequest.SubPicPollInfoListDTO subPicPollInfoListDTO : subPicPollInfoList) {
                MultiPicPollRequest.SubPicPollInfoListDTO subPicPollInfoListDTO1 = new MultiPicPollRequest.SubPicPollInfoListDTO();
                subPicPollInfoListDTO1.setInterval(3600);
                List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIdsDTOS = new ArrayList<>();
                subPicPollInfoListDTO1.setParticipantIds(participantIdsDTOS);
                subPicPollInfoListDTOS.add(subPicPollInfoListDTO1);
                List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIds = subPicPollInfoListDTO.getParticipantIds();
                for (MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantId : participantIds) {
                    MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantIdsDTO = new MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO();
                    participantIdsDTOS.add(participantIdsDTO);
                    String attendeeId = participantId.getParticipantId();
                    participantIdsDTO.setParticipantId(attendeeId);
                    participantIdsDTO.setStreamNumber(participantId.getStreamNumber());
                    participantIdsDTO.setWeight(participantId.getWeight());

                    AttendeeSmc3 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc3Id(attendeeId);
                    if (attendeeBySmc3Id == null) {
                        List<McuAttendeeSmc3> mcuAttendees = conferenceContext.getMcuAttendees();
                        for (McuAttendeeSmc3 mcuAttendee : mcuAttendees) {
                            String cascadeMcuType = mcuAttendee.getCascadeMcuType();
                            if (!Objects.equals(McuType.SMC3.getCode(), cascadeMcuType)) {
                                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendee.getCascadeConferenceId()));
                                BaseAttendee attendeeById = baseConferenceContext.getAttendeeById(attendeeId);
                                if (attendeeById != null) {
                                    String id = attendeeById.getId();
                                    if (Objects.equals(id, attendeeId)) {
                                        participantIdsDTO.setParticipantId(mcuAttendee.getParticipantUuid());
                                        break;
                                    }
                                }
                            } else {
                                Smc3ConferenceContext smc3ConferenceContext = Smc3ConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendee.getCascadeConferenceId()));
                                BaseAttendee attendeeById = smc3ConferenceContext.getAttendeeBySmc3Id(attendeeId);
                                if (attendeeById != null) {
                                    participantIdsDTO.setParticipantId(mcuAttendee.getParticipantUuid());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return multiPicPollRequest_Parse;
    }


    private MultiPicPollRequest parseMutilPicNew(MultiPicPollRequest multiPicPollRequest, Smc3ConferenceContext conferenceContext) {
        MultiPicPollRequest multiPicPollRequest_Parse = new MultiPicPollRequest();
        multiPicPollRequest_Parse.setPollStatus(multiPicPollRequest.getPollStatus());
        multiPicPollRequest_Parse.setPicNum(multiPicPollRequest.getPicNum());
        multiPicPollRequest_Parse.setMode(multiPicPollRequest.getMode());
        multiPicPollRequest_Parse.setConferenceId(multiPicPollRequest.getConferenceId());
        multiPicPollRequest_Parse.setBroadcast(multiPicPollRequest.getBroadcast());
        multiPicPollRequest_Parse.setInterval(multiPicPollRequest.getInterval());
        List<MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoListDTOS = new ArrayList<>();
        multiPicPollRequest_Parse.setSubPicPollInfoList(multiPicPollRequest.getSubPicPollInfoList());

        if (conferenceContext.isUpCascadeConference()) {
            List<MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoList = multiPicPollRequest_Parse.getSubPicPollInfoList();
            for (MultiPicPollRequest.SubPicPollInfoListDTO subPicPollInfoListDTO : subPicPollInfoList) {
                List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIds = subPicPollInfoListDTO.getParticipantIds();
                for (MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantId : participantIds) {
                    String attendeeId = participantId.getParticipantId();
                    AttendeeSmc3 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc3Id(attendeeId);
                    if (attendeeBySmc3Id == null) {
                        List<McuAttendeeSmc3> mcuAttendees = conferenceContext.getMcuAttendees();
                        for (McuAttendeeSmc3 mcuAttendee : mcuAttendees) {
                            String cascadeMcuType = mcuAttendee.getCascadeMcuType();
                            if (!Objects.equals(McuType.SMC3.getCode(), cascadeMcuType)) {
                                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendee.getCascadeConferenceId()));
                                BaseAttendee attendeeById = baseConferenceContext.getAttendeeById(attendeeId);
                                if (attendeeById != null) {
                                    String id = attendeeById.getId();
                                    if (Objects.equals(id, attendeeId)) {
                                        participantId.setParticipantId(mcuAttendee.getCascadeConferenceId());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return multiPicPollRequest_Parse;
    }


    @Override
    public void stopMultiPicPoll(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();

            if (attendeeOperation instanceof PollingAttendeeOperation) {
                com.paradisecloud.smc3.model.request.MultiPicPollRequest multiPicPollRequest = conferenceContext.getMultiPicPollRequest();
                multiPicPollRequest.setPollStatus(PollOperateTypeDto.STOP.name());
                ((PollingAttendeeOperation) attendeeOperation).setPause(true);
            }


        }
    }

    @Override
    public void startMultiPicPoll(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {

            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            com.paradisecloud.smc3.model.request.MultiPicPollRequest multiPicPollRequest = conferenceContext.getMultiPicPollRequest();
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
                if (!(attendeeOperation instanceof DefaultAttendeeOperation)) {
                    attendeeOperation.cancel();
                } else {
                    DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) attendeeOperation;
                    defaultAttendeeOperation.cancelWithOutMaster();
                }
                PollingAttendeeOperation pollingAttendeeOperation = new PollingAttendeeOperation(conferenceContext, JSONObject.parseObject(JSON.toJSONString(multiPicPollRequest)));
                conferenceContext.setAttendeeOperation(pollingAttendeeOperation);
                pollingAttendeeOperation.operate();
            }


        }
    }

    @Override
    public void cancelMultiPicPoll(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext != null) {
            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if (attendeeOperation instanceof PollingAttendeeOperation) {
                attendeeOperation.cancel();
            }
        }
    }

    public boolean isCascadePolling(Smc3ConferenceContext conferenceContext, MultiPicPollRequest multiPicPollRequest) {
        List<MultiPicPollRequest.SubPicPollInfoListDTO> subPicPollInfoList = multiPicPollRequest.getSubPicPollInfoList();
        for (MultiPicPollRequest.SubPicPollInfoListDTO subPicPollInfoListDTO : subPicPollInfoList) {
            List<MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO> participantIds = subPicPollInfoListDTO.getParticipantIds();
            for (MultiPicPollRequest.SubPicPollInfoListDTO.ParticipantIdsDTO participantId : participantIds) {
                AttendeeSmc3 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc3Id(participantId.getParticipantId());
                if (attendeeBySmc3Id == null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void setChairmanParticipantMultiPicPoll(MultiPicPollRequest multiPicPollRequest) {
        String contextKey = EncryptIdUtil.parasToContextKey(multiPicPollRequest.getConferenceId());
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        AttendeeSmc3 masterAttendee = conferenceContext.getMasterAttendee();
        if (masterAttendee == null) {
            throw new CustomException("请先设置主席");
        }
        String smc3conferenceId = conferenceContext.getSmc3conferenceId();
        Smc3Bridge smc3Bridge = conferenceContext.getSmc3Bridge();


        if (multiPicPollRequest.getPicNum() == 1 && conferenceContext.isUpCascadeConference() && isCascadePolling(conferenceContext, multiPicPollRequest)) {
            conferenceContext.setChairmanParticipantMultiPicPoll(multiPicPollRequest);
        } else {
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                smc3Bridge.getSmcMultiPicPollInvoker().chairmanParticipantMultiPicPollCascade(smc3conferenceId, multiPicPollRequest, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                smc3Bridge.getSmcMultiPicPollInvoker().chairmanParticipantMultiPicPoll(smc3conferenceId, multiPicPollRequest, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
            conferenceContext.setChairmanParticipantMultiPicPoll(multiPicPollRequest);
        }


    }

    @Override
    public MultiPicPollRequest chairmanParticipantMultiPicPollQuery(String conferenceId, String participantId) {

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        MultiPicPollRequest chairmanMultiPicPollRequest = conferenceContext.getChairmanMultiPicPollRequest();

        return chairmanMultiPicPollRequest;
    }

    @Override
    public void chairmanParticipantMultiPicPollOperate(ChairmanPollOperateReq chairmanPollOperateReq) {
        String contextKey = EncryptIdUtil.parasToContextKey(chairmanPollOperateReq.getConferenceId());
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
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
                    if (!(attendeeOperation instanceof DefaultAttendeeOperation)) {
                        attendeeOperation.cancel();
                    } else {
                        DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) attendeeOperation;
                        defaultAttendeeOperation.cancelWithOutMaster();
                    }
                    ChairmanPollingAttendeeOperation chairmanPollingAttendeeOperation = new ChairmanPollingAttendeeOperation(conferenceContext);
                    conferenceContext.setAttendeeOperation(chairmanPollingAttendeeOperation);
                    chairmanPollingAttendeeOperation.operate();
                }
            }


        }
    }

    @Override
    public void createMultiPic(MultiPicInfoReq multiPicInfoReq) {
        String contextKey = EncryptIdUtil.parasToContextKey(multiPicInfoReq.getConferenceId());
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
        conferenceContext.setLastAttendeeOperation(attendeeOperation);
        attendeeOperation.cancel();
        DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext, JSONObject.parseObject(JSON.toJSONString(multiPicInfoReq)));
        defaultAttendeeOperation.operate();

    }

    @Override
    public void conferencesControlChoose(String conferenceId, String participantId, MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();

        if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
            bridge.getSmcConferencesInvoker().conferencesControlChooseCascade(conferenceContext.getSmc3conferenceId(), participantId, multiPicInfoDTO, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        } else {
            bridge.getSmcConferencesInvoker().conferencesControlChoose(conferenceContext.getSmc3conferenceId(), participantId, multiPicInfoDTO, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }

        AttendeeSmc3 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc3Id(participantId);

        ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfoDTO_T = new ChooseMultiPicInfo.MultiPicInfoDTO();
        multiPicInfoDTO_T.setPicNum(multiPicInfoDTO.getPicNum());
        multiPicInfoDTO_T.setMode(multiPicInfoDTO.getMode());
        List<ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO> targetSubPicList = new ArrayList<>();

        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> source_subPicList = multiPicInfoDTO.getSubPicList();

        for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : source_subPicList) {

            ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO subPicListDTO_T = new ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO();
            subPicListDTO_T.setParticipantId(subPicListDTO.getParticipantId());
            subPicListDTO_T.setStreamNumber(0);
            targetSubPicList.add(subPicListDTO_T);
        }
        multiPicInfoDTO_T.setSubPicList(targetSubPicList);
        attendeeBySmc3Id.setMultiPicInfo(multiPicInfoDTO_T);
        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeBySmc3Id.getUpdateMap());
    }

    @Override
    public Object queryMulitiPicPoll(String conferenceId) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        return conferenceContext.getMultiPicPollRequest();
//        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
//        MultiPicPollRequest multiPicPollRequest;
//        if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
//             multiPicPollRequest = bridge.getSmcMultiPicPollInvoker().queryMulitiPicPollCascade(conferenceContext.getSmc3conferenceId(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
//        }else {
//             multiPicPollRequest = bridge.getSmcMultiPicPollInvoker().queryMulitiPicPoll(conferenceContext.getSmc3conferenceId(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
//        }
//        conferenceContext.setMultiPicPollRequest(multiPicPollRequest);
//        return multiPicPollRequest;
    }

    @Override
    public void setBroadcastPoll(BroadcastPollRequest broadcastPollRequest) {
        String contextKey = EncryptIdUtil.parasToContextKey(broadcastPollRequest.getConferenceId());
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        bridge.getSmcMultiPicPollInvoker().broadcastPoll(broadcastPollRequest.getConferenceId(), broadcastPollRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

    }

    @Override
    public void multiPicBroad(String conferenceId, boolean enable) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);

        if (enable) {
            ConferenceState.StateDTO.MultiPicInfoDTO multiPicInfo = conferenceContext.getMultiPicInfo();
            if (multiPicInfo == null) {
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "多画面未设置不能广播");
                throw new CustomException("未设置多画面");
            }
        }


        AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
        if (attendeeOperation instanceof DefaultAttendeeOperation) {
            DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) attendeeOperation;
            if (enable) {
                defaultAttendeeOperation.broadcast();
            } else {
                defaultAttendeeOperation.cancelBroadCast();
            }
        } else {
            conferenceContext.setLastAttendeeOperation(attendeeOperation);
            attendeeOperation.cancel();
            MultiPicPollRequest multiPicPollRequest = new MultiPicPollRequest();
            multiPicPollRequest.setBroadcast(enable);
            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(multiPicPollRequest));
            DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext, jsonObject);
            defaultAttendeeOperation.operate();
        }

    }

    @Override
    public LogsConferenceRep listLog(SmcConferenceRequest request) {

        Asserts.notNull(request.getConferenceId(), "请求参数,会议ID");
        String contextKey = EncryptIdUtil.parasToContextKey(request.getConferenceId());
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return null;
        }
        request.setConferenceId(conferenceContext.getSmc3conferenceId());
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        return bridge.getSmcConferencesInvoker().listLog(request, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }


    @Override
    public void downloadLog(SmcConferenceRequest request, HttpServletResponse response) {
        Asserts.notNull(request.getConferenceId(), "请求参数,会议ID");
        String contextKey = EncryptIdUtil.parasToContextKey(request.getConferenceId());
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return;
        }
        request.setConferenceId(conferenceContext.getSmc3conferenceId());
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        bridge.getSmcConferencesInvoker().downloadLog(request, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders(), response);
    }

    @Override
    public Object httpGetListString(SmcConferenceRequest request) {
        Asserts.notNull(request.getConferenceId(), "请求参数,会议ID");
        String contextKey = EncryptIdUtil.parasToContextKey(request.getConferenceId());
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return null;
        }
        request.setConferenceId(conferenceContext.getSmc3conferenceId());
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        Object listString = bridge.getSmcConferencesInvoker().httpGetListString(request, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

        return listString;
    }

    @Override
    public void lockPresenter(String conferenceId, Boolean lock) {
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return;
        }
        Smc3Bridge smc3Bridge = conferenceContext.getSmc3Bridge();
        smc3Bridge.getSmcConferencesInvoker().lockPresenter(conferenceContext.getSmc3conferenceId(), lock, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void voiceActive(String conferenceId, JSONObject jsonObject) {
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return;
        }

        Boolean isVoiceActive = (Boolean) jsonObject.get("isVoiceActive");
        if(isVoiceActive){

            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
            if(attendeeOperation!=null){
                if(attendeeOperation instanceof  ChairmanPollingAttendeeOperation||attendeeOperation instanceof PollingAttendeeOperation){
                    conferenceContext.setLastAttendeeOperation(attendeeOperation);
                    attendeeOperation.cancel();
                }
            }

        }


        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        Object participantFontDto = jsonObject.get("participantFontDto");
        if (Objects.isNull(participantFontDto)) {
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                bridge.getSmcConferencesInvoker().conferencesControlCascade(conferenceContext.getSmc3conferenceId(), jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                bridge.getSmcConferencesInvoker().conferencesControl(conferenceContext.getSmc3conferenceId(), jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
        } else {
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                bridge.getSmcConferencesInvoker().conferencesShareControlCascade(conferenceContext.getSmc3conferenceId(), jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                bridge.getSmcConferencesInvoker().conferencesShareControl(conferenceContext.getSmc3conferenceId(), jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
        }
    }

    @Override
    public void changeQuiet(String conferenceId, JSONObject jsonObject) {
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return;
        }
        conferenceId = conferenceContext.getSmc3conferenceId();
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        Object participantFontDto = jsonObject.get("participantFontDto");
        if (Objects.isNull(participantFontDto)) {
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                bridge.getSmcConferencesInvoker().conferencesControlCascade(conferenceId, jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                bridge.getSmcConferencesInvoker().conferencesControl(conferenceId, jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
        } else {
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                bridge.getSmcConferencesInvoker().conferencesShareControlCascade(conferenceId, jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                bridge.getSmcConferencesInvoker().conferencesShareControl(conferenceId, jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }

        }
    }

    @Override
    public void changeConferencesStatusTextTipsCaption(String conferenceId, JSONObject jsonObject) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return;
        }
        conferenceId = conferenceContext.getSmc3conferenceId();
        TextTipsSetting textTipsSetting = JSONObject.parseObject(jsonObject.toJSONString(), TextTipsSetting.class);
        textTipsSetting.setType(TxtTypeEnum.CAPTION.name());
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
            bridge.getSmcConferencesInvoker().textTipsSettingCascade(conferenceId, textTipsSetting, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        } else {
            bridge.getSmcConferencesInvoker().textTipsSetting(conferenceId, textTipsSetting, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }

    }

    @Override
    public void setStatus(String conferenceId, JSONObject jsonObject) {
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return;
        }
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();

        Object participantFontDto = jsonObject.get("participantFontDto");
        if (Objects.isNull(participantFontDto)) {
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                bridge.getSmcConferencesInvoker().conferencesControlCascade(conferenceContext.getSmc3conferenceId(), jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                bridge.getSmcConferencesInvoker().conferencesControl(conferenceContext.getSmc3conferenceId(), jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
        } else {
            if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
                bridge.getSmcConferencesInvoker().conferencesShareControlCascade(conferenceContext.getSmc3conferenceId(), jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            } else {
                bridge.getSmcConferencesInvoker().conferencesShareControl(conferenceContext.getSmc3conferenceId(), jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }

        }
        if (jsonObject.get(MAX_PARTICIPANT_NUM) != null) {
            conferenceContext.setMaxParticipantNum((Integer) jsonObject.get("maxParticipantNum"));
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
        }
    }


    @Override
    public void changeParticipantStatusOnly(String conferenceId, String participantId, ParticipantStatus participantStatus) {
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return;
        }
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();
        if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
            bridge.getSmcParticipantsInvoker().conferencesParticipantStatusOnlyCascade(conferenceContext.getSmc3conferenceId(), participantId, participantStatus, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        } else {
            bridge.getSmcParticipantsInvoker().conferencesParticipantStatusOnly(conferenceContext.getSmc3conferenceId(), participantId, participantStatus, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }


    }

    @Override
    public void lockPresenterParticipant(String conferenceId, String participantId, Boolean lock) {
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        Smc3ConferenceContext conferenceContext = Smc3ConferenceContextCache.getInstance().get(contextKey);
        if (conferenceContext == null) {
            return;
        }
        Smc3Bridge bridge = conferenceContext.getSmc3Bridge();

        if (Objects.equals(ConstAPI.CASCADE, conferenceContext.getCategory())) {
            bridge.getSmcConferencesInvoker().lockPresenterParticipantCascade(conferenceContext.getSmc3conferenceId(), participantId, lock, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        } else {
            bridge.getSmcConferencesInvoker().lockPresenterParticipant(conferenceContext.getSmc3conferenceId(), participantId, lock, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }

    }
}
