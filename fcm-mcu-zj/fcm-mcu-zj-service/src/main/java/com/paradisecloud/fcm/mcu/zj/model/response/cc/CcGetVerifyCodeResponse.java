package com.paradisecloud.fcm.mcu.zj.model.response.cc;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

/**
 * 获取验证码返回
 */
public class CcGetVerifyCodeResponse extends CommonResponse {

    private String base64;
    private float datetime;

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public float getDatetime() {
        return datetime;
    }

    public void setDatetime(float datetime) {
        this.datetime = datetime;
    }
}
