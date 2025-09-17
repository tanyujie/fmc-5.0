package com.paradisecloud.fcm.fme.conference.scheduler;

import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.enumer.AttendType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.utils.ConferenceContextUtils;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.InvitedAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.system.dao.mapper.SysConfigMapper;
import com.paradisecloud.system.dao.model.SysConfig;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author nj
 * @date 2024/7/10
 */
@Component
public class ConferenceGenKeyFrameThread extends Thread implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private int refreshtimeime = 0;

    @Override
    public void run() {

        while (true) {


            try {
                SysConfigMapper sysConfigMapper = BeanFactory.getBean(SysConfigMapper.class);
                SysConfig sysConfig_q = new SysConfig();
                sysConfig_q.setConfigKey("conference.key.frame");
                SysConfig sysConfig = sysConfigMapper.selectConfig(sysConfig_q);
                if (sysConfig != null) {
                    String configValue = sysConfig.getConfigValue();
                    if (Strings.isNotBlank(configValue)) {
                        refreshtimeime = Integer.valueOf(configValue);
                    }
                }

                if (refreshtimeime != 0) {
                    Collection<ConferenceContext> values = ConferenceContextCache.getInstance().values();

                    if (CollectionUtils.isNotEmpty(values)) {

                        for (ConferenceContext conferenceContext : values) {

                            if (conferenceContext != null && conferenceContext.isStart()) {
                                // 关键帧重发

                                List<Attendee> fcmTerminals = new ArrayList<>();
                                ConferenceContextUtils.eachAttendeeInConference(conferenceContext, (a) -> {
                                    if (a instanceof TerminalAttendee) {
                                        TerminalAttendee ta = (TerminalAttendee) a;
                                        int terminalType = ta.getTerminalType();
                                        // 被叫由会控负责发起呼叫
                                        if (TerminalType.isFCMSIP(terminalType)) {
                                            if (ta.isMeetingJoined()) {
                                                fcmTerminals.add(ta);

                                            }
                                        }
                                    }else if(a instanceof InvitedAttendee){
                                        InvitedAttendee ta = (InvitedAttendee) a;
                                        Long terminalId = ta.getTerminalId();
                                        if(terminalId!=null){
                                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
                                            if(busiTerminal!=null){
                                                if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                                                    if (ta.isMeetingJoined()) {
                                                        fcmTerminals.add(ta);

                                                    }
                                                }
                                            }

                                        }
                                    }
                                });
                                for (Attendee attendee : fcmTerminals) {
                                    logger.info("发起I帧下发");
                                    try {
                                        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
                                        Participant participant = fmeBridge.getDataCache().getParticipantByUuid(attendee.getParticipantUuid());
                                        String callLegId = participant.getCallLeg().getId();
                                        fmeBridge.getCallLegInvoker().generateKeyframe(callLegId);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }

                        }

                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (refreshtimeime == 0) {
                    Threads.sleep(1000 * 60);
                } else {
                    Threads.sleep(1000 * refreshtimeime);
                }

            }

        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}