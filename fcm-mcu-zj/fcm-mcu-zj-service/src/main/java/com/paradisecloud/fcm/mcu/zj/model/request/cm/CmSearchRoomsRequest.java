package com.paradisecloud.fcm.mcu.zj.model.request.cm;

import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;

public class CmSearchRoomsRequest extends CommonRequest {

    private String[] filter_type;
    private Object[] filter_value;
    private String sortby;

    public String[] getFilter_type() {
        return filter_type;
    }

    public void setFilter_type(String[] filter_type) {
        this.filter_type = filter_type;
    }

    public Object[] getFilter_value() {
        return filter_value;
    }

    public void setFilter_value(Object[] filter_value) {
        this.filter_value = filter_value;
    }

    public String getSortby() {
        return sortby;
    }

    public void setSortby(String sortby) {
        this.sortby = sortby;
    }
}
