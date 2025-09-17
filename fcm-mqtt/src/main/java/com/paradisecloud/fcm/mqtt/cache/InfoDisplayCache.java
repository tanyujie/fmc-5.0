package com.paradisecloud.fcm.mqtt.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiInfoDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InfoDisplayCache extends JavaCache<Long, BusiInfoDisplay> {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-10-19 18:06
     */
    private static final long serialVersionUID = 1L;
    private static final InfoDisplayCache INSTANCE = new InfoDisplayCache();
    // key:deptId
    private Map<Long, List<BusiInfoDisplay>> deptIdInfoDisplayMap = new ConcurrentHashMap();

    /**
     * <pre>构造方法</pre>
     * @author sinhy
     * @since 2021-01-22 18:07
     */
    private InfoDisplayCache()
    {
    }

    public synchronized BusiInfoDisplay add(BusiInfoDisplay busiLiveBroadcast) {
        super.put(busiLiveBroadcast.getId(), busiLiveBroadcast);
        boolean containsKey = deptIdInfoDisplayMap.containsKey(busiLiveBroadcast.getDeptId());
        if (containsKey) {
            List<BusiInfoDisplay> busiInfoDisplayList = deptIdInfoDisplayMap.get(busiLiveBroadcast.getDeptId());
            busiInfoDisplayList.removeIf(infoDisplay -> infoDisplay.getId().longValue() == busiLiveBroadcast.getId().longValue());
            busiInfoDisplayList.add(busiLiveBroadcast);
        } else {
            List<BusiInfoDisplay> busiInfoDisplayList = new ArrayList<>();
            busiInfoDisplayList.add(busiLiveBroadcast);
            deptIdInfoDisplayMap.put(busiLiveBroadcast.getDeptId(), busiInfoDisplayList);
        }
        return busiLiveBroadcast;
    }

    public BusiInfoDisplay remove(Long id) {
        BusiInfoDisplay busiInfoDisplay = super.get(id);
        Long deptId = busiInfoDisplay.getDeptId();
        boolean containsKey = deptIdInfoDisplayMap.containsKey(deptId);
        if (containsKey) {
            List<BusiInfoDisplay> busiInfoDisplayList = deptIdInfoDisplayMap.get(deptId);
            busiInfoDisplayList.removeIf(infoDisplay -> infoDisplay.getId().longValue() == id);
        }
        BusiInfoDisplay busiLiveBroadcast = super.remove(id);
        return busiLiveBroadcast;
    }

    public static InfoDisplayCache getInstance()
    {
        return INSTANCE;
    }

    public synchronized List<BusiInfoDisplay> getByDeptId(Long deptId) {
        return deptIdInfoDisplayMap.get(deptId);
    }
}
