package com.paradisecloud.com.fcm.smc.modle;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nj
 * @date 2022/8/22 11:24
 */
@NoArgsConstructor
@Data
public class MeetingRoomCreateReq {
    private String id;
    private String name;

    private String provisionEua;

    private TerminalParam terminalParam;


    private String serviceZoneId;

    private String organizationId;

    private String areaId;

}
