package com.paradisecloud.fcm.fme.model.response.call;

import com.paradisecloud.fcm.fme.model.cms.Call;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * call 详情响应
 *
 * @author zt1994 2019/8/23 15:25
 */
@Getter
@Setter
@ToString
public class CallInfoResponse
{
    
    /**
     * 单个 call 详情
     */
    private Call call;
}
