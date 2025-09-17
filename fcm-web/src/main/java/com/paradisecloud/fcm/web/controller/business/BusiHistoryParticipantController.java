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
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.service.interfaces.IBusiHistoryParticipantService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 历史会议的参会者Controller
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/busi/historyParticipant")
@Tag(name = "历史会议的参会者")
public class BusiHistoryParticipantController extends BaseController
{
    @Autowired
    private IBusiHistoryParticipantService busiHistoryParticipantService;

    /**
     * 查询历史会议的参会者列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询历史会议的参会者列表")
    public RestResponse list(BusiHistoryParticipant busiHistoryParticipant)
    {
        startPage();
        List<BusiHistoryParticipant> list = busiHistoryParticipantService.selectBusiHistoryParticipantList(busiHistoryParticipant);
        return getDataTable(list);
    }

    /**
     * 导出历史会议的参会者列表
     */
    @Log(title = "历史会议的参会者", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出历史会议的参会者列表")
    public RestResponse export(BusiHistoryParticipant busiHistoryParticipant)
    {
        List<BusiHistoryParticipant> list = busiHistoryParticipantService.selectBusiHistoryParticipantList(busiHistoryParticipant);
        ExcelUtil<BusiHistoryParticipant> util = new ExcelUtil<BusiHistoryParticipant>(BusiHistoryParticipant.class);
        return util.exportExcel(list, "participant");
    }

    /**
     * 获取历史会议的参会者详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取历史会议的参会者详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiHistoryParticipantService.selectBusiHistoryParticipantById(id));
    }

    /**
     * 新增历史会议的参会者
     */
    @Log(title = "历史会议的参会者", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增历史会议的参会者")
    public RestResponse add(@RequestBody BusiHistoryParticipant busiHistoryParticipant)
    {
        return toAjax(busiHistoryParticipantService.insertBusiHistoryParticipant(busiHistoryParticipant));
    }

    /**
     * 修改历史会议的参会者
     */
    @Log(title = "历史会议的参会者", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改历史会议的参会者")
    public RestResponse edit(@RequestBody BusiHistoryParticipant busiHistoryParticipant)
    {
        return toAjax(busiHistoryParticipantService.updateBusiHistoryParticipant(busiHistoryParticipant));
    }

    /**
     * 删除历史会议的参会者
     */
    @Log(title = "历史会议的参会者", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除历史会议的参会者")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiHistoryParticipantService.deleteBusiHistoryParticipantByIds(ids));
    }
}
