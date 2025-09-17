package com.paradisecloud.fcm.service.im.tencent.models.sns;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMResult;

public class GroupDeleteResult extends QCloudIMResult {
    @JSONField(name = "CurrentSequence")
    private int currentSequence;

    public int getCurrentSequence() {
        return currentSequence;
    }

    public void setCurrentSequence(int currentSequence) {
        this.currentSequence = currentSequence;
    }
}
