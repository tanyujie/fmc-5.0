/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FailureDetailsResponse.java
 * Package     : com.paradisecloud.fcm.fme.model.response.failure
 * @author lilinhai 
 * @since 2021-03-10 16:51
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.response.failure;

import java.util.Map.Entry;

import org.springframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONObject;

/**  
 * <pre>失败详情信息</pre>
 * @author lilinhai
 * @since 2021-03-10 16:51
 * @version V1.0  
 */
public class FailureDetailsInfo
{
    private JSONObject failureDetails;

    /**
     * <p>Get Method   :   failureDetails JSONObject</p>
     * @return failureDetails
     */
    public JSONObject getFailureDetails()
    {
        return failureDetails;
    }

    /**
     * <p>Set Method   :   failureDetails JSONObject</p>
     * @param failureDetails
     */
    public void setFailureDetails(JSONObject failureDetails)
    {
        this.failureDetails = failureDetails;
    }

    @Override
    public String toString()
    {
        if (!ObjectUtils.isEmpty(failureDetails))
        {
            StringBuilder r = new StringBuilder();
            for (Entry<String, Object> e : failureDetails.entrySet())
            {
                if (r.length() > 0)
                {
                    r.append('\n');
                }
                r.append(e.getKey());
                if (e.getValue() != null)
                {
                    r.append(": ");
                    if (e.getValue() instanceof String && !ObjectUtils.isEmpty(e.getValue()))
                    {
                        r.append(e.getValue());
                    }
                    else if (e.getValue() instanceof JSONObject)
                    {
                        JSONObject v = (JSONObject) e.getValue();
                        v.forEach((k, v1)->{
                            if (!r.toString().endsWith(": "))
                            {
                                r.append(", ");
                            }
                            r.append(k.replaceAll("^@", "")).append("=").append(v1);
                        });
                    }
                    else
                    {
                        r.append(e.getValue());
                    }
                }
            }
            return r.toString();
        }
        return "";
    }
}
