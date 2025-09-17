package com.paradisecloud.fcm.cdr.service.impls;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrTaskResultService;
import com.paradisecloud.fcm.common.constant.GroupType;
import com.paradisecloud.fcm.common.constant.ReportType;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.enums.CallLegEndAlarmTypeEnum;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import com.paradisecloud.system.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author johnson liu
 * @Date 2021/6/8 0:17
 **/
@Service
public class CdrTaskResultServiceImpl implements ICdrTaskResultService
{
    
    @Autowired
    private CdrTaskResultMapper cdrTaskResultMapper;
    
    @Autowired
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;
    
    @Autowired
    private CdrCallLegEndAlarmMapper cdrCallLegEndAlarmMapper;
    
    @Autowired
    private CdrCallLegEndMediaInfoMapper cdrCallLegEndMediaInfoMapper;

    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;
    
    /**
     * 更新每天参会的会议数量和会议总时长
     *
     * @param deptId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int updateMeetingNumAndDuration(Long deptId)
    {
        // 统计会议数量和会议时长
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
            if (busiHistoryConference.getConferenceStartTime().before(calcStartTime)) {
//                long durationSeconds = 60 * 60 * 24;
//                conferenceDuration += durationSeconds;
            } else {
//                long durationSeconds = (calcEndTime.getTime() - busiHistoryConference.getConferenceStartTime().getTime()) / 1000;
//                conferenceDuration += durationSeconds;
                conferenceNum += 1;
            }
        }

        Integer[] reportTypes = {ReportType.CONFERENCE_NUM, ReportType.CONFERENCE_NUM_CONCURRENCY, ReportType.CONFERENCE_DURATION};
        List<CdrTaskResult> cdrTaskResults = getCdrTaskResults(deptId, reportTypes, calcStartTime, calcEndTime);
        if (CollectionUtils.isEmpty(cdrTaskResults))
        {
            // 添加新的记录
            CdrTaskResult conferenceNumTaskResult = buildTaskResult(ReportType.CONFERENCE_NUM, deptId, conferenceNum, calcStartTime);
            CdrTaskResult conferenceNumConcurrencyTaskResult = buildTaskResult(ReportType.CONFERENCE_NUM_CONCURRENCY, deptId, conferenceNumConcurrency, calcStartTime);
            CdrTaskResult conferenceDurationTaskResult = buildTaskResult(ReportType.CONFERENCE_DURATION, deptId, conferenceDuration, calcStartTime);
            cdrTaskResultMapper.insertCdrTaskResult(conferenceNumTaskResult);
            cdrTaskResultMapper.insertCdrTaskResult(conferenceNumConcurrencyTaskResult);
            cdrTaskResultMapper.insertCdrTaskResult(conferenceDurationTaskResult);
        }
        else
        {
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
        return reportTypes.length;
    }

    /**
     * 更新每天参会的会议数量和会议总时长
     *
     * @param deptId
     * @param historyConferenceNotEndList 未结束的会议列表
     * @param calcStartTime
     * @param calcEndTime
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int updateMeetingNumAndDuration(Long deptId, List<BusiHistoryConference> historyConferenceNotEndList, Date calcStartTime, Date calcEndTime)
    {
        List<BusiHistoryConference> historyConferenceEndedList = busiHistoryConferenceMapper.selectEndedCalcDayHistoryConferenceList(deptId, calcStartTime, calcEndTime);
        if (historyConferenceEndedList.size() + historyConferenceNotEndList.size() == 0) {
            return 0;
        }
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
            if (busiHistoryConference.getConferenceStartTime().before(calcStartTime)) {
                long durationSeconds = 60 * 60 * 24;
                conferenceDuration += durationSeconds;
            } else {
                long durationSeconds = (calcEndTime.getTime() - busiHistoryConference.getConferenceStartTime().getTime()) / 1000;
                conferenceDuration += durationSeconds;
                conferenceNum += 1;
            }
        }
        Integer[] reportTypes = {ReportType.CONFERENCE_NUM, ReportType.CONFERENCE_NUM_CONCURRENCY, ReportType.CONFERENCE_DURATION};
        List<CdrTaskResult> cdrTaskResults = getCdrTaskResults(deptId, reportTypes, calcStartTime, calcEndTime);
        if (CollectionUtils.isEmpty(cdrTaskResults))
        {
            // 添加新的记录
            CdrTaskResult conferenceNumTaskResult = buildTaskResult(ReportType.CONFERENCE_NUM, deptId, conferenceNum, calcStartTime);
            CdrTaskResult conferenceNumConcurrencyTaskResult = buildTaskResult(ReportType.CONFERENCE_NUM_CONCURRENCY, deptId, conferenceNumConcurrency, calcStartTime);
            CdrTaskResult conferenceDurationTaskResult = buildTaskResult(ReportType.CONFERENCE_DURATION, deptId, conferenceDuration, calcStartTime);
            cdrTaskResultMapper.insertCdrTaskResult(conferenceNumTaskResult);
            cdrTaskResultMapper.insertCdrTaskResult(conferenceNumConcurrencyTaskResult);
            cdrTaskResultMapper.insertCdrTaskResult(conferenceDurationTaskResult);
        }
        else
        {
            // 更新记录
            for (CdrTaskResult cdrTaskResult : cdrTaskResults) {
                if (cdrTaskResult.getReportType().equals(ReportType.CONFERENCE_NUM)) {
                    cdrTaskResult.setDurationOrNum(conferenceNum);
                    cdrTaskResult.setUpdateTime(new Date());
                    cdrTaskResultMapper.updateCdrTaskResult(cdrTaskResult);
                } else if (cdrTaskResult.getReportType().equals(ReportType.CONFERENCE_NUM_CONCURRENCY)) {
                    cdrTaskResult.setDurationOrNum(conferenceNum);
                    cdrTaskResult.setUpdateTime(new Date());
                    cdrTaskResultMapper.updateCdrTaskResult(cdrTaskResult);
                } else if (cdrTaskResult.getReportType().equals(ReportType.CONFERENCE_DURATION)) {
                    cdrTaskResult.setDurationOrNum(conferenceNum);
                    cdrTaskResult.setUpdateTime(new Date());
                    cdrTaskResultMapper.updateCdrTaskResult(cdrTaskResult);
                }
            }
        }
        return reportTypes.length;
    }
    
    /**
     * 更新每天会议的会议质量数量统计和参会者告警类型统计
     * 优：无告警信息且 丢包率<0.1%,延迟<100ms
     * 良: 无告警信息,且 0.1%< 丢包率< 1%,100ms<延迟<400ms
     * 差: 有告警信息或 丢包率>1%,延迟>400ms
     *
     * @param deptId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int updateCallQuality(Long deptId)
    {
        Integer[] reportTypes = {ReportType.CALL_QUALITY, ReportType.PARTICIPANTS_ALARM_TYPE};
        List<CdrTaskResult> cdrTaskResults = buildInitTaskResultList(reportTypes, deptId);
        
        String currentDate = DateUtil.convertDateToString(LocalDate.now(), null);
        String startTime = DateUtil.fillDateString(currentDate, false);
        String endTime = DateUtil.fillDateString(currentDate, true);
        List<BusiHistoryParticipant> participantList = busiHistoryParticipantMapper.selectParticipantDetailListByDeptAndTime(deptId, startTime, endTime);
        
        Map<Integer, CdrTaskResult> callQualityMap = cdrTaskResults.stream()
                .filter(r -> !r.getReportType().equals(ReportType.PARTICIPANTS_ALARM_TYPE))
                .collect(Collectors.toMap(CdrTaskResult::getGroupType, Function.identity(), (v1, v2) -> v2));
        
        Map<Integer, CdrTaskResult> alarmTaskMap = cdrTaskResults.stream()
                .filter(r -> r.getReportType().equals(ReportType.PARTICIPANTS_ALARM_TYPE))
                .collect(Collectors.toMap(CdrTaskResult::getGroupType, Function.identity(), (v1, v2) -> v2));
        
        for (BusiHistoryParticipant busiHistoryParticipant : participantList)
        {
            // 告警信息查询
            CdrCallLegEndAlarm cdrCallLegEndAlarm = new CdrCallLegEndAlarm();
            cdrCallLegEndAlarm.setCdrLegEndId(busiHistoryParticipant.getCallLegId());
            List<CdrCallLegEndAlarm> legEndAlarms = cdrCallLegEndAlarmMapper.selectCdrCallLegEndAlarmList(cdrCallLegEndAlarm);
            // 重新计算优、良、差参会者的数量
            generationCallQualityResult(deptId, busiHistoryParticipant.getCallLegId(), callQualityMap, legEndAlarms);
            generationAlarmTypeResult(alarmTaskMap, legEndAlarms);
        }
        
        List<CdrTaskResult> cdrTaskResultDB = getCdrTaskResults(deptId, reportTypes);
        if (CollectionUtils.isEmpty(cdrTaskResultDB))
        {
            for (CdrTaskResult value : callQualityMap.values())
            {
                value.setUpdateTime(new Date());
                cdrTaskResultMapper.insertCdrTaskResult(value);
            }
            for (CdrTaskResult taskResult : alarmTaskMap.values())
            {
                taskResult.setUpdateTime(new Date());
                cdrTaskResultMapper.insertCdrTaskResult(taskResult);
            }
        }
        else
        {
            for (CdrTaskResult value : callQualityMap.values())
            {
                value.setUpdateTime(new Date());
                cdrTaskResultMapper.updateCurrentDateTaskResult(value);
            }
            for (CdrTaskResult taskResult : alarmTaskMap.values())
            {
                taskResult.setUpdateTime(new Date());
                cdrTaskResultMapper.updateCurrentDateTaskResult(taskResult);
            }
        }
        
        return 0;
    }
    
    /**
     * 参会者告警统计
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<Map<String, Object>> reportAlarmTypeRate(ReportSearchVo searchVo)
    {
        Long deptId = (searchVo.getDeptId() == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : searchVo.getDeptId();
        searchVo.setDeptId(deptId);
        List<Map<String, Object>> mapList = new ArrayList<>();
        
        Integer[] reportTypes = {ReportType.PARTICIPANTS_ALARM_TYPE};
        List<CdrTaskResult> cdrTaskResults = cdrTaskResultMapper.selectByDateAndDeptAndReportType(deptId, searchVo.getStartTime(), searchVo.getEndTime(), reportTypes);
        cdrTaskResults.stream().collect(Collectors.groupingBy(CdrTaskResult::getDate, LinkedHashMap::new, Collectors.toList())).forEach((day, groupList) -> {
            Map<String, Object> dayMap = new HashMap<>(6);
            dayMap.put("day", DateUtil.convertDateToString(day, "yyyy-MM-dd"));
            groupList.stream().collect(Collectors.groupingBy(CdrTaskResult::getGroupType)).forEach((groupType, typeList) -> {
                long sum = typeList.stream().mapToLong(CdrTaskResult::getDurationOrNum).sum();
                if (groupType.intValue() == CallLegEndAlarmTypeEnum.PACKET_LOSS.getCode())
                {
                    dayMap.put("packetLoss", sum);
                }
                if (groupType.intValue() == CallLegEndAlarmTypeEnum.EXCESSIVE_JITTER.getCode())
                {
                    dayMap.put("excessiveJitter", sum);
                }
                if (groupType.intValue() == CallLegEndAlarmTypeEnum.HIGH_ROUND_TRIP_TIME.getCode())
                {
                    dayMap.put("highRoundTripTime", sum);
                }
            });
            mapList.add(dayMap);
        });
        return mapList;
    }

    private List<CdrTaskResult> getCdrTaskResults(Long deptId, Integer[] reportTypes)
    {
        String date = DateUtil.convertDateToString(LocalDate.now(), null);
        Date startDate = DateUtil.convertDateByString(DateUtil.fillDateString(date, false), null);
        Date endDate = DateUtil.convertDateByString(DateUtil.fillDateString(date, true), null);
        return cdrTaskResultMapper.selectByDateAndDeptAndReportType(deptId, startDate, endDate, reportTypes);
    }
    
    private List<CdrTaskResult> getCdrTaskResults(Long deptId, Integer[] reportTypes, Date startDate, Date endDate)
    {
        return cdrTaskResultMapper.selectByDateAndDeptAndReportType(deptId, startDate, endDate, reportTypes);
    }
    
    /**
     * 生成初始化记录
     *
     * @param reportTypes
     * @param deptId
     * @return
     */
    private List<CdrTaskResult> buildInitTaskResultList(Integer[] reportTypes, Long deptId)
    {
        List<CdrTaskResult> list = new ArrayList<>();
        // 添加新的记录
        CdrTaskResult veryGoodTaskResult = buildTaskResult(ReportType.CALL_QUALITY, deptId, 0, GroupType.VERY_GOOD);
        CdrTaskResult goodTaskResult = buildTaskResult(ReportType.CALL_QUALITY, deptId, 0, GroupType.GOOD);
        CdrTaskResult poorTaskResult = buildTaskResult(ReportType.CALL_QUALITY, deptId, 0, GroupType.POOR);
        CdrTaskResult alarmTypePacketLoss = buildTaskResult(ReportType.PARTICIPANTS_ALARM_TYPE, deptId, 0, CallLegEndAlarmTypeEnum.PACKET_LOSS.getCode());
        CdrTaskResult alarmTypeExcessiveJitter = buildTaskResult(ReportType.PARTICIPANTS_ALARM_TYPE, deptId, 0, CallLegEndAlarmTypeEnum.EXCESSIVE_JITTER.getCode());
        CdrTaskResult alarmTypeHighRoundTripTime = buildTaskResult(ReportType.PARTICIPANTS_ALARM_TYPE, deptId, 0, CallLegEndAlarmTypeEnum.HIGH_ROUND_TRIP_TIME.getCode());
        
        list.add(veryGoodTaskResult);
        list.add(goodTaskResult);
        list.add(poorTaskResult);
        list.add(alarmTypePacketLoss);
        list.add(alarmTypeExcessiveJitter);
        list.add(alarmTypeHighRoundTripTime);
        return list;
    }
    
    /**
     * 更新alarmTask对象属性值
     *
     * @param cdrTaskResultMap
     * @param legEndAlarms     该参会者的所有告警信息
     */
    private void generationAlarmTypeResult(Map<Integer, CdrTaskResult> cdrTaskResultMap, List<CdrCallLegEndAlarm> legEndAlarms)
    {
        legEndAlarms.stream().collect(Collectors.groupingBy(CdrCallLegEndAlarm::getType)).forEach((type, groupList) -> {
            CdrTaskResult taskResult = cdrTaskResultMap.get(type.getCode());
            taskResult.setDurationOrNum(taskResult.getDurationOrNum() + Long.valueOf(groupList.size()));
            taskResult.setUpdateTime(new Date());
        });
    }
    
    /**
     * 优：无告警信息且 丢包率<0.1%,延迟<100ms
     * 良: 无告警信息,且 0.1%< 丢包率< 1%,100ms<延迟<400ms
     * 差: 有告警信息或 丢包率>1%,延迟>400ms
     *
     * @param cdrTaskResultMap
     * @param legEndAlarms
     * @return
     */
    private void generationCallQualityResult(Long deptId, String callLegId, Map<Integer, CdrTaskResult> cdrTaskResultMap, List<CdrCallLegEndAlarm> legEndAlarms)
    {
        CdrTaskResult veryGoodTaskResult = cdrTaskResultMap.get(Integer.valueOf(GroupType.VERY_GOOD));
        CdrTaskResult goodTaskResult = cdrTaskResultMap.get(Integer.valueOf(GroupType.GOOD));
        CdrTaskResult poorTaskResult = cdrTaskResultMap.get(Integer.valueOf(GroupType.POOR));
        
        CdrCallLegEndMediaInfo mediaInfo = new CdrCallLegEndMediaInfo();
        mediaInfo.setCdrId(callLegId);
        List<CdrCallLegEndMediaInfo> mediaInfoList = cdrCallLegEndMediaInfoMapper.selectCdrCallLegEndMediaInfoList(mediaInfo);
        if (CollectionUtils.isEmpty(mediaInfoList))
        {
            veryGoodTaskResult.setDurationOrNum(veryGoodTaskResult.getDurationOrNum() + 1);
            return;
        }
        // 最大丢包率
        BigDecimal maxPacketLossBurstsDurationDb = mediaInfoList.stream().max(Comparator.comparing(CdrCallLegEndMediaInfo::getPacketLossBurstsDensity)).get().getPacketLossBurstsDensity();
        // 最大延迟
        BigDecimal maxPacketGapDurationDb = mediaInfoList.stream().max(Comparator.comparing(CdrCallLegEndMediaInfo::getPacketGapDuration)).get().getPacketGapDuration();
        
        BigDecimal minPacketLossBurstsDuration = new BigDecimal("0.1");
        BigDecimal maxPacketLossBurstsDuration = new BigDecimal("1");
        BigDecimal minNetworkDelay = new BigDecimal("100");
        BigDecimal maxNetworkDelay = new BigDecimal("400");
        
        int compareMinNetworkDelayResult = maxPacketGapDurationDb.compareTo(minNetworkDelay);
        int compareMaxNetworkDelayResult = maxPacketGapDurationDb.compareTo(maxNetworkDelay);
        int compareToMinPacketLossBurstsDuration = maxPacketLossBurstsDurationDb.compareTo(minPacketLossBurstsDuration);
        int compareToMaxPacketLossBurstsDuration = maxPacketLossBurstsDurationDb.compareTo(maxPacketLossBurstsDuration);
        
        if (compareMinNetworkDelayResult < 0 && compareToMinPacketLossBurstsDuration < 0)
        {
            // 丢包率<0.1%,并且 延迟<100ms :优
            veryGoodTaskResult.setDurationOrNum(veryGoodTaskResult.getDurationOrNum() + 1);
        }
        else if (compareMaxNetworkDelayResult > 0 || compareToMaxPacketLossBurstsDuration > 0)
        {
            // 丢包率>1% 或 延迟>400ms :差
            poorTaskResult.setDurationOrNum(poorTaskResult.getDurationOrNum() + 1);
        }
        else
        {
            // 良：0.1%< 丢包率< 1%,100ms<延迟<400ms
            goodTaskResult.setDurationOrNum(goodTaskResult.getDurationOrNum() + 1);
        }
    }

    /**
     * 构造CdrTaskResult对象
     *
     * @param reportType
     * @param deptId
     * @param durationOrNum
     * @return
     */
    private CdrTaskResult buildTaskResult(int reportType, Long deptId, long durationOrNum)
    {
        return buildTaskResult(reportType, deptId, durationOrNum, new Date());
    }
    
    /**
     * 构造CdrTaskResult对象
     *
     * @param reportType
     * @param deptId
     * @param durationOrNum
     * @return
     */
    private CdrTaskResult buildTaskResult(int reportType, Long deptId, long durationOrNum, Date date)
    {
        CdrTaskResult taskResult = new CdrTaskResult();
        taskResult.setDate(date);
        taskResult.setDeptId(deptId);
        taskResult.setReportType(Integer.valueOf(reportType));
        taskResult.setDurationOrNum(Long.valueOf(durationOrNum));
        taskResult.setCreateTime(new Date());
        return taskResult;
    }
    
    /**
     * 构造CdrTaskResult对象
     *
     * @param reportType
     * @param deptId
     * @param durationOrNum
     * @param groupType
     * @return
     */
    private CdrTaskResult buildTaskResult(int reportType, Long deptId, int durationOrNum, int groupType)
    {
        CdrTaskResult taskResult = new CdrTaskResult();
        taskResult.setDate(new Date());
        taskResult.setDeptId(deptId);
        taskResult.setReportType(Integer.valueOf(reportType));
        taskResult.setDurationOrNum(Long.valueOf(durationOrNum));
        taskResult.setGroupType(Integer.valueOf(groupType));
        taskResult.setCreateTime(new Date());
        return taskResult;
    }
}
