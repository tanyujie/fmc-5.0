/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ConferenceMonitoringThread.java
 * Package     : com.paradisecloud.fcm.cdr.service.task
 * @author sinhy
 * @since 2021-12-24 15:22
 * @version  V1.0
 */
package com.paradisecloud.smc.service.core;

import com.paradisecloud.com.fcm.smc.modle.HistoryConferenceDetail;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.smc.service.UTCTimeFormatUtil;
import com.sinhy.utils.ThreadUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 会议监听线程
 *
 * @author sinhy
 * @version V1.0
 * @since 2021-12-24 15:22
 */
@Component
public class SmcHistoryConferenceMonitoringThread extends Thread implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Resource
    private BusiHistoryConferenceMapper busiHistoryConferenceMapper;


    @Override
    public void run() {
        Threads.sleep(20000);
        logger.info("ConferenceMonitoringThread start successfully!");
        while (true) {
            try {
                List<BusiHistoryConference> notEndConferences = busiHistoryConferenceMapper.selectNotEndHistoryConferenceList(McuType.SMC3.getCode());
                if (!ObjectUtils.isEmpty(notEndConferences)) {
                    for (BusiHistoryConference busiHistoryConference : notEndConferences) {
                        if (busiHistoryConference.getCoSpace() != null && busiHistoryConference.getCallLegProfileId() != null && busiHistoryConference.getNumber().length() == 7) {
                            SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(busiHistoryConference.getDeptId());
                            if (bridge == null) {
                                continue;
                            }
                            HistoryConferenceDetail historyConferenceDetail = bridge.getSmcConferencesInvoker().getConferencesHistoryDetailById(busiHistoryConference.getCallLegProfileId(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                            if (historyConferenceDetail != null) {
                                String scheduleEndTime = historyConferenceDetail.getScheduleEndTime();
                                if (Strings.isNotBlank(scheduleEndTime)) {
                                    busiHistoryConference.setDuration(historyConferenceDetail.getDuration());
                                    busiHistoryConference.setConferenceEndTime(UTCTimeFormatUtil.utcToLocal(scheduleEndTime));
                                    busiHistoryConference.setEndReasonsType(EndReasonsType.ABNORMAL_END);
                                    busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConference);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ThreadUtils.sleep(5000);
            }

        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
