package com.paradisecloud.fcm.service.im.tencent.models.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMRequest;

import java.util.List;

public class GetAppInfoRequest extends QCloudIMRequest {
    @JSONField(name = "RequestField")
    private List<String> requestField;

    public List<String> getRequestField() {
        return requestField;
    }

    public void setRequestField(List<String> requestField) {
        this.requestField = requestField;
    }
}
