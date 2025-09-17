package com.paradisecloud.fcm.web.controller.report;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.cdr.service.interfaces.report.IConferenceReportService;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiHistoryConferenceService;

import com.paradisecloud.system.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author johnson liu
 * @Date 2021/6/6 19:21
 **/
@RestController
@RequestMapping("/busi/report")
@Tag(name = "会议报表统计")
public class ConferenceReportController extends BaseController {
    @Autowired
    private IBusiHistoryConferenceService iBusiHistoryConferenceService;
    @Autowired
    private IConferenceReportService iConferenceReportService;
    /**
     * 统计每天发起会议的数量
     * @param searchVo
     * @return
     */
    @GetMapping("/conference/reportNumOfDay")
    @Operation(summary = "统计每天发起会议的数量")
    public RestResponse reportNumOfDay(ReportSearchVo searchVo) {
        Long deptId = (searchVo.getDeptId() == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : searchVo.getDeptId();
        searchVo.setDeptId(deptId);
        List<Map<String, Object>> mapList = iConferenceReportService.selectConferenceNumOfDay(searchVo);
        return RestResponse.success(mapList);
    }

    /**
     * 每天发起会议的时长总和
     * @param searchVo
     * @return
     */
    @GetMapping("/conference/reportDurationOfDay")
    @Operation(summary = "每天发起会议的时长总和")
    public RestResponse reportDurationOfDay(ReportSearchVo searchVo) {
        Long deptId = (searchVo.getDeptId() == null) ? SecurityUtils.getLoginUser().getUser().getDeptId() : searchVo.getDeptId();
        searchVo.setDeptId(deptId);
        List<Map<String, Object>> mapList = iConferenceReportService.selectConferenceDurationOfDay(searchVo);
        return RestResponse.success(mapList);
    }

    /**
     * 不同会议时长类型数量统计
     * @param searchVo
     * @return
     */
    @GetMapping("/conference/reportDurationType")
    @Operation(summary = "不同会议时长类型数量统计")
    public RestResponse reportDurationType(ReportSearchVo searchVo) {
        List<Map<String, Object>> list = iBusiHistoryConferenceService.reportDurationType(searchVo);
        return RestResponse.success(list);
    }


    /**
     * 每天/周/月 发起会议的质量分析 统计
     * @param searchVo
     * @return
     */
    @GetMapping("/conference/reportCallQualityOfDay")
    @Operation(summary = "每天/周/月 发起会议的质量分析统计")
    public RestResponse reportCallQualityOfDay(ReportSearchVo searchVo) {
        List<Map<String, Object>> list = iConferenceReportService.reportCallQualityOfDay(searchVo);
        return RestResponse.success(list);
    }
}
