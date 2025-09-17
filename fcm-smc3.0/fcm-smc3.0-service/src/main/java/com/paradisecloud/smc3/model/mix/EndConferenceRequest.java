package com.paradisecloud.smc3.model.mix;

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
