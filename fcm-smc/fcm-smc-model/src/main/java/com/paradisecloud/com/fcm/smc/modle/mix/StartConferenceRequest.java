package com.paradisecloud.com.fcm.smc.modle.mix;

import com.paradisecloud.com.fcm.smc.modle.StartConference;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/25 13:39
 */
@Data
@NoArgsConstructor
public class StartConferenceRequest {

    private Long templateId;

    private StartConference startConference;
}
