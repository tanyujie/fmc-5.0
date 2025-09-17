package com.paradisecloud.fcm.web.core;

import com.paradisecloud.fcm.dao.mapper.BusiLiveBroadcastAppointmentMapMapper;
import com.paradisecloud.fcm.dao.mapper.BusiLiveBroadcastMapper;
import com.paradisecloud.fcm.dao.model.BusiLiveBroadcast;
import com.paradisecloud.fcm.dao.model.BusiLiveBroadcastAppointmentMap;
import com.paradisecloud.fcm.web.cache.LiveBroadcastCache;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author admin
 */
@Order(4)
@Component
public class LiveBroadcastCacheInitializer  implements ApplicationRunner {


    @Resource
    private BusiLiveBroadcastMapper busiLiveBroadcastMapper;
    @Resource
    private BusiLiveBroadcastAppointmentMapMapper busiLiveBroadcastAppointmentMapMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<BusiLiveBroadcast> busiLiveBroadcastList = busiLiveBroadcastMapper.selectBusiLiveBroadcastList(new BusiLiveBroadcast());
        for (BusiLiveBroadcast busiLiveBroadcast : busiLiveBroadcastList) {
            LiveBroadcastCache.getInstance().add(busiLiveBroadcast);
        }

        List<BusiLiveBroadcastAppointmentMap> busiLiveBroadcastAppointmentMaps = busiLiveBroadcastAppointmentMapMapper.selectBusiLiveBroadcastAppointmentMapList(new BusiLiveBroadcastAppointmentMap());
        for (BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMap : busiLiveBroadcastAppointmentMaps) {
            LiveBroadcastCache.getInstance().addMap(busiLiveBroadcastAppointmentMap);
        }
    }
}
