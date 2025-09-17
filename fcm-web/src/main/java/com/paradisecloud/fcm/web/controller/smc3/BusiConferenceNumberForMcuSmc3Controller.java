package com.paradisecloud.fcm.web.controller.smc3;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.common.enumer.ConferenceNumberCreateType;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumber;
import com.paradisecloud.smc3.service.interfaces.IBusiConferenceNumberSmc3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 会议号码记录Controller
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/busi/mcu/Smc3/conferenceNumber")
@Tag(name = "会议号码记录")
public class BusiConferenceNumberForMcuSmc3Controller extends BaseController
{
    @Resource
    private IBusiConferenceNumberSmc3Service busiConferenceNumberSmc3Service;

    /**
     * 获取部门条目计数
     */
    @GetMapping(value = "/getDeptRecordCounts")
    @Operation(summary = "获取部门条目计数")
    public RestResponse getDeptRecordCounts()
    {
        return RestResponse.success(busiConferenceNumberSmc3Service.getDeptRecordCounts());
    }
    
    /**
     * 查询会议号码记录列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询会议号码记录列表")
    public RestResponse list(BusiConferenceNumber busiConferenceNumber)
    {
        startPage();
        List<BusiConferenceNumber> list = busiConferenceNumberSmc3Service.selectBusiConferenceNumberList(busiConferenceNumber);
        return getDataTable(list);
    }

    /**
     * 获取会议号码记录详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取会议号码记录详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiConferenceNumberSmc3Service.selectBusiConferenceNumberById(id));
    }

    /**
     * 新增会议号码记录
     */
    @Log(title = "会议号码记录", businessType = BusinessType.INSERT)
    @PostMapping("")
    @Operation(summary = "新增会议号码记录")
    public RestResponse add(@RequestBody BusiConferenceNumber busiConferenceNumber)
    {
        // 设置会议号创建类型
        busiConferenceNumber.setCreateType(ConferenceNumberCreateType.MANUAL.getValue());
        return toAjax(busiConferenceNumberSmc3Service.insertBusiConferenceNumber(busiConferenceNumber));
    }

    /**
     * 修改会议号码记录
     */
    @Log(title = "会议号码记录", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    @Operation(summary = "修改会议号码记录")
    public RestResponse edit(@RequestBody BusiConferenceNumber busiConferenceNumber, @PathVariable Long id)
    {
        busiConferenceNumber.setId(id);
        return toAjax(busiConferenceNumberSmc3Service.updateBusiConferenceNumber(busiConferenceNumber));
    }

    /**
     * 删除会议号码记录
     */
    @Log(title = "会议号码记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{id}")
	@Operation(summary = "删除会议号码记录")
    public RestResponse remove(@PathVariable Long id)
    {
        return toAjax(busiConferenceNumberSmc3Service.deleteBusiConferenceNumberById(id));
    }
}
