package com.paradisecloud.com.fcm.smc.modle;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/9/20 14:11
 */
@Data
@NoArgsConstructor
public class DetailConference {

    private ConferenceState conferenceState;
    private ConferenceUiParam conferenceUiParam;
    private String monitorNumber;
}
