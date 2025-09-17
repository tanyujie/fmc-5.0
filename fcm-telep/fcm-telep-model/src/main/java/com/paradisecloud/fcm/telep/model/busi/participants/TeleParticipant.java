package com.paradisecloud.fcm.telep.model.busi.participants;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/10/13 9:58
 */
@NoArgsConstructor
@Data
public class TeleParticipant {
    private Long connectTime;
    private String conferenceName;
    private Integer videoRxLost;
    private Boolean audioRxMuted;
    private String participantProtocol;
    private String audioTxCodec;
    private Boolean videoTxWidescreen;
    private String audioRxCodec;
    private Integer videoRxReceived;
    private Integer audioRxReceived;
    private Integer audioTxSent;
    private String callState;
    private Boolean videoRxMuted;
    private Boolean initialAudioMuted;
    private String displayName;
    private Integer audioTxReportedLost;
    private Boolean activeSpeaker;
    private Integer videoTxReportedLost;
    private Boolean displayNameOverrideStatus;
    private Boolean connectPending;
    private String videoRxCodec;
    private String participantType;
    private String videoTxCodec;
    private Integer maxBitRateFromMCU;
    private Boolean initialVideoMuted;
    private String callDirection;
    private Integer connectionUniqueId;
    private Integer audioRxLost;
    private String address;
    private Integer maxBitRateToMCU;
    private Integer currentLayout;
    private String audioRxGainMode;
    private Boolean important;
    private Integer videoTxSent;
    private Boolean layoutControlEnabled;
    private String participantName;
    private Boolean audioTxMuted;
    private Boolean VideoTxMuted;
    private String focusType;
    private VideoToUse focusParticipant;
    private String autoAttendantUniqueID;
    private Boolean choose;
    private Boolean callTheRoll;
    private String operationScope;
    private String cpLayout;
}
