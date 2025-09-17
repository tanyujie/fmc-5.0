package com.paradisecloud.fcm.web.controller.mqtt;

import java.util.List;

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
import com.paradisecloud.fcm.dao.model.BusiMqttCluster;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiMqttClusterService;
import com.paradisecloud.system.model.ExcelUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * mqtt集群控制器
 * @author zyz
 * @date 2021-07-21
 */
@RestController
@RequestMapping("/busi/mqttCluster")
@Tag(name = "mqtt集群控制器")
public class BusiMqttClusterController extends BaseController
{
    @Autowired
    private IBusiMqttClusterService busiMqttClusterService;

    /**
     * mqtt集群详情
     */
    @GetMapping("/list")
    @Operation(summary = "mqtt集群详情")
    public RestResponse list(BusiMqttCluster busiMqttCluster)
    {
        List<BusiMqttCluster> list = busiMqttClusterService.selectBusiMqttClusterList(busiMqttCluster);
        return success(list);
    }

    /**
             * 集群信息导出
     */
    @Log(title = "集群信息导出", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @Operation(summary = "集群信息导出")
    public RestResponse export(BusiMqttCluster busiMqttCluster)
    {
        List<BusiMqttCluster> list = busiMqttClusterService.selectBusiMqttClusterList(busiMqttCluster);
        ExcelUtil<BusiMqttCluster> util = new ExcelUtil<BusiMqttCluster>(BusiMqttCluster.class);
        return util.exportExcel(list, "cluster");
    }

    /**
             * 获取具体mqtt集群的信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取具体mqtt集群的信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiMqttClusterService.selectBusiMqttClusterById(id));
    }

    /**
             * 新增mqtt集群信息
     */
    @Log(title = "新增mqtt集群信息", businessType = BusinessType.INSERT)
    @PostMapping("")
    @Operation(summary = "新增mqtt集群信息", description = "新增FMQ集群")
    public RestResponse add(@RequestBody BusiMqttCluster busiMqttCluster)
    {
    	busiMqttClusterService.insertBusiMqttCluster(busiMqttCluster);
        return success(busiMqttCluster);
    }

    /**
             * 修改mqtt集群信息
     */
    @Log(title = "修改mqtt集群信息", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    @Operation(summary = "修改mqtt集群信息", description = "修改FMQ集群")
    public RestResponse updateBusiMqttCluster(@RequestBody BusiMqttCluster busiMqttCluster, @PathVariable Long id)
    {	
    	busiMqttCluster.setId(id);
    	busiMqttClusterService.updateBusiMqttCluster(busiMqttCluster);
        return success(busiMqttCluster);
    }

    /**
            * 删除mqtt集群信息
     */
    @Log(title = "删除mqtt集群信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除mqtt集群信息", description = "删除FMQ集群")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiMqttClusterService.deleteBusiMqttClusterByIds(ids));
    }
    
    /**
	   * 删除具体mqtt集群信息
	 */
	@Log(title = "删除具体mqtt集群信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/deleteOne/{id}")
	@Operation(summary = "删除具体mqtt集群信息", description = "删除FMQ集群消息")
	public RestResponse deleteMqttCluster(@PathVariable Long id)
	{
		busiMqttClusterService.deleteBusiMqttClusterById(id);
		return success("删除成功!" + id);
	}
	
	@Operation(summary = "获取所有Mqtt集群信息")
    @GetMapping("/getAllMqttClusterList")
    public RestResponse getAllMqttClusterList()
    {
        List<ModelBean> mn = busiMqttClusterService.getAllMqttCluster();
        return success(mn);
    }
}
