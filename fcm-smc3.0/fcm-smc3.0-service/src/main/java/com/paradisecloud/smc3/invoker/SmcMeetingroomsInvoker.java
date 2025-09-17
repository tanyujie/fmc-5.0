package com.paradisecloud.smc3.invoker;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.smc3.model.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author nj
 * @date 2022/8/22 10:30
 */
public class SmcMeetingroomsInvoker extends SmcApiInvoker {

    public SmcMeetingroomsInvoker(String rootUrl, String meetingUrl) {
        super(rootUrl, meetingUrl);
    }

    public  String resetactivecode(String id, Map<String, String> systemHeaders){
        String url = "/terminalparams/resetactivecode/"+id;

        String res = ClientAuthentication.httpPut(rootUrl + url,"{}", systemHeaders,null);
        errorString(res);
        return res;

    }

    /**
     * 添加模板
     * @param params
     * @param headers
     * @return
     */
    public String creatMeetingrooms(String params, Map<String, String> headers) {
        String url = "/meetingrooms/create";
        try {
            System.out.println(params);
            String res = ClientAuthentication.httpPost(rootUrl + url, params, headers);
            errorString(res);
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
    public String getDevicecapabilities( Map<String, String> headers){
        String url = "/devicecapabilities";
        try {
            String res = ClientAuthentication.httpGet(rootUrl + url,null, headers);
            errorString(res);
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getSmcMcu(Map<String, String> headers) {
        String url = "/mcus?page=0&size=200&sort=createdDate,desc";
        try {
            return ClientAuthentication.httpGet(meetingUrl + url,null, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  String queryAreaId(Map<String, String> headers){
        String url = "/areas";
        try {
            String res = ClientAuthentication.httpGet(rootUrl + url,null, headers);
            errorString(res);
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 查询会议室列表
     * @param page
     * @param size
     * @param headers
     * @return
     */
    public String getMeetingRooms(String organizationId, int page,int size,  Map<String, String> headers) {
        String url = "/meetingrooms/" +"conditions?page="+page+"&size="+size+"&sort=CREATED_DATE,desc";
        try {
            MeetingRoomQueryRequest meetingRoomQueryRequest = new MeetingRoomQueryRequest();
            meetingRoomQueryRequest.setOrganizationId(organizationId);
            return ClientAuthentication.httpPost(rootUrl + url,  JSON.toJSONString(meetingRoomQueryRequest),headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询会议室
     * @param headers
     * @return
     */
    public String getMeetingRooms(String id, Map<String, String> headers) {
        String url = "/meetingrooms/id/"+id;
        try {
            return ClientAuthentication.httpGet(rootUrl + url,null,headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询会议室
     * @param name
     * @param headers
     * @return
     */
    public String getMeetingRoomsByName(String name, Map<String, String> headers) {
        String url = "/meetingrooms/search/name?name="+name;
        try {
            String s = ClientAuthentication.httpGet(rootUrl + url, null, headers);
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询会议室
     *
     * @param page
     * @param size
     * @param headers
     * @return
     */
    public MeetingRoomRep getMeetingRoomsRep(String organizationId, String key, int page, int size, Map<String, String> headers) {
        String url = "/meetingrooms/" + "conditions?page=" + page + "&size=" + size + "&sort=CREATED_DATE,desc";
        try {
            MeetingRoomQueryRequest meetingRoomQueryRequest = new MeetingRoomQueryRequest();
            meetingRoomQueryRequest.setOrganizationId(organizationId);
            if (StringUtils.isNotBlank(key)) {
                meetingRoomQueryRequest.setKeyWord(key);
            }
            return JSON.parseObject(ClientAuthentication.httpPost(rootUrl + url, JSON.toJSONString(meetingRoomQueryRequest), headers), MeetingRoomRep.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询会议室
     *
     * @param page
     * @param size
     * @param headers
     * @return
     */
    public MeetingRoomRep getMeetingRoomsRepAreaId(String areaId, String key, int page, int size, Map<String, String> headers) {
        String url = "/meetingrooms/" + "conditions?page=" + page + "&size=" + size + "&sort=CREATED_DATE,desc";
        try {
            MeetingRoomQueryRequest meetingRoomQueryRequest = new MeetingRoomQueryRequest();
            meetingRoomQueryRequest.setAreaId(areaId);
            if (StringUtils.isNotBlank(key)) {
                meetingRoomQueryRequest.setKeyWord(key);
            }
            return JSON.parseObject(ClientAuthentication.httpPost(rootUrl + url, JSON.toJSONString(meetingRoomQueryRequest), headers), MeetingRoomRep.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 终端状态
     * @param headers
     * @return
     */
    public List<ScEndpointRep> getEndpointsstatus(ScEndpointInfoListRequest scEndpointInfoListRequest, Map<String, String> headers) {
           String url = "/sc/endpoints/status/conditions";
        try {
            if(scEndpointInfoListRequest==null){
                scEndpointInfoListRequest=new ScEndpointInfoListRequest();
            }
            List<ScEndpointInfoListRequest.ScEndpointInfoListDTO> scEndpointInfoList = scEndpointInfoListRequest.getScEndpointInfoList();
            if(CollectionUtils.isNotEmpty(scEndpointInfoList)){
                scEndpointInfoList = scEndpointInfoList.stream().filter(s -> Strings.isNotBlank(s.getServiceZoneId())).collect(Collectors.toList());
                scEndpointInfoListRequest.setScEndpointInfoList(scEndpointInfoList);
            }
            String s = ClientAuthentication.httpPost(rootUrl + url, JSON.toJSONString(scEndpointInfoListRequest), headers);
            errorString(s);
            List<ScEndpointRep> scEndpointReps = JSON.parseArray(s, ScEndpointRep.class);
            return  scEndpointReps;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;

    }

    public  void deleteRooms(List<String> ids, Map<String, String> sysHeaders){
        String url = "/meetingrooms/batch";
        try {
             List<Map<String, String>> params = new ArrayList<>();
            for (String id : ids) {
                HashMap<String, String> idsMap = new HashMap<>();
                idsMap.put("id",id);
                params.add(idsMap);
            }
            ClientAuthentication.httpDeleteVoid(rootUrl + url, JSON.toJSONString(params), sysHeaders);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public MeetingRoomResponse updateMeetingrooms(String id, String jsonString, Map<String, String> headers){
        String url = "/meetingrooms/"+id;
        String s =    ClientAuthentication.httpPut(rootUrl + url, jsonString, headers,null);
        return JSON.parseObject(s, MeetingRoomResponse.class);

    }




}
