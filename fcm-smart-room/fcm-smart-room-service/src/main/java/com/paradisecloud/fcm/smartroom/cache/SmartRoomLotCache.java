/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : EduClassCache.java
 * Package     : com.paradisecloud.fcm.fme.cache.edu
 * @author sinhy 
 * @since 2021-10-19 18:06
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.smartroom.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomLot;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**  
 * <pre>物联网关缓存</pre>
 * @author sinhy
 * @since 2021-10-19 18:06
 * @version V1.0  
 */
public class SmartRoomLotCache extends JavaCache<Long, BusiSmartRoomLot>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-10-19 18:06
     */
    private static final long serialVersionUID = 1L;
    private static final SmartRoomLotCache INSTANCE = new SmartRoomLotCache();
    private boolean loadFinished = false;
    private boolean needUpdateMqStatus = false;
    private Map<String, BusiSmartRoomLot> clientIdMap = new ConcurrentHashMap<>();

    /**
     * <pre>构造方法</pre>
     * @author sinhy
     * @since 2021-01-22 18:07
     */
    private SmartRoomLotCache()
    {
    }
    
    public synchronized BusiSmartRoomLot add(BusiSmartRoomLot busiSmartRoomLot) {
        if (StringUtils.isNotEmpty(busiSmartRoomLot.getClientId())) {
            clientIdMap.put(busiSmartRoomLot.getClientId(), busiSmartRoomLot);
        }
        return super.put(busiSmartRoomLot.getId(), busiSmartRoomLot);
    }
    
    public BusiSmartRoomLot remove(Long id) {
        BusiSmartRoomLot busiSmartRoomLotTemp = super.get(id);
        if (busiSmartRoomLotTemp != null && StringUtils.isNotEmpty(busiSmartRoomLotTemp.getClientId())) {
            clientIdMap.remove(busiSmartRoomLotTemp.getClientId());
        }
        BusiSmartRoomLot busiSmartRoomLot = super.remove(id);
         return busiSmartRoomLot;
    }

    public BusiSmartRoomLot getByClientId(String clientId) {
        return clientIdMap.get(clientId);
    }

    public static SmartRoomLotCache getInstance()
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
}
