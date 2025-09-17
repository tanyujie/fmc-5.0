package com.paradisecloud.fcm.mcu.zj.cache.model;

import com.paradisecloud.fcm.common.enumer.McuZjBridgeStatus;
import com.paradisecloud.fcm.common.enumer.McuZjBusiStatus;
import com.paradisecloud.fcm.dao.model.BusiMcuZj;
import com.paradisecloud.fcm.mcu.zj.cache.api.ConferenceManageApi;
import com.paradisecloud.fcm.mcu.zj.model.SourceTemplate;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class McuZjBridge {

    private McuZjLogger mcuXdLogger;
    private volatile BusiMcuZj busiMcuZj;
    private String bridgeAddress;
    private volatile boolean deleted;

    private volatile McuZjBridgeStatus bridgeStatus;
    /**
     * 连接失败原因
     */
    private volatile String connectionFailedReason;

    private String baseUrl;
    private HttpRequester httpRequester;
    private volatile String sessionId = "";
    private volatile String tenantId = "";
    private volatile int topDepartmentId = 2;
    private ConferenceManageApi conferenceManageApi;
    private volatile long lastUpdateTime = 0;
    private volatile boolean dataInitialized;
    private volatile int systemResourceCount = 0;
    private volatile float usedResourceCount = 0;
    // 资源模板列表 key: ConferenceManageApi.RESOURCE_TEMPLATE_NAME_720_NO_REC, 资源模板>
    private volatile Map<String, SourceTemplate> sourceTemplateMap = new ConcurrentHashMap<>();
    // 资源模板列表 key: 模板id
    private volatile Map<Integer, SourceTemplate> sourceTemplateIdMap = new ConcurrentHashMap<>();
    private volatile List<SourceTemplate> sourceTemplateList = null;
    private volatile SourceTemplate defaultSourceTemplate = null;
    private volatile boolean sourceTemplateChanged = false;

    public McuZjBridge(BusiMcuZj busiMcuZj) {
        this.busiMcuZj = busiMcuZj;
        init();
    }

    private void init() {
        destroy();
        mcuXdLogger = new McuZjLogger(this, true);
        bridgeAddress = busiMcuZj.getIp();
        baseUrl = "https://" + busiMcuZj.getIp() + ":" + busiMcuZj.getPort();
        httpRequester = HttpObjectCreator.getInstance().createHttpRequester();
        conferenceManageApi = new ConferenceManageApi(this);
    }

    public void cleanLoginInfo() {
        setLastUpdateTime(0);
        setSessionId("");
        setTenantId("");
        sourceTemplateMap.clear();
        sourceTemplateIdMap.clear();
    }

    public McuZjLogger getMcuZjLogger() {
        return mcuXdLogger;
    }

    public BusiMcuZj getBusiMcuZj() {
        return busiMcuZj;
    }

    public String getBridgeAddress() {
        return bridgeAddress;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public HttpRequester getHttpRequester() {
        return this.httpRequester;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public int getTopDepartmentId() {
        return topDepartmentId;
    }

    public void setTopDepartmentId(int topDepartmentId) {
        this.topDepartmentId = topDepartmentId;
    }

    public ConferenceManageApi getConferenceManageApi() {
        return this.conferenceManageApi;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public boolean isDataInitialized() {
        return dataInitialized;
    }

    public void setDataInitialized(boolean dataInitialized) {
        this.dataInitialized = dataInitialized;
    }

    public int getSystemResourceCount() {
        return systemResourceCount;
    }

    public void setSystemResourceCount(int systemResourceCount) {
        this.systemResourceCount = systemResourceCount;
    }

    public float getUsedResourceCount() {
        return usedResourceCount;
    }

    public void setUsedResourceCount(float usedResourceCount) {
        this.usedResourceCount = usedResourceCount;
    }

    public void addSourceTemplate(SourceTemplate sourceTemplate) {
        sourceTemplateMap.put(sourceTemplate.getSourceName(), sourceTemplate);
        sourceTemplateIdMap.put(sourceTemplate.getId(), sourceTemplate);
        if (sourceTemplateList != null) {
            sourceTemplateList.clear();
        }
        getSourceTemplateList();
    }

    public SourceTemplate removeSourceTemplate(String name) {
        SourceTemplate sourceTemplate = sourceTemplateMap.remove(name);
        if (sourceTemplate != null) {
            sourceTemplateIdMap.remove(sourceTemplate.getId());
        }
        if (sourceTemplateList != null) {
            sourceTemplateList.clear();
        }
        getSourceTemplateList();
        return sourceTemplate;
    }

    public void setDefaultSourceTemplate(SourceTemplate sourceTemplate) {
        for (SourceTemplate sourceTemplateTemp : sourceTemplateMap.values()) {
            if (sourceTemplateTemp.getName().equals(sourceTemplate.getName())) {
                sourceTemplateTemp.setIs_default(1);
            } else {
                sourceTemplateTemp.setIs_default(0);
            }
        }
        getSourceTemplateList();
    }

    public SourceTemplate getDefaultSourceTemplate() {
        if (defaultSourceTemplate == null) {
            getSourceTemplateList();
        }
        return defaultSourceTemplate;
    }

    public SourceTemplate getSourceTemplate(String name) {
        return sourceTemplateMap.get(name);
    }

    public SourceTemplate getSourceTemplateById(Integer id) {
        return sourceTemplateIdMap.get(id);
    }

    public List<SourceTemplate> getSourceTemplateList() {
        if (sourceTemplateMap.isEmpty()) {
            return null;
        }
        if (sourceTemplateList == null || sourceTemplateList.size() != sourceTemplateMap.size()) {
            sourceTemplateList = new ArrayList<>(sourceTemplateMap.values());
            Collections.sort(sourceTemplateList, new Comparator<SourceTemplate>() {
                @Override
                public int compare(SourceTemplate s1, SourceTemplate s2) {
                    return s1.getId() - s2.getId();
                }
            });
            if (sourceTemplateList.size() > 0) {
                defaultSourceTemplate = sourceTemplateList.get(0);
                for (SourceTemplate sourceTemplate : sourceTemplateList) {
                    if (sourceTemplate.getIs_default() == 1) {
                        defaultSourceTemplate = sourceTemplate;
                    }
                }
            }
        }

        return sourceTemplateList;
    }

    public void destroy()
    {
        if (httpRequester != null)
        {
            httpRequester.destroy();
        }
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
        busiMcuZj.getParams().put("isDeleted", true);
    }

    /**
     * <p>Set Method   :   bridgeStatus McuZjBridgeStatus</p>
     * @param bridgeStatus
     */
    public void setBridgeStatus(McuZjBridgeStatus bridgeStatus)
    {
        this.bridgeStatus = bridgeStatus;
        if (bridgeStatus == McuZjBridgeStatus.AVAILABLE)
        {

        }
        else if (bridgeStatus == McuZjBridgeStatus.NOT_AVAILABLE)
        {
            busiMcuZj.setStatus(McuZjBusiStatus.OFFLINE.getValue());
        }
        else
        {
            busiMcuZj.setStatus(McuZjBusiStatus.OFFLINE.getValue());
        }
    }

    /**
     * <p>Get Method   :   bridgeStatus McuZjBridgeStatus</p>
     * @return bridgeStatus
     */
    public McuZjBridgeStatus getBridgeStatus()
    {
        return bridgeStatus;
    }

    public String getConnectionFailedReason() {
        return connectionFailedReason;
    }

    public void setConnectionFailedReason(String connectionFailedReason) {
        this.connectionFailedReason = connectionFailedReason;
    }

    /**
     * <p>Get Method   :   isAvailable boolean</p>
     * @return isAvailable
     */
    public boolean isAvailable()
    {
        return bridgeStatus == McuZjBridgeStatus.AVAILABLE;
    }

    /**
     * <p>Get Method   :   isAvailable boolean</p>
     * @return isAvailable
     */
    public boolean isInitializing()
    {
        return bridgeStatus == null || bridgeStatus == McuZjBridgeStatus.INITIALIZING;
    }

    public String getAttendeeIp()
    {
        if (!ObjectUtils.isEmpty(busiMcuZj.getCucmIp()))
        {
            return busiMcuZj.getCucmIp();
        }
        return busiMcuZj.getIp();
    }

    public boolean isSourceTemplateChanged() {
        return sourceTemplateChanged;
    }

    public void setSourceTemplateChanged(boolean sourceTemplateChanged) {
        this.sourceTemplateChanged = sourceTemplateChanged;
    }
}
