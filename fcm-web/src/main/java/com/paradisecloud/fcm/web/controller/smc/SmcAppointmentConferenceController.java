package com.paradisecloud.fcm.web.controller.smc;

import com.paradisecloud.com.fcm.smc.modle.ParticipantRspDto;
import com.paradisecloud.com.fcm.smc.modle.SmcAppointmentConferenceContext;
import com.paradisecloud.com.fcm.smc.modle.request.ParticipantReq;
import com.paradisecloud.com.fcm.smc.modle.request.SmcAppointmentConferenceRequest;
import com.paradisecloud.com.fcm.smc.modle.request.SmcConferenceRequest;
import com.paradisecloud.com.fcm.smc.modle.response.SmcConferenceRep;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.smc.service.SmcConferenceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/smc/appointmentconference")
public class SmcAppointmentConferenceController extends BaseController {
    @Resource
    private SmcConferenceService smcConferenceService;
    @Resource
    private IBusiTerminalService iBusiTerminalService;

    @PostMapping("/list")
    public RestResponse appointmentConferenceList(@RequestBody SmcConferenceRequest smcConferenceRequest) {
        smcConferenceRequest.setActive(false);
        SmcConferenceRep conferenceList = smcConferenceService.getConferenceList(smcConferenceRequest);
        return RestResponse.success(conferenceList);
    }


    @PostMapping("/add")
    public RestResponse appointmentConferenceAdd(@RequestBody SmcAppointmentConferenceRequest smcAppointmentConferenceRequest) {
        List<Long> participantsIds = smcAppointmentConferenceRequest.getParticipantsIds();

        convertParticipant(participantsIds);
        SmcAppointmentConferenceContext smcAppointmentConferenceContext = smcConferenceService.appointmentConferenceAdd(smcAppointmentConferenceRequest);

        return RestResponse.success(smcAppointmentConferenceContext);
    }


    @PutMapping("/{conferenceId}")
    public RestResponse changeAppointmentConference(@RequestBody SmcAppointmentConferenceRequest smcAppointmentConferenceRequest) {
        List<Long> participantsIds = smcAppointmentConferenceRequest.getParticipantsIds();
        List<ParticipantRspDto> participantReqs = convertParticipant(participantsIds);
        smcAppointmentConferenceRequest.setParticipants(participantReqs);
        return RestResponse.success(smcConferenceService.appointmentConferenceChange(smcAppointmentConferenceRequest));
    }

    private List<ParticipantRspDto> convertParticipant(List<Long> participantsIds) {
        List<ParticipantRspDto> participants = new ArrayList<>(participantsIds.size());
        if (!CollectionUtils.isEmpty(participantsIds)) {
            for (Long id : participantsIds) {
                BusiTerminal busiTerminal = iBusiTerminalService.selectBusiTerminalById(id);
                if (!Objects.isNull(busiTerminal)) {
                    ParticipantRspDto participantReq = new ParticipantRspDto();
                    String number = busiTerminal.getNumber();
                    participantReq.setName(busiTerminal.getName());
                    participantReq.setUri(number);
                    participantReq.setRate(1920);
                    if (StringUtils.isBlank(number)) {
                        participantReq.setUri(busiTerminal.getIp());
                    }
                    if (StringUtils.isBlank(busiTerminal.getIp())) {
                        break;
                    }
                    participants.add(participantReq);
                }
            }
        }

        return participants;
    }


    @DeleteMapping("/{conferenceId}")
    public RestResponse changeAppointmentConference(@PathVariable String conferenceId) {
        smcConferenceService.appointmentConferenceDelete(conferenceId);
        return RestResponse.success();
    }


    @PostMapping("/local/create")
    public RestResponse appointmentConferenceLocalCreate(@RequestBody SmcAppointmentConferenceRequest smcAppointmentConferenceRequest) {
        List<Long> participantsIds = smcAppointmentConferenceRequest.getParticipantsIds();

        convertParticipant(participantsIds);
        SmcAppointmentConferenceContext smcAppointmentConferenceContext = smcConferenceService.appointmentConferenceAdd(smcAppointmentConferenceRequest);

        return RestResponse.success(smcAppointmentConferenceContext);
    }

}
