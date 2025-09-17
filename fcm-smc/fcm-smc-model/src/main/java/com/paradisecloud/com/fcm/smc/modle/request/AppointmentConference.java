package com.paradisecloud.com.fcm.smc.modle.request;

import com.paradisecloud.com.fcm.smc.modle.ConferenceTimeType;
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
