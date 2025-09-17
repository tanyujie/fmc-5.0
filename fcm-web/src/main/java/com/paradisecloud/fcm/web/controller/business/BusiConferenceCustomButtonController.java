package com.paradisecloud.fcm.web.controller.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiConferenceCustomButton;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceCustomButtonService;
import org.springframework.web.bind.annotation.*;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.Resource;

/**
 * 会议自定义按钮Controller
 * 
 * @author lilinhai
 * @date 2024-07-05
 */
@RestController
@RequestMapping("/busi/button")
@Tag(name = "会议自定义按钮")
public class BusiConferenceCustomButtonController extends BaseController
{
    @Resource
    private IBusiConferenceCustomButtonService busiConferenceCustomButtonService;

    /**
     * 查询会议自定义按钮列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询会议自定义按钮列表")
    public RestResponse list(BusiConferenceCustomButton busiConferenceCustomButton)
    {
        List<BusiConferenceCustomButton> list = busiConferenceCustomButtonService.selectBusiConferenceCustomButtonList(busiConferenceCustomButton);
        return RestResponse.success(list);
    }

    /**
     * 查询会议自定义按钮列表
     */
    @GetMapping("/list/all")
    @Operation(summary = "查询会议自定义按钮列表")
    public RestResponse list()
    {
        List<ModelBean> mbList = new ArrayList<>();
        Map<String, List<BusiConferenceCustomButton>> mcuTypeMap = new HashMap<>();
        List<BusiConferenceCustomButton> list = busiConferenceCustomButtonService.selectBusiConferenceCustomButtonList(new BusiConferenceCustomButton());
        for (int i = 0; i < list.size(); i++) {
            BusiConferenceCustomButton busiConferenceCustomButton = list.get(i);
            List<BusiConferenceCustomButton> busiConferenceCustomButtonList = mcuTypeMap.get(busiConferenceCustomButton.getMcuType());
            if (busiConferenceCustomButtonList == null) {
                busiConferenceCustomButtonList = new ArrayList<>();
                mcuTypeMap.put(busiConferenceCustomButton.getMcuType(), busiConferenceCustomButtonList);
            }
            busiConferenceCustomButtonList.add(busiConferenceCustomButton);
        }
        for (String mcuType : mcuTypeMap.keySet()) {
            ModelBean modelBean = new ModelBean();
            modelBean.put("mcuType", mcuType);
            modelBean.put("list", mcuTypeMap.get(mcuType));
            mbList.add(modelBean);
        }
        return RestResponse.success(mbList);
    }

    /**
     * 修改会议自定义按钮
     */
    @Log(title = "会议自定义按钮", businessType = BusinessType.UPDATE)
    @PutMapping("/list")
    @Operation(summary = "修改会议自定义按钮")
    public RestResponse update(@RequestBody List<BusiConferenceCustomButton> busiConferenceCustomButtonList, @RequestParam("mcuType") String mcuType)
    {
        int i = busiConferenceCustomButtonService.updateBusiConferenceCustomButton(busiConferenceCustomButtonList, mcuType);
        if (i > 0) {
            return RestResponse.success(busiConferenceCustomButtonList);
        }
        return RestResponse.fail();
    }

    /**
     * 获取会议自定义按钮详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取会议自定义按钮详细信息")
    public RestResponse getInfo(@PathVariable("id") String id, @RequestParam("mcuType") String mcuType)
    {
        return RestResponse.success(busiConferenceCustomButtonService.selectBusiConferenceCustomButtonById(id, mcuType));
    }

    /**
     * 新增会议自定义按钮
     */
    @Log(title = "会议自定义按钮", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增会议自定义按钮")
    public RestResponse add(@RequestBody BusiConferenceCustomButton busiConferenceCustomButton)
    {
        return toAjax(busiConferenceCustomButtonService.insertBusiConferenceCustomButton(busiConferenceCustomButton));
    }

    /**
     * 修改会议自定义按钮
     */
    @Log(title = "会议自定义按钮", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改会议自定义按钮")
    public RestResponse edit(@RequestBody BusiConferenceCustomButton busiConferenceCustomButton)
    {
        return toAjax(busiConferenceCustomButtonService.updateBusiConferenceCustomButton(busiConferenceCustomButton));
    }

    /**
     * 删除会议自定义按钮
     */
    @Log(title = "会议自定义按钮", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除会议自定义按钮")
    public RestResponse remove(@PathVariable String[] ids, @RequestParam("mcuType") String mcuType)
    {
        return toAjax(busiConferenceCustomButtonService.deleteBusiConferenceCustomButtonByIds(ids, mcuType));
    }
}
