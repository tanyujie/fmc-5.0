package com.paradisecloud.fcm.smc.cache.modle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.com.fcm.smc.modle.MasterPollTemplate;
import com.paradisecloud.com.fcm.smc.modle.PresetMultiPicReqDto;
import com.paradisecloud.com.fcm.smc.modle.request.BroadcastPollRequest;
import com.paradisecloud.com.fcm.smc.modle.request.ChairmanPollOperateReq;
import com.paradisecloud.com.fcm.smc.modle.request.MultiPicInfoReq;
import com.paradisecloud.com.fcm.smc.modle.request.MultiPicPollRequest;
import com.paradisecloud.common.exception.CustomException;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/26 11:11
 */
public class SmcMultiPicPollInvoker extends SmcApiInvoker{

    public static final String X_30020003 = "0x30020003";

    public SmcMultiPicPollInvoker(String rootUrl, String meetingUrl) {
        super(rootUrl, meetingUrl);
    }

    /**
     * 提供多画面轮询设置功能，包括多画面轮询列表的设置和多画面轮询开始、暂停、取
     * 消的控制。
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     *
     * @param conferenceId
     * @param multiPicPollRequest
     * @param headers
     */
    public void createMultiPicPoll(String conferenceId, MultiPicPollRequest multiPicPollRequest, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/multiPicPoll";
        try {
            String multiPicPollRequestsTr = JSONObject.toJSONString(multiPicPollRequest);
            ClientAuthentication.httpPostVoid(meetingUrl + url, multiPicPollRequestsTr, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopMultiPicPoll(String conferenceId, Map<String, String> headers){
        String url = "/online/conferences/" + conferenceId + "/multiPicPoll";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pollStatus","STOP");
            ClientAuthentication.httpPostVoid(meetingUrl + url, jsonObject.toJSONString(), headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startMultiPicPoll(String conferenceId, Map<String, String> headers){
        String url = "/online/conferences/" + conferenceId + "/multiPicPoll";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pollStatus","START");
            String s = ClientAuthentication.httpPost(meetingUrl + url, jsonObject.toJSONString(), headers);
            if(Strings.isNotBlank(s)&&s.contains("0x30020003")){
                throw new CustomException("多画面轮询正在进行，无法设置和开始主席轮询");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void cancelMultiPicPoll(String conferenceId, Map<String, String> headers){
        String url = "/online/conferences/" + conferenceId + "/multiPicPoll";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pollStatus","CANCEL");
            ClientAuthentication.httpPostVoid(meetingUrl + url, jsonObject.toJSONString(), headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void pollStatus(String conferenceId,String pollStatus, Map<String, String> headers){
        String url = "/online/conferences/" + conferenceId + "/multiPicPoll";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pollStatus",pollStatus);
            ClientAuthentication.httpPost(meetingUrl + url, jsonObject.toJSONString(), headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 主席轮询设置
     *
     * @param conferenceId
     * @param masterPollTemplate
     * @param headers
     */
    public void createChairmanPollMultiPicPoll(String conferenceId, MasterPollTemplate masterPollTemplate, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/participants/chairmanPoll";
        try {
            String masterPollTemplateStr = JSONObject.toJSONString(masterPollTemplate);
            ClientAuthentication.httpPostVoid(meetingUrl + url, masterPollTemplateStr, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询主席轮询设置
     *
     * @param conferenceId
     * @param headers
     */
    public MasterPollTemplate queryChairmanPollMultiPicPoll(String conferenceId, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/participants/chairmanPoll";
        MasterPollTemplate masterPollTemplate = null;
        try {
            String result = ClientAuthentication.httpGet(meetingUrl + url, null, headers);
            errorString(result);
            masterPollTemplate = JSON.parseObject(result, MasterPollTemplate.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return masterPollTemplate;
    }

    /**
     * 主席轮询操作
     *
     * @param conferenceId
     * @param chairmanPollOperateReq
     * @param headers
     */
    public void chairmanPollOperate(String conferenceId, ChairmanPollOperateReq chairmanPollOperateReq, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/participants/chairmanPoll";
        try {
            String masterPollTemplateStr = JSONObject.toJSONString(chairmanPollOperateReq);
            ClientAuthentication.httpPostVoid(meetingUrl + url, masterPollTemplateStr, headers);
//            String result = ClientAuthentication.httpPost(meetingUrl + url, masterPollTemplateStr, headers);
//            if (result != null && result.contains(ConstAPI.NOT_SET_FOR_CHAIRMAN_POLL)) {
//                throw new CustomException("主席轮询未设置");
//            }
//            errorString(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提供多画面轮询设置功能，包括多画面轮询列表的设置和多画面轮询开始、暂停、取
     * 消的控制。
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     *
     * @param conferenceId
     * @param headers
     */
    public MultiPicPollRequest queryMulitiPicPoll(String conferenceId, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/multiPicPoll";
        MultiPicPollRequest multiPicPollRequest = null;
        try {
            String result = ClientAuthentication.httpGet(meetingUrl + url, null, headers);
            errorString(result);
            multiPicPollRequest = JSON.parseObject(result, MultiPicPollRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return multiPicPollRequest;
    }


    /**
     * 提供定时广播设置功能，包括定时广播列表的设置和定时广播开始、暂停、取消的控
     * 制。
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     * @param conferenceId
     * @param broadcastPollRequest
     * @param headers
     */
    public void broadcastPoll(String conferenceId, BroadcastPollRequest broadcastPollRequest, Map<String, String> headers) {
        String url = "/online/cascade-conferences/" + conferenceId + "/broadcastPoll";
        try {
            String multiPicPollRequestsTr = JSONObject.toJSONString(broadcastPollRequest);
            ClientAuthentication.httpPost(meetingUrl + url, multiPicPollRequestsTr, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<PresetMultiPicReqDto> getConferencesPresetParam(String conferenceId, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/presetParam";
        List<PresetMultiPicReqDto> presetMultiPics = null;
        try {
            String result = ClientAuthentication.httpGet(meetingUrl + url, null, headers);
            errorString(result);
            presetMultiPics = JSONArray.parseArray(result, PresetMultiPicReqDto.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return presetMultiPics;
    }

    public List<MultiPicInfoReq> getConferencesPresetParam2(String conferenceId, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/presetParam";
        List<MultiPicInfoReq> presetMultiPics = null;
        try {
            String result = ClientAuthentication.httpGet(meetingUrl + url, null, headers);
            errorString(result);
            presetMultiPics = JSONArray.parseArray(result, MultiPicInfoReq.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return presetMultiPics;
    }


    public  void chairmanParticipantMultiPicPoll(String conferenceId, MultiPicPollRequest multiPicPollRequest, Map<String, String> meetingHeaders){
        String url = "/online/conferences/" + conferenceId + "/participants/participantMultiPicPoll";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("chairmanPoll",true);
            jsonObject.put("participantId","");
            JSONObject multiPicPollDtojsonObject = new JSONObject();
            multiPicPollDtojsonObject.put("mode",multiPicPollRequest.getMode());
            multiPicPollDtojsonObject.put("picNum",multiPicPollRequest.getPicNum());
            multiPicPollDtojsonObject.put("pollStatus",multiPicPollRequest.getPollStatus());
            multiPicPollDtojsonObject.put("subPicPollInfoList",multiPicPollRequest.getSubPicPollInfoList());
            jsonObject.put("multiPicPollDto",multiPicPollDtojsonObject);
            ClientAuthentication.httpPostVoid(meetingUrl + url,  JSONObject.toJSONString(jsonObject), meetingHeaders);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  MultiPicPollRequest chairmanParticipantMultiPicPollQuery(String conferenceId, String participantId, Map<String, String> meetingHeaders){
        //?participantId="+participantId+"&chairmanPoll=true
        String url;
        if(participantId==null){
             url = "/online/conferences/" + conferenceId + "/participants/participantMultiPicPoll?participantId=&chairmanPoll=true";
        }else {
             url = "/online/conferences/" + conferenceId + "/participants/participantMultiPicPoll?participantId="+participantId+"&chairmanPoll=true";
        }

        Map<String,String> param = new HashMap<>();
        param.put("participantId",participantId);
        param.put("chairmanPoll","true");
        MultiPicPollRequest request=new MultiPicPollRequest();
        try {
            String s = ClientAuthentication.httpGet(meetingUrl + url, null, meetingHeaders);
            errorString(s);
            request=JSONObject.parseObject(s,MultiPicPollRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return request;
    }

    public  void chairmanParticipantMultiPicPollOperate(String conferenceId,ChairmanPollOperateReq chairmanPollOperateReq,Map<String, String> meetingHeaders){
        String url = "/online/conferences/" + conferenceId + "/participants/participantMultiPicPoll";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("chairmanPoll",true);
        jsonObject.put("participantId","");
        JSONObject multiPicPollDtojsonObject = new JSONObject();
        multiPicPollDtojsonObject.put("pollStatus",chairmanPollOperateReq.getPollStatus());
        jsonObject.put("multiPicPollDto",multiPicPollDtojsonObject);
        try {
            String s = ClientAuthentication.httpPost(meetingUrl + url, JSONObject.toJSONString(jsonObject), meetingHeaders);
            if(Strings.isNotBlank(s)&&s.contains(X_30020003)){
                throw new CustomException("多画面轮询正在进行，无法设置和开始主席轮询");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
