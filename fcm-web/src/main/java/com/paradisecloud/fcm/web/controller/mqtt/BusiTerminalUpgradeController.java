package com.paradisecloud.fcm.web.controller.mqtt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.pagehelper.PageInfo;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.common.enumer.AppType;
import com.paradisecloud.fcm.common.utils.AppFileUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.fcm.dao.model.BusiTerminalUpgrade;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiTerminalUpgradeService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;

/**
 * 终端升级Controller
 * 
 * @author zyz
 * @date 2021-10-11
 */
@RestController
@RequestMapping("/busi/upgrade")
@Tag(name = "终端升级")
public class BusiTerminalUpgradeController extends BaseController
{
    @Autowired
    private IBusiTerminalUpgradeService busiTerminalUpgradeService;

    /**
     * 查询终端升级列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询终端升级列表")
    public RestResponse list(BusiTerminalUpgrade busiTerminalUpgrade)
    {
        startPage();
        List<BusiTerminalUpgrade> list = busiTerminalUpgradeService.selectBusiTerminalUpgradeList(busiTerminalUpgrade);
        PaginationData<Object> pd = new PaginationData();
        pd.setTotal((new PageInfo(list)).getTotal());
        for (BusiTerminalUpgrade busiTerminalUpgradeTemp : list) {
            ModelBean modelBean = new ModelBean(busiTerminalUpgradeTemp);
            AppType appType = AppType.convert(busiTerminalUpgradeTemp.getTerminalType());
            if (appType != null) {
                modelBean.put("terminalTypeName", appType.getName());
            }
            pd.addRecord(modelBean);
        }
        return RestResponse.success(0L, "查询成功", pd);
    }

    /**
     * 导出终端升级列表
     */
    @Log(title = "终端升级", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出终端升级列表")
    public RestResponse export(BusiTerminalUpgrade busiTerminalUpgrade)
    {
        List<BusiTerminalUpgrade> list = busiTerminalUpgradeService.selectBusiTerminalUpgradeList(busiTerminalUpgrade);
        ExcelUtil<BusiTerminalUpgrade> util = new ExcelUtil<BusiTerminalUpgrade>(BusiTerminalUpgrade.class);
        return util.exportExcel(list, "upgrade");
    }

    /**
     * 获取终端升级详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取终端升级详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiTerminalUpgradeService.selectBusiTerminalUpgradeById(id));
    }

    /**
     * 新增终端升级
     * @param uploadFile
     * @param terminalType
     * @param versionNum
     * @param versionName
     * @param versionDescription
     * @return
     */
    @Log(title = "终端升级", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增终端升级", description = "新增终端升级")
    public RestResponse add(@RequestParam(value = "uploadFile", required = true) MultipartFile uploadFile,
                            @RequestParam(value = "terminalType", required = true) String terminalType,
                            @RequestParam(value = "versionNum", required = true) String versionNum,
                            @RequestParam(value = "versionName", required = true) String versionName,
                            @RequestParam(value = "versionDescription", required = false) String versionDescription)
    {
        if (uploadFile != null && !uploadFile.isEmpty()) {
            // 保存
            try {
                String url = AppFileUtil.saveFile(uploadFile, terminalType, versionName);
                if (StringUtils.isNotEmpty(url)) {
                    BusiTerminalUpgrade busiTerminalUpgrade = new BusiTerminalUpgrade();
                    busiTerminalUpgrade.setTerminalType(terminalType);
                    busiTerminalUpgrade.setVersionNum(versionNum);
                    busiTerminalUpgrade.setVersionName(versionName);
                    busiTerminalUpgrade.setServerUrl(url);
                    busiTerminalUpgrade.setVersionDescription(versionDescription);
                    int row = busiTerminalUpgradeService.insertBusiTerminalUpgrade(busiTerminalUpgrade);
                    return toAjax(row);
                }
            } catch (Exception e) {
                if (e instanceof CustomException) {
                    return RestResponse.fail(e.getMessage());
                } else {
                    if (StringUtils.isNotEmpty(e.getMessage())) {
                        return RestResponse.fail(e.getMessage());
                    } else {
                        return RestResponse.fail("新增终端升级错误");
                    }
                }
            }
        }
        return RestResponse.fail("新增终端升级错误");
    }

    /**
     * 修改终端升级
     * @param uploadFile
     * @param id
     * @param terminalType
     * @param versionNum
     * @param versionName
     * @param versionDescription
     * @return
     */
    @Log(title = "终端升级", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改终端升级", description = "修改终端升级")
    public RestResponse edit(@RequestParam(value = "uploadFile", required = true) MultipartFile uploadFile,
                             @RequestParam(value = "id", required = true) Long id,
                             @RequestParam(value = "terminalType", required = true) String terminalType,
                             @RequestParam(value = "versionNum", required = true) String versionNum,
                             @RequestParam(value = "versionName", required = true) String versionName,
                             @RequestParam(value = "versionDescription", required = true) String versionDescription)
    {
        if (uploadFile != null && !uploadFile.isEmpty()) {
            // 保存
            try {
                String url = AppFileUtil.saveFile(uploadFile, terminalType, versionName);
                if (StringUtils.isNotEmpty(url)) {
                    BusiTerminalUpgrade busiTerminalUpgrade = new BusiTerminalUpgrade();
                    busiTerminalUpgrade.setId(id);
                    busiTerminalUpgrade.setTerminalType(terminalType);
                    busiTerminalUpgrade.setVersionNum(versionNum);
                    busiTerminalUpgrade.setVersionName(versionName);
                    busiTerminalUpgrade.setServerUrl(url);
                    busiTerminalUpgrade.setVersionDescription(versionDescription);
                    int row = busiTerminalUpgradeService.updateBusiTerminalUpgrade(busiTerminalUpgrade);
                    return toAjax(row);
                }
            } catch (Exception e) {
                if (e instanceof CustomException) {
                    return RestResponse.fail(e.getMessage());
                } else {
                    if (StringUtils.isNotEmpty(e.getMessage())) {
                        return RestResponse.fail(e.getMessage());
                    } else {
                        return RestResponse.fail("新增终端升级错误");
                    }
                }
            }
        }
        return RestResponse.fail("修改终端升级错误");
    }

    /**
     * 删除终端升级
     */
    @Log(title = "终端升级", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除终端升级", description = "删除终端升级")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiTerminalUpgradeService.deleteBusiTerminalUpgradeByIds(ids));
    }
    /**
     * 终端App文件的上传
     *
     * @param uploadFile
     * @param terminalType
     * @param versionCode
     * @param versionName
     * @throws Exception
     */
    @PostMapping("/appFilesUpload")
    public RestResponse appFilesUpload(@RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile, @RequestParam(value = "terminalType", required = false) String terminalType, @RequestParam(value = "versionCode", required = false) String versionCode, @RequestParam(value = "versionName", required = false) String versionName) {
        if (uploadFile != null && !uploadFile.isEmpty()) {
            if (terminalType != null) {
                if (versionName == null || versionName.isEmpty()) {
                    return RestResponse.fail("版本名不能为空！");
                }
            }
            // 保存
            try {
                String url = AppFileUtil.saveFile(uploadFile, terminalType, versionName);
                return RestResponse.success(url);
            } catch (Exception e) {
                if (e instanceof CustomException) {
                    return RestResponse.fail(e.getMessage());
                } else {
                    return RestResponse.fail("升级文件上传错误");
                }
            }
        }
        return RestResponse.fail();
    }

    /**
     * 查询终端App类型列表
     */
    @GetMapping("/terminalTypeList")
    @Operation(summary = "查询终端类型列表")
    public RestResponse getAppTypeList() {
        List<ModelBean> list = new ArrayList<>();
        for (AppType appType : AppType.values()) {
            ModelBean modelBean = new ModelBean();
            modelBean.put("terminalType", appType.getCode());
            modelBean.put("terminalTypeName", appType.getName());
            list.add(modelBean);
        }
        return RestResponse.success(list);
    }
}
