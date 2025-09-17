package com.paradisecloud.fcm.fme.model.response.participant;

import java.util.ArrayList;

import com.paradisecloud.fcm.fme.model.cms.participant.Participant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个与会者
 *
 * @author zt1994 2019/8/27 14:04
 */
@Getter
@Setter
@ToString
public class ActiveParticipantsResponse
{
    
    /**
     * 总数
     */
    private Integer total;
    
    /**
     * 多个与会者
     */
    private ArrayList<Participant> participant;
    
}
