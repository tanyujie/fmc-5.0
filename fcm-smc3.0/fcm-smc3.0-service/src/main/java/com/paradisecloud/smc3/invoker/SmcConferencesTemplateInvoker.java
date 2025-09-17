package com.paradisecloud.smc3.invoker;

import com.alibaba.fastjson.JSONObject;

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


    public String getConferencesCascadeTree(String id, Map<String, String> headers) {
        String url = "/cascade-conferences/" + id+"/tree";
        try {
            return ClientAuthentication.httpGet(meetingUrl + url, null, headers);
        } catch (IOException e) {
        }
        return null;
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
        }
        return null;

    }

    /**
     * 添加模板
     *
     * @param params
     * @param headers
     * @return
     */
    public String creatConferencesCascadeTemplate(String params, Map<String, String> headers) {
        String url = "/cascade-templates";
        try {
            return ClientAuthentication.httpPost(meetingUrl + url, params, headers);
        } catch (IOException e) {
        }
        return null;

    }

    /**
     * 查询模板
     *
     * @param params
     * @param headers
     * @return
     */
    public String queryConferencesTemplate(String params, Map<String, String> headers) {
        String url = "/conferences/templates/conditions?page=0&size=500";
        try {
//
//            JSONObject object = new JSONObject();
//            object.put("showCurrentOrg", 1);
//            object.put("keyword", name);
//            String params = object.toJSONString();

            return ClientAuthentication.httpPost(meetingUrl + url, params, headers);
        } catch (IOException e) {
        }
        return null;

    }

    public String startCascadeConferencesTemplateById(String templateId,String params, Map<String, String> headers){
        String url = "/cascade-conferences/"+templateId;
        try {
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

    public void deleteCascadeTemplate(String templateId, Map<String, String> meetingHeaders){
        String url = "/cascade-templates/"+templateId;

        ClientAuthentication.httpDeleteUrl(meetingUrl + url, meetingHeaders, null);
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

    public String updateCascadeTemplate(String toJSONString,String templateId, Map<String, String> meetingHeaders){
        String url = "/cascade-templates/"+templateId;

        String s = ClientAuthentication.httpPut(meetingUrl + url, toJSONString, meetingHeaders, null);

        return s;
    }

    public String getConferencesTemplateById(String id, Map<String, String> headers) {
        String url = "/conferences/templates/" + id;
        try {
            return ClientAuthentication.httpGet(meetingUrl + url, null, headers);
        } catch (IOException e) {
        }
        return null;
    }


    public String startConferencesTemplateById(String id, String params, Map<String, String> headers) {
        String url = "/conferences/templates/" + id + "/conference";
        try {
            return ClientAuthentication.httpPost(meetingUrl + url, params, headers);
        } catch (IOException e) {
        }
        return null;
    }


}
