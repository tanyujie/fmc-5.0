package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CmQuerySysAllResourceStatisticsRequest extends CommonRequest {
    /**
     * require_info : ["get_statics_info","get_statics_mr_info","get_statics_room_info","get_concurrent_mr_tendency","get_system_resource_statistics","get_statics_concurrent_info"]
     * cmdid : get_many_show_infos
     */

    private String cmdid;
    private String[] require_info;

    public String getCmdid() {
        return cmdid;
    }

    public void setCmdid(String cmdid) {
        this.cmdid = cmdid;
    }

    public String[] getRequire_info() {
        return require_info;
    }

    public void setRequire_info(String[] require_info) {
        this.require_info = require_info;
    }
}
