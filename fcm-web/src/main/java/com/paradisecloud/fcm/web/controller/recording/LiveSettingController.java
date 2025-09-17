package com.paradisecloud.fcm.web.controller.recording;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.model.BusiLiveSetting;
import com.paradisecloud.fcm.service.interfaces.IBusiLiveSettingService;
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
@RequestMapping("/busi/liveSetting")
@Tag(name = "直播地址管理配置")
public class LiveSettingController extends BaseController {

    @Autowired
    private IBusiLiveSettingService iBusiLiveSettingService;

    /**
     * 查询录制配置
     *
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "查询录制配置")
    public RestResponse list(BusiLiveSetting busiLiveSetting) {
        List<BusiLiveSetting> liveSettingList = iBusiLiveSettingService.selectBusiLiveSettingList(busiLiveSetting);
        return RestResponse.success(liveSettingList);
    }

    /**
     * 查询录制配置
     *
     * @return
     */
    @GetMapping("/getByDeptId/{deptId}")
    @Operation(summary = "查询录制配置")
    public RestResponse getByDeptId(@PathVariable Long deptId) {
        List<BusiLiveSetting> liveSettingList = iBusiLiveSettingService.getBusiLiveSettingByDeptId(deptId);
        return RestResponse.success(liveSettingList);
    }

    /**
     * 新增录制配置
     *
     * @param liveSetting 会议号
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增录制配置", description = "新增直播服务器")
    public RestResponse add(@RequestBody BusiLiveSetting liveSetting) {
        iBusiLiveSettingService.insertBusiLiveSetting(liveSetting);
        return RestResponse.success();
    }

    /**
     * 编辑录制配置
     *
     * @param liveSetting 会议号
     * @return
     */
    @PutMapping("/update/{id}")
    @Operation(summary = "新增录制配置", description = "修改直播服务器")
    public RestResponse update(@PathVariable Long id, @RequestBody BusiLiveSetting liveSetting) {
        liveSetting.setId(id);
        iBusiLiveSettingService.updateBusiLiveSetting(liveSetting);
        return RestResponse.success();
    }

    /**
     * 删除录制配置
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "删除录制配置", description = "删除直播服务器")
    public RestResponse delete(@PathVariable Long[] ids) {
        return toAjax(iBusiLiveSettingService.deleteBusiLiveSettingByIds(ids));
    }
}
