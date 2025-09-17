package com.paradisecloud.fcm.telep.service.interfaces;


import com.paradisecloud.fcm.telep.model.busi.ConferencesResponse;
import com.paradisecloud.fcm.telep.model.request.EnumerateFilter;

/**
 * @author nj
 * @date 2022/10/14 15:14
 */
public interface IBusiTeleConferenceService {
    /**
     * 开始会议
     * @param number
     * @param ip
     */
    void  startConference(String number,String ip,String accessCode);

    /**
     * 开始会议
     * @param number
     * @param ip
     */
    void  startConference(String number,String ip);

    /**
     * 设置会议主会场
     * @param number
     * @param ip
     */
    void  settingConference(String number,String ip,String accessCode);

    /**
     * 锁定会议 解锁
     * @param uri
     * @param locked
     */
     void conferenceLock(String uri,Boolean locked);

    /**
     * 关闭会议
     * @param conferenceId
     */
    void conferenceEnd(String ip,String conferenceName);

    /**
     * 会议列表
     * @param ip
     * @param enumerateFilter
     * @return
     */
    ConferencesResponse conferenceList(String ip,EnumerateFilter enumerateFilter);

}
