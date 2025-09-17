package com.paradisecloud.smc3.invoker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/3/21 11:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmcConferenceMessage {

    private String conferenceId;

    private String stage;
}
