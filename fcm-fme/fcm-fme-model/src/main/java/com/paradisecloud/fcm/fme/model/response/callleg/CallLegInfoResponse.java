package com.paradisecloud.fcm.fme.model.response.callleg;

import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * call leg详情信息
 *
 * @author zt1994 2019/8/26 11:15
 */
@Getter
@Setter
@ToString
public class CallLegInfoResponse
{
    
    /**
     * call leg 详情
     */
    private CallLeg callLeg;
}
