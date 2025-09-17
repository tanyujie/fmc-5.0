package com.paradisecloud.fcm.cdr.service.impls.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.paradisecloud.fcm.cdr.service.core.cache.CdrCache;
import com.paradisecloud.fcm.cdr.service.interfaces.report.IHistoryAllService;
import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryAllCallMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryAllConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryAllParticipantMapper;
import com.paradisecloud.fcm.dao.mapper.CdrCallLegStartMapper;
import com.paradisecloud.fcm.dao.mapper.CdrCallMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryAllCall;
import com.paradisecloud.fcm.dao.model.BusiHistoryAllConference;
import com.paradisecloud.fcm.dao.model.BusiHistoryAllParticipant;
import com.paradisecloud.fcm.dao.model.CdrCall;
import com.paradisecloud.fcm.dao.model.CdrCallLegStart;

import javax.annotation.Resource;

/**
 * @author johnson liu
 * @date 2021/5/26 14:42
 */
@Service
@Transactional
public class HistoryAllServiceImpl implements IHistoryAllService
{
    private static Logger logger = LoggerFactory.getLogger(HistoryServiceImpl.class);
    
    private static final List<CallLegEndReasonEnum> REASON_ENUM_LIST = new ArrayList<>(Arrays.asList(CallLegEndReasonEnum.API_INITIATED_TEARDOWN,
            CallLegEndReasonEnum.CALL_DEACTIVATED,
            CallLegEndReasonEnum.CALL_ENDED,
            CallLegEndReasonEnum.CALL_MOVED,
            CallLegEndReasonEnum.ERROR,
            CallLegEndReasonEnum.LOCAL_TEARDOWN,
            CallLegEndReasonEnum.REMOTE_TEARDOWN,
            CallLegEndReasonEnum.IVR_UNKNOWN_CALL));
    
    @Resource
    private BusiHistoryAllConferenceMapper busiHistoryAllConferenceMapper;
    
    @Resource
    private BusiHistoryAllParticipantMapper busiHistoryAllParticipantMapper;
    
    @Resource
    private CdrCallMapper cdrCallMapper;
    
    @Resource
    private CdrCallLegStartMapper cdrCallLegStartMapper;
    
    @Resource
    private BusiHistoryAllCallMapper busiHistoryAllCallMapper;
    
    /**
     * 根据callId更新会议历史表信息
     *
     * @param historyConferenceId historyConferenceId
     * @param cdrCall             CdrCall
     * @param participantNum      参会者数量
     * @return
     */
    @Override
    public void updateHistoryAllConferenceByCoSpace(Long historyConferenceId, CdrCall cdrCall, Integer participantNum)
    {
        BusiHistoryAllConference busiHistoryAllConferenceExist = busiHistoryAllConferenceMapper.selectBusiHistoryAllConferenceById(historyConferenceId);
        if (busiHistoryAllConferenceExist != null) {
            BusiHistoryAllConference busiHistoryAllConference = new BusiHistoryAllConference();
            Date conferenceEndTime = new Date(busiHistoryAllConferenceExist.getConferenceStartTime().getTime() + cdrCall.getDurationSeconds() * 1000);
            busiHistoryAllConference.setConferenceEndTime(conferenceEndTime);
            busiHistoryAllConference.setDuration(cdrCall.getDurationSeconds());
            busiHistoryAllConference.setDeviceNum(participantNum);
            busiHistoryAllConference.setUpdateTime(new Date());
            busiHistoryAllConference.setId(historyConferenceId);
            busiHistoryAllConferenceMapper.updateHistoryConferenceByCoSpace(cdrCall.getRecordType(), busiHistoryAllConference);
        }
    }
    
    /**
     * 添加历史会议与会者
     *
     * @param busiHistoryAllParticipant
     * @return
     */
    @Override
    public int insertHistoryAllParticipant(BusiHistoryAllParticipant busiHistoryAllParticipant)
    {
        return busiHistoryAllParticipantMapper.insertBusiHistoryAllParticipant(busiHistoryAllParticipant);
    }
    
    /**
     * 根据callLegId更新与会者记录表中的离会时间
     *
     * @param callLegId
     * @param outgoingTime 离会时间
     * @return
     */
    @Override
    public int updateHistoryParticipantByCallLegId(String callLegId, Date outgoingTime, Integer durationSeconds, Boolean joined)
    {
        return busiHistoryAllParticipantMapper.updateHistoryParticipantByCallLegId(callLegId, outgoingTime, new Date(), durationSeconds, joined);
    }
    
    /**
     * 构建与会者参数
     *
     * @param cdrCallLegStart
     * @return
     */
    @Override
    public BusiHistoryAllParticipant buildHistoryParticipant(CdrCallLegStart cdrCallLegStart)
    {
        String callId = cdrCallLegStart.getCall();
        BusiHistoryAllParticipant busiHistoryAllParticipant = getHistoryConference(cdrCallLegStart, callId);
        return busiHistoryAllParticipant;
    }
    
    /**
     * 构建与会者参数
     *
     * @param callId
     * @param callLegId
     * @return
     */
    @Override
    public BusiHistoryAllParticipant buildHistoryParticipantByUpdate(String callId, String callLegId)
    {
        CdrCallLegStart cdrCallLegStart = cdrCallLegStartMapper.selectCdrCallLegStartByRecordIndex(callLegId, null, null);
        BusiHistoryAllParticipant busiHistoryAllParticipant = getHistoryConference(cdrCallLegStart, callId);
        return busiHistoryAllParticipant;
    }
    
    /**
     * 根据callLegId查询参会者
     *
     * @param callLegId
     * @return
     */
    @Override
    public BusiHistoryAllParticipant findHistoryParticipantByCallLegId(String callLegId)
    {
        BusiHistoryAllParticipant busiHistoryAllParticipant = new BusiHistoryAllParticipant();
        busiHistoryAllParticipant.setCallLegId(callLegId);
        List<BusiHistoryAllParticipant> historyParticipantList = busiHistoryAllParticipantMapper.selectBusiHistoryAllParticipantList(busiHistoryAllParticipant);
        BusiHistoryAllParticipant historyParticipant = (CollectionUtils.isEmpty(historyParticipantList)) ? null : historyParticipantList.get(0);
        return historyParticipant;
    }
    
    public List<BusiHistoryAllParticipant> findHistoryParticipantByCoSpaceId(Long historyConferenceId)
    {
        BusiHistoryAllParticipant busiHistoryAllParticipant = new BusiHistoryAllParticipant();
        busiHistoryAllParticipant.setHistoryConferenceId(historyConferenceId);
        List<BusiHistoryAllParticipant> historyParticipantList = busiHistoryAllParticipantMapper.selectBusiHistoryAllParticipantList(busiHistoryAllParticipant);
        return historyParticipantList;
    }
    
    /**
     * 根据coSpaceId和callId查询指定的call记录
     *
     * @param callId
     * @param coSpaceId
     * @return
     */
    @Override
    public BusiHistoryAllCall findHistoryCallByCoSpaceAndCall(String callId, String coSpaceId)
    {
        List<BusiHistoryAllCall> historyCallByCoSpaceAndCall = busiHistoryAllCallMapper.findHistoryCallByCoSpaceAndCall(callId, coSpaceId);
        BusiHistoryAllCall busiHistoryAllCall = CollectionUtils.isEmpty(historyCallByCoSpaceAndCall) ? null : historyCallByCoSpaceAndCall.get(0);
        return busiHistoryAllCall;
    }
    
    /**
     * 添加会议历史记录
     *
     * @param busiHistoryAllConference
     */
    @Override
    public void insertHistoryConference(BusiHistoryAllConference busiHistoryAllConference)
    {
        busiHistoryAllConferenceMapper.insertBusiHistoryAllConference(busiHistoryAllConference);
    }
    
    /**
     * 条件查询历史与会者
     *
     * @param busiHistoryAllConference
     * @return
     */
    @Override
    public List<BusiHistoryAllConference> selectBusiHistoryAllConferenceList(BusiHistoryAllConference busiHistoryAllConference)
    {
        return busiHistoryAllConferenceMapper.selectBusiHistoryAllConferenceList(busiHistoryAllConference);
    }
    
    private BusiHistoryAllParticipant getHistoryConference(CdrCallLegStart cdrCallLegStart, String callId)
    {
        if (!StringUtils.hasText(callId))
        {
            return null;
        }
        BusiHistoryAllParticipant busiHistoryAllParticipant = new BusiHistoryAllParticipant();
        CdrCall cdrCall = cdrCallMapper.selectCdrCallByIdAndRecordType(callId, 1);
        if (cdrCall == null)
        {
            logger.info("找不到对应的CdrCall记录:{}", callId);
            return null;
        }

        BusiHistoryAllConference historyConference = busiHistoryAllConferenceMapper.selectBusiHistoryAllConferenceByCallId(callId);

        // 空指针异常
        String remoteParty = cdrCallLegStart.getRemoteParty();
        busiHistoryAllParticipant.setCallId(callId);
        busiHistoryAllParticipant.setCoSpace(cdrCall.getCoSpace());
        busiHistoryAllParticipant.setCallLegId(cdrCallLegStart.getCdrId());
        busiHistoryAllParticipant.setRemoteParty(remoteParty);
        busiHistoryAllParticipant.setName(cdrCallLegStart.getDisplayName());
        busiHistoryAllParticipant.setHistoryConferenceId(historyConference.getId());
        busiHistoryAllParticipant.setJoinTime(new Date());
        busiHistoryAllParticipant.setJoined(false);
        busiHistoryAllParticipant.setCreateTime(new Date());
        busiHistoryAllParticipant.setDurationSeconds(0);
        return busiHistoryAllParticipant;
    }
    
    /**
     * 检测是否入会
     * 
     * @param reason
     * @return
     */
    @Override
    public boolean checkIsJoin(CallLegEndReasonEnum reason)
    {
        boolean flag = false;
        if (REASON_ENUM_LIST.contains(reason))
        {
            flag = true;
        }
        return flag;
    }
}
