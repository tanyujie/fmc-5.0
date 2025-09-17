package com.paradisecloud.fcm.web.controller.edu;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.paradisecloud.fcm.dao.model.BusiEduSchoolBuilding;
import com.paradisecloud.fcm.edu.interfaces.IBusiEduSchoolBuildingService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 学校建筑，用于存放教室，教室肯定是归属某个建筑Controller
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
@RestController
@RequestMapping("/busi/eduSchoolBuilding")
@Tag(name = "学校建筑，用于存放教室，教室肯定是归属某个建筑")
public class BusiEduSchoolBuildingController extends BaseController
{
    @Autowired
    private IBusiEduSchoolBuildingService busiEduSchoolBuildingService;

    /**
     * 查询学校建筑，用于存放教室，教室肯定是归属某个建筑列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询学校建筑，用于存放教室，教室肯定是归属某个建筑列表")
    public RestResponse list(BusiEduSchoolBuilding busiEduSchoolBuilding)
    {
        startPage();
        List<BusiEduSchoolBuilding> list = busiEduSchoolBuildingService.selectBusiEduSchoolBuildingList(busiEduSchoolBuilding);
        return getDataTable(list);
    }

    /**
     * 导出学校建筑，用于存放教室，教室肯定是归属某个建筑列表
     */
    @Log(title = "学校建筑，用于存放教室，教室肯定是归属某个建筑", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出学校建筑，用于存放教室，教室肯定是归属某个建筑列表")
    public RestResponse export(BusiEduSchoolBuilding busiEduSchoolBuilding)
    {
        List<BusiEduSchoolBuilding> list = busiEduSchoolBuildingService.selectBusiEduSchoolBuildingList(busiEduSchoolBuilding);
        ExcelUtil<BusiEduSchoolBuilding> util = new ExcelUtil<BusiEduSchoolBuilding>(BusiEduSchoolBuilding.class);
        return util.exportExcel(list, "eduSchoolBuilding");
    }

    /**
     * 获取学校建筑，用于存放教室，教室肯定是归属某个建筑详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取学校建筑，用于存放教室，教室肯定是归属某个建筑详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiEduSchoolBuildingService.selectBusiEduSchoolBuildingById(id));
    }

    /**
     * 新增学校建筑，用于存放教室，教室肯定是归属某个建筑
     */
    @Log(title = "学校建筑，用于存放教室，教室肯定是归属某个建筑", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增学校建筑，用于存放教室，教室肯定是归属某个建筑")
    public RestResponse add(@RequestBody BusiEduSchoolBuilding busiEduSchoolBuilding)
    {
        return toAjax(busiEduSchoolBuildingService.insertBusiEduSchoolBuilding(busiEduSchoolBuilding));
    }

    /**
     * 修改学校建筑，用于存放教室，教室肯定是归属某个建筑
     */
    @Log(title = "学校建筑，用于存放教室，教室肯定是归属某个建筑", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    @Operation(summary = "修改学校建筑，用于存放教室，教室肯定是归属某个建筑")
    public RestResponse edit(@RequestBody BusiEduSchoolBuilding busiEduSchoolBuilding, @PathVariable Long id)
    {
        busiEduSchoolBuilding.setId(id);
        return toAjax(busiEduSchoolBuildingService.updateBusiEduSchoolBuilding(busiEduSchoolBuilding));
    }

    /**
     * 删除学校建筑，用于存放教室，教室肯定是归属某个建筑
     */
    @PreAuthorize("@ss.hasPermi('busi:eduSchoolBuilding:remove')")
    @Log(title = "学校建筑，用于存放教室，教室肯定是归属某个建筑", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除学校建筑，用于存放教室，教室肯定是归属某个建筑")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiEduSchoolBuildingService.deleteBusiEduSchoolBuildingByIds(ids));
    }
}
