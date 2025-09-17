package com.paradisecloud.fcm.fme.model.cms;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 会议桥
 *
 * @author zt1994 2019/8/28 9:55
 */
@Getter
@Setter
@ToString
public class CallBridge
{
    
    private String id;
    
    /**
     * 名称
     */
    private String name;
    
    /**
     * 地址-可以到达集群中此调用桥的地址
     */
    private String address;
    
    /**
     * 用于与此群集调用桥建立对等链接的SIP域
     */
    private String sipDomain;
    
    /**
     * 如果指定，请将此调用桥与提供的调用桥组关联
     */
    private String callBridgeGroup;
}
