package com.paradisecloud.smc3.invoker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/22 9:05
 */
public class TicketAPIInvoker extends SmcApiInvoker{

    public TicketAPIInvoker(String rootUrl, String meetingUrl) {
        super(rootUrl, meetingUrl);
    }


    /**
     * 获取会议服务器ticket,主要用于会议控制websocket建链
     * 该接口需要使用以下角色的帐号进行调用
     * ● 全部角色
     * @param userName
     * @param headers
     * @return
     */
    public String getTicket(String userName, Map<String, String> headers) {
        String url = "/tickets";
        Map<String,String> param=new HashMap<>(1);
        param.put("username",userName);
        try {
            return ClientAuthentication.httpGet(meetingUrl + url,param, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
