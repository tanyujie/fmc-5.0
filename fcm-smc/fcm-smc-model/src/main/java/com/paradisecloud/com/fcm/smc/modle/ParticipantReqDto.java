package com.paradisecloud.com.fcm.smc.modle;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/19 10:40
 */
@NoArgsConstructor
@Data
public class ParticipantReqDto {


    private String name;
    /**
     * SIPANDH323
     */
    private String ipProtocolType = "SIPANDH323";
    private String participantSourceType = "NEW_CRETE";
    private String uri;
    private Integer rate = 0;
    private String dtmfInfo;
}
