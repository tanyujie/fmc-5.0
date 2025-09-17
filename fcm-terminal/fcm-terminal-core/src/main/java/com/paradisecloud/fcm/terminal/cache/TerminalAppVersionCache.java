package com.paradisecloud.fcm.terminal.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.vo.TerminalAppVersion;

public class TerminalAppVersionCache extends JavaCache<Integer, TerminalAppVersion> {

    private static final TerminalAppVersionCache INSTANCE = new TerminalAppVersionCache();

    private TerminalAppVersionCache() {
    }

    public static TerminalAppVersionCache getInstance() {
        return INSTANCE;
    }
}
