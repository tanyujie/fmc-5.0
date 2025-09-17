package com.paradisecloud.fcm.huaweicloud.huaweicloud.event;

import com.paradisecloud.common.cache.JavaCache;


import java.util.HashMap;


/**
 * @author nj
 * @date 2024/3/20 10:31
 */
public class WaitingParticipantCache  extends JavaCache<String, HashMap<String,Object>> {

    private static final WaitingParticipantCache INSTANCE = new WaitingParticipantCache();

    public static WaitingParticipantCache getInstance() {
        return INSTANCE;
    }


}
