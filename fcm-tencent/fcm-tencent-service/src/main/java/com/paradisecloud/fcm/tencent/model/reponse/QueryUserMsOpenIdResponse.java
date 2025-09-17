package com.paradisecloud.fcm.tencent.model.reponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tencentcloudapi.wemeet.models.BaseResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author nj
 * @date 2023/7/17 10:26
 */
@NoArgsConstructor
@Data
public class QueryUserMsOpenIdResponse  extends BaseResponse implements Serializable {

    @Expose
    @SerializedName("meeting_id")
    private String meetingId;
    @Expose
    @SerializedName("ms_open_id")
    private String msOpenId;
}
