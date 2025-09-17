package com.paradisecloud.smc3.invoker;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.smc3.model.ParticipantReqDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SmcParticipantInvoker extends SmcApiInvoker {

    public SmcParticipantInvoker(String rootUrl, String meetingUrl) {
        super(rootUrl, meetingUrl);
    }

    /**
     * 提供添加会场功能，
     * 该接口需要使用以下角色的帐号进行调用
     * ● 会议管理员
     * ● 普通用户
     * @param conferenceId
     * @param participants
     * @param headers
     * @return
     */
    public String createParticipants(String conferenceId, List<ParticipantReqDto> participants , Map<String, String> headers) {
        String url = "/online/conferences/"+conferenceId+"/participants";
        try {
            String jsonString = JSON.toJSONString(participants);
            return ClientAuthentication.httpPost(meetingUrl + url,jsonString,headers);
        } catch (IOException e) {
        }
        return null;
    }
}
