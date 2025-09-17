package com.paradisecloud.fcm.mcu.kdc.model.response.cm;

import com.paradisecloud.fcm.mcu.kdc.model.response.CommonResponse;

public class CmTokenResponse extends CommonResponse {

    private String account_token;

    public String getAccount_token() {
        return account_token;
    }

    public void setAccount_token(String account_token) {
        this.account_token = account_token;
    }
}
