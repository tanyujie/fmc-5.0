package com.paradisecloud.com.fcm.smc.modle.mix;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/25 14:10
 */
@Data
@NoArgsConstructor
public class EndConferenceRequest {
    private String  endConferenceId;
    private String  ConferenceType;
}
