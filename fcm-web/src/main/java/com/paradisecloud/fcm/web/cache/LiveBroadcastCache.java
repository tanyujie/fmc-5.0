package com.paradisecloud.fcm.web.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiLiveBroadcast;
import com.paradisecloud.fcm.dao.model.BusiLiveBroadcastAppointmentMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author admin
 */
public class LiveBroadcastCache extends JavaCache<Long, BusiLiveBroadcast> {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-10-19 18:06
     */
    private static final long serialVersionUID = 1L;
    private static final LiveBroadcastCache INSTANCE = new LiveBroadcastCache();
    private Map<Long, BusiLiveBroadcastAppointmentMap> liveBroadcastAppointmentMap = new ConcurrentHashMap();

    /**
     * <pre>构造方法</pre>
     * @author sinhy
     * @since 2021-01-22 18:07
     */
    private LiveBroadcastCache()
    {
    }

    public synchronized BusiLiveBroadcast add(BusiLiveBroadcast busiLiveBroadcast) {
        return super.put(busiLiveBroadcast.getId(), busiLiveBroadcast);
    }

    public BusiLiveBroadcast remove(Long id) {
        BusiLiveBroadcast busiLiveBroadcast = super.remove(id);
        if (liveBroadcastAppointmentMap.containsKey(id)) {
            BusiLiveBroadcastAppointmentMap remove = liveBroadcastAppointmentMap.remove(id);
        }
        return busiLiveBroadcast;
    }

    public static LiveBroadcastCache getInstance()
    {
        return INSTANCE;
    }

    public synchronized BusiLiveBroadcastAppointmentMap addMap(BusiLiveBroadcastAppointmentMap busiLiveBroadcastAppointmentMap) {
        return liveBroadcastAppointmentMap.put(busiLiveBroadcastAppointmentMap.getLiveBroadcastId(), busiLiveBroadcastAppointmentMap);
    }

    public BusiLiveBroadcastAppointmentMap removeMap(Long id) {
        return liveBroadcastAppointmentMap.remove(id);
    }

    public BusiLiveBroadcastAppointmentMap getMapById(Long id) {
        if (liveBroadcastAppointmentMap.containsKey(id)) {
            return liveBroadcastAppointmentMap.get(id);
        }
        return null;
    }
}
