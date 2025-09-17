package com.paradisecloud.fcm.common.cache;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.common.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ExternalConfigCache {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String customerId = "FMC-APP-GENERAL";
    private String appVersionServerUrl = null;
    private String appFileRootUrl = null;
    private String region = null;
    private String autoLoginUser = null;
    private String autoLoginPassword = null;
    private boolean enableCdrAll = false;
    private String title = null;
    private String company = null;
    private String terminalTypeName_310 = null;
    private String fmcRootUrl = null;
    private String mcuType = null;
    private Integer mqttPort = null;
    private Double recordingFilesStorageSpaceMax = null;
    private String fsUserInfoDbIp = null;
    private String appSecret= null;
    private List<Integer> terminalTypeList = null;
    private Integer cascadeType = null;
    private String fmcRootUrlExternal = null;
    private String tencentEventToken = null;
    private Set<String> tmpCallInGkList = null;
    private Set<String> fmqIpList = null;
    private Set<String> MRAIpList = null;
    private String signInUrl = null;
    private String bookUrl = null;
    private String cloudUrl = null;
    private String cloudMqttIp = null;
    private Integer cloudMqttPort = null;
    private Integer cloudMqttUseSsl = null;
    private String opsVersion = null;
    private boolean disableService28281 = false;
    private String asrAppId = null;
    private String asrKeySecret = null;
    private boolean enableIm = false;
    private String dashscopeApiKey = null;
    private String dashscopeModel = null;

    private ExternalConfigCache() {
    }

    public static ExternalConfigCache getInstance() {
        ExternalConfigCache instance = InnerClass.INSTANCE;

        return instance;
    }

    public void initExternalConfig() {
        String filePath = PathUtil.getRootPath() + "/external_config.properties";System.err.println("####" + filePath);
        Properties properties = PropertiesUtil.readProperties(filePath);
        logger.info("external_config.properties:" + properties);
        if (properties != null) {
            setCustomerId(properties.getProperty("customerId"));
            setAppVersionServerUrl(properties.getProperty("appVersionServerUrl"));
            setAppFileRootUrl(properties.getProperty("appFileRootUrl"));
            setRegion(properties.getProperty("region"));
            setAutoLoginUser(properties.getProperty("autoLoginUser"));
            setAutoLoginPassword(properties.getProperty("autoLoginPassword"));
            String enableCdrAll = properties.getProperty("enableCdrAll");
            if (enableCdrAll != null && "true".equals(enableCdrAll)) {
                setEnableCdrAll(true);
            }
            setTitle(properties.getProperty("title"));
            setCompany(properties.getProperty("company"));
            setTerminalTypeName_310(properties.getProperty("terminalTypeName_310"));
            setFmcRootUrl(properties.getProperty("fmcRootUrl"));
            setMcuType(properties.getProperty("mcuType"));
            setOpsVersion(properties.getProperty("opsVersion"));
            String mqttPort = properties.getProperty("mqttPort");
            setTencentEventToken(properties.getProperty("tencentEventToken"));
            if (mqttPort != null) {
                try {
                    setMqttPort(Integer.valueOf(mqttPort));
                } catch (Exception e) {
                }
            }
            String recordingFilesStorageSpaceMax = properties.getProperty("recordingFilesStorageSpaceMax");
            if (recordingFilesStorageSpaceMax != null) {
                try {
                    setRecordingFilesStorageSpaceMax(Double.valueOf(recordingFilesStorageSpaceMax));
                } catch (Exception e) {
                }
            }
            setFsUserInfoDbIp(properties.getProperty("fsUserInfoDbIp"));
            setAppSecret(properties.getProperty("appSecret"));
            terminalTypeList = new ArrayList<>();
            String terminalTypeStr = properties.getProperty("terminalTypeList");
            if (terminalTypeStr != null && terminalTypeStr.trim().length() > 0) {
                String[] terminalTypeArr = terminalTypeStr.split(",");
                for (String terminalTypeT : terminalTypeArr) {
                    if (terminalTypeT.trim().length() > 0) {
                        try {
                            Integer terminalType = Integer.valueOf(terminalTypeT);
                            terminalTypeList.add(terminalType);
                        } catch (Exception e) {
                        }
                    }
                }
            }
            String cascadeType = properties.getProperty("cascadeType");
            if (cascadeType != null) {
                try {
                    setCascadeType(Integer.valueOf(cascadeType));
                } catch (Exception e) {
                }
            }
            setFmcRootUrlExternal(properties.getProperty("fmcRootUrlExternal"));
            tmpCallInGkList = new HashSet<>();
            String tmpCallInGkStr = properties.getProperty("tmpCallInGkList");
            if (tmpCallInGkStr != null && tmpCallInGkStr.trim().length() > 0) {
                String[] tmpCallInGkArr = tmpCallInGkStr.split(",");
                for (String tmpCallInGkT : tmpCallInGkArr) {
                    if (tmpCallInGkT.trim().length() > 0) {
                        tmpCallInGkList.add(tmpCallInGkT);
                    }
                }
            }
            setFmcRootUrlExternal(properties.getProperty("fmcRootUrlExternal"));
            fmqIpList = new HashSet<>();
            String fmqIpStr = properties.getProperty("fmqIpList");
            if (fmqIpStr != null && fmqIpStr.trim().length() > 0) {
                String[] fmqIpArr = fmqIpStr.split(",");
                for (String fmqIpT : fmqIpArr) {
                    if (fmqIpT.trim().length() > 0) {
                        fmqIpList.add(fmqIpT);
                    }
                }
            }
            MRAIpList = new HashSet<>();
            String mraIpStr = properties.getProperty("MRAIpList");
            if (mraIpStr != null && mraIpStr.trim().length() > 0) {
                String[] mraIpArr = mraIpStr.split(",");
                for (String marIpT : mraIpArr) {
                    if (marIpT.trim().length() > 0) {
                        MRAIpList.add(marIpT);
                    }
                }
            }
            setSignInUrl(properties.getProperty("signInUrl"));
            setBookUrl(properties.getProperty("bookUrl"));
            setCloudUrl(properties.getProperty("cloudUrl"));
            setCloudMqttIp(properties.getProperty("cloudMqttIp"));
            String cloudMqttPort = properties.getProperty("cloudMqttPort");
            if (cloudMqttPort != null) {
                try {
                    setCloudMqttPort(Integer.valueOf(cloudMqttPort));
                } catch (Exception e) {
                }
            }
            String cloudMqttUseSsl = properties.getProperty("cloudMqttUseSsl");
            if (cloudMqttUseSsl != null) {
                try {
                    setCloudMqttUseSsl(Integer.valueOf(cloudMqttUseSsl));
                } catch (Exception e) {
                }
            }
            String disableService28281 = properties.getProperty("disableService28281");
            if (disableService28281 != null && "true".equals(disableService28281)) {
                setDisableService28281(true);
            }
            setAsrAppId(properties.getProperty("asrAppId"));
            setAsrKeySecret(properties.getProperty("asrKeySecret"));
            String enableIm = properties.getProperty("enableIm");
            if (enableIm != null && "true".equals(enableIm)) {
                setEnableIm(true);
            }
            setDashscopeApiKey(properties.getProperty("dashscopeApiKey"));
            setDashscopeModel(properties.getProperty("dashscopeModel"));
        }
    }

    private static class InnerClass {
        private final static ExternalConfigCache INSTANCE = new ExternalConfigCache();
    }

    public String getCustomerId() {
        return customerId;
    }

    private void setCustomerId(String customerId) {
        if (StringUtils.isNotEmpty(customerId)) {
            this.customerId = customerId;
        }
    }

    public String getAppVersionServerUrl() {
        return appVersionServerUrl;
    }

    private void setAppVersionServerUrl(String appVersionServerUrl) {
        this.appVersionServerUrl = appVersionServerUrl;
    }

    public String getAppFileRootUrl() {
        return appFileRootUrl;
    }

    private void setAppFileRootUrl(String appFileRootUrl) {
        this.appFileRootUrl = appFileRootUrl;
    }

    public String getRegion() {
        return region;
    }

    private void setRegion(String region) {
        this.region = region;
    }

    public String getAutoLoginUser() {
        return autoLoginUser;
    }

    private void setAutoLoginUser(String autoLoginUser) {
        this.autoLoginUser = autoLoginUser;
    }

    public String getAutoLoginPassword() {
        return autoLoginPassword;
    }

    private void setAutoLoginPassword(String autoLoginPassword) {
        this.autoLoginPassword = autoLoginPassword;
    }

    public boolean isEnableCdrAll() {
        return enableCdrAll;
    }

    private void setEnableCdrAll(boolean enableCdrAll) {
        this.enableCdrAll = enableCdrAll;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    private void setCompany(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public String getTerminalTypeName_310() {
        return terminalTypeName_310;
    }

    public void setTerminalTypeName_310(String terminalTypeName_310) {
        this.terminalTypeName_310 = terminalTypeName_310;
    }

    public String getFmcRootUrl() {
        return fmcRootUrl;
    }

    public void setFmcRootUrl(String fmcRootUrl) {
        this.fmcRootUrl = fmcRootUrl;
    }

    public String getMcuType() {
        return mcuType;
    }

    public void setMcuType(String mcuType) {
        this.mcuType = mcuType;
    }

    public Integer getMqttPort() {
        return mqttPort;
    }

    public void setMqttPort(Integer mqttPort) {
        this.mqttPort = mqttPort;
    }

    public Double getRecordingFilesStorageSpaceMax() {
        return recordingFilesStorageSpaceMax;
    }

    public void setRecordingFilesStorageSpaceMax(Double recordingFilesStorageSpaceMax) {
        this.recordingFilesStorageSpaceMax = recordingFilesStorageSpaceMax;
    }

    public String getFsUserInfoDbIp() {
        return fsUserInfoDbIp;
    }

    public void setFsUserInfoDbIp(String fsUserInfoDbIp) {
        this.fsUserInfoDbIp = fsUserInfoDbIp;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public List<Integer> getTerminalTypeList() {
        return terminalTypeList;
    }

    public void setTerminalTypeList(List<Integer> terminalTypeList) {
        this.terminalTypeList = terminalTypeList;
    }

    public Integer getCascadeType() {
        return cascadeType;
    }

    public void setCascadeType(Integer cascadeType) {
        this.cascadeType = cascadeType;
    }

    public String getFmcRootUrlExternal() {
        return fmcRootUrlExternal;
    }

    public void setFmcRootUrlExternal(String fmcRootUrlExternal) {
        this.fmcRootUrlExternal = fmcRootUrlExternal;
    }

    public Set<String> getTmpCallInGkList() {
        return tmpCallInGkList;
    }

    public void setTmpCallInGkList(Set<String> tmpCallInGkList) {
        this.tmpCallInGkList = tmpCallInGkList;
    }

    public Set<String> getFmqIpList() {
        return fmqIpList;
    }

    public void setFmqIpList(Set<String> fmqIpList) {
        this.fmqIpList = fmqIpList;
    }

    public Set<String> getMRAIpList() {
        return MRAIpList;
    }

    public void setMRAIpList(Set<String> MRAIpList) {
        this.MRAIpList = MRAIpList;
    }

    public String getTencentEventToken() {
        return tencentEventToken;
    }

    public void setTencentEventToken(String tencentEventToken) {
        this.tencentEventToken = tencentEventToken;
    }

    public String getSignInUrl() {
        return signInUrl;
    }

    public void setSignInUrl(String signInUrl) {
        this.signInUrl = signInUrl;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public String getCloudUrl() {
        return cloudUrl;
    }

    public void setCloudUrl(String cloudUrl) {
        this.cloudUrl = cloudUrl;
    }

    public String getCloudMqttIp() {
        return cloudMqttIp;
    }

    public void setCloudMqttIp(String cloudMqttIp) {
        this.cloudMqttIp = cloudMqttIp;
    }

    public Integer getCloudMqttPort() {
        return cloudMqttPort;
    }

    public void setCloudMqttPort(Integer cloudMqttPort) {
        this.cloudMqttPort = cloudMqttPort;
    }

    public Integer getCloudMqttUseSsl() {
        return cloudMqttUseSsl;
    }

    public void setCloudMqttUseSsl(Integer cloudMqttUseSsl) {
        this.cloudMqttUseSsl = cloudMqttUseSsl;
    }

    public String getOpsVersion() {
        return opsVersion;
    }

    public void setOpsVersion(String opsVersion) {
        this.opsVersion = opsVersion;
    }

    public boolean isDisableService28281() {
        return disableService28281;
    }

    public void setDisableService28281(boolean disableService28281) {
        this.disableService28281 = disableService28281;
    }

    public String getAsrAppId() {
        return asrAppId;
    }

    public void setAsrAppId(String asrAppId) {
        this.asrAppId = asrAppId;
    }

    public String getAsrKeySecret() {
        return asrKeySecret;
    }

    public void setAsrKeySecret(String asrKeySecret) {
        this.asrKeySecret = asrKeySecret;
    }

    public boolean isEnableIm() {
        return enableIm;
    }

    public void setEnableIm(boolean enableIm) {
        this.enableIm = enableIm;
    }

    public String getDashscopeApiKey() {
        return dashscopeApiKey;
    }

    public void setDashscopeApiKey(String dashscopeApiKey) {
        this.dashscopeApiKey = dashscopeApiKey;
    }

    public String getDashscopeModel() {
        return dashscopeModel;
    }

    public void setDashscopeModel(String dashscopeModel) {
        this.dashscopeModel = dashscopeModel;
    }
}
