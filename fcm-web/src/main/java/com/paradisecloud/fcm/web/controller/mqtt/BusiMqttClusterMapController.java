package com.paradisecloud.fcm.web.controller.mqtt;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
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
import com.paradisecloud.fcm.dao.model.BusiMqttClusterMap;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiMqttClusterMapService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * mqtt集群的映射控制器
 * 
 * @author zyz
 * @date 2021-07-21
 */
@RestController
@RequestMapping("/busi/mqttClusterMap")
@Tag(name = "mqtt集群的映射控制器")
public class BusiMqttClusterMapController extends BaseController
{
    @Autowired
    private IBusiMqttClusterMapService busiMqttClusterMapService;

    /**
     *mqtt集群中间映射表列表 
     */
    @GetMapping("/list")
    @Operation(summary = "mqtt集群中间映射表列表 ")
    public RestResponse list(BusiMqttClusterMap busiMqttClusterMap, HttpServletRequest request, HttpServletResponse response)
    {
        List<ModelBean> mlb = busiMqttClusterMapService.selectBusiMqttClusterMapList(busiMqttClusterMap, request, response);
        return success(mlb);
    }

    /**
     * 获取具体的mqtt集群关联信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取具体的mqtt集群关联信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiMqttClusterMapService.selectBusiMqttClusterMapById(id));
    }

    /**
             * 新增mqtt集群关联信息
     */
    @Log(title = "新增mqtt集群关联信息", businessType = BusinessType.INSERT)
    @PostMapping("")
    @Operation(summary = "新增mqtt集群关联信息", description = "新增集群")
    public RestResponse add(@RequestBody BusiMqttClusterMap busiMqttClusterMap)
    {
        return toAjax(busiMqttClusterMapService.insertBusiMqttClusterMap(busiMqttClusterMap));
    }

    /**
             * 修改mqtt集群关联信息
     */
    @Log(title = "修改mqtt集群关联信息", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    @Operation(summary = "修改mqtt集群关联信息", description = "修改集群")
    public RestResponse updateClusterMap(@RequestBody BusiMqttClusterMap busiMqttClusterMap, @PathVariable Long id)
    {	
    	busiMqttClusterMap.setId(id);
        return toAjax(busiMqttClusterMapService.updateBusiMqttClusterMap(busiMqttClusterMap));
    }

    /**
             * 删除mqtt集群关联信息
     */
    @Log(title = "删除mqtt集群关联信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除mqtt集群关联信息", description = "删除集群")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiMqttClusterMapService.deleteBusiMqttClusterMapByIds(ids));
    }
    
    /**
	     * 删除具体mqtt集群关联信息
	*/
	@Log(title = "删除具体mqtt集群关联信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/deleteOne/{id}")
	@Operation(summary = "删除具体mqtt集群关联信息", description = "删除集群")
	public RestResponse deleteBusiMqttCluster(@PathVariable Long id)
	{
		return toAjax(busiMqttClusterMapService.deleteBusiMqttClusterMapById(id));
	}
}
