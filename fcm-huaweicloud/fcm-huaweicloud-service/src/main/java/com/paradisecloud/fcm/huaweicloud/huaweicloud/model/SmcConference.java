package com.paradisecloud.fcm.huaweicloud.huaweicloud.model;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author nj
 * @date 2022/8/16 15:19
 */
@NoArgsConstructor
@Data
public class SmcConference {
    private String id;
    private String confId;

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

}
