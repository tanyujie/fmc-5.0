package com.paradisecloud.fcm.common.model;

import com.paradisecloud.common.core.page.PaginationData;

public class PaginationDataNew<T> extends PaginationData<T> {

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
