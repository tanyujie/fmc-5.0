package com.paradisecloud.fcm.fme.model.response.dtmfprofile;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.fme.model.cms.DtmfProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个callLegProfile
 *
 * @author zt1994 2019/8/19 11:32
 */
@Getter
@Setter
@ToString
public class ActiveDtmfProfilesOfOneResponse
{
    
    /**
     * callLegProfile 总数
     */
    @JSONField(name = "@total")
    private Integer total;
    
    /**
     * 单个callLegProfiles
     */
    private DtmfProfile dtmfProfile;
}
