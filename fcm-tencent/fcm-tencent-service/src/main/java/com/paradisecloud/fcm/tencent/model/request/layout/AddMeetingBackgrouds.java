package com.paradisecloud.fcm.tencent.model.request.layout;



import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

import java.util.List;

/**
 * @author nj
 * @date 2023/7/19 10:03
 */

public class AddMeetingBackgrouds extends AbstractModel {

    private String meetingId;
    private String userid;
    private Integer instanceid;
    private Integer default_image_order;
    private List<ImageListDTO> image_list;


    @Override
    public String getPath() {
        return "/v1/meetings/"+meetingId+"/backgrounds";
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


    public static class ImageListDTO {
        private String image_md5;
        private String image_url;

        public String getImage_md5() {
            return image_md5;
        }

        public void setImage_md5(String image_md5) {
            this.image_md5 = image_md5;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }
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

    public Integer getDefault_image_order() {
        return default_image_order;
    }

    public void setDefault_image_order(Integer default_image_order) {
        this.default_image_order = default_image_order;
    }

    public List<ImageListDTO> getImage_list() {
        return image_list;
    }

    public void setImage_list(List<ImageListDTO> image_list) {
        this.image_list = image_list;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }
}
