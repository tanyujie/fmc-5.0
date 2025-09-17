package com.paradisecloud.fcm.service.im.tencent.models.group;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMRequest;
import com.paradisecloud.fcm.service.im.tencent.models.member.AppDefinedData;
import com.paradisecloud.fcm.service.im.tencent.models.member.MemberListItem;

import java.util.List;

public class CreateGroupRequest extends QCloudIMRequest {
    @JSONField(name = "Owner_Account")
    private String ownerAccount;

    @JSONField(name = "Type")
    private String type;

    @JSONField(name = "Name")
    private String name;

    @JSONField(name = "Introduction")
    private String introduction;

    @JSONField(name = "Notification")
    private String notification;

    @JSONField(name = "FaceUrl")
    private String faceUrl;

    @JSONField(name = "MaxMemberCount")
    private int maxMemberCount;

    @JSONField(name = "ApplyJoinOption")
    private String applyJoinOption;

    @JSONField(name = "GroupId")
    private String groupId;

    @JSONField(name = "MemberList")
    private List<MemberListItem> memberList;

    @JSONField(name = "AppDefinedData")
    private List<AppDefinedData> appDefinedData;

    public String getOwnerAccount() {
        return ownerAccount;
    }

    public void setOwnerAccount(String ownerAccount) {
        this.ownerAccount = ownerAccount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public int getMaxMemberCount() {
        return maxMemberCount;
    }

    public void setMaxMemberCount(int maxMemberCount) {
        this.maxMemberCount = maxMemberCount;
    }

    public String getApplyJoinOption() {
        return applyJoinOption;
    }

    public void setApplyJoinOption(String applyJoinOption) {
        this.applyJoinOption = applyJoinOption;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<MemberListItem> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<MemberListItem> memberList) {
        this.memberList = memberList;
    }

    public List<AppDefinedData> getAppDefinedData() {
        return appDefinedData;
    }

    public void setAppDefinedData(List<AppDefinedData> appDefinedData) {
        this.appDefinedData = appDefinedData;
    }
}
