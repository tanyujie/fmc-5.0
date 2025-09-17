package com.paradisecloud.fcm.tencent.model.request.layout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2023/7/31 10:03
 */
@NoArgsConstructor
@Data
public class ChangeAdancedMeetingLayoutRequest extends AbstractModel {

    private String meetingId;
    @Expose
    @SerializedName("instanceid")
    private Integer instanceid;
    private String layoutId;

    @Override
    public String getPath() {
        return "/v1/meetings/"+meetingId+"/advanced-layouts/"+layoutId;
    }

    @Override
    public String getBody() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.disableHtmlEscaping();
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = gsonBuilder.create();
        return gson.toJson(this);
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("application/json");
    }

    @Override
    public HttpMethodEnum getMethod() {
        return HttpMethodEnum.PUT;
    }


    @Expose
    @SerializedName("operator_id")
    private String operatorId;
    @Expose
    @SerializedName("operator_id_type")
    private Integer operatorIdType;
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
            @SerializedName("polling_interval_unit")
            private Integer pollingIntervalUnit;
            @Expose
            @SerializedName("polling_interval")
            private Integer pollingInterval;
            @Expose
            @SerializedName("ignore_user_novideo")
            private Boolean ignoreUserNovideo;
            @Expose
            @SerializedName("ignore_user_absence")
            private Boolean ignoreUserAbsence;
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
                @Expose
                @SerializedName("ms_open_id")
                private String msOpenId;
            }
        }
    }
}
