/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : EduClassCache.java
 * Package     : com.paradisecloud.fcm.fme.cache.edu
 * @author sinhy 
 * @since 2021-10-19 18:06
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.ops.cloud.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiOps;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**  
 * <pre>OPS缓存</pre>
 * @author sinhy
 * @since 2021-10-19 18:06
 * @version V1.0  
 */
public class OpsCache extends JavaCache<Long, BusiOps>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-10-19 18:06
     */
    private static final long serialVersionUID = 1L;
    private static final OpsCache INSTANCE = new OpsCache();
    private boolean loadFinished = false;
    private boolean needUpdateMqStatus = false;
    private Map<String, BusiOps> snMap = new ConcurrentHashMap<>();
    private Map<Long, Long> registerLastPushTimeMap = new ConcurrentHashMap<>();

    /**
     * <pre>构造方法</pre>
     * @author sinhy
     * @since 2021-01-22 18:07
     */
    private OpsCache()
    {
    }
    
    public synchronized BusiOps add(BusiOps busiOps) {
        if (StringUtils.isNotEmpty(busiOps.getSn())) {
            BusiOps bySn = getBySn(busiOps.getSn());
            if (bySn == null) {
                setNeedUpdateMqStatus(true);
            }
        }
        snMap.put(busiOps.getSn(), busiOps);
        if (loadFinished) {
            setNeedUpdateMqStatus(true);
        }
        return super.put(busiOps.getId(), busiOps);
    }
    
    public BusiOps remove(Long id) {
         BusiOps busiOps = super.remove(id);
         if (busiOps != null) {
             snMap.remove(busiOps.getSn());
         }
         return busiOps;
    }

    public BusiOps getBySn(String sn) {
        return snMap.get(sn);
    }

    public static OpsCache getInstance()
    {
        return INSTANCE;
    }

    public boolean isLoadFinished() {
        return loadFinished;
    }

    public void setLoadFinished() {
        this.loadFinished = true;
    }

    public boolean isNeedUpdateMqStatus() {
        if (loadFinished) {
            return needUpdateMqStatus;
        }
        return false;
    }

    public void setNeedUpdateMqStatus(boolean needUpdateMqStatus) {
        this.needUpdateMqStatus = needUpdateMqStatus;
    }

    public void setRegisterLastPushTime(Long opsId, Long time) {
        registerLastPushTimeMap.put(opsId, time);
    }

    public Long getRegisterLastPushTime(Long opsId) {
        return registerLastPushTimeMap.get(opsId);
    }
}
