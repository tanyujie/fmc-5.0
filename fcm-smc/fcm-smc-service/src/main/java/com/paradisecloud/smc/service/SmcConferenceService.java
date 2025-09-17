package com.paradisecloud.smc.service;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.*;
import com.paradisecloud.com.fcm.smc.modle.mix.ConferenceControllerRequest;
import com.paradisecloud.com.fcm.smc.modle.request.*;
import com.paradisecloud.com.fcm.smc.modle.response.LogsConferenceRep;
import com.paradisecloud.com.fcm.smc.modle.response.ParticipantOrderRep;
import com.paradisecloud.com.fcm.smc.modle.response.SmcConferenceRep;
import com.paradisecloud.com.fcm.smc.modle.response.VideoSourceRep;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/16 15:34
 */
public interface SmcConferenceService {

    String getSmcConferenceInfoById(String id);

    void endConference(String conferenceId);

    void setMic(String conferenceId);

    void statusControl(String conferenceId, ConferenceStatusRequest conferenceStatusRequest);

    void quickHangup(String conferenceId, String participantId);

    void createMulitiPicPoll(MultiPicInfoReq multiPicInfoReq);

    void setBroadcastPoll(BroadcastPollRequest broadcastPollRequest);

    void broadcastStart(String conferenceId);

    void broadcastEnd(String conferenceId);

    SmcConferenceRep getConferenceList(SmcConferenceRequest smcConferenceRequest);

    SmcAppointmentConferenceContext appointmentConferenceAdd(SmcAppointmentConferenceRequest smcAppointmentConferenceRequest);

    SmcAppointmentConferenceContext appointmentConferenceChange(SmcAppointmentConferenceRequest smcAppointmentConferenceRequest);

    void appointmentConferenceDelete(String conferenceId);

    /**
     * 构建模板信息
     * @param templateId
     * @return
     */
    SmcConferenceContext buildTemplateConferenceContext(String templateId);

    /**
     * 会议详情
     * @param id
     * @return
     */
    DetailConference getDetailConferenceInfoById(String id);

    /**
     * 共享材料
     * @param conferenceId
     * @param presenter
     */
    void share(String conferenceId,String presenter);

    /**
     * 锁定会议
     * @param conferenceId
     */
    void lockConference(String conferenceId);

    /**
     * 取消锁定
     * @param conferenceId
     */
    void unlockConference(String conferenceId);

    /**
     * 静音设置
     * @param conferenceId
     * @param b
     */
    void setMute(String conferenceId, boolean b);

    /**
     * 自由讨论
     * @param conferenceId
     */
    void setFreeTalk(String conferenceId);

    /**
     * 延长会议
     * @param extendTimeReq
     */
    void ExtendTime(ExtendTimeReq extendTimeReq);

    /**
     * 选看
     * @param conferenceId
     * @param callTheRollRequest
     */
    void statusControlchoose(String chairmanId,ConferenceControllerRequest callTheRollRequest);

    /**
     * 横幅
     * @param textTipsSetting
     */
    void textTipsSetting(TextTipsSetting textTipsSetting);

    /**
     * 查询多画面轮询设置
     * @param conferenceId
     * @return
     */
    MultiPicPollRequest queryMulitiPicPoll(String conferenceId);

    /**
     * 提供获取预置多画面功能
     * @param conferenceId
     * @return
     */
    List<PresetMultiPicReqDto> getConferencesPresetParam(String conferenceId);

    /**
     * 设置会议多画面
     * @param multiPicInfoReq
     */
    void createMulitiPic(MultiPicInfoReq multiPicInfoReq);

    /**
     * 设置会议多画面
     * @param multiPicInfoReq
     */
    void createMulitiPicNObroad(MultiPicInfoReq multiPicInfoReq);

    /**
     * 设置主席轮询
     * @param masterPollTemplate
     */
    void createChairmanPollMulitiPicPoll(MasterPollTemplate masterPollTemplate);

    /**
     * 查询主席轮询
     * @param conferenceId
     * @return
     */
    MasterPollTemplate chairmanPollQuery(String conferenceId);

    /**
     * 操作主席轮询
     * @param chairmanPollOperateReq
     */
    void chairmanPollOperate(ChairmanPollOperateReq chairmanPollOperateReq);

    /**
     * 获取视频源
     * @param conferenceId
     * @param participants
     * @return
     */
     List<VideoSourceRep> conferencesVideoSource(String conferenceId, List<String> participants);

    /**
     * 设置多画面
     * @param conferenceId
     * @param participantId
     * @param multiPicInfoD
     */
     void conferencesControlChoose(String conferenceId, String participantId, MultiPicInfoReq.MultiPicInfoDTO multiPicInfoD);

    /**
     * 查询常用会场
     * @param conferenceRequest
     * @return
     */
    ParticipantOrderRep orderQuery(SmcConferenceRequest conferenceRequest);

    /**
     * 设置常用会场
     * @param participantOrderRequest
     */
    void order(ParticipantOrderRequest participantOrderRequest);

    /**
     * 日志查询
     * @param smcConferenceRequest
     * @return
     */
    LogsConferenceRep listLog(SmcConferenceRequest smcConferenceRequest);

    /**
     * 日志导出
     * @param smcConferenceRequest
     * @return
     */
    Object exportLog(SmcConferenceRequest smcConferenceRequest);

   void downloadLog(SmcConferenceRequest smcConferenceRequest, HttpServletResponse response);

    Object httpGetListString(SmcConferenceRequest smcConferenceRequest);

    /**
     * 锁定会议材料
     * @param conferenceId
     * @param lock
     */
    void lockPresenter(String conferenceId, Boolean lock);

    /**
     * 锁定会议材料
     * @param conferenceId
     * @param lock
     */
    void lockPresenterParticipant(String conferenceId,String participant, Boolean lock);
    /**
     * 设置多画面轮询
     * @param multiPicPollRequest
     */
    void setMultiPicPoll(MultiPicPollRequest multiPicPollRequest);

    /**
     * 停止多画面轮询
     * @param conferenceId
     */
    void stopMultiPicPoll(String conferenceId);

    /**
     * 开始多画面轮询
     * @param conferenceId
     */
    void startMultiPicPoll(String conferenceId);

    /**
     * 取消多画面轮询
     * @param conferenceId
     */
    void cancelMultiPicPoll(String conferenceId);

    /**
     * 摄像机控制
     * @param conferenceId
     * @param participantId
     */
    void camera(String conferenceId, String participantId, JSONObject jsonObject);

    Object count();

    /**
     * 修改与会者名称
     * @param conferenceId
     * @param jsonObject
     */
    void changeName(String conferenceId, JSONObject jsonObject);

    void remind(String conferenceId, String participantId);

    void setStatus(String conferenceId, JSONObject jsonObject);

    Object conferenceStat();

    Map<String, Object> reportConferenceOfIndex(Long deptId, String startTime, String endTime);

    void chairmanParticipantMultiPicPoll(MultiPicPollRequest multiPicPollRequest);

    MultiPicPollRequest chairmanParticipantMultiPicPollQuery(String conferenceId, String participantId);

    void chairmanParticipantMultiPicPollOperate(ChairmanPollOperateReq chairmanPollOperateReq);

    void multiPicBroad(String conferenceId, boolean enable);


}
