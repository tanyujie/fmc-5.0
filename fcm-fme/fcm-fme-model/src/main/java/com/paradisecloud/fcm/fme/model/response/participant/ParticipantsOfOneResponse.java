package com.paradisecloud.fcm.fme.model.response.participant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个与会者响应
 *
 * @author zt1994 2019/8/27 14:10
 */
@Getter
@Setter
@ToString
public class ParticipantsOfOneResponse
{
    
    /**
     * 单个与会者
     */
    private ActiveParticipantsOfOneResponse participants;
    
}
