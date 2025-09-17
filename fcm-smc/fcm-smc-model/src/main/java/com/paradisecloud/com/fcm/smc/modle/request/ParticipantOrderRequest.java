package com.paradisecloud.com.fcm.smc.modle.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/2/28 15:21
 */
@Data
@NoArgsConstructor
public class ParticipantOrderRequest {
    private String conferenceId;
    private Boolean set;
    private List<String> participantIdList;
}
