package com.paradisecloud.fcm.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2024/6/28 16:18
 */
@NoArgsConstructor
@Data
public class LicenseModel {

    private String sn;
    private Boolean recorder;
    private Boolean streamer;
    private String quality;
    private Integer recorderLimit;
    private Integer liveLimit;
    private Integer participantLimit;
    private Integer conferenceLimit;
    private String termianlType;
    private Integer termianlAmount;
    private String monitor;
    private Integer defaultParticipantLimit;
    private Boolean localRecoder;
    private Boolean fileMeeting;
    private String participantLimitTime;
    private Boolean schedule;
    private Boolean useRecorderLimit;
    private Integer useableSpace;
}
