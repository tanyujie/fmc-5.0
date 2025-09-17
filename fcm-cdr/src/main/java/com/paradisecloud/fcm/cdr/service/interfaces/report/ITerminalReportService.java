package com.paradisecloud.fcm.cdr.service.interfaces.report;

import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import com.paradisecloud.fcm.dao.model.vo.TerminalReportSearchVo;
import com.paradisecloud.fcm.dao.model.vo.TerminalReportTerminalOverviewVo;
import com.paradisecloud.fcm.dao.model.vo.TerminalReportTerminalVo;

import java.util.List;
import java.util.Map;

/**
 * 终端报告
 */
public interface ITerminalReportService {

    /**
     * 查询终端报告终端列表
     *
     * @return
     */
    PaginationData<TerminalReportTerminalVo> getTerminalList(TerminalReportSearchVo terminalReportSearchVo);

    /**
     * 查询终端报告终端概况
     *
     * @return
     */
    TerminalReportTerminalOverviewVo getTerminalOverview(TerminalReportSearchVo terminalReportSearchVo);

    /**
     * 查询终端报告终端利用数
     *
     * @return
     */
    List<Map<String, Object>> getTerminalUseCount(TerminalReportSearchVo terminalReportSearchVo);

    /**
     * 查询终端报告终端类型数量
     *
     * @return
     */
    List<Map<String, Object>> getTerminalTypeCount(TerminalReportSearchVo terminalReportSearchVo);

    /**
     * 查询终端会议历史记录页面
     *
     * @param terminalReportSearchVo
     * @return
     */
    PaginationData<Map<String, Object>> getTerminalHisConferenceList(TerminalReportSearchVo terminalReportSearchVo);

    /**
     * 每天/周/月 发起会议的数量 统计
     *
     * @param searchVo
     * @return
     */
    List<Map<String, Object>> selectTerminalNumOfDay(ReportSearchVo searchVo);

    /**
     * 每天/周/月 发起会议的数量 统计
     *
     * @param searchVo
     * @param dateType 0:日 1：月
     * @return
     */
    List<Map<String, Object>> selectTerminalNumOfDay(ReportSearchVo searchVo, int dateType);
}
