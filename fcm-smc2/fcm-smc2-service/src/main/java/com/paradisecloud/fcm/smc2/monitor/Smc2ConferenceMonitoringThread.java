package com.paradisecloud.fcm.smc2.monitor;

import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.dao.mapper.BusiMcuSmc2TemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2TemplateConference;
import com.paradisecloud.fcm.smc2.cache.Smc2Bridge;
import com.paradisecloud.fcm.smc2.cache.Smc2BridgeCache;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.setvice2.interfaces.IBusiMcuSmc2HistoryConferenceService;
import com.paradisecloud.fcm.smc2.utils.Smc2ThreadPool;
import com.sinhy.spring.BeanFactory;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.ConferenceStatusEx;
import com.suntek.smc.esdk.pojo.local.TPSDKResponseEx;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2023/6/29 9:29
 */
@Component
public class Smc2ConferenceMonitoringThread extends Thread implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private IBusiMcuSmc2HistoryConferenceService busiMcuSmc2HistoryConferenceService;

    @Override
    public void run() {
        Threads.sleep(20 * 1000);
        Map<Long, Smc2Bridge> smc2BridgeMap = Smc2BridgeCache.getInstance().getSmc2BridgeMap();
        if (smc2BridgeMap == null) {
            return;
        }
        logger.info("Smc2ConferenceMonitoringThread start successfully!");
        Smc2ThreadPool.exec(() -> {
            try {
                List<BusiHistoryConference> busiSmc2HistoryConferences = busiMcuSmc2HistoryConferenceService.selectBusiHistoryConferenceListNotEnd();
                for (BusiHistoryConference busiSmc2HistoryConference : busiSmc2HistoryConferences) {

                    BusiMcuSmc2TemplateConference busiMcuSmc2TemplateConference = BeanFactory.getBean(BusiMcuSmc2TemplateConferenceMapper.class).selectBusiMcuSmc2TemplateConferenceById(Long.valueOf(busiSmc2HistoryConference.getCallLegProfileId()));
                    if (busiMcuSmc2TemplateConference != null) {
                        String lastConferenceId = busiMcuSmc2TemplateConference.getLastConferenceId();
                        if (Strings.isNotBlank(lastConferenceId)) {
                            Smc2ConferenceContext smc2ConferenceContext = Smc2ConferenceContextCache.getInstance().get(lastConferenceId);
                            if (smc2ConferenceContext == null) {
                                ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                                List<String> list = new ArrayList<>();
                                list.add(busiMcuSmc2TemplateConference.getSmcTemplateId());
                                TPSDKResponseEx<List<ConferenceStatusEx>> result = conferenceServiceEx.queryConferencesStatusEx(list);
                                //会议不存在
                                if (301989891 == result.getResultCode()) {
                                    busiSmc2HistoryConference.setConferenceEndTime(new Date());
                                    busiSmc2HistoryConference.setEndReasonsType(EndReasonsType.ABNORMAL_END);
                                    busiMcuSmc2HistoryConferenceService.updateBusiHistoryConference(busiSmc2HistoryConference);
                                }
                            }
                        }

                    }

                }
            } catch (Exception e) {
                logger.info("Smc2ConferenceMonitoringThread error!",e);
            }

        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
