package com.paradisecloud.fcm.service.im.tencent.models.sns;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMRequest;

public class FriendDeleteAllRequest extends QCloudIMRequest {
    @JSONField(name = "From_Account")
    private String fromAccount;

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }
}
