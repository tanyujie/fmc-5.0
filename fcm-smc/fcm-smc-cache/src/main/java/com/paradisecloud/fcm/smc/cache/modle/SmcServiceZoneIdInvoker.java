package com.paradisecloud.fcm.smc.cache.modle;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.com.fcm.smc.modle.DefaultServiceZoneIdRep;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author nj
 * @date 2022/8/23 10:31
 */
public class SmcServiceZoneIdInvoker extends SmcApiInvoker{

    public SmcServiceZoneIdInvoker(String rootUrl, String meetingUrl) {
        super(rootUrl, meetingUrl);
    }


    public String getServiceZoneId(Map<String, String> headers) {
        String url = "/servicezones?page=0&size=10&sort=createdDate,desc";
        try {
            return ClientAuthentication.httpGet(rootUrl + url,null, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDefaultServiceZoneId(Map<String, String> headers) {
        String url = "/servicezones?page=0&size=10&sort=createdDate,desc";
        try {
            String s = ClientAuthentication.httpGet(rootUrl + url, null, headers);
            DefaultServiceZoneIdRep defaultServiceZoneIdRep = JSON.parseObject(s, DefaultServiceZoneIdRep.class);
            List<DefaultServiceZoneIdRep.ContentDTO> content = defaultServiceZoneIdRep.getContent();
            Optional<DefaultServiceZoneIdRep.ContentDTO> aDefault = content.stream().filter(p -> Objects.equals(p.getName(), "Default")).findFirst();
            if(aDefault.isPresent()){
                return  aDefault.get().getId();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
