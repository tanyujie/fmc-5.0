package com.paradisecloud.fcm.cdr.service.interfaces.report;

import java.util.Date;
import java.util.List;

import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;
import com.paradisecloud.fcm.dao.model.BusiHistoryAllCall;
import com.paradisecloud.fcm.dao.model.BusiHistoryAllConference;
import com.paradisecloud.fcm.dao.model.BusiHistoryAllParticipant;
import com.paradisecloud.fcm.dao.model.CdrCall;
import com.paradisecloud.fcm.dao.model.CdrCallLegStart;

/**
 * 历史会议和会议与会者记录
 *
 * @author johnson liu
 * @date 2021/5/26 14:41
 */
public interface IHistoryAllService
{
    /**
     * 根据callId更新会议历史表信息
     *
     * @param historyConferenceId historyConferenceId
     * @param cdrCall             coSpaceId
     * @param participantNum      参会者数量
     * @return
     */
    void updateHistoryAllConferenceByCoSpace(Long historyConferenceId, CdrCall cdrCall, Integer participantNum);
    
    /**
     * 添加历史会议与会者
     *
     * @param busiHistoryAllParticipant
     * @return
     */
    int insertHistoryAllParticipant(BusiHistoryAllParticipant busiHistoryAllParticipant);
    
    /**
     * 根据callLegId更新与会者记录表中的离会时间
     *
     * @param callLegId
     * @param outgoingTime 离会时间
     * @param joined       离会时间
     * @return
     */
    int updateHistoryParticipantByCallLegId(String callLegId, Date outgoingTime, Integer durationSeconds, Boolean joined);
    
    /**
     * 构建与会者参数
     *
     * @param cdrCallLegStart
     * @return
     */
    BusiHistoryAllParticipant buildHistoryParticipant(CdrCallLegStart cdrCallLegStart);
    
    /**
     * 构建与会者参数
     *
     * @param callId
     * @param callLegId
     * @return
     */
    BusiHistoryAllParticipant buildHistoryParticipantByUpdate(String callId, String callLegId);
    
    /**
     * 根据callLegId查询参会者
     *
     * @param callLegId
     * @return
     */
    BusiHistoryAllParticipant findHistoryParticipantByCallLegId(String callLegId);
    
    /**
     * 根据历史会议id查询所有的参会者
     * 
     * @param historyConferenceId
     * @param coSpaceId
     * @return
     */
    List<BusiHistoryAllParticipant> findHistoryParticipantByCoSpaceId(Long historyConferenceId);
    
    /**
     * 根据coSpaceId和callId查询指定的call记录
     * 
     * @param callId
     * @param coSpaceId
     * @return
     */
    BusiHistoryAllCall findHistoryCallByCoSpaceAndCall(String callId, String coSpaceId);
    
    /**
     * 添加会议历史记录
     * 
     * @param busiHistoryAllConference
     */
    void insertHistoryConference(BusiHistoryAllConference busiHistoryAllConference);
    
    /**
     * 条件查询历史与会者
     * 
     * @param busiHistoryAllConference
     * @return
     */
    List<BusiHistoryAllConference> selectBusiHistoryAllConferenceList(BusiHistoryAllConference busiHistoryAllConference);
    
    /**
     * 检测是否入会
     * 
     * @param reason
     * @return
     */
    public boolean checkIsJoin(CallLegEndReasonEnum reason);
}
