package com.paradisecloud.smc3.model.request;

import com.paradisecloud.smc3.model.ConferenceTimeType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppointmentConference {
    private String id;
    private String subject;
    private String chairmanPassword;
    private String guestPassword;
    private String scheduleStartTime;
    private ConferenceTimeType conferenceTimeType;
    private int duration;
    private String vmrNumber;
    private String stage;

    private Integer legacyId;

    private String category;

    private String username;
    private String accountName;
    private String organizationName;
    private Boolean active;
    private String accessCode;
    private String token;



}
