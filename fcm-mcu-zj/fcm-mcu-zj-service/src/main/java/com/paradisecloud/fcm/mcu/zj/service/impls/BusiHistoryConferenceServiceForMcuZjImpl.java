package com.paradisecloud.fcm.mcu.zj.service.impls;

import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IAttendeeForMcuZjService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiHistoryConferenceForMcuZjService;
import com.paradisecloud.fcm.mcu.zj.task.DelayTaskService;
import com.paradisecloud.fcm.mcu.zj.task.EndConferenceTask;
import com.paradisecloud.fcm.mcu.zj.task.StatisticalConferenceTask;
import com.paradisecloud.fcm.mcu.zj.task.UpdateAttendeeMediaInfoTask;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantTerminalService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 历史会议，每次挂断会保存该历史记录Service业务层处理
 *
 * @author lilinhai
 * @date 2021-01-20
 */
@Transactional
@Service
public class BusiHistoryConferenceServiceForMcuZjImpl implements IBusiHistoryConferenceForMcuZjService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;
    @Resource
    private BusiHistoryCallMapper busiHistoryCallMapper;
    @Resource
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;
    @Resource
    private IAttendeeForMcuZjService attendeeForMcuZjService;
    @Resource
    private IBusiHistoryParticipantTerminalService busiHistoryParticipantTerminalService;
    @Resource
    private DelayTaskService delayTaskService;
    @Resource
    private CdrCallLegStartMapper cdrCallLegStartMapper;
    @Resource
    private CdrCallLegEndMapper cdrCallLegEndMapper;

    /**
     * 查询历史会议，每次挂断会保存该历史记录
     *
     * @param id 历史会议，每次挂断会保存该历史记录ID
     * @return 历史会议，每次挂断会保存该历史记录
     */
    @Override
    public BusiHistoryConference selectBusiHistoryConferenceById(Long id) {
        return busiHistoryConferenceMapper.selectBusiHistoryConferenceById(id);
    }

    /**
     * 查询历史会议，每次挂断会保存该历史记录列表
     *
     * @param busiHistoryConference 历史会议，每次挂断会保存该历史记录
     * @return 历史会议，每次挂断会保存该历史记录
     */
    @Override
    public List<BusiHistoryConference> selectBusiHistoryConferenceList(BusiHistoryConference busiHistoryConference) {
        return busiHistoryConferenceMapper.selectBusiHistoryConferenceList(busiHistoryConference);
    }

    /**
     * 新增历史会议，每次挂断会保存该历史记录
     *
     * @param busiHistoryConference 历史会议，每次挂断会保存该历史记录
     * @return 结果
     */
    @Override
    public int insertBusiHistoryConference(BusiHistoryConference busiHistoryConference) {
        busiHistoryConference.setCreateTime(new Date());
        return busiHistoryConferenceMapper.insertBusiHistoryConference(busiHistoryConference);
    }

    /**
     * @param conferenceContext void
     * @return
     */
    public BusiHistoryConference saveHistory(McuZjConferenceContext conferenceContext) {
        try {
            BusiHistoryConference busiHistoryConference = new BusiHistoryConference();
            busiHistoryConference.setDeptId(conferenceContext.getDeptId());
            busiHistoryConference.setBandwidth(conferenceContext.getBandwidth());
            busiHistoryConference.setCallLegProfileId(conferenceContext.getTemplateConferenceId().toString());
            busiHistoryConference.setName(conferenceContext.getName());
            busiHistoryConference.setNumber(conferenceContext.getConferenceNumber());
            busiHistoryConference.setDeviceNum(0);
            busiHistoryConference.setCoSpace(conferenceContext.getCoSpaceId());
            busiHistoryConference.setCreateTime(new Date());
            busiHistoryConference.setConferenceStartTime(conferenceContext.getStartTime());
            busiHistoryConference.setCreateUserId(conferenceContext.getCreateUserId());

            int type = 0;
            if (conferenceContext.getAppointmentType() != null) {
                if (conferenceContext.getAppointmentType() == 2) {
                    type = 2;
                } else {
                    type = 1;
                }
            }
            busiHistoryConference.setType(type);
            busiHistoryConference.setDuration(0);
            busiHistoryConferenceMapper.insertBusiHistoryConference(busiHistoryConference);

            // 会议统计
            StatisticalConferenceTask statisticalConferenceTask = new StatisticalConferenceTask(busiHistoryConference.getDeptId().toString(), 10000, busiHistoryConference.getDeptId());
            delayTaskService.addTask(statisticalConferenceTask);
            return busiHistoryConference;
        } catch (Throwable e) {
            logger.error("saveHistory error", e);
            return null;
        }
    }

    public BusiHistoryConference saveHistory(BusiHistoryConference busiHistoryConference, McuZjConferenceContext conferenceContext) {
        try {
            busiHistoryConference.setConferenceStartTime(conferenceContext.getStartTime());
            if (conferenceContext.getEndTime() != null) {
                busiHistoryConference.setConferenceEndTime(conferenceContext.getEndTime());
                busiHistoryConference.setDuration((int) ((conferenceContext.getEndTime().getTime() - conferenceContext.getStartTime().getTime()) / 1000));
            }
            int type = 0;
            if (conferenceContext.getAppointmentType() != null) {
                if (conferenceContext.getAppointmentType() == 2) {
                    type = 2;
                } else {
                    type = 1;
                }
            }
            busiHistoryConference.setType(type);
            busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConference);

            if (busiHistoryConference.getConferenceEndTime() != null) {
                // 结束会议
                EndConferenceTask endConferenceTask = new EndConferenceTask(busiHistoryConference.getId().toString(), 1000, busiHistoryConference.getId());
                delayTaskService.addTask(endConferenceTask);
            }

            // 会议统计
            StatisticalConferenceTask statisticalConferenceTask = new StatisticalConferenceTask(busiHistoryConference.getDeptId().toString(), 10000, busiHistoryConference.getDeptId());
            delayTaskService.addTask(statisticalConferenceTask);

            return busiHistoryConference;
        } catch (Throwable e) {
            logger.error("saveHistory error", e);
            return null;
        }
    }

    /**
     * 修改历史会议，每次挂断会保存该历史记录
     *
     * @param busiHistoryConference 历史会议，每次挂断会保存该历史记录
     * @return 结果
     */
    @Override
    public int updateBusiHistoryConference(BusiHistoryConference busiHistoryConference) {
        busiHistoryConference.setUpdateTime(new Date());

        // 绑定终端归属部门
        LoginUser loginUser = SecurityUtils.getLoginUser();
        busiHistoryConference.setDeptId(loginUser.getUser().getDeptId());
        return busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConference);
    }

    /**
     * 批量删除历史会议，每次挂断会保存该历史记录
     *
     * @param ids 需要删除的历史会议，每次挂断会保存该历史记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiHistoryConferenceByIds(Long[] ids) {
        return busiHistoryConferenceMapper.deleteBusiHistoryConferenceByIds(ids);
    }

    /**
     * 删除历史会议，每次挂断会保存该历史记录信息
     *
     * @param id 历史会议，每次挂断会保存该历史记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiHistoryConferenceById(Long id) {
        return busiHistoryConferenceMapper.deleteBusiHistoryConferenceById(id);
    }

    /**
     * 更新参会者
     *
     * @param conferenceContext
     * @param attendee
     */
    @Override
    public void updateBusiHistoryParticipant(McuZjConferenceContext conferenceContext, AttendeeForMcuZj attendee, boolean updateMediaInfo) {
        if (attendee.isMeetingJoined()) {
            BusiHistoryConference busiHistoryConference = conferenceContext.getHistoryConference();
            if (busiHistoryConference != null) {
                BusiHistoryParticipant busiHistoryParticipant = busiHistoryParticipantMapper.selectBusiHistoryParticipantByCallLegId(attendee.getParticipantUuid());
                if (busiHistoryParticipant != null) {
                    if (busiHistoryParticipant.getJoined() == null || !busiHistoryParticipant.getJoined()) {
                        busiHistoryParticipant.setJoined(true);
                        busiHistoryParticipant.setJoinTime(new Date());
                        busiHistoryParticipant.setUpdateTime(new Date());
                        busiHistoryParticipantMapper.updateBusiHistoryParticipant(busiHistoryParticipant);
                    } else {
                        if (updateMediaInfo) {
                            busiHistoryParticipant.setMediaInfo(attendeeForMcuZjService.detail(conferenceContext, attendee));
                            busiHistoryParticipant.setUpdateTime(new Date());
                            busiHistoryParticipantMapper.updateBusiHistoryParticipantForJoin(busiHistoryParticipant);
                            UpdateAttendeeMediaInfoTask updateAttendeeMediaInfoTask = new UpdateAttendeeMediaInfoTask(attendee.getId(), 20000, conferenceContext, attendee, busiHistoryParticipant);
                            delayTaskService.addTask(updateAttendeeMediaInfoTask);
                        }
                    }
                } else {
                    BusiHistoryCall busiHistoryCallCon = new BusiHistoryCall();
                    busiHistoryCallCon.setHistoryConferenceId(busiHistoryConference.getId());
                    List<BusiHistoryCall> busiHistoryCallList = busiHistoryCallMapper.selectBusiHistoryCallList(busiHistoryCallCon);
                    BusiHistoryCall busiHistoryCall = null;
                    if (busiHistoryCallList.size() > 0) {
                        busiHistoryCall = busiHistoryCallList.get(0);
                    }
                    busiHistoryParticipant = new BusiHistoryParticipant();
                    busiHistoryParticipant.setCreateTime(new Date());
                    busiHistoryParticipant.setDeptId(busiHistoryConference.getDeptId().intValue());
                    if (busiHistoryCall != null) {
                        busiHistoryParticipant.setCallId(busiHistoryCall.getCallId());
                    }
                    busiHistoryParticipant.setCoSpace(conferenceContext.getCoSpaceId());
                    busiHistoryParticipant.setCallLegId(attendee.getParticipantUuid());
                    busiHistoryParticipant.setName(attendee.getName());
                    busiHistoryParticipant.setHistoryConferenceId(busiHistoryConference.getId());
                    busiHistoryParticipant.setJoinTime(new Date());
                    busiHistoryParticipant.setCreateTime(new Date());
                    busiHistoryParticipant.setJoined(true);
                    if (updateMediaInfo) {
                        busiHistoryParticipant.setMediaInfo(attendeeForMcuZjService.detail(conferenceContext, attendee));
                        UpdateAttendeeMediaInfoTask updateAttendeeMediaInfoTask = new UpdateAttendeeMediaInfoTask(attendee.getId(), 20000, conferenceContext, attendee, busiHistoryParticipant);
                        delayTaskService.addTask(updateAttendeeMediaInfoTask);
                    }
                    String remoteParty = attendee.getRemoteParty();
                    if (remoteParty.contains(":")) {
                        remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
                    }
                    busiHistoryParticipant.setRemoteParty(remoteParty);
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
                    if (busiTerminal != null) {
                        {
                            busiHistoryParticipant.setTerminalId(busiTerminal.getId());
                        }
                    }
                    busiHistoryParticipantMapper.insertBusiHistoryParticipant(busiHistoryParticipant);
                    try {
                        CdrCallLegStart cdrCallLegStart = new CdrCallLegStart();
                        Date date = new Date();
                        cdrCallLegStart.setCdrId(busiHistoryParticipant.getCallLegId());
                        cdrCallLegStart.setRecordIndex(0);
                        cdrCallLegStart.setCorrelatorIndex(0);
                        cdrCallLegStart.setTime(date);
                        cdrCallLegStart.setDisplayName(busiHistoryParticipant.getName());
                        cdrCallLegStart.setRemoteParty(busiHistoryParticipant.getRemoteParty());
                        cdrCallLegStart.setRemoteAddress(busiHistoryParticipant.getRemoteParty());
                        cdrCallLegStart.setCall(busiHistoryParticipant.getCallId());
                        cdrCallLegStart.setSession(busiHistoryParticipant.getCallId());
                        cdrCallLegStart.setCreateTime(date);
                        cdrCallLegStart.setRecording(false);
                        cdrCallLegStart.setStreaming(false);
                        cdrCallLegStart.setDirection("outgoing");
                        String type = "sip";
                        Integer protoType = attendee.getProtoType();
                        if (protoType != null) {
                            if (protoType == 1) {
                                type = "h323";
                            } else if (protoType == 3) {
                                type = "zjcs";
                            }
                        }
                        cdrCallLegStart.setType(type);
                        cdrCallLegStartMapper.insertCdrCallLegStart(cdrCallLegStart);
                    } catch (Exception e) {
                    }
                    logger.info("Participant joined and saved: " + attendee.toString());
                }
                // 更新参会者终端信息
                updateBusiHistoryParticipantTerminal(busiHistoryParticipant);
            }
        } else {
            BusiHistoryParticipant busiHistoryParticipant = busiHistoryParticipantMapper.selectBusiHistoryParticipantByCallLegId(attendee.getParticipantUuid());
            if (busiHistoryParticipant != null) {
                if (busiHistoryParticipant.getOutgoingTime() == null || busiHistoryParticipant.getDurationSeconds() == null || busiHistoryParticipant.getDurationSeconds().intValue() == 0) {
                    busiHistoryParticipant.setOutgoingTime(new Date());
                    busiHistoryParticipant.setUpdateTime(new Date());
                    busiHistoryParticipant.setDurationSeconds((int) ((busiHistoryParticipant.getOutgoingTime().getTime() - busiHistoryParticipant.getJoinTime().getTime()) / 1000));
                    busiHistoryParticipantMapper.updateBusiHistoryParticipant(busiHistoryParticipant);
                    logger.info("Participant left and saved: " + attendee.toString());

                    // 更新参会者终端信息
                    updateBusiHistoryParticipantTerminal(busiHistoryParticipant);
                }
                try {
                    CdrCallLegEnd cdrCallLegEnd = new CdrCallLegEnd();
                    Date date = new Date();
                    cdrCallLegEnd.setRecordIndex(0);
                    cdrCallLegEnd.setCorrelatorIndex(0);
                    cdrCallLegEnd.setTime(date);
                    cdrCallLegEnd.setSession(busiHistoryParticipant.getCallId());
                    cdrCallLegEnd.setCreateTime(date);
                    cdrCallLegEnd.setCdrId(busiHistoryParticipant.getCallLegId());
                    cdrCallLegEnd.setDurationSeconds(busiHistoryParticipant.getDurationSeconds());
                    if (cdrCallLegEnd.getReason() == null) {
                        if (attendee.getCallLegEndReasonEnum() != null) {
                            cdrCallLegEnd.setReason(attendee.getCallLegEndReasonEnum());
                        } else {
                            cdrCallLegEnd.setReason(CallLegEndReasonEnum.CALL_ENDED);
                        }
                    }
                    cdrCallLegEndMapper.insertCdrCallLegEnd(cdrCallLegEnd);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 更新参会者
     *
     * @param busiHistoryParticipant
     * @param busiHistoryConference
     */
    @Override
    public void updateBusiHistoryParticipant(BusiHistoryParticipant busiHistoryParticipant, BusiHistoryConference busiHistoryConference) {
        if (busiHistoryParticipant != null && busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() != null) {
            if (busiHistoryParticipant.getOutgoingTime() == null || busiHistoryParticipant.getDurationSeconds() == null || busiHistoryParticipant.getDurationSeconds().intValue() == 0) {
                busiHistoryParticipant.setOutgoingTime(busiHistoryConference.getConferenceEndTime());
                busiHistoryParticipant.setUpdateTime(new Date());
                busiHistoryParticipant.setDurationSeconds((int) ((busiHistoryParticipant.getOutgoingTime().getTime() - busiHistoryParticipant.getJoinTime().getTime()) / 1000));
                busiHistoryParticipantMapper.updateBusiHistoryParticipant(busiHistoryParticipant);
                logger.info("Participant left and saved: " + busiHistoryParticipant.toString());

                // 更新参会者终端信息
                updateBusiHistoryParticipantTerminal(busiHistoryParticipant);
            }
            try {
                CdrCallLegEnd cdrCallLegEnd = new CdrCallLegEnd();
                Date date = new Date();
                cdrCallLegEnd.setRecordIndex(0);
                cdrCallLegEnd.setCorrelatorIndex(0);
                cdrCallLegEnd.setTime(date);
                cdrCallLegEnd.setSession(busiHistoryParticipant.getCallId());
                cdrCallLegEnd.setCreateTime(date);
                cdrCallLegEnd.setCdrId(busiHistoryParticipant.getCallLegId());
                cdrCallLegEnd.setDurationSeconds(busiHistoryParticipant.getDurationSeconds());
                cdrCallLegEnd.setReason(CallLegEndReasonEnum.CALL_ENDED);
                cdrCallLegEndMapper.insertCdrCallLegEnd(cdrCallLegEnd);
            } catch (Exception e) {
            }
        }
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
}
