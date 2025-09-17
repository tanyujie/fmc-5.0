package com.paradisecloud.fcm.cdr.service.impls;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrReportResultService;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryParticipantMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.mapper.CdrCallLegNumDateMapper;
import com.paradisecloud.fcm.dao.mapper.CdrCallNumDateMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.CdrCallLegNumDate;
import com.paradisecloud.fcm.dao.model.CdrCallNumDate;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.enumer.DateTimeFormatPattern;

/**
 * @author johnson liu
 * @date 2021/5/27 16:49
 */
@Service
public class CdrReportResultServiceImpl implements ICdrReportResultService
{
    @Autowired
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;
    
    @Autowired
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;
    
    @Autowired
    private BusiTerminalMapper busiTerminalMapper;
    
    @Autowired
    private CdrCallNumDateMapper cdrCallNumDateMapper;
    
    @Autowired
    private CdrCallLegNumDateMapper cdrCallLegNumDateMapper;
    
    /**
     * 首页会议总时长、会议次数、参数终端数统计
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public Map<String, Object> reportConferenceOfIndex(Long deptId, String startTime, String endTime)
    {
        deptId = (deptId == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : deptId;
        
        if (Objects.nonNull(startTime))
        {
            Assert.isTrue(DateTimeFormatPattern.matchFormatter(startTime) != null, "请传入正确的时间格式");
        }
        if (Objects.nonNull(endTime))
        {
            Assert.isTrue(DateTimeFormatPattern.matchFormatter(endTime) != null, "请传入正确的时间格式");
        }
        
        List<BusiHistoryConference> historyConferences0 = busiHistoryConferenceMapper.reportByDept(null, null, startTime, endTime);
        List<BusiHistoryConference> historyConferences = new ArrayList<>();
        Set<Long> depts = SysDeptCache.getInstance().getSubordinateDeptIds(deptId);
        for (BusiHistoryConference busiHistoryConference : historyConferences0)
        {
            if (depts.contains(busiHistoryConference.getDeptId()))
            {
                historyConferences.add(busiHistoryConference);
            }
        }
        Map<String, Object> map = new HashMap<>();
        
        int sum = 0;
        int deviceNumSum = 0;
        if (!CollectionUtils.isEmpty(historyConferences))
        {
            for (BusiHistoryConference historyConference : historyConferences)
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
        List<BusiHistoryParticipant> participantList = busiHistoryParticipantMapper.selectParticipantByDeptAndTime(deptId, startTime, endTime);
        
        int totalParticipantDuration = participantList.stream().mapToInt(BusiHistoryParticipant::getDurationSeconds).sum();
        
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
    public List<Map<String, Object>> reportByReason(Long deptId, Date startTime, Date endTime)
    {
        deptId = (deptId == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : deptId;
        
        startTime = Objects.nonNull(startTime) ? startTime : DateUtil.convertLocalDateToDate(LocalDate.now().minusDays(7));
        endTime = Objects.nonNull(endTime) ? endTime : DateUtil.convertLocalDateToDate(LocalDate.now());
        List<BusiHistoryParticipant> historyParticipantList = busiHistoryParticipantMapper
                .selectParticipantDetailListByDeptAndTime(deptId, DateUtil.convertDateToString(startTime, null), DateUtil.convertDateToString(endTime, null));
        
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
    public List<Map<String, Object>> reportByDeviceType(Long deptId)
    {
        deptId = (deptId == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : deptId;
        BusiTerminal busiTerminal = new BusiTerminal();
        busiTerminal.setDeptId(deptId);
        List<BusiTerminal> busiTerminals = busiTerminalMapper.selectBusiTerminalList(busiTerminal);
        
        List<Map<String, Object>> mapList = new ArrayList<>();
        Optional.ofNullable(busiTerminals).ifPresent(theList -> {
            busiTerminals.stream().collect(Collectors.groupingBy(BusiTerminal::getType)).forEach((type, groupList) -> {
                Map<String, Object> map = new HashMap<>();
                map.put("type", TerminalType.convert(type).getDisplayName());
                map.put("count", groupList.size());
                mapList.add(map);
            });
        });
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
        Long deptId = validateParam(searchVo);
        List<Map<String, Object>> mapList = busiHistoryParticipantMapper.reportOrderByDuration(deptId, searchVo.getStartTime(), searchVo.getEndTime());
        return mapList;
    }
    
    /**
     * MCU使用情况统计查询
     *
     * @param deptId
     * @param fmeIp
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<Map<String, Object>> usedRate(Integer deptId, String fmeIp, String startTime, String endTime)
    {
        List<CdrCallNumDate> cdrCallNumDateList = cdrCallNumDateMapper.selectByFmeIpAndDate(deptId, fmeIp, startTime, endTime);
        List<CdrCallLegNumDate> cdrCallLegNumDateList = cdrCallLegNumDateMapper.selectByFmeIpAndDate(deptId, fmeIp, startTime, endTime);
        
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(cdrCallNumDateList))
        {
            Map<Date, List<CdrCallNumDate>> callNumMap = cdrCallNumDateList.stream().collect(Collectors.groupingBy(CdrCallNumDate::getRecordDate, LinkedHashMap::new, Collectors.toList()));
            Iterator<Date> iterator = callNumMap.keySet().iterator();
            Map<Date, List<CdrCallLegNumDate>> callLegNumMap = null;
            if (!CollectionUtils.isEmpty(cdrCallLegNumDateList))
            {
                callLegNumMap = cdrCallLegNumDateList.stream().collect(Collectors.groupingBy(CdrCallLegNumDate::getRecordDate));
            }
            while (iterator.hasNext())
            {
                Date date = iterator.next();
                Map<String, Object> map = new HashMap<>(4);
                map.put("date", DateUtil.convertDateToString(date, "yyyy-MM-dd"));
                int callNum = 0;
                if (callNumMap != null)
                {
                    List<CdrCallNumDate> cdrCallNumDates = callNumMap.get(date);
                    callNum = (CollectionUtils.isEmpty(cdrCallNumDates)) ? 0 : cdrCallNumDates.stream().mapToInt(CdrCallNumDate::getNumber).sum();
                }
                map.put("callNum", callNum);
                int callLegNum = 0;
                if (callLegNumMap != null)
                {
                    List<CdrCallLegNumDate> cdrCallLegNumDates = callLegNumMap.get(date);
                    callLegNum = (CollectionUtils.isEmpty(cdrCallLegNumDates)) ? 0 : cdrCallLegNumDates.stream().mapToInt(CdrCallLegNumDate::getNumber).sum();
                }
                map.put("callLegNum", callLegNum);
                resultList.add(map);
            }
        }
        return resultList;
    }
    
    private Long validateParam(ReportSearchVo searchVo)
    {
        Long deptId = (searchVo.getDeptId() == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : searchVo.getDeptId();
        return deptId;
    }
    
}
