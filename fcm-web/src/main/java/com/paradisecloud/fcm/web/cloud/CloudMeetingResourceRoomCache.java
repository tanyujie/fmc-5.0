package com.paradisecloud.fcm.web.cloud;


import org.apache.logging.log4j.util.Strings;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CloudMeetingResourceRoomCache {

    private volatile Map<String, CloudMeetingResourceRoom> cloudMeetingResourceRoomMap;
    private volatile Map<Long, String> userSnMap;
    private static final CloudMeetingResourceRoomCache INSTANCE = new CloudMeetingResourceRoomCache();

    public CloudMeetingResourceRoomCache() {
        cloudMeetingResourceRoomMap=new ConcurrentHashMap<>();
        userSnMap=new ConcurrentHashMap<>();
    }
    public static CloudMeetingResourceRoomCache getInstance()
    {
        return INSTANCE;
    }

    public Map<String, CloudMeetingResourceRoom> getCloudMeetingResourceRoomMap() {
        return cloudMeetingResourceRoomMap;
    }

    public void put(String sn,CloudMeetingResourceRoom room) {
        if(Strings.isNotBlank(sn)){
            this.cloudMeetingResourceRoomMap.put(sn,room);
        }
    }

    public CloudMeetingResourceRoom get(String sn) {
       return this.cloudMeetingResourceRoomMap.get(sn);
    }

    public Map<Long, String> getUserSnMap() {
        return userSnMap;
    }


}
