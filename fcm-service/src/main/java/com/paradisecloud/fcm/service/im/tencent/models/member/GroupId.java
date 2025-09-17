package com.paradisecloud.fcm.service.im.tencent.models.member;

import com.alibaba.fastjson.annotation.JSONField;

public class GroupId {
    @JSONField(name = "GroupId")
    private String groupId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
