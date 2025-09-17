package com.paradisecloud.fcm.mcu.kdc.model.response.cm;

import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.mcu.kdc.model.response.CommonResponse;
import org.json.JSONObject;
import org.json.XML;

import java.time.ZoneId;
import java.util.Date;

public class CmGetMcuTimeResponse extends CommonResponse {

    private Date mcuTime;

    public Date getMcuTime() {
        return mcuTime;
    }

    public void setMcuTime(Date mcuTime) {
        this.mcuTime = mcuTime;
    }

}
