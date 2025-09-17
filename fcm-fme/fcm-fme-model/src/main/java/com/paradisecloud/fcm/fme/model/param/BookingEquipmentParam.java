package com.paradisecloud.fcm.fme.model.param;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 预约设备
 *
 * @author wdy
 * @create 2020-05-22 10:24
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingEquipmentParam
{
    
    private String bookingId;
    
    /**
     * 会议室号码 需要和会议号码模板匹配
     */
    private String confNum;
    
    /**
     * 直播设备ID
     */
    private List<Long> deviceId;
    
}
