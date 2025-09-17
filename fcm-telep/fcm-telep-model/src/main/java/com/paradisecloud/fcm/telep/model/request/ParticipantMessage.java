package com.paradisecloud.fcm.telep.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/10/17 14:05
 */
@Data
@NoArgsConstructor
public class ParticipantMessage {
    private String conferenceName;
    private String autoAttendantUniqueID;
    private String participantName;
    /**
     * h323, sip, or vnc.
     */
    private String participantProtocol;
    private String participantType;
    /**
     * string (255) The string to send to the participant.
     */
    private String message;
    /**
     * Specifies where to show the message in relation to the
     * screen. The message is always horizontally centred, and is
     * vertically positioned to either top, middle (default), or
     * bottom.
     */
    private String verticalPosition;
    /**
     * The period of time, in seconds, for which this item is active
     * (up to a maximum value of 8639999).
     */
    private Integer durationSeconds;
}
