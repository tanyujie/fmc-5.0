package com.paradisecloud.fcm.web.controller.mqtt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiSipAccount;
import com.paradisecloud.fcm.terminal.fs.interfaces.IBusiSipAccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * sip账号信息Controller
 * 
 * @author zyz
 * @date 2021-09-24
 */
@RestController
@RequestMapping("/busi/sipAccount")
@Tag(name = "sip账号信息")
public class BusiSipAccountController extends BaseController
{
    @Autowired
    private IBusiSipAccountService busiSipAccountService;

    /**
     * 查询sip账号信息列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询sip账号信息列表")
    public RestResponse list(BusiSipAccount busiSipAccount)
    {
        startPage();
        List<ModelBean> list = busiSipAccountService.selectBusiSipAccountList(busiSipAccount);
        return getDataTable(list);
    }

    /**
     * 获取sip账号信息详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取sip账号信息详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiSipAccountService.selectBusiSipAccountById(id));
    }

    /**
     * 新增sip账号信息
     */
//    @Log(title = "sip账号信息", businessType = BusinessType.INSERT)
//    @PostMapping
//    @Operation(summary = "新增sip账号信息")
//    public RestResponse add(@RequestBody BusiSipAccount busiSipAccount)
//    {
//        return toAjax(busiSipAccountService.insertBusiSipAccount(busiSipAccount));
//    }

    /**
     * 修改sip账号信息
     */
    @Log(title = "sip账号信息", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改sip账号信息")
    public RestResponse edit(@RequestBody BusiSipAccount busiSipAccount)
    {
        return toAjax(busiSipAccountService.updateBusiSipAccount(busiSipAccount));
    }

    /**
     * 删除sip账号信息
     */
    @Log(title = "sip账号信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除sip账号信息")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiSipAccountService.deleteBusiSipAccountByIds(ids));
    }
}
