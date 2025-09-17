package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CmGetChangesRequest extends CommonRequest {

    private int block_secs;

    public int getBlock_secs() {
        return block_secs;
    }

    public void setBlock_secs(int block_secs) {
        this.block_secs = block_secs;
    }
}
