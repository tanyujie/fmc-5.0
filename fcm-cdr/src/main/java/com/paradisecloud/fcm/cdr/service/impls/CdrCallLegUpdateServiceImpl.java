package com.paradisecloud.fcm.cdr.service.impls;

import java.util.Date;
import java.util.List;

import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantTerminalService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrCallLegUpdateService;
import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.cache.FmeDataCache;
import com.paradisecloud.fcm.fme.cache.model.ParticipantInfo;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * callLegUpdate记录Service业务层处理
 *
 * @author lilinhai
 * @date 2021-05-13
 */
@Service
public class CdrCallLegUpdateServiceImpl implements ICdrCallLegUpdateService
{
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private CdrCallLegUpdateMapper cdrCallLegUpdateMapper;
    
    @Autowired
    private IAttendeeService attendeeService;
    
    @Autowired
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;
    
    @Autowired
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;

    @Resource
    private BusiHistoryAllParticipantMapper busiHistoryAllParticipantMapper;

    @Resource
    private BusiHistoryAllConferenceMapper busiHistoryAllConferenceMapper;

    @Resource
    private IBusiHistoryParticipantTerminalService busiHistoryParticipantTerminalService;
    
    /**
     * 查询callLegUpdate记录
     *
     * @param cdrId callLegUpdate记录ID
     * @return callLegUpdate记录
     */
    @Override
    public CdrCallLegUpdate selectCdrCallLegUpdateById(Integer cdrId)
    {
        return cdrCallLegUpdateMapper.selectCdrCallLegUpdateById(cdrId);
    }
    
    /**
     * 查询callLegUpdate记录列表
     *
     * @param cdrCallLegUpdate callLegUpdate记录
     * @return callLegUpdate记录
     */
    @Override
    public List<CdrCallLegUpdate> selectCdrCallLegUpdateList(CdrCallLegUpdate cdrCallLegUpdate)
    {
        return cdrCallLegUpdateMapper.selectCdrCallLegUpdateList(cdrCallLegUpdate);
    }
    
    /**
     * 新增callLegUpdate记录
     *
     * @param cdrCallLegUpdate callLegUpdate记录
     * @return 结果
     */
    @Override
    public int insertCdrCallLegUpdate(CdrCallLegUpdate cdrCallLegUpdate)
    {
        if (cdrCallLegUpdate.getState() != null)
        {
            try
            {
                ParticipantState state = ParticipantState.convert(cdrCallLegUpdate.getState());
                if (state == ParticipantState.CONNECTED)
                {
                    // 全会议与会者记录 start 2022.05.30
                    if (ExternalConfigCache.getInstance().isEnableCdrAll()) {
                        BusiHistoryAllParticipant busiHistoryAllParticipant = busiHistoryAllParticipantMapper.selectBusiHistoryAllParticipantByCallLegId(cdrCallLegUpdate.getCdrId());
                        if (busiHistoryAllParticipant != null) {
                            if (busiHistoryAllParticipant.getJoined() == null || !busiHistoryAllParticipant.getJoined()) {
                                busiHistoryAllParticipant.setJoined(true);
                                busiHistoryAllParticipant.setUpdateTime(new Date());
                                busiHistoryAllParticipant.setJoinTime(new Date());
                                // mediaInfo在callLegEnd中设置
                                busiHistoryAllParticipantMapper.updateBusiHistoryAllParticipant(busiHistoryAllParticipant);
                            }
                        } else {
                            BusiHistoryAllConference busiHistoryAllConference = busiHistoryAllConferenceMapper.selectBusiHistoryAllConferenceByCallId(cdrCallLegUpdate.getCall());
                            if (busiHistoryAllConference != null) {
                                busiHistoryAllParticipant = new BusiHistoryAllParticipant();
                                busiHistoryAllParticipant.setCallId(cdrCallLegUpdate.getCall());
                                busiHistoryAllParticipant.setCoSpace(busiHistoryAllConference.getCoSpace());
                                busiHistoryAllParticipant.setCallLegId(cdrCallLegUpdate.getCdrId());
                                busiHistoryAllParticipant.setRemoteParty(cdrCallLegUpdate.getRemoteAddress());
                                busiHistoryAllParticipant.setName(cdrCallLegUpdate.getDisplayName());
                                busiHistoryAllParticipant.setHistoryConferenceId(busiHistoryAllConference.getId());
                                busiHistoryAllParticipant.setJoinTime(new Date());
                                busiHistoryAllParticipant.setJoined(true);
                                busiHistoryAllParticipant.setCreateTime(new Date());
                                busiHistoryAllParticipant.setDurationSeconds(0);
                                // mediaInfo在callLegEnd中设置
                                busiHistoryAllParticipantMapper.insertBusiHistoryAllParticipant(busiHistoryAllParticipant);
                                logger.info("All Participant joined and saved: " + cdrCallLegUpdate);
                            }
                        }
                    }
                    // 全会议与会者记录 end 2022.05.30

                    ParticipantInfo pi = FmeDataCache.getParticipantByUuid(cdrCallLegUpdate.getCdrId());
                    BusiHistoryParticipant busiHistoryParticipant = busiHistoryParticipantMapper.selectBusiHistoryParticipantByCallLegId(cdrCallLegUpdate.getCdrId());
                    if (busiHistoryParticipant != null)
                    {
                        if (busiHistoryParticipant.getJoined() == null || !busiHistoryParticipant.getJoined())
                        {
                            busiHistoryParticipant.setJoined(true);
                            busiHistoryParticipant.setUpdateTime(new Date());
                            busiHistoryParticipant.setJoinTime(new Date());
                            if (pi != null)
                            {
                                busiHistoryParticipant.setMediaInfo(attendeeService.toDetail(pi.getParticipant().getCallLeg()));
                            }
                            busiHistoryParticipantMapper.updateBusiHistoryParticipantForJoin(busiHistoryParticipant);
                        }
                    }
                    else
                    {
                        BusiHistoryConference bhc = busiHistoryConferenceMapper.selectBusiHistoryConferenceByCallId(cdrCallLegUpdate.getCall());
                        if (bhc != null)
                        {
                            busiHistoryParticipant = new BusiHistoryParticipant();
                            busiHistoryParticipant.setDeptId(bhc.getDeptId().intValue());
                            busiHistoryParticipant.setCallId(cdrCallLegUpdate.getCall());
                            busiHistoryParticipant.setCoSpace(bhc.getCoSpace());
                            busiHistoryParticipant.setCallLegId(cdrCallLegUpdate.getCdrId());
                            busiHistoryParticipant.setRemoteParty(cdrCallLegUpdate.getRemoteAddress());
                            busiHistoryParticipant.setName(cdrCallLegUpdate.getDisplayName());
                            busiHistoryParticipant.setHistoryConferenceId(bhc.getId());
                            busiHistoryParticipant.setJoinTime(new Date());
                            busiHistoryParticipant.setJoined(true);
                            busiHistoryParticipant.setCreateTime(new Date());
                            busiHistoryParticipant.setDurationSeconds(0);
                            if (pi != null)
                            {
                                busiHistoryParticipant.setMediaInfo(attendeeService.toDetail(pi.getParticipant().getCallLeg()));
                            }
                            String remoteParty = cdrCallLegUpdate.getRemoteAddress();
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
                            busiHistoryParticipantMapper.insertBusiHistoryParticipant(busiHistoryParticipant);
                            logger.info("Participant joined and saved: " + cdrCallLegUpdate);
                        }
                    }
                    // 更新参会者终端信息
                    updateBusiHistoryParticipantTerminal(busiHistoryParticipant);
                }
                else if (state == ParticipantState.DISCONNECT)
                {
                    if (cdrCallLegUpdate.getCdrId() != null)
                    {
                        // 全会议与会者记录 start 2022.05.30
                        if (ExternalConfigCache.getInstance().isEnableCdrAll()) {
                            BusiHistoryAllParticipant busiHistoryAllParticipant = busiHistoryAllParticipantMapper.selectBusiHistoryAllParticipantByCallLegId(cdrCallLegUpdate.getCdrId());
                            if (busiHistoryAllParticipant != null && busiHistoryAllParticipant.getOutgoingTime() == null) {
                                busiHistoryAllParticipant.setUpdateTime(new Date());
                                busiHistoryAllParticipant.setOutgoingTime(new Date());
                                busiHistoryAllParticipantMapper.updateBusiHistoryAllParticipant(busiHistoryAllParticipant);
                                logger.info("All Participant left and saved: " + cdrCallLegUpdate);
                            }
                        }
                        // 全会议与会者记录 end 2022.05.30

                        BusiHistoryParticipant bhp = busiHistoryParticipantMapper.selectBusiHistoryParticipantByCallLegId(cdrCallLegUpdate.getCdrId());
                        if (bhp != null && bhp.getOutgoingTime() == null)
                        {
                            bhp.setUpdateTime(new Date());
                            bhp.setOutgoingTime(new Date());
                            busiHistoryParticipantMapper.updateBusiHistoryParticipant(bhp);
                            logger.info("Participant left and saved: " + cdrCallLegUpdate);
                        }
                        // 更新参会者终端信息
                        updateBusiHistoryParticipantTerminal(bhp);
                    }
                
                }
            }
            catch (Throwable e)
            {
                logger.error("insertCdrCallLegUpdate error", e);
            }
        }
        return cdrCallLegUpdateMapper.insertCdrCallLegUpdate(cdrCallLegUpdate);
    }

    /**
     * 更新参会者终端信息
     *
     * @param busiHistoryParticipant
     */
    private void updateBusiHistoryParticipantTerminal(BusiHistoryParticipant busiHistoryParticipant) {
        if (busiHistoryParticipant != null) {
            if (busiHistoryParticipant.getTerminalId() != null) {
                BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiHistoryParticipant.getTerminalId());
                if (busiTerminal != null) {
                    busiHistoryParticipant.setName(busiTerminal.getName());
                }
            }
            try {
                busiHistoryParticipantTerminalService.updateBusiHistoryParticipantTerminalByBusiHistoryParticipant(busiHistoryParticipant);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 修改callLegUpdate记录
     *
     * @param cdrCallLegUpdate callLegUpdate记录
     * @return 结果
     */
    @Override
    public int updateCdrCallLegUpdate(CdrCallLegUpdate cdrCallLegUpdate)
    {
        return cdrCallLegUpdateMapper.updateCdrCallLegUpdate(cdrCallLegUpdate);
    }
    
    /**
     * 批量删除callLegUpdate记录
     *
     * @param cdrIds 需要删除的callLegUpdate记录ID
     * @return 结果
     */
    @Override
    public int deleteCdrCallLegUpdateByIds(Integer[] cdrIds)
    {
        return cdrCallLegUpdateMapper.deleteCdrCallLegUpdateByIds(cdrIds);
    }
    
    /**
     * 删除callLegUpdate记录信息
     *
     * @param cdrId callLegUpdate记录ID
     * @return 结果
     */
    @Override
    public int deleteCdrCallLegUpdateById(Integer cdrId)
    {
        return cdrCallLegUpdateMapper.deleteCdrCallLegUpdateById(cdrId);
    }
}
