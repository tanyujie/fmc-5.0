package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CmStopMrRequest extends CommonRequest {

    /**
     * mr_id : 8001177
     * reason : 会管上结束会议，操作者：超级管理员(admin@51vmr.com)
     */

    private String mr_id;
    private String reason;

    public String getMr_id() {
        return mr_id;
    }

    public void setMr_id(String mr_id) {
        this.mr_id = mr_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
