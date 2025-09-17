package com.paradisecloud.smc3.invoker;

import java.io.IOException;
import java.util.Map;

/**
 * @author nj
 * @date 2023/2/15 10:07
 */
public class SmcTicketInvoker extends SmcApiInvoker {
    public SmcTicketInvoker(String rootUrl, String meetingUrl) {
        super(rootUrl, meetingUrl);
    }


    public String getTicket(String userName, Map<String, String> headers) {
        String url = "/tickets?username=" + userName;
        try {
            return ClientAuthentication.httpGet(meetingUrl + url,null,headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
