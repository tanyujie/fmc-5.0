package com.paradisecloud.fcm.fme.model.cms;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 外呼拨号规则
 *
 * @Author yl
 * @Date 2020/3/6 14:19
 */
@Getter
@Setter
@ToString
public class OutboundDialPlanRule
{
    
    /**
     * 唯一id
     */
    private String id;
    
    /**
     * 应用拨号规则的域
     */
    private String domain;
    
    /**
     * 优先级(越高越优先)
     */
    private Integer priority;
    
    /**
     * 使用Lync时设置
     */
    private String localContactDomain;
    
    /**
     * 使用外呼拨号时设置
     */
    private String localFromDomain;
    
    /**
     * 地址（IP或者主机名）
     */
    private String sipProxy;
    
    /**
     * sip 标准的sip调用 lync 具有特定行为的lync调用 avaya 具有特定行为的avaya调用
     */
    private String trunkType;
    
    /**
     * 故障时是否使用另一种拨号方式 stop 停止使用 continue 继续尝试
     */
    private String failureAction;
    
    /**
     * sip控制加密 auto 优先允许加密通信，失败时也允许未加密通信 encrypted 只允许加密sip通信 unencrypted 只允许未加密通信
     */
    private String sipControlEncryption;
    
    /**
     * 适用范围 global 所有适用外呼拨号规则的domain callBridge 仅对ID在callBridge参数中给定的单个指定呼叫桥有效 callBridgeGroup
     * 仅对ID在callBridgeGroup参数中给定的单个指定呼叫桥接组有效
     */
    private String scope;
    
    /**
     * callBridge的id
     */
    private String callBridge;
    
    /**
     * callBridgeGroup的id
     */
    private String callBridgeGroup;
    
    /**
     * 租户的id 如果指定租户，则仅用于外呼，否则适用任何call
     */
    private String tenant;
    
    /**
     * default:正常转到媒体路由 traversal：转向服务器
     */
    private String callRouting;
    
}
