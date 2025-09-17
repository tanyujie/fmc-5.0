package com.paradisecloud.fcm.web.controller.terminal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.dao.model.BusiTerminalMeetingJoinSettings;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalMeetingJoinSettingsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 入会设置Controller
 * 
 * @author lilinhai
 * @date 2021-07-08
 */
@RestController
@RequestMapping("/busi/terminalAdvanceSettings")
@Tag(name = "入会设置")
public class BusiTerminalMeetingJoinSettingsController extends BaseController
{
    @Autowired
    private IBusiTerminalMeetingJoinSettingsService busiTerminalMeetingJoinSettingsService;

    /**
     * 获取入会设置详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取入会设置详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiTerminalMeetingJoinSettingsService.selectBusiTerminalMeetingJoinSettingsById(id));
    }

    /**
     * 修改入会设置
     */
    @Log(title = "入会设置", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/{id}")
    @Operation(summary = "修改入会设置", description = "修改入会设置")
    public RestResponse edit(@PathVariable("id") Long id, @RequestBody BusiTerminalMeetingJoinSettings busiTerminalMeetingJoinSettings)
    {
        busiTerminalMeetingJoinSettings.setId(id);
        return toAjax(busiTerminalMeetingJoinSettingsService.updateBusiTerminalMeetingJoinSettings(busiTerminalMeetingJoinSettings));
    }
}
