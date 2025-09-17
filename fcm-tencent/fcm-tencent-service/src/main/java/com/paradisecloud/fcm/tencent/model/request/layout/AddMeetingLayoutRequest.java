package com.paradisecloud.fcm.tencent.model.request.layout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

import java.io.Serializable;
import java.util.List;

/**
 * 对成功预定的会议添加会议布局，支持多个布局的添加，每个布局支持多页模板，默认选中第一页模板作为该布局的首页进行展示。
 * 用户座次设置区分会前和会中两种方式：会前只允许设置邀请者成员，会中只允许设置参会成员。
 * 一场会议最多添加10个布局，添加成功返回新增的会议布局信息。
 * 目前暂不支持 OAuth2.0 鉴权访问
 * @author nj
 * @date 2023/7/19 9:33
 */

public class AddMeetingLayoutRequest  extends AbstractModel implements Serializable {

    private String meetingId;
    @Expose
    @SerializedName("userid")
    private String userid;
    @Expose
    @SerializedName("instanceid")
    private Integer instanceid;
    @Expose
    @SerializedName("default_layout_order")
    private Integer default_layout_order;
    @Expose
    @SerializedName("layout_list")
    private List<LayoutListDTO> layoutList;

    @Override
    public String getPath() {
        return "/v1/meetings/"+meetingId+"/layouts";
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
        @SerializedName("page_list")
        private List<PageListDTO> pageList;


        public static class PageListDTO {
            @Expose
            @SerializedName("layout_template_id")
            private String layoutTemplateId;
            @Expose
            @SerializedName("user_seat_list")
            private List<UserSeatListDTO> userSeatList;


            public static class UserSeatListDTO {
                @Expose
                @SerializedName("grid_id")
                private String gridId;
                @Expose
                @SerializedName("grid_type")
                private Integer gridType;
                @Expose
                @SerializedName("userid")
                private String useridX;
                @Expose
                @SerializedName("username")
                private String username;
                @Expose
                @SerializedName("ms_open_id")
                private String msOpenId;

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

                public String getUseridX() {
                    return useridX;
                }

                public void setUseridX(String useridX) {
                    this.useridX = useridX;
                }

                public String getUsername() {
                    return username;
                }

                public void setUsername(String username) {
                    this.username = username;
                }

                public String getMsOpenId() {
                    return msOpenId;
                }

                public void setMsOpenId(String msOpenId) {
                    this.msOpenId = msOpenId;
                }
            }

            public String getLayoutTemplateId() {
                return layoutTemplateId;
            }

            public void setLayoutTemplateId(String layoutTemplateId) {
                this.layoutTemplateId = layoutTemplateId;
            }

            public List<UserSeatListDTO> getUserSeatList() {
                return userSeatList;
            }

            public void setUserSeatList(List<UserSeatListDTO> userSeatList) {
                this.userSeatList = userSeatList;
            }
        }

        public List<PageListDTO> getPageList() {
            return pageList;
        }

        public void setPageList(List<PageListDTO> pageList) {
            this.pageList = pageList;
        }
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public Integer getDefault_layout_order() {
        return default_layout_order;
    }

    public void setDefault_layout_order(Integer default_layout_order) {
        this.default_layout_order = default_layout_order;
    }
}
