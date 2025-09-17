package com.paradisecloud.fcm.web.controller.smc3;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.AppointmentConferenceStatus;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.smc3.busi.templateconference.StartConference;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3ConferenceAppointment;
import com.paradisecloud.smc3.service.interfaces.IBusiMcuSmc3ConferenceAppointmentService;
import com.sinhy.utils.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 会议预约记录Controller
 * 
 * @author lilinhai
 * @date 2021-05-24
 */
@RestController
@RequestMapping("/busi/mcu/Smc3/conferenceAppointment")
@Tag(name = "会议预约记录")
public class BusiMcuSmc3ConferenceAppointmentController extends BaseController
{
    @Resource
    private IBusiMcuSmc3ConferenceAppointmentService busiMcuSmc3ConferenceAppointmentService;


    /**
     * 获取部门会议模板计数
     */
    @GetMapping(value = "/getDeptRecordCounts/{businessFieldType}")
    @Operation(summary = "获取部门预约会议计数")
    public RestResponse getDeptRecordCounts(@PathVariable("businessFieldType") Integer businessFieldType)
    {
        return RestResponse.success(busiMcuSmc3ConferenceAppointmentService.getDeptRecordCounts(businessFieldType));
    }
    
    /**
     * 查询会议预约记录列表
     */
    @PostMapping(value = "/list")
    @Operation(summary = "查询会议预约记录列表")
    public RestResponse list(@RequestBody BusiMcuSmc3ConferenceAppointment busiMcuSmc3ConferenceAppointment)
    {
        startPage();
        List<BusiMcuSmc3ConferenceAppointment> list = busiMcuSmc3ConferenceAppointmentService.selectBusiMcuSmc3ConferenceAppointmentList(busiMcuSmc3ConferenceAppointment);
        return getDataTable(list);
    }

    /**
     * 获取会议预约记录详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取会议预约记录详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiMcuSmc3ConferenceAppointmentService.selectBusiMcuSmc3ConferenceAppointmentById(id));
    }

    /**
     * 新增会议预约记录
     */
    @PostMapping
    @Operation(summary = "新增会议预约记录")
    public RestResponse add(@RequestBody BusiMcuSmc3ConferenceAppointment busiMcuSmc3ConferenceAppointment)
    {
        Integer type = busiMcuSmc3ConferenceAppointment.getType();
        try {
            if (type == null) {
                type = 1;
            }
            if (type != 1) {
                type = 2;
            }
            busiMcuSmc3ConferenceAppointment.setType(type);
            if (type == 2) {
                String startTimeStr = busiMcuSmc3ConferenceAppointment.getStartTime();
                String endTimeStr = busiMcuSmc3ConferenceAppointment.getEndTime();
                Date startTimeNew = new Date();
                String startTimeNewStr = DateUtil.convertDateToString(startTimeNew, "yyyy-MM-dd HH:mm:ss");
                Long diff = (Timestamp.valueOf(endTimeStr).getTime() - Timestamp.valueOf(startTimeStr).getTime()) / 1000;
                if (diff <= 0) {
                    diff = 60 * 120L;
                }
                Date endTimeNew = DateUtils.getDiffDate(startTimeNew, diff.intValue(), TimeUnit.SECONDS);
                String endTimeNewStr = DateUtil.convertDateToString(endTimeNew, "yyyy-MM-dd HH:mm:ss");
                busiMcuSmc3ConferenceAppointment.setStartTime(startTimeNewStr);
                busiMcuSmc3ConferenceAppointment.setEndTime(endTimeNewStr);
            }
        } catch (Exception e) {
        }

        Map<String, Object> resultMap = busiMcuSmc3ConferenceAppointmentService.insertBusiMcuSmc3ConferenceAppointment(busiMcuSmc3ConferenceAppointment);
        Integer success = 0;
        try {
            success = (Integer) resultMap.get("rows");
        } catch (Exception e) {
        }
        if (success > 0) {
            Long conferenceNumber = null;
            Long templateId = null;
            String tenantId = "";
            try {
                conferenceNumber = (Long) resultMap.get("conferenceNumber");
            } catch (Exception e) {

            }
            try {
                templateId = (Long) resultMap.get("templateId");
            } catch (Exception e) {

            }
            try {
                tenantId = (String) resultMap.get("tenantId");
            } catch (Exception e) {

            }
            if (type == 2) {
                if (templateId != null) {
                    try {
                        String cn = new StartConference().startConference(templateId);
                        if (StringUtils.isNotEmpty(cn)) {
                            if (busiMcuSmc3ConferenceAppointment.getIsStart() == null || YesOrNo.convert(busiMcuSmc3ConferenceAppointment.getIsStart()) != YesOrNo.YES) {
                                busiMcuSmc3ConferenceAppointment.setStatus(AppointmentConferenceStatus.ENABLED.getValue());
                                busiMcuSmc3ConferenceAppointment.setIsStart(YesOrNo.YES.getValue());
                                busiMcuSmc3ConferenceAppointmentService.updateBusiMcuSmc3ConferenceAppointment(busiMcuSmc3ConferenceAppointment, false);
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        int rows = 0;
        Object rowsObj = resultMap.get("rows");
        if (rowsObj != null) {
            rows = (int) rowsObj;
        }
        return toAjax(rows);
    }

    /**
     * 修改会议预约记录
     */
    @PutMapping(value = "/{id}")
    @Operation(summary = "修改会议预约记录")
    public RestResponse edit(@PathVariable("id") Long id, @RequestBody BusiMcuSmc3ConferenceAppointment busiMcuSmc3ConferenceAppointment)
    {
        busiMcuSmc3ConferenceAppointment.setId(id);
        return toAjax(busiMcuSmc3ConferenceAppointmentService.updateBusiMcuSmc3ConferenceAppointment(busiMcuSmc3ConferenceAppointment, true));
    }

    /**
     * 删除会议预约记录
     */
	@DeleteMapping("/{id}")
	@Operation(summary = "删除会议预约记录")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiMcuSmc3ConferenceAppointmentService.deleteBusiMcuSmc3ConferenceAppointmentById(id));
    }


}
