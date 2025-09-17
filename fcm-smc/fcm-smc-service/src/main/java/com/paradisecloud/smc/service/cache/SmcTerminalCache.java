package com.paradisecloud.smc.service.cache;

import com.paradisecloud.fcm.dao.model.BusiTerminal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2022/8/30 15:24
 */
public class SmcTerminalCache {

    private static final SmcTerminalCache INSTANCE = new SmcTerminalCache();

    public static SmcTerminalCache getInstance() {
        return INSTANCE;
    }
    private Map<Long, BusiTerminal> originalTerminalMap = new ConcurrentHashMap<>();
    private Map<String, BusiTerminal> originalNumberTerminalMap = new ConcurrentHashMap<>();

    public Map<Long, BusiTerminal> getOriginalTerminalMap() {
        return originalTerminalMap;
    }

    public void setOriginalTerminalMap(Map<Long, BusiTerminal> originalTerminalMap) {
        this.originalTerminalMap = originalTerminalMap;
    }

    public Map<String, BusiTerminal> getOriginalNumberTerminalMap() {
        return originalNumberTerminalMap;
    }

    public void setOriginalNumberTerminalMap(Map<String, BusiTerminal> originalNumberTerminalMap) {
        this.originalNumberTerminalMap = originalNumberTerminalMap;
    }

    public synchronized  void put(Long id, BusiTerminal busiTerminal) {
        originalTerminalMap.put(id,busiTerminal);
    }

    public synchronized  void putNumber(String number, BusiTerminal busiTerminal) {
        originalNumberTerminalMap.put(number,busiTerminal);
    }
    public List<BusiTerminal> getCopiedOriginalTerminals()
    {
        return Collections.unmodifiableList(new ArrayList<>(originalTerminalMap.values()));
    }

    public List<BusiTerminal> getCopiedOriginalTerminalsNumber() {
        return Collections.unmodifiableList(new ArrayList<>(originalNumberTerminalMap.values()));
    }
}
