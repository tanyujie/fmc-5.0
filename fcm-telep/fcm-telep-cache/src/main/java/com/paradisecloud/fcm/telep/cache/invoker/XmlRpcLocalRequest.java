package com.paradisecloud.fcm.telep.cache.invoker;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.telep.cache.exception.FaultException;
import com.paradisecloud.fcm.telep.cache.util.BeanMapTool;
import com.paradisecloud.fcm.telp.model.response.FaultResponse;
import org.apache.logging.log4j.util.Strings;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

/**
 * @author nj
 * @date 2022/10/12 15:31
 */

public class XmlRpcLocalRequest {

    private static final Logger logger = LoggerFactory.getLogger(XmlRpcLocalRequest.class);

    public static final String OPERATION_SUCCESSFUL = "operation successful";
    public static final String FOCUS_PARTICIPANT = "focusParticipant";
    public static final String PARTICIPANT_CONNECT = "participant.connect";
    private String userName;
    private String password;
    private String rootUrl;

    public static final String AUTHENTICATION_USER = "authenticationUser";
    public static final String AUTHENTICATION_PASSWORD = "authenticationPassword";


    public XmlRpcLocalRequest(String rootUrl, String userName, String password) {
        this.rootUrl = rootUrl;
        this.userName = userName;
        this.password = password;

    }

    public <T> T execute(String method, Map<String, Object> requestParams, Class<T> clazz) {
        return JSON.parseObject( execute(method, requestParams), clazz);

    }

    public String execute(String method, Map<String, ?> requestParams) {
        XmlRpcClient xmlRpcClient = null;
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(rootUrl));
            config.setConnectionTimeout(6000);
            config.setReplyTimeout(0);
            xmlRpcClient = new XmlRpcClient();
            xmlRpcClient.setConfig(config);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Map<String, Object> issue = getAuthParam();
        if (!Objects.isNull(requestParams)) {

            requestParams.forEach((k, v) -> {
                if (v != null) {

                    if(Objects.equals(FOCUS_PARTICIPANT,k)){
                        Hashtable<String, Object> objectObjectHashtable = new Hashtable<>();
                        Map<String, ?> stringMap = BeanMapTool.beanToMap(v);
                        stringMap.forEach((k1,v1)->{
                            if(!Objects.isNull(v1)){
                                objectObjectHashtable.put(k1,v1);
                            }
                        });
                        issue.put(k, objectObjectHashtable);
                    }else {
                        issue.put(k, v);
                    }
                }

            });
        }
        Vector<Object> params = new Vector<>();
        params.addElement(issue);
        Object execute = null;
        try {
            execute = xmlRpcClient.execute(method, params);
            String jsonString = JSON.toJSONString(execute);
            failCheck(jsonString);
            logger.info(method + "操作成功:" + jsonString);
            return jsonString;
        } catch (XmlRpcException e) {
            e.printStackTrace();
            if(Objects.equals(PARTICIPANT_CONNECT,method)){
                throw new CustomException("该设备无法重呼");
            }
            throw new CustomException("【方法" + method + "错误】" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private void failCheck(String jsonString) {
        FaultResponse faultResponse = JSON.parseObject(jsonString, FaultResponse.class);
        if (faultResponse != null && Strings.isNotBlank(faultResponse.getFaultString())) {
            throw new FaultException("操作失败！" + faultResponse.getFaultString());
        }
    }


    private Map<String, Object> getAuthParam() {
        Map<String, Object> issue = new Hashtable<>();
        issue.put(AUTHENTICATION_USER, userName);
        issue.put(AUTHENTICATION_PASSWORD, password);
        return issue;
    }
}
