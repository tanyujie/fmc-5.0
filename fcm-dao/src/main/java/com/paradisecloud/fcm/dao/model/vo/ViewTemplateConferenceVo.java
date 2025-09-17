package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.ViewTemplateConference;

public class ViewTemplateConferenceVo extends ViewTemplateConference {

    private boolean onlyMine;

    public boolean isOnlyMine() {
        return onlyMine;
    }

    public void setOnlyMine(boolean onlyMine) {
        this.onlyMine = onlyMine;
    }
}
