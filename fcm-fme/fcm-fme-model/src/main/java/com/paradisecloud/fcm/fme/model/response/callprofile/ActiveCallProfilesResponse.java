package com.paradisecloud.fcm.fme.model.response.callprofile;

import java.util.List;

import com.paradisecloud.fcm.fme.model.cms.CallProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个callProfile
 */
@Getter
@Setter
@ToString
public class ActiveCallProfilesResponse
{
    
    /**
     * callLegProfile 总数
     */
    private Integer total;
    
    /**
     * 单个callLegProfiles
     */
    private List<CallProfile> callProfile;
}
