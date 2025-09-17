package com.paradisecloud.fcm.web.controller.report;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.fcm.cdr.service.interfaces.report.ITerminalReportService;
import com.paradisecloud.fcm.dao.model.vo.TerminalReportSearchVo;
import com.paradisecloud.fcm.dao.model.vo.TerminalReportTerminalOverviewVo;
import com.paradisecloud.fcm.dao.model.vo.TerminalReportTerminalVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 终端报告
 */

@RestController
@RequestMapping("/busi/terminal/report")
@Tag(name = "终端报表统计")
public class TerminalReportController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalReportController.class);

    @Resource
    private ITerminalReportService terminalReportService;

    /**
     * 按组织、时间范围、终端id搜索
     */
    @GetMapping("/terminalList")
    @Operation(summary = "终端列表")
    public RestResponse getTerminalList(TerminalReportSearchVo terminalReportSearchVo) {
        LOGGER.info("{}/terminalList方法入参:{}", this.getClass(), terminalReportSearchVo);
        PaginationData<TerminalReportTerminalVo> mapList = terminalReportService.getTerminalList(terminalReportSearchVo);
        return RestResponse.success(0L, "查询成功", mapList);
    }

    /**
     * 按组织、时间范围、终端id终端概况
     */
    @GetMapping("/terminalOverview")
    @Operation(summary = "终端列表")
    public RestResponse getTerminalOverview(TerminalReportSearchVo terminalReportSearchVo) {
        LOGGER.info("{}/terminalOverview方法入参:{}", this.getClass(), terminalReportSearchVo);
        TerminalReportTerminalOverviewVo terminalReportTerminalOverviewVo = terminalReportService.getTerminalOverview(terminalReportSearchVo);
        return RestResponse.success(0L, "查询成功", terminalReportTerminalOverviewVo);
    }

    /**
     * 统计终端数量和使用数量
     *
     * @return
     */
    @GetMapping("/terminalUseCount")
    @Operation(summary = "统计终端数量和使用数量")
    public RestResponse getTerminalUseCount(TerminalReportSearchVo terminalReportSearchVo) {
        LOGGER.info("{}/terminalUseCount方法入参:{}", this.getClass(), terminalReportSearchVo);
        List<Map<String, Object>> resultList = terminalReportService.getTerminalUseCount(terminalReportSearchVo);
        return RestResponse.success(resultList);
    }

    /**
     * 根据终端类型统计各类型终端数量
     *
     * @return
     */
    @GetMapping("/terminalTypeCount")
    @Operation(summary = "根据终端类型统计各类型终端数量")
    public RestResponse getTerminalTypeCount(TerminalReportSearchVo terminalReportSearchVo) {
        LOGGER.info("{}/terminalTypeCount方法入参:{}", this.getClass(), terminalReportSearchVo);
        List<Map<String, Object>> mapList = terminalReportService.getTerminalTypeCount(terminalReportSearchVo);
        return RestResponse.success(mapList);
    }

    /**
     * 根据终端ID查询参与历史会议列表
     *
     * @return
     */
    @GetMapping("/terminalHisConferenceList")
    @Operation(summary = "根据终端ID查询参与历史会议列表")
    public RestResponse getTerminalHisConferenceList(TerminalReportSearchVo terminalReportSearchVo) {
        LOGGER.info("{}/terminalTypeCount方法入参:{}", this.getClass(), terminalReportSearchVo);
        PaginationData<Map<String, Object>> mapList = terminalReportService.getTerminalHisConferenceList(terminalReportSearchVo);
        return RestResponse.success(0L, "查询成功", mapList);
    }

}
