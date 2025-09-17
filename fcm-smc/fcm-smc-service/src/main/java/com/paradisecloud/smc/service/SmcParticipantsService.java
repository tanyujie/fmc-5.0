package com.paradisecloud.smc.service;

import com.paradisecloud.com.fcm.smc.modle.ParticipantRspDto;
import com.paradisecloud.com.fcm.smc.modle.ParticipantStatus;
import com.paradisecloud.com.fcm.smc.modle.ParticipantStatusDto;
import com.paradisecloud.com.fcm.smc.modle.mix.CreateParticipantsReq;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/25 15:04
 */
public interface SmcParticipantsService {
    void addParticipants(CreateParticipantsReq createParticipantsReq);

    void changeParticipantsStatus(String conferenceId, List<ParticipantRspDto> participants);

    void delete(String conferenceId, List<String> participantIds);

    void changeParticipantStatusOnly(String conferenceId, String participantId, ParticipantStatus participantStatus);


    SmcParitipantsStateRep getConferencesParticipantsState(String conferenceId,int page,int size);

    void PATCHParticipantsOnly(String conferenceId, List<ParticipantStatusDto> participantStatusList);
}
