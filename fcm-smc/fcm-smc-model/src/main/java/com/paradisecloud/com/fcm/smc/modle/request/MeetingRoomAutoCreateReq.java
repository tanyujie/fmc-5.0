package com.paradisecloud.com.fcm.smc.modle.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/30 11:48
 */
@Data
@NoArgsConstructor
public class MeetingRoomAutoCreateReq {

    private String name;
    private String account;
    private Long deptId;
    private String password;
}
