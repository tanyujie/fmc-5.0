package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.message;

import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.MultiPicInfoNotify;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2024/2/28 14:47
 */
@NoArgsConstructor
@Data
public class ConfDynamicInfoNotifyMessage {
    private String state;
    private String endTime;
    private String mode;
    private Integer vas;
    private Integer picNum;
    private Integer lock;
    private Integer mute;
    private Integer lockSharing;
    private Integer canUnmute;
    private Integer chairView;
    private Integer recState;
    private Integer aiRecState;
    private Integer enableShareSetting;
    private Integer callInRestriction;
    private Integer audienceCallInRestriction;
    private Integer forbiddenChat;
    private Integer realTimeSubtitle;
    private Integer audienceTotalCount;
    private Integer simultaneousInterpretation;
    private List<?> lanChannels;
    private Boolean allowAudience;
    private Boolean pause;
    private Integer partViewNum;
    private Integer maxPartViewNum;
    private String audienceVideoLayout;
    private Integer recOccurred;
    private Integer clientRecState;
    private Integer clientRecMode;
    private Integer waitingRoomState;
    private Integer showAudienceMode;
    private Integer audienceCountMultiple;
    private Integer audienceCountReal;
    private String confID;
    private String msgID;
    private Integer msgMode;
    private Long version;
    private Long createTime;
    private String action;
    private MultiPicInfoNotify multiPic;
}
