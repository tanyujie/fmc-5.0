package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.utils.bean.BeanUtils;

public class BusiRecordSettingForm extends BusiRecordSetting {

    private String host;
    private boolean spaceUsable;

    public BusiRecordSettingForm() {
    }

    public BusiRecordSettingForm(BusiRecordSetting busiRecordSetting) {
        BeanUtils.copyBeanProp(this, busiRecordSetting);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isSpaceUsable() {
        return spaceUsable;
    }

    public void setSpaceUsable(boolean spaceUsable) {
        this.spaceUsable = spaceUsable;
    }
}
