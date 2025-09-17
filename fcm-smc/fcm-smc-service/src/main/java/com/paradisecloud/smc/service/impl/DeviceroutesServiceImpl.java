package com.paradisecloud.smc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.smc.service.DeviceroutesService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author nj
 * @date 2022/8/23 10:51
 */
@Service
public class DeviceroutesServiceImpl implements DeviceroutesService {

    @Override
    public String getDeviceroutes(String zoneId) {
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        String deviceroutes = bridge.getSmcDeviceroutesInvoker().getDeviceroutes(zoneId, bridge.getSmcportalTokenInvoker().getSystemHeaders());
        List<String> strings = JSONArray.parseArray(deviceroutes, String.class);
        return  strings.get(0);
    }
}
