package com.paradisecloud.fcm.fme.model.response.callbridge;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个call leg 响应类
 *
 * @author zt1994 2019/8/26 11:13
 */
@Getter
@Setter
@ToString
public class CallBridgesResponse
{
    
    /**
     * 多个call leg
     */
    private ActiveCallBridgesResponse callBridges;
}
