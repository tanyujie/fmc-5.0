package com.paradisecloud.fcm.mqtt.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiFcmNumberSection;
import com.paradisecloud.fcm.mqtt.model.TerminalLive;
import com.paradisecloud.fcm.terminal.fs.cache.FcmAccountCacheAndUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TerminalLiveCache extends JavaCache<String, List<TerminalLive>> {
    private static final TerminalLiveCache INSTANCE = new TerminalLiveCache()
    {

    };

    private Map<String, List<TerminalLive>> terminalCache = new ConcurrentHashMap<>();

    private TerminalLiveCache(){}

    public List<TerminalLive> getById(String contextKey){
        return terminalCache.get(contextKey);
    }

    public void add(String contextKey,List terminalList){
        terminalCache.put(contextKey,terminalList);
    }

    public void update(String contextKey,List terminalList)
    {
        List oldFcmBridge = terminalCache.put(contextKey, terminalList);
        if (oldFcmBridge != null)
        {
            terminalCache.remove(contextKey);
        }

        terminalCache.put(contextKey, terminalList);
    }

    public void removeById(String contextKey)
    {
        terminalCache.remove(contextKey);
    }

    public static TerminalLiveCache getInstance()
    {
        return INSTANCE;
    }
}
