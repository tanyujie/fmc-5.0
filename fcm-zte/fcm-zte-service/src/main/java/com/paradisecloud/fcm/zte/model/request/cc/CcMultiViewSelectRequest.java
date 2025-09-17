package com.paradisecloud.fcm.zte.model.request.cc;

import com.paradisecloud.fcm.zte.model.request.CommonRequest;

/**
 * @author nj
 * @date 2024/4/11 9:27
 */
public class CcMultiViewSelectRequest extends CommonRequest {

    private String ConferenceIdentifier;
    private String TerminalIdentifier;

    private int MulitiViewGroupID;

    private int ViewNo;

    private int MediaType;


    public String getConferenceIdentifier() {
        return ConferenceIdentifier;
    }

    public void setConferenceIdentifier(String conferenceIdentifier) {
        ConferenceIdentifier = conferenceIdentifier;
    }

    public String getTerminalIdentifier() {
        return TerminalIdentifier;
    }

    public void setTerminalIdentifier(String terminalIdentifier) {
        TerminalIdentifier = terminalIdentifier;
    }

    public int getMulitiViewGroupID() {
        return MulitiViewGroupID;
    }

    public void setMulitiViewGroupID(int mulitiViewGroupID) {
        MulitiViewGroupID = mulitiViewGroupID;
    }

    public int getViewNo() {
        return ViewNo;
    }

    public void setViewNo(int viewNo) {
        ViewNo = viewNo;
    }

    public int getMediaType() {
        return MediaType;
    }

    public void setMediaType(int mediaType) {
        MediaType = mediaType;
    }

    @Override
    public String buildToXml() {
        return "";
    }
}
