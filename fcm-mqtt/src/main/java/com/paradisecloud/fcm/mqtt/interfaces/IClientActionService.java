package com.paradisecloud.fcm.mqtt.interfaces;

import com.alibaba.fastjson.JSONObject;

public interface IClientActionService {

    /** 客户端注册 */
    void register(JSONObject jsonS, String clientId);

    /** 推送客户端注册信息 */
    void pushRegister(String clientId);

    /** 推送客户端注册信息 */
    void asrSign(String clientId);

    /** 更新客户端字license */
    void updateLicense(String clientId, String license);
}
