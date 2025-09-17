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
 * @date 2023/7/14 14:55
 */
@NoArgsConstructor
@Data
public class LayoutTemplatesResponse extends BaseResponse implements Serializable {

    @Expose
    @SerializedName("layout_template_number")
    private Integer layoutTemplateNumber;
    @Expose
    @SerializedName("layout_template_list")
    private List<LayoutTemplateListDTO> layoutTemplateList;

    @NoArgsConstructor
    @Data
    public static class LayoutTemplateListDTO {
        @Expose
        @SerializedName("layout_template_id")
        private String layoutTemplateId;
        @Expose
        @SerializedName("thumbnail_url")
        private String thumbnailUrl;
        @Expose
        @SerializedName("picture_url")
        private String pictureUrl;
        @Expose
        @SerializedName("render_rule")
        private String renderRule;
    }
}
