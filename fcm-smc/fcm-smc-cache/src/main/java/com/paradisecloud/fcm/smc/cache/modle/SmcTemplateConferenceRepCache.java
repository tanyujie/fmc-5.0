package com.paradisecloud.fcm.smc.cache.modle;

import com.paradisecloud.com.fcm.smc.modle.SmcConferenceContextBase;
import com.paradisecloud.com.fcm.smc.modle.response.SmcCreateTemplateRep;
import com.paradisecloud.common.cache.JavaCache;

/**
 * @author nj
 * @date 2022/9/20 11:46
 */
public class SmcTemplateConferenceRepCache extends JavaCache<String, SmcCreateTemplateRep> {

    public SmcTemplateConferenceRepCache() {
    }

    private static final SmcTemplateConferenceRepCache INSTANCE = new SmcTemplateConferenceRepCache();
    public static SmcTemplateConferenceRepCache getInstance()
    {
        return INSTANCE;
    }
}
