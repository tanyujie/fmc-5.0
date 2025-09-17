package com.paradisecloud.fcm.web.controller.packet;

import java.util.List;

import com.paradisecloud.fcm.dao.model.BusiPacket;
import com.paradisecloud.fcm.service.interfaces.IBusiPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 抓包服务器Controller
 * 
 * @author lilinhai
 * @date 2024-09-04
 */
@RestController
@RequestMapping("/busi/packet")
@Tag(name = "抓包服务器")
public class BusiPacketController extends BaseController
{
    @Autowired
    private IBusiPacketService busiPacketService;

    /**
     * 查询抓包服务器列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询抓包服务器列表")
    public RestResponse list(BusiPacket busiPacket)
    {
        startPage();
        List<BusiPacket> list = busiPacketService.selectBusiPacketList(busiPacket);
        return getDataTable(list);
    }


    /**
     * 获取抓包服务器详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取抓包服务器详细信息")
    public RestResponse getInfo(@PathVariable("id") Integer id)
    {
        return RestResponse.success(busiPacketService.selectBusiPacketById(id));
    }

    /**
     * 新增抓包服务器
     */
    @PostMapping
    @Operation(summary = "新增抓包服务器")
    public RestResponse add(@Validated @RequestBody BusiPacket busiPacket)
    {
        return toAjax(busiPacketService.insertBusiPacket(busiPacket));
    }

    /**
     * 修改抓包服务器
     */
    @PutMapping
    @Operation(summary = "修改抓包服务器")
    public RestResponse edit(@RequestBody BusiPacket busiPacket)
    {
        return toAjax(busiPacketService.updateBusiPacket(busiPacket));
    }

    /**
     * 删除抓包服务器
     */
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除抓包服务器")
    public RestResponse remove(@PathVariable Integer[] ids)
    {
        return toAjax(busiPacketService.deleteBusiPacketByIds(ids));
    }
}
