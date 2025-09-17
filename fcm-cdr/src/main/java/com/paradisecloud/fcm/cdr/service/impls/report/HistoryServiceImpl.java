package com.paradisecloud.fcm.cdr.service.impls.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.paradisecloud.fcm.cdr.service.core.cache.CdrCache;
import com.paradisecloud.fcm.cdr.service.interfaces.report.IHistoryService;
import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryCallMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantMapper;
import com.paradisecloud.fcm.dao.mapper.CdrCallLegStartMapper;
import com.paradisecloud.fcm.dao.mapper.CdrCallMapper;

/**
 * @author johnson liu
 * @date 2021/5/26 14:42
 */
@Service
@Transactional
public class HistoryServiceImpl implements IHistoryService
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
    
    @Autowired
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;
    
    @Autowired
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;
    
    @Autowired
    private CdrCallMapper cdrCallMapper;
    
    @Autowired
    private CdrCallLegStartMapper cdrCallLegStartMapper;
    
    @Autowired
    private BusiHistoryCallMapper historyCallMapper;
    
    /**
     * 根据callId更新会议历史表信息
     *
     * @param historyConferenceId 会议id
     * @param cdrCall             CdrCall
     * @param participantNum      与会者数
     * @return
     */
    @Override
    public void updateHistoryConferenceByCoSpace(Long historyConferenceId, CdrCall cdrCall, Integer participantNum)
    {
        BusiHistoryConference busiHistoryConferenceExist = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(historyConferenceId);
        if (busiHistoryConferenceExist != null) {
            BusiHistoryConference busiHistoryConference = new BusiHistoryConference();
            Date conferenceEndTime = new Date(busiHistoryConferenceExist.getConferenceStartTime().getTime() + cdrCall.getDurationSeconds() * 1000);
            busiHistoryConference.setConferenceEndTime(conferenceEndTime);
            busiHistoryConference.setDuration(cdrCall.getDurationSeconds());
            busiHistoryConference.setDeviceNum(participantNum);
            busiHistoryConference.setUpdateTime(new Date());
            busiHistoryConference.setId(historyConferenceId);
            if (busiHistoryConferenceExist.getConferenceEndTime() == null) {
                busiHistoryConference.setEndReasonsType(EndReasonsType.ABNORMAL_END);
            }
            busiHistoryConferenceMapper.updateHistoryConferenceByCoSpace(cdrCall.getRecordType(), busiHistoryConference);
        }
    }
    
    /**
     * 添加历史会议与会者
     *
     * @param busiHistoryParticipant
     * @return
     */
    @Override
    public int insertHistoryParticipant(BusiHistoryParticipant busiHistoryParticipant)
    {
        if(busiHistoryParticipant==null){
            return 0;
        }
        CdrCache.getInstance().putHistoryParticipantMap(busiHistoryParticipant.getCallLegId(), busiHistoryParticipant);
        String remoteParty = busiHistoryParticipant.getRemoteParty();
        if (remoteParty.contains(":")) {
            remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
        }
        BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(remoteParty);
        if (busiTerminal == null) {
            if (remoteParty.contains("@")) {
                try {
                    String[] remotePartyArr = remoteParty.split("@");
                    String credential = remotePartyArr[0];
                    String ip = remotePartyArr[1];
                    if (StringUtils.hasText(ip)) {
                        FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByDomainName(ip);
                        if (fsbcBridge != null) {
                            String remotePartyNew = credential + "@" + fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                            busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyNew);
                        }
                        if (busiTerminal == null) {
                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByDomainName(ip);
                            if (fcmBridge != null) {
                                String remotePartyNew = credential + "@" + fcmBridge.getBusiFreeSwitch().getIp();
                                busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyNew);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        if (busiTerminal != null) { {
            busiHistoryParticipant.setTerminalId(busiTerminal.getId());
        }}
        return busiHistoryParticipantMapper.insertBusiHistoryParticipant(busiHistoryParticipant);
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
        return busiHistoryParticipantMapper.updateHistoryParticipantByCallLegId(callLegId, outgoingTime, new Date(), durationSeconds, joined);
    }
    
    /**
     * 构建与会者参数
     *
     * @param cdrCallLegStart
     * @return
     */
    @Override
    public BusiHistoryParticipant buildHistoryParticipant(CdrCallLegStart cdrCallLegStart)
    {
        String callId = cdrCallLegStart.getCall();
        BusiHistoryParticipant busiHistoryParticipant = getHistoryConference(cdrCallLegStart, callId);
        return busiHistoryParticipant;
    }
    
    /**
     * 构建与会者参数
     *
     * @param callId
     * @param callLegId
     * @return
     */
    @Override
    public BusiHistoryParticipant buildHistoryParticipantByUpdate(String callId, String callLegId)
    {
        CdrCallLegStart cdrCallLegStart = cdrCallLegStartMapper.selectCdrCallLegStartByRecordIndex(callLegId, null, null);
        BusiHistoryParticipant busiHistoryParticipant = getHistoryConference(cdrCallLegStart, callId);
        return busiHistoryParticipant;
    }
    
    /**
     * 根据callLegId查询参会者
     *
     * @param callLegId
     * @return
     */
    @Override
    public BusiHistoryParticipant findHistoryParticipantByCallLegId(String callLegId)
    {
        BusiHistoryParticipant busiHistoryParticipant = new BusiHistoryParticipant();
        busiHistoryParticipant.setCallLegId(callLegId);
        List<BusiHistoryParticipant> historyParticipantList = busiHistoryParticipantMapper.selectBusiHistoryParticipantList(busiHistoryParticipant);
        BusiHistoryParticipant historyParticipant = (CollectionUtils.isEmpty(historyParticipantList)) ? null : historyParticipantList.get(0);
        return historyParticipant;
    }
    
    public List<BusiHistoryParticipant> findHistoryParticipantByCoSpaceId(Long historyConferenceId)
    {
        BusiHistoryParticipant busiHistoryParticipant = new BusiHistoryParticipant();
        busiHistoryParticipant.setHistoryConferenceId(historyConferenceId);
        List<BusiHistoryParticipant> historyParticipantList = busiHistoryParticipantMapper.selectBusiHistoryParticipantList(busiHistoryParticipant);
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
    public BusiHistoryCall findHistoryCallByCoSpaceAndCall(String callId, String coSpaceId)
    {
        List<BusiHistoryCall> historyCallByCoSpaceAndCall = historyCallMapper.findHistoryCallByCoSpaceAndCall(callId, coSpaceId);
        BusiHistoryCall busiHistoryCall = CollectionUtils.isEmpty(historyCallByCoSpaceAndCall) ? null : historyCallByCoSpaceAndCall.get(0);
        return busiHistoryCall;
    }
    
    /**
     * 添加会议历史记录
     *
     * @param busiHistoryConference
     */
    @Override
    public void insertHistoryConference(BusiHistoryConference busiHistoryConference)
    {
        busiHistoryConferenceMapper.insertBusiHistoryConference(busiHistoryConference);
    }
    
    /**
     * 条件查询历史与会者
     *
     * @param busiHistoryConference
     * @return
     */
    @Override
    public List<BusiHistoryConference> selectBusiHistoryConferenceList(BusiHistoryConference busiHistoryConference)
    {
        return busiHistoryConferenceMapper.selectBusiHistoryConferenceList(busiHistoryConference);
    }
    
    private BusiHistoryParticipant getHistoryConference(CdrCallLegStart cdrCallLegStart, String callId)
    {
        if (!StringUtils.hasText(callId))
        {
            return null;
        }
        BusiHistoryParticipant busiHistoryParticipant = new BusiHistoryParticipant();
        CdrCall cdrCall = cdrCallMapper.selectCdrCallByIdAndRecordType(callId, 1);
        cdrCall = (cdrCall == null) ? CdrCache.getInstance().getCdrCallMap(callId) : cdrCall;
        if (cdrCall == null)
        {
            logger.info("找不到对应的CdrCall记录:{}", callId);
            return null;
        }
        
        BusiHistoryConference historyConference = CdrCache.getInstance().getHistoryConference(cdrCall.getCoSpace());
        if (historyConference == null)
        {
            historyConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceByCallId(callId);
            if (historyConference != null) {
                CdrCache.getInstance().putHistoryConferenceMap(cdrCall.getCoSpace(), historyConference);
            }
        }
        if(historyConference == null){
            return null;
        }
        // 空指针异常
        String remoteParty = cdrCallLegStart.getRemoteParty();
        busiHistoryParticipant.setDeptId(historyConference.getDeptId().intValue());
        busiHistoryParticipant.setCallId(callId);
        busiHistoryParticipant.setCoSpace(cdrCall.getCoSpace());
        busiHistoryParticipant.setCallLegId(cdrCallLegStart.getCdrId());
        busiHistoryParticipant.setRemoteParty(remoteParty);
        busiHistoryParticipant.setName(cdrCallLegStart.getDisplayName());
        busiHistoryParticipant.setHistoryConferenceId(historyConference.getId());
        busiHistoryParticipant.setJoinTime(new Date());
        busiHistoryParticipant.setJoined(false);
        busiHistoryParticipant.setCreateTime(new Date());
        busiHistoryParticipant.setDurationSeconds(0);
        if (remoteParty.contains(":")) {
            remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
        }
        BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(remoteParty);
        if (busiTerminal == null) {
            if (remoteParty.contains("@")) {
                try {
                    String[] remotePartyArr = remoteParty.split("@");
                    String credential = remotePartyArr[0];
                    String ip = remotePartyArr[1];
                    if (StringUtils.hasText(ip)) {
                        FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByDomainName(ip);
                        if (fsbcBridge != null) {
                            String remotePartyNew = credential + "@" + fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                            busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyNew);
                        }
                        if (busiTerminal == null) {
                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByDomainName(ip);
                            if (fcmBridge != null) {
                                String remotePartyNew = credential + "@" + fcmBridge.getBusiFreeSwitch().getIp();
                                busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyNew);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        if (busiTerminal != null) { {
            busiHistoryParticipant.setTerminalId(busiTerminal.getId());
        }}
        return busiHistoryParticipant;
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
