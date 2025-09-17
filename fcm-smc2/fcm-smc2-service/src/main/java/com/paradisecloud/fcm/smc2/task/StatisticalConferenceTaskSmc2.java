package com.paradisecloud.fcm.smc2.task;

import com.paradisecloud.fcm.common.constant.ReportType;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.CdrCallNumDateMapper;
import com.paradisecloud.fcm.dao.mapper.CdrTaskResultMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.CdrCallNumDate;
import com.paradisecloud.fcm.dao.model.CdrTaskResult;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class StatisticalConferenceTaskSmc2 extends Smc2DelayTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticalConferenceTaskSmc2.class);

    private Long deptId;

    public StatisticalConferenceTaskSmc2(String id, long delayInMilliseconds, Long deptId) {
        super("statistics_" + id, delayInMilliseconds);
        this.deptId = deptId;
    }

    @Override
    public void run() {
        LOGGER.info("SMC2会议统计开始,ID:" + getId());

        // 统计会议数量和会议时长
        if (deptId != null) {
            BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
            Date calcDay = DateUtil.convertLocalDateToDate(LocalDate.now());
            String calcDayYmd = DateUtil.convertDateToString(calcDay, "yyyy-MM-dd");
            Date calcStartTime = DateUtil.convertDateByString(DateUtil.fillDateString(calcDayYmd, false), null);
            Date calcEndTime = DateUtil.convertDateByString(DateUtil.fillDateString(calcDayYmd, true), null);
            List<BusiHistoryConference> historyConferenceNotEndList = busiHistoryConferenceMapper.selectNotEndCalcDayHistoryConferenceList(deptId, calcEndTime);
            List<BusiHistoryConference> historyConferenceEndedList = busiHistoryConferenceMapper.selectEndedCalcDayHistoryConferenceList(deptId, calcStartTime, calcEndTime);

            long conferenceNum = 0;
            long conferenceNumConcurrency = 0;
            long conferenceDuration = 0;
            for (BusiHistoryConference busiHistoryConference : historyConferenceEndedList) {
                conferenceNumConcurrency += 1;
                if (busiHistoryConference.getConferenceStartTime().before(calcStartTime)) {
                    long durationSeconds = (busiHistoryConference.getConferenceEndTime().getTime() - calcStartTime.getTime()) /1000;
                    conferenceDuration += durationSeconds;
                } else {
                    conferenceDuration += busiHistoryConference.getDuration();
                    conferenceNum += 1;
                }
            }
            for (BusiHistoryConference busiHistoryConference : historyConferenceNotEndList) {
                conferenceNumConcurrency += 1;
                if (busiHistoryConference.getConferenceStartTime()!=null&&busiHistoryConference.getConferenceStartTime().before(calcStartTime)) {
//                    long durationSeconds = 60 * 60 * 24;
//                    conferenceDuration += durationSeconds;
                } else {
//                    long durationSeconds = (calcEndTime.getTime() - busiHistoryConference.getConferenceStartTime().getTime()) / 1000;
//                    conferenceDuration += durationSeconds;
                    conferenceNum += 1;
                }
            }

            updateMeetingNumAndDuration(deptId, conferenceNum, conferenceNumConcurrency, conferenceDuration);

            updateCallNumDate(deptId, "smcIp");
        }
    }

    public void updateMeetingNumAndDuration(Long deptId, Long conferenceNum, Long conferenceNumConcurrency, Long conferenceDuration) {
        CdrTaskResultMapper cdrTaskResultMapper = BeanFactory.getBean(CdrTaskResultMapper.class);
        Integer[] reportTypes = {ReportType.CONFERENCE_NUM, ReportType.CONFERENCE_NUM_CONCURRENCY, ReportType.CONFERENCE_DURATION};
        List<CdrTaskResult> cdrTaskResults = getCdrTaskResults(deptId, reportTypes);
        if (CollectionUtils.isEmpty(cdrTaskResults)) {
            // 添加新的记录
            CdrTaskResult conferenceNumTaskResult = buildTaskResult(ReportType.CONFERENCE_NUM, deptId, conferenceNum);
            CdrTaskResult conferenceNumConcurrencyTaskResult = buildTaskResult(ReportType.CONFERENCE_NUM_CONCURRENCY, deptId, conferenceNumConcurrency);
            CdrTaskResult conferenceDurationTaskResult = buildTaskResult(ReportType.CONFERENCE_DURATION, deptId, conferenceDuration);
            cdrTaskResultMapper.insertCdrTaskResult(conferenceNumTaskResult);
            cdrTaskResultMapper.insertCdrTaskResult(conferenceNumConcurrencyTaskResult);
            cdrTaskResultMapper.insertCdrTaskResult(conferenceDurationTaskResult);
        } else {
            // 更新记录
            for (CdrTaskResult cdrTaskResult : cdrTaskResults) {
                if (cdrTaskResult.getReportType().equals(ReportType.CONFERENCE_NUM)) {
                    cdrTaskResult.setDurationOrNum(conferenceNum);
                    cdrTaskResult.setUpdateTime(new Date());
                    cdrTaskResultMapper.updateCdrTaskResult(cdrTaskResult);
                } else if (cdrTaskResult.getReportType().equals(ReportType.CONFERENCE_NUM_CONCURRENCY)) {
                    cdrTaskResult.setDurationOrNum(conferenceNumConcurrency);
                    cdrTaskResult.setUpdateTime(new Date());
                    cdrTaskResultMapper.updateCdrTaskResult(cdrTaskResult);
                } else if (cdrTaskResult.getReportType().equals(ReportType.CONFERENCE_DURATION)) {
                    cdrTaskResult.setDurationOrNum(conferenceDuration);
                    cdrTaskResult.setUpdateTime(new Date());
                    cdrTaskResultMapper.updateCdrTaskResult(cdrTaskResult);
                }
            }
        }
    }

    private List<CdrTaskResult> getCdrTaskResults(Long deptId, Integer[] reportTypes) {
        CdrTaskResultMapper cdrTaskResultMapper = BeanFactory.getBean(CdrTaskResultMapper.class);
        String date = DateUtil.convertDateToString(LocalDate.now(), null);
        Date startDate = DateUtil.convertDateByString(DateUtil.fillDateString(date, false), null);
        Date endDate = DateUtil.convertDateByString(DateUtil.fillDateString(date, true), null);
        return cdrTaskResultMapper.selectByDateAndDeptAndReportType(deptId, startDate, endDate, reportTypes);
    }

    private List<CdrTaskResult> getCdrTaskResults(Long deptId, Integer[] reportTypes, Date startDate, Date endDate) {
        CdrTaskResultMapper cdrTaskResultMapper = BeanFactory.getBean(CdrTaskResultMapper.class);
        return cdrTaskResultMapper.selectByDateAndDeptAndReportType(deptId, startDate, endDate, reportTypes);
    }

    private CdrTaskResult buildTaskResult(int reportType, Long deptId, long durationOrNum) {
        return buildTaskResult(reportType, deptId, durationOrNum, new Date());
    }

    private CdrTaskResult buildTaskResult(int reportType, Long deptId, long durationOrNum, Date date) {
        CdrTaskResult taskResult = new CdrTaskResult();
        taskResult.setDate(date);
        taskResult.setDeptId(deptId);
        taskResult.setReportType(Integer.valueOf(reportType));
        taskResult.setDurationOrNum(Long.valueOf(durationOrNum));
        taskResult.setCreateTime(new Date());
        return taskResult;
    }


    public void updateCallNumDate(Long deptId, String mcuIp) {
        CdrCallNumDateMapper cdrCallNumDateMapper = BeanFactory.getBean(CdrCallNumDateMapper.class);
        String date = DateUtil.convertDateToString(LocalDate.now(), null);
        CdrCallNumDate cdrCallNumDate = cdrCallNumDateMapper.selectByDeptIdAndFmeIpAndDate(deptId, mcuIp, date);
        int number = 0;
        if (cdrCallNumDate == null) {
            cdrCallNumDate = new CdrCallNumDate();
            cdrCallNumDate.setFmeIp(mcuIp);
            cdrCallNumDate.setDeptId(deptId.intValue());
            number = 1;
            cdrCallNumDate.setNumber(number);
            cdrCallNumDate.setRecordDate(new Date());
            cdrCallNumDate.setCreateTime(new Date());
            cdrCallNumDateMapper.insertCdrCallNumDate(cdrCallNumDate);
        } else {
            number = cdrCallNumDate.getNumber() + 1;
            cdrCallNumDate.setNumber(number);
            cdrCallNumDate.setUpdateTime(new Date());
            cdrCallNumDateMapper.updateCdrCallNumDate(cdrCallNumDate);
        }
    }
}
