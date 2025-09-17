package com.paradisecloud.fcm.service.conference.cascade;

import com.paradisecloud.common.cache.JavaCache;


/**
 * @author nj
 * @date 2023/8/3 16:47
 */
public class ProcessContextCache extends JavaCache<String, ProcessContext> {

    private static final long serialVersionUID = 1L;

    private static final ProcessContextCache INSTANCE = new ProcessContextCache();


    public static ProcessContextCache getInstance() {
        return INSTANCE;
    }

    public void add(ProcessContext processContext) {
        super.put(processContext.getMainConferenceId(), processContext);
    }

}
