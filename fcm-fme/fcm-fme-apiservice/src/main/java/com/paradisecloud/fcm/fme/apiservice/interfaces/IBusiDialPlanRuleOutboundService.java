package com.paradisecloud.fcm.fme.apiservice.interfaces;

import java.util.List;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.fcm.dao.model.BusiDialPlanRuleOutbound;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.OutboundDialPlanRule;

/**
 * 外呼计划Service接口
 * 
 * @author lilinhai
 * @date 2022-05-13
 */
public interface IBusiDialPlanRuleOutboundService 
{
    
    public List<ModelBean> getAllRecords(Long deptId);

    /**
     * 新增外呼计划
     * 
     * @param busiDialPlanRuleOutbound 外呼计划
     * @return 结果
     */
    public int insertBusiDialPlanRuleOutbound(BusiDialPlanRuleOutbound busiDialPlanRuleOutbound);

    /**
     * 修改外呼计划
     * 
     * @param busiDialPlanRuleOutbound 外呼计划
     * @return 结果
     */
    public int updateBusiDialPlanRuleOutbound(BusiDialPlanRuleOutbound busiDialPlanRuleOutbound);

    public RestResponse deleteBusiProfileCallById(BusiDialPlanRuleOutbound busiProfileCall);
    
    void syncAllPlan(FmeBridge fmeBridge, OutboundDialPlanRuleProcessor outboundDialPlanRuleProcessor);
    public static interface OutboundDialPlanRuleProcessor
    {
        void process(OutboundDialPlanRule outboundDialPlanRule);
    }
}
