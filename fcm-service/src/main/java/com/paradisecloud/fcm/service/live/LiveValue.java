package com.paradisecloud.fcm.service.live;

import java.util.ArrayList;
import java.util.List;

public class LiveValue {

    private String pushId;
    private String pushUrl;
    private String pullUrl;
    private List<String> pullUrlList;

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public String getPullUrl() {
        return pullUrl;
    }

    public void setPullUrl(String pullUrl) {
        this.pullUrl = pullUrl;
    }

    public List<String> getPullUrlList() {
        if (pullUrlList == null) {
            pullUrlList = new ArrayList<>();
        }
        return pullUrlList;
    }

    public void setPullUrlList(List<String> pullUrlList) {
        this.pullUrlList = pullUrlList;
    }
}
