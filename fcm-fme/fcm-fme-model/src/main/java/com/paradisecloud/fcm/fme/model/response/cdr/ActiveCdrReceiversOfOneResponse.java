package com.paradisecloud.fcm.fme.model.response.cdr;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.fme.model.cms.CdrReceiver;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个callProfile
 *
 */
@Getter
@Setter
@ToString
public class ActiveCdrReceiversOfOneResponse
{
    
    /**
     * callLegProfile 总数
     */
    @JSONField(name = "@total")
    private Integer total;
    
    /**
     * 单个callLegProfiles
     */
    private CdrReceiver cdrReceiver;
}
