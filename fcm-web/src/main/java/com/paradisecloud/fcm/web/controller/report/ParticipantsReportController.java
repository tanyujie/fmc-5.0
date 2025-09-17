package com.paradisecloud.fcm.web.controller.report;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.cdr.service.interfaces.ICdrReportResultService;
import com.paradisecloud.fcm.cdr.service.interfaces.ICdrTaskResultService;
import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author johnson liu
 * @Date 2021/6/6 19:23
 **/
@RestController
@RequestMapping("/busi/report")
@Tag(name = "会场报表统计")
public class ParticipantsReportController extends BaseController {
    @Autowired
    private ICdrReportResultService iCdrReportResultService;
    @Autowired
    private ICdrTaskResultService iCdrTaskResultService;

    /**
     * 终端使用情况分析(柱状图)
     * 通话时长最长的 5 个终端，时长/终端
     *
     * @param searchVo
     * @return
     */
    @GetMapping("/participant/reportOrderByDuration")
    @Operation(summary = "终端使用情况分析(柱状图)-通话时长最长的 5 个终端，时长/终端")
    public RestResponse reportOrderByDuration(ReportSearchVo searchVo) {
        List<Map<String, Object>> mapList = iCdrReportResultService.reportOrderByDuration(searchVo);
        return RestResponse.success(mapList);
    }

    /**
     * 参会者告警统计
     *
     * @param searchVo
     * @return
     */
    @GetMapping("/participant/reportAlarmTypeRate")
    @Operation(summary = "参会者告警统计")
    public RestResponse reportAlarmTypeRate(ReportSearchVo searchVo) {
        List<Map<String, Object>> alarmTypeRateList = iCdrTaskResultService.reportAlarmTypeRate(searchVo);
        return RestResponse.success(alarmTypeRateList);
    }

    /**
     * 根据设备类型统计各类型设备数量
     *
     * @return
     */
    @GetMapping("/participant/reportByDeviceType")
    @Operation(summary = "根据设备类型统计各类型设备数量")
    public RestResponse reportByDeviceType(@RequestParam(required = false) Long deptId) {
        List<Map<String, Object>> mapList = iCdrReportResultService.reportByDeviceType(deptId);
        return RestResponse.success(mapList);
    }

    /**
     * 终止原因统计
     *
     * @return
     */
    @GetMapping("/participant/reportByReason")
    @Operation(summary = "终止原因统计")
    public RestResponse reportByReason(ReportSearchVo searchVo) {
        List<Map<String, Object>> mapList = iCdrReportResultService.reportByReason(searchVo.getDeptId(), searchVo.getStartTime(), searchVo.getEndTime());
        return RestResponse.success(mapList);
    }
    @GetMapping("/participant/getAllReason")
    @Operation(summary = "终止原因统计")
    public RestResponse getAllReason(){
        List<CallLegEndReasonEnum> allReason = iCdrReportResultService.getAllReason();
        return RestResponse.success(allReason);
    }

}
