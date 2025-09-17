package com.paradisecloud.fcm.service.im.tencent.models.sns;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMResult;
import com.paradisecloud.fcm.service.im.tencent.models.friend.ResultItem;

import java.util.List;

public class FriendAddResult extends QCloudIMResult {
    @JSONField(name = "ResultItem")
    private List<ResultItem> resultItem;

    @JSONField(name = "Fail_Account")
    private List<String> failAccount;

    @JSONField(name = "Invalid_Account")
    private List<String> invalidAccount;

    public List<ResultItem> getResultItem() {
        return resultItem;
    }

    public void setResultItem(List<ResultItem> resultItem) {
        this.resultItem = resultItem;
    }

    public List<String> getFailAccount() {
        return failAccount;
    }

    public void setFailAccount(List<String> failAccount) {
        this.failAccount = failAccount;
    }

    public List<String> getInvalidAccount() {
        return invalidAccount;
    }

    public void setInvalidAccount(List<String> invalidAccount) {
        this.invalidAccount = invalidAccount;
    }
}
