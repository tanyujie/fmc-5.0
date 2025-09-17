package com.paradisecloud.fcm.mcu.zj.task;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paradisecloud.fcm.common.constant.MqttConfigConstant;
import com.paradisecloud.fcm.common.constant.TerminalTopic;
import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class McuZjSendLeftConferenceToTerminalTask extends DelayTask {

    private McuZjConferenceContext conferenceContext;
    private AttendeeForMcuZj attendee;

    public McuZjSendLeftConferenceToTerminalTask(String id, long delayInMilliseconds, McuZjConferenceContext conferenceContext, AttendeeForMcuZj attendee) {
        super("left_c_" + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendee = attendee;
    }

    @Override
    public void run() {
        if (conferenceContext != null) {
            if (attendee != null) {

                BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
                ObjectMapper objectMapper = BeanFactory.getBean(ObjectMapper.class);
                IMqttService mqttService = BeanFactory.getBean(IMqttService.class);

                Set<BusiTerminal> busiTerminalList = new HashSet<>();
                Long createUserId = conferenceContext.getCreateUserId();
                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByUserId(createUserId);
                if (busiUserTerminal != null) {
                    AttendeeForMcuZj terminalAttendee = conferenceContext.getAttendeeByTerminalId(busiUserTerminal.getTerminalId());
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
                    AttendeeForMcuZj terminalAttendeePresenter = conferenceContext.getAttendeeByTerminalId(busiUserTerminalPresenter.getTerminalId());
                    if (terminalAttendeePresenter != null && terminalAttendeePresenter.getMeetingStatus() == AttendeeMeetingStatus.IN.getValue()) {
                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeePresenter.getTerminalId());
                        if (StringUtils.isNotEmpty(busiTerminal.getSn())) {
                            if (busiTerminal.getMqttOnlineStatus() == TerminalOnlineStatus.ONLINE.getValue()) {
                                busiTerminalList.add(busiTerminal);
                            }
                        }
                    }
                }
                try {
                    String action = TerminalTopic.LEFT_CONFERENCE;
                    String jsonStr = objectMapper.writeValueAsString(attendee);
                    JSONObject jsonObject = JSONObject.parseObject(jsonStr);
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
}
