package com.paradisecloud.fcm.fme.model.cms;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * WebBridge 实体类
 *
 * @Author yl
 * @Date 2020/4/20 10:20
 */
@Getter
@Setter
@ToString
public class WebBridge
{
    
    /**
     * 唯一id
     */
    @JSONField(name = "@id")
    private String id;
    
    /**
     * callBridge需要使用到的webBridge的地址
     */
    private String url;
    
    /**
     * callBridge桥用于此WebBridge的登录页面上的背景图像和徽标标识的任何自定义存档文件的地址 在指定路径时，使用任意端口值除了http:80和https:443被认为是无效的
     */
    private String resourceArchive;
    
    /**
     * 如果您提供这个wenbridge要关联的租户的ID，只调用coSpaces的id由该租户拥有的可以通过它加入
     */
    private String tenant;
    
    /**
     * 只有与内部租户关联的coSpaces可以访问指定的租户组通过此web桥接调用ID。 如果没有租户组被提供，只有coSpaces没有a租客，或与没有租客的租客有关联组，可以通过调用ID访问
     */
    private String tenantGroup;
    
    /**
     * 通过调用ID和来控制coSpace访问密码 disabled：密码禁用 secure：callID和密码必须指定来查找并加入一个coSpace
     * legacy：只需要指定一个callID就可以查找一个coSpace。(2.3中弃用)
     */
    private String idEntryMode;
    
    /**
     * 这个WebBridge是否允许用户以访客的身份访问coSpace(和coSpace访问方法) 如果在创建(POST)操作中没有提供此参数，则默认为true
     */
    private Boolean allowWeblinkAccess;
    
    /**
     * 此WebBridge是否将在索引页上显示“登录”选项卡 如果在创建(POST)操作中没有提供此参数，则默认为true
     */
    private Boolean showSignIn;
    
    /**
     * 这个WebBridge是否应该接受coSpace和coSpace访问方法调用id，以便允许访问者加入coSpaces 如果在创建(POST)操作中没有提供此参数，则默认为true。
     */
    private Boolean resolveCoSpaceCallIds;
    
    /**
     * 这个WebBridge是否接受将id解析为Lync调度的会议id 如果在创建(POST)操作中没有提供此参数，则默认为false
     */
    private Boolean resolveLyncConferenceIds;
    
    /**
     * 如果指定，则将此WebBridge与提供的callBridge关联(来自版本2.1)
     */
    private String callBridge;
    
    /**
     * 如果指定，将此WebBridge与提供的callBridgeGroup关联(来自版本2.1)
     */
    private String callBridgeGroup;
    
}
