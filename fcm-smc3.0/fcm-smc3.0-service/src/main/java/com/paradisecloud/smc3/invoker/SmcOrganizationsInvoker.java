package com.paradisecloud.smc3.invoker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.smc3.model.response.SmcErrorResponse;
import com.paradisecloud.smc3.model.response.SmcOrganization;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author nj
 * @date 2022/8/23 8:54
 */
public class SmcOrganizationsInvoker extends SmcApiInvoker {

    public SmcOrganizationsInvoker(String rootUrl, String meetingUrl) {
        super(rootUrl, meetingUrl);
    }


    /**
     * 查询出所有组织
     * 该接口需要使用以下角色的帐号进行调用：
     * ● 系统管理员
     * ● 安全管理员
     * ● 会议管理员
     *
     * @param headers
     * @return
     */
    public String getOrganizations(Map<String, String> headers) {
        String url = "/organizations";
        try {
            return ClientAuthentication.httpGet(meetingUrl + url, null, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<SmcOrganization> getOrganizationsList(Map<String, String> headers) {
        String url = "/organizations";
        String res = null;
        try {
            res = ClientAuthentication.httpGet(meetingUrl + url, null, headers);
            errorString(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONArray.parseArray(res, SmcOrganization.class);

    }

    public SmcOrganization create(SmcOrganization smcOrganization, Map<String, String> headers) {
        String url = "/organizations";
        String res = null;
        try {
            res = ClientAuthentication.httpPost(meetingUrl + url, JSON.toJSONString(smcOrganization), headers);

        } catch (IOException e) {
            e.printStackTrace();
        }
        SmcErrorResponse smcErrorResponse = JSON.parseObject(res, SmcErrorResponse.class);
        if (Objects.isNull(smcErrorResponse)) {
            return JSON.parseObject(res, SmcOrganization.class);
        }
        throw new CustomException("新增SMC组织失败");
    }


    public SmcOrganization update(SmcOrganization smcOrganization, Map<String, String> headers) {
        String url = "/organizations/" + smcOrganization.getId();
        String res = ClientAuthentication.httpPut(meetingUrl + url, JSON.toJSONString(smcOrganization), headers, null);
        SmcErrorResponse smcErrorResponse = JSON.parseObject(res, SmcErrorResponse.class);
        if (Objects.isNull(smcErrorResponse)) {
            return JSON.parseObject(res, SmcOrganization.class);
        }
        throw new CustomException("更新SMC组织失败" + smcErrorResponse.getErrorDesc());
    }

    public void delete(String orgId, Map<String, String> headers) {
        String url = "/organizations/" + orgId;
        ClientAuthentication.httpDeleteUrl(meetingUrl + url, headers, null);

    }

    public SmcOrganization getDetail(String orgId, Map<String, String> headers) {
        String url = "/organizations/" + orgId;
        String res = null;
        try {
            res = ClientAuthentication.httpGet(meetingUrl + url, null, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SmcErrorResponse smcErrorResponse = JSON.parseObject(res, SmcErrorResponse.class);
        if (Objects.isNull(smcErrorResponse)) {
            return JSON.parseObject(res, SmcOrganization.class);
        }
        throw new CustomException("获取smc组织失败" + smcErrorResponse.getErrorDesc());

    }

    //GET /organizations/search/names?name=test&id=8ab9e3c4-69ce-04e5-0169-
    //ce058e5d1234

    /**
     * 名字重复查询
     *
     * @param id      父ID
     * @param name
     * @param headers
     * @return
     */
    public SmcOrganization getDetailByName(String name, String id, Map<String, String> headers) {
        String url = "/organizations/search/names";
        Map<String, String> param = new HashMap<>();
        param.put("name", name);
        param.put("id", id);
        String res = null;
        try {
            res = ClientAuthentication.httpGet(meetingUrl + url, param, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SmcErrorResponse smcErrorResponse = JSON.parseObject(res, SmcErrorResponse.class);
        if (Objects.isNull(smcErrorResponse)) {
            return JSON.parseObject(res, SmcOrganization.class);
        }
        throw new CustomException("名称查询smc组织失败" + smcErrorResponse.getErrorDesc());

    }

}
