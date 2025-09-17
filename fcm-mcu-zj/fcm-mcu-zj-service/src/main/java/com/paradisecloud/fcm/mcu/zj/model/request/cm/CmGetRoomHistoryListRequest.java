package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CmGetRoomHistoryListRequest extends CommonRequest {

    private Integer from_dtm;
    private Integer to_dtm;
    private String tenant_id;

    public Integer getFrom_dtm() {
        return from_dtm;
    }

    public void setFrom_dtm(Integer from_dtm) {
        this.from_dtm = from_dtm;
    }

    public Integer getTo_dtm() {
        return to_dtm;
    }

    public void setTo_dtm(Integer to_dtm) {
        this.to_dtm = to_dtm;
    }

    public String getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(String tenant_id) {
        this.tenant_id = tenant_id;
    }
}
