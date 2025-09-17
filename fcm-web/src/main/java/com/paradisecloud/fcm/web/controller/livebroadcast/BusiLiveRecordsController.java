package com.paradisecloud.fcm.web.controller.livebroadcast;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiLiveRecords;
import com.paradisecloud.fcm.web.service.interfaces.IBusiLiveRecordsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.Resource;

/**
 * 直播录制文件记录Controller
 * 
 * @author lilinhai
 * @date 2024-05-21
 */
@RestController
@RequestMapping("/busi/live/records")
@Tag(name = "直播录制文件记录")
public class BusiLiveRecordsController extends BaseController
{
    @Resource
    private IBusiLiveRecordsService busiLiveRecordsService;

    /**
     * 查询直播录制文件记录列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询直播录制文件记录列表")
    public RestResponse list(BusiLiveRecords busiLiveRecords)
    {
        startPage();
        List<BusiLiveRecords> list = busiLiveRecordsService.selectBusiLiveRecordsList(busiLiveRecords);
        return getDataTable(list);
    }

    /**
     * 获取直播录制文件记录详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取直播录制文件记录详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiLiveRecordsService.selectBusiLiveRecordsById(id));
    }
//
//    /**
//     * 新增直播录制文件记录
//     */
//    @PreAuthorize("@ss.hasPermi('live:records:add')")
//    @Log(title = "直播录制文件记录", businessType = BusinessType.INSERT)
//    @PostMapping
//    @Operation(summary = "新增直播录制文件记录")
//    public RestResponse add(@RequestBody BusiLiveRecords busiLiveRecords)
//    {
//        return toAjax(busiLiveRecordsService.insertBusiLiveRecords(busiLiveRecords));
//    }

    /**
     * 修改直播录制文件记录
     */
    @PreAuthorize("@ss.hasPermi('live:records:edit')")
    @Log(title = "直播录制文件记录", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改直播录制文件记录")
    public RestResponse edit(@RequestBody BusiLiveRecords busiLiveRecords)
    {
        return toAjax(busiLiveRecordsService.updateBusiLiveRecords(busiLiveRecords));
    }

    /**
     * 删除直播录制文件记录
     */
    @PreAuthorize("@ss.hasPermi('live:records:remove')")
    @Log(title = "直播录制文件记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除直播录制文件记录")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiLiveRecordsService.deleteBusiLiveRecordsByIds(ids));
    }
}
