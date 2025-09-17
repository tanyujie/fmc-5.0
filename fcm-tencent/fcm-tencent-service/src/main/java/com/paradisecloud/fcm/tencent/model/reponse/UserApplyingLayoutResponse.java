package com.paradisecloud.fcm.tencent.model.reponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tencentcloudapi.wemeet.models.BaseResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/7/19 9:27
 */
@NoArgsConstructor
@Data
public class UserApplyingLayoutResponse extends BaseResponse {

    @Expose
    @SerializedName("selected_layout_id")
    private String selectedLayoutId;
    @Expose
    @SerializedName("layout_name")
    private String layoutName;
    @Expose
    @SerializedName("layout_type")
    private Integer layoutType;
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
        @SerializedName("enable_polling")
        private Boolean enablePolling;
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
            @SerializedName("user_list")
            private List<UserListDTO> userList;

            @NoArgsConstructor
            @Data
            public static class UserListDTO {
                @Expose
                @SerializedName("username")
                private String username;
            }
        }
    }
}
