package com.paradisecloud.fcm.mcu.plc.task;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paradisecloud.fcm.common.constant.MqttConfigConstant;
import com.paradisecloud.fcm.common.constant.TerminalTopic;
import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class McuPlcSendConferenceInfoToTerminalTask extends DelayTask {

    private McuPlcConferenceContext conferenceContext;
    private Long newPresenter;

    public McuPlcSendConferenceInfoToTerminalTask(String id, long delayInMilliseconds, McuPlcConferenceContext conferenceContext) {
        super("send_c_i_" + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
    }

    public McuPlcSendConferenceInfoToTerminalTask(String id, long delayInMilliseconds, McuPlcConferenceContext conferenceContext, Long newPresenter) {
        super("send_c_i_n_" + (newPresenter != null ? "n_" : "") + id, delayInMilliseconds);
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
                AttendeeForMcuPlc terminalAttendee = conferenceContext.getAttendeeByTerminalId(busiUserTerminal.getTerminalId());
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
                AttendeeForMcuPlc terminalAttendeePresenter = conferenceContext.getAttendeeByTerminalId(busiUserTerminalPresenter.getTerminalId());
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
                    AttendeeForMcuPlc terminalAttendeePresenter = conferenceContext.getAttendeeByTerminalId(busiUserTerminalNewPresenter.getTerminalId());
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
