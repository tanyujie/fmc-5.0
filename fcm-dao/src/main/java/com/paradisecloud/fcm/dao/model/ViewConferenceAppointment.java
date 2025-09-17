package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;

public class ViewConferenceAppointment extends BusiConferenceAppointment {

    private String mcuType;

    public String getMcuType() {
        return mcuType;
    }

    public void setMcuType(String mcuType) {
        this.mcuType = mcuType;
    }

    public String getConferenceId() {
        return EncryptIdUtil.generateConferenceId(getTemplateId(), getMcuType());
    }

    public String getApConferenceId() {
        return EncryptIdUtil.generateConferenceId(getId(), getMcuType());
    }

    @Override
    public String toString() {
        return "ViewConferenceAppointment{" +
                "mcuType='" + mcuType + '\'' +
                '}'
                + super.toString();
    }
}
