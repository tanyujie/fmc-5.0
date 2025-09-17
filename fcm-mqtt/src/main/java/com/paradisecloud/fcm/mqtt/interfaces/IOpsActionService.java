package com.paradisecloud.fcm.mqtt.interfaces;

import com.alibaba.fastjson.JSONObject;

public interface IOpsActionService {

    /** OPS注册 */
    void register(JSONObject jsonS, String clientId);

    /** 推送OPS注册信息 */
    void pushRegister(String clientId);
}
