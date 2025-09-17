package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

public class CmSearchDepartmentsResponse extends CommonResponse {

    private Integer[] department_ids;

    public Integer[] getDepartment_ids() {
        return department_ids;
    }

    public void setDepartment_ids(Integer[] department_ids) {
        this.department_ids = department_ids;
    }
}
