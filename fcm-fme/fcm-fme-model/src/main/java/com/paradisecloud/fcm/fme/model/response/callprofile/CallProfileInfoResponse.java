package com.paradisecloud.fcm.fme.model.response.callprofile;

import com.paradisecloud.fcm.fme.model.cms.CallProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * callProfile 详情响应类
 */
@Getter
@Setter
@ToString
public class CallProfileInfoResponse
{
    
    /**
     * 单个 callProfile
     */
    private CallProfile callProfile;
}
