package com.paradisecloud.fcm.terminal.fs.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiLiveSetting;

public class LiveSettingCache extends JavaCache<Long, BusiLiveSetting> {
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-01-28 13:36
     */
    private static final long serialVersionUID = 1L;
    private static final LiveSettingCache INSTANCE = new LiveSettingCache();

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2021-01-22 18:07
     */
    private LiveSettingCache()
    {
    }

    public static LiveSettingCache getInstance()
    {
        return INSTANCE;
    }

    public BusiLiveSetting getByUrl(String url) {
        BusiLiveSetting busiLiveSetting = null;
        if (INSTANCE != null && INSTANCE.size() > 0) {
            for (BusiLiveSetting value : INSTANCE.values()) {
                if (value.getUrl().equals(url)) {
                    busiLiveSetting = value;
                }
            }
        }
        return busiLiveSetting;
    }
}
