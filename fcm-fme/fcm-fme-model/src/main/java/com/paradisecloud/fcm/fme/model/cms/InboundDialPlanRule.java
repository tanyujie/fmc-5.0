package com.paradisecloud.fcm.fme.model.cms;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 内呼规则
 *
 * @Author yl
 * @Date 2020/3/9 10:27
 */
@Getter
@Setter
@ToString
public class InboundDialPlanRule
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
     * 根据users解决 true 对该域的调用将针对用户进行匹配JIDs(如果找到匹配，则传入调用将导致“点对点”呼叫该用户的会议app)
     */
    private Boolean resolveToUsers;
    
    /**
     * 根据coSpaces解决 true 则对该域的调用将根据coSpace进行匹配uri(如果找到匹配，则传入调用分支成参与coSpace)
     */
    private Boolean resolveTocoSpaces;
    
    /**
     * 根据Ivrs解决 true 则对该域的调用将根据配置进行匹配IVR uri(如果找到匹配，则传入呼叫分支连接IVR)
     */
    private Boolean resolveToIvrs;
    
    /**
     * 根据LyncConferences解决 true 对该域的调用将解析为Lync会议网址;如果解决成功，call leg成为Lync会议的与会者 默认值为“false”
     */
    private Boolean resolveToLyncConferences;
    
    /**
     * 租户的id 如果指定，对该入站域的调用仅指定租户的用户jid和coSpace uri
     */
    private String tenant;
}
