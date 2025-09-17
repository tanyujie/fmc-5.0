package com.paradisecloud.fcm.fme.model.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 会议 coSpace 创建请求参数类
 *
 * @author zt1994 2019/8/22 10:52
 */
@Getter
@Setter
@ToString
public class CoSpaceRequest
{
    
    /**
     * coSpace 唯一id
     */
    @JSONField(name = "@total")
    private String id;
    
    /**
     * 将显示在此coSpace的客户端UI上的人可读名称
     */
    private String name;
    
    /**
     * SIP系统将用于拨入此coSpace的URI，URI user part 是完整URI中任何 @ 字符前面的部分
     */
    private String uri;
    
    /**
     * 这个 coSpace 的辅助 URI ——它提供了与 URI 参数相同的功能，但是允许为一个 coSpace 配置多个 URI
     */
    private String secondaryUri;
    
    /**
     * 用户将在IVR(或通过web客户机)上输入的数字ID，以连接到此coSpace 会议号码 例如：89055
     */
    private String callId;
    
    private String cdrTag;
    
    /**
     * 这个coSpace的入会密码
     */
    private String passcode;
    
    /**
     * 默认布局
     */
    private String defaultLayout;
    
    /**
     * 租户
     */
    private String tenant;
    
    /**
     * callLeg配置id
     */
    private String callLegProfile;
    
    /**
     * call配置id
     */
    private String callProfile;
    
    /**
     * callBranding配置id
     */
    private String callBrandingProfile;
    
    /**
     * 如果这个值为true，并且当前没有为coSpace指定callId，那么将分配一个新的自动生成的callId，自动分配会议号
     */
    private Boolean requireCallId;
    
    /**
     * 如果提供，则设置此coSpace的安全字符串。如果没有，如果coSpace具有callId值，则自动选择安全字符串。 这是与coSpace相关的安全值，需要与callId一起提供，以便客户访问coSpace
     */
    private String secret;
    
    /**
     * 如果true—为这个coSpace生成一个新的安全值，而前一个值不再有效(例如，任何包含它的超链接将停止工作) 如果false—不为这个coSpace生成一个新的秘密值;这没有效果
     * 此参数仅适用于modify (PUT)情况
     */
    private Boolean regenerateSecret;
    
    /**
     * 控制非coSpace成员是否能够访问coSpace。如果没有提供，行为默认为true
     */
    private Boolean nonMemberAccess;
    
    /**
     * 指示具有指定JID的用户拥有coSpace
     */
    private String ownerJid;
    
    /**
     * 直播推流地址
     */
    private String streamUrl;
    
    /**
     * 如果提供，coSpace将由具有给定AD GUID的用户拥有
     */
    private String ownerAdGuid;
    
    /**
     * 计划创建此coSpace的人员(不一定是用户)的名称，如果设置coSpace，它将作为“ownerName”字段传播到任何调用对象
     */
    private String meetingScheduler;
    
    /**
     * 如果提供了panePlacementHighestImportance，则将为此coSpace激活窗格放置 权重从高到最低为1
     */
    private Integer panePlacementHighestImportance;
    
    /**
     * 设置显示自己画面
     */
    private String panePlacementSelfPaneMode;
    
}
