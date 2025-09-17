package com.paradisecloud.fcm.service.im.tencent.models.ologin;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMResult;

import java.util.List;

public class MultiAccountImportResult extends QCloudIMResult {
    @JSONField(name = "FailAccounts")
    private List<String> failAccounts;

    public MultiAccountImportResult() {
        super();
    }

    public List<String> getFailAccounts() {
        return failAccounts;
    }

    public void setFailAccounts(List<String> failAccounts) {
        this.failAccounts = failAccounts;
    }
}
