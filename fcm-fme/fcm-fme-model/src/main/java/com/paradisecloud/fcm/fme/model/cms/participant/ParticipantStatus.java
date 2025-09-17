package com.paradisecloud.fcm.fme.model.cms.participant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 与会者状态
 *
 * @author zt1994 2019/8/27 14:25
 */
@Getter
@Setter
@ToString
public class ParticipantStatus
{
    
    /**
     * initial|ringing|connected|onHold 调用状态
     */
    private String state;
}
