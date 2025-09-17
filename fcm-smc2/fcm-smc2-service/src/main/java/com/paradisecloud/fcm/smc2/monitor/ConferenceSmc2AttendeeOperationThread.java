package com.paradisecloud.fcm.smc2.monitor;

import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.attendee.operation.TalkPrivateAttendeeOperation;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author nj
 * @date 2022/9/5 15:21
 */
@Component
public class ConferenceSmc2AttendeeOperationThread extends Thread implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());


    public static List<AttendeeSmc2> list=new ArrayList<>();

    public static List<AttendeeSmc2> getList() {
        return list;
    }

    public static void add(AttendeeSmc2 attendeeSmc2) {
        list.add(attendeeSmc2);
    }

    @Override
    public void run() {

        while (true) {

            try {
                Collection<Smc2ConferenceContext> values = Smc2ConferenceContextCache.getInstance().values();

                if (CollectionUtils.isNotEmpty(values)) {

                    for (Smc2ConferenceContext conferenceContext : values) {

                        if (conferenceContext != null && conferenceContext.isStart()) {
                                try {
                                    AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
                                    if (attendeeOperation != null) {
                                        try {
                                            if(attendeeOperation instanceof TalkPrivateAttendeeOperation){
                                                Iterator<AttendeeSmc2> iterator = list.iterator();
                                                while (iterator.hasNext()) {
                                                    AttendeeSmc2 attendeeSmc2 = iterator.next();
                                                    ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
                                                    Integer integer = conferenceServiceEx.setVideoSourceEx(conferenceContext.getSmc2conferenceId(), attendeeSmc2.getRemoteParty(), attendeeSmc2.getRemoteParty(), 0);
                                                    if(integer==0){
                                                        iterator.remove();
                                                    }

                                                }
                                            }
                                        } catch (Exception e) {
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    logger.info(e.getMessage());
                                }
                        }

                    }

                }
            } catch (Exception e) {
                logger.info(e.getMessage());
            } finally {
                Threads.sleep(200);
            }

        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
