package com.paradisecloud.fcm.service.impls;

import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantTerminalService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;;

/**
 * 历史会议的参会者终端Service业务层处理
 * 
 * @author lilinhai
 * @date 2022-06-08
 */
@Service
public class BusiHistoryParticipantTerminalServiceImpl implements IBusiHistoryParticipantTerminalService
{
    @Resource
    private BusiHistoryParticipantTerminalMapper busiHistoryParticipantTerminalMapper;
    @Resource
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;
    @Resource
    private CdrTerminalUsageMapper cdrTerminalUsageMapper;
    @Resource
    private BusiTerminalMapper busiTerminalMapper;

    /**
     * 查询历史会议的参会者终端
     *
     * @param id 历史会议的参会者终端ID
     * @return 历史会议的参会者终端
     */
    @Override
    public BusiHistoryParticipantTerminal selectBusiHistoryParticipantTerminalById(Long id)
    {
        return busiHistoryParticipantTerminalMapper.selectBusiHistoryParticipantTerminalById(id);
    }

    /**
     * 查询历史会议的参会者终端列表
     *
     * @param busiHistoryParticipantTerminal 历史会议的参会者终端
     * @return 历史会议的参会者终端
     */
    @Override
    public List<BusiHistoryParticipantTerminal> selectBusiHistoryParticipantTerminalList(BusiHistoryParticipantTerminal busiHistoryParticipantTerminal)
    {
        return busiHistoryParticipantTerminalMapper.selectBusiHistoryParticipantTerminalList(busiHistoryParticipantTerminal);
    }

    /**
     * 新增历史会议的参会者终端
     *
     * @param busiHistoryParticipantTerminal 历史会议的参会者终端
     * @return 结果
     */
    @Override
    public int insertBusiHistoryParticipantTerminal(BusiHistoryParticipantTerminal busiHistoryParticipantTerminal)
    {
        busiHistoryParticipantTerminal.setCreateTime(new Date());
        return busiHistoryParticipantTerminalMapper.insertBusiHistoryParticipantTerminal(busiHistoryParticipantTerminal);
    }

    /**
     * 修改历史会议的参会者终端
     *
     * @param busiHistoryParticipantTerminal 历史会议的参会者终端
     * @return 结果
     */
    @Override
    public int updateBusiHistoryParticipantTerminal(BusiHistoryParticipantTerminal busiHistoryParticipantTerminal)
    {
        busiHistoryParticipantTerminal.setUpdateTime(new Date());
        return busiHistoryParticipantTerminalMapper.updateBusiHistoryParticipantTerminal(busiHistoryParticipantTerminal);
    }

    /**
     * 批量删除历史会议的参会者终端
     *
     * @param ids 需要删除的历史会议的参会者终端ID
     * @return 结果
     */
    @Override
    public int deleteBusiHistoryParticipantTerminalByIds(Long[] ids)
    {
        return busiHistoryParticipantTerminalMapper.deleteBusiHistoryParticipantTerminalByIds(ids);
    }

    /**
     * 删除历史会议的参会者终端信息
     *
     * @param id 历史会议的参会者终端ID
     * @return 结果
     */
    @Override
    public int deleteBusiHistoryParticipantTerminalById(Long id)
    {
        return busiHistoryParticipantTerminalMapper.deleteBusiHistoryParticipantTerminalById(id);
    }

    /**
     * 查询历史会议的参会者终端
     *
     * @param historyConferenceId 会议ID
     * @param terminalId          终端ID
     * @param remoteParty
     * @return 历史会议的参会者终端
     */
    @Override
    public BusiHistoryParticipantTerminal selectBusiHistoryParticipantTerminalByConferenceTerminal(@NotNull Long historyConferenceId, Long terminalId, @NotNull String remoteParty) {
        if (terminalId == null) {
            terminalId = 0L;
        }
        if (terminalId > 0) {
            if (remoteParty.contains(":")) {
                remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
            }
        }
        BusiHistoryParticipantTerminal busiHistoryParticipantTerminalCon = new BusiHistoryParticipantTerminal();
        busiHistoryParticipantTerminalCon.setHistoryConferenceId(historyConferenceId);
        busiHistoryParticipantTerminalCon.setTerminalId(terminalId);
        busiHistoryParticipantTerminalCon.setRemoteParty(remoteParty);
        List<BusiHistoryParticipantTerminal> busiHistoryParticipantTerminalList = busiHistoryParticipantTerminalMapper.selectBusiHistoryParticipantTerminalList(busiHistoryParticipantTerminalCon);
        if (busiHistoryParticipantTerminalList.size() > 0) {
            return busiHistoryParticipantTerminalList.get(0);
        }
        return null;
    }

    /**
     * 根据参会者信息更新（新增）参会者终端信息
     *
     * @param busiHistoryParticipant
     * @return
     */
    @Override
    public int updateBusiHistoryParticipantTerminalByBusiHistoryParticipant(BusiHistoryParticipant busiHistoryParticipant) {
        int result = 0;
        if (busiHistoryParticipant != null) {
            if (busiHistoryParticipant.getHistoryConferenceId() != null) {
                Long historyConferenceId = busiHistoryParticipant.getHistoryConferenceId();
                Long terminalId = busiHistoryParticipant.getTerminalId();
                if (terminalId == null) {
                    terminalId = 0L;
                }
                String remoteParty = busiHistoryParticipant.getRemoteParty();
                if (terminalId > 0) {
                    if (remoteParty.contains(":")) {
                        remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
                    }
                }
                BusiHistoryParticipantTerminal busiHistoryParticipantTerminalCon = new BusiHistoryParticipantTerminal();
                busiHistoryParticipantTerminalCon.setHistoryConferenceId(historyConferenceId);
                if (terminalId > 0) {
                    busiHistoryParticipantTerminalCon.setTerminalId(terminalId);
                } else {
                    busiHistoryParticipantTerminalCon.setRemoteParty(remoteParty);
                }
                List<BusiHistoryParticipantTerminal> busiHistoryParticipantTerminalList = busiHistoryParticipantTerminalMapper.selectBusiHistoryParticipantTerminalList(busiHistoryParticipantTerminalCon);
                if (busiHistoryParticipantTerminalList.size() > 0) {
                    BusiHistoryParticipantTerminal busiHistoryParticipantTerminal = busiHistoryParticipantTerminalList.get(0);
                    if (busiHistoryParticipantTerminal.getJoined() == null || !busiHistoryParticipantTerminal.getJoined()) {
                        if (busiHistoryParticipant.getJoined() != null && busiHistoryParticipant.getJoined()) {
                            busiHistoryParticipantTerminal.setJoined(busiHistoryParticipant.getJoined());
                            busiHistoryParticipantTerminal.setJoinTime(busiHistoryParticipant.getJoinTime());
                            busiHistoryParticipantTerminal.setMediaInfo(busiHistoryParticipant.getMediaInfo());
                            busiHistoryParticipantTerminal.setUpdateTime(new Date());
                            BusiHistoryParticipant busiHistoryParticipantCon = new BusiHistoryParticipant();
                            busiHistoryParticipantCon.setHistoryConferenceId(historyConferenceId);
                            if (terminalId > 0) {
                                busiHistoryParticipantCon.setTerminalId(terminalId);
                            } else {
                                busiHistoryParticipantCon.setRemoteParty(remoteParty);
                            }
                            TerminalJoinedCount terminalJoinedCount = busiHistoryParticipantMapper.getTerminalJoinedCount(busiHistoryParticipantCon.getHistoryConferenceId(), busiHistoryParticipantCon.getTerminalId(), busiHistoryParticipantCon.getRemoteParty());
                            if (terminalJoinedCount != null) {
                                busiHistoryParticipantTerminal.setDurationSeconds(terminalJoinedCount.getDurationSeconds());
                                busiHistoryParticipantTerminal.setJoinedTimes(terminalJoinedCount.getJoinedTimes());
                            }
                            result = busiHistoryParticipantTerminalMapper.updateBusiHistoryParticipantTerminal(busiHistoryParticipantTerminal);
                            if (terminalId > 0) {
                                // 终端统计信息
                                updateTerminalUsage(terminalId);
                            }
                        }
                    } else {
                        if (busiHistoryParticipant.getJoined() != null && busiHistoryParticipant.getJoined()) {
                            busiHistoryParticipantTerminal.setMediaInfo(busiHistoryParticipant.getMediaInfo());
                            busiHistoryParticipantTerminal.setUpdateTime(new Date());
                            if (busiHistoryParticipantTerminal.getOutgoingTime() == null) {
                                busiHistoryParticipantTerminal.setOutgoingTime(busiHistoryParticipant.getOutgoingTime());
                            } else {
                                if (busiHistoryParticipant.getOutgoingTime() != null) {
                                    if (busiHistoryParticipant.getOutgoingTime().after(busiHistoryParticipantTerminal.getOutgoingTime())) {
                                        busiHistoryParticipantTerminal.setOutgoingTime(busiHistoryParticipant.getOutgoingTime());
                                    }
                                }
                            }
                            BusiHistoryParticipant busiHistoryParticipantCon = new BusiHistoryParticipant();
                            busiHistoryParticipantCon.setHistoryConferenceId(historyConferenceId);
                            if (terminalId > 0) {
                                busiHistoryParticipantCon.setTerminalId(terminalId);
                            } else {
                                busiHistoryParticipantCon.setRemoteParty(remoteParty);
                            }
                            TerminalJoinedCount terminalJoinedCount = busiHistoryParticipantMapper.getTerminalJoinedCount(busiHistoryParticipantCon.getHistoryConferenceId(), busiHistoryParticipantCon.getTerminalId(), busiHistoryParticipantCon.getRemoteParty());
                            if (terminalJoinedCount != null) {
                                busiHistoryParticipantTerminal.setDurationSeconds(terminalJoinedCount.getDurationSeconds());
                                busiHistoryParticipantTerminal.setJoinedTimes(terminalJoinedCount.getJoinedTimes());
                            }
                            result = busiHistoryParticipantTerminalMapper.updateBusiHistoryParticipantTerminal(busiHistoryParticipantTerminal);
                            if (terminalId > 0) {
                                // 终端统计信息
                                updateTerminalUsage(terminalId);
                            }
                        }
                    }
                } else {
                    BusiHistoryParticipantTerminal busiHistoryParticipantTerminal = new BusiHistoryParticipantTerminal();
                    busiHistoryParticipantTerminal.setHistoryConferenceId(historyConferenceId);
                    busiHistoryParticipantTerminal.setTerminalId(terminalId);
                    busiHistoryParticipantTerminal.setRemoteParty(remoteParty);
                    busiHistoryParticipantTerminal.setCreateTime(busiHistoryParticipant.getCreateTime());
                    busiHistoryParticipantTerminal.setDeptId(busiHistoryParticipant.getDeptId().longValue());
                    busiHistoryParticipantTerminal.setName(busiHistoryParticipant.getName());
                    busiHistoryParticipantTerminal.setJoined(busiHistoryParticipant.getJoined());
                    busiHistoryParticipantTerminal.setJoinTime(busiHistoryParticipant.getJoinTime());
                    if (busiHistoryParticipantTerminal.getJoined()) {
                        busiHistoryParticipantTerminal.setJoinedTimes(1);
                    } else {
                        busiHistoryParticipantTerminal.setJoinedTimes(0);
                    }
                    busiHistoryParticipantTerminal.setMediaInfo(busiHistoryParticipant.getMediaInfo());
                    result = busiHistoryParticipantTerminalMapper.insertBusiHistoryParticipantTerminal(busiHistoryParticipantTerminal);
                    if (terminalId > 0) {
                        // 终端统计信息
                        updateTerminalUsage(terminalId);
                    }
                }
            }
        }
        return result;
    }

    private int updateTerminalUsage(Long terminalId) {
        BusiTerminal busiTerminal = busiTerminalMapper.selectBusiTerminalById(terminalId);
        if (busiTerminal == null) {
            return 0;
        }
        Date today = new Date();
        Date startTime = DateUtil.fillDate(today, false);
        Date endTime = DateUtil.fillDate(today, true);
        int conferenceNum = 0;
        int durationSecondsTotal = 0;
        // 统计日的会议
        List<BusiHistoryParticipant> busiHistoryParticipantList = busiHistoryParticipantMapper.selectHistoryParticipantListForTerminal(terminalId, startTime, endTime, null, null);
        conferenceNum += busiHistoryParticipantList.size();
        for (BusiHistoryParticipant busiHistoryParticipantTemp : busiHistoryParticipantList) {
            if (busiHistoryParticipantTemp.getOutgoingTime() != null) {
                if (busiHistoryParticipantTemp.getJoinTime().before(startTime)) {
                    int durationSeconds = (int) ((busiHistoryParticipantTemp.getOutgoingTime().getTime() - startTime.getTime()) / 1000);
                    durationSecondsTotal += durationSeconds;
                } else {
                    int durationSeconds = (int) ((busiHistoryParticipantTemp.getOutgoingTime().getTime() - busiHistoryParticipantTemp.getJoinTime().getTime()) / 1000);
                    durationSecondsTotal += durationSeconds;
                }
            }
        }
        CdrTerminalUsage cdrTerminalUsageCon = new CdrTerminalUsage();
        cdrTerminalUsageCon.setDeptId(busiTerminal.getDeptId());
        cdrTerminalUsageCon.setTerminalId(terminalId);
        cdrTerminalUsageCon.setDate(startTime);
        List<CdrTerminalUsage> cdrTerminalUsageList = cdrTerminalUsageMapper.selectCdrTerminalUsageList(cdrTerminalUsageCon);
        if (cdrTerminalUsageList.size() > 0) {
            CdrTerminalUsage cdrTerminalUsage = cdrTerminalUsageList.get(0);
            CdrTerminalUsage cdrTerminalUsageUpdate = new CdrTerminalUsage();
            cdrTerminalUsageUpdate.setId(cdrTerminalUsage.getId());
            cdrTerminalUsageUpdate.setNum(conferenceNum);
            cdrTerminalUsageUpdate.setDurationSeconds(durationSecondsTotal);
            cdrTerminalUsageUpdate.setUpdateTime(today);
            return cdrTerminalUsageMapper.updateCdrTerminalUsage(cdrTerminalUsageUpdate);
        } else {
            CdrTerminalUsage cdrTerminalUsageInsert = new CdrTerminalUsage();
            cdrTerminalUsageInsert.setDeptId(busiTerminal.getDeptId());
            cdrTerminalUsageInsert.setTerminalId(terminalId);
            cdrTerminalUsageInsert.setDate(startTime);
            cdrTerminalUsageInsert.setNum(conferenceNum);
            cdrTerminalUsageInsert.setDurationSeconds(durationSecondsTotal);
            cdrTerminalUsageInsert.setCreateTime(today);
            return cdrTerminalUsageMapper.insertCdrTerminalUsage(cdrTerminalUsageInsert);
        }
    }
}
