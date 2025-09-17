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
import com.paradisecloud.fcm.common.enumer.DeviceType;
import com.paradisecloud.fcm.dao.model.BusiSmartRoomDevice;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**  
 * <pre>物联网关缓存</pre>
 * @author sinhy
 * @since 2021-10-19 18:06
 * @version V1.0  
 */
public class SmartRoomDeviceCache extends JavaCache<Long, BusiSmartRoomDevice>
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-10-19 18:06
     */
    private static final long serialVersionUID = 1L;
    private static final SmartRoomDeviceCache INSTANCE = new SmartRoomDeviceCache();
    // key: lotId + "_" + channel
    private Map<String, BusiSmartRoomDevice> lotDeviceMap = new ConcurrentHashMap<>();
    // key: lotId + "_" + channel
    private Map<String, Long> lotDeviceOnlineTimeMap = new ConcurrentHashMap<>();

    /**
     * <pre>构造方法</pre>
     * @author sinhy
     * @since 2021-01-22 18:07
     */
    private SmartRoomDeviceCache()
    {
    }
    
    public synchronized BusiSmartRoomDevice add(BusiSmartRoomDevice busiSmartRoomDevice) {
        if (busiSmartRoomDevice.getDeviceType() == DeviceType.LOT_DEVICE.getCode() && busiSmartRoomDevice.getLotId() != null) {
            lotDeviceMap.put(busiSmartRoomDevice.getLotId() + "_" + busiSmartRoomDevice.getLotChannel(), busiSmartRoomDevice);
        }
        return super.put(busiSmartRoomDevice.getId(), busiSmartRoomDevice);
    }
    
    public BusiSmartRoomDevice remove(Long id) {
        BusiSmartRoomDevice busiSmartRoomDeviceTemp = super.get(id);
        if (busiSmartRoomDeviceTemp != null && busiSmartRoomDeviceTemp.getDeviceType() == DeviceType.LOT_DEVICE.getCode() && busiSmartRoomDeviceTemp.getLotId() != null) {
            lotDeviceMap.remove(busiSmartRoomDeviceTemp.getLotId() + "_" + busiSmartRoomDeviceTemp.getLotChannel());
            lotDeviceOnlineTimeMap.remove(busiSmartRoomDeviceTemp.getLotId() + "_" + busiSmartRoomDeviceTemp.getLotChannel());
        }
        BusiSmartRoomDevice busiSmartRoomDevice = super.remove(id);
         return busiSmartRoomDevice;
    }

    public BusiSmartRoomDevice getLotDevice(long lotId, int channel) {
        return lotDeviceMap.get(lotId + "_" + channel);
    }

    public void updateLotDeviceOnlineTime(long lotId, int channel, long time) {
        lotDeviceOnlineTimeMap.put(lotId + "_" + channel, time);
    }

    public Long getLotDeviceOnlineTime(long lotId, int channel) {
        return lotDeviceOnlineTimeMap.get(lotId + "_" + channel);
    }

    public static SmartRoomDeviceCache getInstance()
    {
        return INSTANCE;
    }
}
