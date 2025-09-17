package com.paradisecloud.fcm.fme.apiservice.interfaces;

import java.util.List;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiDialPlanRuleInbound;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.InboundDialPlanRule;

/**
 * 内呼计划Service接口
 * 
 * @author lilinhai
 * @date 2022-05-13
 */
public interface IBusiDialPlanRuleInboundService 
{
    
    List<ModelBean> getAllRecords(Long deptId);

    /**
     * 新增内呼计划
     * 
     * @param busiDialPlanRuleInbound 内呼计划
     * @return 结果
     */
    public int insertBusiDialPlanRuleInbound(BusiDialPlanRuleInbound busiDialPlanRuleInbound);

    /**
     * 修改内呼计划
     * 
     * @param busiDialPlanRuleInbound 内呼计划
     * @return 结果
     */
    public int updateBusiDialPlanRuleInbound(BusiDialPlanRuleInbound busiDialPlanRuleInbound);

    RestResponse deleteBusiProfileCallById(BusiDialPlanRuleInbound busiProfileCall);
    
    void syncAllPlan(FmeBridge fmeBridge, InboundDialPlanRuleProcessor inboundDialPlanRuleProcessor);
    public static interface InboundDialPlanRuleProcessor
    {
        void process(InboundDialPlanRule inboundDialPlanRule);
    }
}
