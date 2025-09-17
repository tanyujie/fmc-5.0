package com.paradisecloud.smc.dao.model;

import com.paradisecloud.common.core.model.BaseEntity;

public class BusiSmcDeptConference extends BaseEntity {

    private Long id;

    private Long deptId;


    private String smcConferenceId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getSmcConferenceId() {
        return smcConferenceId;
    }

    public void setSmcConferenceId(String smcConferenceId) {
        this.smcConferenceId = smcConferenceId;
    }

    @Override
    public String toString() {
        return "busiSmcDeptConference{" +
                "id=" + id +
                ", deptId=" + deptId +
                ", smcConferenceId='" + smcConferenceId + '\'' +
                '}';
    }
}
