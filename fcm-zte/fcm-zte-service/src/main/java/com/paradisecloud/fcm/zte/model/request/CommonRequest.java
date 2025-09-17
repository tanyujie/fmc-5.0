package com.paradisecloud.fcm.zte.model.request;

public class CommonRequest {

    protected String mcuToken = "0";
    protected String mcuUserToken = "0";
    protected String yourToken1 = "0";
    protected String yourToken2 = "0";
    protected String messageId = "0";
    protected String version = "1";

    public String getMcuToken() {
        return mcuToken;
    }

    public void setMcuToken(String mcuToken) {
        this.mcuToken = mcuToken;
    }

    public String getMcuUserToken() {
        return mcuUserToken;
    }

    public void setMcuUserToken(String mcuUserToken) {
        this.mcuUserToken = mcuUserToken;
    }

    public String getYourToken1() {
        return yourToken1;
    }

    public void setYourToken1(String yourToken1) {
        this.yourToken1 = yourToken1;
    }

    public String getYourToken2() {
        return yourToken2;
    }

    public void setYourToken2(String yourToken2) {
        this.yourToken2 = yourToken2;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String buildToXml() {
        return "";
    }
}
