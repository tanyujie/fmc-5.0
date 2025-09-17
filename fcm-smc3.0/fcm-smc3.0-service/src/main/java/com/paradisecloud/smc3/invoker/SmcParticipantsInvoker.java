package com.paradisecloud.smc3.invoker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.smc3.model.*;
import com.paradisecloud.smc3.model.request.TextTipsSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/19 10:44
 */
public class SmcParticipantsInvoker extends SmcApiInvoker {


    private Logger logger= LoggerFactory.getLogger(SmcParticipantsInvoker.class);

    public SmcParticipantsInvoker(String rootUrl, String meetingUrl) {
        super(rootUrl, meetingUrl);
    }

    /**
     * 获取会场列表
     * 提供获取会场列表功能，包括会场基本参数，以及当前会场活动状态
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     * 是否关闭扬声器(关闭：
     * true/取消：false)
     * @param conferenceId
     * @param headers
     * @return
     */
    public String getConferencesParticipantsState(String conferenceId, int page,int size,  Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId+"/participants/conditions?page="+page+"&size="+size;
        JSONObject object = new JSONObject();
        object.put("showCurrentOrg", 1);
        object.put("name", "");
        String params = object.toJSONString();
        try {
            return ClientAuthentication.httpPost(meetingUrl + url,params,headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getConferencesParticipantsStateCascade(String conferenceId,Boolean  notDisplay,int page,int size,  Map<String, String> headers) {
        String url = "/online/cascade-conferences/" + conferenceId+"/participants/conditions?page="+page+"&size="+size;
        JSONObject object = new JSONObject();
        object.put("showCurrentOrg", notDisplay==true?1:0);
        object.put("name", "");
        object.put("notDisplay", true);
        String params = object.toJSONString();
        try {
            return ClientAuthentication.httpPost(meetingUrl + url,params,headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     *  查询会场视音频能力
     * 提供获取会场能力功能，包括本端能力、远端能力以及公共能力
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     * 是否关闭扬声器(关闭：
     * true/取消：false)
     * @param conferenceId
     * @param headers
     * @return
     */
    public String getConferencesParticipantCapability(String conferenceId, String participantId,  Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId+"/participants/"+participantId+"/capability";
        try {
            return ClientAuthentication.httpGet(meetingUrl + url,null,headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }


    /**
     * 添加会场
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     * 是否关闭扬声器(关闭：
     * true/取消：false)
     * @param conferenceId
     * @param headers
     * @return
     */
    public void createParticipants(String conferenceId, List<ParticipantReqDto> participants , Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId+"/participants";
        try {
            String s = ClientAuthentication.httpPost(meetingUrl + url, JSON.toJSONString(participants), headers);
            if(s!=null){
                if(s.contains(ConstAPI.X_2003_B)){
                    throw new CustomException("会议人数已达到上限，如需加入请联系会议管理员");
                }
                if(s.contains(ConstAPI.ERRORNO_20040010)){
                    throw new CustomException("MCU资源不足呼叫失败");
                }
                throw new CustomException(s);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public String createParticipantsStr(String conferenceId, List<ParticipantReqDto> participants , Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId+"/participants";

        String s = null;
        try {
            s = ClientAuthentication.httpPost(meetingUrl + url, JSON.toJSONString(participants), headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return s;

    }

    public void createParticipantsCascade(String conferenceId, List<ParticipantReqDto> participants , Map<String, String> headers) {
        String url = "/online/cascade-conferences/" + conferenceId+"/participants";
        try {
            String s = ClientAuthentication.httpPost(meetingUrl + url, JSON.toJSONString(participants), headers);
            if(s!=null){
                if(s.contains(ConstAPI.X_2003_B)){
                    throw new CustomException("会议人数已达到上限，如需加入请联系会议管理员");
                }
                throw new CustomException(s);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }



    public  void remind(String conferenceId, String participantId, Map<String, String> meetingHeaders){
        String url = "/online/conferences/" + conferenceId+"/participants/"+participantId+"/remind";
        try {
            ClientAuthentication.httpPostVoid(meetingUrl + url,JSON.toJSONString(participantId),meetingHeaders);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }



    /**
     *  会场控制
     * 提供更新会场状态功能，包括呼叫挂断、静闭音、开关扬声器、锁定视频源、调节音
     * 量、设置会场视频源等功能
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     * 是否关闭扬声器(关闭：
     * true/取消：false)
     * @param conferenceId
     * @param headers
     * @return
     */
    public void conferencesParticipantStatusOnly(String conferenceId, String participantId, ParticipantStatus participantStatus, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/participants/" + participantId + "/status";
        try {
            String participantStatusStr = JSONObject.toJSONString(participantStatus);
            ClientAuthentication.httpPatch(meetingUrl + url, participantStatusStr, headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    public void conferencesParticipantStatusOnlyCascade(String conferenceId, String participantId, ParticipantStatus participantStatus, Map<String, String> headers) {
        String url = "/online/cascade-conferences/" + conferenceId + "/participants/" + participantId + "/status";
        try {
            String participantStatusStr = JSONObject.toJSONString(participantStatus);
            ClientAuthentication.httpPatch(meetingUrl + url, participantStatusStr, headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }


    /**
     *  删除会场
     * 提供删除会场功能，支持批量操作
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     * 是否关闭扬声器(关闭：
     * true/取消：false)
     * @param conferenceId
     * @param headers
     * @return
     */
    public void deleteParticipants(String conferenceId, List<String> participantIds, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/participants/";
        try {
            String participantStatusStr = JSONObject.toJSONString(participantIds);
            ClientAuthentication.httpDeleteVoid(meetingUrl + url, participantStatusStr, headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void deleteParticipantsCascade(String conferenceId, List<String> participantIds, Map<String, String> headers) {
        String url = "/online/cascade-conferences/" + conferenceId + "/participants/";
        try {
            String participantStatusStr = JSONObject.toJSONString(participantIds);
            ClientAuthentication.httpDeleteVoid(meetingUrl + url, participantStatusStr, headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     *  批量会场控制
     * 提供批量更新会场状态功能，包括呼叫挂断、静闭音、关闭扬声器、打开视频源等功
     * 能
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     * PATCH
     * 是否关闭扬声器(关闭：
     * true/取消：false)
     * @param conferenceId
     * @param headers
     * @return
     */
    public void PATCHParticipants(String conferenceId, List<ParticipantStatusDto> participantStatusList, Map<String, String> headers) {
        String url = "/online/cascade-conferences/" + conferenceId + "/participants/status";
        try {
            String participantStatusStr = JSONObject.toJSONString(participantStatusList);
            ClientAuthentication.httpPatch(meetingUrl + url, participantStatusStr, headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }


    public void PATCHParticipantsOnly(String conferenceId, List<ParticipantStatusDto> participantStatusList, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/participants/status";
        try {
            String participantStatusStr = JSONObject.toJSONString(participantStatusList);
            ClientAuthentication.httpPatch(meetingUrl + url, participantStatusStr, headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }


    public void PATCHParticipantsOnlyCascade(String conferenceId, List<ParticipantStatusDto> participantStatusList, Map<String, String> headers) {
        String url = "/online/cascade-conferences/" + conferenceId + "/participants/status";
        try {
            String participantStatusStr = JSONObject.toJSONString(participantStatusList);
            ClientAuthentication.httpPatch(meetingUrl + url, participantStatusStr, headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


    public void textTipsSetting(String conferenceId, String participantId, TextTipsSetting textTipsSetting, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId+"/participants"+participantId+"/textTips";
        try {
            ClientAuthentication.httpPostVoid(meetingUrl + url,JSON.toJSONString(textTipsSetting),headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void textTipsSetting(String conferenceId, String participantId, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId+"/participants/"+participantId+"/textTips";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content","会议主席正在点调对话中,请耐心等候......");
            jsonObject.put("disPosition","2");
            jsonObject.put("displayType","4");
            jsonObject.put("opType","SET");
            jsonObject.put("type","0");
            ClientAuthentication.httpPostVoid(meetingUrl + url,JSON.toJSONString(jsonObject),headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void textTipsSettingCascade(String conferenceId, String participantId, Map<String, String> headers) {
        String url = "/online/cascade-conferences/" + conferenceId+"/participants/"+participantId+"/textTips";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content","会议主席正在点调对话中,请耐心等候......");
            jsonObject.put("disPosition","2");
            jsonObject.put("displayType","4");
            jsonObject.put("opType","SET");
            jsonObject.put("type","0");
            ClientAuthentication.httpPostVoid(meetingUrl + url,JSON.toJSONString(jsonObject),headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void textTipsSettingCancel(String conferenceId, String participantId, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId+"/participants/"+participantId+"/textTips";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content","会议主席正在点调对话中,请耐心等候......");
            jsonObject.put("disPosition","2");
            jsonObject.put("displayType","4");
            jsonObject.put("opType","CANCEL");
            jsonObject.put("type","0");
            ClientAuthentication.httpPostVoid(meetingUrl + url,JSON.toJSONString(jsonObject),headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void textTipsSettingCancelCascade(String conferenceId, String participantId, Map<String, String> headers) {
        String url = "/online/cascade-conferences/" + conferenceId+"/participants/"+participantId+"/textTips";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content","");
            jsonObject.put("disPosition","2");
            jsonObject.put("displayType","4");
            jsonObject.put("opType","CANCEL");
            jsonObject.put("type","0");
            ClientAuthentication.httpPostVoid(meetingUrl + url,JSON.toJSONString(jsonObject),headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     *  批量会场控制
     * 提供批量更新会场状态功能，包括呼叫挂断、静闭音、关闭扬声器、打开视频源等功
     * 能
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     * PATCH
     * 是否关闭扬声器(关闭：
     * true/取消：false)
     * @param conferenceId
     * @param headers
     * @return
     */
    public void PATCHParticipantsMap(String conferenceId, List<?> participantStatusList, Map<String, String> headers) {
        String url = "/online/cascade-conferences/" + conferenceId + "/participants/status";
        try {
            String participantStatusStr = JSONObject.toJSONString(participantStatusList);
            ClientAuthentication.httpPatch(meetingUrl + url, participantStatusStr, headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    /**
     * 查询会场详情
     * 提供获取指定会场详情功能，包括会场基本参数，以及所属MCU和协议参数
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     *
     * @param conferenceId
     * @param participantId
     * @param headers
     * @return
     */
    public String getParticipantsDetailInfo(String conferenceId, String participantId, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/participants/" + participantId + "/detailInfo";
        try {
            return ClientAuthentication.httpGet(meetingUrl + url, null, headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }


    /**
     * 查询会场详情
     * 提供获取指定会场详情功能，包括会场基本参数，以及所属MCU和协议参数
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     *
     * @param conferenceId
     * @param participantId
     * @param headers
     * @return
     */
    public ParticipantRspDto getParticipantsDetailInfoDto(String conferenceId, String participantId, Map<String, String> headers) {
        String s = getParticipantsDetailInfo(conferenceId, participantId, headers);
        ParticipantRspDto participantRspDto = JSON.parseObject(s, ParticipantRspDto.class);
        return participantRspDto;

    }



    public void participantsCameraControl(String conferenceId, String participantId,JSONObject jsonObject, Map<String, String> headers) {
        String url = "/online/conferences/" + conferenceId + "/participants/" + participantId + "/camera";
        try {
             ClientAuthentication.httpPostVoid(meetingUrl + url, jsonObject.toJSONString(), headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void participantsCameraControlCascade(String conferenceId, String participantId,JSONObject jsonObject, Map<String, String> headers) {
        String url = "/online/cascade-conferences/" + conferenceId + "/participants/" + participantId + "/camera";
        try {
            ClientAuthentication.httpPostVoid(meetingUrl + url, jsonObject.toJSONString(), headers);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 修改与会者名称
     * @param conferenceId
     * @param jsonObject
     * @param headers
     */
    public void  participantsParam(String conferenceId,JSONObject jsonObject, Map<String, String> headers){
        String url = "/online/conferences/" + conferenceId + "/participants/param";
        ClientAuthentication.httpPut(meetingUrl + url, jsonObject.toJSONString(), headers,"utf-8");
    }

}
