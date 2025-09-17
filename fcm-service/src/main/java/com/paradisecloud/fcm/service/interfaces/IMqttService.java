/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IMqttService.java
 * Package     : com.paradisecloud.fcm.fme.conference.interfaces
 * @author sinhy 
 * @since 2021-11-18 14:05
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.service.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiLive;
import com.paradisecloud.fcm.dao.model.BusiLiveDept;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;

import java.util.List;

/**  
 * <pre>Mqtt业务处理</pre>
 * @author sinhy
 * @since 2021-11-18 14:05
 * @version V1.0  
 */
public interface IMqttService
{
    
    /**
     * 向推送会议信息到mqtt模块
     * @author sinhy
     * @since 2021-11-18 14:07 
     * @param conferenceId 会议号
     * @param confencePassword 会议密码
     * @param meetingJoinTerminals 需要入会的终端列表
     * @param liveWatchTerminals 观看直播的终端列表 
     * void
     */
    void pushConferenceInfo(String conferenceId, String confencePassword, List<? extends BaseAttendee> meetingJoinTerminals, List<? extends BaseAttendee> liveWatchTerminals);
    
    /**
     * 结束会议
     * @author sinhy
     * @since 2021-12-13 14:57 
     * @param conferenceNumber
     * @param meetingJoinTerminals
     * @param liveWatchTerminals void
     */
    void endConference(String conferenceNumber, List<? extends BaseAttendee> meetingJoinTerminals, List<? extends BaseAttendee> liveWatchTerminals, BaseConferenceContext conferenceContext);
    
    /**
     * 接受举手
     * @author sinhy
     * @since 2021-12-07 10:27 
     * @param conferenceNumber
     * @param ta
     */
    void acceptRaiseHand(String conferenceNumber, BaseAttendee ta);
    
    /**
     * 拒绝举手
     * @author sinhy
     * @since 2021-12-07 10:27 
     * @param conferenceNumber
     * @param ta
     */
    void rejectRaiseHand(String conferenceNumber, BaseAttendee ta);

    /**
     * <pre>设置横幅</pre>
     * @author sinhy
     * @since 2022-01-12 20:58 
     * @param cn
     * @param ta
     * @param params void
     */
    void setBanner(String cn, BaseAttendee ta, JSONObject params);

    /**
     * 邀请终端进入直播
     * @param contextKey
     * @param streaming
     * @param streamUrl
     * @param templateConferenceId
     */
    void isInviteLiveTerminal(String contextKey, Boolean streaming , String streamUrl, Long templateConferenceId);

    /**
     * <pre>会议列表</pre>
     * @param snList
     */
    void conferenceList(List snList);

    /**
     * <pre>会议列表缓存</pre>
     * @param id
     * @return busiConferenceAppointment
     */
    BusiConferenceAppointment getAppointmentCache(String id);

    /**
     * <pre>会议列表缓存put方法</pre>
     * @param id
     * @param busiConferenceAppointment
     * @return busiConferenceAppointment
     */
    BusiConferenceAppointment putAppointmentCache(String id,BusiConferenceAppointment busiConferenceAppointment);

    /**
     * <pre>会议列表缓存remove方法</pre>
     * @param id
     * @param busiConferenceAppointment
     * @return 是否删除成功
     */
    Boolean removeAppointmentCache(String id,BusiConferenceAppointment busiConferenceAppointment);

    /**
     * <pre>会议列表缓存update方法</pre>
     * @param id
     * @param busiConferenceAppointment
     * @return BusiConferenceAppointment
     */
    BusiConferenceAppointment updateAppointmentCache(String id,BusiConferenceAppointment busiConferenceAppointment);

    /**
     * 获取直播URL列表
     * @param deptId
     * @param busiLiveDept
     * @return
     */
    List<BusiLive> getStreamUrlList(long deptId, BusiLiveDept busiLiveDept);

    /**
     * 推送消息给终端
     * @param terminalTopic
     * @param action
     * @param jObj
     * @param clientId
     * @param messageId
     */
    void responseTerminal(String terminalTopic, String action, JSONObject jObj, String clientId, String messageId);

    /**
     * 推送会议信息给目标终端
     * @param conferenceContext
     */
    void sendConferenceInfoToPushTargetTerminal(BaseConferenceContext conferenceContext);

    /**
     * 推送会议信息给目标终端
     * @param conferenceContext
     * @param newPresenter 新主持人
     */
    void sendConferenceInfoToPushTargetTerminal(BaseConferenceContext conferenceContext, Long newPresenter);

    /**
     * 推送入会消息给目标终端
     * @param conferenceContext
     * @param attendee
     */
    void sendJoinConferenceToPushTargetTerminal(BaseConferenceContext conferenceContext, BaseAttendee attendee);

    /**
     * 推送离会消息给目标终端
     * @param conferenceContext
     * @param attendee
     */
    void sendLeftConferenceToPushTargetTerminal(BaseConferenceContext conferenceContext, BaseAttendee attendee);

    /**
     * 推送会议即将结束
     * @param conferenceContext
     */
    void sendConferenceComingToEndMessage(BaseConferenceContext conferenceContext, long min, long s);

    /**
     * 推送轮询终端消息
     * @param attendee
     */
    void sendPollingAttendMessage(BaseAttendee attendee, BaseConferenceContext conferenceContext, boolean pollingAttend);

    /**
     * 邀请终端入会
     * @param terminalAttendee
     * @param conferenceContext
     * @param joinType
     */
    void inviteAttendeeJoinConference(BaseAttendee terminalAttendee, BaseConferenceContext conferenceContext, Integer joinType);

    /**
     * 向电子门牌推送注册信息
     * @param doorplateSn 电子门牌sn
     */
    void pushRegister(String doorplateSn);

    /**
     * 向电子门牌推送会议室信息到
     * @author sinhy
     * @since 2021-11-18 14:07
     * @param doorplateSn 电子门牌sn
     * void
     */
    void pushMeetingRoomInfo(String doorplateSn);

    /**
     * 推送消息
     *
     * @param topic 主题
     * @param msg 消息
     */
    void pushMsg(String topic, byte[] msg);
}
