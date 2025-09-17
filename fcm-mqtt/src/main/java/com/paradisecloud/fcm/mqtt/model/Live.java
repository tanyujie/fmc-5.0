package com.paradisecloud.fcm.mqtt.model;

public class Live {
    private String name;
    private String Url;

    @Override
    public String toString() {
        return "Live{" +
                "name='" + name + '\'' +
                ", Url='" + Url + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
