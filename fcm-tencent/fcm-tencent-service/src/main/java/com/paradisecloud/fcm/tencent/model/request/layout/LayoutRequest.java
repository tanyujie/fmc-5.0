package com.paradisecloud.fcm.tencent.model.request.layout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;


import java.util.List;

/**
 *
 * 对当前会议添加高级自定义布局，支持批量添加。
 * 用户座次设置需设置参会成员。
 * 单个会议最多允许添加20个布局。
 * 目前暂不支持 OAuth2.0 鉴权访问。
 * 目前仅会应用于 H.323/SIP 终端。
 * @author nj
 * @date 2023/7/14 11:23
 *
 */
public class LayoutRequest extends AbstractModel {

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
    @SerializedName("layout_list")
    private List<LayoutListDTO> layoutList;

    private String meetingId;

    @Override
    public String getPath() {
        return "/v1/meetings/"+meetingId+"/advanced-layouts";
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
        return HttpMethodEnum.POST;
    }


    public static class LayoutListDTO {
        @Expose
        @SerializedName("layout_name")
        private String layoutName;
        @Expose
        @SerializedName("page_list")
        private List<PageListDTO> pageList;

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

                public Integer getPollingIntervalUnit() {
                    return pollingIntervalUnit;
                }

                public void setPollingIntervalUnit(Integer pollingIntervalUnit) {
                    this.pollingIntervalUnit = pollingIntervalUnit;
                }

                public Integer getPollingInterval() {
                    return pollingInterval;
                }

                public void setPollingInterval(Integer pollingInterval) {
                    this.pollingInterval = pollingInterval;
                }

                public Boolean getIgnoreUserNovideo() {
                    return ignoreUserNovideo;
                }

                public void setIgnoreUserNovideo(Boolean ignoreUserNovideo) {
                    this.ignoreUserNovideo = ignoreUserNovideo;
                }

                public Boolean getIgnoreUserAbsence() {
                    return ignoreUserAbsence;
                }

                public void setIgnoreUserAbsence(Boolean ignoreUserAbsence) {
                    this.ignoreUserAbsence = ignoreUserAbsence;
                }


            }


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


                public static class UserListDTO {
                    @Expose
                    @SerializedName("userid")
                    private String userid;
                    @Expose
                    @SerializedName("username")
                    private String username;

                    public String getUserid() {
                        return userid;
                    }

                    public void setUserid(String userid) {
                        this.userid = userid;
                    }

                    public String getUsername() {
                        return username;
                    }

                    public void setUsername(String username) {
                        this.username = username;
                    }
                }

                public String getGridId() {
                    return gridId;
                }

                public void setGridId(String gridId) {
                    this.gridId = gridId;
                }

                public Integer getGridType() {
                    return gridType;
                }

                public void setGridType(Integer gridType) {
                    this.gridType = gridType;
                }

                public Integer getVideoType() {
                    return videoType;
                }

                public void setVideoType(Integer videoType) {
                    this.videoType = videoType;
                }

                public List<UserListDTO> getUserList() {
                    return userList;
                }

                public void setUserList(List<UserListDTO> userList) {
                    this.userList = userList;
                }
            }
        }

        public String getLayoutName() {
            return layoutName;
        }

        public void setLayoutName(String layoutName) {
            this.layoutName = layoutName;
        }

        public List<PageListDTO> getPageList() {
            return pageList;
        }

        public void setPageList(List<PageListDTO> pageList) {
            this.pageList = pageList;
        }
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getOperatorIdType() {
        return operatorIdType;
    }

    public void setOperatorIdType(Integer operatorIdType) {
        this.operatorIdType = operatorIdType;
    }

    public Integer getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(Integer instanceid) {
        this.instanceid = instanceid;
    }

    public List<LayoutListDTO> getLayoutList() {
        return layoutList;
    }

    public void setLayoutList(List<LayoutListDTO> layoutList) {
        this.layoutList = layoutList;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }
}
