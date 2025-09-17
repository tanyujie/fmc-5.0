package com.paradisecloud.fcm.telep.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/10/13 14:47
 */
@Data
@NoArgsConstructor
public class ParticipantModifyRequest {
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
}
