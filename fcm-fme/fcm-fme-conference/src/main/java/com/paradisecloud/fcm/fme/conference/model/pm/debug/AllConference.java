/* 

 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AllConference.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.debug
 * @author sinhy 
 * @since 2021-09-18 08:27
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.debug;

import java.lang.reflect.Method;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.proxy.ProxyMethod;
import com.sinhy.utils.DateUtils;

public class AllConference extends ProxyMethod
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-18 08:27 
     * @param method 
     */
    protected AllConference(Method method)
    {
        super(method);
    }
    
    public JSONObject allConference()
    {
        JSONObject topJsonObj = new JSONObject();
        topJsonObj.put("totalConference", ConferenceContextCache.getInstance().size());
        JSONArray ja = new JSONArray();
        ConferenceContextCache.getInstance().values().forEach((cc) -> {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("conferenceNumber", cc.getConferenceNumber());
            jsonObj.put("conferenceName", cc.getName());
            jsonObj.put("startTime", cc.getStartTime());
            jsonObj.put("duration", DateUtils.toTimeDuration(System.currentTimeMillis() - cc.getStartTime().getTime()));
            jsonObj.put("deptId", cc.getDeptId());
            jsonObj.put("deptName", SysDeptCache.getInstance().get(cc.getDeptId()).getDeptName());
            ja.add(jsonObj);
        });
        topJsonObj.put("conferences", ja);
        return topJsonObj;
    }
}
