package com.paradisecloud.com.fcm.smc.modle.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/26 16:55
 */
@Data
@NoArgsConstructor
public class TemplateParticipantReq {
    private String id;
    private String name;
    private String ipProtocolType="SIP";
    private String participantSourceType="NEW_CRETE";
    private String uri;
    private Integer rate=0;
    private String entryUuid;
    private Long deptId;
    private Long terminalId;
}
