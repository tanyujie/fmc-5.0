package com.paradisecloud.fcm.tencent.model.request.layout;

import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

/**
 * @author nj
 * @date 2023/7/14 15:02
 */
public class QueryLayoutTemplatesAllRequest extends AbstractModel {

    @Override
    public String getPath() {
        return "/v1/layout-templates";
    }

    @Override
    public String getBody() {
        return null;
    }

    @Override
    public MediaType contentType() {
        return null;
    }

    @Override
    public HttpMethodEnum getMethod() {
        return HttpMethodEnum.GET;
    }

}
