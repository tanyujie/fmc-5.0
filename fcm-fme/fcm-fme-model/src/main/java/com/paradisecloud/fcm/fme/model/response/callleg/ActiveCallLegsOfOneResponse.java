package com.paradisecloud.fcm.fme.model.response.callleg;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个call leg响应
 *
 * @author zt1994 2019/8/26 11:15
 */
@Getter
@Setter
@ToString
public class ActiveCallLegsOfOneResponse
{
    
    /**
     * 总数
     */
    @JSONField(name = "@total")
    private Integer total;
    
    /**
     * call leg详情
     */
    private CallLeg callLeg;
    
}
