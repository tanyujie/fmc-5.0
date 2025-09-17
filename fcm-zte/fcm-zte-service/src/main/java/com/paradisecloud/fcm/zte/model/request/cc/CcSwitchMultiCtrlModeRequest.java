package com.paradisecloud.fcm.zte.model.request.cc;

import com.paradisecloud.fcm.zte.model.request.CommonRequest;

public class CcSwitchMultiCtrlModeRequest extends CommonRequest {

    private String ConferenceIdentifier;
    /**
     * auto:自动控制多画面
     * manual:手动控制
     */
    private String MultiViewCtrlMode;


    public String getConferenceIdentifier() {
        return ConferenceIdentifier;
    }

    public void setConferenceIdentifier(String conferenceIdentifier) {
        ConferenceIdentifier = conferenceIdentifier;
    }

    public String getMultiViewCtrlMode() {
        return MultiViewCtrlMode;
    }

    public void setMultiViewCtrlMode(String multiViewCtrlMode) {
        MultiViewCtrlMode = multiViewCtrlMode;
    }

    @Override
    public String buildToXml() {

        String xml = "";

        return xml;
    }
}
