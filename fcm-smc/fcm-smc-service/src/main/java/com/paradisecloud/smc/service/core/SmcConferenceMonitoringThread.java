package com.paradisecloud.smc.service.core;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.com.fcm.smc.modle.HistoryConferenceDetail;
import com.paradisecloud.com.fcm.smc.modle.SmcConferenceContext;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.fcm.smc.cache.modle.util.SmcThreadPool;
import com.paradisecloud.smc.dao.model.BusiSmcAppointmentConference;
import com.paradisecloud.smc.dao.model.BusiSmcHistoryConference;
import com.paradisecloud.smc.service.IBusiSmcAppointmentConferenceService;
import com.paradisecloud.smc.service.IBusiSmcHistoryConferenceService;
import com.sinhy.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author nj
 * @date 2023/3/14 17:58
 */
@Component
public class SmcConferenceMonitoringThread extends Thread implements InitializingBean {

    public static final String CANCEL = "CANCEL";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private IBusiSmcHistoryConferenceService smcHistoryConferenceService;
    @Resource
    private IBusiSmcAppointmentConferenceService appointmentConferenceService;

    @Override
    public void run() {
        ThreadUtils.sleep(30000);
        List<SmcBridge> initSmcBridgeList = SmcBridgeCache.getInstance().getInitSmcBridgeList();
        if (CollectionUtils.isEmpty(initSmcBridgeList)) {
            return;
        }
        logger.info("SmcConferenceMonitoringThread start successfully!");
        SmcThreadPool.exec(()->{
            try {
                BusiSmcHistoryConference busiSmcHistoryConferenceStart = new BusiSmcHistoryConference();
                busiSmcHistoryConferenceStart.setEndStatus(2);
                List<BusiSmcHistoryConference> busiSmcHistoryConferences = smcHistoryConferenceService.selectBusiSmcHistoryConferenceList(busiSmcHistoryConferenceStart);
                if (!CollectionUtils.isEmpty(busiSmcHistoryConferences)) {
                    for (BusiSmcHistoryConference busiSmcHistoryConference : busiSmcHistoryConferences) {
                        String conferenceId = busiSmcHistoryConference.getConferenceId();
                        Long deptId = busiSmcHistoryConference.getDeptId();
                        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(deptId);
                        HistoryConferenceDetail historyConferenceDetail = bridge.getSmcConferencesInvoker().getConferencesHistoryDetailById(conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                        if (historyConferenceDetail != null) {
                            String scheduleEndTime = historyConferenceDetail.getScheduleEndTime();
                            Date endDate = utcToLocal(scheduleEndTime);
                            busiSmcHistoryConference.setEndTime(endDate);
                            busiSmcHistoryConference.setEndStatus(1);
                            busiSmcHistoryConference.setParticipantNum(historyConferenceDetail.getParticipants().size());
                            smcHistoryConferenceService.updateBusiSmcHistoryConference(busiSmcHistoryConference);
                        }
                    }
                }


                //查询预约会议 查询开始开始时间小于当前时间的会议
                List<BusiSmcAppointmentConference> busiSmcAppointmentConferences = appointmentConferenceService.selectBusiSmcAppointmentConferenceByStartTimeLtNoExisTHistory();
                if (!CollectionUtils.isEmpty(busiSmcAppointmentConferences)) {
                    for (BusiSmcAppointmentConference busiSmcAppointmentConference : busiSmcAppointmentConferences) {
                        String conferenceId = busiSmcAppointmentConference.getConferenceId();
                        Long deptId = busiSmcAppointmentConference.getDeptId();
                        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(deptId);

                        HistoryConferenceDetail historyConferenceDetail = bridge.getSmcConferencesInvoker().getConferencesHistoryDetailById(conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                        if (historyConferenceDetail != null) {
                            BusiSmcHistoryConference historyConference = new BusiSmcHistoryConference();
                            historyConference.setConferenceId(conferenceId);
                            historyConference.setConferenceCode(busiSmcAppointmentConference.getAccessCode());
                            historyConference.setEndStatus(1);
                            historyConference.setCreateTime(new Date());
                            try {
                                historyConference.setEndTime(utcToLocal(historyConferenceDetail.getScheduleEndTime()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            historyConference.setDeptId(busiSmcAppointmentConference.getDeptId());
                            historyConference.setSubject(busiSmcAppointmentConference.getSubject());
                            historyConference.setConferenceAvcType(busiSmcAppointmentConference.getType());
                            try {
                                historyConference.setStartTime(utcToLocal(historyConferenceDetail.getScheduleStartTime()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            historyConference.setDuration(historyConferenceDetail.getDuration());
                            smcHistoryConferenceService.insertBusiSmcHistoryConference(historyConference);
                        } else {
                            String conferencesById = bridge.getSmcConferencesInvoker().getConferencesById(conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                            SmcConferenceContext smcConferenceContext = JSON.parseObject(conferencesById, SmcConferenceContext.class);
                            if (smcConferenceContext != null&&smcConferenceContext.getConference()!=null) {
                                String stage = smcConferenceContext.getConference().getStage();
                                if (Objects.equals(stage, "ONLINE")) {
                                    BusiSmcHistoryConference busiSmcHistoryConference = smcHistoryConferenceService.selectBusiSmcHistoryConferenceByConferenceId(conferenceId);
                                    if (busiSmcHistoryConference == null) {
                                        BusiSmcHistoryConference historyConferenceONLINE = new BusiSmcHistoryConference();
                                        historyConferenceONLINE.setConferenceId(conferenceId);
                                        historyConferenceONLINE.setConferenceCode(busiSmcAppointmentConference.getAccessCode());
                                        historyConferenceONLINE.setEndStatus(2);
                                        historyConferenceONLINE.setCreateTime(new Date());
                                        historyConferenceONLINE.setDeptId(busiSmcAppointmentConference.getDeptId());
                                        historyConferenceONLINE.setSubject(smcConferenceContext.getConference().getSubject());
                                        historyConferenceONLINE.setConferenceAvcType(smcConferenceContext.getMultiConferenceService().getConferenceCapabilitySetting().getType());
                                        try {
                                            historyConferenceONLINE.setStartTime(utcToLocal(busiSmcAppointmentConference.getStartDate()));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        historyConferenceONLINE.setDuration(busiSmcAppointmentConference.getDuration());
                                        smcHistoryConferenceService.insertBusiSmcHistoryConference(historyConferenceONLINE);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }


    /**
     * utc时间转成local时间
     *
     * @param utcTime
     * @return
     */
    public Date utcToLocal(String utcTime) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = null;

        utcDate = sdf.parse(utcTime);
        sdf.setTimeZone(TimeZone.getDefault());
        Date locatlDate = null;
        String localTime = sdf.format(utcDate.getTime());
        try {
            locatlDate = sdf.parse(localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return locatlDate;
    }
}
