package com.paradisecloud.fcm.fme.model.response.participant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个与会者响应
 *
 * @author zt1994 2019/8/27 14:02
 */
@Getter
@Setter
@ToString
public class ParticipantsResponse
{
    
    /**
     * 多个与会者
     */
    private ActiveParticipantsResponse participants;
}
