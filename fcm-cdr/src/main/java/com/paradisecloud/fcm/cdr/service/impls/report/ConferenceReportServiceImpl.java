package com.paradisecloud.fcm.cdr.service.impls.report;

import com.paradisecloud.fcm.cdr.service.interfaces.report.IConferenceReportService;
import com.paradisecloud.fcm.common.constant.GroupType;
import com.paradisecloud.fcm.common.constant.ReportType;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.CdrTaskResultMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.CdrTaskResult;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import com.paradisecloud.system.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author johnson liu
 * @date 2021/6/18 14:09
 */
@Service
public class ConferenceReportServiceImpl implements IConferenceReportService
{
    
    @Autowired
    private CdrTaskResultMapper cdrTaskResultMapper;
    
    @Autowired
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;

    /**
     * 每天/周/月 发起会议的数量 统计
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<Map<String, Object>> selectConferenceNumOfDay(ReportSearchVo searchVo) {
        return selectConferenceNumOfDay(searchVo, false);
    }

    /**
     * 每天/周/月 发起会议的数量 统计
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<Map<String, Object>> selectConferenceNumOfDay(ReportSearchVo searchVo, boolean isConcurrency) {
        return selectConferenceNumOfDay(searchVo, 0, isConcurrency);
    }

    /**
     * 每天/周/月 发起会议的数量 统计
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<Map<String, Object>> selectConferenceNumOfDay(ReportSearchVo searchVo, int dateType)
    {
        return selectConferenceNumOfDay(searchVo, dateType, false);
    }
    
    /**
     * 每天/周/月 发起会议的数量 统计
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<Map<String, Object>> selectConferenceNumOfDay(ReportSearchVo searchVo, int dateType, boolean isConcurrency)
    {
        List<Map<String, Object>> mapList = new ArrayList<>();

        TreeMap<String, Long> dayNumMap = new TreeMap<>();
        if (StringUtils.hasText(searchVo.getNumber()))
        {
            List<BusiHistoryConference> list = busiHistoryConferenceMapper.selectBySearchVo(searchVo);
            
            Map<String, List<BusiHistoryConference>> allList = list.stream().collect(Collectors.groupingBy(item -> new SimpleDateFormat("yyyy-MM-dd").format(item.getConferenceStartTime())));
            
            allList.forEach((day, dayList) -> {
                dayNumMap.put(day, (long) dayList.size());
            });
        }
        else
        {
            Integer[] reportTypes = {ReportType.CONFERENCE_NUM};
            if (isConcurrency) {
                reportTypes = new Integer[]{ReportType.CONFERENCE_NUM_CONCURRENCY};
            }
            List<CdrTaskResult> cdrTaskResults = cdrTaskResultMapper.selectByDateAndDeptAndReportType(searchVo.getDeptId(), searchVo.getStartTime(), searchVo.getEndTime(), reportTypes);

            cdrTaskResults.stream().collect(Collectors.groupingBy(CdrTaskResult::getDate)).forEach((day, groupList) -> {
                String dayStr = DateUtil.convertDateToString(day, "yyyy-MM-dd");
                dayNumMap.put(dayStr, groupList.stream().mapToLong(CdrTaskResult::getDurationOrNum).sum());
            });
        }

        Date startTime = searchVo.getStartTime();
        Date endTime = searchVo.getEndTime();
        if (startTime != null && endTime != null) {
            if (startTime.after(endTime)) {
                startTime = searchVo.getEndTime();
                endTime = searchVo.getStartTime();
            }
        }
        LocalDate beginDate = null;
        LocalDate endDate = null;
        if (!dayNumMap.isEmpty()) {
            String beginDayStr = dayNumMap.firstKey();
            String endDayStr = dayNumMap.lastKey();
            beginDate = LocalDate.parse(beginDayStr);
            endDate = LocalDate.parse(endDayStr);
        }
        if (startTime != null) {
            LocalDate startDateTime = LocalDate.parse(DateUtil.convertDateToString(startTime, "yyyy-MM-dd"));
            beginDate = startDateTime;
        }
        if (endTime != null) {
            LocalDate endDateTime = LocalDate.parse(DateUtil.convertDateToString(endTime, "yyyy-MM-dd"));
            endDate = endDateTime;
        }

        if (beginDate != null) {
            String previousDayStr = null;
            Long previousNum = null;

            //当开始时间不大于结束时间，循环执行
            while (!beginDate.isAfter(endDate)) {
                String dayStr = DateUtil.convertDateToString(beginDate, "yyyy-MM-dd");
                Long num = dayNumMap.get(dayStr);
                if (num == null) {
                    num = 0L;
                }
                Map<String, Object> dayMap = new HashMap<>(3);
                dayMap.put("day", dayStr);
                dayMap.put("num", num);
                beginDate = beginDate.plusDays(1);

                if (previousDayStr == null) {
                    mapList.add(dayMap);
                } else {
                    if (num >= 0) {
                        mapList.add(dayMap);
                    } else {
                        if (previousNum >= 0) {
                            mapList.add(dayMap);
                        } else {
                            String nextDayStr = DateUtil.convertDateToString(beginDate, "yyyy-MM-dd");
                            Long nextNum = dayNumMap.get(nextDayStr);
                            if (nextNum != null && nextNum > 0) {
                                mapList.add(dayMap);
                            }
                        }
                    }
                }

                previousDayStr = dayStr;
                previousNum = num;
            }
        }

        if (dateType == 1) {
            if (mapList.size() > 0) {
                List<Map<String, Object>> monthList = new ArrayList<>();
                TreeMap<String, Map<String, Object>> monthsMap = new TreeMap<>();
                for (Map<String, Object> dayMap : mapList) {
                    String dayStr = (String) dayMap.get("day");
                    Long num = (Long) dayMap.get("num");
                    String monthStr = dayStr.substring(0, 7);
                    Map<String, Object> monthMap = monthsMap.get(monthStr);
                    if (monthMap == null) {
                        monthMap = new HashMap<>();
                        monthMap.put("month", monthStr);
                        monthMap.put("num", num);
                        monthsMap.put(monthStr, monthMap);
                    } else {
                        Long numExist = (Long) monthMap.get("num");
                        monthMap.put("num", numExist + num);
                    }
                }
                SortedMap<String, Map<String, Object>> monthsSortedMap = monthsMap.tailMap("0000-00");
                monthList.addAll(monthsSortedMap.values());
                return monthList;
            }
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

        TreeMap<String, Long> dayDurationMap = new TreeMap<>();
        if (StringUtils.hasText(searchVo.getNumber()))
        {
            List<BusiHistoryConference> list = busiHistoryConferenceMapper.selectBySearchVo(searchVo);
            
            Map<String, List<BusiHistoryConference>> allList = list.stream().collect(Collectors.groupingBy(item -> new SimpleDateFormat("yyyy-MM-dd").format(item.getConferenceStartTime())));
            
            allList.forEach((day, dayList) -> {
                dayDurationMap.put(day, dayList.stream().mapToLong(BusiHistoryConference::getDuration).sum());
            });
        }
        else
        {
            Integer[] reportTypes = {ReportType.CONFERENCE_DURATION};
            List<CdrTaskResult> cdrTaskResults = cdrTaskResultMapper.selectByDateAndDeptAndReportType(searchVo.getDeptId(), searchVo.getStartTime(), searchVo.getEndTime(), reportTypes);
            
            cdrTaskResults.stream().collect(Collectors.groupingBy(CdrTaskResult::getDate)).forEach((day, groupList) -> {
                String dayStr = DateUtil.convertDateToString(day, "yyyy-MM-dd");
                dayDurationMap.put(dayStr, groupList.stream().mapToLong(CdrTaskResult::getDurationOrNum).sum());
            });
        }

        if (!dayDurationMap.isEmpty()) {
            String beginDayStr = dayDurationMap.firstKey();
            String endDayStr = dayDurationMap.lastKey();
            LocalDate beginDate = LocalDate.parse(beginDayStr);
            LocalDate endDate = LocalDate.parse(endDayStr);
            Date startTime = searchVo.getStartTime();
            Date endTime = searchVo.getEndTime();
            if (startTime != null) {
                LocalDate startDateTime = LocalDate.parse(DateUtil.convertDateToString(startTime, "yyyy-MM-dd"));
                if (startDateTime.isBefore(beginDate)) {
                    beginDate = startDateTime;
                }
            }
            if (endTime != null) {
                LocalDate endDateTime = LocalDate.parse(DateUtil.convertDateToString(endTime, "yyyy-MM-dd"));
                if (endDateTime.isAfter(endDate)) {
                    endDate = endDateTime;
                }
            }

            String previousDayStr = null;
            Long previousDuration = null;
            //当开始时间不大于结束时间，循环执行
            while (!beginDate.isAfter(endDate)) {
                String dayStr = DateUtil.convertDateToString(beginDate, "yyyy-MM-dd");
                Long duration = dayDurationMap.get(dayStr);
                if (duration == null) {
                    duration = 0L;
                }
                Map<String, Object> dayMap = new HashMap<>(3);
                dayMap.put("day", dayStr);
                dayMap.put("duration", duration);
                beginDate = beginDate.plusDays(1);

                if (previousDayStr == null) {
                    mapList.add(dayMap);
                } else {
                    if (duration > 0) {
                        mapList.add(dayMap);
                    } else {
                        if (previousDuration > 0) {
                            mapList.add(dayMap);
                        } else {
                            String nextDayStr = DateUtil.convertDateToString(beginDate, "yyyy-MM-dd");
                            Long nextDuration = dayDurationMap.get(nextDayStr);
                            if (nextDuration != null && nextDuration > 0) {
                                mapList.add(dayMap);
                            }
                        }
                    }
                }

                previousDayStr = dayStr;
                previousDuration = duration;
            }
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
        Long deptId = (searchVo.getDeptId() == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : searchVo.getDeptId();
        searchVo.setDeptId(deptId);
        List<Map<String, Object>> mapList = new ArrayList<>();
        
        Integer[] reportTypes = {ReportType.CALL_QUALITY};
        List<CdrTaskResult> cdrTaskResults = cdrTaskResultMapper.selectByDateAndDeptAndReportType(deptId, searchVo.getStartTime(), searchVo.getEndTime(), reportTypes);
        
        Optional.ofNullable(cdrTaskResults).ifPresent(resultList -> {
            resultList.stream().collect(Collectors.groupingBy(CdrTaskResult::getDate, LinkedHashMap::new, Collectors.toList())).forEach((date, groupList) -> {
                Map<String, Object> map = new HashMap<>(6);
                map.put("day", DateUtil.convertDateToString(date, "yyyy-MM-dd"));
                groupList.stream().collect(Collectors.groupingBy(CdrTaskResult::getGroupType)).forEach((groupType, typeList) -> {
                    if (groupType.intValue() == GroupType.VERY_GOOD)
                    {
                        map.put("veryGood", typeList.stream().mapToLong(CdrTaskResult::getDurationOrNum).sum());
                    }
                    if (groupType.intValue() == GroupType.GOOD)
                    {
                        map.put("good", typeList.stream().mapToLong(CdrTaskResult::getDurationOrNum).sum());
                    }
                    if (groupType.intValue() == GroupType.POOR)
                    {
                        map.put("poor", typeList.stream().mapToLong(CdrTaskResult::getDurationOrNum).sum());
                    }
                });
                mapList.add(map);
            });
        });
        return mapList;
    }
}
