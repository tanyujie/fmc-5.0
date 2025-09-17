package com.paradisecloud.fcm.fme.conference.task;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paradisecloud.fcm.common.constant.MqttConfigConstant;
import com.paradisecloud.fcm.common.constant.TerminalTopic;
import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.task.Task;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.DateUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SendConferenceInfoToTerminalTask extends Task {

    private ConferenceContext conferenceContext;
    private Long newPresenter;

    public SendConferenceInfoToTerminalTask(String id, long delayInMilliseconds, ConferenceContext conferenceContext) {
        super("send_c_i_" + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
    }

    public SendConferenceInfoToTerminalTask(String id, long delayInMilliseconds, ConferenceContext conferenceContext, Long newPresenter) {
        super("send_c_i_" + (newPresenter != null ? "n_" : "") + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.newPresenter = newPresenter;
    }

    @Override
    public void run() {
        if (conferenceContext != null) {
            BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
            ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
            IMqttService mqttService = BeanFactory.getBean(IMqttService.class);

            Set<BusiTerminal> busiTerminalList = new HashSet<>();
            Long createUserId = conferenceContext.getCreateUserId();
            BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByUserId(createUserId);
            if (busiUserTerminal != null) {
                Attendee terminalAttendee = conferenceContext.getAttendeeByTerminalId(busiUserTerminal.getTerminalId());
                if (terminalAttendee != null && terminalAttendee.getMeetingStatus() == AttendeeMeetingStatus.IN.getValue()) {
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiUserTerminal.getTerminalId());
                    if (StringUtils.isNotEmpty(busiTerminal.getSn())) {
                        if (busiTerminal.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                            busiTerminalList.add(busiTerminal);
                        }
                    }
                }
            }
            Long presenter = conferenceContext.getPresenter();
            BusiUserTerminal busiUserTerminalPresenter = busiUserTerminalMapper.selectBusiUserTerminalByUserId(presenter);
            if (busiUserTerminalPresenter != null) {
                Attendee terminalAttendeePresenter = conferenceContext.getAttendeeByTerminalId(busiUserTerminalPresenter.getTerminalId());
                if (terminalAttendeePresenter != null && terminalAttendeePresenter.getMeetingStatus() == AttendeeMeetingStatus.IN.getValue()) {
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeePresenter.getTerminalId());
                    if (StringUtils.isNotEmpty(busiTerminal.getSn())) {
                        if (busiTerminal.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                            busiTerminalList.add(busiTerminal);
                        }
                    }
                }
            }
            if (newPresenter != null && newPresenter.longValue() != presenter.longValue()) {
                BusiUserTerminal busiUserTerminalNewPresenter = busiUserTerminalMapper.selectBusiUserTerminalByUserId(newPresenter);
                if (busiUserTerminalNewPresenter != null) {
                    Attendee terminalAttendeePresenter = conferenceContext.getAttendeeByTerminalId(busiUserTerminalNewPresenter.getTerminalId());
                    if (terminalAttendeePresenter != null && terminalAttendeePresenter.getMeetingStatus() == AttendeeMeetingStatus.IN.getValue()) {
                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeePresenter.getTerminalId());
                        if (StringUtils.isNotEmpty(busiTerminal.getSn())) {
                            if (busiTerminal.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                                busiTerminalList.add(busiTerminal);
                            }
                        }
                    }
                }
            }
            try {
                String action = TerminalTopic.CONFERENCE_INFO;
                String jsonStr = objectMapper.writeValueAsString(conferenceContext);
                JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                jsonObject.remove("supervisorPassword");
                if (jsonObject.get("conferenceAppointment") == null) {
                    if (!conferenceContext.isAppointment() && conferenceContext.getConferenceAppointment() == null) {
                        BusiConferenceAppointment busiConferenceAppointment = new BusiConferenceAppointment();
                        String startTimeStr = DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", conferenceContext.getStartTime());
                        Date endTime = DateUtils.getDiffDate(conferenceContext.getStartTime(), conferenceContext.getDurationTime(), TimeUnit.MINUTES);
                        String endTimeStr = DateUtils.formatTo("yyyy-MM-dd HH:mm:ss", endTime);
                        busiConferenceAppointment.setStartTime(startTimeStr);
                        busiConferenceAppointment.setEndTime(endTimeStr);
                        busiConferenceAppointment.setDeptId(conferenceContext.getDeptId());
                        busiConferenceAppointment.setTemplateId(conferenceContext.getTemplateConferenceId());
                        jsonObject.put("conferenceAppointment", busiConferenceAppointment);
                    }
                }
                if (busiTerminalList != null && busiTerminalList.size() > 0) {
                    for (BusiTerminal busiTerminal : busiTerminalList) {
                        if (StringUtils.isNotEmpty(busiTerminal.getSn())) {
                            String terminalTopic = MqttConfigConstant.TOPIC_PREFIX + busiTerminal.getSn();
                            mqttService.responseTerminal(terminalTopic, action, jsonObject, busiTerminal.getSn(), "");
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }
}
