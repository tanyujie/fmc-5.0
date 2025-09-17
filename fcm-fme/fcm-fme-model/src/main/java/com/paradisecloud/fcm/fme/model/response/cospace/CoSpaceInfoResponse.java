package com.paradisecloud.fcm.fme.model.response.cospace;

import com.paradisecloud.fcm.fme.model.cms.CoSpace;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * coSpace 详情信息
 *
 * @author zt1994 2019/8/22 14:15
 */
@Getter
@Setter
@ToString
public class CoSpaceInfoResponse
{
    
    /**
     * coSpace 详情
     */
    private CoSpace coSpace;
}
