package com.paradisecloud.fcm.cdr.service.impls;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrAllTaskResultService;
import com.paradisecloud.fcm.common.constant.GroupType;
import com.paradisecloud.fcm.common.constant.ReportType;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.enums.CallLegEndAlarmTypeEnum;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryAllParticipantMapper;
import com.paradisecloud.fcm.dao.mapper.CdrAllTaskResultMapper;
import com.paradisecloud.fcm.dao.mapper.CdrCallLegEndAlarmMapper;
import com.paradisecloud.fcm.dao.mapper.CdrCallLegEndMediaInfoMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import com.paradisecloud.system.utils.SecurityUtils;
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
public class CdrAllTaskResultServiceImpl implements ICdrAllTaskResultService
{
    
    @Resource
    private CdrAllTaskResultMapper cdrAllTaskResultMapper;
    
    @Resource
    private BusiHistoryAllParticipantMapper busiHistoryAllParticipantMapper;
    
    @Resource
    private CdrCallLegEndAlarmMapper cdrCallLegEndAlarmMapper;
    
    @Resource
    private CdrCallLegEndMediaInfoMapper cdrCallLegEndMediaInfoMapper;
    
    /**
     * 更新每天参会的会议数量和会议总时长
     *
     * @param groupList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int updateMeetingNumAndDuration(List<BusiHistoryAllConference> groupList)
    {
        Integer[] reportTypes = {ReportType.CONFERENCE_NUM, ReportType.CONFERENCE_DURATION};
        List<CdrAllTaskResult> cdrTaskResults = getCdrAllTaskResults(reportTypes);
        if (CollectionUtils.isEmpty(cdrTaskResults))
        {
            // 添加新的记录
            CdrAllTaskResult conferenceNumTaskResult = buildTaskResult(ReportType.CONFERENCE_NUM, 0);
            CdrAllTaskResult conferenceDurationTaskResult = buildTaskResult(ReportType.CONFERENCE_DURATION, 0);
            cdrAllTaskResultMapper.insertCdrAllTaskResult(conferenceNumTaskResult);
            cdrAllTaskResultMapper.insertCdrAllTaskResult(conferenceDurationTaskResult);
        }
        else
        {
            // 更新记录
            for (CdrAllTaskResult cdrAllTaskResult : cdrTaskResults)
            {
                if (CollectionUtils.isEmpty(groupList))
                {
                    break;
                }
                int durationOrNum = cdrAllTaskResult.getReportType().equals(ReportType.CONFERENCE_NUM) ? groupList.size() : groupList.stream().mapToInt(m -> m.getDuration()).sum();
                cdrAllTaskResult.setDurationOrNum(Long.valueOf(durationOrNum));
                cdrAllTaskResult.setUpdateTime(new Date());
                cdrAllTaskResultMapper.updateCdrAllTaskResult(cdrAllTaskResult);
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
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int updateCallQuality()
    {
        Integer[] reportTypes = {ReportType.CALL_QUALITY, ReportType.PARTICIPANTS_ALARM_TYPE};
        List<CdrAllTaskResult> cdrTaskResults = buildInitTaskResultList(reportTypes);
        
        String currentDate = DateUtil.convertDateToString(LocalDate.now(), null);
        String startTime = DateUtil.fillDateString(currentDate, false);
        String endTime = DateUtil.fillDateString(currentDate, true);
        List<BusiHistoryAllParticipant> participantList = busiHistoryAllParticipantMapper.selectParticipantDetailListByTime(startTime, endTime);
        
        Map<Integer, CdrAllTaskResult> callQualityMap = cdrTaskResults.stream()
                .filter(r -> !r.getReportType().equals(ReportType.PARTICIPANTS_ALARM_TYPE))
                .collect(Collectors.toMap(CdrAllTaskResult::getGroupType, Function.identity(), (v1, v2) -> v2));
        
        Map<Integer, CdrAllTaskResult> alarmTaskMap = cdrTaskResults.stream()
                .filter(r -> r.getReportType().equals(ReportType.PARTICIPANTS_ALARM_TYPE))
                .collect(Collectors.toMap(CdrAllTaskResult::getGroupType, Function.identity(), (v1, v2) -> v2));
        
        for (BusiHistoryAllParticipant busiHistoryAllParticipant : participantList)
        {
            // 告警信息查询
            CdrCallLegEndAlarm cdrCallLegEndAlarm = new CdrCallLegEndAlarm();
            cdrCallLegEndAlarm.setCdrLegEndId(busiHistoryAllParticipant.getCallLegId());
            List<CdrCallLegEndAlarm> legEndAlarms = cdrCallLegEndAlarmMapper.selectCdrCallLegEndAlarmList(cdrCallLegEndAlarm);
            // 重新计算优、良、差参会者的数量
            generationCallQualityResult(busiHistoryAllParticipant.getCallLegId(), callQualityMap, legEndAlarms);
            generationAlarmTypeResult(alarmTaskMap, legEndAlarms);
        }
        
        List<CdrAllTaskResult> cdrTaskResultDB = getCdrAllTaskResults(reportTypes);
        if (CollectionUtils.isEmpty(cdrTaskResultDB))
        {
            for (CdrAllTaskResult value : callQualityMap.values())
            {
                value.setUpdateTime(new Date());
                cdrAllTaskResultMapper.insertCdrAllTaskResult(value);
            }
            for (CdrAllTaskResult taskResult : alarmTaskMap.values())
            {
                taskResult.setUpdateTime(new Date());
                cdrAllTaskResultMapper.insertCdrAllTaskResult(taskResult);
            }
        }
        else
        {
            for (CdrAllTaskResult value : callQualityMap.values())
            {
                value.setUpdateTime(new Date());
                cdrAllTaskResultMapper.updateCurrentDateTaskResult(value);
            }
            for (CdrAllTaskResult taskResult : alarmTaskMap.values())
            {
                taskResult.setUpdateTime(new Date());
                cdrAllTaskResultMapper.updateCurrentDateTaskResult(taskResult);
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
        List<CdrAllTaskResult> cdrTaskResults = cdrAllTaskResultMapper.selectByDateAndReportType(searchVo.getStartTime(), searchVo.getEndTime(), reportTypes);
        cdrTaskResults.stream().collect(Collectors.groupingBy(CdrAllTaskResult::getDate, LinkedHashMap::new, Collectors.toList())).forEach((day, groupList) -> {
            Map<String, Object> dayMap = new HashMap<>(6);
            dayMap.put("day", DateUtil.convertDateToString(day, "yyyy-MM-dd"));
            groupList.stream().collect(Collectors.groupingBy(CdrAllTaskResult::getGroupType)).forEach((groupType, typeList) -> {
                long sum = typeList.stream().mapToLong(CdrAllTaskResult::getDurationOrNum).sum();
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
    
    private List<CdrAllTaskResult> getCdrAllTaskResults(Integer[] reportTypes)
    {
        String date = DateUtil.convertDateToString(LocalDate.now(), null);
        Date startDate = DateUtil.convertDateByString(DateUtil.fillDateString(date, false), null);
        Date endDate = DateUtil.convertDateByString(DateUtil.fillDateString(date, true), null);
        return cdrAllTaskResultMapper.selectByDateAndReportType(startDate, endDate, reportTypes);
    }
    
    /**
     * 生成初始化记录
     *
     * @param reportTypes
     * @return
     */
    private List<CdrAllTaskResult> buildInitTaskResultList(Integer[] reportTypes)
    {
        List<CdrAllTaskResult> list = new ArrayList<>();
        // 添加新的记录
        CdrAllTaskResult veryGoodTaskResult = buildTaskResult(ReportType.CALL_QUALITY, 0, GroupType.VERY_GOOD);
        CdrAllTaskResult goodTaskResult = buildTaskResult(ReportType.CALL_QUALITY, 0, GroupType.GOOD);
        CdrAllTaskResult poorTaskResult = buildTaskResult(ReportType.CALL_QUALITY, 0, GroupType.POOR);
        CdrAllTaskResult alarmTypePacketLoss = buildTaskResult(ReportType.PARTICIPANTS_ALARM_TYPE, 0, CallLegEndAlarmTypeEnum.PACKET_LOSS.getCode());
        CdrAllTaskResult alarmTypeExcessiveJitter = buildTaskResult(ReportType.PARTICIPANTS_ALARM_TYPE, 0, CallLegEndAlarmTypeEnum.EXCESSIVE_JITTER.getCode());
        CdrAllTaskResult alarmTypeHighRoundTripTime = buildTaskResult(ReportType.PARTICIPANTS_ALARM_TYPE,  0, CallLegEndAlarmTypeEnum.HIGH_ROUND_TRIP_TIME.getCode());
        
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
    private void generationAlarmTypeResult(Map<Integer, CdrAllTaskResult> cdrTaskResultMap, List<CdrCallLegEndAlarm> legEndAlarms)
    {
        legEndAlarms.stream().collect(Collectors.groupingBy(CdrCallLegEndAlarm::getType)).forEach((type, groupList) -> {
            CdrAllTaskResult taskResult = cdrTaskResultMap.get(type.getCode());
            taskResult.setDurationOrNum(Long.valueOf(groupList.size()));
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
    private void generationCallQualityResult(String callLegId, Map<Integer, CdrAllTaskResult> cdrTaskResultMap, List<CdrCallLegEndAlarm> legEndAlarms)
    {
        CdrAllTaskResult veryGoodTaskResult = cdrTaskResultMap.get(Integer.valueOf(GroupType.VERY_GOOD));
        CdrAllTaskResult goodTaskResult = cdrTaskResultMap.get(Integer.valueOf(GroupType.GOOD));
        CdrAllTaskResult poorTaskResult = cdrTaskResultMap.get(Integer.valueOf(GroupType.POOR));
        
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
     * 构造CdrAllTaskResult对象
     *
     * @param reportType
     * @param durationOrNum
     * @return
     */
    private CdrAllTaskResult buildTaskResult(int reportType, int durationOrNum)
    {
        CdrAllTaskResult taskResult = new CdrAllTaskResult();
        taskResult.setDate(new Date());
        taskResult.setReportType(Integer.valueOf(reportType));
        taskResult.setDurationOrNum(Long.valueOf(durationOrNum));
        taskResult.setCreateTime(new Date());
        return taskResult;
    }
    
    /**
     * 构造CdrAllTaskResult对象
     *
     * @param reportType
     * @param durationOrNum
     * @param groupType
     * @return
     */
    private CdrAllTaskResult buildTaskResult(int reportType, int durationOrNum, int groupType)
    {
        CdrAllTaskResult taskResult = new CdrAllTaskResult();
        taskResult.setDate(new Date());
        taskResult.setReportType(Integer.valueOf(reportType));
        taskResult.setDurationOrNum(Long.valueOf(durationOrNum));
        taskResult.setGroupType(Integer.valueOf(groupType));
        taskResult.setCreateTime(new Date());
        return taskResult;
    }
}
