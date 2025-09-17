package com.paradisecloud.fcm.web.controller.mqtt;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiMqtt;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiMqttService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * mqtt配置控制器
 * 
 * @author zyz
 * @date 2021-07-21
 */
@RestController
@RequestMapping("/busi/mqtt")
@Tag(name = "mqtt配置控制器")
public class BusiMqttController extends BaseController
{
    @Autowired
    private IBusiMqttService busiMqttService;

    /**
     * mqtt配置查询列表
     */
    @GetMapping("/list")
    @Operation(summary = "mqtt配置查询列表")
    public RestResponse list(BusiMqtt busiMqtt, HttpServletResponse response) throws UnknownHostException, IOException
    {
        List<ModelBean>  busiMqttBenas = busiMqttService.getMqttConfigurationInfo(response);
        return success(busiMqttBenas);
    }

    /**
     * 导出mqtt配置信息
     */
    @Log(title = "导出mqtt配置信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "导出mqtt配置信息")
    public RestResponse export(BusiMqtt busiMqtt)
    {
        List<BusiMqtt> list = busiMqttService.selectBusiMqttList(busiMqtt);
        ExcelUtil<BusiMqtt> util = new ExcelUtil<BusiMqtt>(BusiMqtt.class);
        return util.exportExcel(list, "mqtt");
    }

    /**
     * 获取mqtt配置详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取mqtt配置详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiMqttService.selectBusiMqttById(id));
    }

    /**
     * 新增mqtt配置信息
     */
    @Log(title = "新增mqtt配置信息", businessType = BusinessType.INSERT)
    @PostMapping("")
    @Operation(summary = "新增mqtt配置信息", description = "新增FMQ")
    public RestResponse addMqtt(@RequestBody BusiMqtt busiMqtt)
    {
        return RestResponse.success(busiMqttService.insertBusiMqtt(busiMqtt));
    }

    /**
     * 修改mqtt配置详细信息
     */
    @Log(title = "修改mqtt配置详细信息", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    @Operation(summary = "修改mqtt配置详细信息", description = "修改FMQ")
    public RestResponse updateMqtt(@RequestBody BusiMqtt busiMqtt, @PathVariable Long id)
    {
    	busiMqtt.setId(id);
    	busiMqttService.updateBusiMqtt(busiMqtt);
        return success(busiMqtt);
    }

    /**S
     * 批量删除mqtt配置详细信息
     */
    @Log(title = "批量删除mqtt配置详细信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除mqtt配置详细信息", description = "删除FMQ")
    public RestResponse removeMqtt(@PathVariable Long[] ids)
    {
        return toAjax(busiMqttService.deleteBusiMqttByIds(ids));
    }
    
    /**
     * 删除mqtt配置详细信息
     */
	@DeleteMapping("/deleteOne/{id}")
	@Operation(summary = "删除mqtt配置详细信息", description = "删除FMQ")
    public RestResponse deleteMqtt(@PathVariable Long id)
    {	
    	busiMqttService.deleteBusiMqttById(id);
        return success("删除mqtt节点成功"+id);
    }
	
	/**
 	* 监测mqtt服务是都启动成功
	*/
	@PostMapping("/restartMqttIsSuccess")
	@Operation(summary = "监测重新启动mqtt是否成功")
	public Boolean mqttServerIsSuccess(@RequestBody BusiMqtt busiMqtt)
	{
	    return busiMqttService.restartMqttListen(busiMqtt.getId());
	}
	
	/**
 	* 查询修改后的节点名字是否重复
	*/
	@PostMapping("/nodeNameIsRepeat")
	@Operation(summary = "查询修改后的节点名字是否重复")
	public Boolean getNodeNameIsRepeat(@RequestBody BusiMqtt busiMqtt)
	{
	    return busiMqttService.nameIsRepeat(busiMqtt);
	}
}
