package com.paradisecloud.smc3.model.mix;

import com.paradisecloud.smc3.model.StartConference;
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
