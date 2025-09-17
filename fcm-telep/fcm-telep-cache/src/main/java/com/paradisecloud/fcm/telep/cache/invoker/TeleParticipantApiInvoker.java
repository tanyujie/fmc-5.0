package com.paradisecloud.fcm.telep.cache.invoker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.telep.cache.util.BeanMapTool;
import com.paradisecloud.fcm.telep.model.busi.participants.ParticipantsResponse;
import com.paradisecloud.fcm.telep.model.busi.participants.TeleParticipant;
import com.paradisecloud.fcm.telep.model.request.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author nj
 * @date 2022/10/12 17:15
 */
public class TeleParticipantApiInvoker extends TelePApiInvoker {

    public TeleParticipantApiInvoker(String rootUrl, XmlRpcLocalRequest xmlRpcLocalRequest) {
        super(rootUrl, xmlRpcLocalRequest);
    }

    /**
     * 重连
     * @param conferenceName
     * @param participantName
     */
    public void participantConnect(String conferenceName, String participantName) {
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("conferenceName", conferenceName);
        map.put("participantName", participantName);
        xmlRpcLocalRequest.execute("participant.connect", map);
    }

    /**
     * operationScope array of strings
     * This should contain none, either or both of currentState
     * or configuredState. If currentState is present, the active
     * configuration of each participant is returned by the
     * MCU in the currentState structure. If configuredState is
     * present, the stored configuration is returned in the
     * configuredState structure
     *
     * @param enumerateFilter connected disconnected connecting True if the participant is currently connected to a
     *                        conference.True if the participant has been connected to a
     *                        conference, but is now disconnected.True if the scheduled participant is in the process of
     *                        connecting.
     * @param enumerateID     The value returned by the last enumeration call. If this
     *                        parameter is omitted, a new enumeration is started
     */
    public List<TeleParticipant> enumerate(EnumerateFilter enumerateFilter, String enumerateID, List<TeleParticipant> result) {
        HashMap<String, Object> map = new HashMap<>(3);
        if (enumerateFilter != null) {
            map.put("enumerateFilter", enumerateFilter.name());
        }
        if (!Objects.isNull(enumerateID)) {
            map.put("enumerateID", enumerateID);
        }
        List<String> operationScope = new ArrayList<>();
        operationScope.add("activeState");
        operationScope.add("configuredState");
        String execute = null;
        try {
            execute = xmlRpcLocalRequest.execute("participant.enumerate", map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ParticipantsResponse participantsResponse = JSON.parseObject(execute, ParticipantsResponse.class);
        if (participantsResponse.getEnumerateID() != null) {
            result.addAll(participantsResponse.getParticipants());
            enumerate(null, participantsResponse.getEnumerateID(), result);
        } else {
            if(CollectionUtils.isNotEmpty(participantsResponse.getParticipants())){
                result.addAll(participantsResponse.getParticipants());
            }
            return result;
        }
        return result;
    }

    /**
     * participant.diagnostics
     * Returns diagnostic information about a given participant.
     */
    public void participantDiagnostics(String conferenceName, String participantName) {

        HashMap<String, Object> map = new HashMap<>(3);
        if (Strings.isNotBlank(conferenceName)) {
            map.put("conferenceName", conferenceName);
            map.put("participantName", participantName);
        }
        String execute = xmlRpcLocalRequest.execute("participant.diagnostics", map);
    }

    /**
     * participant.status
     * Returns information about an individual participant on the MCU.
     */
    public void participantStatus(String conferenceName, String participantName) {
        HashMap<String, Object> map = new HashMap<>(3);
        if (Strings.isNotBlank(conferenceName)) {
            map.put("conferenceName", conferenceName);
            map.put("participantName", participantName);
        }
        String execute = xmlRpcLocalRequest.execute("participant.status", map);
    }

    /**
     * participant.status
     * Returns information about an individual participant on the MCU.
     */
    public void participantStatus(ParticipantDisconnectOrConnectRequest request) {
        if (request == null) {
            throw new CustomException("参数不存在");
        }
        Map<String, ?> stringMap = BeanMapTool.beanToMap(request);
        xmlRpcLocalRequest.execute("participant.status", stringMap);
    }

    /**
     * participant.modify  important This setting should not be present if
     * operationScope is "configuredState". Specifies
     * whether this participant is important.
     *Conference layouts
     * Some API calls allow a particular layout to be specified for video sent to that participant via the cpLayout,
     * currentLayout, customLayout parameters. These parameter can take the following values:
     * n default: use the MCU’s default view family
     * n family<index>: use the specified layout family  12345
     * n layout<index>: use a specific layout 1-59
     * n conferenceCustom: use the conference custom layout
     */
    public void participantModify(TeleParticipant teleParticipant) {
        teleParticipant.setOperationScope("activeState");
        teleParticipant.setConnectTime(null);
         Map<String, ?> stringMap = BeanMapTool.beanToMap(teleParticipant);

        xmlRpcLocalRequest.execute("participant.modify", stringMap);
    }


    /**
     * participant.connect
     * Used primarily for API-configured participants with deferConnection set to TRUE, but can also be used to
     * reconnect disconnected participants.
     *
     * @param request conferenceName string The name of the conference.
     *                autoAttendantUniqueID string Unique identifier for the auto attendant.
     *                participantName string The unique name of a participant. more...
     *                participantProtocol string h323, sip, or vnc.
     *                participantType string One of: by_address, by_name, or ad_hoc.
     */
    public void participantConnect(ParticipantDisconnectOrConnectRequest request) {
        if (request == null) {
            throw new CustomException("参数不存在");
        }
        Map<String, ?> stringMap = BeanMapTool.beanToMap(request);
        xmlRpcLocalRequest.execute("participant.connect", stringMap);
    }

    /**
     * participant.disconnect
     * This call causes the MCU to tear down its connection to the specified participant, if such a connection
     * exists. This is different from participant.remove above because:
     * n In the case of configured participants, it does not remove the configuration (thus allowing later reconnection with participant.connect).
     * n In the case of ad hoc participants, it does not remove the record of the previous connection.
     *
     * @param request conferenceName string The name of the conference.
     *                autoAttendantUniqueID string Unique identifier for the auto attendant.
     *                participantName string The unique name of a participant. more...
     *                participantProtocol string h323, sip, or vnc.
     *                participantType string One of: by_address, by_name, or ad_hoc.
     */
    public void participantDisconnect(ParticipantDisconnectOrConnectRequest request) {
        if (request == null) {
            throw new CustomException("参数不存在");
        }
        Map<String, ?> stringMap = BeanMapTool.beanToMap(request);
        xmlRpcLocalRequest.execute("participant.disconnect", stringMap);
    }

    /**
     * Controls far end camera. Sends a direction to the identified camera.
     * @param request
     */
    public void participantFecc(ParticipantFecc request) {
        if (request == null) {
            throw new CustomException("参数不存在");
        }
        Map<String, ?> stringMap = BeanMapTool.beanToMap(request);
        xmlRpcLocalRequest.execute("participant.fecc", stringMap);
    }


    /**
     * Puts a message on the display of a given participant.
     * @param request
     */
    public void participantMessage(ParticipantMessage request) {
        if (request == null) {
            throw new CustomException("参数不存在");
        }
        Map<String, ?> stringMap = BeanMapTool.beanToMap(request);
        xmlRpcLocalRequest.execute("participant.message", stringMap);
    }

    /**
     * participant.remove
     * Removes a participant from the database of configured participants, and also removes this participant
     * from any conferences. It will also remove all records of this participant's presence in a conference
     *
     * @param req
     */
    public void participantRemove(ParticipantDisconnectOrConnectRequest req) {
        if (req == null) {
            throw new CustomException("参数不存在");
        }
        Map<String, ?> stringMap = BeanMapTool.beanToMap(req);
        xmlRpcLocalRequest.execute("participant.remove", stringMap);
    }


    /**
     * participant.add
     * Adds a participant to a conference. All participants in a conference must have a participantName that is
     * unique to the conference but it need not be unique across all conferences. Add the participant as type by_
     * address unless you are adding the participant to an ad hoc conference.
     * Send the addResponse parameter if you want the call to return the details of the added participant (in a
     * participant struct.)
     *
     * @param addRequest
     */
    public TeleParticipant participantAdd(ParticipantAddRequest addRequest) {
        if (addRequest == null) {
            throw new CustomException("参数不存在");
        }
        Map<String, ?> stringMap = BeanMapTool.beanToMap(addRequest);
        String addStringResp = xmlRpcLocalRequest.execute("participant.add", stringMap);
        if (StringUtils.hasText(addStringResp)) {
            return JSONObject.parseObject(addStringResp, TeleParticipant.class);
        }
        return null;

    }

}
