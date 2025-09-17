package com.paradisecloud.fcm.web.model.tele;

import com.paradisecloud.fcm.telep.model.request.ParticipantFecc;

/**
 * @author nj
 * @date 2022/10/17 11:10
 */

public class ParticipantFeccReqVo {
    private ParticipantFecc participantFecc;
    private String uri;

    public ParticipantFecc getParticipantFecc() {
        return participantFecc;
    }

    public void setParticipantFecc(ParticipantFecc participantFecc) {
        this.participantFecc = participantFecc;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
