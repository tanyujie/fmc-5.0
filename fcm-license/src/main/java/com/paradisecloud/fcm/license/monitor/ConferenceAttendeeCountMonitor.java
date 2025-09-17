/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : BookingConferenceTask.java
 * Package     : com.paradisecloud.fcm.fme.conference.task
 * @author lilinhai
 * @since 2021-05-20 18:47
 * @version  V1.0
 */
package com.paradisecloud.fcm.license.monitor;


import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.dao.model.BusiFmeCluster;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.FmeClusterCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.FmeBridgeCluster;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;
import com.paradisecloud.fcm.fme.model.cms.callleg.CallLegConfiguration;
import com.paradisecloud.fcm.fme.model.response.callleg.CallLegsResponse;
import com.paradisecloud.fcm.license.LicenseExecutor;
import com.paradisecloud.fcm.license.LicenseManagerHolder;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.sinhy.utils.ThreadUtils;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

//@Component
public class ConferenceAttendeeCountMonitor extends Thread implements InitializingBean {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    private int maxAttendeeCount_1080P;
    private int maxAttendeeCount_720P;


    @Override
    public void run() {
        logger.info("ConferenceAttendeeCountMonitor 启动成功！");


        ThreadUtils.sleep(3 * 1000);

        while (true) {
            Boolean flag = true;
            LicenseContent licenseContent = null;
            try {
                LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
                licenseContent = licenseManager.verify();

            } catch (Exception e) {
                flag = false;
                LicenseExecutor.execParticipantLimit(0);

            }


            try {

                if (licenseContent != null) {
                    Map extra = (Map<String, Object>) licenseContent.getExtra();
                    boolean schedule = (boolean) extra.get("schedule");
                    int limit = (int) extra.get("participantLimit");
                    if (schedule) {
                        String participantLimitTime = (String) extra.get("participantLimitTime");
                        long willTime = DateUtil.convertDateByString(participantLimitTime, null).getTime();
                        if (System.currentTimeMillis() - willTime >= 0) {
                            flag = false;
                            LicenseExecutor.execParticipantLimit(0);
                        }
                    }

                    if (flag) {
                        this.maxAttendeeCount_1080P = limit / 2;
                        this.maxAttendeeCount_720P = limit;
                        int cout1080 = 0;

                        Collection<BaseConferenceContext> values = AllConferenceContextCache.getInstance().values();
                        if (CollectionUtils.isEmpty(values)) {
                            LicenseExecutor.execParticipantLimit(maxAttendeeCount_720P + 1);
                        } else {
                            for (BaseConferenceContext value : values) {
                                if(value instanceof ConferenceContext){
                                    ConferenceContext conferenceContext=(ConferenceContext)value;
                                    for (Attendee attendee : conferenceContext.getAttendees()) {
                                        String participantUuid = attendee.getParticipantUuid();
                                        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
                                        CallLegsResponse callLegsResponse = fmeBridge.getCallLegInvoker().getCallLegs(participantUuid);
                                        ArrayList<CallLeg> callLegs = callLegsResponse.getCallLegs().getCallLeg();

                                        for (CallLeg callLeg : callLegs) {
                                            CallLegConfiguration configuration = callLeg.getConfiguration();
                                            String qualityMain = configuration.getQualityMain();
                                            if (Objects.equals(qualityMain, "1080p")) {
                                                cout1080++;
                                            }
                                        }

                                    }
                                }

                            }
                            if (cout1080 >= maxAttendeeCount_1080P) {
                                //数量限制
                                LicenseExecutor.execParticipantLimit(maxAttendeeCount_1080P + 1);
                            } else {
                                LicenseExecutor.execParticipantLimit(maxAttendeeCount_720P - (cout1080 * 2) + 1);
                            }
                        }
                    }

                }

            } catch (Throwable e) {
                logger.error("ConferenceAttendeeCountMonitor", e);
                break;
            } finally {
                ThreadUtils.sleep(1000);
            }
        }
    }




    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
