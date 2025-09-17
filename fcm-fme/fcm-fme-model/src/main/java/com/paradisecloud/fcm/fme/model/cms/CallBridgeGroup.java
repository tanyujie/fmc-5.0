package com.paradisecloud.fcm.fme.model.cms;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 会议桥组
 *
 * @author zt1994 2019/8/28 10:42
 */
@Getter
@Setter
@ToString
public class CallBridgeGroup
{
    
    @JSONField(name = "@id")
    private String id;
    
    /**
     * callBridgeGroup的名称
     */
    private String name;
    
    /**
     * 此callBridgeGroup中的callBridge是否将尝试在组内负载平衡调用。如果在创建(POST)操作中没有提供此参数，则默认为“false”(从2.1版开始)
     */
    private Boolean loadBalancingEnabled;
    
    /**
     * Lync传入coSpaces的调用在callBridgeGroup中是否负载平衡。如果在创建(POST)操作中没有提供此参数，则默认为“false”(从2.1版开始)
     * 注意:调用桥接组目前不支持从/到Lync客户机的调用的负载平衡。
     */
    private Boolean loadBalanceLyncCalls;
    
    /**
     * 来自coSpaces的调用是否应该在组内实现负载平衡。如果在创建中没有提供此参数(POST)操作，默认为“false”(版本2.2)
     */
    private Boolean loadBalanceOutgoingCalls;
    
    /**
     * 思科会议应用程序调用coSpaces是否应该在集团内部实现负载平衡。如果在创建(POST)操作中没有提供此参数，则默认为“true”
     */
    private Boolean loadBalanceUserCalls;
    
    /**
     * 具有记录路由SIP报头的传入呼叫是否应该在组内进行负载平衡。如果在创建(POST)操作中没有提供此参数，则默认为“false”
     */
    private Boolean loadBalanceIndirectCalls;
    
}
