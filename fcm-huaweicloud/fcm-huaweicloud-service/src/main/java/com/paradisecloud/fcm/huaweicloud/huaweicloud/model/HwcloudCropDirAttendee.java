package com.paradisecloud.fcm.huaweicloud.huaweicloud.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2024/3/11 9:46
 */
@NoArgsConstructor
@Data
public class HwcloudCropDirAttendee {
    private String accountId;
    private String deptName;
    private String markId;
    private String name;
    private String phone;
    private Integer role;
    private String sip;
    private String sms;
    private String type;
    private String userUUID;
}
