package com.paradisecloud.smc3.model;

import com.paradisecloud.smc3.model.request.AppointmentConference;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SmcAppointmentConferenceContext extends SmcConferenceContextBase {

    private SmcCheckInService checkInService;
    private AppointmentConference conference;
    private SmcMultiConferenceService multiConferenceService;
    private SmcConferenceTemplate.StreamServiceDTO streamService;
    private SmcConferenceTemplate.ConfPresetParamDTO confPresetParam;
    private SmcConferenceTemplate.SubtitleServiceDTO subtitleService;
    private List<?> attendees;
    private List<ParticipantRspDto> participants;
}
