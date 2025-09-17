package com.paradisecloud.fcm.service.conference.cascade;

import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;

/**
 * @author nj
 * @date 2023/8/2 9:18
 */
public abstract class AbstractConference {

    private  String uri;
    private  String conferenceId;

    /**
     * 开始会议
     * @param mcuType
     * @param templateId
     */
    public abstract void startConference(McuType mcuType, Long templateId);

    /**
     * 级联处理
     * @param name
     * @param uri
     * @param ipProtocolType
     */
     public abstract void processCascade(String name,String uri,String ipProtocolType);

    /**
     * 结束会议
     */
    public abstract void end();

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }


    /**
     * 开始会议
     * @param templateId
     */
    public abstract BaseConferenceContext startConference(Long templateId);

    /**
     * 结束会议
     */
    public abstract BaseConferenceContext endConference(String conferenceId, int endType);

    /**
     * 获取会议详情
     * @param templateId
     * @return
     */
    public abstract BaseConferenceContext buildTemplateConferenceContext(Long templateId);

    /**
     * 重呼
     * @param conferenceId
     * @param attendeeId
     */
    public abstract void recall(String conferenceId, String attendeeId);

    /**
     * 选看
     * @param conferenceId
     * @param attendeeId
     */
    public abstract void chooseSee(String conferenceId, String attendeeId);
    /**
     * 点名
     * @param conferenceId
     * @param attendeeId
     */
    public void callTheRoll(String conferenceId, String attendeeId){

    }
    /**
     * 选看
     * @param conferenceId
     * @param attendeeId
     * @param upCascadeOperate 是否为上级会议操作
     * @param upCascadeBroadcast 是否为上级会议广播
     * @param upCascadePolling 是否为上级会议轮询
     */
    public abstract void chooseSee(String conferenceId, String attendeeId, boolean upCascadeOperate, boolean upCascadeBroadcast, boolean upCascadePolling, boolean upCascadeRollCall);

    public  void invite(String conferenceId,String conferenceName, String selfCallTencent,String password){

    }
}
