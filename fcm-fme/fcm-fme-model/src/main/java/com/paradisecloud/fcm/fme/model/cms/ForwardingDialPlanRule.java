package com.paradisecloud.fcm.fme.model.cms;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 转移呼叫规则
 *
 * @Author yl
 * @Date 2020/3/9 15:01
 */
@Getter
@Setter
@ToString
public class ForwardingDialPlanRule
{
    
    /**
     * 唯一id
     */
    @JSONField(name = "@id")
    private String id;
    
    /**
     * 要匹配的域，以便应用拨号计划规则。必须是一个完整的域名(例如:“example.com”)或“通配符”(例如:exa* . com)。
     * 在域匹配的任何部分都允许通配符模式，但不要使用“matchPattern=*”作为全部匹配，否则您会创建call loops
     */
    private String matchPattern;
    
    /**
     * 使用此规则转发的调用将具有其目标域重写为这个值
     */
    private String destinationDomain;
    
    /**
     * forward 匹配的调用分支将变为点对点呼叫新的目的地 reject 设置incoming call leg 为拒绝
     */
    private String action;
    
    /**
     * regenerate 将呼入呼叫转发到新的目标地址时,生成新id preserve 将呼入呼叫转发到新的目标地址时,保留原id 默认为regenerate
     */
    private String callerIdMode;
    
    /**
     * 优先级(越高越优先)
     */
    private Integer priority;
    
    /**
     * 租户的id 如果指定，指定关联的租户才能使用此规则
     */
    private String tenant;
    
    /**
     * uri参数（当将传入呼叫转发到新的目标地址时，此参数确定是放弃传URI中存在的任何其他参数，还是将它们转发到出站的目标URI） discard 放弃 forward 转发
     * 默认是discard
     */
    private String uriParameters;
    
}
