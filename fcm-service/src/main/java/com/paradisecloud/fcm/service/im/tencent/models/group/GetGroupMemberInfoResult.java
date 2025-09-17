package com.paradisecloud.fcm.service.im.tencent.models.group;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMResult;
import com.paradisecloud.fcm.service.im.tencent.models.member.MemberListResultItem;

import java.util.List;

public class GetGroupMemberInfoResult extends QCloudIMResult {
    @JSONField(name = "MemberNum")
    private int memberNum;

    @JSONField(name = "MemberList")
    private List<MemberListResultItem> memberList;

    public int getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(int memberNum) {
        this.memberNum = memberNum;
    }

    public List<MemberListResultItem> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<MemberListResultItem> memberList) {
        this.memberList = memberList;
    }
}
