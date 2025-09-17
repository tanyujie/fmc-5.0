package com.paradisecloud.fcm.smc2.core;

import com.paradisecloud.com.fcm.smc.modle.ConferenceUiParam;
import com.paradisecloud.com.fcm.smc.modle.ParticipantRspDto;
import com.paradisecloud.com.fcm.smc.modle.ParticipantState;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2DeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2Mapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2Dept;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplateConference;
import com.paradisecloud.fcm.smc2.cache.*;
import com.paradisecloud.fcm.smc2.conference.templateconference.StartTemplateConference;
import com.paradisecloud.fcm.smc2.conference.updateprocess.SelfCallAttendeeNewSmc2Processor;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.fcm.smc2.monitor.Smc2SIPOnlineStatusMonitor;
import com.paradisecloud.fcm.smc2.service.IBusiSmc2HistoryConferenceService;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2TemplateConferenceService;
import com.paradisecloud.fcm.smc2.utils.AttendeeSmc2Utils;
import com.sinhy.spring.BeanFactory;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.ConferenceStatusEx;
import com.suntek.smc.esdk.pojo.local.SiteStatusEx;
import com.suntek.smc.esdk.pojo.local.TPSDKResponseEx;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

/**
 *
 * 登录保活
 * @author nj
 * @date 2023/4/21 17:18
 */
@Component
@Order(1)
public class Smc2ModuleInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Smc2ModuleInitializer.class);
    public static final int INT_3 = 3;
    public static final int INT_5 = 5;
    public static final int INT_2 = 2;

    @Resource
    private BusiMcuSmc2Mapper busiMcuSmc2Mapper;
    @Resource
    private BusiMcuSmc2DeptMapper busiMcuSmc2DeptMapper;
    @Resource
    private IBusiMcuSmc2TemplateConferenceService busiMcuSmc2TemplateConferenceService;

    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        initDept();
        initBridge();
        //sycnConference();

        Map<String, Smc2Bridge> ipToSmc2BridgeMap = Smc2BridgeCache.getInstance().getIpToSmc2BridgeMap();
        if(ipToSmc2BridgeMap!=null){
            ipToSmc2BridgeMap.forEach((k,v)->{
               if(v.isAvailable()){
                   Smc2SIPOnlineStatusMonitor.getInstance().start();
                   return;
               }
            });
        }
    }

    private void sycnConference() {
        try {

            List<BusiHistoryConference> busiSmc2HistoryConferences = busiHistoryConferenceMapper.selectNotEndHistoryConferenceList(McuType.SMC2.getCode());
            if(CollectionUtils.isEmpty(busiSmc2HistoryConferences)){
                return;
            }
            for (BusiHistoryConference busiSmc2HistoryConference : busiSmc2HistoryConferences) {
                Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(busiSmc2HistoryConference.getCallLegProfileId());
                if (smc2ConferenceContext == null) {
                    //查询会议状态
                    BusiMcuSmc2TemplateConference busiTemplateConference=new BusiMcuSmc2TemplateConference();
                    busiTemplateConference.setId(Long.valueOf(busiSmc2HistoryConference.getCallLegProfileId()));
                    busiTemplateConference.setBusinessFieldType(100);
                    List<BusiMcuSmc2TemplateConference> busiMcuSmc2TemplateConferences = busiMcuSmc2TemplateConferenceService.selectAllBusiTemplateConferenceList(busiTemplateConference);
                    if(!CollectionUtils.isEmpty(busiMcuSmc2TemplateConferences)){
                        BusiMcuSmc2TemplateConference busiMcuSmc2TemplateConference = busiMcuSmc2TemplateConferences.get(0);
                        String confId = busiMcuSmc2TemplateConference.getConfId();
                        if(Strings.isNotBlank(confId)){
                            ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                            TPSDKResponseEx<List<ConferenceStatusEx>> result = getConferenceStatusResult(confId,conferenceServiceEx);
                            if (0 == result.getResultCode()) {
                                List<ConferenceStatusEx> status = result.getResult();
                                for (ConferenceStatusEx conferenceStatusEx : status) {
                                    String id1 = conferenceStatusEx.getId();
                                    if (Objects.equals(confId, id1)) {
                                        if(conferenceStatusEx.getStatus()==3){
                                            smc2ConferenceContext = new StartTemplateConference().startTemplateConference(busiMcuSmc2TemplateConference.getId());
                                            sycnParticiPants(smc2ConferenceContext, conferenceServiceEx, id1);
                                            sycnConferenDetail(smc2ConferenceContext, conferenceStatusEx);
                                        }

                                    }
                                }
                            }
                        }

                    }

                }
            }
        } catch (Exception e) {
            LOGGER.error("smc2 会议同步错误："+e.getMessage());
        }
    }

    private  TPSDKResponseEx<List<ConferenceStatusEx>> getConferenceStatusResult(String confId, ConferenceServiceEx conferenceServiceEx){
        List<String> list = new ArrayList<>();
        list.add(confId);
        TPSDKResponseEx<List<ConferenceStatusEx>> result = conferenceServiceEx.queryConferencesStatusEx(list);
        return result;
    }

    private void sycnParticiPants(Smc2ConferenceContext smc2ConferenceContext, ConferenceServiceEx conferenceServiceEx, String id1) {
        TPSDKResponseEx<List<SiteStatusEx>> listTPSDKResponseEx = conferenceServiceEx.queryConfSitesStatusEx(id1, null);

        int resultCodeSiteStatusEx = listTPSDKResponseEx.getResultCode();

        if(0==resultCodeSiteStatusEx){
            List<SiteStatusEx> siteStatusExList = listTPSDKResponseEx.getResult();
            List<SmcParitipantsStateRep.ContentDTO> contentDTOList = new ArrayList<>();
            for (SiteStatusEx statusEx : siteStatusExList) {
                String uri = statusEx.getUri();
                String particiPantIdBySiteUri = smc2ConferenceContext.getParticiPantIdBySiteUri(uri);
                if(particiPantIdBySiteUri!=null){
                    AttendeeSmc2 a = smc2ConferenceContext.getAttendeeById(particiPantIdBySiteUri);
                    if(a!=null){
                        SmcParitipantsStateRep.ContentDTO smcParticipant = a.getSmcParticipant();
                        if (smcParticipant != null) {
                            smcParticipant = AttendeeSmc2Utils.initContent(smcParticipant, smc2ConferenceContext, statusEx);
                            if (smcParticipant.getId() == null) {
                                smcParticipant.setId(a.getId());
                            }
                            SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = smcParticipant.getGeneralParam();
                            if (generalParam != null && generalParam.getId() == null) {
                                generalParam.setId(a.getId());
                            }
                            smcParticipant.getState().setVolume(a.getVolume());
                            smcParticipant.setAttendeeId(a.getId());
                            smcParticipant.getState().setParticipantId(a.getId());
                            generalParam.setType(smcParticipant.getGeneralParam().getType());
                            generalParam.setUri(smcParticipant.getGeneralParam().getUri());
                        } else {
                            smcParticipant = AttendeeSmc2Utils.initContent(null, smc2ConferenceContext, statusEx);
                            SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = smcParticipant.getGeneralParam();
                            generalParam.setId(a.getId());
                            smcParticipant.setId(a.getId());
                            smcParticipant.getState().setParticipantId(a.getId());
                            generalParam.setUri(statusEx.getUri());
                            generalParam.setType(statusEx.getType());
                        }


                        smc2ConferenceContext.getParticipantAttendeeAllMap().put(smcParticipant.getId(), a);
                        a.setConferenceNumber(smc2ConferenceContext.getConferenceNumber());
                        a.setSmcParticipant(smcParticipant);
                        AttendeeSmc2Utils.updateByParticipant(smc2ConferenceContext, smcParticipant, a);
                        processUpdateParticipant(smc2ConferenceContext, smcParticipant, smcParticipant.getChangeType()==null?false:(smcParticipant.getChangeType() != 1));
                    }

                }else {
                    syscnAddParticipants(smc2ConferenceContext, contentDTOList, statusEx);
                }
            }


        }

        ConferenceUiParam conferenceUiParam = smc2ConferenceContext.getDetailConference().getConferenceUiParam();
        List<SmcParitipantsStateRep.ContentDTO> content = smc2ConferenceContext.getContent();
        if(!CollectionUtils.isEmpty(content)){
            int size = content.size();
            long count = content.stream().filter(p -> p.getState().getOnline()).count();
            conferenceUiParam.setTotalParticipantNum(size);
            conferenceUiParam.setOnlineParticipantNum(Integer.valueOf(count+""));
            conferenceUiParam.setOnlineNum(Integer.valueOf(count+""));
            conferenceUiParam.setTotalNum(size);
        }


    }

    private void syscnAddParticipants(Smc2ConferenceContext smc2ConferenceContext, List<SmcParitipantsStateRep.ContentDTO> contentDTOList, SiteStatusEx statusEx) {
        SmcParitipantsStateRep.ContentDTO contentDTO = new SmcParitipantsStateRep.ContentDTO();
        SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
        contentDTO.setGeneralParam(generalParam);
        ParticipantState state = new ParticipantState();
        contentDTO.setState(state);
        contentDTO.setWeight(0);

        generalParam.setName(statusEx.getName());
        generalParam.setUri(statusEx.getUri());
        String uuid = UUID.randomUUID().toString().replaceAll("//-", "");
        generalParam.setId(uuid);
        state.setParticipantId(uuid);
        state.setOnline(true);
        state.setVolume(statusEx.getVolume());
        state.setMute(statusEx.getIsMute() == null ? false : (statusEx.getIsMute() == 1 ? true : false));
        state.setQuiet(statusEx.getIsQuiet() == null ? false : (statusEx.getIsQuiet() == 1 ? true : false));
        state.setParticipantId(uuid);
        generalParam.setType(statusEx.getType());
        state.setOnline(true);
        contentDTO.setTerminalOnline(true);
        Integer statusExStatus = statusEx.getStatus();
        /**
         * 会场状态。
         * 0：未知状态（保留）
         * 1：会场不存在
         * 2：在会议中
         * 3：未入会
         * 4：正在呼叫
         * 5：正在振铃
         */
        if(statusExStatus!=null){
            if(statusExStatus.equals(INT_2)){
                state.setOnline(true);
            }else {
                state.setOnline(false);
            }

        }
        new SelfCallAttendeeNewSmc2Processor(contentDTO,smc2ConferenceContext);
        contentDTOList.add(contentDTO);
        processUpdateParticipant(smc2ConferenceContext,contentDTO,false);
        Smc2WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(smc2ConferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + statusEx.getName() + "】入会");
    }

    private void sycnConferenDetail(Smc2ConferenceContext smc2ConferenceContext , ConferenceStatusEx conferenceStatusEx) {
        String chairmanId = smc2ConferenceContext.getParticiPantIdBySiteUri(conferenceStatusEx.getChair());
        if(Strings.isNotBlank(chairmanId)){
            smc2ConferenceContext.setChairmanId(chairmanId);
            AttendeeSmc2 attendee = smc2ConferenceContext.getAttendeeById(chairmanId);
            ChangeMasterAttendeeOperation changeMasterAttendeeOperation = new ChangeMasterAttendeeOperation(smc2ConferenceContext, attendee);
            smc2ConferenceContext.setAttendeeOperation(changeMasterAttendeeOperation);
            changeMasterAttendeeOperation.operate();
        }
        smc2ConferenceContext.setLocked(conferenceStatusEx.getIsLock() != null && (conferenceStatusEx.getIsLock() == 1));

        XMLGregorianCalendar beginTime = conferenceStatusEx.getBeginTime();
        if(beginTime != null){
            Date time = beginTime.toGregorianCalendar().getTime();
            smc2ConferenceContext.setStartTime(time);
        }


    }



    private ParticipantRspDto getParticipantRspDto(Smc2ConferenceContext smc2ConferenceContext, String chairmanId) {
        SmcParitipantsStateRep.ContentDTO participant = smc2ConferenceContext.getParticipant(chairmanId);
        ParticipantRspDto participantRspDto = new ParticipantRspDto();
        participantRspDto.setUri(participant.getGeneralParam().getUri());
        participantRspDto.setName(participant.getGeneralParam().getName());
        participantRspDto.setId(participant.getState().getParticipantId());
        participantRspDto.setDeptId(participant.getDeptId());
        participantRspDto.setIsOnline(participant.getState().getOnline());
        participantRspDto.setTerminalId(participant.getTerminalId());
        participantRspDto.setIsMute(participant.getState().getMute());
        return participantRspDto;
    }

    private void initDept() {
        List<BusiMcuSmc2Dept> busiSmc2Depts = busiMcuSmc2DeptMapper.selectBusiMcuSmc2DeptList(new BusiMcuSmc2Dept());
        if(!CollectionUtils.isEmpty(busiSmc2Depts)){
            for (BusiMcuSmc2Dept busiSmc2Dept : busiSmc2Depts) {
                DeptSmc2MappingCache.getInstance().put(busiSmc2Dept.getDeptId(),busiSmc2Dept);
            }
        }

    }
    private void initBridge() {

        List<BusiMcuSmc2> busiSmc2s = busiMcuSmc2Mapper.selectBusiMcuSmc2List(new BusiMcuSmc2());
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(busiSmc2s)){
            for (BusiMcuSmc2 busiSmc2 : busiSmc2s) {
                Smc2Bridge smc2Bridge = new Smc2Bridge(busiSmc2);
                Smc2BridgeCache.getInstance().init(smc2Bridge);
                if(smc2Bridge.isAvailable()){
                    Smc2SubscribleTask smc2SubscribleTask = new Smc2SubscribleTask(smc2Bridge.getSubscribeServiceEx(),smc2Bridge);
                    Thread thread = new Thread(smc2SubscribleTask);
                    thread.setName("Smc2SubscribleTask-THREAD");
                    thread.start();
                }
            }
        }

    }




    private void processUpdateParticipant(Smc2ConferenceContext conferenceContext,  SmcParitipantsStateRep.ContentDTO contentDTO, boolean updateMediaInfo) {
        IBusiSmc2HistoryConferenceService busiSmc2HistoryConferenceService = BeanFactory.getBean(IBusiSmc2HistoryConferenceService.class);
        busiSmc2HistoryConferenceService.updateBusiHistoryParticipant(conferenceContext, contentDTO, updateMediaInfo);
    }
}
