package com.paradisecloud.fcm.service.im.tencent.models.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMRequest;

public class GetNoSpeakingRequest extends QCloudIMRequest {
    @JSONField(name = "Get_Account")
    private String getAccount;

    public String getGetAccount() {
        return getAccount;
    }

    public void setGetAccount(String getAccount) {
        this.getAccount = getAccount;
    }
}
