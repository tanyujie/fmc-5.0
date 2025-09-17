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
import com.paradisecloud.fcm.dao.model.BusiMqttDept;
import com.paradisecloud.fcm.mqtt.interfaces.IBusiMqttDeptService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * mqtt资源分配关联租户控制器
 * 
 * @author zyz
 * @date 2021-07-21
 */
@RestController
@RequestMapping("/busi/mqttDept")
@Tag(name = "mqtt资源分配关联租户控制器，一个mqtt节点或者mqtt集群可以分配给多个租户")
public class BusiMqttDeptController extends BaseController
{
    @Autowired
    private IBusiMqttDeptService busiMqttDeptService;

    /**
            * 查询mqtt资源分配关联租户列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询mqtt资源分配关联租户列表")
    public RestResponse list(BusiMqttDept busiMqttDept)
    {
        List<ModelBean> modelBeans = busiMqttDeptService.selectBusiMqttDeptList(busiMqttDept);
        return RestResponse.success(modelBeans);
    }

    /**
     * 获取mqtt资源分配关联租户详细信息
     */
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取mqtt资源分配关联租户详细信息")
    public RestResponse getInfo(@PathVariable("id") Long id)
    {
        return RestResponse.success(busiMqttDeptService.selectBusiMqttDeptById(id));
    }

    /**
     * 新增mqtt资源分配关联租户信息
     */
    @Log(title = "新增mqtt资源分配关联租户信息", businessType = BusinessType.INSERT)
    @PostMapping("")
    @Operation(summary = "新增mqtt资源分配关联租户信息", description = "绑定FMQ")
    public RestResponse add(@RequestBody BusiMqttDept busiMqttDept)
    {
        return toAjax(busiMqttDeptService.insertBusiMqttDept(busiMqttDept));
    }

    /**
     * 修改mqtt资源分配关联租户信息
     */
    @Log(title = " 修改mqtt资源分配关联租户信息", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    @Operation(summary = "修改mqtt资源分配关联租户信息", description = "修改FMQ绑定")
    public RestResponse edit(@RequestBody BusiMqttDept busiMqttDept, @PathVariable Long id)
    {
    	busiMqttDept.setId(id);
        return toAjax(busiMqttDeptService.updateBusiMqttDept(busiMqttDept));
    }

    /**
     * 删除mqtt资源分配关联租户信息
     */
    @Log(title = "删除mqtt资源分配关联租户信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	@Operation(summary = "删除mqtt资源分配关联租户信息", description = "解绑FMQ")
    public RestResponse remove(@PathVariable Long[] ids)
    {
        return toAjax(busiMqttDeptService.deleteBusiMqttDeptByIds(ids));
    }
    
    /**
	 * 删除具体mqtt资源分配关联租户信息
	 */
	@Log(title = "删除具体mqtt资源分配关联租户信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/delete/{id}")
	@Operation(summary = "删除具体mqtt资源分配关联租户信息", description = "解绑FMQ")
	public RestResponse deleteMqttDept(@PathVariable Long id)
	{
	return toAjax(busiMqttDeptService.deleteBusiMqttDeptById(id));
	}
}
