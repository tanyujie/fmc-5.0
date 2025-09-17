package com.paradisecloud.fcm.smc.cache.modle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.ChooseMultiPicInfo;
import com.paradisecloud.com.fcm.smc.modle.DetailConference;
import com.paradisecloud.com.fcm.smc.modle.HistoryConferenceDetail;
import com.paradisecloud.com.fcm.smc.modle.SmcAppointmentConferenceContext;
import com.paradisecloud.com.fcm.smc.modle.request.*;
import com.paradisecloud.com.fcm.smc.modle.response.*;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/16 15:29
 */
public class SmcConferencesInvoker extends SmcApiInvoker{



    public SmcConferencesInvoker(String rootUrl, String meetingUrl) {
        super(rootUrl, meetingUrl);
    }


    public void deleteConference(String conferenceId ,Map<String, String> headers){
        String url = "/conferences/" + conferenceId;
        ClientAuthentication.httpDeleteUrl(meetingUrl+url,headers,"utf-8");
    }

    /**
     * 提供取消订阅会场实时状态功能，关闭会场详情页面时需要取消订阅
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     *  /online/conferences/{conferenceId}/participants/groups/{groupId}/realTimeInfo
     * @param conferenceId
     * @param groupId
     * @param headers
     */
    public void cancelRealTimeInfo(String conferenceId ,long groupId,Map<String, String> headers){
        String url = "/online/conferences/conferences/" + conferenceId+"/participants/groups/"+groupId+"/realTimeInfo";
        ClientAuthentication.httpDeleteUrl(meetingUrl+url,headers,"utf-8");
    }


    public DetailConference  getDetailConferencesById(String id, Map<String, String> headers){
        String url = "/online/conferences/" + id+"/detail";
        try {
            String res =  ClientAuthentication.httpGet(meetingUrl + url, null, headers);
            if(res!=null&&res.contains(ERROR_NO)){
                SmcErrorResponse smcErrorResponse = JSON.parseObject(res, SmcErrorResponse.class);
                errorString(smcErrorResponse.getErrorDesc());
                throw new CustomException("SMC会议详情错误:" + smcErrorResponse.getErrorDesc(),11);
            }
            return  JSON.parseObject(res,DetailConference.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getConferencesAlarm(String conferenceId,int page,int size,String sort,Map<String, String> headers){
        String url = "/conferences/history/" + conferenceId+"/alarms/?page="+page+"&size="+size+"&sort="+sort;
        try {
            String res =  ClientAuthentication.httpGet(meetingUrl + url, null, headers);
           errorString(res);
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getConferencesParticipants(String conferenceId,int page,int size,Map<String, String> headers){
        String url = "/conferences/" + conferenceId+"/participants/?page="+page+"&size="+size;
        try {
            String res =  ClientAuthentication.httpGet(meetingUrl + url, null, headers);
            errorString(res);
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置多画面
     * @param conferenceId
     * @param multiPicInfoReq
     * @param headers
     */
    public void  createMulitiPic(String conferenceId, MultiPicInfoReq multiPicInfoReq, Map<String, String> headers){
        String url = "/online/conferences/" + conferenceId+"/status";
        try {
            ClientAuthentication.httpPatch(meetingUrl + url,JSON.toJSONString(multiPicInfoReq),headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询会议信息
     * @param id
     * @param headers
     * @return
     */
    public String getConferencesById(String id, Map<String, String> headers) {
        String url = "/conferences/" + id;
        try {
            return ClientAuthentication.httpGet(meetingUrl + url, null, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SmcAppointmentConferenceContext appointmentConferenceAdd(SmcAppointmentConferenceRequest smcAppointmentConferenceRequest, Map<String, String> meetingHeaders) {
        String url = "/conferences";

        String res = null;
        try {
            res = ClientAuthentication.httpPost(meetingUrl + url, JSON.toJSONString(smcAppointmentConferenceRequest), meetingHeaders);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("SMC预约会议请求错误");
        }
        errorString(res);
        SmcAppointmentConferenceContext smcAppointmentConferenceContext = JSON.parseObject(res, SmcAppointmentConferenceContext.class);
        if(smcAppointmentConferenceContext==null){
            SmcErrorResponse smcErrorResponse = JSON.parseObject(res, SmcErrorResponse.class);
            throw new CustomException("SMC预约会议错误:" + smcErrorResponse.getErrorDesc());
        }
        return smcAppointmentConferenceContext;
    }

    public SmcAppointmentConferenceContext appointmentConferenceEdit(String conferenceId,SmcAppointmentConferenceRequest smcAppointmentConferenceRequest, Map<String, String> meetingHeaders) {
        String url = "/conferences/"+conferenceId;

        String  res = ClientAuthentication.httpPut(meetingUrl + url, JSON.toJSONString(smcAppointmentConferenceRequest), meetingHeaders,"utf-8");

        errorString(res);
        SmcAppointmentConferenceContext smcAppointmentConferenceContext = JSON.parseObject(res, SmcAppointmentConferenceContext.class);
        if(smcAppointmentConferenceContext==null){
            SmcErrorResponse smcErrorResponse = JSON.parseObject(res, SmcErrorResponse.class);
            throw new CustomException("SMC预约会议编辑错误:" + smcErrorResponse.getErrorDesc());
        }
        return smcAppointmentConferenceContext;
    }


    public SmcAppointmentConferenceContext appointmentConferenceChange(SmcAppointmentConferenceRequest smcAppointmentConferenceRequest, Map<String, String> meetingHeaders) {
        String url = "/conferences/" + smcAppointmentConferenceRequest.getConference().getId();
        String res = ClientAuthentication.httpPut(meetingUrl + url, JSON.toJSONString(smcAppointmentConferenceRequest), meetingHeaders, null);
        SmcErrorResponse smcErrorResponse = JSON.parseObject(res, SmcErrorResponse.class);
        if (smcErrorResponse == null) {
            return JSON.parseObject(res, SmcAppointmentConferenceContext.class);
        } else {
            throw new CustomException("SMC预约会议修改错误:" + smcErrorResponse.getErrorDesc());
        }
    }

    public void appointmentConferenceDelete(String conferenceId, Map<String, String> meetingHeaders) {
        String url = "/conferences/" + conferenceId;
        ClientAuthentication.httpDeleteUrl(meetingUrl + url, meetingHeaders, null);

    }

    public void conferencesControlChoose(String conferenceId,String doParticipantId,String beChosenParticipantId, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId+"/participants/"+doParticipantId+"/status";
        ChooseMultiPicInfo chooseMultiPicInfo = new ChooseMultiPicInfo();
        ChooseMultiPicInfo.MultiPicInfoDTO multiPicInfoDTO = new ChooseMultiPicInfo.MultiPicInfoDTO();
        multiPicInfoDTO.setPicNum(1);
        multiPicInfoDTO.setMode(1);
        List<ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO> subPicListDTOs = new ArrayList<>();
        ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO subPicListDTO = new ChooseMultiPicInfo.MultiPicInfoDTO.SubPicListDTO();
        subPicListDTO.setStreamNumber(0);
        subPicListDTO.setParticipantId(beChosenParticipantId);
        subPicListDTOs.add(subPicListDTO);
        multiPicInfoDTO.setSubPicList(subPicListDTOs);
        chooseMultiPicInfo.setMultiPicInfo(multiPicInfoDTO);
        try {
            ClientAuthentication.httpPatch(meetingUrl + url,JSON.toJSONString(chooseMultiPicInfo),headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置多画面
     *
     * @param conferenceId
     * @param participantId
     * @param multiPicInfoD
     * @param headers
     */
    public void conferencesControlChoose(String conferenceId, String participantId, MultiPicInfoReq.MultiPicInfoDTO multiPicInfoD, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/participants/" + participantId + "/status";

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("multiPicInfo", multiPicInfoD);
            ClientAuthentication.httpPatch(meetingUrl + url, jsonObject.toJSONString(), headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void conferencesControlChooseMaster(String conferenceId,String masterParticipantId,MultiPicInfoReq multiPicInfoReq, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId+"/participants/"+masterParticipantId+"/status";

        try {
            ClientAuthentication.httpPatch(meetingUrl + url,JSON.toJSONString(multiPicInfoReq),headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void textTipsSetting(String conferenceId, TextTipsSetting textTipsSetting, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId+"/textTips";
        try {
            ClientAuthentication.httpPostVoid(meetingUrl + url,JSON.toJSONString(textTipsSetting),headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取会议详情
     * 提供获取单个会议详情功能，包括会议的基本参数信息以及当前会议的会控状态
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     * @param conferenceId
     * @param headers
     * @return
     */
    public String getConferencesDetail(String conferenceId,  Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId+"/detail";
        try {
            return ClientAuthentication.httpGet(meetingUrl + url,null, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 会议控制
     * 提供会控控制功能，包括设置主席、广播、点名、演示、全体静闭音、声控切换、设
     * 置会议多画面、一键呼叫、自由讨论等会控操作
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     * @param conferenceId
     * @param headers
     * @return
     */
    public void conferencesControl(String conferenceId,String str , Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId+"/status";
        try {
            ClientAuthentication.httpPatch(meetingUrl + url,str,headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String conferencesShareControl(String conferenceId,String str , Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId+"/status";
            String s=ClientAuthentication.httpPatchStr(meetingUrl + url,str,headers);
            errorString(s);
            return s;

    }

    public void  lockPresenter(String conferenceId,Boolean lock,Map<String, String> headers){
        String url = "/online/conferences/" + conferenceId+"/status";
        JSONObject jsonObject = new JSONObject();
        if(lock){
            jsonObject.put("lockPresenter","00000000-0000-0000-0000-000000000000");
        }else {
            jsonObject.put("lockPresenter","");
        }
        try {
            ClientAuthentication.httpPatch(meetingUrl + url,jsonObject.toJSONString(),headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void  lockPresenterParticipant(String conferenceId,String participantId,Boolean lock,Map<String, String> headers){
        String url = "/online/conferences/" + conferenceId+"/status";
        JSONObject jsonObject = new JSONObject();
        if(lock){
            jsonObject.put("lockPresenter",participantId);
        }else {
            jsonObject.put("lockPresenter","");
        }
        try {
            ClientAuthentication.httpPatch(meetingUrl + url,jsonObject.toJSONString(),headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void conferencesStatusControl(String conferenceId, String str, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/status";
        try {
            ClientAuthentication.httpPatch(meetingUrl + url, str, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void conferencesExTendTime(String conferenceId, String str, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/duration";
        ClientAuthentication.httpPut(meetingUrl + url, str, headers,null);
    }

    /**
     * 会议控制
     * 提供会控控制功能，包括设置主席、广播、点名、演示、全体静闭音、声控切换、设
     * 置会议多画面、一键呼叫、自由讨论等会控操作
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     * @param conferenceId
     * @param headers
     * @return
     */
    public void conferencesStatusControl(String conferenceId, ConferenceStatusRequest request, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/status";
        try {
            ClientAuthentication.httpPatch(meetingUrl + url, JSON.toJSONString(request), headers);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    /**
     * 全体静音
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     * 是否静音(静音：true/取
     * 消：false）
     * @param conferenceId
     * @param headers
     * @return
     */
    public String conferencesChatmic(String conferenceId,boolean set , Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId+"/chat/mic";
        try {
            JSONObject obj = new JSONObject();
            obj.put("set",set);
            return ClientAuthentication.httpPost(meetingUrl + url,obj.toJSONString(),headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 全体关闭扬声器
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     * 是否关闭扬声器(关闭：
     * true/取消：false)
     * @param conferenceId
     * @param headers
     * @return
     */
    public String conferencesChatSpeaker(String conferenceId,boolean set , Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId+"/chat/speaker";
        try {
            JSONObject obj = new JSONObject();
            obj.put("set",set);
            return ClientAuthentication.httpPost(meetingUrl + url,obj.toJSONString(),headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 结束会议
     * @param id
     * @param headers
     * @return
     */
    public void endConferences(String id,  Map<String, String> headers) {
        String url = "/online/conferences/" + id;
        ClientAuthentication.httpDeleteUrl(meetingUrl + url, headers, null);

    }


    /**
     * 根据指定的会场号码，查询在SMC侧管理终端注册的SC，到指定的SC上挂断该会场的
     * 呼叫
     *  挂断指定呼叫
     *  该接口需要使用以下角色的帐号进行调用：
     * ● 会议管理员
     * @param uri
     * @param headers
     * @return
     */
    public String quickHangup(String uri, Map<String, String> headers) {
        String url = "/conferences/quickHangup";
        return ClientAuthentication.httpPut(meetingUrl + url, uri, headers, null);

    }


    public SmcConferenceRep list(SmcConferenceRequest request, Map<String, String> headers) {
        String url = "/conferences/conditions?page=" + request.getPage() + "&size=" + request.getSize();
        String s = null;
        try {
            s = ClientAuthentication.httpPost(meetingUrl + url, JSON.toJSONString(request), headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (s == null) {
            return null;
        }

        SmcConferenceRep smcConferenceRep = null;
        try {
            smcConferenceRep = JSON.parseObject(s, SmcConferenceRep.class);
            if (smcConferenceRep == null) {
                SmcErrorResponse smcErrorResponse = JSON.parseObject(s, SmcErrorResponse.class);
                throw new CustomException("会议列表获取错误" + smcErrorResponse.getErrorDesc());
            }
            return smcConferenceRep;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("会议列表获取错误" + e.getMessage());
        }


    }


    public LogsConferenceRep listLog(SmcConferenceRequest request, Map<String, String> headers) {
        String url = "/conferences/logs/search/findAllByConfId?page=" + request.getPage() + "&size=" + request.getSize() + "&confId=" + request.getConferenceId();
        if (Strings.isNotBlank(request.getStartTime()) && Strings.isNotBlank(request.getEndTime())) {
            url = "/conferences/logs/search/findAllByConfIdAndTimeBetween?page=" + request.getPage() + "&size=" + request.getSize() + "&confId=" + request.getConferenceId() + "&start=" + request.getStartTime() + "&end=" + request.getEndTime();
        }
        String s = null;
        try {
            Map<String, String> param = new HashMap<>(3);
            param.put("confId", request.getConferenceId());
            param.put("page", String.valueOf(request.getPage()));
            param.put("size", String.valueOf(request.getSize()));
            s = ClientAuthentication.httpGet(meetingUrl + url, param, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (s == null) {
            return null;
        }

        LogsConferenceRep logsConferenceRep = null;
        try {
            logsConferenceRep = JSON.parseObject(s, LogsConferenceRep.class);
            if (logsConferenceRep == null) {
                SmcErrorResponse smcErrorResponse = JSON.parseObject(s, SmcErrorResponse.class);
                throw new CustomException("会议日志获取错误" + smcErrorResponse.getErrorDesc());
            }
            return logsConferenceRep;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("smcErrorResponse" + e.getMessage());
        }


    }

    /**
     * 提供查询会场视频源的功能；
     * 访问控制
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     *
     * @param conferenceId
     * @param participants
     * @param headers
     * @return
     */
    public List<VideoSourceRep> conferencesVideoSource(String conferenceId, List<String> participants, Map<String, String> headers) {

        String url = "/online/conferences/" + conferenceId + "/participants/videoSource";
        String s = null;
        try {

            s = ClientAuthentication.httpPost(meetingUrl + url, JSONArray.toJSONString(participants), headers);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (s == null) {
            return null;
        }
        return JSON.parseArray(s, VideoSourceRep.class);

    }

    /**
     * 提供设置常用会场功能，常用会场会显示在会场列表前列
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     *
     * @param conferenceId
     * @param set
     * @param participants
     * @param headers
     * @return
     */
    public void participantsOrder(String conferenceId, Boolean set, List<String> participants, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/participants/order";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("set", set);
        jsonObject.put("participantIdList", StringUtils.join(participants, ","));
        ClientAuthentication.httpPut(meetingUrl + url, JSON.toJSONString(jsonObject), headers, null);

    }

    public void participantsOrderCancel(String conferenceId, Boolean set, List<String> participants, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/participants/order";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("set", set);
        jsonObject.put("participantIdList", StringUtils.join(participants, ","));
        try {
            ClientAuthentication.httpPostVoid(meetingUrl + url, JSON.toJSONString(jsonObject), headers);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询常用会场列表
     * 提供获取常用会场详细信息功能，包括会场基本参数，以及会场活动状态
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     *
     * @param conferenceId
     * @param conditions
     * @param headers
     * @return
     */
    public String participantsOrderQuery(String conferenceId, int page, int size, QueryParticipantConditionDto conditions, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/participants/order?page=" + page + "&size=" + size;

        try {
            String s = ClientAuthentication.httpPost(meetingUrl + url, JSON.toJSONString(conditions), headers);
            if (s == null) {
                return null;
            }
            return s;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 查询常用会场列表
     * 提供获取常用会场详细信息功能，包括会场基本参数，以及会场活动状态
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     *
     * @param conferenceId
     * @param conditions
     * @param headers
     * @return
     */
    public ParticipantOrderRep participantsOrderQueryBody(String conferenceId, int page, int size, QueryParticipantConditionDto conditions, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/participants/order?page=" + page + "&size=" + size;

        try {
            String s = ClientAuthentication.httpPost(meetingUrl + url, JSON.toJSONString(conditions), headers);
            if (s == null) {
                return null;
            }
            ParticipantOrderRep participantOrderRep = null;
            participantOrderRep = JSON.parseObject(s, ParticipantOrderRep.class);
            if (participantOrderRep == null) {
                SmcErrorResponse smcErrorResponse = JSON.parseObject(s, SmcErrorResponse.class);
                throw new CustomException("查询常用会场列表错误" + smcErrorResponse.getErrorDesc());
            }
            return participantOrderRep;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据会议Id,查找会议Token，用于websocket鉴权，对于单个子会议状态订阅需要在头
     * 域中添加token
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     * @param id
     * @param headers
     * @return
     */
    public String getTokenByConferencesId(String id, Map<String, String> headers) {
        String url = "/conferences/" + id+"/token";
        try {
            return ClientAuthentication.httpGet(meetingUrl + url, null, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 查看历史会议详情
     * @param id
     * @param headers
     * @return
     */
    public String getConferencesHistoryById(String id, Map<String, String> headers) {
        String url = "/conferences/history/" + id;
        try {
            return ClientAuthentication.httpGet(meetingUrl + url, null, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getSystemtimezone(Map<String, String> headers) {
        String url = "/systemconfig/systemtimezone?lang=zh-CN";
        try {
            return ClientAuthentication.httpGet(meetingUrl + url, null, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查看历史会议详情
     *
     * @param id
     * @param headers
     * @return
     */
    public HistoryConferenceDetail getConferencesHistoryDetailById(String id, Map<String, String> headers) {
        String url = "/conferences/history/" + id;
        try {
            String s = ClientAuthentication.httpGet(meetingUrl + url, null, headers);
            errorString(s);
            return JSON.parseObject(s, HistoryConferenceDetail.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void realTimeInfoGroup(String conferenceId, String groupId, List<String> participantIdList, Map<String, String> meetingHeaders) {
        String url = "/online/conferences/" + conferenceId + "/participants/groups/" + groupId + "/realTimeInfo";

        try {
            ClientAuthentication.httpPostVoid(meetingUrl + url, JSONArray.toJSONString(participantIdList), meetingHeaders);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void realTimeInfoGroupCascade(String conferenceId, String groupId, List<String> participantIdList, Map<String, String> meetingHeaders) {
        String url = "/online/cascade-conferences/" + conferenceId + "/participants/groups/" + groupId+"/realTimeInfo";

        try {
            ClientAuthentication.httpPostVoid(meetingUrl + url, JSONArray.toJSONString(participantIdList), meetingHeaders);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeListGroup(String conferenceId, String groupId, List<String> participantIdList,int page, Map<String, String> meetingHeaders) {
        String url = "/online/conferences/" + conferenceId + "/participants/groups/" + groupId;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageSize",page);
        jsonObject.put("participants",participantIdList);
        try {
            ClientAuthentication.httpPostVoid(meetingUrl + url, jsonObject.toJSONString(), meetingHeaders);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void exportLog(SmcConferenceRequest request, Map<String, String> headers) {
        String url = "/conferences/logs/export";
        try {
            Map<String, String> param = new HashMap<>(3);
            param.put("confId", request.getConferenceId());
            param.put("startTime", request.getStartTime());
            param.put("endTime", request.getEndTime());
            param.put("locale", "ZH_CN");
            ClientAuthentication.httpGetFileDownLoad(meetingUrl + url, param, headers, "smc_" + System.currentTimeMillis() + ".csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void downloadLog(SmcConferenceRequest request, Map<String, String> headers, HttpServletResponse resp) {
        String url = "/conferences/logs/export";
        try {
            Map<String, String> param = new HashMap<>(3);
            param.put("confId", request.getConferenceId());
            param.put("startTime", request.getStartTime());
            param.put("endTime", request.getEndTime());
            param.put("locale", "ZH_CN");
            ClientAuthentication.httpGetDownLoad(meetingUrl + url, param, headers, resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object httpGetListString(SmcConferenceRequest request, Map<String, String> headers) {
        String url = "/conferences/logs/export";
        HashMap<String, Object> ObjectHashMap = new HashMap<>();
        try {
            Map<String, String> param = new HashMap<>(3);
            param.put("confId", request.getConferenceId());
            param.put("startTime", request.getStartTime());
            param.put("endTime", request.getEndTime());
            param.put("locale", "ZH_CN");
            List<String[]> strings = ClientAuthentication.httpGetListString(meetingUrl + url, param, headers);

            if (!CollectionUtils.isEmpty(strings)) {
                ObjectHashMap.put("total", strings.size() - 1);
                ObjectHashMap.put("page", request.getPage());
                ObjectHashMap.put("size", request.getSize());

                String[] keys = strings.get(0);

                int page = request.getPage();
                List<Map> mapList = new ArrayList<>();
                List<String[]> subList;
                if (page <= 1) {
                    subList = strings.subList(1, (request.getSize() + 1) <= strings.size() ? request.getSize() + 1 : strings.size());
                } else {
                    subList = strings.subList((page - 1) * request.getSize() + 1, (page * request.getSize() + 1) <= strings.size() ? (page * request.getSize() + 1) : strings.size());
                }
                for (int i = 0; i < subList.size(); i++) {
                    HashMap<String, String> result = new HashMap<>(request.getSize());
                    String[] value = subList.get(i);
                    for (int j = 0; j < value.length; j++) {
                        String s = value[j];
                        if (keys[j].equals("Time")) {
                            s = s.replaceAll("\t", "").substring(0, s.indexOf(".") - 1);
                        }
                        if(s!=null&&s.contains("\\\\")){
                            s.replaceAll("\\\\",  "");
                        }
                        result.put(lowerFirst(keys[j]),s);
                    }
                    mapList.add(result);
                }
                ObjectHashMap.put("content", mapList);

            }

            return ObjectHashMap;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




    private static String lowerFirst(String str) {

        if(str.contains("ID")&&str.length()==4){
            return "id";
        }

        char[] cs=str.toCharArray();
        cs[0]+=32;
        return String.valueOf(cs);
    }

}
