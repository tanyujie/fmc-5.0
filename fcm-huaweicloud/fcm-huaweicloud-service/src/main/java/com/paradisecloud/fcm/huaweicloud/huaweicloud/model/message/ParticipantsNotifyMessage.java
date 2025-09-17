package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2024/2/28 15:42
 */
@NoArgsConstructor
@Data
public class ParticipantsNotifyMessage {


    private List<DataDTO> data;
    private String confID;
    private String msgID;
    private Integer msgMode;
    private Long version;
    private Long createTime;
    private String action;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String pid;
        private Integer mode;
        private PinfoMapDTO pinfoMap;

        @NoArgsConstructor
        @Data
        public static class PinfoMapDTO {
            private String camerastate;
            private String orgId;
            private String lockedView;
            private String mute;
            private String accountId;
            private String localrec;
            private String tel;
            private String allowClientRec;
            private String state;
            private String share;
            private String anonymous;
            private String addtime;
            private String name;
            private String clientRecState;
            private String clientCapabilities;
            private String roleSwitchOver;
            private String rtcUserId;
            private String clientLoginType;
            private String T;
            private String account;
            private String broadcast;
            private String isCohost;
            private String hand;
            private String isSvc;
            private String rollcall;
            private String M;
            private String video;
            private String role;
            private String thirdaccount;
            private String inviteShareState;
            private String userAgent;
        }
    }
}
