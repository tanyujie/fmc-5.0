/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ParticipantParamBuilder.java
 * Package     : com.paradisecloud.fcm.fme.model.param
 * @author lilinhai 
 * @since 2021-02-19 11:46
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.parambuilder;

/**  
 * <pre>会议室参数构建器</pre>
 * @author lilinhai
 * @since 2021-02-19 11:46
 * @version V1.0  
 */
public class CoSpaceParamBuilder extends ParamBuilder<CoSpaceParamBuilder>
{
    
    /**
     * 会议室名
     * @author lilinhai
     * @since 2021-02-19 11:48 
     * @param defaultLayout
     * @return ParticipantParamBuilder
     */
    public CoSpaceParamBuilder name(String name)
    {
        return param("name", name);
    }
    
    /**
     * 入会方案
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public CoSpaceParamBuilder callLegProfile(String callLegProfileId)
    {
        return param("callLegProfile", callLegProfileId);
    }
    
    /**
     * call profile
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public CoSpaceParamBuilder callProfile(String callProfileId)
    {
        return param("callProfile", callProfileId);
    }
    
    /**
     * callBrandingProfile
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public CoSpaceParamBuilder callBrandingProfile(String callBrandingProfile)
    {
        return param("callBrandingProfile", callBrandingProfile);
    }
    
    /**
     * dialInSecurityProfile
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public CoSpaceParamBuilder dialInSecurityProfile(String dialInSecurityProfile)
    {
        return param("dialInSecurityProfile", dialInSecurityProfile);
    }
    
    /**
     * uri
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    protected CoSpaceParamBuilder uri(String uri)
    {
        return param("uri", uri);
    }
    
    /**
     * callId
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    protected CoSpaceParamBuilder callId(String callId)
    {
        return param("callId", callId);
    }
    
    /**
     * callId
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public CoSpaceParamBuilder conferenceNumber(String conferenceNumber)
    {
        return uri(conferenceNumber).callId(conferenceNumber);
    }
    
    /**
     * 窗格最高权重（多分频业务专用）
     * @author lilinhai
     * @since 2021-04-09 17:50 
     * @param panePlacementHighestImportance
     * @return CoSpaceParamBuilder
     */
    public CoSpaceParamBuilder panePlacementHighestImportance(Integer panePlacementHighestImportance)
    {
        return param("panePlacementHighestImportance", panePlacementHighestImportance);
    }
    
    /**
     * 窗格最高权重（多分频业务专用）
     * @author lilinhai
     * @since 2021-04-09 17:50 
     * @param panePlacementHighestImportance
     * @return CoSpaceParamBuilder
     */
    public CoSpaceParamBuilder panePlacementHighestImportance()
    {
        return param("panePlacementHighestImportance", "");
    }
    
    /**
     * 窗格模式，是否显示自己
     * @author lilinhai
     * @since 2021-04-09 17:50 
     * @param panePlacementSelfPaneMode
     * @return CoSpaceParamBuilder
     */
    public CoSpaceParamBuilder panePlacementSelfPaneMode(String panePlacementSelfPaneMode)
    {
        return param("panePlacementSelfPaneMode", panePlacementSelfPaneMode);
    }
    
    /**
     * 会议室布局
     * @author sinhy
     * @since 2021-06-10 14:00 
     * @param defaultLayout
     * @return CoSpaceParamBuilder
     */
    public CoSpaceParamBuilder defaultLayout(String defaultLayout)
    {
        return param("defaultLayout", defaultLayout);
    }

    /**
     * 会议室布局
     * @author nj
     * @since 2021-06-10 14:00
     * @param layoutTemplate
     * @return CoSpaceParamBuilder
     */
    public CoSpaceParamBuilder layoutTemplate(String layoutTemplate)
    {
        return param("layoutTemplate", layoutTemplate);
    }
    
    /**
     * 租户绑定
     * @author sinhy
     * @since 2021-06-10 14:00 
     * @param tenant
     * @return CoSpaceParamBuilder
     */
    public CoSpaceParamBuilder tenant(String tenant)
    {
        return param("tenant", tenant);
    }
    
    /**
     * 会议室密码
     * @author sinhy
     * @since 2021-06-10 14:00 
     * @param passcode
     * @return CoSpaceParamBuilder
     */
    public CoSpaceParamBuilder passcode(String passcode)
    {
        return param("passcode", passcode);
    }
    
    /**
     * 直播地址
     * @author sinhy
     * @since 2021-06-10 14:00 
     * @param streamUrl
     * @return CoSpaceParamBuilder
     */
    public CoSpaceParamBuilder streamUrl(String streamUrl)
    {
        return param("streamUrl", streamUrl);
    }
    
    /**
     * 第二会议室号码
     * @author sinhy
     * @since 2021-06-10 14:00 
     * @param streamUrl
     * @return CoSpaceParamBuilder
     */
    public CoSpaceParamBuilder secondaryUri(String secondaryUri)
    {
        return param("secondaryUri", secondaryUri);
    }
    
}
