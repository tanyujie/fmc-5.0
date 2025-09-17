package com.paradisecloud.fcm.service.conference;

import com.paradisecloud.common.cache.JavaCache;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.dao.model.BusiTerminal;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * key:[模板ID:MCU类型] ex.1:fme
 */
public class BaseConferenceContextCache<T extends BaseConferenceContext> extends JavaCache<String, T> {

    /**
     * key 会议号 subKey [模板ID:MCU类型]
     */
    private Map<String, Map<String, T>> conferenceNumMap = new ConcurrentHashMap<>();

    /**
     * key 全会议号 subKey [模板ID:MCU类型]
     */
    private Map<String, Map<String, T>> fullConferenceNumMap = new ConcurrentHashMap<>();

    /**
     * key 会议名 subKey [模板ID:MCU类型]
     */
    private Map<String, Map<String, T>> conferenceNameMap = new ConcurrentHashMap<>();

    @Override
    public T put(String key, T value) {
        T context = super.put(key, value);
        context = super.get(key);
        if (context == null) {
            return null;
        }
        {
            Map<String, T> contextMap = conferenceNumMap.get(context.getConferenceNumber());
            if (contextMap == null) {
                contextMap = new ConcurrentHashMap<>();
                conferenceNumMap.put(context.getConferenceNumber(), contextMap);
            }
            contextMap.put(key, context);
        }
        {
            Map<String, T> contextMap = fullConferenceNumMap.get(context.getTenantId() + context.getConferenceNumber());
            if (contextMap == null) {
                contextMap = new ConcurrentHashMap<>();
                fullConferenceNumMap.put(context.getTenantId() + context.getConferenceNumber(), contextMap);
            }
            contextMap.put(key, context);
        }
        {
            Map<String, T> contextMap = conferenceNameMap.get(context.getName());
            if (contextMap == null) {
                contextMap = new ConcurrentHashMap<>();
                conferenceNameMap.put(context.getName(), contextMap);
            }
            contextMap.put(key, context);
        }
        if (!(this instanceof AllConferenceContextCache)) {
            AllConferenceContextCache.getInstance().put(key, context);
        }
        return context;
    }

    @Override
    public T remove(Object key) {
        T context = super.get(key);
        super.remove(key);
        if (context == null) {
            return null;
        }
        {
            Map<String, T> contextMap = conferenceNumMap.get(context.getConferenceNumber());
            if (contextMap != null) {
                contextMap.remove(key);
                if (contextMap.isEmpty()) {
                    conferenceNumMap.remove(context.getConferenceNumber());
                }
            }
        }
        {
            Map<String, T> contextMap = fullConferenceNumMap.get(context.getTenantId() + context.getConferenceNumber());
            if (contextMap != null) {
                contextMap.remove(key);
                if (contextMap.isEmpty()) {
                    fullConferenceNumMap.remove(context.getTenantId() + context.getConferenceNumber());
                }
            }
        }
        {
            Map<String, T> contextMap = conferenceNameMap.get(context.getName());
            if (contextMap != null) {
                contextMap.remove(key);
                if (contextMap.isEmpty()) {
                    conferenceNameMap.remove(context.getName());
                }
            }
        }
        AllConferenceContextCache.getInstance().remove(key);
        return context;
    }

    public Collection<T> getConferenceContextListByConferenceNum(String conferenceNum) {
        Map<String, T> map = conferenceNumMap.get(conferenceNum);
        if (map != null && !map.isEmpty()) {
            return map.values();
        }
        return null;
    }

    public Collection<T> getConferenceContextListByFullConferenceNum(String fullConferenceNum) {
        Map<String, T> map = fullConferenceNumMap.get(fullConferenceNum);
        if (map != null && !map.isEmpty()) {
            return map.values();
        }
        return null;
    }

    /**
     * 是否已经存在会议名
     *
     * @param conferenceName
     * @param mcuType
     * @param mcuId
     * @return
     */
    public boolean hasConferenceName(String conferenceName, McuType mcuType, long mcuId) {
        Map<String, T> contextMap = conferenceNameMap.get(conferenceName);
        if (contextMap != null) {
            for (T context : contextMap.values()) {
                if (context.getMcuType().equals(mcuType) && context.getMcuId() == mcuId) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param fullConferenceNum 租户号（tenantId：通常为""） + 会议号
     * @param busiTerminal
     * @return
     */
    public T getContext(String fullConferenceNum, BusiTerminal busiTerminal) {
        if (busiTerminal != null) {
            Collection<T> conferenceContexts = getConferenceContextListByFullConferenceNum(fullConferenceNum);
            if (conferenceContexts != null) {
                for (T conferenceContext : conferenceContexts) {
                    BaseAttendee attendee = conferenceContext.getAttendeeByTerminalId(busiTerminal.getId());
                    if (attendee != null && attendee.isMeetingJoined()) {
                        return conferenceContext;
                    }
                }
            }
        }
        return null;
    }

    /**
     *
     * @param fullConferenceNum 租户号（tenantId：通常为""） + 会议号
     * @return
     */
    public T getFirstContext(String fullConferenceNum) {
        Collection<T> conferenceContexts = getConferenceContextListByFullConferenceNum(fullConferenceNum);
        if (conferenceContexts != null) {
            for (T conferenceContext : conferenceContexts) {
                return conferenceContext;
            }
        }
        return null;
    }
}
