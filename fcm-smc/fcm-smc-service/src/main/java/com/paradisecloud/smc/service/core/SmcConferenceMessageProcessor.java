package com.paradisecloud.smc.service.core;

import com.paradisecloud.com.fcm.smc.modle.ConferenceTimeType;
import com.paradisecloud.com.fcm.smc.modle.HistoryConferenceDetail;
import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryCallMapper;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryCall;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.smc.cache.modle.*;
import com.paradisecloud.smc.dao.model.BusiSmcAppointmentConference;
import com.paradisecloud.smc.dao.model.BusiSmcConferenceState;
import com.paradisecloud.smc.dao.model.BusiSmcHistoryConference;
import com.paradisecloud.smc.processormessage.RealTimeInfoProcessorMessage;
import com.paradisecloud.smc.service.*;
import com.sinhy.model.AsyncBlockingMessageProcessor;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ReflectionUtils;
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
 * @date 2023/3/21 15:33
 */
@Component
public class SmcConferenceMessageProcessor extends AsyncBlockingMessageProcessor<SmcConferenceMessage> implements InitializingBean {


    public static final String CANCEL = "CANCEL";
    public static final String ONLINE = "ONLINE";
    public static final int INT = 2;
    @Resource
    private IBusiSmcHistoryConferenceService smcHistoryConferenceService;
    @Resource
    private IBusiSmcAppointmentConferenceService appointmentConferenceService;

    @Resource
    private IBusiSmcTemplateConferenceService smcTemplateConferenceService;

    @Resource
    private IBusiSmcMulitpicService busiSmcMulitpicService;

    @Resource
    private IBusiSmcConferenceStateService conferenceStateService;

    public SmcConferenceMessageProcessor() {
        super("SmcConferenceMessageProcessor", (SmcConferenceMessageQueue) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(SmcConferenceMessageQueue.class, "getInstance"), null));
    }

    @Override
    protected void process(SmcConferenceMessage message) {
        try {
            if(message!=null){
                String stage = message.getStage();
                String conferenceId = message.getConferenceId();
                if(Objects.equals(stage, CANCEL)){
                    BusiSmcHistoryConference busiSmcHistoryConference = smcHistoryConferenceService.selectBusiSmcHistoryConferenceByConferenceId(conferenceId);
                    if(busiSmcHistoryConference!=null){
                        if(busiSmcHistoryConference.getEndStatus()== INT){
                            SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(busiSmcHistoryConference.getDeptId());
                            HistoryConferenceDetail historyConferenceDetail = bridge.getSmcConferencesInvoker().getConferencesHistoryDetailById(conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                            if(historyConferenceDetail!=null){
                                Integer duration = historyConferenceDetail.getDuration();
                                String scheduleEndTime = historyConferenceDetail.getScheduleEndTime();
                                busiSmcHistoryConference.setDuration(duration);
                                try {
                                    busiSmcHistoryConference.setEndTime(UTCTimeFormatUtil.utcToLocal(scheduleEndTime));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                busiSmcHistoryConference.setParticipantNum(historyConferenceDetail.getParticipants().size());
                            }else {
                                busiSmcHistoryConference.setEndTime(new Date());
                            }
                            busiSmcHistoryConference.setEndStatus(1);
                            smcHistoryConferenceService.updateBusiSmcHistoryConference(busiSmcHistoryConference);
                        }
                        BusiSmcAppointmentConference busiSmcAppointmentConference = appointmentConferenceService.selectBusiSmcAppointmentConferenceByConferenceId(conferenceId);
                        if(busiSmcAppointmentConference!=null){
                            busiSmcAppointmentConference.setActive(3);
                            appointmentConferenceService.updateBusiSmcAppointmentConference(busiSmcAppointmentConference);
                        }
                    }else {
                        BusiSmcAppointmentConference busiSmcAppointmentConference = appointmentConferenceService.selectBusiSmcAppointmentConferenceByConferenceId(conferenceId);
                        if(busiSmcAppointmentConference!=null){

                            BusiSmcHistoryConference historyConference=new BusiSmcHistoryConference();
                            SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(busiSmcHistoryConference.getDeptId());
                            HistoryConferenceDetail historyConferenceDetail = bridge.getSmcConferencesInvoker().getConferencesHistoryDetailById(conferenceId, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                            if(historyConferenceDetail!=null){
                                Integer duration = historyConferenceDetail.getDuration();
                                String scheduleEndTime = historyConferenceDetail.getScheduleEndTime();
                                historyConference.setDuration(duration);
                                historyConference.setParticipantNum(historyConferenceDetail.getParticipants().size());
                                try {
                                    historyConference.setEndTime(UTCTimeFormatUtil.utcToLocal(scheduleEndTime));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                historyConference.setParticipantNum(historyConferenceDetail.getParticipants().size());
                            }else {
                                historyConference.setEndTime(new Date());
                            }

                            historyConference.setConferenceId(conferenceId);
                            historyConference.setConferenceCode(busiSmcAppointmentConference.getAccessCode());
                            historyConference.setEndStatus(1);
                            historyConference.setCreateTime(new Date());
                            historyConference.setDeptId(busiSmcAppointmentConference.getDeptId());
                            historyConference.setConferenceAvcType(busiSmcAppointmentConference.getType());
                            historyConference.setStartTime(utcToLocal(busiSmcAppointmentConference.getStartDate()));
                            historyConference.setSubject(busiSmcAppointmentConference.getSubject());
                            smcHistoryConferenceService.insertBusiSmcHistoryConference(historyConference);
                        }

                    }
                    smcTemplateConferenceService.deleteBusiSmcTemplateConferenceByConferenceId(conferenceId);
                    busiSmcMulitpicService.deleteBusiSmcMulitpicByConferenceId(conferenceId);
                    BusiSmcConferenceState busiSmcConferenceState = new BusiSmcConferenceState();
                    busiSmcConferenceState.setConferenceId(conferenceId);
                    List<BusiSmcConferenceState> busiSmcConferenceStates = conferenceStateService.selectBusiSmcConferenceStateList(busiSmcConferenceState);
                    if (!CollectionUtils.isEmpty(busiSmcConferenceStates)) {
                        conferenceStateService.deleteBusiSmcConferenceStateById(busiSmcConferenceStates.get(0).getId());
                    }
                    SmcConferenceContextCache.getInstance().cleanCacheMap(conferenceId);
                    SmcBridgeCache.getInstance().getConferenceBridge().remove(conferenceId);


                    try {
                        BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
                        BusiHistoryConference busiHistoryConference = new BusiHistoryConference();
                        busiHistoryConference.setCallLegProfileId(conferenceId);
                        List<BusiHistoryConference> busiHistoryConferences = busiHistoryConferenceMapper.selectBusiHistoryConferenceList(busiHistoryConference);
                        if(!CollectionUtils.isEmpty(busiHistoryConferences)){
                            BusiHistoryConference busiHistoryConference1 = busiHistoryConferences.get(0);
                            if(busiHistoryConference1.getConferenceEndTime()==null){
                                busiHistoryConference1.setConferenceEndTime(new Date());
                                busiHistoryConference1.setDuration((int) ((System.currentTimeMillis() - busiHistoryConference.getConferenceStartTime().getTime()) / 1000));
                                busiHistoryConference1.setEndReasonsType(EndReasonsType.AUTO_END);
                                busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConference1);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                if(Objects.equals(stage, ONLINE)){
                    BusiSmcAppointmentConference busiSmcAppointmentConference = appointmentConferenceService.selectBusiSmcAppointmentConferenceByConferenceId(conferenceId);
                    if(busiSmcAppointmentConference!=null){
                        busiSmcAppointmentConference.setActive(1);
                        appointmentConferenceService.updateBusiSmcAppointmentConference(busiSmcAppointmentConference);
                        BusiSmcHistoryConference busiSmcHistoryConference = smcHistoryConferenceService.selectBusiSmcHistoryConferenceByConferenceId(conferenceId);
                        if(busiSmcHistoryConference==null){
                            BusiSmcHistoryConference historyConferenceONLINE=new BusiSmcHistoryConference();
                            historyConferenceONLINE.setConferenceId(conferenceId);
                            historyConferenceONLINE.setConferenceCode(busiSmcAppointmentConference.getAccessCode());
                            historyConferenceONLINE.setEndStatus(2);
                            historyConferenceONLINE.setCreateTime(new Date());
                            historyConferenceONLINE.setDeptId(busiSmcAppointmentConference.getDeptId());
                            historyConferenceONLINE.setSubject(busiSmcAppointmentConference.getSubject());
                            historyConferenceONLINE.setStartTime(utcToLocal(busiSmcAppointmentConference.getStartDate()));
                            historyConferenceONLINE.setDuration(busiSmcAppointmentConference.getDuration());
                            smcHistoryConferenceService.insertBusiSmcHistoryConference(historyConferenceONLINE);


                            try {
                                BusiHistoryConference busiHistoryConference =   BeanFactory.getBean(IBusiSmc3HistoryConferenceService.class).saveHistory(busiSmcHistoryConference,busiSmcAppointmentConference.getRate(), ConferenceTimeType.INSTANT_CONFERENCE);
                                // 历史call保存
                                String callId = com.paradisecloud.common.utils.uuid.UUID.randomUUID().toString();
                                BusiHistoryCall busiHistoryCall = new BusiHistoryCall();
                                busiHistoryCall.setCallId(callId);
                                busiHistoryCall.setCoSpace(busiSmcHistoryConference.getConferenceCode());
                                busiHistoryCall.setDeptId(busiSmcHistoryConference.getDeptId());
                                busiHistoryCall.setCreateTime(new Date());
                                busiHistoryCall.setHistoryConferenceId(busiHistoryConference.getId());
                                BeanFactory.getBean(BusiHistoryCallMapper.class).insertBusiHistoryCall(busiHistoryCall);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }

    public  Date utcToLocal(String utcTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = null;

        try {
            utcDate = sdf.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

