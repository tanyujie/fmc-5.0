package com.paradisecloud.fcm.fme.model.response.call;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个 call 响应
 *
 * @author zt1994 2019/8/23 15:23
 */
@Getter
@Setter
@ToString
public class CallsOfOneResponse
{
    
    /**
     * 单个 call
     */
    private ActiveCallsOfOneResponse calls;
}
