package com.paradisecloud.fcm.fme.conference.interfaces;

import java.util.List;
import java.util.Set;

import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import org.apache.http.NameValuePair;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;

/**
 * 活跃会议室信息，用于存放活跃的会议室Service接口
 * 
 * @author lilinhai
 * @date 2021-02-02
 */
public interface IBusiConferenceService 
{

    /**
     * <pre>根据coSpaceId挂断会议</pre>
     * @author lilinhai
     * @since 2021-02-03 18:05
     * @param conferenceId void
     * @param endType
     * @param endReasonsType
     */
    void endConference(String conferenceId, int endType, int endReasonsType);
    
    /**
     * 结束会议
     * @author lilinhai
     * @since 2021-02-28 15:19
     * @param callId void
     * @param endReasonsType
     */
    void endConference(String callId, int endReasonsType);

    /**
     * <pre>会议讨论</pre>
     * @author lilinhai
     * @since 2021-04-25 14:07 
     * @param conferenceId void
     */
    public void discuss(String conferenceId);


    void mode(String conferenceId,String modelName);
    /**
     * <pre>锁定会议</pre>
     * @author lilinhai
     * @since 2021-04-27 16:23 
     * @param conferenceId void
     * @param locked 
     */
    public void lock(String conferenceId, Boolean locked);
    
    /**
     * 修改call
     * @author lilinhai
     * @since 2021-04-28 13:38 
     * @param conferenceNumber
     * @param nameValuePairs void
     */
    void updateCall(String conferenceNumber, List<NameValuePair> nameValuePairs);

    void updateCallRecordStatus(String conferenceNumber,Boolean record);

    /**
     * <pre>延长会议时间</pre>
     * @author lilinhai
     * @since 2021-05-27 16:59 
     * @param conferenceId
     * @param minutes void
     */
    @Transactional
    BusiConferenceAppointment extendMinutes(String conferenceId, int minutes);

    /**
     * <pre>取消会议讨论</pre>
     * @author sinhy
     * @since 2021-08-17 13:36 
     * @param conferenceId void
     */
    void cancelDiscuss(String conferenceId);

    /**
     * <pre>直播</pre>
     * @author sinhy
     * @since 2021-08-17 16:58 
     * @param conferenceId
     * @param streaming
     * @param string void
     */
    void stream(String conferenceId, Boolean streaming, String string);
    
    /**
     * 直播
     * @author sinhy
     * @since 2021-08-17 16:39 
     * @param conferenceId
     * @param locked void
     */
    void stream(ConferenceContext mainConferenceContext, Boolean streaming, String streamUrl);

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
     * <pre>同步会议数据</pre>
     * @author sinhy
     * @since 2021-08-30 10:46 
     * @param conferenceId void
     */
    void sync(String conferenceId);
    
    void sync(ConferenceContext conferenceContext, String reason);

    Set<String> getLayoutTemplates(Long deptId);

    JSONObject getLayoutTemplate(Long deptId, String name);

    /**
     * <pre>一键呼入</pre>
     * @author sinhy
     * @since 2021-09-27 21:47 
     * @param conferenceId void
     */
    void reCall(String conferenceId);

    /**
     * 修改会议名称
     * @param conferenceId
     * @param name
     */
    void updateConferenceName(String conferenceId, String name);


    /**
     * 修改会议支持人
     * @param conferenceId
     * @param enable
     * @param presenter
     */
    void updateConferencePresenter(String conferenceId, Boolean enable,Long presenter);

    /**
     * 根据Ip、域名、账号查询与会者列表
     * @param conferenceId
     * @param searchKey
     * @return
     */
    List<Attendee> attendeeList(String conferenceId, String searchKey);
}
