package com.paradisecloud.smc3.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/9/22 8:06
 */
@NoArgsConstructor
@Data
public class SmcConferenceIntAccessCode {
    private String id;

    private String confId;

    private StartConference.PeriodConferenceTimeDTO periodConferenceTime;

    private String organizationName;

    private String accountName;

    private String subject;

    private Boolean active;

    private String conferenceTimeType;

    private String scheduleStartTime;

    private String token;

    private Integer duration;

    private String stage;

    private Integer legacyId;

    private String category;

    private String username;

    private Integer rate;

    private String chairmanPassword;

    private String guestPassword;

    private Long accessCode;

    private String vmrNumber;

}
