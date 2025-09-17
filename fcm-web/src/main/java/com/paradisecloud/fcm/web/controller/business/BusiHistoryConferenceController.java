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
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiHistoryConferenceService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 历史会议，每次挂断会保存该历史记录Controller
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
@RestController
@RequestMapping("/busi/historyConference")
@Tag(name = "历史会议，每次挂断会保存该历史记录")
public class BusiHistoryConferenceController extends BaseController
{
    @Autowired
    private IBusiHistoryConferenceService busiHistoryConferenceService;

    /**
     * 查询历史会议，每次挂断会保存该历史记录列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询历史会议，每次挂断会保存该历史记录列表")
    public RestResponse list(BusiHistoryConference busiHistoryConference)
    {
        startPage();
        List<BusiHistoryConference> list = busiHistoryConferenceService.selectBusiHistoryConferenceList(busiHistoryConference);
        return getDataTable(list);
    }

    /**
     * 导出历史会议，每次挂断会保存该历史记录列表
     */
    @Log(title = "历史会议，每次挂断会保存该历史记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出历史会议，每次挂断会保存该历史记录列表")
    public RestResponse export(BusiHistoryConference busiHistoryConference)
    {
        List<BusiHistoryConference> list = busiHistoryConferenceService.selectBusiHistoryConferenceList(busiHistoryConference);
        ExcelUtil<BusiHistoryConference> util = new ExcelUtil<BusiHistoryConference>(BusiHistoryConference.class);
        return util.exportExcel(list, "conference");
    }

    /**
     * 获取历史会议，每次挂断会保存该历史记录详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取历史会议，每次挂断会保存该历史记录详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiHistoryConferenceService.selectBusiHistoryConferenceById(id));
    }

    /**
     * 新增历史会议，每次挂断会保存该历史记录
     */
    @Log(title = "历史会议，每次挂断会保存该历史记录", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增历史会议，每次挂断会保存该历史记录")
    public RestResponse add(@RequestBody BusiHistoryConference busiHistoryConference)
    {
        return toAjax(busiHistoryConferenceService.insertBusiHistoryConference(busiHistoryConference));
    }

    /**
     * 修改历史会议，每次挂断会保存该历史记录
     */
    @Log(title = "历史会议，每次挂断会保存该历史记录", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改历史会议，每次挂断会保存该历史记录")
    public RestResponse edit(@RequestBody BusiHistoryConference busiHistoryConference)
    {
        return toAjax(busiHistoryConferenceService.updateBusiHistoryConference(busiHistoryConference));
    }

    /**
     * 删除历史会议，每次挂断会保存该历史记录
     */
    @Log(title = "历史会议，每次挂断会保存该历史记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除历史会议，每次挂断会保存该历史记录")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiHistoryConferenceService.deleteBusiHistoryConferenceByIds(ids));
    }
}
