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
import com.paradisecloud.fcm.dao.model.BusiDialPlanRuleOutbound;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiDialPlanRuleOutboundService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 外呼计划Controller
 * 
 * @author lilinhai
 * @date 2022-05-13
 */
@RestController
@RequestMapping("/dialplan/outbound")
@Tag(name = "外呼计划")
public class BusiDialPlanRuleOutboundController extends BaseController
{
    @Autowired
    private IBusiDialPlanRuleOutboundService busiDialPlanRuleOutboundService;
    
    /**
     * 查询外呼计划列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询外呼计划列表")
    public RestResponse list(Long fmeId)
    {
        List<ModelBean> list = busiDialPlanRuleOutboundService.getAllRecords(fmeId);
        return RestResponse.success(list);
    }

    /**
     * 新增外呼计划
     */
    @PostMapping
    @Operation(summary = "新增外呼计划")
    public RestResponse add(@RequestBody BusiDialPlanRuleOutbound busiDialPlanRuleOutbound)
    {
        if (busiDialPlanRuleOutboundService.insertBusiDialPlanRuleOutbound(busiDialPlanRuleOutbound) == 1)
        {
            return RestResponse.success(busiDialPlanRuleOutbound.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 修改外呼计划
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改外呼计划")
    public RestResponse edit(@RequestBody BusiDialPlanRuleOutbound busiDialPlanRuleOutbound, @PathVariable String id)
    {
        busiDialPlanRuleOutbound.getParams().put("id", id);
        if (busiDialPlanRuleOutboundService.updateBusiDialPlanRuleOutbound(busiDialPlanRuleOutbound) == 1)
        {
            return RestResponse.success(busiDialPlanRuleOutbound.getParams());
        }
        else
        {
            return RestResponse.fail();
        }
    }

    /**
     * 删除外呼计划
     */
    @PostMapping("/delete")
	@Operation(summary = "删除外呼计划")
    public RestResponse remove(@RequestBody BusiDialPlanRuleOutbound busiProfileCall)
    {
        return busiDialPlanRuleOutboundService.deleteBusiProfileCallById(busiProfileCall);
    }
}
