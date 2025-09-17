package com.paradisecloud.fcm.telep.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/10/17 11:02
 */
@Data
@NoArgsConstructor
public class ParticipantFecc {
    private String conferenceName;
    private String autoAttendantUniqueID;
    private String participantName;
    private String participantProtocol;
    private String participantType;
    private String direction;

}
