package com.paradisecloud.smc.service.delay.service.impl;


import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.smc.service.delay.service.DelayService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author nj
 */
@Service("SMC_CONFERENCE_AUTO_END")
public class SmcConferenceEndTimeDelayServiceImpl implements DelayService {


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean execute(String conferenceId) {

        SmcBridge bridge = SmcBridgeCache.getInstance().getConferenceBridge().get(conferenceId);
        bridge.getSmcConferencesInvoker().endConferences(conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        SmcBridgeCache.getInstance().removeConferenceBridge(conferenceId, bridge);

        return true;
    }
}
