package com.paradisecloud.fcm.web.controller.fme;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiDialPlanRuleInbound;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiDialPlanRuleInboundService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 内呼计划Controller
 * 
 * @author lilinhai
 * @date 2022-05-13
 */
@RestController
@RequestMapping("/dialplan/inbound")
@Tag(name = "内呼计划")
public class BusiDialPlanRuleInboundController extends BaseController
{
    @Autowired
    private IBusiDialPlanRuleInboundService busiDialPlanRuleInboundService;
    
    /**
     * 查询内呼计划列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询内呼计划列表")
    public RestResponse list(Long fmeId)
    {
        List<ModelBean> list = busiDialPlanRuleInboundService.getAllRecords(fmeId);
        return RestResponse.success(list);
    }

    /**
     * 新增内呼计划
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改内呼计划")
    public RestResponse edit(@RequestBody BusiDialPlanRuleInbound busiDialPlanRuleInbound, @PathVariable String id)
    {
        busiDialPlanRuleInbound.getParams().put("id", id);
        if (busiDialPlanRuleInboundService.updateBusiDialPlanRuleInbound(busiDialPlanRuleInbound) == 1)
        {
            return RestResponse.success(busiDialPlanRuleInbound.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 新增内呼计划
     */
    @PostMapping
    @Operation(summary = "新增内呼计划")
    public RestResponse add(@RequestBody BusiDialPlanRuleInbound busiDialPlanRuleInbound)
    {
        if (busiDialPlanRuleInboundService.insertBusiDialPlanRuleInbound(busiDialPlanRuleInbound) == 1)
        {
            return RestResponse.success(busiDialPlanRuleInbound.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 删除内呼计划
     */
    @PostMapping("/delete")
	@Operation(summary = "删除内呼计划")
    public RestResponse remove(@RequestBody BusiDialPlanRuleInbound busiProfileCall)
    {
        return busiDialPlanRuleInboundService.deleteBusiProfileCallById(busiProfileCall);
    }
}
