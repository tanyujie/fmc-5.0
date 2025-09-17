package com.paradisecloud.fcm.smc2.setvice2.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.request.BroadcastPollRequest;
import com.paradisecloud.com.fcm.smc.modle.request.ChairmanPollOperateReq;
import com.paradisecloud.com.fcm.smc.modle.request.MultiPicInfoReq;
import com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2ConferenceAppointment;
import org.apache.http.NameValuePair;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 活跃会议室信息，用于存放活跃的会议室Service接口
 * 
 * @author lilinhai
 * @date 2021-02-02
 */
public interface IBusiSmc2ConferenceService
{

    /**
     * <pre>根据模板会议数据库ID启动会议</pre>
     * @author lilinhai
     * @since 2021-01-20 16:52
     * @param templateConferenceId void
     */
    String startTemplateConference(long templateConferenceId);

    /**
     * <pre>根据模板会议数据库ID启动会议</pre>
     * @author lilinhai
     * @since 2021-01-20 16:52
     * @param templateConferenceId void
     */
    String startConference(long templateConferenceId);

    /**
     * <pre>根据模板ID构建会议上下文对象</pre>
     * @author lilinhai
     * @since 2021-02-02 12:14
     * @param templateConferenceId
     * @return ConferenceContext
     */
    Smc2ConferenceContext buildTemplateConferenceContext(long templateConferenceId);

    /**
     * <pre>根据coSpaceId挂断会议</pre>
     * @author lilinhai
     * @since 2021-02-02 18:05
     * @param encryptConferenceId void
     * @param endType
     */
    void endConference(String encryptConferenceId, int endType);

    /**
     * <pre>根据coSpaceId挂断会议</pre>
     * @author lilinhai
     * @since 2021-02-02 18:05
     * @param conferenceNumber void
     * @param endType
     */
    void endConference(String conferenceNumber, int endType, boolean forceEnd, boolean pushMessage);
    
    /**
     * 结束会议
     * @author lilinhai
     * @since 2021-02-28 15:19
     */
    void endConference(String encryptConferenceId);

    /**
     * <pre>会议讨论</pre>
     * @author lilinhai
     * @since 2021-04-25 14:07 
     * @param conferenceId void
     */
    public void discuss(String conferenceId);

    /**
     * <pre>锁定会议</pre>
     * @author lilinhai
     * @since 2021-04-27 16:22 
     * @param conferenceId void
     * @param locked 
     */
    public void lock(String conferenceId, Boolean locked);
    
    /**
     * 修改call
     * @author lilinhai
     * @since 2021-04-28 12:28 
     * @param conferenceNumber
     * @param nameValuePairs void
     */
    void updateCall(String conferenceNumber, List<NameValuePair> nameValuePairs);

    boolean updateCallRecordStatus(String conferenceNumber,Boolean record);

    /**
     * <pre>延长会议时间</pre>
     * @author lilinhai
     * @since 2021-05-27 16:59
     * @param conferenceId
     * @param minutes void
     * @return
     */
    @Transactional
    BusiMcuSmc2ConferenceAppointment extendMinutes(String conferenceId, int minutes);

    /**
     * <pre>取消会议讨论</pre>
     * @author sinhy
     * @since 2021-08-17 12:26 
     * @param conferenceId void
     */
    void cancelDiscuss(String conferenceId);

    /**
     * <pre>直播</pre>
     * @author sinhy
     * @since 2021-08-17 16:58 
     * @param conferenceId
     * @param enabled
     * @param streamUrl void
     */
    void stream(String conferenceId, Boolean enabled, String streamUrl);
    
    /**
     * 直播
     * @author sinhy
     * @since 2021-08-17 16:29
     */
    void stream(Smc2ConferenceContext mainConferenceContext, Boolean streaming, String streamUrl);

    /**
     * <pre>允许所有人静音自己</pre>
     * @author sinhy
     * @since 2021-08-17 18:18 
     * @param conferenceId
     * @param enabled void
     */
    void allowAllMuteSelf(String conferenceId, Boolean enabled);
    
    /**
     * <pre>允许辅流控制</pre>
     * @author sinhy
     * @since 2021-08-18 10:14 
     * @param conferenceId
     * @param enabled void
     */
    void allowAllPresentationContribution(String conferenceId, Boolean enabled);
    
    /**
     * 新加入用户静音
     * @author sinhy
     * @since 2021-08-18 10:15 
     * @param conferenceId
     * @param enabled void
     */
    void joinAudioMuteOverride(String conferenceId, Boolean enabled);

    /**
     * 获取显示模板
     *
     * @param deptId
     * @return
     */
    List<Map<String, String>> getLayoutTemplates(Long deptId);

    JSONObject getLayoutTemplate(Long deptId, String name);

    /**
     * <pre>一键呼入</pre>
     * @author sinhy
     * @since 2021-09-27 21:47 
     * @param conferenceId void
     */
    void reCall(String conferenceId);

    /**
     * <pre>同步会议数据</pre>
     * @author sinhy
     * @since 2021-08-20 10:46
     * @param conferenceId void
     */
    void sync(String conferenceId);

    Integer getLiveTerminalCount(String conferenceId);

    void setMute(String conferenceId, Boolean mute);

    void setMultiPicPoll(MultiPicPollRequest multiPicPollRequest);

    void stopMultiPicPoll(String conferenceId);

    void startMultiPicPoll(String conferenceId);

    void cancelMultiPicPoll(String conferenceId);

    void setChairmanParticipantMultiPicPoll(MultiPicPollRequest multiPicPollRequest);

    MultiPicPollRequest chairmanParticipantMultiPicPollQuery(String conferenceId, String participantId);

    void chairmanParticipantMultiPicPollOperate(ChairmanPollOperateReq chairmanPollOperateReq);

    void createMultiPic(MultiPicInfoReq multiPicInfoReq);

    void conferencesControlChoose(String conferenceId, String participantId, MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO);

    Object queryMulitiPicPoll(String conferenceId);

    void setBroadcastPoll(BroadcastPollRequest broadcastPollRequest);

    void multiPicBroad(String conferenceId, boolean enable);

    void voiceActive(String conferenceId,Boolean enable);

    void changeQuiet(String conferenceId, boolean enable);

    void chooseMultiPicManly(String conferenceId, String participantId, MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO2);

    void lockPresenter(String conferenceId, Boolean lock);
}
