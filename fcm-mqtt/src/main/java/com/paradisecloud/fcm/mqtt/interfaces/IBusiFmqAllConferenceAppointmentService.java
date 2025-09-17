package com.paradisecloud.fcm.mqtt.interfaces;

import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;

import java.util.Map;

public interface IBusiFmqAllConferenceAppointmentService {

    Map<String, Object> addConferenceAppointment(BusiConferenceAppointment busiConferenceAppointment, String mcuTypeStr);

    int editConferenceAppointment(String apConferenceId, BusiConferenceAppointment busiConferenceAppointment);

    int removeConferenceAppointment(String apConferenceId);

    boolean endConference(String conferenceId, int endType);

    int onlyEditConferenceAppointment(BusiConferenceAppointment busiConferenceAppointment, String mcuTypeStr);
}
