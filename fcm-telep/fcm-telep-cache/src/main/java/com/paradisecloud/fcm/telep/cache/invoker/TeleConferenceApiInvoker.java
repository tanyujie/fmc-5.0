package com.paradisecloud.fcm.telep.cache.invoker;


import com.paradisecloud.fcm.telep.cache.util.ClientAuthentication;
import com.paradisecloud.fcm.telep.cache.util.JaxbMapper;
import com.paradisecloud.fcm.telep.model.Member;
import com.paradisecloud.fcm.telep.model.MethodCall;
import com.paradisecloud.fcm.telep.model.MethodResponse;
import com.paradisecloud.fcm.telep.model.Value;
import com.paradisecloud.fcm.telep.model.busi.ConferencesResponse;
import com.paradisecloud.fcm.telep.model.busi.TeleConference;
import com.paradisecloud.fcm.telep.model.request.BaseCall;
import com.paradisecloud.fcm.telep.model.request.EnumerateFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * @author nj
 * @date 2022/10/11 15:12
 */
public class TeleConferenceApiInvoker extends TelePApiInvoker {


    public TeleConferenceApiInvoker(String rootUrl, XmlRpcLocalRequest xmlRpcLocalRequest) {
        super(rootUrl, xmlRpcLocalRequest);
    }

    public MethodResponse create(String xml) {
        String postXml = ClientAuthentication.postXml(rootUrl, xml);
        MethodResponse methodResponse = JaxbMapper.fromXml(postXml, MethodResponse.class);
        return methodResponse;
    }

    //conference.enumerate  completed scheduled active
    public List<TeleConference> enumerate(EnumerateFilter enumerateFilter) {

        MethodCall baseCall = BaseCall.createBaseCall("conference.enumerate");
        List<Member> members = BaseCall.getMembers(baseCall);

        Member member = new Member();
        Value value = new Value();
        member.setName(enumerateFilter.name());
        value.setBooleanValue(true);
        member.setValue(value);
        members.add(member);
        String xml = JaxbMapper.toXml(baseCall, MethodCall.class, "utf-8");
        String res = ClientAuthentication.postXml(rootUrl, xml);
        return getEntityList(res, TeleConference.class, "conferences");
    }


    public ConferencesResponse enumerateRes(EnumerateFilter enumerateFilter) {

        MethodCall baseCall = BaseCall.createBaseCall("conference.enumerate");
        List<Member> members = BaseCall.getMembers(baseCall);

        Member member = new Member();
        Value value = new Value();
        member.setName(enumerateFilter.name());
        value.setBooleanValue(true);
        member.setValue(value);
        members.add(member);
        String xml = JaxbMapper.toXml(baseCall, MethodCall.class, "utf-8");
        String res = ClientAuthentication.postXml(rootUrl, xml);
        return getEntityPlus(res, ConferencesResponse.class);
    }

    public String enumerateJson(EnumerateFilter enumerateFilter) {
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("enumerateFilter", enumerateFilter.name());
        return xmlRpcLocalRequest.execute("conference.enumerate", map);

    }

    public ConferencesResponse enumerateBean(EnumerateFilter enumerateFilter) {
        HashMap<String, Object> map = new HashMap<>(3);
        if (!Objects.isNull(enumerateFilter)) {
            map.put("enumerateFilter", enumerateFilter.name());
        }
        map.put("moreThanFour", true);
        ConferencesResponse response = null;
        try {
            response = xmlRpcLocalRequest.execute("conference.enumerate", map, ConferencesResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public void end(String conferenceName) {
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("conferenceName", conferenceName);
        xmlRpcLocalRequest.execute("conference.end", map);
    }

    /**
     * This call queries the status of the conference floor control.
     */
    public void conferenceFloorQuery(String conferenceName){
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("conferenceName", conferenceName);
       xmlRpcLocalRequest.execute("conference.floor.query", map, ConferencesResponse.class);
    }

    /**
     * Returns information about a named conference on the MCU.
     * @param conferenceName Name of the conference for which information is
     * required.
     * This call returns a struct as described in conference.enumerate above, containing information about
     * the conference indicated.
     * A fault code of “no such conference” is returned if there is no conference with the given name
     */
    public void conferenceStatus(String conferenceName){
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("conferenceName", conferenceName);
        xmlRpcLocalRequest.execute("conference.status", map);
    }

    /**
     * conference.modify
     * This call modifies the settings of an existing conference. Conferences created through the
     * management API will appear in the list of conferences accessible via the web interface. Therefore, the
     * API can be used to modify conferences scheduled via the web interface, and vice versa.
     * @param conferenceName
     * @param locked
     */
    public void conferenceLock(String conferenceName,Boolean locked){
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("conferenceName", conferenceName);
        map.put("locked", locked);
        xmlRpcLocalRequest.execute("conference.modify", map);
    }
    /**
     * conference.modify
     * This call modifies the settings of an existing conference. Conferences created through the
     * management API will appear in the list of conferences accessible via the web interface. Therefore, the
     * API can be used to modify conferences scheduled via the web interface, and vice versa.
     * @param conferenceName
     */
    public void conferenceModify(String conferenceName){
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("conferenceName", conferenceName);
        map.put("durationSeconds", 0);
        xmlRpcLocalRequest.execute("conference.modify", map);
    }


    /**
     *conference.create
     * This call creates a new conference on the MCU. Conferences created through the management API
     * will appear in the list of conferences accessible via the web interface, and vice versa.
     * @param teleConference
     */
    public void create(TeleConference teleConference){
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("conferenceName", teleConference.getConferenceName());
        map.put("durationSeconds", 0);
        xmlRpcLocalRequest.execute("conference.create", map);
    }



}
