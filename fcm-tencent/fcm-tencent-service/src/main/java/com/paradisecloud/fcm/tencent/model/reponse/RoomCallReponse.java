package com.paradisecloud.fcm.tencent.model.reponse;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tencentcloudapi.wemeet.models.BaseResponse;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author nj
 * @date 2024/3/12 17:14
 */
@NoArgsConstructor
@Data
public class RoomCallReponse  extends BaseResponse {

    @Expose
    @SerializedName("invite_id")
    private String inviteId;

}
