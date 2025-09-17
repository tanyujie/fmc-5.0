package com.paradisecloud.fcm.smc.cache.modle;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * @author nj
 * @date 2022/8/15 11:04
 */
public class FailureDetailsInfo {
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
            for (Map.Entry<String, Object> e : failureDetails.entrySet())
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
