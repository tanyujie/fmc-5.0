package com.paradisecloud.fcm.service.im.tencent.models.openim;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMRequest;

import java.util.List;

public class QueryStateRequest extends QCloudIMRequest {
    @JSONField(name = "To_Account")
    private List<String> toAccount;

    public QueryStateRequest(String reqIdentifier, List<String> toAccount) {
        super(reqIdentifier);
        this.toAccount = toAccount;
    }

    public QueryStateRequest(List<String> toAccount) {
        this.toAccount = toAccount;
    }

    public List<String> getToAccount() {
        return toAccount;
    }

    public void setToAccount(List<String> toAccount) {
        this.toAccount = toAccount;
    }
}
