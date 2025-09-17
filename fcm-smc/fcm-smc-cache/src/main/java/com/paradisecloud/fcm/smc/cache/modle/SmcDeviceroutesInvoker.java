package com.paradisecloud.fcm.smc.cache.modle;

import java.io.IOException;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/23 10:43
 */
public class SmcDeviceroutesInvoker extends SmcApiInvoker{
    public SmcDeviceroutesInvoker(String rootUrl, String meetingUrl) {
        super(rootUrl, meetingUrl);
    }


    /**
     * 终端号码
     * @param zoneid
     * @param headers
     * @return
     */
    public String getDeviceroutes(String zoneid,Map<String, String> headers) {
        String url = "/deviceroutes"+"?zoneid="+zoneid+"&number=1";
        try {
            return ClientAuthentication.httpGet(rootUrl + url,null, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
