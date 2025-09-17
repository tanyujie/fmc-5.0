package com.paradisecloud.fcm.fme.model.param;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 直播观众参数
 *
 * @author bkc
 * @date 2020年8月25日
 */
@Getter
@Setter
@ToString
public class LiveViewerParam
{
    
    @NotNull(message = "设备ID不能为空")
    private String deviceId;
    
    @NotNull(message = "会议号不能为空")
    private String confNum;
}
