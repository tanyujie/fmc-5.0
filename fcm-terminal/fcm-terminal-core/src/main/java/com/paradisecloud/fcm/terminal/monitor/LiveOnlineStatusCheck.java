package com.paradisecloud.fcm.terminal.monitor;

import com.paradisecloud.fcm.dao.mapper.BusiLiveMapper;
import com.paradisecloud.fcm.terminal.fs.cache.LiveBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.LiveBridge;
import com.sinhy.spring.BeanFactory;

public class LiveOnlineStatusCheck {

    public void check() {

        for (LiveBridge value : LiveBridgeCache.getInstance().getLiveBridgeMap().values()) {
            Boolean liveStatus = value.getLiveStatus(value.getBusiLive());
            if (liveStatus) {
                value.getBusiLive().setStatus(1);
            } else {
                value.getBusiLive().setStatus(2);
            }
            BeanFactory.getBean(BusiLiveMapper.class).updateBusiLive(value.getBusiLive());
        }
    }
}
