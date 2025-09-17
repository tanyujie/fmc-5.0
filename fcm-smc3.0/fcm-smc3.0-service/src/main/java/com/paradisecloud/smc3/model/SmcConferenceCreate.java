package com.paradisecloud.smc3.model;

import com.paradisecloud.smc3.model.response.SmcCreateTemplateRep;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nj
 * @date 2023/9/21 14:55
 */
@NoArgsConstructor
@Data
public class SmcConferenceCreate {

    private SmcConferenceIntAccessCode conference;
    private SmcMultiConferenceServiceCreate multiConferenceService;
 //   private SmcConferenceTemplate.StreamServiceDTO streamService;
    private SmcConferenceTemplate.ConfPresetParamDTO confPresetParam;
    private SmcConferenceTemplate.SubtitleServiceDTO subtitleService;
    private List<?> attendees= new ArrayList<>();
    private List<ParticipantRspDto> participants=new ArrayList<>();
    private SmcCreateTemplateRep.CheckInServiceDTO checkInService;
    private SmcConferenceTemplate.StreamServiceDTO streamService;
}
