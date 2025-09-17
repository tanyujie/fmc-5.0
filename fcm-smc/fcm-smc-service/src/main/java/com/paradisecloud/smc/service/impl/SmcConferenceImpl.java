package com.paradisecloud.smc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.paradisecloud.com.fcm.smc.modle.ConferenceState;
import com.paradisecloud.com.fcm.smc.modle.*;
import com.paradisecloud.com.fcm.smc.modle.mix.ConferenceControllerRequest;
import com.paradisecloud.com.fcm.smc.modle.request.*;
import com.paradisecloud.com.fcm.smc.modle.response.*;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.smc.cache.modle.*;
import com.paradisecloud.smc.SMCWebsocketClient;
import com.paradisecloud.smc.SmcWebSocketProcessor;
import com.paradisecloud.smc.SmcWebsocketContext;
import com.paradisecloud.smc.dao.model.*;
import com.paradisecloud.smc.dao.model.mapper.BusiSmcAppointmentConferenceMapper;
import com.paradisecloud.smc.service.*;
import com.paradisecloud.smc.service.busi.ConferenceSMCService;
import com.paradisecloud.smc.service.core.ChairManPollingThread;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.enumer.DateTimeFormatPattern;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.util.Asserts;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author nj
 * @date 2022/8/16 15:35
 */
@Service
public class SmcConferenceImpl implements SmcConferenceService {

    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("MulitiPicPoll-pool-%d").build();
    private static ExecutorService service = new ThreadPoolExecutor(
            4,
            40,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024),
            namedThreadFactory,
            new ThreadPoolExecutor.AbortPolicy()
    );
    @Resource
    private BusiSmcDeptConferenceService busiSmcDeptConferenceService;
    @Resource
    private TemplateService templateService;
    @Resource
    private SmcTemplateTerminalService smcTemplateTerminalService;
    @Resource
    private IBusiSmcTemplateConferenceService smcTemplateConferenceService;
    @Resource
    private BusiSmcDeptTemplateService busiSmcDeptTemplateService;
    @Resource
    private IBusiSmcMulitpicService busiSmcMulitpicService;
    @Resource
    private SmcParticipantsService smcParticipantsService;
    @Resource
    private IBusiSmcHistoryConferenceService smcHistoryConferenceService;
    @Resource
    private IBusiSmcAppointmentConferenceService appointmentConferenceService;
    @Resource
    private BusiSmcAppointmentConferenceMapper busiSmcAppointmentConferenceMapper;
    @Resource
    private IBusiSmcAppointmentConferenceService busiSmcAppointmentConferenceService;
    @Resource
    private IBusiSmcHistoryConferenceService busiSmcHistoryConferenceService;

    @Override
    public String getSmcConferenceInfoById(String id) {

        SmcBridge bridge = getSmcBridge(id, SmcBridgeCache.getInstance().getConferenceBridge());
        if (bridge == null) {
            bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        }
        return bridge.getSmcConferencesInvoker().getConferencesById(id, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void endConference(String conferenceId) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        if (bridge == null) {
            bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        }
        bridge.getSmcConferencesInvoker().endConferences(conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        SmcBridgeCache.getInstance().removeConferenceBridge(conferenceId, bridge);
    }

    @Override
    public void setMic(String conferenceId) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        if (bridge == null) {
            bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        }
        bridge.getSmcConferencesInvoker().conferencesChatmic(conferenceId, true, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void statusControl(String conferenceId, ConferenceStatusRequest conferenceStatusRequest) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        if (bridge == null) {
            bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        }
        bridge.getSmcConferencesInvoker().conferencesStatusControl(conferenceId, conferenceStatusRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    /**
     * 会场ID ->会场号码
     *
     * @param conferenceId
     * @param participantId
     */
    @Override
    public void quickHangup(String conferenceId, String participantId) {

        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        //查询会场详情
        ParticipantRspDto participantsDetailInfoDto = bridge.getSmcParticipantsInvoker().getParticipantsDetailInfoDto(conferenceId, participantId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        bridge.getSmcConferencesInvoker().quickHangup(participantsDetailInfoDto.getUri(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    /**
     * 轮询操作
     *
     * @param multiPicInfoReq
     */
    @Override
    public void createMulitiPicPoll(MultiPicInfoReq multiPicInfoReq) {
        String conferenceId = multiPicInfoReq.getConferenceId();
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());

        //不广播 设置主席为多画面
        if (!multiPicInfoReq.getBroadcast()) {
            //查看会议状态
            DetailConference detailConference = getDetailConferenceInfoById(conferenceId);
            if (detailConference == null) {
                throw new CustomException("会议不存在");
            }
            String chairmanId = detailConference.getConferenceState().getChairmanId();
            if (StringUtils.isBlank(chairmanId)) {
                throw new CustomException("请先设置主席");
            }
            //设置主席轮询


            bridge.getSmcConferencesInvoker().conferencesControlChooseMaster(conferenceId, chairmanId, multiPicInfoReq, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        } else {
            //   bridge.getSmcMultiPicPollInvoker().createMultiPicPoll(multiPicPollRequest.getConferenceId(), multiPicPollRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

        }

    }

    @Override
    public void setBroadcastPoll(BroadcastPollRequest broadcastPollRequest) {
        SmcBridge bridge = getSmcBridge(broadcastPollRequest.getConferenceId(), SmcBridgeCache.getInstance().getConferenceBridge());

        bridge.getSmcMultiPicPollInvoker().broadcastPoll(broadcastPollRequest.getConferenceId(), broadcastPollRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void broadcastStart(String conferenceId) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("broadcaster", "00000000-0000-0000-0000-000000000000");
        bridge.getSmcConferencesInvoker().conferencesStatusControl(conferenceId, jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void broadcastEnd(String conferenceId) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("broadcaster", "");
        bridge.getSmcConferencesInvoker().conferencesStatusControl(conferenceId, jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public SmcConferenceRep getConferenceList(SmcConferenceRequest smcConferenceRequest) {
        Long deptId = smcConferenceRequest.getDeptId();
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(deptId);
        return bridge.getSmcConferencesInvoker().list(smcConferenceRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public SmcAppointmentConferenceContext appointmentConferenceAdd(SmcAppointmentConferenceRequest smcAppointmentConferenceRequest) {
        Long deptId = smcAppointmentConferenceRequest.getDeptId();
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(deptId);
        SmcAppointmentConferenceContext conferenceContext = bridge.getSmcConferencesInvoker().appointmentConferenceAdd(smcAppointmentConferenceRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        SmcBridgeCache.getInstance().putAppointmentConferenceBridge(conferenceContext.getConference().getId(), bridge);
        //同步数据库 会议ID 和部门
        BusiSmcDeptConference busiSmcDeptConference = new BusiSmcDeptConference();
        busiSmcDeptConference.setSmcConferenceId(conferenceContext.getConference().getId());
        busiSmcDeptConference.setDeptId(deptId);
        busiSmcDeptConference.setCreateTime(new Date());
        busiSmcDeptConferenceService.add(busiSmcDeptConference);

        return conferenceContext;
    }

    @Override
    public SmcAppointmentConferenceContext appointmentConferenceChange(SmcAppointmentConferenceRequest smcAppointmentConferenceRequest) {
        Long deptId = smcAppointmentConferenceRequest.getDeptId();
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(deptId);
        SmcBridgeCache.getInstance().putAppointmentConferenceBridge(smcAppointmentConferenceRequest.getConference().getId(), bridge);
        return bridge.getSmcConferencesInvoker().appointmentConferenceChange(smcAppointmentConferenceRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void appointmentConferenceDelete(String conferenceId) {
        SmcBridge smcBridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getAppointmentConferenceBridge());
        smcBridge.getSmcConferencesInvoker().appointmentConferenceDelete(conferenceId, smcBridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        SmcBridgeCache.getInstance().removeAppointConferenceBridge(conferenceId, smcBridge);
    }

    @Override
    public SmcConferenceContext buildTemplateConferenceContext(String templateId) {

        SmcConferenceContext smcConferenceContext = null;
        List<SmcParitipantsStateRep.ContentDTO> contents = new ArrayList<>();
        BusiSmcTemplateConference busiSmcTemplateConference = new BusiSmcTemplateConference();
        busiSmcTemplateConference.setSmcTemplateId(templateId);

        List<BusiSmcTemplateConference> busiSmcTemplateConferences = smcTemplateConferenceService.selectBusiSmcTemplateConferenceList(busiSmcTemplateConference);

        if (CollectionUtils.isEmpty(busiSmcTemplateConferences)) {
            smcConferenceContext = getSmcConferenceContext(templateId);

        } else {
            BusiSmcTemplateConference busiSmcTemplateConference1 = busiSmcTemplateConferences.get(0);

            String conferenceId = busiSmcTemplateConference1.getConferenceId();
            SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
            if (bridge == null) {
                smcConferenceContext = getSmcConferenceContext(templateId);
                smcTemplateConferenceService.deleteBusiSmcTemplateConferenceByConferenceId(conferenceId);
            } else {
                String smcConferenceInfoById = getSmcConferenceInfoById(conferenceId);
                if (smcConferenceInfoById.contains(ConstAPI.CONFERENCE_NOT_EXIST)) {
                    smcTemplateConferenceService.deleteBusiSmcTemplateConferenceById(busiSmcTemplateConference1.getId());
                    smcConferenceContext = getSmcConferenceContext(templateId);
                } else {
                    smcConferenceContext = JSON.parseObject(smcConferenceInfoById, SmcConferenceContext.class);

                    if (Objects.isNull(smcConferenceContext)) {
                        if (Strings.isNotBlank(smcConferenceInfoById) && smcConferenceInfoById.contains(ConstAPI.CONFERENCE_NOT_EXIST)) {
                            smcTemplateConferenceService.deleteBusiSmcTemplateConferenceById(busiSmcTemplateConference1.getId());
                            smcConferenceContext = getSmcConferenceContext(templateId);
                            return smcConferenceContext;
                        } else {
                            throw new CustomException("SMC会议详情错误");
                        }
                    }

                    //查询详情
                    DetailConference detailConference = getDetailConferenceInfoById(conferenceId);
                    if (detailConference != null && detailConference.getConferenceState() != null) {
                        List<ConferenceState.ParticipantPollStatusListDTO> participantPollStatusList = detailConference.getConferenceState().getParticipantPollStatusList();
                        if (!CollectionUtils.isEmpty(participantPollStatusList)) {
                            ConferenceState.ParticipantPollStatusListDTO participantPollStatusListDTO = participantPollStatusList.get(0);
                            String pollStatus = participantPollStatusListDTO.getPollStatus();
                            detailConference.getConferenceState().setChairmanPollStatus(pollStatus);
                        }
                    }
                    smcConferenceContext.setDetailConference(detailConference);
                    //订阅消息
                    SMCWebsocketClient mwsc = SmcWebsocketContext.getSmcWebsocketClientMap().get(bridge.getIp());
                    if (mwsc != null) {
                        SmcWebSocketProcessor webSocketProcessor = mwsc.getWebSocketProcessor();
                        webSocketProcessor.firstSubscription(conferenceId);
                    }
                }

            }
        }
        BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplate(templateId);
        Long deptId = busiSmcDeptTemplate.getDeptId();
        smcConferenceContext.setDeptId(deptId);
        List<SmcTemplateTerminal> templateTerminals = smcTemplateTerminalService.list(templateId);
        List<ParticipantRspDto> templateParticipants = smcConferenceContext.getParticipants();
        if (!CollectionUtils.isEmpty(templateParticipants)) {
            templateParticipants.stream().forEach(p -> {
                SmcParitipantsStateRep.ContentDTO contentDTO = new SmcParitipantsStateRep.ContentDTO();
                Optional<SmcTemplateTerminal> first = templateTerminals.stream().filter(m -> Objects.equals(p.getUri(), m.getSmcnumber())).findFirst();
                if (first.isPresent()) {
                    SmcTemplateTerminal smcTemplateTerminal = first.get();
                    // p.setDeptId(smcTemplateTerminal.getTerminalDeptId());
                    // p.setTerminalId(smcTemplateTerminal.getTerminalId());
                    contentDTO.setDeptId(smcTemplateTerminal.getTerminalDeptId());
                    contentDTO.setTerminalId(smcTemplateTerminal.getTerminalId());
                }
                ParticipantState participantState = new ParticipantState();
                participantState.setParticipantId(p.getId());
                p.setParticipantState(participantState);

                SmcParitipantsStateRep.ContentDTO.GeneralParamDTO paramDTO = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
                paramDTO.setName(p.getName());
                paramDTO.setUri(p.getUri());
                contentDTO.setState(participantState);
                contentDTO.setGeneralParam(paramDTO);

                contents.add(contentDTO);
            });
        }


        smcConferenceContext.setContent(contents);

        return smcConferenceContext;
    }

    private SmcConferenceContext getSmcConferenceContext(String templateId) {
        SmcConferenceContext smcConferenceContext;
        smcConferenceContext = new SmcConferenceContext();

        // 获取模板会议实体对象
        String templateById = templateService.getTemplateById(templateId);
        if (!Objects.isNull(templateById)) {
            SmcConferenceTemplate smcConferenceTemplate = JSON.parseObject(templateById, SmcConferenceTemplate.class);
            List<ParticipantRspDto> templateParticipants = smcConferenceTemplate.getTemplateParticipants();
            //  List<SmcTemplateTerminal> templateTerminals = smcTemplateTerminalService.list(smcConferenceTemplate.getId());

            //构建会议
            SmcConference smcConference = new SmcConference();
            smcConference.setDuration(smcConferenceTemplate.getDuration());
            smcConference.setSubject(smcConferenceTemplate.getSubject());
            smcConference.setActive(false);
            smcConferenceContext.setConference(smcConference);

            BusiSmcDeptTemplate busiSmcDeptTemplate = busiSmcDeptTemplateService.queryTemplate(templateId);
            smcConferenceContext.setDeptId(busiSmcDeptTemplate.getDeptId());
            smcConferenceContext.setParticipants(templateParticipants);
            initDetailparam(smcConferenceContext);
        }
        return smcConferenceContext;
    }

    private void initDetailparam(SmcConferenceContext smcConferenceContext) {
        DetailConference detailConference = new DetailConference();
        detailConference.setConferenceState(new ConferenceState());
        detailConference.setConferenceUiParam(new ConferenceUiParam());
        smcConferenceContext.setDetailConference(detailConference);
    }

    @Override
    public DetailConference getDetailConferenceInfoById(String id) {
        SmcBridge bridge = getSmcBridge(id, SmcBridgeCache.getInstance().getConferenceBridge());
        if (bridge == null) {
            return null;
        }
        DetailConference detailConference = bridge.getSmcConferencesInvoker().getDetailConferencesById(id, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        return detailConference;
    }

    @Override
    public void share(String conferenceId, String presenter) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("presenter", presenter);
        String s = bridge.getSmcConferencesInvoker().conferencesShareControl(conferenceId, jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        System.out.println(s);
    }

    @Override
    public void lockConference(String conferenceId) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isLock", true);
        bridge.getSmcConferencesInvoker().conferencesControl(conferenceId, jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void unlockConference(String conferenceId) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isLock", false);
        bridge.getSmcConferencesInvoker().conferencesControl(conferenceId, jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void setMute(String conferenceId, boolean b) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isMute", b);
        bridge.getSmcConferencesInvoker().conferencesControl(conferenceId, jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void setFreeTalk(String conferenceId) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mode", "FREE_TALK");
        bridge.getSmcConferencesInvoker().conferencesControl(conferenceId, jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void ExtendTime(ExtendTimeReq extendTimeReq) {
        SmcBridge bridge = getSmcBridge(extendTimeReq.getConferenceId(), SmcBridgeCache.getInstance().getConferenceBridge());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("extendTime", extendTimeReq.getExtendTime());
        bridge.getSmcConferencesInvoker().conferencesExTendTime(extendTimeReq.getConferenceId(), jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void statusControlchoose(String chairmanId, ConferenceControllerRequest callTheRollRequest) {
        String conferenceId = callTheRollRequest.getConferenceId();
        String participantId = callTheRollRequest.getParticipantId();
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        bridge.getSmcConferencesInvoker().conferencesControlChoose(conferenceId, chairmanId, participantId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

    }

    @Override
    public void textTipsSetting(TextTipsSetting textTipsSetting) {
        String conferenceId = textTipsSetting.getConferenceId();
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        bridge.getSmcConferencesInvoker().textTipsSetting(conferenceId, textTipsSetting, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public MultiPicPollRequest queryMulitiPicPoll(String conferenceId) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        return bridge.getSmcMultiPicPollInvoker().queryMulitiPicPoll(conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public List<PresetMultiPicReqDto> getConferencesPresetParam(String conferenceId) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        return bridge.getSmcMultiPicPollInvoker().getConferencesPresetParam(conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void createMulitiPic(MultiPicInfoReq multiPicInfoReq) {

        String conferenceId = multiPicInfoReq.getConferenceId();
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        //不广播 设置主席为多画面
        if (!multiPicInfoReq.getBroadcast()) {
            //查看会议状态
            DetailConference detailConference = getDetailConferenceInfoById(conferenceId);
            if (detailConference == null) {
                throw new CustomException("会议不存在");
            }
            String chairmanId = detailConference.getConferenceState().getChairmanId();
            if (StringUtils.isBlank(chairmanId)) {
                throw new CustomException("请先设置主席");
            }
            ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
            conferenceStatusRequest.setBroadcaster(chairmanId);
            statusControl(conferenceId, conferenceStatusRequest);

            bridge.getSmcConferencesInvoker().conferencesControlChooseMaster(conferenceId, chairmanId, multiPicInfoReq, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        } else {
            bridge.getSmcConferencesInvoker().createMulitiPic(conferenceId, multiPicInfoReq, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            //开始广播
            broadcastStart(conferenceId);
        }


    }

    @Override
    public void createMulitiPicNObroad(MultiPicInfoReq multiPicInfoReq) {
        String conferenceId = multiPicInfoReq.getConferenceId();
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());

        bridge.getSmcConferencesInvoker().createMulitiPic(conferenceId, multiPicInfoReq, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

        try {
            BusiSmcMulitpic busiSmcMulitpic = busiSmcMulitpicService.selectBusiSmcMulitpicByConferenceId(conferenceId);
            if (busiSmcMulitpic == null) {
                BusiSmcMulitpic busiSmcMulitpic1 = new BusiSmcMulitpic();
                busiSmcMulitpic1.setConferenceId(conferenceId);
                busiSmcMulitpic1.setMulitpic(JSON.toJSONString(multiPicInfoReq));
                busiSmcMulitpicService.insertBusiSmcMulitpic(busiSmcMulitpic1);
            } else {
                busiSmcMulitpic.setMulitpic(JSON.toJSONString(multiPicInfoReq));
                busiSmcMulitpicService.updateBusiSmcMulitpic(busiSmcMulitpic);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, MultiPicInfoReq> localMultiPicInfoMap = SmcConferenceContextCache.getInstance().getLocalMultiPicInfoMap();
        localMultiPicInfoMap.put(conferenceId, multiPicInfoReq);
        //不广播 设置主席为多画面
        if (multiPicInfoReq.getBroadcast()) {


            try {
                String chooseId = SmcConferenceContextCache.getInstance().getChooseParticipantMap().get(conferenceId);
                if (Strings.isNotBlank(chooseId)) {
                    ConferenceSMCService conferenceSMCService = BeanFactory.getBean(ConferenceSMCService.class);
                    conferenceSMCService.cancelChoose(conferenceId, chooseId, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                ChairmanPollOperateReq chairmanPollOperateReq = new ChairmanPollOperateReq();
                chairmanPollOperateReq.setConferenceId(conferenceId);
                chairmanPollOperateReq.setPollStatus(PollOperateTypeDto.CANCEL);
                chairmanParticipantMultiPicPollOperate(chairmanPollOperateReq);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //开始广播
            broadcastStart(conferenceId);
            setMute(conferenceId, true);
            MultiPicInfoReq.MultiPicInfoDTO multiPicInfo = multiPicInfoReq.getMultiPicInfo();
            List<String> broadParticipants = new ArrayList<>();
            List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfo.getSubPicList();
            if (!CollectionUtils.isEmpty(subPicList)) {
                for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                    String participantId = subPicListDTO.getParticipantId();
                    broadParticipants.add(participantId);
                }
            }
            SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.BROAD_LIST, broadParticipants);
        }
    }

    private void muteTrue(String conferenceId, String broadcastId) {
        if (StringUtils.isBlank(broadcastId)) {
            return;
        }
        List<ParticipantStatusDto> participantStatusList = new ArrayList<>();
        ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
        participantStatusDto.setId(broadcastId);
        participantStatusDto.setIsMute(true);
        participantStatusList.add(participantStatusDto);
        smcParticipantsService.PATCHParticipantsOnly(conferenceId, participantStatusList);
    }

    @Override
    public void createChairmanPollMulitiPicPoll(MasterPollTemplate masterPollTemplate) {
        String conferenceId = masterPollTemplate.getConferenceId();
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());

        bridge.getSmcMultiPicPollInvoker().createChairmanPollMultiPicPoll(conferenceId, masterPollTemplate, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public MasterPollTemplate chairmanPollQuery(String conferenceId) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());

        return bridge.getSmcMultiPicPollInvoker().queryChairmanPollMultiPicPoll(conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void chairmanPollOperate(ChairmanPollOperateReq chairmanPollOperateReq) {
        String conferenceId = chairmanPollOperateReq.getConferenceId();
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        DetailConference detailConference = getDetailConferenceInfoById(conferenceId);
        if (detailConference == null) {
            throw new CustomException("会议不存在");
        }
        String chairmanId = detailConference.getConferenceState().getChairmanId();
        if (StringUtils.isBlank(chairmanId)) {
            throw new CustomException("主席不存在");
        }
        //查询主席轮询设置
        MasterPollTemplate masterPollTemplate = bridge.getSmcMultiPicPollInvoker().queryChairmanPollMultiPicPoll(conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        if (masterPollTemplate == null || CollectionUtils.isEmpty(masterPollTemplate.getPollTemplates())) {
            throw new CustomException("主席轮询未设置");
        }
        bridge.getSmcMultiPicPollInvoker().chairmanPollOperate(conferenceId, chairmanPollOperateReq, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public List<VideoSourceRep> conferencesVideoSource(String conferenceId, List<String> participants) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        return bridge.getSmcConferencesInvoker().conferencesVideoSource(conferenceId, participants, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void conferencesControlChoose(String conferenceId, String participantId, MultiPicInfoReq.MultiPicInfoDTO multiPicInfoD) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        bridge.getSmcConferencesInvoker().conferencesControlChoose(conferenceId, participantId, multiPicInfoD, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public ParticipantOrderRep orderQuery(SmcConferenceRequest conferenceRequest) {
        String conferenceId = conferenceRequest.getConferenceId();
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        return bridge.getSmcConferencesInvoker().participantsOrderQueryBody(conferenceId, conferenceRequest.getPage(), conferenceRequest.getSize(), conferenceRequest.getQueryParticipantConditionDto(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void order(ParticipantOrderRequest participantOrderRequest) {
        String conferenceId = participantOrderRequest.getConferenceId();
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        bridge.getSmcConferencesInvoker().participantsOrder(conferenceId, participantOrderRequest.getSet(), participantOrderRequest.getParticipantIdList(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public LogsConferenceRep listLog(SmcConferenceRequest request) {

        Asserts.notNull(request.getConferenceId(), "请求参数,会议ID");
        SmcBridge bridge = getSmcBridge(request.getConferenceId(), SmcBridgeCache.getInstance().getConferenceBridge());
        Asserts.notNull(bridge, "会议桥未找到");
        return bridge.getSmcConferencesInvoker().listLog(request, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public Object exportLog(SmcConferenceRequest request) {
        Asserts.notNull(request.getConferenceId(), "请求参数,会议ID");
        SmcBridge bridge = getSmcBridge(request.getConferenceId(), SmcBridgeCache.getInstance().getConferenceBridge());
        Asserts.notNull(bridge, "会议桥未找到");
        bridge.getSmcConferencesInvoker().exportLog(request, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        return null;
    }

    @Override
    public void downloadLog(SmcConferenceRequest request, HttpServletResponse response) {
        Asserts.notNull(request.getConferenceId(), "请求参数,会议ID");
        SmcBridge bridge = getSmcBridge(request.getConferenceId(), SmcBridgeCache.getInstance().getConferenceBridge());
        if (bridge == null) {
            bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        }
        Asserts.notNull(bridge, "会议桥未找到");
        bridge.getSmcConferencesInvoker().downloadLog(request, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders(), response);
    }

    @Override
    public Object httpGetListString(SmcConferenceRequest request) {
        Asserts.notNull(request.getConferenceId(), "请求参数,会议ID");
        SmcBridge bridge = getSmcBridge(request.getConferenceId(), SmcBridgeCache.getInstance().getConferenceBridge());
        Asserts.notNull(bridge, "会议桥未找到");
        Object listString = bridge.getSmcConferencesInvoker().httpGetListString(request, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

        return listString;
    }

    @Override
    public void lockPresenter(String conferenceId, Boolean lock) {
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        if (bridge == null) {
            bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        }
        Asserts.notNull(bridge, "会议桥未找到");
        bridge.getSmcConferencesInvoker().lockPresenter(conferenceId, lock, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void lockPresenterParticipant(String conferenceId, String participantId, Boolean lock) {
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        if (bridge == null) {
            bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        }
        Asserts.notNull(bridge, "会议桥未找到");
        bridge.getSmcConferencesInvoker().lockPresenterParticipant(conferenceId, participantId, lock, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    private SmcBridge getSmcBridge(String conferenceId, Map<String, SmcBridge> conferenceBridge) {
        SmcBridge smcBridge = conferenceBridge.get(conferenceId);
        if (smcBridge == null) {
            smcBridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        }
        return smcBridge;
    }

    @Override
    public void setMultiPicPoll(MultiPicPollRequest multiPicPollRequest) {
        String conferenceId = multiPicPollRequest.getConferenceId();
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        Asserts.notNull(bridge, "会议桥未找到");
        bridge.getSmcMultiPicPollInvoker().createMultiPicPoll(conferenceId, multiPicPollRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void stopMultiPicPoll(String conferenceId) {
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        Asserts.notNull(bridge, "会议桥未找到");
        bridge.getSmcMultiPicPollInvoker().stopMultiPicPoll(conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void startMultiPicPoll(String conferenceId) {
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        Asserts.notNull(bridge, "会议桥未找到");
        bridge.getSmcMultiPicPollInvoker().startMultiPicPoll(conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void cancelMultiPicPoll(String conferenceId) {
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        Asserts.notNull(bridge, "会议桥未找到");
        bridge.getSmcMultiPicPollInvoker().cancelMultiPicPoll(conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void camera(String conferenceId, String participantId, JSONObject jsonObject) {
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        Asserts.notNull(bridge, "会议桥未找到");
        bridge.getSmcParticipantsInvoker().participantsCameraControl(conferenceId, participantId, jsonObject, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public Object count() {
        int running = 0;
        int coming = 0;
        int today = 0;
        int tomorrow = 0;

        BusiSmcHistoryConference historyConference = new BusiSmcHistoryConference();
        historyConference.setEndStatus(2);
        List<BusiSmcHistoryConference> busiSmcHistoryConferences = smcHistoryConferenceService.selectBusiSmcHistoryConferenceList(historyConference);
        if (!CollectionUtils.isEmpty(busiSmcHistoryConferences)) {
            running = busiSmcHistoryConferences.size();
        }
        //统计今日会议
        List<BusiSmcAppointmentConference> historyConferenceTodayList = appointmentConferenceService.selectBusiSmcAppointmentConferenceByTime(new Date());
        if (!CollectionUtils.isEmpty(historyConferenceTodayList)) {
            today = historyConferenceTodayList.size();
        }

        List<BusiSmcTemplateConference> busiSmcTemplateConferences = smcTemplateConferenceService.selectTemplateConferenceList(null, 1, 10000);
        if (!CollectionUtils.isEmpty(busiSmcTemplateConferences)) {
            for (BusiSmcTemplateConference busiSmcTemplateConference : busiSmcTemplateConferences) {
                SmcConferenceContext smcConferenceContext = SmcConferenceContextCache.getInstance().getSmcConferenceContextMap().get(busiSmcTemplateConference.getConferenceId());

                if (smcConferenceContext != null) {
                    String conferenceId = smcConferenceContext.getConference().getId();
                    DetailConference detailConference = getDetailConferenceInfoById(conferenceId);
                    if (detailConference != null) {
                        today = today + 1;
                    }
                }
            }
        }

        //统计明日会议
        List<BusiSmcAppointmentConference> historyConferenceTomorrowList = appointmentConferenceService.selectBusiSmcAppointmentConferenceByTime(DateUtils.addDays(new Date(), 1));
        if (!CollectionUtils.isEmpty(historyConferenceTomorrowList)) {
            today = historyConferenceTomorrowList.size();
        }


        //统计待召开会议
        List<BusiSmcAppointmentConference> busiSmcAppointmentConferences = appointmentConferenceService.selectBusiSmcAppointmentConferenceByStartTime();
        if (!CollectionUtils.isEmpty(busiSmcAppointmentConferences)) {
            coming = busiSmcAppointmentConferences.size();
        }
        HashMap<String, Object> obj = new HashMap<>(4);
        obj.put("running", running);
        obj.put("coming", coming);
        obj.put("today", today);
        obj.put("tomorrow", tomorrow);

        return obj;
    }

    @Override
    public void changeName(String conferenceId, JSONObject jsonObject) {
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        Asserts.notNull(bridge, "会议桥未找到");
        bridge.getSmcParticipantsInvoker().participantsParam(conferenceId, jsonObject, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void remind(String conferenceId, String participantId) {
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        Asserts.notNull(bridge, "会议桥未找到");
        bridge.getSmcParticipantsInvoker().remind(conferenceId, participantId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void setStatus(String conferenceId, JSONObject jsonObject) {
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        Asserts.notNull(bridge, "会议桥未找到");
        Object participantFontDto = jsonObject.get("participantFontDto");
        if (Objects.isNull(participantFontDto)) {
            bridge.getSmcConferencesInvoker().conferencesControl(conferenceId, jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        } else {
            bridge.getSmcConferencesInvoker().conferencesShareControl(conferenceId, jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

        }

    }

    @Override
    public Object conferenceStat() {
        Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
        int running = 0;
        JSONObject json = new JSONObject();
        if (deptId != null) {

            json.put("deptTemplateCount", busiSmcDeptTemplateService.queryTemplateListByDeptId(deptId).size());

            BusiSmcHistoryConference historyConference = new BusiSmcHistoryConference();
            historyConference.setEndStatus(2);
            historyConference.setDeptId(deptId);
            List<BusiSmcHistoryConference> busiSmcHistoryConferences = smcHistoryConferenceService.selectBusiSmcHistoryConferenceList(historyConference);
            if (!CollectionUtils.isEmpty(busiSmcHistoryConferences)) {
                running = busiSmcHistoryConferences.size();
                // 活跃会议室的数量
            }
            json.put("activeConferenceCount", running);

            List<BusiSmcAppointmentConference> busiSmcAppointmentConferences = busiSmcAppointmentConferenceMapper.selectBusiSmcAppointmentConferenceQuery(deptId, null, DateUtil.convertDateToString(new Date(), null), null, null);
            if (CollectionUtils.isEmpty(busiSmcAppointmentConferences)) {
                // 预约会议数
                json.put("appointConferenceCount", 0);
            } else {
                // 预约会议数
                json.put("appointConferenceCount", busiSmcAppointmentConferences.size());
            }

        } else {


            BusiSmcHistoryConference historyConference = new BusiSmcHistoryConference();
            historyConference.setEndStatus(2);
            List<BusiSmcHistoryConference> busiSmcHistoryConferences = smcHistoryConferenceService.selectBusiSmcHistoryConferenceList(historyConference);
            if (!CollectionUtils.isEmpty(busiSmcHistoryConferences)) {
                running = busiSmcHistoryConferences.size();
                // 活跃会议室的数量
            }
            // 活跃会议室的数量
            json.put("activeConferenceCount", running);
            json.put("deptTemplateCount", busiSmcDeptTemplateService.queryTemplateListByDeptId(deptId).size());

            // 预约会议数
            List<BusiSmcAppointmentConference> busiSmcAppointmentConferences = busiSmcAppointmentConferenceMapper.selectBusiSmcAppointmentConferenceQuery(deptId, null, DateUtil.convertDateToString(new Date(), null), null, null);
            if (CollectionUtils.isEmpty(busiSmcAppointmentConferences)) {
                // 预约会议数
                json.put("appointConferenceCount", 0);
            } else {
                // 预约会议数
                json.put("appointConferenceCount", busiSmcAppointmentConferences.size());
            }
        }
        return json;
    }

    @Override
    public Map<String, Object> reportConferenceOfIndex(Long deptId, String startTime, String endTime) {
        deptId = (deptId == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : deptId;

        if (Objects.nonNull(startTime)) {
            org.springframework.util.Assert.isTrue(DateTimeFormatPattern.matchFormatter(startTime) != null, "请传入正确的时间格式");
        }
        if (Objects.nonNull(endTime)) {
            Assert.isTrue(DateTimeFormatPattern.matchFormatter(endTime) != null, "请传入正确的时间格式");
        }

        BusiSmcAppointmentConferenceQuery query = new BusiSmcAppointmentConferenceQuery();
        query.setDeptId(deptId);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        List<BusiSmcHistoryConference> historyConferences0 = busiSmcHistoryConferenceService.selectBusiSmcHistoryConferenceListBySearchKey(query);

        List<BusiSmcHistoryConference> historyConferences = new ArrayList<>();
        Set<Long> depts = SysDeptCache.getInstance().getSubordinateDeptIds(deptId);
        for (BusiSmcHistoryConference busiHistoryConference : historyConferences0) {
            if (depts.contains(busiHistoryConference.getDeptId())) {
                if (busiHistoryConference.getEndStatus() == 1) {
                    historyConferences.add(busiHistoryConference);
                }
            }
        }
        Map<String, Object> map = new HashMap<>();

        Integer sum = 0;
        Integer deviceNum = 0;
        if (!CollectionUtils.isEmpty(historyConferences)) {
            for (BusiSmcHistoryConference historyConference : historyConferences) {
                int duration = (historyConference.getDuration() == null) ? 0 : historyConference.getDuration();
                if (duration == Integer.MAX_VALUE) {
                    duration = 0;
                }
                sum += duration;
                Integer participantNum = historyConference.getParticipantNum();
                if (participantNum != null) {
                    deviceNum += participantNum.intValue();
                }
            }
        }

        map.put("conferenceCount", CollectionUtils.isEmpty(historyConferences) ? 0 : historyConferences.size());
        map.put("totalDuration", sum);
        map.put("deviceNum", deviceNum);

        return map;
    }

    @Override
    public void chairmanParticipantMultiPicPoll(MultiPicPollRequest multiPicPollRequest) {
        String conferenceId = multiPicPollRequest.getConferenceId();
        Asserts.notNull(conferenceId, "请求参数,会议ID");
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        Asserts.notNull(bridge, "会议桥未找到");
        bridge.getSmcMultiPicPollInvoker().chairmanParticipantMultiPicPoll(conferenceId, multiPicPollRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public MultiPicPollRequest chairmanParticipantMultiPicPollQuery(String conferenceId, String participantId) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        Asserts.notNull(bridge, "会议桥未找到");

        DetailConference detailConference = getDetailConferenceInfoById(conferenceId);
        if (detailConference == null) {
            throw new CustomException("会议状态获取错误");
        }
        String chairmanId = detailConference.getConferenceState().getChairmanId();
        if (participantId == null) {
            participantId = chairmanId;
        }

        MultiPicPollRequest multiPicPollRequest = bridge.getSmcMultiPicPollInvoker().chairmanParticipantMultiPicPollQuery(conferenceId, participantId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        return multiPicPollRequest;
    }


    @Override
    public void chairmanParticipantMultiPicPollOperate(ChairmanPollOperateReq chairmanPollOperateReq) {
        String conferenceId = chairmanPollOperateReq.getConferenceId();
        Asserts.notNull(conferenceId, "请求参数,会议ID");


        DetailConference detailConference = getDetailConferenceInfoById(conferenceId);
        if (detailConference == null) {
            throw new CustomException("会议状态获取错误");
        }
        String chairmanId = detailConference.getConferenceState().getChairmanId();
        MultiPicPollRequest multiPicPollRequest = chairmanParticipantMultiPicPollQuery(chairmanPollOperateReq.getConferenceId(), chairmanId);
        if (multiPicPollRequest == null) {
            throw new CustomException("未设置主席轮询");
        }
        if (Objects.equals(chairmanPollOperateReq.getPollStatus(), PollOperateTypeDto.START)) {
            if (Strings.isBlank(chairmanId)) {
                throw new CustomException("请先设置主席");
            }
            //锁定视频源
            ParticipantStatus participantStatus=new ParticipantStatus();
            participantStatus.setVideoSwitchAttribute("CUSTOMIZED");
            smcParticipantsService.changeParticipantStatusOnly(conferenceId,chairmanId,participantStatus);
            //取消选看
            try {
                String cId = SmcConferenceContextCache.getInstance().getChooseParticipantMap().get(conferenceId);
                ConferenceSMCService conferenceSMCService = BeanFactory.getBean(ConferenceSMCService.class);
                conferenceSMCService.cancelChoose(conferenceId, cId, true);
                String spokesmanId = detailConference.getConferenceState().getSpokesmanId();
                conferenceSMCService.cancelCallTheRoll(conferenceId, spokesmanId, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            setMute(conferenceId, true);
            //取消广播
            ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
            conferenceStatusRequest.setBroadcaster(chairmanId);
            statusControl(conferenceId, conferenceStatusRequest);
            SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.CHOOSE_LIST, new ArrayList<>());
            Map<String, Thread> chairmanPollingThread = SmcConferenceContextCache.getInstance().getChairmanPollingThread();
            Thread thread = chairmanPollingThread.get(conferenceId);
            if (thread == null || !thread.isAlive()) {
                createThread(conferenceId, chairmanId);
            } else {
                if (!thread.isInterrupted()) {
                    thread.start();
                } else {
                    createThread(conferenceId, chairmanId);
                }
            }

        }
        if (Objects.equals(chairmanPollOperateReq.getPollStatus(), PollOperateTypeDto.STOP) || Objects.equals(chairmanPollOperateReq.getPollStatus(), PollOperateTypeDto.CANCEL)) {

            Map<String, Thread> chairmanPollingThread = SmcConferenceContextCache.getInstance().getChairmanPollingThread();
            Thread thread = chairmanPollingThread.get(conferenceId);
            if (thread != null) {
                ChairManPollingThread chairManPollingThread = (ChairManPollingThread) thread;
                chairManPollingThread.flag = false;
            }

            ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
            conferenceStatusRequest.setBroadcaster(chairmanId);
            statusControl(conferenceId, conferenceStatusRequest);

            if (Objects.equals(chairmanPollOperateReq.getPollStatus(), PollOperateTypeDto.CANCEL)) {
                SmcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceId, SmcWebsocketMessageType.POLLING_LIST, new ArrayList<>());
                 MultiPicInfoReq.MultiPicInfoDTO multiPicInfoD=new MultiPicInfoReq.MultiPicInfoDTO();
                multiPicInfoD.setPicNum(1);
                multiPicInfoD.setMode(1);
                List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList=new ArrayList<>();
                MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO = new MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO();
                subPicListDTO.setStreamNumber(0);
                subPicListDTO.setParticipantId("00000000-0000-0000-0000-000000000000");
                subPicList.add(subPicListDTO);
                multiPicInfoD.setSubPicList(subPicList);
                conferencesControlChoose(conferenceId,chairmanId,multiPicInfoD);
            }

        }

        try {
            SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
            Asserts.notNull(bridge, "会议桥未找到");
            bridge.getSmcMultiPicPollInvoker().chairmanParticipantMultiPicPollOperate(conferenceId, chairmanPollOperateReq, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //解锁视频源
        ParticipantStatus participantStatus=new ParticipantStatus();
        participantStatus.setVideoSwitchAttribute("AUTO");
        smcParticipantsService.changeParticipantStatusOnly(conferenceId,chairmanId,participantStatus);
    }

    @Override
    public void multiPicBroad(String conferenceId, boolean enable) {
        SmcBridge bridge = getSmcBridge(conferenceId, SmcBridgeCache.getInstance().getConferenceBridge());
        Asserts.notNull(bridge, "会议桥未找到");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("broadcaster", "");
        if (enable) {
            jsonObject.put("broadcaster", "00000000-0000-0000-0000-000000000000");
        }
        bridge.getSmcConferencesInvoker().conferencesStatusControl(conferenceId, jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }


    private synchronized void createThread(String conferenceId, String chairmanId) {
        Map<String, Thread> chairmanPollingThread = SmcConferenceContextCache.getInstance().getChairmanPollingThread();
        Thread thread = chairmanPollingThread.get(conferenceId);
        if (thread != null) {
            ChairManPollingThread chairManPollingThread = (ChairManPollingThread) thread;
            chairManPollingThread.flag = false;
        }
        ChairManPollingThread chairManPollingThread = new ChairManPollingThread(conferenceId, chairmanId, this);
        chairmanPollingThread.put(conferenceId, chairManPollingThread);
        chairManPollingThread.start();

    }

}
