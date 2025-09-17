package com.paradisecloud.fcm.common.model;

public class MinutesParam {

    private String lang;
    private String transType;
    private Integer transStrategy;
    private String targetLang;
    private String punc;
    private String pd;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public Integer getTransStrategy() {
        return transStrategy;
    }

    public void setTransStrategy(Integer transStrategy) {
        this.transStrategy = transStrategy;
    }

    public String getTargetLang() {
        return targetLang;
    }

    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    }

    public String getPunc() {
        return punc;
    }

    public void setPunc(String punc) {
        this.punc = punc;
    }

    public String getPd() {
        return pd;
    }

    public void setPd(String pd) {
        this.pd = pd;
    }

    @Override
    public String toString() {
        return "MinutesParam{" +
                "lang='" + lang + '\'' +
                ", transType='" + transType + '\'' +
                ", transStrategy=" + transStrategy +
                ", targetLang='" + targetLang + '\'' +
                ", punc='" + punc + '\'' +
                ", pd='" + pd + '\'' +
                '}';
    }
}
