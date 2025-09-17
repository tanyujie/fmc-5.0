package com.paradisecloud.fcm.mqtt.cache;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import org.springframework.boot.ApplicationRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lxl
 * @date 2022/6/27 17:34
 */
public class AppointmentCache extends JavaCache<String, BusiConferenceAppointment> {
    private static final long serialVersionUID = 1L;
    private static final AppointmentCache INSTANCE = new AppointmentCache();

    private Map<String, BusiConferenceAppointment> appointmentCache = new HashMap<>();

    public AppointmentCache()
    {
        super();
    }

    public static AppointmentCache getInstance()
    {
        return INSTANCE;
    }


    public Map<String, BusiConferenceAppointment> getAll(){
        return appointmentCache;
    }

    public synchronized BusiConferenceAppointment put(String key, BusiConferenceAppointment busiConferenceAppointment){

        if (busiConferenceAppointment != null && busiConferenceAppointment.getId() != null){
            if (busiConferenceAppointment.getParams() != null){
                appointmentCache.put(key, busiConferenceAppointment);
            }
        }
        return busiConferenceAppointment;
    }

    public BusiConferenceAppointment get(String key){
        if (appointmentCache.containsKey(key)){
            return appointmentCache.get(key);
        }
        return null;
    }

    public boolean remove(String key) {
        if (appointmentCache.containsKey(key)){
            appointmentCache.remove(key);
            return true;
        }
        return false;
    }

    public BusiConferenceAppointment update(String key, BusiConferenceAppointment busiConferenceAppointment){
        if (appointmentCache.containsKey(key)){
            appointmentCache.put(key, busiConferenceAppointment);
            return busiConferenceAppointment;
        }
        return null;
    }
}


