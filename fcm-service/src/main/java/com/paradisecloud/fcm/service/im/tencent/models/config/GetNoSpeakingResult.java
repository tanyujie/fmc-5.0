package com.paradisecloud.fcm.service.im.tencent.models.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMResult;

public class GetNoSpeakingResult extends QCloudIMResult {
    @JSONField(name = "C2CmsgNospeakingTime")
    private long c2cMsgNoSpeakingTime;

    @JSONField(name = "GroupmsgNospeakingTime")
    private long groupMsgNoSpeakingTime;

    public long getC2cMsgNoSpeakingTime() {
        return c2cMsgNoSpeakingTime;
    }

    public void setC2cMsgNoSpeakingTime(long c2cMsgNoSpeakingTime) {
        this.c2cMsgNoSpeakingTime = c2cMsgNoSpeakingTime;
    }

    public long getGroupMsgNoSpeakingTime() {
        return groupMsgNoSpeakingTime;
    }

    public void setGroupMsgNoSpeakingTime(long groupMsgNoSpeakingTime) {
        this.groupMsgNoSpeakingTime = groupMsgNoSpeakingTime;
    }
}
