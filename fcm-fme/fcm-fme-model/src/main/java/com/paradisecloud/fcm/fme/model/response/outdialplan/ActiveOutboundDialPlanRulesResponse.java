package com.paradisecloud.fcm.fme.model.response.outdialplan;

import java.util.List;

import com.paradisecloud.fcm.fme.model.cms.OutboundDialPlanRule;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个callProfile
 */
@Getter
@Setter
@ToString
public class ActiveOutboundDialPlanRulesResponse
{
    
    /**
     * callLegProfile 总数
     */
    private Integer total;
    
    /**
     * 单个callLegProfiles
     */
    private List<OutboundDialPlanRule> outboundDialPlanRule;
}
