package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CmQueryRecordsRequest extends CommonRequest {

    /**
     * option : public
     * filter_value :
     */

    private String option;
    private String filter_value;

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getFilter_value() {
        return filter_value;
    }

    public void setFilter_value(String filter_value) {
        this.filter_value = filter_value;
    }
}
