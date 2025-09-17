package com.paradisecloud.smc3.invoker;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.smc3.model.response.SmcCreateTemplateRep;

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
