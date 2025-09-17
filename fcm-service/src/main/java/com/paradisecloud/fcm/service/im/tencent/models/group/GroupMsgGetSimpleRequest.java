package com.paradisecloud.fcm.service.im.tencent.models.group;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMRequest;

public class GroupMsgGetSimpleRequest extends QCloudIMRequest {
    @JSONField(name = "GroupId")
    private String groupId;

    @JSONField(name = "ReqMsgNumber")
    private int reqMsgNumber;

    @JSONField(name = "ReqMsgSeq")
    private Long reqMsgSeq;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getReqMsgNumber() {
        return reqMsgNumber;
    }

    public void setReqMsgNumber(int reqMsgNumber) {
        this.reqMsgNumber = reqMsgNumber;
    }

    public Long getReqMsgSeq() {
        return reqMsgSeq;
    }

    public void setReqMsgSeq(Long reqMsgSeq) {
        this.reqMsgSeq = reqMsgSeq;
    }
}
