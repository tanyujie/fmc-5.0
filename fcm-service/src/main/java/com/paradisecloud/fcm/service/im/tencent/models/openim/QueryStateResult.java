package com.paradisecloud.fcm.service.im.tencent.models.openim;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.service.im.tencent.models.QCloudIMResult;

import java.util.List;

public class QueryStateResult extends QCloudIMResult {
    public static class OnlineStatus {
        @JSONField(name = "To_Account")
        private String toAccount;

        @JSONField(name = "State")
        private String state;

        public String getToAccount() {
            return toAccount;
        }

        public void setToAccount(String toAccount) {
            this.toAccount = toAccount;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    @JSONField(name = "QueryResult")
    private List<OnlineStatus> queryResult;

    public List<OnlineStatus> getQueryResult() {
        return queryResult;
    }

    public void setQueryResult(List<OnlineStatus> queryResult) {
        this.queryResult = queryResult;
    }
}
