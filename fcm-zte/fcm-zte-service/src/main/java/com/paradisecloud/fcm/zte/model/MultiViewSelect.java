package com.paradisecloud.fcm.zte.model;

/**
 * @author nj
 * @date 2024/4/10 15:31
 */
public class MultiViewSelect {

    private String TerminalIdentifier;
    /**
     * 0~24
     */
    private int ViewNo;
    /**
     * 多画面号，暂填写为
     * 0；不填默认为 0
     */
    private int MulitiViewGroupID;
    /**
     * 1：主视频
     * 2：辅视频
     * 不填默认为主视频
     */
    private int mediaType;

    public String getTerminalIdentifier() {
        return TerminalIdentifier;
    }

    public void setTerminalIdentifier(String terminalIdentifier) {
        TerminalIdentifier = terminalIdentifier;
    }

    public int getViewNo() {
        return ViewNo;
    }

    public void setViewNo(int viewNo) {
        ViewNo = viewNo;
    }

    public int getMulitiViewGroupID() {
        return MulitiViewGroupID;
    }

    public void setMulitiViewGroupID(int mulitiViewGroupID) {
        MulitiViewGroupID = mulitiViewGroupID;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }
}
