package com.paradisecloud.fcm.web.controller.recording;

import com.paradisecloud.fcm.fme.conference.interfaces.IBusiConfigService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;

import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.Resource;

/**
 * 【请填写功能名称】Controller
 *
 * @author lilinhai
 * @date 2022-09-10
 */
@RestController
@RequestMapping("/busi/config")
public class BusiConfigController extends BaseController
{
    @Resource
    private IBusiConfigService busiConfigService;

    /**
     * 获取录制空间最大容量
     */
    @GetMapping(value = "/getRecordingFilesStorageSpaceMax")
    @Operation(summary = "获取录制空间最大容量")
    public RestResponse getRecordingFilesStorageSpaceMax()
    {
        return RestResponse.success(busiConfigService.getRecordingFilesStorageSpaceMax());
    }

    /**
     * 设置录制空间最大容量
     * @param recordingFilesStorageSpaceMax
     * @return
     */
    @PostMapping("updateRecordingFilesStorageSpaceMax")
    @Operation(summary = "更新录制空间最大容量", description = "设置录制空间最大容量")
    public RestResponse updateRecordingFilesStorageSpaceMax(@RequestParam(value = "recordingFilesStorageSpaceMax") Double recordingFilesStorageSpaceMax)
    {
        return toAjax(busiConfigService.updateRecordingFilesStorageSpaceMax(recordingFilesStorageSpaceMax));
    }


}
