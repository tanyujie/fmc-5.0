package com.paradisecloud.fcm.web.controller.smartRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.paradisecloud.fcm.common.enumer.LotDeviceType;
import com.paradisecloud.fcm.common.enumer.LotModel;
import com.paradisecloud.fcm.common.enumer.LotType;
import com.paradisecloud.fcm.common.enumer.PowerSequencerModel;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomLot;
import com.paradisecloud.fcm.dao.model.vo.BusiSmartRoomLotVo;
import com.paradisecloud.fcm.smartroom.service.interfaces.IBusiSmartRoomLotService;
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
 * 智慧办公物联网关Controller
 *
 * @author lilinhai
 * @date 2024-03-06
 */
@RestController
@RequestMapping("/busi/lot")
@Tag(name = "智慧办公物联网关")
public class BusiSmartRoomLotController extends BaseController
{
    @Resource
    private IBusiSmartRoomLotService busiSmartRoomLotService;

    /**
     * 查询物联网类型列表
     *
     * @return
     */
    @GetMapping("/getLotTypeList")
    @Operation(summary = "查询物联网类型列表")
    public RestResponse getLotTypeList() {
        List<Map<String, Object>> lotTypeList = LotType.getLotTypeList();
        return RestResponse.success(lotTypeList);
    }

    /**
     * 查询物联网型号列表
     *
     * @return
     */
    @GetMapping("/getLotModelList")
    @Operation(summary = "查询物联网型号列表")
    public RestResponse getLotModelList(Integer lotType) {
        List<Map<String, Object>> lotModelList = LotModel.getLotModelList(lotType);
        return RestResponse.success(lotModelList);
    }

    /**
     * 查询物联网设备类型列表
     *
     * @return
     */
    @GetMapping("/getLotDeviceTypeList")
    @Operation(summary = "查询物联网设备类型列表")
    public RestResponse getLotDeviceTypeList() {
        List<Map<String, Object>> lotDeviceTypeList = LotDeviceType.getLotDeviceTypeList();
        return RestResponse.success(lotDeviceTypeList);
    }

    /**
     * 查询物联网设备型号列表
     *
     * @return
     */
    @GetMapping("/getLotDeviceModelList")
    @Operation(summary = "查询物联网设备型号列表")
    public RestResponse getLotDeviceModelList(Integer lotType, Integer lotDeviceType) {
        List<Map<String, Object>> lotDeviceModelList;
        if (LotDeviceType.POWER_SEQUENCER.getCode() == lotDeviceType) {
            lotDeviceModelList = PowerSequencerModel.getPowerSequencerModelList(lotType);
        } else {
            lotDeviceModelList = new ArrayList<>();
        }
        return RestResponse.success(lotDeviceModelList);
    }

    /**
     * 查询智慧办公物联网关列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询智慧办公物联网关列表")
    public RestResponse list(BusiSmartRoomLotVo busiSmartRoomLot)
    {
        startPage();
        List<BusiSmartRoomLot> list = busiSmartRoomLotService.selectBusiSmartRoomLotList(busiSmartRoomLot);
        return getDataTable(list);
    }

    /**
     * 获取智慧办公物联网关详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取智慧办公物联网关详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiSmartRoomLotService.selectBusiSmartRoomLotById(id));
    }

    /**
     * 新增智慧办公物联网关
     */
    @PreAuthorize("@ss.hasPermi('busi:lot:add')")
    @Log(title = "智慧办公物联网关", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增智慧办公物联网关")
    public RestResponse add(@RequestBody BusiSmartRoomLot busiSmartRoomLot)
    {
        return toAjax(busiSmartRoomLotService.insertBusiSmartRoomLot(busiSmartRoomLot));
    }

    /**
     * 修改智慧办公物联网关
     */
    @PreAuthorize("@ss.hasPermi('busi:lot:edit')")
    @Log(title = "智慧办公物联网关", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改智慧办公物联网关")
    public RestResponse edit(@RequestBody BusiSmartRoomLot busiSmartRoomLot)
    {
        return toAjax(busiSmartRoomLotService.updateBusiSmartRoomLot(busiSmartRoomLot));
    }

    /**
     * 删除智慧办公物联网关
     */
    @PreAuthorize("@ss.hasPermi('busi:lot:remove')")
    @Log(title = "智慧办公物联网关", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除智慧办公物联网关")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiSmartRoomLotService.deleteBusiSmartRoomLotByIds(ids));
    }
}
