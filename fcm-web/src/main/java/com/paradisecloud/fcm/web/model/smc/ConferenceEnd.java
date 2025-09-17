package com.paradisecloud.fcm.web.model.smc;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/10/21 15:01
 */
@Data
@NoArgsConstructor
public class ConferenceEnd {
    private String conferenceId;
    private Boolean closeCascade;
}
