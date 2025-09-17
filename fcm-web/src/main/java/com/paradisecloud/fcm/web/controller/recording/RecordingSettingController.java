package com.paradisecloud.fcm.web.controller.recording;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.model.BusiRecordSetting;
import com.paradisecloud.fcm.dao.model.BusiRecordSettingForm;
import com.paradisecloud.fcm.web.service.interfaces.IBusiRecordSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author johnson liu
 * @date 2021/4/30 14:57
 */
@RestController
@RequestMapping("/busi/recordSetting")
@Tag(name = "会议录制配置")
public class RecordingSettingController extends BaseController {

    @Autowired
    private IBusiRecordSettingService iBusiRecordSettingService;

    /**
     * 查询录制配置
     *
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "查询录制配置")
    public RestResponse list(BusiRecordSetting busiRecordSetting) {
        List<BusiRecordSetting> busiRecordSettings = iBusiRecordSettingService.selectBusiRecordSettingList(busiRecordSetting);
        return RestResponse.success(busiRecordSettings);
    }

    /**
     * 新增录制配置
     *
     * @param recordSetting 会议号
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增录制配置", description = "新增录制配置")
    public RestResponse add(@RequestBody BusiRecordSettingForm recordSetting) {
        iBusiRecordSettingService.insertBusiRecordSetting(recordSetting);
        return RestResponse.success();
    }

    /**
     * 编辑录制配置
     *
     * @param recordSetting 会议号
     * @return
     */
    @PutMapping("/update/{id}")
    @Operation(summary = "新增录制配置", description = "修改录制配置")
    public RestResponse update(@PathVariable Long id, @RequestBody BusiRecordSettingForm recordSetting) {
        recordSetting.setId(id);
        iBusiRecordSettingService.updateBusiRecordSetting(recordSetting);
        return RestResponse.success();
    }

    /**
     * 删除录制配置
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "删除录制配置", description = "删除录制配置")
    public RestResponse delete(@PathVariable Long[] ids) {
        return toAjax(iBusiRecordSettingService.deleteBusiRecordSettingByIds(ids));
    }
}
