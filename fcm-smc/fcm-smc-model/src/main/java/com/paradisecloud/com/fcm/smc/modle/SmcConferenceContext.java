package com.paradisecloud.com.fcm.smc.modle;

import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/17 10:08
 */
@NoArgsConstructor
@Data
public class SmcConferenceContext extends SmcConferenceContextBase {

    private SmcCheckInService checkInService;
    private SmcConference conference;
    private SmcMultiConferenceService multiConferenceService;
    private SmcConferenceTemplate.StreamServiceDTO streamService;
    private SmcConferenceTemplate.ConfPresetParamDTO confPresetParam;
    private SmcConferenceTemplate.SubtitleServiceDTO subtitleService;
    private List<?> attendees;
    private List<ParticipantRspDto> participants;

    private DetailConference detailConference;
    private List<SmcParitipantsStateRep.ContentDTO> content;
    private String monitorNumber;
    private int rate;
    private String number;
}
