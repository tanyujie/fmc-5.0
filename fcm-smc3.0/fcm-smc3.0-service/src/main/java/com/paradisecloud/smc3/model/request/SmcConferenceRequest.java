package com.paradisecloud.smc3.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SmcConferenceRequest {
    private int page = 0;
    private int size = 10;
    private String keyWord;
    private Boolean active = Boolean.TRUE;
    private String organizationId;
    private Long deptId;
    private String startTime;
    private String endTime;
    private int showCurrentOrg = 1;
    private String conferenceId;
    private QueryParticipantConditionDto queryParticipantConditionDto;
}
