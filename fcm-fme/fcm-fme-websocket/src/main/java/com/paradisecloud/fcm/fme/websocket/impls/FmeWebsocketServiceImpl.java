/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : McuNodeWebsocketServiceImpl.java
 * Package     : com.paradisecloud.sync.service.impls
 * @author lilinhai 
 * @since 2020-12-11 14:01
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.impls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiCallLegProfileService;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiCallLegProfileService.CallLegProfileProcessor;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiDialPlanRuleInboundService;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiDialPlanRuleOutboundService;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileCallBrandingService;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileCallBrandingService.CallBrandingProfileProcessor;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileCallService;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileCallService.CallProfileProcessor;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileCompatibilityService;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileCompatibilityService.CompatibilityProfileProcessor;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileDialInSecurityService;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileDialInSecurityService.DialInSecurityProfileProcessor;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileDtmfService;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileDtmfService.DtmfProfileProcessor;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileIvrBrandingService;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiProfileIvrBrandingService.IvrBrandingProfileProcessor;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiTenantSettingsService;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiTenantSettingsService.TenantProcessor;
import com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService;
import com.paradisecloud.fcm.fme.attendee.interfaces.ICallService;
import com.paradisecloud.fcm.fme.attendee.interfaces.ILayoutTemplateService;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.conference.interfaces.IParticipantSyncService;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.CustomScreenCreater;
import com.paradisecloud.fcm.fme.model.cms.CallBrandingProfile;
import com.paradisecloud.fcm.fme.model.cms.CallLegProfile;
import com.paradisecloud.fcm.fme.model.cms.CallProfile;
import com.paradisecloud.fcm.fme.model.cms.CompatibilityProfile;
import com.paradisecloud.fcm.fme.model.cms.DialInSecurityProfile;
import com.paradisecloud.fcm.fme.model.cms.DtmfProfile;
import com.paradisecloud.fcm.fme.model.cms.IvrBrandingProfile;
import com.paradisecloud.fcm.fme.model.cms.LayoutTemplate;
import com.paradisecloud.fcm.fme.model.cms.Tenant;
import com.paradisecloud.fcm.fme.model.websocket.callinfo.CallInfoUpdateMessage;
import com.paradisecloud.fcm.fme.model.websocket.calllist.CallListUpdateMessage;
import com.paradisecloud.fcm.fme.model.websocket.roster.RosterUpdateMessage;
import com.paradisecloud.fcm.fme.websocket.interfaces.ICallInfoUpdateMessageService;
import com.paradisecloud.fcm.fme.websocket.interfaces.ICallListUpdateMessageService;
import com.paradisecloud.fcm.fme.websocket.interfaces.IRosterUpdateMessageService;
import com.paradisecloud.fcm.fme.websocket.interfaces.IWebSocketService;

/**  
 * <pre>websocket消息业务处理实现类</pre>
 * @author lilinhai
 * @since 2020-12-11 14:01
 * @version V1.0  
 */
@Service
public class FmeWebsocketServiceImpl implements IWebSocketService
{
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ICoSpaceService coSpaceService;
    
    @Autowired
    private ICallService callService;
    
    @Autowired
    private IParticipantSyncService participantSyncService;
    
    @Autowired
    private ICallListUpdateMessageService callListUpdateMessageService;
    
    @Autowired
    private ICallInfoUpdateMessageService callInfoUpdateMessageService;
    
    @Autowired
    private IRosterUpdateMessageService rosterUpdateMessageService;
    
    @Autowired
    private IBusiCallLegProfileService callLegProfileService;
    
    @Autowired
    private IBusiProfileCallBrandingService busiProfileCallBrandingService;
    
    @Autowired
    private IBusiProfileCallService busiProfileCallService;
    
    @Autowired
    private IBusiProfileCompatibilityService busiProfileCompatibilityService;
    
    @Autowired
    private IBusiProfileDialInSecurityService busiProfileDialInSecurityService;
    
    @Autowired
    private IBusiProfileDtmfService busiProfileDtmfService;
    
    @Autowired
    private IBusiProfileIvrBrandingService busiProfileIvrBrandingService;
    
    @Autowired
    private IBusiDialPlanRuleOutboundService busiDialPlanRuleOutboundService;
    
    @Autowired
    private IBusiDialPlanRuleInboundService busiDialPlanRuleInboundService;
    
    @Autowired
    private IBusiTenantSettingsService busiTenantSettingsService;
    
    @Autowired
    private ILayoutTemplateService layoutTemplateService;
    
    /**
     * <pre>同步全量数据方法实现</pre>
     * @author lilinhai
     * @since 2020-12-11 14:20 
     * @param fmeBridge
     * @see com.paradisecloud.fcm.fme.websocket.interfaces.IWebSocketService#syncAllData(com.paradisecloud.fcm.fme.cache.model.fmeinvoker.FmeBridge.FmeHttpInvoker.BridgeApiUtil)
     */
    public void syncAllData(FmeBridge fmeBridge)
    {
        try
        {
            fmeBridge.getDataCache().clear();
            fmeBridge.getFmeLogger().logWebsocketInfo("Full synchronization---------begin", true);
            
            layoutTemplateService.sync(fmeBridge, (FmeBridge fmeBridge0, LayoutTemplate layoutTemplate) -> {
                JSONObject json = fmeBridge.getLayoutTemplateInvoker().getLayoutTemplate(layoutTemplate.getId());
                if (json != null)
                {
                    JSONArray templates = json.getJSONArray("templates");
                    if (templates != null && templates.size() > 0)
                    {
                        JSONObject template = templates.getJSONObject(0);
                        JSONArray panes = template.getJSONArray("panes");
                        fmeBridge.getDataCache().getSplitScreenCreaterMap().registerSplitScreen(new CustomScreenCreater(layoutTemplate, panes));
                    }
                }
            });
            coSpaceService.syncCoSpaces(fmeBridge);
            callService.syncCall(fmeBridge, (fmeBridge0, call) -> {
                callService.syncCall(fmeBridge0, call.getId());
            });
            
            participantSyncService.syncParticipants(fmeBridge, (FmeBridge fmeBridge0, int offset) -> {
                return fmeBridge0.getParticipantInvoker().getParticipants(offset);
            }, null);
            
            callLegProfileService.syncCallLegProfile(fmeBridge, new CallLegProfileProcessor()
            {
                public void process(CallLegProfile callLegProfile)
                {
                    fmeBridge.getDataCache().update(callLegProfile);
                    fmeBridge.getFmeLogger().logWebsocketInfo("CallLegProfile Sync: " + callLegProfile.getId(), true);
                }
            });
            
            busiProfileCallBrandingService.syncAllProfile(fmeBridge, new CallBrandingProfileProcessor()
            {
                public void process(CallBrandingProfile callBrandingProfile)
                {
                    fmeBridge.getDataCache().update(callBrandingProfile);
                    fmeBridge.getFmeLogger().logWebsocketInfo("callBrandingProfile Sync: " + callBrandingProfile.getId(), true);
                }
            });
            
            busiDialPlanRuleOutboundService.syncAllPlan(fmeBridge, (outPlan) -> {
                fmeBridge.getDataCache().update(outPlan);
                fmeBridge.getFmeLogger().logWebsocketInfo("outPlan Sync: " + outPlan.getId(), true);
            });
            busiDialPlanRuleInboundService.syncAllPlan(fmeBridge, (inPlan) -> {
                fmeBridge.getDataCache().update(inPlan);
                fmeBridge.getFmeLogger().logWebsocketInfo("inPlan Sync: " + inPlan.getId(), true);
            });
            
            busiProfileCallService.syncAllProfile(fmeBridge, new CallProfileProcessor()
            {
                public void process(CallProfile callProfile)
                {
                    fmeBridge.getDataCache().update(callProfile);
                    fmeBridge.getFmeLogger().logWebsocketInfo("callProfile Sync: " + callProfile.getId(), true);
                }
            });
            
            busiProfileCompatibilityService.syncAllProfile(fmeBridge, new CompatibilityProfileProcessor()
            {
                public void process(CompatibilityProfile compatibilityProfile)
                {
                    fmeBridge.getDataCache().update(compatibilityProfile);
                    fmeBridge.getFmeLogger().logWebsocketInfo("compatibilityProfile Sync: " + compatibilityProfile.getId(), true);
                }
            });
            
            busiProfileDialInSecurityService.syncAllProfile(fmeBridge, new DialInSecurityProfileProcessor()
            {
                public void process(DialInSecurityProfile dialInSecurityProfile)
                {
                    fmeBridge.getDataCache().update(dialInSecurityProfile);
                    fmeBridge.getFmeLogger().logWebsocketInfo("dialInSecurityProfile Sync: " + dialInSecurityProfile.getId(), true);
                }
            });
            
            busiProfileDtmfService.syncAllProfile(fmeBridge, new DtmfProfileProcessor()
            {
                public void process(DtmfProfile dtmfProfile)
                {
                    fmeBridge.getDataCache().update(dtmfProfile);
                    fmeBridge.getFmeLogger().logWebsocketInfo("dtmfProfile Sync: " + dtmfProfile.getId(), true);
                }
            });
            
            busiProfileIvrBrandingService.syncAllProfile(fmeBridge, new IvrBrandingProfileProcessor()
            {
                public void process(IvrBrandingProfile ivrBrandingProfile)
                {
                    fmeBridge.getDataCache().update(ivrBrandingProfile);
                    fmeBridge.getFmeLogger().logWebsocketInfo("ivrBrandingProfile Sync: " + ivrBrandingProfile.getId(), true);
                }
            });
            
            busiTenantSettingsService.syncAllProfile(fmeBridge, new TenantProcessor()
            {
                public void process(Tenant tenant)
                {
                    fmeBridge.getDataCache().update(tenant);
                    fmeBridge.getFmeLogger().logWebsocketInfo("tenant Sync: " + tenant.getId(), true);
                }
            });
            
            fmeBridge.getFmeLogger().logWebsocketInfo("Full synchronization---------end", true);
        }
        catch (Throwable e)
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("syncAllData error", true, e);
        }
    }

    @Override
    public void process(CallListUpdateMessage callListUpdateMessage, JSONObject json, FmeBridge fmeBridge)
    {
        callListUpdateMessageService.process(callListUpdateMessage, json, fmeBridge);
    }
    
    @Override
    public void process(CallInfoUpdateMessage callInfoUpdateMessage, JSONObject messageObj, FmeBridge fmeBridge)
    {
        callInfoUpdateMessageService.process(callInfoUpdateMessage, messageObj, fmeBridge);
    }

    @Override
    public void process(RosterUpdateMessage rosterUpdateMessage, JSONObject json, FmeBridge fmeBridge)
    {
        rosterUpdateMessageService.process(rosterUpdateMessage, json, fmeBridge);
    }
    
}
