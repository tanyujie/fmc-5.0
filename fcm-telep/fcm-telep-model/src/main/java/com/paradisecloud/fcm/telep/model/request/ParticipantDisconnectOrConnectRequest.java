package com.paradisecloud.fcm.telep.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/10/13 15:24
 */
@Data
@NoArgsConstructor
public class ParticipantDisconnectOrConnectRequest {

    private String conferenceName;
    private String autoAttendantUniqueID;
    private String participantName;
    private String participantProtocol;
    private String participantType;
}
