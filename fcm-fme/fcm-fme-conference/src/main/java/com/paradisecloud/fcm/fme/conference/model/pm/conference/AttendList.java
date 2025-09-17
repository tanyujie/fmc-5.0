/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ReCall.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy
 * @since 2021-09-27 21:48
 * @version  V1.0
 */
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.sinhy.proxy.ProxyMethod;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AttendList extends ProxyMethod {

    /**
     * <pre>构造方法</pre>
     *
     * @param method
     * @author sinhy
     * @since 2021-09-27 21:48
     */
    public AttendList(Method method) {
        super(method);
    }

    /**
     * <pre>根据Ip、域名、账号查询与会者列表</pre>
     *
     * @param conferenceId void
     * @author sinhy
     * @since 2021-09-27 21:47
     */
    public List<Attendee> attendeeList(String conferenceId, String searchKey) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        List<Attendee> attendList = new ArrayList<>();
        List<Attendee> conferenceAttendList = new ArrayList<>();
        conferenceAttendList.addAll(conferenceContext.getAttendees());
        for (List<Attendee> attendeeList : conferenceContext.getCascadeAttendeesMap().values()) {
            conferenceAttendList.addAll(attendeeList);
        }
        conferenceAttendList.addAll(conferenceContext.getMasterAttendees());
        for (Attendee attendee : conferenceAttendList) {
            boolean containsName = attendee.getName().contains(searchKey);
            boolean containsIpNew = attendee.getIpNew().contains(searchKey);
            String remotePartyNew = attendee.getRemotePartyNew();
            String credential = StringUtils.substringBefore(remotePartyNew, "@");
            boolean containsCredential = searchKey.contains(credential);
            if (containsIpNew || containsName || containsCredential) {
                attendList.add(attendee);
            }
        }
        return attendList;
    }
}
