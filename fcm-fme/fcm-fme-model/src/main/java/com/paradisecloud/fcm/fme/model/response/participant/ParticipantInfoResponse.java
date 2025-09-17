package com.paradisecloud.fcm.fme.model.response.participant;

import com.paradisecloud.fcm.fme.model.cms.participant.Participant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 与会者详情响应
 *
 * @author zt1994 2019/8/27 14:15
 */
@Getter
@Setter
@ToString
public class ParticipantInfoResponse
{
    
    /**
     * 与会者详情
     */
    private Participant participant;
}
