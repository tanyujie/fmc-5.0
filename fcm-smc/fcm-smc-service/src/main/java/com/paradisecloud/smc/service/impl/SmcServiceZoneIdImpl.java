package com.paradisecloud.smc.service.impl;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.com.fcm.smc.modle.DefaultServiceZoneIdRep;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import org.springframework.stereotype.Service;

/**
 * @author nj
 * @date 2022/8/23 10:58
 */
@Service
public class SmcServiceZoneIdImpl implements SmcServiceZoneId{


    @Override
    public DefaultServiceZoneIdRep getSmcServiceZoneId() {
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        String s=bridge.getSmcServiceZoneIdInvoker().getServiceZoneId(bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        DefaultServiceZoneIdRep defaultServiceZoneIdRep = JSON.parseObject(s, DefaultServiceZoneIdRep.class);
        return defaultServiceZoneIdRep;
    }
}
