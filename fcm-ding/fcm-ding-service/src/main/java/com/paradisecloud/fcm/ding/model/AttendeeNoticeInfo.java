package com.paradisecloud.fcm.ding.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/5/19 11:31
 */
@NoArgsConstructor
@Data
public class AttendeeNoticeInfo {


    private String conferenceId;
    private Integer type;
    private Integer size;
    private List<ChangeListDTO> changeList;

    @NoArgsConstructor
    @Data
    public static class ChangeListDTO {
        private String id;
        private String participantId;
        private Boolean online;
        private Boolean calling;
        private Boolean dataOnline;
        private Boolean pureData;
        private Boolean voice;
        private Boolean mute;
        private Boolean quiet;
        private Boolean videoMute;
        private Boolean siteVideoMute;
        private Boolean important;
        private Integer videoSwitchAttribute;
        private Integer volume;
        private ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfo;
        private Integer callFailReason;
        private Boolean captionSet;
        private Boolean handUp;
        private Boolean unObserved;
        private String mcuName;
        private Boolean supportAiSubtitleCapability;
        private Boolean mcuSwitch;
        private Boolean tp;
        private Boolean displayCaption;
        private String name;
        private Boolean terminalOnline;
        private Boolean raise_hands_state;
        private Boolean screen_shared_state;
        private Boolean host;
        private String nick_name;
    }
}
