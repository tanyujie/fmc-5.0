package com.paradisecloud.fcm.cdr.service.impls.report;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.fcm.cdr.service.interfaces.report.ITerminalReportService;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.*;
import com.paradisecloud.system.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 终端报告
 */
@Service
public class TerminalReportServiceImpl implements ITerminalReportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalReportServiceImpl.class);

    @Resource
    private CdrTerminalUsageMapper cdrTerminalUsageMapper;
    @Resource
    private BusiTerminalMapper busiTerminalMapper;
    @Resource
    private BusiHistoryParticipantMapper busiHistoryParticipantMapper;
    @Resource
    private CdrCallLegEndAlarmMapper cdrCallLegEndAlarmMapper;

    /**
     * 查询终端报告终端列表
     *
     * @param terminalReportSearchVo
     * @return
     */
    @Override
    public PaginationData<TerminalReportTerminalVo> getTerminalList(TerminalReportSearchVo terminalReportSearchVo) {
        PaginationData<TerminalReportTerminalVo> paginationData = new PaginationData<>();
        if (terminalReportSearchVo == null) {
            terminalReportSearchVo = new TerminalReportSearchVo();
        }
        if (terminalReportSearchVo.getPageNum() == null) {
            terminalReportSearchVo.setPageNum(1);
        }
        if (terminalReportSearchVo.getPageSize() == null) {
            terminalReportSearchVo.setPageSize(10);
        }
        if (terminalReportSearchVo.getPageSize() > 100) {
            terminalReportSearchVo.setPageSize(100);
        }
        PageHelper.startPage(terminalReportSearchVo.getPageNum(), terminalReportSearchVo.getPageSize());

        Long deptId = terminalReportSearchVo.getDeptId();
        Date startDate = null;
        Date endDate = null;
        if (terminalReportSearchVo.getStartTime() != null) {
            startDate = DateUtil.clearTime(terminalReportSearchVo.getStartTime());
        }
        if (terminalReportSearchVo.getEndTime() != null) {
            endDate = DateUtil.clearTime(terminalReportSearchVo.getEndTime());
        }
        Long terminalId = terminalReportSearchVo.getTerminalId();
        String terminalName = terminalReportSearchVo.getTerminalName();
        Integer terminalType = terminalReportSearchVo.getTerminalType();

        List<TerminalReportTerminalVo> terminalReportTerminalVoList = cdrTerminalUsageMapper.selectTerminalListForReport(deptId, startDate, endDate, terminalId, terminalName, terminalType);
        for (TerminalReportTerminalVo terminalReportTerminalVo : terminalReportTerminalVoList) {
            paginationData.addRecord(terminalReportTerminalVo);
        }

        PageInfo<?> pageInfo = new PageInfo<>(terminalReportTerminalVoList);
        paginationData.setTotal(pageInfo.getTotal());
        paginationData.setSize(pageInfo.getSize());
        paginationData.setPage(pageInfo.getPageNum());

        return paginationData;
    }

    /**
     * 查询终端报告终端概况
     *
     * @param terminalReportSearchVo
     * @return
     */
    @Override
    public TerminalReportTerminalOverviewVo getTerminalOverview(TerminalReportSearchVo terminalReportSearchVo) {
        Long deptId = terminalReportSearchVo.getDeptId();
        Date startDate = null;
        Date endDate = null;
        if (terminalReportSearchVo.getStartTime() != null) {
            startDate = DateUtil.clearTime(terminalReportSearchVo.getStartTime());
        }
        if (terminalReportSearchVo.getEndTime() != null) {
            endDate = DateUtil.clearTime(terminalReportSearchVo.getEndTime());
        }
        Long terminalId = terminalReportSearchVo.getTerminalId();
        TerminalReportTerminalOverviewVo terminalReportTerminalOverviewVo = cdrTerminalUsageMapper.selectTerminalOverviewForReport(deptId, startDate, endDate, terminalId);
        return terminalReportTerminalOverviewVo;
    }

    /**
     * 查询终端报告终端利用数
     *
     * @param terminalReportSearchVo
     * @return
     */
    @Override
    public List<Map<String, Object>> getTerminalUseCount(TerminalReportSearchVo terminalReportSearchVo) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Long deptId = terminalReportSearchVo.getDeptId();
        Date startDate = null;
        Date endDate = null;
        if (terminalReportSearchVo.getStartTime() != null) {
            startDate = DateUtil.clearTime(terminalReportSearchVo.getStartTime());
        }
        if (terminalReportSearchVo.getEndTime() != null) {
            endDate = DateUtil.clearTime(terminalReportSearchVo.getEndTime());
        }
        long count = 0;
        long useCount = 0;
        List<DeptRecordCount> deptRecordCountList = busiTerminalMapper.getDeptTerminalCount(deptId);
        if (deptRecordCountList.size() > 0) {
            DeptRecordCount deptRecordCount = deptRecordCountList.get(0);
            count = deptRecordCount.getCount();
            List<DeptTerminalUseCountVo> terminalReportTerminalUseCountVoList = cdrTerminalUsageMapper.selectTerminalUseCountForReport(deptId, startDate, endDate);
            if (terminalReportTerminalUseCountVoList.size() > 0) {
                DeptTerminalUseCountVo terminalReportTerminalUseCountVo = terminalReportTerminalUseCountVoList.get(0);
                useCount = terminalReportTerminalUseCountVo.getUseCount();
            }
        }

        {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("name", "使用终端数");
            resultMap.put("value", useCount);
            resultList.add(resultMap);
        }
        {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("name", "未使用终端数");
            resultMap.put("value", count - useCount);
            resultList.add(resultMap);
        }

        return resultList;
    }

    /**
     * 查询终端报告终端类型数量
     *
     * @param terminalReportSearchVo
     * @return
     */
    @Override
    public List<Map<String, Object>> getTerminalTypeCount(TerminalReportSearchVo terminalReportSearchVo) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Long deptId = terminalReportSearchVo.getDeptId();
        Date startDate = null;
        Date endDate = null;
        if (terminalReportSearchVo.getStartTime() != null) {
            startDate = DateUtil.clearTime(terminalReportSearchVo.getStartTime());
        }
        if (terminalReportSearchVo.getEndTime() != null) {
            endDate = DateUtil.clearTime(terminalReportSearchVo.getEndTime());
        }

        List<TerminalTypeCountVo> terminalTypeCountList = busiTerminalMapper.selectTerminalTypeCount(deptId);
        Map<Integer, Object> useCountMap = new HashMap<>();
        List<TerminalTypeCountVo> terminalTypeUseCountList = cdrTerminalUsageMapper.selectTerminalTypeUseCountForReport(deptId, startDate, endDate);
        if (terminalTypeUseCountList != null) {
            for (TerminalTypeCountVo terminalTypeCountVo : terminalTypeUseCountList) {
                useCountMap.put(terminalTypeCountVo.getType(), terminalTypeCountVo.getCount());
            }
        }
        if (terminalTypeCountList != null) {
            for (TerminalTypeCountVo terminalTypeCountVo : terminalTypeCountList) {
                Map<String, Object> countMap = new HashMap<>();
                int type = terminalTypeCountVo.getType();
                countMap.put("type", TerminalType.convert(type).getDisplayName());
                countMap.put("count", terminalTypeCountVo.getCount());
                long useCount = 0;
                if (useCountMap.containsKey(type)) {
                    useCount = (long) useCountMap.get(type);
                }
                countMap.put("useCount", useCount);
                resultList.add(countMap);
            }
        }
        return resultList;
    }

    /**
     * 查询终端会议历史记录页面
     *
     * @param terminalReportSearchVo
     * @return
     */
    @Override
    public PaginationData<Map<String, Object>> getTerminalHisConferenceList(TerminalReportSearchVo terminalReportSearchVo) {
        PaginationData<Map<String, Object>> paginationData = new PaginationData<>();
        long terminalId = terminalReportSearchVo.getTerminalId();
        if (terminalReportSearchVo.getPageNum() == null) {
            terminalReportSearchVo.setPageNum(1);
        }
        if (terminalReportSearchVo.getPageSize() == null) {
            terminalReportSearchVo.setPageSize(10);
        }
        Date startTime = null;
        Date endTime = null;
        if (terminalReportSearchVo.getStartTime() != null) {
            startTime = DateUtil.fillDate(terminalReportSearchVo.getStartTime(), false);
        }
        if (terminalReportSearchVo.getEndTime() != null) {
            endTime = DateUtil.fillDate(terminalReportSearchVo.getEndTime(), true);
        }
        PageHelper.startPage(terminalReportSearchVo.getPageNum(), terminalReportSearchVo.getPageSize());

        List<BusiHistoryParticipantConferenceVo> busiHistoryParticipantList = busiHistoryParticipantMapper.selectHistoryParticipantTerminalDetailListByTerminal(startTime, endTime, null, terminalId, null, terminalReportSearchVo.getIsJoin());
        for (BusiHistoryParticipantConferenceVo busiHistoryParticipant : busiHistoryParticipantList) {
            CdrCallLegEnd cdrCallLegEnd = busiHistoryParticipant.getCdrCallLegEnd();
            List<CdrCallLegEndAlarm> alarmList = cdrCallLegEndAlarmMapper.selectCdrCallLegEndAlarmList(new CdrCallLegEndAlarm(busiHistoryParticipant.getCallLegId()));
            cdrCallLegEnd.setCdrCallLegEndAlarmList(alarmList);
            if (busiHistoryParticipant.getOutgoingTime() != null && cdrCallLegEnd.getReason() == null) {
                cdrCallLegEnd.setReason(CallLegEndReasonEnum.CALL_ENDED);
            }
            Map<String, Object> participantMap = new HashMap<>();
            String coSpace = busiHistoryParticipant.getCoSpace();
            if (coSpace != null && (coSpace.endsWith("-zj") || coSpace.endsWith("-plc") || coSpace.endsWith("-kdc")|| coSpace.endsWith("-SMC3")|| coSpace.endsWith("-SMC2")|| coSpace.endsWith("-TENCENT"))) {
                if (coSpace.indexOf("-") > 0) {
                    String conferenceNumber = coSpace.substring(0, coSpace.indexOf("-"));
                    busiHistoryParticipant.setHistoryConferenceNumber(conferenceNumber);
                }
            }
            participantMap.put("historyParticipant", busiHistoryParticipant);
            participantMap.put("media", new ArrayList<>());
            paginationData.addRecord(participantMap);
        }

        PageInfo<?> pageInfo = new PageInfo<>(busiHistoryParticipantList);
        paginationData.setTotal(pageInfo.getTotal());
        paginationData.setSize(pageInfo.getSize());
        paginationData.setPage(pageInfo.getPageNum());

        return paginationData;
    }

    /**
     * 每天/周/月 终端参会的数量 统计
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<Map<String, Object>> selectTerminalNumOfDay(ReportSearchVo searchVo) {
        return selectTerminalNumOfDay(searchVo, 0);
    }

    /**
     * 每天/周/月 终端参会的数量 统计
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<Map<String, Object>> selectTerminalNumOfDay(ReportSearchVo searchVo, int dateType)
    {
        Long deptId = (searchVo.getDeptId() == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : searchVo.getDeptId();
        searchVo.setDeptId(deptId);
        List<Map<String, Object>> mapList = new ArrayList<>();

        TreeMap<String, Long> dayNumMap = new TreeMap<>();

        List<CdrTerminalUsage> terminalUsageList = cdrTerminalUsageMapper.searchCdrTerminalUsageList(searchVo);
        terminalUsageList.stream().collect(Collectors.groupingBy(CdrTerminalUsage::getDate)).forEach((day, groupList) -> {
            String dayStr = DateUtil.convertDateToString(day, "yyyy-MM-dd");
            dayNumMap.put(dayStr, groupList.stream().mapToLong(CdrTerminalUsage::getNum).sum());
        });

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
}
