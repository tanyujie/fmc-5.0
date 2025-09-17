package com.paradisecloud.fcm.tencent.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会议中对 MRA 的默认布局进行设置。如果当前 MRA 已显示会议自定义布局或个性布局或焦点视频，则不支持进行默认布局设置。
 * @author nj
 * @date 2023/7/31 16:59
 */
@NoArgsConstructor
@Data
public class MraDefaultLayoutRequest extends AbstractModel {

    private String meetingId;
    @Expose
    @SerializedName("operator_id")
    private String operatorId;
    @Expose
    @SerializedName("operator_id_type")
    private Integer operatorIdType;
    @Expose
    @SerializedName("instanceid")
    private Integer instanceid;
    @Expose
    @SerializedName("default_layout")
    private Integer defaultLayout;
    @Expose
    @SerializedName("default_novideo_user")
    private Integer defaultNovideoUser;
    @Expose
    @SerializedName("user")
    private UserDTO user;

    @NoArgsConstructor
    @Data
    public static class UserDTO {
        @Expose
        @SerializedName("ms_open_id")
        private String msOpenId;
        /**
         * 用户的终端设备类型：
         *  9：voip、sip 设备
         */
        @Expose
        @SerializedName("instanceid")
        private Integer instanceid;
    }


    @Override
    public String getPath() {
        return "/v1/mra-control/meetings/"+meetingId+"/default-layout";
    }

    @Override
    public String getBody() {
        return GSON.toJson(this);
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("application/json");
    }

    @Override
    public HttpMethodEnum getMethod() {
        return HttpMethodEnum.PUT;
    }
}
