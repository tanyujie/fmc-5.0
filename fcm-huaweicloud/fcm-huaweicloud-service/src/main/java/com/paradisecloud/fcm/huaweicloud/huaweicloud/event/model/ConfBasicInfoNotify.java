package com.paradisecloud.fcm.huaweicloud.huaweicloud.event.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2024/3/22 15:42
 */
@NoArgsConstructor
@Data
public class ConfBasicInfoNotify {


    private String displayID;
    private String title;
    private String startTime;
    private String guestJoinUri;
    private List<PwdsDTO> pwds;
    private Integer recType;
    private Integer media;
    private String owner;
    private String orgID;
    private Integer aiRecType;
    private Integer callInRestriction;
    private Integer audienceCallInRestriction;
    private String confMode;
    private Boolean webinar;
    private Integer realTimeSubtitle;
    private Boolean bignumMultiPicSwitch;
    private Boolean partViewSwitch;
    private Integer simultaneousInterpretation;
    private Integer supportWatermark;
    private Integer userType;
    private String ownerID;
    private String scheduledStartTime;
    private Integer supportCohost;
    private Integer supportClientRecord;
    private Boolean inviteShareSwitch;
    private String confPortalCapabilities;
    private Boolean supportWaitingRoom;
    private String confID;
    private String msgID;
    private Integer msgMode;
    private Long version;
    private Long createTime;
    private String action;

    @NoArgsConstructor
    @Data
    public static class PwdsDTO {
        private String role;
        private String pwd;
    }
}
