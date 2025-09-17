package com.paradisecloud.fcm.web.controller.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.common.enumer.ConferenceNumberCreateType;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumber;
//import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConferenceNumberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 会议号码记录Controller
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
//@RestController
@RequestMapping("/busi/conferenceNumber")
@Tag(name = "会议号码记录")
public class BusiConferenceNumberController extends BaseController
{
//    @Autowired
//    private IBusiConferenceNumberService busiConferenceNumberService;
//
//    /**
//     * 获取部门条目计数
//     */
//    @GetMapping(value = "/getDeptRecordCounts")
//    @Operation(summary = "获取部门条目计数")
//    public RestResponse getDeptRecordCounts()
//    {
//        return RestResponse.success(busiConferenceNumberService.getDeptRecordCounts());
//    }
//
//    /**
//     * 查询会议号码记录列表
//     */
//    @GetMapping("/list")
//    @Operation(summary = "查询会议号码记录列表")
//    public RestResponse list(BusiConferenceNumber busiConferenceNumber)
//    {
//        startPage();
//        List<BusiConferenceNumber> list = busiConferenceNumberService.selectBusiConferenceNumberList(busiConferenceNumber);
//        return getDataTable(list);
//    }
//
//    /**
//     * 获取会议号码记录详细信息
//     */
//    @GetMapping(value = "/{id}")
//    @Operation(summary = "获取会议号码记录详细信息")
//    public RestResponse getInfo(@PathVariable("id") Long id)
//    {
//        return RestResponse.success(busiConferenceNumberService.selectBusiConferenceNumberById(id));
//    }
//
//    /**
//     * 新增会议号码记录
//     */
//    @Log(title = "会议号码记录", businessType = BusinessType.INSERT)
//    @PostMapping("")
//    @Operation(summary = "新增会议号码记录")
//    public RestResponse add(@RequestBody BusiConferenceNumber busiConferenceNumber)
//    {
//        // 设置会议号创建类型
//        busiConferenceNumber.setCreateType(ConferenceNumberCreateType.MANUAL.getValue());
//        return toAjax(busiConferenceNumberService.insertBusiConferenceNumber(busiConferenceNumber));
//    }
//
//    /**
//     * 修改会议号码记录
//     */
//    @Log(title = "会议号码记录", businessType = BusinessType.UPDATE)
//    @PutMapping("/{id}")
//    @Operation(summary = "修改会议号码记录")
//    public RestResponse edit(@RequestBody BusiConferenceNumber busiConferenceNumber, @PathVariable Long id)
//    {
//        busiConferenceNumber.setId(id);
//        return toAjax(busiConferenceNumberService.updateBusiConferenceNumber(busiConferenceNumber));
//    }
//
//    /**
//     * 删除会议号码记录
//     */
//    @Log(title = "会议号码记录", businessType = BusinessType.DELETE)
//	@DeleteMapping("/{id}")
//	@Operation(summary = "删除会议号码记录")
//    public RestResponse remove(@PathVariable Long id)
//    {
//        return toAjax(busiConferenceNumberService.deleteBusiConferenceNumberById(id));
//    }
}
