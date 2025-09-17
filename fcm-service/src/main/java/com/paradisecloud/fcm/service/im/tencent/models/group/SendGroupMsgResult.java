package com.paradisecloud.fcm.service.im.tencent.models.group;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMResult;

public class SendGroupMsgResult extends QCloudIMResult {
    @JSONField(name = "MsgTime")
    private long msgTime;

    @JSONField(name = "MsgSeq")
    private int msgSeq;

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }

    public int getMsgSeq() {
        return msgSeq;
    }

    public void setMsgSeq(int msgSeq) {
        this.msgSeq = msgSeq;
    }
}
