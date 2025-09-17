package com.paradisecloud.fcm.service.conference.operation;

public class BaseAttendeeOperation {

    /**
     * 上级会议操作
     */
    private boolean upCascadeOperate;
    /**
     * 上级会议广播
     */
    private boolean upCascadeBroadcast;
    /**
     * 上级会议轮询
     */
    private boolean upCascadePolling;
    /**
     * 上级会议点名
     */
    private boolean upCascadeRollCall;

    public boolean isUpCascadeOperate() {
        return upCascadeOperate;
    }

    public void setUpCascadeOperate(boolean upCascadeOperate) {
        this.upCascadeOperate = upCascadeOperate;
    }

    public boolean isUpCascadeBroadcast() {
        return upCascadeBroadcast;
    }

    public void setUpCascadeBroadcast(boolean upCascadeBroadcast) {
        this.upCascadeBroadcast = upCascadeBroadcast;
    }

    public boolean isUpCascadePolling() {
        return upCascadePolling;
    }

    public void setUpCascadePolling(boolean upCascadePolling) {
        this.upCascadePolling = upCascadePolling;
    }

    public boolean isUpCascadeRollCall() {
        return upCascadeRollCall;
    }

    public void setUpCascadeRollCall(boolean upCascadeRollCall) {
        this.upCascadeRollCall = upCascadeRollCall;
    }
}
