package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CmQueryRecordsInfoRequest extends CommonRequest {

    /**
     * uuids : ["2078550573344e28c0d786992cd7535b","df58f5ca55ce223be8a2d1019ce0732e","89e4e42a375b5cd1a6034e5722b76185"]
     * is_recycle : 0
     */

    private int is_recycle;
    private String[] uuids;

    public int getIs_recycle() {
        return is_recycle;
    }

    public void setIs_recycle(int is_recycle) {
        this.is_recycle = is_recycle;
    }

    public String[] getUuids() {
        return uuids;
    }

    public void setUuids(String[] uuids) {
        this.uuids = uuids;
    }
}
