package com.paradisecloud.fcm.fme.model.response.dtmfprofile;

import com.paradisecloud.fcm.fme.model.cms.DtmfProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * dtmfProfile 详情响应类
 */
@Getter
@Setter
@ToString
public class DtmfProfileInfoResponse
{
    
    /**
     * 单个 dtmfProfile
     */
    private DtmfProfile dtmfProfile;
}
