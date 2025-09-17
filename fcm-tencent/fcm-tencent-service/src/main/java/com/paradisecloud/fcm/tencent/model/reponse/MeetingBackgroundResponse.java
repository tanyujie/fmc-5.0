package com.paradisecloud.fcm.tencent.model.reponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tencentcloudapi.wemeet.models.BaseResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/7/19 10:14
 */
@NoArgsConstructor
@Data
public class MeetingBackgroundResponse extends BaseResponse {


    @Expose
    @SerializedName("background_number")
    private Integer backgroundNumber;
    @Expose
    @SerializedName("selected_background_id")
    private String selectedBackgroundId;
    @Expose
    @SerializedName("background_list")
    private List<BackgroundListDTO> backgroundList;

    @NoArgsConstructor
    @Data
    public static class BackgroundListDTO {
        @Expose
        @SerializedName("background_id")
        private String backgroundId;
        @Expose
        @SerializedName("image_md5")
        private String imageMd5;
    }
}
