package com.paradisecloud.fcm.mcu.kdc.model.response.cm;

import com.paradisecloud.fcm.mcu.kdc.model.response.CommonResponse;

public class CmStartMrResponse extends CommonResponse {

    private String conf_id;
    private String meeting_id;
    private String description;

    public String getConf_id() {
        return conf_id;
    }

    public void setConf_id(String conf_id) {
        this.conf_id = conf_id;
    }

    public String getMeeting_id() {
        return meeting_id;
    }

    public void setMeeting_id(String meeting_id) {
        this.meeting_id = meeting_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
