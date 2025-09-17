package com.paradisecloud.fcm.web.controller.transcodec;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiTransServer;
import com.paradisecloud.fcm.service.interfaces.IBusiTransServerService;
import org.apache.commons.collections4.CollectionUtils;
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
 * 转流服务器Controller
 *
 * @author lilinhai
 * @date 2024-03-29
 */
@RestController
@RequestMapping("/system/transServer")
@Tag(name = "转流服务器")
public class BusiTransServerController extends BaseController
{
    @Resource
    private IBusiTransServerService busiTransServerService;

    /**
     * 查询转流服务器列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询转流服务器列表")
    public RestResponse list(BusiTransServer busiTransServer)
    {
        startPage();
        List<BusiTransServer> list = busiTransServerService.selectBusiTransServerList(busiTransServer);
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(item->{
                item.setPassword("******");
                item.setUserName("******");
            });
        }
        return getDataTable(list);
    }



    /**
     * 获取转流服务器详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取转流服务器详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiTransServerService.selectBusiTransServerById(id));
    }

    /**
     * 新增转流服务器
     */
    @Log(title = "转流服务器", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增转流服务器")
    public RestResponse add(@RequestBody BusiTransServer busiTransServer)
    {
        return toAjax(busiTransServerService.insertBusiTransServer(busiTransServer));
    }

    /**
     * 修改转流服务器
     */
    @Log(title = "转流服务器", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改转流服务器")
    public RestResponse edit(@RequestBody BusiTransServer busiTransServer)
    {
        return toAjax(busiTransServerService.updateBusiTransServer(busiTransServer));
    }

    /**
     * 删除转流服务器
     */
    @Log(title = "转流服务器", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除转流服务器")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiTransServerService.deleteBusiTransServerByIds(ids));
    }
}
