package com.paradisecloud.fcm.mcu.plc.model.request.cc;

import com.paradisecloud.fcm.mcu.plc.model.request.CommonRequest;

public class CcGetChangesRequest extends CommonRequest {

    private int block_secs;

    public int getBlock_secs() {
        return block_secs;
    }

    public void setBlock_secs(int block_secs) {
        this.block_secs = block_secs;
    }
}
