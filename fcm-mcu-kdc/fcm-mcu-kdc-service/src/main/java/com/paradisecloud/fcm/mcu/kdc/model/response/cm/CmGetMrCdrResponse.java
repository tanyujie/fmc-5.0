package com.paradisecloud.fcm.mcu.kdc.model.response.cm;

import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.mcu.kdc.model.response.CommonResponse;
import org.json.JSONObject;
import org.json.XML;

public class CmGetMrCdrResponse extends CommonResponse {

    private String id;
    private Integer endReasonType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getEndReasonType() {
        return endReasonType;
    }

    public void setEndReasonType(Integer endReasonType) {
        this.endReasonType = endReasonType;
    }

}
