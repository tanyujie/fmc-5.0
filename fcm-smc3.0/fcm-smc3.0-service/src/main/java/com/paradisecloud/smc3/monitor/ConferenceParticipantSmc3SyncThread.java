package com.paradisecloud.smc3.monitor;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.dao.model.BusiTerminal;

import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.smc3.busi.AttendeeCountingStatistics;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.McuAttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.SelfCallAttendeeSmc3;
import com.paradisecloud.smc3.busi.attende.TerminalAttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.updateprocessor.OtherAttendeeUpdateProcessor;
import com.paradisecloud.smc3.busi.updateprocessor.RegisteredAttendeeUpdateProcessor;
import com.paradisecloud.smc3.busi.updateprocessor.SelfCallAttendeeNewProcessor;
import com.paradisecloud.smc3.busi.utils.AttendeeUtils;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.ChooseMultiPicInfo;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3HistoryConferenceService;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author nj
 * @date 2023/9/5 15:21
 */
public class ConferenceParticipantSmc3SyncThread extends Thread implements InitializingBean {

    private  final Logger logger = LoggerFactory.getLogger(getClass());


    private Smc3ConferenceContext conferenceContext;

    public ConferenceParticipantSmc3SyncThread(Smc3ConferenceContext conferenceContext) {
        this.conferenceContext = conferenceContext;
    }

    @Override
    public void run() {


            try {

            if (conferenceContext != null && conferenceContext.isStart()) {
                Smc3Bridge smc3Bridge = conferenceContext.getSmc3Bridge();
                String res;
                if(Objects.equals(ConstAPI.NORMAL,conferenceContext.getCategory())){
                     res = smc3Bridge.getSmcParticipantsInvoker().getConferencesParticipantsState(conferenceContext.getSmc3conferenceId(), 0, 1000, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }else {
                    res = smc3Bridge.getSmcParticipantsInvoker().getConferencesParticipantsStateCascade(conferenceContext.getSmc3conferenceId(),false, 0, 1000, smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                }

                SmcParitipantsStateRep smcParitipantsStateRep = JSON.parseObject(res, SmcParitipantsStateRep.class);
                List<SmcParitipantsStateRep.ContentDTO> contents = smcParitipantsStateRep.getContent();
                if(CollectionUtils.isNotEmpty(contents)){
                    for (SmcParitipantsStateRep.ContentDTO content : contents) {
                        participantProcess(content, conferenceContext);

                    }
                }
                HashMap<Object, Object> countMap = new HashMap<>();
                countMap.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE,countMap);
            }


            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    public synchronized void participantProcess(SmcParitipantsStateRep.ContentDTO participant, Smc3ConferenceContext conferenceContext) {


        if (conferenceContext != null) {
            AttendeeSmc3 a = AttendeeUtils.matchAttendee(conferenceContext, participant);
            if (a != null) {
                a.setConferenceNumber(conferenceContext.getConferenceNumber());
                a.setSmcParticipant(participant);
                AttendeeUtils.updateByParticipant(conferenceContext, participant, a);
                processUpdateParticipant(conferenceContext, a, false);
            } else if (participant.getState().getOnline()) {
                new SelfCallAttendeeNewProcessor(participant, conferenceContext).process();
            }

        }
    }



    private void processUpdateParticipant(Smc3ConferenceContext conferenceContext, AttendeeSmc3 attendeeSmc3, boolean updateMediaInfo) {
        IBusiMcuSmc3HistoryConferenceService smc3HistoryConferenceService = BeanFactory.getBean(IBusiMcuSmc3HistoryConferenceService.class);
        smc3HistoryConferenceService.updateBusiHistoryParticipant(conferenceContext, attendeeSmc3, updateMediaInfo);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
