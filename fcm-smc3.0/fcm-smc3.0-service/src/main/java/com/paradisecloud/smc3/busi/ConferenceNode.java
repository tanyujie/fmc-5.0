package com.paradisecloud.smc3.busi;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/12/1 14:43
 */
@Data
@NoArgsConstructor
public class ConferenceNode {
    private String conferenceId;
    private String name;
    private String parentConferenceId;
}
