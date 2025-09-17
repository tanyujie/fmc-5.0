package com.paradisecloud.smc3.model.request;

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
