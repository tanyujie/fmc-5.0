package com.paradisecloud.fcm.fme.model.cms.callleg;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * call leg 实体类
 *
 * @author zt1994 2019/8/26 11:18
 */
@Getter
@Setter
@ToString
public class CallLeg
{
    
    private String id;
    
    /**
     * call leg 名称
     */
    private String name;
    
    /**
     * 仅用于POST，指定调用分支的地址;这可以是一个SIP URI、一个电话号码或一个用户JID来邀请该用户进行调用。
     */
    private String remoteParty;
    
    /**
     * 原始远程调用分支地址
     */
    private String originalRemoteParty;
    
    /**
     * 本地地址
     */
    private String localAddress;
    
    /**
     * 所属call的id
     */
    private String call;
    
    /**
     * 租户id
     */
    private String tenant;
    
    /**
     * sip | acano
     */
    private String type;
    
    /**
     * lync | avaya | distributionLink | lyncDistribution
     */
    private String subtype;
    
    /**
     * audioVideo | applicationSharing | instantMessaging
     */
    private String lyncSubType;
    
    /**
     * incoming | outgoing
     */
    private String direction;
    
    /**
     * 是否可用移动此call leg
     */
    private Boolean canMove;
    
    /**
     * 如果此call leg是通过移动参与者创建的，则ID指示从参与者移动的原始call leg。
     */
    private String movedCallLeg;
    
    /**
     * 如果此call leg是通过移动参与者创建的，则ID指示从原始call leg移动到其所在的call bridge
     */
    private String movedCallLegCallBridge;
    
    /**
     * call leg 配置信息
     */
    private CallLegConfiguration configuration;
    
    /**
     * call leg 状态信息
     */
    private CallLegStatus status;
    
}
