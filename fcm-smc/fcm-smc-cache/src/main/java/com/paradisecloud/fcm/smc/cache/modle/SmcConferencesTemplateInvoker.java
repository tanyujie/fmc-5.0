package com.paradisecloud.fcm.smc.cache.modle;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/15 10:40
 */
public class SmcConferencesTemplateInvoker extends SmcApiInvoker {

    public SmcConferencesTemplateInvoker(String rootUrl, String meetingUrl) {
        super(rootUrl, meetingUrl);
    }

    /**
     * 添加模板
     *
     * @param params
     * @param headers
     * @return
     */
    public String creatConferencesTemplate(String params, Map<String, String> headers) {
        String url = "/conferences/templates";
        try {
            return ClientAuthentication.httpPost(meetingUrl + url, params, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 查询模板
     *
     * @param name
     * @param headers
     * @return
     */
    public String queryConferencesTemplate(String name, Map<String, String> headers) {
        String url = "/conferences/templates/conditions?page=0&size=10";
        try {

            JSONObject object = new JSONObject();
            object.put("showCurrentOrg", 1);
            object.put("keyword", name);
            String params = object.toJSONString();

            return ClientAuthentication.httpPost(meetingUrl + url, params, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 删除模板
     *
     * @param id
     * @param headers
     * @return
     */
    public String deleteConferencesTemplate(String id, Map<String, String> headers) {
        String url = "/conferences/templates/" + id;
         ClientAuthentication.httpDeleteUrl(meetingUrl + url, headers, null);
         return null;

    }

    /**
     * 修改模板
     *
     * @param id
     * @param params
     * @param headers
     * @return
     */
    public String putConferencesTemplate(String id, String params, Map<String, String> headers) {
        String url = "/conferences/templates/" + id;
        return ClientAuthentication.httpPut(meetingUrl + url, params, headers, null);

    }

    public String getConferencesTemplateById(String id, Map<String, String> headers) {
        String url = "/conferences/templates/" + id;
        try {
            return ClientAuthentication.httpGet(meetingUrl + url, null, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String startConferencesTemplateById(String id, String params, Map<String, String> headers) {
        String url = "/conferences/templates/" + id + "/conference";
        try {
            return ClientAuthentication.httpPost(meetingUrl + url, params, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
