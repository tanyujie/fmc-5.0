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
 * @date 2023/7/19 9:46
 */
@NoArgsConstructor
@Data
public class ChangeMeetingLayoutRequest  extends AbstractModel {
    private String meetingId;

    @Expose
    private String userid;

    @Expose
    private Integer instanceid;

    private String layoutId;
    @Expose
    @SerializedName("page_list")
    private List<PageListDTO> pageList;

    @Override
    public String getPath() {
        return "/v1/meetings/"+meetingId+"/layouts/"+layoutId;
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
            @SerializedName("username")
            private String username;
            @Expose
            @SerializedName("ms_open_id")
            private String msOpenId;
        }
    }
}
