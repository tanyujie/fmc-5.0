package com.paradisecloud.fcm.fme.model.response.calllegprofile;

import java.util.List;

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
public class ActiveCallLegProfilesResponse
{
    
    /**
     * callLegProfile 总数
     */
    private Integer total;
    
    /**
     * 单个callLegProfiles
     */
    private List<CallLegProfile> callLegProfile;
}
