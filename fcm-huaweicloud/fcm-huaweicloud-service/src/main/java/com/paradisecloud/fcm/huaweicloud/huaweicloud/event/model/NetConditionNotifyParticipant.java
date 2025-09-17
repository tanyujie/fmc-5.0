package com.paradisecloud.fcm.huaweicloud.huaweicloud.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2024/3/7 14:10
 */
@NoArgsConstructor
@Data
public class NetConditionNotifyParticipant {

    private Integer mode;
    private String participantID;
    private String netInfo;
    private String lostPacketRate;
    private String delay;
    private String jitter;
    private String fluxIn;
    private String fluxOut;
    private String audioOutLossPacketRate;
    private String videoInLossPacketRate;
    private String videoOutLossPacketRate;
    private String assistVideoInLossPacketRate;
    private String assistVideoOutLossPacketRate;
    private String outJitter;
    private String videoFluxIn;
    private String videoFluxOut;
    private String assistVideoFluxIn;
    private String assistVideoFluxOut;
    private Integer bandwidth;
    private String audioCodecType;
    private String videoCodecType;
}
