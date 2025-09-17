package com.paradisecloud.fcm.fme.model.response.calllegprofile;

import com.paradisecloud.fcm.fme.model.cms.CallLegProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * callLegProfile 详情响应类
 *
 * @author zt1994 2019/8/19 11:13
 */
@Getter
@Setter
@ToString
public class CallLegProfileInfoResponse
{
    
    /**
     * 单个 callLegProfile
     */
    private CallLegProfile callLegProfile;
}
