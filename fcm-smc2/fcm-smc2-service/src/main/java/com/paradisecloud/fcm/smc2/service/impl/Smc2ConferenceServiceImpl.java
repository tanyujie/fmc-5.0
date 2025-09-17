package com.paradisecloud.fcm.smc2.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.*;
import com.paradisecloud.com.fcm.smc.modle.request.*;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.enumer.FmeBridgeProcessingStrategy;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.cache.Smc2WebsocketMessageType;
import com.paradisecloud.fcm.smc2.conference.templateconference.StartTemplateConference;
import com.paradisecloud.fcm.dao.mapper.BusiSmc2AppointmentConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiSmc2HistoryConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2ConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiSmc2AppointmentConference;
import com.paradisecloud.fcm.dao.model.BusiSmc2DeptTemplate;
import com.paradisecloud.fcm.dao.model.BusiSmc2HistoryConference;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.TerminalAttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.fcm.smc2.model.layout.ChairManSmc2PollingThread;
import com.paradisecloud.fcm.smc2.model.layout.ContinuousPresenceModeEnum;
import com.paradisecloud.fcm.smc2.service.IBusiSmc2AppointmentConferenceService;
import com.paradisecloud.fcm.smc2.service.IBusiSmc2DeptTemplateService;
import com.paradisecloud.fcm.smc2.service.IBusiSmc2HistoryConferenceService;
import com.paradisecloud.fcm.smc2.service.Smc2ConferenceService;
import com.paradisecloud.fcm.smc2.utils.AesEnsUtils;
import com.paradisecloud.fcm.smc2.utils.Smc2ConferenceContextUtils;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.service.ISysDeptService;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.enumer.DateTimeFormatPattern;
import com.sinhy.spring.BeanFactory;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.common.State;
import com.suntek.smc.esdk.pojo.local.*;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.Duration;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author nj
 * @date 2023/4/20 17:20
 */
@Service
public class Smc2ConferenceServiceImpl implements Smc2ConferenceService {

    public static final String SMC_2 = "会议监控smc2";
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Resource
    private IBusiSmc2DeptTemplateService iBusiSmc2DeptTemplateService;

    @Resource
    private IBusiSmc2AppointmentConferenceService busiSmc2AppointmentConferenceService;


    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;

    @Resource
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;
    @Resource
    private ISysDeptService sysDeptService;

    @Override
    public Smc2ConferenceContext startTemplateConference(long templateConferenceId) {
        return new StartTemplateConference().startTemplateConference(templateConferenceId);
    }

    @Override
    public void endConference(String encryptConferenceId, int endType, int endReasonsType) {

        AtomicInteger successCount = new AtomicInteger();
        Smc2ConferenceContext cc = Smc2ConferenceContextCache.getInstance().get(encryptConferenceId);
        endConference(cc, successCount, endReasonsType);

    }

    @Override
    public void lockConference(String conferenceId) {
        throw new CustomException("不支持");
    }

    @Override
    public void unlockConference(String conferenceId) {
        throw new CustomException("不支持");
    }

    @Override
    public void recallAll(String conferenceId) {
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        if (smc2ConferenceContext == null || smc2ConferenceContext.isEnd()) {
            return;
        }
        String confId = smc2ConferenceContext.getConference().getConfId();
        List<String> list = new ArrayList<String>();
        List<SmcParitipantsStateRep.ContentDTO> content = smc2ConferenceContext.getContent();
        if (!CollectionUtils.isEmpty(content)) {
            for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {
                if (!contentDTO.getState().getOnline()) {
                    list.add(contentDTO.getGeneralParam().getUri());
                }
            }
        }
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer resultCode = conferenceServiceEx.connectSitesEx(confId, list);
    }

    @Override
    public void setMuteAll(String conferenceId, boolean b) {
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        if (conferenceContext == null || conferenceContext.isEnd()) {
            return;
        }

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
        if (b) {
            isMute = 1;
        }
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer resultCode = conferenceServiceEx.setSitesMuteEx(confId, list, isMute);
    }

    @Override
    public void setQuietAll(String conferenceId, boolean enable) {
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        if (conferenceContext == null || conferenceContext.isEnd()) {
            return;
        }

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
        // 0：不静音 1：静音
        int isQuiet = 0;
        if (enable) {
            isQuiet = 1;
        }
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer resultCode = conferenceServiceEx.setSitesQuietEx(confId, list, isQuiet);

    }

    @Override
    public void extendTime(ExtendTimeReq extendTimeReq) {
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(extendTimeReq.getConferenceId());
        if (conferenceContext == null || conferenceContext.isEnd()) {
            return;
        }
        //会议开始时间为8小时后，如果需要延长周期性会议中的单个会议时长，才需要该参数。
        Date date = conferenceContext.getStartTime();
        String confId = conferenceContext.getConference().getConfId();
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        try {
            Duration prolongTime = javax.xml.datatype.DatatypeFactory.newInstance().newDuration(1000 * 60 * extendTimeReq.getExtendTime());
            //如果返回值为0，则表示延长成功，否则表示延长失败，具体失败原因请参考错误码列表。
            Integer resultCode = conferenceServiceEx.prolongScheduledConfEx(confId, date, prolongTime);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void textTipsSetting(TextTipsSetting textTipsSetting) {
        String conferenceId = textTipsSetting.getConferenceId();
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        if (conferenceContext == null || conferenceContext.isEnd()) {
            return;
        }
        WSConfTextParamEx value = new WSConfTextParamEx();
        String confId = conferenceContext.getConference().getConfId();
        List<String> siteUriList = new ArrayList<>();
        if (!Objects.equals(TxtOperationTypeEnumDto.SAVE.name(), textTipsSetting.getOpType())) {
            List<SmcParitipantsStateRep.ContentDTO> content = conferenceContext.getContent();
            if (!CollectionUtils.isEmpty(content)) {
                for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {
                    if (contentDTO.getState().getOnline()) {
                        siteUriList.add(contentDTO.getGeneralParam().getUri());
                    }
                }
            }

        }

        value.setSiteUriList(siteUriList);
        value.setContent(textTipsSetting.getContent());
        int disPosition = textTipsSetting.getDisPosition();
        int displayType = textTipsSetting.getDisplayType();
        String opType = textTipsSetting.getOpType();
        if (Objects.equals(TxtOperationTypeEnumDto.SET.name(), opType)) {
            //设置生效
            value.setOpType(0);
        }
        if (Objects.equals(TxtOperationTypeEnumDto.SAVE.name(), opType)) {
            value.setOpType(0);
            value.setSiteUriList(null);
        }
        if (Objects.equals(TxtOperationTypeEnumDto.CANCEL.name(), opType)) {
            value.setOpType(1);
        }
        value.setDisPos(disPosition);
        if (displayType == 3) {
            value.setDisType(1);

        } else if (displayType == 4) {
            value.setDisType(2);
        } else {
            value.setDisType(3);
        }
        String type = textTipsSetting.getType();
        if (Objects.equals(TxtTypeEnum.BANNER.name(), type)) {
            value.setTextType(0);
            value.setDisPos(null);
        }

        if (Objects.equals(TxtTypeEnum.CAPTION.name(), type) || Objects.equals(TxtTypeEnum.BOTTOMCAP.name(), type)) {
            value.setTextType(1);
        }
        if (Objects.equals(TxtTypeEnum.SHORTMSG.name(), type)) {
            value.setTextType(2);
        }

        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        int resultCode = conferenceServiceEx.setConfTextCtrlEx(confId, value);
        if (resultCode == 1345323080) {
            throw new CustomException("当前显示位置不支持该显示效果");
        }
        if (resultCode == 1347440727) {
            throw new CustomException("指定的会场为SIP会场");
        }
        if (resultCode != 0) {
            throw new CustomException("当前显示错误：" + resultCode);
        }

    }

    @Override
    public void lockPresenter(String conferenceId, Boolean lock) {
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        if (conferenceContext == null || conferenceContext.isEnd()) {
            return;
        }
        String confId = conferenceContext.getConference().getConfId();
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        if (lock) {
            //查询会场状态
            DetailConference detailConference = conferencesDetailInfo(conferenceId);
            String presenterId = detailConference.getConferenceState().getPresenterId();
            if (Strings.isBlank(presenterId)) {
                throw new CustomException("操作失败:没有可锁定的会场" );
            }
            SmcParitipantsStateRep.ContentDTO participant = conferenceContext.getParticipant(presenterId);
            Integer resultCode = conferenceServiceEx.lockPresentationEx(confId, participant.getGeneralParam().getUri());
            if (resultCode != 0) {
                log.error("锁定会议材料令牌失败" + resultCode);
                throw new CustomException("操作失败" + resultCode);
            }
        } else {
            Integer resultCode = conferenceServiceEx.unlockPresentationEx(confId);
            if (resultCode != 0) {
                log.error("解锁会议材料令牌失败" + resultCode);
                throw new CustomException("操作失败" + resultCode);
            }
        }

    }

    @Override
    public DetailConference conferencesDetailInfo(String conferenceId) {
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        if (smc2ConferenceContext == null) {
            return null;
        }

        DetailConference detailConference = smc2ConferenceContext.getDetailConference();

        String confId = smc2ConferenceContext.getConference().getConfId();
        List<String> list = new ArrayList<>();
        list.add(confId);
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        TPSDKResponseEx<List<ConferenceStatusEx>> result = conferenceServiceEx.queryConferencesStatusEx(list);
        Integer resultCode = result.getResultCode();
        if (0 == resultCode) {
            //查询成功，则返回一个List<ConferenceStatusEx>对象
            List<ConferenceStatusEx> status = result.getResult();
            for (ConferenceStatusEx conferenceStatusEx : status) {

                String id1 = conferenceStatusEx.getId();
                if (Objects.equals(confId, id1)) {
                    ConferenceState conferenceState = detailConference.getConferenceState();
                    conferenceState.setChairmanId(smc2ConferenceContext.getParticiPantIdBySiteUri(conferenceStatusEx.getChair()));
                    conferenceState.setSpokesmanId(smc2ConferenceContext.getParticiPantIdBySiteUri(conferenceStatusEx.getSpeaking()));
                    conferenceState.setBroadcastId(smc2ConferenceContext.getParticiPantIdBySiteUri(conferenceStatusEx.getBroadcast()));
                    conferenceState.setLockPresenterId(smc2ConferenceContext.getParticiPantIdBySiteUri(conferenceStatusEx.getLockPresentation()));
                    conferenceState.setPresenterId(smc2ConferenceContext.getParticiPantIdBySiteUri(conferenceStatusEx.getPresentation()));
                    conferenceState.setLock(conferenceStatusEx.getIsLock() != null && (conferenceStatusEx.getIsLock() == 1));
                    Integer status1 = conferenceStatusEx.getStatus();
                    if (status1 == 3) {
                        smc2ConferenceContext.getConference().setStage("ONLINE");
                    }
                    if (status1 == 5) {
                        smc2ConferenceContext.getConference().setStage("CANCEL");
                    }
                    if (status1 == 2) {
                        smc2ConferenceContext.getConference().setStage("OFFLINE");
                    }
                    ConferenceUiParam conferenceUiParam = detailConference.getConferenceUiParam();
                    String scheduleStartTime = conferenceUiParam.getScheduleStartTime();
                    Date date = new Date();
                    Date startDate = DateUtil.convertDateByString(scheduleStartTime, null);
                    long time = startDate.getTime();

                    conferenceUiParam.setStartedTime(Integer.valueOf((date.getTime() - time) + ""));

                }
            }
        }
        detailConference.setMonitorNumber(smc2ConferenceContext.getMonitorNumber());
        return detailConference;

    }

    @Override
    public Object count() {
        int running = 0;
        int coming = 0;
        int today = 0;
        int tomorrow = 0;

        HashMap<String, Object> obj = new HashMap<>(4);
        obj.put("running", running);
        obj.put("coming", coming);
        obj.put("today", today);
        obj.put("tomorrow", tomorrow);

        return obj;
    }

    /**
     * 设置多画面参数时，不支持多画面模式设置为0。
     *
     * @param multiPicInfoReq
     */
    @Override
    public synchronized void createMulitiPic(com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq multiPicInfoReq) {
        String conferenceId = multiPicInfoReq.getConferenceId();
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        String confId = smc2ConferenceContext.getConference().getConfId();
        com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq.MultiPicInfoDTO multiPicInfo = multiPicInfoReq.getMultiPicInfo();
        String target = "(%CP)";
        Integer picNum = multiPicInfo.getPicNum();
        Integer mode = multiPicInfo.getMode();
        int presenceMode = ContinuousPresenceModeEnum.getModelValue(picNum, mode);
        if (presenceMode == -1) {
            throw new CustomException("多画面设置失败:不支持该" + picNum + "画面");
        }
        List<String> subPics = new ArrayList<>();
        List<com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfoReq.getMultiPicInfo().getSubPicList();
        if (!CollectionUtils.isEmpty(subPicList)) {
            for (com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                String participantId = subPicListDTO.getParticipantId();
                SmcParitipantsStateRep.ContentDTO participant = smc2ConferenceContext.getParticipant(participantId);
                if (participant != null) {
                    subPics.add(participant.getGeneralParam().getUri());
                }
            }
        }
        ConferenceServiceEx conferenceServiceEx = smc2ConferenceContext.getSmc2Bridge().getConferenceServiceEx();
        Integer resultCode = conferenceServiceEx.setContinuousPresenceEx(confId, target, presenceMode, subPics);
        if (resultCode != 0) {
            throw new CustomException("多画面设置失败:" + resultCode);
        } else {
            smc2ConferenceContext.setMultiPicInfoReq(multiPicInfoReq);
            com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq.MultiPicInfoDTO source = multiPicInfoReq.getMultiPicInfo();
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

    @Override
    public void conferencesControlChoose(String conferenceId, String participantId, MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO) {
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        String confId = smc2ConferenceContext.getConference().getConfId();
        SmcParitipantsStateRep.ContentDTO participant = smc2ConferenceContext.getParticipant(participantId);
        if (participant == null) {
            throw new CustomException("未找到与会者");
        }
        ConferenceServiceEx conferenceServiceEx = smc2ConferenceContext.getSmc2Bridge().getConferenceServiceEx();
        String uri = participant.getGeneralParam().getUri();
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
                SmcParitipantsStateRep.ContentDTO m_participant = smc2ConferenceContext.getParticipant(m_participantId);
                if (m_participant != null) {
                    subPics.add(m_participant.getGeneralParam().getUri());
                }
            }

        }


        Integer resultCode = 0;
        if (!CollectionUtils.isEmpty(subPics) && subPics.size() == 1) {
            String videoSourceUri = subPics.get(0);
            resultCode = conferenceServiceEx.setVideoSourceEx(confId, uri, videoSourceUri, 0);
        } else if (CollectionUtils.isEmpty(subPics)) {
            //观看多画面
//            if (!CollectionUtils.isEmpty(subPicList) && subPicList.size() == 1) {
//                MultiPicInfoReq multiPicInfoReq = smc2ConferenceContext.getMultiPicInfoReq();
//                if (multiPicInfoReq != null) {
//                    MultiPicInfoReq.MultiPicInfoDTO multiPicInfo = multiPicInfoReq.getMultiPicInfo();
//                    presenceMode = ContinuousPresenceModeEnum.getModelValue(multiPicInfo.getPicNum(), multiPicInfo.getMode());
//                    List<String> subPicsMode = new ArrayList<>();
//                    subPicList = multiPicInfoReq.getMultiPicInfo().getSubPicList();
//                    if (!CollectionUtils.isEmpty(subPicList)) {
//                        for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
//                            String id = subPicListDTO.getParticipantId();
//                            SmcParitipantsStateRep.ContentDTO participantDto = smc2ConferenceContext.getParticipant(id);
//                            if (participantDto != null) {
//                                subPicsMode.add(participantDto.getGeneralParam().getUri());
//                            }
//                        }
//                    }
//                    conferenceServiceEx.setContinuousPresenceEx(confId, target, presenceMode, subPicsMode);
//                }
//            }
//            String videoSourceUri = "";
//            resultCode = conferenceServiceEx.setVideoSourceEx(confId, uri, videoSourceUri, 0);
            //选看多画面实现方式二
            ConferenceState conferenceState = smc2ConferenceContext.getDetailConference().getConferenceState();
            String chairmanPollStatus = conferenceState.getChairmanPollStatus();
            String multiPicPollStatus = conferenceState.getMultiPicPollStatus();
            String broadcastPollStatus = conferenceState.getBroadcastPollStatus();
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
                item2.setSiteUri(participant.getGeneralParam().getUri());
                wsCtrlSiteCommParams2.add(item2);
                conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams2);
                if (Objects.equals(broadcastPollStatus, "CANCEL")) {
                    conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 0);
                    conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 1);
                }
                if (Objects.equals(broadcastPollStatus, "START")) {
                    conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 0);
                }
                //锁定当前
                List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams3 = new ArrayList<>();
                WSCtrlSiteCommParamEx item3 = new WSCtrlSiteCommParamEx();
                item3.setOperaTypeParam(1);
                item3.setSiteUri(participant.getGeneralParam().getUri());
                wsCtrlSiteCommParams3.add(item3);
                conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams3);
                //点名
                String spokesmanId = conferenceState.getSpokesmanId();
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
        if (!("会议监控smc2").equals(participant.getGeneralParam().getName())) {
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
    public void chairmanParticipantMultiPicPoll(MultiPicPollRequest multiPicPollRequest) {
        Integer interval = multiPicPollRequest.getInterval();
        String conferenceId = multiPicPollRequest.getConferenceId();
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        String confId = smc2ConferenceContext.getConference().getConfId();
        String chairmanId = smc2ConferenceContext.getDetailConference().getConferenceState().getChairmanId();
        if (Strings.isBlank(chairmanId)) {
            throw new CustomException("请先设置主席");
        }
        SmcParitipantsStateRep.ContentDTO participant = smc2ConferenceContext.getParticipant(chairmanId);

        String uri = participant.getGeneralParam().getUri();
        String target = uri + "(%CP)";
        Integer picNum = multiPicPollRequest.getPicNum();
        Integer mode = multiPicPollRequest.getMode();
        int presenceMode = ContinuousPresenceModeEnum.getModelValue(picNum, mode);
        if (presenceMode == -1) {
            throw new CustomException("多画面设置失败:不支持该" + picNum + "画面");
        }
        List<List<String>> subPics = new ArrayList<>();
        interval = subPicsSetting(multiPicPollRequest, smc2ConferenceContext, picNum, subPics);
        ConferenceServiceEx conferenceServiceEx = smc2ConferenceContext.getSmc2Bridge().getConferenceServiceEx();
        int result = conferenceServiceEx.setContinuousPresencePollingEx(confId, target, presenceMode, subPics, interval, -1);
        if (result != 0) {
            throw new CustomException("主席轮询设置失败");
        } else {
            smc2ConferenceContext.setChairmanMultiPicPollRequest(multiPicPollRequest);
        }
    }

    @Override
    public MultiPicPollRequest chairmanParticipantMultiPicPollQuery(String conferenceId, String participantId) {
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        if (smc2ConferenceContext == null) {
            throw new CustomException("会议不存在");
        }
        return smc2ConferenceContext.getChairmanMultiPicPollRequest();
    }

    /**
     * THREAD_START
     * THREAD_SUPEND
     * ThREAD_STOP
     *
     * @param chairmanPollOperateReq
     */
    @Override
    public void chairmanParticipantMultiPicPollOperate(ChairmanPollOperateReq chairmanPollOperateReq) {
        String conferenceId = chairmanPollOperateReq.getConferenceId();
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        if (smc2ConferenceContext == null) {
            throw new CustomException("会议不存在");
        }
        MultiPicPollRequest multiPicPollRequest = smc2ConferenceContext.getChairmanMultiPicPollRequest();
        if (multiPicPollRequest == null) {
            throw new CustomException("请先设置 主席轮询");
        }
        String target = "(%CP)";
        List<List<String>> subPics = new ArrayList<>();
        Integer picNum = multiPicPollRequest.getPicNum();
        Integer mode = multiPicPollRequest.getMode();
        int presenceMode = ContinuousPresenceModeEnum.getModelValue(picNum, mode);
        if (presenceMode == -1) {
            throw new CustomException("多画面设置失败:不支持该" + picNum + "画面");
        }

        String confId = smc2ConferenceContext.getConference().getConfId();
        PollOperateTypeDto pollStatus = chairmanPollOperateReq.getPollStatus();
        ParticipantRspDto chairman = smc2ConferenceContext.getDetailConference().getConferenceState().getChairman();

        ConferenceServiceEx conferenceServiceEx = smc2ConferenceContext.getSmc2Bridge().getConferenceServiceEx();
        ConferenceState conferenceState = smc2ConferenceContext.getDetailConference().getConferenceState();
        JSONObject jsonObject = new JSONObject();
        int result = 0;
        switch (pollStatus) {

            case START:
                Integer interval = subPicsSetting(multiPicPollRequest, smc2ConferenceContext, multiPicPollRequest.getPicNum(), subPics);
                AttendeeOperation attendeeOperation = smc2ConferenceContext.getAttendeeOperation();
                if (!(attendeeOperation instanceof ChangeMasterAttendeeOperation)) {
                    attendeeOperation.cancel();
                }
                Integer resultCode = conferenceServiceEx.setBroadcastSiteEx(confId, chairman.getUri(), 0);
                if (resultCode != 0) {
                    log.error(chairman.getName() + "广播主席失败：" + resultCode);
                }
                //轮询设置 主席选看多画面
                ChairManSmc2PollingThread chairManSmc2PollingThread = smc2ConferenceContext.getChairManSmc2PollingThread();
                if (chairManSmc2PollingThread != null) {
                    chairManSmc2PollingThread.starton();
                } else {
                    chairManSmc2PollingThread = new ChairManSmc2PollingThread(interval, subPics, confId, target, presenceMode, chairman.getUri(), conferenceServiceEx, smc2ConferenceContext);
                    chairManSmc2PollingThread.start();
                    smc2ConferenceContext.setChairManSmc2PollingThread(chairManSmc2PollingThread);
                }
                //
                conferenceState.setChairmanPollStatus("START");
                conferenceState.setChooseId("");
                jsonObject = new JSONObject();
                jsonObject.put("state", conferenceState);
//                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(smc2ConferenceContext, Smc2WebsocketMessageType.CONFERENCE_CHANGED, jsonObject);
//                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(smc2ConferenceContext, Smc2WebsocketMessageType.CHOOSE_LIST, new ArrayList<>());
//                Smc2WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(smc2ConferenceContext, Smc2WebsocketMessageType.POLLING_LIST, new ArrayList<>());
                break;
            case CANCEL:
                chairManSmc2PollingThread = smc2ConferenceContext.getChairManSmc2PollingThread();
                if (chairManSmc2PollingThread != null) {
                    chairManSmc2PollingThread.stops();
                }
                smc2ConferenceContext.setChairManSmc2PollingThread(null);
                conferenceState.setChairmanPollStatus("CANCEL");
                conferenceState.setChooseId("");
                jsonObject.put("state", conferenceState);
//                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(smc2ConferenceContext, Smc2WebsocketMessageType.CONFERENCE_CHANGED, jsonObject);
//                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(smc2ConferenceContext, Smc2WebsocketMessageType.CHOOSE_LIST, new ArrayList<>());
//                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(smc2ConferenceContext, Smc2WebsocketMessageType.POLLING_LIST, new ArrayList<>());
                break;
            case STOP:
                chairManSmc2PollingThread = smc2ConferenceContext.getChairManSmc2PollingThread();
                if (chairManSmc2PollingThread != null) {
                    chairManSmc2PollingThread.supend();
                }
                conferenceState.setChairmanPollStatus("STOP");
                jsonObject.put("state", conferenceState);
               // Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(smc2ConferenceContext, Smc2WebsocketMessageType.CONFERENCE_CHANGED, jsonObject);
                log.info("主席轮询广播" + State.ThREAD_STOP);
                break;
        }


        if (result != 0) {
            throw new CustomException("轮询操作失败");
        }
    }

    @Override
    public Object queryMulitiPicPoll(String conferenceId) {
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        if (smc2ConferenceContext == null) {
            throw new CustomException("会议不存在");
        }
        return smc2ConferenceContext.getMultiPicPollRequest();
    }

    @Override
    public void setMultiPicPoll(MultiPicPollRequest multiPicPollRequest) {
        Integer interval = multiPicPollRequest.getInterval();
        String conferenceId = multiPicPollRequest.getConferenceId();
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        String confId = smc2ConferenceContext.getConference().getConfId();
        String target = "(%CP)";
        Integer picNum = multiPicPollRequest.getPicNum();
        Integer mode = multiPicPollRequest.getMode();
        int presenceMode = ContinuousPresenceModeEnum.getModelValue(picNum, mode);
        if (presenceMode == -1) {
            throw new CustomException("多画面设置失败:不支持该" + picNum + "画面");
        }
        List<List<String>> subPics = new ArrayList<>();

        interval = subPicsSetting(multiPicPollRequest, smc2ConferenceContext, picNum, subPics);

        ConferenceServiceEx conferenceServiceEx = smc2ConferenceContext.getSmc2Bridge().getConferenceServiceEx();
        int result = conferenceServiceEx.setContinuousPresencePollingEx(confId, target, presenceMode, subPics, interval, -1);
        if (result != 0) {
            throw new CustomException("多画面轮询设置失败");
        } else {
            smc2ConferenceContext.setMultiPicPollRequest(multiPicPollRequest);

        }
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
        if(!attendeeById.isMeetingJoined()){
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
    public void stopMultiPicPoll(String conferenceId) {
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        if (smc2ConferenceContext == null) {
            throw new CustomException("会议不存在");
        }
        MultiPicPollRequest multiPicPollRequest = smc2ConferenceContext.getMultiPicPollRequest();
        if (multiPicPollRequest == null) {
            throw new CustomException("请先设置 主席轮询");
        }
        String confId = smc2ConferenceContext.getConference().getConfId();


        ConferenceServiceEx conferenceServiceEx = smc2ConferenceContext.getSmc2Bridge().getConferenceServiceEx();
        State state = State.THREAD_SUPEND;
        int result = conferenceServiceEx.setContinuousPresencePollingStateEx(confId, state);
        if (result != 0) {
            throw new CustomException("轮询操作失败");
        } else {
            ConferenceState conferenceState = smc2ConferenceContext.getDetailConference().getConferenceState();
            conferenceState.setMultiPicPollStatus("STOP");
            Integer integer = conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 1);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("state", conferenceState);
           // Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(smc2ConferenceContext, Smc2WebsocketMessageType.CONFERENCE_CHANGED, jsonObject);
        }
    }

    @Override
    public void cancelMultiPicPoll(String conferenceId) {
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        if (smc2ConferenceContext == null) {
            throw new CustomException("会议不存在");
        }
        MultiPicPollRequest multiPicPollRequest = smc2ConferenceContext.getMultiPicPollRequest();
        if (multiPicPollRequest == null) {
            throw new CustomException("请先设置 多画面轮询");
        }
        String confId = smc2ConferenceContext.getConference().getConfId();


        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        State state = State.ThREAD_STOP;
        int result = conferenceServiceEx.setContinuousPresencePollingStateEx(confId, state);

        if (result != 0) {
            throw new CustomException("轮询操作失败");
        } else {

            ConferenceState conferenceState = smc2ConferenceContext.getDetailConference().getConferenceState();
            //广播主席
            String chairmanId = smc2ConferenceContext.getDetailConference().getConferenceState().getChairmanId();
            if (Strings.isNotBlank(chairmanId)) {
                SmcParitipantsStateRep.ContentDTO participant = smc2ConferenceContext.getParticipant(chairmanId);
                if (participant != null) {
                    conferenceServiceEx.setBroadcastSiteEx(confId, participant.getGeneralParam().getUri(), 0);
                }
            }
            conferenceState.setBroadcastId("");
            conferenceState.setChooseId("");
           // Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, Smc2WebsocketMessageType.CHOOSE_LIST, new ArrayList<>());


            conferenceState.setMultiPicPollStatus("CANCEL");
            //Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, Smc2WebsocketMessageType.BROAD_LIST, new ArrayList<>());
            Integer integer = conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 1);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("state", conferenceState);
           // Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(smc2ConferenceContext, Smc2WebsocketMessageType.CONFERENCE_CHANGED, jsonObject);
        }

    }

    @Override
    public void startMultiPicPoll(String conferenceId) {

        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        if (smc2ConferenceContext == null) {
            throw new CustomException("会议不存在");
        }
        MultiPicPollRequest multiPicPollRequest = smc2ConferenceContext.getMultiPicPollRequest();
        if (multiPicPollRequest == null) {
            throw new CustomException("请先设置轮询");
        }
        setMultiPicPoll(multiPicPollRequest);
        String confId = smc2ConferenceContext.getConference().getConfId();

       // Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, Smc2WebsocketMessageType.CHOOSE_LIST, new ArrayList<>());

        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        State state = State.THREAD_START;
        int result = conferenceServiceEx.setContinuousPresencePollingStateEx(confId, state);
        if (result != 0) {
            throw new CustomException("轮询操作失败");
        } else {
            AttendeeOperation attendeeOperation = smc2ConferenceContext.getAttendeeOperation();
            if (!(attendeeOperation instanceof ChangeMasterAttendeeOperation)) {
                attendeeOperation.cancel();
            }
            //广播多画面
            Integer integer = conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 0);
            ConferenceState conferenceState = smc2ConferenceContext.getDetailConference().getConferenceState();
            conferenceState.setMultiPicPollStatus("START");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("state", conferenceState);
            conferenceState.setSpokesmanId("");
            conferenceState.setChooseId("");
           // Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(smc2ConferenceContext, Smc2WebsocketMessageType.CONFERENCE_CHANGED, jsonObject);
            try {
                //停止主席轮询
                String chairmanPollStatus = smc2ConferenceContext.getDetailConference().getConferenceState().getChairmanPollStatus();
                if (!Objects.equals(chairmanPollStatus, PollOperateTypeDto.CANCEL.name())) {
                    ChairmanPollOperateReq chairmanPollOperateReq = new ChairmanPollOperateReq();
                    chairmanPollOperateReq.setConferenceId(conferenceId);
                    chairmanPollOperateReq.setPollStatus(PollOperateTypeDto.CANCEL);
                    chairmanParticipantMultiPicPollOperate(chairmanPollOperateReq);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void multiPicBroad(String conferenceId, boolean enable) {
        Integer isBroadcast = enable == true ? 0 : 1;
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        if (smc2ConferenceContext == null) {
            throw new CustomException("会议不存在");
        }

        String confId = smc2ConferenceContext.getConference().getConfId();

        ConferenceServiceEx conferenceServiceEx = smc2ConferenceContext.getSmc2Bridge().getConferenceServiceEx();
        Integer resultCode = conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, isBroadcast);
        if (resultCode != 0) {
            throw new CustomException("多画面广播失败");
        }
        if (enable) {
            com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq multiPicInfoReq = smc2ConferenceContext.getMultiPicInfoReq();
            if (multiPicInfoReq == null) {
                throw new CustomException("多画面未设置");
            }
            AttendeeOperation attendeeOperation = smc2ConferenceContext.getAttendeeOperation();
            if (!(attendeeOperation instanceof ChangeMasterAttendeeOperation)) {
                attendeeOperation.cancel();
            }
           // Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, Smc2WebsocketMessageType.CHOOSE_LIST, new ArrayList<>());
            ConferenceState conferenceState = smc2ConferenceContext.getDetailConference().getConferenceState();
            conferenceState.setBroadcastId("");
            conferenceState.setChooseId("");
            conferenceState.setBroadcastPollStatus("START");
            try {
                //停止主席轮询
                try {
                    ChairmanPollOperateReq chairmanPollOperateReq = new ChairmanPollOperateReq();
                    chairmanPollOperateReq.setConferenceId(conferenceId);
                    chairmanPollOperateReq.setPollStatus(PollOperateTypeDto.CANCEL);
                    chairmanParticipantMultiPicPollOperate(chairmanPollOperateReq);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //停止多画面轮询
                try {
                    MultiPicPollRequest multiPicPollRequest = smc2ConferenceContext.getMultiPicPollRequest();
                    if (multiPicPollRequest != null) {
                        cancelMultiPicPoll(confId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("state", conferenceState);
          //  Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, Smc2WebsocketMessageType.CONFERENCE_CHANGED, jsonObject);
        } else {
            ConferenceState conferenceState = smc2ConferenceContext.getDetailConference().getConferenceState();
            conferenceState.setBroadcastPollStatus("CANCEL");
            //广播主席
            String chairmanId = smc2ConferenceContext.getDetailConference().getConferenceState().getChairmanId();
            if (Strings.isNotBlank(chairmanId)) {
                SmcParitipantsStateRep.ContentDTO participant = smc2ConferenceContext.getParticipant(chairmanId);
                if (participant != null) {
                    conferenceServiceEx.setBroadcastSiteEx(confId, participant.getGeneralParam().getUri(), 0);
                }
            }
            conferenceState.setSpokesmanId("");
            conferenceState.setChooseId("");
            conferenceState.setBroadcastId("");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("state", conferenceState);
           // Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, Smc2WebsocketMessageType.CHOOSE_LIST, new ArrayList<>());
           // Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(smc2ConferenceContext, Smc2WebsocketMessageType.CONFERENCE_CHANGED, jsonObject);
        }


    }

    @Override
    public void setVoiceActive(String conferenceId, Boolean enable) {

        Integer isSwitch = enable == true ? 1 : 0;
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        String confId = smc2ConferenceContext.getConference().getConfId();

        ConferenceServiceEx conferenceServiceEx = smc2ConferenceContext.getSmc2Bridge().getConferenceServiceEx();
        Integer resultCode = conferenceServiceEx.setAudioSwitchEx(confId, 50, isSwitch);
        if (resultCode != 0) {
            throw new CustomException("声控切换错误" + resultCode);
        }

    }

    @Override
    public void setMuteConf(String conferenceId, Boolean enable) {
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        if (conferenceContext == null || conferenceContext.isEnd()) {
            return;
        }

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
        if (enable) {
            isMute = 1;
        }
        ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
        Integer resultCode = conferenceServiceEx.setSitesMuteEx(confId, list, isMute);
        if (resultCode != 0) {
            throw new CustomException("全体静音错误：" + resultCode);
        }
    }

    @Override
    public Object conferenceStat() {
        Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
        int running = 0;
        JSONObject json = new JSONObject();

        List<BusiSmc2DeptTemplate> busiSmcDeptTemplateList = iBusiSmc2DeptTemplateService.queryTemplateListByDeptId(deptId);
        json.put("deptTemplateCount", busiSmcDeptTemplateList.size());
        BusiSmc2DeptTemplate busiSmc2DeptTemplate = new BusiSmc2DeptTemplate();
        busiSmc2DeptTemplate.setDeptId(deptId == null ? null : deptId.intValue());
        List<BusiSmc2DeptTemplate> busiSmc2Depts = iBusiSmc2DeptTemplateService.selectBusiSmc2DeptTemplateList(busiSmc2DeptTemplate);
        if (!CollectionUtils.isEmpty(busiSmc2Depts)) {
            for (BusiSmc2DeptTemplate busiSmc2Dept : busiSmc2Depts) {
                String confid = busiSmc2Dept.getConfid();
                if (confid != null) {
                    Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().encryptToHex(busiSmc2Dept.getConfid()));

                    if (smc2ConferenceContext != null) {
                        running++;
                    }

                }
            }
        }
        json.put("activeConferenceCount", running);
        BusiSmcAppointmentConferenceQuery query = new BusiSmcAppointmentConferenceQuery();
        if (deptId != null) {
            query.setDeptId(deptId);
        }
        List<BusiSmc2AppointmentConference> list = busiSmc2AppointmentConferenceService.selectBusiSmcAppointmentConferenceQuery(query);
        if (CollectionUtils.isEmpty(list)) {
            // 预约会议数
            json.put("appointConferenceCount", 0);
        } else {
            // 预约会议数
            json.put("appointConferenceCount", list.size());
        }


        return json;
    }

    @Override
    public Map<String, Object> reportConferenceOfIndex(Long deptId, String startTime, String endTime) {
        deptId = (deptId == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : deptId;

        if (Objects.nonNull(startTime)) {
            Assert.isTrue(DateTimeFormatPattern.matchFormatter(startTime) != null, "请传入正确的时间格式");
        }
        if (Objects.nonNull(endTime)) {
            Assert.isTrue(DateTimeFormatPattern.matchFormatter(endTime) != null, "请传入正确的时间格式");
        }

        List<BusiHistoryConference> historyConferences0 = busiHistoryConferenceMapper.reportByDept(null, null, startTime, endTime);
        List<BusiHistoryConference> historyConferences = new ArrayList<>();
        Set<Long> depts = SysDeptCache.getInstance().getSubordinateDeptIds(deptId);
        for (BusiHistoryConference busiHistoryConference : historyConferences0) {
            if (depts.contains(busiHistoryConference.getDeptId())) {
                historyConferences.add(busiHistoryConference);
            }
        }
        Map<String, Object> map = new HashMap<>();

        Integer sum = 0;
        Integer deviceNumSum = 0;
        if (!CollectionUtils.isEmpty(historyConferences)) {
            for (BusiHistoryConference historyConference : historyConferences) {
                int duration = (historyConference.getDuration() == null) ? 0 : historyConference.getDuration();
                int deviceNum = (historyConference.getDeviceNum() == null) ? 0 : historyConference.getDeviceNum();
                sum += duration;
                deviceNumSum += deviceNum;
            }
        }

        startTime = (StringUtils.hasText(startTime)) ? DateUtil.fillDateString(startTime, false) : null;
        endTime = (StringUtils.hasText(endTime)) ? DateUtil.fillDateString(endTime, true) : null;
        // 根据deptId查询与会者列表
        List<BusiHistoryParticipant> participantList = busiHistoryParticipantMapper.selectParticipantByDeptAndTime(deptId, startTime, endTime);

        int totalParticipantDuration = participantList.stream().mapToInt(BusiHistoryParticipant::getDurationSeconds).sum();

        map.put("conferenceCount", CollectionUtils.isEmpty(historyConferences) ? 0 : historyConferences.size());
        map.put("totalDuration", sum);
        map.put("totalDeviceNum", deviceNumSum);
        map.put("deviceNum", deviceNumSum);
        map.put("totalParticipantDuration", totalParticipantDuration);
        map.put("avgParticipantDuration", CollectionUtils.isEmpty(participantList) ? 0 : totalParticipantDuration / participantList.size());
        map.put("avgDuration", CollectionUtils.isEmpty(historyConferences) ? 0 : sum / historyConferences.size());
        map.put("avgDeviceNum", CollectionUtils.isEmpty(historyConferences) ? 0 : deviceNumSum / historyConferences.size());
        return map;
    }

    @Override
    public void order(ParticipantOrderRequest participantOrderRequest) {
        String conferenceId = participantOrderRequest.getConferenceId();
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        List<SmcParitipantsStateRep.ContentDTO> participantOrderList = smc2ConferenceContext.getParticipantOrderList();
        List<String> participantIdList = participantOrderRequest.getParticipantIdList();

        for (String participantId : participantIdList) {
            SmcParitipantsStateRep.ContentDTO participant = smc2ConferenceContext.getParticipant(participantId);
            if (participantOrderRequest.getSet()) {
                participantOrderList.remove(participant);
                participantOrderList.add(participant);
            } else {
                participantOrderList.remove(participant);
            }
        }
    }

    @Override
    public List<SmcParitipantsStateRep.ContentDTO> orderQuery(SmcConferenceRequest conferenceRequest) {
        String conferenceId = conferenceRequest.getConferenceId();
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);

        return smc2ConferenceContext.getParticipantOrderList();
    }

    @Override
    public void orderCancel(ParticipantOrderRequest participantOrderRequest) {
        String conferenceId = participantOrderRequest.getConferenceId();
        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        List<SmcParitipantsStateRep.ContentDTO> participantOrderList = smc2ConferenceContext.getParticipantOrderList();
        List<String> participantIdList = participantOrderRequest.getParticipantIdList();

        for (String participantId : participantIdList) {
            SmcParitipantsStateRep.ContentDTO participant = smc2ConferenceContext.getParticipant(participantId);
            participantOrderList.remove(participant);
        }
    }

    @Override
    public Object terminalStat() {
        JSONObject json = new JSONObject();
        Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
        if (deptId != null) {
            Set<Long> deptIds = SysDeptCache.getInstance().getSubordinateDeptIds(deptId);
            int total = 0;
            int onlineCount = 0;
            int meetingCount = 0;
            Collection<BusiTerminal> bts = TerminalCache.getInstance().values();
            for (Iterator<BusiTerminal> iterator = bts.iterator(); iterator.hasNext(); ) {
                BusiTerminal busiTerminal = iterator.next();
                if (deptIds.contains(busiTerminal.getDeptId())) {
                    total++;
                    if (TerminalOnlineStatus.convert(busiTerminal.getOnlineStatus()) == TerminalOnlineStatus.ONLINE) {
                        onlineCount++;
                    }
                }
            }

            Collection<Smc2ConferenceContext> cc = Smc2ConferenceContextCache.getInstance().values();
            for (Iterator<Smc2ConferenceContext> iterator0 = cc.iterator(); iterator0.hasNext(); ) {
                Smc2ConferenceContext conferenceContext = iterator0.next();
                AtomicInteger ai = new AtomicInteger();
                List<SmcParitipantsStateRep.ContentDTO> content = conferenceContext.getContent();
                for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {
                    Boolean online = contentDTO.getState().getOnline();
                    if (online) {
                        ai.incrementAndGet();
                    }
                }
                meetingCount += ai.get();
            }

            json.put("total", total);
            json.put("onlineCount", onlineCount);
            json.put("meetingCount", meetingCount);
        } else {
            int onlineCount = 0;
            int meetingCount = 0;
            Collection<BusiTerminal> bts = TerminalCache.getInstance().values();
            for (Iterator<BusiTerminal> iterator = bts.iterator(); iterator.hasNext(); ) {
                BusiTerminal busiTerminal = iterator.next();
                if (TerminalOnlineStatus.convert(busiTerminal.getOnlineStatus()) == TerminalOnlineStatus.ONLINE) {
                    onlineCount++;
                }
            }

            Collection<Smc2ConferenceContext> cc = Smc2ConferenceContextCache.getInstance().values();
            for (Iterator<Smc2ConferenceContext> iterator0 = cc.iterator(); iterator0.hasNext(); ) {
                Smc2ConferenceContext conferenceContext = iterator0.next();
                AtomicInteger ai = new AtomicInteger();
                List<SmcParitipantsStateRep.ContentDTO> content = conferenceContext.getContent();
                for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {
                    Boolean online = contentDTO.getState().getOnline();
                    if (online) {
                        ai.incrementAndGet();
                    }
                }
                meetingCount += ai.get();
            }

            json.put("total", TerminalCache.getInstance().size());
            json.put("onlineCount", onlineCount);
            json.put("meetingCount", meetingCount);
        }
        return json;
    }

    @Override
    public void chooseMultiPicManly(String conferenceId, String participantId, MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO) {

        Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(conferenceId);
        ConferenceServiceEx conferenceServiceEx = smc2ConferenceContext.getSmc2Bridge().getConferenceServiceEx();
        ConferenceState conferenceState = smc2ConferenceContext.getDetailConference().getConferenceState();
        String chairmanPollStatus = conferenceState.getChairmanPollStatus();
        String multiPicPollStatus = conferenceState.getMultiPicPollStatus();
        String broadcastPollStatus = conferenceState.getBroadcastPollStatus();
        String broadcastId = conferenceState.getBroadcastId();

        SmcParitipantsStateRep.ContentDTO participant = smc2ConferenceContext.getParticipant(participantId);
        com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq multiPicInfoReq = smc2ConferenceContext.getMultiPicInfoReq();
        String confId = smc2ConferenceContext.getConference().getConfId();
        if (participant == null) {
            throw new CustomException("未找到与会者");
        }
//        if(Objects.equals("00000000-0000-0000-0000-000000000000",broadcastId)){
//            throw new CustomException("会议正在广播不能设置");
//        }
//        if(Objects.equals(chairmanPollStatus,"START")||Objects.equals(multiPicPollStatus,"START")){
//
//            if(!Objects.equals("会议监控smc2",participant.getGeneralParam().getName())){
//                throw new CustomException("会议正在轮询不能设置");
//            }
//
//        }

        if (Objects.equals(chairmanPollStatus, "START")) {
            //锁定主席
            ParticipantRspDto chairman = conferenceState.getChairman();
            String chairmanUri = chairman.getUri();

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
        // 解锁当前
        List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams2 = new ArrayList<>();
        WSCtrlSiteCommParamEx item2 = new WSCtrlSiteCommParamEx();
        item2.setOperaTypeParam(0);
        item2.setSiteUri(participant.getGeneralParam().getUri());
        wsCtrlSiteCommParams2.add(item2);
        conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams2);
        makeSubpic(multiPicInfoDTO, smc2ConferenceContext, conferenceServiceEx, confId);
        if (Objects.equals(broadcastPollStatus, "CANCEL")) {
            conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 0);
            conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 1);
        }
        if (Objects.equals(broadcastPollStatus, "START")) {
            conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 0);
        }


        //锁定当前
        List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams3 = new ArrayList<>();
        WSCtrlSiteCommParamEx item3 = new WSCtrlSiteCommParamEx();
        item3.setOperaTypeParam(1);
        item3.setSiteUri(participant.getGeneralParam().getUri());
        wsCtrlSiteCommParams3.add(item3);
        conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams3);
        //点名
        String spokesmanId = conferenceState.getSpokesmanId();
        if (Strings.isNotBlank(spokesmanId)) {
            SmcParitipantsStateRep.ContentDTO participantSp = smc2ConferenceContext.getParticipant(spokesmanId);
            conferenceServiceEx.setBroadcastSiteEx(confId, participantSp.getGeneralParam().getUri(), 0);
        }

        if (multiPicInfoReq != null) {
            multiPicInfoReq.setBroadcast(false);
            createMulitiPic(multiPicInfoReq);
        }
        //解锁所有
        List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParamsAll2 = new ArrayList<>();
        for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {

            Boolean online = contentDTO.getState().getOnline();
            //解锁所有
            //&& !Objects.equals(uri1, contentDTO.getGeneralParam().getUri())
            if (online && !Objects.equals(SMC_2, contentDTO.getGeneralParam().getName())) {
                WSCtrlSiteCommParamEx item = new WSCtrlSiteCommParamEx();
                //解锁所有
                item.setOperaTypeParam(0);
                item.setSiteUri(contentDTO.getGeneralParam().getUri());
                wsCtrlSiteCommParamsAll2.add(item);
            }
        }
        conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParamsAll2);
        //查询自身锁定状态

        Integer videoSwitchAttribute = participant.getState().getVideoSwitchAttribute();
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
    public Object activeConferences() {
        List<JSONObject> jsons = new ArrayList<>();

        Collection<Smc2ConferenceContext> cc = Smc2ConferenceContextCache.getInstance().values();
        Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
        if (deptId != null) {
            SysDept con = new SysDept();
            con.setDeptId(deptId);
            List<SysDept> sds = sysDeptService.selectDeptList(con);
            for (Iterator<Smc2ConferenceContext> iterator = cc.iterator(); iterator.hasNext(); ) {
                Smc2ConferenceContext conferenceContext = iterator.next();
                if (!conferenceContext.isEnd() && conferenceContext.isStart()) {

                    for (SysDept sysDept : sds) {
                        if (conferenceContext.getDeptId() == sysDept.getDeptId().longValue()) {
                            JSONObject json = toJson(conferenceContext);
                            jsons.add(json);
                            break;
                        }
                    }

                }
            }
        } else {
            for (Iterator<Smc2ConferenceContext> iterator = cc.iterator(); iterator.hasNext(); ) {
                Smc2ConferenceContext conferenceContext = iterator.next();
                if (!conferenceContext.isEnd() && conferenceContext.isStart()) {

                    JSONObject json = toJson(conferenceContext);
                    jsons.add(json);

                }
            }
        }

        return jsons;
    }

    private JSONObject toJson(Smc2ConferenceContext conferenceContext) {
        JSONObject json = new JSONObject();
        json.put("conferenceName", conferenceContext.getConference().getSubject());
        json.put("conferenceNumber", conferenceContext.getAccessCode());
        json.put("templateId", conferenceContext.getTemplateConferenceId());
        DecimalFormat df = new DecimalFormat("0.00");
        Integer rate = conferenceContext.getConference().getRate();
      if(rate>2048){
        switch (rate){
            case 2880:
                json.put("bindwidth", 3);
            break;
            case 3840:
                json.put("bindwidth",  4);
                break;
            case 4800:
                json.put("bindwidth",  5);
                break;
            case 5760:
                json.put("bindwidth", 6 );
                break;
            case 6720:
                json.put("bindwidth",  7);
                break;
            case 7680:
                json.put("bindwidth",  8);
                break;
        }
      }else {
          json.put("bindwidth",  df.format((float)conferenceContext.getConference().getRate()/1024));
      }

        json.put("isAppointment", conferenceContext.isAppointment());
        if (conferenceContext.isAppointment()) {
            json.put("type", conferenceContext.getConferenceAppointment().getType());
        }
        json.put("conferenceId", conferenceContext.getId());
        json.put("deptId", conferenceContext.getDeptId());
        json.put("deptName", SysDeptCache.getInstance().get(conferenceContext.getDeptId()).getDeptName());
        json.put("createUserId", conferenceContext.getCreateUser());
        AtomicInteger as = new AtomicInteger();
        AtomicInteger inMeetings = new AtomicInteger();
        List<SmcParitipantsStateRep.ContentDTO> content = conferenceContext.getContent();
        for (SmcParitipantsStateRep.ContentDTO contentDTO : content) {
            as.incrementAndGet();
            if (contentDTO.getState().getOnline()) {
                inMeetings.incrementAndGet();
            }

        }
        json.put("terminalCount", as.get());
        json.put("inMeetingTerminalCount", inMeetings.get());
        json.put("conferenceStartTime", conferenceContext.getStartTime());
        json.put("masterName", conferenceContext.getMasterParticipant() != null ? conferenceContext.getMasterParticipant().getName() : null);
        json.put("type", "smc2");
        json.put("tenantId", "");
        return json;
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
                SmcParitipantsStateRep.ContentDTO m_participant = smc2ConferenceContext.getParticipant(m_participantId);
                if (m_participant != null) {
                    subPics.add(m_participant.getGeneralParam().getUri());
                }
            }

        }

        conferenceServiceEx.setContinuousPresenceEx(confId, target, presenceMode, subPics);
    }

    private void endConference(Smc2ConferenceContext cc, AtomicInteger successCount, int endReasonsType) {
        if (cc != null) {
            try {
                ConferenceServiceEx conferenceServiceEx = cc.getSmc2Bridge().getConferenceServiceEx();
                Integer resultCode = conferenceServiceEx.delScheduledConfEx(cc.getSmc2conferenceId(), null);
                if (resultCode != 0) {
                    log.error("Smc2 mcu endConference-error:" + resultCode);
                    throw new CustomException("smc2.0 endConference-error" + resultCode);
                }
            } catch (Throwable e) {
                log.error("Smc2 mcu endConference-error", e);
            } finally {
                long s1 = System.currentTimeMillis();
                endConference(cc.getId(), endReasonsType);
                if (cc.getConferenceAppointment() != null) {
                    BusiSmc2AppointmentConference conferenceAppointment = BeanFactory.getBean(BusiSmc2AppointmentConferenceMapper.class).selectBusiSmc2AppointmentConferenceById(cc.getConferenceAppointment().getId().intValue());
                    if (conferenceAppointment != null) {
                        conferenceAppointment.setIsHangUp(YesOrNo.YES.getValue());
                        conferenceAppointment.setIsStart(null);
                        BeanFactory.getBean(BusiSmc2AppointmentConferenceMapper.class).updateBusiSmc2AppointmentConference(conferenceAppointment);
                    }
                }

                StringBuilder infoBuilder = new StringBuilder();
                infoBuilder.append("结束会议【").append(cc.getConference().getSubject()).append("】");
                infoBuilder.append(", 会议号码: ").append(cc.getAccessCode());
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(cc, WebsocketMessageType.MESSAGE_TIP, infoBuilder);
            }
        }

    }

    public void endConference(String conferenceId, int endReasonsType) {
        Smc2ConferenceContext conferenceContext = Smc2ConferenceContextCache.getInstance().remove(conferenceId);
        if (conferenceContext != null) {
            try {
                // 设置结束状态
                conferenceContext.setEnd(true);
                conferenceContext.setEndTime(new Date());
                conferenceContext.getConference().setStage("CANCEL");
                // 保存历史记录
                conferenceContext.getHistoryConference().setEndReasonsType(endReasonsType);
                BeanFactory.getBean(IBusiSmc2HistoryConferenceService.class).saveHistory(conferenceContext.getHistoryConference(), conferenceContext);
                // 会议结束推送mqtt
                try {
                    pushEndMessageToMqtt(conferenceId, conferenceContext);
                } catch (Exception e) {
                    log.error("结束会议时取消会议当前操作失败", e);
                }

                Smc2ConferenceContextCache.getInstance().deleteCascadeConferenceContexts(conferenceId);
                Smc2ConferenceContextCache.getInstance().remove(conferenceContext.getId());
                BusiMcuSmc2ConferenceAppointment busiConferenceAppointment = conferenceContext.getConferenceAppointment();
                if (busiConferenceAppointment != null) {
                    BeanFactory.getBean(IBusiSmc2AppointmentConferenceService.class).deleteBusiSmc2AppointmentConferenceById(busiConferenceAppointment.getId().intValue());
                }
                CoSpace coSpace = (CoSpace) conferenceContext.getMonitorParticipantMap().get(conferenceId);
                if (!Objects.isNull(coSpace)) {
                    Long deptId = conferenceContext.getDeptId();
                    FmeBridge fmeBridge = BridgeUtils.getAvailableFmeBridge(deptId);
                    if (fmeBridge != null) {
                        fmeBridge.getCoSpaceInvoker().deleteCoSpace(coSpace.getId());
                        fmeBridge.doFmeBridgeBusiness(FmeBridgeProcessingStrategy.TRAVERSE, new FmeBridgeAddpterProcessor() {
                            @Override
                            public void process(FmeBridge fmeBridge) {
                                fmeBridge.getDataCache().deleteCoSpace(coSpace.getId());
                            }
                        });
                    }
                }

                BusiSmc2HistoryConferenceMapper smc2HistoryConferenceMapper = BeanFactory.getBean(BusiSmc2HistoryConferenceMapper.class);
                BusiSmc2HistoryConference busiSmc2HistoryConference = new BusiSmc2HistoryConference();
                busiSmc2HistoryConference.setConferenceId(conferenceContext.getId());
                List<BusiSmc2HistoryConference> busiSmc2HistoryConferences = smc2HistoryConferenceMapper.selectBusiSmc2HistoryConferenceList(busiSmc2HistoryConference);
                if(!CollectionUtils.isEmpty(busiSmc2HistoryConferences)){
                    BusiSmc2HistoryConference busiSmc2HistoryConference1 = busiSmc2HistoryConferences.get(0);
                    busiSmc2HistoryConference1.setEndStatus(1L);
                    busiSmc2HistoryConference1.setEndTime(new Date());
                    smc2HistoryConferenceMapper.updateBusiSmc2HistoryConference(busiSmc2HistoryConference1);
                }

            } catch (Throwable e) {
                log.error("结束会议失败: " + conferenceContext.getAccessCode(), e);
            } finally {
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                JSONObject object = new JSONObject();
                object.put("conferenceId", conferenceContext.getId());
                object.put("stage", "CANCEL");
                jsonArray.add(object);
                jsonObject.put("conferenceStages", jsonArray);

                Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已结束");
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
    private void pushEndMessageToMqtt(String conferenceNumber, Smc2ConferenceContext conferenceContext) {
        List<TerminalAttendeeSmc2> mqttJoinTerminals = new ArrayList<>();
        Smc2ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
            if (a instanceof TerminalAttendeeSmc2) {
                TerminalAttendeeSmc2 ta = (TerminalAttendeeSmc2) a;
                BusiTerminal bt = TerminalCache.getInstance().get(ta.getTerminalId());
                if (!ObjectUtils.isEmpty(bt.getSn())) {
                    mqttJoinTerminals.add(ta);
                }
            }
        });

        //BeanFactory.getBean(IMqttService.class).endConference(conferenceNumber, mqttJoinTerminals, new ArrayList<>(conferenceContext.getLiveTerminals()),conferenceContext);
    }

}
