package com.paradisecloud.com.fcm.smc.modle.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParticipantReq {

    private String name;
    private String ipProtocolType = "SIP";
    private String uri;
    private Integer rate = 1920;
}
