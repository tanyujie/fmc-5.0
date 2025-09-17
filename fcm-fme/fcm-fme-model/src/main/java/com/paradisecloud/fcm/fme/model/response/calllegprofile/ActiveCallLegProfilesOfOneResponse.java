package com.paradisecloud.fcm.fme.model.response.calllegprofile;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.fme.model.cms.CallLegProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个callLegProfiles
 *
 * @author zt1994 2019/8/19 11:32
 */
@Getter
@Setter
@ToString
public class ActiveCallLegProfilesOfOneResponse
{
    
    /**
     * callLegProfile 总数
     */
    @JSONField(name = "@total")
    private Integer total;
    
    /**
     * 单个callLegProfiles
     */
    private CallLegProfile callLegProfile;
}
