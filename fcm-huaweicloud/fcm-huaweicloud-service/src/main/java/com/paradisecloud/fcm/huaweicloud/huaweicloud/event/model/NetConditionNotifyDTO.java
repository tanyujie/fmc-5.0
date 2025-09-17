package com.paradisecloud.fcm.huaweicloud.huaweicloud.event.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nj
 * @date 2024/3/7 14:05
 */
@NoArgsConstructor
@Data
public class NetConditionNotifyDTO {

    private String confID;
    private String msgID;
    private Integer msgMode;
    private Integer version;
    private Long createTime;
    private String action;
    private List<NetConditionNotifyParticipant> data;

}
