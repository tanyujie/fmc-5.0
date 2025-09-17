package com.paradisecloud.fcm.mcu.kdc.model.request.cm;

import com.paradisecloud.fcm.mcu.kdc.model.request.CommonRequest;

public class CmGetMrCdrRequest extends CommonRequest {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
