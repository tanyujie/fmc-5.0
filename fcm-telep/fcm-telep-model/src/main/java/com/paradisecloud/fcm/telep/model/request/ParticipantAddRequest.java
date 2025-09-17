package com.paradisecloud.fcm.telep.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/12/29 10:00
 */
@Data
@NoArgsConstructor
public class ParticipantAddRequest {

    private String conferenceName;
    private String participantName;
    private Boolean addResponse;
    /**
     * h323, sip, or vnc.
     */
    private String participantProtocol;
    /**
     *  One of: by_address or ad_hoc .
     */
    private String participantType;
    /**
     *  The address of the endpoint; may be hostname, IP address,
     * E.164 number, SIP URI, or H.323 ID.
     */
    private String address;

    private String uri;
    private Boolean audioRxMuted = Boolean.TRUE;
}
