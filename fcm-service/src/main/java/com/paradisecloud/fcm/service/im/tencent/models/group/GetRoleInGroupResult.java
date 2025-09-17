package com.paradisecloud.fcm.service.im.tencent.models.group;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMResult;

import java.util.List;

public class GetRoleInGroupResult extends QCloudIMResult {
    public static class UserId {
        @JSONField(name = "Member_Account")
        private String memberAccount;

        @JSONField(name = "Role")
        private String role;

        public String getMemberAccount() {
            return memberAccount;
        }

        public void setMemberAccount(String memberAccount) {
            this.memberAccount = memberAccount;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    @JSONField(name = "UserIdList")
    private List<UserId> userIdList;

    public List<UserId> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<UserId> userIdList) {
        this.userIdList = userIdList;
    }
}
