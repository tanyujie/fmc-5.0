/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IAttendeeService.java
 * Package     : com.paradisecloud.fcm.fme.service.interfaces
 * @author lilinhai 
 * @since 2021-02-05 17:28
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.smc2.setvice2.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.RaiseHandStatus;
import com.paradisecloud.fcm.service.conference.attendee.BaseFixedParamValue;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;

import java.util.List;

/**  
 * <pre>参会者业务处理类</pre>
 * @author lilinhai
 * @since 2021-02-05 17:28
 * @version V1.0  
 */
public interface IAttendeeSmc2Service
{

    /**
     * 与会者重呼
     * @author lilinhai
     * @since 2021-02-05 17:29 
     * @param conferenceId
     * @param attendeeId void
     */
    void recall(String conferenceId, String attendeeId);
    
    /**
     * 呼叫与会者
     * @author lilinhai
     * @since 2021-02-05 17:56
     * @param attendee
     */
    public void callAttendee(AttendeeSmc2 attendee);
    
    /**
     * 挂断与会者
     * @author lilinhai
     * @since 2021-02-05 18:01 
     * @param conferenceId
     * @param attendeeId void
     */
    void hangUp(String conferenceId, String attendeeId);
    
    /**
     * 变更主会场
     * @author lilinhai
     * @since 2021-02-09 11:22  void
     */
    void changeMaster(String conferenceId, String attendeeId);
    
    /**
     * 选看
     * @author lilinhai
     * @since 2021-02-09 11:22  void
     */
    void chooseSee(String conferenceId, String attendeeId);
    /**
     * 选看
     * @author lilinhai
     * @since 2021-02-09 11:22  void
     */
    void chooseSee(String conferenceId, String attendeeId, boolean upCascadeOperate, boolean upCascadeBroadcast, boolean upCascadePolling, boolean upCascadeRollCall);


    /**
     * 默认选看
     * @author lilinhai
     * @since 2021-02-09 11:22  void
     */
    void defaultChooseSee(Smc2ConferenceContext mainConferenceContext);
    
    /**
     * 与会者呼叫失败通知
     * @author lilinhai
     * @since 2021-02-08 12:49 
     * @param participantUuid void
     */
    void callAttendeeFailedNotice(String participantUuid, String reason);

    /**
     * <pre>点名</pre>
     * @author lilinhai
     * @since 2021-02-22 18:06 
     * @param conferenceId
     * @param attendeeId void
     */
    void callTheRoll(String conferenceId, String attendeeId);
    
    /**
     * 对话
     * @author sinhy
     * @since 2021-12-02 12:46 
     * @param conferenceId
     * @param attendeeId void
     */
    void talk(String conferenceId, String attendeeId);
    
    /**
     * <pre>取消点名</pre>
     * @author lilinhai
     * @since 2021-02-22 18:06 
     * @param conferenceId
     */
    void cancelCallTheRoll(String conferenceId);

    void cancelTalk(String conferenceId);
    
    void cancelCurrentOperation(Smc2ConferenceContext conferenceContext);
    
    /**
     * <pre>混音</pre>
     * @author lilinhai
     * @since 2021-02-22 18:07 
     * @param conferenceId
     * @param attendeeId
     * @see IAttendeeSmc2Service#callTheRoll(String, String)
     */
    void openMixing(String conferenceId, String attendeeId);
    
    /**
     * 接受举手
     * @author sinhy
     * @since 2021-12-07 11:19 
     * @param conferenceId
     * @param attendeeId void
     */
    void acceptRaiseHand(String conferenceId, String attendeeId);
    
    /**
     * 拒绝举手
     * @author sinhy
     * @since 2021-12-07 10:27 
     * @param conferenceId
     * @param attendeeId
     */
    void rejectRaiseHand(String conferenceId, String attendeeId);
    
    /**
     * 举手
     * @author sinhy
     * @since 2021-12-07 10:27 
     * @param conferenceId
     * @param attendeeId
     * @param raiseHandStatus void
     */
    void raiseHand(String conferenceId, String attendeeId, RaiseHandStatus raiseHandStatus);
    
    /**
     * <pre>关闭混音</pre>
     * @author lilinhai
     * @since 2021-02-22 18:07 
     * @param conferenceId
     * @param attendeeId
     * @see IAttendeeSmc2Service#callTheRoll(String, String)
     */
    void closeMixing(String conferenceId, String attendeeId);
    
    /**
     * <pre>混音</pre>
     * @author lilinhai
     * @since 2021-02-22 18:07 
     * @param conferenceId
     * @see IAttendeeSmc2Service#callTheRoll(String, String)
     */
    void openMixing(String conferenceId);
    
    /**
     * <pre>混音</pre>
     * @author lilinhai
     * @since 2021-02-22 18:07
     */
    void openMixing(Smc2ConferenceContext cc);
    
    /**
     * <pre>关闭混音</pre>
     * @author lilinhai
     * @since 2021-02-22 18:07 
     * @param conferenceId
     * @see IAttendeeSmc2Service#callTheRoll(String, String)
     */
    void closeMixing(String conferenceId);
    
    /**
     * 关闭会议中所有参会者的混音
     * @author sinhy
     * @since 2021-08-17 11:49 
     * @param cc void
     */
    void closeMixing(Smc2ConferenceContext cc, AttendeeSmc2... excludes);
    
    /**
     * <pre>开启镜头</pre>
     * @author lilinhai
     * @since 2021-02-22 18:07 
     * @param conferenceId
     * @see IAttendeeSmc2Service#callTheRoll(String, String)
     */
    void openCamera(String conferenceId);
    
    /**
     * <pre>关闭镜头</pre>
     * @author lilinhai
     * @since 2021-02-22 18:07 
     * @param conferenceId
     * @see IAttendeeSmc2Service#callTheRoll(String, String)
     */
    void closeCamera(String conferenceId);
    
    /**
     * <pre>单个开镜</pre>
     * @author lilinhai
     * @since 2021-04-25 17:26 
     * @param conferenceId
     * @param attendeeId void
     */
    void openCamera(String conferenceId, String attendeeId);

    /**
     * <pre>单个关镜</pre>
     * @author lilinhai
     * @since 2021-04-25 17:26 
     * @param conferenceId
     * @param attendeeId void
     */
    void closeCamera(String conferenceId, String attendeeId);

    /**
     * <pre>轮询</pre>
     * @author lilinhai
     * @since 2021-02-25 15:22 
     * @param conferenceId
     */
    void polling(String conferenceId);
    
    /**
     * 轮询暂停
     * @author sinhy
     * @since 2022-04-11 10:09  void
     */
    void pollingPause(String conferenceId);
    
    /**
     * 轮询恢复运行
     * @author sinhy
     * @since 2022-04-11 10:09  void
     */
    void pollingResume(String conferenceId);

    /**
     * <pre>取消轮询</pre>
     * @author lilinhai
     * @since 2021-02-22 18:06
     * @param conferenceId
     */
    void cancelPolling(String conferenceId);

    /**
     * <pre>发送消息</pre>
     * @author lilinhai
     * @since 2021-04-25 15:42 
     * @param conferenceId
     * @param jsonObject void
     */
    void sendMessage(String conferenceId, JSONObject jsonObject);
    
    /**
     * 设置横幅
     * @author sinhy
     * @since 2022-05-07 10:44 
     * @param conferenceId
     * @param jsonObject void
     */
    void setMessageBannerText(String conferenceId, JSONObject jsonObject);

    /**
     * 设置横幅
     * @author sinhy
     * @since 2022-05-07 10:44
     * @param conferenceId
     * @param text void
     */
    void setMessageBannerText(String conferenceId, String text);

    /**
     * <pre>批量邀请</pre>
     * @author lilinhai
     * @since 2021-04-26 16:29 
     * @param conferenceId
     * @param terminalIds void
     */
    void invite(String conferenceId, List<Long> terminalIds);

    /**
     * <pre>uri邀请</pre>
     * @author lilinhai
     * @since 2021-04-26 16:24 
     * @param conferenceId
     * @param jsonObj void
     */
    void invite(String conferenceId, JSONObject jsonObj);

    /**
     * <pre>参会者明细</pre>
     * @author lilinhai
     * @since 2021-04-20 11:14 
     * @param conferenceId
     * @param attendeeId void
     */
    JSONObject detail(String conferenceId, String attendeeId);

    /**
     * <pre>参会者明细</pre>
     * @author lilinhai
     * @since 2021-04-20 11:14
     * @param conferenceContext
     * @param attendee void
     */
    JSONObject detail(Smc2ConferenceContext conferenceContext, AttendeeSmc2 attendee);

    /**
     * <pre>批量开显示器</pre>
     * @author lilinhai
     * @since 2021-04-20 14:55 
     * @param conferenceId void
     */
    void openDisplayDevice(String conferenceId);

    /**
     * <pre>批量关显示器</pre>
     * @author lilinhai
     * @since 2021-04-20 14:57 
     * @param conferenceId void
     */
    void closeDisplayDevice(String conferenceId);

    /**
     * <pre>单个开显示器</pre>
     * @author lilinhai
     * @since 2021-04-20 14:58 
     * @param conferenceId
     * @param attendeeId void
     */
    void openDisplayDevice(String conferenceId, String attendeeId);

    /**
     * <pre>关显示器</pre>
     * @author lilinhai
     * @since 2021-04-20 14:59 
     * @param conferenceId
     * @param attendeeId void
     */
    void closeDisplayDevice(String conferenceId, String attendeeId);

    /**
     * <pre>移除参会者</pre>
     * @author lilinhai
     * @since 2021-05-14 10:54 
     * @param conferenceId
     * @param attendeeId void
     */
    void remove(String conferenceId, String attendeeId);

    /**
     * <pre>获取快照</pre>
     * @author sinhy
     * @since 2021-08-18 12:11 
     * @param conferenceId
     * @param attendeeId void
     * @param params 
     */
    String takeSnapshot(String conferenceId, String attendeeId, JSONObject params);

    /**
     * <pre>摄像机控制</pre>
     * @author sinhy
     * @since 2021-08-18 12:52 
     * @param conferenceId
     * @param attendeeId
     * @param params
     * @return Object
     */
    void cameraControl(String conferenceId, String attendeeId, JSONObject params);

    /**
     * <pre>辅流设置</pre>
     * @author sinhy
     * @since 2021-09-02 15:09 
     * @param conferenceId
     * @param attendeeId
     * @param params void
     */
    void presentationSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params);

    /**
     * <pre>主流设置</pre>
     * @author sinhy
     * @since 2021-09-02 15:10 
     * @param conferenceId
     * @param attendeeId
     * @param params void
     */
    void mainSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params);

    /**
     * <pre>获取CallLeg</pre>
     * @author sinhy
     * @since 2021-09-02 15:22 
     * @param conferenceId
     * @param attendeeId void
     */
    JSONObject attendeeCallLegSetting(String conferenceId, String attendeeId);

    /**
     * <pre>字母设置</pre>
     * @author sinhy
     * @since 2021-09-02 16:22 
     * @param conferenceId
     * @param attendeeId
     * @param params void
     */
    void subtitle(String conferenceId, String attendeeId, List<BaseFixedParamValue> params);

    /**
     * <pre>布局设置</pre>
     * @author sinhy
     * @since 2021-09-02 17:17 
     * @param conferenceId
     * @param attendeeId
     * @param params void
     */
    void layoutSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params);

    /**
     * <pre>高级设置</pre>
     * @author sinhy
     * @since 2021-09-02 19:57 
     * @param conferenceId
     * @param attendeeId
     * @param params void
     */
    void advanceSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params);

    /**
     * <pre>设置横幅</pre>
     * @author sinhy
     * @since 2022-01-12 20:50 
     * @param conferenceId
     * @param attendeeId
     * @param params void
     */
    void setBanner(String conferenceId, String attendeeId, JSONObject params);

    /**
     * <pre>批量设置横幅</pre>
     * @author sinhy
     * @since 2022-01-12 21:06 
     * @param conferenceId
     * @param jsonObject void
     */
    void sendBanner(String conferenceId, JSONObject jsonObject);

    /**
     * <pre>直播录制设置</pre>
     * @author sinhy
     * @since 2022-01-17 19:27 
     * @param conferenceId
     * @param attendeeId
     * @param params void
     */
    void recordStreamSetting(String conferenceId, String attendeeId, List<BaseFixedParamValue> params);

    void closeSpeaker(String conferenceId, String attendeeId);

    void openSpeaker(String conferenceId, String attendeeId);

    Object lockPresenter(String conferenceId, String participantId, Boolean lock);

    Object videoSwitchAttribute(String conferenceId, String participantId, boolean b);

    Object setVolume(String conferenceId, String participantId, int volume);

    void privateTalk(String conferenceId, JSONObject jsonObject);

    void cancelPrivateTalk(String conferenceId);
}
