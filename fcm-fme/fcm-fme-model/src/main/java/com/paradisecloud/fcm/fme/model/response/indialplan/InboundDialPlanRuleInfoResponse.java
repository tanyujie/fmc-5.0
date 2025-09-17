package com.paradisecloud.fcm.fme.model.response.indialplan;

import com.paradisecloud.fcm.fme.model.cms.InboundDialPlanRule;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * callProfile 详情响应类
 */
@Getter
@Setter
@ToString
public class InboundDialPlanRuleInfoResponse
{
    
    /**
     * 单个 callProfile
     */
    private InboundDialPlanRule inboundDialPlanRule;
}
