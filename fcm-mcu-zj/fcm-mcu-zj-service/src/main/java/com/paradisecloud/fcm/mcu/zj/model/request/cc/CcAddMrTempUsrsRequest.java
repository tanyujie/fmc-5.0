package com.paradisecloud.fcm.mcu.zj.model.request.cc;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CcAddMrTempUsrsRequest extends CommonRequest {

    /**
     * call_url : 172.16.100.56
     * usr_type : 2
     */

    private String call_url;
    private int usr_type;
    private String disp_name;

    public String getCall_url() {
        return call_url;
    }

    public void setCall_url(String call_url) {
        this.call_url = call_url;
    }

    public int getUsr_type() {
        return usr_type;
    }

    public void setUsr_type(int usr_type) {
        this.usr_type = usr_type;
    }

    public String getDisp_name() {
        return disp_name;
    }

    public void setDisp_name(String disp_name) {
        this.disp_name = disp_name;
    }
}
