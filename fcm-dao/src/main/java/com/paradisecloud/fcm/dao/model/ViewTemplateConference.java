package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;

public class ViewTemplateConference extends BusiTemplateConference {

    private String mcuType;
    private String tenantId;
    private boolean modeConference;

    public String getMcuType() {
        return mcuType;
    }

    public void setMcuType(String mcuType) {
        this.mcuType = mcuType;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public boolean isModeConference() {
        return modeConference;
    }

    public void setModeConference(boolean modeConference) {
        this.modeConference = modeConference;
    }

    public String getConferenceId() {
        return EncryptIdUtil.generateConferenceId(getId(), getMcuType());
    }

    public String getUpCascadeConferenceId() {
        return EncryptIdUtil.generateConferenceId(getUpCascadeId(), getUpCascadeMcuType());
    }

    @Override
    public String toString() {
        return "ViewTemplateConference{" +
                "mcuType='" + mcuType + '\'' +
                '}'
                + super.toString();
    }
}
