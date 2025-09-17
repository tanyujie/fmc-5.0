package com.paradisecloud.fcm.mcu.zj.model.response.cm;

import com.paradisecloud.fcm.mcu.zj.model.response.CommonResponse;

import java.math.BigDecimal;

/**
 * 获取验证码返回
 */
public class CmGetVerifyCodeResponse extends CommonResponse {

    private String base64;
    private BigDecimal date_time;
    private String cert_code;

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public BigDecimal getDate_time() {
        return date_time;
    }

    public void setDate_time(BigDecimal date_time) {
        this.date_time = date_time;
    }

    public String getCert_code() {
        return cert_code;
    }

    public void setCert_code(String cert_code) {
        this.cert_code = cert_code;
    }
}
