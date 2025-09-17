package com.paradisecloud.smc3.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/9/20 11:26
 */
@NoArgsConstructor
@Data
public class ParticipantState {


    private String participantId;
    private Boolean online=Boolean.FALSE;
    private Boolean calling=Boolean.FALSE;
    private Boolean dataOnline=Boolean.FALSE;
    private Boolean pureData=Boolean.FALSE;
    private Boolean voice=Boolean.FALSE;
    private Boolean mute=Boolean.TRUE;
    private Boolean quiet=Boolean.FALSE;
    private Boolean videoMute=Boolean.FALSE;
    private Boolean siteVideoMute=Boolean.TRUE;
    private Boolean important=Boolean.FALSE;
    private Integer videoSwitchAttribute;
    private Integer volume;
    private Integer callFailReason;
    private Boolean captionSet=Boolean.FALSE;
    private Boolean handUp=Boolean.FALSE;
    private Boolean unObserved=Boolean.FALSE;
    private Boolean supportAiSubtitleCapability;
    private Boolean displayCaption=Boolean.FALSE;
    private Boolean mcuSwitch=Boolean.FALSE;
    private Boolean tp=Boolean.FALSE;
    private ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfo;
}
