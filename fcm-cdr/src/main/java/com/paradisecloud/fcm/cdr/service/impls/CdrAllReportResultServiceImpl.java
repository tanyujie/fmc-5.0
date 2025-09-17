package com.paradisecloud.fcm.cdr.service.impls;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrAllReportResultService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.BusiHistoryAllConference;
import com.paradisecloud.fcm.dao.model.BusiHistoryAllParticipant;
import com.paradisecloud.fcm.dao.model.CdrAllCallLegNumDate;
import com.paradisecloud.fcm.dao.model.CdrAllCallNumDate;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import com.sinhy.enumer.DateTimeFormatPattern;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author johnson liu
 * @date 2021/5/27 16:49
 */
@Service
public class CdrAllReportResultServiceImpl implements ICdrAllReportResultService
{
    @Resource
    private BusiHistoryAllConferenceMapper busiHistoryAllConferenceMapper;
    
    @Resource
    private BusiHistoryAllParticipantMapper busiHistoryAllParticipantMapper;
    
    @Resource
    private BusiTerminalMapper busiTerminalMapper;
    
    @Resource
    private CdrAllCallNumDateMapper cdrAllCallNumDateMapper;
    
    @Resource
    private CdrAllCallLegNumDateMapper cdrAllCallLegNumDateMapper;
    
    /**
     * 首页会议总时长、会议次数、参数终端数统计
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public Map<String, Object> reportConferenceOfIndex(String startTime, String endTime)
    {
        if (Objects.nonNull(startTime))
        {
            Assert.isTrue(DateTimeFormatPattern.matchFormatter(startTime) != null, "请传入正确的时间格式");
        }
        if (Objects.nonNull(endTime))
        {
            Assert.isTrue(DateTimeFormatPattern.matchFormatter(endTime) != null, "请传入正确的时间格式");
        }
        
        List<BusiHistoryAllConference> historyConferences0 = busiHistoryAllConferenceMapper.report(null, startTime, endTime);
        List<BusiHistoryAllConference> historyConferences = new ArrayList<>();
        for (BusiHistoryAllConference busiHistoryConference : historyConferences0)
        {
            historyConferences.add(busiHistoryConference);
        }
        Map<String, Object> map = new HashMap<>();
        
        int sum = 0;
        int deviceNumSum = 0;
        if (!CollectionUtils.isEmpty(historyConferences))
        {
            for (BusiHistoryAllConference historyConference : historyConferences)
            {
                int duration = (historyConference.getDuration() == null) ? 0 : historyConference.getDuration();
                int deviceNum = (historyConference.getDeviceNum() == null) ? 0 : historyConference.getDeviceNum();
                sum += duration;
                deviceNumSum += deviceNum;
            }
        }
        
        startTime = (StringUtils.hasText(startTime)) ? DateUtil.fillDateString(startTime, false) : null;
        endTime = (StringUtils.hasText(endTime)) ? DateUtil.fillDateString(endTime, true) : null;
        // 根据deptId查询与会者列表
        List<BusiHistoryAllParticipant> participantList = busiHistoryAllParticipantMapper.selectParticipantByTime(startTime, endTime);
        
        int totalParticipantDuration = participantList.stream().mapToInt(BusiHistoryAllParticipant::getDurationSeconds).sum();
        
        map.put("conferenceCount", CollectionUtils.isEmpty(historyConferences) ? 0 : historyConferences.size());
        map.put("totalDuration", sum);
        map.put("totalDeviceNum", deviceNumSum);
        
        map.put("totalParticipantDuration", totalParticipantDuration);
        map.put("avgParticipantDuration", CollectionUtils.isEmpty(participantList) ? 0 : totalParticipantDuration / participantList.size());
        map.put("avgDuration", CollectionUtils.isEmpty(historyConferences) ? 0 : sum / historyConferences.size());
        map.put("avgDeviceNum", CollectionUtils.isEmpty(historyConferences) ? 0 : deviceNumSum / historyConferences.size());
        return map;
    }
    
    @Override
    public List<Map<String, Object>> reportByReason(Date startTime, Date endTime)
    {
        startTime = Objects.nonNull(startTime) ? startTime : DateUtil.convertLocalDateToDate(LocalDate.now().minusDays(7));
        endTime = Objects.nonNull(endTime) ? endTime : DateUtil.convertLocalDateToDate(LocalDate.now());
        List<BusiHistoryAllParticipant> historyParticipantList = busiHistoryAllParticipantMapper
                .selectParticipantDetailListByTime(DateUtil.convertDateToString(startTime, null), DateUtil.convertDateToString(endTime, null));
        
        List<Map<String, Object>> mapList = new ArrayList<>();
        historyParticipantList.stream()
        .filter(i -> i.getCdrCallLegEnd() != null && i.getCdrCallLegEnd().getReason() != null)
        .collect(Collectors.groupingBy(p -> p.getCdrCallLegEnd().getReason())).forEach((key, groupList) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("reason", key.getDisplayName());
            map.put("count", groupList.size());
            mapList.add(map);
        });
        return mapList;
    }
    
    /**
     * 获取所有离线原因列表
     *
     * @return
     */
    @Override
    public List<CallLegEndReasonEnum> getAllReason()
    {
        CallLegEndReasonEnum[] values = CallLegEndReasonEnum.values();
        return new ArrayList<>(Arrays.asList(values));
    }
    
    @Override
    public List<Map<String, Object>> reportByDeviceType()
    {
//        BusiTerminal busiTerminal = new BusiTerminal();
//        List<BusiTerminal> busiTerminals = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
        
        List<Map<String, Object>> mapList = new ArrayList<>();
//        Optional.ofNullable(busiTerminals).ifPresent(theList -> {
//            busiTerminals.stream().collect(Collectors.groupingBy(BusiTerminal::getType)).forEach((type, groupList) -> {
//                Map<String, Object> map = new HashMap<>();
//                map.put("type", TerminalType.convert(type).getDisplayName());
//                map.put("count", groupList.size());
//                mapList.add(map);
//            });
//        });
        return mapList;
    }
    
    /**
     * 终端使用情况分析(柱状图)
     * 通话时长最长的 5 个终端，时长/终端
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<Map<String, Object>> reportOrderByDuration(ReportSearchVo searchVo)
    {
        List<Map<String, Object>> mapList = busiHistoryAllParticipantMapper.reportOrderByDuration(searchVo.getStartTime(), searchVo.getEndTime());
        return mapList;
    }
    
    /**
     * MCU使用情况统计查询
     *
     * @param fmeIp
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<Map<String, Object>> usedRate(String fmeIp, String startTime, String endTime)
    {
        List<CdrAllCallNumDate> cdrCallNumDateList = cdrAllCallNumDateMapper.selectByFmeIpAndDateRange(fmeIp, startTime, endTime);
        List<CdrAllCallLegNumDate> cdrCallLegNumDateList = cdrAllCallLegNumDateMapper.selectByFmeIpAndDate(fmeIp, startTime, endTime);
        
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(cdrCallNumDateList))
        {
            Map<Date, List<CdrAllCallNumDate>> callNumMap = cdrCallNumDateList.stream().collect(Collectors.groupingBy(CdrAllCallNumDate::getRecordDate, LinkedHashMap::new, Collectors.toList()));
            Iterator<Date> iterator = callNumMap.keySet().iterator();
            Map<Date, List<CdrAllCallLegNumDate>> callLegNumMap = null;
            if (!CollectionUtils.isEmpty(cdrCallLegNumDateList))
            {
                callLegNumMap = cdrCallLegNumDateList.stream().collect(Collectors.groupingBy(CdrAllCallLegNumDate::getRecordDate));
            }
            while (iterator.hasNext())
            {
                Date date = iterator.next();
                Map<String, Object> map = new HashMap<>(4);
                map.put("date", DateUtil.convertDateToString(date, "yyyy-MM-dd"));
                int callNum = 0;
                if (callNumMap != null)
                {
                    List<CdrAllCallNumDate> cdrCallNumDates = callNumMap.get(date);
                    callNum = (CollectionUtils.isEmpty(cdrCallNumDates)) ? 0 : cdrCallNumDates.stream().mapToInt(CdrAllCallNumDate::getNumber).sum();
                }
                map.put("callNum", callNum);
                int callLegNum = 0;
                if (callLegNumMap != null)
                {
                    List<CdrAllCallLegNumDate> cdrCallLegNumDates = callLegNumMap.get(date);
                    callLegNum = (CollectionUtils.isEmpty(cdrCallLegNumDates)) ? 0 : cdrCallLegNumDates.stream().mapToInt(CdrAllCallLegNumDate::getNumber).sum();
                }
                map.put("callLegNum", callLegNum);
                resultList.add(map);
            }
        }
        return resultList;
    }
    
}
