package com.paradisecloud.fcm.fme.model.param;

import lombok.Getter;
import lombok.Setter;

/**
 * 会议请求参数
 *
 * @author bkc
 * @date 2020年10月20日
 */
@Getter
@Setter
public class ConferenceRequestParam
{
    private String coSpaceId;
    
    private String confNum;
}
