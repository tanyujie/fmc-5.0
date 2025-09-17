package com.paradisecloud.fcm.fme.model.response.dtmfprofile;

import java.util.List;

import com.paradisecloud.fcm.fme.model.cms.DtmfProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个dtmfProfile
 */
@Getter
@Setter
@ToString
public class ActiveDtmfProfilesResponse
{
    
    /**
     * callLegProfile 总数
     */
    private Integer total;
    
    /**
     * 单个callLegProfiles
     */
    private List<DtmfProfile> dtmfProfile;
}
