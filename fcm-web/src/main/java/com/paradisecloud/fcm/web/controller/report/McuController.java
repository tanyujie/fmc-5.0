package com.paradisecloud.fcm.web.controller.report;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.cdr.service.interfaces.ICdrReportResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author johnson liu
 * @date 2021/6/17 9:48
 */
@RestController
@RequestMapping("/busi/mcu")
@Tag(name = "Mcu使用情况统计")
public class McuController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(McuController.class);
    @Autowired
    private ICdrReportResultService cdrReportResultService;

    /**
     * MCU使用情况统计查询
     */
    @PostMapping("/usedRate")
    @Operation(summary = "MCU使用情况统计查询")
    public RestResponse usedRate(@RequestBody JSONObject jsonObject) {
        String fmeIp = jsonObject.getString("fmeIp");
        String startTime = jsonObject.getString("startTime");
        String endTime = jsonObject.getString("endTime");
        Integer deptId = jsonObject.getInteger("deptId");
        log.info("{}/usedRate方法入参:{},{},{},{}", this.getClass(), fmeIp, startTime, endTime, deptId);
        List<Map<String, Object>> mapList = cdrReportResultService.usedRate(deptId, fmeIp, startTime, endTime);
        return RestResponse.success(mapList);
    }
}
