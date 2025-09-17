/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IAttendeeService.java
 * Package     : com.paradisecloud.fcm.fme.service.interfaces
 * @author lilinhai 
 * @since 2021-02-05 17:38
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.interfaces;

import java.util.List;

import com.paradisecloud.fcm.service.conference.attendee.BaseFixedParamValue;
import org.apache.http.NameValuePair;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.RaiseHandStatus;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.fme.cache.model.enumer.ParticipantBulkOperationMode;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;

/**  
 * <pre>参会者业务处理类</pre>
 * @author lilinhai
 * @since 2021-02-05 17:38
 * @version V1.0  
 */
public interface IAttendeeService
{
    
    void updateAttendeeImportance(ConferenceContext cc, AttendeeImportance attendeeImportance, Attendee... excludes);
    
    void updateAttendeeImportance(ConferenceContext cc, Attendee attendee, AttendeeImportance attendeeImportance);
    
    /**
     * @author lilinhai
     * @since 2021-04-01 16:51 
     * @param deptId
     * @param conferenceNumber
     * @param nameValuePairs
     * @param participantIds void
     */
    void updateAttendeeAttrs(ConferenceContext cc, long deptId, String conferenceNumber, List<NameValuePair> nameValuePairs, ParticipantBulkOperationMode participantBulkOperationMode, String... participantIds);
    
    /**
     * 批量修改参会者业务参数，支持集群
     * @author lilinhai
     * @since 2021-04-19 11:45 
     * @param cc
     * @param attendees
     * @param nameValuePairs void
     */
    void updateAttendeeAttrs(ConferenceContext cc, List<Attendee> attendees, ParticipantBulkOperationMode participantBulkOperationMode, List<NameValuePair> nameValuePairs);
    
    /**
     * 与会者重呼
     * @author lilinhai
     * @since 2021-02-05 17:39 
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
    public void callAttendee(Attendee attendee);

    /**
     * rtsp协议支持呼叫
     * @param conferenceContext
     * @param attendee
     */
    public void callRtsp(ConferenceContext conferenceContext,Attendee attendee,String rtspUri);
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
     * @since 2021-02-09 11:32  void
     */
    void changeMaster(String conferenceId, String attendeeId);
    
    /**
     * 选看
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    void chooseSee(String conferenceId, String attendeeId);

    /**
     * 选看
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    void chooseSee(String conferenceId, String attendeeId, boolean upCascadeOperate, boolean upCascadeBroadcast, boolean upCascadePolling, boolean upCascadeRollCall);
    
    /**
     * 默认选看
     * @author lilinhai
     * @since 2021-02-09 11:32  void
     */
    void defaultChooseSee(ConferenceContext mainConferenceContext);
    
    /**
     * 与会者呼叫失败通知
     * @author lilinhai
     * @since 2021-02-08 13:49 
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
     * <pre>取消点名</pre>
     * @author lilinhai
     * @since 2021-02-22 18:06
     * @param conferenceId
     */
    void cancelCallTheRoll(String conferenceId);
    
    /**
     * 对话
     * @author sinhy
     * @since 2021-12-02 12:46 
     * @param conferenceId
     * @param attendeeId void
     */
    void talk(String conferenceId, String attendeeId);
    
    /**
     * <pre>取消对话</pre>
     * @author lilinhai
     * @since 2021-02-22 18:06 
     * @param conferenceId
     */
    void cancelTalk(String conferenceId);
    
    void cancelCurrentOperation(ConferenceContext conferenceContext);
    
    /**
     * <pre>混音</pre>
     * @author lilinhai
     * @since 2021-02-22 18:07 
     * @param conferenceId
     * @param attendeeId
     * @see com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService#callTheRoll(java.lang.String, java.lang.String)
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
     * @see com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService#callTheRoll(java.lang.String, java.lang.String)
     */
    void closeMixing(String conferenceId, String attendeeId);
    
    /**
     * <pre>混音</pre>
     * @author lilinhai
     * @since 2021-02-22 18:07 
     * @param conferenceId
     * @see com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService#callTheRoll(java.lang.String, java.lang.String)
     */
    void openMixing(String conferenceId);
    
    /**
     * <pre>混音</pre>
     * @author lilinhai
     * @since 2021-02-22 18:07 
     * @param cc
     * @see com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService#callTheRoll(java.lang.String, java.lang.String)
     */
    void openMixing(ConferenceContext cc);
    
    /**
     * <pre>关闭混音</pre>
     * @author lilinhai
     * @since 2021-02-22 18:07 
     * @param conferenceId
     * @see com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService#callTheRoll(java.lang.String, java.lang.String)
     */
    void closeMixing(String conferenceId);
    
    /**
     * 关闭会议中所有参会者的混音
     * @author sinhy
     * @since 2021-08-17 11:49 
     * @param cc void
     */
    void closeMixing(ConferenceContext cc, Attendee... excludes);
    
    /**
     * <pre>开启镜头</pre>
     * @author lilinhai
     * @since 2021-02-22 18:07 
     * @param conferenceId
     * @see com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService#callTheRoll(java.lang.String, java.lang.String)
     */
    void openCamera(String conferenceId);
    
    /**
     * <pre>关闭镜头</pre>
     * @author lilinhai
     * @since 2021-02-22 18:07 
     * @param conferenceId
     * @see com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService#callTheRoll(java.lang.String, java.lang.String)
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
     * @since 2021-02-25 15:32 
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
     * @since 2021-04-25 15:43
     * @param conferenceId
     * @param message
     * @param duration 秒
     */
    void sendSystemMessage(String conferenceId, String message, int duration);

    /**
     * <pre>发送消息</pre>
     * @author lilinhai
     * @since 2021-04-25 15:43 
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
     * @since 2021-04-26 16:34 
     * @param conferenceId
     * @param jsonObj void
     */
    void invite(String conferenceId, JSONObject jsonObj);

    /**
     * <pre>参会者明细</pre>
     * @author lilinhai
     * @since 2021-04-30 11:14 
     * @param conferenceId
     * @param attendeeId void
     */
    JSONObject detail(String conferenceId, String attendeeId);
    
    JSONObject toDetail(CallLeg callLeg);

    /**
     * <pre>批量开显示器</pre>
     * @author lilinhai
     * @since 2021-04-30 14:55 
     * @param conferenceId void
     */
    void openDisplayDevice(String conferenceId);

    /**
     * <pre>批量关显示器</pre>
     * @author lilinhai
     * @since 2021-04-30 14:57 
     * @param conferenceId void
     */
    void closeDisplayDevice(String conferenceId);

    /**
     * <pre>单个开显示器</pre>
     * @author lilinhai
     * @since 2021-04-30 14:58 
     * @param conferenceId
     * @param attendeeId void
     */
    void openDisplayDevice(String conferenceId, String attendeeId);

    /**
     * <pre>关显示器</pre>
     * @author lilinhai
     * @since 2021-04-30 14:59 
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
     * @since 2021-08-18 13:11 
     * @param conferenceId
     * @param attendeeId void
     * @param params 
     */
    String takeSnapshot(String conferenceId, String attendeeId, JSONObject params);

    /**
     * <pre>摄像机控制</pre>
     * @author sinhy
     * @since 2021-08-18 13:53 
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
    void presentationSetting(String conferenceId, String attendeeId, List<? extends BaseFixedParamValue> params);

    /**
     * <pre>主流设置</pre>
     * @author sinhy
     * @since 2021-09-02 15:10 
     * @param conferenceId
     * @param attendeeId
     * @param params void
     */
    void mainSetting(String conferenceId, String attendeeId, List<? extends BaseFixedParamValue> params);

    /**
     * <pre>获取CallLeg</pre>
     * @author sinhy
     * @since 2021-09-02 15:23 
     * @param conferenceId
     * @param attendeeId void
     */
    JSONObject attendeeCallLegSetting(String conferenceId, String attendeeId);

    /**
     * <pre>字母设置</pre>
     * @author sinhy
     * @since 2021-09-02 16:32 
     * @param conferenceId
     * @param attendeeId
     * @param params void
     */
    void subtitle(String conferenceId, String attendeeId, List<? extends BaseFixedParamValue> params);

    /**
     * <pre>布局设置</pre>
     * @author sinhy
     * @since 2021-09-02 17:17 
     * @param conferenceId
     * @param attendeeId
     * @param params void
     */
    void layoutSetting(String conferenceId, String attendeeId, List<? extends BaseFixedParamValue> params);

    /**
     * <pre>高级设置</pre>
     * @author sinhy
     * @since 2021-09-02 19:57 
     * @param conferenceId
     * @param attendeeId
     * @param params void
     */
    void advanceSetting(String conferenceId, String attendeeId, List<? extends BaseFixedParamValue> params);

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
    void recordStreamSetting(String conferenceId, String attendeeId, List<? extends BaseFixedParamValue> params);

    /**
     * 轮询获取快照
     * @param conferenceId
     * @param params
     * @return
     */
    List<String> takeSnapshotPolling(String conferenceId, JSONObject params);
}
