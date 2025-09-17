package com.paradisecloud.fcm.cdr.service.impls.report;

import com.paradisecloud.fcm.cdr.service.interfaces.report.IAllConferenceReportService;
import com.paradisecloud.fcm.common.constant.GroupType;
import com.paradisecloud.fcm.common.constant.ReportType;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryAllConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.CdrAllTaskResultMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryAllConference;
import com.paradisecloud.fcm.dao.model.CdrAllTaskResult;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author johnson liu
 * @date 2021/6/18 14:09
 */
@Service
public class AllConferenceReportServiceImpl implements IAllConferenceReportService
{
    
    @Resource
    private CdrAllTaskResultMapper cdrAllTaskResultMapper;
    
    @Resource
    private BusiHistoryAllConferenceMapper busiHistoryAllConferenceMapper;
    
    /**
     * 每天/周/月 发起会议的数量 统计
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<Map<String, Object>> selectConferenceNumOfDay(ReportSearchVo searchVo)
    {
        List<Map<String, Object>> mapList = new ArrayList<>();
        
        if (StringUtils.hasText(searchVo.getNumber()))
        {
            List<BusiHistoryAllConference> list = busiHistoryAllConferenceMapper.selectBySearchVo(searchVo);
            
            Map<String, List<BusiHistoryAllConference>> allList = list.stream().collect(Collectors.groupingBy(item -> new SimpleDateFormat("yyyy-MM-dd").format(item.getConferenceStartTime())));
            
            allList.forEach((day, dayList) -> {
                Map<String, Object> dayMap = new HashMap<>(3);
                dayMap.put("num", dayList.size());
                dayMap.put("day", day);
                mapList.add(dayMap);
            });
        }
        else
        {
            Integer[] reportTypes = {ReportType.CONFERENCE_NUM};
            List<CdrAllTaskResult> cdrTaskResults = cdrAllTaskResultMapper.selectByDateAndReportType(searchVo.getStartTime(), searchVo.getEndTime(), reportTypes);
            
            cdrTaskResults.stream().collect(Collectors.groupingBy(CdrAllTaskResult::getDate, LinkedHashMap::new, Collectors.toList())).forEach((day, groupList) -> {
                Map<String, Object> dayMap = new HashMap<>(3);
                dayMap.put("num", groupList.stream().mapToLong(CdrAllTaskResult::getDurationOrNum).sum());
                dayMap.put("day", DateUtil.convertDateToString(day, "yyyy-MM-dd"));
                mapList.add(dayMap);
            });
        }
        return mapList;
    }
    
    /**
     * 每天/周/月 发起会议的总时长 统计
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<Map<String, Object>> selectConferenceDurationOfDay(ReportSearchVo searchVo)
    {
        List<Map<String, Object>> mapList = new ArrayList<>();
        
        if (StringUtils.hasText(searchVo.getNumber()))
        {
            List<BusiHistoryAllConference> list = busiHistoryAllConferenceMapper.selectBySearchVo(searchVo);
            
            Map<String, List<BusiHistoryAllConference>> allList = list.stream().collect(Collectors.groupingBy(item -> new SimpleDateFormat("yyyy-MM-dd").format(item.getConferenceStartTime())));
            
            allList.forEach((day, dayList) -> {
                Map<String, Object> dayMap = new HashMap<>(4);
                dayMap.put("duration", dayList.stream().mapToInt(BusiHistoryAllConference::getDuration).sum());
                dayMap.put("day", day);
                mapList.add(dayMap);
            });
        }
        else
        {
            Integer[] reportTypes = {ReportType.CONFERENCE_DURATION};
            List<CdrAllTaskResult> cdrTaskResults = cdrAllTaskResultMapper.selectByDateAndReportType(searchVo.getStartTime(), searchVo.getEndTime(), reportTypes);
            
            cdrTaskResults.stream().collect(Collectors.groupingBy(CdrAllTaskResult::getDate, LinkedHashMap::new, Collectors.toList())).forEach((day, groupList) -> {
                Map<String, Object> dayMap = new HashMap<>(3);
                dayMap.put("duration", groupList.stream().mapToLong(CdrAllTaskResult::getDurationOrNum).sum());
                dayMap.put("day", DateUtil.convertDateToString(day, "yyyy-MM-dd"));
                mapList.add(dayMap);
            });
        }
        return mapList;
    }
    
    /**
     * 每天/周/月 参会者的质量分析统计
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<Map<String, Object>> reportCallQualityOfDay(ReportSearchVo searchVo)
    {
        List<Map<String, Object>> mapList = new ArrayList<>();
        
        Integer[] reportTypes = {ReportType.CALL_QUALITY};
        List<CdrAllTaskResult> cdrTaskResults = cdrAllTaskResultMapper.selectByDateAndReportType(searchVo.getStartTime(), searchVo.getEndTime(), reportTypes);
        
        Optional.ofNullable(cdrTaskResults).ifPresent(resultList -> {
            resultList.stream().collect(Collectors.groupingBy(CdrAllTaskResult::getDate, LinkedHashMap::new, Collectors.toList())).forEach((date, groupList) -> {
                Map<String, Object> map = new HashMap<>(6);
                map.put("day", DateUtil.convertDateToString(date, "yyyy-MM-dd"));
                groupList.stream().collect(Collectors.groupingBy(CdrAllTaskResult::getGroupType)).forEach((groupType, typeList) -> {
                    if (groupType.intValue() == GroupType.VERY_GOOD)
                    {
                        map.put("veryGood", typeList.stream().mapToLong(CdrAllTaskResult::getDurationOrNum).sum());
                    }
                    if (groupType.intValue() == GroupType.GOOD)
                    {
                        map.put("good", typeList.stream().mapToLong(CdrAllTaskResult::getDurationOrNum).sum());
                    }
                    if (groupType.intValue() == GroupType.POOR)
                    {
                        map.put("poor", typeList.stream().mapToLong(CdrAllTaskResult::getDurationOrNum).sum());
                    }
                });
                mapList.add(map);
            });
        });
        return mapList;
    }
}
