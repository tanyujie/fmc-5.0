package com.paradisecloud.fcm.tencent.model.reponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tencentcloudapi.wemeet.models.BaseResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2023/7/25 10:56
 */
@NoArgsConstructor
@Data
public class MRAstatusResponse extends BaseResponse {

    @Expose
    @SerializedName("ms_open_id")
    private String msOpenId;
    @Expose
    @SerializedName("instanceid")
    private Integer instanceid;
    @Expose
    @SerializedName("user_role")
    private Integer userRole;
    @Expose
    @SerializedName("IP")
    private String IP;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("audio_state")
    private Boolean audioState;
    @Expose
    @SerializedName("video_state")
    private Boolean videoState;
    @Expose
    @SerializedName("screen_shared_state")
    private Boolean screenSharedState;
    @Expose
    @SerializedName("default_layout")
    private Integer defaultLayout;
    @Expose
    @SerializedName("raise_hands_state")
    private Boolean raiseHandsState;
}
