package com.paradisecloud.fcm.web.model;

import com.paradisecloud.fcm.service.eunm.NotifyType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2024/6/29 8:55
 */
@NoArgsConstructor
@Data
public class SensSmcVo {

    private String notifyType;
    private String conferenceNumber;
    private  String phone;
    private String conferenceName;
    private  String startTime;
    private String endTime;

}
