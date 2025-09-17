package com.paradisecloud.fcm.fme.model.response.call;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.fme.model.cms.Call;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个 call 响应
 *
 * @author zt1994 2019/8/23 15:24
 */
@Getter
@Setter
@ToString
public class ActiveCallsOfOneResponse
{
    
    /**
     * 总数
     */
    @JSONField(name = "@total")
    private Integer total;
    
    /**
     * 单个 call
     */
    private Call call;
}
