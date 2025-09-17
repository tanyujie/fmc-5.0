package com.paradisecloud.smc3.invoker;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.smc3.model.response.UserInfoRep;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/29 11:34
 */
public class SmcUserInvoker extends SmcApiInvoker {

    public SmcUserInvoker(String rootUrl, String meetingUrl) {
        super(rootUrl, meetingUrl);
    }


    /**
     * 查询出所有组织
     * 该接口需要使用以下角色的帐号进行调用：
     * ● 系统管理员
     * ● 安全管理员
     * ● 会议管理员
     * @param headers
     * @return
     */
    public String getUserInfo(String name,  Map<String, String> headers) {
        String url = "/users/search/names";
        HashMap<String, String> param = new HashMap<>();
        param.put("name", name);
        try {
            return ClientAuthentication.httpGet(rootUrl + url, param, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserInfo(String name, String token) {
        String url = "/users/search/names";
        HashMap<String, String> param = new HashMap<>();
        param.put("name", name);
        HashMap<String, String> headers = new HashMap<>();
        param.put("token", token);
        try {
            return ClientAuthentication.httpGet(rootUrl + url, param, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserInfoRep getUserInfoREP(String name, String token) {
        String user = getUserInfo(name, token);
        return JSON.parseObject(user, UserInfoRep.class);
    }
}
