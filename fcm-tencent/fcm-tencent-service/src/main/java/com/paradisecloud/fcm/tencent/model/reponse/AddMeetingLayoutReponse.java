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
 * @date 2023/7/19 9:43
 */
@NoArgsConstructor
@Data
public class AddMeetingLayoutReponse  extends BaseResponse implements Serializable {

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
        @SerializedName("layout_name")
        private String layoutName;
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
            @SerializedName("polling_setting")
            private PollingSettingDTO pollingSetting;
            @Expose
            @SerializedName("user_seat_list")
            private List<UserSeatListDTO> userSeatList;

            @NoArgsConstructor
            @Data
            public static class PollingSettingDTO {
                @Expose
                @SerializedName("polling_interval")
                private Integer pollingInterval;
                @Expose
                @SerializedName("polling_interval_unit")
                private Integer pollingIntervalUnit;
                @Expose
                @SerializedName("ignore_user_absence")
                private Boolean ignoreUserAbsence;
                @Expose
                @SerializedName("ignore_user_novideo")
                private Boolean ignoreUserNovideo;
            }

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
                @SerializedName("video_type")
                private Integer videoType;
                @Expose
                @SerializedName("user_list")
                private List<UserListDTO> userList;

                @NoArgsConstructor
                @Data
                public static class UserListDTO {
                    @Expose
                    @SerializedName("userid")
                    private String userid;
                    @Expose
                    @SerializedName("username")
                    private String username;
                }
            }
        }
    }
}
