package com.paradisecloud.smc.service.impl;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.com.fcm.smc.modle.*;
import com.paradisecloud.com.fcm.smc.modle.mix.CreateParticipantsReq;
import com.paradisecloud.com.fcm.smc.modle.response.SmcErrorResponse;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.smc.cache.modle.SmcApiInvoker;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.smc.service.SmcParticipantsService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/25 15:04
 */
@Service
public class SmcParticipantsServiceImpl implements SmcParticipantsService {

    @Override
    public void addParticipants(CreateParticipantsReq createParticipantsReq) {
        SmcBridge bridge = SmcBridgeCache.getInstance().getConferenceBridge().get(createParticipantsReq.getConferenceId());
        bridge.getSmcParticipantsInvoker().createParticipants(createParticipantsReq.getConferenceId(),createParticipantsReq.getParticipants(),bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void changeParticipantsStatus(String conferenceId, List<ParticipantRspDto> participants) {
        SmcBridge bridge = SmcBridgeCache.getInstance().getConferenceBridge().get(conferenceId);

        bridge.getSmcParticipantsInvoker().PATCHParticipantsMap(conferenceId,participants,bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void delete(String conferenceId,List<String> participantIds) {
        SmcBridge bridge = SmcBridgeCache.getInstance().getConferenceBridge().get(conferenceId);

        bridge.getSmcParticipantsInvoker().deleteParticipants(conferenceId,participantIds,bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    @Override
    public void changeParticipantStatusOnly(String conferenceId, String participantId, ParticipantStatus participantStatus) {
        SmcBridge bridge = SmcBridgeCache.getInstance().getConferenceBridge().get(conferenceId);
        bridge.getSmcParticipantsInvoker().conferencesParticipantStatusOnly(conferenceId,participantId,participantStatus,bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

    }

    @Override
    public SmcParitipantsStateRep getConferencesParticipantsState(String conferenceId, int page, int size) {
        SmcBridge bridge = SmcBridgeCache.getInstance().getConferenceBridge().get(conferenceId);
        if(bridge==null){
            throw new CustomException("会议不存在",110330);
        }
        String res = bridge.getSmcParticipantsInvoker().getConferencesParticipantsState(conferenceId, page, size, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        SmcApiInvoker.errorString(res);
        return  JSON.parseObject(res, SmcParitipantsStateRep.class);
    }

    @Override
    public void PATCHParticipantsOnly(String conferenceId, List<ParticipantStatusDto> participantStatusList) {
        SmcBridge bridge = SmcBridgeCache.getInstance().getConferenceBridge().get(conferenceId);
        bridge.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceId,participantStatusList,bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

}
