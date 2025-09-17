package com.paradisecloud.fcm.cdr.service.task;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrTaskResultService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantMapper;
import com.paradisecloud.fcm.dao.mapper.CdrTerminalUsageMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.dao.model.CdrTerminalUsage;
import com.paradisecloud.fcm.service.interfaces.ICdrTerminalUsageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * CDR定时任务类
 */
@Component
public class CdrScheduleTask {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;
    @Resource
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;
    @Resource
    private CdrTerminalUsageMapper cdrTerminalUsageMapper;
    @Resource
    private ICdrTaskResultService cdrTaskResultService;
    @Resource
    private ICdrTerminalUsageService cdrTerminalUsageService;

    /**
     * 每天00:30启动计算前一天终端在会数量及时长
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void calcTerminalUsage() {
        logger.info("计算终端使用情况定时任务启动");
        Date today = new Date();
        long startId = 0;
        Date startTime = DateUtil.fillDate(today, false);
        LocalDate localDateCalcDay = LocalDate.now().minusDays(1);
        Date calcDay = DateUtil.convertLocalDateToDate(localDateCalcDay);
        Date calcStartTime = DateUtil.fillDate(calcDay, false);
        Date calcEndTime = DateUtil.fillDate(calcDay, true);
        List<CdrTerminalUsage> cdrTerminalUsageList = cdrTerminalUsageMapper.selectCdrTerminalUsageListForJob(startId, calcStartTime);
        for (; cdrTerminalUsageList.size() > 0;) {
            for (CdrTerminalUsage cdrTerminalUsage : cdrTerminalUsageList) {
                try {
                    updateTerminalUsage(cdrTerminalUsage, calcStartTime, calcEndTime);
                } catch (Exception e) {
                    logger.error("终端使用情况计算错误。terminalId:" + cdrTerminalUsage.getTerminalId(), e);
                }
                startId = cdrTerminalUsage.getId();
            }

            if (cdrTerminalUsageList.size() < 1000) {
                break;
            } else {
                cdrTerminalUsageList = cdrTerminalUsageMapper.selectCdrTerminalUsageListForJob(startId, calcStartTime);
            }
        }
    }

    private int updateTerminalUsage(CdrTerminalUsage cdrTerminalUsage, Date calcStartTime, Date calcEndTime) {
        Long deptId = cdrTerminalUsage.getDeptId();
        Long terminalId = cdrTerminalUsage.getTerminalId();

        int conferenceNum = 0;
        int durationSecondsTotal = 0;
        // 统计日的会议
        List<BusiHistoryParticipant> busiHistoryParticipantList = busiHistoryParticipantMapper.selectHistoryParticipantListForTerminal(terminalId, calcStartTime, calcEndTime, null, null);
        conferenceNum += busiHistoryParticipantList.size();
        for (BusiHistoryParticipant busiHistoryParticipantTemp : busiHistoryParticipantList) {
            if (busiHistoryParticipantTemp.getOutgoingTime() != null) {
                if (busiHistoryParticipantTemp.getJoinTime().before(calcStartTime)) {
                    int durationSeconds = (int) ((busiHistoryParticipantTemp.getOutgoingTime().getTime() - calcStartTime.getTime()) / 1000);
                    durationSecondsTotal += durationSeconds;
                } else {
                    int durationSeconds = (int) ((busiHistoryParticipantTemp.getOutgoingTime().getTime() - busiHistoryParticipantTemp.getJoinTime().getTime()) / 1000);
                    durationSecondsTotal += durationSeconds;
                }
            } else {
                if (busiHistoryParticipantTemp.getJoinTime().before(calcStartTime)) {
                    int durationSeconds = (int) ((calcEndTime.getTime() - calcStartTime.getTime()) / 1000);
                    durationSecondsTotal += durationSeconds;
                } else {
                    int durationSeconds = (int) ((calcEndTime.getTime() - busiHistoryParticipantTemp.getJoinTime().getTime()) / 1000);
                    durationSecondsTotal += durationSeconds;
                }
            }
        }
        CdrTerminalUsage cdrTerminalUsageUpdate = new CdrTerminalUsage();
        cdrTerminalUsageUpdate.setId(cdrTerminalUsage.getId());
        cdrTerminalUsageUpdate.setNum(conferenceNum);
        cdrTerminalUsageUpdate.setDurationSeconds(durationSecondsTotal);
        cdrTerminalUsageUpdate.setUpdateTime(new Date());
        return cdrTerminalUsageService.updateCdrTerminalUsage(cdrTerminalUsageUpdate);
    }

    /**
     * 每天01:00启动计算部门会议情况
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void calcConference() {
        logger.info("计算部门会议情况定时任务启动");
        LocalDate localDateCalcDay = LocalDate.now().minusDays(1);
        Date calcDay = DateUtil.convertLocalDateToDate(localDateCalcDay);
        String calcDayYmd = DateUtil.convertDateToString(calcDay, "yyyy-MM-dd");
        Date calcStartTime = DateUtil.convertDateByString(DateUtil.fillDateString(calcDayYmd, false), null);
        Date calcEndTime = DateUtil.convertDateByString(DateUtil.fillDateString(calcDayYmd, true), null);
        List<Long> deptList = busiHistoryConferenceMapper.selectNotEndCalcDayHistoryConferenceDeptList(calcEndTime);
        for (Long deptId : deptList) {
            List<BusiHistoryConference> busiHistoryConferenceNotEndList = busiHistoryConferenceMapper.selectNotEndCalcDayHistoryConferenceList(deptId, calcEndTime);
            try {
                cdrTaskResultService.updateMeetingNumAndDuration(deptId, busiHistoryConferenceNotEndList, calcStartTime, calcEndTime);
            } catch (Exception e) {
                logger.error("部门会议情况计算错误。deptId:" + deptId, e);
            }
            try {
                cdrTaskResultService.updateMeetingNumAndDuration(deptId);
            } catch (Exception e) {
                logger.error("部门会议情况计算错误。deptId:" + deptId, e);
            }
        }
    }
}
