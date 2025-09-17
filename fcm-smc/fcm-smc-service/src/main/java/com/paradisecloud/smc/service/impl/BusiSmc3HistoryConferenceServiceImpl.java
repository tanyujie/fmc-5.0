package com.paradisecloud.smc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.*;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantTerminalService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.smc.dao.model.BusiSmcAppointmentConference;
import com.paradisecloud.smc.dao.model.BusiSmcHistoryConference;
import com.paradisecloud.smc.dao.model.mapper.BusiSmcAppointmentConferenceMapper;
import com.paradisecloud.smc.processormessage.RealTimeInfoProcessorMessage;
import com.paradisecloud.smc.service.IBusiSmc3HistoryConferenceService;
import com.paradisecloud.smc.service.task.EndConferenceTask;
import com.paradisecloud.smc.service.task.SmcDelayTaskService;
import com.paradisecloud.smc.service.task.StatisticalConferenceTask;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author nj
 * @date 2023/6/21 10:03
 */
@Transactional
@Service
public class BusiSmc3HistoryConferenceServiceImpl implements IBusiSmc3HistoryConferenceService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;
    @Resource
    private BusiHistoryCallMapper busiHistoryCallMapper;
    @Resource
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;
    @Resource
    private SmcDelayTaskService smcDelayTaskService;
    @Resource
    private CdrCallLegStartMapper cdrCallLegStartMapper;
    @Resource
    private CdrCallLegEndMapper cdrCallLegEndMapper;
    @Resource
    private IBusiHistoryParticipantTerminalService busiHistoryParticipantTerminalService;
    @Override
    public BusiHistoryConference selectBusiHistoryConferenceById(Long id) {
        return busiHistoryConferenceMapper.selectBusiHistoryConferenceById(id);
    }

    @Override
    public List<BusiHistoryConference> selectBusiHistoryConferenceList(BusiHistoryConference busiHistoryConference) {
        return busiHistoryConferenceMapper.selectBusiHistoryConferenceList(busiHistoryConference);
    }

    @Override
    public int insertBusiHistoryConference(BusiHistoryConference busiHistoryConference) {
        busiHistoryConference.setCreateTime(new Date());
        return busiHistoryConferenceMapper.insertBusiHistoryConference(busiHistoryConference);
    }

    @Override
    public BusiHistoryConference saveHistory(SmcConferenceContext conferenceContext) {
        try {
            BusiHistoryConference busiHistoryConference = new BusiHistoryConference();
            busiHistoryConference.setCallId(conferenceContext.getConference().getId());
            busiHistoryConference.setDeptId(conferenceContext.getDeptId());
            busiHistoryConference.setBandwidth(conferenceContext.getRate());
            busiHistoryConference.setCallLegProfileId(conferenceContext.getConference().getId());
            busiHistoryConference.setName(conferenceContext.getConference().getSubject());
            busiHistoryConference.setNumber(conferenceContext.getNumber());
            busiHistoryConference.setDeviceNum(0);
            busiHistoryConference.setCoSpace(conferenceContext.getConference().getId());
            busiHistoryConference.setCreateTime(new Date());
            String scheduleStartTime = conferenceContext.getConference().getScheduleStartTime();
           // busiHistoryConference.setConferenceStartTime(UTCTimeFormatUtil.utcToLocal(scheduleStartTime));
            busiHistoryConference.setConferenceStartTime(new Date());
            int type = 0;
            BusiSmcAppointmentConferenceMapper busiSmcAppointmentConferenceMapper = BeanFactory.getBean(BusiSmcAppointmentConferenceMapper.class);
            BusiSmcAppointmentConference busiSmcAppointmentConference = busiSmcAppointmentConferenceMapper.selectBusiSmcAppointmentConferenceByConferenceId(conferenceContext.getConference().getId());
            if(busiSmcAppointmentConference!=null){

                if (conferenceContext.getConference().getConferenceTimeType() != null) {
                    if (Objects.equals(ConferenceTimeType.EDIT_CONFERENCE, conferenceContext.getConference().getConferenceTimeType())) {
                        type = 1;
                    } else {
                        type = 2;
                    }
                }
            }



            busiHistoryConference.setType(type);
            busiHistoryConference.setDuration(0);
            busiHistoryConferenceMapper.insertBusiHistoryConference(busiHistoryConference);

            // 会议统计
            StatisticalConferenceTask statisticalConferenceTask = new StatisticalConferenceTask(busiHistoryConference.getDeptId().toString(), 100, busiHistoryConference.getDeptId());
            smcDelayTaskService.addTask(statisticalConferenceTask);
            return busiHistoryConference;
        } catch (Throwable e) {
            logger.error("saveHistory error", e);
            return null;
        }
    }

    @Override
    public BusiHistoryConference saveHistory(BusiSmcHistoryConference historyConference,int rate,ConferenceTimeType conferenceTimeType) {
        try {
            BusiHistoryConference busiHistoryConference = new BusiHistoryConference();
            busiHistoryConference.setDeptId(historyConference.getDeptId());
            busiHistoryConference.setBandwidth(rate);
            busiHistoryConference.setCallId(historyConference.getConferenceId());
            busiHistoryConference.setCallLegProfileId(historyConference.getConferenceId());
            busiHistoryConference.setName(historyConference.getSubject());
            busiHistoryConference.setNumber(historyConference.getConferenceCode());
            busiHistoryConference.setDeviceNum(0);
            busiHistoryConference.setCoSpace(historyConference.getConferenceCode());
            busiHistoryConference.setCreateTime(new Date());
            busiHistoryConference.setConferenceStartTime(historyConference.getStartTime());

            int type = 0;
            BusiSmcAppointmentConferenceMapper busiSmcAppointmentConferenceMapper = BeanFactory.getBean(BusiSmcAppointmentConferenceMapper.class);
            BusiSmcAppointmentConference busiSmcAppointmentConference = busiSmcAppointmentConferenceMapper.selectBusiSmcAppointmentConferenceByConferenceId(historyConference.getConferenceId());
            if(busiSmcAppointmentConference!=null){
                if (conferenceTimeType != null) {
                    if (Objects.equals(ConferenceTimeType.EDIT_CONFERENCE,conferenceTimeType)) {
                        type = 1;
                    } else {
                        type = 2;
                    }
                }
            }

            busiHistoryConference.setType(type);
            busiHistoryConference.setDuration(0);
            busiHistoryConferenceMapper.insertBusiHistoryConference(busiHistoryConference);

            // 会议统计
            StatisticalConferenceTask statisticalConferenceTask = new StatisticalConferenceTask(busiHistoryConference.getDeptId().toString(), 10000, busiHistoryConference.getDeptId());
            smcDelayTaskService.addTask(statisticalConferenceTask);
            return busiHistoryConference;
        } catch (Throwable e) {
            logger.error("saveHistory error", e);
            return null;
       }
    }

    @Override
    public BusiHistoryConference saveHistory(BusiHistoryConference busiHistoryConference, SmcConferenceContext conferenceContext) {
        try {
            String scheduleStartTime = conferenceContext.getConference().getScheduleStartTime();
            Date date = DateUtil.convertDateByString(scheduleStartTime, null);
            busiHistoryConference.setDuration(conferenceContext.getConference().getDuration());
            busiHistoryConference.setConferenceStartTime(date);

            int type = 0;
            BusiSmcAppointmentConferenceMapper busiSmcAppointmentConferenceMapper = BeanFactory.getBean(BusiSmcAppointmentConferenceMapper.class);
            BusiSmcAppointmentConference busiSmcAppointmentConference = busiSmcAppointmentConferenceMapper.selectBusiSmcAppointmentConferenceByConferenceId(conferenceContext.getConference().getId());
            if(busiSmcAppointmentConference!=null){

                if (conferenceContext.getConference().getConferenceTimeType() != null) {
                    if (Objects.equals(ConferenceTimeType.EDIT_CONFERENCE, conferenceContext.getConference().getConferenceTimeType())) {
                        type = 1;
                    } else {
                        type = 2;
                    }
                }
            }
            busiHistoryConference.setType(type);
            busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConference);

            if (busiHistoryConference.getConferenceEndTime() != null) {
                // 结束会议
                EndConferenceTask endConferenceTask = new EndConferenceTask(busiHistoryConference.getId().toString(), 1000, busiHistoryConference.getId());
                smcDelayTaskService.addTask(endConferenceTask);
            }

            // 会议统计
            StatisticalConferenceTask statisticalConferenceTask = new StatisticalConferenceTask(busiHistoryConference.getDeptId().toString(), 10000, busiHistoryConference.getDeptId());
            smcDelayTaskService.addTask(statisticalConferenceTask);

            return busiHistoryConference;
        } catch (Throwable e) {
            logger.error("saveHistory error", e);
            return null;
        }
    }

    @Override
    public int updateBusiHistoryConference(BusiHistoryConference busiHistoryConference) {
        busiHistoryConference.setUpdateTime(new Date());

        // 绑定终端归属部门
        LoginUser loginUser = SecurityUtils.getLoginUser();
        busiHistoryConference.setDeptId(loginUser.getUser().getDeptId());
        return busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConference);
    }

    @Override
    public int deleteBusiHistoryConferenceByIds(Long[] ids) {
        return busiHistoryConferenceMapper.deleteBusiHistoryConferenceByIds(ids);
    }

    @Override
    public int deleteBusiHistoryConferenceById(Long id) {
        return busiHistoryConferenceMapper.deleteBusiHistoryConferenceById(id);
    }

    @Override
    public void updateBusiHistoryParticipant(SmcConferenceContext conferenceContext, ParticipantRspDto contentDTO, boolean updateMediaInfo,String subscription) {
        String callLegId=contentDTO.getId();
        if (contentDTO.getIsOnline()) {
            BusiHistoryConference busiHistoryConferenceQ=new BusiHistoryConference();
            busiHistoryConferenceQ.setNumber(conferenceContext.getNumber());
            List<BusiHistoryConference> busiHistoryConferences = busiHistoryConferenceMapper.selectBusiHistoryConferenceList(busiHistoryConferenceQ);

            BusiHistoryConference busiHistoryConference = busiHistoryConferences.get(0);
            if (busiHistoryConference != null) {
                BusiHistoryParticipant busiHistoryParticipant = busiHistoryParticipantMapper.selectBusiHistoryParticipantByCallLegId(callLegId);
                if (busiHistoryParticipant != null) {
                    if (busiHistoryParticipant.getJoined() == null || !busiHistoryParticipant.getJoined()) {
                        busiHistoryParticipant.setJoined(true);
                        busiHistoryParticipant.setJoinTime(new Date());
                        busiHistoryParticipant.setUpdateTime(new Date());
                        busiHistoryParticipantMapper.updateBusiHistoryParticipant(busiHistoryParticipant);
                    } else {
                        if (updateMediaInfo) {
                            Map<String, JSONObject> realJSONObjectMap = RealTimeInfoProcessorMessage.getConferenceParitipantsRealTimeMap().get(conferenceContext.getConference().getId()+subscription);
                            if(realJSONObjectMap!=null){
                                JSONObject jsonObject = realJSONObjectMap.get(contentDTO.getId());
                                busiHistoryParticipant.setMediaInfo(toDetail(jsonObject, contentDTO));
                                busiHistoryParticipant.setUpdateTime(new Date());
                                busiHistoryParticipantMapper.updateBusiHistoryParticipantForJoin(busiHistoryParticipant);
                            }

                        }
                    }
                } else {
                    BusiHistoryCall busiHistoryCallCon = new BusiHistoryCall();
                    busiHistoryCallCon.setHistoryConferenceId(busiHistoryConference.getId());
                    List<BusiHistoryCall> busiHistoryCallList = busiHistoryCallMapper.selectBusiHistoryCallList(busiHistoryCallCon);
                    BusiHistoryCall busiHistoryCall = null;
                    if (busiHistoryCallList.size() > 0) {
                        busiHistoryCall = busiHistoryCallList.get(0);
                    }
                    busiHistoryParticipant = new BusiHistoryParticipant();
                    busiHistoryParticipant.setCreateTime(new Date());
                    busiHistoryParticipant.setDeptId(busiHistoryConference.getDeptId().intValue());
                    if (busiHistoryCall != null) {
                        busiHistoryParticipant.setCallId(busiHistoryCall.getCallId());
                    }
                    busiHistoryParticipant.setCoSpace(conferenceContext.getConference().getId());
                    busiHistoryParticipant.setCallLegId(callLegId);
                    busiHistoryParticipant.setName(contentDTO.getName());
                    busiHistoryParticipant.setHistoryConferenceId(busiHistoryConference.getId());
                    busiHistoryParticipant.setJoinTime(new Date());
                    busiHistoryParticipant.setCreateTime(new Date());
                    busiHistoryParticipant.setJoined(true);
                    busiHistoryParticipant.setTerminalId(contentDTO.getTerminalId());
                    if (updateMediaInfo) {
                        Map<String, JSONObject> realJSONObjectMap = RealTimeInfoProcessorMessage.getConferenceParitipantsRealTimeMap().get(conferenceContext.getConference().getId()+subscription);
                        if(realJSONObjectMap!=null){
                            JSONObject jsonObject = realJSONObjectMap.get(contentDTO.getId());
                            busiHistoryParticipant.setMediaInfo(toDetail(jsonObject, contentDTO));
                        }
                    }
                    String remoteParty = contentDTO.getUri();
                    if (remoteParty.contains(":")) {
                        remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
                    }
                    busiHistoryParticipant.setRemoteParty(remoteParty);
                    BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(remoteParty);
                    if (busiTerminal == null) {
                        if (remoteParty.contains("@")) {
                            try {
                                String[] remotePartyArr = remoteParty.split("@");
                                String credential = remotePartyArr[0];
                                String ip = remotePartyArr[1];
                                if (StringUtils.hasText(ip)) {
                                    FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByDomainName(ip);
                                    if (fsbcBridge != null) {
                                        String remotePartyNew = credential + "@" + fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                                        busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyNew);
                                    }
                                    if (busiTerminal == null) {
                                        FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByDomainName(ip);
                                        if (fcmBridge != null) {
                                            String remotePartyNew = credential + "@" + fcmBridge.getBusiFreeSwitch().getIp();
                                            busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyNew);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                    if (busiTerminal != null) {
                        {
                            busiHistoryParticipant.setTerminalId(busiTerminal.getId());
                        }
                    }
                    busiHistoryParticipantMapper.insertBusiHistoryParticipant(busiHistoryParticipant);
                    try {
                        CdrCallLegStart cdrCallLegStart = new CdrCallLegStart();
                        Date date = new Date();
                        cdrCallLegStart.setCdrId(busiHistoryParticipant.getCallLegId());
                        cdrCallLegStart.setRecordIndex(0);
                        cdrCallLegStart.setCorrelatorIndex(0);
                        cdrCallLegStart.setTime(date);
                        cdrCallLegStart.setDisplayName(busiHistoryParticipant.getName());
                        cdrCallLegStart.setRemoteParty(busiHistoryParticipant.getRemoteParty());
                        cdrCallLegStart.setRemoteAddress(busiHistoryParticipant.getRemoteParty());
                        cdrCallLegStart.setCall(busiHistoryParticipant.getCallId());
                        cdrCallLegStart.setSession(busiHistoryParticipant.getCallId());
                        cdrCallLegStart.setCreateTime(date);
                        cdrCallLegStart.setRecording(false);
                        cdrCallLegStart.setStreaming(false);
                        cdrCallLegStart.setDirection("outgoing");
                        String type = "sip";
                        Integer protoType = contentDTO.getIpProtocolType();
                        if (protoType != null) {
                            if (protoType == 1) {
                                type = "sip";
                            } else if (protoType ==0) {
                                type = "h323";
                            }else {
                                type = "sipAndH323";
                            }
                        }
                        cdrCallLegStart.setType(type);
                        cdrCallLegStartMapper.insertCdrCallLegStart(cdrCallLegStart);
                    } catch (Exception e) {
                    }
                    logger.info("Participant joined and saved: " + contentDTO.toString());
                }
                // 更新参会者终端信息
                updateBusiHistoryParticipantTerminal(busiHistoryParticipant);
            }
        } else {
            BusiHistoryParticipant busiHistoryParticipant = busiHistoryParticipantMapper.selectBusiHistoryParticipantByCallLegId(callLegId);
            if (busiHistoryParticipant != null) {
                if (busiHistoryParticipant.getOutgoingTime() == null || busiHistoryParticipant.getDurationSeconds() == null || busiHistoryParticipant.getDurationSeconds().intValue() == 0) {
                    busiHistoryParticipant.setOutgoingTime(new Date());
                    busiHistoryParticipant.setUpdateTime(new Date());
                    busiHistoryParticipant.setDurationSeconds((int) ((busiHistoryParticipant.getOutgoingTime().getTime() - busiHistoryParticipant.getJoinTime().getTime()) / 1000));
                    busiHistoryParticipantMapper.updateBusiHistoryParticipant(busiHistoryParticipant);
                    logger.info("Participant left and saved: " + contentDTO.toString());

                    // 更新参会者终端信息
                    updateBusiHistoryParticipantTerminal(busiHistoryParticipant);
                }
                try {
                    CdrCallLegEnd cdrCallLegEnd = new CdrCallLegEnd();
                    Date date = new Date();
                    cdrCallLegEnd.setRecordIndex(0);
                    cdrCallLegEnd.setCorrelatorIndex(0);
                    cdrCallLegEnd.setTime(date);
                    cdrCallLegEnd.setSession(busiHistoryParticipant.getCallId());
                    cdrCallLegEnd.setCreateTime(date);
                    cdrCallLegEnd.setCdrId(busiHistoryParticipant.getCallLegId());
                    cdrCallLegEnd.setDurationSeconds(busiHistoryParticipant.getDurationSeconds());
                    if (cdrCallLegEnd.getReason() == null) {
                        if (contentDTO.getParticipantState().getCallFailReason()!= null) {
                            //TODO
                            cdrCallLegEnd.setReason(CallLegEndReasonEnum.ERROR);
                        } else {
                            cdrCallLegEnd.setReason(CallLegEndReasonEnum.CALL_ENDED);
                        }
                    }
                    cdrCallLegEndMapper.insertCdrCallLegEnd(cdrCallLegEnd);
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void updateBusiHistoryParticipant(BusiHistoryParticipant busiHistoryParticipant, BusiHistoryConference busiHistoryConference) {
        if (busiHistoryParticipant != null && busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() != null) {
            if (busiHistoryParticipant.getOutgoingTime() == null || busiHistoryParticipant.getDurationSeconds() == null || busiHistoryParticipant.getDurationSeconds().intValue() == 0) {
                busiHistoryParticipant.setOutgoingTime(busiHistoryConference.getConferenceEndTime());
                busiHistoryParticipant.setUpdateTime(new Date());
                busiHistoryParticipant.setDurationSeconds((int) ((busiHistoryParticipant.getOutgoingTime().getTime() - busiHistoryParticipant.getJoinTime().getTime()) / 1000));
                busiHistoryParticipantMapper.updateBusiHistoryParticipant(busiHistoryParticipant);
                logger.info("Participant left and saved: " + busiHistoryParticipant.toString());

                // 更新参会者终端信息
                updateBusiHistoryParticipantTerminal(busiHistoryParticipant);
            }
            try {
                CdrCallLegEnd cdrCallLegEnd = new CdrCallLegEnd();
                Date date = new Date();
                cdrCallLegEnd.setRecordIndex(0);
                cdrCallLegEnd.setCorrelatorIndex(0);
                cdrCallLegEnd.setTime(date);
                cdrCallLegEnd.setSession(busiHistoryParticipant.getCallId());
                cdrCallLegEnd.setCreateTime(date);
                cdrCallLegEnd.setCdrId(busiHistoryParticipant.getCallLegId());
                cdrCallLegEnd.setDurationSeconds(busiHistoryParticipant.getDurationSeconds());
                cdrCallLegEnd.setReason(CallLegEndReasonEnum.CALL_ENDED);
                cdrCallLegEndMapper.insertCdrCallLegEnd(cdrCallLegEnd);
            } catch (Exception e) {
            }
        }
    }


    /**
     * 更新参会者终端信息
     *
     * @param busiHistoryParticipant
     */
    private void updateBusiHistoryParticipantTerminal(BusiHistoryParticipant busiHistoryParticipant) {
        if (busiHistoryParticipant != null) {
            if (busiHistoryParticipant.getTerminalId() != null) {
                BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiHistoryParticipant.getTerminalId());
                if (busiTerminal != null) {
                    busiHistoryParticipant.setName(busiTerminal.getName());
                }
            }
            try {
                busiHistoryParticipantTerminalService.updateBusiHistoryParticipantTerminalByBusiHistoryParticipant(busiHistoryParticipant);
            } catch (Exception e) {
            }
        }
    }

    public JSONObject toDetail(JSONObject jsonObject,  ParticipantRspDto contentDTO){
        if(Objects.isNull(jsonObject)||Objects.isNull(contentDTO)){
            return null;
        }
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("direction", "outgoing");
        Integer protoType = contentDTO.getIpProtocolType();
        String protoTypeStr = null;
        if (protoType != null) {
            if (protoType == 1) {
                protoTypeStr = "sip";
            } else if (protoType ==0) {
                protoTypeStr = "h323";
            }else {
                protoTypeStr = "sipAndH323";
            }
        }
        jsonObj.put("type", protoTypeStr);
        jsonObj.put("isEncrypted", false);
        jsonObj.put("remoteParty", contentDTO.getUri());
        JSONObject upLink = new JSONObject();
        jsonObj.put("upLink", upLink);
        JSONObject upLinkAudio = new JSONObject();
        JSONArray upLinkVideos = new JSONArray();

        JSONObject downLink = new JSONObject();
        jsonObj.put("downLink", downLink);

        JSONObject downLinkAudio = new JSONObject();
        JSONArray downLinkVideos = new JSONArray();

        upLink.put("videos", upLinkVideos);
        upLink.put("audio", upLinkAudio);
        downLink.put("videos", downLinkVideos);
        downLink.put("audio", downLinkAudio);

        JSONObject realTimeInfo = (JSONObject)jsonObject.get("realTimeInfo");
        //下行信息
        JSONObject receiveRealTimeInfo = (JSONObject)realTimeInfo.get("receiveRealTimeInfo");

        JSONObject rttRealTimeInfo = (JSONObject)realTimeInfo.get("rttRealTimeInfo");
        JSONObject video = new JSONObject();
        video.put("role", Objects.equals("false",receiveRealTimeInfo.get("openAux"))?"main":"");
        int videoResolutionCode = (int)receiveRealTimeInfo.get("videoResolution");
        int videoProtocolCode = (int)receiveRealTimeInfo.get("videoProtocol");
        video.put("resolutionRatio", VideoResolutionEnum.getValueByCode(videoResolutionCode).name());
        video.put("frameRate", receiveRealTimeInfo.get("videoFrameRate"));
        video.put("videoCodec", VideoProtocolEnum.getValueByCode(videoProtocolCode).name());
        video.put("bandwidth", receiveRealTimeInfo.get("videoBandWidth"));
        video.put("packetLossPercentage", receiveRealTimeInfo.get("videoLoss"));
        video.put("jitter", receiveRealTimeInfo.get("videoJitter"));
        video.put("roundTripTime", rttRealTimeInfo.get("videoRtt"));
        downLinkVideos.add(video);


        int audioProtocolCode = (int)receiveRealTimeInfo.get("audioProtocol");

        downLinkAudio.put("codec",AudioProtocolEnum.getValueByCode(audioProtocolCode).name());
        downLinkAudio.put("bandwidth",  receiveRealTimeInfo.get("audioBandWidth"));
        downLinkAudio.put("packetLossPercentage", receiveRealTimeInfo.get("audioLoss"));
        downLinkAudio.put("codecBitRate", null);
        downLinkAudio.put("jitter", receiveRealTimeInfo.get("audioJitter"));
        downLinkAudio.put("roundTripTime", rttRealTimeInfo.get("audioRtt"));
        downLinkAudio.put("gainApplied", null);

        //上行
        JSONObject sendRealTimeInfo = (JSONObject)realTimeInfo.get("sendRealTimeInfo");
        if(sendRealTimeInfo==null){
            JSONObject videoSend = new JSONObject();
            video.put("role", Objects.equals("false",sendRealTimeInfo.get("openAux"))?"main":"");
            int videoResolutionCode2 = (int)sendRealTimeInfo.get("videoResolution");
            int videoProtocolCode2 = (int)sendRealTimeInfo.get("videoProtocol");
            video.put("resolutionRatio", VideoResolutionEnum.getValueByCode(videoResolutionCode2).name());
            video.put("frameRate", sendRealTimeInfo.get("videoFrameRate"));
            video.put("videoCodec", VideoProtocolEnum.getValueByCode(videoProtocolCode2).name());
            video.put("bandwidth", sendRealTimeInfo.get("videoBandWidth"));
            video.put("packetLossPercentage", sendRealTimeInfo.get("videoLoss"));
            video.put("jitter", sendRealTimeInfo.get("videoJitter"));
            video.put("roundTripTime", rttRealTimeInfo.get("videoRtt"));
            upLinkVideos.add(videoSend);

            int audioProtocolCode2 = (int)sendRealTimeInfo.get("audioProtocol");

            upLinkAudio.put("codec",AudioProtocolEnum.getValueByCode(audioProtocolCode2).name());
            upLinkAudio.put("bandwidth",  sendRealTimeInfo.get("audioBandWidth"));
            upLinkAudio.put("packetLossPercentage", sendRealTimeInfo.get("audioLoss"));
            upLinkAudio.put("codecBitRate", null);
            upLinkAudio.put("jitter", sendRealTimeInfo.get("audioJitter"));
            upLinkAudio.put("roundTripTime", rttRealTimeInfo.get("audioRtt"));
            upLinkAudio.put("gainApplied", null);
        }

        return jsonObj;
    }
}
