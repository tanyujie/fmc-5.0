package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;

public class ViewTemplateParticipant extends BusiTemplateParticipant {

    private String mcuType;

    public String getMcuType() {
        return mcuType;
    }

    public void setMcuType(String mcuType) {
        this.mcuType = mcuType;
    }

    public String getConferenceId() {
        return EncryptIdUtil.generateConferenceId(getTemplateConferenceId(), getMcuType());
    }

    @Override
    public String toString() {
        return "ViewTemplateParticipant{" +
                "mcuType='" + mcuType + '\'' +
                '}'
                + super.toString();
    }
}
