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
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDoorplate;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**  
 * <pre>电子门牌缓存</pre>
 * @author sinhy
 * @since 2021-10-19 18:06
 * @version V1.0  
 */
public class SmartRoomDoorplateCache extends JavaCache<Long, BusiSmartRoomDoorplate>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-10-19 18:06 
     */
    private static final long serialVersionUID = 1L;
    private static final SmartRoomDoorplateCache INSTANCE = new SmartRoomDoorplateCache();
    private boolean loadFinished = false;
    private boolean needUpdateMqStatus = false;
    private Map<String, BusiSmartRoomDoorplate> snMap = new ConcurrentHashMap<>();
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-01-22 18:07  
     */
    private SmartRoomDoorplateCache()
    {
    }
    
    public synchronized BusiSmartRoomDoorplate add(BusiSmartRoomDoorplate busiSmartRoomDoorplate) {
        snMap.put(busiSmartRoomDoorplate.getSn(), busiSmartRoomDoorplate);
        if (loadFinished) {
            setNeedUpdateMqStatus(true);
        }
        return super.put(busiSmartRoomDoorplate.getId(), busiSmartRoomDoorplate);
    }
    
    public BusiSmartRoomDoorplate remove(Long id) {
         BusiSmartRoomDoorplate busiSmartRoomDoorplate = super.remove(id);
         if (busiSmartRoomDoorplate != null) {
             snMap.remove(busiSmartRoomDoorplate.getSn());
         }
         return busiSmartRoomDoorplate;
    }

    public BusiSmartRoomDoorplate getBySn(String sn) {
        return snMap.get(sn);
    }

    public static SmartRoomDoorplateCache getInstance()
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
