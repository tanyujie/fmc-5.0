package com.paradisecloud.fcm.tencent.model.reponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tencentcloudapi.wemeet.models.BaseResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author nj
 * @date 2023/7/19 11:05
 */
@NoArgsConstructor
@Data
public class MeetingLayoutResponse extends BaseResponse implements Serializable {

    @Expose
    @SerializedName("layout_number")
    private Integer layoutNumber;
    @Expose
    @SerializedName("selected_layout_id")
    private String selectedLayoutId;
    @Expose
    @SerializedName("layout_list")
    private List<LayoutListDTO> layoutList;

    @NoArgsConstructor
    @Data
    public static class LayoutListDTO {
        @Expose
        @SerializedName("layout_id")
        private String layoutId;
        @Expose
        @SerializedName("page_list")
        private List<PageListDTO> pageList;

        @NoArgsConstructor
        @Data
        public static class PageListDTO {
            @Expose
            @SerializedName("layout_template_id")
            private String layoutTemplateId;
            @Expose
            @SerializedName("user_seat_list")
            private List<UserSeatListDTO> userSeatList;

            @NoArgsConstructor
            @Data
            public static class UserSeatListDTO {
                @Expose
                @SerializedName("grid_id")
                private String gridId;
                @Expose
                @SerializedName("grid_type")
                private Integer gridType;
                @Expose
                @SerializedName("userid")
                private String userid;
                @Expose
                @SerializedName("uuid")
                private String uuid;
                @Expose
                @SerializedName("username")
                private String username;
                @Expose
                @SerializedName("ms_open_id")
                private String msOpenId;
            }
        }
    }
}
