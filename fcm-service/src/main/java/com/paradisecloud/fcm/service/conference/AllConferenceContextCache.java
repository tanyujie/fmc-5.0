package com.paradisecloud.fcm.service.conference;

public class AllConferenceContextCache extends BaseConferenceContextCache<BaseConferenceContext> {

    private static final AllConferenceContextCache INSTANCE = new AllConferenceContextCache();

    public static AllConferenceContextCache getInstance() {
        return INSTANCE;
    }

}
