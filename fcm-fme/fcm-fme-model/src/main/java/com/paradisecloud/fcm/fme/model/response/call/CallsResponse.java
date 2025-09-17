package com.paradisecloud.fcm.fme.model.response.call;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个 calls 响应
 *
 * @author zt1994 2019/8/23 15:23
 */
@Getter
@Setter
@ToString
public class CallsResponse
{
    
    /**
     * 多个 calls 响应
     */
    private ActiveCallsResponse calls;
}
