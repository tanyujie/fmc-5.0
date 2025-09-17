package com.paradisecloud.fcm.service.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2024/7/12 16:45
 */
@Data
@NoArgsConstructor
public class CloudConference {

    private String cascadeConferenceId;
    private String name;
    private String conferenceNumber;
    private String cascadeMcuType;

}
