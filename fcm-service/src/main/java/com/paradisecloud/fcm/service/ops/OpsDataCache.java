/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : EduClassCache.java
 * Package     : com.paradisecloud.fcm.fme.cache.edu
 * @author sinhy 
 * @since 2021-10-19 18:06
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.service.ops;

import com.paradisecloud.fcm.dao.model.vo.BusiOpsResourceVo;

import java.util.List;

/**
 * <pre>电子门牌缓存</pre>
 * @author sinhy
 * @since 2021-10-19 18:06
 * @version V1.0  
 */
public class OpsDataCache
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-10-19 18:06
     */
    private static final long serialVersionUID = 1L;
    private static final OpsDataCache INSTANCE = new OpsDataCache();
    private volatile boolean registered;
    private volatile String cloudToken;
    private List<BusiOpsResourceVo> resources;
    private volatile int cloudLiveTime;
    private volatile int asrTime;
    private volatile int tencentTime;
    private volatile int imTime;

    /**
     * <pre>构造方法</pre>
     * @author sinhy
     * @since 2021-01-22 18:07
     */
    private OpsDataCache()
    {
    }

    public static OpsDataCache getInstance()
    {
        return INSTANCE;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public String getCloudToken() {
        return cloudToken;
    }

    public void setCloudToken(String cloudToken) {
        this.cloudToken = cloudToken;
    }

    public List<BusiOpsResourceVo> getResources() {
        return resources;
    }

    public void setResources(List<BusiOpsResourceVo> resources) {
        this.resources = resources;
    }

    public int getCloudLiveTime() {
        return cloudLiveTime;
    }

    public void setCloudLiveTime(int cloudLiveTime) {
        this.cloudLiveTime = cloudLiveTime;
    }

    public int getAsrTime() {
        return asrTime;
    }

    public void setAsrTime(int asrTime) {
        this.asrTime = asrTime;
    }

    public int getTencentTime() {
        return tencentTime;
    }

    public void setTencentTime(int tencentTime) {
        this.tencentTime = tencentTime;
    }

    public int getImTime() {
        return imTime;
    }

    public void setImTime(int imTime) {
        this.imTime = imTime;
    }
}
