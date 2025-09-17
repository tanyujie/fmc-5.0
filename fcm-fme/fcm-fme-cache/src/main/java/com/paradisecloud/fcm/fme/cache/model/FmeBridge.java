package com.paradisecloud.fcm.fme.cache.model;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.paradisecloud.fcm.fme.cache.model.invoker.*;
import com.paradisecloud.fcm.service.conference.McuBridge;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.util.Strings;
import org.json.XML;
import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.fcm.common.enumer.FmeBridgeStatus;
import com.paradisecloud.fcm.common.enumer.FmeBusiStatus;
import com.paradisecloud.fcm.dao.model.BusiFme;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeProcessor;
import com.paradisecloud.fcm.fme.cache.model.enumer.FmeBridgeProcessingStrategy;
import com.paradisecloud.fcm.fme.model.cms.CallBridge;
import com.paradisecloud.fcm.fme.model.core.FmeIDBuilder;
import com.paradisecloud.fcm.fme.model.response.callbridge.ActiveCallBridgesResponse;
import com.paradisecloud.fcm.fme.model.response.callbridge.CallBridgesResponse;
import com.paradisecloud.fcm.fme.model.response.failure.FailureDetailsInfo;
import com.paradisecloud.fcm.fme.model.response.system.ConfigurationClusterResponse;
import com.sinhy.exception.SystemException;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;

public class FmeBridge extends McuBridge
{
    private static final String VERSION = "v1";
    private static final int MAX_CALL_COUNT = 49;
    private volatile BusiFme busiFme;
    private String rootUrl;
    private String eventUrl;
    private String bridgeAddress;
    private volatile boolean isDeleted;
    
    private volatile FmeBridgeStatus bridgeStatus;
    
    /**
     * 连接失败原因
     */
    private volatile String connectionFailedReason;
    
    private volatile Date firstConnectedTime;
    private volatile Date lastConnectedTime;
    private volatile Date lastDisConnectedTime;
    
    private volatile int webSocketBreakCount;
    
    private volatile Set<String> wsAuthTokens = new HashSet<>();
    private volatile int websocketConnectionTryTimesSinceLastDisconnected;
    
    private HttpRequester httpRequester;
    
    private volatile DataCache dataCache;
    
    private FmeBackgroundInvoker fmeBackgroundInvoker;
    private JschInvoker jschInvoker;
    private CdrReceiverInvoker cdrReceiverInvoker;
    
    private CoSpaceInvoker coSpaceInvoker;
    
    /**
     * CallLegProfile
     */
    private CallLegProfileInvoker callLegProfileInvoker;
    
    /**
     * DTMF
     */
    private DtmfProfileInvoker dtmfProfileInvoker;
    
    /**
     * Call
     */
    private CallProfileInvoker callProfileInvoker;
    
    /**
     * CallBranding
     */
    private CallBrandingProfileInvoker callBrandingProfileInvoker;
    
    /**
     * Compatibility
     */
    private CompatibilityProfileInvoker compatibilityProfileInvoker;
    
    /**
     * DialInSecurity
     */
    private DialInSecurityProfileInvoker dialInSecurityProfileInvoker;
    
    private InboundDialPlanRuleInvoker inboundDialPlanRuleInvoker;
    private OutboundDialPlanRuleInvoker outboundDialPlanRuleInvoker;
    
    /**
     * IvrBranding
     */
    private IvrBrandingProfileInvoker ivrBrandingProfileInvoker;
    private CallInvoker callInvoker;
    private CallLegInvoker callLegInvoker;
    private ParticipantInvoker participantInvoker;
    private TenantInvoker tenantInvoker;
    private SystemInvoker systemInvoker;
    private CallBridgeInvoker callBridgeInvoker;
    private LayoutTemplateInvoker layoutTemplateInvoker;
    private FmeLogger fmeLogger;
    private volatile int weight;
    private String callBridgeId;
    private FmeBridgeCluster fmeBridgeCluster;
    private ClusterConfigInvoker clusterConfigInvoker;
    
    public FmeBridge(BusiFme bridgeHost)
    {
        this.busiFme = bridgeHost;
        init();
    }

    public void init()
    {
        this.destroy();
        String baseUrl = "https://" + busiFme.getIp() + ":" + busiFme.getPort() + "/";
        this.rootUrl = baseUrl + "api/" + VERSION + "/";
        this.eventUrl = "wss://" + busiFme.getIp() + ":" + busiFme.getPort() + "/events/" + VERSION;
        this.bridgeAddress = FmeIDBuilder.build(busiFme);
        if (!ObjectUtils.isEmpty(busiFme.getUsername()) && !ObjectUtils.isEmpty(busiFme.getPassword()))
        {
            this.httpRequester = HttpObjectCreator.getInstance().createHttpRequester(busiFme.getUsername(), busiFme.getPassword(), false);
        }
        else
        {
            this.httpRequester = HttpObjectCreator.getInstance().createHttpRequester(false);
        }
        
        coSpaceInvoker = new CoSpaceInvoker(httpRequester, rootUrl);
        inboundDialPlanRuleInvoker = new InboundDialPlanRuleInvoker(httpRequester, rootUrl);
        outboundDialPlanRuleInvoker = new OutboundDialPlanRuleInvoker(httpRequester, rootUrl);
        callInvoker = new CallInvoker(httpRequester, rootUrl);
        callLegProfileInvoker = new CallLegProfileInvoker(httpRequester, rootUrl);
        dtmfProfileInvoker = new DtmfProfileInvoker(httpRequester, rootUrl);
        callProfileInvoker = new CallProfileInvoker(httpRequester, rootUrl);
        compatibilityProfileInvoker = new CompatibilityProfileInvoker(httpRequester, rootUrl);
        callBrandingProfileInvoker = new CallBrandingProfileInvoker(httpRequester, rootUrl);
        dialInSecurityProfileInvoker = new DialInSecurityProfileInvoker(httpRequester, rootUrl);
        ivrBrandingProfileInvoker = new IvrBrandingProfileInvoker(httpRequester, rootUrl);
        tenantInvoker = new TenantInvoker(httpRequester, rootUrl);
        callBridgeInvoker = new CallBridgeInvoker(httpRequester, rootUrl);
        callLegInvoker = new CallLegInvoker(httpRequester, rootUrl);
        participantInvoker = new ParticipantInvoker(httpRequester, rootUrl);
        systemInvoker = new SystemInvoker(httpRequester, rootUrl);
        layoutTemplateInvoker = new LayoutTemplateInvoker(httpRequester, rootUrl);
        if(Strings.isNotBlank(busiFme.getAdminUsername())&&Strings.isNotBlank(busiFme.getAdminUsername())){
            fmeBackgroundInvoker = new FmeBackgroundInvoker(httpRequester, baseUrl, busiFme.getAdminUsername(), busiFme.getAdminPassword());
        }else {
            fmeBackgroundInvoker = new FmeBackgroundInvoker(httpRequester, baseUrl, busiFme.getUsername(), busiFme.getPassword());
        }

        jschInvoker = new JschInvoker(busiFme);
        cdrReceiverInvoker = new CdrReceiverInvoker(httpRequester, rootUrl);
        this.fmeLogger = new FmeLogger(this, true);
        this.dataCache = new DataCache(this);
        clusterConfigInvoker =new ClusterConfigInvoker(httpRequester, rootUrl);
        setMcuId(busiFme.getId());
        setCallIp(busiFme.getIp());
    }
    
    public void initCallBridgeId()
    {
        ConfigurationClusterResponse configurationClusterResponse = this.systemInvoker.getConfigurationCluster();
        if (!ObjectUtils.isEmpty(configurationClusterResponse.getCluster().getUniqueName()))
        {
            int offset = 0;
            AtomicInteger totalCount = new AtomicInteger();
            while (true)
            {
                CallBridgesResponse cbr = this.callBridgeInvoker.getCallBridges(offset);
                if (cbr != null)
                {
                    ActiveCallBridgesResponse activeCallBridgesResponse = cbr.getCallBridges();
                    List<CallBridge> callBridges = activeCallBridgesResponse.getCallBridge();
                    if (callBridges != null)
                    {
                        // 业务处理
                        for (CallBridge cb : callBridges)
                        {
                            if (configurationClusterResponse.getCluster().getUniqueName().equals(cb.getName()))
                            {
                                callBridgeId = cb.getId();
                                if (this.fmeBridgeCluster != null)
                                {
                                    this.fmeBridgeCluster.registerCallBridge(this);
                                }
                                return;
                            }
                        }
                        
                        Integer total = activeCallBridgesResponse.getTotal();
                        totalCount.addAndGet(callBridges.size());
                        if (totalCount.get() < total.intValue())
                        {
                            offset = totalCount.get();
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
                else
                {
                    break;
                }
            }
        }
    }
    
    /**
     * <p>Get Method   :   tenantInvoker TenantInvoker</p>
     * @return tenantInvoker
     */
    public TenantInvoker getTenantInvoker()
    {
        return tenantInvoker;
    }

    /**
     * <p>Get Method   :   coSpaceInvoker CoSpaceInvoker</p>
     * @return coSpaceInvoker
     */
    public CoSpaceInvoker getCoSpaceInvoker()
    {
        return coSpaceInvoker;
    }
    
    /**
     * <p>Get Method   :   callLegProfileInvoker CallLegProfileInvoker</p>
     * @return callLegProfileInvoker
     */
    public CallLegProfileInvoker getCallLegProfileInvoker()
    {
        return callLegProfileInvoker;
    }
    
    /**
     * <p>Get Method   :   dtmfProfileInvoker DtmfProfileInvoker</p>
     * @return dtmfProfileInvoker
     */
    public DtmfProfileInvoker getDtmfProfileInvoker()
    {
        return dtmfProfileInvoker;
    }
    
    /**
     * <p>Get Method   :   inboundDialPlanRuleInvoker InboundDialPlanRuleInvoker</p>
     * @return inboundDialPlanRuleInvoker
     */
    public InboundDialPlanRuleInvoker getInboundDialPlanRuleInvoker()
    {
        return inboundDialPlanRuleInvoker;
    }

    /**
     * <p>Get Method   :   outboundDialPlanRuleInvoker OutboundDialPlanRuleInvoker</p>
     * @return outboundDialPlanRuleInvoker
     */
    public OutboundDialPlanRuleInvoker getOutboundDialPlanRuleInvoker()
    {
        return outboundDialPlanRuleInvoker;
    }

    /**
     * <p>Get Method   :   callProfileInvoker CallProfileInvoker</p>
     * @return callProfileInvoker
     */
    public CallProfileInvoker getCallProfileInvoker()
    {
        return callProfileInvoker;
    }
    
    /**
     * <p>Get Method   :   compatibilityProfileInvoker CompatibilityProfileInvoker</p>
     * @return compatibilityProfileInvoker
     */
    public CompatibilityProfileInvoker getCompatibilityProfileInvoker()
    {
        return compatibilityProfileInvoker;
    }
    
    /**
     * <p>Get Method   :   callBrandingProfileInvoker CallBrandingProfileInvoker</p>
     * @return callBrandingProfileInvoker
     */
    public CallBrandingProfileInvoker getCallBrandingProfileInvoker()
    {
        return callBrandingProfileInvoker;
    }
    
    /**
     * <p>Get Method   :   dialInSecurityProfileInvoker DialInSecurityProfileInvoker</p>
     * @return dialInSecurityProfileInvoker
     */
    public DialInSecurityProfileInvoker getDialInSecurityProfileInvoker()
    {
        return dialInSecurityProfileInvoker;
    }

    /**
     * <p>Get Method   :   ivrBrandingProfileInvoker IvrBrandingProfileInvoker</p>
     * @return ivrBrandingProfileInvoker
     */
    public IvrBrandingProfileInvoker getIvrBrandingProfileInvoker()
    {
        return ivrBrandingProfileInvoker;
    }

    /**
     * <p>Get Method   :   callInvoker CallInvoker</p>
     * @return callInvoker
     */
    public CallInvoker getCallInvoker()
    {
        return callInvoker;
    }

    /**
     * <p>Get Method   :   callLegInvoker CallLegInvoker</p>
     * @return callLegInvoker
     */
    public CallLegInvoker getCallLegInvoker()
    {
        return callLegInvoker;
    }
    
    /**
     * <p>Get Method   :   layoutTemplateInvoker LayoutTemplateInvoker</p>
     * @return layoutTemplateInvoker
     */
    public LayoutTemplateInvoker getLayoutTemplateInvoker()
    {
        return layoutTemplateInvoker;
    }

    /**
     * <p>Get Method   :   participantInvoker ParticipantInvoker</p>
     * @return participantInvoker
     */
    public ParticipantInvoker getParticipantInvoker()
    {
        return participantInvoker;
    }
    
    /**
     * <p>Get Method   :   systemInvoker SystemInvoker</p>
     * @return systemInvoker
     */
    public SystemInvoker getSystemInvoker()
    {
        return systemInvoker;
    }
    
    /**
     * <p>Get Method   :   cdrReceiverInvoker CdrReceiverInvoker</p>
     * @return cdrReceiverInvoker
     */
    public CdrReceiverInvoker getCdrReceiverInvoker()
    {
        return cdrReceiverInvoker;
    }

    /**
     * <pre>获取websocket动态链接地址（每次都会不一样）</pre>
     * @author Administrator
     * @since 2020-12-27 00:26 
     * @return String
     */
    public String getWebSocketEventUrl(String authToken)
    {
        if (ObjectUtils.isEmpty(authToken))
        {
            return null;
        }
        return getEventUrl() + "?authToken=" + authToken;
    }
    
    /**
     * <p>Get Method   :   bridgeAddress String</p>
     * @return bridgeAddress
     */
    public String getBridgeAddress()
    {
        return bridgeAddress;
    }

    /**
     * <p>Get Method   :   bridgeHost BridgeHost</p>
     * @return bridgeHost
     */
    public BusiFme getBusiFme()
    {
        return busiFme;
    }
    
    public String getAttendeeIp()
    {
        if (!ObjectUtils.isEmpty(busiFme.getCucmIp()))
        {
            return busiFme.getCucmIp();
        }
        return busiFme.getIp();
    }
    
    public String getEventUrl()
    {
        return this.eventUrl;
    }
    
    /**
     * 获取rootUrl
     *
     * @return
     */
    public String getRootUrl()
    {
        return this.rootUrl;
    }
    
    /**
     * <p>Get Method   :   isAvailable boolean</p>
     * @return isAvailable
     */
    public boolean isAvailable()
    {
        return bridgeStatus == FmeBridgeStatus.AVAILABLE;
    }
    
    /**
     * <p>Get Method   :   isAvailable boolean</p>
     * @return isAvailable
     */
    public boolean isInitializing()
    {
        return bridgeStatus == null || bridgeStatus == FmeBridgeStatus.INITIALIZING;
    }
    
    /**
     * <p>Get Method   :   firstConnectedTime Date</p>
     * @return firstConnectedTime
     */
    public Date getFirstConnectedTime()
    {
        return firstConnectedTime;
    }

    /**
     * <p>Get Method   :   lastConnectedTime Date</p>
     * @return lastConnectedTime
     */
    public Date getLastConnectedTime()
    {
        return lastConnectedTime;
    }

    /**
     * <p>Get Method   :   lastDisConnectedTime Date</p>
     * @return lastDisConnectedTime
     */
    public Date getLastDisConnectedTime()
    {
        return lastDisConnectedTime;
    }
    
    /**
     * <p>Get Method   :   webSocketBreakCount int</p>
     * @return webSocketBreakCount
     */
    public int getWebSocketBreakCount()
    {
        return webSocketBreakCount;
    }
    
    /**
     * <p>Get Method   :   connectionFailedReason String</p>
     * @return connectionFailedReason
     */
    public String getConnectionFailedReason()
    {
        return connectionFailedReason;
    }

    /**
     * <p>Set Method   :   connectionFailedReason String</p>
     * @param connectionFailedReason
     */
    public void setConnectionFailedReason(String connectionFailedReason)
    {
        this.connectionFailedReason = connectionFailedReason;
    }

    public ClusterConfigInvoker getClusterConfigInvoker() {
        return clusterConfigInvoker;
    }

    /**
     * <p>Set Method   :   bridgeStatus FmeBridgeStatus</p>
     * @param bridgeStatus
     */
    public void setBridgeStatus(FmeBridgeStatus bridgeStatus, String authToken)
    {
        this.bridgeStatus = bridgeStatus;
        if (bridgeStatus == FmeBridgeStatus.AVAILABLE)
        {
            if (this.wsAuthTokens.contains(authToken))
            {
                busiFme.setStatus(FmeBusiStatus.ONLINE.getValue());
                this.lastConnectedTime = new Date();
                if (this.firstConnectedTime == null)
                {
                    this.firstConnectedTime = this.lastConnectedTime;
                }
                websocketConnectionTryTimesSinceLastDisconnected = 0;
                this.connectionFailedReason = null;
                fmeLogger.logWebsocketInfo("websocket 已连接，当前连接数: " + getWsAuthTokens(), true);
            }
            else
            {
                fmeLogger.logWebsocketInfo("websocket 连接失败，连接authToken[" + authToken + "]已被销毁", true);
            }
        }
        else if (bridgeStatus == FmeBridgeStatus.NOT_AVAILABLE)
        {
            busiFme.setStatus(FmeBusiStatus.OFFLINE.getValue());
            this.decWebsocketConnectionCount(authToken);
        }
        else
        {
            busiFme.setStatus(FmeBusiStatus.OFFLINE.getValue());
        }
    }
    
    /**
     * <p>Get Method   :   bridgeStatus FmeBridgeStatus</p>
     * @return bridgeStatus
     */
    public FmeBridgeStatus getBridgeStatus()
    {
        return bridgeStatus;
    }

    /**
     * <p>Get Method   :   dataCache FmeDataCache</p>
     * @return dataCache
     */
    public DataCache getDataCache()
    {
        return dataCache;
    }

    /**
     * <pre>判断会议桥是否被删除</pre>
     * @author lilinhai
     * @since 2020-12-29 18:22
     * @return boolean
     */
    public boolean isDeleted()
    {
        return isDeleted;
    }
    
    /**
     * <p>Set Method   :   isDeleted boolean</p>
     * @param isDeleted
     */
    public void setDeleted(boolean isDeleted)
    {
        this.isDeleted = isDeleted;
        this.busiFme.getParams().put("isDeleted", true);
    }

    public void destroy()
    {
        if (httpRequester != null)
        {
            httpRequester.destroy();
        }
    }
    
    /**
     * <p>Get Method   :   fmeLogger FmeLogger</p>
     * @return fmeLogger
     */
    public FmeLogger getFmeLogger()
    {
        return fmeLogger;
    }
    
    /**
     * <p>Get Method   :   weight int</p>
     * @return weight
     */
    public Integer getWeight()
    {
        return weight;
    }

    /**
     * <p>Set Method   :   weight int</p>
     * @param weight
     */
    public void setWeight(int weight)
    {
        this.weight = weight;
    }
    
    /**
     * <p>Get Method   :   websocketConnectionCount int</p>
     * @return websocketConnectionCount
     */
    public Set<String> getWsAuthTokens()
    {
        return wsAuthTokens;
    }
    
    /**
     * <p>Set Method   :   websocketConnectionCount int</p>
     * @param authToken
     */
    public void addWebsocketConnectionCount(String authToken)
    {
        this.wsAuthTokens.add(authToken);
    }
    
    /**
     * <p>Set Method   :   websocketConnectionCount int</p>t
     */
    private void decWebsocketConnectionCount(String authToken)
    {
        if (!ObjectUtils.isEmpty(authToken))
        {
            if (this.wsAuthTokens.remove(authToken))
            {
                this.lastDisConnectedTime = new Date();
                this.webSocketBreakCount++;
            }
        }
        websocketConnectionTryTimesSinceLastDisconnected++;
    }
    
    /**
     * <p>Get Method   :   websocketConnectionTryTimesSinceLastDisconnected int</p>
     * @return websocketConnectionTryTimesSinceLastDisconnected
     */
    public int getWebsocketConnectionTryTimesSinceLastDisconnected()
    {
        return websocketConnectionTryTimesSinceLastDisconnected;
    }

    public void doFmeBridgeBusiness(FmeBridgeProcessingStrategy fmeBridgeProcessingStrategy, FmeBridgeProcessor fmeBridgeProcessor)
    {
        if (fmeBridgeCluster != null)
        {
            fmeBridgeProcessingStrategy.process(fmeBridgeCluster.getAvailableFmeBridges(), fmeBridgeProcessor);
        }
        else
        {
            FmeBridgeProcessingStrategy.BREAK.process(Arrays.asList(this), fmeBridgeProcessor);
        }
    }

    /**
     * <p>Set Method   :   fmeBridgeCluster FmeBridgeCluster</p>
     * @param fmeBridgeCluster
     */
    public void setFmeBridgeCluster(FmeBridgeCluster fmeBridgeCluster)
    {
        this.fmeBridgeCluster = fmeBridgeCluster;
    }
    
    public FmeBridge getByCallBridge(String callBridgeId)
    {
        if (fmeBridgeCluster != null)
        {
            return fmeBridgeCluster.getByCallBridge(callBridgeId);
        }
        return null;
    }
    
    /**
     * <p>Get Method   :   callBridgeId String</p>
     * @return callBridgeId
     */
    public String getCallBridgeId()
    {
        return callBridgeId;
    }
    
    /**
     * <p>Get Method   :   jschInvoker JschInvoker</p>
     * @return jschInvoker
     */
    public JschInvoker getJschInvoker()
    {
        return jschInvoker;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bridgeAddress == null) ? 0 : bridgeAddress.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        FmeBridge other = (FmeBridge) obj;
        if (bridgeAddress == null)
        {
            if (other.bridgeAddress != null) return false;
        }
        else if (!bridgeAddress.equals(other.bridgeAddress)) return false;
        return true;
    }
    
    /**
     * <pre>获取webSocket的鉴权token</pre>
     * @author Administrator
     * @since 2020-12-27 00:08 
     * @return String
     */
    public String getWebSocketAuthToken()
    {
        GenericValue<FailureDetailsInfo> genericValue = new GenericValue<>();
        StringBuilder authTokenBuilder = new StringBuilder();
        httpRequester.post(rootUrl + "authTokens", new HttpResponseProcessorAdapter()
        {
            public void success(HttpResponse httpResponse, ContentType contentType)
            {
                fmeLogger.logWebsocketInfo(Arrays.toString(httpResponse.getAllHeaders()), true, true);
                fmeLogger.logWebsocketInfo("url:" + rootUrl + "authTokens", true, true);
                Header authTokenHeader = httpResponse.getFirstHeader("X-Cisco-CMS-Auth-Token");
                if (authTokenHeader != null)
                {
                    String authToken = authTokenHeader.getValue().trim();
                    if (!ObjectUtils.isEmpty(authToken))
                    {
                        authTokenBuilder.append(authToken);
                    }
                }
            }

            public void fail(HttpResponse httpResponse)
            {
                fmeLogger.logWebsocketInfo("authTokens请求错误", true, true);
                fmeLogger.logWebsocketInfo("url:" + rootUrl + "authTokens", true, true);
                if (isXmlContentType(httpResponse))
                {
                    genericValue.setValue(JSON.parseObject(XML.toJSONObject(getBodyContent(httpResponse)).toString().replaceAll("\" : null", "\" : \"\""), FailureDetailsInfo.class));
                    fmeLogger.logWebsocketInfo(getBodyContent(httpResponse), true, true);
                }
            }
        });
        
        if (genericValue.getValue() != null)
        {
            throw new SystemException(1005435, genericValue.getValue().toString());
        }
        return authTokenBuilder.toString();
    }
    
    public void checkCallCount()
    {
        if (getDataCache().getCallCount() >= MAX_CALL_COUNT)
        {
            throw new SystemException(1002322, "启动失败，活跃会议数已超过" + MAX_CALL_COUNT + "个，请先关闭一些不必要的会议，再开始本会议！");
        }
    }
    
    /**
     * <p>Get Method   :   fmeBackgroundInvoker FmeBackgroundInvoker</p>
     * @return fmeBackgroundInvoker
     */
    public FmeBackgroundInvoker getFmeBackgroundInvoker()
    {
        return fmeBackgroundInvoker;
    }

    @Override
    public String toString()
    {
        return "FmeBridge [bridgeAddress=" + bridgeAddress + ", bridgeStatus=" + bridgeStatus + ", callBridgeId=" + callBridgeId + "]";
    }
    
}
