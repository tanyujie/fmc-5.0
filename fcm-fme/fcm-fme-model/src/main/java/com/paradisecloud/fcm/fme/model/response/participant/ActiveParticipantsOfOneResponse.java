package com.paradisecloud.fcm.fme.model.response.participant;

import com.alibaba.fastjson.annotation.JSONField;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个与会者
 *
 * @author zt1994 2019/8/27 14:12
 */
@Getter
@Setter
@ToString
public class ActiveParticipantsOfOneResponse
{
    
    /**
     * 总数
     */
    @JSONField(name = "@total")
    private Integer total;
    
    /**
     * 多个与会者
     */
    private Participant participant;
    
}
